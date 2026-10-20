package com.example.hello

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "com.example.hello.ACTION_IGNORE_CAPTURE" -> {
                val id = intent.getIntExtra("notif_id", -1)
                if (id != -1) {
                    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                    nm?.cancel(id)
                }
            }
        }
    }
}
