package com.example.hello

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
import android.widget.Toast
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
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.animateScrollBy
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
import androidx.compose.ui.graphics.graphicsLayer
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
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
import kotlinx.coroutines.flow.distinctUntilChanged
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

// ===== 由通知／快捷入口啟動時嘅標記（top-level state） =====
private val keyboardLaunchToken = mutableStateOf(0)
private val keyboardPrefillNote = mutableStateOf<String?>(null)
private val keyboardPrefillAmount = mutableStateOf<Double?>(null)
private val keyboardPrefillCategory = mutableStateOf<String?>(null)

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
        appContext?.let {
            try { refreshQuickAddNotification(it) } catch (_: Exception) {}
        }
    }

    fun formatAmountForLabel(v: Double): String =
        String.format(java.util.Locale.US, "%.2f", v)
            .trimEnd('0').trimEnd('.').ifEmpty { "0" }
}

private const val QUICK_ADD_CHANNEL_ID = "ledger_quick_add"
private const val CONFIRM_CHANNEL_ID = "ledger_capture_confirm_v3"
private const val QUICK_ADD_NOTIFICATION_ID = 1001
private const val CONFIRM_NOTIFICATION_BASE_ID = 2000

fun ensureNotificationChannels(context: Context) {
    if (Build.VERSION.SDK_INT < 26) return
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
    try {
        val staleIds = listOf(
            "ledger_capture_confirm",
            "ledger_capture_confirm_v2",
            "ledger_capture_confirm_v3_old"
        )
        for (oldId in staleIds) {
            if (oldId == CONFIRM_CHANNEL_ID) continue
            try {
                if (nm.getNotificationChannel(oldId) != null) {
                    nm.deleteNotificationChannel(oldId)
                }
            } catch (_: Exception) {}
        }
    } catch (_: Exception) {}

    val quick = NotificationChannel(
        QUICK_ADD_CHANNEL_ID, "快速記帳", NotificationManager.IMPORTANCE_LOW
    ).apply {
        description = "常駐快速記賬入口"
        setShowBadge(false)
    }
    val confirm = NotificationChannel(
        CONFIRM_CHANNEL_ID, "消費確認", NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "檢測到消費通知時，詢問你是否記賬"
        enableVibration(true)
        enableLights(true)
    }
    nm.createNotificationChannel(quick)
    nm.createNotificationChannel(confirm)
}

// 🟢 只清已知固定 ID，唔好 loop 10 萬次
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
    prefillAmount: Double? = null, prefillCategory: String? = null
): Intent {
    return Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra(MainActivity.EXTRA_OPEN_KEYBOARD, true)
        if (!prefillNote.isNullOrBlank()) putExtra(MainActivity.EXTRA_PREFILL_NOTE, prefillNote)
        if (!prefillCategory.isNullOrBlank()) putExtra(MainActivity.EXTRA_PREFILL_CATEGORY, prefillCategory)
        if (prefillAmount != null && prefillAmount > 0.0) putExtra(MainActivity.EXTRA_PREFILL_AMOUNT, prefillAmount)
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

        val notifId = CONFIRM_NOTIFICATION_BASE_ID
        val amountStr = NotificationCaptureBridge.formatAmountForLabel(captured.amount)
        val title = "檢測到消費：${captured.note} · HKD $amountStr"

        val piFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val addIntent = buildLaunchIntentForMain(
            context, captured.note, captured.amount, captured.category
        )
        val addPi = PendingIntent.getActivity(context, notifId + 1, addIntent, piFlags)
        val fullScreenPi = PendingIntent.getActivity(context, notifId + 1, addIntent, piFlags)

        val ignoreIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "com.example.hello.ACTION_IGNORE_CAPTURE"
            putExtra("notif_id", notifId)
        }
        val ignorePi = PendingIntent.getBroadcast(context, notifId + 2, ignoreIntent, piFlags)

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
            .setTimeoutAfter(5 * 60 * 1000L)
            .addAction(android.R.drawable.ic_menu_save, "✅ 記賬", addPi)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "❌ 忽略", ignorePi)

        nm.notify(notifId, builder.build())
    } catch (_: Exception) {}
}

private val SWIPE_SPRING = spring<Float>(
    stiffness = Spring.StiffnessMediumLow,
    dampingRatio = Spring.DampingRatioNoBouncy
)

// ===== 配色（iOS 簡約風格）=====
val BRAND_PRIMARY = Color(0xFF6366F1)
val BRAND_PRIMARY_DARK = Color(0xFF4F46E5)
val BRAND_PRIMARY_LIGHT = Color(0xFFE0E7FF)

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

val CATEGORIES = listOf("收入", "娛樂", "家用", "飲食", "交通", "個人", "購物", "月費", "旅遊")
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
    "月費" to CategoryStyle(Icons.Default.Autorenew, Color(0xFFE2E8F0), Color(0xFF334155)),
    "旅遊" to CategoryStyle(Icons.Default.Flight, Color(0xFFCCFBF1), Color(0xFF115E59))
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
    val availableMonths: List<String> = emptyList(),
    val visibleCategories: List<String> = emptyList(),
    val topQuickNotesInfo: List<QuickNoteInfo> = emptyList(),
)

@Immutable
data class NoteIndex(
    val recentAmountByNote: Map<String, Double> = emptyMap(),
    val noteIconMap: Map<String, String> = emptyMap(),
    val allNoteNames: List<String> = emptyList(),
    val noteCategoryMap: Map<String, String> = emptyMap(),
    val topNotes: List<Pair<String, Int>> = emptyList(),
    val topQuickNotesInfo: List<QuickNoteInfo> = emptyList(),
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

private val headerBlurEffectCache = ConcurrentHashMap<Int, androidx.compose.ui.graphics.RenderEffect>(32)
private fun getCachedHeaderBlurEffect(blurRadiusInt: Int): androidx.compose.ui.graphics.RenderEffect? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || blurRadiusInt <= 0) return null
    return headerBlurEffectCache.getOrPut(blurRadiusInt) {
        val rx = blurRadiusInt * 0.3f
        val ry = blurRadiusInt * 1.5f
        android.graphics.RenderEffect.createBlurEffect(
            rx, ry, android.graphics.Shader.TileMode.CLAMP
        ).asComposeRenderEffect()
    }
}

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

    private val _justAddedIds = MutableStateFlow<Set<String>>(emptySet())
    val justAddedIds: StateFlow<Set<String>> = _justAddedIds.asStateFlow()

    private val _deletingIds = MutableStateFlow<Set<String>>(emptySet())
    val deletingIds: StateFlow<Set<String>> = _deletingIds.asStateFlow()

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

    private val noteIndexState: StateFlow<NoteIndex> = _records
        .map { records -> buildNoteIndex(records) }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NoteIndex())

    val ledgerUiState: StateFlow<LedgerUiState> = combine(
        _records, noteIndexState, _loading, _filter, _deletingIds
    ) { records, noteIndex, loading, filter, deletingIds ->
        computeLedgerUiState(records, noteIndex, loading, filter, deletingIds)
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LedgerUiState())

    val compareUiState: StateFlow<CompareUiState> = combine(
        _records, _compareMonthA, _compareMonthB
    ) { records, a, b -> computeCompareUiState(records, a, b) }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CompareUiState())

    val calendarUiState: StateFlow<CalendarUiState> = combine(
        _records, _calendarMonthKey, _calendarFilterCategory
    ) { records, mk, cat -> computeCalendarUiState(records, mk, cat) }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CalendarUiState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val savedCategory = prefs.getString("last_category", "飲食") ?: "飲食"
            val savedMonthA = prefs.getString("compare_month_a", _defaultMonthA) ?: _defaultMonthA
            val savedMonthB = prefs.getString("compare_month_b", _defaultMonthB) ?: _defaultMonthB
            _lastSavedCategory.value = savedCategory
            _compareMonthA.value = savedMonthA
            _compareMonthB.value = savedMonthB
            withContext(Dispatchers.Default) { listenToFirestore() }
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

    val minDistByNote = LinkedHashMap<String, Int>()
    val recentAmountByNote = HashMap<String, Double>()
    val noteIconMap = HashMap<String, String>()
    val noteCategoryMap = HashMap<String, String>()
    val allNoteNames = ArrayList<String>()

    for (r in records) {
        val note = r.note
        if (note.isBlank()) continue
        val sec = getTimestampMeta(r.timestamp).secOfDay
        val d = abs(secNow - sec)
        val dist = if (d < 86400 - d) d else 86400 - d
        val prevMin = minDistByNote[note]
        if (prevMin == null) {
            minDistByNote[note] = dist
            recentAmountByNote[note] = r.amount
            noteCategoryMap[note] = r.category
            allNoteNames.add(note)
        } else if (dist < prevMin) {
            minDistByNote[note] = dist
            recentAmountByNote[note] = r.amount
        }
        if (r.iconUrl.isNotBlank() && !noteIconMap.containsKey(note)) {
            noteIconMap[note] = r.iconUrl
        }
    }

    val topNotes = minDistByNote.entries.sortedBy { it.value }.take(42).map { it.key to 0 }
    val quickInfo = topNotes.take(3).mapNotNull { (note, _) ->
        if (note.isBlank()) null
        else QuickNoteInfo(
            note = note,
            category = noteCategoryMap[note] ?: "飲食",
            amount = recentAmountByNote[note] ?: 0.0
        )
    }

    return NoteIndex(
        recentAmountByNote = recentAmountByNote,
        noteIconMap = noteIconMap,
        allNoteNames = allNoteNames,
        noteCategoryMap = noteCategoryMap,
        topNotes = topNotes,
        topQuickNotesInfo = quickInfo,
    )
}

private fun computeLedgerUiState(
    records: List<Record>, noteIndex: NoteIndex,
    loading: Boolean, filter: FilterState, deletingIds: Set<String>,
): LedgerUiState {
    val now = System.currentTimeMillis()
    val searchFiltered: List<Record> = if (filter.search.isBlank()) records else {
        val q = filter.search
        val hasExactNote = records.any { it.note == q }
        val hasExactCategory = CATEGORIES.any { it == q }
        records.filter { r ->
            when {
                hasExactNote -> r.note == q
                hasExactCategory -> r.category == q
                else -> r.note.contains(q, true) || r.category.contains(q, true)
            }
        }
    }

    val filterCat = filter.category
    val filterMon = filter.month
    val catMonthFiltered = if (filterCat == null && filterMon == null) searchFiltered else {
        searchFiltered.filter { r ->
            (filterCat == null || r.category == filterCat) &&
            (filterMon == null || getTimestampMeta(r.timestamp).monthKey == filterMon)
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

    for (r in ledgerRecords) {
        if (r.id !in deletingIds) {
            if (r.category == INCOME_CATEGORY) {
                hasIncome = true; totalIncome += r.amount
            } else {
                hasExpense = true; totalExpense += r.amount
            }
        }
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
        availableMonths = availableMonths,
        visibleCategories = visibleCategories,
        topQuickNotesInfo = noteIndex.topQuickNotesInfo,
    )
}

private fun computeCompareUiState(records: List<Record>, monthA: String, monthB: String): CompareUiState {
    val categoryMonthSum = HashMap<String, HashMap<String, Double>>()
    for (r in records) {
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
    val monthRecords = if (filterCategory == null) monthAllRecords
                       else monthAllRecords.filter { it.category == filterCategory }

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

fun computeWeeklyExpenses(records: List<Record>): List<WeeklyExpenseDay> {
    val todayStart = getTodayStartMillis()
    val dayMs = 86_400_000L
    val startDay = todayStart - 6 * dayMs
    val amountByDay = HashMap<Long, Double>(7)
    for (r in records) {
        if (r.category == INCOME_CATEGORY) continue
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
    val cal = threadLocalCal.get()!!.apply {
        set(y, mo - 1, d)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val dow = cal.get(Calendar.DAY_OF_WEEK)
    val weekdayChar = WEEKDAY_CHARS[dow - 1]
    val isWeekend = dow == Calendar.SUNDAY || dow == Calendar.SATURDAY
    val todayStart = getTodayStartMillis()
    val diff = ((todayStart - cal.timeInMillis) / 86_400_000L).toInt()
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
        if (s != q && s.startsWith(q, ignoreCase = true)) {
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
    center: Offset, touch: Offset, minRadiusPx: Float, arcCenterAngle: Float
): Int? {
    val dx = touch.x - center.x
    val dy = touch.y - center.y
    val dist = kotlin.math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
    if (dist < minRadiusPx) return null
    val angle = kotlin.math.atan2(dy.toDouble(), dx.toDouble()) * 180 / Math.PI
    var relative = (angle - arcCenterAngle) % 360.0
    if (relative > 180) relative -= 360
    if (relative < -180) relative += 360
    return when {
        relative in -80.0..-40.0 -> 0
        relative in -40.0..0.0 -> 1
        relative in 0.0..40.0 -> 2
        relative in 40.0..80.0 -> 3
        else -> null
    }
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
    val progress = progressProvider()
    if (progress <= 0.001f) return
    val center = centerProvider()
    val currentTouch = touchProvider()

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val minRadiusPx = remember(density) { with(density) { 40.dp.toPx() } }
    val radiusPx = remember(density) { with(density) { 105.dp.toPx() } }
    val halfBtnPx = remember(density) { with(density) { 24.dp.toPx() } }
    val edgeThreshold = radiusPx + halfBtnPx
    val leftSpace = center.x
    val rightSpace = screenWidthPx - center.x
    val rotation = when {
        leftSpace < edgeThreshold -> (1f - leftSpace / edgeThreshold).coerceIn(0f, 1f) * 80f
        rightSpace < edgeThreshold -> -(1f - rightSpace / edgeThreshold).coerceIn(0f, 1f) * 80f
        else -> 0f
    }
    val arcCenterAngle = -90f + rotation
    val hoveredIndex = getHoveredActionIndexWithCenter(center, currentTouch, minRadiusPx, arcCenterAngle)

    val haptic = LocalHapticFeedback.current
    var previousHovered by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(hoveredIndex) {
        if (hoveredIndex != null && hoveredIndex != previousHovered) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        previousHovered = hoveredIndex
    }

    Box(Modifier.fillMaxSize()) {
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
                        text = action.second, color = Color.White, fontSize = 12.sp,
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

// =========================================================
//  ★ MainActivity
// =========================================================
class MainActivity : ComponentActivity() {

    companion object {
        const val EXTRA_OPEN_KEYBOARD = "extra_open_keyboard"
        const val EXTRA_PREFILL_NOTE = "extra_prefill_note"
        const val EXTRA_PREFILL_AMOUNT = "extra_prefill_amount"
        const val EXTRA_PREFILL_CATEGORY = "extra_prefill_category"
        private const val CHANNEL_ID = "ledger_quick_add"
        private const val NOTIFICATION_ID = 1001
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition { !AppStartupState.isFirstDataReady }
        super.onCreate(savedInstanceState)

        handleLaunchIntent(intent)

        lifecycleScope.launch(Dispatchers.IO) {
            NotificationCaptureBridge.init(applicationContext)
            ensureNotificationChannels(applicationContext)
            cleanupStaleConfirmNotifications(applicationContext)
            createQuickAddChannel()
        }

        setContent {
            val token = keyboardLaunchToken.value
            val prefillNote = keyboardPrefillNote.value
            val prefillAmount = keyboardPrefillAmount.value
            val prefillCategory = keyboardPrefillCategory.value
            val notesInfo = topQuickNotesInfo.value

            LaunchedEffect(notesInfo) {
                persistTopQuickNotes(notesInfo)
                showQuickAddNotification()
            }

            LaunchedEffect(Unit) {
                delay(800)
                askNotificationPermissionIfNeeded()
            }

            MaterialTheme {
                Surface(Modifier.fillMaxSize(), color = SURFACE_BG) {
                    MainApp(
                        keyboardLaunchToken = token,
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

    override fun onResume() {
        super.onResume()
        showQuickAddNotification()
        
        // 兜底：如果 Activity 從背景返回（例如從磁貼點擊），確保檢查 Intent
        handleLaunchIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleLaunchIntent(intent)
    }

    private fun handleLaunchIntent(intent: Intent?) {
        if (intent == null) return
        if (intent.getBooleanExtra(EXTRA_OPEN_KEYBOARD, false)) {
            keyboardLaunchToken.value += 1
            keyboardPrefillNote.value = intent.getStringExtra(EXTRA_PREFILL_NOTE)
            keyboardPrefillCategory.value = intent.getStringExtra(EXTRA_PREFILL_CATEGORY)
            keyboardPrefillAmount.value = if (intent.hasExtra(EXTRA_PREFILL_AMOUNT))
                intent.getDoubleExtra(EXTRA_PREFILL_AMOUNT, 0.0) else null
            
            // 🟢 關鍵：處理完必須清空，防止在 onResume 或其他生命週期重複觸發
            intent.removeExtra(EXTRA_OPEN_KEYBOARD)
        }
    }

    private fun createQuickAddChannel() {
        if (Build.VERSION.SDK_INT < 26) return
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val ch = NotificationChannel(CHANNEL_ID, "快速記帳", NotificationManager.IMPORTANCE_LOW)
        nm.createNotificationChannel(ch)
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
//  導航 & 模糊緩存
// =========================================================
private val MAIN_NAV_ITEMS = listOf(
    NavItem("記帳", Icons.Default.Receipt),
    NavItem("比較", Icons.Default.CompareArrows),
    NavItem("月曆", Icons.Default.CalendarMonth)
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
    prefillNoteOnLaunch: String? = null,
    prefillAmountOnLaunch: Double? = null,
    prefillCategoryOnLaunch: String? = null,
    onKeyboardLaunchConsumed: () -> Unit = {},
    vm: LedgerViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

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
    var showKeyboard by remember { mutableStateOf(false) }

    var lastManualAmountInputMs by remember { mutableLongStateOf(0L) }

    val keyboardAnimProgress = remember { Animatable(0f) }
    LaunchedEffect(showKeyboard) {
        if (showKeyboard) {
            keyboardAnimProgress.snapTo(0f)
            delay(30)
            keyboardAnimProgress.animateTo(1f, tween(480, easing = FastOutSlowInEasing))
        } else {
            keyboardAnimProgress.animateTo(0f, tween(420, easing = FastOutSlowInEasing))
        }
    }

    var keyboardState by remember { mutableStateOf(KeyboardState()) }
    var scrollToTopTrigger by remember { mutableIntStateOf(0) }
    var nameFlashTrigger by remember { mutableIntStateOf(0) }
    var afterSaveHint by remember { mutableStateOf<AfterSaveHint?>(null) }
    var afterSaveHintVisible by remember { mutableStateOf(false) }

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

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        val target = iconTargetRecord
        if (uri != null && target != null) {
            scope.launch {
                uploading = true
                val db = withContext(Dispatchers.IO) { Firebase.firestore }
                uploadIconAndApplyToSameName(context, db, target.id, target.note, uri)
                uploading = false
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
                val db = withContext(Dispatchers.IO) { Firebase.firestore }
                uploadIconAndApplyToSameName(context, db, target.id, target.note, uri)
                uploading = false
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
                selectAmountOnInput = amount != null && !keepManualAmount
            )
            showKeyboard = true
        } else {
            keyboardState = keyboardState.copy(
                noteText = note,
                amountText = amtText.ifBlank { keyboardState.amountText },
                category = defaultCategory, currency = defaultCurrency,
                selectAmountOnInput = if (keepManualAmount) false else (amount != null)
            )
        }
        if (note.isNotBlank()) nameFlashTrigger++
    }

    fun openKeyboardForCopy(r: Record) {
        keyboardState = KeyboardState(
            amountText = r.amount.toString(), noteText = r.note,
            category = r.category, selectAmountOnInput = true)
        showKeyboard = true
    }

    fun openKeyboardForEdit(r: Record) {
        val amt = if (r.amount % 1.0 == 0.0) r.amount.toInt().toString() else r.amount.toString()
        keyboardState = KeyboardState(
            amountText = amt, noteText = r.note, category = r.category,
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

    fun dismissKeyboard() {
        showKeyboard = false
        keyboardState = KeyboardState()
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
        showKeyboard = false
        keyboardState = KeyboardState()
        scrollToTopTrigger++
    }

    LaunchedEffect(keyboardLaunchToken) {
        if (keyboardLaunchToken > 0) {
            delay(400)
            if (filter.modeOn) vm.setFilterModeOn(false)
            if (currentPage != 0) currentPage = 0
            val note = prefillNoteOnLaunch
            val amt = prefillAmountOnLaunch
            val cat = prefillCategoryOnLaunch
            when {
                note.isNullOrBlank() && amt == null -> openKeyboardForce(null)
                amt != null && amt > 0.0 -> openKeyboardForNew(note ?: "", amt, cat)
                else -> openKeyboardForce(note, cat)
            }
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
                hasIncome = ui.hasIncome,
                hasExpense = ui.hasExpense,
                totalIncome = ui.totalIncome,
                totalExpense = ui.totalExpense,
                filterMode = filter.modeOn,
                filterCategory = filter.category,
                onFilterCategoryChange = { vm.setFilterCategory(it) },
                filterMonth = filter.month,
                onFilterMonthChange = { vm.setFilterMonth(it) },
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
                onWeeklyIntroFinished = { hasPlayedWeeklyIntro = true }
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
                    onAddClick = { currentPage = 0; openKeyboardForNew() }
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
                    onCopyClick = { r -> currentPage = 0; openKeyboardForCopy(r) },
                    onEditClick = { r -> currentPage = 0; openKeyboardForEdit(r) },
                    onFilterClick = { r ->
                        currentPage = 0
                        vm.setFilterCategory(null)
                        vm.setFilterMonth(null)
                        filterSearch = TextFieldValue(r.note)
                        vm.setFilterSearch(r.note)
                        vm.setFilterModeOn(true)
                    },
                    onDeleteClick = { deleteRecordWithUndo(it) },
                    onChangeIconClick = { iconTargetRecord = it; showIconSourceDialog = true },
                    onAddClick = { currentPage = 0; openKeyboardForNew() },
                    deletingIds = deletingIds,
                    justAddedIds = justAddedIds
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
                if (showKeyboard) {
                    keyboardState = keyboardState.copy(amountText = "", selectAmountOnInput = false)
                } else {
                    if (filter.modeOn) vm.setFilterModeOn(false)
                    if (currentPage != 0) currentPage = 0
                    openKeyboardForNew()
                }
            },
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
                                    val finalBytes = withContext(Dispatchers.IO) {
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
                                        val processedBitmap = removeBackground(srcBitmap)
                                        val baos = ByteArrayOutputStream()
                                        processedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                                        if (processedBitmap !== srcBitmap) processedBitmap.recycle()
                                        srcBitmap.recycle()
                                        baos.toByteArray()
                                    }
                                    val cloudUrl = uploadBytesToCloudinary(finalBytes)
                                    if (cloudUrl != null) {
                                        val db = withContext(Dispatchers.IO) { Firebase.firestore }
                                        applyUrlToSameName(context, db, target.note, cloudUrl)
                                    } else {
                                        Toast.makeText(context, "處理圖片失敗，請嘗試其他圖片",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "下載失敗，這張圖片可能有版權保護",
                                        Toast.LENGTH_SHORT).show()
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
}

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
    keyboardState: KeyboardState,
    onKeyboardStateChange: (KeyboardState) -> Unit,
    onKeyboardDismiss: () -> Unit,
    onKeyboardConfirm: () -> Unit,
    onKeyboardNext: () -> Unit,
    allNoteNames: List<String>,
    noteCategoryMap: Map<String, String>,
    nameFlashTrigger: Int,
) {
    Row(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp,
                bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 12.dp)
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

                if (filterModeOn && filterSearchHasFocus) {
                    val filterSuggestions = remember(filterSearch.text, allNoteNames) {
                        filterNoteSuggestions(filterSearch.text, allNoteNames)
                    }
                    if (filterSuggestions.isNotEmpty()) {
                        androidx.compose.ui.window.Popup(
                            popupPositionProvider = AboveAnchorPositionProvider,
                            onDismissRequest = { },
                            properties = PopupProperties(focusable = false)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SURFACE_CARD,
                                border = BorderStroke(0.5.dp, DIVIDER_COLOR),
                                modifier = Modifier.width(260.dp).heightIn(max = 260.dp)
                            ) {
                                Column(Modifier.verticalScroll(rememberScrollState())) {
                                    filterSuggestions.forEach { s ->
                                        Row(
                                            Modifier.fillMaxWidth()
                                                .clickable { onFilterSearchChange(TextFieldValue(s)) }
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

    AnimatedFabLayer(
        progressProvider = progressProvider,
        onFabTap = onFabTap,
        modifier = Modifier.align(Alignment.BottomEnd)
    )

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
        nameFlashTrigger = nameFlashTrigger,
        modifier = Modifier.align(Alignment.BottomCenter)
    )
}

@Composable
private fun AnimatedFabLayer(
    progressProvider: () -> Float,
    onFabTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isExpandedTint by remember {
        derivedStateOf { progressProvider() > 0.35f }
    }
    val tintColor by animateColorAsState(
        targetValue = if (isExpandedTint) TEXT_TERTIARY else Color.White,
        animationSpec = tween(180),
        label = "fabTint"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                val currentP = progressProvider()
                val fabX = lerp(20.dp, 24.dp, currentP).toPx()
                val fabY = lerp(92.dp, 388.dp, currentP).toPx()
                translationX = -fabX
                translationY = -fabY
                transformOrigin = TransformOrigin(1f, 1f)
                shape = CircleShape
                clip = true
            }
            .size(56.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onFabTap() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.matchParentSize()) {
            val currentP = progressProvider()
            val bgAlpha = (1f - currentP * 3.3f).coerceIn(0f, 1f)
            drawCircle(color = BRAND_PRIMARY.copy(alpha = bgAlpha))
        }

        Icon(
            Icons.Default.Add,
            contentDescription = "Clear",
            tint = tintColor,
            modifier = Modifier
                .align(Alignment.Center)
                .size(24.dp)
                .graphicsLayer { rotationZ = progressProvider() * -405f }
        )
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
    nameFlashTrigger: Int,
    modifier: Modifier = Modifier
) {
    val currentP = progressProvider()
    val isActive = currentP > 0f || showKeyboard

    if (!isActive) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp, end = 16.dp, start = 16.dp)
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
            onPickCategory = { },
            allNoteNames = allNoteNames,
            noteCategoryMap = noteCategoryMap,
            nameFlashTrigger = nameFlashTrigger,
            showKeyboard = showKeyboard,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun FloatingNavBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val tabWidthPx = with(density) { NAV_TAB_WIDTH.toPx() }
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
        if (isDragging) snap() else spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "bubble"
    )

    Box(
        modifier = modifier
            .width(NAV_TAB_WIDTH * items.size)
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
                    .width(NAV_TAB_WIDTH).fillMaxHeight().padding(6.dp)
                    .clip(RoundedCornerShape((NAV_HEIGHT - 12.dp) / 2))
                    .background(BRAND_PRIMARY_LIGHT.copy(alpha = 0.9f))
            )
            Row(Modifier.fillMaxSize()) {
                items.forEachIndexed { idx, item ->
                    val sel = idx == selectedIndex
                    val tint = if (sel) BRAND_PRIMARY_DARK else TEXT_SECONDARY
                    Box(Modifier.width(NAV_TAB_WIDTH).fillMaxHeight(),
                        contentAlignment = Alignment.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(item.icon, item.label, tint = tint, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(item.label, fontSize = 14.sp,
                                fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun LedgerContent(
    isFirstShow: Boolean = false,
    loading: Boolean,
    filtered: List<Record>,
    groupedByDate: List<Pair<String, List<Record>>>,
    topNotes: List<Pair<String, Int>>,
    noteIconMap: Map<String, String>,
    hasIncome: Boolean,
    hasExpense: Boolean,
    totalIncome: Double,
    totalExpense: Double,
    filterMode: Boolean,
    filterCategory: String?,
    onFilterCategoryChange: (String?) -> Unit,
    filterMonth: String?,
    onFilterMonthChange: (String?) -> Unit,
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
) {
    val listState = rememberLazyListState()

    val weeklyIntroProgress = remember { Animatable(1f) }
    var weeklyIntroTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(shouldPlayWeeklyIntro) {
        if (!shouldPlayWeeklyIntro || weeklyIntroTriggered) return@LaunchedEffect
        delay(150)
        weeklyIntroTriggered = true
        if (!listState.isScrollInProgress) {
            weeklyIntroProgress.snapTo(0f)
            weeklyIntroProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(820, easing = FastOutSlowInEasing)
            )
        } else {
            weeklyIntroProgress.snapTo(1f)
        }
        onWeeklyIntroFinished()
    }

    var preFilterIndex by remember { mutableIntStateOf(-1) }
    var preFilterOffset by remember { mutableIntStateOf(0) }

    var activeFanRecord by remember { mutableStateOf<Record?>(null) }
    val fanMenuCenterState = remember { mutableStateOf(Offset.Zero) }
    val fanMenuTouchState = remember { mutableStateOf(Offset.Zero) }

    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val headerHeightPx = remember(density) { with(density) { 64.dp.toPx() } }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { inProgress ->
                if (!inProgress) {
                    val transitioningHeader = listState.layoutInfo.visibleItemsInfo.find {
                        it.contentType == "day_header" && it.offset < -2 && it.offset > -(headerHeightPx - 2)
                    }
                    if (transitioningHeader != null) {
                        val offset = transitioningHeader.offset.toFloat()
                        val distanceToScroll = if (offset < -headerHeightPx / 2) {
                            headerHeightPx + offset
                        } else offset
                        try {
                            listState.animateScrollBy(
                                value = distanceToScroll,
                                animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f)
                            )
                        } catch (_: Exception) {}
                    }
                }
            }
    }

    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val fanRadiusPx = remember(density) { with(density) { 105.dp.toPx() } }
    val fanHalfBtnPx = remember(density) { with(density) { 24.dp.toPx() } }
    val fanEdgeThreshold = fanRadiusPx + fanHalfBtnPx
    val oneRowPx = remember(density) { with(density) { 72.dp.toPx() } }

    val hasChart = !filterMode && weeklyExpenses.isNotEmpty()
    val chartHeightPx = remember(density) { with(density) { 108.dp.toPx() } }

    val quickInputMaxRows = remember { mutableIntStateOf(4) }

    LaunchedEffect(listState, hasChart, chartHeightPx) {
        snapshotFlow {
            val info = listState.layoutInfo.visibleItemsInfo.firstOrNull()
            if (info == null) null
            else info.index to listState.firstVisibleItemScrollOffset.toFloat()
        }
            .distinctUntilChanged()
            .collect { pair ->
                val (index, offset) = pair ?: return@collect
                val rows = when {
                    index == 0 -> if (hasChart) {
                        when {
                            offset < chartHeightPx * 0.35f -> 4
                            offset < chartHeightPx * 0.70f -> 3
                            else -> 2
                        }
                    } else 4
                    index == 1 -> 2
                    else -> 1
                }
                if (quickInputMaxRows.intValue != rows) {
                    quickInputMaxRows.intValue = rows
                }
            }
    }

    val fanMenuProgress = animateFloatAsState(
        targetValue = if (activeFanRecord != null) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
        label = "fanMenuProgress"
    )

    val minFanRadiusPx = remember(density) { with(density) { 40.dp.toPx() } }
    val handleFanStart = remember {
        { r: Record, offset: Offset ->
            activeFanRecord = r
            fanMenuCenterState.value = offset
            fanMenuTouchState.value = offset
        }
    }
    val handleFanDrag = remember {
        { dragAmount: Offset -> fanMenuTouchState.value += dragAmount }
    }
    val latestFilterMode by rememberUpdatedState(filterMode)
    val latestOnCopy by rememberUpdatedState(onCopyClick)
    val latestOnEdit by rememberUpdatedState(onEditClick)
    val latestOnFilterByName by rememberUpdatedState(onFilterByName)
    val latestOnDelete by rememberUpdatedState(onDeleteClick)
    val latestOnChangeIcon by rememberUpdatedState(onChangeIconClick)
    val latestOnExpand by rememberUpdatedState(onExpandChange)

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
            val hovered = getHoveredActionIndexWithCenter(
                center = center, touch = fanMenuTouchState.value,
                minRadiusPx = minFanRadiusPx, arcCenterAngle = arcCenterAngle
            )
            val targetRecord = activeFanRecord
            activeFanRecord = null
            if (targetRecord != null && hovered != null) {
                scope.launch {
                    delay(120)
                    when (hovered) {
                        0 -> latestOnCopy(targetRecord)
                        1 -> latestOnEdit(targetRecord)
                        2 -> {
                            if (!latestFilterMode) {
                                preFilterIndex = listState.firstVisibleItemIndex
                                preFilterOffset = listState.firstVisibleItemScrollOffset
                            }
                            latestOnFilterByName(targetRecord.note)
                        }
                        3 -> latestOnDelete(targetRecord)
                    }
                }
            }
        }
    }

    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger > 0) {
            try {
                if (listState.firstVisibleItemIndex > 3) listState.scrollToItem(3)
                listState.animateScrollToItem(0)
            } catch (_: Exception) {
                try { listState.scrollToItem(0) } catch (_: Exception) {}
            }
        }
    }

    LaunchedEffect(showKeyboard) {
        if (showKeyboard) {
            try {
                if (listState.firstVisibleItemIndex > 3) listState.scrollToItem(3)
                listState.animateScrollToItem(0)
            } catch (_: Exception) {
            } finally {
                try {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.NonCancellable) {
                        listState.scrollToItem(0)
                    }
                } catch (_: Exception) {}
            }
        }
    }

    LaunchedEffect(showFuture) {
        try {
            if (showFuture) {
                if (listState.firstVisibleItemIndex > 3) listState.scrollToItem(3)
                listState.animateScrollToItem(0)
            } else {
                if (listState.firstVisibleItemIndex <= 3) listState.animateScrollToItem(0)
            }
        } catch (_: Exception) {}
    }

    LaunchedEffect(filterMode) {
        if (!filterMode && preFilterIndex >= 0) {
            try {
                listState.requestScrollToItem(preFilterIndex, preFilterOffset)
            } catch (_: Exception) {
                try { listState.scrollToItem(preFilterIndex, preFilterOffset) } catch (_: Exception) {}
            }
            preFilterIndex = -1
        }
    }

    LaunchedEffect(filterMode) {
        if (filterMode) { try { listState.scrollToItem(0) } catch (_: Exception) {} }
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
    val isSingleCategoryFilter = singleCategory != null

    var cachedCategory by remember { mutableStateOf("飲食") }
    LaunchedEffect(singleCategory) { if (singleCategory != null) cachedCategory = singleCategory!! }

    val displayGroups = groupedByDate
    val isFanActive = activeFanRecord != null
    val activeFanId = activeFanRecord?.id

    data class DayStat(val income: Double, val expense: Double, val isGroupDeleting: Boolean)
    val dayStats: Map<String, DayStat> = remember(displayGroups, deletingIds) {
        displayGroups.associate { (dateKey, records) ->
            var inc = 0.0
            var exp = 0.0
            for (r in records) {
                if (r.id !in deletingIds) {
                    if (r.category == INCOME_CATEGORY) inc += r.amount
                    else exp += r.amount
                }
            }
            val isGroupDeleting = records.size == 1 && records[0].id in deletingIds
            dateKey to DayStat(inc, exp, isGroupDeleting)
        }
    }

    val listContent: LazyListScope.() -> Unit = {
        displayGroups.forEachIndexed { index, (dateKey, dayRecords) ->
            val stat = dayStats[dateKey] ?: DayStat(0.0, 0.0, false)
            val dayIncome = stat.income
            val dayExpense = stat.expense
            val isGroupDeleting = stat.isGroupDeleting

            if (!isGroupDeleting) {
                stickyHeader(key = "header_$dateKey", contentType = "day_header") {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .background(SURFACE_BG)
                    ) {
                        DayHeader(
                            dateKey = dateKey,
                            income = dayIncome,
                            expense = dayExpense,
                            listState = listState
                        )
                    }
                }

                item(key = "group_$dateKey", contentType = "day_group") {
                    Box(modifier = Modifier.animateItem(
                        fadeInSpec = tween(250, easing = FastOutSlowInEasing),
                        fadeOutSpec = tween(200, easing = FastOutSlowInEasing),
                        placementSpec = spring(dampingRatio = 0.85f, stiffness = 450f)
                    )) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SURFACE_CARD)
                        ) {
                            val currentRecords = dayRecords
                            val activeCount = if (deletingIds.isEmpty()) currentRecords.size
                                              else currentRecords.count { it.id !in deletingIds }
                            var renderedVisibleIdx = 0

                            for (r in currentRecords) {
                                val isRecDeleting = r.id in deletingIds
                                if (!isRecDeleting) renderedVisibleIdx++
                                val showDividerBelow = !isRecDeleting && renderedVisibleIdx < activeCount

                                key(r.id) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        AnimatedRecordItem(
                                            animateOnMount = r.id in justAddedIds,
                                            isDeleting = isRecDeleting
                                        ) {
                                            SwipeableRecordItem(
                                                backgroundColor = SURFACE_CARD,
                                                record = r,
                                                isExpanded = expandedId == r.id,
                                                hasAnyExpanded = expandedId != null,
                                                onExpand = onExpandChange,
                                                onCopy = { onCopyClick(r) },
                                                onEdit = { onEditClick(r) },
                                                onFilter = {
                                                    if (!filterMode) {
                                                        preFilterIndex = listState.firstVisibleItemIndex
                                                        preFilterOffset = listState.firstVisibleItemScrollOffset
                                                    }
                                                    onFilterByName(r.note)
                                                },
                                                onDelete = { onDeleteClick(r) },
                                                onChangeIcon = { onChangeIconClick(r) },
                                                hideCategory = isExactNoteFilter,
                                                onFanMenuStart = { _, offset -> handleFanStart(r, offset) },
                                                onFanMenuDrag = handleFanDrag,
                                                onFanMenuEnd = handleFanEnd,
                                                isFanMenuActive = isFanActive,
                                                isOtherItem = isFanActive && activeFanId != r.id,
                                                isActiveItem = activeFanId == r.id
                                            )
                                        }

                                        if (afterSaveHint?.recordId == r.id) {
                                            CategoryTotalHint(hint = afterSaveHint, visible = afterSaveHintVisible)
                                        }

                                        if (showDividerBelow) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(start = 68.dp, end = 16.dp),
                                                color = DIVIDER_COLOR,
                                                thickness = 0.5.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item(key = "spacer_$dateKey", contentType = "day_spacer") {
                    Spacer(modifier = Modifier
                        .height(10.dp)
                        .animateItem(
                            fadeInSpec = tween(250), fadeOutSpec = tween(200),
                            placementSpec = spring(dampingRatio = 0.85f, stiffness = 450f)
                        )
                    )
                }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().background(SURFACE_BG)) {
            NotificationAccessGuide(context = LocalContext.current)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SURFACE_CARD)
                    .padding(start = 8.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val showCategoryChip = filterMode && isSingleCategoryFilter && singleCategory != null
                Box(modifier = Modifier.weight(1f)) {
                    TopStats(
                        hasIncome = hasIncome, hasExpense = hasExpense,
                        income = totalIncome, expense = totalExpense,
                        isFilterMode = filterMode,
                        filteredCount = if (filterMode) filtered.size else null,
                        showCategory = showCategoryChip, categoryName = cachedCategory
                    )
                }
                IconButton(onClick = { onShowFutureChange(!showFuture) }) {
                    Icon(
                        if (showFuture) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        "顯示未來項目",
                        tint = if (showFuture) BRAND_PRIMARY else TEXT_TERTIARY
                    )
                }
            }

            Column(
                modifier = Modifier
                    .zIndex(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = filterMode,
                    transitionSpec = {
                        (fadeIn(tween(200, easing = FastOutSlowInEasing)) togetherWith
                            fadeOut(tween(150, easing = FastOutSlowInEasing)))
                            .using(SizeTransform(clip = false) { _, _ ->
                                tween(120, easing = FastOutSlowInEasing)
                            })
                    },
                    label = "filterContent"
                ) { isFilter ->
                    if (!isFilter) {
                        Column(Modifier.fillMaxWidth()) {
                            if (topNotes.isNotEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                QuickInputSection(
                                    topNotes = topNotes, noteIconMap = noteIconMap,
                                    maxRowsPerPage = quickInputMaxRows.intValue,
                                    // 🟢 呢度已經改為 !isFirstShow 嚟防止切換時重新播放動畫
                                    skipAppearAnimation = !isFirstShow, 
                                    onClick = onQuickInputClick
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    } else {
                        Column(Modifier.padding(horizontal = 16.dp, vertical = 0.dp)) {
                            Spacer(Modifier.height(4.dp))
                            val allCategories: List<String?> = remember(visibleCategories) {
                                listOf(null) + visibleCategories
                            }
                            val rows = remember(allCategories) { allCategories.chunked(4) }
                            rows.forEach { row ->
                                Row(Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    row.forEach { cat ->
                                        AnimatedFilterChip(
                                            modifier = Modifier.weight(1f),
                                            selected = filterCategory == cat,
                                            label = cat ?: "全部",
                                            fillWidth = true,
                                            onClick = {
                                                onFilterCategoryChange(
                                                    if (filterCategory == cat) null else cat
                                                )
                                            }
                                        )
                                    }
                                    repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                                }
                                Spacer(Modifier.height(4.dp))
                            }

                            Spacer(Modifier.height(4.dp))
                            if (availableMonths.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AnimatedFilterChip(
                                        selected = filterMonth == null, label = "全年",
                                        fillWidth = false,
                                        onClick = { onFilterMonthChange(null) }
                                    )
                                    availableMonths.forEach { m ->
                                        AnimatedFilterChip(
                                            selected = filterMonth == m,
                                            label = formatMonthLabel(m), fillWidth = false,
                                            onClick = {
                                                onFilterMonthChange(if (filterMonth == m) null else m)
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                        }
                    }
                }
            }

            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clipToBounds()
                    .graphicsLayer {
                        val p = keyboardProgressProvider()
                        alpha = 1f - (p * 0.4f)
                        if (p > 0.02f && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val quantized = ((p * 24f).roundToInt() * 2).coerceIn(2, 48)
                            renderEffect = getCachedBlurEffect(quantized)
                        } else {
                            renderEffect = null
                        }
                    }
            ) {
                if (loading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BRAND_PRIMARY)
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 0.dp, end = 0.dp, top = 0.dp,
                            bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 80.dp
                        )
                    ) {
                        if (!filterMode && weeklyExpenses.isNotEmpty()) {
                            item(key = "weekly_chart", contentType = "weekly_chart") {
                                WeeklyExpenseChart(
                                    data = weeklyExpenses,
                                    listState = listState,
                                    introProgress = weeklyIntroProgress.value,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        item(key = "magic_top_anchor", contentType = "anchor") {
                            Spacer(Modifier.height(1.dp))
                        }
                        if (displayGroups.isEmpty() && !showKeyboard) {
                            item(key = "__empty__", contentType = "empty") {
                                Box(Modifier.fillParentMaxWidth().padding(top = 80.dp),
                                    contentAlignment = Alignment.Center) {
                                    Text(if (filterMode) "冇符合篩選條件嘅記錄" else "仲未有記錄,撳右下角 + 新增",
                                        color = TEXT_SECONDARY)
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
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(200.dp)
                .background(Brush.verticalGradient(colorStops = arrayOf(
                    0.00f to SURFACE_BG.copy(alpha = 0f),
                    0.50f to SURFACE_BG.copy(alpha = 0f),
                    0.70f to SURFACE_BG.copy(alpha = 0.45f),
                    0.88f to SURFACE_BG.copy(alpha = 0.88f),
                    1.00f to SURFACE_BG
                )))
        )

        FanMenuOverlay(
            progressProvider = { fanMenuProgress.value },
            centerProvider = { fanMenuCenterState.value },
            touchProvider = { fanMenuTouchState.value }
        )
    }
}

@Composable
fun WeeklyExpenseChart(
    data: List<WeeklyExpenseDay>,
    listState: LazyListState,
    introProgress: Float,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val chartHeight = 108.dp
    val chartHeightPx = remember(density) { with(density) { chartHeight.toPx() } }
    val maxBarHeight = 62.dp

    val scrollProgress by remember(listState) {
        derivedStateOf {
            val info = listState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.key == "weekly_chart" }
            if (info == null) {
                if (listState.firstVisibleItemIndex > 0) 1f else 0f
            } else {
                val offset = info.offset
                if (offset >= 0) 0f
                else (-offset / chartHeightPx).coerceIn(0f, 1f)
            }
        }
    }

    val maxAmount = remember(data) {
        data.maxOfOrNull { it.amount }?.coerceAtLeast(0.0001) ?: 0.0001
    }

    Box(modifier = modifier.fillMaxWidth().height(chartHeight).clipToBounds()) {
        Box(
            modifier = Modifier.fillMaxSize().graphicsLayer {
                val p = scrollProgress
                alpha = (1f - p * 1.05f).coerceIn(0f, 1f)
                scaleY = 1f - p * 0.35f
                scaleX = 1f - p * 0.08f
                translationY = -p * 14.dp.toPx()
                transformOrigin = TransformOrigin(0.5f, 0f)
            }
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEach { day ->
                    WeeklyExpenseBar(
                        day = day, maxAmount = maxAmount,
                        introProgress = introProgress,
                        maxBarHeight = maxBarHeight,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyExpenseBar(
    day: WeeklyExpenseDay, maxAmount: Double, introProgress: Float,
    maxBarHeight: Dp, modifier: Modifier = Modifier,
) {
    val targetRatio = (day.amount / maxAmount).toFloat().coerceIn(0f, 1f)
    val animatedRatio by animateFloatAsState(
        targetValue = targetRatio,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 260f),
        label = "weeklyBarRatio_${day.dayOfMonth}"
    )

    val minBarHeight = 4.dp
    val minRatio = if (maxBarHeight.value > 0f) (minBarHeight.value / maxBarHeight.value) else 0f
    val displayRatio: Float = if (day.amount > 0.0) {
        (animatedRatio * introProgress).coerceAtLeast(minRatio * introProgress)
    } else {
        animatedRatio * introProgress
    }.coerceIn(0f, 1f)

    val barHeight = maxBarHeight * displayRatio

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(maxBarHeight),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier.width(16.dp).fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(BRAND_PRIMARY_LIGHT.copy(alpha = 0.35f))
            )
            if (displayRatio > 0.0005f) {
                Box(
                    modifier = Modifier.width(16.dp).height(barHeight)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = if (day.isToday) {
                                    listOf(BRAND_PRIMARY_DARK, BRAND_PRIMARY)
                                } else {
                                    listOf(
                                        BRAND_PRIMARY.copy(alpha = 0.88f),
                                        BRAND_PRIMARY.copy(alpha = 0.58f)
                                    )
                                }
                            )
                        )
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = day.weekdayChar, fontSize = 11.sp,
            color = if (day.isToday) BRAND_PRIMARY_DARK else TEXT_SECONDARY,
            fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
fun CategoryStatChip(category: String) {
    val style = CATEGORY_STYLES[category]
    Column(
        Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("類別", fontSize = 11.sp, color = TEXT_SECONDARY,
            fontWeight = FontWeight.Medium, maxLines = 1, softWrap = false)
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (style != null) {
                Box(Modifier.size(24.dp).clip(RoundedCornerShape(8.dp)).background(style.bgColor),
                    contentAlignment = Alignment.Center) {
                    Icon(style.icon, null, tint = style.fgColor, modifier = Modifier.size(14.dp))
                }
                Spacer(Modifier.width(4.dp))
            }
            Text(category, fontSize = 20.sp, color = TEXT_PRIMARY,
                fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
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

@Composable
fun TrianglePointer(
    colIndex: Int, cellWidth: Dp, color: Color = SURFACE_CARD,
    triangleWidth: Dp = 22.dp, triangleHeight: Dp = 11.dp,
) {
    val targetX = cellWidth * colIndex + cellWidth / 2 - triangleWidth / 2
    val animatedX by animateDpAsState(
        targetValue = targetX,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "triangleX"
    )
    Box(Modifier.fillMaxWidth().height(triangleHeight)) {
        Canvas(Modifier.offset(x = animatedX).size(triangleWidth, triangleHeight)) {
            val path = Path().apply {
                moveTo(0f, size.height)
                lineTo(size.width / 2f, 0f)
                lineTo(size.width, size.height)
                close()
            }
            drawPath(path, color)
        }
    }
}

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
            .pointerInput(record.id) {
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
            .pointerInput("longPress_${record.id}") {
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
                Text(metaText, fontSize = META_FONT_SIZE, color = TEXT_TERTIARY,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.width(8.dp))
            Text(amtText, color = amtColor, fontSize = AMOUNT_FONT_SIZE, fontWeight = FontWeight.Bold)
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
    onPickCategory: () -> Unit,
    allNoteNames: List<String>, noteCategoryMap: Map<String, String>,
    nameFlashTrigger: Int, showKeyboard: Boolean = true,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current

    LaunchedEffect(state.noteText) {
        if (state.noteText.isNotBlank()) {
            val lastCat = noteCategoryMap[state.noteText]
            if (lastCat != null && lastCat != state.category) {
                onStateChange(state.copy(category = lastCat))
            }
        }
    }

    val panelShape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .clip(panelShape)
            .background(SURFACE_CARD)
            .pointerInput(Unit) { detectTapGestures { } }
    ) {
        Column(
            Modifier.fillMaxWidth()
                .verticalScroll(rememberScrollState())
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
                }
            }

            Spacer(Modifier.height(10.dp))

            Column(Modifier.fillMaxWidth().height(190.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                KEYBOARD_ROWS.forEach { row ->
                    Row(Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        row.forEach { key ->
                            KeyboardKey(key, {
                                when (key) {
                                    "backspace" -> {
                                        if (state.focusedField == "amount") {
                                            val t = state.amountText
                                            onStateChange(state.copy(
                                                amountText = if (t.isEmpty()) t else t.dropLast(1),
                                                selectAmountOnInput = false))
                                        } else {
                                            val t = state.exchangeRateText
                                            onStateChange(state.copy(
                                                exchangeRateText = if (t.isEmpty()) t else t.dropLast(1)))
                                        }
                                    }
                                    "." -> {
                                        if (state.focusedField == "amount") {
                                            val t = state.amountText
                                            if (!t.contains("."))
                                                onStateChange(state.copy(
                                                    amountText = if (t.isEmpty()) "0." else "$t.",
                                                    selectAmountOnInput = false))
                                        } else {
                                            val t = state.exchangeRateText
                                            if (!t.contains("."))
                                                onStateChange(state.copy(
                                                    exchangeRateText = if (t.isEmpty()) "0." else "$t."))
                                        }
                                    }
                                    else -> {
                                        if (state.focusedField == "amount") {
                                            val base = if (state.selectAmountOnInput) "" else state.amountText
                                            val dotIdx = base.indexOf(".")
                                            val newT = if (dotIdx >= 0) {
                                                if (base.length - dotIdx - 1 >= 2 && state.currency != "JPY") base
                                                else "$base$key"
                                            } else {
                                                if (base.length >= 9) base else "$base$key"
                                            }
                                            onStateChange(state.copy(
                                                amountText = newT, selectAmountOnInput = false))
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

                        LaunchedEffect(state.editingNote) { if (state.editingNote) focusReq.requestFocus() }
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
                                onStateChange(state.copy(noteText = nv.text))
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
                            modifier = Modifier.fillMaxSize().focusRequester(focusReq)
                        )

                        val suggestions = remember(state.noteText, allNoteNames) {
                            filterNoteSuggestions(state.noteText, allNoteNames)
                        }
                        if (suggestions.isNotEmpty()) {
                            androidx.compose.ui.window.Popup(
                                popupPositionProvider = AboveAnchorPositionProvider,
                                onDismissRequest = { },
                                properties = PopupProperties(focusable = false)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SURFACE_CARD,
                                    border = BorderStroke(0.5.dp, DIVIDER_COLOR),
                                    modifier = Modifier.width(280.dp).heightIn(max = 220.dp)
                                ) {
                                    Column(Modifier.verticalScroll(rememberScrollState())) {
                                        suggestions.forEach { s ->
                                            Row(
                                                Modifier.fillMaxWidth()
                                                    .clickable(
                                                        interactionSource = remember { MutableInteractionSource() },
                                                        indication = null
                                                    ) { onStateChange(state.copy(noteText = s)) }
                                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(s, fontSize = 14.sp, color = TEXT_PRIMARY,
                                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            }
                                            if (s != suggestions.last())
                                                HorizontalDivider(color = DIVIDER_COLOR, thickness = 0.5.dp)
                                        }
                                    }
                                }
                            }
                        }
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

                var showCategoryMenu by remember { mutableStateOf(false) }
                val currentStyle = CATEGORY_STYLES[state.category]
                Box {
                    Button(
                        onClick = { showCategoryMenu = true },
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
                    DropdownMenu(
                        expanded = showCategoryMenu,
                        onDismissRequest = { showCategoryMenu = false },
                        modifier = Modifier.width(288.dp),
                        shape = RoundedCornerShape(18.dp),
                        containerColor = SURFACE_CARD
                    ) {
                        Column(Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            CHUNKED_CATEGORIES.forEach { pair ->
                                Row(Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    pair.forEach { cat ->
                                        val s = CATEGORY_STYLES[cat]
                                        val sel = cat == state.category
                                        val chipShape = RoundedCornerShape(12.dp)
                                        Row(
                                            Modifier.weight(1f).height(44.dp)
                                                .clip(chipShape)
                                                .background(
                                                    if (sel) (s?.fgColor ?: BRAND_PRIMARY)
                                                    else (s?.bgColor ?: SURFACE_ELEVATED))
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) {
                                                    onStateChange(state.copy(
                                                        category = cat, currency = "HKD",
                                                        focusedField = "amount"))
                                                    showCategoryMenu = false
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
                                                color = if (sel) Color.White else (s?.fgColor ?: TEXT_PRIMARY),
                                                fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
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
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "cs"
    )
    val bgColor = if (selected) BRAND_PRIMARY else SURFACE_ELEVATED
    val textColor = if (selected) Color.White else Color.Black
    val fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium

    Surface(
        onClick = onClick,
        modifier = modifier.height(34.dp).graphicsLayer { scaleX = scale; scaleY = scale },
        shape = RoundedCornerShape(12.dp), color = bgColor,
        interactionSource = src
    ) {
        Box(Modifier.fillMaxWidth().padding(horizontal = 14.dp), contentAlignment = Alignment.Center) {
            Text(label, textAlign = TextAlign.Center, fontSize = 13.sp, maxLines = 1,
                color = textColor, fontWeight = fontWeight,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)))
        }
    }
}

@Composable
private fun DollarAmount(
    text: String,
    color: Color,
    fontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Bold,
    showDollar: Boolean = true,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        when {
            !showDollar -> {
                AnimatedAmount(
                    text = text, color = color,
                    fontSize = fontSize, fontWeight = fontWeight
                )
            }
            text.startsWith("-") -> {
                Text("-$", color = color, fontSize = fontSize,
                    fontWeight = fontWeight, maxLines = 1, softWrap = false)
                AnimatedAmount(
                    text = text.substring(1), color = color,
                    fontSize = fontSize, fontWeight = fontWeight
                )
            }
            else -> {
                Text("$", color = color, fontSize = fontSize,
                    fontWeight = fontWeight, maxLines = 1, softWrap = false)
                AnimatedAmount(
                    text = text, color = color,
                    fontSize = fontSize, fontWeight = fontWeight
                )
            }
        }
    }
}

@Composable
fun TopStats(
    hasIncome: Boolean, hasExpense: Boolean,
    income: Double, expense: Double,
    isFilterMode: Boolean = false, filteredCount: Int? = null,
    showCategory: Boolean = false, categoryName: String = ""
) {
    val balance = income - expense
    val showIncome = hasIncome || (!hasIncome && !hasExpense)
    val showExpense = hasExpense

    val spec = tween<Float>(durationMillis = 420, easing = FastOutSlowInEasing)
    val incomeW by animateFloatAsState(if (showIncome) 1f else 0f, spec, label = "iW")
    val expenseW by animateFloatAsState(if (showExpense) 1f else 0f, spec, label = "eW")
    val catW by animateFloatAsState(if (showCategory) 1f else 0f, spec, label = "cW")

    val thirdBlockText = if (isFilterMode) "${filteredCount ?: 0}"
                         else formatAmountNoDecimal(balance)
    val targetBalColor = if (balance >= 0) BRAND_PRIMARY else COLOR_EXPENSE
    val thirdBlockColor by animateColorAsState(
        targetValue = if (isFilterMode) BRAND_PRIMARY else targetBalColor,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "3rdColor"
    )

    Row(
        modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showCategory || catW > 0.001f) {
            Box(modifier = Modifier.weight(catW.coerceAtLeast(0.001f)).clipToBounds(),
                contentAlignment = Alignment.Center) {
                val alphaScale = catW.coerceIn(0f, 1f)
                Box(Modifier.wrapContentWidth(unbounded = true).graphicsLayer {
                    alpha = alphaScale
                    scaleX = 0.85f + 0.15f * alphaScale
                    scaleY = 0.85f + 0.15f * alphaScale
                }) { CategoryStatChip(category = categoryName) }
            }
        }

        if (showIncome || incomeW > 0.001f) {
            Box(modifier = Modifier.weight(incomeW.coerceAtLeast(0.001f)).clipToBounds(),
                contentAlignment = Alignment.Center) {
                val alphaScale = incomeW.coerceIn(0f, 1f)
                Column(
                    Modifier.padding(horizontal = 4.dp).graphicsLayer {
                        alpha = alphaScale
                        scaleX = 0.85f + 0.15f * alphaScale
                        scaleY = 0.85f + 0.15f * alphaScale
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(22.dp)) {
                        Icon(Icons.Default.TrendingUp, null, tint = COLOR_INCOME,
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("收入", fontSize = STAT_LABEL_FONT_SIZE, color = TEXT_SECONDARY,
                            fontWeight = FontWeight.Medium, maxLines = 1, softWrap = false)
                    }
                    Spacer(Modifier.height(4.dp))
                    Box(Modifier.fillMaxWidth().height(32.dp).clipToBounds(),
                        contentAlignment = Alignment.Center) {
                        DollarAmount(
                            text = formatAmountNoDecimal(income),
                            color = COLOR_INCOME,
                            fontSize = STAT_AMOUNT_FONT_SIZE,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (showExpense || expenseW > 0.001f) {
            Box(modifier = Modifier.weight(expenseW.coerceAtLeast(0.001f)).clipToBounds(),
                contentAlignment = Alignment.Center) {
                val alphaScale = expenseW.coerceIn(0f, 1f)
                Column(
                    Modifier.padding(horizontal = 4.dp).graphicsLayer {
                        alpha = alphaScale
                        scaleX = 0.85f + 0.15f * alphaScale
                        scaleY = 0.85f + 0.15f * alphaScale
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(22.dp)) {
                        Icon(Icons.Default.TrendingDown, null, tint = COLOR_EXPENSE,
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("支出", fontSize = STAT_LABEL_FONT_SIZE, color = TEXT_SECONDARY,
                            fontWeight = FontWeight.Medium, maxLines = 1, softWrap = false)
                    }
                    Spacer(Modifier.height(4.dp))
                    Box(Modifier.fillMaxWidth().height(32.dp).clipToBounds(),
                        contentAlignment = Alignment.Center) {
                        DollarAmount(
                            text = formatAmountNoDecimal(expense),
                            color = COLOR_EXPENSE,
                            fontSize = STAT_AMOUNT_FONT_SIZE,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(1f).clipToBounds(),
            contentAlignment = Alignment.Center) {
            Column(Modifier.padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.Center) {
                    Crossfade(
                        targetState = isFilterMode,
                        animationSpec = tween(200, easing = FastOutSlowInEasing),
                        label = "labelCrossfade"
                    ) { isFilter ->
                        if (!isFilter) {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.height(22.dp)) {
                                Icon(Icons.Default.AccountBalanceWallet, null, tint = targetBalColor,
                                    modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("餘額", fontSize = STAT_LABEL_FONT_SIZE, color = TEXT_SECONDARY,
                                    fontWeight = FontWeight.Medium, maxLines = 1, softWrap = false)
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.height(22.dp)) {
                                Icon(Icons.Default.FilterList, null, tint = BRAND_PRIMARY,
                                    modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("筆數", fontSize = STAT_LABEL_FONT_SIZE, color = TEXT_SECONDARY,
                                    fontWeight = FontWeight.Medium, maxLines = 1, softWrap = false)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Box(Modifier.fillMaxWidth().height(32.dp).clipToBounds(),
                    contentAlignment = Alignment.Center) {
                    DollarAmount(
                        text = thirdBlockText,
                        color = thirdBlockColor,
                        fontSize = STAT_AMOUNT_FONT_SIZE,
                        fontWeight = FontWeight.Bold,
                        showDollar = !isFilterMode
                    )
                }
            }
        }
    }
}

private val DIGIT_SLIDE_ENTER = spring<IntOffset>(dampingRatio = 0.65f, stiffness = 450f)
private val DIGIT_SLIDE_EXIT = spring<IntOffset>(dampingRatio = 0.7f, stiffness = 400f)
private val DIGIT_FLOAT_ENTER = spring<Float>(dampingRatio = 0.65f, stiffness = 450f)
private val DIGIT_FLOAT_EXIT = spring<Float>(dampingRatio = 0.7f, stiffness = 400f)
private val DIGIT_SIZE_SPRING = spring<IntSize>(dampingRatio = 0.75f, stiffness = 350f)

@Composable
fun AnimatedAmount(
    text: String, color: Color, fontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Bold, modifier: Modifier = Modifier,
) {
    val maxSlots = remember { mutableIntStateOf((text.length + 2).coerceAtLeast(3)) }
    LaunchedEffect(text.length) {
        if (text.length > maxSlots.value) maxSlots.value = text.length
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        for (i in 0 until maxSlots.value) {
            val char = text.getOrNull(i)
            key(i) {
                AnimatedContent(
                    targetState = char,
                    transitionSpec = {
                        if (targetState != null && initialState == null) {
                            (slideInVertically(DIGIT_SLIDE_ENTER) { it / 2 } +
                             fadeIn(tween(200)) +
                             scaleIn(initialScale = 0.4f, animationSpec = DIGIT_FLOAT_ENTER)) togetherWith
                            fadeOut(tween(0))
                        } else if (targetState == null && initialState != null) {
                            fadeIn(tween(0)) togetherWith
                            (slideOutVertically(DIGIT_SLIDE_EXIT) { it / 2 } +
                             fadeOut(tween(180)) +
                             scaleOut(targetScale = 0.4f, animationSpec = DIGIT_FLOAT_EXIT))
                        } else {
                            (slideInVertically(DIGIT_SLIDE_ENTER) { it } + fadeIn(tween(220))) togetherWith
                            (slideOutVertically(DIGIT_SLIDE_EXIT) { -it } + fadeOut(tween(220)))
                        }.using(SizeTransform(clip = false, sizeAnimationSpec = { _, _ -> DIGIT_SIZE_SPRING }))
                    },
                    label = "digit_$i"
                ) { c ->
                    if (c != null) {
                        Text(text = c.toString(), color = color, fontSize = fontSize,
                            fontWeight = fontWeight, maxLines = 1, softWrap = false)
                    } else {
                        Spacer(Modifier.width(0.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SmartElement(
    listState: LazyListState?, dateKey: String,
    isStaticWithNext: Boolean, isStaticWithPrev: Boolean,
    modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    Box(modifier) { content() }
}

@Composable
fun DayHeader(
    dateKey: String, income: Double, expense: Double,
    listState: LazyListState? = null,
    prevDateKey: String? = null, prevIncome: Double? = null, prevExpense: Double? = null,
    nextDateKey: String? = null, nextIncome: Double? = null, nextExpense: Double? = null
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
                    Text(weekStr, fontSize = 11.sp, fontWeight = FontWeight.Medium,
                        color = if (isToday) BRAND_PRIMARY.copy(alpha = 0.8f) else TEXT_TERTIARY)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            if (income > 0) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("收入", fontSize = 10.sp, color = TEXT_TERTIARY, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(2.dp))
                    Text(incStr, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = COLOR_INCOME)
                }
            }
            if (expense > 0) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("支出", fontSize = 10.sp, color = TEXT_TERTIARY, fontWeight = FontWeight.Medium)
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
fun QuickInputChip(name: String, iconUrl: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
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
        modifier = modifier.height(36.dp).clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        shadowElevation = 0.dp
    ) {
        Row(Modifier.padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            IconView(iconUrl, name, size = 18.dp)
            Spacer(Modifier.width(6.dp))
            Text(name, fontSize = 13.sp, color = TEXT_PRIMARY,
                fontWeight = FontWeight.Medium, maxLines = 1,
                overflow = TextOverflow.Ellipsis)
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
    maxRowsPerPage: Int = 4,
    skipAppearAnimation: Boolean = false,
    onClick: (String) -> Unit
) {
    if (topNotes.isEmpty()) return

    val topNoteNames = remember(topNotes) { topNotes.map { it.first } }
    val items = remember(topNoteNames) { topNoteNames.take(42).map { it to 0 } }

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

    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
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
                    val itemPx = baseItemPx + textW
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

            if (pagesWithChips.isEmpty()) return@BoxWithConstraints

            val pagerState = rememberPagerState(pageCount = { pagesWithChips.size })

            LaunchedEffect(pagesWithChips.size) {
                val maxPage = (pagesWithChips.size - 1).coerceAtLeast(0)
                if (pagerState.currentPage > maxPage) pagerState.scrollToPage(maxPage)
            }

            val hasIndicator = pagesWithChips.size > 1
            val safeCurrentPage = pagerState.currentPage.coerceIn(0, pagesWithChips.lastIndex)

            val targetHeightDp = remember(pagesWithChips, safeCurrentPage, hasIndicator) {
                val currentRowCount = pagesWithChips[safeCurrentPage].let { chips ->
                    if (chips.isEmpty()) 1 else {
                        val maxY = chips.maxOf { it.targetY }
                        ((maxY / (chipHeightPx + verticalGapPx)).roundToInt() + 1)
                    }
                }
                chipHeightDp * currentRowCount +
                verticalGapDp * (currentRowCount - 1).coerceAtLeast(0) +
                panelVPaddingDp * 2 +
                (if (hasIndicator) indicatorSpaceDp else 0.dp)
            }

            val animatedHeight by animateDpAsState(
                targetValue = targetHeightDp,
                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 350f),
                label = "quickPanelHeight"
            )

            Box(
                Modifier.fillMaxWidth().height(animatedHeight)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SURFACE_CARD)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Top
                ) { page ->
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
                                        onClick = { onClick(chip.name) }
                                    )
                                }
                            }
                        }
                    }
                }

                if (hasIndicator) {
                    Row(Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val safeCurrent = pagerState.currentPage.coerceIn(0, pagesWithChips.lastIndex)
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
        Column(Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(2.dp))
            Box(contentAlignment = Alignment.Center) {
                AnimatedAmount(text = valueText, color = valueColor,
                    fontSize = valueFontSize, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CalendarRow(
    rowIdx: Int, firstDayOffset: Int, daysInMonth: Int, today: Calendar,
    isCurrentMonth: Boolean, recordsByDay: Map<Int, List<Record>>,
    displayMode: Int, maxAbsNet: Double,
    selectedDay: Int?, selectedCol: Int?, isVisible: Boolean,
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

    val contentHeight: Dp = when (displayMode) {
        0 -> 52.dp
        1 -> (34 + maxItemsInRow * 16).dp.coerceAtLeast(56.dp)
        2 -> {
            val iconRows = (maxItemsInRow + 2) / 3
            (30 + iconRows * 21).dp.coerceAtLeast(52.dp)
        }
        else -> 52.dp
    }

    val cellMaxScale = 1.06f
    val verticalPad = (contentHeight * (cellMaxScale - 1f) / 2f + 2.dp).coerceAtLeast(4.dp)
    val horizontalPad = 3.dp

    val rowHeight by animateDpAsState(
        targetValue = if (isVisible) contentHeight + verticalPad * 2 else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow),
        label = "rowHeight_$rowIdx"
    )

    val todayDayOfMonth = remember(today) { today.get(Calendar.DAY_OF_MONTH) }

    Row(
        Modifier.fillMaxWidth().height(rowHeight).clipToBounds().padding(horizontal = horizontalPad),
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
                        isVisible = isVisible,
                        records = dayRecords,
                        displayMode = displayMode,
                        maxAbsNet = maxAbsNet,
                        onClick = { onCellClick(dayOfMonth, col) }
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
    day: Int, isToday: Boolean, isSelected: Boolean, isVisible: Boolean = true,
    records: List<Record>, displayMode: Int, maxAbsNet: Double, onClick: () -> Unit,
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
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow),
        label = "selScale"
    )
    val visibilityScale = if (isVisible) 1f else {
        animateFloatAsState(
            targetValue = 0f,
            animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow),
            label = "cellVis_$day"
        ).value
    }
    val borderWidth = if (isSelected) 2.dp else 0.5.dp
    val borderColor = if (isSelected) BRAND_PRIMARY else DIVIDER_COLOR
    val elevation = if (hasRecords || isToday || isSelected) 1.dp else 0.dp

    val barRatio = if (maxAbsNet > 0.0 && hasRecords && absNet > 0.0)
        (absNet / maxAbsNet).toFloat().coerceIn(0f, 1f)
    else 0f
    val barColor = if (net >= 0) CELL_BAR_POS else CELL_BAR_NEG

    Box(
        Modifier.fillMaxSize()
            .graphicsLayer {
                val s = selectionScale * visibilityScale
                scaleX = s; scaleY = s; alpha = visibilityScale
            }
            .shadow(elevation, shape, clip = false,
                ambientColor = Color(0x1A000000), spotColor = Color(0x1A000000))
            .clip(shape)
            .background(bgColor)
            .border(width = borderWidth, color = borderColor, shape = shape)
            .clickable(interactionSource = remember { MutableInteractionSource() },
                indication = null) { onClick() }
    ) {
        if (displayMode == 0 && barRatio > 0.001f) {
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .fillMaxHeight(barRatio).background(barColor.copy(alpha = 0.75f)))
        }

        Column(
            Modifier.fillMaxSize().padding(horizontal = 2.dp, vertical = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text("$day", fontSize = 13.sp, lineHeight = 14.sp, fontWeight = FontWeight.Black,
                color = if (isToday || isSelected) BRAND_PRIMARY_DARK else Color.Black,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(5.dp))

            when (displayMode) {
                0 -> {
                    if (absNet > 0) {
                        Text(text = summary.compactNet, fontSize = 11.sp, lineHeight = 13.sp,
                            color = if (net >= 0) COLOR_INCOME else COLOR_EXPENSE,
                            fontWeight = FontWeight.Black, maxLines = 1, softWrap = false,
                            overflow = TextOverflow.Clip, textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth())
                    }
                }
                1 -> {
                    summary.sortedAsc.forEach { r ->
                        Row(Modifier.fillMaxWidth().height(16.dp)) {
                            FadedText(text = r.note.ifBlank { "(無)" }, color = TEXT_PRIMARY,
                                fontSize = 9.sp, fontWeight = FontWeight.Medium,
                                fadeWidth = 6.dp,
                                modifier = Modifier.weight(1f).alignByBaseline())
                            Spacer(Modifier.width(4.dp))
                            val compactRecAmt = remember(r.amount, r.category) {
                                if (r.category == INCOME_CATEGORY) compactAmount(r.amount)
                                else compactAmount(-r.amount)
                            }
                            Text(text = compactRecAmt,
                                fontSize = 9.sp, lineHeight = 11.sp,
                                color = amountColor(r.category), fontWeight = FontWeight.Bold,
                                maxLines = 1, softWrap = false,
                                modifier = Modifier.alignByBaseline())
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
                2 -> {
                    summary.chunked3.forEach { row ->
                        Row(Modifier.fillMaxWidth().height(18.dp).padding(horizontal = 1.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            row.forEach { r ->
                                IconViewAdaptive(iconUrl = r.iconUrl, name = r.note,
                                    modifier = Modifier.weight(1f).fillMaxHeight())
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

private val CALENDAR_WEEKDAY_HEADERS = listOf("一", "二", "三", "四", "五", "六", "日")

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
    val today = remember { Calendar.getInstance() }
    val todayKey = remember { monthKeyFromTimestamp(System.currentTimeMillis()) }

    var monthDirection by remember { mutableIntStateOf(1) }
    var displayMode by remember { mutableIntStateOf(0) }

    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var selectedRowIdx by remember { mutableStateOf<Int?>(null) }
    var selectedColIdx by remember { mutableStateOf<Int?>(null) }
    var detailExpandedId by remember { mutableStateOf<String?>(null) }

    var cachedSelectedDay by remember { mutableIntStateOf(1) }
    var cachedSelectedCol by remember { mutableIntStateOf(0) }

    val scrollState = rememberScrollState()

    var activeFanRecord by remember { mutableStateOf<Record?>(null) }
    val fanMenuCenterState = remember { mutableStateOf(Offset.Zero) }
    val fanMenuTouchState = remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val fanRadiusPx = remember(density) { with(density) { 105.dp.toPx() } }
    val fanHalfBtnPx = remember(density) { with(density) { 24.dp.toPx() } }
    val fanEdgeThreshold = fanRadiusPx + fanHalfBtnPx

    val fanMenuProgress = animateFloatAsState(
        targetValue = if (activeFanRecord != null) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
        label = "fanMenuProgress"
    )

    val minFanRadiusPx = remember(density) { with(density) { 40.dp.toPx() } }
    val latestOnCopy by rememberUpdatedState(onCopyClick)
    val latestOnEdit by rememberUpdatedState(onEditClick)
    val latestOnFilter by rememberUpdatedState(onFilterClick)
    val latestOnDelete by rememberUpdatedState(onDeleteClick)

    val handleFanStart = remember {
        { r: Record, offset: Offset ->
            activeFanRecord = r
            fanMenuCenterState.value = offset
            fanMenuTouchState.value = offset
        }
    }
    val handleFanDrag = remember {
        { dragAmount: Offset -> fanMenuTouchState.value += dragAmount }
    }
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
            val hovered = getHoveredActionIndexWithCenter(
                center = center, touch = fanMenuTouchState.value,
                minRadiusPx = minFanRadiusPx, arcCenterAngle = arcCenterAngle
            )
            val targetRecord = activeFanRecord
            activeFanRecord = null
            if (targetRecord != null && hovered != null) {
                scope.launch {
                    delay(120)
                    when (hovered) {
                        0 -> latestOnCopy(targetRecord)
                        1 -> latestOnEdit(targetRecord)
                        2 -> latestOnFilter(targetRecord)
                        3 -> latestOnDelete(targetRecord)
                    }
                }
            }
        }
    }

    val curCal = remember(currentMonthKey) {
        val parts = currentMonthKey.split("-")
        Calendar.getInstance().apply {
            set(parts[0].toInt(), parts[1].toInt() - 1, 1)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
    }
    val year = curCal.get(Calendar.YEAR)
    val month = curCal.get(Calendar.MONTH) + 1
    val daysInMonth = curCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val isCurrentMonth = currentMonthKey == todayKey

    val recordsByDay = calendarUi.recordsByDay
    val maxAbsNet = calendarUi.maxAbsNet
    val totalNet = calendarUi.totalNet
    val avgPerItem = calendarUi.avgPerItem
    val avgPerDay = calendarUi.avgPerDay
    val visibleCategories = calendarUi.visibleCategories

    val firstDayOffset = remember(curCal) {
        val first = curCal.clone() as Calendar
        first.set(Calendar.DAY_OF_MONTH, 1)
        (first.get(Calendar.DAY_OF_WEEK) + 5) % 7
    }
    val totalCells = firstDayOffset + daysInMonth
    val totalRows = (totalCells + 6) / 7

    LaunchedEffect(currentMonthKey) {
        selectedDay = null
        selectedRowIdx = null
        selectedColIdx = null
        detailExpandedId = null
    }

    LaunchedEffect(selectedDay) {
        if (selectedDay != null) {
            try { scrollState.animateScrollTo(0) } catch (_: Exception) {}
        }
    }

    BackHandler(enabled = selectedDay != null) {
        selectedDay = null
        selectedRowIdx = null
        selectedColIdx = null
        detailExpandedId = null
    }

    Box(Modifier.fillMaxSize().background(SURFACE_BG)) {
        Column(Modifier.fillMaxSize()) {
            Column(Modifier.padding(horizontal = 10.dp)) {
                Spacer(Modifier.height(8.dp))

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        onClick = { monthDirection = -1; onMonthChange(shiftMonthKey(currentMonthKey, -1)) },
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape, color = SURFACE_CARD,
                        border = BorderStroke(0.5.dp, DIVIDER_COLOR)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ChevronLeft, "上個月",
                                tint = Color.Black, modifier = Modifier.size(22.dp))
                        }
                    }
                    Spacer(Modifier.width(10.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (currentMonthKey != todayKey) {
                                monthDirection = if (todayKey > currentMonthKey) 1 else -1
                                onMonthChange(todayKey)
                            }
                        }
                    ) {
                        Text("${year}年", fontSize = 11.sp, lineHeight = 12.sp,
                            color = Color.Black, fontWeight = FontWeight.Medium)
                        AnimatedContent(
                            targetState = currentMonthKey,
                            transitionSpec = {
                                val forward = monthDirection > 0
                                if (forward) {
                                    (slideInHorizontally(animationSpec = tween(280)) { it } + fadeIn(tween(200)))
                                        .togetherWith(slideOutHorizontally(animationSpec = tween(280)) { -it } + fadeOut(tween(180)))
                                } else {
                                    (slideInHorizontally(animationSpec = tween(280)) { -it } + fadeIn(tween(200)))
                                        .togetherWith(slideOutHorizontally(animationSpec = tween(280)) { it } + fadeOut(tween(180)))
                                }
                            },
                            label = "monthAnim"
                        ) { key ->
                            val m = key.split("-").getOrNull(1)?.toIntOrNull() ?: month
                            Text("${m}月", fontSize = 28.sp, lineHeight = 30.sp,
                                fontWeight = FontWeight.Bold, color = TEXT_PRIMARY)
                        }
                    }

                    Spacer(Modifier.width(10.dp))
                    Surface(
                        onClick = { monthDirection = 1; onMonthChange(shiftMonthKey(currentMonthKey, 1)) },
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape, color = SURFACE_CARD,
                        border = BorderStroke(0.5.dp, DIVIDER_COLOR)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ChevronRight, "下個月",
                                tint = Color.Black, modifier = Modifier.size(22.dp))
                        }
                    }

                    Spacer(Modifier.weight(1f))
                    SegmentedModeControl(displayMode = displayMode, onModeChange = { displayMode = it })
                }

                Spacer(Modifier.height(10.dp))

                AnimatedVisibility(
                    visible = selectedDay == null,
                    enter = fadeIn(tween(250)) + expandVertically(tween(300, easing = FastOutSlowInEasing)),
                    exit = fadeOut(tween(200)) + shrinkVertically(tween(250, easing = FastOutSlowInEasing))
                ) {
                    Column {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 72.dp),
                            modifier = Modifier.fillMaxWidth().animateContentSize(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            userScrollEnabled = false
                        ) {
                            item(key = "__all__") {
                                CalendarFilterChip(
                                    modifier = Modifier.fillMaxWidth(),
                                    selected = filterCategory == null,
                                    label = "全部",
                                    onClick = { onFilterCategoryChange(null) }
                                )
                            }
                            items(items = visibleCategories, key = { it }) { cat ->
                                CalendarFilterChip(
                                    modifier = Modifier.animateItem().fillMaxWidth(),
                                    selected = filterCategory == cat,
                                    label = cat,
                                    onClick = {
                                        onFilterCategoryChange(if (filterCategory == cat) null else cat)
                                    }
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AnimatedInfoChip(
                                label = "總計",
                                valueText = "$${formatAmountNoDecimal(totalNet)}",
                                modifier = Modifier.weight(1f),
                                valueFontSize = 16.sp,
                                valueColor = if (totalNet >= 0) COLOR_INCOME else COLOR_EXPENSE
                            )
                            AnimatedInfoChip(
                                label = "平均每項",
                                valueText = "$${formatAmountNoDecimal(avgPerItem)}",
                                modifier = Modifier.weight(1f),
                                valueFontSize = 16.sp
                            )
                            AnimatedInfoChip(
                                label = "日均支出",
                                valueText = "$${formatAmountNoDecimal(avgPerDay)}",
                                modifier = Modifier.weight(1f),
                                valueFontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                Row(Modifier.fillMaxWidth()) {
                    CALENDAR_WEEKDAY_HEADERS.forEach { w ->
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text(w, fontSize = 12.sp, color = Color.Black,
                                fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }

            Box(Modifier.weight(1f).fillMaxWidth()) {
                val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
                val cellWidth = (screenWidthDp - 20.dp) / 7

                Column(
                    Modifier.fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 10.dp)
                        .padding(top = 10.dp)
                        .padding(bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 12.dp + 56.dp + 12.dp)
                ) {
                    val masterSpring = spring<IntSize>(dampingRatio = 0.85f, stiffness = 350f)
                    val fadeAnim = tween<Float>(250, easing = FastOutSlowInEasing)

                    for (rowIdx in 0 until totalRows) {
                        val isThisRowSelected = selectedRowIdx == rowIdx
                        val isHidden = selectedDay != null && !isThisRowSelected

                        Column(Modifier.fillMaxWidth()) {
                            CalendarRow(
                                rowIdx = rowIdx,
                                firstDayOffset = firstDayOffset,
                                daysInMonth = daysInMonth,
                                today = today,
                                isCurrentMonth = isCurrentMonth,
                                recordsByDay = recordsByDay,
                                displayMode = displayMode,
                                maxAbsNet = maxAbsNet,
                                selectedDay = if (isThisRowSelected) selectedDay else null,
                                selectedCol = if (isThisRowSelected) selectedColIdx else null,
                                isVisible = !isHidden,
                                onCellClick = { day, col ->
                                    if (selectedDay == day) {
                                        selectedDay = null
                                        selectedRowIdx = null
                                        selectedColIdx = null
                                        detailExpandedId = null
                                    } else {
                                        selectedDay = day
                                        selectedRowIdx = rowIdx
                                        selectedColIdx = col
                                        cachedSelectedDay = day
                                        cachedSelectedCol = col
                                        detailExpandedId = null
                                    }
                                }
                            )

                            AnimatedVisibility(
                                visible = isThisRowSelected && selectedDay != null,
                                enter = expandVertically(masterSpring, expandFrom = Alignment.Top) + fadeIn(fadeAnim),
                                exit = shrinkVertically(masterSpring, shrinkTowards = Alignment.Top) + fadeOut(fadeAnim)
                            ) {
                                Column(Modifier.fillMaxWidth()) {
                                    Spacer(Modifier.height(8.dp))

                                    val activeDay = if (isThisRowSelected && selectedDay != null)
                                        selectedDay!! else cachedSelectedDay
                                    val activeCol = if (isThisRowSelected && selectedColIdx != null)
                                        selectedColIdx!! else cachedSelectedCol

                                    TrianglePointer(
                                        colIndex = activeCol,
                                        cellWidth = cellWidth,
                                        color = SURFACE_CARD
                                    )

                                    val dateKey = "$year-${pad2(month)}-${pad2(activeDay)}"

                                    DayDetailPanel(
                                        dateKey = dateKey,
                                        records = recordsByDay[activeDay] ?: emptyList(),
                                        expandedId = detailExpandedId,
                                        onExpandChange = { detailExpandedId = it },
                                        onCopy = { onCopyClick(it) },
                                        onEdit = { onEditClick(it) },
                                        onFilter = { onFilterClick(it) },
                                        onDelete = { onDeleteClick(it) },
                                        onChangeIcon = { onChangeIconClick(it) },
                                        onDismiss = {
                                            selectedDay = null
                                            selectedRowIdx = null
                                            selectedColIdx = null
                                            detailExpandedId = null
                                        },
                                        deletingIds = deletingIds,
                                        justAddedIds = justAddedIds,
                                        onFanMenuStart = handleFanStart,
                                        onFanMenuDrag = handleFanDrag,
                                        onFanMenuEnd = handleFanEnd,
                                        activeFanRecord = activeFanRecord
                                    )
                                    Spacer(Modifier.height(12.dp))
                                }
                            }

                            if (!isHidden && !(isThisRowSelected && selectedDay != null)) {
                                Spacer(Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }
        }

        FanMenuOverlay(
            progressProvider = { fanMenuProgress.value },
            centerProvider = { fanMenuCenterState.value },
            touchProvider = { fanMenuTouchState.value }
        )
    }
}

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
    amount: Double, isIncome: Boolean, pct: Double?, modifier: Modifier = Modifier
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
        Text(amtDisplay, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold,
            color = amtColor, textAlign = TextAlign.End, maxLines = 1, softWrap = false,
            overflow = TextOverflow.Clip, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(4.dp))
        Box(Modifier.width(38.dp), contentAlignment = Alignment.CenterEnd) {
            if (pctText != null) {
                Text(pctText, fontSize = 11.sp, color = TEXT_TERTIARY,
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

    Box(Modifier.fillMaxSize().background(SURFACE_BG)) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 80.dp)
        ) {
            Text("月份比較", fontSize = 26.sp, fontWeight = FontWeight.Bold,
                color = TEXT_PRIMARY, modifier = Modifier.padding(vertical = 8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = SURFACE_CARD,
                border = BorderStroke(0.5.dp, DIVIDER_COLOR)
            ) {
                Column(Modifier.fillMaxSize()) {
                    Row(
                        Modifier.fillMaxWidth().background(SURFACE_CARD)
                            .padding(horizontal = 10.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("類別", Modifier.weight(1.0f),
                            fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TEXT_SECONDARY)
                        Spacer(Modifier.weight(0.65f))

                        Row(Modifier.weight(1.55f),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically) {
                            MonthDropdown(
                                value = selectedMonthA,
                                months = monthlyKeysThisYear,
                                onChange = { onMonthAChange(it) }
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        Row(Modifier.weight(1.55f),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically) {
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
                                val calculatedHeight =
                                    (maxHeight - totalDividerHeight) / rowCount
                                val rowHeight = calculatedHeight.coerceAtLeast(44.dp)

                                LazyColumn(Modifier.fillMaxSize(), userScrollEnabled = false) {
                                    itemsIndexed(items = displayCategories, key = { _, cat -> cat }) { idx, cat ->
                                        val amtA = sumLocal(cat, selectedMonthA)
                                        val amtB = sumLocal(cat, selectedMonthB)
                                        val style = CATEGORY_STYLES[cat]
                                        val isIncome = cat == INCOME_CATEGORY

                                        val pctA = if (incomeA > 0.0 && !isIncome && amtA > 0.0)
                                            (amtA / incomeA * 100.0) else null
                                        val pctB = if (incomeB > 0.0 && !isIncome && amtB > 0.0)
                                            (amtB / incomeB * 100.0) else null

                                        Column(
                                            Modifier.fillMaxWidth().animateItem(
                                                fadeInSpec = tween(220, easing = FastOutSlowInEasing),
                                                fadeOutSpec = tween(180, easing = FastOutSlowInEasing),
                                                placementSpec = spring(
                                                    stiffness = Spring.StiffnessMediumLow,
                                                    dampingRatio = Spring.DampingRatioLowBouncy
                                                )
                                            )
                                        ) {
                                            Row(
                                                Modifier.fillMaxWidth().height(rowHeight)
                                                    .background(SURFACE_CARD)
                                                    .padding(horizontal = 10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(Modifier.weight(1.0f),
                                                    verticalAlignment = Alignment.CenterVertically) {
                                                    if (style != null) {
                                                        Box(
                                                            Modifier.size(26.dp)
                                                                .clip(RoundedCornerShape(7.dp))
                                                                .background(style.bgColor),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Icon(style.icon, null, tint = style.fgColor,
                                                                modifier = Modifier.size(15.dp))
                                                        }
                                                        Spacer(Modifier.width(7.dp))
                                                    }
                                                    Text(cat, fontSize = 15.sp, color = TEXT_PRIMARY,
                                                        fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Medium)
                                                }

                                                Box(
                                                    Modifier.weight(0.65f).fillMaxHeight()
                                                        .padding(start = 12.dp, end = 16.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (!isIncome) {
                                                        val values = histogramData[cat].orEmpty()
                                                        val histHeight =
                                                            (rowHeight - 16.dp).coerceAtLeast(20.dp)
                                                        MiniHistogram(
                                                            values = values,
                                                            color = style?.fgColor ?: TEXT_TERTIARY,
                                                            modifier = Modifier.fillMaxWidth().height(histHeight)
                                                        )
                                                    }
                                                }

                                                CompareValueCell(
                                                    amount = amtA, isIncome = isIncome, pct = pctA,
                                                    modifier = Modifier.weight(1.55f)
                                                )
                                                Spacer(Modifier.width(6.dp))
                                                CompareValueCell(
                                                    amount = amtB, isIncome = isIncome, pct = pctB,
                                                    modifier = Modifier.weight(1.55f)
                                                )
                                            }

                                            if (idx < displayCategories.lastIndex) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(horizontal = 10.dp),
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

                    Row(
                        Modifier.fillMaxWidth().background(SURFACE_CARD)
                            .padding(horizontal = 10.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("餘額", Modifier.weight(1.0f),
                            fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BRAND_PRIMARY_DARK)
                        Spacer(Modifier.weight(0.65f))

                        CompareValueCell(
                            amount = balanceA, isIncome = balanceA >= 0, pct = null,
                            modifier = Modifier.weight(1.55f)
                        )
                        Spacer(Modifier.width(6.dp))
                        CompareValueCell(
                            amount = balanceB, isIncome = balanceB >= 0, pct = null,
                            modifier = Modifier.weight(1.55f)
                        )
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

suspend fun compressImage(context: Context, uri: Uri, targetSize: Int = ICON_SIZE): ByteArray? =
    withContext(Dispatchers.IO) {
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

            val bgRemovedBitmap = removeBackground(src)

            val processed = if (bgRemovedBitmap.width > bgRemovedBitmap.height) {
                scaleKeepAspectWidth(bgRemovedBitmap, targetSize)
            } else {
                scaleCropCenter(bgRemovedBitmap, targetSize)
            }

            val baos = ByteArrayOutputStream()
            processed.compress(Bitmap.CompressFormat.PNG, 100, baos)

            if (processed !== bgRemovedBitmap && processed !== src) processed.recycle()
            if (bgRemovedBitmap !== src) bgRemovedBitmap.recycle()
            src.recycle()

            baos.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

suspend fun uploadIconAndApplyToSameName(context: Context, db: FirebaseFirestore,
    recordId: String, recordName: String, uri: Uri) {
    try {
        val bytes = compressImage(context, uri) ?: run {
            Toast.makeText(context, "讀取圖片失敗", Toast.LENGTH_LONG).show(); return
        }
        val url = uploadBytesToCloudinary(bytes) ?: run {
            Toast.makeText(context, "上傳失敗,請檢查網絡", Toast.LENGTH_LONG).show(); return
        }
        applyUrlToSameName(context, db, recordName, url)
    } catch (e: Exception) { Toast.makeText(context, "失敗:${e.message}", Toast.LENGTH_LONG).show() }
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

// === 文件結束 ===
