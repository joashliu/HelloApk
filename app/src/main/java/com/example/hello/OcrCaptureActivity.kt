package com.example.hello

import android.app.Activity
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

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
            Toast.makeText(this, "需要螢幕截圖權限先可以 OCR", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "無法請求權限", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
