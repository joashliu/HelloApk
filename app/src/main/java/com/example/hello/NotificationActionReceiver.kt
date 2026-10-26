package com.example.hello

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.hello.ACTION_IGNORE_CAPTURE") {
            Log.d("LedgerNotif", "用戶選擇忽略通知")
            val notifId = intent.getIntExtra("notif_id", -1)
            if (notifId != -1) {
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                nm?.cancel(notifId)
            }
            // 清理橋接器嘅最新記錄，防止重複彈出
            NotificationCaptureBridge.clear()
        }
    }
}
