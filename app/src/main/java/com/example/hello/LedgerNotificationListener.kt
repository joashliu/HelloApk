package com.example.hello

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class LedgerNotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "LedgerNotifListener"

        // 金額：前綴或後綴，容許普通空格 / NBSP / 零寬空格
        private val AMOUNT_PREFIX = Regex(
            """(?:HKD|HK\$|MOP|RMB|CNY|USD|\$)[\s\u00A0\u200B]*([0-9]+(?:,[0-9]{3})*(?:\.[0-9]{1,2})?)""",
            RegexOption.IGNORE_CASE
        )
        private val AMOUNT_SUFFIX = Regex(
            """([0-9]+(?:,[0-9]{3})*(?:\.[0-9]{1,2})?)[\s\u00A0\u200B]*(?:HKD|MOP|RMB|CNY)""",
            RegexOption.IGNORE_CASE
        )

        // 商戶關鍵字 → (顯示名, 分類)
        // 用「前綴」概念：McDonald 可以食到 McDonalds / McDonald's
        private val MERCHANTS = listOf(
            Triple("McDonald", "麥當勞", "飲食"),
            Triple("麥當勞",   "麥當勞", "飲食"),
            Triple("麦当劳",   "麥當勞", "飲食"),
            Triple("KFC",      "KFC",   "飲食"),
            Triple("肯德基",   "KFC",   "飲食"),
            Triple("易乘碼",   "地鐵",   "交通"),
            Triple("港鐵",     "地鐵",   "交通"),
            Triple("MTR",      "地鐵",   "交通")
        )

        // 保底關鍵字：認唔到商戶，但有呢啲字就當消費
        private val FALLBACK_KEYWORDS = listOf(
            "扣款", "已付款", "付款", "消費", "交易",
            "transaction", "payment", "paid", "purchase", "charged"
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
        Log.d(TAG, "✅ Listener connected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val n = sbn?.notification ?: return
        val e = n.extras ?: return

        // 1) 撈文本
        val combined = listOf(
            n.tickerText?.toString(),
            e.getCharSequence(Notification.EXTRA_TITLE)?.toString(),
            e.getCharSequence(Notification.EXTRA_TEXT)?.toString(),
            e.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString(),
            e.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString(),
            e.getCharSequence(Notification.EXTRA_INFO_TEXT)?.toString(),
            e.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.joinToString(" ") { it.toString() }
        ).filterNotNull().filter { it.isNotBlank() }.joinToString(" ")

        if (combined.isBlank()) return
        Log.d(TAG, "① combined=[$combined]")

        // 2) 抽金額
        val amount = extractAmount(combined)
        Log.d(TAG, "② amount=$amount")
        if (amount <= 0.0 || amount > 50000.0) {
            Log.d(TAG, "③ 金額無效 → skip")
            return
        }

        // 3) 認商戶（統一表，唔分白名單/保底）
        val hit = MERCHANTS.firstOrNull { (k, _, _) -> combined.contains(k, ignoreCase = true) }
        if (hit != null) {
            val (matchedKey, name, category) = hit
            Log.d(TAG, "④ 命中商戶 matched=[$matchedKey] → $name · $amount · $category")
            NotificationCaptureBridge.pushCaptured(name, amount, category)
            return
        }

        // 4) 保底：有消費關鍵字但認唔到商戶
        if (FALLBACK_KEYWORDS.any { combined.contains(it, ignoreCase = true) }) {
            val title = e.getCharSequence(Notification.EXTRA_TITLE)?.toString()
            val note = title?.takeIf { it.isNotBlank() && it.length <= 20 } ?: "消費"
            Log.d(TAG, "⑤ 保底命中 → $note · $amount")
            NotificationCaptureBridge.pushCaptured(note, amount, "個人")
            return
        }

        Log.d(TAG, "⑥ 唔係消費通知 → skip")
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {}
}
