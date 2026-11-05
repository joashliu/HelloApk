package com.example.hello

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class LedgerNotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "LedgerNotifListener"
        // 地鐵／八達通／一般消費通知嘅關鍵字
        private val AMOUNT_PATTERN = Regex("""(?:HK\$|\$)\s*(\d+(?:\.\d{1,2})?)""")
        private val DEDUCT_KEYWORDS = listOf(
            "扣款", "已付款", "付款", "消費", "交易", "易乘碼", "地鐵", "港鐵", "入閘", "出閘"
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        val pkg = sbn.packageName ?: ""

        val extras = sbn.notification?.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString().orEmpty()

        val combined = "$title $text $bigText $subText"
        if (combined.isBlank()) return

        // 1. AEON 短訊/任何 App 通知特定處理
        if (combined.contains("AEON: We received a transaction", ignoreCase = true)) {
            val aeonRegex = "transaction (.*?) at amount HKD([0-9.]+)".toRegex(RegexOption.IGNORE_CASE)
            val match = aeonRegex.find(combined)

            if (match != null) {
                val rawMerchant = match.groupValues[1].trim()
                val amount = match.groupValues[2].toDoubleOrNull() ?: 0.0

                if (amount > 0.0) {
                    val noteName = if (rawMerchant.contains("McDonalds", ignoreCase = true)) {
                        "麥當勞"
                    } else {
                        rawMerchant
                    }
                    val category = if (noteName == "麥當勞") "飲食" else "個人"

                    Log.d(TAG, "捕獲 AEON 通知：$noteName · $amount · $category")
                    NotificationCaptureBridge.pushCaptured(noteName, amount, category)
                    return 
                }
            }
        }

        // 2. 一般消費處理邏輯 (掃描所有 App 嘅通知)
        // 冇扣款關鍵字就跳過（避免誤觸一般聊天或新聞通知）
        if (DEDUCT_KEYWORDS.none { combined.contains(it, ignoreCase = true) }) return

        val match = AMOUNT_PATTERN.find(combined) ?: return
        val amount = match.groupValues.getOrNull(1)?.toDoubleOrNull() ?: return
        if (amount <= 0.0 || amount > 5000.0) return

        // 歸納名稱：如果係港鐵 App 出就叫「地鐵」，否則優先用 title
        val note = when {
            pkg == "com.mtr.mobile" -> "地鐵"
            title.isNotBlank() && title.length <= 20 -> title
            else -> "消費"
        }

        // 分類映射
        val category = when {
            combined.contains("地鐵", true) || combined.contains("港鐵", true)
                || combined.contains("易乘碼", true) -> "交通"
            combined.contains("餐", true) || combined.contains("食", true) -> "飲食"
            combined.contains("超市", true) || combined.contains("店", true) -> "購物"
            else -> "個人"
        }

        Log.d(TAG, "捕獲通知：$note · $amount · $category → $combined")
        NotificationCaptureBridge.pushCaptured(note, amount, category)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
    }
}
