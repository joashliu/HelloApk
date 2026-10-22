package com.example.hello

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log

/**
 * 快速記帳快捷磁貼
 *
 * ⚠️ 注意：第三方 App 嘅 TileService 冇長按事件 API，
 *    handleSecondaryClick() 係 SystemUI 內部方法，無法 override。
 *    所以呢個檔案只有短按（onClick）功能。
 *
 *    長按 OCR 功能改由其他入口提供（例如 App 內按鈕）。
 */
class QuickAddTileService : TileService() {

    companion object {
        private const val TAG = "QuickAddTile"
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTileUi()
    }

    override fun onTileAdded() {
        super.onTileAdded()
        updateTileUi()
    }

    private fun updateTileUi() {
        try {
            qsTile?.apply {
                state = Tile.STATE_INACTIVE
                updateTile()
            }
        } catch (e: Exception) {
            Log.w(TAG, "updateTileUi failed: ${e.message}")
        }
    }

    // ★ 原有短按邏輯，完全不動
    override fun onClick() {
        super.onClick()
        try {
            val intent = buildLaunchIntentForMain(this, prefillNote = null).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivityAndCollapse(intent)
        } catch (e: Exception) {
            Log.e(TAG, "onClick failed: ${e.message}", e)
        }
    }
}
