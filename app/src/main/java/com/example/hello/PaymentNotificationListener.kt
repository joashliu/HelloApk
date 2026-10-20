package com.example.hello // 替換為你嘅 package name

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import java.util.concurrent.ConcurrentHashMap

// 處理「略過」按鈕嘅廣播
class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ACTION_SKIP") {
            val id = intent.getIntExtra("NOTIFY_ID", -1)
            if (id != -1) {
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.cancel(id) // 清除該條系統通知
            }
        }
    }
}

class PaymentNotificationListener : NotificationListenerService() {
    
    // 防重複機制：記錄已處理過嘅通知 ID 或特徵
    private val processedKeys = ConcurrentHashMap<String, Long>()

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val text = extras.getString(Notification.EXTRA_TEXT) ?: ""
        val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
        
        val fullText = "$title$text"
        
        // 規則 1 & 9：只匹配「易乘碼」+「成功付款」，其他安靜忽略
        if (fullText.contains("易乘碼") && fullText.contains("成功付款")) {
            
            // 規則 2：正則抓取 HKD 後面的數字
            val regex = Regex("""HKD\s*([0-9]+(?:\.[0-9]{1,2})?)""")
            val match = regex.find(fullText)
            
            if (match != null) {
                val amountStr = match.groupValues[1]
                val amount = amountStr.toDoubleOrNull() ?: return
                
                // 規則 8：防重複機制 (利用時間戳截斷至秒 + 金額 作為唯一標識)
                val uniqueKey = "${sbn.postTime / 1000}_$amountStr"
                if (processedKeys.containsKey(uniqueKey)) return
                
                processedKeys[uniqueKey] = System.currentTimeMillis()
                // 簡單清理舊記錄防爆內存
                if (processedKeys.size > 200) processedKeys.clear()

                // 發送待記賬通知
                sendLedgerNotification(amount)
            }
        }
    }

    private fun sendLedgerNotification(amount: Double) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "ledger_auto_add"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 規則 12：預設優先度，只保留喺通知欄，唔強制彈出
            val ch = NotificationChannel(channelId, "自動記賬提示", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(ch)
        }

        // 規則 5 & 7：加入記賬 Intent (喚起 App)
        val addIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_OPEN_KEYBOARD, true)
            putExtra(MainActivity.EXTRA_PREFILL_AMOUNT, amount)
            putExtra(MainActivity.EXTRA_PREFILL_NOTE, "易乘碼")
            putExtra(MainActivity.EXTRA_PREFILL_CATEGORY, "交通")
        }
        val notifyId = amount.hashCode() xor System.currentTimeMillis().hashCode()
        
        val addPending = PendingIntent.getActivity(
            this, notifyId, addIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 規則 6：略過 Intent (清除通知)
        val skipIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = "ACTION_SKIP"
            putExtra("NOTIFY_ID", notifyId)
        }
        val skipPending = PendingIntent.getBroadcast(
            this, notifyId, skipIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 規則 4：生成系統通知設定
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_input_add) // 建議換成你嘅 App Icon `R.drawable.ic_launcher_foreground`
            .setContentTitle("易乘碼扣費｜待記賬")
            .setContentText("HKD$amount，點擊處理記賬")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(addPending) // 點擊通知本體
            .addAction(0, "加入記賬", addPending) // 動作按鈕 1
            .addAction(0, "略過", skipPending)   // 動作按鈕 2

        nm.notify(notifyId, builder.build())
    }
}
