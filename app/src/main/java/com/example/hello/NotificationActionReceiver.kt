package com.example.hello

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class PaymentNotificationListener : NotificationListenerService() {

    // 簡單去重機制：記錄最近 30 秒內處理過嘅文字內容
    private val recentProcessed = mutableMapOf<String, Long>()

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""
        val tickerText = sbn.notification.tickerText?.toString() ?: ""

        val fullText = "$title$text $bigText$tickerText"
        
        // 1. 等待原始通知完整載入：如果無任何文字，直接忽略等下一次更新
        if (fullText.isBlank()) return

        // 2. 關鍵字匹配 (唔干擾其他無關通知)
        if (fullText.contains("乘車碼") || fullText.contains("易乘碼")) {
            Log.d("LedgerNotif", "👀 偵測到乘車碼通知：$fullText")
            
            // 3. 去重機制 (避免相同通知喺幾毫秒內重複發送更新)
            val now = System.currentTimeMillis()
            val hashKey = fullText.trim()
            val lastProcessedTime = recentProcessed[hashKey] ?: 0L
            if (now - lastProcessedTime < 30_000L) {
                Log.d("LedgerNotif", "⏳ 30秒內已處理過相同通知，忽略去重")
                return
            }

            // 4. 提取金額 (支援 HKD 4.9, HK$ 4.90, $ 4.9 等格式)
            val amountRegex = Regex("(?:HKD|HK\\$\vert{}\\$)\\s*([0-9]+\\.?[0-9]*)")
            val match = amountRegex.find(fullText)
            val amount = match?.groupValues?.get(1)?.toDoubleOrNull()
            
            if (amount != null && amount > 0) {
                Log.d("LedgerNotif", "✅ 匹配成功！準備推送金額: $amount")
                recentProcessed[hashKey] = now
                
                // 觸發自定義 APP 彈出通知 (分類強制設定為"交通")
                NotificationCaptureBridge.pushCaptured("乘車扣費", amount, "交通")
            } else {
                Log.d("LedgerNotif", "⚠️ 搵到關鍵字，但提取唔到金額: $fullText")
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // 唔干擾原始通知被滑走嘅行為，保留喺系統欄
    }
}
