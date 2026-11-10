package com.example.hello

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.compose.ui.unit.Constraints
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.isActive
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.ui.window.Popup
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.layout
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.BorderStroke
import android.app.Application
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.view.animation.AlphaAnimation
import android.util.TypedValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import java.util.Locale
import java.util.Random
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap

// ===== 由通知／快捷入口啟動時嘅標記（top-level state） =====
private val keyboardLaunchToken = mutableStateOf(0)
private val keyboardPrefillNote = mutableStateOf<String?>(null)
private val keyboardPrefillAmount = mutableStateOf<Double?>(null)
private val keyboardPrefillCategory = mutableStateOf<String?>(null)
private val keyboardPrefillNotifId = mutableStateOf<Int?>(null)
private val keyboardPrefillFluidCardId = mutableStateOf<String?>(null)
// 🟢 桌面 Shortcut / 外部入口觸發語音記帳
private val voiceLaunchToken = mutableStateOf(0)

// 🟢 啟動狀態：等 Firestore 第一次回傳先收 Splash
object AppStartupState {
    @Volatile var isFirstDataReady = false
}

// ===== 供通知顯示嘅最近項目資訊 =====
@Immutable
data class QuickNoteInfo(
    val note: String,
    val category: String,
    val amount: Double
)
private val topQuickNotesInfo = mutableStateOf<List<QuickNoteInfo>>(emptyList())

// ===== 最近捕獲嘅手機消費通知（例如：地鐵 · 4.9） =====
@Immutable
data class CapturedNotification(
    val note: String,
    val amount: Double,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)
private val topLatestCapturedNotification = mutableStateOf<CapturedNotification?>(null)

object NotificationCaptureBridge {

    private var appContext: Context? = null
    private val confirmHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var pendingConfirmRunnable: Runnable? = null

    @Volatile private var lastNote: String? = null
    @Volatile private var lastAmount: Double = 0.0
    @Volatile private var lastPushMs: Long = 0L

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun pushCaptured(note: String, amount: Double, category: String) {
        if (note.isBlank() || amount <= 0.0) return
        val trimmed = note.trim()
        val now = System.currentTimeMillis()
        if (lastNote == trimmed && abs(lastAmount - amount) < 0.01 && (now - lastPushMs) < 30_000L) return
        lastNote = trimmed
        lastAmount = amount
        lastPushMs = now
        val captured = CapturedNotification(
            note = trimmed, amount = amount, category = category, timestamp = now
        )
        topLatestCapturedNotification.value = captured
        val ctx = appContext ?: return
        try { refreshQuickAddNotification(ctx) } catch (_: Exception) {}
        // 可選 Overlay（有權限且開關開先顯示；否則只走原有通知）
        try { CaptureOverlayController.show(ctx, captured) } catch (_: Exception) {}
        pendingConfirmRunnable?.let { confirmHandler.removeCallbacks(it) }
        val runnable = Runnable {
            pendingConfirmRunnable = null
            val latest = topLatestCapturedNotification.value ?: return@Runnable
            try { showCaptureConfirmNotification(ctx, latest) } catch (_: Exception) {}
        }
        pendingConfirmRunnable = runnable
        confirmHandler.postDelayed(runnable, 800L)
    }

    fun clear() {
        pendingConfirmRunnable?.let { confirmHandler.removeCallbacks(it) }
        pendingConfirmRunnable = null
        topLatestCapturedNotification.value = null
        lastNote = null
        lastAmount = 0.0
        lastPushMs = 0L
        try { CaptureOverlayController.dismiss() } catch (_: Exception) {}
        appContext?.let {
            try { refreshQuickAddNotification(it) } catch (_: Exception) {}
        }
    }

    fun formatAmountForLabel(v: Double): String =
        String.format(java.util.Locale.US, "%.2f", v)
            .trimEnd('0').trimEnd('.').ifEmpty { "0" }
}

// ===== 可選：消費通知底部 Overlay 浮窗（需「顯示在其他應用之上」）=====
private const val PREF_OVERLAY_CAPTURE_ENABLED = "overlay_capture_enabled"
private const val OVERLAY_AUTO_DISMISS_MS = 8_000L

// 未完成入帳鍵盤草稿（名稱／金額／開關狀態）
private const val PREF_KB_OPEN = "keyboard_draft_open"
private const val PREF_KB_AMOUNT = "keyboard_draft_amount"
private const val PREF_KB_NOTE = "keyboard_draft_note"
private const val PREF_KB_CATEGORY = "keyboard_draft_category"
private const val PREF_KB_EDITING_NOTE = "keyboard_draft_editing_note"
private const val PREF_KB_EDIT_ID = "keyboard_draft_edit_id"
private const val PREF_KB_TIMESTAMP = "keyboard_draft_timestamp"
private const val PREF_KB_CURRENCY = "keyboard_draft_currency"
private const val PREF_KB_RATE = "keyboard_draft_rate"
private const val PREF_KB_FOCUS = "keyboard_draft_focus"

fun loadKeyboardDraft(context: Context): Pair<Boolean, KeyboardState> {
    val p = context.getSharedPreferences("ledger_prefs", Context.MODE_PRIVATE)
    val open = p.getBoolean(PREF_KB_OPEN, false)
    val state = KeyboardState(
        amountText = p.getString(PREF_KB_AMOUNT, "") ?: "",
        noteText = p.getString(PREF_KB_NOTE, "") ?: "",
        category = p.getString(PREF_KB_CATEGORY, "飲食") ?: "飲食",
        editingNote = p.getBoolean(PREF_KB_EDITING_NOTE, false),
        editingRecordId = p.getString(PREF_KB_EDIT_ID, null),
        timestamp = p.getLong(PREF_KB_TIMESTAMP, System.currentTimeMillis()),
        selectAmountOnInput = false,
        currency = p.getString(PREF_KB_CURRENCY, "HKD") ?: "HKD",
        exchangeRateText = p.getString(PREF_KB_RATE, "0.052") ?: "0.052",
        focusedField = p.getString(PREF_KB_FOCUS, "amount") ?: "amount"
    )
    return open to state
}

fun saveKeyboardDraft(context: Context, open: Boolean, state: KeyboardState) {
    context.getSharedPreferences("ledger_prefs", Context.MODE_PRIVATE).edit()
        .putBoolean(PREF_KB_OPEN, open)
        .putString(PREF_KB_AMOUNT, state.amountText)
        .putString(PREF_KB_NOTE, state.noteText)
        .putString(PREF_KB_CATEGORY, state.category)
        .putBoolean(PREF_KB_EDITING_NOTE, state.editingNote)
        .putString(PREF_KB_EDIT_ID, state.editingRecordId)
        .putLong(PREF_KB_TIMESTAMP, state.timestamp)
        .putString(PREF_KB_CURRENCY, state.currency)
        .putString(PREF_KB_RATE, state.exchangeRateText)
        .putString(PREF_KB_FOCUS, state.focusedField)
        .apply()
}

fun clearKeyboardDraftPrefs(context: Context) {
    context.getSharedPreferences("ledger_prefs", Context.MODE_PRIVATE).edit()
        .putBoolean(PREF_KB_OPEN, false)
        .putString(PREF_KB_AMOUNT, "")
        .putString(PREF_KB_NOTE, "")
        .putString(PREF_KB_EDIT_ID, null)
        .putBoolean(PREF_KB_EDITING_NOTE, false)
        .apply()
}

fun isOverlayCapturePrefEnabled(context: Context): Boolean =
    context.getSharedPreferences("ledger_prefs", Context.MODE_PRIVATE)
        .getBoolean(PREF_OVERLAY_CAPTURE_ENABLED, true)

fun setOverlayCapturePrefEnabled(context: Context, enabled: Boolean) {
    context.getSharedPreferences("ledger_prefs", Context.MODE_PRIVATE)
        .edit().putBoolean(PREF_OVERLAY_CAPTURE_ENABLED, enabled).apply()
}

fun canDrawCaptureOverlay(context: Context): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Settings.canDrawOverlays(context) else true

/** 輕量 Toast 風格浮窗：非搶焦、單實例、5–10 秒自動淡出 */
object CaptureOverlayController {
    private var appContext: Context? = null
    private var overlayView: View? = null
    private val mainHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var dismissRunnable: Runnable? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun isShowing(): Boolean = overlayView != null

    fun dismiss(fade: Boolean = false) {
        val ctx = appContext ?: return
        val view = overlayView ?: return
        dismissRunnable?.let { mainHandler.removeCallbacks(it) }
        dismissRunnable = null
        val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        if (fade) {
            val anim = AlphaAnimation(1f, 0f).apply {
                duration = 220
                fillAfter = true
            }
            view.startAnimation(anim)
            mainHandler.postDelayed({
                try { wm?.removeView(view) } catch (_: Exception) {}
                if (overlayView === view) overlayView = null
            }, 230)
        } else {
            try { wm?.removeView(view) } catch (_: Exception) {}
            overlayView = null
        }
    }

    fun show(context: Context, captured: CapturedNotification) {
        val ctx = context.applicationContext
        appContext = ctx
        // 權限或開關關閉 → 完全唔顯示，保留原有通知流程
        if (!canDrawCaptureOverlay(ctx) || !isOverlayCapturePrefEnabled(ctx)) return

        // 防重複：已有浮窗先拆走
        dismiss(fade = false)

        val density = ctx.resources.displayMetrics.density
        fun dp(v: Int) = (v * density + 0.5f).toInt()

        val panelBg = GradientDrawable().apply {
            setColor(0xF0FFFFFF.toInt())
            cornerRadius = dp(16).toFloat()
            setStroke(dp(1), 0x22000000)
        }
        val primaryBtnBg = GradientDrawable().apply {
            setColor(0xFF2563EB.toInt())
            cornerRadius = dp(10).toFloat()
        }
        val ignoreBtnBg = GradientDrawable().apply {
            setColor(0xFFF1F5F9.toInt())
            cornerRadius = dp(10).toFloat()
        }

        val amountStr = NotificationCaptureBridge.formatAmountForLabel(captured.amount)
        val root = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = panelBg
            elevation = dp(8).toFloat()
        }
        val title = TextView(ctx).apply {
            text = "檢測到消費"
            setTextColor(0xFF0F172A.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        val body = TextView(ctx).apply {
            text = "${captured.note} · HKD $amountStr"
            setTextColor(0xFF334155.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            setPadding(0, dp(4), 0, dp(12))
        }
        val btnRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        val ignoreBtn = TextView(ctx).apply {
            text = "忽略"
            gravity = Gravity.CENTER
            setTextColor(0xFF64748B.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            background = ignoreBtnBg
            setPadding(dp(12), dp(10), dp(12), dp(10))
            isClickable = true
            isFocusable = true
        }
        val addBtn = TextView(ctx).apply {
            text = "記賬"
            gravity = Gravity.CENTER
            setTextColor(0xFFFFFFFF.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            background = primaryBtnBg
            setPadding(dp(12), dp(10), dp(12), dp(10))
            isClickable = true
            isFocusable = true
        }
        val ignoreLp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginEnd = dp(8)
        }
        val addLp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        btnRow.addView(ignoreBtn, ignoreLp)
        btnRow.addView(addBtn, addLp)
        root.addView(title)
        root.addView(body)
        root.addView(btnRow)

        val container = FrameLayout(ctx).apply {
            setPadding(dp(16), 0, dp(16), dp(28))
            addView(
                root,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
            )
        }

        ignoreBtn.setOnClickListener {
            dismiss(fade = true)
        }
        addBtn.setOnClickListener {
            dismiss(fade = false)
            try {
                val intent = buildLaunchIntentForMain(
                    ctx, captured.note, captured.amount, captured.category, null
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                ctx.startActivity(intent)
            } catch (_: Exception) {}
        }

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            // 非搶焦：唔搶鍵盤／唔擋住其他 App 操作
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM
            y = 0
            // 只接收浮窗自身點擊
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }

        val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: return
        try {
            wm.addView(container, params)
            overlayView = container
            val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 180 }
            container.startAnimation(fadeIn)
            val r = Runnable { dismiss(fade = true) }
            dismissRunnable = r
            mainHandler.postDelayed(r, OVERLAY_AUTO_DISMISS_MS)
        } catch (_: Exception) {
            overlayView = null
        }
    }
}

// 🟢 升級 Channel ID，強制系統重置橫幅權限
private const val QUICK_ADD_CHANNEL_ID = "ledger_quick_add_v2"
private const val CONFIRM_CHANNEL_ID = "ledger_capture_confirm_v5"
private const val QUICK_ADD_NOTIFICATION_ID = 1001
private const val CONFIRM_NOTIFICATION_BASE_ID = 2000

fun ensureNotificationChannels(context: Context) {
    if (Build.VERSION.SDK_INT < 26) return
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
    try {
        val staleIds = listOf(
            "ledger_capture_confirm", "ledger_capture_confirm_v2",
            "ledger_capture_confirm_v3", "ledger_capture_confirm_v3_old",
            "ledger_capture_confirm_v4", "ledger_quick_add"
        )
        for (oldId in staleIds) {
            try { nm.deleteNotificationChannel(oldId) } catch (_: Exception) {}
        }
    } catch (_: Exception) {}

    val quick = NotificationChannel(
        QUICK_ADD_CHANNEL_ID, "快速記帳 (常駐)", NotificationManager.IMPORTANCE_LOW
    ).apply {
        description = "常駐快速記賬入口"
        setShowBadge(false)
    }
    val confirm = NotificationChannel(
        CONFIRM_CHANNEL_ID, "消費確認 (強制橫幅)", NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "檢測到消費通知時，強制橫幅彈出詢問"
        enableVibration(true)
        enableLights(true)
        setBypassDnd(true)
    }
    nm.createNotificationChannel(quick)
    nm.createNotificationChannel(confirm)
}

fun cleanupStaleConfirmNotifications(context: Context) {
    try {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val knownIds = listOf(CONFIRM_NOTIFICATION_BASE_ID, QUICK_ADD_NOTIFICATION_ID)
        for (id in knownIds) {
            try { nm.cancel(id) } catch (_: Exception) {}
        }
        for (id in 2000..2100) {
            try { nm.cancel(id) } catch (_: Exception) {}
        }
    } catch (_: Exception) {}
}

fun buildLaunchIntentForMain(
    context: Context, prefillNote: String?,
    prefillAmount: Double? = null, prefillCategory: String? = null,
    cancelNotifId: Int? = null
): Intent {
    return Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra(MainActivity.EXTRA_OPEN_KEYBOARD, true)
        if (!prefillNote.isNullOrBlank()) putExtra(MainActivity.EXTRA_PREFILL_NOTE, prefillNote)
        if (!prefillCategory.isNullOrBlank()) putExtra(MainActivity.EXTRA_PREFILL_CATEGORY, prefillCategory)
        if (prefillAmount != null && prefillAmount > 0.0) putExtra(MainActivity.EXTRA_PREFILL_AMOUNT, prefillAmount)
        if (cancelNotifId != null) putExtra(MainActivity.EXTRA_CANCEL_NOTIF_ID, cancelNotifId)
    }
}

fun refreshQuickAddNotification(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= 33) {
            val granted = context.checkSelfPermission("android.permission.POST_NOTIFICATIONS") ==
                PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        ensureNotificationChannels(context)

        val normalIntent = buildLaunchIntentForMain(context, prefillNote = null)
        val normalPi = PendingIntent.getActivity(
            context, 0, normalIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val infos = topQuickNotesInfo.value
        val first = infos.firstOrNull()

        val builder = NotificationCompat.Builder(context, QUICK_ADD_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_input_add)
            .setContentTitle("快速記帳")
            .setContentText("撳一下即開鍵盤記一筆")
            .setContentIntent(normalPi)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        if (first != null && first.note.isNotBlank()) {
            val noteIntent = buildLaunchIntentForMain(context, first.note, first.amount, first.category)
            val notePi = PendingIntent.getActivity(
                context, 1, noteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(android.R.drawable.ic_menu_edit, first.note, notePi)
        }

        val captured = topLatestCapturedNotification.value
        if (captured != null && captured.note.isNotBlank()) {
            val todayStart = getTimestampMeta(System.currentTimeMillis()).startOfDayMillis
            val capturedStart = getTimestampMeta(captured.timestamp).startOfDayMillis
            if (capturedStart == todayStart) {
                val label = "${captured.note} · ${NotificationCaptureBridge.formatAmountForLabel(captured.amount)}"
                val capturedIntent = buildLaunchIntentForMain(context, captured.note, captured.amount, captured.category)
                val capturedPi = PendingIntent.getActivity(
                    context, 2, capturedIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                builder.addAction(android.R.drawable.ic_menu_recent_history, label, capturedPi)
            }
        }

        nm.notify(QUICK_ADD_NOTIFICATION_ID, builder.build())
    } catch (_: Exception) {}
}

fun showCaptureConfirmNotification(context: Context, captured: CapturedNotification) {
    try {
        if (Build.VERSION.SDK_INT >= 33) {
            val granted = context.checkSelfPermission("android.permission.POST_NOTIFICATIONS") ==
                PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        ensureNotificationChannels(context)

        val notifId = CONFIRM_NOTIFICATION_BASE_ID + (captured.timestamp % 10000).toInt()
        val amountStr = NotificationCaptureBridge.formatAmountForLabel(captured.amount)
        val title = "檢測到消費：${captured.note} · HKD $amountStr"

        val piFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val addIntent = buildLaunchIntentForMain(
            context, captured.note, captured.amount, captured.category, notifId
        )
        val addPi = PendingIntent.getActivity(context, notifId, addIntent, piFlags)

        val fullScreenPi = PendingIntent.getActivity(context, notifId + 100, addIntent, piFlags)

        val ignoreIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "com.example.hello.ACTION_IGNORE_CAPTURE"
            putExtra("notif_id", notifId)
        }
        val ignorePi = PendingIntent.getBroadcast(context, notifId + 10000, ignoreIntent, piFlags)

        val builder = NotificationCompat.Builder(context, CONFIRM_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText("是否新增這筆記錄？（分類：${captured.category}）")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("是否新增這筆記錄？\n\n📝 名稱：${captured.note}\n💰 金額：HKD $amountStr\n🏷 分類：${captured.category}")
            )
            .setContentIntent(addPi)
            .setFullScreenIntent(fullScreenPi, true)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .setTimeoutAfter(10 * 60 * 1000L)
            .addAction(android.R.drawable.ic_menu_save, "✅ 記賬", addPi)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "❌ 忽略", ignorePi)

        nm.notify(notifId, builder.build())
    } catch (_: Exception) {}
}

private val SWIPE_SPRING = spring<Float>(
    stiffness = Spring.StiffnessMediumLow,
    dampingRatio = Spring.DampingRatioNoBouncy
)

// ===== 配色（泥黃色風格）=====
val BRAND_PRIMARY = Color(0xFFB68D40)       // 泥黃色 (主色)
val BRAND_PRIMARY_DARK = Color(0xFF8B6A2A)  // 深泥黃色 (用於按壓或強烈對比)
val BRAND_PRIMARY_LIGHT = Color(0xFFF5EFE6) // 淺泥黃底色 (用於淡色標籤或背景)

// 頂部（TopStats／快速輸入）滑入 同 鍵盤展開：統一時長，同步完成
const val HEADER_KEYBOARD_EXPAND_DELAY_MS = 30L
const val HEADER_KEYBOARD_EXPAND_MS = 480
const val HEADER_KEYBOARD_COLLAPSE_MS = 420

// iOS systemGroupedBackground / secondarySystemGroupedBackground
val SURFACE_BG = Color(0xFFF2F2F7)
val SURFACE_CARD = Color(0xFFFFFFFF)
val SURFACE_ELEVATED = Color(0xFFE9E9EF)

// iOS label / secondaryLabel / tertiaryLabel
val TEXT_PRIMARY = Color(0xFF1C1C1E)
val TEXT_SECONDARY = Color(0xFF8E8E93)
val TEXT_TERTIARY = Color(0xFFC7C7CC)
val DIVIDER_COLOR = Color(0xFFE5E5EA)

val COLOR_INCOME = Color(0xFF047857)
val COLOR_EXPENSE = Color(0xFFB91C1C)

val CELL_BG_POS = Color(0xFFD1FAE5)
val CELL_BAR_POS = Color(0xFF34D399)
val CELL_BG_NEG = Color(0xFFFEE2E2)
val CELL_BAR_NEG = Color(0xFFF87171)

const val CLOUDINARY_CLOUD_NAME = "dfl59grn"
const val CLOUDINARY_UPLOAD_PRESET = "ledger_icons"
const val ICON_SIZE = 100

val NOTE_FONT_SIZE = 16.sp
val META_FONT_SIZE = 12.sp
val AMOUNT_FONT_SIZE = 18.sp
val STAT_AMOUNT_FONT_SIZE = 22.sp
val STAT_LABEL_FONT_SIZE = 11.sp
val NAV_HEIGHT = 60.dp
val NAV_TAB_WIDTH = 96.dp
val NAV_BOTTOM_PADDING = 20.dp
val ROW_ALT_COLOR = Color(0xFFF4F7FC)
const val FILTER_ANIM_MS = 250

@Immutable
data class CategoryStyle(val icon: ImageVector, val bgColor: Color, val fgColor: Color)

val CATEGORIES = listOf("收入", "娛樂", "家用", "飲食", "交通", "個人", "購物", "月費", "旅遊", "還錢")
val EXPENSE_CATEGORIES = CATEGORIES.filter { it != "收入" }
const val INCOME_CATEGORY = "收入"

val CATEGORY_STYLES: Map<String, CategoryStyle> = mapOf(
    "收入" to CategoryStyle(Icons.Default.TrendingUp, Color(0xFFD1FAE5), Color(0xFF065F46)),
    "娛樂" to CategoryStyle(Icons.Default.SportsEsports, Color(0xFFEDE9FE), Color(0xFF5B21B6)),
    "家用" to CategoryStyle(Icons.Default.Home, Color(0xFFCFFAFE), Color(0xFF155E75)),
    "飲食" to CategoryStyle(Icons.Default.Restaurant, Color(0xFFFFEDD5), Color(0xFF9A3412)),
    "交通" to CategoryStyle(Icons.Default.DirectionsBus, Color(0xFFDBEAFE), Color(0xFF1E40AF)),
    "個人" to CategoryStyle(Icons.Default.Person, Color(0xFFFCE7F3), Color(0xFF9D174D)),
    "購物" to CategoryStyle(Icons.Default.ShoppingCart, Color(0xFFFEF3C7), Color(0xFF854D0E)),
    "月費" to CategoryStyle(Icons.Default.CreditCard, Color(0xFFE2E8F0), Color(0xFF334155)),
    "旅遊" to CategoryStyle(Icons.Default.Flight, Color(0xFFCCFBF1), Color(0xFF115E59)),
    "還錢" to CategoryStyle(Icons.Default.Payments, Color(0xFFFFE4E6), Color(0xFFBE123C))
)

@Immutable
data class Record(
    val amount: Double = 0.0,
    val note: String = "",
    val category: String = "飲食",
    val timestamp: Long = System.currentTimeMillis(),
    val iconUrl: String = "",
    val id: String = ""
)

@Immutable
data class NavItem(val label: String, val icon: ImageVector)

@Immutable
data class KeyboardState(
    val amountText: String = "",
    val noteText: String = "",
    val category: String = "飲食",
    val editingNote: Boolean = false,
    val editingRecordId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val selectAmountOnInput: Boolean = false,
    val currency: String = "HKD",
    val exchangeRateText: String = "0.052",
    val focusedField: String = "amount"
)

@Immutable
data class AfterSaveHint(
    val recordId: String,
    val category: String,
    val monthTotal: Double
)

@Immutable
data class DateHeaderInfo(
    val year: Int,
    val month: Int,
    val day: Int,
    val weekdayChar: String,
    val isWeekend: Boolean,
    val dayTag: String?
)

@Immutable
data class FilterState(
    val modeOn: Boolean = false,
    val category: String? = null,
    val month: String? = null,
    val search: String = "",
    val showFuture: Boolean = false,
    val startTimeMin: Int = 0,
    val endTimeMin: Int = 1440,
)

@Immutable
data class LedgerUiState(
    val loading: Boolean = true,
    val ledgerRecords: List<Record> = emptyList(),
    val groupedByDate: List<Pair<String, List<Record>>> = emptyList(),
    val hasIncome: Boolean = false,
    val hasExpense: Boolean = false,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val isExactNoteFilter: Boolean = false,
    val topNotes: List<Pair<String, Int>> = emptyList(),
    val recentAmountByNote: Map<String, Double> = emptyMap(),
    val noteIconMap: Map<String, String> = emptyMap(),
    val allNoteNames: List<String> = emptyList(),
    val noteCategoryMap: Map<String, String> = emptyMap(),
    val noteUsageCount: Map<String, Int> = emptyMap(),
    val availableMonths: List<String> = emptyList(),
    val visibleCategories: List<String> = emptyList(),
    val topQuickNotesInfo: List<QuickNoteInfo> = emptyList(),
    val hiddenQuickNotes: Set<String> = emptySet(),
)

@Immutable
data class NoteIndex(
    val recentAmountByNote: Map<String, Double> = emptyMap(),
    val noteIconMap: Map<String, String> = emptyMap(),
    val allNoteNames: List<String> = emptyList(),
    val noteCategoryMap: Map<String, String> = emptyMap(),
    val noteUsageCount: Map<String, Int> = emptyMap(),
    val topNotes: List<Pair<String, Int>> = emptyList(),
    // ★ 精簡：移除 topQuickNotesInfo（改由 computeLedgerUiState 現算）
)

@Immutable
data class CompareUiState(
    val categoryMonthSum: Map<String, Map<String, Double>> = emptyMap(),
    val sortedCategories: List<String> = emptyList(),
    val displayCategories: List<String> = emptyList(),
    val incomeA: Double = 0.0,
    val incomeB: Double = 0.0,
    val totalExpenseA: Double = 0.0,
    val totalExpenseB: Double = 0.0,
    val balanceA: Double = 0.0,
    val balanceB: Double = 0.0,
    val histogramData: Map<String, List<Double>> = emptyMap(),
    val monthlyKeysThisYear: List<String> = emptyList(),
)

@Immutable
data class CalendarUiState(
    val monthKey: String = "",
    val monthAllRecords: List<Record> = emptyList(),
    val visibleCategories: List<String> = emptyList(),
    val monthRecords: List<Record> = emptyList(),
    val recordsByDay: Map<Int, List<Record>> = emptyMap(),
    val maxAbsNet: Double = 0.0,
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val totalNet: Double = 0.0,
    val itemCount: Int = 0,
    val avgPerItem: Double = 0.0,
    val avgPerDay: Double = 0.0,
)

// =========================================================
//  ★ 資料透視表 - 數據類（新增）
// =========================================================
@Immutable
data class PivotCell(
    val total: Double = 0.0,
    val count: Int = 0
)

@Immutable
data class PivotRow(
    val category: String,
    val note: String,
    val isCategory: Boolean,
    val cells: Map<String, PivotCell> = emptyMap(),
    val rowTotal: Double = 0.0,
)

@Immutable
data class PivotUiState(
    val months: List<String> = emptyList(),        // 升序：1月 → 至今／12月
    val rows: List<PivotRow> = emptyList(),
    val monthTotals: Map<String, Double> = emptyMap(),
    val grandTotal: Double = 0.0,
    val loading: Boolean = true,
    val availableYears: List<Int> = emptyList(),   // 有資料的年份，小→大
    val selectedYear: Int = 0,
    val categories: List<String> = emptyList(),    // 顯示順序，供頂部 chips
    val noteIconMap: Map<String, String> = emptyMap(),
)

@Immutable
data class WeeklyExpenseDay(
    val dayOfMonth: Int,
    val weekdayChar: String,
    val amount: Double,
    val isToday: Boolean
)

@Immutable
private data class TimestampMeta(
    val year: Int,
    val month: Int,
    val dayOfMonth: Int,
    val dayOfWeek: Int,
    val hour: Int,
    val minute: Int,
    val secOfDay: Int,
    val startOfDayMillis: Long,
    val dateKey: String,
    val monthKey: String,
    val datePart: String,
    val timePart: String,
    val weekTimePrefix: String
)

private val WEEKDAY_LABELS = arrayOf("週日", "週一", "週二", "週三", "週四", "週五", "週六")
private val WEEKDAY_CHARS = arrayOf("日", "一", "二", "三", "四", "五", "六")
private val timestampMetaCache = ConcurrentHashMap<Long, TimestampMeta>(2048)
private val dominantColorCache = ConcurrentHashMap<String, Color>(128)
private val noteTextWidthCache = ConcurrentHashMap<String, Float>()

private val threadLocalCal = object : java.lang.ThreadLocal<Calendar>() {
    override fun initialValue(): Calendar = Calendar.getInstance()
}

private fun pad2(n: Int): String = if (n < 10) "0$n" else n.toString()

private fun getTimestampMeta(timestamp: Long): TimestampMeta {
    timestampMetaCache[timestamp]?.let { return it }
    val cal = threadLocalCal.get()!!
    cal.timeInMillis = timestamp
    val y = cal.get(Calendar.YEAR)
    val m = cal.get(Calendar.MONTH) + 1
    val d = cal.get(Calendar.DAY_OF_MONTH)
    val dow = cal.get(Calendar.DAY_OF_WEEK)
    val hh = cal.get(Calendar.HOUR_OF_DAY)
    val mm = cal.get(Calendar.MINUTE)
    val secOfDay = hh * 3600 + mm * 60

    cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
    val startOfDay = cal.timeInMillis

    val mStr = pad2(m)
    val dStr = pad2(d)
    val hhStr = pad2(hh)
    val mmStr = pad2(mm)
    val wk = WEEKDAY_LABELS[dow - 1]
    val monthKey = "$y-$mStr"
    val dateKey = "$monthKey-$dStr"
    val datePart = "${m}月${d}日 $wk"
    val timePart = "$hhStr:$mmStr"
    val weekTimePrefix = "$wk．$timePart"

    val meta = TimestampMeta(
        year = y, month = m, dayOfMonth = d, dayOfWeek = dow, hour = hh, minute = mm,
        secOfDay = secOfDay, startOfDayMillis = startOfDay,
        dateKey = dateKey, monthKey = monthKey, datePart = datePart,
        timePart = timePart, weekTimePrefix = weekTimePrefix
    )
    if (timestampMetaCache.size > 10_000) timestampMetaCache.clear()
    timestampMetaCache[timestamp] = meta
    return meta
}

@Volatile private var cachedTodayCheckMillis = 0L
@Volatile private var cachedTodayStartMillis = 0L
@Volatile private var cachedCurrentYear = 2026

private fun getTodayStartMillis(): Long {
    val now = System.currentTimeMillis()
    if (now - cachedTodayCheckMillis < 30_000L && cachedTodayStartMillis > 0L) {
        return cachedTodayStartMillis
    }
    val meta = getTimestampMeta(now)
    cachedTodayStartMillis = meta.startOfDayMillis
    cachedCurrentYear = meta.year
    cachedTodayCheckMillis = now
    return meta.startOfDayMillis
}

// =========================================================
//  ★ LedgerViewModel
// =========================================================
class LedgerViewModel(app: Application) : AndroidViewModel(app) {

    private val db by lazy { Firebase.firestore }
    private val prefs by lazy { app.getSharedPreferences("ledger_prefs", Context.MODE_PRIVATE) }

    private val recordsById = HashMap<String, Record>()
    private val ghostRecordsById = HashMap<String, Record>()

    @Volatile private var hasReceivedFirstSnapshot = false

    private val _lastSavedCategory = MutableStateFlow("飲食")
    val lastSavedCategory: StateFlow<String> = _lastSavedCategory.asStateFlow()

    private val _records = MutableStateFlow<List<Record>>(emptyList())
    val records: StateFlow<List<Record>> = _records.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _filter = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filter.asStateFlow()

    fun setFilterModeOn(v: Boolean)     { _filter.update { it.copy(modeOn = v) } }
    fun setFilterCategory(v: String?)   { _filter.update { it.copy(category = v) } }
    fun setFilterMonth(v: String?)      { _filter.update { it.copy(month = v) } }
    fun setFilterSearch(v: String)      { _filter.update { it.copy(search = v) } }
    fun setShowFuture(v: Boolean)       { _filter.update { it.copy(showFuture = v) } }
    
    // 👉 加入呢個函數
    fun setFilterTimeRange(startMin: Int, endMin: Int) {
        _filter.update { it.copy(startTimeMin = startMin, endTimeMin = endMin) }
    }

    private val _justAddedIds = MutableStateFlow<Set<String>>(emptySet())
    val justAddedIds: StateFlow<Set<String>> = _justAddedIds.asStateFlow()

    private val _deletingIds = MutableStateFlow<Set<String>>(emptySet())
    val deletingIds: StateFlow<Set<String>> = _deletingIds.asStateFlow()

    // ★ 比較頁快照源（只喺比較頁激活時先同步，並且永遠過濾「還錢」分類）
    private val _compareSnapshotRecords = MutableStateFlow<List<Record>>(emptyList())
    private val _isComparePageActive = MutableStateFlow(false)
    @Volatile private var hasInitializedCompareSnapshot = false

    /** 進入比較頁時呼叫：將底層最新數據一次性推入快照，觸發比較頁動畫 */
    fun onComparePageEnter() {
        _isComparePageActive.value = true
        if (!_loading.value) {
            // ★ 比較頁永遠過濾「還錢」分類
            _compareSnapshotRecords.value = _records.value.filter { it.category != "還錢" }
            hasInitializedCompareSnapshot = true
        }
    }

    /** 離開比較頁時呼叫：快照凍結，其他頁面改動唔會流入 */
    fun onComparePageExit() {
        _isComparePageActive.value = false
    }

    private val _defaultMonthA = monthKeyFromTimestamp(System.currentTimeMillis())
    private val _defaultMonthB = shiftMonthKey(_defaultMonthA, -1)

    private val _compareMonthA = MutableStateFlow(_defaultMonthA)
    val compareMonthA: StateFlow<String> = _compareMonthA.asStateFlow()

    private val _compareMonthB = MutableStateFlow(_defaultMonthB)
    val compareMonthB: StateFlow<String> = _compareMonthB.asStateFlow()

    fun setCompareMonthA(v: String) {
        _compareMonthA.value = v
        viewModelScope.launch(Dispatchers.IO) { prefs.edit().putString("compare_month_a", v).apply() }
    }
    fun setCompareMonthB(v: String) {
        _compareMonthB.value = v
        viewModelScope.launch(Dispatchers.IO) { prefs.edit().putString("compare_month_b", v).apply() }
    }

    fun updateLastSavedCategory(category: String) {
        _lastSavedCategory.value = category
        viewModelScope.launch(Dispatchers.IO) {
            prefs.edit().putString("last_category", category).apply()
        }
    }

    private val _calendarMonthKey = MutableStateFlow(
        monthKeyFromTimestamp(System.currentTimeMillis()))
    val calendarMonthKey: StateFlow<String> = _calendarMonthKey.asStateFlow()

    private val _calendarFilterCategory = MutableStateFlow<String?>(null)
    val calendarFilterCategory: StateFlow<String?> = _calendarFilterCategory.asStateFlow()

    fun setCalendarMonthKey(v: String)          { _calendarMonthKey.value = v }
    fun setCalendarFilterCategory(v: String?)   { _calendarFilterCategory.value = v }

    // 每 10 分鐘一個時間桶，控制首位「時間匹配」刷新頻率，避免頻繁輪轉
    private val _timeBucket = MutableStateFlow(System.currentTimeMillis() / 600_000L)

    private val noteIndexState: StateFlow<NoteIndex> = combine(_records, _timeBucket) { records, _ ->
        buildNoteIndex(records)
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NoteIndex())

    private val _hiddenQuickNotes = MutableStateFlow<Set<String>>(emptySet())
    val hiddenQuickNotes: StateFlow<Set<String>> = _hiddenQuickNotes.asStateFlow()

    fun hideQuickNote(note: String) {
        if (note.isBlank()) return
        val currentVisibleCount = noteIndexState.value.topNotes.count { it.first !in _hiddenQuickNotes.value }
        if (currentVisibleCount <= 1) return // 至少保留一項
        val newSet = _hiddenQuickNotes.value + note
        _hiddenQuickNotes.value = newSet
        viewModelScope.launch(Dispatchers.IO) {
            prefs.edit().putStringSet("hidden_quick_notes", newSet).apply()
        }
    }

    fun unhideQuickNote(note: String) {
        if (note.isBlank()) return
        val newSet = _hiddenQuickNotes.value - note
        _hiddenQuickNotes.value = newSet
        viewModelScope.launch(Dispatchers.IO) {
            prefs.edit().putStringSet("hidden_quick_notes", newSet).apply()
        }
    }

    val ledgerUiState: StateFlow<LedgerUiState> = combine(
        _records, noteIndexState, _loading, _filter, _deletingIds
    ) { records, noteIndex, loading, filter, deletingIds ->
        // 先算不含 hidden 的中間結果，再與 hidden 合併
        Triple(records, noteIndex, Triple(loading, filter, deletingIds))
    }.combine(_hiddenQuickNotes) { triple, hidden ->
        val (records, noteIndex, rest) = triple
        val (loading, filter, deletingIds) = rest
        computeLedgerUiState(records, noteIndex, loading, filter, deletingIds, hidden)
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LedgerUiState())

    // ★ compareUiState 以「快照源」為數據基礎
    val compareUiState: StateFlow<CompareUiState> = combine(
        _compareSnapshotRecords, _compareMonthA, _compareMonthB
    ) { records, a, b -> computeCompareUiState(records, a, b) }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CompareUiState())

    val calendarUiState: StateFlow<CalendarUiState> = combine(
        _records, _calendarMonthKey, _calendarFilterCategory
    ) { records, mk, cat -> computeCalendarUiState(records, mk, cat) }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CalendarUiState())

    // ============================================================
    // ★★★ 資料透視表 state —— 必須喺類「內部」！
    // ============================================================
    private val _pivotYear = MutableStateFlow(
        getTimestampMeta(System.currentTimeMillis()).year
    )
    val pivotYear: StateFlow<Int> = _pivotYear.asStateFlow()

    fun setPivotYear(year: Int) {
        if (year > 0) _pivotYear.value = year
    }

    val pivotUiState: StateFlow<PivotUiState> = combine(_records, _pivotYear) { records, year ->
        computePivotUiState(records, year)
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PivotUiState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val savedCategory = prefs.getString("last_category", "飲食") ?: "飲食"
            val savedMonthA = prefs.getString("compare_month_a", _defaultMonthA) ?: _defaultMonthA
            val savedMonthB = prefs.getString("compare_month_b", _defaultMonthB) ?: _defaultMonthB
            val savedHidden = prefs.getStringSet("hidden_quick_notes", emptySet()) ?: emptySet()
            _lastSavedCategory.value = savedCategory
            _compareMonthA.value = savedMonthA
            _compareMonthB.value = savedMonthB
            _hiddenQuickNotes.value = savedHidden
            withContext(Dispatchers.Default) { listenToFirestore() }
        }

        // ★ 快照同步邏輯 + 永遠過濾「還錢」
        viewModelScope.launch {
            _records.collect { records ->
                val shouldSync =
                    _isComparePageActive.value ||
                    (!hasInitializedCompareSnapshot && !_loading.value)
                if (shouldSync) {
                    // ★ 比較頁永遠過濾「還錢」分類
                    _compareSnapshotRecords.value = records.filter { it.category != "還錢" }
                    hasInitializedCompareSnapshot = true
                }
            }
        }

        // ★ 每 10 分鐘刷新時間桶，更新首位時間匹配項目（控制頻率，避免 UI 晃動）
        viewModelScope.launch {
            while (true) {
                delay(60_000L) // 每分鐘檢查一次
                val bucket = System.currentTimeMillis() / 600_000L
                if (bucket != _timeBucket.value) {
                    _timeBucket.value = bucket
                }
            }
        }
    }

    private fun buildSortedList(): List<Record> {
        val list = ArrayList<Record>(recordsById.size + ghostRecordsById.size)
        list.addAll(recordsById.values)
        list.addAll(ghostRecordsById.values)
        list.sortWith(compareByDescending<Record> { it.timestamp }.thenByDescending { it.id })
        return list
    }

    private fun emitSorted() {
        _records.value = buildSortedList()
    }

    private fun listenToFirestore() {
        db.collection("records")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                _loading.value = false
                AppStartupState.isFirstDataReady = true
                if (err != null || snap == null) return@addSnapshotListener

                val changes = snap.documentChanges
                if (changes.isEmpty()) return@addSnapshotListener

                val isFirstSnapshot = !hasReceivedFirstSnapshot
                hasReceivedFirstSnapshot = true

                viewModelScope.launch(Dispatchers.Default) {
                    val remoteAdded = ArrayList<String>(changes.size)
                    val remoteRemoved = ArrayList<String>(changes.size)

                    for (change in changes) {
                        val r = change.document.toObject(Record::class.java)
                            ?.copy(id = change.document.id) ?: continue
                        getTimestampMeta(r.timestamp)
                        when (change.type) {
                            DocumentChange.Type.ADDED -> {
                                val existed = recordsById.containsKey(r.id)
                                recordsById[r.id] = r
                                if (!existed) remoteAdded.add(r.id)
                            }
                            DocumentChange.Type.MODIFIED -> recordsById[r.id] = r
                            DocumentChange.Type.REMOVED -> {
                                val existing = recordsById.remove(r.id)
                                if (existing != null) {
                                    ghostRecordsById[r.id] = existing
                                    remoteRemoved.add(r.id)
                                }
                            }
                        }
                    }

                    if (remoteAdded.isEmpty() && remoteRemoved.isEmpty()) {
                        emitSorted()
                        return@launch
                    }

                    if (remoteAdded.isNotEmpty() && !isFirstSnapshot) {
                        _justAddedIds.update { it + remoteAdded.toSet() }
                    }
                    if (remoteRemoved.isNotEmpty()) {
                        _deletingIds.update { it + remoteRemoved.toSet() }
                    }
                    emitSorted()

                    if (remoteAdded.isNotEmpty() && !isFirstSnapshot) {
                        val ids = remoteAdded.toSet()
                        viewModelScope.launch {
                            delay(800)
                            _justAddedIds.update { it - ids }
                        }
                    }
                    if (remoteRemoved.isNotEmpty()) {
                        val ids = remoteRemoved.toSet()
                        viewModelScope.launch {
                            delay(900)
                            for (id in ids) ghostRecordsById.remove(id)
                            emitSorted()
                            _deletingIds.update { it - ids }
                        }
                    }
                }
            }
    }

    fun addRecord(record: Record, onIdReady: (String) -> Unit = {}) {
        val newId = db.collection("records").document().id
        val toSave = record.copy(id = newId)
        getTimestampMeta(toSave.timestamp)
        recordsById[newId] = toSave
        _justAddedIds.update { it + newId }
        emitSorted()
        onIdReady(newId)
        viewModelScope.launch {
            delay(800)
            _justAddedIds.update { it - newId }
        }
        viewModelScope.launch(Dispatchers.IO) {
            try { db.collection("records").document(newId).set(toSave).await() } catch (_: Exception) {}
        }
    }

    fun updateRecord(id: String, amount: Double, note: String, category: String, timestamp: Long) {
        getTimestampMeta(timestamp)
        val existing = recordsById[id] ?: return
        recordsById[id] = existing.copy(
            amount = amount, note = note, category = category, timestamp = timestamp
        )
        emitSorted()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("records").document(id).update(mapOf(
                    "amount" to amount, "note" to note,
                    "category" to category, "timestamp" to timestamp)).await()
            } catch (_: Exception) {}
        }
    }

    fun markAsDeleting(id: String) { _deletingIds.update { it + id } }
    fun cancelDelete(id: String)   { _deletingIds.update { it - id } }

    fun commitDelete(id: String) {
        _deletingIds.update { it - id }
        recordsById.remove(id)
        ghostRecordsById.remove(id)
        emitSorted()
        viewModelScope.launch(Dispatchers.IO) {
            try { db.collection("records").document(id).delete().await() } catch (_: Exception) {}
        }
    }

    fun deleteRecord(id: String) { commitDelete(id) }

    fun restoreRecord(record: Record) {
        getTimestampMeta(record.timestamp)
        recordsById[record.id] = record
        ghostRecordsById.remove(record.id)
        _deletingIds.update { it - record.id }
        _justAddedIds.update { it + record.id }
        emitSorted()
        viewModelScope.launch {
            delay(800)
            _justAddedIds.update { it - record.id }
        }
        viewModelScope.launch(Dispatchers.IO) {
            try { db.collection("records").document(record.id).set(record).await() } catch (_: Exception) {}
        }
    }

    fun latestIconForNote(note: String): String {
        if (note.isBlank()) return ""
        return _records.value.firstOrNull { it.note == note && it.iconUrl.isNotBlank() }?.iconUrl ?: ""
    }

    fun monthTotal(category: String, monthKey: String): Double {
        var sum = 0.0
        for (r in _records.value) {
            if (r.category == category && monthKeyFromTimestamp(r.timestamp) == monthKey) sum += r.amount
        }
        return sum
    }
}

private fun buildNoteIndex(records: List<Record>): NoteIndex {
    if (records.isEmpty()) return NoteIndex()
    val now = System.currentTimeMillis()
    val secNow = getTimestampMeta(now).secOfDay

    // 使用次數、時間距離、建議金額（跟一日內時段）、分類、圖標
    val usageCountByNote = HashMap<String, Int>()
    val minDistByNote = LinkedHashMap<String, Int>()
    // ★ 快速輸入金額：揀「一日入面時間最接近而家」嗰筆，唔係純最近一次
    val recentAmountByNote = HashMap<String, Double>()
    val amountMatchDistByNote = HashMap<String, Int>()
    val amountMatchTsByNote = HashMap<String, Long>()
    val categoryTsByNote = HashMap<String, Long>()
    val noteIconMap = HashMap<String, String>()
    val noteCategoryMap = HashMap<String, String>()
    val allNoteNames = ArrayList<String>()
    // 穩定排序用：首次出現順序
    val firstSeenOrder = LinkedHashMap<String, Int>()
    var seenIdx = 0

    for (r in records) {
        val note = r.note
        if (note.isBlank()) continue

        usageCountByNote[note] = (usageCountByNote[note] ?: 0) + 1

        if (!firstSeenOrder.containsKey(note)) {
            firstSeenOrder[note] = seenIdx++
            allNoteNames.add(note)
        }

        val sec = getTimestampMeta(r.timestamp).secOfDay
        val d = abs(secNow - sec)
        // 圓周距離：23:50 同 00:10 只差 20 分鐘
        val dist = if (d < 86400 - d) d else 86400 - d
        val prevMin = minDistByNote[note]
        if (prevMin == null || dist < prevMin) {
            minDistByNote[note] = dist
        }

        // ★ 建議金額：時段最貼近優先；同分用較新日期打破
        //   例如午餐 12:30 / 晚餐 19:00 → 中午撳用午餐金額，夜晚撳用晚餐金額
        val prevDist = amountMatchDistByNote[note]
        val prevTs = amountMatchTsByNote[note] ?: 0L
        val betterAmount = when {
            prevDist == null -> true
            dist < prevDist -> true
            dist == prevDist && r.timestamp >= prevTs -> true
            else -> false
        }
        if (betterAmount) {
            amountMatchDistByNote[note] = dist
            amountMatchTsByNote[note] = r.timestamp
            recentAmountByNote[note] = r.amount
        }

        // 分類仍跟最近一次紀錄（類別通常唔隨時段變）
        val prevCatTs = categoryTsByNote[note] ?: 0L
        if (r.timestamp >= prevCatTs) {
            categoryTsByNote[note] = r.timestamp
            noteCategoryMap[note] = r.category
        }

        if (r.iconUrl.isNotBlank() && !noteIconMap.containsKey(note)) {
            noteIconMap[note] = r.iconUrl
        }
    }

    // ★ 首位：時間最貼近嘅項目（特殊固定位，唔參與次數排序）
    //   只喺有合理時間距離時先選（例如 6 小時內），否則留空由後面補
    val TIME_MATCH_THRESHOLD_SEC = 6 * 3600 // 6 小時
    val timeMatchedNote: String? = minDistByNote.entries
        .filter { it.value <= TIME_MATCH_THRESHOLD_SEC }
        .minWithOrNull(
            compareBy<Map.Entry<String, Int>> { it.value }
                .thenBy { firstSeenOrder[it.key] ?: Int.MAX_VALUE }
        )?.key

    // ★ 第 2 位起：按使用次數降序；次數相同維持首次出現順序（穩定）
    val restSorted = usageCountByNote.entries
        .filter { it.key != timeMatchedNote }
        .sortedWith(
            compareByDescending<Map.Entry<String, Int>> { it.value }
                .thenBy { firstSeenOrder[it.key] ?: Int.MAX_VALUE }
        )
        .map { it.key to it.value }

    val topNotes = ArrayList<Pair<String, Int>>(30)
    if (timeMatchedNote != null) {
        topNotes.add(timeMatchedNote to (usageCountByNote[timeMatchedNote] ?: 0))
    }
    for (pair in restSorted) {
        if (topNotes.size >= 30) break
        topNotes.add(pair)
    }

    // ★ 精簡：移除 topQuickNotesInfo 构建（改由 computeLedgerUiState 现算，口径完全一致）

    return NoteIndex(
        recentAmountByNote = recentAmountByNote,
        noteIconMap = noteIconMap,
        allNoteNames = allNoteNames,
        noteCategoryMap = noteCategoryMap,
        noteUsageCount = usageCountByNote,
        topNotes = topNotes,
    )
}

private fun computeLedgerUiState(
    records: List<Record>, noteIndex: NoteIndex,
    loading: Boolean, filter: FilterState, deletingIds: Set<String>,
    hiddenQuickNotes: Set<String> = emptySet(),
): LedgerUiState {
    val now = System.currentTimeMillis()

    // ★ 已修復嘅搜尋邏輯
    val searchFiltered: List<Record> = if (filter.search.isBlank()) records else {
        val q = filter.search
        records.filter { r ->
            r.note.contains(q, ignoreCase = true) || r.category.contains(q, ignoreCase = true)
        }
    }

    val filterCat = filter.category
    val filterMon = filter.month
    val startMin = filter.startTimeMin
    val endMin = filter.endTimeMin
    
    val catMonthFiltered = if (filterCat == null && filterMon == null && startMin == 0 && endMin == 1440) searchFiltered else {
        searchFiltered.filter { r ->
            val minOfDay = getTimestampMeta(r.timestamp).secOfDay / 60
            (filterCat == null || r.category == filterCat) &&
            (filterMon == null || getTimestampMeta(r.timestamp).monthKey == filterMon) &&
            (minOfDay in startMin..endMin)
        }
    }

    val base = if (filter.modeOn) catMonthFiltered else records
    val futureCutoff = now + 60_000L
    val ledgerRecords = if (filter.showFuture) base else base.filter { it.timestamp <= futureCutoff }

    var hasIncome = false
    var hasExpense = false
    var totalIncome = 0.0
    var totalExpense = 0.0
    val groupedMap = LinkedHashMap<String, ArrayList<Record>>()
    var firstNote: String? = null
    var allSameNote = true

    // ★ 非篩選模式：永遠用本月；篩選模式：跟 chip 月份（null = 全年）
    val currentMonthKey = getTimestampMeta(now).monthKey
    val topStatRecords = when {
        !filter.modeOn -> ledgerRecords.filter { getTimestampMeta(it.timestamp).monthKey == currentMonthKey }
        filter.month == null -> ledgerRecords
        else -> ledgerRecords.filter { getTimestampMeta(it.timestamp).monthKey == filter.month }
    }

    for (r in topStatRecords) {
        if (r.id !in deletingIds && r.category != "還錢") {
            if (r.category == INCOME_CATEGORY) {
                hasIncome = true; totalIncome += r.amount
            } else {
                hasExpense = true; totalExpense += r.amount
            }
        }
    }

    for (r in ledgerRecords) {
        val dKey = getTimestampMeta(r.timestamp).dateKey
        groupedMap.getOrPut(dKey) { ArrayList() }.add(r)
        if (filter.modeOn && allSameNote) {
            if (firstNote == null) firstNote = r.note
            else if (firstNote != r.note) allSameNote = false
        }
    }

    val groupedByDate = groupedMap.entries.map { it.key to it.value }
    val isExactNoteFilter = filter.modeOn && ledgerRecords.isNotEmpty() && allSameNote

    val monthsSet = HashSet<String>()
    var hasIncForCat = false
    val expenseSums = HashMap<String, Double>()
    for (r in searchFiltered) {
        val mKey = getTimestampMeta(r.timestamp).monthKey
        monthsSet.add(mKey)
        if (filterMon == null || mKey == filterMon) {
            if (r.category == INCOME_CATEGORY) hasIncForCat = true
            else expenseSums[r.category] = (expenseSums[r.category] ?: 0.0) + r.amount
        }
    }

    val availableMonths = monthsSet.sorted()
    val expenseCats = expenseSums.entries.sortedByDescending { it.value }.map { it.key }
    val visibleCategories =
        (if (hasIncForCat) listOf(INCOME_CATEGORY) else emptyList()) + expenseCats

    val finalTopNotes = if (filter.showFuture) noteIndex.topNotes else {
        val notesWithPast = HashSet<String>()
        for (r in records) {
            if (r.timestamp <= futureCutoff && r.note.isNotBlank()) notesWithPast.add(r.note)
        }
        noteIndex.topNotes.filter { it.first in notesWithPast }
    }

        // ★ 精簡：由 noteIndex.topNotes 現算 quickInfo（與原本 buildNoteIndex 的口徑完全一致）
    // 🟢 新增過濾：同步快速記賬介面，剔除已隱藏嘅項目，確保通知唔會再顯示
    val quickInfo = noteIndex.topNotes
        .filter { it.first !in hiddenQuickNotes }
        .take(3)
        .mapNotNull { (note, _) ->
            if (note.isBlank()) null
            else QuickNoteInfo(
                note = note,
                category = noteIndex.noteCategoryMap[note] ?: "飲食",
                amount = noteIndex.recentAmountByNote[note] ?: 0.0
            )
        }

    return LedgerUiState(
        loading = loading,
        ledgerRecords = ledgerRecords,
        groupedByDate = groupedByDate,
        hasIncome = hasIncome,
        hasExpense = hasExpense,
        totalIncome = totalIncome,
        totalExpense = totalExpense,
        isExactNoteFilter = isExactNoteFilter,
        topNotes = finalTopNotes,
        recentAmountByNote = noteIndex.recentAmountByNote,
        noteIconMap = noteIndex.noteIconMap,
        allNoteNames = noteIndex.allNoteNames,
        noteCategoryMap = noteIndex.noteCategoryMap,
        noteUsageCount = noteIndex.noteUsageCount,
        availableMonths = availableMonths,
        visibleCategories = visibleCategories,
        topQuickNotesInfo = quickInfo,
        hiddenQuickNotes = hiddenQuickNotes,
    )
}

private fun computeCompareUiState(records: List<Record>, monthA: String, monthB: String): CompareUiState {
    val categoryMonthSum = HashMap<String, HashMap<String, Double>>()
    for (r in records) {
        // ★ 比較頁永遠排除「還錢」分類（雙重保險，上游快照已過濾）
        if (r.category == "還錢") continue
        val inner = categoryMonthSum.getOrPut(r.category) { HashMap() }
        val key = getTimestampMeta(r.timestamp).monthKey
        inner[key] = (inner[key] ?: 0.0) + r.amount
    }
    fun sumLocal(cat: String, month: String): Double =
        categoryMonthSum[cat]?.get(month) ?: 0.0

    val sortedCategories = listOf(INCOME_CATEGORY) +
        EXPENSE_CATEGORIES.sortedByDescending { sumLocal(it, monthB) }
    val displayCategories = sortedCategories.filter { cat ->
        sumLocal(cat, monthA) != 0.0 || sumLocal(cat, monthB) != 0.0
    }

    val incomeA = sumLocal(INCOME_CATEGORY, monthA)
    val incomeB = sumLocal(INCOME_CATEGORY, monthB)
    val totalExpenseA = EXPENSE_CATEGORIES.sumOf { sumLocal(it, monthA) }
    val totalExpenseB = EXPENSE_CATEGORIES.sumOf { sumLocal(it, monthB) }

    val nowMeta = getTimestampMeta(System.currentTimeMillis())
    val currentYear = nowMeta.year
    val currentMonthNum = nowMeta.month
    val monthlyKeysThisYear = (1..currentMonthNum).map { m -> "$currentYear-${pad2(m)}" }
    val histogramData = EXPENSE_CATEGORIES.associateWith { cat ->
        monthlyKeysThisYear.map { key -> sumLocal(cat, key) }
    }

    return CompareUiState(
        categoryMonthSum = categoryMonthSum,
        sortedCategories = sortedCategories,
        displayCategories = displayCategories,
        incomeA = incomeA, incomeB = incomeB,
        totalExpenseA = totalExpenseA, totalExpenseB = totalExpenseB,
        balanceA = incomeA - totalExpenseA, balanceB = incomeB - totalExpenseB,
        histogramData = histogramData,
        monthlyKeysThisYear = monthlyKeysThisYear,
    )
}

private fun computeCalendarUiState(records: List<Record>, monthKey: String, filterCategory: String?): CalendarUiState {
    val monthAllRecords = ArrayList<Record>()
    var hasIncome = false
    val expenseSums = HashMap<String, Double>()
    for (r in records) {
        if (getTimestampMeta(r.timestamp).monthKey == monthKey) {
            monthAllRecords.add(r)
            if (r.category == INCOME_CATEGORY) hasIncome = true
            else expenseSums[r.category] = (expenseSums[r.category] ?: 0.0) + r.amount
        }
    }
    val expenseCats = expenseSums.entries.sortedByDescending { it.value }.map { it.key }
    val visibleCategories =
        (if (hasIncome) listOf(INCOME_CATEGORY) else emptyList()) + expenseCats
        // ★ 「全部」模式自動排除「還錢」；只有手動揀「還錢」時先計入
    val monthRecords = when (filterCategory) {
        null -> monthAllRecords.filter { it.category != "還錢" }
        else -> monthAllRecords.filter { it.category == filterCategory }
    }

    val recordsByDay = HashMap<Int, ArrayList<Record>>()
    var monthIncome = 0.0
    var monthExpense = 0.0
    for (r in monthRecords) {
        val day = getTimestampMeta(r.timestamp).dayOfMonth
        recordsByDay.getOrPut(day) { ArrayList() }.add(r)
        if (r.category == INCOME_CATEGORY) monthIncome += r.amount
        else monthExpense += r.amount
    }

    var maxAbsNet = 0.0
    for (dayRecords in recordsByDay.values) {
        var inc = 0.0; var exp = 0.0
        for (r in dayRecords) {
            if (r.category == INCOME_CATEGORY) inc += r.amount else exp += r.amount
        }
        val netAbs = abs(inc - exp)
        if (netAbs > maxAbsNet) maxAbsNet = netAbs
    }

    val totalNet = monthIncome - monthExpense
    val itemCount = monthRecords.size
    val avgPerItem = if (itemCount > 0) (monthIncome + monthExpense) / itemCount else 0.0

    val nowMeta = getTimestampMeta(System.currentTimeMillis())
    val todayKey = nowMeta.monthKey
    val parts = monthKey.split("-")
    val curCal = threadLocalCal.get()!!.apply {
        if (parts.size == 2) set(parts[0].toInt(), parts[1].toInt() - 1, 1)
    }
    val daysInMonth = curCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val isCurrentMonth = monthKey == todayKey
    val daysElapsed = if (isCurrentMonth) nowMeta.dayOfMonth else daysInMonth
    val avgPerDay = if (daysElapsed > 0) monthExpense / daysElapsed else 0.0

    return CalendarUiState(
        monthKey = monthKey,
        monthAllRecords = monthAllRecords,
        visibleCategories = visibleCategories,
        monthRecords = monthRecords,
        recordsByDay = recordsByDay,
        maxAbsNet = maxAbsNet,
        monthIncome = monthIncome,
        monthExpense = monthExpense,
        totalNet = totalNet,
        itemCount = itemCount,
        avgPerItem = avgPerItem,
        avgPerDay = avgPerDay,
    )
}

// =========================================================
//  ★ 資料透視表 - 計算函數
//    按選定年份；月份 1月→12月（左→右）；類別按年內總額降序
// =========================================================
private fun computePivotUiState(records: List<Record>, selectedYear: Int): PivotUiState {
    val yearsSet = HashSet<Int>()
    // monthKey -> category -> note -> PivotCell（只收納選定年份）
    val data = HashMap<String, HashMap<String, HashMap<String, PivotCell>>>()
    val noteIconMap = HashMap<String, String>()

    for (r in records) {
        if (r.category == "還錢") continue
        val meta = getTimestampMeta(r.timestamp)
        yearsSet.add(meta.year)
        if (r.note.isNotBlank() && r.iconUrl.isNotBlank() && !noteIconMap.containsKey(r.note)) {
            noteIconMap[r.note] = r.iconUrl
        }
        if (meta.year != selectedYear) continue
        val mk = meta.monthKey
        val catMap = data.getOrPut(mk) { HashMap() }
        val noteMap = catMap.getOrPut(r.category) { HashMap() }
        val key = r.note.ifBlank { "(無名稱)" }
        val existing = noteMap[key] ?: PivotCell()
        noteMap[key] = PivotCell(existing.total + r.amount, existing.count + 1)
    }

    // 年份左→右：小至大
    val availableYears = yearsSet.sorted()
    val year = when {
        selectedYear in yearsSet -> selectedYear
        availableYears.isNotEmpty() -> availableYears.last() // 預設最新一年
        else -> selectedYear
    }

    // 月份 1→N：若係今年只顯示到本月，未來月份唔顯示
    val nowMeta = getTimestampMeta(System.currentTimeMillis())
    val maxMonth = when {
        year < nowMeta.year -> 12
        year > nowMeta.year -> 0
        else -> nowMeta.month
    }
    val months = if (maxMonth <= 0) emptyList()
                 else (1..maxMonth).map { m -> "$year-${pad2(m)}" }

    if (data.isEmpty()) {
        return PivotUiState(
            months = months,
            rows = emptyList(),
            monthTotals = emptyMap(),
            grandTotal = 0.0,
            loading = false,
            availableYears = availableYears,
            selectedYear = year,
            categories = emptyList(),
            noteIconMap = noteIconMap,
        )
    }

    // 排序用：年內總額；次要用「最新有資料月份」金額
    val categoryTotalAll = HashMap<String, Double>()
    val categoryLatest = HashMap<String, Double>()
    val noteTotalAll = HashMap<Pair<String, String>, Double>()
    val noteLatest = HashMap<Pair<String, String>, Double>()
    var latestMonthWithData: String? = null
    for (mk in months.asReversed()) {
        if (data.containsKey(mk)) {
            latestMonthWithData = mk
            break
        }
    }

    for ((mk, catMap) in data) {
        for ((cat, noteMap) in catMap) {
            var catSum = 0.0
            for ((note, cell) in noteMap) {
                catSum += cell.total
                val pair = cat to note
                noteTotalAll[pair] = (noteTotalAll[pair] ?: 0.0) + cell.total
                if (mk == latestMonthWithData) {
                    noteLatest[pair] = (noteLatest[pair] ?: 0.0) + cell.total
                }
            }
            categoryTotalAll[cat] = (categoryTotalAll[cat] ?: 0.0) + catSum
            if (mk == latestMonthWithData) {
                categoryLatest[cat] = (categoryLatest[cat] ?: 0.0) + catSum
            }
        }
    }

    val sortedCats = categoryTotalAll.keys.sortedWith(
        compareByDescending<String> { categoryLatest[it] ?: 0.0 }
            .thenByDescending { categoryTotalAll[it] ?: 0.0 }
    )

    val rows = mutableListOf<PivotRow>()
    for (cat in sortedCats) {
        val catCells = HashMap<String, PivotCell>()
        var catTotal = 0.0
        for (mk in months) {
            val noteMap = data[mk]?.get(cat) ?: continue
            var sum = 0.0; var cnt = 0
            for (cell in noteMap.values) { sum += cell.total; cnt += cell.count }
            if (sum != 0.0) { catCells[mk] = PivotCell(sum, cnt); catTotal += sum }
        }
        rows.add(PivotRow(cat, "", true, catCells, catTotal))

        val notesInCat = noteTotalAll.keys
            .filter { it.first == cat }
            .sortedWith(
                compareByDescending<Pair<String, String>> { noteLatest[it] ?: 0.0 }
                    .thenByDescending { noteTotalAll[it] ?: 0.0 }
            )

        for (pair in notesInCat) {
            val note = pair.second
            val noteCells = HashMap<String, PivotCell>()
            var rowTotal = 0.0
            for (mk in months) {
                val cell = data[mk]?.get(cat)?.get(note) ?: continue
                noteCells[mk] = cell; rowTotal += cell.total
            }
            rows.add(PivotRow(cat, note, false, noteCells, rowTotal))
        }
    }

    val monthTotals = HashMap<String, Double>()
    var grandTotal = 0.0
    for (mk in months) {
        var sum = 0.0
        data[mk]?.forEach { (_, noteMap) -> noteMap.values.forEach { sum += it.total } }
        if (sum != 0.0) monthTotals[mk] = sum
        grandTotal += sum
    }

    return PivotUiState(
        months = months,
        rows = rows,
        monthTotals = monthTotals,
        grandTotal = grandTotal,
        loading = false,
        availableYears = availableYears,
        selectedYear = year,
        categories = sortedCats,
        noteIconMap = noteIconMap,
    )
}

fun computeWeeklyExpenses(records: List<Record>): List<WeeklyExpenseDay> {
    val todayStart = getTodayStartMillis()
    val dayMs = 86_400_000L
    val startDay = todayStart - 6 * dayMs
    val amountByDay = HashMap<Long, Double>(7)
    for (r in records) {
        // 🟢 計算棒形圖時，直接跳過「收入」同「還錢」類別
        if (r.category == INCOME_CATEGORY || r.category == "還錢") continue

        val dayStart = getTimestampMeta(r.timestamp).startOfDayMillis
        if (dayStart in startDay..todayStart) {
            amountByDay[dayStart] = (amountByDay[dayStart] ?: 0.0) + r.amount
        }
    }
    val result = ArrayList<WeeklyExpenseDay>(7)
    for (i in 0..6) {
        val dayStart = startDay + i * dayMs
        val meta = getTimestampMeta(dayStart)
        result.add(
            WeeklyExpenseDay(
                dayOfMonth = meta.dayOfMonth,
                weekdayChar = WEEKDAY_CHARS[meta.dayOfWeek - 1],
                amount = amountByDay[dayStart] ?: 0.0,
                isToday = dayStart == todayStart
            )
        )
    }
    return result
}

object CategoryMenuPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val margin = 8
        val targetX = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
        val x = targetX.coerceIn(
            margin,
            (windowSize.width - popupContentSize.width - margin).coerceAtLeast(margin)
        )
        val yAbove = anchorBounds.top - popupContentSize.height - margin
        if (yAbove >= margin) return IntOffset(x, yAbove)
        val yBelow = anchorBounds.bottom + margin
        if (yBelow + popupContentSize.height <= windowSize.height - margin) {
            return IntOffset(x, yBelow)
        }
        val maxY = (windowSize.height - popupContentSize.height - margin).coerceAtLeast(margin)
        return IntOffset(x, yAbove.coerceIn(margin, maxY))
    }
}

object AboveAnchorPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect, windowSize: IntSize,
        layoutDirection: LayoutDirection, popupContentSize: IntSize
    ): IntOffset {
        val x = anchorBounds.left
        val yAbove = anchorBounds.top - popupContentSize.height - 4
        if (yAbove >= 0) return IntOffset(x, yAbove)
        val yBelow = anchorBounds.bottom + 4
        if (yBelow + popupContentSize.height <= windowSize.height) return IntOffset(x, yBelow)
        val maxY = (windowSize.height - popupContentSize.height).coerceAtLeast(0)
        return IntOffset(x, yAbove.coerceIn(0, maxY))
    }
}

fun formatAmount(amount: Double): String = String.format(Locale.US, "%,.1f", amount)
fun formatAmountNoDecimal(amount: Double): String = String.format(Locale.US, "%,.0f", amount)

private val CN_DIGIT_MAP = mapOf(
    '零' to 0, '〇' to 0, '○' to 0,
    '一' to 1, '壹' to 1,
    '二' to 2, '兩' to 2, '贰' to 2, '貳' to 2,
    '三' to 3, '叁' to 3,
    '四' to 4, '肆' to 4,
    '五' to 5, '伍' to 5,
    '六' to 6, '陸' to 6, '陆' to 6,
    '七' to 7, '柒' to 7,
    '八' to 8, '捌' to 8,
    '九' to 9, '玖' to 9
)

/** 解析中文整數（支援到百），例如：二十八→28、十二→12、一百零五→105、十→10 */
fun parseChineseInteger(s: String): Int? {
    val t = s.trim()
    if (t.isEmpty()) return null
    if (t.all { it in CN_DIGIT_MAP }) {
        // 純逐位：三二八 → 328（少見，但可兜）
        var v = 0
        for (c in t) v = v * 10 + (CN_DIGIT_MAP[c] ?: return null)
        return v
    }
    var result = 0
    var unit = 0
    var i = 0
    while (i < t.length) {
        val c = t[i]
        when {
            c in CN_DIGIT_MAP -> unit = CN_DIGIT_MAP[c]!!
            c == '十' -> {
                result += (if (unit == 0) 1 else unit) * 10
                unit = 0
            }
            c == '百' -> {
                result += (if (unit == 0) 1 else unit) * 100
                unit = 0
            }
            c == '千' -> {
                result += (if (unit == 0) 1 else unit) * 1000
                unit = 0
            }
            else -> return null
        }
        i++
    }
    result += unit
    return result
}

/**
 * 從字串尾部抽出金額（阿拉伯數字或粵語／中文金額口語）。
 * 支援：28、28.5、四個九(4.9)、四點九、二十八、十二蚊半(12.5)
 * @return Pair(前面剩餘文字, 金額?)
 */
fun extractTrailingAmount(raw: String): Pair<String, Double?> {
    var text = raw.trim()
    if (text.isEmpty()) return "" to null

    // 單位／口語正規化
    text = text
        .replace("塊錢", "")
        .replace("蚊半", "點五")
        .replace("塊半", "點五")
        .replace("元半", "點五")
        .replace(Regex("[蚊元塊圓块圆錢]"), "")
        .replace(Regex("\\s+"), "")
        .trim()

    // 1) 阿拉伯數字在尾：…28 / …28.5
    Regex("""^(.*?)(\d+(?:\.\d+)?)$""").find(text)?.let { m ->
        val amt = m.groupValues[2].toDoubleOrNull()
        if (amt != null && amt > 0) return m.groupValues[1] to amt
    }

    // 2) 粵語「X個Y」→ X.Y（例如 四個九 → 4.9、十二個五 → 12.5）
    Regex("""^(.*?)([零〇○一二兩三四五六七八九十百千壹贰貳叁肆伍陸陆柒捌玖]+)個([零〇○一二兩三四五六七八九壹贰貳叁肆伍陸陆柒捌玖])$""").find(text)?.let { m ->
        val whole = parseChineseInteger(m.groupValues[2])
        val frac = CN_DIGIT_MAP[m.groupValues[3][0]]
        if (whole != null && frac != null) {
            val amt = whole + frac / 10.0
            if (amt > 0) return m.groupValues[1] to amt
        }
    }

    // 3) 「X點Y」→ X.Y
    Regex("""^(.*?)([零〇○一二兩三四五六七八九十百千]+)?點([零〇○一二兩三四五六七八九]+)$""").find(text)?.let { m ->
        val wholePart = m.groupValues[2]
        val fracPart = m.groupValues[3]
        val whole = if (wholePart.isBlank()) 0 else parseChineseInteger(wholePart)
        if (whole != null && fracPart.isNotEmpty()) {
            var frac = 0.0
            var div = 10.0
            for (c in fracPart) {
                val d = CN_DIGIT_MAP[c] ?: return@let
                frac += d / div
                div *= 10
            }
            val amt = whole + frac
            if (amt > 0) return m.groupValues[1] to amt
        }
    }

    // 4) 純中文整數在尾：…二十八
    Regex("""^(.*?)([零〇○一二兩三四五六七八九十百千壹贰貳叁肆伍陸陆柒捌玖]+)$""").find(text)?.let { m ->
        val whole = parseChineseInteger(m.groupValues[2])
        if (whole != null && whole > 0) return m.groupValues[1] to whole.toDouble()
    }

    return text to null
}

/**
 * 解析語音記帳輸入，例如：
 * 「麥當勞28」「麥當勞 28 蚊」「地鐵四個九」「咖啡十五」「28」
 */
fun parseVoiceLedgerInput(raw: String): Pair<String, Double?> {
    val text = raw.trim()
    if (text.isEmpty()) return "" to null

    val (prefix, amt) = extractTrailingAmount(text)
    val name = prefix.trim()

    // 整句都係金額
    if (name.isEmpty() && amt != null) return "" to amt
    // 有名稱 + 金額
    if (name.isNotEmpty() && amt != null && amt > 0) return name to amt
    // 金額在前：28麥當勞
    Regex("""^(\d+(?:\.\d+)?)(.+)$""").find(text.replace(" ", ""))?.let { m ->
        val a = m.groupValues[1].toDoubleOrNull()
        val n = m.groupValues[2].trim()
        if (a != null && a > 0 && n.isNotEmpty()) return n to a
    }
    // 淨係名稱
    return text to null
}
fun compactAmount(value: Double): String {
    val absV = abs(value)
    val sign = if (value < 0) "-" else ""
    return when {
        absV < 1000 -> "${sign}${absV.roundToInt()}"
        absV < 10000 -> "${sign}${String.format(Locale.US, "%.1f", absV / 1000.0).removeSuffix(".0")}K"
        absV < 1000000 -> "${sign}${(absV / 1000.0).roundToInt()}K"
        absV < 10000000 -> "${sign}${String.format(Locale.US, "%.1f", absV / 1000000.0).removeSuffix(".0")}M"
        else -> "${sign}${(absV / 1000000.0).roundToInt()}M"
    }
}
fun displayAmount(record: Record): String =
    if (record.category == INCOME_CATEGORY) formatAmount(record.amount)
    else formatAmount(-record.amount)
fun amountColor(category: String): Color =
    if (category == INCOME_CATEGORY) COLOR_INCOME else COLOR_EXPENSE

fun relativeDayLabel(timestamp: Long): String {
    val todayStart = getTodayStartMillis()
    val recStart = getTimestampMeta(timestamp).startOfDayMillis
    val diff = ((todayStart - recStart) / 86_400_000L).toInt()
    return when {
        diff < 0 -> "未來"
        diff == 0 -> "今日"
        diff == 1 -> "琴日"
        diff == 2 -> "前日"
        else -> "${diff}日前"
    }
}

fun formatRecordTime(timestamp: Long): String {
    val meta = getTimestampMeta(timestamp)
    return "${meta.weekTimePrefix}．${relativeDayLabel(timestamp)}"
}

fun formatDatePart(timestamp: Long): String = getTimestampMeta(timestamp).datePart
fun formatTimePart(timestamp: Long): String = getTimestampMeta(timestamp).timePart
fun dateKeyFromTimestamp(timestamp: Long): String = getTimestampMeta(timestamp).dateKey
fun monthKeyFromTimestamp(timestamp: Long): String = getTimestampMeta(timestamp).monthKey

fun shiftMonthKey(key: String, delta: Int): String {
    val parts = key.split("-")
    if (parts.size != 2) return key
    val y = parts[0].toIntOrNull() ?: return key
    val m = parts[1].toIntOrNull() ?: return key
    val totalMonths = y * 12 + (m - 1) + delta
    val newY = totalMonths / 12
    val newM = (totalMonths % 12) + 1
    return "$newY-${pad2(newM)}"
}

fun formatMonthLabel(ym: String): String {
    val p = ym.split("-"); if (p.size != 2) return ym
    val y = p[0].toIntOrNull() ?: return ym
    val m = p[1].toIntOrNull() ?: return ym
    getTodayStartMillis()
    return if (y == cachedCurrentYear) "${m}月" else "${y % 100}年${m}月"
}

fun formatDateHeader(dateKey: String): String {
    val info = parseDateHeader(dateKey) ?: return dateKey
    val wk = "週${info.weekdayChar}"
    val base = "${info.year}年${info.month}月${info.day}日 $wk"
    return if (info.dayTag != null) "$base ${info.dayTag}" else base
}

fun parseDateHeader(dateKey: String): DateHeaderInfo? {
    val p = dateKey.split("-")
    if (p.size != 3) return null
    val y = p[0].toIntOrNull() ?: return null
    val mo = p[1].toIntOrNull() ?: return null
    val d = p[2].toIntOrNull() ?: return null

    val todayStart = getTodayStartMillis()

    val cal = threadLocalCal.get()!!.apply {
        set(y, mo - 1, d)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }

    val targetMillis = cal.timeInMillis
    val dow = cal.get(Calendar.DAY_OF_WEEK)
    val weekdayChar = WEEKDAY_CHARS[dow - 1]
    val isWeekend = dow == Calendar.SUNDAY || dow == Calendar.SATURDAY

    val diff = ((todayStart - targetMillis) / 86_400_000L).toInt()
    val tag = when {
        diff == -1 -> "明日"
        diff == -2 -> "後日"
        diff < -2 -> null
        diff == 0 -> "今日"
        diff == 1 -> "琴日"
        diff == 2 -> "前日"
        else -> null
    }
    return DateHeaderInfo(y, mo, d, weekdayChar, isWeekend, tag)
}

fun filterNoteSuggestions(query: String, all: List<String>): List<String> {
    if (query.isBlank()) return emptyList()
    val q = query.trim()
    if (q.isEmpty()) return emptyList()
    val result = ArrayList<String>(8)
    for (s in all) {
        // ★ 移除咗 s != q，就算項目名完全等於搜尋字，都會正常彈出嚟俾你撳
        if (s.startsWith(q, ignoreCase = true)) {
            result.add(s)
            if (result.size >= 8) return result
        }
    }
    for (s in all) {
        if (!s.startsWith(q, ignoreCase = true) && s.contains(q, ignoreCase = true)) {
            result.add(s)
            if (result.size >= 8) return result
        }
    }
    return result
}

val AVATAR_COLORS = listOf(
    Color(0xFFE57373), Color(0xFFF06292), Color(0xFFBA68C8), Color(0xFF9575CD),
    Color(0xFF7986CB), Color(0xFF64B5F6), Color(0xFF4FC3F7), Color(0xFF4DB6AC),
    Color(0xFF81C784), Color(0xFFAED581), Color(0xFFFFB74D), Color(0xFFFF8A65),
    Color(0xFFA1887F), Color(0xFF90A4AE)
)
fun avatarColor(name: String): Color {
    if (name.isBlank()) return AVATAR_COLORS[0]
    return AVATAR_COLORS[(name.hashCode() and 0x7fffffff) % AVATAR_COLORS.size]
}

fun getDominantMutedColor(bitmap: Bitmap): Color {
    if (bitmap.width <= 0 || bitmap.height <= 0) return Color(0xFFF1F5F9)
    val targetSize = 48
    val scaled: Bitmap = if (bitmap.width > targetSize || bitmap.height > targetSize) {
        val ratio = minOf(targetSize.toFloat() / bitmap.width, targetSize.toFloat() / bitmap.height)
        val nw = (bitmap.width * ratio).toInt().coerceAtLeast(1)
        val nh = (bitmap.height * ratio).toInt().coerceAtLeast(1)
        Bitmap.createScaledBitmap(bitmap, nw, nh, true)
    } else bitmap

    val w = scaled.width; val h = scaled.height
    val pixels = IntArray(w * h)
    scaled.getPixels(pixels, 0, w, 0, 0, w, h)
    if (scaled !== bitmap) scaled.recycle()

    val colorCount = HashMap<Int, Int>(64)
    var bestRgb = android.graphics.Color.LTGRAY
    var bestCount = 0
    for (p in pixels) {
        val a = p ushr 24
        if (a < 120) continue
        val r = ((p shr 16) and 0xFF) / 24 * 24
        val g = ((p shr 8) and 0xFF) / 24 * 24
        val b = (p and 0xFF) / 24 * 24
        val maxC = maxOf(r, g, b); val minC = minOf(r, g, b)
        if (maxC > 235 && minC > 220) continue
        if (maxC < 28) continue
        val rgb = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
        val cnt = (colorCount[rgb] ?: 0) + 1
        colorCount[rgb] = cnt
        if (cnt > bestCount) { bestCount = cnt; bestRgb = rgb }
    }
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(bestRgb, hsv)
    hsv[1] = (hsv[1] * 0.42f).coerceAtMost(0.45f)
    hsv[2] = 0.96f
    return Color(android.graphics.Color.HSVToColor(hsv))
}

fun getHoveredActionIndexWithCenter(
    center: Offset,
    touch: Offset,
    minRadiusPx: Float,
    arcCenterAngle: Float,
    radiusPx: Float,          // 實際扇形半徑
    hitRadiusPx: Float        // 按鈕可點擊半徑
): Int? {
    val dx = touch.x - center.x
    val dy = touch.y - center.y
    val distFromCenter = kotlin.math.hypot(dx, dy)

    // 1. 縮短死區判定：只要手指離開起點極短距離就開始判定
    if (distFromCenter < minRadiusPx) return null

    // 2. 計算手指相對於中心點嘅滑動方向角度 (0~360度)
    val touchAngleRad = kotlin.math.atan2(dy, dx)
    var touchAngleDeg = Math.toDegrees(touchAngleRad.toDouble()).toFloat()
    if (touchAngleDeg < 0) touchAngleDeg += 360f

    var bestIndex: Int? = null
    var minAngleDiff = Float.MAX_VALUE

    // 3. 搵出角度最接近嘅按鈕
    for (i in 0 until 4) {
        var btnAngle = arcCenterAngle - 60f + i * 40f
        btnAngle = (btnAngle % 360f + 360f) % 360f

        var diff = kotlin.math.abs(touchAngleDeg - btnAngle)
        if (diff > 180f) diff = 360f - diff

        if (diff < minAngleDiff) {
            minAngleDiff = diff
            bestIndex = i
        }
    }

    // 4. 極大寬容度判定：只要角度偏差喺 60 度以內，無論手指滑到幾遠都算命中！
    // 咁樣基本上成個上半螢幕都係觸發區，隨手一滑就自動吸附
    if (minAngleDiff <= 60f) {
        return bestIndex
    }

    // 5. 保底機制：傳統嘅圓形半徑判定
    for (i in 0 until 4) {
        val angleRad = Math.toRadians((arcCenterAngle - 60f + i * 40f).toDouble())
        val cx = center.x + radiusPx * kotlin.math.cos(angleRad).toFloat()
        val cy = center.y + radiusPx * kotlin.math.sin(angleRad).toFloat()
        val dist = (touch - Offset(cx, cy)).getDistance()
        if (dist <= hitRadiusPx) {
            return i
        }
    }

    return null
}

private val FAN_MENU_ACTIONS = listOf(
    Triple(Icons.Default.ContentCopy, "複製", Color(0xFF64748B)),
    Triple(Icons.Default.Edit, "編輯", Color(0xFF3B82F6)),
    Triple(Icons.Default.FilterList, "篩選", Color(0xFF8B5CF6)),
    Triple(Icons.Default.Delete, "刪除", COLOR_EXPENSE)
)

@Composable
fun FanMenuOverlay(
    progressProvider: () -> Float,
    centerProvider: () -> Offset,
    touchProvider: () -> Offset
) {
    val boxPositionInRoot = remember { mutableStateOf(Offset.Zero) }

    val progress = progressProvider()
    if (progress <= 0.001f) return

    val center = centerProvider() - boxPositionInRoot.value
    val currentTouch = touchProvider() - boxPositionInRoot.value

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }

    // ★ 大幅縮短距離：死區由 40dp 減至 16dp，彈出距離由 105dp 減至 85dp
    val minRadiusPx = remember(density) { with(density) { 16.dp.toPx() } }
val radiusPx = remember(density) { with(density) { 105.dp.toPx() } } 
val halfBtnPx = remember(density) { with(density) { 24.dp.toPx() } }
    val hitRadiusPx = remember(density) { with(density) { 40.dp.toPx() } } // 加大保底半徑

    val edgeThreshold = radiusPx + halfBtnPx
    val leftSpace = center.x
    val rightSpace = screenWidthPx - center.x
    val rotation = when {
        leftSpace < edgeThreshold -> (1f - leftSpace / edgeThreshold).coerceIn(0f, 1f) * 80f
        rightSpace < edgeThreshold -> -(1f - rightSpace / edgeThreshold).coerceIn(0f, 1f) * 80f
        else -> 0f
    }
    val arcCenterAngle = -90f + rotation

    val hoveredIndex = getHoveredActionIndexWithCenter(
        center = center,
        touch = currentTouch,
        minRadiusPx = minRadiusPx,
        arcCenterAngle = arcCenterAngle,
        radiusPx = radiusPx * progress,
        hitRadiusPx = hitRadiusPx
    )

    val haptic = LocalHapticFeedback.current
    var previousHovered by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(hoveredIndex) {
        if (hoveredIndex != null && hoveredIndex != previousHovered) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        previousHovered = hoveredIndex
    }

    Box(
        Modifier
            .fillMaxSize()
            .onGloballyPositioned { coords ->
                boxPositionInRoot.value = coords.boundsInRoot().topLeft
            }
    ) {
        FAN_MENU_ACTIONS.forEachIndexed { i, action ->
            val angle = arcCenterAngle - 60f + i * 40f
            val angleRad = Math.toRadians(angle.toDouble())
            val currentRadius = radiusPx * progress
            val cx = center.x + currentRadius * kotlin.math.cos(angleRad).toFloat()
            val cy = center.y + currentRadius * kotlin.math.sin(angleRad).toFloat()
            val isHovered = hoveredIndex == i
            val btnScale = if (isHovered) 1.25f else 1f
            val btnAlpha = progress.coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .offset { IntOffset((cx - halfBtnPx).roundToInt(), (cy - halfBtnPx).roundToInt()) }
                    .size(48.dp)
                    .graphicsLayer {
                        scaleX = btnScale * progress
                        scaleY = btnScale * progress
                        alpha = btnAlpha
                    }
                    .shadow(if (isHovered) 4.dp else 0.dp, CircleShape)
                    .clip(CircleShape)
                    .background(action.third)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(action.first, action.second, tint = Color.White, modifier = Modifier.size(24.dp))
                androidx.compose.animation.AnimatedVisibility(
                    visible = isHovered,
                    enter = fadeIn() + slideInVertically { 20 },
                    exit = fadeOut() + slideOutVertically { 20 },
                    modifier = Modifier.offset(y = (-38).dp)
                ) {
                    Text(
                        text = action.second,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color(0x99000000), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// 🟢 定義 APP 當前渲染模式
enum class AppUiMode { LIGHTWEIGHT, FULL }

// 🟢 判斷進程係咪啱啱啟動 (冷啟動)
private var isProcessAlive = false

// =========================================================
//  ★ MainActivity
// =========================================================
class MainActivity : ComponentActivity() {

    companion object {
        const val EXTRA_OPEN_KEYBOARD = "extra_open_keyboard"
        const val EXTRA_PREFILL_NOTE = "extra_prefill_note"
        const val EXTRA_PREFILL_AMOUNT = "extra_prefill_amount"
        const val EXTRA_PREFILL_CATEGORY = "extra_prefill_category"
        const val EXTRA_CANCEL_NOTIF_ID = "extra_cancel_notif_id"
        const val EXTRA_FROM_TILE = "extra_from_tile"
        /** 桌面長按 App icon → 語音記帳 Shortcut */
        const val EXTRA_VOICE_INPUT = "extra_voice_input"
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition { !AppStartupState.isFirstDataReady }
        super.onCreate(savedInstanceState)

        // ★ 強制 edge-to-edge + 自己控制 Insets
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val isColdStart = !isProcessAlive
        isProcessAlive = true
        val isFromTile = intent?.getBooleanExtra(EXTRA_FROM_TILE, false) == true
        val startLightweight = isColdStart && isFromTile

        handleLaunchIntent(intent)

        lifecycleScope.launch(Dispatchers.IO) {
            NotificationCaptureBridge.init(applicationContext)
            CaptureOverlayController.init(applicationContext)
            ensureNotificationChannels(applicationContext)
            cleanupStaleConfirmNotifications(applicationContext)
        }

        setContent {
            val token = keyboardLaunchToken.value
            val voiceToken = voiceLaunchToken.value
            val prefillNote = keyboardPrefillNote.value
            val prefillAmount = keyboardPrefillAmount.value
            val prefillCategory = keyboardPrefillCategory.value
            val notesInfo = topQuickNotesInfo.value

            var currentAppMode by rememberSaveable {
                mutableStateOf(if (startLightweight) AppUiMode.LIGHTWEIGHT else AppUiMode.FULL)
            }

            LaunchedEffect(notesInfo) {
                persistTopQuickNotes(notesInfo)
                showQuickAddNotification()
            }

            LaunchedEffect(Unit) {
                delay(800)
                askNotificationPermissionIfNeeded()
            }

            MaterialTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars),   // ★ 最關鍵
                    color = SURFACE_BG
                ) {
                    if (currentAppMode == AppUiMode.LIGHTWEIGHT) {
                        LightweightEntryApp(
                            vm = viewModel(),
                            onTransitionToMain = { stateToTransfer ->
                                if (stateToTransfer != null) {
                                    keyboardPrefillAmount.value = stateToTransfer.amountText.toDoubleOrNull()
                                    keyboardPrefillNote.value = stateToTransfer.noteText
                                    keyboardPrefillCategory.value = stateToTransfer.category
                                    keyboardLaunchToken.value += 1
                                } else {
                                    keyboardLaunchToken.value = 0
                                    keyboardPrefillAmount.value = null
                                    keyboardPrefillNote.value = null
                                    keyboardPrefillCategory.value = null
                                }
                                currentAppMode = AppUiMode.FULL
                            }
                        )
                    } else {
                        MainApp(
                            keyboardLaunchToken = token,
                            voiceLaunchToken = voiceToken,
                            prefillNoteOnLaunch = prefillNote,
                            prefillAmountOnLaunch = prefillAmount,
                            prefillCategoryOnLaunch = prefillCategory,
                            onKeyboardLaunchConsumed = {
                                keyboardPrefillNote.value = null
                                keyboardPrefillAmount.value = null
                                keyboardPrefillCategory.value = null
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showQuickAddNotification()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleLaunchIntent(intent)
    }

    private fun handleLaunchIntent(intent: Intent?) {
        if (intent == null) return

        // 🟢 桌面 Shortcut：語音記帳（extra 可能係 boolean 或字串 "true"）
        val voiceExtra = intent.getBooleanExtra(EXTRA_VOICE_INPUT, false) ||
            intent.getStringExtra(EXTRA_VOICE_INPUT) == "true"
        if (voiceExtra) {
            voiceLaunchToken.value += 1
            intent.removeExtra(EXTRA_VOICE_INPUT)
            return
        }
        // 🟢 DeepLink：ledgerapp://voice
        val uri = intent.data
        if (uri != null && uri.scheme == "ledgerapp" && uri.host == "voice") {
            voiceLaunchToken.value += 1
            intent.data = null
            return
        }
        // 🟢 處理來自 UPK 流体云嘅 DeepLink 指令
        if (uri != null && uri.scheme == "ledgerapp" && uri.host == "quickadd") {
            keyboardLaunchToken.value += 1
            keyboardPrefillNote.value = uri.getQueryParameter("note")
            keyboardPrefillCategory.value = uri.getQueryParameter("category")
            keyboardPrefillAmount.value = uri.getQueryParameter("amount")?.toDoubleOrNull()
            keyboardPrefillFluidCardId.value = uri.getQueryParameter("card_id")
            intent.data = null
        }
        // 處理原生通知 / 磁貼啟動
        else if (intent.getBooleanExtra(EXTRA_OPEN_KEYBOARD, false)) {
            keyboardLaunchToken.value += 1
            keyboardPrefillNote.value = intent.getStringExtra(EXTRA_PREFILL_NOTE)
            keyboardPrefillCategory.value = intent.getStringExtra(EXTRA_PREFILL_CATEGORY)
            keyboardPrefillAmount.value = if (intent.hasExtra(EXTRA_PREFILL_AMOUNT))
                intent.getDoubleExtra(EXTRA_PREFILL_AMOUNT, 0.0) else null

            if (intent.hasExtra(EXTRA_CANCEL_NOTIF_ID)) {
                keyboardPrefillNotifId.value = intent.getIntExtra(EXTRA_CANCEL_NOTIF_ID, -1)
            }

            intent.removeExtra(EXTRA_OPEN_KEYBOARD)
        }
    }

    private fun askNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < 33) return
        val granted = checkSelfPermission("android.permission.POST_NOTIFICATIONS") ==
            PackageManager.PERMISSION_GRANTED
        if (!granted) notificationPermissionLauncher.launch("android.permission.POST_NOTIFICATIONS")
    }

    private fun persistTopQuickNotes(infos: List<QuickNoteInfo>) {
        try {
            val prefs = getSharedPreferences("ledger_prefs", Context.MODE_PRIVATE)
            val arr = org.json.JSONArray()
            infos.take(5).forEach { info ->
                val obj = org.json.JSONObject()
                obj.put("note", info.note)
                obj.put("category", info.category)
                obj.put("amount", info.amount)
                arr.put(obj)
            }
            prefs.edit().putString("top_quick_notes", arr.toString()).apply()
        } catch (_: Throwable) {}
    }

    private fun showQuickAddNotification() {
        val ctx = applicationContext
        lifecycleScope.launch(Dispatchers.IO) {
            try { refreshQuickAddNotification(ctx) } catch (_: Exception) {}
        }
    }
}

// =========================================================
//  ★ LightweightEntryApp (極簡冷啟動介面)
// =========================================================
@Composable
fun LightweightEntryApp(
    vm: LedgerViewModel,
    onTransitionToMain: (KeyboardState?) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current

    val ui by vm.ledgerUiState.collectAsState()
    val lastSavedCategory by vm.lastSavedCategory.collectAsState()

    var keyboardState by remember {
        val cat = if (lastSavedCategory == "旅遊") "旅遊" else "飲食"
        val cur = if (cat == "旅遊") "JPY" else "HKD"
        mutableStateOf(KeyboardState(category = cat, currency = cur, selectAmountOnInput = false))
    }

    val appearProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        appearProgress.animateTo(1f, spring(dampingRatio = 0.85f, stiffness = 300f))
    }

    fun performSave() {
        val inputAmt = keyboardState.amountText.toDoubleOrNull() ?: return
        if (inputAmt <= 0.0) {
            Toast.makeText(context, "請輸入金額", Toast.LENGTH_SHORT).show()
            return
        }
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        val amt = if (keyboardState.category == "旅遊" && keyboardState.currency == "JPY") {
            val rate = keyboardState.exchangeRateText.toDoubleOrNull() ?: 0.052
            inputAmt * rate
        } else inputAmt

        val ts = keyboardState.timestamp
        val inherited = vm.latestIconForNote(keyboardState.noteText)

        vm.updateLastSavedCategory(keyboardState.category)
        vm.addRecord(
            Record(amount = amt, note = keyboardState.noteText, category = keyboardState.category,
                   timestamp = ts, iconUrl = inherited)
        )
        Toast.makeText(context, "✅ 記帳成功", Toast.LENGTH_SHORT).show()

        focusManager.clearFocus()
        onTransitionToMain(null)
    }

    Box(Modifier.fillMaxSize().background(SURFACE_BG)) {
        Row(
            Modifier.fillMaxWidth().padding(top = 64.dp, start = 24.dp, end = 20.dp)
                .graphicsLayer {
                    alpha = appearProgress.value
                    translationY = (1f - appearProgress.value) * -50f
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("快速記帳", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TEXT_PRIMARY)
                Spacer(Modifier.height(4.dp))
                Text("極速啟動模式", fontSize = 14.sp, color = TEXT_SECONDARY, fontWeight = FontWeight.Medium)
            }

            Surface(
                onClick = {
                    focusManager.clearFocus()
                    onTransitionToMain(keyboardState)
                },
                shape = CircleShape,
                color = BRAND_PRIMARY_LIGHT
            ) {
                Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.OpenInFull, null, modifier = Modifier.size(16.dp), tint = BRAND_PRIMARY_DARK)
                    Spacer(Modifier.width(6.dp))
                    Text("完整模式", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BRAND_PRIMARY_DARK)
                }
            }
        }

        val densityLw = LocalDensity.current
        val imeBottomLw = WindowInsets.ime.getBottom(densityLw)
        val navBottomLw = WindowInsets.navigationBars.getBottom(densityLw)
        val imeLiftLw = with(densityLw) { (imeBottomLw - navBottomLw).coerceAtLeast(0).toDp() }
        val isImeOpenLw = imeLiftLw > 0.dp // ★ 加入呢行：偵測系統鍵盤是否打開

        Column(
            Modifier.align(Alignment.BottomCenter)
                .padding(bottom = 20.dp + imeLiftLw)
                .graphicsLayer {
                    alpha = appearProgress.value
                    translationY = (1f - appearProgress.value) * 150f
                }
        ) {

            if (ui.topNotes.isNotEmpty() || ui.hiddenQuickNotes.isNotEmpty()) {
                QuickInputSection(
                    topNotes = ui.topNotes,
                    noteIconMap = ui.noteIconMap,
                    noteCategoryMap = ui.noteCategoryMap,
                    noteUsageCount = ui.noteUsageCount,
                    maxRowsPerPage = 3,
                    skipAppearAnimation = false,
                    collapseProgress = 0f,
                    onExpandClick = {},
                    onClick = { name ->
                        val amt = ui.recentAmountByNote[name]
                        val cat = ui.noteCategoryMap[name] ?: if (lastSavedCategory == "旅遊") "旅遊" else "飲食"
                        val cur = if (cat == "旅遊") "JPY" else "HKD"
                        val amtText = amt?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: ""

                        keyboardState = keyboardState.copy(
                            noteText = name,
                            amountText = amtText,
                            category = cat,
                            currency = cur,
                            selectAmountOnInput = amt != null && amt > 0.0,
                            focusedField = "amount"
                        )
                    },
                    hiddenQuickNotes = ui.hiddenQuickNotes,
                    onHideNote = { vm.hideQuickNote(it) },
                    onUnhideNote = { vm.unhideQuickNote(it) }
                )
                Spacer(Modifier.height(8.dp))
            }

            LedgerKeyboardPanel(
                state = keyboardState,
                onStateChange = { keyboardState = it },
                onDismiss = {
                    focusManager.clearFocus()
                    onTransitionToMain(null)
                },
                onConfirm = { performSave() },
                onNext = { keyboardState = keyboardState.copy(editingNote = true) },
                allNoteNames = ui.allNoteNames,
                noteCategoryMap = ui.noteCategoryMap,
                noteIconMap = ui.noteIconMap,
                nameFlashTrigger = 0,
                showKeyboard = true,
                imeOpen = isImeOpenLw, // ★ 加入呢行：將系統鍵盤狀態傳入
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
        }
    }
}

// =========================================================
//  導航 & 模糊緩存
// =========================================================
private val MAIN_NAV_ITEMS = listOf(
    NavItem("記帳", Icons.Default.Receipt),
    NavItem("比較", Icons.Default.CompareArrows),
    NavItem("月曆", Icons.Default.CalendarMonth),
    NavItem("透視", Icons.Default.TableView)      // ★ 新增第 4 個 Tab
)

private val blurEffectCache = ConcurrentHashMap<Int, androidx.compose.ui.graphics.RenderEffect>(32)

private fun getCachedBlurEffect(radiusPx: Int): androidx.compose.ui.graphics.RenderEffect? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || radiusPx <= 0) return null
    return blurEffectCache.getOrPut(radiusPx) {
        val r = radiusPx.toFloat()
        android.graphics.RenderEffect.createBlurEffect(
            r, r, android.graphics.Shader.TileMode.CLAMP
        ).asComposeRenderEffect()
    }
}

// =========================================================
//  ★ MainApp
// =========================================================
@Composable
fun MainApp(
    keyboardLaunchToken: Int = 0,
    voiceLaunchToken: Int = 0,
    prefillNoteOnLaunch: String? = null,
    prefillAmountOnLaunch: Double? = null,
    prefillCategoryOnLaunch: String? = null,
    onKeyboardLaunchConsumed: () -> Unit = {},
    vm: LedgerViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val lastSavedCategory by vm.lastSavedCategory.collectAsState()
    val ui by vm.ledgerUiState.collectAsState()
    val filter by vm.filterState.collectAsState()
    val justAddedIds by vm.justAddedIds.collectAsState()
    val deletingIds by vm.deletingIds.collectAsState()
    val rawRecords by vm.records.collectAsState()
    val weeklyExpenses = remember(rawRecords) { computeWeeklyExpenses(rawRecords) }
    var hasPlayedWeeklyIntro by rememberSaveable { mutableStateOf(false) }

    var ledgerHasBeenShownOnce by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) { ledgerHasBeenShownOnce = true }

    var expandedId by remember { mutableStateOf<String?>(null) }
    var currentPage by remember { mutableIntStateOf(0) }
    // 其他頁撳 FAB 返記帳頁：首幀由隱藏狀態開始滑入（避免閃爍）
    var headerEnterFromHidden by remember { mutableStateOf(false) }

    fun goLedgerAndAdd(openKeyboard: () -> Unit = {}) {
        if (currentPage != 0) {
            headerEnterFromHidden = true
            currentPage = 0
        }
        openKeyboard()
    }
    var filterSearch by remember { mutableStateOf(TextFieldValue("")) }
    var filterSearchHasFocus by remember { mutableStateOf(false) }
    var filterSelectAllTrigger by remember { mutableIntStateOf(0) }
    val filterSearchFocusRequester = remember { FocusRequester() }

    var iconTargetRecord by remember { mutableStateOf<Record?>(null) }
    var webSearchTarget by remember { mutableStateOf<Record?>(null) }
    var showIconSourceDialog by remember { mutableStateOf(false) }
    var showUrlInputDialog by remember { mutableStateOf(false) }
    var urlInput by remember { mutableStateOf("") }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }
    // ★ 待確認圖片效果
    var pendingImageUpload by remember { mutableStateOf<PendingImageUpload?>(null) }
    // 還原未完成入帳草稿；唔自動開鍵盤（除 shortcut／磁貼）
    val restoredDraft = remember { loadKeyboardDraft(context) }
    var showKeyboard by remember { mutableStateOf(restoredDraft.first) }
    var keyboardState by remember { mutableStateOf(restoredDraft.second) }

    var lastManualAmountInputMs by remember { mutableLongStateOf(0L) }

    // 同頂部滑入（headerEnter）共用：展開 30ms delay + 480ms；收起 420ms
    val keyboardAnimProgress = remember { Animatable(0f) }
    LaunchedEffect(showKeyboard) {
        if (showKeyboard) {
            keyboardAnimProgress.snapTo(0f)
            delay(HEADER_KEYBOARD_EXPAND_DELAY_MS)
            keyboardAnimProgress.animateTo(
                1f,
                tween(HEADER_KEYBOARD_EXPAND_MS, easing = FastOutSlowInEasing)
            )
        } else {
            keyboardAnimProgress.animateTo(
                0f,
                tween(HEADER_KEYBOARD_COLLAPSE_MS, easing = FastOutSlowInEasing)
            )
        }
    }

    // 持續保存草稿同開關狀態
    LaunchedEffect(showKeyboard, keyboardState) {
        saveKeyboardDraft(context, showKeyboard, keyboardState)
    }

    var scrollToTopTrigger by remember { mutableIntStateOf(0) }
    var quickInputScrollToFirstTrigger by remember { mutableIntStateOf(0) }
    var nameFlashTrigger by remember { mutableIntStateOf(0) }
    var afterSaveHint by remember { mutableStateOf<AfterSaveHint?>(null) }
    var afterSaveHintVisible by remember { mutableStateOf(false) }

    /** 收起鍵盤，但保留未完成輸入 */
    fun dismissKeyboard() {
        showKeyboard = false
        keyboardController?.hide()
        focusManager.clearFocus()
        saveKeyboardDraft(context, false, keyboardState)
    }

    /** 入帳成功後先清空草稿 */
    fun clearKeyboardAfterSave() {
        keyboardState = KeyboardState()
        showKeyboard = false
        keyboardController?.hide()
        focusManager.clearFocus()
        clearKeyboardDraftPrefs(context)
    }

    LaunchedEffect(currentPage) {
        // 轉去其他頁只收起鍵盤，保留草稿
        if (currentPage != 0 && showKeyboard) {
            showKeyboard = false
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }

    // ★ 比較頁快照 - 導航切換觸發
    LaunchedEffect(currentPage) {
        if (currentPage == 1) vm.onComparePageEnter()
        else vm.onComparePageExit()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    // 離開 App：保存狀態，唔清空、唔強制關（還原時跟 prefs）
                    saveKeyboardDraft(context, showKeyboard, keyboardState)
                }
                Lifecycle.Event.ON_RESUME -> {
                    // 唔自動開關鍵盤；只確保草稿仍在 memory
                    val (open, draft) = loadKeyboardDraft(context)
                    // 若 memory 已被清但 prefs 有草稿，補返
                    if (keyboardState.amountText.isBlank() && keyboardState.noteText.isBlank() &&
                        (draft.amountText.isNotBlank() || draft.noteText.isNotBlank())
                    ) {
                        keyboardState = draft
                    }
                    // 唔喺 resume 強行 set showKeyboard，避免同用戶意圖衝突
                    // （重新進入由 remember 初值／Activity recreate 還原）
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(filterSearch.text) {
        delay(250)
        vm.setFilterSearch(filterSearch.text)
    }

    LaunchedEffect(ui.topQuickNotesInfo) {
        topQuickNotesInfo.value = ui.topQuickNotesInfo
    }

    LaunchedEffect(afterSaveHint?.recordId) {
        val hint = afterSaveHint
        if (hint == null) {
            afterSaveHintVisible = false
            return@LaunchedEffect
        }
        afterSaveHintVisible = true
        delay(5000)
        afterSaveHintVisible = false
        delay(320)
        if (afterSaveHint?.recordId == hint.recordId) afterSaveHint = null
    }

    var recentlyDeletedRecord by remember { mutableStateOf<Record?>(null) }
    var showUndoToast by remember { mutableStateOf(false) }
    var deleteJob by remember { mutableStateOf<Job?>(null) }

    // (刪除咗 transportDialogState 同 transportPendingNotifId)

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        val target = iconTargetRecord
        if (uri != null && target != null) {
            scope.launch {
                uploading = true
                val bitmaps = loadAndProcessBitmaps(context, uri)
                uploading = false
                if (bitmaps != null) {
                    pendingImageUpload = PendingImageUpload(target, bitmaps.first, bitmaps.second)
                } else {
                    Toast.makeText(context, "讀取圖片失敗", Toast.LENGTH_SHORT).show()
                }
            }
        }
        iconTargetRecord = null
    }

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val target = iconTargetRecord
        val uri = pendingCameraUri
        if (ok && target != null && uri != null) {
            scope.launch {
                uploading = true
                val bitmaps = loadAndProcessBitmaps(context, uri)
                uploading = false
                if (bitmaps != null) {
                    pendingImageUpload = PendingImageUpload(target, bitmaps.first, bitmaps.second)
                } else {
                    Toast.makeText(context, "讀取圖片失敗", Toast.LENGTH_SHORT).show()
                }
                try {
                    uri.path?.let { p ->
                        val f = java.io.File(p)
                        if (f.exists()) f.delete()
                    }
                } catch (_: Exception) {}
            }
        }
        pendingCameraUri = null
        iconTargetRecord = null
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val target = iconTargetRecord
        if (granted && target != null) {
            try {
                val uri = createTempImageUri(context)
                if (uri != null) {
                    pendingCameraUri = uri
                    takePictureLauncher.launch(uri)
                } else {
                    Toast.makeText(context, "開啟相機失敗", Toast.LENGTH_SHORT).show()
                    iconTargetRecord = null
                }
            } catch (e: Exception) {
                Toast.makeText(context, "開啟相機失敗：${e.message}", Toast.LENGTH_SHORT).show()
                iconTargetRecord = null
            }
        } else if (!granted) {
            Toast.makeText(context, "需要相機權限才能拍照", Toast.LENGTH_SHORT).show()
            iconTargetRecord = null
        }
    }

    fun openKeyboardForNew(note: String = "", amount: Double? = null, forceCategory: String? = null) {
        val now = System.currentTimeMillis()
        val keepManualAmount = showKeyboard &&
            keyboardState.amountText.isNotBlank() &&
            lastManualAmountInputMs > 0L &&
            (now - lastManualAmountInputMs) < 15_000L
        val amtText = if (keepManualAmount) keyboardState.amountText else {
            amount?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: ""
        }
        val defaultCategory = forceCategory ?: if (lastSavedCategory == "旅遊") "旅遊" else "飲食"
        val defaultCurrency = if (defaultCategory == "旅遊") "JPY" else "HKD"
        if (!showKeyboard) {
            keyboardState = KeyboardState(
                noteText = note, amountText = amtText,
                category = defaultCategory, currency = defaultCurrency,
                selectAmountOnInput = amount != null && !keepManualAmount,
                editingNote = false,
                focusedField = "amount"
            )
            showKeyboard = true
        } else {
            keyboardState = keyboardState.copy(
                noteText = note,
                amountText = amtText.ifBlank { keyboardState.amountText },
                category = defaultCategory, currency = defaultCurrency,
                selectAmountOnInput = if (keepManualAmount) false else (amount != null),
                editingNote = false,
                focusedField = "amount"
            )
        }
        if (note.isNotBlank()) nameFlashTrigger++
    }

    fun openKeyboardForCopy(r: Record) {
        keyboardState = KeyboardState(
            amountText = r.amount.toString(), noteText = r.note,
            category = r.category, selectAmountOnInput = true,
            editingNote = false, focusedField = "amount")
        showKeyboard = true
    }

    fun openKeyboardForEdit(r: Record) {
        val amt = if (r.amount % 1.0 == 0.0) r.amount.toInt().toString() else r.amount.toString()
        keyboardState = KeyboardState(
            amountText = amt, noteText = r.note, category = r.category,
            editingNote = false, focusedField = "amount",
            editingRecordId = r.id, timestamp = r.timestamp, selectAmountOnInput = true)
        showKeyboard = true
    }

    fun openKeyboardForce(note: String?, forceCategory: String? = null) {
        val defaultCategory = forceCategory ?: if (lastSavedCategory == "旅遊") "旅遊" else "飲食"
        val defaultCurrency = if (defaultCategory == "旅遊") "JPY" else "HKD"
        if (note == null) {
            keyboardState = KeyboardState(
                noteText = "", amountText = "",
                category = defaultCategory, currency = defaultCurrency
            )
        } else {
            if (showKeyboard) {
                keyboardState = keyboardState.copy(noteText = note, editingNote = false)
            } else {
                keyboardState = KeyboardState(
                    noteText = note, amountText = "",
                    category = defaultCategory, currency = defaultCurrency
                )
            }
            if (note.isNotBlank()) nameFlashTrigger++
        }
        showKeyboard = true
    }

    fun deleteRecordWithUndo(r: Record) {
        deleteJob?.cancel()
        vm.markAsDeleting(r.id)
        recentlyDeletedRecord = r
        showUndoToast = true
        deleteJob = scope.launch {
            delay(900)
            vm.commitDelete(r.id)
            delay(4100)
            if (recentlyDeletedRecord?.id == r.id) {
                showUndoToast = false
                recentlyDeletedRecord = null
            }
        }
    }

    fun restoreDeletedRecord() {
        val target = recentlyDeletedRecord ?: return
        deleteJob?.cancel(); deleteJob = null
        showUndoToast = false
        recentlyDeletedRecord = null
        vm.cancelDelete(target.id)
        vm.restoreRecord(target)
    }

    fun saveFromKeyboard() {
        val inputAmt = keyboardState.amountText.toDoubleOrNull() ?: return
        if (inputAmt <= 0.0) {
            Toast.makeText(context, "請輸入金額", Toast.LENGTH_SHORT).show(); return
        }
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        val amt = if (keyboardState.category == "旅遊" && keyboardState.currency == "JPY") {
            val rate = keyboardState.exchangeRateText.toDoubleOrNull() ?: 0.052
            inputAmt * rate
        } else inputAmt
        val note = keyboardState.noteText
        val category = keyboardState.category
        vm.updateLastSavedCategory(category)
        val editId = keyboardState.editingRecordId
        val ts = keyboardState.timestamp
        if (editId != null) {
            vm.updateRecord(editId, amt, note, category, ts)
        } else {
            val inherited = vm.latestIconForNote(note)
            val monthKey = monthKeyFromTimestamp(ts)
            val totalThisMonth = vm.monthTotal(category, monthKey) + amt
            vm.addRecord(
                Record(amount = amt, note = note, category = category,
                       timestamp = ts, iconUrl = inherited)
            ) { newId -> afterSaveHint = AfterSaveHint(newId, category, totalThisMonth) }
        }

        clearKeyboardAfterSave()
        scrollToTopTrigger++

        keyboardPrefillNotifId.value?.let { notifId ->
            if (notifId != -1) {
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                nm?.cancel(notifId)
                keyboardPrefillNotifId.value = null
            }
        }

        keyboardPrefillFluidCardId.value?.let { cardId ->
            OppoFluidCloudBridge.dismissCard(context, cardId)
            keyboardPrefillFluidCardId.value = null
        }
    }

    LaunchedEffect(keyboardLaunchToken) {
        if (keyboardLaunchToken > 0) {
            delay(400)
            if (filter.modeOn) vm.setFilterModeOn(false)
            if (currentPage != 0) currentPage = 0

            val note = prefillNoteOnLaunch
            val amt = prefillAmountOnLaunch
            val cat = prefillCategoryOnLaunch

            // 🟢 當收到「地鐵」且有通知 ID 時，直接彈鍵盤，默認地鐵
            if (note == "地鐵" && amt != null && amt > 0.0 && keyboardPrefillNotifId.value != null) {
                openKeyboardForNew("地鐵", amt, "交通")
            } else {
                // 原有邏輯
                when {
                    note.isNullOrBlank() && amt == null -> openKeyboardForce(null)
                    amt != null && amt > 0.0 -> openKeyboardForNew(note ?: "", amt, cat)
                    else -> openKeyboardForce(note, cat)
                }
            }

            quickInputScrollToFirstTrigger++
            onKeyboardLaunchConsumed()
            keyboardPrefillCategory.value = null
        }
    }

    BackHandler(enabled = showKeyboard) { dismissKeyboard() }
    BackHandler(enabled = filter.modeOn && !showKeyboard && currentPage == 0) {
        vm.setFilterModeOn(false)
    }
    LaunchedEffect(filterSelectAllTrigger) {
        if (filterSelectAllTrigger > 0 && filter.modeOn && currentPage == 0) {
            delay(300)
            try {
                filterSearchFocusRequester.requestFocus()
                filterSearch = filterSearch.copy(selection = TextRange(0, filterSearch.text.length))
            } catch (_: Exception) {}
        }
    }

    val keyboardProgressProvider = remember { { keyboardAnimProgress.value } }

    Box(Modifier.fillMaxSize().background(SURFACE_BG)) {

        when (currentPage) {
            0 -> LedgerContent(
                isFirstShow = !ledgerHasBeenShownOnce,
                loading = ui.loading,
                filtered = ui.ledgerRecords,
                groupedByDate = ui.groupedByDate,
                topNotes = ui.topNotes,
                noteIconMap = ui.noteIconMap,
                noteCategoryMap = ui.noteCategoryMap,
                noteUsageCount = ui.noteUsageCount,
                hasIncome = ui.hasIncome,
                hasExpense = ui.hasExpense,
                totalIncome = ui.totalIncome,
                totalExpense = ui.totalExpense,
                filterMode = filter.modeOn,
                filterCategory = filter.category,
                onFilterCategoryChange = { vm.setFilterCategory(it) },
                filterMonth = filter.month,
                onFilterMonthChange = { vm.setFilterMonth(it) },
                // 👉 加入呢三行
                filterStartTimeMin = filter.startTimeMin,
                filterEndTimeMin = filter.endTimeMin,
                onFilterTimeRangeChange = { s, e -> vm.setFilterTimeRange(s, e) },
                availableMonths = ui.availableMonths,
                visibleCategories = ui.visibleCategories,
                expandedId = expandedId,
                onExpandChange = { expandedId = it },
                isExactNoteFilter = ui.isExactNoteFilter,
                onQuickInputClick = { name ->
                    val amt = ui.recentAmountByNote[name]
                    openKeyboardForNew(name, if (amt != null && amt > 0.0) amt else null)
                },
                onCopyClick = { openKeyboardForCopy(it) },
                onEditClick = { openKeyboardForEdit(it) },
                onDeleteClick = { deleteRecordWithUndo(it) },
                onChangeIconClick = { iconTargetRecord = it; showIconSourceDialog = true },
                onFilterByName = { name ->
                    vm.setFilterCategory(null)
                    vm.setFilterMonth(null)
                    filterSearch = TextFieldValue(name)
                    vm.setFilterSearch(name)
                    vm.setFilterModeOn(true)
                },
                showKeyboard = showKeyboard,
                showFuture = filter.showFuture,
                onShowFutureChange = { vm.setShowFuture(it) },
                scrollToTopTrigger = scrollToTopTrigger,
                justAddedIds = justAddedIds,
                deletingIds = deletingIds,
                afterSaveHint = afterSaveHint,
                afterSaveHintVisible = afterSaveHintVisible,
                keyboardProgressProvider = keyboardProgressProvider,
                weeklyExpenses = weeklyExpenses,
                shouldPlayWeeklyIntro = !hasPlayedWeeklyIntro && !ui.loading,
                onWeeklyIntroFinished = { hasPlayedWeeklyIntro = true },
                hiddenQuickNotes = ui.hiddenQuickNotes,
                onHideNote = { vm.hideQuickNote(it) },
                onUnhideNote = { vm.unhideQuickNote(it) },
                quickInputScrollToFirstTrigger = quickInputScrollToFirstTrigger,
                headerEnterFromHidden = headerEnterFromHidden,
                onHeaderEnterConsumed = { headerEnterFromHidden = false }
            )
            1 -> {
                val compareMonthA by vm.compareMonthA.collectAsState()
                val compareMonthB by vm.compareMonthB.collectAsState()
                val compareUi by vm.compareUiState.collectAsState()
                CompareContent(
                    compareUi = compareUi,
                    selectedMonthA = compareMonthA,
                    onMonthAChange = { newA ->
                        var newB = compareMonthB
                        if (newA == newB) newB = shiftMonthKey(newA, 1)
                        vm.setCompareMonthA(newA)
                        if (newB != compareMonthB) vm.setCompareMonthB(newB)
                    },
                    selectedMonthB = compareMonthB,
                    onMonthBChange = { newB ->
                        var newA = compareMonthA
                        if (newB == newA) newA = shiftMonthKey(newB, -1)
                        vm.setCompareMonthB(newB)
                        if (newA != compareMonthA) vm.setCompareMonthA(newA)
                    },
                    onAddClick = { goLedgerAndAdd { openKeyboardForNew() } }
                )
            }
            2 -> {
                val calendarMonthKey by vm.calendarMonthKey.collectAsState()
                val calendarFilterCategory by vm.calendarFilterCategory.collectAsState()
                val calendarUi by vm.calendarUiState.collectAsState()
                CalendarContent(
                    calendarUi = calendarUi,
                    currentMonthKey = calendarMonthKey,
                    onMonthChange = { vm.setCalendarMonthKey(it) },
                    filterCategory = calendarFilterCategory,
                    onFilterCategoryChange = { vm.setCalendarFilterCategory(it) },
                    onCopyClick = { r -> goLedgerAndAdd { openKeyboardForCopy(r) } },
                    onEditClick = { r -> goLedgerAndAdd { openKeyboardForEdit(r) } },
                    onFilterClick = { r ->
                        goLedgerAndAdd {
                            vm.setFilterCategory(null)
                            vm.setFilterMonth(null)
                            filterSearch = TextFieldValue(r.note)
                            vm.setFilterSearch(r.note)
                            vm.setFilterModeOn(true)
                        }
                    },
                    onDeleteClick = { deleteRecordWithUndo(it) },
                    onChangeIconClick = { iconTargetRecord = it; showIconSourceDialog = true },
                    onAddClick = { goLedgerAndAdd { openKeyboardForNew() } },
                    deletingIds = deletingIds,
                    justAddedIds = justAddedIds
                )
            }
            // ★ 資料透視表頁
            3 -> {
                val pivotUi by vm.pivotUiState.collectAsState()
                PivotContent(
                    pivotUi = pivotUi,
                    onYearChange = { vm.setPivotYear(it) },
                    onAddClick = { goLedgerAndAdd { openKeyboardForNew() } }
                )
            }
        }

        AnimatedVisibility(
            visible = showUndoToast,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeIn(tween(250)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(250, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(200)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 88.dp)
                .padding(horizontal = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(CircleShape)
                    .background(SURFACE_CARD)
                    .border(0.5.dp, DIVIDER_COLOR, CircleShape)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(start = 8.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(SURFACE_ELEVATED),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Delete, "刪除", tint = Color(0xFFF87171),
                                modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("記錄已刪除", color = TEXT_PRIMARY, fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold)
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { restoreDeletedRecord() }
                            .background(BRAND_PRIMARY.copy(alpha = 0.12f))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("復原", color = BRAND_PRIMARY_DARK,
                            fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = NAV_BOTTOM_PADDING)
                .graphicsLayer {
                    val p = keyboardProgressProvider()
                    val effectiveP = if (currentPage == 0) p else 0f
                    alpha = (1f - effectiveP * 2.5f).coerceIn(0f, 1f)
                    val s = 1f - (0.15f * effectiveP)
                    scaleX = s
                    scaleY = s
                    translationY = effectiveP * 60.dp.toPx()
                    transformOrigin = TransformOrigin(0.5f, 1f)
                }
        ) {
            FloatingNavBar(
                items = MAIN_NAV_ITEMS,
                selectedIndex = currentPage,
                onIndexChange = { currentPage = it },
                modifier = Modifier
            )
        }

        KeyboardAndFabLayer(
            showKeyboard = showKeyboard,
            progressProvider = keyboardProgressProvider,
            currentPage = currentPage,
            filterModeOn = filter.modeOn,
            filterSearch = filterSearch,
            onFilterSearchChange = { filterSearch = it },
            filterSearchHasFocus = filterSearchHasFocus,
            onFilterSearchFocusChange = { filterSearchHasFocus = it },
            filterSearchFocusRequester = filterSearchFocusRequester,
            onFilterButtonTap = {
                if (currentPage != 0) {
                    currentPage = 0
                    vm.setFilterModeOn(true)
                } else if (!filter.modeOn) {
                    vm.setFilterModeOn(true)
                } else {
                    filterSelectAllTrigger++
                }
            },
            onFabTap = {
                quickInputScrollToFirstTrigger++
                if (showKeyboard) {
                    keyboardState = keyboardState.copy(amountText = "", selectAmountOnInput = false)
                } else {
                    if (filter.modeOn) vm.setFilterModeOn(false)
                    goLedgerAndAdd { openKeyboardForNew() }
                }
            },
            onVoiceResult = { note, amount ->
                if (filter.modeOn) vm.setFilterModeOn(false)
                val cat = when {
                    note.isNotBlank() -> ui.noteCategoryMap[note]
                    else -> null
                }
                goLedgerAndAdd {
                    openKeyboardForNew(
                        note = note,
                        amount = amount,
                        forceCategory = cat
                    )
                }
                quickInputScrollToFirstTrigger++
            },
            voiceLaunchToken = voiceLaunchToken,
            keyboardState = keyboardState,
            onKeyboardStateChange = { newState ->
                if (newState.amountText != keyboardState.amountText) {
                    lastManualAmountInputMs = System.currentTimeMillis()
                }
                keyboardState = newState
            },
            onKeyboardDismiss = { dismissKeyboard() },
            onKeyboardConfirm = { saveFromKeyboard() },
            onKeyboardNext = { keyboardState = keyboardState.copy(editingNote = true) },
            allNoteNames = ui.allNoteNames,
            noteCategoryMap = ui.noteCategoryMap,
            noteIconMap = ui.noteIconMap, // ★ 加入呢行
            nameFlashTrigger = nameFlashTrigger
        )

        if (uploading) {
            Box(Modifier.fillMaxSize().background(Color(0x80000000)), contentAlignment = Alignment.Center) {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = BRAND_PRIMARY)
                        Spacer(Modifier.height(12.dp))
                        Text("處理中...", color = TEXT_PRIMARY)
                    }
                }
            }
        }
    }

    if (showIconSourceDialog) {
        val target = iconTargetRecord
        val hasIcon = target?.iconUrl?.isNotBlank() == true
        AlertDialog(
            onDismissRequest = { showIconSourceDialog = false; iconTargetRecord = null },
            title = { Text("圖標設定") },
            text = {
                Column {
                    if (target != null && target.note.isNotBlank()) {
                        IconSourceOption(Icons.Default.Search, "搜尋「${target.note}」圖標", tint = BRAND_PRIMARY) {
                            showIconSourceDialog = false
                            webSearchTarget = target
                            iconTargetRecord = null
                        }
                        HorizontalDivider(Modifier.padding(vertical = 4.dp),
                            color = DIVIDER_COLOR.copy(alpha = 0.5f))
                    }
                    IconSourceOption(Icons.Default.PhotoLibrary, "從相冊揀") {
                        showIconSourceDialog = false
                        pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    IconSourceOption(Icons.Default.PhotoCamera, "即時拍照") {
                        showIconSourceDialog = false
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context, android.Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasPermission) {
                            try {
                                val uri = createTempImageUri(context)
                                if (uri != null) {
                                    pendingCameraUri = uri
                                    takePictureLauncher.launch(uri)
                                } else {
                                    Toast.makeText(context, "開啟相機失敗", Toast.LENGTH_SHORT).show()
                                    iconTargetRecord = null
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "開啟相機失敗：${e.message}", Toast.LENGTH_SHORT).show()
                                iconTargetRecord = null
                            }
                        } else {
                            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                        }
                    }
                    IconSourceOption(Icons.Default.Link, "貼上網址") {
                        showIconSourceDialog = false; urlInput = ""; showUrlInputDialog = true
                    }
                    if (hasIcon) {
                        HorizontalDivider(Modifier.padding(vertical = 4.dp))
                        IconSourceOption(Icons.Default.Delete, "刪除圖標", tint = COLOR_EXPENSE) {
                            showIconSourceDialog = false
                            if (target != null) {
                                scope.launch {
                                    uploading = true
                                    val db = withContext(Dispatchers.IO) { Firebase.firestore }
                                    removeIconFromSameName(context, db, target.note)
                                    uploading = false
                                }
                            }
                            iconTargetRecord = null
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showIconSourceDialog = false; iconTargetRecord = null }) { Text("取消") }
            }
        )
    }

    if (showUrlInputDialog) {
        AlertDialog(
            onDismissRequest = { showUrlInputDialog = false; urlInput = ""; iconTargetRecord = null },
            title = { Text("輸入圖片網址") },
            text = {
                Column {
                    Text("貼上 PNG / JPG / WebP 圖片連結",
                        style = MaterialTheme.typography.bodySmall, color = TEXT_SECONDARY)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text("URL") },
                        placeholder = { Text("https://...") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BRAND_PRIMARY,
                            unfocusedBorderColor = DIVIDER_COLOR
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val url = urlInput.trim()
                    val target = iconTargetRecord
                    val valid = url.startsWith("http://") || url.startsWith("https://")
                    if (target != null && url.isNotBlank() && valid) {
                        scope.launch {
                            uploading = true
                            val db = withContext(Dispatchers.IO) { Firebase.firestore }
                            applyUrlToSameName(context, db, target.note, url)
                            uploading = false
                        }
                    } else if (!valid) {
                        Toast.makeText(context, "網址要 http:// 或 https:// 開頭",
                            Toast.LENGTH_SHORT).show()
                    }
                    showUrlInputDialog = false; urlInput = ""; iconTargetRecord = null
                }) { Text("確定") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showUrlInputDialog = false; urlInput = ""; iconTargetRecord = null
                }) { Text("取消") }
            }
        )
    }

    if (webSearchTarget != null) {
        val target = webSearchTarget!!
        var selectedImageUrl by remember { mutableStateOf<String?>(null) }
        BackHandler { webSearchTarget = null }

        Box(Modifier.fillMaxSize().zIndex(100f).background(SURFACE_BG)) {
            Column(Modifier.fillMaxSize()) {
                Surface(color = SURFACE_CARD, shadowElevation = 0.dp) {
                    Row(
                        Modifier.fillMaxWidth().height(NAV_HEIGHT + 24.dp).padding(top = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { webSearchTarget = null }) {
                            Icon(Icons.Default.ArrowBack, "返回", tint = TEXT_PRIMARY)
                        }
                        Text("長按圖片設定圖標", fontSize = 18.sp,
                            fontWeight = FontWeight.Bold, color = TEXT_PRIMARY)
                    }
                }

                androidx.compose.ui.viewinterop.AndroidView(
                    factory = { ctx ->
                        android.webkit.WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            webViewClient = android.webkit.WebViewClient()
                            setOnTouchListener { v, event ->
                                when (event.actionMasked) {
                                    android.view.MotionEvent.ACTION_DOWN ->
                                        v.parent?.requestDisallowInterceptTouchEvent(true)
                                    android.view.MotionEvent.ACTION_UP,
                                    android.view.MotionEvent.ACTION_CANCEL ->
                                        v.parent?.requestDisallowInterceptTouchEvent(false)
                                }
                                false
                            }
                            setOnLongClickListener { v ->
                                val result = (v as android.webkit.WebView).hitTestResult
                                if (result.type == android.webkit.WebView.HitTestResult.IMAGE_TYPE ||
                                    result.type == android.webkit.WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                                    val url = result.extra
                                    if (!url.isNullOrBlank()) selectedImageUrl = url
                                }
                                false
                            }
                            val q = Uri.encode(target.note + " icon")
                            loadUrl("https://www.google.com/search?tbm=isch&q=$q")
                        }
                    },
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
            }

            if (selectedImageUrl != null) {
                AlertDialog(
                    onDismissRequest = { selectedImageUrl = null },
                    title = { Text("使用這張圖片？", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("將會設為「${target.note}」的專屬圖標",
                                color = TEXT_SECONDARY, fontSize = 14.sp)
                            Spacer(Modifier.height(16.dp))
                            AsyncImage(
                                model = selectedImageUrl,
                                contentDescription = null,
                                modifier = Modifier.size(120.dp).clip(RoundedCornerShape(24.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val url = selectedImageUrl!!
                            selectedImageUrl = null
                            webSearchTarget = null
                            scope.launch {
                                uploading = true
                                try {
                                    val bitmaps = withContext(Dispatchers.IO) {
                                        val rawBytes = if (url.startsWith("data:image")) {
                                            val base64 = url.substringAfter(",")
                                            android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                                        } else {
                                            val conn = java.net.URL(url).openConnection() as java.net.HttpURLConnection
                                            conn.setRequestProperty("User-Agent", "Mozilla/5.0")
                                            conn.connectTimeout = 10000
                                            conn.readTimeout = 10000
                                            conn.inputStream.readBytes()
                                        }
                                        val srcBitmap = BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size)
                                            ?: throw Exception("圖片解碼失敗（非圖片 / 被攔截 / 格式不支援）")
                                        val original = if (srcBitmap.width > 800 || srcBitmap.height > 800) {
                                            val scaled = scaleKeepAspectWidth(srcBitmap, 800)
                                            if (scaled !== srcBitmap) srcBitmap.recycle()
                                            scaled
                                        } else srcBitmap
                                        val processed = removeBackground(original)
                                        Pair(original, processed)
                                    }
                                    pendingImageUpload = PendingImageUpload(target, bitmaps.first, bitmaps.second)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "下載失敗：${e.message ?: "未知錯誤"}",
                                        Toast.LENGTH_LONG).show()
                                    e.printStackTrace()
                                } finally {
                                    uploading = false
                                }
                            }
                        }) { Text("使用", fontWeight = FontWeight.Bold) }
                    },
                    dismissButton = {
                        TextButton(onClick = { selectedImageUrl = null }) {
                            Text("取消", color = TEXT_SECONDARY)
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    containerColor = SURFACE_CARD
                )
            }
        }
    }

    if (pendingImageUpload != null) {
        Box(Modifier.fillMaxSize().zIndex(200f)) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .pointerInput(Unit) { detectTapGestures { } }
            )
            Box(Modifier.align(Alignment.BottomCenter)) {
                ImageEffectSelectionPanel(
                    state = pendingImageUpload!!,
                    onSelect = { bitmap, forceCrop, isEnhanced ->
                        val current = pendingImageUpload
                        if (current != null) {
                            pendingImageUpload = null
                            val targetRecord = current.record
                            val origBmp = current.originalBitmap
                            val procBmp = current.processedBitmap

                            scope.launch {
                                uploading = true
                                try {
                                    val finalBmp = if (isEnhanced) applyEnhancedFilter(bitmap) else bitmap
                                    val finalBytes = compressBitmapToPngForUpload(finalBmp, forceCrop)

                                    if (finalBmp !== origBmp && finalBmp !== procBmp) finalBmp.recycle()
                                    origBmp.recycle()
                                    procBmp.recycle()

                                    val url = uploadBytesToCloudinary(finalBytes)
                                    if (url != null) {
                                        val db = Firebase.firestore
                                        applyUrlToSameName(context, db, targetRecord.note, url)
                                    } else {
                                        Toast.makeText(context, "上傳失敗,請檢查網絡", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "處理失敗: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    uploading = false
                                }
                            }
                        }
                    }
                )
            }
        }
    }
} // 👈 呢個大括號係 MainApp 嘅結尾，貼到呢度為止！
// =========================================================
//  ★ KeyboardAndFabLayer
// =========================================================
@Composable
private fun BoxScope.KeyboardAndFabLayer(
    showKeyboard: Boolean,
    progressProvider: () -> Float,
    currentPage: Int,
    filterModeOn: Boolean,
    filterSearch: TextFieldValue,
    onFilterSearchChange: (TextFieldValue) -> Unit,
    filterSearchHasFocus: Boolean,
    onFilterSearchFocusChange: (Boolean) -> Unit,
    filterSearchFocusRequester: FocusRequester,
    onFilterButtonTap: () -> Unit,
    onFabTap: () -> Unit,
    onVoiceResult: (note: String, amount: Double?) -> Unit = { _, _ -> },
    voiceLaunchToken: Int = 0,
    keyboardState: KeyboardState,
    onKeyboardStateChange: (KeyboardState) -> Unit,
    onKeyboardDismiss: () -> Unit,
    onKeyboardConfirm: () -> Unit,
    onKeyboardNext: () -> Unit,
    allNoteNames: List<String>,
    noteCategoryMap: Map<String, String>,
    noteIconMap: Map<String, String>,
    nameFlashTrigger: Int,
) {
    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current
    val imeBottomPx = WindowInsets.ime.getBottom(density)
    val navBottomPx = WindowInsets.navigationBars.getBottom(density)
    val imeLiftDp = with(density) { (imeBottomPx - navBottomPx).coerceAtLeast(0).toDp() }
    val imeOpen = imeLiftDp > 0.dp
    val bottomChromePad = if (imeOpen) 8.dp else NAV_HEIGHT + NAV_BOTTOM_PADDING + 12.dp

    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(bottom = imeLiftDp)
    ) {
        // [1] 頂部篩選器列
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = bottomChromePad)
                .height(56.dp)
                .graphicsLayer {
                    val p = progressProvider()
                    alpha = (1f - p * 2.5f).coerceIn(0f, 1f)
                    val s = 1f - (0.15f * p)
                    scaleX = s
                    scaleY = s
                    translationY = p * 60.dp.toPx()
                    transformOrigin = TransformOrigin(0.5f, 1f)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                if (currentPage == 0) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = filterModeOn,
                        enter = slideInHorizontally(animationSpec = tween(300, easing = FastOutSlowInEasing)) { -it } + fadeIn(tween(200)),
                        exit = slideOutHorizontally(animationSpec = tween(260, easing = FastOutSlowInEasing)) { -it } + fadeOut(tween(180))
                    ) {
                        Box(Modifier.padding(vertical = 8.dp).padding(end = 8.dp)) {
                            Surface(
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = SURFACE_CARD,
                                border = BorderStroke(0.5.dp, DIVIDER_COLOR)
                            ) {
                                Row(Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.weight(1f)) {
                                        BasicTextField(
                                            value = filterSearch,
                                            onValueChange = onFilterSearchChange,
                                            singleLine = true,
                                            textStyle = TextStyle(fontSize = 15.sp, color = TEXT_PRIMARY),
                                            cursorBrush = SolidColor(BRAND_PRIMARY),
                                            decorationBox = { inner ->
                                                Box(Modifier.fillMaxWidth(),
                                                    contentAlignment = Alignment.CenterStart) {
                                                    if (filterSearch.text.isEmpty())
                                                        Text("搜尋名稱或類別…", fontSize = 15.sp,
                                                            color = TEXT_TERTIARY)
                                                    inner()
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .focusRequester(filterSearchFocusRequester)
                                                .onFocusChanged { onFilterSearchFocusChange(it.isFocused) }
                                        )
                                    }
                                    if (filterSearch.text.isNotBlank()) {
                                        Spacer(Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.Close, "清除", tint = TEXT_SECONDARY,
                                            modifier = Modifier.size(20.dp)
                                                .clickable { onFilterSearchChange(TextFieldValue("")) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .pointerInput(filterModeOn, currentPage) {
                        detectTapGestures(onTap = { onFilterButtonTap() })
                    },
                shape = CircleShape,
                color = if (filterModeOn && currentPage == 0) BRAND_PRIMARY else SURFACE_CARD,
                border = if (filterModeOn && currentPage == 0) null
                         else BorderStroke(0.5.dp, DIVIDER_COLOR)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.FilterAlt, "篩選",
                        tint = if (filterModeOn && currentPage == 0) Color.White else BRAND_PRIMARY,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(60.dp))
        }

        // [2] 實體鍵盤面板層 (底層)
        AnimatedKeyboardLayer(
            showKeyboard = showKeyboard,
            progressProvider = progressProvider,
            keyboardState = keyboardState,
            onKeyboardStateChange = onKeyboardStateChange,
            onKeyboardDismiss = onKeyboardDismiss,
            onKeyboardConfirm = onKeyboardConfirm,
            onKeyboardNext = onKeyboardNext,
            allNoteNames = allNoteNames,
            noteCategoryMap = noteCategoryMap,
            noteIconMap = noteIconMap,
            nameFlashTrigger = nameFlashTrigger,
            imeOpen = imeOpen,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // [3] FAB 按鈕層 (頂層) —— 精確計算額外高度
        val isTransport = keyboardState.noteText == "地鐵" || keyboardState.noteText == "巴士"
        val isTravel = keyboardState.category == "旅遊"
        var extraHeight = 0.dp
        if (isTransport) extraHeight += 48.dp
        if (isTravel) extraHeight += 48.dp

                // [3] FAB 按鈕層 (頂層)
        AnimatedFabLayer(
            progressProvider = progressProvider,
            bottomPadding = bottomChromePad,
            onFabTap = onFabTap,
            onVoiceResult = onVoiceResult,
            voiceLaunchToken = voiceLaunchToken,
            modifier = Modifier.align(Alignment.BottomEnd)
        )

        // [4] 搜尋建議浮出層
        if (currentPage == 0 && filterModeOn && filterSearchHasFocus) {
            val filterSuggestions = remember(filterSearch.text, allNoteNames) {
                filterNoteSuggestions(filterSearch.text, allNoteNames)
            }
            if (filterSuggestions.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = bottomChromePad + 56.dp + 4.dp)
                        .zIndex(20f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SURFACE_CARD,
                        border = BorderStroke(0.5.dp, DIVIDER_COLOR),
                        modifier = Modifier
                            .width(260.dp)
                            .heightIn(max = 260.dp)
                            .animateContentSize(
                                animationSpec = spring(dampingRatio = 0.85f, stiffness = 350f),
                                alignment = Alignment.BottomCenter
                            )
                    ) {
                        Column(Modifier.verticalScroll(rememberScrollState())) {
                            filterSuggestions.forEach { s ->
                                Row(
                                    Modifier.fillMaxWidth()
                                        .clickable {
                                            // 填入建議後即收起下拉（含完全相同字串）
                                            onFilterSearchChange(TextFieldValue(s))
                                            onFilterSearchFocusChange(false)
                                            focusManager.clearFocus(force = true)
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(s, fontSize = 14.sp, color = TEXT_PRIMARY,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                HorizontalDivider(color = DIVIDER_COLOR, thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun AnimatedFabLayer(
    progressProvider: () -> Float,
    bottomPadding: Dp,
    onFabTap: () -> Unit,
    onVoiceResult: (note: String, amount: Double?) -> Unit = { _, _ -> },
    voiceLaunchToken: Int = 0,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val currentP = progressProvider()
    val alpha = (1f - currentP * 2.5f).coerceIn(0f, 1f)
    val scale = 1f - (0.5f * currentP)

    var listening by remember { mutableStateOf(false) }
    var partialText by remember { mutableStateOf("") }
    var statusHint by remember { mutableStateOf("聽緊…") }
    var pendingVoiceStart by remember { mutableStateOf(false) }
    // 錯誤提示氣泡（最多顯示 5 秒）
    var voiceBubbleVisible by remember { mutableStateOf(false) }
    // 錄音／其他 App 佔用咪高風時，重試一次
    var audioRetryUsed by remember { mutableStateOf(false) }
    // 卡在「準備中」時自動重連
    var prepRetryUsed by remember { mutableStateOf(false) }
    // 已收到 onReadyForSpeech
    var speechReady by remember { mutableStateOf(false) }
    // 每次 start 遞增，用嚟取消過期 watchdog
    var voiceSessionGen by remember { mutableIntStateOf(0) }
    var audioFocusRequest by remember {
        mutableStateOf<android.media.AudioFocusRequest?>(null)
    }

    val audioManager = remember {
        context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
    }

    val recognizer = remember {
        if (android.speech.SpeechRecognizer.isRecognitionAvailable(context)) {
            android.speech.SpeechRecognizer.createSpeechRecognizer(context)
        } else null
    }

    fun releaseMicFocus() {
        try {
            val req = audioFocusRequest
            if (req != null) {
                audioManager.abandonAudioFocusRequest(req)
            }
        } catch (_: Exception) {}
        audioFocusRequest = null
    }

    /** 搶 transient exclusive 音訊焦點，盡量令錄屏／其他收音暫時讓路 */
    fun requestMicFocus(): Boolean {
        return try {
            val attrs = android.media.AudioAttributes.Builder()
                .setUsage(android.media.AudioAttributes.USAGE_ASSISTANT)
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            val req = android.media.AudioFocusRequest.Builder(
                android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE
            )
                .setAudioAttributes(attrs)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener { }
                .build()
            val result = audioManager.requestAudioFocus(req)
            audioFocusRequest = req
            result == android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED ||
                result == android.media.AudioManager.AUDIOFOCUS_REQUEST_DELAYED
        } catch (_: Exception) {
            // API 降級：舊式 requestAudioFocus
            try {
                @Suppress("DEPRECATION")
                val result = audioManager.requestAudioFocus(
                    null,
                    android.media.AudioManager.STREAM_MUSIC,
                    android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE
                )
                result == android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            } catch (_: Exception) {
                false
            }
        }
    }

    fun stopListening() {
        voiceSessionGen += 1
        try { recognizer?.stopListening() } catch (_: Exception) {}
        try { recognizer?.cancel() } catch (_: Exception) {}
        listening = false
        speechReady = false
        releaseMicFocus()
    }

    fun handleFinalText(spoken: String) {
        listening = false
        releaseMicFocus()
        audioRetryUsed = false
        partialText = spoken
        val (note, amount) = parseVoiceLedgerInput(spoken)
        if (note.isBlank() && (amount == null || amount <= 0.0)) {
            statusHint = "聽唔明"
            return
        }
        statusHint = buildString {
            if (note.isNotBlank()) append(note)
            if (amount != null) {
                if (isNotEmpty()) append(" · ")
                append("$")
                append(
                    if (amount % 1.0 == 0.0) amount.toInt().toString()
                    else String.format(Locale.US, "%.1f", amount)
                )
            }
        }
        onVoiceResult(note, amount)
    }

    fun startInAppListening() {
        val r = recognizer
        if (r == null) {
            statusHint = "唔支援語音"
            listening = false
            return
        }
        try {
            // 先搶咪高風焦點（對抗系統錄屏開咗「錄製音訊」）
            requestMicFocus()

            val intent = Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, "zh-HK")
                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "zh-HK")
                putExtra(android.speech.RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(android.speech.RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(android.speech.RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                putExtra(android.speech.RecognizerIntent.EXTRA_PREFER_OFFLINE, false)
                // 加快結束／減少假死等待
                putExtra(
                    android.speech.RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                    1500
                )
                putExtra(
                    android.speech.RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                    1000
                )
                putExtra(
                    android.speech.RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
                    800
                )
            }
            partialText = ""
            speechReady = false
            statusHint = "準備中…"
            listening = true
            voiceSessionGen += 1
            r.startListening(intent)
        } catch (e: Exception) {
            listening = false
            speechReady = false
            releaseMicFocus()
            statusHint = "無法啟動"
        }
    }

    // 卡在「準備中」超過 2.5 秒 → 自動 cancel 再重連一次；再唔得就結束
    LaunchedEffect(listening, voiceSessionGen, statusHint) {
        if (!listening) return@LaunchedEffect
        if (statusHint != "準備中…" && statusHint != "重連中…") return@LaunchedEffect
        val gen = voiceSessionGen
        delay(2500)
        if (!listening || gen != voiceSessionGen) return@LaunchedEffect
        if (speechReady) return@LaunchedEffect
        if (statusHint != "準備中…" && statusHint != "重連中…") return@LaunchedEffect
        try { recognizer?.cancel() } catch (_: Exception) {}
        if (!prepRetryUsed) {
            prepRetryUsed = true
            statusHint = "重連中…"
            delay(350)
            if (!listening || gen != voiceSessionGen) return@LaunchedEffect
            startInAppListening()
        } else {
            listening = false
            speechReady = false
            releaseMicFocus()
            statusHint = "無法啟動"
            prepRetryUsed = false
        }
    }

    // 單次聆聽最長 12 秒，避免永遠卡住
    LaunchedEffect(listening, voiceSessionGen) {
        if (!listening) return@LaunchedEffect
        val gen = voiceSessionGen
        delay(12_000)
        if (listening && gen == voiceSessionGen) {
            try { recognizer?.stopListening() } catch (_: Exception) {}
            delay(600)
            if (listening && gen == voiceSessionGen) {
                stopListening()
                statusHint = "聽唔到，再試一次"
            }
        }
    }

    DisposableEffect(recognizer) {
        val r = recognizer
        if (r == null) {
            onDispose { }
        } else {
            val listener = object : android.speech.RecognitionListener {
                override fun onReadyForSpeech(params: android.os.Bundle?) {
                    speechReady = true
                    prepRetryUsed = false
                    statusHint = "請開始講…"
                    partialText = ""
                    audioRetryUsed = false
                }
                override fun onBeginningOfSpeech() {
                    speechReady = true
                    statusHint = "聽緊…"
                }
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    statusHint = "識別中…"
                }
                override fun onError(error: Int) {
                    // 咪被佔用／忙線：自動搶焦點再試一次
                    val micBusy = error == android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY ||
                        error == android.speech.SpeechRecognizer.ERROR_AUDIO ||
                        error == android.speech.SpeechRecognizer.ERROR_CLIENT ||
                        error == android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS
                    if (micBusy && !audioRetryUsed) {
                        audioRetryUsed = true
                        statusHint = "咪忙碌，重試中…"
                        try { r.cancel() } catch (_: Exception) {}
                        releaseMicFocus()
                        // 喺主線程稍後重試
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            requestMicFocus()
                            startInAppListening()
                        }, 350)
                        return
                    }

                    listening = false
                    releaseMicFocus()
                    val msg = when (error) {
                        android.speech.SpeechRecognizer.ERROR_NO_MATCH -> "聽唔到，再試一次"
                        android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "冇聽到聲"
                        android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "冇麥克風權限"
                        android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY,
                        android.speech.SpeechRecognizer.ERROR_AUDIO ->
                            "咪高風被佔用（錄屏可試關「錄製音訊」）"
                        android.speech.SpeechRecognizer.ERROR_NETWORK,
                        android.speech.SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "網絡問題"
                        else -> "語音識別失敗"
                    }
                    statusHint = msg
                    audioRetryUsed = false
                }
                override fun onResults(results: android.os.Bundle?) {
                    val spoken = results
                        ?.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        ?.trim()
                    if (!spoken.isNullOrBlank()) handleFinalText(spoken)
                    else {
                        listening = false
                        releaseMicFocus()
                        statusHint = "未識別到"
                    }
                }
                override fun onPartialResults(partialResults: android.os.Bundle?) {
                    val p = partialResults
                        ?.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        ?.trim()
                    if (!p.isNullOrBlank()) {
                        partialText = p
                        statusHint = p
                    }
                }
                override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
            }
            r.setRecognitionListener(listener)
            onDispose {
                try { r.cancel() } catch (_: Exception) {}
                try { r.destroy() } catch (_: Exception) {}
                releaseMicFocus()
            }
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startInAppListening()
        else statusHint = "需要麥克風權限"
    }

    fun requestVoiceInput() {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        if (listening) {
            stopListening()
            return
        }
        val granted = ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) startInAppListening()
        else micPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
    }

    // 🟢 桌面 Shortcut / DeepLink 觸發：進 App 後自動開始語音
    LaunchedEffect(voiceLaunchToken) {
        if (voiceLaunchToken <= 0) return@LaunchedEffect
        // 等 UI 同權限就緒
        delay(450)
        if (!listening) requestVoiceInput()
    }

    fun isVoiceErrorHint(s: String): Boolean {
        if (s.isBlank()) return false
        return s == "聽唔明" ||
            s.startsWith("聽唔到") ||
            s == "冇聽到聲" ||
            s == "未識別到" ||
            s == "唔支援語音" ||
            s == "無法啟動" ||
            s.contains("麥克風") ||
            s.contains("咪高風") ||
            s.contains("咪忙碌") ||
            s.contains("網絡") ||
            s.contains("語音識別失敗")
    }

    // 聽緊時常駐；錯誤提示最多 5 秒後淡出
    LaunchedEffect(listening, statusHint) {
        if (listening) {
            voiceBubbleVisible = true
            return@LaunchedEffect
        }
        if (isVoiceErrorHint(statusHint)) {
            voiceBubbleVisible = true
            delay(5000)
            voiceBubbleVisible = false
        } else {
            // 成功／閒置：短延遲後收埋
            delay(200)
            voiceBubbleVisible = false
        }
    }

    if (alpha <= 0.01f && !listening && !voiceBubbleVisible) return

    Row(
        modifier = modifier
            .padding(bottom = bottomPadding, end = 20.dp)
            .graphicsLayer {
                this.alpha = alpha
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin(1f, 1f)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        // 語音中／錯誤提示：FAB 左邊提示條（錯誤最多 5 秒，帶進出動畫）
        androidx.compose.animation.AnimatedVisibility(
            visible = listening || voiceBubbleVisible,
            enter = fadeIn(tween(220)) + slideInHorizontally(tween(260)) { it / 3 },
            exit = fadeOut(tween(280)) + slideOutHorizontally(tween(280)) { it / 3 }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xE6111827),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .widthIn(min = 120.dp, max = 220.dp)
                        .heightIn(min = 40.dp)
                ) {
                    Row(
                        Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 聲波動畫點
                        if (listening) {
                            val infinite = rememberInfiniteTransition(label = "voiceDots")
                            val d1 by infinite.animateFloat(0.3f, 1f, infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "d1")
                            val d2 by infinite.animateFloat(0.3f, 1f, infiniteRepeatable(tween(500, delayMillis = 120), RepeatMode.Reverse), label = "d2")
                            val d3 by infinite.animateFloat(0.3f, 1f, infiniteRepeatable(tween(500, delayMillis = 240), RepeatMode.Reverse), label = "d3")
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                listOf(d1, d2, d3).forEach { a ->
                                    Box(
                                        Modifier
                                            .size(5.dp)
                                            .graphicsLayer { this.alpha = a }
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(
                            text = when {
                                partialText.isNotBlank() -> partialText
                                else -> statusHint
                            },
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(if (listening) Color(0xFFDC2626) else BRAND_PRIMARY)
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (listening) stopListening()
                        else onFabTap()
                    },
                    onLongClick = { requestVoiceInput() }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (listening) Icons.Default.Mic else Icons.Default.Add,
                contentDescription = if (listening) "語音輸入中（再撳停止）" else "新增（長按語音）",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer { rotationZ = if (listening) 0f else currentP * 90f }
            )
        }
    }
}

@Composable
private fun AnimatedKeyboardLayer(
    showKeyboard: Boolean,
    progressProvider: () -> Float,
    keyboardState: KeyboardState,
    onKeyboardStateChange: (KeyboardState) -> Unit,
    onKeyboardDismiss: () -> Unit,
    onKeyboardConfirm: () -> Unit,
    onKeyboardNext: () -> Unit,
    allNoteNames: List<String>,
    noteCategoryMap: Map<String, String>,
    noteIconMap: Map<String, String>,
    nameFlashTrigger: Int,
    imeOpen: Boolean = false,
    modifier: Modifier = Modifier
) {
    val currentP = progressProvider()
    val isActive = currentP > 0f || showKeyboard

    if (!isActive) return

    // ★ 已完全移除 maxPanelHeight 嘅強制高度限制
    // 咁樣當系統鍵盤推上嚟時，自訂鍵盤會保持完整高度並自然遮蓋上方嘅 Top Stat，
    // 跌落嚟嘅時候亦會完整平滑過渡，唔會再出現被裁切嘅情況。

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                bottom = if (imeOpen) 8.dp else 20.dp,
                end = 16.dp,
                start = 16.dp
            )
            .graphicsLayer {
                alpha = currentP
                val w = size.width
                val h = size.height
                if (w > 0f && h > 0f) {
                    val ox = (1f - (32.dp.toPx() / w)).coerceIn(0.02f, 0.98f)
                    val oy = (1f - (100.dp.toPx() / h)).coerceIn(0.02f, 0.98f)
                    transformOrigin = TransformOrigin(ox, oy)
                } else {
                    transformOrigin = TransformOrigin(1f, 1f)
                }
                val s = 0.13f + 0.87f * currentP
                scaleX = s
                scaleY = s
            }
    ) {
        LedgerKeyboardPanel(
            state = keyboardState,
            onStateChange = onKeyboardStateChange,
            onDismiss = onKeyboardDismiss,
            onConfirm = onKeyboardConfirm,
            onNext = onKeyboardNext,
            allNoteNames = allNoteNames,
            noteCategoryMap = noteCategoryMap,
            noteIconMap = noteIconMap,
            nameFlashTrigger = nameFlashTrigger,
            showKeyboard = showKeyboard,
            imeOpen = imeOpen,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// =========================================================
//  ★ FloatingNavBar（★ 支援 4 Tab 自適應寬度）
// =========================================================
@Composable
fun FloatingNavBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // ★ 4 個 Tab 用 80dp，3 個或以下維持 96dp，令膠囊永遠貼合內容
    val tabWidthDp: Dp = if (items.size >= 4) 80.dp else NAV_TAB_WIDTH
    val density = LocalDensity.current
    val tabWidthPx = with(density) { tabWidthDp.toPx() }
    val maxOffset = tabWidthPx * items.size - tabWidthPx
    val vc = LocalViewConfiguration.current
    val latestSel by rememberUpdatedState(selectedIndex)
    val latestOnChange by rememberUpdatedState(onIndexChange)
    val latestSize by rememberUpdatedState(items.size)
    var bubbleOffset by remember { mutableFloatStateOf(selectedIndex * tabWidthPx) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(selectedIndex) { if (!isDragging) bubbleOffset = selectedIndex * tabWidthPx }
    val animatedOffset by animateFloatAsState(
        bubbleOffset,
        if (isDragging) snap() else spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioNoBouncy
        ),
        label = "bubble"
    )

    Box(
        modifier = modifier
            .width(tabWidthDp * items.size)
            .height(NAV_HEIGHT)
            .clip(RoundedCornerShape(NAV_HEIGHT / 2))
            .background(SURFACE_CARD)
            .border(0.5.dp, DIVIDER_COLOR, RoundedCornerShape(NAV_HEIGHT / 2))
    ) {
        Box(Modifier.fillMaxSize().pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val downX = down.position.x
                    val pid = down.id
                    var dx = 0f; var dragged = false
                    while (true) {
                        val e = awaitPointerEvent()
                        val c = e.changes.firstOrNull { it.id == pid }
                        if (c == null || !c.pressed) {
                            if (!dragged) {
                                val i = (downX / tabWidthPx).toInt().coerceIn(0, latestSize - 1)
                                if (i != latestSel) latestOnChange(i)
                                bubbleOffset = i * tabWidthPx
                            } else {
                                val i = (bubbleOffset / tabWidthPx).roundToInt().coerceIn(0, latestSize - 1)
                                bubbleOffset = i * tabWidthPx
                                if (i != latestSel) latestOnChange(i)
                            }
                            isDragging = false; break
                        }
                        val d = c.positionChange().x; dx += d
                        if (!dragged && abs(dx) > vc.touchSlop) { dragged = true; isDragging = true }
                        if (dragged) {
                            bubbleOffset = (bubbleOffset + d).coerceIn(0f, maxOffset)
                            c.consume()
                            val i = (bubbleOffset / tabWidthPx).roundToInt().coerceIn(0, latestSize - 1)
                            if (i != latestSel) latestOnChange(i)
                        }
                    }
                }
            }
        }) {
            Box(
                Modifier
                    .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                    .width(tabWidthDp).fillMaxHeight().padding(6.dp)
                    .clip(RoundedCornerShape((NAV_HEIGHT - 12.dp) / 2))
                    .background(BRAND_PRIMARY_LIGHT.copy(alpha = 0.9f))
            )
            Row(Modifier.fillMaxSize()) {
                items.forEachIndexed { idx, item ->
                    val sel = idx == selectedIndex
                    val tint = if (sel) BRAND_PRIMARY_DARK else Color(0xFF636366)
                    Box(Modifier.width(tabWidthDp).fillMaxHeight(),
                        contentAlignment = Alignment.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // ★ 4 Tab 時 icon / gap / 字號 略收細，確保 80dp 內唔會逼爆
                            val iconSize = if (items.size >= 4) 18.dp else 20.dp
                            val gap = if (items.size >= 4) 4.dp else 6.dp
                            val fontSize = if (items.size >= 4) 13.sp else 14.sp
                            Icon(item.icon, item.label, tint = tint,
                                modifier = Modifier.size(iconSize))
                            Spacer(Modifier.width(gap))
                            Text(item.label, fontSize = fontSize,
                                fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Medium,
                                color = tint)
                        }
                    }
                }
            }
        }
    }
}

// =========================================================
//  ★ NotificationAccessGuide
// =========================================================
@Composable
fun NotificationAccessGuide(context: Context) {
    var isGranted by remember { mutableStateOf<Boolean?>(null) }
    var showPantanalHint by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isGranted = withContext(Dispatchers.IO) {
            NotificationManagerCompat
                .getEnabledListenerPackages(context)
                .contains(context.packageName)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    isGranted = withContext(Dispatchers.IO) {
                        NotificationManagerCompat
                            .getEnabledListenerPackages(context)
                            .contains(context.packageName)
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(showPantanalHint) {
        if (showPantanalHint) {
            Toast.makeText(context, "提示：如需使用流体云體驗，請確保在系統設定開啟「泛在服務」", Toast.LENGTH_LONG).show()
            showPantanalHint = false
        }
    }

    if (isGranted == false) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    try {
                        context.startActivity(
                            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        )
                        if (OppoFluidCloudBridge.isPantanalSupported(context) &&
                            !OppoFluidCloudBridge.hasPantanalPermission(context)) {
                            showPantanalHint = true
                        }
                    } catch (_: Exception) { }
                },
            shape = RoundedCornerShape(12.dp),
            color = SURFACE_CARD,
            border = BorderStroke(0.5.dp, DIVIDER_COLOR)
        ) {
            Row(
                Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = "開啟通知", tint = BRAND_PRIMARY)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("開啟自動記賬功能",
                        fontWeight = FontWeight.Bold, fontSize = 14.sp,
                        color = BRAND_PRIMARY_DARK)
                    Text("點擊前往授權讀取通知，自動捕捉易乘碼消費",
                        fontSize = 12.sp, color = TEXT_SECONDARY)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TEXT_TERTIARY)
            }
        }
    }
}
fun formatTimeMin(min: Int): String {
    val h = min / 60
    val m = min % 60
    return "${pad2(h)}:${pad2(m)}"
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun LedgerContent(
    isFirstShow: Boolean = false,
    loading: Boolean,
    filtered: List<Record>,
    groupedByDate: List<Pair<String, List<Record>>>,
    topNotes: List<Pair<String, Int>>,
    noteIconMap: Map<String, String>,
    noteCategoryMap: Map<String, String>,
    noteUsageCount: Map<String, Int> = emptyMap(),
    hasIncome: Boolean,
    hasExpense: Boolean,
    totalIncome: Double,
    totalExpense: Double,
    filterMode: Boolean,
    filterCategory: String?,
    onFilterCategoryChange: (String?) -> Unit,
    filterMonth: String?,
    onFilterMonthChange: (String?) -> Unit,
    filterStartTimeMin: Int,
    filterEndTimeMin: Int,
    onFilterTimeRangeChange: (Int, Int) -> Unit,
    availableMonths: List<String>,
    visibleCategories: List<String>,
    expandedId: String?,
    onExpandChange: (String?) -> Unit,
    isExactNoteFilter: Boolean,
    onQuickInputClick: (String) -> Unit,
    onCopyClick: (Record) -> Unit,
    onEditClick: (Record) -> Unit,
    onDeleteClick: (Record) -> Unit,
    onChangeIconClick: (Record) -> Unit,
    onFilterByName: (String) -> Unit,
    showKeyboard: Boolean,
    showFuture: Boolean,
    onShowFutureChange: (Boolean) -> Unit,
    scrollToTopTrigger: Int,
    justAddedIds: Set<String>,
    deletingIds: Set<String>,
    afterSaveHint: AfterSaveHint?,
    afterSaveHintVisible: Boolean,
    keyboardProgressProvider: () -> Float,
    weeklyExpenses: List<WeeklyExpenseDay> = emptyList(),
    shouldPlayWeeklyIntro: Boolean = false,
    onWeeklyIntroFinished: () -> Unit = {},
    hiddenQuickNotes: Set<String> = emptySet(),
    onHideNote: (String) -> Unit = {},
    onUnhideNote: (String) -> Unit = {},
    quickInputScrollToFirstTrigger: Int = 0,
    /** 由其他頁 FAB 返嚟：首幀由隱藏狀態開始滑入 */
    headerEnterFromHidden: Boolean = false,
    onHeaderEnterConsumed: () -> Unit = {},
) {
    val listState = rememberLazyListState()

    var animateTopStats by remember { mutableStateOf(false) }
    LaunchedEffect(loading) {
        if (!loading) {
            delay(800)
            animateTopStats = true
        }
    }

    val density = LocalDensity.current
    val maxCollapsePx = remember(density) { with(density) { 120.dp.toPx() } }

    // 由其他頁撳 FAB 返記帳頁：頂部高度 0→滿（下面內容貼頂，再被一齊推落）
    // layout 先完整量度子項，再只報告 progress 比例高度（唔會被父高度夾壞）
    val headerExpandProgress = remember {
        Animatable(if (headerEnterFromHidden) 0f else 1f)
    }
    LaunchedEffect(headerEnterFromHidden) {
        if (!headerEnterFromHidden) return@LaunchedEffect
        if (headerExpandProgress.value > 0.01f) {
            headerExpandProgress.snapTo(0f)
        }
        delay(HEADER_KEYBOARD_EXPAND_DELAY_MS)
        headerExpandProgress.animateTo(
            1f,
            tween(HEADER_KEYBOARD_EXPAND_MS, easing = FastOutSlowInEasing)
        )
        onHeaderEnterConsumed()
    }
    val headerP = headerExpandProgress.value
    var quickInputCollapseOffset by remember { mutableFloatStateOf(0f) }
    var isAutoAnimating by remember { mutableStateOf(false) }

    // ★ 退出保護狀態
    var isExitingFilter by remember { mutableStateOf(false) }

    LaunchedEffect(filterMode) {
        try {
            if (!filterMode) {
                isExitingFilter = true
                quickInputCollapseOffset = 0f
                listState.animateScrollToItem(0)
                isExitingFilter = false
            } else {
                quickInputCollapseOffset = 0f
                if (listState.firstVisibleItemIndex > 20) {
                    listState.animateScrollToItem(0)
                }
            }
        } catch (_: Exception) {
            isExitingFilter = false
        }
    }

    val singleCategory by remember(filterMode, filtered) {
        derivedStateOf {
            if (!filterMode || filtered.isEmpty()) null
            else {
                val firstCat = filtered[0].category
                if (filtered.all { it.category == firstCat }) firstCat else null
            }
        }
    }
    
    var cachedCategory by remember { mutableStateOf("飲食") }
    LaunchedEffect(singleCategory) { if (singleCategory != null) cachedCategory = singleCategory!! }
    val showCategoryChip = filterMode && singleCategory != null

    // ★ 非篩選模式：永遠用本月；篩選模式：跟 chip 月份（null = 全年）
    val currentMonthStr = remember { monthKeyFromTimestamp(System.currentTimeMillis()) }
    val statRecords = remember(filtered, filterMonth, filterMode, currentMonthStr) {
        when {
            !filterMode -> filtered.filter { monthKeyFromTimestamp(it.timestamp) == currentMonthStr }
            filterMonth == null -> filtered
            else -> filtered.filter { monthKeyFromTimestamp(it.timestamp) == filterMonth }
        }
    }
    
    val avgExpense = remember(statRecords, filterMode) {
        if (!filterMode) 0.0
        else {
            val exps = statRecords.filter { it.category != INCOME_CATEGORY && it.category != "還錢" }
            if (exps.isNotEmpty()) exps.sumOf { it.amount } / exps.size else 0.0
        }
    }
    // 篩選模式：平均消費時間（分上午／下午）
    val (avgMorningLabel, avgAfternoonLabel) = remember(statRecords, filterMode) {
        if (!filterMode) Pair<String?, String?>(null, null)
        else {
            val exps = statRecords.filter { it.category != INCOME_CATEGORY && it.category != "還錢" }
            var amSum = 0; var amCnt = 0
            var pmSum = 0; var pmCnt = 0
            for (r in exps) {
                val minOfDay = getTimestampMeta(r.timestamp).secOfDay / 60
                if (minOfDay < 12 * 60) {
                    amSum += minOfDay; amCnt++
                } else {
                    pmSum += minOfDay; pmCnt++
                }
            }
            val am = if (amCnt > 0) formatTimeMin(amSum / amCnt) else null
            val pm = if (pmCnt > 0) formatTimeMin(pmSum / pmCnt) else null
            Pair(am, pm)
        }
    }
    val displayCount = if (filterMode) statRecords.size else null

    // 追蹤係咪離開咗頂部
    val isScrolledAway by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 20
        }
    }
    
    // 只有真正滾動離開頂部，並且「唔係退出緊篩選模式」，先至會縮細
    val effectiveScrolledAway = isScrolledAway && !isExitingFilter
    
    var isFilterDeckExpanded by remember { mutableStateOf(true) }
    var manualExpandTime by remember { mutableLongStateOf(0L) }

    LaunchedEffect(effectiveScrolledAway) {
        if (!effectiveScrolledAway) isFilterDeckExpanded = true
    }

    // 正常模式嘅 QuickInput 滾動壓縮邏輯 + ★ 只攔截手動 Drag 嚟收起啤牌
    val topScrollConnection = remember(filterMode, topNotes, isAutoAnimating, maxCollapsePx, isFilterDeckExpanded) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val isManualDrag = source == androidx.compose.ui.input.nestedscroll.NestedScrollSource.Drag || source.toString() == "UserInput"
                val now = System.currentTimeMillis()
                
                // ★ 修正：拎走咗 effectiveScrolledAway 限制！
                // 依家只要係手動向下拉 (available.y < -2f)，就算係由最頂開始拉，都會即刻觸發啤牌摺疊！
                if (isManualDrag && available.y < -2f && isFilterDeckExpanded && (now - manualExpandTime > 500L)) {
                    isFilterDeckExpanded = false
                }
                
                if (filterMode || topNotes.isEmpty() || isAutoAnimating) return Offset.Zero
                val delta = available.y
                val next = (quickInputCollapseOffset - delta).coerceIn(0f, maxCollapsePx)
                val consumed = quickInputCollapseOffset - next
                quickInputCollapseOffset = next
                return Offset(0f, consumed)
            }
        }
    }

    val isListScrolling by remember { derivedStateOf { listState.isScrollInProgress } }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isListScrolling) {
        if (!isListScrolling && !isAutoAnimating && quickInputCollapseOffset > 0f && quickInputCollapseOffset < maxCollapsePx) {
            try {
                isAutoAnimating = true
                val target = if (quickInputCollapseOffset > maxCollapsePx * 0.5f) maxCollapsePx else 0f
                val a = Animatable(quickInputCollapseOffset)
                a.animateTo(target, spring(dampingRatio = 0.85f, stiffness = 400f)) {
                    quickInputCollapseOffset = this.value
                }
            } finally {
                isAutoAnimating = false
            }
        }
    }

    val collapseProgress = if (maxCollapsePx > 0f) (quickInputCollapseOffset / maxCollapsePx).coerceIn(0f, 1f) else 0f

    val weeklyIntroProgress = remember { Animatable(1f) }
    var weeklyIntroTriggered by remember { mutableStateOf(false) }
    val filterChartCollapse by animateFloatAsState(targetValue = if (filterMode) 1f else 0f, animationSpec = spring(0.85f, 300f))

    LaunchedEffect(shouldPlayWeeklyIntro) {
        if (!shouldPlayWeeklyIntro || weeklyIntroTriggered) return@LaunchedEffect
        weeklyIntroTriggered = true
        if (!listState.isScrollInProgress) {
            weeklyIntroProgress.snapTo(0f)
            weeklyIntroProgress.animateTo(1f, tween(350, easing = FastOutSlowInEasing))
        } else {
            weeklyIntroProgress.snapTo(1f)
        }
        onWeeklyIntroFinished()
    }

    var activeFanRecord by remember { mutableStateOf<Record?>(null) }
    val fanMenuCenterState = remember { mutableStateOf(Offset.Zero) }
    val fanMenuTouchState = remember { mutableStateOf(Offset.Zero) }
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val fanRadiusPx = remember(density) { with(density) { 105.dp.toPx() } }
    val fanHalfBtnPx = remember(density) { with(density) { 24.dp.toPx() } }
    val fanEdgeThreshold = fanRadiusPx + fanHalfBtnPx
    val fanMenuProgress = animateFloatAsState(if (activeFanRecord != null) 1f else 0f, spring(0.65f, Spring.StiffnessMediumLow))
    val minFanRadiusPx = remember(density) { with(density) { 16.dp.toPx() } }

    val handleFanStart = remember { { r: Record, offset: Offset -> activeFanRecord = r; fanMenuCenterState.value = offset; fanMenuTouchState.value = offset } }
    val handleFanDrag = remember { { dragAmount: Offset -> fanMenuTouchState.value += dragAmount } }
    val latestOnCopy by rememberUpdatedState(onCopyClick)
    val latestOnEdit by rememberUpdatedState(onEditClick)
    val latestOnFilterByName by rememberUpdatedState(onFilterByName)
    val latestOnDelete by rememberUpdatedState(onDeleteClick)

    val handleFanEnd = remember {
        {
            val center = fanMenuCenterState.value
            val leftSpace = center.x
            val rightSpace = screenWidthPx - center.x
            val rotation = when {
                leftSpace < fanEdgeThreshold -> (1f - leftSpace / fanEdgeThreshold).coerceIn(0f, 1f) * 80f
                rightSpace < fanEdgeThreshold -> -(1f - rightSpace / fanEdgeThreshold).coerceIn(0f, 1f) * 80f
                else -> 0f
            }
            val arcCenterAngle = -90f + rotation
            val hovered = getHoveredActionIndexWithCenter(center, fanMenuTouchState.value, minFanRadiusPx, arcCenterAngle, fanRadiusPx, with(density) { 40.dp.toPx() })
            val targetRecord = activeFanRecord
            activeFanRecord = null
            if (targetRecord != null && hovered != null) {
                scope.launch {
                    delay(120)
                    when (hovered) {
                        0 -> latestOnCopy(targetRecord)
                        1 -> latestOnEdit(targetRecord)
                        2 -> latestOnFilterByName(targetRecord.note)
                        3 -> latestOnDelete(targetRecord)
                    }
                }
            }
        }
    }

    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger > 0) {
            try {
                if (listState.firstVisibleItemIndex > 5) listState.scrollToItem(3)
                listState.animateScrollToItem(0)
            } catch (_: Exception) {}
        }
    }

    LaunchedEffect(showKeyboard) {
        if (showKeyboard) {
            if (quickInputCollapseOffset > 0f) {
                launch {
                    try {
                        isAutoAnimating = true
                        Animatable(quickInputCollapseOffset).animateTo(0f, spring(0.85f, 400f)) {
                            quickInputCollapseOffset = this.value
                        }
                    } finally {
                        isAutoAnimating = false
                    }
                }
            }
            try {
                if (listState.firstVisibleItemIndex > 5) listState.scrollToItem(3)
                listState.animateScrollToItem(0)
            } catch (_: Exception) {}
        }
    }

    LaunchedEffect(showFuture) {
        try {
            if (showFuture) {
                if (listState.firstVisibleItemIndex > 5) listState.scrollToItem(3)
                listState.animateScrollToItem(0)
            } else {
                if (listState.firstVisibleItemIndex <= 3) listState.animateScrollToItem(0)
            }
        } catch (_: Exception) {}
    }

    val displayGroups = groupedByDate
    val isFanActive = activeFanRecord != null
    val activeFanId = activeFanRecord?.id

    data class DayStat(val income: Double, val expense: Double, val isGroupDeleting: Boolean)
    val dayStats: Map<String, DayStat> = remember(displayGroups, deletingIds) {
        displayGroups.associate { (dateKey, records) ->
            var inc = 0.0; var exp = 0.0
            for (r in records) { if (r.id !in deletingIds) { if (r.category == INCOME_CATEGORY) { inc += r.amount } else if (r.category != "還錢") { exp += r.amount } } }
            dateKey to DayStat(inc, exp, records.size == 1 && records[0].id in deletingIds)
        }
    }

    val listContent: LazyListScope.() -> Unit = {
        displayGroups.forEachIndexed { index, (dateKey, dayRecords) ->
            val stat = dayStats[dateKey] ?: DayStat(0.0, 0.0, false)
            if (!stat.isGroupDeleting) {
                stickyHeader(key = "header_$dateKey", contentType = "day_header") {
                    Box(modifier = Modifier.fillMaxWidth().height(64.dp).background(SURFACE_BG)) {
                        DayHeader(dateKey = dateKey, income = stat.income, expense = stat.expense, listState = listState)
                    }
                }
                item(key = "group_$dateKey", contentType = "day_group") {
                    Box(modifier = Modifier.animateItem(fadeInSpec = tween(250), fadeOutSpec = tween(200), placementSpec = spring(0.9f, 350f))) {
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clip(RoundedCornerShape(12.dp)).background(SURFACE_CARD)) {
                            val activeCount = if (deletingIds.isEmpty()) dayRecords.size else dayRecords.count { it.id !in deletingIds }
                            var renderedVisibleIdx = 0
                            for (r in dayRecords) {
                                val isRecDeleting = r.id in deletingIds
                                if (!isRecDeleting) renderedVisibleIdx++
                                val showDividerBelow = !isRecDeleting && renderedVisibleIdx < activeCount
                                key(r.id) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        AnimatedRecordItem(animateOnMount = r.id in justAddedIds, isDeleting = isRecDeleting) {
                                            SwipeableRecordItem(backgroundColor = SURFACE_CARD, record = r, isExpanded = expandedId == r.id, hasAnyExpanded = expandedId != null, onExpand = onExpandChange, onCopy = { onCopyClick(r) }, onEdit = { onEditClick(r) }, onFilter = { onFilterByName(r.note) }, onDelete = { onDeleteClick(r) }, onChangeIcon = { onChangeIconClick(r) }, hideCategory = isExactNoteFilter, onFanMenuStart = { _, offset -> handleFanStart(r, offset) }, onFanMenuDrag = handleFanDrag, onFanMenuEnd = handleFanEnd, isFanMenuActive = isFanActive, isOtherItem = isFanActive && activeFanId != r.id, isActiveItem = activeFanId == r.id)
                                        }
                                        if (afterSaveHint?.recordId == r.id) CategoryTotalHint(hint = afterSaveHint, visible = afterSaveHintVisible)
                                        if (showDividerBelow) HorizontalDivider(modifier = Modifier.padding(start = 68.dp, end = 16.dp), color = DIVIDER_COLOR, thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                }
                item(key = "spacer_$dateKey", contentType = "day_spacer") { Spacer(modifier = Modifier.height(10.dp).animateItem(fadeInSpec = tween(250), fadeOutSpec = tween(200), placementSpec = spring(0.9f, 350f))) }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().background(SURFACE_BG)) {
            NotificationAccessGuide(context = LocalContext.current)

            val context = LocalContext.current
            var isNotificationGranted by remember { mutableStateOf(false) }
            var overlayCaptureEnabled by remember { mutableStateOf(isOverlayCapturePrefEnabled(context)) }

            LaunchedEffect(Unit) {
                isNotificationGranted = kotlinx.coroutines.withContext(Dispatchers.IO) {
                    NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
                }
            }

            var futureBtnRevealed by rememberSaveable { mutableStateOf(false) }
            val futureBtnProgress = remember { Animatable(if (futureBtnRevealed) 1f else 0f) }
            val barScope = rememberCoroutineScope()
            var dragStartProgress by remember { mutableFloatStateOf(0f) }
            var totalDragPx by remember { mutableFloatStateOf(0f) }
            val revealDistancePx = with(density) { 56.dp.toPx() }

            LaunchedEffect(futureBtnRevealed) {
                futureBtnProgress.animateTo(if (futureBtnRevealed) 1f else 0f, spring(0.82f, 380f))
            }

            // 頂部背景色動畫：使用 SURFACE_BG 避免陰影
            val topBarBgColor by animateColorAsState(
                targetValue = if (effectiveScrolledAway) SURFACE_BG else SURFACE_CARD,
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            )

            // 頂部區塊：layout 完整量度後按 headerP 展開高度；內容由上滑入，下面被推落
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = 0.35f + 0.65f * headerP.coerceIn(0f, 1f) }
                    .layout { measurable, constraints ->
                        // 用無限高量度真實全高，避免被「動畫中嘅矮高度」反餵
                        val loose = constraints.copy(
                            minHeight = 0,
                            maxHeight = Constraints.Infinity
                        )
                        val placeable = measurable.measure(loose)
                        val fullH = placeable.height
                        val animatedH = (fullH * headerP.coerceIn(0f, 1f))
                            .roundToInt()
                            .coerceIn(0, fullH)
                        layout(
                            width = placeable.width.coerceIn(constraints.minWidth, constraints.maxWidth),
                            height = animatedH
                        ) {
                            // 對齊底部 → 由上方滑入，同時把下面列表推落
                            placeable.placeRelative(0, animatedH - fullH)
                        }
                    }
                    .clipToBounds()
            ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 0.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(topBarBgColor)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = { dragStartProgress = futureBtnProgress.value; totalDragPx = 0f },
                            onDragEnd = { futureBtnRevealed = futureBtnProgress.value >= 0.45f },
                            onDragCancel = { futureBtnRevealed = futureBtnProgress.value >= 0.45f },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                totalDragPx += dragAmount
                                val newP = (dragStartProgress - totalDragPx / revealDistancePx).coerceIn(0f, 1f)
                                barScope.launch { futureBtnProgress.snapTo(newP) }
                            }
                        )
                    }
            ) {
                val p = futureBtnProgress.value
                val gap = 6.dp
                val totalW = maxWidth - 16.dp
                val btnCount = if (isNotificationGranted) 2 else 1
                val slotCount = 3f + btnCount * p
                val slotW = if (slotCount > 0f) (totalW - gap * (slotCount - 1f)) / slotCount else totalW
                val statsWidth = slotW * 3f + gap * 2f
                val singleBtnWidth = slotW * p

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = if (effectiveScrolledAway) 0.dp else 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.width(statsWidth)) {
                        // ★ 完美版 TopStats (原生變形、0值自動消失)
                        TopStats(
                            hasIncome = hasIncome, hasExpense = hasExpense,
                            income = totalIncome, expense = totalExpense,
                            isFilterMode = filterMode,
                            filteredCount = displayCount,
                            showCategory = showCategoryChip, categoryName = cachedCategory,
                            avgExpense = avgExpense,
                            avgMorningLabel = avgMorningLabel,
                            avgAfternoonLabel = avgAfternoonLabel,
                            animateValues = animateTopStats,
                            isCompact = effectiveScrolledAway
                        )
                    }

                    if (p > 0.001f) {
                        Spacer(Modifier.width(gap * p.coerceIn(0f, 1f)))
                        Box(Modifier.width(singleBtnWidth.coerceAtLeast(0.dp)).graphicsLayer { alpha = p }, contentAlignment = Alignment.Center) {
                            IconButton(onClick = { onShowFutureChange(!showFuture) }, modifier = Modifier.size(40.dp)) {
                                Icon(if (showFuture) Icons.Default.Visibility else Icons.Default.VisibilityOff, "顯示未來項目", tint = if (showFuture) BRAND_PRIMARY else TEXT_TERTIARY)
                            }
                        }

                        if (isNotificationGranted) {
                            Spacer(Modifier.width(gap * p.coerceIn(0f, 1f)))
                            Box(Modifier.width(singleBtnWidth.coerceAtLeast(0.dp)).graphicsLayer { alpha = p }, contentAlignment = Alignment.Center) {
                                IconButton(
                                    onClick = {
                                        if (!canDrawCaptureOverlay(context)) {
                                            try { context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))) } 
                                            catch (_: Exception) { try { context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)) } catch (_: Exception) {} }
                                        } else {
                                            overlayCaptureEnabled = !overlayCaptureEnabled
                                            setOverlayCapturePrefEnabled(context, overlayCaptureEnabled)
                                            if (!overlayCaptureEnabled) CaptureOverlayController.dismiss()
                                        }
                                    },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.Layers, "消費浮窗提示", tint = when { !canDrawCaptureOverlay(context) -> Color(0xFFF59E0B); overlayCaptureEnabled -> BRAND_PRIMARY; else -> TEXT_TERTIARY })
                                }
                            }
                        }
                    }
                }
            }

            val sectionGap = 8.dp
            Spacer(Modifier.height(sectionGap))

            // [2] 內容區塊（快速輸入／篩選區）— 同 TopStats 一齊隨高度展開
            AnimatedContent(
                targetState = filterMode,
                modifier = Modifier.fillMaxWidth(),
                transitionSpec = {
                    ((fadeIn(tween(220, delayMillis = 90)) + expandVertically(spring(0.85f, 300f), expandFrom = Alignment.Top)) togetherWith 
                    (fadeOut(tween(150)) + shrinkVertically(spring(0.85f, 300f), shrinkTowards = Alignment.Top))).using(SizeTransform(clip = true) { _, _ -> spring(0.85f, 300f) })
                },
                label = "filterContent"
            ) { isFilter ->
                if (!isFilter) {
                    if (topNotes.isNotEmpty() || hiddenQuickNotes.isNotEmpty()) {
                        QuickInputSection(
                            topNotes = topNotes,
                            noteIconMap = noteIconMap,
                            noteCategoryMap = noteCategoryMap,
                            noteUsageCount = noteUsageCount,
                            maxRowsPerPage = 4,
                            skipAppearAnimation = !isFirstShow,
                            collapseProgress = collapseProgress,
                            onExpandClick = {
                                scope.launch {
                                    try {
                                        isAutoAnimating = true
                                        Animatable(quickInputCollapseOffset).animateTo(0f, spring(0.85f, 400f)) {
                                            quickInputCollapseOffset = this.value
                                        }
                                    } finally {
                                        isAutoAnimating = false
                                    }
                                }
                            },
                            onClick = onQuickInputClick,
                            hiddenQuickNotes = hiddenQuickNotes,
                            onHideNote = onHideNote,
                            onUnhideNote = onUnhideNote,
                            scrollToFirstTrigger = quickInputScrollToFirstTrigger
                        )
                    }
                } else {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 0.dp)) {
                        
                        FilterDeckArea(
                            categories = listOf(null) + visibleCategories,
                            selectedCategory = filterCategory,
                            months = availableMonths,
                            selectedMonth = filterMonth,
                            isExpanded = isFilterDeckExpanded,
                            onToggleExpand = { 
                                isFilterDeckExpanded = true
                                manualExpandTime = System.currentTimeMillis()
                            },
                            onCategorySelect = { onFilterCategoryChange(if (it == filterCategory) null else it) },
                            onMonthSelect = { onFilterMonthChange(if (it == filterMonth) null else it) }
                        )
                        
                        Spacer(Modifier.height(8.dp))
                        androidx.compose.animation.AnimatedVisibility(
                            visible = isFilterDeckExpanded,
                            enter = expandVertically(spring(0.85f, 300f)) + fadeIn(),
                            exit = shrinkVertically(spring(0.85f, 300f)) + fadeOut()
                        ) {
                            Column(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp)) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("時間範圍", fontSize = 13.sp, color = TEXT_SECONDARY, fontWeight = FontWeight.Medium)
                                    Text("${formatTimeMin(filterStartTimeMin)} - ${formatTimeMin(filterEndTimeMin)}", 
                                         fontSize = 13.sp, color = BRAND_PRIMARY, fontWeight = FontWeight.Bold)
                                }
                                
                                var sliderValues by remember(filterStartTimeMin, filterEndTimeMin) { mutableStateOf(filterStartTimeMin.toFloat()..filterEndTimeMin.toFloat()) }
                                
                                androidx.compose.material3.RangeSlider(
                                    value = sliderValues,
                                    onValueChange = { sliderValues = it },
                                    onValueChangeFinished = { onFilterTimeRangeChange(sliderValues.start.roundToInt(), sliderValues.endInclusive.roundToInt()) },
                                    valueRange = 0f..1440f,
                                    steps = 95,
                                    colors = androidx.compose.material3.SliderDefaults.colors(
                                        thumbColor = BRAND_PRIMARY,
                                        activeTrackColor = BRAND_PRIMARY,
                                        inactiveTrackColor = DIVIDER_COLOR
                                    ),
                                    // 左右向內縮少少，拇指唔貼邊，方便手指拖
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // ★ 篩選結果棒形圖：永遠可見，位於時間範圍下方、列表上方（淨係棒，一頁顯示晒）
                        if (filtered.isNotEmpty()) {
                            Spacer(Modifier.height(6.dp))
                            FilterResultsBarChart(
                                records = filtered,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            } // Column (header expand-from-top)

            Spacer(Modifier.height(8.dp * headerP.coerceIn(0f, 1f)))

            Box(
                Modifier.weight(1f).fillMaxWidth().clipToBounds()
                    .nestedScroll(topScrollConnection) 
                    .graphicsLayer {
                        val p = keyboardProgressProvider()
                        alpha = 1f - (p * 0.4f)
                        if (p > 0.02f && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            renderEffect = getCachedBlurEffect(((p * 24f).roundToInt() * 2).coerceIn(2, 48))
                        } else { renderEffect = null }
                    }
            ) {
                if (loading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BRAND_PRIMARY) }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 0.dp, end = 0.dp, top = 0.dp, bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 80.dp)
                    ) {
                        val showWeeklyChart = weeklyExpenses.isNotEmpty() && (!filterMode || filterChartCollapse < 0.99f)
                        if (showWeeklyChart) {
                            item(key = "weekly_chart", contentType = "weekly_chart") {
                                WeeklyExpenseChart(data = weeklyExpenses, listState = listState, introProgress = weeklyIntroProgress.value, filterCollapseProgress = filterChartCollapse, modifier = Modifier.fillMaxWidth())
                            }
                        }
                        item(key = "magic_top_anchor", contentType = "anchor") { Spacer(Modifier.height(1.dp)) }
                        if (displayGroups.isEmpty() && !showKeyboard) {
                            item(key = "__empty__", contentType = "empty") {
                                Box(Modifier.fillParentMaxWidth().padding(top = 80.dp), contentAlignment = Alignment.Center) {
                                    Text(if (filterMode) "冇符合篩選條件嘅記錄" else "仲未有記錄,撳右下角 + 新增", color = TEXT_SECONDARY)
                                }
                            }
                        } else {
                            listContent()
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(200.dp)
                .background(Brush.verticalGradient(colorStops = arrayOf(
                    0.00f to SURFACE_BG.copy(alpha = 0f), 0.50f to SURFACE_BG.copy(alpha = 0f),
                    0.70f to SURFACE_BG.copy(alpha = 0.45f), 0.88f to SURFACE_BG.copy(alpha = 0.88f), 1.00f to SURFACE_BG
                )))
        )
        FanMenuOverlay(progressProvider = { fanMenuProgress.value }, centerProvider = { fanMenuCenterState.value }, touchProvider = { fanMenuTouchState.value })
    }
}

// ==========================================
// ★ TopStats & Helper Composables
// ==========================================

enum class StatBlockType { CAT, INC, EXP, AVG, BAL, COUNT, TIME_AM, TIME_PM }
private data class BlockGeometry(val x: Dp, val y: Dp, val w: Dp, val h: Dp)

@Composable
fun TopStats(
    hasIncome: Boolean, hasExpense: Boolean,
    income: Double, expense: Double,
    isFilterMode: Boolean = false, filteredCount: Int? = null,
    showCategory: Boolean = false, categoryName: String = "",
    avgExpense: Double? = null,
    avgMorningLabel: String? = null,
    avgAfternoonLabel: String? = null,
    animateValues: Boolean = false,
    isCompact: Boolean = false 
) {
    val balance = income - expense
    val targetBalColor = if (balance >= 0) COLOR_INCOME else COLOR_EXPENSE

    val activeBlocks = remember(
        showCategory, income, expense, avgExpense, balance, isFilterMode, filteredCount,
        avgMorningLabel, avgAfternoonLabel
    ) {
        val list = mutableListOf<StatBlockType>()
        if (isFilterMode) {
            // 篩選模式：唔顯示結餘
            if ((filteredCount ?: 0) > 0) list.add(StatBlockType.COUNT)
            if (income != 0.0) list.add(StatBlockType.INC)
            if (expense != 0.0) list.add(StatBlockType.EXP)
            if ((avgExpense ?: 0.0) > 0.0) list.add(StatBlockType.AVG)
            if (avgMorningLabel != null) list.add(StatBlockType.TIME_AM)
            if (avgAfternoonLabel != null) list.add(StatBlockType.TIME_PM)
        } else {
            if (showCategory) list.add(StatBlockType.CAT)
            if (income != 0.0) list.add(StatBlockType.INC)
            if (expense != 0.0) list.add(StatBlockType.EXP)
            if ((avgExpense ?: 0.0) > 0.0) list.add(StatBlockType.AVG)
            if (balance != 0.0) list.add(StatBlockType.BAL)
        }
        if (list.isEmpty()) list.add(StatBlockType.COUNT)
        list
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        val totalW = maxWidth
        
        val geoMapPair = remember(activeBlocks, totalW) {
            val nGapX = 6.dp
            val nGapY = 2.dp
            val nRowH = 52.dp
            val nMap = mutableMapOf<StatBlockType, BlockGeometry>()
            val nRows = when (activeBlocks.size) {
                1, 2, 3 -> listOf(activeBlocks)
                4 -> listOf(activeBlocks.take(2), activeBlocks.drop(2))
                5 -> listOf(activeBlocks.take(3), activeBlocks.drop(3))
                6 -> listOf(activeBlocks.take(3), activeBlocks.drop(3))
                7 -> listOf(activeBlocks.take(4), activeBlocks.drop(4))
                8 -> listOf(activeBlocks.take(4), activeBlocks.drop(4))
                else -> listOf(activeBlocks.take(4), activeBlocks.drop(4))
            }
            nRows.forEachIndexed { rIdx, row ->
                val w = (totalW - nGapX * (row.size - 1).coerceAtLeast(0)) / row.size
                val y = (nRowH + nGapY) * rIdx
                row.forEachIndexed { cIdx, block ->
                    val x = (w + nGapX) * cIdx
                    nMap[block] = BlockGeometry(x, y, w, nRowH)
                }
            }

            val cGapX = 8.dp
            val cGapY = 8.dp
            val cRowH = 34.dp
            val cMap = mutableMapOf<StatBlockType, BlockGeometry>()
            
            val chunked = activeBlocks.chunked(3)
            chunked.forEachIndexed { rIdx, rowBlocks ->
                val cy = (cRowH + cGapY) * rIdx
                val itemW = (totalW - cGapX * (rowBlocks.size - 1).coerceAtLeast(0)) / rowBlocks.size
                rowBlocks.forEachIndexed { cIdx, block ->
                    val cx = (itemW + cGapX) * cIdx
                    cMap[block] = BlockGeometry(cx, cy, itemW, cRowH)
                }
            }
            Pair(nMap, cMap)
        }

        val nMap = geoMapPair.first
        val cMap = geoMapPair.second

        val nMaxY = nMap.values.maxOfOrNull { it.y + it.h } ?: 52.dp
        val cMaxY = cMap.values.maxOfOrNull { it.y + it.h } ?: 34.dp

        val targetHeight = if (isCompact) cMaxY else nMaxY
        val animHeight by animateDpAsState(targetHeight, MASTER_SPRING_DP, label = "containerH")
        val progress by animateFloatAsState(if (isCompact) 1f else 0f, MASTER_SPRING_FLOAT, label = "compactP")

        val allPossibleBlocks = listOf(
            StatBlockType.COUNT, StatBlockType.CAT, StatBlockType.INC, StatBlockType.EXP,
            StatBlockType.AVG, StatBlockType.BAL, StatBlockType.TIME_AM, StatBlockType.TIME_PM
        )

        Box(Modifier.fillMaxWidth().height(animHeight)) {
            allPossibleBlocks.forEach { block ->
                val isVisible = activeBlocks.contains(block)
                
                var memNGeo by remember { mutableStateOf(BlockGeometry(0.dp, 0.dp, 0.dp, 0.dp)) }
                var memCGeo by remember { mutableStateOf(BlockGeometry(0.dp, 0.dp, 0.dp, 0.dp)) }
                
                LaunchedEffect(nMap[block]) { nMap[block]?.let { memNGeo = it } }
                LaunchedEffect(cMap[block]) { cMap[block]?.let { memCGeo = it } }
                
                val nGeo = nMap[block] ?: memNGeo
                val cGeo = cMap[block] ?: memCGeo
                
                val x by animateDpAsState(androidx.compose.ui.unit.lerp(nGeo.x, cGeo.x, progress), MASTER_SPRING_DP, label = "x_$block")
                val y by animateDpAsState(androidx.compose.ui.unit.lerp(nGeo.y, cGeo.y, progress), MASTER_SPRING_DP, label = "y_$block")
                val w by animateDpAsState(androidx.compose.ui.unit.lerp(nGeo.w, cGeo.w, progress), MASTER_SPRING_DP, label = "w_$block")
                val h by animateDpAsState(androidx.compose.ui.unit.lerp(nGeo.h, cGeo.h, progress), MASTER_SPRING_DP, label = "h_$block")

                val bgColor = androidx.compose.ui.graphics.lerp(SURFACE_CARD.copy(alpha = 0f), SURFACE_CARD, progress)
                val borderColor = androidx.compose.ui.graphics.lerp(DIVIDER_COLOR.copy(alpha = 0f), DIVIDER_COLOR.copy(alpha = 0.5f), progress)

                androidx.compose.animation.AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(250)) + scaleIn(spring(0.85f, 300f), initialScale = 0.7f),
                    exit = fadeOut(tween(200)) + scaleOut(spring(0.85f, 300f), targetScale = 0.7f),
                    modifier = Modifier.offset(x = x, y = y).width(w).height(h)
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(bgColor)
                            .border(BorderStroke(0.5.dp, borderColor), RoundedCornerShape(10.dp))
                    ) {
                        when (block) {
                            StatBlockType.COUNT -> AnimatedStatBlock(compactProgress = progress, iconNormal = Icons.Default.Numbers, iconColorNormal = BRAND_PRIMARY, labelNormal = "筆數", valueNormal = "${filteredCount ?: 0}", valueColorNormal = BRAND_PRIMARY, showDollar = false, animateValues = animateValues, isCount = true)
                            StatBlockType.CAT -> AnimatedStatBlock(compactProgress = progress, iconNormal = CATEGORY_STYLES[categoryName]?.icon ?: Icons.Default.Category, iconColorNormal = CATEGORY_STYLES[categoryName]?.fgColor ?: TEXT_PRIMARY, labelNormal = "類別", valueNormal = categoryName, valueColorNormal = TEXT_PRIMARY, showDollar = false, animateValues = animateValues)
                            StatBlockType.INC -> AnimatedStatBlock(compactProgress = progress, iconNormal = Icons.Default.TrendingUp, iconColorNormal = COLOR_INCOME, labelNormal = "收入", valueNormal = formatAmountNoDecimal(income), valueColorNormal = COLOR_INCOME, animateValues = animateValues)
                            StatBlockType.EXP -> AnimatedStatBlock(compactProgress = progress, iconNormal = Icons.Default.TrendingDown, iconColorNormal = COLOR_EXPENSE, labelNormal = "支出", valueNormal = formatAmountNoDecimal(expense), valueColorNormal = COLOR_EXPENSE, animateValues = animateValues)
                            StatBlockType.AVG -> AnimatedStatBlock(compactProgress = progress, iconNormal = Icons.Default.Receipt, iconColorNormal = Color(0xFFF59E0B), labelNormal = "均支", valueNormal = formatAmountNoDecimal(avgExpense ?: 0.0), valueColorNormal = Color(0xFFF59E0B), animateValues = animateValues)
                            StatBlockType.BAL -> AnimatedStatBlock(compactProgress = progress, iconNormal = Icons.Default.AccountBalanceWallet, iconColorNormal = targetBalColor, labelNormal = "結餘", valueNormal = formatAmountNoDecimal(balance), valueColorNormal = targetBalColor, animateValues = animateValues)
                            StatBlockType.TIME_AM -> AnimatedStatBlock(compactProgress = progress, iconNormal = Icons.Default.WbSunny, iconColorNormal = Color(0xFFF59E0B), labelNormal = "上午均時", valueNormal = avgMorningLabel ?: "--:--", valueColorNormal = Color(0xFFF59E0B), showDollar = false, animateValues = animateValues)
                            StatBlockType.TIME_PM -> AnimatedStatBlock(compactProgress = progress, iconNormal = Icons.Default.NightsStay, iconColorNormal = Color(0xFF6366F1), labelNormal = "下午均時", valueNormal = avgAfternoonLabel ?: "--:--", valueColorNormal = Color(0xFF6366F1), showDollar = false, animateValues = animateValues)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedStatBlock(
    compactProgress: Float,
    iconNormal: ImageVector,
    iconColorNormal: Color,
    labelNormal: String,
    valueNormal: String,
    valueColorNormal: Color,
    showDollar: Boolean = true,
    animateValues: Boolean = false,
    isCount: Boolean = false,
    modifier: Modifier = Modifier
) {
    val finalValueString = if (showDollar) {
        if (valueNormal.startsWith("-")) "-$" + valueNormal.substring(1) else "$" + valueNormal
    } else if (isCount && compactProgress > 0.5f) {
        "${valueNormal}筆"
    } else valueNormal

    val currentLabelColor = androidx.compose.ui.graphics.lerp(TEXT_SECONDARY, TEXT_PRIMARY, compactProgress)

    androidx.compose.ui.layout.Layout(
        content = {
            Icon(iconNormal, null, tint = iconColorNormal.copy(alpha = (1f - compactProgress * 2f).coerceIn(0f, 1f)), modifier = Modifier.size(16.dp))
            Text(text = labelNormal, fontSize = 11.sp, color = currentLabelColor, fontWeight = if (compactProgress > 0.5f) FontWeight.SemiBold else FontWeight.Medium, maxLines = 1, softWrap = false)
            val currentFontSize = (19f - 6f * compactProgress).sp
            RollingNumber(text = finalValueString, color = valueColorNormal, fontSize = currentFontSize, fontWeight = if (compactProgress > 0.5f) FontWeight.Bold else FontWeight.ExtraBold, animateChanges = animateValues)
        },
        modifier = modifier.fillMaxSize()
    ) { measurables, constraints ->
        val iconP = measurables[0].measure(constraints.copy(minWidth = 0, minHeight = 0))
        val labelP = measurables[1].measure(constraints.copy(minWidth = 0, minHeight = 0))
        val valueP = measurables[2].measure(constraints.copy(minWidth = 0, minHeight = 0))

        layout(constraints.maxWidth, constraints.maxHeight) {
            fun lerpI(a: Int, b: Int, p: Float) = (a + (b - a) * p).roundToInt()
            val titleGapNormal = 4.dp.roundToPx()

            val topRowWNormal = iconP.width + titleGapNormal + labelP.width
            val topRowXNormal = (constraints.maxWidth - topRowWNormal) / 2
            val topRowHNormal = maxOf(iconP.height, labelP.height)
            val topRowYNormal = (constraints.maxHeight / 2) - topRowHNormal + 1.dp.roundToPx()

            val nIconX = topRowXNormal
            val nLabelX = nIconX + iconP.width + titleGapNormal
            val nIconY = topRowYNormal + (topRowHNormal - iconP.height) / 2
            val nLabelY = topRowYNormal + (topRowHNormal - labelP.height) / 2
            val nValueX = (constraints.maxWidth - valueP.width) / 2
            val nValueY = (constraints.maxHeight / 2) + 1.dp.roundToPx()

            val gapCompact = 6.dp.roundToPx()
            val cTotalW = labelP.width + gapCompact + valueP.width
            val cStartX = (constraints.maxWidth - cTotalW) / 2
            
            val cLabelX = cStartX
            val cLabelY = (constraints.maxHeight - labelP.height) / 2
            val cValueX = cLabelX + labelP.width + gapCompact
            val cValueY = (constraints.maxHeight - valueP.height) / 2
            val cIconX = cLabelX
            val cIconY = cLabelY

            iconP.placeRelative(lerpI(nIconX, cIconX, compactProgress), lerpI(nIconY, cIconY, compactProgress))
            labelP.placeRelative(lerpI(nLabelX, cLabelX, compactProgress), lerpI(nLabelY, cLabelY, compactProgress))
            valueP.placeRelative(lerpI(nValueX, cValueX, compactProgress), lerpI(nValueY, cValueY, compactProgress))
        }
    }
}

@Composable
fun FilterDeckArea(
    categories: List<String?>,
    selectedCategory: String?,
    months: List<String>,
    selectedMonth: String?,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onCategorySelect: (String?) -> Unit,
    onMonthSelect: (String?) -> Unit
) {
    val density = LocalDensity.current
    val gap = 6.dp
    val chipH = 34.dp

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().animateContentSize(spring(dampingRatio = 0.8f, stiffness = 300f))
    ) {
        val totalWidth = maxWidth
        val chipW = (totalWidth - gap * 3) / 4

        val catRows = (categories.size + 3) / 4
        val monthList = listOf<String?>(null) + months
        val monthRows = (monthList.size + 3) / 4

        val expandedHeight = (catRows * (chipH + gap).value).dp + 8.dp + (monthRows * (chipH + gap).value).dp
        val collapsedHeight = chipH + 8.dp

        Box(Modifier.fillMaxWidth().height(if (isExpanded) expandedHeight else collapsedHeight)) {
            if (!isExpanded) {
                Box(Modifier.fillMaxSize().clickable(interactionSource = remember{MutableInteractionSource()}, indication=null) { onToggleExpand() })
            }

            categories.forEachIndexed { idx, cat ->
                // ★ 核心修改：加入 key 追蹤 Clip 身份，當 idx 改變時，會觸發流暢嘅位移動畫
                key(cat ?: "all_categories_key") {
                    val isSelected = cat == selectedCategory
                    val col = idx % 4
                    val row = idx / 4
                    val expX = col * (chipW + gap).value
                    val expY = row * (chipH + gap).value

                    val deckX = (totalWidth.value / 4) - (chipW.value / 2)
                    val staggerX = if (isSelected) 0f else (idx % 3) * 2f
                    val staggerY = if (isSelected) 0f else (idx % 3) * 3f
                    val colX = deckX + staggerX
                    val colY = staggerY

                    val targetX = if (isExpanded) expX else colX
                    val targetY = if (isExpanded) expY else colY

                    val animX by animateFloatAsState(targetX, spring(0.75f, 350f), label = "x_$cat")
                    val animY by animateFloatAsState(targetY, spring(0.75f, 350f), label = "y_$cat")
                    
                    val zIndex = if (isSelected) 10f else (1f - idx * 0.01f)

                    Box(Modifier.offset(animX.dp, animY.dp).width(chipW).height(chipH).zIndex(if(isExpanded) 1f else zIndex)) {
                        AnimatedFilterChip(
                            selected = isSelected, label = cat ?: "全部", fillWidth = true,
                            onClick = { if (isExpanded) onCategorySelect(cat) else onToggleExpand() }
                        )
                    }
                }
            }

            val monthStartY = catRows * (chipH + gap).value + 8f
            monthList.forEachIndexed { idx, m ->
                // ★ 月份同樣加入 key 綁定
                key(m ?: "all_months_key") {
                    val isSelected = m == selectedMonth
                    val col = idx % 4
                    val row = idx / 4
                    val expX = col * (chipW + gap).value
                    val expY = monthStartY + row * (chipH + gap).value

                    val deckX = (totalWidth.value * 3 / 4) - (chipW.value / 2)
                    val staggerX = if (isSelected) 0f else (idx % 3) * 2f
                    val staggerY = if (isSelected) 0f else (idx % 3) * 3f
                    val colX = deckX + staggerX
                    val colY = staggerY

                    val targetX = if (isExpanded) expX else colX
                    val targetY = if (isExpanded) expY else colY

                    val animX by animateFloatAsState(targetX, spring(0.75f, 350f), label = "mx_$m")
                    val animY by animateFloatAsState(targetY, spring(0.75f, 350f), label = "my_$m")
                    
                    val zIndex = if (isSelected) 10f else (1f - idx * 0.01f)

                    Box(Modifier.offset(animX.dp, animY.dp).width(chipW).height(chipH).zIndex(if(isExpanded) 1f else zIndex)) {
                        AnimatedFilterChip(
                            selected = isSelected, label = m?.let{formatMonthLabel(it)} ?: "全年", fillWidth = true,
                            onClick = { if (isExpanded) onMonthSelect(m) else onToggleExpand() }
                        )
                    }
                }
            }
        }
    }
}

// 🟢 核心：全局統一嘅物理彈簧，保證高度、位置、大小、內部排版「完美同步」
private val MASTER_SPRING_DP = spring<Dp>(dampingRatio = 0.9f, stiffness = 400f)
private val MASTER_SPRING_FLOAT = spring<Float>(dampingRatio = 0.9f, stiffness = 400f)

@Composable
fun WeeklyExpenseChart(
    data: List<WeeklyExpenseDay>,
    listState: LazyListState,
    introProgress: Float,
    filterCollapseProgress: Float = 0f,
    modifier: Modifier = Modifier,
) {
    val chartHeight = 178.dp
    val horizontalPadding = 16.dp
    val innerPadding = 12.dp
    val titleGap = 4.dp
    val labelRowHeight = 20.dp

    val density = LocalDensity.current
    val chartHeightPx = remember(density) { with(density) { chartHeight.toPx() } }

    // filter 收起的「外部高度上限」：0→1 對應 178→0
    val collapsed = filterCollapseProgress.coerceIn(0f, 1f)
    val maxBoxHeightPx = (chartHeightPx * (1f - collapsed)).coerceAtLeast(0f)
    val maxBoxHeightDp = with(density) { maxBoxHeightPx.toDp() }

    val maxAmount = remember(data) {
        data.maxOfOrNull { it.amount }?.coerceAtLeast(0.0001) ?: 0.0001
    }
    val totalAmount = remember(data) { data.sumOf { it.amount } }

    // 滾動偏移：只反映「用戶手動滾動」造成的裁切
    val scrollOffsetPx by remember(listState, chartHeightPx) {
        derivedStateOf {
            val info = listState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.key == "weekly_chart" }
            if (info == null) 0f
            else (-info.offset).toFloat().coerceIn(0f, chartHeightPx)
        }
    }

    // 卡片最終可見高度 = filter 收起後的最大高度 - 滾動偏移
    val visibleHeightPx = (maxBoxHeightPx - scrollOffsetPx).coerceAtLeast(0f)
    val visibleHeightDp = with(density) { visibleHeightPx.toDp() }

    if (maxBoxHeightPx <= 0.5f) {
        Spacer(modifier = modifier.fillMaxWidth().height(0.dp))
        return
    }

    // ★ 當前選中柱 index（null = 無選中）
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(data) { selectedIndex = null }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(maxBoxHeightDp)
            .padding(horizontal = horizontalPadding)
    ) {
        if (visibleHeightPx > 0.5f) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(visibleHeightDp)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SURFACE_CARD)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .height(chartHeight)
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = innerPadding, vertical = innerPadding)
                ) {
                    // ── 標題行
                    Row(
                        Modifier.fillMaxWidth().wrapContentHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "近 7 日支出",
                            fontSize = 13.sp,
                            // ★ 明確加大 lineHeight，俾中文字有足夠高度
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TEXT_PRIMARY,
                            // ★ 移除 includeFontPadding，用返系統預設，避免內部裁切
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "合計 $${formatAmountNoDecimal(totalAmount)}",
                            fontSize = 12.sp,
                            // ★ 同樣加大 lineHeight
                            lineHeight = 18.sp,
                            color = TEXT_SECONDARY,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    Spacer(Modifier.height(titleGap))

                    // ── 柱體區（含浮標層）
                    BoxWithConstraints(
                        Modifier.fillMaxWidth().weight(1f)
                    ) {
                        val areaWPx = with(density) { maxWidth.toPx() }
                        val selIdx = selectedIndex

                        // 記住最後選中嘅位置 + 金額，令淡出動畫穩定
                        var lastCxRatio by remember { mutableFloatStateOf(0.5f) }
                        var lastAmountText by remember { mutableStateOf("") }
                        LaunchedEffect(selIdx) {
                            if (selIdx != null && selIdx in data.indices) {
                                lastCxRatio = (selIdx + 0.5f) / data.size
                                lastAmountText = "$${formatAmountNoDecimal(data[selIdx].amount)}"
                            }
                        }
                        // 水平平滑跟隨
                        val animatedCxRatio by animateFloatAsState(
                            targetValue = lastCxRatio,
                            animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f),
                            label = "weeklyBubbleCx"
                        )
                        // 透明度淡入淡出
                        val bubbleAlpha by animateFloatAsState(
                            targetValue = if (selIdx != null && selIdx in data.indices) 1f else 0f,
                            animationSpec = tween(160, easing = FastOutSlowInEasing),
                            label = "weeklyBubbleAlpha"
                        )

                        // 柱體 Row
                        Row(
                            Modifier
                                .fillMaxSize()
                                .pointerInput(data.size) {
                                    if (data.isEmpty()) return@pointerInput
                                    awaitEachGesture {
                                        val down = awaitFirstDown(requireUnconsumed = false)
                                        val w = size.width.toFloat()
                                        val h = size.height.toFloat()
                                        if (w <= 0f) return@awaitEachGesture
                                        val cw = w / data.size

                                        val downIdx = (down.position.x / cw).toInt()
                                        if (downIdx in data.indices) selectedIndex = downIdx

                                        var dragging = false
                                        while (true) {
                                            val event = awaitPointerEvent()
                                            val change = event.changes
                                                .firstOrNull { it.id == down.id } ?: break

                                            if (!change.pressed || change.isConsumed) {
                                                selectedIndex = null
                                                break
                                            }

                                            val pos = change.position
                                            val totalDx = abs(pos.x - down.position.x)
                                            val totalDy = abs(pos.y - down.position.y)

                                            if (!dragging && totalDx > viewConfiguration.touchSlop && totalDx > totalDy) {
                                                dragging = true
                                            }

                                            if (dragging) change.consume()

                                            if (pos.x < 0f || pos.x > w || pos.y < 0f || pos.y > h) {
                                                selectedIndex = null
                                            } else {
                                                selectedIndex = (pos.x / cw).toInt().coerceIn(0, data.size - 1)
                                            }
                                        }
                                    }
                                },
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val anySelected = selIdx != null
                            data.forEachIndexed { idx, day ->
                                WeeklyExpenseBarBody(
                                    day = day,
                                    maxAmount = maxAmount,
                                    introProgress = introProgress,
                                    isSelected = selIdx == idx,
                                    isAnySelected = anySelected,
                                    modifier = Modifier.weight(1f).fillMaxHeight()
                                )
                            }
                        }

                        // ★ 浮標層
                        Box(
                            modifier = Modifier
                                .graphicsLayer { alpha = bubbleAlpha }
                                .layout { measurable, outerConstraints ->
                                    val placeable = measurable.measure(
                                        outerConstraints.copy(
                                            minWidth = 0,
                                            maxWidth = Constraints.Infinity
                                        )
                                    )
                                    layout(outerConstraints.maxWidth, placeable.height) {
                                        val cx = areaWPx * animatedCxRatio
                                        val maxLeft = (outerConstraints.maxWidth - placeable.width).coerceAtLeast(0)
                                        val x = (cx - placeable.width / 2f).roundToInt().coerceIn(0, maxLeft)
                                        placeable.placeRelative(x, 0)
                                    }
                                }
                        ) {
                            Box(
                                Modifier
                                    .widthIn(min = 40.dp)
                                    .height(22.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BRAND_PRIMARY_DARK)
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lastAmountText,
                                    fontSize = 10.sp,
                                    lineHeight = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    softWrap = false,
                                    letterSpacing = (-0.3).sp
                                )
                            }
                        }
                    }

                    // ── 日期行
                    Row(
                        Modifier.fillMaxWidth().heightIn(min = labelRowHeight),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        data.forEach { day ->
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                Text(
                                    text = day.weekdayChar,
                                    fontSize = 11.sp,
                                    // ★ 設定充足嘅行高，移除 includeFontPadding
                                    lineHeight = 16.sp,
                                    color = if (day.isToday) BRAND_PRIMARY_DARK else TEXT_SECONDARY,
                                    fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private val PARTICLE_COLORS = listOf(
    BRAND_PRIMARY, BRAND_PRIMARY_DARK, Color(0xFF818CF8), Color(0xFFFBBF24), Color(0xFF34D399)
)

@Composable
fun AnimatedRecordItem(
    animateOnMount: Boolean,
    isDeleting: Boolean = false,
    content: @Composable () -> Unit
) {
    var everDeleting by remember { mutableStateOf(isDeleting) }
    LaunchedEffect(animateOnMount, isDeleting) {
        when {
            animateOnMount -> everDeleting = false
            isDeleting -> everDeleting = true
        }
    }

    if (!animateOnMount && !everDeleting) {
        Box(Modifier.fillMaxWidth().height(72.dp)) { content() }
        return
    }

    val density = LocalDensity.current
    val maxHeightPx = remember(density) { with(density) { 72.dp.toPx() } }
    val heightAnim = remember { Animatable(if (animateOnMount) 0f else maxHeightPx) }
    val scaleAnim = remember { Animatable(if (animateOnMount) 0.85f else 1f) }
    val alphaAnim = remember { Animatable(if (animateOnMount) 0f else 1f) }
    val translateYAnim = remember { Animatable(if (animateOnMount) 40f else 0f) }

    LaunchedEffect(animateOnMount, isDeleting) {
        if (isDeleting) {
            launch {
                translateYAnim.animateTo(16f,
                    spring(dampingRatio = 0.85f, stiffness = 220f))
            }
            launch { alphaAnim.animateTo(0f, tween(320, easing = FastOutSlowInEasing)) }
            delay(260)
            launch {
                heightAnim.animateTo(0f, spring(dampingRatio = 1f, stiffness = 250f))
            }
            launch {
                scaleAnim.animateTo(0.85f,
                    spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow))
            }
        } else if (animateOnMount) {
            delay(30)
            launch {
                heightAnim.animateTo(maxHeightPx,
                    spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow))
            }
            launch {
                scaleAnim.animateTo(1f,
                    spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow))
            }
            launch { alphaAnim.animateTo(1f, tween(300, easing = FastOutSlowInEasing)) }
            launch {
                translateYAnim.animateTo(0f,
                    spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow))
            }
        }
    }

    val heightDp = with(density) { heightAnim.value.toDp() }

    Box(Modifier.fillMaxWidth().height(heightDp).clipToBounds()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(72.dp).graphicsLayer {
                alpha = alphaAnim.value
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
                translationY = translateYAnim.value
            }
        ) { content() }

        if (isDeleting) {
            val particleProgress = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                particleProgress.animateTo(1f, tween(550, easing = FastOutSlowInEasing))
            }
            val particles = remember {
                val random = Random(42)
                List(60) { Triple(random.nextFloat(), random.nextFloat(), (random.nextFloat() - 0.5f) * 280f) }
            }
            Canvas(modifier = Modifier.matchParentSize()) {
                val prog = particleProgress.value
                if (prog <= 0f) return@Canvas
                val w = size.width
                val h = 72.dp.toPx()
                particles.forEachIndexed { idx, (rx, ry, angle) ->
                    val startX = rx * w
                    val startY = ry * h
                    val dist = prog * 300f
                    val rad = Math.toRadians(angle.toDouble())
                    val px = startX + dist * kotlin.math.cos(rad).toFloat()
                    val py = startY + dist * kotlin.math.sin(rad).toFloat() - prog * 100f
                    val pAlpha = (1f - prog).coerceIn(0f, 1f)
                    val pRadius = (1.6.dp.toPx() * (1f - prog * 0.4f)).coerceAtLeast(0.5f)
                    drawCircle(
                        color = PARTICLE_COLORS[idx % PARTICLE_COLORS.size].copy(alpha = pAlpha),
                        radius = pRadius, center = Offset(px, py)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryTotalHint(hint: AfterSaveHint, visible: Boolean) {
    val isIncome = hint.category == INCOME_CATEGORY
    val bgColor = if (isIncome) Color(0xFFD1FAE5) else Color(0xFFFEE2E2)
    val fgColor = if (isIncome) COLOR_INCOME else COLOR_EXPENSE
    val icon = if (isIncome) Icons.Default.TrendingUp else Icons.Default.TrendingDown

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(250)) + expandVertically(tween(250), expandFrom = Alignment.Top),
        exit = fadeOut(tween(250)) + shrinkVertically(tween(250), shrinkTowards = Alignment.Top)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = fgColor, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (isIncome)
                    "今個月「${hint.category}」共收入 ${formatAmount(hint.monthTotal)}"
                else
                    "今個月「${hint.category}」共支出 ${formatAmount(hint.monthTotal)}",
                fontSize = 12.sp, color = fgColor, fontWeight = FontWeight.Medium
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CalendarContent(
    calendarUi: CalendarUiState,
    currentMonthKey: String,
    onMonthChange: (String) -> Unit,
    filterCategory: String?,
    onFilterCategoryChange: (String?) -> Unit,
    onCopyClick: (Record) -> Unit,
    onEditClick: (Record) -> Unit,
    onFilterClick: (Record) -> Unit,
    onDeleteClick: (Record) -> Unit,
    onChangeIconClick: (Record) -> Unit,
    onAddClick: () -> Unit,
    deletingIds: Set<String> = emptySet(),
    justAddedIds: Set<String> = emptySet()
) {
    // ★ 核心修復：狀態鎖 (State Lock)
    // 攔截唔匹配嘅過期數據，只有月份一致時先至放行更新網格
    var safeUiState by remember { mutableStateOf(calendarUi) }
    var safeMonthKey by remember { mutableStateOf(currentMonthKey) }

    LaunchedEffect(currentMonthKey, calendarUi) {
        if (calendarUi.monthKey == currentMonthKey || calendarUi.monthKey.isEmpty()) {
            safeUiState = calendarUi
            safeMonthKey = currentMonthKey
        }
    }

    var animateValues by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(800); animateValues = true }

    val today = remember { Calendar.getInstance() }
    val todayKey = remember { monthKeyFromTimestamp(System.currentTimeMillis()) }

    var monthDirection by remember { mutableIntStateOf(1) }
    var displayMode by remember { mutableIntStateOf(0) }

    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var selectedRowIdx by remember { mutableStateOf<Int?>(null) }
    var selectedColIdx by remember { mutableStateOf<Int?>(null) }
    var detailExpandedId by remember { mutableStateOf<String?>(null) }

    // 月曆明細：長按扇形選單（同記帳頁）
    var activeFanRecord by remember { mutableStateOf<Record?>(null) }
    val fanMenuCenterState = remember { mutableStateOf(Offset.Zero) }
    val fanMenuTouchState = remember { mutableStateOf(Offset.Zero) }
    val densityCal = LocalDensity.current
    val configurationCal = LocalConfiguration.current
    val screenWidthPxCal = with(densityCal) { configurationCal.screenWidthDp.dp.toPx() }
    val fanRadiusPxCal = remember(densityCal) { with(densityCal) { 105.dp.toPx() } }
    val fanHalfBtnPxCal = remember(densityCal) { with(densityCal) { 24.dp.toPx() } }
    val fanEdgeThresholdCal = fanRadiusPxCal + fanHalfBtnPxCal
    val fanMenuProgressCal = animateFloatAsState(
        if (activeFanRecord != null) 1f else 0f,
        spring(0.65f, Spring.StiffnessMediumLow),
        label = "calFan"
    )
    val minFanRadiusPxCal = remember(densityCal) { with(densityCal) { 16.dp.toPx() } }
    val scopeCal = rememberCoroutineScope()
    val latestOnCopyCal by rememberUpdatedState(onCopyClick)
    val latestOnEditCal by rememberUpdatedState(onEditClick)
    val latestOnFilterCal by rememberUpdatedState(onFilterClick)
    val latestOnDeleteCal by rememberUpdatedState(onDeleteClick)
    val handleFanStartCal = remember {
        { r: Record, offset: Offset ->
            activeFanRecord = r
            fanMenuCenterState.value = offset
            fanMenuTouchState.value = offset
        }
    }
    val handleFanDragCal = remember {
        { dragAmount: Offset -> fanMenuTouchState.value += dragAmount }
    }
    val handleFanEndCal = remember {
        {
            val center = fanMenuCenterState.value
            val leftSpace = center.x
            val rightSpace = screenWidthPxCal - center.x
            val rotation = when {
                leftSpace < fanEdgeThresholdCal -> (1f - leftSpace / fanEdgeThresholdCal).coerceIn(0f, 1f) * 80f
                rightSpace < fanEdgeThresholdCal -> -(1f - rightSpace / fanEdgeThresholdCal).coerceIn(0f, 1f) * 80f
                else -> 0f
            }
            val arcCenterAngle = -90f + rotation
            val hovered = getHoveredActionIndexWithCenter(
                center, fanMenuTouchState.value, minFanRadiusPxCal,
                arcCenterAngle, fanRadiusPxCal, with(densityCal) { 40.dp.toPx() }
            )
            val targetRecord = activeFanRecord
            activeFanRecord = null
            if (targetRecord != null && hovered != null) {
                scopeCal.launch {
                    delay(120)
                    when (hovered) {
                        0 -> latestOnCopyCal(targetRecord)
                        1 -> latestOnEditCal(targetRecord)
                        2 -> latestOnFilterCal(targetRecord)
                        3 -> latestOnDeleteCal(targetRecord)
                    }
                }
            }
        }
    }

    var cachedSelectedDay by remember { mutableIntStateOf(1) }
    var cachedSelectedRowIdx by remember { mutableIntStateOf(0) }
    var cachedSelectedCol by remember { mutableIntStateOf(0) }

    // 階段 1：collapseP 0→1 日曆收到剩選中一行，明細停喺該日期格下
    // 階段 2：chromeP 0→1（只喺 collapseP≈1 之後）星期行頂到頁頂，chips/stats 跟手推走
    var collapseP by remember { mutableFloatStateOf(0f) }
    var chromeP by remember { mutableFloatStateOf(0f) }
    var isSettling by remember { mutableStateOf(false) }
    var topSectionHeightPx by remember { mutableFloatStateOf(0f) }

    val detailScrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val headerHeightDp = 32.dp
    val headerHeightPx = remember(density) { with(density) { headerHeightDp.toPx() } }
    val triangleHeightDp = 11.dp
    val handleBarHeightDp = 20.dp
    val minDetailHeightPx = remember(density) { with(density) { 120.dp.toPx() } }
    val fabTopPaddingPx = remember(density) {
        with(density) { (NAV_HEIGHT + NAV_BOTTOM_PADDING + 56.dp + 20.dp).toPx() }
    }

    // ★ 網格日期計算依賴 safeMonthKey
    val curCal = remember(safeMonthKey) {
        val parts = safeMonthKey.split("-")
        Calendar.getInstance().apply {
            set(parts[0].toInt(), parts[1].toInt() - 1, 1)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
    }
    val year = curCal.get(Calendar.YEAR)
    val month = curCal.get(Calendar.MONTH) + 1
    val daysInMonth = curCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val isCurrentMonth = safeMonthKey == todayKey

    val recordsByDay = safeUiState.recordsByDay
    val maxAbsNet = safeUiState.maxAbsNet
    val totalNet = safeUiState.totalNet
    val avgPerItem = safeUiState.avgPerItem
    val avgPerDay = safeUiState.avgPerDay
    val visibleCategories = safeUiState.visibleCategories

    val firstDayOffset = remember(curCal) {
        val first = curCal.clone() as Calendar
        first.set(Calendar.DAY_OF_MONTH, 1)
        first.get(Calendar.DAY_OF_WEEK) - 1
    }
    val totalRows = (firstDayOffset + daysInMonth + 6) / 7
    val gridSpacingPx = remember(density) { with(density) { 6.dp.toPx() } }

    val rowHeightsPx = remember(totalRows, firstDayOffset, daysInMonth, recordsByDay, displayMode, density) {
        val heights = FloatArray(totalRows)
        for (rowIdx in 0 until totalRows) {
            var maxItems = 0
            for (col in 0 until 7) {
                val day = rowIdx * 7 + col - firstDayOffset + 1
                if (day in 1..daysInMonth) {
                    val c = recordsByDay[day]?.size ?: 0
                    if (c > maxItems) maxItems = c
                }
            }
            val contentHeightDp = when (displayMode) {
                0 -> 52f
                1 -> maxOf(56f, 34f + maxItems * 16f)
                2 -> {
                    val iconRows = (maxItems + 2) / 3
                    maxOf(52f, 30f + iconRows * 21f)
                }
                else -> 52f
            }
            val contentHeightPx = with(density) { contentHeightDp.dp.toPx() }
            val cellMaxScale = 1.06f
            val verticalPadPx = maxOf(
                with(density) { 4.dp.toPx() },
                contentHeightPx * (cellMaxScale - 1f) / 2f + with(density) { 2.dp.toPx() }
            )
            heights[rowIdx] = contentHeightPx + verticalPadPx * 2f
        }
        heights
    }

    val fullGridHeightPx = remember(rowHeightsPx, totalRows, gridSpacingPx) {
        if (totalRows <= 0) 0f
        else rowHeightsPx.sum() + gridSpacingPx * (totalRows - 1).coerceAtLeast(0)
    }

    val rowOffsetsPx = remember(rowHeightsPx, totalRows, gridSpacingPx) {
        val offsets = FloatArray(totalRows)
        var cur = 0f
        for (i in 0 until totalRows) {
            offsets[i] = cur
            cur += rowHeightsPx[i] + if (i < totalRows - 1) gridSpacingPx else 0f
        }
        offsets
    }

    fun selRowOf(day: Int?): Int {
        if (day == null) return 0
        return ((firstDayOffset + day - 1) / 7).coerceIn(0, (totalRows - 1).coerceAtLeast(0))
    }

    fun collapseRangeForDay(day: Int?): Float {
        val row = selRowOf(day)
        if (row !in rowHeightsPx.indices) return 0f
        return (fullGridHeightPx - rowHeightsPx[row]).coerceAtLeast(0f)
    }

    fun chromeRangePx(): Float = topSectionHeightPx.coerceAtLeast(1f)

    /** 手指向上（px>0）→ 先階段1再階段2；回傳實際消耗 px */
    fun applyDragUp(px: Float): Float {
        if (px <= 0f || selectedDay == null) return 0f
        var left = px
        var consumed = 0f
        // 階段 1：收日曆
        if (collapseP < 0.999f && left > 0f) {
            val range = collapseRangeForDay(selectedDay).coerceAtLeast(1f)
            val newP = (collapseP + left / range).coerceIn(0f, 1f)
            val used = (newP - collapseP) * range
            collapseP = newP
            consumed += used
            left -= used
        }
        // 階段 2：推走 chips/stats（星期自然頂到月份導航下）
        if (collapseP >= 0.999f && left > 0f) {
            val range = chromeRangePx()
            val newP = (chromeP + left / range).coerceIn(0f, 1f)
            val used = (newP - chromeP) * range
            chromeP = newP
            consumed += used
        }
        return consumed
    }

    /** 手指向下（px>0）→ 先退階段2再退階段1 */
    fun applyDragDown(px: Float): Float {
        if (px <= 0f || selectedDay == null) return 0f
        var left = px
        var consumed = 0f
        if (chromeP > 0.001f && left > 0f) {
            val range = chromeRangePx()
            val newP = (chromeP - left / range).coerceIn(0f, 1f)
            val used = (chromeP - newP) * range
            chromeP = newP
            consumed += used
            left -= used
        }
        if (chromeP <= 0.001f && left > 0f) {
            val range = collapseRangeForDay(selectedDay).coerceAtLeast(1f)
            val newP = (collapseP - left / range).coerceIn(0f, 1f)
            val used = (collapseP - newP) * range
            collapseP = newP
            consumed += used
        }
        return consumed
    }

    fun animateCollapseTo(target: Float) {
        isSettling = true
        val start = collapseP
        scope.launch {
            try {
                Animatable(start).animateTo(
                    target.coerceIn(0f, 1f),
                    spring(dampingRatio = 0.9f, stiffness = 380f)
                ) { collapseP = value }
            } finally {
                isSettling = false
            }
        }
    }

    fun animateChromeTo(target: Float) {
        isSettling = true
        val start = chromeP
        scope.launch {
            try {
                Animatable(start).animateTo(
                    target.coerceIn(0f, 1f),
                    spring(dampingRatio = 0.9f, stiffness = 380f)
                ) { chromeP = value }
            } finally {
                isSettling = false
            }
        }
    }

    fun snapAfterDrag() {
        // 階段2優先吸附
        if (chromeP > 0.01f || collapseP >= 0.99f) {
            if (chromeP >= 0.45f) animateChromeTo(1f) else animateChromeTo(0f)
            if (collapseP < 0.99f) animateCollapseTo(1f)
            return
        }
        if (collapseP >= 0.45f) animateCollapseTo(1f) else animateCollapseTo(0f)
    }

    val handleDismiss: () -> Unit = {
        if (selectedDay != null || collapseP > 0.01f || chromeP > 0.01f) {
            scope.launch {
                isSettling = true
                try {
                    // 先退 chrome 再退 collapse
                    val c0 = chromeP
                    if (c0 > 0.01f) {
                        Animatable(c0).animateTo(0f, spring(dampingRatio = 0.9f, stiffness = 380f)) {
                            chromeP = value
                        }
                    }
                    chromeP = 0f
                    val s0 = collapseP
                    Animatable(s0).animateTo(0f, spring(dampingRatio = 0.9f, stiffness = 380f)) {
                        collapseP = value
                    }
                } finally {
                    isSettling = false
                    collapseP = 0f
                    chromeP = 0f
                    selectedDay = null
                    selectedRowIdx = null
                    selectedColIdx = null
                    detailExpandedId = null
                }
            }
        }
    }

    LaunchedEffect(currentMonthKey) { handleDismiss() }
    BackHandler(enabled = selectedDay != null) { handleDismiss() }

    // ★ 兩段式 nested scroll：上滑先收日曆，再推 chrome；下滑相反
    val detailNestedConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (selectedDay == null || isSettling) return Offset.Zero
            // 手指向上 available.y < 0
            if (available.y < 0f && detailScrollState.value <= 0) {
                val used = applyDragUp(-available.y)
                return Offset(0f, -used)
            }
            return Offset.Zero
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            if (selectedDay == null || isSettling) return Offset.Zero
            // 明細到頂後向下 → 展開
            if (available.y > 0f && detailScrollState.value <= 0) {
                val used = applyDragDown(available.y)
                return Offset(0f, used)
            }
            return Offset.Zero
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            if (selectedDay == null) return Velocity.Zero
            if (detailScrollState.value > 0) return Velocity.Zero
            when {
                available.y < -400f -> {
                    // 快速向上
                    if (collapseP < 0.99f) animateCollapseTo(1f)
                    else animateChromeTo(1f)
                }
                available.y > 400f -> {
                    if (chromeP > 0.01f) animateChromeTo(0f)
                    else animateCollapseTo(0f)
                }
                else -> snapAfterDrag()
            }
            return available
        }
    }

    BoxWithConstraints(Modifier.fillMaxSize().background(SURFACE_BG)) {
        val containerHeightPx = constraints.maxHeight.toFloat()
        val bottomLimitPx = containerHeightPx - fabTopPaddingPx

        Column(Modifier.fillMaxSize()) {
            // ===== 月份導航 =====
            Box(Modifier.fillMaxWidth().background(SURFACE_BG).zIndex(30f).padding(horizontal = 10.dp, vertical = 10.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        onClick = { monthDirection = -1; onMonthChange(shiftMonthKey(currentMonthKey, -1)) },
                        modifier = Modifier.size(36.dp), shape = CircleShape, color = SURFACE_CARD, border = BorderStroke(0.5.dp, DIVIDER_COLOR)
                    ) { Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.ChevronLeft, "上個月", tint = Color.Black, modifier = Modifier.size(22.dp)) } }
                    Spacer(Modifier.width(10.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            if (currentMonthKey != todayKey) {
                                monthDirection = if (todayKey > currentMonthKey) 1 else -1
                                onMonthChange(todayKey)
                            }
                        }
                    ) {
                        Text("${year}年", fontSize = 11.sp, lineHeight = 12.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                        AnimatedContent(
                            targetState = currentMonthKey,
                            transitionSpec = {
                                val forward = monthDirection > 0
                                if (forward) {
                                    (slideInHorizontally(tween(280)) { it } + fadeIn(tween(200)))
                                        .togetherWith(slideOutHorizontally(tween(280)) { -it } + fadeOut(tween(180)))
                                } else {
                                    (slideInHorizontally(tween(280)) { -it } + fadeIn(tween(200)))
                                        .togetherWith(slideOutHorizontally(tween(280)) { it } + fadeOut(tween(180)))
                                }
                            },
                            label = "monthAnim"
                        ) { key ->
                            val m = key.split("-").getOrNull(1)?.toIntOrNull() ?: month
                            Text("${m}月", fontSize = 28.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold, color = TEXT_PRIMARY)
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Surface(
                        onClick = { monthDirection = 1; onMonthChange(shiftMonthKey(currentMonthKey, 1)) },
                        modifier = Modifier.size(36.dp), shape = CircleShape, color = SURFACE_CARD, border = BorderStroke(0.5.dp, DIVIDER_COLOR)
                    ) { Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.ChevronRight, "下個月", tint = Color.Black, modifier = Modifier.size(22.dp)) } }
                    Spacer(Modifier.weight(1f))
                    SegmentedModeControl(displayMode = displayMode, onModeChange = { displayMode = it })
                }
            }

            // ===== 統計區：只喺階段2（chromeP）先跟手推走；階段1 保持不動 =====
            val chrome = if (selectedDay != null) chromeP.coerceIn(0f, 1f) else 0f
            val topTranslatePx = topSectionHeightPx * chrome
            val topVisibleH = if (topSectionHeightPx <= 0f) -1f
            else (topSectionHeightPx - topTranslatePx).coerceAtLeast(0f)

            Box(
                Modifier
                    .fillMaxWidth()
                    .then(
                        if (topVisibleH < 0f) Modifier.wrapContentHeight()
                        else Modifier
                            .height(with(density) { topVisibleH.toDp() })
                            .clipToBounds()
                    )
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .then(
                            if (topSectionHeightPx > 0f)
                                Modifier.requiredHeight(with(density) { topSectionHeightPx.toDp() })
                            else Modifier
                        )
                        .onSizeChanged { sz ->
                            if (sz.height > topSectionHeightPx + 1f) {
                                topSectionHeightPx = sz.height.toFloat()
                            }
                        }
                        .offset {
                            IntOffset(0, if (topSectionHeightPx > 0f) (-topTranslatePx).roundToInt() else 0)
                        }
                        .padding(horizontal = 10.dp)
                ) {
                    Spacer(Modifier.height(4.dp))
                    AnimatedCalendarCategoryChips(
                        visibleCategories = visibleCategories,
                        filterCategory = filterCategory,
                        onFilterCategoryChange = onFilterCategoryChange
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AnimatedInfoChip(label = "總計", valueText = "$${formatAmountNoDecimal(totalNet)}", modifier = Modifier.weight(1f), valueFontSize = 16.sp, valueColor = if (totalNet >= 0) COLOR_INCOME else COLOR_EXPENSE)
                        AnimatedInfoChip(label = "平均每項", valueText = "$${formatAmountNoDecimal(avgPerItem)}", modifier = Modifier.weight(1f), valueFontSize = 16.sp)
                        AnimatedInfoChip(label = "日均支出", valueText = "$${formatAmountNoDecimal(avgPerDay)}", modifier = Modifier.weight(1f), valueFontSize = 16.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            // ===== 日曆 + 當日明細（滑動以明細為主：只改日曆可視窗）=====
            Box(Modifier.fillMaxWidth().weight(1f).clipToBounds()) {
                val p = if (selectedDay != null) collapseP.coerceIn(0f, 1f) else 0f
                // ★ 用日期反算行，唔依賴可能過期嘅 selectedRowIdx
                val selRow = if (selectedDay != null) {
                    ((firstDayOffset + selectedDay!! - 1) / 7).coerceIn(0, (totalRows - 1).coerceAtLeast(0))
                } else {
                    0
                }
                val selRowH = rowHeightsPx.getOrElse(selRow) { 0f }
                val collapseRange = (fullGridHeightPx - selRowH).coerceAtLeast(0f)
                // 可視高度：全日曆 → 只剩選中行
                val visibleGridH = if (selectedDay != null) {
                    (fullGridHeightPx - collapseRange * p).coerceAtLeast(selRowH)
                } else {
                    fullGridHeightPx
                }
                // 上移量：令選中行頂部對齊可視窗頂部
                val gridScroll = if (selectedDay != null && selRow in rowOffsetsPx.indices) {
                    rowOffsetsPx[selRow] * p
                } else 0f

                Column(Modifier.fillMaxSize()) {
                    // 星期標題
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(headerHeightDp)
                            .background(SURFACE_BG)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 13.dp)
                                .fillMaxHeight(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CALENDAR_WEEKDAY_HEADERS.forEach { w ->
                                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = w,
                                        fontSize = 12.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.SemiBold,
                                        style = TextStyle(
                                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // 日曆：完整格仔 + clip 遮擋；unbounded 量度避免被父層壓扁
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .height(with(density) { visibleGridH.toDp().coerceAtLeast(0.dp) })
                            .clipToBounds()
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(align = Alignment.Top, unbounded = true)
                                .offset { IntOffset(0, (-gridScroll).roundToInt()) }
                        ) {
                            for (rowIdx in 0 until totalRows) {
                                val isSel = selectedDay != null && rowIdx == selRow
                                CalendarRow(
                                    rowIdx = rowIdx,
                                    firstDayOffset = firstDayOffset,
                                    daysInMonth = daysInMonth,
                                    today = today,
                                    isCurrentMonth = isCurrentMonth,
                                    recordsByDay = recordsByDay,
                                    displayMode = displayMode,
                                    maxAbsNet = maxAbsNet,
                                    selectedDay = selectedDay,
                                    selectedCol = if (isSel) selectedColIdx else null,
                                    animateValues = animateValues,
                                    heightFraction = 1f,
                                    onCellClick = { day, col ->
                                        val rowOfDay = ((firstDayOffset + day - 1) / 7)
                                            .coerceIn(0, (totalRows - 1).coerceAtLeast(0))
                                        if (selectedDay == day) {
                                            when {
                                                chromeP > 0.05f -> animateChromeTo(0f)
                                                collapseP < 0.95f -> animateCollapseTo(1f)
                                                else -> animateCollapseTo(0f)
                                            }
                                        } else {
                                            selectedDay = day
                                            selectedRowIdx = rowOfDay
                                            selectedColIdx = col
                                            cachedSelectedDay = day
                                            cachedSelectedRowIdx = rowOfDay
                                            cachedSelectedCol = col
                                            detailExpandedId = null
                                            chromeP = 0f
                                            scope.launch { detailScrollState.scrollTo(0) }
                                            animateCollapseTo(1f)
                                        }
                                    }
                                )
                                if (rowIdx < totalRows - 1) {
                                    Spacer(Modifier.height(6.dp))
                                }
                            }
                        }
                    }

                    // 當日明細：永遠喺日曆下面；底部預留 FAB + 導航高度，唔會被 FAB 遮住
                    if (selectedDay != null) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                // FAB 喺 BottomEnd：nav + FAB(56) + 間距，明細最底唔超過 FAB 頂
                                .padding(bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 56.dp + 20.dp)
                        ) {
                            TrianglePointer(
                                colIndex = cachedSelectedCol,
                                color = SURFACE_CARD,
                                triangleHeight = triangleHeightDp
                            )
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(horizontal = 10.dp)
                                    .padding(bottom = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SURFACE_CARD)
                            ) {
                                Column(Modifier.fillMaxSize()) {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .height(handleBarHeightDp)
                                            .pointerInput(selRow) {
                                                detectVerticalDragGestures(
                                                    onDragEnd = { snapAfterDrag() },
                                                    onVerticalDrag = { change, dragAmount ->
                                                        change.consume()
                                                        if (isSettling) return@detectVerticalDragGestures
                                                        // dragAmount>0 向下；<0 向上
                                                        if (dragAmount < 0f) applyDragUp(-dragAmount)
                                                        else applyDragDown(dragAmount)
                                                    }
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            Modifier
                                                .width(36.dp)
                                                .height(4.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(TEXT_TERTIARY.copy(alpha = 0.45f))
                                        )
                                    }
                                    Column(
                                        Modifier
                                            .fillMaxSize()
                                            .nestedScroll(detailNestedConnection)
                                            .verticalScroll(detailScrollState)
                                    ) {
                                        DayDetailPanel(
                                            dateKey = "$year-${pad2(month)}-${pad2(cachedSelectedDay)}",
                                            records = recordsByDay[cachedSelectedDay] ?: emptyList(),
                                            expandedId = detailExpandedId,
                                            onExpandChange = { detailExpandedId = it },
                                            onCopy = onCopyClick,
                                            onEdit = onEditClick,
                                            onFilter = onFilterClick,
                                            onDelete = onDeleteClick,
                                            onChangeIcon = onChangeIconClick,
                                            // 交叉：退回階段2再階段1，保留選取同明細
                                            onDismiss = {
                                                scope.launch {
                                                    isSettling = true
                                                    try {
                                                        if (chromeP > 0.01f) {
                                                            Animatable(chromeP).animateTo(0f, spring(dampingRatio = 0.9f, stiffness = 380f)) { chromeP = value }
                                                        }
                                                        chromeP = 0f
                                                        Animatable(collapseP).animateTo(0f, spring(dampingRatio = 0.9f, stiffness = 380f)) { collapseP = value }
                                                    } finally {
                                                        isSettling = false
                                                    }
                                                }
                                            },
                                            deletingIds = deletingIds,
                                            justAddedIds = justAddedIds,
                                            onFanMenuStart = handleFanStartCal,
                                            onFanMenuDrag = handleFanDragCal,
                                            onFanMenuEnd = handleFanEndCal,
                                            activeFanRecord = activeFanRecord
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        FanMenuOverlay(
            progressProvider = { fanMenuProgressCal.value },
            centerProvider = { fanMenuCenterState.value },
            touchProvider = { fanMenuTouchState.value }
        )

        FloatingActionButton(
            onClick = onAddClick, shape = CircleShape, containerColor = BRAND_PRIMARY, contentColor = Color.White,
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 12.dp)
        ) { Icon(Icons.Default.Add, "新增") }
    }
}

@Composable
fun TrianglePointer(
    colIndex: Int, color: Color = SURFACE_CARD,
    triangleWidth: Dp = 22.dp, triangleHeight: Dp = 11.dp,
) {
    // ★ 增加 SURFACE_BG 底色：完美填補並遮擋 Z 軸後方嘅下一行日期，視覺上三角喺格仔正下方彈出
    BoxWithConstraints(Modifier.fillMaxWidth().height(triangleHeight).background(SURFACE_BG)) {
        val gridPad = 10.dp
        val rowPad = 3.dp
        val spacing = 6.dp
        val cellWidth = (maxWidth - gridPad * 2 - rowPad * 2 - spacing * 6) / 7
        val targetX = gridPad + rowPad + (cellWidth + spacing) * colIndex + cellWidth / 2 - triangleWidth / 2

        val animatedX by animateDpAsState(
            targetValue = targetX,
            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 300f),
            label = "triangleX"
        )
        Canvas(Modifier.offset(x = animatedX).size(triangleWidth, triangleHeight)) {
            val path = Path().apply {
                moveTo(size.width / 2f, 0f) // 頂點朝上，剛好對準上面嘅 Row 底部
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path, color)
        }
    }
}

private fun Float?.orZero(): Float = this ?: 0f

@Composable
fun SwipeableRecordItem(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SURFACE_CARD,
    record: Record,
    expandedId: String? = null,
    isExpanded: Boolean = expandedId == record.id,
    hasAnyExpanded: Boolean = expandedId != null,
    onExpand: (String?) -> Unit,
    onCopy: (Record) -> Unit,
    onEdit: (Record) -> Unit,
    onFilter: (Record) -> Unit,
    onDelete: (Record) -> Unit,
    onChangeIcon: (Record) -> Unit,
    hideCategory: Boolean = false,
    /** false = 月曆等頁面唔支援左右滑 */
    enableSwipe: Boolean = true,
    /** 長按優先：例如月曆開當日 pie；null 則用扇形選單 */
    onLongPressOverride: ((Record) -> Unit)? = null,
    onFanMenuStart: (Record, Offset) -> Unit = { _, _ -> },
    onFanMenuDrag: (Offset) -> Unit = {},
    onFanMenuEnd: () -> Unit = {},
    isFanMenuActive: Boolean = false,
    isOtherItem: Boolean = false,
    isActiveItem: Boolean = false
) {
    val density = LocalDensity.current
    val bw = 56.dp; val bh = 44.dp; val gap = 6.dp
    val bwPx = remember(density) { with(density) { bw.toPx() } }
    val gapPx = remember(density) { with(density) { gap.toPx() } }
    val edgePx = remember(density) { with(density) { 8.dp.toPx() } }
    val leftTotal = bwPx * 4 + gapPx * 3
    val maxLeft = -(leftTotal + edgePx)
    val maxRight = bwPx + edgePx

    var targetOffset by remember { mutableFloatStateOf(0f) }
    var startOffset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val layoutCoordsHolder = remember { arrayOf<androidx.compose.ui.layout.LayoutCoordinates?>(null) }

    val animatedOffset by animateFloatAsState(
        targetValue = targetOffset,
        animationSpec = if (isDragging) snap() else SWIPE_SPRING,
        label = "swipeOffset"
    )

    val offsetX = if (isDragging) targetOffset else animatedOffset
    val needsLayer = isFanMenuActive || isActiveItem || isOtherItem

    val itemAlpha by animateFloatAsState(
        targetValue = if (isOtherItem) 0.35f else 1f,
        animationSpec = tween(300),
        label = "itemAlpha"
    )
    val itemScale by animateFloatAsState(
        targetValue = when {
            isActiveItem -> 1.03f
            isOtherItem -> 0.95f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow),
        label = "itemScale"
    )

    LaunchedEffect(isExpanded) { if (!isExpanded && targetOffset != 0f) targetOffset = 0f }

    val leftProgress = if (maxLeft == 0f) 0f else (offsetX / maxLeft).coerceIn(0f, 1f)
    val rightProgress = if (maxRight == 0f) 0f else (offsetX / maxRight).coerceIn(0f, 1f)

    val metaText = remember(record.category, record.timestamp, hideCategory) {
        if (hideCategory) formatRecordTime(record.timestamp)
        else "${record.category}．${formatRecordTime(record.timestamp)}"
    }
    val amtText = remember(record.amount, record.category) { displayAmount(record) }
    val amtColor = remember(record.category) { amountColor(record.category) }
    val headlineText = remember(record.note) { record.note.ifBlank { "(無名稱)" } }

    val latestRecord by rememberUpdatedState(record)
    val latestOnExpand by rememberUpdatedState(onExpand)
    val latestOnCopy by rememberUpdatedState(onCopy)
    val latestOnEdit by rememberUpdatedState(onEdit)
    val latestOnFilter by rememberUpdatedState(onFilter)
    val latestOnDelete by rememberUpdatedState(onDelete)
    val latestOnChangeIcon by rememberUpdatedState(onChangeIcon)
    val latestOnFanStart by rememberUpdatedState(onFanMenuStart)
    val latestOnFanDrag by rememberUpdatedState(onFanMenuDrag)
    val latestOnFanEnd by rememberUpdatedState(onFanMenuEnd)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .zIndex(if (isActiveItem) 10f else 0f)
            .then(
                if (needsLayer) Modifier.graphicsLayer {
                    scaleX = itemScale; scaleY = itemScale; alpha = itemAlpha
                } else Modifier
            )
            .background(backgroundColor)
            .onPlaced { coords -> layoutCoordsHolder[0] = coords }
            .then(
                if (enableSwipe) {
                    Modifier.pointerInput(record.id) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                isDragging = true
                                startOffset = targetOffset
                                latestOnExpand(latestRecord.id)
                            },
                            onDragEnd = {
                                isDragging = false
                                val dist = targetOffset - startOffset
                                val hasIcon = latestRecord.iconUrl.isNotBlank()
                                var shouldTriggerIconAction = false
                                val newOffset = when {
                                    startOffset == maxLeft && dist > 15f -> 0f
                                    startOffset == maxRight && dist < -15f -> 0f
                                    targetOffset < maxLeft * 0.65f -> maxLeft
                                    targetOffset > maxRight * 0.65f -> {
                                        if (hasIcon) maxRight
                                        else { shouldTriggerIconAction = true; 0f }
                                    }
                                    else -> 0f
                                }
                                targetOffset = newOffset
                                if (newOffset == 0f) latestOnExpand(null)
                                if (shouldTriggerIconAction) latestOnChangeIcon(latestRecord)
                            },
                            onDragCancel = { isDragging = false; targetOffset = 0f; latestOnExpand(null) },
                            onHorizontalDrag = { c, d ->
                                c.consume()
                                val dampFactor = when {
                                    targetOffset < maxLeft && d < 0 -> 0.35f
                                    targetOffset > maxRight && d > 0 -> 0.35f
                                    else -> 1f
                                }
                                targetOffset += d * dampFactor
                            })
                    }
                } else Modifier
            )
            .pointerInput("longPress_${record.id}", onLongPressOverride) {
                if (onLongPressOverride != null) {
                    detectTapGestures(
                        onLongPress = { onLongPressOverride.invoke(latestRecord) }
                    )
                } else {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { localOffset ->
                            val globalTopLeft = layoutCoordsHolder[0]?.boundsInRoot()?.topLeft ?: Offset.Zero
                            latestOnFanStart(latestRecord, globalTopLeft + localOffset)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            latestOnFanDrag(dragAmount)
                        },
                        onDragEnd = { latestOnFanEnd() },
                        onDragCancel = { latestOnFanEnd() }
                    )
                }
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { if (hasAnyExpanded) onExpand(null) }
    ) {
        if (leftProgress > 0.005f) {
            Row(
                Modifier.matchParentSize()
                    .offset {
                        val overscroll = if (offsetX < maxLeft) (offsetX - maxLeft).roundToInt() else 0
                        IntOffset(overscroll, 0)
                    }
                    .padding(end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(gap, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedActionButton(Icons.Default.ContentCopy, "複製",
                    iconTint = Color(0xFF64748B), width = bw, height = bh,
                    progress = leftProgress, delay = 0f
                ) { targetOffset = 0f; onExpand(null); onCopy(record) }

                AnimatedActionButton(Icons.Default.Edit, "編輯",
                    iconTint = Color(0xFF3B82F6), width = bw, height = bh,
                    progress = leftProgress, delay = 0.12f
                ) { targetOffset = 0f; onExpand(null); onEdit(record) }

                AnimatedActionButton(Icons.Default.FilterList, "篩選",
                    iconTint = Color(0xFF8B5CF6), width = bw, height = bh,
                    progress = leftProgress, delay = 0.24f
                ) { targetOffset = 0f; onExpand(null); onFilter(record) }

                AnimatedActionButton(Icons.Default.Delete, "刪除",
                    iconTint = COLOR_EXPENSE, width = bw, height = bh,
                    progress = leftProgress, delay = 0.36f
                ) { targetOffset = 0f; onExpand(null); onDelete(record) }
            }
        }

        if (rightProgress > 0.005f) {
            Row(
                Modifier.matchParentSize()
                    .offset {
                        val overscroll = if (offsetX > maxRight) (offsetX - maxRight).roundToInt() else 0
                        IntOffset(overscroll, 0)
                    }
                    .padding(start = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(gap, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedActionButton(Icons.Default.Image, "改圖標",
                    iconTint = Color(0xFF10B981), width = bw, height = bh,
                    progress = rightProgress, delay = 0f
                ) { targetOffset = 0f; onExpand(null); onChangeIcon(record) }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .background(backgroundColor)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconView(record.iconUrl, record.note)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(headlineText, fontSize = NOTE_FONT_SIZE,
                    fontWeight = FontWeight.SemiBold, color = TEXT_PRIMARY,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Text(metaText, fontSize = META_FONT_SIZE, color = TEXT_SECONDARY,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.width(8.dp))
            Text(amtText, color = amtColor, fontSize = AMOUNT_FONT_SIZE, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun AnimatedActionButton(
    icon: ImageVector, label: String, iconTint: Color,
    width: Dp, height: Dp, progress: Float, delay: Float,
    onClick: () -> Unit,
) {
    if (progress < 0.01f) {
        Spacer(Modifier.width(width).height(height))
        return
    }
    val p = ((progress - delay) / (1f - delay).coerceAtLeast(0.001f)).coerceIn(0f, 1f)
    val alpha = (p * 1.4f).coerceIn(0f, 1f)
    val scale = 0.6f + 0.4f * p

    Box(
        Modifier
            .width(width).height(height)
            .graphicsLayer { this.alpha = alpha; scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(14.dp))
            .background(iconTint.copy(alpha = 0.12f))
            .clickable(enabled = progress > 0.2f) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, label, tint = iconTint, modifier = Modifier.size(24.dp))
    }
}

@Composable
fun DayDetailPanel(
    dateKey: String, records: List<Record>,
    expandedId: String?, onExpandChange: (String?) -> Unit,
    onCopy: (Record) -> Unit, onEdit: (Record) -> Unit,
    onFilter: (Record) -> Unit, onDelete: (Record) -> Unit,
    onChangeIcon: (Record) -> Unit, onDismiss: () -> Unit,
    deletingIds: Set<String> = emptySet(),
    justAddedIds: Set<String> = emptySet(),
    onFanMenuStart: (Record, Offset) -> Unit = { _, _ -> },
    onFanMenuDrag: (Offset) -> Unit = {},
    onFanMenuEnd: () -> Unit = {},
    activeFanRecord: Record? = null
) {
    val (income, expense, sortedRecords) = remember(records) {
        var inc = 0.0
        var exp = 0.0
        for (r in records) {
            if (r.category == INCOME_CATEGORY) inc += r.amount else exp += r.amount
        }
        Triple(inc, exp, records.sortedByDescending { it.timestamp })
    }
    val headerText = remember(dateKey) { formatDateHeader(dateKey) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SURFACE_CARD)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(headerText, fontSize = 14.sp,
                    fontWeight = FontWeight.Bold, color = TEXT_PRIMARY)
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (income > 0) {
                        Text("收 $${formatAmountNoDecimal(income)}", fontSize = 12.sp,
                            color = COLOR_INCOME, fontWeight = FontWeight.Bold)
                    }
                    if (income > 0 && expense > 0) Spacer(Modifier.width(10.dp))
                    if (expense > 0) {
                        Text("支 $${formatAmountNoDecimal(expense)}", fontSize = 12.sp,
                            color = COLOR_EXPENSE, fontWeight = FontWeight.Bold)
                    }
                }
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, "關閉", tint = TEXT_SECONDARY)
            }
        }
        HorizontalDivider(color = DIVIDER_COLOR, thickness = 0.5.dp)

        val isFanActive = activeFanRecord != null
        val activeFanId = activeFanRecord?.id

        sortedRecords.forEachIndexed { idx, r ->
            key(r.id) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AnimatedRecordItem(
                        animateOnMount = r.id in justAddedIds,
                        isDeleting = r.id in deletingIds
                    ) {
                        SwipeableRecordItem(
                            backgroundColor = SURFACE_CARD,
                            record = r,
                            isExpanded = expandedId == r.id,
                            hasAnyExpanded = expandedId != null,
                            onExpand = onExpandChange,
                            onCopy = { onCopy(r) },
                            onEdit = { onEdit(r) },
                            onFilter = { onFilter(r) },
                            onDelete = { onDelete(r) },
                            onChangeIcon = { onChangeIcon(r) },
                            enableSwipe = false,
                            onFanMenuStart = { _, offset -> onFanMenuStart(r, offset) },
                            onFanMenuDrag = onFanMenuDrag,
                            onFanMenuEnd = onFanMenuEnd,
                            isFanMenuActive = isFanActive,
                            isOtherItem = isFanActive && activeFanId != r.id,
                            isActiveItem = activeFanId == r.id
                        )
                    }
                    if (idx < sortedRecords.lastIndex && r.id !in deletingIds) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 68.dp, end = 16.dp),
                            color = DIVIDER_COLOR,
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(6.dp))
    }
}

private val KEYBOARD_ROWS = listOf(
    listOf("1", "2", "3"), listOf("4", "5", "6"),
    listOf("7", "8", "9"), listOf(".", "0", "backspace")
)
private val CHUNKED_CATEGORIES = CATEGORIES.chunked(2)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LedgerKeyboardPanel(
    state: KeyboardState, onStateChange: (KeyboardState) -> Unit,
    onDismiss: () -> Unit, onConfirm: () -> Unit, onNext: () -> Unit,
    allNoteNames: List<String>, noteCategoryMap: Map<String, String>,
    noteIconMap: Map<String, String> = emptyMap(),
    nameFlashTrigger: Int, showKeyboard: Boolean = true,
    imeOpen: Boolean = false,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    var noteFieldFocused by remember { mutableStateOf(false) }

    // 🟢 已固定為 34.dp
    val panelShape = RoundedCornerShape(34.dp)

    LaunchedEffect(state.noteText) {
        if (state.noteText.isNotBlank()) {
            val lastCat = noteCategoryMap[state.noteText]
            if (lastCat != null && lastCat != state.category) {
                onStateChange(state.copy(category = lastCat))
            }
        }
    }

    LaunchedEffect(state.focusedField) {
        if (state.focusedField == "amount" || state.focusedField == "rate") {
            noteFieldFocused = false
            focusManager.clearFocus(force = true)
        }
    }

    LaunchedEffect(imeOpen) {
        if (!imeOpen) {
            noteFieldFocused = false
            focusManager.clearFocus(force = true)
            if (state.editingNote || state.focusedField == "note") {
                onStateChange(state.copy(focusedField = "amount", editingNote = false))
            }
        }
    }

    val panelScrollState = rememberScrollState()
    LaunchedEffect(state.editingNote, state.noteText, noteFieldFocused) {
        if (state.editingNote || noteFieldFocused) {
            kotlinx.coroutines.delay(80)
            panelScrollState.animateScrollTo(panelScrollState.maxValue)
        }
    }

    Box(
        modifier = modifier
            .clip(panelShape)
            .background(SURFACE_CARD)
            .pointerInput(Unit) { detectTapGestures { } }
    ) {
        Column(
            Modifier.fillMaxWidth()
                .verticalScroll(panelScrollState)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
        ) {
            val dateText = remember(state.timestamp) { formatDatePart(state.timestamp) }
            val timeText = remember(state.timestamp) { formatTimePart(state.timestamp) }

            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1.4f).fillMaxHeight()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val cal = Calendar.getInstance().apply { timeInMillis = state.timestamp }
                            DatePickerDialog(ctx, { _, y, m, d ->
                                cal.set(Calendar.YEAR, y)
                                cal.set(Calendar.MONTH, m)
                                cal.set(Calendar.DAY_OF_MONTH, d)
                                onStateChange(state.copy(timestamp = cal.timeInMillis))
                            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)).show()
                        },
                    shape = RoundedCornerShape(14.dp),
                    color = SURFACE_ELEVATED
                ) {
                    Row(Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Event, null, tint = BRAND_PRIMARY, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(dateText, fontSize = 14.sp,
                            color = TEXT_PRIMARY, fontWeight = FontWeight.Medium,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f).fillMaxHeight()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val cal = Calendar.getInstance().apply { timeInMillis = state.timestamp }
                            TimePickerDialog(ctx, { _, h, mi ->
                                cal.set(Calendar.HOUR_OF_DAY, h)
                                cal.set(Calendar.MINUTE, mi)
                                cal.set(Calendar.SECOND, 0)
                                cal.set(Calendar.MILLISECOND, 0)
                                onStateChange(state.copy(timestamp = cal.timeInMillis))
                            }, cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE), true).show()
                        },
                    shape = RoundedCornerShape(14.dp),
                    color = SURFACE_ELEVATED
                ) {
                    Row(Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, null, tint = BRAND_PRIMARY, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(timeText, fontSize = 14.sp,
                            color = TEXT_PRIMARY, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            val isTransportContext = state.noteText == "地鐵" || state.noteText == "巴士"
            
            androidx.compose.animation.AnimatedVisibility(
                visible = isTransportContext,
                enter = expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
                exit = shrinkVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut()
            ) {
                Column(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("乘車工具", fontSize = 13.sp, color = TEXT_SECONDARY, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.weight(1f))
                        
                        Surface(shape = RoundedCornerShape(12.dp), color = SURFACE_ELEVATED) {
                            Row(Modifier.padding(4.dp)) {
                                val isBus = state.noteText == "巴士"
                                
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (!isBus) BRAND_PRIMARY else Color.Transparent)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { 
                                            onStateChange(state.copy(noteText = "地鐵", category = "交通", focusedField = "amount")) 
                                        }
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    Text("🚇 地鐵", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                        color = if (!isBus) Color.White else TEXT_SECONDARY)
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isBus) BRAND_PRIMARY else Color.Transparent)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { 
                                            onStateChange(state.copy(noteText = "巴士", category = "交通", focusedField = "amount")) 
                                        }
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    Text("🚌 巴士", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                        color = if (isBus) Color.White else TEXT_SECONDARY)
                                }
                            }
                        }
                    }
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = state.category == "旅遊",
                enter = expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
                exit = shrinkVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut()
            ) {
                Column(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(12.dp), color = SURFACE_ELEVATED) {
                            Row(Modifier.padding(4.dp)) {
                                val isHKD = state.currency == "HKD"
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isHKD) BRAND_PRIMARY else Color.Transparent)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { onStateChange(state.copy(currency = "HKD", focusedField = "amount")) }
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    Text("HKD", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                        color = if (isHKD) Color.White else TEXT_SECONDARY)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (!isHKD) BRAND_PRIMARY else Color.Transparent)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { onStateChange(state.copy(currency = "JPY", focusedField = "amount")) }
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    Text("JPY", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                        color = if (!isHKD) Color.White else TEXT_SECONDARY)
                                }
                            }
                        }

                        androidx.compose.animation.AnimatedVisibility(
                            visible = state.currency == "JPY",
                            enter = fadeIn() + slideInHorizontally { it / 2 },
                            exit = fadeOut() + slideOutHorizontally { it / 2 }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("匯率 ", fontSize = 12.sp, color = TEXT_SECONDARY)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (state.focusedField == "rate") BRAND_PRIMARY_LIGHT
                                            else SURFACE_ELEVATED)
                                        .border(1.dp,
                                            if (state.focusedField == "rate") BRAND_PRIMARY else Color.Transparent,
                                            RoundedCornerShape(8.dp))
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { onStateChange(state.copy(focusedField = "rate")) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(state.exchangeRateText.ifEmpty { "0" },
                                        fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                        color = BRAND_PRIMARY)
                                }
                                Spacer(Modifier.width(8.dp))
                                val jpyAmt = state.amountText.toDoubleOrNull() ?: 0.0
                                val rate = state.exchangeRateText.toDoubleOrNull() ?: 0.0
                                val hkd = jpyAmt * rate
                                Text("≈ $${formatAmount(hkd)}", fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold, color = TEXT_PRIMARY)
                            }
                        }
                    }
                }
            }

            Surface(Modifier.fillMaxWidth().height(76.dp),
                shape = RoundedCornerShape(16.dp), color = SURFACE_ELEVATED) {
                Box(
                    Modifier.fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp,
                            if (state.focusedField == "amount" && state.currency == "JPY") BRAND_PRIMARY_LIGHT
                            else Color.Transparent,
                            RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onStateChange(state.copy(focusedField = "amount")) }
                        .padding(start = 22.dp, end = 12.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = if (state.category == "旅遊" && state.currency == "JPY") "¥" else "$",
                        fontSize = 24.sp, color = TEXT_TERTIARY, fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    
                    val showText = if (state.amountText.isEmpty()) "0" else state.amountText
                    val highlightColor = if (state.selectAmountOnInput) Color(0xFFDBEAFE)
                                         else Color.Transparent

                    Box(
                        modifier = Modifier.padding(end = 44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(highlightColor)
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        AnimatedAmount(
                            text = showText,
                            color = if (state.amountText.isEmpty()) TEXT_TERTIARY else TEXT_PRIMARY,
                            fontSize = 38.sp, fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (state.amountText.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { 
                                    onStateChange(state.copy(amountText = "", selectAmountOnInput = false, focusedField = "amount")) 
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "清除金額",
                                tint = TEXT_TERTIARY,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            val noteSuggestions = remember(state.noteText, allNoteNames) {
                filterNoteSuggestions(state.noteText, allNoteNames)
            }

            Box(Modifier.fillMaxWidth().height(190.dp)) {
                androidx.compose.animation.AnimatedContent(
                    targetState = noteFieldFocused,
                    transitionSpec = {
                        (fadeIn(tween(220, delayMillis = 90)) + scaleIn(initialScale = 0.92f, animationSpec = tween(220)))
                            .togetherWith(fadeOut(tween(150)) + scaleOut(targetScale = 0.92f, animationSpec = tween(150)))
                    },
                    label = "keyboard_or_suggestions"
                ) { isNoteFocused ->
                    if (isNoteFocused) {
                        if (noteSuggestions.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (state.noteText.isBlank()) "輸入名稱以搜尋項目" else "搵唔到相關項目",
                                    color = TEXT_SECONDARY,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            // 同快速輸入區：按文字實際闊度排，盡量完整顯示；只用盡頁闊時先 ellipsis
                            BoxWithConstraints(Modifier.fillMaxSize()) {
                                val maxChipWidth = maxWidth
                                val suggestScroll = rememberScrollState()
                                FlowRow(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(suggestScroll)
                                        .padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    noteSuggestions.forEach { s ->
                                        key(s) {
                                            QuickInputChip(
                                                name = s,
                                                iconUrl = noteIconMap[s] ?: "",
                                                modifier = Modifier
                                                    .height(44.dp)
                                                    .widthIn(max = maxChipWidth),
                                                onClick = {
                                                    onStateChange(
                                                        state.copy(
                                                            noteText = s,
                                                            editingNote = false,
                                                            focusedField = "amount"
                                                        )
                                                    )
                                                    focusManager.clearFocus()
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Column(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            KEYBOARD_ROWS.forEach { row ->
                                Row(
                                    Modifier.fillMaxWidth().weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    row.forEach { key ->
                                        KeyboardKey(key, {
                                            val editingRate = state.focusedField == "rate"
                                            if (!editingRate && state.focusedField != "amount") {
                                                onStateChange(state.copy(focusedField = "amount", editingNote = false))
                                            }
                                            when (key) {
                                                "backspace" -> {
                                                    if (!editingRate) {
                                                        val t = state.amountText
                                                        onStateChange(state.copy(
                                                            amountText = if (t.isEmpty()) t else t.dropLast(1),
                                                            selectAmountOnInput = false,
                                                            focusedField = "amount",
                                                            editingNote = false))
                                                    } else {
                                                        val t = state.exchangeRateText
                                                        onStateChange(state.copy(
                                                            exchangeRateText = if (t.isEmpty()) t else t.dropLast(1)))
                                                    }
                                                }
                                                "." -> {
                                                    if (!editingRate) {
                                                        val t = state.amountText
                                                        if (!t.contains("."))
                                                            onStateChange(state.copy(
                                                                amountText = if (t.isEmpty()) "0." else "$t.",
                                                                selectAmountOnInput = false,
                                                                focusedField = "amount",
                                                                editingNote = false))
                                                    } else {
                                                        val t = state.exchangeRateText
                                                        if (!t.contains("."))
                                                            onStateChange(state.copy(
                                                                exchangeRateText = if (t.isEmpty()) "0." else "$t."))
                                                    }
                                                }
                                                else -> {
                                                    if (!editingRate) {
                                                        val base = if (state.selectAmountOnInput) "" else state.amountText
                                                        val dotIdx = base.indexOf(".")
                                                        val newT = if (dotIdx >= 0) {
                                                            if (base.length - dotIdx - 1 >= 2 && state.currency != "JPY") base
                                                            else "$base$key"
                                                        } else {
                                                            if (base.length >= 9) base else "$base$key"
                                                        }
                                                        onStateChange(state.copy(
                                                            amountText = newT,
                                                            selectAmountOnInput = false,
                                                            focusedField = "amount",
                                                            editingNote = false))
                                                    } else {
                                                        val t = state.exchangeRateText
                                                        onStateChange(state.copy(exchangeRateText = "$t$key"))
                                                    }
                                                }
                                            }
                                        }, Modifier.weight(1f).fillMaxHeight())
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(Modifier.fillMaxWidth().height(64.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically) {

                val glowAlpha = remember { Animatable(0f) }
                LaunchedEffect(nameFlashTrigger) {
                    if (nameFlashTrigger > 0) {
                        try {
                            glowAlpha.snapTo(0f)
                            glowAlpha.animateTo(1f, tween(80))
                            glowAlpha.animateTo(0f, tween(420))
                        } catch (_: Exception) {}
                    }
                }
                val glowShape = RoundedCornerShape(14.dp)

                Box(
                    Modifier.weight(1f).fillMaxHeight()
                        .clip(glowShape)
                        .drawWithContent {
                            drawContent()
                            val a = glowAlpha.value
                            if (a > 0.01f) {
                                val radius = CornerRadius(14.dp.toPx())
                                drawRoundRect(color = BRAND_PRIMARY_LIGHT.copy(alpha = a * 0.25f),
                                    cornerRadius = radius)
                                drawRoundRect(color = BRAND_PRIMARY.copy(alpha = a),
                                    cornerRadius = radius,
                                    style = Stroke(width = 2.5.dp.toPx()))
                            }
                        }
                ) {
                    if (state.editingNote || state.noteText.isNotBlank()) {
                        var tfValue by remember {
                            mutableStateOf(TextFieldValue(text = state.noteText,
                                selection = TextRange(0, state.noteText.length)))
                        }
                        val focusReq = remember { FocusRequester() }
                        val keyboardController = LocalSoftwareKeyboardController.current

                        LaunchedEffect(state.editingNote) {
                            if (state.editingNote) {
                                focusReq.requestFocus()
                            }
                        }
                        LaunchedEffect(state.noteText) {
                            if (state.noteText != tfValue.text) {
                                tfValue = TextFieldValue(
                                    text = state.noteText,
                                    selection = TextRange(0, state.noteText.length)
                                )
                            }
                        }

                        TextField(
                            value = tfValue,
                            onValueChange = { nv ->
                                tfValue = nv
                                onStateChange(state.copy(noteText = nv.text, editingNote = true))
                            },
                            placeholder = { Text("名稱") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = SURFACE_ELEVATED,
                                unfocusedContainerColor = SURFACE_ELEVATED,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            trailingIcon = {
                                if (state.noteText.isNotEmpty()) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "清除",
                                        tint = TEXT_SECONDARY,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                tfValue = TextFieldValue("")
                                                onStateChange(state.copy(noteText = "", editingNote = true))
                                                focusReq.requestFocus()
                                                keyboardController?.show()
                                            }
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { onConfirm() }),
                            modifier = Modifier
                                .fillMaxSize()
                                .focusRequester(focusReq)
                                .onFocusChanged { fi ->
                                    noteFieldFocused = fi.isFocused
                                    if (fi.isFocused) {
                                        onStateChange(state.copy(editingNote = true, focusedField = "note"))
                                    }
                                }
                        )

                    } else {
                        Button(
                            onClick = { onStateChange(state.copy(editingNote = true)) },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SURFACE_ELEVATED, contentColor = TEXT_PRIMARY)
                        ) {
                            Text(
                                if (state.noteText.isBlank()) "輸入名稱" else state.noteText,
                                maxLines = 1, fontSize = 15.sp,
                                color = if (state.noteText.isBlank()) TEXT_TERTIARY else TEXT_PRIMARY
                            )
                        }
                    }
                }

                val currentStyle = CATEGORY_STYLES[state.category]
                val menuVisibleState = remember { MutableTransitionState(false) }
                Box {
                    Button(
                        onClick = { menuVisibleState.targetState = true },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.width(110.dp).fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SURFACE_ELEVATED, contentColor = TEXT_PRIMARY)
                    ) {
                        if (currentStyle != null) {
                            Icon(currentStyle.icon, null, tint = currentStyle.fgColor,
                                modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                        }
                        Text(state.category, maxLines = 1, fontSize = 14.sp)
                    }

                    if (menuVisibleState.currentState || menuVisibleState.targetState) {
                        androidx.compose.ui.window.Popup(
                            popupPositionProvider = CategoryMenuPositionProvider,
                            onDismissRequest = { menuVisibleState.targetState = false },
                            properties = PopupProperties(focusable = true)
                        ) {
                            val staggerMs = 25
                            val rowEnterDur = 200

                            androidx.compose.animation.AnimatedVisibility(
                                visibleState = menuVisibleState,
                                enter = fadeIn(tween(100, easing = LinearEasing)) +
                                        scaleIn(initialScale = 0.92f, animationSpec = tween(220, easing = LinearOutSlowInEasing)),
                                exit = fadeOut(tween(150, easing = FastOutLinearInEasing)) +
                                       scaleOut(targetScale = 0.9f, animationSpec = tween(150, easing = FastOutLinearInEasing))
                            ) {
                                var isReady by remember { mutableStateOf(false) }
                                LaunchedEffect(menuVisibleState.targetState) {
                                    isReady = menuVisibleState.targetState
                                }

                                Column(
                                    modifier = Modifier
                                        .width(288.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(SURFACE_CARD)
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CHUNKED_CATEGORIES.forEachIndexed { rowIdx, pair ->
                                        key(rowIdx) {
                                            val enterDelay = rowIdx * staggerMs

                                            val rowAlpha by animateFloatAsState(
                                                targetValue = if (isReady) 1f else 0f,
                                                animationSpec = if (isReady) {
                                                    tween(rowEnterDur, enterDelay, LinearOutSlowInEasing)
                                                } else {
                                                    tween(150, 0, FastOutLinearInEasing)
                                                },
                                                label = "rowAlpha_$rowIdx"
                                            )

                                            val rowScale by animateFloatAsState(
                                                targetValue = if (isReady) 1f else 0.85f,
                                                animationSpec = if (isReady) {
                                                    tween(rowEnterDur, enterDelay, LinearOutSlowInEasing)
                                                } else {
                                                    tween(150, 0, FastOutLinearInEasing)
                                                },
                                                label = "rowScale_$rowIdx"
                                            )

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .graphicsLayer {
                                                        alpha = rowAlpha
                                                        scaleX = rowScale
                                                        scaleY = rowScale
                                                    },
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                pair.forEach { cat ->
                                                    val s = CATEGORY_STYLES[cat]
                                                    val sel = cat == state.category
                                                    val chipShape = RoundedCornerShape(12.dp)

                                                    Row(
                                                        Modifier
                                                            .weight(1f)
                                                            .height(44.dp)
                                                            .clip(chipShape)
                                                            .background(
                                                                if (sel) (s?.fgColor ?: BRAND_PRIMARY)
                                                                else (s?.bgColor ?: SURFACE_ELEVATED)
                                                            )
                                                            .clickable(
                                                                interactionSource = remember { MutableInteractionSource() },
                                                                indication = null
                                                            ) {
                                                                onStateChange(
                                                                    state.copy(
                                                                        category = cat,
                                                                        currency = "HKD",
                                                                        focusedField = "amount"
                                                                    )
                                                                )
                                                                menuVisibleState.targetState = false
                                                            },
                                                        horizontalArrangement = Arrangement.Center,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        if (s != null) {
                                                            Icon(s.icon, null,
                                                                tint = if (sel) Color.White else s.fgColor,
                                                                modifier = Modifier.size(16.dp))
                                                            Spacer(Modifier.width(5.dp))
                                                        }
                                                        Text(cat, fontSize = 13.sp,
                                                            color = if (sel) Color.White
                                                                    else (s?.fgColor ?: TEXT_PRIMARY),
                                                            fontWeight = if (sel) FontWeight.SemiBold
                                                                         else FontWeight.Normal,
                                                            maxLines = 1)
                                                    }
                                                }
                                                if (pair.size == 1) Spacer(Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(Modifier.fillMaxWidth().height(58.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onDismiss, shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SURFACE_ELEVATED, contentColor = TEXT_PRIMARY)
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("收起", fontSize = 15.sp)
                }
                Button(
                    onClick = { if (state.noteText.isBlank()) onNext() else onConfirm() },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(2f).fillMaxHeight(),
                    enabled = state.amountText.isNotEmpty() && state.amountText != "0",
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BRAND_PRIMARY, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(when {
                        state.noteText.isBlank() -> "下一步"
                        state.editingRecordId != null -> "更新"
                        else -> "入帳"
                    }, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun KeyboardKey(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "ks")
    val bg by animateColorAsState(if (pressed) BRAND_PRIMARY_LIGHT else SURFACE_ELEVATED,
        spring(stiffness = Spring.StiffnessMedium), label = "kb")
    Box(modifier.clip(RoundedCornerShape(14.dp)).background(bg)
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .clickable(interactionSource = src, indication = null) { onClick() },
        contentAlignment = Alignment.Center) {
        if (label == "backspace")
            Icon(Icons.Default.Backspace, "退格", modifier = Modifier.size(22.dp), tint = TEXT_SECONDARY)
        else Text(label, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = TEXT_PRIMARY)
    }
}
@Composable
fun AnimatedFilterChip(
    selected: Boolean, label: String, onClick: () -> Unit,
    modifier: Modifier = Modifier, fillWidth: Boolean = false,
) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "cs"
    )
    val bgColor = if (selected) BRAND_PRIMARY else SURFACE_ELEVATED
    val textColor = if (selected) Color.White else Color.Black
    val fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal

    Surface(
        onClick = onClick,
        modifier = modifier.height(34.dp).graphicsLayer { scaleX = scale; scaleY = scale },
        shape = RoundedCornerShape(12.dp), color = bgColor,
        interactionSource = src
    ) {
        Box(
            modifier = if (fillWidth) Modifier.fillMaxWidth() else Modifier.padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label, textAlign = TextAlign.Center, fontSize = 13.sp, maxLines = 1,
                color = textColor, fontWeight = fontWeight,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)))
        }
    }
}

@Composable
fun CalendarFilterChip(
    selected: Boolean, label: String, onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "calendarChipPress"
    )
    val bgColor by animateColorAsState(
        targetValue = if (selected) BRAND_PRIMARY else SURFACE_ELEVATED,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "calendarChipBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color.Black,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "calendarChipText"
    )
    val fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium

    Surface(
        onClick = onClick,
        modifier = modifier
            .height(34.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            },
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        interactionSource = src
    ) {
        Box(Modifier.fillMaxWidth().padding(horizontal = 14.dp), contentAlignment = Alignment.Center) {
            Text(
                label,
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                maxLines = 1,
                color = textColor,
                fontWeight = fontWeight,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
            )
        }
    }
}

@Composable
fun AnimatedCalendarCategoryChips(
    visibleCategories: List<String>,
    filterCategory: String?,
    onFilterCategoryChange: (String?) -> Unit,
) {
    val targetKeys = remember(visibleCategories) { listOf<String?>(null) + visibleCategories }

    // 1. 計算 Grid 需要的動態總高度 (每行 4 個)
    val rowCount = (targetKeys.size + 3) / 4
    // +2.dp 避免第三行 chips 底部被裁少少
    val targetHeight = if (rowCount == 0) 0.dp else (40.dp * rowCount) + (6.dp * (rowCount - 1)) + 2.dp

    val animatedHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f),
        label = "calChipsGridHeight"
    )

    // 2. 使用 LazyVerticalGrid，佢天生就識得處理行列佈局同等分寬度
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxWidth()
            .height(animatedHeight),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        userScrollEnabled = false
    ) {
        items(
            items = targetKeys,
            key = { it ?: "all_categories_key" }
        ) { cat ->

            // 3. 處理新加入 Chip 的縮放與向上滑動進場
            var appear by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { appear = true }

            val scale by animateFloatAsState(
                targetValue = if (appear) 1f else 0.55f,
                animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
                label = "chipScale"
            )
            val offsetY by animateFloatAsState(
                targetValue = if (appear) 0f else 30f,
                animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
                label = "chipOffset"
            )

            Box(
                modifier = Modifier
                    .height(40.dp)
                    // ★ 靈魂所在：呢一句就會令「留低嘅 chips」自動平滑移動補位，並令「消失嘅 chips」優雅淡出
                    .animateItem(
                        fadeInSpec = tween(300, easing = FastOutSlowInEasing),
                        fadeOutSpec = tween(250, easing = FastOutSlowInEasing),
                        placementSpec = spring(dampingRatio = 0.85f, stiffness = 300f)
                    )
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationY = offsetY
                    },
                contentAlignment = Alignment.Center
            ) {
                CalendarFilterChip(
                    modifier = Modifier.fillMaxSize(),
                    selected = filterCategory == cat,
                    label = cat ?: "全部",
                    onClick = {
                        onFilterCategoryChange(if (filterCategory == cat) null else cat)
                    }
                )
            }
        }
    }
}

private val DIGIT_SLIDE_ENTER = spring<IntOffset>(dampingRatio = 0.65f, stiffness = 450f)
private val DIGIT_SLIDE_EXIT = spring<IntOffset>(dampingRatio = 0.7f, stiffness = 400f)
private val DIGIT_FLOAT_ENTER = spring<Float>(dampingRatio = 0.65f, stiffness = 450f)
private val DIGIT_FLOAT_EXIT = spring<Float>(dampingRatio = 0.7f, stiffness = 400f)
private val DIGIT_SIZE_SPRING = spring<IntSize>(dampingRatio = 0.75f, stiffness = 350f)

// ★ 精簡：原 SmartElement 整個函數從未調用，直接刪除

@Composable
fun DayHeader(
    dateKey: String, income: Double, expense: Double,
    listState: LazyListState? = null
    // ★ 精簡：移除 prevDateKey/prevIncome/prevExpense/nextDateKey/nextIncome/nextExpense 六個死參數
) {
    val parsed = parseDateHeader(dateKey)
    val dayStr = if (parsed != null) pad2(parsed.day) else dateKey
    val monthStr = if (parsed != null) "${parsed.month}月" else ""
    val weekStr = if (parsed != null) (if (parsed.dayTag == "今日") "今天" else "週${parsed.weekdayChar}") else ""
    val incStr = if (income > 0) "+$${formatAmountNoDecimal(income)}" else ""
    val expStr = if (expense > 0) "-$${formatAmountNoDecimal(expense)}" else ""
    val isToday = parsed?.dayTag == "今日"

    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(46.dp), contentAlignment = Alignment.CenterStart) {
                Row {
                    dayStr.forEach { char ->
                        Text(
                            text = char.toString(),
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isToday) BRAND_PRIMARY else TEXT_PRIMARY,
                            letterSpacing = (-1).sp,
                            style = TextStyle(fontFeatureSettings = "tnum")
                        )
                    }
                }
            }

            if (monthStr.isNotEmpty()) {
                Spacer(modifier = Modifier.width(4.dp))
                Column(verticalArrangement = Arrangement.Center) {
                    Text(monthStr, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        color = if (isToday) BRAND_PRIMARY else TEXT_SECONDARY)
                    Spacer(Modifier.height(1.dp))
                    Text(weekStr, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                        color = if (isToday) BRAND_PRIMARY else TEXT_SECONDARY)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            if (income > 0) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("收入", fontSize = 10.sp, color = TEXT_SECONDARY, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                    Text(incStr, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = COLOR_INCOME)
                }
            }
            if (expense > 0) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("支出", fontSize = 10.sp, color = TEXT_SECONDARY, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                    Text(expStr, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = COLOR_EXPENSE)
                }
            }
        }
    }
}

@Composable
fun IconSourceOption(icon: ImageVector, label: String, tint: Color = TEXT_PRIMARY, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }.padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(label, color = tint, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun QuickInputChip(
    name: String,
    iconUrl: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val ctx = LocalContext.current
    var extracted by remember(iconUrl) {
        mutableStateOf(if (iconUrl.isBlank()) null else dominantColorCache[iconUrl])
    }

    LaunchedEffect(iconUrl) {
        if (iconUrl.isBlank()) { extracted = null; return@LaunchedEffect }
        dominantColorCache[iconUrl]?.let { extracted = it; return@LaunchedEffect }
        val computed = withContext(Dispatchers.IO) {
            try {
                val request = ImageRequest.Builder(ctx)
                    .data(iconUrl).allowHardware(false).build()
                val result = ctx.imageLoader.execute(request)
                when (val d = result.drawable) {
                    is android.graphics.drawable.BitmapDrawable -> getDominantMutedColor(d.bitmap)
                    is android.graphics.drawable.ColorDrawable -> {
                        val c = d.color
                        if (android.graphics.Color.alpha(c) > 0 &&
                            android.graphics.Color.red(c)   < 240 &&
                            android.graphics.Color.green(c) < 240 &&
                            android.graphics.Color.blue(c)  < 240) {
                            val hsv = FloatArray(3)
                            android.graphics.Color.colorToHSV(c, hsv)
                            hsv[1] = (hsv[1] * 0.42f).coerceAtMost(0.45f)
                            hsv[2] = 0.96f
                            Color(android.graphics.Color.HSVToColor(hsv))
                        } else null
                    }
                    else -> null
                }
            } catch (_: Exception) { null }
        }
        if (computed != null) {
            dominantColorCache[iconUrl] = computed
            extracted = computed
        }
    }

    val bgColor = extracted ?: SURFACE_ELEVATED

    Surface(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (onLongClick != null) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onLongClick() },
                            onTap = { onClick() }
                        )
                    }
                } else {
                    Modifier.clickable { onClick() }
                }
            ),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        shadowElevation = 0.dp
    ) {
        Row(Modifier.padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            IconView(iconUrl, name, size = 18.dp)
            Spacer(Modifier.width(6.dp))
            Text(
                text = name,
                fontSize = 13.sp,
                color = TEXT_PRIMARY,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
    }
}

private data class PlacedChip(
    val name: String, val targetW: Float, val targetX: Float, val targetY: Float
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuickInputSection(
    topNotes: List<Pair<String, Int>>,
    noteIconMap: Map<String, String>,
    noteCategoryMap: Map<String, String>,
    noteUsageCount: Map<String, Int> = emptyMap(),
    maxRowsPerPage: Int = 4,
    skipAppearAnimation: Boolean = false,
    collapseProgress: Float = 0f,
    onExpandClick: () -> Unit = {},
    onClick: (String) -> Unit,
    hiddenQuickNotes: Set<String> = emptySet(),
    onHideNote: (String) -> Unit = {},
    onUnhideNote: (String) -> Unit = {},
    scrollToFirstTrigger: Int = 0,
) {
    // ★ 旅遊模式狀態
    var travelModeOn by remember { mutableStateOf(false) }

    // ★ 一般模式：用 topNotes（最多約 30，時間匹配 + 使用次數）
    // ★ 旅遊模式：用晒所有「旅遊」類別項目，按使用次數大→小，唔受 top 30 限制
    val visibleTopNotes = remember(
        topNotes, hiddenQuickNotes, travelModeOn, noteCategoryMap, noteUsageCount
    ) {
        if (travelModeOn) {
            noteCategoryMap.entries
                .filter { (name, cat) ->
                    cat == "旅遊" && name.isNotBlank() && name !in hiddenQuickNotes
                }
                .map { (name, _) -> name to (noteUsageCount[name] ?: 0) }
                .sortedByDescending { it.second }
        } else {
            topNotes.filter { it.first !in hiddenQuickNotes }
        }
    }

    if (visibleTopNotes.isEmpty() && hiddenQuickNotes.isEmpty() && !travelModeOn) return

    val topNoteNames = remember(visibleTopNotes) { visibleTopNotes.map { it.first } }
    // 旅遊模式顯示全部旅遊項目；一般模式維持最多 30
    val items = remember(topNoteNames, travelModeOn) {
        if (travelModeOn) topNoteNames.map { it to 0 }
        else topNoteNames.take(30).map { it to 0 }
    }

    var showManageDialog by remember { mutableStateOf(false) }
    var longPressTarget by remember { mutableStateOf<String?>(null) }
    var manageRevealProgress by remember { mutableFloatStateOf(0f) }
    val manageRevealAnim = remember { Animatable(0f) }
    val canHideMore = visibleTopNotes.size > 1
    val hiddenList = remember(topNotes, hiddenQuickNotes) {
        topNotes.map { it.first }.filter { it in hiddenQuickNotes }
    }
    val scope = rememberCoroutineScope()

    val density = LocalDensity.current
    val gapDp = 8.dp
    val verticalGapDp = 8.dp
    val chipHeightDp = 36.dp
    val panelVPaddingDp = 14.dp
    val indicatorSpaceDp = 20.dp

    val gapPx = remember(density) { with(density) { gapDp.toPx() } }
    val verticalGapPx = remember(density) { with(density) { verticalGapDp.toPx() } }
    val chipHeightPx = remember(density) { with(density) { chipHeightDp.toPx() } }
    val baseItemPx = remember(density) { with(density) { 44.dp.toPx() } + 12f }

    val textMeasurer = androidx.compose.ui.text.rememberTextMeasurer()
    val textStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium)

    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 0.dp)) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val contentWidthPx = with(density) { (maxWidth - 28.dp).toPx() }

            val pagesWithChips = remember(items, contentWidthPx, gapPx, maxRowsPerPage, chipHeightPx, verticalGapPx, baseItemPx) {
                val intrinsicRows = mutableListOf<MutableList<Pair<String, Float>>>()
                var currentRow = mutableListOf<Pair<String, Float>>()
                var currentRowWidth = 0f

                for (item in items) {
                    val name = item.first
                    val textW = noteTextWidthCache.getOrPut(name) {
                        textMeasurer.measure(name, textStyle).size.width.toFloat()
                    }

                    val itemPx = (baseItemPx + textW).coerceAtMost(contentWidthPx)

                    val needed = if (currentRow.isEmpty()) itemPx else currentRowWidth + gapPx + itemPx
                    if (currentRow.isNotEmpty() && needed > contentWidthPx) {
                        intrinsicRows.add(currentRow)
                        currentRow = mutableListOf()
                        currentRowWidth = 0f
                    }
                    currentRow.add(name to itemPx)
                    currentRowWidth = if (currentRow.size == 1) itemPx else currentRowWidth + gapPx + itemPx
                }
                if (currentRow.isNotEmpty()) intrinsicRows.add(currentRow)

                val perfectlyFilledRows = intrinsicRows.map { row ->
                    val n = row.size
                    val spacingTotal = (n - 1).coerceAtLeast(0) * gapPx
                    val intrinsicSum = row.fold(0f) { acc, p -> acc + p.second }
                    val extra = contentWidthPx - intrinsicSum - spacingTotal
                    if (extra <= 0f) {
                        row.map { (name, w) -> name to w }
                    } else {
                        val perItem = extra / n
                        row.map { (name, w) -> name to (w + perItem) }
                    }
                }

                perfectlyFilledRows.chunked(maxRowsPerPage).map { rowsInPage ->
                    val chips = mutableListOf<PlacedChip>()
                    rowsInPage.forEachIndexed { rowIdx, rowChips ->
                        val y = rowIdx * (chipHeightPx + verticalGapPx)
                        var x = 0f
                        rowChips.forEach { (name, w) ->
                            chips.add(PlacedChip(name, w, x, y))
                            x += w + gapPx
                        }
                    }
                    chips
                }
            }

            val pagerState = rememberPagerState(pageCount = { pagesWithChips.size.coerceAtLeast(1) })

            LaunchedEffect(pagesWithChips.size) {
                val maxPage = (pagesWithChips.size - 1).coerceAtLeast(0)
                if (pagerState.currentPage > maxPage) pagerState.scrollToPage(maxPage)
            }

            LaunchedEffect(scrollToFirstTrigger) {
                if (scrollToFirstTrigger <= 0) return@LaunchedEffect
                if (pagerState.currentPage != 0 || pagerState.currentPageOffsetFraction != 0f) {
                    pagerState.animateScrollToPage(
                        page = 0,
                        animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f)
                    )
                }
                if (manageRevealProgress > 0f) {
                    manageRevealAnim.snapTo(manageRevealProgress)
                    manageRevealAnim.animateTo(0f, spring(dampingRatio = 0.8f, stiffness = 400f)) {
                        manageRevealProgress = value
                    }
                }
            }

            val manageButtonWidthDp = 136.dp
            val manageButtonWidthPx = with(density) { manageButtonWidthDp.toPx() }

            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                        if (available.x < 0 && manageRevealProgress > 0f) {
                            val consume = available.x.coerceAtLeast(-manageRevealProgress * manageButtonWidthPx)
                            manageRevealProgress = ((manageRevealProgress * manageButtonWidthPx + consume) / manageButtonWidthPx).coerceIn(0f, 1f)
                            return Offset(consume, 0f)
                        }
                        return Offset.Zero
                    }
                    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                        if (pagerState.currentPage == 0 && available.x > 0f && source == NestedScrollSource.UserInput) {
                            val consume = available.x.coerceAtMost((1f - manageRevealProgress) * manageButtonWidthPx)
                            manageRevealProgress = ((manageRevealProgress * manageButtonWidthPx + consume) / manageButtonWidthPx).coerceIn(0f, 1f)
                            return Offset(consume, 0f)
                        }
                        return Offset.Zero
                    }
                }
            }

            LaunchedEffect(pagerState.isScrollInProgress) {
                if (!pagerState.isScrollInProgress) {
                    val target = if (manageRevealProgress >= 0.45f) 1f else 0f
                    manageRevealAnim.snapTo(manageRevealProgress)
                    manageRevealAnim.animateTo(target, spring(dampingRatio = 0.8f, stiffness = 400f)) {
                        manageRevealProgress = value
                    }
                }
            }

            val hasIndicator = pagesWithChips.size > 1
            val safeCurrentPage = pagerState.currentPage.coerceIn(0, (pagesWithChips.size - 1).coerceAtLeast(0))

            val targetHeightDp = remember(pagesWithChips, safeCurrentPage, hasIndicator) {
                val currentRowCount = pagesWithChips.getOrNull(safeCurrentPage)?.let { chips ->
                    if (chips.isEmpty()) 1 else {
                        val maxY = chips.maxOf { it.targetY }
                        ((maxY / (chipHeightPx + verticalGapPx)).roundToInt() + 1)
                    }
                } ?: 1

                chipHeightDp * currentRowCount +
                verticalGapDp * (currentRowCount - 1).coerceAtLeast(0) +
                panelVPaddingDp * 2 +
                (if (hasIndicator) indicatorSpaceDp else 0.dp)
            }

            val animatedHeight by animateDpAsState(
                targetValue = targetHeightDp,
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                label = "quickPanelHeight"
            )

            // 摺疊時高度收到 0，唔留「展開快速輸入」掣位；列表可一路滑到 Top Stats 後便
            val collapsedHeight = 0.dp
            val finalHeight = androidx.compose.ui.unit.lerp(animatedHeight, collapsedHeight, collapseProgress)

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(finalHeight)
                    .graphicsLayer {
                        // 整區漸變淡出 + 輕微縮小，營造消失喺 Top Stats 後便嘅朦朧感
                        alpha = (1f - collapseProgress).coerceIn(0f, 1f)
                        scaleY = 1f - (collapseProgress * 0.12f)
                        transformOrigin = TransformOrigin(0.5f, 0f)
                        if (collapseProgress > 0.05f && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val blurPx = (collapseProgress * 18f).roundToInt().coerceIn(1, 24)
                            renderEffect = getCachedBlurEffect(blurPx)
                        } else {
                            renderEffect = null
                        }
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .background(SURFACE_CARD)
                    .nestedScroll(nestedScrollConnection)
            ) {
                val mainAlpha = (1f - collapseProgress * 1.2f).coerceIn(0f, 1f)
                val mainScale = 1f - (collapseProgress * 0.06f)
                val revealPx = manageRevealProgress * manageButtonWidthPx

                // ★ 從左側拉出嘅選單（包含旅遊與管理）
                if (manageRevealProgress > 0.01f) {
                    Box(
                        Modifier
                            .align(Alignment.CenterStart)
                            .offset { IntOffset((-manageButtonWidthPx + revealPx).roundToInt(), 0) }
                            .width(manageButtonWidthDp)
                            .fillMaxHeight()
                            // 稍微加大 vertical padding
                            .padding(vertical = 6.dp, horizontal = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BRAND_PRIMARY_LIGHT),
                        contentAlignment = Alignment.Center
                    ) {
                        val actionTextAlpha = (1f - collapseProgress * 2.5f).coerceIn(0f, 1f)
                        // ★ 加大文字高度到 18.dp，確保字腳唔會被裁切
                        val textHeightDp = 18.dp * actionTextAlpha
                        val spacingDp = 2.dp * actionTextAlpha

                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // --- 旅遊掣 ---
                            Column(
                                modifier = Modifier.weight(1f).fillMaxHeight()
                                    .clickable { travelModeOn = !travelModeOn },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    if (travelModeOn) Icons.Default.Flight else Icons.Default.FlightTakeoff,
                                    "旅遊",
                                    tint = if (travelModeOn) Color(0xFF10B981) else BRAND_PRIMARY_DARK,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.height(spacingDp))
                                Text(
                                    "旅遊",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (travelModeOn) Color(0xFF10B981) else BRAND_PRIMARY_DARK,
                                    // ★ 移除字體默認 Padding，防止內部裁切
                                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                                    modifier = Modifier
                                        .height(textHeightDp)
                                        .graphicsLayer { alpha = actionTextAlpha }
                                )
                            }

                            // --- 分隔線 ---
                            Box(Modifier.width(1.dp).height(24.dp).background(BRAND_PRIMARY_DARK.copy(alpha = 0.2f)))

                            // --- 管理掣 ---
                            Column(
                                modifier = Modifier.weight(1f).fillMaxHeight()
                                    .clickable {
                                        showManageDialog = true
                                        scope.launch {
                                            manageRevealAnim.animateTo(0f, spring(dampingRatio = 0.8f, stiffness = 400f)) {
                                                manageRevealProgress = value
                                            }
                                        }
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Settings, "管理", tint = BRAND_PRIMARY_DARK, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.height(spacingDp))
                                Text(
                                    "管理",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BRAND_PRIMARY_DARK,
                                    // ★ 移除字體默認 Padding
                                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                                    modifier = Modifier
                                        .height(textHeightDp)
                                        .graphicsLayer { alpha = actionTextAlpha }
                                )
                            }
                        }
                    }
                }

                if (mainAlpha > 0.01f) {
                    Box(Modifier
                        .fillMaxSize()
                        .offset { IntOffset(revealPx.roundToInt(), 0) }
                        .graphicsLayer {
                            alpha = mainAlpha
                            scaleX = mainScale
                            scaleY = mainScale
                            transformOrigin = TransformOrigin(0.5f, 0f)
                        }
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.Top
                        ) { page ->
                            if (pagesWithChips.isEmpty()) {
                                // 旅遊模式無項目提示
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("暫時冇旅遊相關項目", color = TEXT_SECONDARY, fontSize = 13.sp)
                                }
                            } else {
                                val chips = pagesWithChips.getOrNull(page) ?: emptyList()
                                Box(Modifier.fillMaxWidth()
                                    .padding(start = 14.dp, end = 14.dp, top = panelVPaddingDp)) {
                                    chips.forEach { chip ->
                                        key(chip.name) {
                                            val animX = animateFloatAsState(chip.targetX, spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 320f), label="x")
                                            val animY = animateFloatAsState(chip.targetY, spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 320f), label="y")
                                            val animW = animateFloatAsState(chip.targetW, spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 320f), label="w")

                                            var appear by remember { mutableStateOf(skipAppearAnimation) }
                                            LaunchedEffect(Unit) {
                                                if (!skipAppearAnimation) appear = true
                                            }
                                            val scale by animateFloatAsState(if (appear) 1f else 0.6f, spring(0.65f, 400f), label="s")
                                            val alpha by animateFloatAsState(if (appear) 1f else 0f, tween(250), label="a")

                                            Box(
                                                Modifier
                                                    .offset { IntOffset(animX.value.roundToInt(), animY.value.roundToInt()) }
                                                    .layout { measurable, constraints ->
                                                        val w = animW.value.roundToInt()
                                                        val placeable = measurable.measure(constraints.copy(minWidth = w, maxWidth = w))
                                                        layout(w, placeable.height) { placeable.placeRelative(0, 0) }
                                                    }
                                                    .height(chipHeightDp)
                                                    .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
                                            ) {
                                                QuickInputChip(
                                                    name = chip.name,
                                                    iconUrl = noteIconMap[chip.name] ?: "",
                                                    modifier = Modifier.fillMaxSize(),
                                                    onClick = { onClick(chip.name) },
                                                    onLongClick = { longPressTarget = chip.name }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (hasIndicator) {
                            Row(Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val safeCurrent = pagerState.currentPage.coerceIn(0, (pagesWithChips.size - 1).coerceAtLeast(0))
                                repeat(pagesWithChips.size) { i ->
                                    val isSelected = safeCurrent == i
                                    Box(Modifier
                                        .size(if (isSelected) 6.dp else 4.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) BRAND_PRIMARY else DIVIDER_COLOR))
                                }
                            }
                        }
                    }
                }

                // 已移除「展開快速輸入」掣：摺疊時整區淡出收埋，向下滑動仍會由 nestedScroll 即刻展開
            }
        }
    }

    // ===== 長按隱藏選單 =====
    if (longPressTarget != null) {
        val targetName = longPressTarget!!
        AlertDialog(
            onDismissRequest = { longPressTarget = null },
            title = { Text("快速項目", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("「$targetName」", color = TEXT_SECONDARY, fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        onClick = {
                            if (canHideMore) {
                                onHideNote(targetName)
                                longPressTarget = null
                            }
                        },
                        enabled = canHideMore,
                        shape = RoundedCornerShape(12.dp),
                        color = if (canHideMore) SURFACE_ELEVATED else SURFACE_ELEVATED.copy(alpha = 0.5f)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.VisibilityOff, "隱藏",
                                tint = if (canHideMore) COLOR_EXPENSE else TEXT_TERTIARY,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                if (canHideMore) "隱藏呢個項目" else "至少保留一項（無法隱藏）",
                                color = if (canHideMore) TEXT_PRIMARY else TEXT_TERTIARY,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { longPressTarget = null }) { Text("取消") }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = SURFACE_CARD
        )
    }

    // ===== 管理隱藏項目彈窗 =====
    if (showManageDialog) {
        AlertDialog(
            onDismissRequest = { showManageDialog = false },
            title = { Text("隱藏的常用項目", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                if (hiddenList.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                        Text("暫時沒有隱藏項目", color = TEXT_SECONDARY, fontSize = 15.sp)
                    }
                } else {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        hiddenList.forEach { name ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SURFACE_ELEVATED,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconView(iconUrl = noteIconMap[name] ?: "", name = name, size = 28.dp)
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        name,
                                        modifier = Modifier.weight(1f),
                                        fontSize = 15.sp,
                                        color = TEXT_PRIMARY,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    TextButton(
                                        onClick = { onUnhideNote(name) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text("復原", color = BRAND_PRIMARY, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showManageDialog = false }) {
                    Text("完成", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = SURFACE_CARD
        )
    }
}

@Composable
fun AnimatedInfoChip(
    label: String, valueText: String,
    modifier: Modifier = Modifier,
    valueFontSize: TextUnit = 16.sp,
    valueColor: Color = TEXT_PRIMARY,
) {
    Surface(modifier = modifier, shape = RoundedCornerShape(12.dp), color = SURFACE_ELEVATED) {
        Column(
            Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(2.dp))
            Box(contentAlignment = Alignment.Center) {
                AnimatedAmount(
                    text = valueText,
                    color = valueColor,
                    fontSize = valueFontSize,
                    fontWeight = FontWeight.Bold,
                    animateChanges = true
                )
            }
        }
    }
}

@Composable
fun CalendarRow(
    rowIdx: Int, firstDayOffset: Int, daysInMonth: Int, today: Calendar,
    isCurrentMonth: Boolean, recordsByDay: Map<Int, List<Record>>,
    displayMode: Int, maxAbsNet: Double,
    selectedDay: Int?, selectedCol: Int?,
    animateValues: Boolean = false,
    heightFraction: Float = 1f,
    onCellClick: (day: Int, col: Int) -> Unit,
) {
    val spacing = 6.dp
    val maxItemsInRow = remember(rowIdx, firstDayOffset, daysInMonth, recordsByDay) {
        var maxItems = 0
        for (col in 0 until 7) {
            val day = rowIdx * 7 + col - firstDayOffset + 1
            if (day in 1..daysInMonth) {
                val c = recordsByDay[day]?.size ?: 0
                if (c > maxItems) maxItems = c
            }
        }
        maxItems
    }

    val targetContentHeight: Dp = when (displayMode) {
        0 -> 52.dp
        1 -> (34 + maxItemsInRow * 16).dp.coerceAtLeast(56.dp)
        2 -> {
            val iconRows = (maxItemsInRow + 2) / 3
            (30 + iconRows * 21).dp.coerceAtLeast(52.dp)
        }
        else -> 52.dp
    }
    val contentHeight by animateDpAsState(
        targetValue = targetContentHeight,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "calendarRowContentHeight"
    )

    val cellMaxScale = 1.06f
    val verticalPad = (contentHeight * (cellMaxScale - 1f) / 2f + 2.dp).coerceAtLeast(4.dp)
    val horizontalPad = 3.dp

    val baseRowHeight = contentHeight + verticalPad * 2
    val frac = heightFraction.coerceIn(0f, 1f)
    val rowHeight = baseRowHeight * frac

    val todayDayOfMonth = remember(today) { today.get(Calendar.DAY_OF_MONTH) }

    if (rowHeight < 0.5.dp) return

    Row(
        Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .clipToBounds()
            .graphicsLayer { alpha = frac }
            .padding(horizontal = horizontalPad),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (col in 0 until 7) {
            val cellIdx = rowIdx * 7 + col
            val dayOfMonth = cellIdx - firstDayOffset + 1
            Box(Modifier.weight(1f).fillMaxHeight().padding(vertical = verticalPad)) {
                if (dayOfMonth in 1..daysInMonth) {
                    val dayRecords = recordsByDay[dayOfMonth] ?: emptyList()
                    CalendarDayCell(
                        day = dayOfMonth,
                        isToday = isCurrentMonth && dayOfMonth == todayDayOfMonth,
                        isSelected = selectedDay == dayOfMonth,
                        records = dayRecords,
                        displayMode = displayMode,
                        maxAbsNet = maxAbsNet,
                        onClick = { onCellClick(dayOfMonth, col) },
                        animateValues = animateValues
                    )
                }
            }
        }
    }
}

@Immutable
private data class DayCellSummary(
    val net: Double, val absNet: Double, val compactNet: String,
    val sortedAsc: List<Record>, val chunked3: List<List<Record>>
)

@Composable
fun CalendarDayCell(
    day: Int, isToday: Boolean, isSelected: Boolean,
    records: List<Record>, displayMode: Int, maxAbsNet: Double, onClick: () -> Unit,
    animateValues: Boolean = false,
) {
    val shape = RoundedCornerShape(12.dp)
    val hasRecords = records.isNotEmpty()

    val summary = remember(records) {
        if (records.isEmpty()) {
            DayCellSummary(0.0, 0.0, "", emptyList(), emptyList())
        } else {
            var inc = 0.0; var exp = 0.0
            for (r in records) {
                if (r.category == INCOME_CATEGORY) inc += r.amount else exp += r.amount
            }
            val net = inc - exp
            val absNet = abs(net)
            val sorted = records.sortedBy { it.timestamp }
            DayCellSummary(
                net = net, absNet = absNet,
                compactNet = if (absNet > 0) compactAmount(net) else "",
                sortedAsc = sorted, chunked3 = records.chunked(3)
            )
        }
    }

    val net = summary.net
    val absNet = summary.absNet

    val targetBgColor = when {
        isSelected -> BRAND_PRIMARY_LIGHT
        hasRecords && net > 0 -> CELL_BG_POS
        hasRecords && net < 0 -> CELL_BG_NEG
        else -> SURFACE_CARD
    }
    val bgColor = if (!isSelected) targetBgColor else {
        animateColorAsState(
            targetValue = targetBgColor,
            animationSpec = tween(220, easing = FastOutSlowInEasing),
            label = "cellBg"
        ).value
    }

    val selectionScale by animateFloatAsState(
        targetValue = if (isSelected) 1.06f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "selScale"
    )

    val borderWidth = if (isSelected) 2.dp else 0.5.dp
    val borderColor = if (isSelected) BRAND_PRIMARY else DIVIDER_COLOR
    val elevation = if (hasRecords || isToday || isSelected) 1.dp else 0.dp

    val targetBarRatio = if (maxAbsNet > 0.0 && hasRecords && absNet > 0.0)
        (absNet / maxAbsNet).toFloat().coerceIn(0f, 1f)
    else 0f

    val animatedBarRatio by animateFloatAsState(
        targetValue = targetBarRatio,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "calendarBarRatio_$day"
    )

    val animatedBarColor by animateColorAsState(
        targetValue = if (net >= 0) CELL_BAR_POS else CELL_BAR_NEG,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "calendarBarColor_$day"
    )

    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = selectionScale
                scaleY = selectionScale
            }
            .shadow(
                elevation, shape, clip = false,
                ambientColor = Color(0x1A000000), spotColor = Color(0x1A000000)
            )
            .clip(shape)
            .background(bgColor)
            .border(width = borderWidth, color = borderColor, shape = shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        if (displayMode == 0 && animatedBarRatio > 0.001f) {
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(animatedBarRatio)
                    .background(animatedBarColor.copy(alpha = 0.75f))
            )
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 2.dp, vertical = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                "$day",
                fontSize = 13.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Black,
                color = if (isToday || isSelected) BRAND_PRIMARY_DARK else Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(5.dp))

            when (displayMode) {
                0 -> {
                    if (absNet > 0) {
                        Box(
                            Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedAmount(
                                text = summary.compactNet,
                                color = if (net >= 0) COLOR_INCOME else COLOR_EXPENSE,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                animateChanges = animateValues
                            )
                        }
                    }
                }
                1 -> {
                    if (isSelected) {
                        // ★ 選中時只顯示一行摘要
                        val count = records.size
                        val summaryText = when {
                            count == 0 -> ""
                            count == 1 -> records.first().note.ifBlank { "(無)" }
                            else -> "${count} 筆"
                        }
                        if (summaryText.isNotEmpty()) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(16.dp)
                                    .padding(horizontal = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FadedText(
                                    text = summaryText,
                                    color = TEXT_PRIMARY,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    fadeWidth = 6.dp,
                                    modifier = Modifier.weight(1f)
                                )
                                if (absNet > 0) {
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = summary.compactNet,
                                        fontSize = 9.sp,
                                        color = if (net >= 0) COLOR_INCOME else COLOR_EXPENSE,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    } else {
                        // 未選中：原本多行
                        summary.sortedAsc.forEach { r ->
                            Row(Modifier.fillMaxWidth().height(16.dp)) {
                                FadedText(
                                    text = r.note.ifBlank { "(無)" },
                                    color = TEXT_PRIMARY,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    fadeWidth = 6.dp,
                                    modifier = Modifier.weight(1f).alignByBaseline()
                                )
                                Spacer(Modifier.width(4.dp))
                                val compactRecAmt = remember(r.amount, r.category) {
                                    if (r.category == INCOME_CATEGORY) compactAmount(r.amount)
                                    else compactAmount(-r.amount)
                                }
                                Text(
                                    text = compactRecAmt,
                                    fontSize = 9.sp,
                                    lineHeight = 11.sp,
                                    color = amountColor(r.category),
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.alignByBaseline()
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
                2 -> {
                    summary.chunked3.forEach { row ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(18.dp)
                                .padding(horizontal = 1.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            row.forEach { r ->
                                IconViewAdaptive(
                                    iconUrl = r.iconUrl,
                                    name = r.note,
                                    modifier = Modifier.weight(1f).fillMaxHeight()
                                )
                            }
                            repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}

private val CALENDAR_WEEKDAY_HEADERS = listOf("日", "一", "二", "三", "四", "五", "六")

private val SEGMENTED_MODES = listOf(
    Triple("金額", Icons.Default.AttachMoney, 0),
    Triple("項目", Icons.Default.List, 1),
    Triple("圖標", Icons.Default.Apps, 2)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedModeControl(
    displayMode: Int,
    onModeChange: (Int) -> Unit
) {
    val itemWidth = 76.dp
    val itemHeight = 28.dp
    val density = LocalDensity.current
    val itemWidthPx = remember(density) { with(density) { itemWidth.toPx() } }

    var dragAccum by remember { mutableFloatStateOf(0f) }
    val latestMode by rememberUpdatedState(displayMode)
    val latestOnChange by rememberUpdatedState(onModeChange)

    Surface(shape = RoundedCornerShape(12.dp), color = SURFACE_ELEVATED) {
        Box(
            Modifier.padding(3.dp).pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { dragAccum = 0f },
                    onDragEnd = { dragAccum = 0f },
                    onDragCancel = { dragAccum = 0f },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragAccum += dragAmount.x
                        val steps = (dragAccum / itemWidthPx).toInt()
                        if (steps != 0) {
                            val newMode = (latestMode + steps).coerceIn(0, 2)
                            if (newMode != latestMode) latestOnChange(newMode)
                            dragAccum -= steps * itemWidthPx
                        }
                    }
                )
            }
        ) {
            val indicatorOffset by animateDpAsState(
                targetValue = itemWidth * displayMode,
                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow),
                label = "segIndicator"
            )

            Box(
                Modifier.offset(x = indicatorOffset)
                    .width(itemWidth).height(itemHeight)
                    .clip(RoundedCornerShape(9.dp))
                    .background(SURFACE_CARD)
            )

            Row {
                SEGMENTED_MODES.forEach { (label, icon, mode) ->
                    val selected = displayMode == mode
                    Row(
                        modifier = Modifier.width(itemWidth).height(itemHeight)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onModeChange(mode) }
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(icon, label,
                            tint = if (selected) BRAND_PRIMARY else Color.Black,
                            modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(label, fontSize = 12.sp,
                            color = if (selected) TEXT_PRIMARY else Color.Black,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                            maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
fun FadedText(
    text: String, color: Color, fontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Normal,
    modifier: Modifier = Modifier, fadeWidth: Dp = 6.dp
) {
    val density = LocalDensity.current
    val fadePx = remember(density, fadeWidth) { with(density) { fadeWidth.toPx() } }
    var textWidthPx by remember(text) { mutableFloatStateOf(0f) }

    val brush = remember(textWidthPx, fadePx, color) {
        if (textWidthPx > 1f) {
            val effectiveFade = fadePx.coerceAtMost(textWidthPx * 0.45f)
            val stop = ((textWidthPx - effectiveFade) / textWidthPx).coerceIn(0.5f, 1f)
            Brush.horizontalGradient(
                colorStops = arrayOf(0f to color, stop to color, 1f to color.copy(alpha = 0f)),
                startX = 0f, endX = textWidthPx
            )
        } else SolidColor(color)
    }

    Text(
        text = text, maxLines = 1, softWrap = false, overflow = TextOverflow.Clip,
        style = TextStyle(brush = brush, fontSize = fontSize,
            fontWeight = fontWeight, lineHeight = 11.sp),
        onTextLayout = { result ->
            if (result.lineCount > 0) {
                val w = result.getLineRight(0) - result.getLineLeft(0)
                if (abs(w - textWidthPx) > 1f) textWidthPx = w
            }
        },
        modifier = modifier
    )
}

/**
 * 篩選模式專用：只顯示棒形圖，唔要標題／金額。
 * 全部結果一頁顯示晒（唔橫向滑動），棒可以好密。
 */
@Composable
fun FilterResultsBarChart(
    records: List<Record>,
    modifier: Modifier = Modifier,
) {
    if (records.isEmpty()) return

    // 最新日期在左，舊日期在右
    val ordered = remember(records) {
        records.sortedByDescending { it.timestamp }
    }
    val maxAmount = remember(ordered) {
        ordered.maxOf { it.amount }.coerceAtLeast(0.0001)
    }
    val count = ordered.size
    val chartBarAreaHeight = 48.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(chartBarAreaHeight)
    ) {
        val availablePx = constraints.maxWidth.toFloat()
        // 棒之間留極少空隙；再密都要一頁顯示晒
        val gapPx = if (count <= 1) 0f else 1f
        val barPx = if (count <= 1) {
            availablePx
        } else {
            ((availablePx - gapPx * (count - 1)) / count).coerceAtLeast(1f)
        }

        Canvas(Modifier.fillMaxSize()) {
            ordered.forEachIndexed { i, r ->
                val ratio = (r.amount / maxAmount).toFloat().coerceIn(0.06f, 1f)
                val h = (ratio * size.height).coerceAtLeast(2f)
                val x = i * (barPx + gapPx)
                val y = size.height - h
                val isIncome = r.category == INCOME_CATEGORY
                val style = CATEGORY_STYLES[r.category]
                val fillColor = (style?.fgColor ?: if (isIncome) COLOR_INCOME else COLOR_EXPENSE)
                drawRect(
                    color = fillColor.copy(alpha = 0.9f),
                    topLeft = Offset(x, y),
                    size = androidx.compose.ui.geometry.Size(barPx, h)
                )
            }
        }
    }
}

@Composable
fun MiniHistogram(
    values: List<Double>, color: Color, modifier: Modifier = Modifier,
) {
    if (values.isEmpty()) { Spacer(modifier); return }
    val maxValue = remember(values) { values.maxOrNull() ?: 0.0 }
    if (maxValue <= 0.0) { Spacer(modifier); return }
    Canvas(modifier) {
        val count = values.size
        val gapRatio = 0.35f
        val totalWidth = size.width
        val barWidth = if (count == 1) totalWidth
                       else totalWidth / (count + (count - 1) * gapRatio)
        val gap = if (count == 1) 0f else barWidth * gapRatio
        values.forEachIndexed { i, v ->
            val ratio = (v / maxValue).toFloat().coerceIn(0f, 1f)
            val h = (ratio * size.height).coerceAtLeast(1.5f)
            val x = i * (barWidth + gap)
            val y = size.height - h
            drawRect(color = color, topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth, h))
        }
    }
}

@Composable
fun CompareValueCell(
    amount: Double,
    isIncome: Boolean,
    pct: Double?,
    animateValues: Boolean = false,
    modifier: Modifier = Modifier
) {
    val amtDisplay = remember(amount, isIncome) {
        val formattedAmt = formatAmountNoDecimal(abs(amount))
        if (isIncome) formattedAmt else "($formattedAmt)"
    }
    val amtColor = if (isIncome) COLOR_INCOME else COLOR_EXPENSE

    val pctText: String? = remember(pct) {
        when {
            pct == null -> null
            pct > 999.0 -> ">1K%"
            else -> String.format(Locale.US, "%.0f%%", pct)
        }
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            RollingNumber(
                text = amtDisplay,
                color = amtColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                animateChanges = animateValues
            )
        }
        Spacer(Modifier.width(4.dp))
        Box(Modifier.width(38.dp), contentAlignment = Alignment.CenterEnd) {
            if (pctText != null) {
                Text(pctText, fontSize = 11.sp, color = TEXT_SECONDARY,
                    fontWeight = FontWeight.Medium, textAlign = TextAlign.End, maxLines = 1)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CompareContent(
    compareUi: CompareUiState,
    selectedMonthA: String,
    onMonthAChange: (String) -> Unit,
    selectedMonthB: String,
    onMonthBChange: (String) -> Unit,
    onAddClick: () -> Unit,
) {
    // 首次進入唔播動畫
    var animateValues by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(800)
        animateValues = true
    }

    val categoryMonthSum = compareUi.categoryMonthSum
    fun sumLocal(cat: String, month: String): Double =
        categoryMonthSum[cat]?.get(month) ?: 0.0

    val displayCategories = compareUi.displayCategories
    val incomeA = compareUi.incomeA
    val incomeB = compareUi.incomeB
    val balanceA = compareUi.balanceA
    val balanceB = compareUi.balanceB
    val histogramData = compareUi.histogramData
    val monthlyKeysThisYear = compareUi.monthlyKeysThisYear
    // false = 兩個月對比；true = 一次顯示全年各月欄
    var showAllMonths by rememberSaveable { mutableStateOf(false) }

    // ★ 高亮 + 延遲滾動控制
    var previousSums by remember { mutableStateOf<Map<String, Pair<Double, Double>>>(emptyMap()) }
    var highlightedCategories by remember { mutableStateOf<Set<String>>(emptySet()) }
    var allowNumberAnimate by remember { mutableStateOf(true) }

    LaunchedEffect(displayCategories, selectedMonthA, selectedMonthB, categoryMonthSum) {
        val newSums = displayCategories.associateWith { cat ->
            sumLocal(cat, selectedMonthA) to sumLocal(cat, selectedMonthB)
        }

        // ① 第一次入比較頁：記錄 baseline，唔 highlight、唔動畫
        if (previousSums.isEmpty()) {
            previousSums = newSums
            highlightedCategories = emptySet()
            allowNumberAnimate = true
            return@LaunchedEffect
        }

        val changed = newSums.filter { (cat, pair) ->
            previousSums[cat] != pair
        }.keys

        if (changed.isEmpty()) {
            previousSums = newSums
            highlightedCategories = emptySet()
            allowNumberAnimate = true
            return@LaunchedEffect
        }

        // ② 即刻顯示藍色 highlight（唔延遲）
        highlightedCategories = changed
        allowNumberAnimate = false

        // ③ highlight 停留 1 秒，數字先至開始滾動
        delay(1000)
        allowNumberAnimate = true
        previousSums = newSums

        // ④ 等滾動 + 行移動完成（約 1.5 秒），之後才開始漸退
        delay(1500)
        highlightedCategories = emptySet()
    }

    Box(Modifier.fillMaxSize().background(SURFACE_BG)) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 80.dp)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "月份比較",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TEXT_PRIMARY,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    if (showAllMonths) "全年" else "近兩月",
                    fontSize = 13.sp,
                    color = TEXT_SECONDARY,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.width(6.dp))
                Switch(
                    checked = showAllMonths,
                    onCheckedChange = { showAllMonths = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BRAND_PRIMARY,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = DIVIDER_COLOR
                    )
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = SURFACE_CARD,
                border = BorderStroke(0.5.dp, DIVIDER_COLOR)
            ) {
                AnimatedContent(
                    targetState = showAllMonths,
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = {
                        (fadeIn(tween(240)) + expandHorizontally(
                            animationSpec = spring(0.88f, 280f),
                            expandFrom = Alignment.Start
                        )) togetherWith (fadeOut(tween(180)) + shrinkHorizontally(
                            animationSpec = spring(0.88f, 280f),
                            shrinkTowards = Alignment.Start
                        ))
                    },
                    label = "compareMode"
                ) { allMonthsMode ->
                    if (!allMonthsMode) {
                        // ===== 兩個月對比（原有） =====
                        Column(Modifier.fillMaxSize()) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .background(SURFACE_CARD)
                                    .padding(horizontal = 10.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "類別",
                                    Modifier.weight(1.0f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TEXT_SECONDARY
                                )
                                Spacer(Modifier.weight(0.65f))
                                Row(
                                    Modifier.weight(1.55f),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    MonthDropdown(
                                        value = selectedMonthA,
                                        months = monthlyKeysThisYear,
                                        onChange = { onMonthAChange(it) }
                                    )
                                }
                                Spacer(Modifier.width(6.dp))
                                Row(
                                    Modifier.weight(1.55f),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    MonthDropdown(
                                        value = selectedMonthB,
                                        months = monthlyKeysThisYear,
                                        onChange = { onMonthBChange(it) }
                                    )
                                }
                            }
                            HorizontalDivider(color = DIVIDER_COLOR, thickness = 0.5.dp)
                            Box(Modifier.weight(1f).fillMaxWidth()) {
                                if (displayCategories.isEmpty()) {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("此兩月份皆無紀錄", color = TEXT_SECONDARY, fontSize = 15.sp)
                                    }
                                } else {
                                    BoxWithConstraints(Modifier.fillMaxSize()) {
                                        val rowCount = displayCategories.size
                                        val dividerThickness = 0.5.dp
                                        val totalDividerHeight =
                                            dividerThickness * (rowCount - 1).coerceAtLeast(0)
                                        val rowHeight =
                                            ((maxHeight - totalDividerHeight) / rowCount).coerceAtLeast(44.dp)
                                        LazyColumn(Modifier.fillMaxSize(), userScrollEnabled = false) {
                                            itemsIndexed(displayCategories, key = { _, c -> c }) { idx, cat ->
                                                val amtA = sumLocal(cat, selectedMonthA)
                                                val amtB = sumLocal(cat, selectedMonthB)
                                                val style = CATEGORY_STYLES[cat]
                                                val isIncome = cat == INCOME_CATEGORY
                                                val pctA = if (incomeA > 0.0 && !isIncome && amtA > 0.0) amtA / incomeA * 100.0 else null
                                                val pctB = if (incomeB > 0.0 && !isIncome && amtB > 0.0) amtB / incomeB * 100.0 else null
                                                val isHighlighted = cat in highlightedCategories
                                                val highlightBg by animateColorAsState(
                                                    if (isHighlighted) Color(0xFFDBEAFE) else SURFACE_CARD,
                                                    tween(600, easing = FastOutSlowInEasing),
                                                    label = "hl_$cat"
                                                )
                                                Column(Modifier.fillMaxWidth()) {
                                                    Row(
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .height(rowHeight)
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .background(highlightBg)
                                                            .padding(horizontal = 4.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                                            if (style != null) {
                                                                Box(
                                                                    Modifier.size(26.dp).clip(RoundedCornerShape(7.dp)).background(style.bgColor),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    Icon(style.icon, null, tint = style.fgColor, modifier = Modifier.size(15.dp))
                                                                }
                                                                Spacer(Modifier.width(7.dp))
                                                            }
                                                            Text(cat, fontSize = 15.sp, color = TEXT_PRIMARY, fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Medium)
                                                        }
                                                        Box(Modifier.weight(0.65f).fillMaxHeight().padding(start = 12.dp, end = 16.dp), contentAlignment = Alignment.Center) {
                                                            if (!isIncome) {
                                                                MiniHistogram(
                                                                    values = histogramData[cat].orEmpty(),
                                                                    color = style?.fgColor ?: TEXT_TERTIARY,
                                                                    modifier = Modifier.fillMaxWidth().height((rowHeight - 16.dp).coerceAtLeast(20.dp))
                                                                )
                                                            }
                                                        }
                                                        CompareValueCell(amtA, isIncome, pctA, animateValues && allowNumberAnimate, Modifier.weight(1.55f))
                                                        Spacer(Modifier.width(6.dp))
                                                        CompareValueCell(amtB, isIncome, pctB, animateValues && allowNumberAnimate, Modifier.weight(1.55f))
                                                    }
                                                    if (idx < displayCategories.lastIndex) {
                                                        HorizontalDivider(Modifier.padding(horizontal = 10.dp), color = DIVIDER_COLOR, thickness = dividerThickness)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            HorizontalDivider(color = DIVIDER_COLOR, thickness = 0.5.dp)
                            Row(
                                Modifier.fillMaxWidth().background(SURFACE_CARD).padding(horizontal = 10.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("餘額", Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BRAND_PRIMARY_DARK)
                                Spacer(Modifier.weight(0.65f))
                                CompareValueCell(balanceA, balanceA >= 0, null, animateValues && allowNumberAnimate, Modifier.weight(1.55f))
                                Spacer(Modifier.width(6.dp))
                                CompareValueCell(balanceB, balanceB >= 0, null, animateValues && allowNumberAnimate, Modifier.weight(1.55f))
                            }
                        }
                    } else {
                        // ===== 全年：一次顯示所有月份欄 =====
                        val hScroll = rememberScrollState()
                        val monthColW = 64.dp
                        val catColW = 88.dp
                        Column(Modifier.fillMaxSize()) {
                            // Header
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .background(SURFACE_CARD)
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "類別",
                                    Modifier.width(catColW).padding(start = 10.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TEXT_SECONDARY
                                )
                                Row(
                                    Modifier
                                        .weight(1f)
                                        .horizontalScroll(hScroll)
                                        .padding(end = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    monthlyKeysThisYear.forEach { mk ->
                                        Text(
                                            text = mk.substringAfter("-").toIntOrNull()?.toString()?.plus("月") ?: mk,
                                            modifier = Modifier.width(monthColW),
                                            textAlign = TextAlign.End,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BRAND_PRIMARY
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(color = DIVIDER_COLOR, thickness = 0.5.dp)
                            Box(Modifier.weight(1f).fillMaxWidth()) {
                                if (displayCategories.isEmpty()) {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("今年暫無紀錄", color = TEXT_SECONDARY, fontSize = 15.sp)
                                    }
                                } else {
                                    BoxWithConstraints(Modifier.fillMaxSize()) {
                                        val rowCount = displayCategories.size
                                        val dividerThickness = 0.5.dp
                                        val rowHeight =
                                            ((maxHeight - dividerThickness * (rowCount - 1).coerceAtLeast(0)) / rowCount)
                                                .coerceAtLeast(40.dp)
                                        LazyColumn(Modifier.fillMaxSize(), userScrollEnabled = false) {
                                            itemsIndexed(displayCategories, key = { _, c -> "y_$c" }) { idx, cat ->
                                                val style = CATEGORY_STYLES[cat]
                                                val isIncome = cat == INCOME_CATEGORY
                                                Column(Modifier.fillMaxWidth()) {
                                                    Row(
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .height(rowHeight)
                                                            .padding(vertical = 1.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Row(
                                                            Modifier.width(catColW).padding(start = 10.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            if (style != null) {
                                                                Box(
                                                                    Modifier.size(22.dp).clip(RoundedCornerShape(6.dp)).background(style.bgColor),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    Icon(style.icon, null, tint = style.fgColor, modifier = Modifier.size(13.dp))
                                                                }
                                                                Spacer(Modifier.width(5.dp))
                                                            }
                                                            Text(
                                                                cat,
                                                                fontSize = 12.sp,
                                                                color = TEXT_PRIMARY,
                                                                fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Medium,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                        }
                                                        Row(
                                                            Modifier
                                                                .weight(1f)
                                                                .horizontalScroll(hScroll)
                                                                .padding(end = 8.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            monthlyKeysThisYear.forEach { mk ->
                                                                val amt = sumLocal(cat, mk)
                                                                Text(
                                                                    text = if (amt == 0.0) "—" else formatAmountNoDecimal(amt),
                                                                    modifier = Modifier.width(monthColW),
                                                                    textAlign = TextAlign.End,
                                                                    fontSize = 12.sp,
                                                                    fontWeight = FontWeight.SemiBold,
                                                                    color = when {
                                                                        amt == 0.0 -> TEXT_TERTIARY
                                                                        isIncome -> COLOR_INCOME
                                                                        else -> COLOR_EXPENSE
                                                                    },
                                                                    maxLines = 1
                                                                )
                                                            }
                                                        }
                                                    }
                                                    if (idx < displayCategories.lastIndex) {
                                                        HorizontalDivider(
                                                            Modifier.padding(start = 10.dp),
                                                            color = DIVIDER_COLOR,
                                                            thickness = dividerThickness
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            HorizontalDivider(color = DIVIDER_COLOR, thickness = 0.5.dp)
                            // 全年餘額列
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .background(SURFACE_CARD)
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "餘額",
                                    Modifier.width(catColW).padding(start = 10.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = BRAND_PRIMARY_DARK
                                )
                                Row(
                                    Modifier
                                        .weight(1f)
                                        .horizontalScroll(hScroll)
                                        .padding(end = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    monthlyKeysThisYear.forEach { mk ->
                                        val inc = sumLocal(INCOME_CATEGORY, mk)
                                        val exp = displayCategories
                                            .filter { it != INCOME_CATEGORY }
                                            .sumOf { sumLocal(it, mk) }
                                        val bal = inc - exp
                                        Text(
                                            text = if (bal == 0.0 && inc == 0.0) "—" else formatAmountNoDecimal(bal),
                                            modifier = Modifier.width(monthColW),
                                            textAlign = TextAlign.End,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                bal > 0 -> COLOR_INCOME
                                                bal < 0 -> COLOR_EXPENSE
                                                else -> TEXT_TERTIARY
                                            },
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            shape = CircleShape,
            containerColor = BRAND_PRIMARY,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 12.dp)
        ) {
            Icon(Icons.Default.Add, "新增")
        }
    }
}

@Composable
fun MonthDropdown(value: String, months: List<String>, onChange: (String) -> Unit,
                  modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val labelText = remember(value) { formatMonthLabel(value) }
    Box(modifier) {
        Surface(onClick = { expanded = true }, shape = RoundedCornerShape(10.dp),
            color = SURFACE_CARD) {
            Row(Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(labelText, fontSize = 14.sp,
                    fontWeight = FontWeight.Bold, color = BRAND_PRIMARY)
                Spacer(Modifier.width(3.dp))
                Icon(Icons.Default.ArrowDropDown, "選擇月份",
                    tint = BRAND_PRIMARY, modifier = Modifier.size(18.dp))
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(14.dp),
            containerColor = SURFACE_CARD
        ) {
            months.forEach { m ->
                DropdownMenuItem(
                    text = { Text(formatMonthLabel(m), fontSize = 15.sp) },
                    onClick = { onChange(m); expanded = false }
                )
            }
        }
    }
}
// =========================================================
//  ★ PivotContent（資料透視表頁面）
// =========================================================
@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun PivotContent(
    pivotUi: PivotUiState,
    onYearChange: (Int) -> Unit,
    onAddClick: () -> Unit,
) {
    val hState = rememberScrollState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val leftWidth = 148.dp
    val colWidth = 72.dp
    val headerHeight = 42.dp
    val rowHeight = 38.dp
    val stickyHeight = 40.dp
    val colWidthPx = remember(density, colWidth) { with(density) { colWidth.roundToPx() } }

    // 標題／年份：向下滾動時漸隱，滾到頂先出現
    val headerCollapse by remember {
        derivedStateOf {
            val idx = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            when {
                idx > 0 -> 1f
                else -> (offset / 120f).coerceIn(0f, 1f)
            }
        }
    }
    val headerAlpha = 1f - headerCollapse
    val headerHeightDp = (56.dp * (1f - headerCollapse)).coerceAtLeast(0.dp)

    val categoryHeaderIndex = remember(pivotUi.rows) {
        val map = LinkedHashMap<String, Int>()
        var idx = 0
        for (row in pivotUi.rows) {
            if (row.isCategory) map[row.category] = idx
            idx++
        }
        map
    }

    fun scrollToCategory(cat: String) {
        val index = categoryHeaderIndex[cat] ?: return
        val months = pivotUi.months
        val year = pivotUi.selectedYear
        scope.launch {
            // 垂直：用較順滑嘅 tween，同時（若係今年）橫向捲去本月
            val vertical = launch {
                listState.animateScrollToItem(
                    index = index,
                    scrollOffset = 0
                )
            }
            val nowMeta = getTimestampMeta(System.currentTimeMillis())
            if (year == nowMeta.year && months.isNotEmpty()) {
                val monthKey = "$year-${pad2(nowMeta.month)}"
                val monthIdx = months.indexOf(monthKey)
                if (monthIdx >= 0) {
                    val target = (monthIdx * colWidthPx).coerceIn(0, hState.maxValue)
                    // 與垂直滾動並行，體感更流暢
                    launch {
                        hState.animateScrollTo(
                            value = target,
                            animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing)
                        )
                    }
                }
            }
            vertical.join()
        }
    }

    Box(Modifier.fillMaxSize().background(SURFACE_BG)) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 80.dp)
        ) {
            // ===== 標題 + 年份（可摺疊）=====
            if (headerHeightDp > 0.dp) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(headerHeightDp)
                        .graphicsLayer { alpha = headerAlpha },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "資料透視表",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TEXT_PRIMARY,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    if (pivotUi.availableYears.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 已係小→大
                            pivotUi.availableYears.forEach { y ->
                                val selected = y == pivotUi.selectedYear
                                Surface(
                                    onClick = { onYearChange(y) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) BRAND_PRIMARY else SURFACE_ELEVATED,
                                    border = if (selected) null else BorderStroke(0.5.dp, DIVIDER_COLOR)
                                ) {
                                    Text(
                                        "$y",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else TEXT_PRIMARY,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ===== 類別 chips：每行最少 5 個、等寬、左右對齊、少空隙 =====
            if (pivotUi.categories.isNotEmpty()) {
                PivotCategoryChipGrid(
                    categories = pivotUi.categories,
                    onClick = { scrollToCategory(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = SURFACE_CARD,
                border = BorderStroke(0.5.dp, DIVIDER_COLOR)
            ) {
                if (pivotUi.rows.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            if (pivotUi.availableYears.isEmpty()) "暫時未有記錄"
                            else "${pivotUi.selectedYear} 年暫時未有記錄",
                            color = TEXT_SECONDARY,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    Column(Modifier.fillMaxSize()) {
                        // ===== 固定表頭（只顯示到本月）=====
                        Row(
                            Modifier.fillMaxWidth().height(headerHeight).background(SURFACE_ELEVATED),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier.width(leftWidth).fillMaxHeight().padding(start = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    "類別 / 項目",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TEXT_SECONDARY
                                )
                            }
                            Box(Modifier.weight(1f).clipToBounds()) {
                                Row(
                                    Modifier.horizontalScroll(hState).height(headerHeight),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    pivotUi.months.forEach { mk ->
                                        Box(
                                            Modifier.width(colWidth).fillMaxHeight(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                formatMonthLabel(mk),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BRAND_PRIMARY_DARK
                                            )
                                        }
                                    }
                                    Box(
                                        Modifier.width(colWidth).fillMaxHeight()
                                            .background(BRAND_PRIMARY_LIGHT.copy(alpha = 0.5f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "總計",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BRAND_PRIMARY_DARK
                                        )
                                    }
                                }
                            }
                        }
                        HorizontalDivider(color = DIVIDER_COLOR, thickness = 0.5.dp)

                        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                            pivotUi.rows.forEach { row ->
                                if (row.isCategory) {
                                    stickyHeader(key = "cat_${row.category}") {
                                        PivotCategoryStickyRow(
                                            row = row,
                                            months = pivotUi.months,
                                            hState = hState,
                                            leftWidth = leftWidth,
                                            colWidth = colWidth,
                                            height = stickyHeight,
                                        )
                                    }
                                } else {
                                    item(key = "note_${row.category}_${row.note}") {
                                        PivotNoteRow(
                                            row = row,
                                            months = pivotUi.months,
                                            hState = hState,
                                            leftWidth = leftWidth,
                                            colWidth = colWidth,
                                            height = rowHeight,
                                            iconUrl = pivotUi.noteIconMap[row.note] ?: "",
                                        )
                                        HorizontalDivider(
                                            color = DIVIDER_COLOR.copy(alpha = 0.5f),
                                            thickness = 0.5.dp
                                        )
                                    }
                                }
                            }

                            item(key = "grand_total") {
                                Row(
                                    Modifier.fillMaxWidth().height(48.dp)
                                        .background(BRAND_PRIMARY_LIGHT.copy(alpha = 0.5f)),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        Modifier.width(leftWidth).fillMaxHeight().padding(start = 12.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            "總計",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BRAND_PRIMARY_DARK
                                        )
                                    }
                                    Box(Modifier.weight(1f).clipToBounds()) {
                                        Row(
                                            Modifier.horizontalScroll(hState).height(48.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            pivotUi.months.forEach { mk ->
                                                Box(
                                                    Modifier.width(colWidth).fillMaxHeight(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    val total = pivotUi.monthTotals[mk] ?: 0.0
                                                    if (total != 0.0) {
                                                        Text(
                                                            formatAmountNoDecimal(total),
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = BRAND_PRIMARY_DARK,
                                                            maxLines = 1
                                                        )
                                                    } else {
                                                        Text("–", fontSize = 12.sp, color = TEXT_TERTIARY)
                                                    }
                                                }
                                            }
                                            Box(
                                                Modifier.width(colWidth).fillMaxHeight(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    formatAmountNoDecimal(pivotUi.grandTotal),
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = COLOR_EXPENSE,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            shape = CircleShape,
            containerColor = BRAND_PRIMARY,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 12.dp)
        ) {
            Icon(Icons.Default.Add, "新增")
        }
    }
}

/**
 * 類別 chips 網格
 * - 優先完整顯示文字（唔截斷）
 * - 在「字完整」前提下盡量多欄（目標 ≥5，夠位先加）
 * - 等寬 + 末行佔位，避免大空隙
 */
@Composable
private fun PivotCategoryChipGrid(
    categories: List<String>,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gap = 6.dp
    val preferredMinCols = 5
    val density = LocalDensity.current
    val textMeasurer = androidx.compose.ui.text.rememberTextMeasurer()

    BoxWithConstraints(modifier) {
        val count = categories.size
        if (count == 0) return@BoxWithConstraints

        val maxWidthPx = constraints.maxWidth.toFloat()
        val gapPx = with(density) { gap.toPx() }

        data class ChipMetrics(val fontSize: TextUnit, val iconSize: Dp, val hPad: Dp, val cols: Int)

        fun neededWidthPx(cat: String, fontSize: TextUnit, iconSize: Dp, hPad: Dp): Float {
            val style = TextStyle(fontSize = fontSize, fontWeight = FontWeight.SemiBold)
            val textW = textMeasurer.measure(text = cat, style = style).size.width.toFloat()
            val hasIcon = CATEGORY_STYLES.containsKey(cat)
            val iconPx = if (hasIcon) with(density) { (iconSize + 4.dp).toPx() } else 0f
            val padPx = with(density) { (hPad * 2).toPx() }
            return textW + iconPx + padPx
        }

        // 候選：大字 → 細字。完整顯示文字優先，再盡量多欄（夠位先爭 ≥5）
        val candidates = listOf(
            Triple(13.sp, 14.dp, 8.dp),
            Triple(12.sp, 13.dp, 7.dp),
            Triple(11.sp, 12.dp, 6.dp),
        )

        var bestFullText: ChipMetrics? = null          // 最大字 + 完整顯示
        var bestWithMinCols: ChipMetrics? = null       // 完整顯示且 ≥ preferredMinCols

        for ((fs, isz, hp) in candidates) {
            val maxNeed = categories.maxOf { neededWidthPx(it, fs, isz, hp) }
            val fitCols = ((maxWidthPx + gapPx) / (maxNeed + gapPx)).toInt().coerceAtLeast(1)
            val cols = minOf(count, fitCols)
            val metrics = ChipMetrics(fs, isz, hp, cols)
            if (bestFullText == null) bestFullText = metrics
            if (fitCols >= preferredMinCols && bestWithMinCols == null) {
                bestWithMinCols = metrics
                // 已係由大字掃落嚟，第一個達標即可
                break
            }
        }

        // 若細字先做到 ≥5 欄，用佢；否則用最大字完整顯示（欄位可能少過 5）
        val chosen = bestWithMinCols ?: bestFullText ?: ChipMetrics(13.sp, 14.dp, 8.dp, 1)
        val fontSize = chosen.fontSize
        val iconSize = chosen.iconSize
        val hPad = chosen.hPad
        val cols = chosen.cols

        Column(verticalArrangement = Arrangement.spacedBy(gap)) {
            categories.chunked(cols).forEach { rowCats ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(gap),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rowCats.forEach { cat ->
                        val style = CATEGORY_STYLES[cat]
                        Surface(
                            onClick = { onClick(cat) },
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = style?.bgColor ?: SURFACE_ELEVATED,
                            border = BorderStroke(
                                0.5.dp,
                                (style?.fgColor ?: TEXT_SECONDARY).copy(alpha = 0.25f)
                            )
                        ) {
                            Row(
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = hPad),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (style != null) {
                                    Icon(
                                        style.icon, null,
                                        tint = style.fgColor,
                                        modifier = Modifier.size(iconSize)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                }
                                Text(
                                    cat,
                                    fontSize = fontSize,
                                    fontWeight = FontWeight.SemiBold,
                                    color = style?.fgColor ?: TEXT_PRIMARY,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Visible
                                )
                            }
                        }
                    }
                    repeat(cols - rowCats.size) {
                        Spacer(Modifier.weight(1f).height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PivotCategoryStickyRow(
    row: PivotRow,
    months: List<String>,
    hState: androidx.compose.foundation.ScrollState,
    leftWidth: Dp,
    colWidth: Dp,
    height: Dp,
) {
    Column(Modifier.fillMaxWidth().background(SURFACE_CARD)) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(height)
                .background(SURFACE_ELEVATED),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .width(leftWidth)
                    .fillMaxHeight()
                    .padding(start = 12.dp, end = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                val style = CATEGORY_STYLES[row.category]
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (style != null) {
                        Icon(
                            style.icon, null,
                            tint = style.fgColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                    }
                    Text(
                        row.category,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TEXT_PRIMARY,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Box(Modifier.weight(1f).clipToBounds()) {
                Row(
                    Modifier.horizontalScroll(hState).height(height),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    months.forEach { mk ->
                        val cell = row.cells[mk]
                        Box(
                            Modifier.width(colWidth).fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (cell != null && cell.total != 0.0) {
                                Text(
                                    formatAmountNoDecimal(cell.total),
                                    fontSize = 12.sp,
                                    color = TEXT_PRIMARY,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            } else {
                                Text("–", fontSize = 12.sp, color = TEXT_TERTIARY)
                            }
                        }
                    }
                    Box(
                        Modifier.width(colWidth).fillMaxHeight()
                            .background(BRAND_PRIMARY_LIGHT.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            formatAmountNoDecimal(row.rowTotal),
                            fontSize = 12.sp,
                            color = BRAND_PRIMARY_DARK,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = DIVIDER_COLOR, thickness = 0.5.dp)
    }
}

@Composable
private fun PivotNoteRow(
    row: PivotRow,
    months: List<String>,
    hState: androidx.compose.foundation.ScrollState,
    leftWidth: Dp,
    colWidth: Dp,
    height: Dp,
    iconUrl: String,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(height)
            .background(SURFACE_CARD),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier
                .width(leftWidth)
                .fillMaxHeight()
                .padding(start = 20.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconView(iconUrl = iconUrl, name = row.note, size = 20.dp)
            Spacer(Modifier.width(6.dp))
            Text(
                row.note,
                fontSize = 12.sp,
                color = TEXT_PRIMARY,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
        Box(Modifier.weight(1f).clipToBounds()) {
            Row(
                Modifier.horizontalScroll(hState).height(height),
                verticalAlignment = Alignment.CenterVertically
            ) {
                months.forEach { mk ->
                    val cell = row.cells[mk]
                    Box(
                        Modifier.width(colWidth).fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (cell != null && cell.total != 0.0) {
                            Text(
                                formatAmountNoDecimal(cell.total),
                                fontSize = 12.sp,
                                color = TEXT_PRIMARY,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1
                            )
                        } else {
                            Text("–", fontSize = 12.sp, color = TEXT_TERTIARY)
                        }
                    }
                }
                Box(
                    Modifier.width(colWidth).fillMaxHeight()
                        .background(BRAND_PRIMARY_LIGHT.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        formatAmountNoDecimal(row.rowTotal),
                        fontSize = 12.sp,
                        color = BRAND_PRIMARY_DARK,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// =========================================================
//  圖片處理函數
// =========================================================
fun createTempImageUri(context: Context): Uri? {
    return try {
        val dir = File(context.cacheDir, "camera").apply { if (!exists()) mkdirs() }
        val file = File(dir, "camera_${System.currentTimeMillis()}.jpg")
        if (!file.exists()) file.createNewFile()
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    } catch (e: Exception) { null }
}

fun scaleCropCenter(src: Bitmap, size: Int): Bitmap {
    val w = src.width; val h = src.height; val md = minOf(w, h)
    val x = (w - md) / 2; val y = (h - md) / 2
    val cropped = if (x == 0 && y == 0 && w == md && h == md) src
                  else Bitmap.createBitmap(src, x, y, md, md)
    val scaled = if (cropped.width == size && cropped.height == size) cropped
                 else Bitmap.createScaledBitmap(cropped, size, size, true)
    if (cropped !== src && cropped !== scaled) cropped.recycle()
    return scaled
}

fun scaleKeepAspectWidth(src: Bitmap, maxWidth: Int): Bitmap {
    if (src.width <= maxWidth) return src
    val ratio = maxWidth.toFloat() / src.width
    val newH = (src.height * ratio).roundToInt().coerceAtLeast(1)
    return Bitmap.createScaledBitmap(src, maxWidth, newH, true)
}

private val segmenterOptions = com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions.Builder()
    .enableForegroundBitmap()
    .build()
private val subjectSegmenter = com.google.mlkit.vision.segmentation.subject.SubjectSegmentation.getClient(segmenterOptions)

suspend fun removeBackground(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
    var argbBitmap: Bitmap? = null
    try {
        argbBitmap = if (bitmap.config == Bitmap.Config.ARGB_8888) bitmap
                     else bitmap.copy(Bitmap.Config.ARGB_8888, true)
        if (argbBitmap == null) return@withContext bitmap
        val image = com.google.mlkit.vision.common.InputImage.fromBitmap(argbBitmap, 0)
        val result = subjectSegmenter.process(image).await()
        result.foregroundBitmap ?: bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        bitmap
    } finally {
        if (argbBitmap != null && argbBitmap !== bitmap) {
            argbBitmap.recycle()
        }
    }
}

suspend fun applyUrlToSameName(context: Context, db: FirebaseFirestore, name: String, url: String) {
    try {
        if (name.isBlank()) return
        val snap = db.collection("records").whereEqualTo("note", name).get().await()
        val batch = db.batch()
        snap.documents.forEach { doc -> batch.update(doc.reference, "iconUrl", url) }
        batch.commit().await()
        Toast.makeText(context,
            if (snap.size() > 1) "圖標已套用到 ${snap.size()} 條同名記錄" else "圖標已更新",
            Toast.LENGTH_SHORT).show()
    } catch (e: Exception) { Toast.makeText(context, "失敗:${e.message}", Toast.LENGTH_LONG).show() }
}

suspend fun removeIconFromSameName(context: Context, db: FirebaseFirestore, name: String) {
    try {
        if (name.isBlank()) { Toast.makeText(context, "冇名稱", Toast.LENGTH_SHORT).show(); return }
        val snap = db.collection("records").whereEqualTo("note", name).get().await()
        val batch = db.batch()
        snap.documents.forEach { doc -> batch.update(doc.reference, "iconUrl", "") }
        batch.commit().await()
        Toast.makeText(context,
            if (snap.size() > 1) "已刪除 ${snap.size()} 條同名記錄嘅圖標" else "圖標已刪除",
            Toast.LENGTH_SHORT).show()
    } catch (e: Exception) { Toast.makeText(context, "失敗:${e.message}", Toast.LENGTH_LONG).show() }
}

suspend fun uploadBytesToCloudinary(bytes: ByteArray): String? = withContext(Dispatchers.IO) {
    try {
        val endpoint = "https://api.cloudinary.com/v1_1/$CLOUDINARY_CLOUD_NAME/image/upload"
        val boundary = "----LedgerBoundary${System.currentTimeMillis()}"
        val lineEnd = "\r\n"
        val conn = URL(endpoint).openConnection() as HttpURLConnection
        conn.requestMethod = "POST"; conn.doOutput = true
        conn.connectTimeout = 30_000; conn.readTimeout = 60_000
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
        conn.outputStream.use { o ->
            o.write("--$boundary$lineEnd".toByteArray())
            o.write(("Content-Disposition: form-data; name=\"upload_preset\"$lineEnd$lineEnd").toByteArray())
            o.write("$CLOUDINARY_UPLOAD_PRESET$lineEnd".toByteArray())
            o.write("--$boundary$lineEnd".toByteArray())
            o.write(("Content-Disposition: form-data; name=\"file\"; filename=\"icon.png\"$lineEnd").toByteArray())
            o.write("Content-Type: image/png$lineEnd$lineEnd".toByteArray())
            o.write(bytes); o.write("$lineEnd".toByteArray())
            o.write("--$boundary--$lineEnd".toByteArray()); o.flush()
        }
        val code = conn.responseCode
        val text = if (code in 200..299) conn.inputStream.bufferedReader().use { it.readText() }
                   else conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
        if (code in 200..299) {
            val url = org.json.JSONObject(text).optString("secure_url")
            if (url.isNotBlank()) url.replace(".jpg", ".png").replace(".jpeg", ".png") else null
        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// =========================================================
//  IconView + IconViewAdaptive
// =========================================================
private val AVATAR_TEXT_STYLE_BASE = TextStyle(
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.Both
    )
)

@Composable
fun IconView(iconUrl: String, name: String, size: Dp = 40.dp) {
    val shape = RoundedCornerShape(percent = 28)

    if (iconUrl.isBlank()) {
        val ch = remember(name) { name.trim().take(1).ifBlank { "?" } }
        val bgColor = remember(name) { avatarColor(name) }
        Box(Modifier.size(size).clip(shape).background(bgColor),
            contentAlignment = Alignment.Center) {
            Text(
                text = ch, color = Color.White,
                fontSize = (size.value * 0.42f).sp, fontWeight = FontWeight.Bold,
                style = AVATAR_TEXT_STYLE_BASE
            )
        }
    } else {
        val ctx = LocalContext.current
        val density = LocalDensity.current
        val sizePx = remember(density, size) { with(density) { size.roundToPx() } }

        val req = remember(iconUrl, ctx, sizePx) {
            ImageRequest.Builder(ctx)
                .data(iconUrl)
                .memoryCacheKey("$iconUrl@$sizePx")
                .diskCacheKey(iconUrl)
                .size(sizePx, sizePx)
                .crossfade(false)
                .build()
        }
        AsyncImage(model = req, contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(size).clip(shape))
    }
}

@Composable
fun IconViewAdaptive(
    iconUrl: String, name: String, modifier: Modifier = Modifier,
) {
    Box(modifier.clip(RoundedCornerShape(percent = 28)), contentAlignment = Alignment.Center) {
        if (iconUrl.isBlank()) {
            val ch = remember(name) { name.trim().take(1).ifBlank { "?" } }
            val bgColor = remember(name) { avatarColor(name) }
            Box(Modifier.fillMaxSize().background(bgColor),
                contentAlignment = Alignment.Center) {
                Text(
                    text = ch, color = Color.White,
                    fontSize = 9.sp, fontWeight = FontWeight.Bold,
                    style = AVATAR_TEXT_STYLE_BASE
                )
            }
        } else {
            val ctx = LocalContext.current
            val req = remember(iconUrl, ctx) {
                ImageRequest.Builder(ctx)
                    .data(iconUrl)
                    .memoryCacheKey("$iconUrl@48")
                    .diskCacheKey(iconUrl)
                    .size(48, 48)
                    .crossfade(false)
                    .build()
            }
            AsyncImage(model = req, contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize())
        }
    }
}

// =========================================================
//  ★ 圖片效果選擇面板 + 相關數據類和處理函數
// =========================================================
data class PendingImageUpload(
    val record: Record,
    val originalBitmap: Bitmap,
    val processedBitmap: Bitmap
)

/** 讀取用戶選的圖片（相冊/相機），自動去背，返回 (原圖, 去背圖) */
suspend fun loadAndProcessBitmaps(context: Context, uri: Uri): Pair<Bitmap, Bitmap>? = withContext(Dispatchers.IO) {
    try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        val w = bounds.outWidth; val h = bounds.outHeight
        if (w <= 0 || h <= 0) return@withContext null

        var s = 1; val md = maxOf(w, h)
        while (md / (s * 2) >= 800) s *= 2

        val opts = BitmapFactory.Options().apply {
            inSampleSize = s
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val src = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opts)
        } ?: return@withContext null

        val original = if (src.width > 800 || src.height > 800) {
            val scaled = scaleKeepAspectWidth(src, 800)
            if (scaled !== src) src.recycle()
            scaled
        } else src

        val processed = removeBackground(original)
        Pair(original, processed)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/** 強化去背 / 提亮主體：用 ColorMatrix 提高飽和與對比 */
suspend fun applyEnhancedFilter(src: Bitmap): Bitmap = withContext(Dispatchers.Default) {
    val bmp = Bitmap.createBitmap(src.width, src.height, src.config ?: Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bmp)
    val paint = android.graphics.Paint()
    val cm = android.graphics.ColorMatrix().apply {
        set(floatArrayOf(
            1.15f, 0f, 0f, 0f, -5f,
            0f, 1.15f, 0f, 0f, -5f,
            0f, 0f, 1.15f, 0f, -5f,
            0f, 0f, 0f, 1f, 0f
        ))
    }
    paint.colorFilter = android.graphics.ColorMatrixColorFilter(cm)
    canvas.drawBitmap(src, 0f, 0f, paint)
    bmp
}

/** 把最終選定的 bitmap 壓成 PNG bytes 上傳 */
suspend fun compressBitmapToPngForUpload(bitmap: Bitmap, forceCrop: Boolean, targetSize: Int = ICON_SIZE): ByteArray = withContext(Dispatchers.Default) {
    val finalBitmap = if (forceCrop) {
        scaleCropCenter(bitmap, targetSize)
    } else {
        if (bitmap.width > bitmap.height) scaleKeepAspectWidth(bitmap, targetSize)
        else scaleCropCenter(bitmap, targetSize)
    }
    val baos = ByteArrayOutputStream()
    finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
    if (finalBitmap !== bitmap) finalBitmap.recycle()
    baos.toByteArray()
}

@Composable
fun ImageEffectSelectionPanel(
    state: PendingImageUpload,
    onSelect: (Bitmap, Boolean, Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    var showCropDialog by remember { mutableStateOf(false) }
    var dismissed by remember { mutableStateOf(false) }

    val whiteRemovedBitmap by produceState<Bitmap?>(initialValue = null, key1 = state.originalBitmap) {
        value = withContext(Dispatchers.Default) {
            removeWhiteBackgroundSimple(state.originalBitmap)
        }
    }

    val tightCroppedEnhancedBitmap by produceState<Bitmap?>(
        initialValue = null,
        key1 = state.processedBitmap
    ) {
        value = withContext(Dispatchers.Default) {
            try {
                val enhanced = applyEnhancedFilter(state.processedBitmap)
                val cropped = tightCropAroundSubject(enhanced)
                if (cropped !== enhanced) enhanced.recycle()
                cropped
            } catch (e: Exception) {
                state.processedBitmap
            }
        }
    }

    fun safeSelect(bitmap: Bitmap, forceCrop: Boolean, isEnhanced: Boolean) {
        if (dismissed) return
        dismissed = true
        onSelect(bitmap, forceCrop, isEnhanced)
    }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(spring(dampingRatio = 0.8f, stiffness = 300f)) { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(SURFACE_CARD)
                .padding(bottom = 32.dp, top = 20.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("選擇圖片效果", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TEXT_PRIMARY)
                Spacer(Modifier.weight(1f))
                Text("點選使用 / 手動裁剪", fontSize = 12.sp, color = TEXT_SECONDARY)
            }
            Spacer(Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                item {
                    EffectOptionCard(
                        title = "自動去背圖", bitmap = state.processedBitmap,
                        buttonText = "使用去背圖", isPrimary = true,
                        onClick = { safeSelect(state.processedBitmap, false, false) }
                    )
                }
                item {
                    val preview = whiteRemovedBitmap ?: state.originalBitmap
                    EffectOptionCard(
                        title = if (whiteRemovedBitmap == null) "去白底（計算中）" else "簡單去白底",
                        bitmap = preview,
                        buttonText = "使用去白底",
                        isPrimary = false,
                        onClick = {
                            scope.launch {
                                val target = whiteRemovedBitmap ?: withContext(Dispatchers.Default) {
                                    removeWhiteBackgroundSimple(state.originalBitmap)
                                }
                                safeSelect(target, false, false)
                            }
                        }
                    )
                }
                item {
                    EffectOptionCard(
                        title = "手動裁剪", bitmap = state.originalBitmap,
                        buttonText = "手動裁剪", isPrimary = false, showCropGrid = true,
                        onClick = { showCropDialog = true }
                    )
                }
                item {
                    val preview = tightCroppedEnhancedBitmap ?: state.processedBitmap
                    EffectOptionCard(
                        title = if (tightCroppedEnhancedBitmap == null)
                                    "裁剪強化（計算中）"
                                else
                                    "緊裁 + 強化",
                        bitmap = preview,
                        buttonText = "使用緊裁版",
                        isPrimary = false,
                        onClick = {
                            scope.launch {
                                val target = tightCroppedEnhancedBitmap ?: withContext(Dispatchers.Default) {
                                    try {
                                        val enhanced = applyEnhancedFilter(state.processedBitmap)
                                        tightCropAroundSubject(enhanced)
                                    } catch (e: Exception) {
                                        state.processedBitmap
                                    }
                                }
                                safeSelect(target, false, false)
                            }
                        }
                    )
                }
            }
        }
    }

    if (showCropDialog) {
        CropDialog(
            sourceBitmap = state.originalBitmap,
            onDismiss = { showCropDialog = false },
            onCropped = { cropped ->
                showCropDialog = false
                safeSelect(cropped, false, false)
            }
        )
    }
}

@Composable
fun EffectOptionCard(
    title: String, bitmap: Bitmap, buttonText: String, isPrimary: Boolean,
    timeHint: String = "", showCropGrid: Boolean = false, isEnhanced: Boolean = false,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.width(140.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(16.dp))
                .background(SURFACE_ELEVATED)
                .border(2.dp, if (isPrimary) BRAND_PRIMARY else Color.Transparent, RoundedCornerShape(16.dp))
                .clickable { onClick() }
        ) {
            val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }
            val colorFilter = remember(isEnhanced) {
                if (isEnhanced) {
                    ColorFilter.colorMatrix(
                        ColorMatrix().apply { setToScale(1.15f, 1.15f, 1.15f, 1f) }
                    )
                } else null
            }

            Image(
                bitmap = imageBitmap, contentDescription = null,
                contentScale = ContentScale.Fit, colorFilter = colorFilter,
                modifier = Modifier.fillMaxSize().drawWithContent {
                    drawContent()
                    if (showCropGrid) {
                        val strokeW = 1.dp.toPx()
                        val w = size.width; val h = size.height
                        drawLine(Color.White.copy(alpha = 0.6f), Offset(w / 3, 0f), Offset(w / 3, h), strokeWidth = strokeW)
                        drawLine(Color.White.copy(alpha = 0.6f), Offset(w * 2 / 3, 0f), Offset(w * 2 / 3, h), strokeWidth = strokeW)
                        drawLine(Color.White.copy(alpha = 0.6f), Offset(0f, h / 3), Offset(w, h / 3), strokeWidth = strokeW)
                        drawLine(Color.White.copy(alpha = 0.6f), Offset(0f, h * 2 / 3), Offset(w, h * 2 / 3), strokeWidth = strokeW)

                        drawRect(Color.Black.copy(alpha = 0.35f), size = size)

                        val md = minOf(w, h) * 0.8f
                        val cx = w / 2; val cy = h / 2; val half = md / 2
                        drawRect(
                            Color.White,
                            topLeft = Offset(cx - half, cy - half),
                            size = androidx.compose.ui.geometry.Size(md, md),
                            style = Stroke(2.dp.toPx())
                        )
                    }
                }
            )

            if (isPrimary && timeHint.isNotEmpty()) {
                Box(
                    Modifier.align(Alignment.TopEnd).padding(8.dp)
                        .clip(RoundedCornerShape(8.dp)).background(BRAND_PRIMARY)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("推薦", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(title, fontSize = 12.sp, color = TEXT_SECONDARY, fontWeight = FontWeight.Medium, maxLines = 1)
        Spacer(Modifier.height(6.dp))

        Button(
            onClick = onClick, shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(36.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPrimary) BRAND_PRIMARY else SURFACE_ELEVATED,
                contentColor = if (isPrimary) Color.White else TEXT_PRIMARY
            )
        ) {
            Text(
                if (isPrimary && timeHint.isNotEmpty()) "$buttonText $timeHint" else buttonText,
                fontSize = 13.sp, fontWeight = FontWeight.Bold
            )
        }
    }
}

suspend fun removeWhiteBackgroundSimple(
    src: Bitmap,
    tolerance: Int = 30
): Bitmap = withContext(Dispatchers.Default) {
    val w = src.width
    val h = src.height
    if (w <= 0 || h <= 0) return@withContext src

    val result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val pixels = IntArray(w * h)
    src.getPixels(pixels, 0, w, 0, 0, w, h)
    val out = IntArray(w * h)
    val threshold = 255 - tolerance

    for (i in pixels.indices) {
        val p = pixels[i]
        val r = (p shr 16) and 0xFF
        val g = (p shr 8) and 0xFF
        val b = p and 0xFF
        out[i] = if (r >= threshold && g >= threshold && b >= threshold) {
            0x00000000
        } else {
            p
        }
    }
    result.setPixels(out, 0, w, 0, 0, w, h)
    result
}

@Composable
fun CropDialog(
    sourceBitmap: Bitmap,
    onDismiss: () -> Unit,
    onCropped: (Bitmap) -> Unit
) {
    val density = LocalDensity.current
    var userScale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val imageBitmap = remember(sourceBitmap) { sourceBitmap.asImageBitmap() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BoxWithConstraints(Modifier.fillMaxSize().background(Color.Black)) {
            val screenW = with(density) { maxWidth.toPx() }
            val screenH = with(density) { maxHeight.toPx() }
            val boxSize = minOf(screenW, screenH) * 0.85f
            val boxLeft = (screenW - boxSize) / 2f
            val boxTop = (screenH - boxSize) / 2f

            val bitmapW = sourceBitmap.width.toFloat()
            val bitmapH = sourceBitmap.height.toFloat()
            val baseScale = boxSize / minOf(bitmapW, bitmapH)
            val finalScale = baseScale * userScale
            val displayW = bitmapW * finalScale
            val displayH = bitmapH * finalScale

            Box(
                Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val newUserScale = (userScale * zoom).coerceIn(1f, 5f)
                            val newFinalScale = baseScale * newUserScale
                            val newW = bitmapW * newFinalScale
                            val newH = bitmapH * newFinalScale
                            val newMaxOffX = maxOf(0f, (newW - boxSize) / 2f)
                            val newMaxOffY = maxOf(0f, (newH - boxSize) / 2f)
                            userScale = newUserScale
                            offsetX = (offsetX + pan.x).coerceIn(-newMaxOffX, newMaxOffX)
                            offsetY = (offsetY + pan.y).coerceIn(-newMaxOffY, newMaxOffY)
                        }
                    }
            ) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .requiredSize(
                            with(density) { displayW.toDp() },
                            with(density) { displayH.toDp() }
                        )
                        .align(Alignment.Center)
                        .graphicsLayer {
                            translationX = offsetX
                            translationY = offsetY
                        }
                )
            }

            Canvas(Modifier.fillMaxSize()) {
                val maskColor = Color.Black.copy(alpha = 0.6f)
                drawRect(maskColor, topLeft = Offset(0f, 0f), size = Size(size.width, boxTop))
                drawRect(maskColor, topLeft = Offset(0f, boxTop + boxSize),
                    size = Size(size.width, size.height - boxTop - boxSize))
                drawRect(maskColor, topLeft = Offset(0f, boxTop), size = Size(boxLeft, boxSize))
                drawRect(maskColor, topLeft = Offset(boxLeft + boxSize, boxTop),
                    size = Size(size.width - boxLeft - boxSize, boxSize))

                drawRect(
                    Color.White,
                    topLeft = Offset(boxLeft, boxTop),
                    size = Size(boxSize, boxSize),
                    style = Stroke(2.dp.toPx())
                )

                val gridColor = Color.White.copy(alpha = 0.45f)
                val gridW = 1.dp.toPx()
                drawLine(gridColor, Offset(boxLeft + boxSize / 3f, boxTop),
                    Offset(boxLeft + boxSize / 3f, boxTop + boxSize), gridW)
                drawLine(gridColor, Offset(boxLeft + boxSize * 2f / 3f, boxTop),
                    Offset(boxLeft + boxSize * 2f / 3f, boxTop + boxSize), gridW)
                drawLine(gridColor, Offset(boxLeft, boxTop + boxSize / 3f),
                    Offset(boxLeft + boxSize, boxTop + boxSize / 3f), gridW)
                drawLine(gridColor, Offset(boxLeft, boxTop + boxSize * 2f / 3f),
                    Offset(boxLeft + boxSize, boxTop + boxSize * 2f / 3f), gridW)
            }

            Row(
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, "取消", tint = Color.White)
                }
                Text("裁剪圖片", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Text("拖動 / 雙指縮放", color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp)
            }

            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SURFACE_ELEVATED,
                        contentColor = TEXT_PRIMARY
                    )
                ) { Text("取消", fontSize = 15.sp, fontWeight = FontWeight.Medium) }

                Button(
                    onClick = {
                        val cropSizePx = (boxSize / finalScale).roundToInt().coerceAtLeast(1)
                        val actualSize = minOf(
                            cropSizePx,
                            sourceBitmap.width,
                            sourceBitmap.height
                        ).coerceAtLeast(1)
                        val leftF = ((displayW - boxSize) / 2f - offsetX) / finalScale
                        val topF = ((displayH - boxSize) / 2f - offsetY) / finalScale
                        val left = leftF.roundToInt().coerceIn(0, sourceBitmap.width - actualSize)
                        val top = topF.roundToInt().coerceIn(0, sourceBitmap.height - actualSize)
                        val cropped = Bitmap.createBitmap(sourceBitmap, left, top, actualSize, actualSize)
                        onCropped(cropped)
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BRAND_PRIMARY,
                        contentColor = Color.White
                    )
                ) { Text("裁剪", fontSize = 15.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

suspend fun tightCropAroundSubject(
    src: Bitmap,
    paddingPercent: Float = 0.08f,
    alphaThreshold: Int = 20
): Bitmap = withContext(Dispatchers.Default) {
    val w = src.width
    val h = src.height
    if (w <= 0 || h <= 0) return@withContext src

    val pixels = IntArray(w * h)
    src.getPixels(pixels, 0, w, 0, 0, w, h)

    var minX = w
    var minY = h
    var maxX = -1
    var maxY = -1

    for (y in 0 until h) {
        val rowStart = y * w
        for (x in 0 until w) {
            val a = (pixels[rowStart + x] ushr 24) and 0xFF
            if (a >= alphaThreshold) {
                if (x < minX) minX = x
                if (x > maxX) maxX = x
                if (y < minY) minY = y
                if (y > maxY) maxY = y
            }
        }
    }

    if (maxX < 0 || maxY < 0) return@withContext src

    val subjectW = maxX - minX + 1
    val subjectH = maxY - minY + 1

    val side = maxOf(subjectW, subjectH)
    val padding = (side * paddingPercent).toInt().coerceAtLeast(2)
    val finalSide = side + padding * 2

    val cx = (minX + maxX) / 2f
    val cy = (minY + maxY) / 2f

    val result = Bitmap.createBitmap(finalSide, finalSide, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(result)
    val dx = finalSide / 2f - cx
    val dy = finalSide / 2f - cy
    canvas.drawBitmap(src, dx, dy, null)
    result
}

@Composable
private fun WeeklyExpenseBarBody(
    day: WeeklyExpenseDay,
    maxAmount: Double,
    introProgress: Float,
    isSelected: Boolean = false,
    isAnySelected: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val targetRatio = (day.amount / maxAmount).toFloat().coerceIn(0f, 1f)
    val animatedRatio by animateFloatAsState(
        targetValue = targetRatio,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 260f),
        label = "weeklyBarRatio_${day.dayOfMonth}"
    )
    val displayRatio = (animatedRatio * introProgress).coerceIn(0f, 1f)
    val hasAmount = day.amount > 0.0

    val dimAlpha by animateFloatAsState(
        targetValue = if (!isAnySelected || isSelected) 1f else 0.45f,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "barDim_${day.dayOfMonth}"
    )

    val barShape = RoundedCornerShape(
        topStart = 6.dp, topEnd = 6.dp,
        bottomStart = 2.dp, bottomEnd = 2.dp
    )

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        val maxBarHeight = maxHeight
        val minBarHeight = 3.dp
        val barHeight = if (hasAmount) {
            (maxBarHeight * displayRatio).coerceAtLeast(minBarHeight)
        } else 0.dp

        Box(
            modifier = Modifier
                .width(22.dp)
                .fillMaxHeight()
                .graphicsLayer { alpha = dimAlpha }
                .clip(barShape)
                .background(BRAND_PRIMARY_LIGHT.copy(alpha = 0.3f))
        )

        if (barHeight > 0.dp) {
            Box(
                modifier = Modifier
                    .width(22.dp)
                    .height(barHeight)
                    .graphicsLayer { alpha = dimAlpha }
                    .clip(barShape)
                    .background(
                        if (day.isToday) {
                            Brush.verticalGradient(
                                listOf(BRAND_PRIMARY_DARK, BRAND_PRIMARY.copy(alpha = 0.85f))
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(
                                    BRAND_PRIMARY.copy(alpha = 0.85f),
                                    BRAND_PRIMARY.copy(alpha = 0.55f)
                                )
                            )
                        }
                    )
            )
        }
    }
}

@Composable
fun RollingNumber(
    text: String,
    color: Color,
    fontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Bold,
    animateChanges: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val maxLen = remember { mutableIntStateOf(text.length.coerceAtLeast(1)) }
    LaunchedEffect(text.length) {
        if (text.length > maxLen.value) maxLen.value = text.length
    }

    val totalSlots = maxLen.value

    val compactStyle = remember(fontSize, fontWeight) {
        TextStyle(
            fontSize = fontSize,
            fontWeight = fontWeight,
            // ★ Top Stat 等寬數字（tabular figures）
            fontFeatureSettings = "tnum",
            fontFamily = FontFamily.Monospace,
            platformStyle = PlatformTextStyle(includeFontPadding = false),
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.Both
            )
        )
    }

    val textMeasurer = androidx.compose.ui.text.rememberTextMeasurer()
    val density = LocalDensity.current
    val cellHeight: Dp = remember(compactStyle) {
        val layout = textMeasurer.measure("0", compactStyle)
        with(density) { layout.size.height.toDp() }
    }

    val currentAnimate by rememberUpdatedState(animateChanges)
    var displayedText by remember { mutableStateOf(text) }

    // ★ 唔再 finally 強制跳字：取消時保留已顯示進度，新 effect 由當前 displayedText 繼續
    LaunchedEffect(text, animateChanges) {
        if (!animateChanges) {
            displayedText = text
            return@LaunchedEffect
        }
        val target = text
        val current = displayedText
        if (target == current) return@LaunchedEffect
        // 刪字 / 非前綴：即刻對齊，由每位動畫負責出入場
        if (target.length <= current.length || !target.startsWith(current)) {
            displayedText = target
            return@LaunchedEffect
        }
        // 一次補多個字先做輕量打字機；中途被取消唔強制跳字
        var shown = current
        while (shown.length < target.length && isActive) {
            if (!target.startsWith(shown)) {
                displayedText = target
                return@LaunchedEffect
            }
            shown = target.substring(0, shown.length + 1)
            displayedText = shown
            if (shown.length < target.length) delay(40)
        }
        if (isActive && displayedText != target) displayedText = target
    }

    Row(
        modifier = modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        for (i in 0 until totalSlots) {
            val char = displayedText.getOrNull(i)
            key(i) {
                val delayMs = (totalSlots - 1 - i) * 8
                RollingDigit(
                    targetChar = char,
                    color = color,
                    style = compactStyle,
                    cellHeight = cellHeight,
                    animate = currentAnimate,
                    delayMs = delayMs,
                    modifier = Modifier.wrapContentWidth()
                )
            }
        }
    }
}

@Composable
fun RollingDigit(
    targetChar: Char?,
    color: Color,
    style: TextStyle,
    cellHeight: Dp,
    animate: Boolean,
    delayMs: Int,
    modifier: Modifier = Modifier,
) {
    val targetDigit: Int? = targetChar?.digitToIntOrNull()

    if (targetDigit != null) {
        val digit: Int = targetDigit
        val continuousValue = remember { Animatable(0f) }
        var baseDigit by remember { mutableIntStateOf(digit) }

        // ★ 保證無論動畫被取消，最終一定顯示完整目標數字，絕不中途停留
        LaunchedEffect(digit, animate) {
            try {
                if (!animate) {
                    baseDigit = digit
                    continuousValue.snapTo(0f)
                    return@LaunchedEffect
                }
                if (baseDigit == digit) {
                    continuousValue.snapTo(0f)
                    return@LaunchedEffect
                }
                val steps = (digit - baseDigit + 10) % 10
                if (steps == 0) {
                    baseDigit = digit
                    continuousValue.snapTo(0f)
                    return@LaunchedEffect
                }
                val durationMs = (180 + steps * 60).coerceIn(180, 700)
                if (delayMs > 0) delay(delayMs.toLong())
                continuousValue.snapTo(0f)
                continuousValue.animateTo(
                    targetValue = steps.toFloat(),
                    animationSpec = tween(durationMs, easing = FastOutSlowInEasing)
                )
            } finally {
                // 動畫被取消或正常結束，都強制落地到完整數字
                baseDigit = digit
                continuousValue.snapTo(0f)
            }
        }

        val progress = continuousValue.value
        val progressInt = progress.toInt()
        val progressFrac = progress - progressInt

        val currentDigit = (baseDigit + progressInt) % 10
        val nextDigit = (baseDigit + progressInt + 1) % 10

        val isAnimating = progressFrac > 0.001f

        Box(
            modifier = modifier
                .height(cellHeight)
                .wrapContentWidth()
                .clipToBounds(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentDigit.toString(),
                color = color,
                style = style,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .offset(y = (-progressFrac * cellHeight.value).dp)
                    .wrapContentWidth()
            )
            if (isAnimating) {
                Text(
                    text = nextDigit.toString(),
                    color = color,
                    style = style,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier
                        .offset(y = ((1f - progressFrac) * cellHeight.value).dp)
                        .wrapContentWidth()
                )
            }
        }
    } else {
        if (targetChar != null) {
            Text(
                text = targetChar.toString(),
                color = color,
                style = style,
                maxLines = 1,
                softWrap = false,
                modifier = modifier.height(cellHeight).wrapContentWidth(),
            )
        } else {
            Spacer(Modifier.width(0.dp))
        }
    }
}

@Composable
fun AnimatedAmount(
    text: String, color: Color, fontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Bold,
    animateChanges: Boolean = true,
    modifier: Modifier = Modifier,
) {
    // ★ 預先保留足夠 slot，確保新數字永遠走 null→char 過渡（有入場動畫）
    val maxSlots = remember { mutableIntStateOf(maxOf(text.length + 4, 12)) }
    LaunchedEffect(text.length) {
        if (text.length + 2 > maxSlots.value) maxSlots.value = text.length + 4
    }

    val currentAnimate by rememberUpdatedState(animateChanges)

    var displayedText by remember { mutableStateOf(text) }

    LaunchedEffect(text, animateChanges) {
        if (!animateChanges) {
            displayedText = text
            return@LaunchedEffect
        }
        val target = text
        val current = displayedText
        if (target == current) return@LaunchedEffect

        // 刪字、改中間、非前綴：即刻對齊，由每位 AnimatedContent 負責出入場
        if (target.length <= current.length || !target.startsWith(current)) {
            displayedText = target
            return@LaunchedEffect
        }

        // 一次補多個字（例如外部一次 set 成完整金額）：逐字補，間隔短啲保持跟手
        var shown = current
        while (shown.length < target.length && isActive) {
            if (!target.startsWith(shown)) {
                displayedText = target
                return@LaunchedEffect
            }
            shown = target.substring(0, shown.length + 1)
            displayedText = shown
            if (shown.length < target.length) {
                delay(40)
            }
        }
        if (isActive && displayedText != target) {
            displayedText = target
        }
    }

    val style = remember(fontSize, fontWeight) {
        TextStyle(
            fontSize = fontSize,
            fontWeight = fontWeight,
            platformStyle = PlatformTextStyle(includeFontPadding = false),
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.Both
            )
        )
    }

    Row(
        modifier = modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        for (i in 0 until maxSlots.value) {
            val char = displayedText.getOrNull(i)
            key(i) {
                AnimatedContent(
                    targetState = char,
                    transitionSpec = {
                        if (!currentAnimate) {
                            fadeIn(tween(0)) togetherWith fadeOut(tween(0))
                        } else {
                            val initialDigit = initialState?.digitToIntOrNull() ?: -1
                            val targetDigit = targetState?.digitToIntOrNull() ?: -1
                            val goingUp = when {
                                initialDigit >= 0 && targetDigit >= 0 -> targetDigit >= initialDigit
                                else -> true
                            }
                            when {
                                targetState != null && initialState == null -> {
                                    (slideInVertically(DIGIT_SLIDE_ENTER) { it / 2 } +
                                     fadeIn(tween(200)) +
                                     scaleIn(initialScale = 0.4f, animationSpec = DIGIT_FLOAT_ENTER)) togetherWith fadeOut(tween(0))
                                }
                                targetState == null && initialState != null -> {
                                    fadeIn(tween(0)) togetherWith
                                    (slideOutVertically(DIGIT_SLIDE_EXIT) { it / 2 } +
                                     fadeOut(tween(180)) +
                                     scaleOut(targetScale = 0.4f, animationSpec = DIGIT_FLOAT_EXIT))
                                }
                                goingUp -> {
                                    (slideInVertically(DIGIT_SLIDE_ENTER) { it } + fadeIn(tween(220))) togetherWith
                                    (slideOutVertically(DIGIT_SLIDE_EXIT) { -it } + fadeOut(tween(220)))
                                }
                                else -> {
                                    (slideInVertically(DIGIT_SLIDE_ENTER) { -it } + fadeIn(tween(220))) togetherWith
                                    (slideOutVertically(DIGIT_SLIDE_EXIT) { it } + fadeOut(tween(220)))
                                }
                            }.using(SizeTransform(clip = false, sizeAnimationSpec = { _, _ -> DIGIT_SIZE_SPRING }))
                        }
                    },
                    label = "digit_$i"
                ) { c ->
                    if (c != null) {
                        Text(
                            text = c.toString(), color = color, style = style,
                            maxLines = 1, softWrap = false,
                            modifier = Modifier.wrapContentWidth()
                        )
                    } else {
                        Spacer(Modifier.width(0.dp))
                    }
                }
            }
        }
    }
}
