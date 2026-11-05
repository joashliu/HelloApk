package com.example.hello

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class LedgerNotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "LedgerNotifListener"

        // 金額上限（可按需要調整）
        private const val MAX_AMOUNT = 50000.0

        // 前綴金額：HKD6.00 / hk$4.90 / $10 / MOP 12.5 / RMB 30
        // 注意：[\s\u00A0\u200B]* 支援普通空格、NBSP、零寬空格
        private val AMOUNT_PREFIX_PATTERN = Regex(
            """(?:HKD|HK\$|MOP|RMB|CNY|USD|SGD|\$)[\s\u00A0\u200B]*([0-9]+(?:,[0-9]{3})*(?:\.[0-9]{1,2})?)""",
            RegexOption.IGNORE_CASE
        )

        // 後綴金額：6.00 HKD / 12.5 MOP
        private val AMOUNT_SUFFIX_PATTERN = Regex(
            """([0-9]+(?:,[0-9]{3})*(?:\.[0-9]{1,2})?)[\s\u00A0\u200B]*(?:HKD|MOP|RMB|CNY)""",
            RegexOption.IGNORE_CASE
        )

        // 一般消費關鍵字（保底過濾）
        private val DEDUCT_KEYWORDS = listOf(
            "扣款", "已付款", "付款", "消費", "交易", "易乘碼", "地鐵", "港鐵", "入閘", "出閘",
            "transaction", "payment", "paid", "purchase", "charged"
        )

        // 商戶白名單：key = 通知文本匹配關鍵字, value = 記帳用商戶名
        private val MERCHANT_WHITELIST = listOf(
            "McDonalds" to "麥當勞",
            "麥當勞" to "麥當勞",
            "KFC" to "KFC",
            "肯德基" to "KFC"
        )

        private fun extractAmount(text: String): Double {
            fun toDouble(raw: String) = raw.replace(",", "").toDoubleOrNull() ?: 0.0

            AMOUNT_PREFIX_PATTERN.find(text)?.groupValues?.getOrNull(1)?.let {
                val v = toDouble(it)
                if (v > 0.0) return v
            }
            AMOUNT_SUFFIX_PATTERN.find(text)?.groupValues?.getOrNull(1)?.let {
                val v = toDouble(it)
                if (v > 0.0) return v
            }
            return 0.0
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Listener connected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "Listener disconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return

        // 撈齊所有可能嘅文本欄位
        // 注意：Notification.EXTRA_PROGRESS_TEXT 係 @hide，SDK 冇 expose，唔可以用
        val ticker    = notification.tickerText?.toString().orEmpty()
        val title     = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text      = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val bigText   = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()
        val subText   = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString().orEmpty()
        val infoText  = extras.getCharSequence(Notification.EXTRA_INFO_TEXT)?.toString().orEmpty()
        val textLines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
            ?.joinToString(" ") { it.toString() }
            .orEmpty()
        val messagesText = readMessagingStyle(extras)

        val combined = listOf(
            ticker, title, text, bigText, subText, infoText, textLines, messagesText
        ).filter { it.isNotBlank() }.joinToString(" ")

        if (combined.isBlank()) return

        Log.d(TAG, "RAW combined=[$combined]")

        val amount = extractAmount(combined)

        // 🎯 白名單優先（先判斷商戶，再驗金額）
        val whitelistHit = MERCHANT_WHITELIST.firstOrNull { (key, _) ->
            combined.contains(key, ignoreCase = true)
        }

        if (whitelistHit != null) {
            val (matchedKey, merchantName) = whitelistHit

            if (amount <= 0.0 || amount > MAX_AMOUNT) {
                // 白名單命中但金額唔合理 → 出 warning，方便排查
                Log.w(
                    TAG,
                    "白名單命中 [$matchedKey → $merchantName]，但金額唔合理 (amount=$amount)，跳過 → $combined"
                )
                return
            }

            val category = when (merchantName) {
                "麥當勞", "KFC" -> "飲食"
                else -> "個人"
            }

            Log.d(TAG, "🔥 觸發直通車：$merchantName · $amount (matched=$matchedKey)")
            NotificationCaptureBridge.pushCaptured(merchantName, amount, category)
            return
        }

        // 🎯 非白名單：先驗金額合理性
        if (amount <= 0.0 || amount > MAX_AMOUNT) return

        // 🎯 一般關鍵字保底過濾
        if (DEDUCT_KEYWORDS.none { combined.contains(it, ignoreCase = true) }) {
            return
        }

        // 歸納名稱
        val note = when {
            combined.contains("易乘碼", ignoreCase = true) -> "地鐵"
            combined.contains("AEON", ignoreCase = true) -> "AEON消費"
            title.isNotBlank() && title.length <= 20 -> title
            else -> "消費"
        }

        // 分類映射
        val category = when {
            note == "地鐵" || combined.contains("港鐵", true) -> "交通"
            combined.contains("餐", true) || combined.contains("食", true) -> "飲食"
            combined.contains("超市", true) || combined.contains("店", true) -> "購物"
            else -> "個人"
        }

        Log.d(TAG, "捕獲一般通知：$note · $amount · $category → $combined")
        NotificationCaptureBridge.pushCaptured(note, amount, category)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // 預留
    }

    /** 讀取 MessagingStyle 通知每一條訊息文本 */
    @Suppress("DEPRECATION")
    private fun readMessagingStyle(extras: Bundle): String {
        val arr = extras.getParcelableArray(Notification.EXTRA_MESSAGES) ?: return ""
        return arr.joinToString(" ") { item ->
            val b = item as? Bundle ?: return@joinToString ""
            b.getCharSequence("text")?.toString().orEmpty()
        }
    }
}
