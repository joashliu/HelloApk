package com.example.hello

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
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
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.imageLoader
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import java.util.Locale
import java.util.Random
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

// ===== 配色 =====
val BRAND_PRIMARY = Color(0xFF6366F1)
val BRAND_PRIMARY_DARK = Color(0xFF4F46E5)
val BRAND_PRIMARY_LIGHT = Color(0xFFE0E7FF)
val SURFACE_BG = Color(0xFFF8FAFC)
val SURFACE_CARD = Color(0xFFFFFFFF)
val SURFACE_ELEVATED = Color(0xFFF1F5F9)
val TEXT_PRIMARY = Color(0xFF0F172A)
val TEXT_SECONDARY = Color(0xFF64748B)
val TEXT_TERTIARY = Color(0xFF94A3B8)
val DIVIDER_COLOR = Color(0xFFE2E8F0)

val COLOR_INCOME = Color(0xFF047857)
val COLOR_EXPENSE = Color(0xFFB91C1C)

val CELL_BG_POS = Color(0xFFD1FAE5)
val CELL_BAR_POS = Color(0xFF34D399)
val CELL_BG_NEG = Color(0xFFFEE2E2)
val CELL_BAR_NEG = Color(0xFFF87171)

val WEEKDAY_BG = Color(0xFFE0E7FF)
val WEEKDAY_FG = Color(0xFF4F46E5)
val WEEKEND_BG = Color(0xFFFFE4E6)
val WEEKEND_FG = Color(0xFFBE123C)

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

data class CategoryStyle(val icon: ImageVector, val bgColor: Color, val fgColor: Color)

val CATEGORIES = listOf("收入", "娛樂", "家用", "飲食", "交通", "個人", "購物", "月費", "旅遊")
val EXPENSE_CATEGORIES = CATEGORIES.filter { it != "收入" }
val INCOME_CATEGORY = "收入"

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

data class Record(
    val amount: Double = 0.0,
    val note: String = "",
    val category: String = "飲食",
    val timestamp: Long = System.currentTimeMillis(),
    val iconUrl: String = "",
    var id: String = ""
)

data class NavItem(val label: String, val icon: ImageVector)

data class KeyboardState(
    val amountText: String = "",
    val noteText: String = "",
    val category: String = "飲食",
    val editingNote: Boolean = false,
    val editingRecordId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val selectAmountOnInput: Boolean = false
)

data class AfterSaveHint(
    val recordId: String,
    val category: String,
    val monthTotal: Double
)

data class DateHeaderInfo(
    val year: Int,
    val month: Int,
    val day: Int,
    val weekdayChar: String,
    val isWeekend: Boolean,
    val dayTag: String?
)

object AboveAnchorPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val x = anchorBounds.left
        val yAbove = anchorBounds.top - popupContentSize.height - 4
        if (yAbove >= 0) return IntOffset(x, yAbove)
        val yBelow = anchorBounds.bottom + 4
        if (yBelow + popupContentSize.height <= windowSize.height) {
            return IntOffset(x, yBelow)
        }
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
        absV < 10000 -> {
            val k = absV / 1000.0
            val s = String.format(Locale.US, "%.1f", k).removeSuffix(".0")
            "${sign}${s}K"
        }
        absV < 1000000 -> "${sign}${(absV / 1000.0).roundToInt()}K"
        absV < 10000000 -> {
            val m = absV / 1000000.0
            val s = String.format(Locale.US, "%.1f", m).removeSuffix(".0")
            "${sign}${s}M"
        }
        else -> "${sign}${(absV / 1000000.0).roundToInt()}M"
    }
}
fun displayAmount(record: Record): String =
    if (record.category == INCOME_CATEGORY) formatAmount(record.amount) else formatAmount(-record.amount)
fun amountColor(category: String): Color =
    if (category == INCOME_CATEGORY) COLOR_INCOME else COLOR_EXPENSE

fun relativeDayLabel(timestamp: Long): String {
    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val recStart = Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
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
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    val wk = arrayOf("週日","週一","週二","週三","週四","週五","週六")[cal.get(Calendar.DAY_OF_WEEK)-1]
    val hh = String.format(Locale.US, "%02d", cal.get(Calendar.HOUR_OF_DAY))
    val mm = String.format(Locale.US, "%02d", cal.get(Calendar.MINUTE))
    return "$wk．$hh:$mm．${relativeDayLabel(timestamp)}"
}

fun formatDatePart(timestamp: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    val wk = arrayOf("週日","週一","週二","週三","週四","週五","週六")[cal.get(Calendar.DAY_OF_WEEK)-1]
    return "${cal.get(Calendar.MONTH)+1}月${cal.get(Calendar.DAY_OF_MONTH)}日 $wk"
}

fun formatTimePart(timestamp: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    return String.format(Locale.US, "%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
}

fun dateKeyFromTimestamp(timestamp: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    return String.format(Locale.US, "%04d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH))
}
fun monthKeyFromTimestamp(timestamp: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    return String.format(Locale.US, "%04d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1)
}
fun shiftMonthKey(key: String, delta: Int): String {
    val parts = key.split("-")
    if (parts.size != 2) return key
    val y = parts[0].toIntOrNull() ?: return key
    val m = parts[1].toIntOrNull() ?: return key
    val cal = Calendar.getInstance().apply { set(y, m - 1, 1) }
    cal.add(Calendar.MONTH, delta)
    return String.format(Locale.US, "%04d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
}
fun formatMonthLabel(ym: String): String {
    val p = ym.split("-"); if (p.size != 2) return ym
    val y = p[0].toIntOrNull() ?: return ym; val m = p[1].toIntOrNull() ?: return ym
    return if (y == Calendar.getInstance().get(Calendar.YEAR)) "${m}月" else "${y % 100}年${m}月"
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
    val cal = Calendar.getInstance().apply {
        set(y, mo - 1, d)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val dow = cal.get(Calendar.DAY_OF_WEEK)
    val weekArr = arrayOf("日", "一", "二", "三", "四", "五", "六")
    val weekdayChar = weekArr[dow - 1]
    val isWeekend = dow == Calendar.SUNDAY || dow == Calendar.SATURDAY

    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
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
    val prefix = all.filter { it.startsWith(q, ignoreCase = true) && it != q }
    val contains = all.filter { !it.startsWith(q, true) && it.contains(q, true) }
    return (prefix + contains).take(8)
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

fun timeOfDayDistanceSeconds(timestamp: Long, nowMillis: Long): Int {
    val calNow = Calendar.getInstance().apply { timeInMillis = nowMillis }
    val secNow = calNow.get(Calendar.HOUR_OF_DAY) * 3600 + calNow.get(Calendar.MINUTE) * 60
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    val sec = cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60
    val d = abs(secNow - sec)
    return minOf(d, 86400 - d)
}

fun getDominantMutedColor(bitmap: Bitmap): Color {
    if (bitmap.width <= 0 || bitmap.height <= 0) return Color(0xFFF1F5F9)
    val targetSize = 72
    val scaled: Bitmap = if (bitmap.width > targetSize || bitmap.height > targetSize) {
        val ratio = minOf(targetSize.toFloat() / bitmap.width, targetSize.toFloat() / bitmap.height)
        val nw = (bitmap.width * ratio).toInt().coerceAtLeast(1)
        val nh = (bitmap.height * ratio).toInt().coerceAtLeast(1)
        Bitmap.createScaledBitmap(bitmap, nw, nh, true)
    } else bitmap

    val w = scaled.width
    val h = scaled.height
    val pixels = IntArray(w * h)
    scaled.getPixels(pixels, 0, w, 0, 0, w, h)
    if (scaled !== bitmap) scaled.recycle()

    val colorCount = mutableMapOf<Int, Int>()
    for (p in pixels) {
        val a = android.graphics.Color.alpha(p)
        if (a < 120) continue
        val r = android.graphics.Color.red(p) / 24 * 24
        val g = android.graphics.Color.green(p) / 24 * 24
        val b = android.graphics.Color.blue(p) / 24 * 24
        val maxC = maxOf(r, g, b)
        val minC = minOf(r, g, b)
        if (maxC > 235 && minC > 220) continue
        if (maxC < 28) continue
        val rgb = android.graphics.Color.rgb(r, g, b)
        colorCount[rgb] = (colorCount[rgb] ?: 0) + 1
    }
    val dominant = colorCount.maxByOrNull { it.value }?.key ?: android.graphics.Color.LTGRAY
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(dominant, hsv)
    hsv[1] = (hsv[1] * 0.42f).coerceAtMost(0.45f)
    hsv[2] = 0.96f
    return Color(android.graphics.Color.HSVToColor(hsv))
}

fun getHoveredActionIndex(center: Offset, touch: Offset, minRadiusPx: Float): Int? {
    val dx = touch.x - center.x
    val dy = touch.y - center.y
    val dist = kotlin.math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
    if (dist < minRadiusPx) return null

    val angle = kotlin.math.atan2(dy.toDouble(), dx.toDouble()) * 180 / Math.PI
    return when {
        angle in -170.0..-130.0 -> 0 
        angle in -130.0..-90.0 -> 1  
        angle in -90.0..-50.0 -> 2   
        angle in -50.0..-10.0 -> 3   
        else -> null
    }
}

// ★ 修改參數為 Provider，避免拖慢整個列表
@Composable
fun FanMenuOverlay(
    progressProvider: () -> Float,
    centerProvider: () -> Offset,
    touchProvider: () -> Offset
) {
    val progress = progressProvider()
    if (progress <= 0.001f) return // 進度係 0 就唔好畫，慳資源

    val center = centerProvider()
    val currentTouch = touchProvider()

    val density = LocalDensity.current
    val minRadiusPx = with(density) { 40.dp.toPx() }
    val hoveredIndex = getHoveredActionIndex(center, currentTouch, minRadiusPx)
    val haptic = LocalHapticFeedback.current
    var previousHovered by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(hoveredIndex) {
        if (hoveredIndex != null && hoveredIndex != previousHovered) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        previousHovered = hoveredIndex
    }

    val radiusPx = with(density) { 105.dp.toPx() }
    val actions = listOf(
        Triple(Icons.Default.ContentCopy, "複製", Color(0xFF64748B)),
        Triple(Icons.Default.Edit, "編輯", Color(0xFF3B82F6)),
        Triple(Icons.Default.FilterList, "篩選", Color(0xFF8B5CF6)),
        Triple(Icons.Default.Delete, "刪除", COLOR_EXPENSE)
    )

    Box(Modifier.fillMaxSize()) {
        actions.forEachIndexed { i, action ->
            val angle = -150f + i * 40f
            val angleRad = Math.toRadians(angle.toDouble())

            val currentRadius = radiusPx * progress
            val cx = center.x + currentRadius * kotlin.math.cos(angleRad).toFloat()
            val cy = center.y + currentRadius * kotlin.math.sin(angleRad).toFloat()

            val isHovered = hoveredIndex == i
            val btnScale = if (isHovered) 1.25f else 1f
            val btnAlpha = progress.coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (cx - with(density) { 24.dp.toPx() }).roundToInt(),
                            (cy - with(density) { 24.dp.toPx() }).roundToInt()
                        )
                    }
                    .size(48.dp)
                    .graphicsLayer {
                        scaleX = btnScale * progress
                        scaleY = btnScale * progress
                        alpha = btnAlpha
                    }
                    .shadow(if (isHovered) 12.dp else 6.dp, CircleShape)
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


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize(), color = SURFACE_BG) { MainApp() }
            }
        }
    }
}

@Composable
fun MainApp() {
    val db = Firebase.firestore
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val prefs = remember { context.getSharedPreferences("ledger_prefs", Context.MODE_PRIVATE) }

    val records = remember { mutableStateListOf<Record>() }
    var loading by remember { mutableStateOf(true) }
    var expandedId by remember { mutableStateOf<String?>(null) }
    var currentPage by remember { mutableIntStateOf(0) }
    var filterModeOn by remember { mutableStateOf(false) }
    var filterCategory by remember { mutableStateOf<String?>(null) }
    var filterMonth by remember { mutableStateOf<String?>(null) }
    var filterSearch by remember { mutableStateOf(TextFieldValue("")) }
    var filterSearchHasFocus by remember { mutableStateOf(false) }
    var filterSelectAllTrigger by remember { mutableIntStateOf(0) }
    val filterSearchFocusRequester = remember { FocusRequester() }
    var iconTargetRecord by remember { mutableStateOf<Record?>(null) }
    var showIconSourceDialog by remember { mutableStateOf(false) }
    var showUrlInputDialog by remember { mutableStateOf(false) }
    var urlInput by remember { mutableStateOf("") }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }
    var showKeyboard by remember { mutableStateOf(false) }

    val keyboardAnimProgress = remember { Animatable(0f) }
    LaunchedEffect(showKeyboard) {
        if (showKeyboard) {
            keyboardAnimProgress.snapTo(0f)
            keyboardAnimProgress.animateTo(1f, tween(480, easing = FastOutSlowInEasing))
        } else {
            keyboardAnimProgress.animateTo(0f, tween(420, easing = FastOutSlowInEasing))
        }
    }

    var keyboardState by remember { mutableStateOf(KeyboardState()) }
    var showFuture by remember { mutableStateOf(false) }
    var scrollToTopTrigger by remember { mutableIntStateOf(0) }
    var nameFlashTrigger by remember { mutableIntStateOf(0) }
    var justAddedId by remember { mutableStateOf<String?>(null) }
    var afterSaveHint by remember { mutableStateOf<AfterSaveHint?>(null) }
    var afterSaveHintVisible by remember { mutableStateOf(false) }
    val animatedQuickInputs = remember { mutableStateMapOf<String, Boolean>() }

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
        if (afterSaveHint?.recordId == hint.recordId) {
            afterSaveHint = null
        }
    }

    val defaultMonthA = remember { monthKeyFromTimestamp(System.currentTimeMillis()) }
    val defaultMonthB = remember { shiftMonthKey(defaultMonthA, -1) }

    var compareMonthA by remember {
        mutableStateOf(prefs.getString("compare_month_a", defaultMonthA) ?: defaultMonthA)
    }
    var compareMonthB by remember {
        mutableStateOf(prefs.getString("compare_month_b", defaultMonthB) ?: defaultMonthB)
    }
    fun setCompareMonthA(v: String) { compareMonthA = v; prefs.edit().putString("compare_month_a", v).apply() }
    fun setCompareMonthB(v: String) { compareMonthB = v; prefs.edit().putString("compare_month_b", v).apply() }

    var recentlyDeletedRecord by remember { mutableStateOf<Record?>(null) }
    var showUndoToast by remember { mutableStateOf(false) }
    var deletingRecordId by remember { mutableStateOf<String?>(null) }
    var deleteJob by remember { mutableStateOf<Job?>(null) }

    val allNoteNames by remember {
        derivedStateOf { records.map { it.note }.filter { it.isNotBlank() }.distinct() }
    }
    val noteCategoryMap by remember {
        derivedStateOf {
            records.filter { it.note.isNotBlank() }
                .groupBy { it.note }
                .mapValues { (_, list) -> list.maxByOrNull { it.timestamp }?.category ?: "飲食" }
        }
    }

    val filtered by remember {
        derivedStateOf {
            val q = filterSearch.text
            val hasExactNoteMatch = q.isNotBlank() && records.any { it.note == q }
            val hasExactCategoryMatch = q.isNotBlank() && CATEGORIES.any { it == q }
            records.toList().filter { r ->
                val catOk = filterCategory == null || r.category == filterCategory
                val monthOk = filterMonth == null || monthKeyFromTimestamp(r.timestamp) == filterMonth
                val searchOk = if (q.isBlank()) true else when {
                    hasExactNoteMatch -> r.note == q
                    hasExactCategoryMatch -> r.category == q
                    else -> r.note.contains(q, true) || r.category.contains(q, true)
                }
                catOk && monthOk && searchOk
            }
        }
    }
    val ledgerRecords by remember {
        derivedStateOf {
            val base = if (filterModeOn) filtered else records.toList()
            if (showFuture) base else base.filter { it.timestamp <= System.currentTimeMillis() + 60_000 }
        }
    }
    
    val hasIncome by remember { derivedStateOf { ledgerRecords.any { it.category == INCOME_CATEGORY && it.id != deletingRecordId } } }
    val hasExpense by remember { derivedStateOf { ledgerRecords.any { it.category != INCOME_CATEGORY && it.id != deletingRecordId } } }
    val totalIncome by remember { derivedStateOf { ledgerRecords.filter { it.category == INCOME_CATEGORY && it.id != deletingRecordId }.sumOf { it.amount } } }
    val totalExpense by remember { derivedStateOf { ledgerRecords.filter { it.category != INCOME_CATEGORY && it.id != deletingRecordId }.sumOf { it.amount } } }
    val groupedByDate by remember { derivedStateOf { ledgerRecords.groupBy { dateKeyFromTimestamp(it.timestamp) }.toList() } }

    val isExactNoteFilter by remember {
        derivedStateOf {
            filterModeOn &&
                ledgerRecords.isNotEmpty() &&
                ledgerRecords.map { it.note }.distinct().size == 1
        }
    }

    val topNotes by remember {
        derivedStateOf {
            val now = System.currentTimeMillis()
            records.toList().filter { it.note.isNotBlank() }
                .groupBy { it.note }
                .map { (note, list) -> note to list.minOf { timeOfDayDistanceSeconds(it.timestamp, now) } }
                .sortedBy { it.second }
                .take(20)
                .map { it.first to 0 }
        }
    }

    val recentAmountByNote by remember {
        derivedStateOf {
            val now = System.currentTimeMillis()
            records.toList().filter { it.note.isNotBlank() }
                .groupBy { it.note }
                .mapValues { (_, list) -> list.minByOrNull { timeOfDayDistanceSeconds(it.timestamp, now) }?.amount ?: 0.0 }
        }
    }

    val noteIconMap by remember {
        derivedStateOf {
            records.toList().filter { it.note.isNotBlank() && it.iconUrl.isNotBlank() }
                .groupBy { it.note }.mapValues { (_, l) -> l.maxByOrNull { it.timestamp }?.iconUrl ?: "" }
        }
    }
    val availableMonths by remember {
        derivedStateOf { records.map { monthKeyFromTimestamp(it.timestamp) }.distinct().sorted() }
    }
    val visibleCategories by remember {
        derivedStateOf {
            val base = if (filterMonth == null) records.toList()
                       else records.filter { monthKeyFromTimestamp(it.timestamp) == filterMonth }
            val hasIncome = base.any { it.category == INCOME_CATEGORY }
            val expenseCats = base.filter { it.category != INCOME_CATEGORY }
                .groupBy { it.category }
                .mapValues { (_, list) -> list.sumOf { it.amount } }
                .toList().sortedByDescending { it.second }.map { it.first }
            (if (hasIncome) listOf(INCOME_CATEGORY) else emptyList()) + expenseCats
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        val target = iconTargetRecord
        if (uri != null && target != null) {
            scope.launch {
                uploading = true
                uploadIconAndApplyToSameName(context, db, target.id, target.note, uri)
                uploading = false
            }
        }
        iconTargetRecord = null
    }
    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val target = iconTargetRecord; val uri = pendingCameraUri
        if (ok && target != null && uri != null) {
            scope.launch {
                uploading = true
                uploadIconAndApplyToSameName(context, db, target.id, target.note, uri)
                uploading = false
            }
        }
        pendingCameraUri = null; iconTargetRecord = null
    }

    DisposableEffect(Unit) {
        val listener = db.collection("records").orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                loading = false
                if (err != null) return@addSnapshotListener
                if (snap != null) {
                    records.clear()
                    snap.documents.forEach { doc ->
                        val r = doc.toObject(Record::class.java)
                        if (r != null) records.add(r.copy(id = doc.id))
                    }
                }
            }
        onDispose { listener.remove() }
    }

    fun openKeyboardForNew(note: String = "", amount: Double? = null) {
        val amtText = amount?.let {
            if (it % 1.0 == 0.0) it.toInt().toString() else it.toString()
        } ?: ""
        if (!showKeyboard) {
            keyboardState = KeyboardState(
                noteText = note,
                amountText = amtText,
                selectAmountOnInput = amount != null
            )
            showKeyboard = true
        } else {
            keyboardState = keyboardState.copy(
                noteText = note,
                amountText = amtText.ifBlank { keyboardState.amountText },
                selectAmountOnInput = amount != null
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

    fun dismissKeyboard() {
        showKeyboard = false
        keyboardState = KeyboardState()
    }

    fun deleteRecordWithUndo(r: Record) {
        deleteJob?.cancel()
        deletingRecordId = r.id
        recentlyDeletedRecord = r
        showUndoToast = true
        deleteJob = scope.launch {
            delay(650)
            try { db.collection("records").document(r.id).delete() } catch (_: Exception) {}
            delay(4350)
            if (recentlyDeletedRecord?.id == r.id) {
                showUndoToast = false
                recentlyDeletedRecord = null
                if (deletingRecordId == r.id) deletingRecordId = null
            }
        }
    }

    fun restoreDeletedRecord() {
        val target = recentlyDeletedRecord ?: return
        deleteJob?.cancel(); deleteJob = null
        showUndoToast = false
        recentlyDeletedRecord = null
        deletingRecordId = null
        
        justAddedId = target.id
        scope.launch { delay(800); if (justAddedId == target.id) justAddedId = null }
        
        try { db.collection("records").document(target.id).set(target) } catch (_: Exception) {}
    }

    fun saveFromKeyboard() {
        val amt = keyboardState.amountText.toDoubleOrNull() ?: return
        if (amt <= 0.0) { Toast.makeText(context, "請輸入金額", Toast.LENGTH_SHORT).show(); return }
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        val note = keyboardState.noteText
        val category = keyboardState.category
        val editId = keyboardState.editingRecordId
        val ts = keyboardState.timestamp
        val monthKey = monthKeyFromTimestamp(ts)
        if (editId != null) {
            db.collection("records").document(editId).update(mapOf(
                "amount" to amt, "note" to note, "category" to category, "timestamp" to ts))
        } else {
            val inherited = records.filter { it.note == note && it.note.isNotBlank() }
                .maxByOrNull { it.timestamp }?.iconUrl ?: ""
            val newId = db.collection("records").document().id
            justAddedId = newId
            val totalThisMonth = records
                .filter { it.category == category && monthKeyFromTimestamp(it.timestamp) == monthKey }
                .sumOf { it.amount } + amt
            afterSaveHint = AfterSaveHint(newId, category, totalThisMonth)
            db.collection("records").document(newId).set(Record(
                amount = amt, note = note, category = category, timestamp = ts, iconUrl = inherited))
            scope.launch { delay(800); if (justAddedId == newId) justAddedId = null }
        }
        showKeyboard = false
        keyboardState = KeyboardState()
        scrollToTopTrigger++
    }

    BackHandler(enabled = showKeyboard) { dismissKeyboard() }
    BackHandler(enabled = filterModeOn && !showKeyboard && currentPage == 0) { filterModeOn = false }
    LaunchedEffect(filterSelectAllTrigger) {
        if (filterSelectAllTrigger > 0 && filterModeOn && currentPage == 0) {
            delay(300)
            try {
                filterSearchFocusRequester.requestFocus()
                filterSearch = filterSearch.copy(selection = TextRange(0, filterSearch.text.length))
            } catch (_: Exception) {}
        }
    }

    Box(Modifier.fillMaxSize().background(SURFACE_BG)) {
        when (currentPage) {
            0 -> LedgerContent(
                loading = loading, filtered = ledgerRecords,
                groupedByDate = groupedByDate,
                topNotes = topNotes, noteIconMap = noteIconMap,
                hasIncome = hasIncome, hasExpense = hasExpense,
                totalIncome = totalIncome, totalExpense = totalExpense,
                filterMode = filterModeOn, filterCategory = filterCategory,
                onFilterCategoryChange = { filterCategory = it },
                filterMonth = filterMonth, onFilterMonthChange = { filterMonth = it },
                availableMonths = availableMonths,
                visibleCategories = visibleCategories,
                expandedId = expandedId, onExpandChange = { expandedId = it },
                isExactNoteFilter = isExactNoteFilter,
                onQuickInputClick = { name ->
                    val amt = recentAmountByNote[name]
                    openKeyboardForNew(name, if (amt != null && amt > 0.0) amt else null)
                },
                onCopyClick = { openKeyboardForCopy(it) },
                onEditClick = { openKeyboardForEdit(it) },
                onDeleteClick = { deleteRecordWithUndo(it) },
                onChangeIconClick = { iconTargetRecord = it; showIconSourceDialog = true },
                onFilterByName = { name ->
                    filterCategory = null; filterMonth = null
                    filterSearch = TextFieldValue(name); filterModeOn = true
                },
                showKeyboard = showKeyboard, keyboardState = keyboardState,
                onKeyboardStateChange = { keyboardState = it },
                onKeyboardDismiss = { dismissKeyboard() },
                onKeyboardConfirm = { saveFromKeyboard() },
                onKeyboardNext = { keyboardState = keyboardState.copy(editingNote = true) },
                onKeyboardPickCategory = { },
                showFuture = showFuture, onShowFutureChange = { showFuture = it },
                allNoteNames = allNoteNames,
                noteCategoryMap = noteCategoryMap,
                scrollToTopTrigger = scrollToTopTrigger,
                nameFlashTrigger = nameFlashTrigger,
                justAddedId = justAddedId,
                deletingRecordId = deletingRecordId,
                afterSaveHint = afterSaveHint,
                afterSaveHintVisible = afterSaveHintVisible,
                animatedQuickInputs = animatedQuickInputs
            )
            1 -> CompareContent(
                records = records, availableMonths = availableMonths,
                selectedMonthA = compareMonthA, onMonthAChange = { setCompareMonthA(it) },
                selectedMonthB = compareMonthB, onMonthBChange = { setCompareMonthB(it) },
                onAddClick = { currentPage = 0; openKeyboardForNew() }
            )
            2 -> CalendarContent(
                records = records,
                onCopyClick = { r -> currentPage = 0; openKeyboardForCopy(r) },
                onEditClick = { r -> currentPage = 0; openKeyboardForEdit(r) },
                onFilterClick = { r ->
                    currentPage = 0
                    filterCategory = null; filterMonth = null
                    filterSearch = TextFieldValue(r.note); filterModeOn = true
                },
                onDeleteClick = { deleteRecordWithUndo(it) },
                onChangeIconClick = { iconTargetRecord = it; showIconSourceDialog = true },
                onAddClick = { currentPage = 0; openKeyboardForNew() },
                deletingRecordId = deletingRecordId
            )
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
            Surface(
                shape = CircleShape,
                color = Color(0xE6FFFFFF), 
                border = BorderStroke(1.dp, Color.White),
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "刪除",
                                tint = Color(0xFFF87171),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "記錄已刪除", 
                            color = TEXT_PRIMARY,
                            fontSize = 15.sp, 
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { restoreDeletedRecord() }
                            .background(BRAND_PRIMARY.copy(alpha = 0.12f))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "復原",
                            color = BRAND_PRIMARY_DARK,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        androidx.compose.animation.AnimatedVisibility(
            visible = if (currentPage != 0) true else !showKeyboard,
            enter = fadeIn(tween(220)),
            exit = fadeOut(tween(200)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = NAV_BOTTOM_PADDING)
        ) {
            FloatingNavBar(
                items = listOf(
                    NavItem("記帳", Icons.Default.Receipt),
                    NavItem("比較", Icons.Default.CompareArrows),
                    NavItem("月曆", Icons.Default.CalendarMonth)),
                selectedIndex = currentPage,
                onIndexChange = { currentPage = it },
                modifier = Modifier)
        }

        KeyboardAndFabLayer(
            showKeyboard = showKeyboard,
            progressProvider = { keyboardAnimProgress.value },
            currentPage = currentPage,
            filterModeOn = filterModeOn,
            filterSearch = filterSearch,
            onFilterSearchChange = { filterSearch = it },
            filterSearchHasFocus = filterSearchHasFocus,
            onFilterSearchFocusChange = { filterSearchHasFocus = it },
            filterSearchFocusRequester = filterSearchFocusRequester,
            onFilterButtonTap = {
                if (currentPage != 0) {
                    currentPage = 0
                    filterModeOn = true
                } else if (!filterModeOn) {
                    filterModeOn = true
                } else {
                    filterSelectAllTrigger++
                }
            },
            onFabTap = {
                if (showKeyboard) {
                    keyboardState = keyboardState.copy(amountText = "", selectAmountOnInput = false)
                } else {
                    if (filterModeOn) filterModeOn = false
                    if (currentPage != 0) currentPage = 0
                    openKeyboardForNew()
                }
            },
            keyboardState = keyboardState,
            onKeyboardStateChange = { keyboardState = it },
            onKeyboardDismiss = { dismissKeyboard() },
            onKeyboardConfirm = { saveFromKeyboard() },
            onKeyboardNext = { keyboardState = keyboardState.copy(editingNote = true) },
            allNoteNames = allNoteNames,
            noteCategoryMap = noteCategoryMap,
            nameFlashTrigger = nameFlashTrigger
        )

        if (uploading) {
            Box(Modifier.fillMaxSize().background(Color(0x80000000)), contentAlignment = Alignment.Center) {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = BRAND_PRIMARY)
                        Spacer(Modifier.height(12.dp))
                        Text("上傳中...", color = TEXT_PRIMARY)
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
                    IconSourceOption(Icons.Default.PhotoLibrary, "從相冊揀") {
                        showIconSourceDialog = false
                        pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    IconSourceOption(Icons.Default.PhotoCamera, "即時拍照") {
                        showIconSourceDialog = false
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
                                    removeIconFromSameName(context, db, target.note)
                                    uploading = false
                                }
                            }
                            iconTargetRecord = null
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showIconSourceDialog = false; iconTargetRecord = null }) { Text("取消") } }
        )
    }

    if (showUrlInputDialog) {
        AlertDialog(
            onDismissRequest = { showUrlInputDialog = false; urlInput = ""; iconTargetRecord = null },
            title = { Text("輸入圖片網址") },
            text = {
                Column {
                    Text("貼上 PNG / JPG / WebP 圖片連結", style = MaterialTheme.typography.bodySmall, color = TEXT_SECONDARY)
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
                        scope.launch { uploading = true; applyUrlToSameName(context, db, target.note, url); uploading = false }
                    } else if (!valid) {
                        Toast.makeText(context, "網址要 http:// 或 https:// 開頭", Toast.LENGTH_SHORT).show()
                    }
                    showUrlInputDialog = false; urlInput = ""; iconTargetRecord = null
                }) { Text("確定") }
            },
            dismissButton = {
                TextButton(onClick = { showUrlInputDialog = false; urlInput = ""; iconTargetRecord = null }) { Text("取消") }
            }
        )
    }
}

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
            .padding(
                start = 16.dp, end = 86.dp,
                bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 12.dp
            )
            .height(56.dp)
            .graphicsLayer {
                val p = progressProvider()
                val fabAlpha = if (showKeyboard) {
                    (1f - ((p - 0.65f) / 0.35f).coerceIn(0f, 1f))
                } else {
                    (1f - (p / 0.35f).coerceIn(0f, 1f))
                }
                alpha = fabAlpha
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
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            color = SURFACE_CARD,
                            shadowElevation = 8.dp
                        ) {
                            Row(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.weight(1f)) {
                                    BasicTextField(
                                        value = filterSearch,
                                        onValueChange = onFilterSearchChange,
                                        singleLine = true,
                                        textStyle = TextStyle(fontSize = 15.sp, color = TEXT_PRIMARY),
                                        cursorBrush = SolidColor(BRAND_PRIMARY),
                                        decorationBox = { inner ->
                                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                                                if (filterSearch.text.isEmpty())
                                                    Text("搜尋名稱或類別…", fontSize = 15.sp, color = TEXT_TERTIARY)
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
                                        modifier = Modifier.size(20.dp).clickable { onFilterSearchChange(TextFieldValue("")) }
                                    )
                                }
                            }
                        }
                    }
                }

                if (filterModeOn && filterSearchHasFocus) {
                    val filterSuggestions = filterNoteSuggestions(filterSearch.text, allNoteNames)
                    if (filterSuggestions.isNotEmpty()) {
                        androidx.compose.ui.window.Popup(
                            popupPositionProvider = AboveAnchorPositionProvider,
                            onDismissRequest = { },
                            properties = PopupProperties(focusable = false)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SURFACE_CARD,
                                shadowElevation = 8.dp,
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
                                            Text(
                                                s, fontSize = 14.sp, color = TEXT_PRIMARY,
                                                maxLines = 1, overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        HorizontalDivider(color = DIVIDER_COLOR.copy(alpha = 0.5f))
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
            color = if (filterModeOn && currentPage == 0) BRAND_PRIMARY else Color.White,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.FilterAlt, "篩選",
                    tint = if (filterModeOn && currentPage == 0) Color.White else BRAND_PRIMARY,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }

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

    AnimatedFabLayer(
        progressProvider = progressProvider,
        onFabTap = onFabTap,
        modifier = Modifier.align(Alignment.BottomEnd)
    )
}

@Composable
private fun AnimatedFabLayer(
    progressProvider: () -> Float,
    onFabTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val p = progressProvider()

    Box(
        modifier = modifier
            .graphicsLayer {
                val currentP = progressProvider()

                val fabX = androidx.compose.ui.unit.lerp(20.dp, 24.dp, currentP).toPx()
                val fabY = androidx.compose.ui.unit.lerp(92.dp, 388.dp, currentP).toPx()

                translationX = -fabX
                translationY = -fabY

                transformOrigin = TransformOrigin(1f, 1f)
                shadowElevation = androidx.compose.ui.unit.lerp(6.dp, 0.dp, (currentP * 5f).coerceIn(0f, 1f)).toPx()
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
        androidx.compose.foundation.Canvas(Modifier.matchParentSize()) {
            val currentP = progressProvider()
            val bgAlpha = (1f - currentP * 3.3f).coerceIn(0f, 1f)
            val fabColor = BRAND_PRIMARY.copy(alpha = bgAlpha)
            drawCircle(color = fabColor)
        }

        val tintColor = androidx.compose.ui.graphics.lerp(Color.White, TEXT_TERTIARY, p)
        Icon(
            Icons.Default.Add,
            contentDescription = "Clear",
            tint = tintColor,
            modifier = Modifier
                .align(Alignment.Center)
                .size(24.dp)
                .graphicsLayer {
                    rotationZ = p * -405f
                }
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
    var shouldRender by remember { mutableStateOf(showKeyboard) }

    LaunchedEffect(showKeyboard) {
        if (showKeyboard) {
            shouldRender = true
        } else {
            kotlinx.coroutines.delay(500)
            if (progressProvider() < 0.001f) {
                shouldRender = false
            }
        }
    }

    if (shouldRender) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, end = 16.dp, start = 16.dp)
                .graphicsLayer {
                    val currentP = progressProvider()
                    alpha = currentP
                    scaleX = 0.17f + 0.83f * currentP
                    scaleY = 0.17f + 0.83f * currentP
                    transformOrigin = TransformOrigin(1f, 1f)
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
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(NAV_HEIGHT / 2),
                clip = false,
                ambientColor = Color(0x33000000),
                spotColor = Color(0x33000000)
            )
            .clip(RoundedCornerShape(NAV_HEIGHT / 2))
            .background(SURFACE_CARD)
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
                    .background(BRAND_PRIMARY_LIGHT)
            )
            Row(Modifier.fillMaxSize()) {
                items.forEachIndexed { idx, item ->
                    val sel = idx == selectedIndex
                    val tint = if (sel) BRAND_PRIMARY_DARK else TEXT_SECONDARY
                    Box(Modifier.width(NAV_TAB_WIDTH).fillMaxHeight(), contentAlignment = Alignment.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(item.icon, item.label, tint = tint, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(item.label, fontSize = 14.sp,
                                fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal, color = tint)
                        }
                    }
                }
            }
        }
    }
}

// ===== LedgerContent =====

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun LedgerContent(
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
    keyboardState: KeyboardState,
    onKeyboardStateChange: (KeyboardState) -> Unit,
    onKeyboardDismiss: () -> Unit,
    onKeyboardConfirm: () -> Unit,
    onKeyboardNext: () -> Unit,
    onKeyboardPickCategory: () -> Unit,
    showFuture: Boolean,
    onShowFutureChange: (Boolean) -> Unit,
    allNoteNames: List<String>,
    noteCategoryMap: Map<String, String>,
    scrollToTopTrigger: Int,
    nameFlashTrigger: Int,
    justAddedId: String?,
    deletingRecordId: String?,
    afterSaveHint: AfterSaveHint?,
    afterSaveHintVisible: Boolean,
    animatedQuickInputs: MutableMap<String, Boolean>,
) {
    val listState = rememberLazyListState()

    var preFilterIndex by remember { mutableIntStateOf(-1) }
    var preFilterOffset by remember { mutableIntStateOf(0) }

   // ★ Fan Menu 全局狀態
    var activeFanRecord by remember { mutableStateOf<Record?>(null) }
    var fadingFanRecordId by remember { mutableStateOf<String?>(null) }
    var fanMenuCenter by remember { mutableStateOf(Offset.Zero) }
    var fanMenuTouch by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    val scope = rememberCoroutineScope() // ★ 加入 scope 用嚟延遲執行動作

    // ★ 移除 by，變成 State 物件，防止每次數值改變都拖垮整個畫面
    val fanMenuProgress = animateFloatAsState(
        targetValue = if (activeFanRecord != null) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
        label = "fanMenuProgress"
    )

    // ★ 獨立處理退出模糊嘅時機
    LaunchedEffect(activeFanRecord) {
        if (activeFanRecord == null) {
            delay(300) // 等收起動畫播完先解除模糊
            fadingFanRecordId = null
        }
    }

    val handleFanStart = { r: Record, offset: Offset ->
        activeFanRecord = r
        fadingFanRecordId = r.id
        fanMenuCenter = offset
        fanMenuTouch = offset
    }
    val handleFanDrag = { dragAmount: Offset ->
        fanMenuTouch += dragAmount
    }
    val handleFanEnd = {
        val hovered = getHoveredActionIndex(fanMenuCenter, fanMenuTouch, with(density) { 40.dp.toPx() })
        val targetRecord = activeFanRecord
        activeFanRecord = null 
        
        if (targetRecord != null && hovered != null) {
            // ★ 放手後延遲 120ms 先執行動作，確保 UI 收起動畫可以流暢起步，唔會被卡死
            scope.launch {
                delay(120)
                when (hovered) {
                    0 -> onCopyClick(targetRecord)
                    1 -> onEditClick(targetRecord)
                    2 -> {
                        if (!filterMode) {
                            preFilterIndex = listState.firstVisibleItemIndex
                            preFilterOffset = listState.firstVisibleItemScrollOffset
                        }
                        onFilterByName(targetRecord.note)
                    }
                    3 -> onDeleteClick(targetRecord)
                }
            }
        }
    }

    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger > 0) {
            try { listState.requestScrollToItem(0) }
            catch (_: Exception) { try { listState.scrollToItem(0) } catch (_: Exception) {} }
        }
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

    val isSingleCategoryFilter by remember(filterMode, filtered) {
        derivedStateOf {
            filterMode && filtered.isNotEmpty() && filtered.map { it.category }.distinct().size == 1
        }
    }
    val singleCategory by remember(isSingleCategoryFilter, filtered) {
        derivedStateOf {
            if (isSingleCategoryFilter) filtered.firstOrNull()?.category else null
        }
    }

    var cachedCategory by remember { mutableStateOf("飲食") }
    LaunchedEffect(singleCategory) {
        if (singleCategory != null) {
            cachedCategory = singleCategory!!
        }
    }

    val listContent: LazyListScope.() -> Unit = {
        if (isExactNoteFilter) {
            itemsIndexed(items = filtered, key = { _, r -> r.id }) { idx, r ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .animateItem(
                            fadeInSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            fadeOutSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            placementSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioLowBouncy)
                        )
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = SURFACE_CARD,
                        shadowElevation = 1.dp,
                        border = BorderStroke(0.5.dp, DIVIDER_COLOR.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            AnimatedRecordItem(
                                animateOnMount = r.id == justAddedId,
                                isDeleting = r.id == deletingRecordId
                            ) {
                                SwipeableRecordItem(
                                    backgroundColor = Color.Transparent, 
                                    record = r,
                                    expandedId = expandedId,
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
                                    hideCategory = true,
                                    onFanMenuStart = { offset -> handleFanStart(r, offset) },
                                    onFanMenuDrag = { dragAmount -> handleFanDrag(dragAmount) },
                                    onFanMenuEnd = { handleFanEnd() },
                                    // ★ 兩個 SwipeableRecordItem 都要改成咁樣
                                                isFanMenuActive = activeFanRecord != null || fadingFanRecordId != null,
                                                isOtherItem = (activeFanRecord != null || fadingFanRecordId != null) && fadingFanRecordId != r.id,
                                                isActiveItem = fadingFanRecordId == r.id
                                )
                            }
                            if (afterSaveHint?.recordId == r.id) {
                                CategoryTotalHint(hint = afterSaveHint, visible = afterSaveHintVisible)
                            }
                        }
                    }
                }
            }
        } else {
            val dataToIterate = filtered.groupBy { dateKeyFromTimestamp(it.timestamp) }.toList()

            dataToIterate.forEach { (dateKey, dayRecords) ->
                val dayIncome = dayRecords.sumOf { if (it.category == INCOME_CATEGORY && it.id != deletingRecordId) it.amount else 0.0 }
                val dayExpense = dayRecords.sumOf { if (it.category != INCOME_CATEGORY && it.id != deletingRecordId) it.amount else 0.0 }

                stickyHeader(key = "header_$dateKey") {
                    FadingStickyHeader {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SURFACE_CARD) 
                        ) {
                            DayHeader(dateKey = dateKey, income = dayIncome, expense = dayExpense)
                        }
                    }
                }

                item(key = "group_$dateKey") {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp)
                            .animateItem(
                                fadeInSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                fadeOutSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                placementSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioLowBouncy)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        color = SURFACE_CARD,
                        shadowElevation = 1.dp,
                        border = BorderStroke(0.5.dp, DIVIDER_COLOR.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            dayRecords.forEachIndexed { idx, r ->
                                key(r.id) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        AnimatedRecordItem(
                                            animateOnMount = r.id == justAddedId,
                                            isDeleting = r.id == deletingRecordId
                                        ) {
                                            SwipeableRecordItem(
                                                backgroundColor = SURFACE_CARD, 
                                                record = r,
                                                expandedId = expandedId,
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
                                                onFanMenuStart = { offset -> handleFanStart(r, offset) },
                                                onFanMenuDrag = { dragAmount -> handleFanDrag(dragAmount) },
                                                onFanMenuEnd = { handleFanEnd() },
                                                // ★ 兩個 SwipeableRecordItem 都要改成咁樣
                                                isFanMenuActive = activeFanRecord != null || fadingFanRecordId != null,
                                                isOtherItem = (activeFanRecord != null || fadingFanRecordId != null) && fadingFanRecordId != r.id,
                                                isActiveItem = fadingFanRecordId == r.id
                                            )
                                        }
                                        
                                        if (afterSaveHint?.recordId == r.id) {
                                            CategoryTotalHint(hint = afterSaveHint, visible = afterSaveHintVisible)
                                        }

                                        if (idx < dayRecords.lastIndex) {
                                            AnimatedVisibility(
                                                visible = deletingRecordId != r.id,
                                                enter = fadeIn(tween(200)),
                                                exit = fadeOut(tween(200))
                                            ) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(start = 68.dp, end = 16.dp),
                                                    color = DIVIDER_COLOR.copy(alpha = 0.4f),
                                                    thickness = 0.5.dp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                item(key = "spacer_$dateKey") {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().background(SURFACE_BG)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .padding(top = 4.dp, bottom = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val showCategoryChip = filterMode && isSingleCategoryFilter && singleCategory != null

                AnimatedVisibility(
                    visible = showCategoryChip,
                    enter = fadeIn(tween(320, easing = FastOutSlowInEasing)) +
                            expandHorizontally(tween(320, easing = FastOutSlowInEasing), expandFrom = Alignment.Start),
                    exit = fadeOut(tween(320, easing = FastOutSlowInEasing)) +
                            shrinkHorizontally(tween(320, easing = FastOutSlowInEasing), shrinkTowards = Alignment.Start)
                ) {
                    CategoryStatChip(cachedCategory)
                }

                Box(modifier = Modifier.weight(1f)) {
                    TopStats(
                        hasIncome = hasIncome,
                        hasExpense = hasExpense,
                        income = totalIncome,
                        expense = totalExpense,
                        isFilterMode = filterMode,
                        filteredCount = if (filterMode) filtered.size else null
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

            AnimatedContent(
                targetState = filterMode,
                transitionSpec = {
                    (expandVertically(tween(400, easing = FastOutSlowInEasing), expandFrom = Alignment.Top) + fadeIn(tween(300))) togetherWith
                    (shrinkVertically(tween(400, easing = FastOutSlowInEasing), shrinkTowards = Alignment.Top) + fadeOut(tween(200))) using SizeTransform(
                        clip = true,
                        sizeAnimationSpec = { _, _ -> tween(400, easing = FastOutSlowInEasing) }
                    )
                },
                label = "bottomArea"
            ) { isFilterMode ->
                if (isFilterMode) {
                    Column(
                        Modifier.padding(horizontal = 16.dp, vertical = 0.dp)
                    ) {
                        Spacer(Modifier.height(4.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 72.dp),
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            userScrollEnabled = false
                        ) {
                            item(key = "__all__") {
                                AnimatedFilterChip(
                                    modifier = Modifier.fillMaxWidth(),
                                    selected = filterCategory == null,
                                    label = "全部",
                                    fillWidth = true,
                                    onClick = { onFilterCategoryChange(null) }
                                )
                            }
                            items(items = visibleCategories, key = { it }) { cat ->
                                AnimatedFilterChip(
                                    modifier = Modifier.animateItem().fillMaxWidth(),
                                    selected = filterCategory == cat,
                                    label = cat,
                                    fillWidth = true,
                                    onClick = { onFilterCategoryChange(if (filterCategory == cat) null else cat) }
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        if (availableMonths.isNotEmpty()) {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                AnimatedFilterChip(
                                    selected = filterMonth == null,
                                    label = "全年",
                                    fillWidth = false,
                                    onClick = { onFilterMonthChange(null) }
                                )
                                availableMonths.forEach { m ->
                                    AnimatedFilterChip(
                                        selected = filterMonth == m,
                                        label = formatMonthLabel(m),
                                        fillWidth = false,
                                        onClick = { onFilterMonthChange(if (filterMonth == m) null else m) }
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                    }
                } else {
                    Column(Modifier.fillMaxWidth()) {
                        if (topNotes.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            QuickInputSection(
                                topNotes = topNotes,
                                noteIconMap = noteIconMap,
                                animatedQuickInputs = animatedQuickInputs,
                                onClick = onQuickInputClick
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }

            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
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
                        if (filtered.isEmpty() && !showKeyboard) {
                            item(key = "__empty__") {
                                Box(
                                    Modifier.fillParentMaxWidth().padding(top = 80.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        if (filterMode) "冇符合篩選條件嘅記錄" else "仲未有記錄,撳右下角 + 新增",
                                        color = TEXT_SECONDARY
                                    )
                                }
                            }
                        } else {
                            listContent()
                        }
                    }
                }
            }
        } // <-- 收埋 Column(Modifier.fillMaxSize().background(SURFACE_BG))

        FanMenuOverlay(
            progressProvider = { fanMenuProgress.value },
            centerProvider = { fanMenuCenter },
            touchProvider = { fanMenuTouch }
        )
    } // <-- 收埋最外層嘅 Box(Modifier.fillMaxSize())
} // <-- 收埋成個 LedgerContent 函數

// (下面緊接嘅應該係 @Composable fun CategoryStatChip(category: String) ... )


@Composable
fun CategoryStatChip(category: String) {
    val style = CATEGORY_STYLES[category]
    Column(
        Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("類別", fontSize = 11.sp, color = TEXT_SECONDARY, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (style != null) {
                Box(
                    Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(style.bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(style.icon, null, tint = style.fgColor, modifier = Modifier.size(14.dp))
                }
                Spacer(Modifier.width(4.dp))
            }
            Text(
                category,
                fontSize = 20.sp,
                color = TEXT_PRIMARY,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FadingStickyHeader(content: @Composable () -> Unit) {
    var rootTop by remember { mutableFloatStateOf(0f) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coords ->
                rootTop = coords.boundsInRoot().top
            }
            .graphicsLayer {
                val fadeDist = 60f
                val a = if (rootTop >= 0f) 1f
                        else (1f + rootTop / fadeDist).coerceIn(0f, 1f)
                this.alpha = a
            }
    ) {
        content()
    }
}

@Composable
fun AnimatedRecordItem(
    animateOnMount: Boolean,
    isDeleting: Boolean = false,
    content: @Composable () -> Unit
) {
    var appeared by remember { mutableStateOf(!animateOnMount) }
    LaunchedEffect(animateOnMount) {
        if (animateOnMount) {
            delay(30)
            appeared = true
        }
    }

    val targetHeight = if (isDeleting) 0.dp else if (animateOnMount && !appeared) 0.dp else 72.dp
    val itemHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "eHeight"
    )

    val targetScale = if (isDeleting) 0.85f else if (animateOnMount && !appeared) 0.85f else 1f
    val entranceScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
        label = "eScale"
    )

    val targetAlpha = if (isDeleting) 0f else if (animateOnMount && !appeared) 0f else 1f
    val entranceAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(if (isDeleting) 200 else 300, easing = FastOutSlowInEasing),
        label = "eAlpha"
    )

    val targetTranslateY = if (animateOnMount && !appeared) 40f else 0f
    val entranceTranslationY by animateFloatAsState(
        targetValue = targetTranslateY,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "eY"
    )

    val particleProgress = remember { Animatable(0f) }
    LaunchedEffect(isDeleting) {
        if (isDeleting) {
            particleProgress.animateTo(1f, tween(550, easing = FastOutSlowInEasing))
        }
    }
    
    val particleCount = 60
    val random = remember { Random(42) }
    val particles = remember {
        List(particleCount) { Triple(random.nextFloat(), random.nextFloat(), (random.nextFloat() - 0.5f) * 280f) }
    }
    val particleColors = remember {
        listOf(BRAND_PRIMARY, BRAND_PRIMARY_DARK, Color(0xFF818CF8), Color(0xFFFBBF24), Color(0xFF34D399))
    }

    Box(
        Modifier
            .fillMaxWidth()
            .height(itemHeight)
            .clipToBounds()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .graphicsLayer {
                    alpha = entranceAlpha
                    scaleX = entranceScale
                    scaleY = entranceScale
                    translationY = entranceTranslationY
                }
        ) {
            content()
        }

        if (isDeleting) {
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
                        color = particleColors[idx % particleColors.size].copy(alpha = pAlpha),
                        radius = pRadius,
                        center = Offset(px, py)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryTotalHint(
    hint: AfterSaveHint,
    visible: Boolean
) {
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
                fontSize = 12.sp,
                color = fgColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun TrianglePointer(
    colIndex: Int,
    cellWidth: Dp,
    color: Color = SURFACE_CARD,
    triangleWidth: Dp = 22.dp,
    triangleHeight: Dp = 11.dp,
) {
    val targetX = cellWidth * colIndex + cellWidth / 2 - triangleWidth / 2
    val animatedX by animateDpAsState(
        targetValue = targetX,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "triangleX"
    )
    Box(Modifier.fillMaxWidth().height(triangleHeight)) {
        Canvas(
            Modifier.offset(x = animatedX).size(triangleWidth, triangleHeight)
        ) {
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
    record: Record, expandedId: String?,
    onExpand: (String?) -> Unit,
    onCopy: () -> Unit, onEdit: () -> Unit,
    onFilter: () -> Unit, onDelete: () -> Unit, onChangeIcon: () -> Unit,
    hideCategory: Boolean = false,
    onFanMenuStart: (Offset) -> Unit = {},
    onFanMenuDrag: (Offset) -> Unit = {},
    onFanMenuEnd: () -> Unit = {},
    isFanMenuActive: Boolean = false,
    isOtherItem: Boolean = false,
    isActiveItem: Boolean = false
) {
    val density = LocalDensity.current
    val bw = 56.dp; val bh = 44.dp; val gap = 6.dp
    val bwPx = with(density) { bw.toPx() }
    val gapPx = with(density) { gap.toPx() }
    val edgePx = with(density) { 8.dp.toPx() }
    val leftTotal = bwPx * 4 + gapPx * 3
    val maxLeft = -(leftTotal + edgePx)
    val maxRight = bwPx + edgePx

    var targetOffset by remember { mutableStateOf(0f) }
    var startOffset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var itemGlobalPosition by remember { mutableStateOf(Offset.Zero) } 

    val blurRadius by animateDpAsState(if (isOtherItem) 8.dp else 0.dp, tween(300), label = "blur")
    val itemAlpha by animateFloatAsState(if (isOtherItem) 0.35f else 1f, tween(300), label = "alpha")
    val itemScale by animateFloatAsState(
        targetValue = when {
            isActiveItem -> 1.03f 
            isOtherItem -> 0.95f  
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow),
        label = "scale"
    )

    LaunchedEffect(expandedId) { if (expandedId != record.id && targetOffset != 0f) targetOffset = 0f }
    val offsetX by animateFloatAsState(targetOffset,
        if (isDragging) snap<Float>() else spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "swipe")

    val leftProgress = if (maxLeft == 0f) 0f else (offsetX / maxLeft).coerceIn(0f, 1f)
    val rightProgress = if (maxRight == 0f) 0f else (offsetX / maxRight).coerceIn(0f, 1f)

    val metaText = remember(record.category, record.timestamp, hideCategory) {
        if (hideCategory) formatRecordTime(record.timestamp)
        else "${record.category}．${formatRecordTime(record.timestamp)}"
    }
    val amtText = remember(record.amount, record.category) { displayAmount(record) }
    val amtColor = remember(record.category) { amountColor(record.category) }
    val headlineText = remember(record.note) { record.note.ifBlank { "(無名稱)" } }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .zIndex(if (isActiveItem) 10f else 0f)
            .graphicsLayer {
                scaleX = itemScale
                scaleY = itemScale
                alpha = itemAlpha
            }
            .blur(blurRadius)
            .background(backgroundColor)
            .onGloballyPositioned { itemGlobalPosition = it.boundsInRoot().topLeft }
            .pointerInput(record.id) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        isDragging = true
                        startOffset = targetOffset
                        onExpand(record.id)
                    },
                    onDragEnd = {
                        isDragging = false
                        val dist = targetOffset - startOffset
                        val hasIcon = record.iconUrl.isNotBlank()
                        var shouldTriggerIconAction = false

                        val newOffset = when {
                            startOffset == maxLeft && dist > 15f -> 0f
                            startOffset == maxRight && dist < -15f -> 0f
                            targetOffset < maxLeft * 0.65f -> maxLeft
                            targetOffset > maxRight * 0.65f -> {
                                if (hasIcon) {
                                    maxRight 
                                } else {
                                    shouldTriggerIconAction = true
                                    0f 
                                }
                            }
                            else -> 0f
                        }
                        
                        targetOffset = newOffset
                        if (newOffset == 0f) onExpand(null)
                        
                        if (shouldTriggerIconAction) {
                            onChangeIcon()
                        }
                    },
                    onDragCancel = { isDragging = false; targetOffset = 0f; onExpand(null) },
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
                        onFanMenuStart(itemGlobalPosition + localOffset)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onFanMenuDrag(dragAmount)
                    },
                    onDragEnd = { onFanMenuEnd() },
                    onDragCancel = { onFanMenuEnd() }
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (expandedId != null) onExpand(null)
            }
    ) {
        Row(
            Modifier
                .matchParentSize()
                .offset { 
                    val overscroll = if (offsetX < maxLeft) (offsetX - maxLeft).roundToInt() else 0
                    IntOffset(overscroll, 0) 
                }
                .padding(end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(gap, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedActionButton(
                icon = Icons.Default.ContentCopy, label = "複製",
                iconTint = Color(0xFF64748B),
                width = bw, height = bh,
                progress = leftProgress, delay = 0f
            ) { targetOffset = 0f; onExpand(null); onCopy() }
            AnimatedActionButton(
                icon = Icons.Default.Edit, label = "編輯",
                iconTint = Color(0xFF3B82F6),
                width = bw, height = bh,
                progress = leftProgress, delay = 0.12f
            ) { targetOffset = 0f; onExpand(null); onEdit() }
            AnimatedActionButton(
                icon = Icons.Default.FilterList, label = "篩選",
                iconTint = Color(0xFF8B5CF6),
                width = bw, height = bh,
                progress = leftProgress, delay = 0.24f
            ) { targetOffset = 0f; onExpand(null); onFilter() }
            AnimatedActionButton(
                icon = Icons.Default.Delete, label = "刪除",
                iconTint = COLOR_EXPENSE,
                width = bw, height = bh,
                progress = leftProgress, delay = 0.36f
            ) { targetOffset = 0f; onExpand(null); onDelete() }
        }

        Row(
            Modifier
                .matchParentSize()
                .offset { 
                    val overscroll = if (offsetX > maxRight) (offsetX - maxRight).roundToInt() else 0
                    IntOffset(overscroll, 0) 
                }
                .padding(start = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(gap, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedActionButton(
                icon = Icons.Default.Image, label = "改圖標",
                iconTint = Color(0xFF10B981),
                width = bw, height = bh,
                progress = rightProgress, delay = 0f
            ) { targetOffset = 0f; onExpand(null); onChangeIcon() }
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
                Text(
                    headlineText,
                    fontSize = NOTE_FONT_SIZE,
                    fontWeight = FontWeight.SemiBold,
                    color = TEXT_PRIMARY,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    metaText,
                    fontSize = META_FONT_SIZE,
                    color = TEXT_TERTIARY,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                amtText,
                color = amtColor,
                fontSize = AMOUNT_FONT_SIZE,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AnimatedActionButton(
    icon: ImageVector, label: String,
    iconTint: Color,
    width: Dp, height: Dp,
    progress: Float, delay: Float,
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
            .graphicsLayer {
                this.alpha = alpha
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(14.dp))
            .background(iconTint.copy(alpha = 0.12f))
            .clickable(enabled = progress > 0.2f) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon, label,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun DayDetailPanel(
    dateKey: String,
    records: List<Record>,
    expandedId: String?,
    onExpandChange: (String?) -> Unit,
    onCopy: (Record) -> Unit,
    onEdit: (Record) -> Unit,
    onFilter: (Record) -> Unit,
    onDelete: (Record) -> Unit,
    onChangeIcon: (Record) -> Unit,
    onDismiss: () -> Unit,
    deletingRecordId: String? = null
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(Modifier.animateContentSize(tween(380, easing = FastOutSlowInEasing))) {
        if (visible) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 6.dp,
                color = SURFACE_CARD
            ) {
                Column(Modifier.fillMaxWidth()) {
                    val income = records.filter { it.category == INCOME_CATEGORY }.sumOf { it.amount }
                    val expense = records.filter { it.category != INCOME_CATEGORY }.sumOf { it.amount }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                formatDateHeader(dateKey),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TEXT_PRIMARY
                            )
                            Spacer(Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (income > 0) {
                                    Text(
                                        "收 $${formatAmountNoDecimal(income)}",
                                        fontSize = 12.sp, color = COLOR_INCOME,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (income > 0 && expense > 0) Spacer(Modifier.width(10.dp))
                                if (expense > 0) {
                                    Text(
                                        "支 $${formatAmountNoDecimal(expense)}",
                                        fontSize = 12.sp, color = COLOR_EXPENSE,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, "關閉", tint = TEXT_SECONDARY)
                        }
                    }
                    HorizontalDivider(color = DIVIDER_COLOR)

                    records.sortedByDescending { it.timestamp }.forEachIndexed { idx, r ->
                        AnimatedRecordItem(
                            animateOnMount = false,
                            isDeleting = r.id == deletingRecordId
                        ) {
                            SwipeableRecordItem(
                                backgroundColor = if (idx % 2 == 0) SURFACE_CARD else ROW_ALT_COLOR,
                                record = r,
                                expandedId = expandedId,
                                onExpand = onExpandChange,
                                onCopy = { onCopy(r) },
                                onEdit = { onEdit(r) },
                                onFilter = { onFilter(r) },
                                onDelete = { onDelete(r) },
                                onChangeIcon = { onChangeIcon(r) }
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LedgerKeyboardPanel(
    state: KeyboardState,
    onStateChange: (KeyboardState) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onNext: () -> Unit,
    onPickCategory: () -> Unit,
    allNoteNames: List<String>,
    noteCategoryMap: Map<String, String>,
    nameFlashTrigger: Int,
    showKeyboard: Boolean = true,
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

    val panelShape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .shadow(
                elevation = 16.dp,
                shape = panelShape,
                clip = false,
                ambientColor = Color(0x33000000),
                spotColor = Color(0x33000000)
            )
            .clip(panelShape)
            .background(SURFACE_CARD)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 14.dp, end = 14.dp, top = 16.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1.4f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
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
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Event, null, tint = BRAND_PRIMARY, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            formatDatePart(state.timestamp),
                            fontSize = 14.sp,
                            color = TEXT_PRIMARY,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
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
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, null, tint = BRAND_PRIMARY, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            formatTimePart(state.timestamp),
                            fontSize = 14.sp,
                            color = TEXT_PRIMARY,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Surface(Modifier.fillMaxWidth().height(76.dp), shape = RoundedCornerShape(16.dp), color = SURFACE_ELEVATED) {
                Box(Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)).padding(start = 22.dp, end = 12.dp),
                    contentAlignment = Alignment.CenterEnd) {
                    val showText = if (state.amountText.isEmpty()) "0" else state.amountText

                    AnimatedAmount(
                        text = showText,
                        color = if (state.amountText.isEmpty()) TEXT_TERTIARY else TEXT_PRIMARY,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 44.dp)
                    )

                    Spacer(Modifier.size(36.dp))
                }
            }

            Spacer(Modifier.height(10.dp))

            val rows = listOf(
                listOf("1", "2", "3"), listOf("4", "5", "6"),
                listOf("7", "8", "9"), listOf(".", "0", "backspace")
            )
            Column(Modifier.fillMaxWidth().height(190.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                rows.forEach { row ->
                    Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        row.forEach { key ->
                            KeyboardKey(key, {
                                when (key) {
                                    "backspace" -> {
                                        val t = state.amountText
                                        onStateChange(state.copy(
                                            amountText = if (t.isEmpty()) t else t.dropLast(1),
                                            selectAmountOnInput = false))
                                    }
                                    "." -> {
                                        val t = state.amountText
                                        if (t.contains(".")) return@KeyboardKey
                                        onStateChange(state.copy(
                                            amountText = if (t.isEmpty()) "0." else "$t.",
                                            selectAmountOnInput = false))
                                    }
                                    else -> {
                                        val base = if (state.selectAmountOnInput) "" else state.amountText
                                        val dotIdx = base.indexOf(".")
                                        val newT = if (dotIdx >= 0) {
                                            if (base.length - dotIdx - 1 >= 2) base else "$base$key"
                                        } else {
                                            if (base.length >= 9) base else "$base$key"
                                        }
                                        onStateChange(state.copy(amountText = newT, selectAmountOnInput = false))
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
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(glowShape)
                        .drawWithContent {
                            drawContent()
                            val a = glowAlpha.value
                            if (a > 0.01f) {
                                val radius = CornerRadius(14.dp.toPx())
                                drawRoundRect(
                                    color = BRAND_PRIMARY_LIGHT.copy(alpha = a * 0.25f),
                                    cornerRadius = radius
                                )
                                drawRoundRect(
                                    color = BRAND_PRIMARY.copy(alpha = a),
                                    cornerRadius = radius,
                                    style = Stroke(width = 2.5.dp.toPx())
                                )
                            }
                        }
                ) {
                    if (state.editingNote) {
                        var tfValue by remember {
                            mutableStateOf(
                                TextFieldValue(
                                    text = state.noteText,
                                    selection = TextRange(0, state.noteText.length)
                                )
                            )
                        }
                        val focusReq = remember { FocusRequester() }
                        LaunchedEffect(state.editingNote) {
                            if (state.editingNote) focusReq.requestFocus()
                        }
                        LaunchedEffect(nameFlashTrigger) {
                            if (state.editingNote) {
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
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { onConfirm() }),
                            modifier = Modifier.fillMaxSize().focusRequester(focusReq)
                        )

                        val suggestions = filterNoteSuggestions(state.noteText, allNoteNames)
                        if (suggestions.isNotEmpty()) {
                            androidx.compose.ui.window.Popup(
                                popupPositionProvider = AboveAnchorPositionProvider,
                                onDismissRequest = { },
                                properties = PopupProperties(focusable = false)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = SURFACE_CARD,
                                    shadowElevation = 8.dp,
                                    modifier = Modifier.width(280.dp).heightIn(max = 220.dp)
                                ) {
                                    Column(Modifier.verticalScroll(rememberScrollState())) {
                                        suggestions.forEach { s ->
                                            Row(
                                                Modifier.fillMaxWidth()
                                                    .clickable { onStateChange(state.copy(noteText = s)) }
                                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    s,
                                                    fontSize = 14.sp,
                                                    color = TEXT_PRIMARY,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            if (s != suggestions.last())
                                                HorizontalDivider(color = DIVIDER_COLOR.copy(alpha = 0.5f))
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
                                containerColor = SURFACE_ELEVATED,
                                contentColor = TEXT_PRIMARY
                            )
                        ) {
                            Text(
                                if (state.noteText.isBlank()) "輸入名稱" else state.noteText,
                                maxLines = 1,
                                fontSize = 15.sp,
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
                            containerColor = SURFACE_ELEVATED,
                            contentColor = TEXT_PRIMARY
                        )
                    ) {
                        if (currentStyle != null) {
                            Icon(currentStyle.icon, null, tint = currentStyle.fgColor, modifier = Modifier.size(18.dp))
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
                        Column(
                            Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CATEGORIES.chunked(2).forEach { pair ->
                                Row(
                                    Modifier.fillMaxWidth(),
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
                                                .clickable {
                                                    onStateChange(state.copy(category = cat))
                                                    showCategoryMenu = false
                                                },
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (s != null) {
                                                Icon(
                                                    s.icon, null,
                                                    tint = if (sel) Color.White else s.fgColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(Modifier.width(5.dp))
                                            }
                                            Text(
                                                cat,
                                                fontSize = 13.sp,
                                                color = if (sel) Color.White else (s?.fgColor ?: TEXT_PRIMARY),
                                                fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                                                maxLines = 1
                                            )
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

            Row(Modifier.fillMaxWidth().height(58.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onDismiss, shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SURFACE_ELEVATED,
                        contentColor = TEXT_PRIMARY
                    )
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
                    colors = ButtonDefaults.buttonColors(containerColor = BRAND_PRIMARY, contentColor = Color.White)
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
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium), label = "ks")
    val bg by animateColorAsState(if (pressed) BRAND_PRIMARY_LIGHT else SURFACE_ELEVATED,
        spring(stiffness = Spring.StiffnessMedium), label = "kb")
    Box(modifier.clip(RoundedCornerShape(14.dp)).background(bg)
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .clickable(interactionSource = src, indication = null) { onClick() },
        contentAlignment = Alignment.Center) {
        if (label == "backspace") Icon(Icons.Default.Backspace, "退格", modifier = Modifier.size(22.dp), tint = TEXT_SECONDARY)
        else Text(label, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = TEXT_PRIMARY)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedFilterChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fillWidth: Boolean = false,
) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.9f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium), label = "cs")

    FilterChip(
        selected = selected, onClick = onClick,
        label = {
            Box(
                modifier = if (fillWidth) Modifier.fillMaxWidth() else Modifier,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    maxLines = 1,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        },
        interactionSource = src,
        shape = RoundedCornerShape(12.dp),
        border = null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = BRAND_PRIMARY,
            selectedLabelColor = Color.White,
            containerColor = SURFACE_ELEVATED,
            labelColor = Color.Black
        ),
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale })
}

@Composable
fun TopStats(
    hasIncome: Boolean,
    hasExpense: Boolean,
    income: Double,
    expense: Double,
    isFilterMode: Boolean = false,
    filteredCount: Int? = null,
) {
    val balance = income - expense
    val showIncome = hasIncome || (!hasIncome && !hasExpense)
    val showExpense = hasExpense

    val spec = tween<Float>(durationMillis = 420, easing = FastOutSlowInEasing)

    val animIncome by animateFloatAsState(income.toFloat(), spec, label = "iAmt")
    val animExpense by animateFloatAsState(expense.toFloat(), spec, label = "eAmt")
    val animBalance by animateFloatAsState(balance.toFloat(), spec, label = "bAmt")
    val animCount by animateFloatAsState((filteredCount ?: 0).toFloat(), spec, label = "cAmt")

    val incomeW by animateFloatAsState(if (showIncome) 1f else 0f, spec, label = "iW")
    val expenseW by animateFloatAsState(if (showExpense) 1f else 0f, spec, label = "eW")

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIncome || incomeW > 0.001f) {
            Box(
                modifier = Modifier.weight(incomeW.coerceAtLeast(0.001f)).clipToBounds(),
                contentAlignment = Alignment.Center
            ) {
                val alphaScale = incomeW.coerceIn(0f, 1f)
                Column(
                    Modifier
                        .padding(horizontal = 4.dp)
                        .graphicsLayer {
                            alpha = alphaScale
                            scaleX = 0.85f + 0.15f * alphaScale
                            scaleY = 0.85f + 0.15f * alphaScale
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(22.dp)) {
                        Icon(Icons.Default.TrendingUp, null, tint = COLOR_INCOME, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("收入", fontSize = STAT_LABEL_FONT_SIZE, color = TEXT_SECONDARY, fontWeight = FontWeight.Medium, maxLines = 1, softWrap = false)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        formatAmountNoDecimal(animIncome.toDouble()),
                        color = COLOR_INCOME,
                        fontSize = STAT_AMOUNT_FONT_SIZE,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        if (showExpense || expenseW > 0.001f) {
            Box(
                modifier = Modifier.weight(expenseW.coerceAtLeast(0.001f)).clipToBounds(),
                contentAlignment = Alignment.Center
            ) {
                val alphaScale = expenseW.coerceIn(0f, 1f)
                Column(
                    Modifier
                        .padding(horizontal = 4.dp)
                        .graphicsLayer {
                            alpha = alphaScale
                            scaleX = 0.85f + 0.15f * alphaScale
                            scaleY = 0.85f + 0.15f * alphaScale
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(22.dp)) {
                        Icon(Icons.Default.TrendingDown, null, tint = COLOR_EXPENSE, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("支出", fontSize = STAT_LABEL_FONT_SIZE, color = TEXT_SECONDARY, fontWeight = FontWeight.Medium, maxLines = 1, softWrap = false)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        formatAmountNoDecimal(animExpense.toDouble()),
                        color = COLOR_EXPENSE,
                        fontSize = STAT_AMOUNT_FONT_SIZE,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        Box(
            modifier = Modifier.weight(1f).clipToBounds(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = isFilterMode,
                transitionSpec = {
                    if (targetState) {
                        (slideInVertically(tween(420, easing = FastOutSlowInEasing)) { it } + fadeIn(tween(300))) togetherWith
                        (slideOutVertically(tween(420, easing = FastOutSlowInEasing)) { -it } + fadeOut(tween(300)))
                    } else {
                        (slideInVertically(tween(420, easing = FastOutSlowInEasing)) { -it } + fadeIn(tween(300))) togetherWith
                        (slideOutVertically(tween(420, easing = FastOutSlowInEasing)) { it } + fadeOut(tween(300)))
                    }
                },
                contentAlignment = Alignment.Center, 
                label = "balanceCountAnim"
            ) { isFilter ->
                if (!isFilter) {
                    val balColor = if (animBalance >= 0) BRAND_PRIMARY else COLOR_EXPENSE
                    Column(Modifier.padding(horizontal = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(22.dp)) {
                            Icon(Icons.Default.AccountBalanceWallet, null, tint = balColor, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("餘額", fontSize = STAT_LABEL_FONT_SIZE, color = TEXT_SECONDARY, fontWeight = FontWeight.Medium, maxLines = 1, softWrap = false)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            formatAmountNoDecimal(animBalance.toDouble()),
                            color = balColor,
                            fontSize = STAT_AMOUNT_FONT_SIZE,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                } else {
                    Column(Modifier.padding(horizontal = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(22.dp)) {
                            Text("筆數", fontSize = STAT_LABEL_FONT_SIZE, color = TEXT_SECONDARY, fontWeight = FontWeight.Medium, maxLines = 1, softWrap = false)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${animCount.roundToInt()}",
                            fontSize = STAT_AMOUNT_FONT_SIZE,
                            color = BRAND_PRIMARY,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(icon: ImageVector, label: String, amountText: String, gradStart: Color, gradEnd: Color) {
    Column(Modifier.padding(horizontal = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = gradStart, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(label, fontSize = STAT_LABEL_FONT_SIZE, color = TEXT_SECONDARY, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(4.dp))
        AnimatedAmount(amountText, gradEnd, STAT_AMOUNT_FONT_SIZE, FontWeight.Bold)
    }
}

@Composable
fun AnimatedAmount(
    text: String,
    color: Color,
    fontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Bold,
    modifier: Modifier = Modifier,
) {
    var prevText by remember { mutableStateOf(text) }

    val isFirstCharPop = (prevText == "0" && text.length == 1 && text != "0") ||
                         (prevText.length == 1 && text == "0" && prevText != "0")

    val isTyping = (text.length == prevText.length + 1 && text.startsWith(prevText)) ||
                   (prevText.length == text.length + 1 && prevText.startsWith(text)) ||
                   (text == prevText)

    val isReplacing = !isFirstCharPop && !isTyping

    LaunchedEffect(text) { prevText = text }

    Row(
        modifier = modifier.animateContentSize(spring(stiffness = Spring.StiffnessMediumLow)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        text.forEachIndexed { idx, c ->
            key(idx) {
                AnimatedContent(
                    targetState = c,
                    transitionSpec = {
                        if (isReplacing) {
                            fadeIn(tween(0)) togetherWith fadeOut(tween(0))
                        } else if (isFirstCharPop) {
                            (scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow), initialScale = 0.5f) + fadeIn(tween(200))) togetherWith
                            (scaleOut(tween(150), targetScale = 0.5f) + fadeOut(tween(150)))
                        } else {
                            (slideInVertically { it } + fadeIn()) togetherWith
                            (slideOutVertically { -it } + fadeOut())
                        }
                    },
                    label = "d_$idx"
                ) { ch ->
                    Text(ch.toString(), color = color, fontSize = fontSize, fontWeight = fontWeight)
                }
            }
        }
    }
}
@Composable
fun DayHeader(dateKey: String, income: Double, expense: Double) {
    val info = remember(dateKey) { parseDateHeader(dateKey) }
    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
    
    val animIncome by animateFloatAsState(
        targetValue = income.toFloat(),
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "dayIncomeAmt"
    )
    val animExpense by animateFloatAsState(
        targetValue = expense.toFloat(),
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "dayExpenseAmt"
    )

    val incomeText = formatAmountNoDecimal(animIncome.toDouble())
    val expenseText = formatAmountNoDecimal(animExpense.toDouble())

    val showIncome = income > 0 || animIncome > 0.5f
    val showExpense = expense > 0 || animExpense > 0.5f

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (info != null) {
                val monthStr = if (info.year == currentYear) "${info.month}月" else "${info.year}年${info.month}月"
                Text(
                    text = "${info.day}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TEXT_PRIMARY
                )
                Spacer(Modifier.width(8.dp))
                
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = monthStr,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TEXT_SECONDARY
                    )
                    Text(
                        text = "週${info.weekdayChar}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (info.isWeekend) WEEKEND_FG else TEXT_TERTIARY
                    )
                }

                if (info.dayTag != null) {
                    Spacer(Modifier.width(12.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (info.dayTag == "今日") BRAND_PRIMARY else SURFACE_ELEVATED)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = info.dayTag,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (info.dayTag == "今日") Color.White else TEXT_SECONDARY
                        )
                    }
                }
            } else {
                Text(
                    formatDateHeader(dateKey),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TEXT_PRIMARY
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showIncome) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, null, tint = COLOR_INCOME, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(2.dp))
                    Text(incomeText, fontSize = 14.sp, color = COLOR_INCOME, fontWeight = FontWeight.ExtraBold)
                }
            }
            if (showIncome && showExpense) Spacer(Modifier.width(10.dp))
            if (showExpense) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingDown, null, tint = TEXT_PRIMARY, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(2.dp))
                    Text(expenseText, fontSize = 14.sp, color = TEXT_PRIMARY, fontWeight = FontWeight.ExtraBold)
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
    var extracted by remember(iconUrl, name) { mutableStateOf<Color?>(null) }

    LaunchedEffect(iconUrl, name) {
        if (iconUrl.isBlank()) { extracted = null; return@LaunchedEffect }
        extracted = withContext(Dispatchers.IO) {
            try {
                val request = ImageRequest.Builder(ctx)
                    .data(iconUrl)
                    .allowHardware(false)
                    .build()
                val result = ctx.imageLoader.execute(request)
                when (val d = result.drawable) {
                    is android.graphics.drawable.BitmapDrawable ->
                        getDominantMutedColor(d.bitmap)

                    is android.graphics.drawable.ColorDrawable -> {
                        val c = d.color
                        if (android.graphics.Color.alpha(c) > 0 &&
                            android.graphics.Color.red(c)   < 240 &&
                            android.graphics.Color.green(c) < 240 &&
                            android.graphics.Color.blue(c)  < 240
                        ) {
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
    }

    val bgColor by animateColorAsState(
        targetValue = extracted ?: SURFACE_ELEVATED,
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "chipBg"
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        onClick = onClick,
        shadowElevation = 1.dp
    ) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconView(iconUrl, name, size = 18.dp)
            Spacer(Modifier.width(6.dp))
            Text(
                name,
                fontSize = 13.sp,
                color = TEXT_PRIMARY,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AnimatedStretchingFlowRow(
    items: List<Pair<String, Int>>,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    itemContent: @Composable (Pair<String, Int>) -> Unit
) {
    val hSpacingPx = with(LocalDensity.current) { horizontalSpacing.roundToPx() }
    val vSpacingPx = with(LocalDensity.current) { verticalSpacing.roundToPx() }

    val offsets = remember { mutableMapOf<String, Animatable<IntOffset, AnimationVector2D>>() }
    val coroutineScope = rememberCoroutineScope()

    androidx.compose.ui.layout.Layout(
        content = { items.forEach { item -> itemContent(item) } },
        modifier = modifier
    ) { measurables, constraints ->
        if (measurables.isEmpty()) return@Layout layout(0, 0) {}
        
        val rows = mutableListOf<MutableList<Pair<androidx.compose.ui.layout.Measurable, Int>>>()
        var currentRow = mutableListOf<Pair<androidx.compose.ui.layout.Measurable, Int>>()
        var currentWidth = 0
        
        val safeMaxWidth = if (constraints.hasBoundedWidth) constraints.maxWidth else 1000
        
        measurables.forEach { measurable ->
            val intrinsicW = measurable.maxIntrinsicWidth(constraints.maxHeight)
            if (currentRow.isEmpty()) {
                currentRow.add(measurable to intrinsicW)
                currentWidth = intrinsicW
            } else {
                if (currentWidth + hSpacingPx + intrinsicW > safeMaxWidth) {
                    rows.add(currentRow)
                    currentRow = mutableListOf(measurable to intrinsicW)
                    currentWidth = intrinsicW
                } else {
                    currentRow.add(measurable to intrinsicW)
                    currentWidth += hSpacingPx + intrinsicW
                }
            }
        }
        if (currentRow.isNotEmpty()) rows.add(currentRow)
        
        val finalPlaceables = mutableListOf<Pair<androidx.compose.ui.layout.Placeable, IntOffset>>()
        var y = 0
        
        rows.forEach { row ->
            val rowIntrinsicWidth = row.sumOf { it.second } + (row.size - 1) * hSpacingPx
            val extraSpace = safeMaxWidth - rowIntrinsicWidth
            val extraPerItem = if (extraSpace > 0) extraSpace / row.size else 0
            var remainder = if (extraSpace > 0) extraSpace % row.size else 0
            
            var x = 0
            var rowMaxHeight = 0
            
            row.forEach { (measurable, intrinsicW) ->
                val extra = extraPerItem + if (remainder > 0) { remainder--; 1 } else 0
                val targetWidth = intrinsicW + extra
                
                val placeable = measurable.measure(
                    constraints.copy(minWidth = targetWidth, maxWidth = targetWidth)
                )
                
                finalPlaceables.add(placeable to IntOffset(x, y))
                
                x += targetWidth + hSpacingPx
                rowMaxHeight = maxOf(rowMaxHeight, placeable.height)
            }
            y += rowMaxHeight + vSpacingPx
        }
        
        val totalHeight = maxOf(0, y - vSpacingPx)
        
        layout(safeMaxWidth, totalHeight) {
            finalPlaceables.forEachIndexed { index, (placeable, targetOffset) ->
                val key = items[index].first
                val animatable = offsets.getOrPut(key) { Animatable(targetOffset, IntOffset.VectorConverter) }
                
                if (animatable.targetValue != targetOffset) {
                    coroutineScope.launch {
                        animatable.animateTo(
                            targetOffset,
                            spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
                        )
                    }
                }
                placeable.place(animatable.value)
            }
        }
    }
}

@Composable
private fun AnimatedQuickChip(
    name: String,
    iconUrl: String,
    index: Int,
    animatedNames: MutableMap<String, Boolean>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alreadyAnimated = animatedNames.containsKey(name)
    val animProgress = remember { Animatable(if (alreadyAnimated) 1f else 0f) }

    LaunchedEffect(name) {
        if (!animatedNames.containsKey(name)) {
            animatedNames[name] = true
            delay((index * 24L).coerceAtMost(260L))
            animProgress.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.85f,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                val p = animProgress.value
                alpha = p
                scaleX = 0.92f + 0.08f * p
                scaleY = 0.92f + 0.08f * p
                transformOrigin = TransformOrigin.Center
            }
    ) {
        QuickInputChip(
            name = name,
            iconUrl = iconUrl,
            modifier = Modifier.fillMaxWidth(), 
            onClick = onClick
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuickInputSection(
    topNotes: List<Pair<String, Int>>,
    noteIconMap: Map<String, String>,
    animatedQuickInputs: MutableMap<String, Boolean>,
    onClick: (String) -> Unit
) {
    if (topNotes.isEmpty()) return
    val items = topNotes.take(42)
    
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val availableWidth = configuration.screenWidthDp - 52
    
    val pages = remember(items, availableWidth) {
        val result = mutableListOf<List<Pair<String, Int>>>()
        var currentPage = mutableListOf<Pair<String, Int>>()
        var currentRowCount = 1
        var currentRowWidth = 0
        
        for (item in items) {
            val name = item.first
            val estimatedItemWidth = 50 + (name.length * 14)
            
            if (currentRowWidth > 0 && currentRowWidth + estimatedItemWidth > availableWidth) {
                if (currentRowCount >= 4) {
                    result.add(currentPage)
                    currentPage = mutableListOf(item)
                    currentRowCount = 1
                    currentRowWidth = estimatedItemWidth + 8
                } else {
                    currentRowCount++
                    currentPage.add(item)
                    currentRowWidth = estimatedItemWidth + 8
                }
            } else {
                currentPage.add(item)
                currentRowWidth += estimatedItemWidth + 8
            }
        }
        if (currentPage.isNotEmpty()) {
            result.add(currentPage)
        }
        result
    }
    
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .shadow(2.dp, RoundedCornerShape(18.dp), clip = false)
            .clip(RoundedCornerShape(18.dp))
            .background(SURFACE_CARD)
            .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top 
        ) { page ->
            AnimatedStretchingFlowRow(
                items = pages[page],
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp)
                    .padding(bottom = if (pages.size > 1) 16.dp else 0.dp),
                horizontalSpacing = 8.dp,
                verticalSpacing = 8.dp
            ) { item ->
                val name = item.first
                val originalIndex = items.indexOfFirst { it.first == name }
                
                AnimatedQuickChip(
                    name = name,
                    iconUrl = noteIconMap[name] ?: "",
                    index = maxOf(0, originalIndex),
                    animatedNames = animatedQuickInputs,
                    onClick = { onClick(name) },
                    modifier = Modifier.fillMaxWidth() 
                )
            }
        }
        
        if (pages.size > 1) {
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(pages.size) { i ->
                    val isSelected = pagerState.currentPage == i
                    Box(
                        Modifier
                            .size(if (isSelected) 6.dp else 4.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) BRAND_PRIMARY else DIVIDER_COLOR)
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarFilterChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed) 0.9f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "cs"
    )

    FilterChip(
        selected = selected, onClick = onClick,
        label = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    maxLines = 1,
                    color = if (selected) Color.White else Color.Black,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        },
        interactionSource = src,
        shape = RoundedCornerShape(12.dp),
        border = null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = BRAND_PRIMARY,
            selectedLabelColor = Color.White,
            containerColor = SURFACE_ELEVATED,
            labelColor = Color.Black
        ),
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale }
    )
}

@Composable
fun AnimatedInfoChip(
    label: String,
    valueText: String,
    modifier: Modifier = Modifier,
    valueFontSize: TextUnit = 16.sp,
    valueColor: Color = TEXT_PRIMARY,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = SURFACE_ELEVATED
    ) {
        Column(
            Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                fontSize = 10.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(2.dp))
            BoxWithConstraints {
                AnimatedAmount(
                    text = valueText,
                    color = valueColor,
                    fontSize = valueFontSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CalendarRow(
    rowIdx: Int,
    firstDayOffset: Int,
    daysInMonth: Int,
    today: Calendar,
    isCurrentMonth: Boolean,
    recordsByDay: Map<Int, List<Record>>,
    displayMode: Int,
    maxAbsNet: Double,
    selectedDay: Int?,
    selectedCol: Int?,
    onCellClick: (day: Int, col: Int) -> Unit,
) {
    val spacing = 6.dp

    var maxItemsInRow = 0
    for (col in 0 until 7) {
        val day = rowIdx * 7 + col - firstDayOffset + 1
        if (day in 1..daysInMonth) {
            val c = recordsByDay[day]?.size ?: 0
            if (c > maxItemsInRow) maxItemsInRow = c
        }
    }

    val targetHeight: Dp = when (displayMode) {
        0 -> 52.dp
        1 -> (34 + maxItemsInRow * 16).dp.coerceAtLeast(56.dp)
        2 -> {
            val iconRows = (maxItemsInRow + 2) / 3
            (30 + iconRows * 21).dp.coerceAtLeast(52.dp)
        }
        else -> 52.dp
    }

    val rowHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "rowHeight_$rowIdx"
    )

    Row(
        Modifier.fillMaxWidth().height(rowHeight),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        for (col in 0 until 7) {
            val cellIdx = rowIdx * 7 + col
            val dayOfMonth = cellIdx - firstDayOffset + 1
            Box(Modifier.weight(1f).fillMaxHeight()) {
                if (dayOfMonth in 1..daysInMonth) {
                    CalendarDayCell(
                        day = dayOfMonth,
                        isToday = isCurrentMonth && dayOfMonth == today.get(Calendar.DAY_OF_MONTH),
                        isSelected = selectedDay == dayOfMonth,
                        records = recordsByDay[dayOfMonth] ?: emptyList(),
                        displayMode = displayMode,
                        maxAbsNet = maxAbsNet,
                        onClick = { onCellClick(dayOfMonth, col) }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDayCell(
    day: Int,
    isToday: Boolean,
    isSelected: Boolean,
    records: List<Record>,
    displayMode: Int,
    maxAbsNet: Double,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    val hasRecords = records.isNotEmpty()

    val income = records.filter { it.category == INCOME_CATEGORY }.sumOf { it.amount }
    val expense = records.filter { it.category != INCOME_CATEGORY }.sumOf { it.amount }
    val net = income - expense
    val absNet = abs(net)

    val bgColor by animateColorAsState(
        targetValue = when {
            isSelected -> BRAND_PRIMARY_LIGHT
            hasRecords && net > 0 -> CELL_BG_POS
            hasRecords && net < 0 -> CELL_BG_NEG
            else -> SURFACE_CARD
        },
        animationSpec = tween(280, easing = FastOutSlowInEasing),
        label = "cellBg"
    )

    val selectionScale by animateFloatAsState(
        targetValue = if (isSelected) 1.06f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "selScale"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 0.5.dp,
        animationSpec = tween(240, easing = FastOutSlowInEasing),
        label = "selBorder"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) BRAND_PRIMARY else DIVIDER_COLOR,
        animationSpec = tween(240),
        label = "selBorderColor"
    )

    val elevation = if (hasRecords || isToday || isSelected) 1.dp else 0.dp

    val barRatio = if (maxAbsNet > 0.0 && hasRecords && absNet > 0.0)
        (absNet / maxAbsNet).toFloat().coerceIn(0f, 1f)
    else 0f
    val barColor = if (net >= 0) CELL_BAR_POS else CELL_BAR_NEG

    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = selectionScale
                scaleY = selectionScale
            }
            .shadow(
                elevation = elevation,
                shape = shape,
                clip = false,
                ambientColor = Color(0x1A000000),
                spotColor = Color(0x1A000000)
            )
            .clip(shape)
            .background(bgColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = shape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        if (displayMode == 0 && barRatio > 0.001f) {
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(barRatio)
                    .background(barColor.copy(alpha = 0.75f))
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
                        Text(
                            text = compactAmount(net),
                            fontSize = 11.sp,
                            lineHeight = 13.sp,
                            color = if (net >= 0) COLOR_INCOME else COLOR_EXPENSE,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                1 -> {
                    records.sortedBy { it.timestamp }.forEach { r ->
                        Row(
                            Modifier.fillMaxWidth().height(16.dp)
                        ) {
                            FadedText(
                                text = r.note.ifBlank { "(無)" },
                                color = TEXT_PRIMARY,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                fadeWidth = 6.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .alignByBaseline()
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (r.category == INCOME_CATEGORY) compactAmount(r.amount)
                                       else compactAmount(-r.amount),
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
                2 -> {
                    val rows = records.chunked(3)
                    rows.forEach { row ->
                        Row(
                            Modifier.fillMaxWidth().height(18.dp).padding(horizontal = 1.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            row.forEach { r ->
                                IconViewAdaptive(
                                    iconUrl = r.iconUrl,
                                    name = r.note,
                                    modifier = Modifier.weight(1f).fillMaxHeight()
                                )
                            }
                            repeat(3 - row.size) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CalendarContent(
    records: List<Record>,
    onCopyClick: (Record) -> Unit,
    onEditClick: (Record) -> Unit,
    onFilterClick: (Record) -> Unit,
    onDeleteClick: (Record) -> Unit,
    onChangeIconClick: (Record) -> Unit,
    onAddClick: () -> Unit,
    deletingRecordId: String? = null
) {
    val today = remember { Calendar.getInstance() }
    val todayKey = remember { monthKeyFromTimestamp(System.currentTimeMillis()) }

    var currentMonthKey by remember { mutableStateOf(todayKey) }
    var monthDirection by remember { mutableIntStateOf(1) }
    var filterCategory by remember { mutableStateOf<String?>(null) }
    var displayMode by remember { mutableIntStateOf(0) }

    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var selectedRowIdx by remember { mutableStateOf<Int?>(null) }
    var selectedColIdx by remember { mutableStateOf<Int?>(null) }
    var detailExpandedId by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

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

    val monthAllRecords by remember(currentMonthKey) {
        derivedStateOf { records.filter { monthKeyFromTimestamp(it.timestamp) == currentMonthKey } }
    }

    val visibleCategories by remember {
        derivedStateOf {
            val hasIncome = monthAllRecords.any { it.category == INCOME_CATEGORY }
            val expenseCats = monthAllRecords
                .filter { it.category != INCOME_CATEGORY }
                .groupBy { it.category }
                .mapValues { (_, list) -> list.sumOf { it.amount } }
                .toList()
                .sortedByDescending { it.second }
                .map { it.first }
            (if (hasIncome) listOf(INCOME_CATEGORY) else emptyList()) + expenseCats
        }
    }

    val monthRecords by remember(filterCategory) {
        derivedStateOf {
            if (filterCategory == null) monthAllRecords
            else monthAllRecords.filter { it.category == filterCategory }
        }
    }

    val recordsByDay by remember {
        derivedStateOf {
            monthRecords.groupBy { r ->
                Calendar.getInstance().apply { timeInMillis = r.timestamp }
                    .get(Calendar.DAY_OF_MONTH)
            }
        }
    }

    val maxAbsNet by remember {
        derivedStateOf {
            recordsByDay.values.maxOfOrNull { dayRecords ->
                val inc = dayRecords.filter { it.category == INCOME_CATEGORY }.sumOf { it.amount }
                val exp = dayRecords.filter { it.category != INCOME_CATEGORY }.sumOf { it.amount }
                abs(inc - exp)
            } ?: 0.0
        }
    }

    val monthIncome = monthRecords.filter { it.category == INCOME_CATEGORY }.sumOf { it.amount }
    val monthExpense = monthRecords.filter { it.category != INCOME_CATEGORY }.sumOf { it.amount }
    val totalNet = monthIncome - monthExpense

    val itemCount = monthRecords.size
    val avgPerItem = if (itemCount > 0) {
        (monthIncome + monthExpense) / itemCount
    } else 0.0
    val isCurrentMonth = currentMonthKey == todayKey
    val daysElapsed = if (isCurrentMonth) today.get(Calendar.DAY_OF_MONTH) else daysInMonth
    val avgPerDay = if (daysElapsed > 0) monthExpense / daysElapsed else 0.0

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

                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = {
                            monthDirection = -1
                            currentMonthKey = shiftMonthKey(currentMonthKey, -1)
                        },
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = SURFACE_ELEVATED
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.ChevronLeft, "上個月",
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
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
                                currentMonthKey = todayKey
                            }
                        }
                    ) {
                        Text(
                            "${year}年",
                            fontSize = 11.sp,
                            lineHeight = 12.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
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
                            Text(
                                "${m}月",
                                fontSize = 28.sp,
                                lineHeight = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = TEXT_PRIMARY
                            )
                        }
                    }

                    Spacer(Modifier.width(10.dp))
                    Surface(
                        onClick = {
                            monthDirection = 1
                            currentMonthKey = shiftMonthKey(currentMonthKey, 1)
                        },
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = SURFACE_ELEVATED
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.ChevronRight, "下個月",
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    SegmentedModeControl(
                        displayMode = displayMode,
                        onModeChange = { displayMode = it }
                    )
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            userScrollEnabled = false
                        ) {
                            item(key = "__all__") {
                                CalendarFilterChip(
                                    modifier = Modifier.fillMaxWidth(),
                                    selected = filterCategory == null,
                                    label = "全部",
                                    onClick = { filterCategory = null }
                                )
                            }
                            items(
                                items = visibleCategories,
                                key = { it }
                            ) { cat ->
                                CalendarFilterChip(
                                    modifier = Modifier.animateItem().fillMaxWidth(),
                                    selected = filterCategory == cat,
                                    label = cat,
                                    onClick = {
                                        filterCategory = if (filterCategory == cat) null else cat
                                    }
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
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
                    listOf("一", "二", "三", "四", "五", "六", "日").forEach { w ->
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text(
                                w, fontSize = 12.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }

            Box(Modifier.weight(1f).fillMaxWidth()) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 10.dp)
                        .padding(top = 10.dp)
                        .padding(bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 12.dp + 56.dp + 12.dp)
                ) {
                    for (rowIdx in 0 until totalRows) {
                        val isThisRowSelected = selectedRowIdx == rowIdx
                        val isHidden = selectedDay != null && !isThisRowSelected
                        val distance = if (selectedRowIdx != null) abs(rowIdx - selectedRowIdx!!) else 0
                        val exitDelay = (distance * 28).coerceAtMost(220)
                        val enterDelay = (distance * 22).coerceAtMost(180)

                        AnimatedVisibility(
                            visible = !isHidden,
                            enter = fadeIn(tween(280, delayMillis = enterDelay, easing = FastOutSlowInEasing)) +
                                    expandVertically(
                                        tween(320, delayMillis = enterDelay, easing = FastOutSlowInEasing),
                                        expandFrom = Alignment.Top
                                    ),
                            exit = fadeOut(tween(240, delayMillis = exitDelay, easing = FastOutSlowInEasing)) +
                                    shrinkVertically(
                                        tween(300, delayMillis = exitDelay, easing = FastOutSlowInEasing),
                                        shrinkTowards = Alignment.Top
                                    )
                        ) {
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
                                            detailExpandedId = null
                                        }
                                    }
                                )

                                if (isThisRowSelected && selectedDay != null && selectedColIdx != null) {
                                    BoxWithConstraints(Modifier.fillMaxWidth()) {
                                        val cellWidth = maxWidth / 7
                                        TrianglePointer(
                                            colIndex = selectedColIdx!!,
                                            cellWidth = cellWidth,
                                            color = SURFACE_CARD
                                        )
                                    }
                                    val dateKey = String.format(
                                        Locale.US, "%04d-%02d-%02d",
                                        year, month, selectedDay
                                    )
                                    DayDetailPanel(
                                        dateKey = dateKey,
                                        records = recordsByDay[selectedDay] ?: emptyList(),
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
                                        deletingRecordId = deletingRecordId
                                    )
                                    Spacer(Modifier.height(8.dp))
                                } else {
                                    Spacer(Modifier.height(6.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedModeControl(
    displayMode: Int,
    onModeChange: (Int) -> Unit
) {
    val modes = remember {
        listOf(
            Triple("金額", Icons.Default.AttachMoney, 0),
            Triple("項目", Icons.Default.List, 1),
            Triple("圖標", Icons.Default.Apps, 2)
        )
    }
    val itemWidth = 76.dp
    val itemHeight = 28.dp
    val density = LocalDensity.current
    val itemWidthPx = with(density) { itemWidth.toPx() }

    var dragAccum by remember { mutableFloatStateOf(0f) }
    val latestMode by rememberUpdatedState(displayMode)
    val latestOnChange by rememberUpdatedState(onModeChange)

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SURFACE_ELEVATED
    ) {
        Box(
            Modifier
                .padding(3.dp)
                .pointerInput(Unit) {
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
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "segIndicator"
            )

            Box(
                Modifier
                    .offset(x = indicatorOffset)
                    .width(itemWidth)
                    .height(itemHeight)
                    .shadow(2.dp, RoundedCornerShape(9.dp), clip = false)
                    .clip(RoundedCornerShape(9.dp))
                    .background(SURFACE_CARD)
            )

            Row {
                modes.forEach { (label, icon, mode) ->
                    val selected = displayMode == mode
                    Row(
                        modifier = Modifier
                            .width(itemWidth)
                            .height(itemHeight)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onModeChange(mode) }
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            icon, label,
                            tint = if (selected) BRAND_PRIMARY else Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            label,
                            fontSize = 12.sp,
                            color = if (selected) TEXT_PRIMARY else Color.Black,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FadedText(
    text: String,
    color: Color,
    fontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Normal,
    modifier: Modifier = Modifier,
    fadeWidth: Dp = 6.dp
) {
    val density = LocalDensity.current
    val fadePx = with(density) { fadeWidth.toPx() }
    var textWidthPx by remember { mutableFloatStateOf(0f) }

    val brush = remember(textWidthPx, fadePx, color) {
        if (textWidthPx > 1f) {
            val effectiveFade = fadePx.coerceAtMost(textWidthPx * 0.45f)
            val stop = ((textWidthPx - effectiveFade) / textWidthPx).coerceIn(0.5f, 1f)
            Brush.horizontalGradient(
                colorStops = arrayOf(
                    0f to color,
                    stop to color,
                    1f to color.copy(alpha = 0f)
                ),
                startX = 0f,
                endX = textWidthPx
            )
        } else {
            SolidColor(color)
        }
    }

    Text(
        text = text,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Clip,
        style = TextStyle(
            brush = brush,
            fontSize = fontSize,
            fontWeight = fontWeight,
            lineHeight = 11.sp
        ),
        onTextLayout = { result ->
            if (result.lineCount > 0) {
                textWidthPx = result.getLineRight(0) - result.getLineLeft(0)
            }
        },
        modifier = modifier
    )
}

@Composable
fun MiniHistogram(
    values: List<Double>,
    color: Color,
    modifier: Modifier = Modifier,
) {
    if (values.isEmpty()) {
        Spacer(modifier)
        return
    }
    val maxValue = values.maxOrNull() ?: 0.0
    if (maxValue <= 0.0) {
        Spacer(modifier)
        return
    }
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
            drawRect(
                color = color,
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth, h)
            )
        }
    }
}

@Composable
fun CompareValueCell(
    amount: Double,
    isIncome: Boolean,
    pct: Double?,
    modifier: Modifier = Modifier
) {
    val formattedAmt = formatAmountNoDecimal(abs(amount))
    val amtColor = if (isIncome) COLOR_INCOME else COLOR_EXPENSE

    val pctText: String? = when {
        pct == null -> null
        pct > 999.0 -> ">1K%"
        else -> String.format(Locale.US, "%.0f%%", pct)
    }

    val amtDisplay = if (isIncome) formattedAmt else "($formattedAmt)"

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = amtDisplay,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = amtColor,
            textAlign = TextAlign.End,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(4.dp))
        Box(
            modifier = Modifier.width(38.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (pctText != null) {
                Text(
                    text = pctText,
                    fontSize = 11.sp,
                    color = TEXT_TERTIARY,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CompareContent(
    records: List<Record>,
    availableMonths: List<String>,
    selectedMonthA: String,
    onMonthAChange: (String) -> Unit,
    selectedMonthB: String,
    onMonthBChange: (String) -> Unit,
    onAddClick: () -> Unit,
) {
    val currentMonthKey = remember { monthKeyFromTimestamp(System.currentTimeMillis()) }

    val sortedCategories = remember(records, selectedMonthB) {
        val expenses = EXPENSE_CATEGORIES.sortedByDescending { cat ->
            sumByCategoryAndMonth(records, cat, selectedMonthB)
        }
        listOf(INCOME_CATEGORY) + expenses
    }

    val displayCategories = remember(sortedCategories, records, selectedMonthA, selectedMonthB) {
        sortedCategories.filter { cat ->
            val amtA = sumByCategoryAndMonth(records, cat, selectedMonthA)
            val amtB = sumByCategoryAndMonth(records, cat, selectedMonthB)
            amtA != 0.0 || amtB != 0.0
        }
    }

    val incomeA = remember(records, selectedMonthA) { sumByCategoryAndMonth(records, INCOME_CATEGORY, selectedMonthA) }
    val incomeB = remember(records, selectedMonthB) { sumByCategoryAndMonth(records, INCOME_CATEGORY, selectedMonthB) }

    val totalExpenseA = remember(records, selectedMonthA) {
        EXPENSE_CATEGORIES.sumOf { sumByCategoryAndMonth(records, it, selectedMonthA) }
    }
    val totalExpenseB = remember(records, selectedMonthB) {
        EXPENSE_CATEGORIES.sumOf { sumByCategoryAndMonth(records, it, selectedMonthB) }
    }
    val balanceA = incomeA - totalExpenseA
    val balanceB = incomeB - totalExpenseB

    val nowCal = remember { Calendar.getInstance() }
    val currentYear = remember { nowCal.get(Calendar.YEAR) }
    val currentMonthNum = remember { nowCal.get(Calendar.MONTH) + 1 }

    val monthlyKeysThisYear = remember(currentYear, currentMonthNum) {
        (1..currentMonthNum).map { m ->
            String.format(Locale.US, "%04d-%02d", currentYear, m)
        }
    }

    val histogramData = remember(records, monthlyKeysThisYear) {
        EXPENSE_CATEGORIES.associateWith { cat ->
            monthlyKeysThisYear.map { key ->
                sumByCategoryAndMonth(records, cat, key)
            }
        }
    }

    Box(Modifier.fillMaxSize().background(SURFACE_BG)) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 80.dp)
        ) {
            Text(
                "月份比較",
                fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TEXT_PRIMARY,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = SURFACE_CARD,
                shadowElevation = 4.dp
            ) {
                Column(Modifier.fillMaxSize()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(SURFACE_ELEVATED)
                            .padding(horizontal = 10.dp, vertical = 10.dp),
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
                                months = availableMonths.ifEmpty { listOf(currentMonthKey) },
                                onChange = { newA ->
                                    var newB = selectedMonthB
                                    if (newA == newB) newB = shiftMonthKey(newA, 1)
                                    onMonthAChange(newA)
                                    if (newB != selectedMonthB) onMonthBChange(newB)
                                }
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
                                months = availableMonths.ifEmpty { listOf(currentMonthKey) },
                                onChange = { newB ->
                                    var newA = selectedMonthA
                                    if (newB == newA) newA = shiftMonthKey(newB, -1)
                                    onMonthBChange(newB)
                                    if (newA != selectedMonthA) onMonthAChange(newA)
                                }
                            )
                        }
                    }

                    HorizontalDivider(color = DIVIDER_COLOR)

                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        if (displayCategories.isEmpty()) {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("此兩月份皆無紀錄", color = TEXT_SECONDARY, fontSize = 15.sp)
                            }
                        } else {
                            BoxWithConstraints(Modifier.fillMaxSize()) {
                                val rowCount = displayCategories.size
                                val calculatedHeight = maxHeight / rowCount
                                val rowHeight = calculatedHeight.coerceAtLeast(44.dp)

                                LazyColumn(
                                    Modifier.fillMaxSize(),
                                    userScrollEnabled = false
                                ) {
                                    itemsIndexed(
                                        items = displayCategories,
                                        key = { _, cat -> cat }
                                    ) { idx, cat ->
                                        val amtA = sumByCategoryAndMonth(records, cat, selectedMonthA)
                                        val amtB = sumByCategoryAndMonth(records, cat, selectedMonthB)
                                        val style = CATEGORY_STYLES[cat]
                                        val isIncome = cat == INCOME_CATEGORY

                                        val pctA = if (incomeA > 0.0 && !isIncome && amtA > 0.0) (amtA / incomeA * 100.0) else null
                                        val pctB = if (incomeB > 0.0 && !isIncome && amtB > 0.0) (amtB / incomeB * 100.0) else null

                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                                .height(rowHeight)
                                                .animateItem(
                                                    fadeInSpec = tween(220, easing = FastOutSlowInEasing),
                                                    fadeOutSpec = tween(180, easing = FastOutSlowInEasing),
                                                    placementSpec = spring(
                                                        stiffness = Spring.StiffnessMediumLow,
                                                        dampingRatio = Spring.DampingRatioLowBouncy
                                                    )
                                                )
                                                .background(if (idx % 2 == 0) SURFACE_CARD else ROW_ALT_COLOR)
                                                .padding(horizontal = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(Modifier.weight(1.0f), verticalAlignment = Alignment.CenterVertically) {
                                                if (style != null) {
                                                    Box(
                                                        Modifier
                                                            .size(26.dp)
                                                            .clip(RoundedCornerShape(7.dp))
                                                            .background(style.bgColor),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(style.icon, null, tint = style.fgColor, modifier = Modifier.size(15.dp))
                                                    }
                                                    Spacer(Modifier.width(7.dp))
                                                }
                                                Text(
                                                    cat, fontSize = 15.sp, color = TEXT_PRIMARY,
                                                    fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Medium
                                                )
                                            }

                                            Box(
                                                Modifier
                                                    .weight(0.65f)
                                                    .fillMaxHeight()
                                                    .padding(start = 12.dp, end = 16.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (!isIncome) {
                                                    val values = histogramData[cat].orEmpty()
                                                    val histHeight = (rowHeight - 16.dp).coerceAtLeast(20.dp)
                                                    MiniHistogram(
                                                        values = values,
                                                        color = style?.fgColor ?: TEXT_TERTIARY,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(histHeight)
                                                    )
                                                }
                                            }

                                            CompareValueCell(
                                                amount = amtA,
                                                isIncome = isIncome,
                                                pct = pctA,
                                                modifier = Modifier.weight(1.55f)
                                            )

                                            Spacer(Modifier.width(6.dp))

                                            CompareValueCell(
                                                amount = amtB,
                                                isIncome = isIncome,
                                                pct = pctB,
                                                modifier = Modifier.weight(1.55f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = DIVIDER_COLOR, thickness = 1.5.dp)

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(SURFACE_ELEVATED)
                            .padding(horizontal = 10.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "餘額",
                            Modifier.weight(1.0f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = BRAND_PRIMARY_DARK
                        )
                        Spacer(Modifier.weight(0.65f))

                        CompareValueCell(
                            amount = balanceA,
                            isIncome = balanceA >= 0,
                            pct = null,
                            modifier = Modifier.weight(1.55f)
                        )

                        Spacer(Modifier.width(6.dp))

                        CompareValueCell(
                            amount = balanceB,
                            isIncome = balanceB >= 0,
                            pct = null,
                            modifier = Modifier.weight(1.55f)
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            shape = RoundedCornerShape(20.dp),
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

fun sumByCategoryAndMonth(records: List<Record>, category: String, month: String): Double =
    records.filter { it.category == category && monthKeyFromTimestamp(it.timestamp) == month }.sumOf { it.amount }

@Composable
fun MonthDropdown(value: String, months: List<String>, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier) {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(10.dp),
            color = SURFACE_CARD
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    formatMonthLabel(value),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BRAND_PRIMARY
                )
                Spacer(Modifier.width(3.dp))
                Icon(
                    Icons.Default.ArrowDropDown, "選擇月份",
                    tint = BRAND_PRIMARY,
                    modifier = Modifier.size(18.dp)
                )
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

fun createTempImageUri(context: Context): Uri? {
    return try {
        val name = "camera_${System.currentTimeMillis()}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Ledger")
            }
        }
        context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )
    } catch (e: Exception) {
        null
    }
}

suspend fun compressImage(context: Context, uri: Uri, maxSize: Int = ICON_SIZE): ByteArray? =
    withContext(Dispatchers.IO) {
        try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            val w = bounds.outWidth; val h = bounds.outHeight
            if (w <= 0 || h <= 0) return@withContext null
            var s = 1; val md = minOf(w, h)
            while (md / (s * 2) >= maxSize) s *= 2
            val opts = BitmapFactory.Options().apply { inSampleSize = s }
            val src = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
                ?: return@withContext null
            val scaled = scaleCropCenter(src, maxSize)
            if (scaled !== src) src.recycle()
            val baos = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            scaled.recycle(); baos.toByteArray()
        } catch (e: Exception) { null }
    }

fun scaleCropCenter(src: Bitmap, size: Int): Bitmap {
    val w = src.width; val h = src.height; val md = minOf(w, h)
    val x = (w - md) / 2; val y = (h - md) / 2
    val cropped = if (x == 0 && y == 0 && w == md && h == md) src else Bitmap.createBitmap(src, x, y, md, md)
    val scaled = if (cropped.width == size && cropped.height == size) cropped
                 else Bitmap.createScaledBitmap(cropped, size, size, true)
    if (cropped !== src && cropped !== scaled) cropped.recycle()
    return scaled
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
            o.write(("Content-Disposition: form-data; name=\"file\"; filename=\"icon.jpg\"$lineEnd").toByteArray())
            o.write("Content-Type: image/jpeg$lineEnd$lineEnd".toByteArray())
            o.write(bytes); o.write("$lineEnd".toByteArray())
            o.write("--$boundary--$lineEnd".toByteArray()); o.flush()
        }
        val code = conn.responseCode
        val text = if (code in 200..299) conn.inputStream.bufferedReader().use { it.readText() }
                   else conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
        if (code in 200..299) JSONObject(text).optString("secure_url").takeIf { it.isNotBlank() } else null
    } catch (e: Exception) { null }
}

@Composable
fun IconView(iconUrl: String, name: String, size: Dp = 40.dp) {
    if (iconUrl.isBlank()) {
        val ch = remember(name) { name.trim().take(1).ifBlank { "?" } }
        val bgColor = remember(name) { avatarColor(name) }
        Box(Modifier.size(size).clip(CircleShape).background(bgColor), contentAlignment = Alignment.Center) {
            Text(
                text = ch,
                color = Color.White,
                fontSize = (size.value * 0.42f).sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both
                    )
                )
            )
        }
    } else {
        AsyncImage(model = iconUrl, contentDescription = null, contentScale = ContentScale.Crop,
            modifier = Modifier.size(size).clip(CircleShape))
    }
}

@Composable
fun IconViewAdaptive(
    iconUrl: String,
    name: String,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier.clip(CircleShape)) {
        val sizeDp = maxWidth
        if (iconUrl.isBlank()) {
            val ch = name.trim().take(1).ifBlank { "?" }
            Box(
                Modifier.fillMaxSize().background(avatarColor(name)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ch,
                    color = Color.White,
                    fontSize = (sizeDp.value * 0.5f).sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.Both
                        )
                    )
                )
            }
        } else {
            AsyncImage(
                model = iconUrl, contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
