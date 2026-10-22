package com.example.hello

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

/**
 * OCR 授權中介 Activity
 *
 * 用途：TileService 唔可以直接 startActivityForResult 請求 MediaProjection 授權，
 *      所以用呢個透明 Activity 做中介，用戶只會見到系統「允許螢幕截圖」對話框。
 *
 * 流程：
 *   QuickAddTileService.handleSecondaryClick()
 *     → startActivity(OcrCaptureActivity)
 *     → 彈系統授權對話框
 *     → 用戶允許 → OcrForegroundService.start(...)
 *     → 用戶拒絕 → Toast「需要螢幕截圖權限先可以 OCR」
 *     → finish()
 */
class OcrCaptureActivity : ComponentActivity() {

    private val mediaProjectionManager by lazy {
        getSystemService(MediaProjectionManager::class.java)
    }

    private val requestCapture = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            OcrForegroundService.start(this, result.resultCode, result.data!!)
        } else {
            Toast.makeText(
                this,
                "需要螢幕截圖權限先可以 OCR",
                Toast.LENGTH_SHORT
            ).show()
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mediaProjectionManager == null) {
            Toast.makeText(this, "此裝置不支援螢幕截圖", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        try {
            requestCapture.launch(mediaProjectionManager.createScreenCaptureIntent())
        } catch (e: Exception) {
            Toast.makeText(this, "無法請求螢幕截圖權限", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
