package com.example.hello

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log

/**
 * 快速記帳快捷磁貼
 *
 * ┌─ 短按（onClick）        → 原有邏輯：直接打開記帳輸入頁 + 喚出鍵盤
 * └─ 長按（handleSecondaryClick）→ 新增：觸發離線 OCR 掃描畫面 HKD 金額
 *
 * 注意：handleSecondaryClick() 由 Android 14 (API 34) 起提供。
 *      喺 Android 13 及以下，長按會 fallback 到 onClick()（即等於短按），
 *      呢個係系統行為，App 無法干預。
 */
class QuickAddTileService : TileService() {

    companion object {
        private const val TAG = "QuickAddTile"
        private const val OCR_ACTIVITY_REQUEST_CODE = 1001
    }

    // =========================================================
    // 生命週期：監聽磁貼狀態
    // =========================================================
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
                // 可選：subtitle 提示長按功能（部分 ROM 會顯示喺 label 下面）
                if (Build.VERSION.SDK_INT >= 29) {
                    subtitle = "點按記賬 · 長按OCR"
                }
                updateTile()
            }
        } catch (e: Exception) {
            Log.w(TAG, "updateTileUi failed: ${e.message}")
        }
    }

    // =========================================================
    // ★ 短按：原有邏輯（完全不改）
    // =========================================================
    override fun onClick() {
        super.onClick()
        try {
            val intent = buildLaunchIntentForMain(this, prefillNote = null).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivityAndCollapseCompat(intent)
        } catch (e: Exception) {
            Log.e(TAG, "onClick startActivity failed: ${e.message}", e)
            // 最後防線：直接 startActivity
            try {
                startActivity(
                    buildLaunchIntentForMain(this, prefillNote = null).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
            } catch (_: Exception) { }
        }
    }

    // =========================================================
    // ★ 長按：新增 OCR 觸發（Android 14+）
    // =========================================================
    override fun handleSecondaryClick(): Boolean {
        // 1. 震動回饋（提示用戶已觸發 OCR）
        vibrate()

        // 2. 啟動透明 Activity 請求 MediaProjection 授權
        return try {
            val intent = Intent(this, OcrCaptureActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivityAndCollapseCompat(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "handleSecondaryClick failed: ${e.message}", e)
            // 部分 ROM 喺 collapsed 狀態下唔畀 startActivityAndCollapse
            try {
                startActivity(Intent(this, OcrCaptureActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    // =========================================================
    // 兼容性封裝：startActivityAndCollapse
    // - API 34+：建議用 PendingIntent 版本（避免 deprecated）
    // - API < 34：用 Intent 版本
    // =========================================================
    @Suppress("DEPRECATION")
    private fun startActivityAndCollapseCompat(intent: Intent) {
        if (Build.VERSION.SDK_INT >= 34) {
            val pi = PendingIntent.getActivity(
                this,
                OCR_ACTIVITY_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            startActivityAndCollapse(pi)
        } else {
            startActivityAndCollapse(intent)
        }
    }

    // =========================================================
    // 震動回饋
    // =========================================================
    private fun vibrate() {
        try {
            val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= 31) {
                val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator == null || !vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(30L, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(30L)
            }
        } catch (e: Exception) {
            Log.w(TAG, "vibrate failed: ${e.message}")
        }
    }
}
