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
        
        // 1. 等待文字載入完整，否則跳過
        if (fullText.isBlank()) return

        // 2. 嚴格匹配交通扣費關鍵字，不干擾其他通知
        if (fullText.contains("乘車碼") || fullText.contains("易乘碼")) {
            Log.d("LedgerNotif", "👀 偵測到乘車碼通知：$fullText")
            
            val now = System.currentTimeMillis()
            val hashKey = fullText.trim()
            val lastProcessedTime = recentProcessed[hashKey] ?: 0L
            
            // 3. 去重機制 (30秒內唔重複觸發，無論係流体云定普通通知)
            if (now - lastProcessedTime < 30000L) {
                Log.d("LedgerNotif", "⏳ 30秒內已處理過相同通知，忽略去重")
                return
            }

            val amountRegex = Regex("(?:HKD|HK\\$|\\$)\\s*([0-9]+\\.?[0-9]*)")
            val match = amountRegex.find(fullText)
            val amount = match?.groupValues?.get(1)?.toDoubleOrNull()
            
            if (amount != null && amount > 0) {
                Log.d("LedgerNotif", "✅ 匹配成功！準備推送金額: $amount")
                recentProcessed[hashKey] = now
                
                // 🟢 4. 優先嘗試喚起 OPPO 流体云 (將名稱改為地鐵)
                val triggeredCloud = OppoFluidCloudBridge.triggerFluidCloud(
                    context = this,
                    note = "地鐵",
                    amount = amount,
                    category = "交通",
                    timestamp = now
                )
                
                // 🟢 5. 降級備用：如果流体云不支援/發送失敗，彈出原生抬頭通知 (將名稱改為地鐵)
                if (!triggeredCloud) {
                    NotificationCaptureBridge.pushCaptured("地鐵", amount, "交通")
                }
            } else {
                Log.d("LedgerNotif", "⚠️ 搵到關鍵字，但提取唔到金額: $fullText")
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // 唔刪除原始支付平台嘅通知
    }
}
