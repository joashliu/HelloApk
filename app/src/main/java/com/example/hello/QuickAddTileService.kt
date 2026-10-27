package com.example.hello

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class QuickAddTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        qsTile?.state = Tile.STATE_ACTIVE
        qsTile?.updateTile()
    }

    override fun onClick() {
        super.onClick()
        
        // 1. 建立 Intent，並加入 FLAG_ACTIVITY_NEW_TASK 同埋新嘅輕量模式標記
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_OPEN_KEYBOARD, true)
            // 🟢 核心標記：話俾 MainActivity 聽呢個啟動要求係來自磁鐵
            putExtra(MainActivity.EXTRA_FROM_TILE, true) 
        }

        // 2. 判斷 Android 版本，Android 14 (API 34) 或以上必須用 PendingIntent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            startActivityAndCollapse(pendingIntent)
        } else {
            // Android 13 或以下維持舊寫法
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}
