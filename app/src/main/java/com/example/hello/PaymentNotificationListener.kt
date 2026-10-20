package com.example.hello

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class PaymentNotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "PaymentNotifListener"
        private val TARGET_PACKAGES = setOf(
            "com.mtr.mobile",
            "com.octopus.card",
            "com.octopuscards.oepay",
            "com.hsbc.hsbc_hk",
            "com.hangsengbank.hangseng"
        )
        private val AMOUNT_PATTERN = Regex("""(?:HK\$|\$)\s*(\d+(?:\.\d{1,2})?)""")
        private val DEDUCT_KEYWORDS = listOf(
            "扣款", "已付款", "付款", "消費", "交易", "易乘碼", "地鐵", "港鐵", "入閘", "出閘"
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        val pkg = sbn.packageName ?: return
        if (pkg !in TARGET_PACKAGES) return

        val extras = sbn.notification?.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString().orEmpty()

        val combined = "$title $text $bigText $subText"
        if (combined.isBlank()) return

        if (DEDUCT_KEYWORDS.none { combined.contains(it, ignoreCase = true) }) return

        val match = AMOUNT_PATTERN.find(combined) ?: return
        val amount = match.groupValues.getOrNull(1)?.toDoubleOrNull() ?: return
        if (amount <= 0.0 || amount > 5000.0) return

        val note = when {
            pkg == "com.mtr.mobile" -> "地鐵"
            title.isNotBlank() && title.length <= 20 -> title
            else -> "消費"
        }

        val category = when {
            combined.contains("地鐵", true) || combined.contains("港鐵", true)
                || combined.contains("易乘碼", true) -> "交通"
            combined.contains("餐", true) || combined.contains("食", true) -> "飲食"
            combined.contains("超市", true) || combined.contains("店", true) -> "購物"
            else -> "個人"
        }

        Log.d(TAG, "捕獲：$note · $amount · $category")
        NotificationCaptureBridge.pushCaptured(note, amount, category)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {}
}
