package com.example.hello

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log

class QuickAddTileService : TileService() {

    companion object {
        private const val TAG = "QuickAddTile"
    }

    override fun onStartListening() {
        super.onStartListening()
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
