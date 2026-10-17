package com.example.hello

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import org.json.JSONArray

class QuickAddTileService : TileService() {

    override fun onTileAdded() {
        super.onTileAdded()
        updateTileState()
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        tile.state = Tile.STATE_INACTIVE
        tile.label = "快速記帳"
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()

        // ★ 讀返最近使用嘅項目名（由 MainActivity 持久化落去）
        val (note, amount) = readTopQuickNote(this)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_OPEN_KEYBOARD, true)
            if (!note.isNullOrBlank()) {
                putExtra(MainActivity.EXTRA_PREFILL_NOTE, note)
            }
            if (amount != null && amount > 0.0) {
                putExtra(MainActivity.EXTRA_PREFILL_AMOUNT, amount)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            startActivityAndCollapse(pendingIntent)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }

    /**
     * 從 SharedPreferences 讀取最近使用嘅項目名。
     * 呢個 key 由 MainActivity 喺每次 topQuickNotesInfo 變化時寫入。
     *
     * @return Pair(note, amount) —— 冇資料時 note = null
     */
    private fun readTopQuickNote(context: Context): Pair<String?, Double?> {
        return try {
            val prefs = context.getSharedPreferences("ledger_prefs", Context.MODE_PRIVATE)
            val jsonStr = prefs.getString("top_quick_notes", null) ?: return null to null

            val arr = JSONArray(jsonStr)
            if (arr.length() == 0) return null to null

            val first = arr.getJSONObject(0)
            val note = first.optString("note", "").takeIf { it.isNotBlank() }
            val amt = first.optDouble("amount", 0.0).takeIf { it > 0.0 }
            note to amt
        } catch (_: Exception) {
            null to null
        }
    }
}
