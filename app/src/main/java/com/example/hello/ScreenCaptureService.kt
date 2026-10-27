package com.example.hello

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class ScreenCaptureService : Service() {
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    companion object {
        const val ACTION_START = "ACTION_START"
        const val EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE"
        const val EXTRA_RESULT_DATA = "EXTRA_RESULT_DATA"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_START) {
            startForegroundServiceNotification()
            
            val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0)
            val resultData: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_RESULT_DATA, Intent::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_RESULT_DATA)
            }

            if (resultCode != 0 && resultData != null) {
                captureSingleFrame(resultCode, resultData)
            } else {
                stopSelfAndCleanup()
            }
        }
        return START_NOT_STICKY
    }

    private fun startForegroundServiceNotification() {
        val channelId = "screen_capture_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "屏幕捕獲", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("正在識別畫面金額...")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .build()
        startForeground(3001, notification)
    }

    private fun captureSingleFrame(resultCode: Int, resultData: Intent) {
        val mpm = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = mpm.getMediaProjection(resultCode, resultData)

        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val density = metrics.densityDpi

        // 建立 ImageReader 準備接收畫面
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
        
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenCapture", width, height, density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface, null, null
        )

        imageReader?.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage()
            if (image != null) {
                // 1. 成功獲取一幀，立即移除監聽器，防止連續截圖
                imageReader?.setOnImageAvailableListener(null, null)

                // 2. 將 Image 轉換為 Bitmap
                val planes = image.planes
                val buffer = planes[0].buffer
                val pixelStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride
                val rowPadding = rowStride - pixelStride * width
                
                val bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
                bitmap.copyPixelsFromBuffer(buffer)
                val croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height)
                
                image.close()

                // 3. 處理 OCR，然後銷毀 Service
                processBitmapAndStop(croppedBitmap)
            }
        }, null)
    }

    private fun processBitmapAndStop(bitmap: Bitmap) {
        scope.launch {
            val amounts = OcrExtractor.extractAmountFromBitmap(bitmap)
            bitmap.recycle() // 立即釋放 Bitmap

            if (amounts.isNotEmpty()) {
                val bestAmount = amounts.maxOrNull() ?: amounts[0]
                // 借用你原有嘅 NotificationCaptureBridge 將金額推入記帳系統
                withContext(Dispatchers.Main) {
                    NotificationCaptureBridge.pushCaptured("畫面識別", bestAmount, "飲食")
                }
            } else {
                // 可透過 Broadcast 或 Bridge 提示用戶 OCR 失敗
            }
            // 4. 徹底銷毀自己
            stopSelfAndCleanup()
        }
    }

    private fun stopSelfAndCleanup() {
        virtualDisplay?.release()
        imageReader?.close()
        mediaProjection?.stop()
        scope.cancel()
        stopForeground(true)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() {
        super.onDestroy()
        stopSelfAndCleanup()
    }
}
