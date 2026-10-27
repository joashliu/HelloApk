package com.example.hello

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class PaymentNotificationListener : NotificationListenerService() {

    private val recentProcessed = mutableMapOf<String, Long>()

    // 當監聽器成功連接系統時，順便喚醒常駐嘅快速記帳通知
    override fun onListenerConnected() {
        super.onListenerConnected()
        try { refreshQuickAddNotification(this) } catch (_: Exception) {}
    }

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
            Log.d("LedgerNotif", "👀 偵測到乘車碼通知：$fullText")
            
            val now = System.currentTimeMillis()
            val hashKey = fullText.trim()
            val lastProcessedTime = recentProcessed[hashKey] ?: 0L
            
            // 🟢 縮短冷卻時間至 3 秒 (3000ms)，確保連續消費都能觸發
            if (now - lastProcessedTime < 3000L) {
                Log.d("LedgerNotif", "⏳ 3秒內已處理過相同通知，忽略去重")
                return
            }

            val amountRegex = Regex("(?:HKD|HK\\$|\\$)\\s*([0-9]+\\.?[0-9]*)")
            val match = amountRegex.find(fullText)
            val amount = match?.groupValues?.get(1)?.toDoubleOrNull()
            
            if (amount != null && amount > 0) {
                Log.d("LedgerNotif", "✅ 匹配成功！準備推送金額: $amount")
                recentProcessed[hashKey] = now
                
                val triggeredCloud = OppoFluidCloudBridge.triggerFluidCloud(
                    context = this,
                    note = "地鐵",
                    amount = amount,
                    category = "交通",
                    timestamp = now
                )
                
                if (!triggeredCloud) {
                    NotificationCaptureBridge.pushCaptured("地鐵", amount, "交通")
                }
            } else {
                Log.d("LedgerNotif", "⚠️ 搵到關鍵字，但提取唔到金額: $fullText")
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
    }
}
