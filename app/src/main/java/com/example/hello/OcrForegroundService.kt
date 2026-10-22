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

/**
 * 離線 OCR 前台服務
 *
 * 流程：
 *   1. startForeground（指定 mediaProjection 類型，Android 14+ 強制）
 *   2. getMediaProjection（由 OcrCaptureActivity 傳入授權 token）
 *   3. ImageReader + VirtualDisplay 截圖一次
 *   4. ML Kit 中文 OCR（bundled 離線模型）
 *   5. 正則提取 HKD 金額
 *   6. 開啟 MainActivity，帶 EXTRA_PREFILL_AMOUNT + EXTRA_OPEN_KEYBOARD
 *
 * 所有 OCR 運算喺 Dispatchers.Default 背景線程，唔阻塞主線程。
 */
class OcrForegroundService : Service() {

    companion object {
        private const val TAG = "OcrService"
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

        // ★ Android 14 (API 34) 強制：必須先 startForeground（帶類型），
        //   才可以呼叫 getMediaProjection()，否則 SecurityException。
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                startForeground(
                    NOTIFICATION_ID,
                    buildNotification(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                )
            } else {
                startForeground(NOTIFICATION_ID, buildNotification())
            }
        } catch (e: Exception) {
            Log.e(TAG, "startForeground failed: ${e.message}", e)
            stopSelf()
            return START_NOT_STICKY
        }

        val resultCode = intent?.getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED)
            ?: Activity.RESULT_CANCELED
        val resultData: Intent? = if (Build.VERSION.SDK_INT >= 33) {
            intent?.getParcelableExtra(EXTRA_RESULT_DATA, Intent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(EXTRA_RESULT_DATA)
        }

        if (resultCode != Activity.RESULT_OK || resultData == null) {
            finishWithToast("授權無效")
            return START_NOT_STICKY
        }

        val projectionManager = getSystemService(MediaProjectionManager::class.java)
        mediaProjection = try {
            projectionManager.getMediaProjection(resultCode, resultData)
        } catch (e: Exception) {
            Log.e(TAG, "getMediaProjection failed: ${e.message}", e)
            null
        }

        if (mediaProjection == null) {
            finishWithToast("取得 MediaProjection 失敗")
            return START_NOT_STICKY
        }

        // ★ Android 14+ 必須註冊 Callback，否則 getMediaProjection 後立即 stop
        mediaProjection!!.registerCallback(object : MediaProjection.Callback() {
            override fun onStop() {
                Log.d(TAG, "MediaProjection stopped by system")
                cleanup()
            }
        }, Handler(Looper.getMainLooper()))

        serviceScope.launch { performCaptureAndOcr() }
        return START_NOT_STICKY
    }

    private suspend fun performCaptureAndOcr() {
        try {
            val bitmap = captureScreenOnce()
            if (bitmap == null) {
                finishWithToast("截圖失敗")
                return
            }
            val amount = recognizeHkdAmount(bitmap)
            bitmap.recycle()

            if (amount != null && amount > 0.0) {
                launchMainWithAmount(amount)
            } else {
                finishWithToast("未檢測到HKD金額")
            }
        } catch (e: Exception) {
            Log.e(TAG, "performCaptureAndOcr failed: ${e.message}", e)
            finishWithToast("OCR 失敗：${e.message}")
        } finally {
            cleanup()
            stopSelf()
        }
    }

    /**
     * 單次截圖：ImageReader + VirtualDisplay
     * 取第一幀後立即 close，避免後續重複觸發。
     */
    private suspend fun captureScreenOnce(): Bitmap? = suspendCancellableCoroutine { cont ->
        val dm = getSystemService(DisplayManager::class.java)
        val display = dm.getDisplay(Display.DEFAULT_DISPLAY)
        val width = display.mode.physicalWidth
        val height = display.mode.physicalHeight
        val dpi = resources.displayMetrics.densityDpi

        val reader = try {
            ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
        } catch (e: Exception) {
            Log.e(TAG, "ImageReader.newInstance failed: ${e.message}", e)
            if (cont.isActive) cont.resume(null)
            return@suspendCancellableCoroutine
        }
        imageReader = reader

        var resumed = false

        reader.setOnImageAvailableListener({ r ->
            if (resumed) return@setOnImageAvailableListener
            val image = r.acquireLatestImage() ?: return@setOnImageAvailableListener
            try {
                val plane = image.planes[0]
                val buffer = plane.buffer
                val rowStride = plane.rowStride
                val pixelStride = plane.pixelStride
                val rowPadding = rowStride - pixelStride * width

                val paddedWidth = width + rowPadding / pixelStride
                val bmp = Bitmap.createBitmap(paddedWidth, height, Bitmap.Config.ARGB_8888)
                bmp.copyPixelsFromBuffer(buffer)

                val cropped = if (rowPadding > 0) {
                    Bitmap.createBitmap(bmp, 0, 0, width, height).also {
                        if (it !== bmp) bmp.recycle()
                    }
                } else bmp

                resumed = true
                if (cont.isActive) cont.resume(cropped)
            } catch (e: Exception) {
                Log.e(TAG, "decode frame failed: ${e.message}", e)
                if (!resumed) {
                    resumed = true
                    if (cont.isActive) cont.resume(null)
                }
            } finally {
                try { image.close() } catch (_: Exception) {}
            }
        }, Handler(Looper.getMainLooper()))

        try {
            virtualDisplay = mediaProjection?.createVirtualDisplay(
                "ocr_capture",
                width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                reader.surface, null, null
            )
        } catch (e: Exception) {
            Log.e(TAG, "createVirtualDisplay failed: ${e.message}", e)
            if (!resumed) {
                resumed = true
                if (cont.isActive) cont.resume(null)
            }
            return@suspendCancellableCoroutine
        }

        cont.invokeOnCancellation {
            try { reader.close() } catch (_: Exception) {}
        }
    }

    /**
     * ML Kit 中文 OCR + 正則提取 HKD 金額
     * 支援格式：
     *   HKD 12.5
     *   HKD: 1,234.56
     *   hkd 1234
     *   HKD $88
     *   HKD＄99.9
     */
    private suspend fun recognizeHkdAmount(bitmap: Bitmap): Double? {
        return withContext(Dispatchers.Default) {
            try {
                val recognizer = TextRecognition.getClient(
                    ChineseTextRecognizerOptions.Builder().build()
                )
                val image = InputImage.fromBitmap(bitmap, 0)
                val result = recognizer.process(image).await()
                val fullText = result.text
                Log.d(TAG, "OCR text: $fullText")

                val regex = Regex(
                    """HKD\s*[:：]?\s*[${'$'}＄]?\s*([0-9]{1,3}(?:,[0-9]{3})*(?:\.[0-9]{1,2})?|[0-9]+(?:\.[0-9]{1,2})?)""",
                    RegexOption.IGNORE_CASE
                )
                val match = regex.find(fullText) ?: return@withContext null
                val raw = match.groupValues.getOrNull(1) ?: return@withContext null
                raw.replace(",", "").toDoubleOrNull()?.takeIf { it > 0.0 }
            } catch (e: Exception) {
                Log.e(TAG, "recognizeHkdAmount failed: ${e.message}", e)
                null
            }
        }
    }

    /**
     * 啟動 MainActivity，帶 OCR 金額預填 + 彈鍵盤
     * 復用現有 EXTRA_PREFILL_AMOUNT / EXTRA_OPEN_KEYBOARD 機制
     */
    private fun launchMainWithAmount(amount: Double) {
        val launch = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_OPEN_KEYBOARD, true)
            putExtra(MainActivity.EXTRA_PREFILL_AMOUNT, amount)
        }
        try {
            startActivity(launch)
        } catch (e: Exception) {
            Log.e(TAG, "startActivity MainActivity failed: ${e.message}", e)
        }
    }

    private fun cleanup() {
        try { virtualDisplay?.release() } catch (_: Exception) {}
        virtualDisplay = null
        try { imageReader?.close() } catch (_: Exception) {}
        imageReader = null
        try { mediaProjection?.stop() } catch (_: Exception) {}
        mediaProjection = null
    }

    override fun onDestroy() {
        cleanup()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < 26) return
        try {
            val nm = getSystemService(NotificationManager::class.java)
            val ch = NotificationChannel(
                CHANNEL_ID, "OCR 截圖", NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "離線 OCR 掃描畫面用"
                setShowBadge(false)
            }
            nm.createNotificationChannel(ch)
        } catch (_: Exception) {}
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentTitle("正在掃描畫面…")
            .setContentText("本地 OCR 處理中，請稍候")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun finishWithToast(msg: String) {
        serviceScope.launch {
            withContext(Dispatchers.Main) {
                try {
                    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                } catch (_: Exception) {}
            }
            stopSelf()
        }
    }
}
