package com.example.hello

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class PaymentNotificationListener : NotificationListenerService() {

    private val recentProcessed = mutableMapOf<String, Long>()

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""
        val tickerText = sbn.notification.tickerText?.toString() ?: ""

        val fullText = "$title $text $bigText $tickerText"
        
        if (fullText.isBlank()) return

        if (fullText.contains("乘車碼") || fullText.contains("易乘碼")) {
            Log.d("LedgerNotif", "偵測到乘車碼通知：$fullText")
            
            val now = System.currentTimeMillis()
            val hashKey = fullText.trim()
            val lastProcessedTime = recentProcessed[hashKey] ?: 0L
            if (now - lastProcessedTime < 30000L) {
                Log.d("LedgerNotif", "30秒內已處理過相同通知，忽略去重")
                return
            }

            val amountRegex = Regex("(?:HKD|HK\\$|\\$)\\s*([0-9]+\\.?[0-9]*)")
            val match = amountRegex.find(fullText)
            val amount = match?.groupValues?.get(1)?.toDoubleOrNull()
            
            if (amount != null && amount > 0) {
                Log.d("LedgerNotif", "匹配成功！準備推送金額: $amount")
                recentProcessed[hashKey] = now
                NotificationCaptureBridge.pushCaptured("乘車扣費", amount, "交通")
            } else {
                Log.d("LedgerNotif", "搵到關鍵字，但提取唔到金額: $fullText")
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
    }
}
