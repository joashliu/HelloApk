package com.example.hello

import android.content.Intent
import android.service.quicksettings.TileService

class QuickAddTileService : TileService() {
    override fun onClick() {
        super.onClick()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_OPEN_KEYBOARD, true)
            // 🟢 核心標記：話俾 MainActivity 聽呢個啟動要求係來自磁鐵
            putExtra(MainActivity.EXTRA_FROM_TILE, true)
        }
        startActivityAndCollapse(intent)
    }
}
