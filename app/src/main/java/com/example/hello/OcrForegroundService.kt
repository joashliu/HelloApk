package com.example.hello

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class OcrForegroundService : Service() {

    companion object {
        private const val CHANNEL_ID = "ocr_capture"
        private const val NOTIFICATION_ID = 3001
        private const val EXTRA_RESULT_CODE = "extra_result_code"
        private const val EXTRA_RESULT_DATA = "extra_result_data"

        fun start(context: Context, resultCode: Int, data: Intent) {
            val intent = Intent(context, OcrForegroundService::class.java).apply {
                putExtra(EXTRA_RESULT_CODE, resultCode)
                putExtra(EXTRA_RESULT_DATA, data)
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannel()
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                startForeground(NOTIFICATION_ID, buildNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
            } else {
                startForeground(NOTIFICATION_ID, buildNotification())
            }
        } catch (e: Exception) {
            stopSelf(); return START_NOT_STICKY
        }

        val resultCode = intent?.getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED) ?: Activity.RESULT_CANCELED
        val resultData: Intent? = if (Build.VERSION.SDK_INT >= 33) {
            intent?.getParcelableExtra(EXTRA_RESULT_DATA, Intent::class.java)
        } else {
            @Suppress("DEPRECATION") intent?.getParcelableExtra(EXTRA_RESULT_DATA)
        }

        if (resultCode != Activity.RESULT_OK || resultData == null) {
            finishWithToast("授權無效"); return START_NOT_STICKY
        }

        val projectionManager = getSystemService(MediaProjectionManager::class.java)
        mediaProjection = projectionManager.getMediaProjection(resultCode, resultData)
        
        mediaProjection?.registerCallback(object : MediaProjection.Callback() {
            override fun onStop() { cleanup() }
        }, Handler(Looper.getMainLooper()))

        serviceScope.launch { performCaptureAndOcr() }
        return START_NOT_STICKY
    }

    private suspend fun performCaptureAndOcr() {
        try {
            val bitmap = captureScreenOnce()
            if (bitmap == null) { finishWithToast("截圖失敗"); return }
            val amount = recognizeHkdAmount(bitmap)
            bitmap.recycle()

            if (amount != null) {
                launchMainWithAmount(amount)
            } else {
                finishWithToast("未檢測到HKD金額")
            }
        } catch (e: Exception) {
            finishWithToast("OCR 失敗")
        } finally {
            cleanup()
            stopSelf()
        }
    }

    private suspend fun captureScreenOnce(): Bitmap? = suspendCancellableCoroutine { cont ->
        val dm = getSystemService(DisplayManager::class.java)
        val display = dm.getDisplay(Display.DEFAULT_DISPLAY)
        val width = display.mode.physicalWidth
        val height = display.mode.physicalHeight
        val dpi = resources.displayMetrics.densityDpi

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
        
        imageReader?.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
            try {
                val plane = image.planes[0]
                val buffer = plane.buffer
                val rowStride = plane.rowStride
                val pixelStride = plane.pixelStride
                val rowPadding = rowStride - pixelStride * width
                val paddedWidth = width + rowPadding / pixelStride
                
                val bmp = Bitmap.createBitmap(paddedWidth, height, Bitmap.Config.ARGB_8888)
                bmp.copyPixelsFromBuffer(buffer)
                val cropped = if (rowPadding > 0) Bitmap.createBitmap(bmp, 0, 0, width, height).also { bmp.recycle() } else bmp
                
                if (cont.isActive) cont.resume(cropped)
            } catch (e: Exception) {
                if (cont.isActive) cont.resume(null)
            } finally {
                image.close()
            }
        }, Handler(Looper.getMainLooper()))

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ocr_capture", width, height, dpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface, null, null
        )
    }

    private suspend fun recognizeHkdAmount(bitmap: Bitmap): Double? = withContext(Dispatchers.Default) {
        try {
            val recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
            val result = recognizer.process(InputImage.fromBitmap(bitmap, 0)).await()
            val regex = Regex("""HKD\s*[:：]?\s*[${'$'}＄]?\s*([0-9]{1,3}(?:,[0-9]{3})*(?:\.[0-9]{1,2})?|[0-9]+(?:\.[0-9]{1,2})?)""", RegexOption.IGNORE_CASE)
            val match = regex.find(result.text) ?: return@withContext null
            match.groupValues.getOrNull(1)?.replace(",", "")?.toDoubleOrNull()?.takeIf { it > 0.0 }
        } catch (e: Exception) { null }
    }

    private fun launchMainWithAmount(amount: Double) {
        val launch = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_OPEN_KEYBOARD, true)
            putExtra(MainActivity.EXTRA_PREFILL_AMOUNT, amount)
        }
        startActivity(launch)
    }

    private fun cleanup() {
        virtualDisplay?.release(); virtualDisplay = null
        imageReader?.close(); imageReader = null
        mediaProjection?.stop(); mediaProjection = null
    }

    override fun onDestroy() { cleanup(); serviceScope.cancel(); super.onDestroy() }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(NotificationChannel(CHANNEL_ID, "OCR 截圖", NotificationManager.IMPORTANCE_LOW))
        }
    }

    private fun buildNotification(): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentTitle("正在掃描畫面…")
            .setOngoing(true).build()

    private fun finishWithToast(msg: String) {
        serviceScope.launch { withContext(Dispatchers.Main) { Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show() }; stopSelf() }
    }
}
