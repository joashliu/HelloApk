package com.example.hello

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class PaymentNotificationListener : NotificationListenerService() {

    // 去重：key → 上次處理時間（易乘碼用 fullText hash，白名單用 "商戶|金額"）
    private val recentProcessed = mutableMapOf<String, Long>()

    companion object {
        private const val TAG = "LedgerNotif"
        private const val COOLDOWN_MS = 3000L
        private const val MAX_AMOUNT = 50000.0

        // 前綴金額：HKD6.00 / hk$4.90 / $10 / MOP 12.5 / RMB 30
        // [\s\u00A0\u200B]* 支援普通空格、NBSP、零寬空格
        private val AMOUNT_PREFIX = Regex(
            """(?:HKD|HK\$|MOP|RMB|CNY|USD|SGD|\$)[\s\u00A0\u200B]*([0-9]+(?:,[0-9]{3})*(?:\.[0-9]{1,2})?)""",
            RegexOption.IGNORE_CASE
        )

        // 後綴金額：6.00 HKD / 12.5 MOP
        private val AMOUNT_SUFFIX = Regex(
            """([0-9]+(?:,[0-9]{3})*(?:\.[0-9]{1,2})?)[\s\u00A0\u200B]*(?:HKD|MOP|RMB|CNY)""",
            RegexOption.IGNORE_CASE
        )

        // 商戶白名單：(匹配關鍵字, 顯示名, 分類)
        // 用 "McDonald" 做前綴，McDonalds / McDonald's 都中
        private val MERCHANTS = listOf(
            Triple("McDonald", "麥當勞", "飲食"),
            Triple("麥當勞",   "麥當勞", "飲食"),
            Triple("麦当劳",   "麥當勞", "飲食"),
            Triple("KFC",      "KFC",   "飲食"),
            Triple("肯德基",   "KFC",   "飲食")
        )

        private fun extractAmount(t: String): Double {
            fun d(s: String) = s.replace(",", "").toDoubleOrNull() ?: 0.0
            AMOUNT_PREFIX.find(t)?.groupValues?.getOrNull(1)?.let { if (d(it) > 0) return d(it) }
            AMOUNT_SUFFIX.find(t)?.groupValues?.getOrNull(1)?.let { if (d(it) > 0) return d(it) }
            return 0.0
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "✅ PaymentNotificationListener connected")
        try { refreshQuickAddNotification(this) } catch (_: Exception) {}
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "⚠️ PaymentNotificationListener disconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val n = sbn?.notification ?: return
        val e = n.extras ?: return

        // 撈齊所有可能嘅文本欄位
        val title     = e.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text      = e.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val bigText   = e.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()
        val subText   = e.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString().orEmpty()
        val infoText  = e.getCharSequence(Notification.EXTRA_INFO_TEXT)?.toString().orEmpty()
        val ticker    = n.tickerText?.toString().orEmpty()
        val textLines = e.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
            ?.joinToString(" ") { it.toString() }
            .orEmpty()

        val fullText = listOf(title, text, bigText, subText, infoText, ticker, textLines)
            .filter { it.isNotBlank() }
            .joinToString(" ")

        if (fullText.isBlank()) return

        Log.d(TAG, "① combined=[$fullText]")

        val amount = extractAmount(fullText)
        Log.d(TAG, "② amount=$amount")

        // =========================================================
        // 🎯 第一優先：商戶白名單（唔理係咪 AEON 交易通知）
        // =========================================================
        if (amount > 0.0 && amount <= MAX_AMOUNT) {
            val hit = MERCHANTS.firstOrNull { (k, _, _) ->
                fullText.contains(k, ignoreCase = true)
            }
            if (hit != null) {
                val (matchedKey, name, category) = hit
                val now = System.currentTimeMillis()
                val dedupeKey = "$name|$amount"
                if (now - (recentProcessed[dedupeKey] ?: 0L) < COOLDOWN_MS) {
                    Log.d(TAG, "⏳ 白名單冷卻中，忽略重複：$name · $amount")
                    return
                }
                recentProcessed[dedupeKey] = now
                Log.d(TAG, "🔥 白名單命中：$name · $amount (matched=$matchedKey)")
                NotificationCaptureBridge.pushCaptured(name, amount, category)
                return
            }
        } else if (amount <= 0.0) {
            Log.d(TAG, "⚠️ 抽唔到金額（若為白名單商戶，檢查文本有冇 HKD / 金額）")
        }

        // =========================================================
        // 🎯 第二優先：原有易乘碼 / 乘車碼邏輯（保持不變）
        // =========================================================
        if (fullText.contains("乘車碼") || fullText.contains("易乘碼")) {
            Log.d(TAG, "👀 偵測到乘車碼通知")

            val now = System.currentTimeMillis()
            val hashKey = fullText.trim()
            if (now - (recentProcessed[hashKey] ?: 0L) < COOLDOWN_MS) {
                Log.d(TAG, "⏳ 3秒內已處理過相同易乘碼通知，忽略去重")
                return
            }

            if (amount > 0.0 && amount <= MAX_AMOUNT) {
                Log.d(TAG, "✅ 易乘碼匹配成功，準備推送金額: $amount")
                recentProcessed[hashKey] = now

                val triggeredCloud = try {
                    OppoFluidCloudBridge.triggerFluidCloud(
                        context = this,
                        note = "地鐵",
                        amount = amount,
                        category = "交通",
                        timestamp = now
                    )
                } catch (_: Exception) {
                    false
                }

                if (!triggeredCloud) {
                    NotificationCaptureBridge.pushCaptured("地鐵", amount, "交通")
                }
            } else {
                Log.d(TAG, "⚠️ 搵到易乘碼關鍵字，但提取唔到有效金額: $fullText")
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // 預留
    }
}
