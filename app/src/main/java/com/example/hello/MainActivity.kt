package com.example.hello

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
val COLOR_INCOME = Color(0xFF059669)
val COLOR_EXPENSE = Color(0xFFDC2626)

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
val ROW_ALT_COLOR = Color(0xFFF1F5F9)
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
fun formatMonthLabel(ym: String): String {
    val p = ym.split("-"); if (p.size != 2) return ym
    val y = p[0].toIntOrNull() ?: return ym; val m = p[1].toIntOrNull() ?: return ym
    return if (y == Calendar.getInstance().get(Calendar.YEAR)) "${m}月" else "${y % 100}年${m}月"
}
fun formatDateHeader(dateKey: String): String {
    val p = dateKey.split("-"); if (p.size != 3) return dateKey
    val y = p[0].toIntOrNull() ?: return dateKey
    val mo = p[1].toIntOrNull() ?: return dateKey
    val d = p[2].toIntOrNull() ?: return dateKey
    val cal = Calendar.getInstance().apply {
        set(y, mo - 1, d); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val wk = arrayOf("週日","週一","週二","週三","週四","週五","週六")[cal.get(Calendar.DAY_OF_WEEK)-1]
    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val diff = ((todayStart - cal.timeInMillis) / 86_400_000L).toInt()
    val dp = when {
        diff == -1 -> "明日"
        diff == -2 -> "後日"
        diff < -2 -> "${mo}月${d}日"
        diff == 0 -> "今日"
        diff == 1 -> "琴日"
        diff == 2 -> "前日"
        else -> "${mo}月${d}日"
    }
    return "$dp $wk"
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
    val records = remember { mutableStateListOf<Record>() }
    var loading by remember { mutableStateOf(true) }
    var expandedId by remember { mutableStateOf<String?>(null) }
    var currentPage by remember { mutableIntStateOf(0) }
    var filterModeOn by remember { mutableStateOf(false) }
    var filterCategory by remember { mutableStateOf<String?>(null) }
    var filterMonth by remember { mutableStateOf<String?>(null) }
    var filterSearch by remember { mutableStateOf(TextFieldValue("")) }
    var filterSelectAllTrigger by remember { mutableIntStateOf(0) }
    val filterSearchFocusRequester = remember { FocusRequester() }
    var iconTargetRecord by remember { mutableStateOf<Record?>(null) }
    var showIconSourceDialog by remember { mutableStateOf(false) }
    var showUrlInputDialog by remember { mutableStateOf(false) }
    var urlInput by remember { mutableStateOf("") }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }
    var showKeyboard by remember { mutableStateOf(false) }
    var keyboardState by remember { mutableStateOf(KeyboardState()) }
    var showFuture by remember { mutableStateOf(false) }
    var scrollToTopTrigger by remember { mutableIntStateOf(0) }
    var nameFlashTrigger by remember { mutableIntStateOf(0) }
    var justAddedId by remember { mutableStateOf<String?>(null) }
    var afterSaveHint by remember { mutableStateOf<AfterSaveHint?>(null) }

    val allNoteNames by remember {
        derivedStateOf { records.map { it.note }.filter { it.isNotBlank() }.distinct() }
    }
    val noteCategoryMap by remember {
        derivedStateOf {
            records.filter { it.note.isNotBlank() }
                .groupBy { it.note }
                .mapValues { (_, list) ->
                    list.maxByOrNull { it.timestamp }?.category ?: "飲食"
                }
        }
    }

    val filtered by remember {
        derivedStateOf {
            records.toList().filter { r ->
                val catOk = filterCategory == null || r.category == filterCategory
                val monthOk = filterMonth == null || monthKeyFromTimestamp(r.timestamp) == filterMonth
                val searchOk = filterSearch.text.isBlank() ||
                    r.note.contains(filterSearch.text, true) ||
                    r.category.contains(filterSearch.text, true)
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
    val hasIncome by remember { derivedStateOf { ledgerRecords.any { it.category == INCOME_CATEGORY } } }
    val hasExpense by remember { derivedStateOf { ledgerRecords.any { it.category != INCOME_CATEGORY } } }
    val totalIncome by remember { derivedStateOf { ledgerRecords.filter { it.category == INCOME_CATEGORY }.sumOf { it.amount } } }
    val totalExpense by remember { derivedStateOf { ledgerRecords.filter { it.category != INCOME_CATEGORY }.sumOf { it.amount } } }
    val groupedByDate by remember { derivedStateOf { ledgerRecords.groupBy { dateKeyFromTimestamp(it.timestamp) }.toList() } }
    val topNotes by remember {
        derivedStateOf {
            ledgerRecords.filter { it.note.isNotBlank() }.groupBy { it.note }
                .map { (n, l) -> n to l.size }.sortedByDescending { it.second }.take(20)
        }
    }
    val noteIconMap by remember {
        derivedStateOf {
            ledgerRecords.filter { it.note.isNotBlank() && it.iconUrl.isNotBlank() }
                .groupBy { it.note }.mapValues { (_, l) -> l.maxByOrNull { it.timestamp }?.iconUrl ?: "" }
        }
    }
    val availableMonths by remember {
        derivedStateOf {
            records.map { monthKeyFromTimestamp(it.timestamp) }.distinct().sorted()
        }
    }
    val visibleCategories by remember {
        derivedStateOf {
            val base = if (filterMonth == null) records.toList()
                       else records.filter { monthKeyFromTimestamp(it.timestamp) == filterMonth }
            val hasIncome = base.any { it.category == INCOME_CATEGORY }
            val expenseCats = base
                .filter { it.category != INCOME_CATEGORY }
                .groupBy { it.category }
                .mapValues { (_, list) -> list.sumOf { it.amount } }
                .toList()
                .sortedByDescending { it.second }
                .map { it.first }
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
                        doc.toObject(Record::class.java)?.let { it.id = doc.id; records.add(it) }
                    }
                }
            }
        onDispose { listener.remove() }
    }

    fun openKeyboardForNew(note: String = "") {
        if (!showKeyboard) {
            keyboardState = KeyboardState(noteText = note)
            showKeyboard = true
        } else {
            keyboardState = keyboardState.copy(noteText = note)
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
    fun saveFromKeyboard() {
        val amt = keyboardState.amountText.toDoubleOrNull() ?: return
        if (amt <= 0.0) { Toast.makeText(context, "請輸入金額", Toast.LENGTH_SHORT).show(); return }
        val note = keyboardState.noteText
        val category = keyboardState.category
        val editId = keyboardState.editingRecordId
        val ts = keyboardState.timestamp
        val monthKey = monthKeyFromTimestamp(ts)
        if (editId != null) {
            db.collection("records").document(editId).update(mapOf(
                "amount" to amt, "note" to note, "category" to category,
                "timestamp" to ts))
        } else {
            val inherited = records.filter { it.note == note && it.note.isNotBlank() }
                .maxByOrNull { it.timestamp }?.iconUrl ?: ""
            val newRef = db.collection("records").add(Record(
                amount = amt, note = note, category = category,
                timestamp = ts, iconUrl = inherited))
            newRef.addOnSuccessListener { docRef ->
                val newId = docRef.id
                justAddedId = newId
                val totalThisMonth = records
                    .filter { it.category == category && monthKeyFromTimestamp(it.timestamp) == monthKey }
                    .sumOf { it.amount } + amt
                afterSaveHint = AfterSaveHint(newId, category, totalThisMonth)
                scope.launch {
                    delay(800)
                    if (justAddedId == newId) justAddedId = null
                }
            }
        }
        showKeyboard = false
        keyboardState = KeyboardState()
        scrollToTopTrigger++
    }

    BackHandler(enabled = showKeyboard) {
        showKeyboard = false; keyboardState = KeyboardState()
    }
    BackHandler(enabled = filterModeOn && !showKeyboard) {
        filterModeOn = false
    }
    LaunchedEffect(filterSelectAllTrigger) {
        if (filterSelectAllTrigger > 0 && filterModeOn) {
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
                onQuickInputClick = { openKeyboardForNew(it) },
                onCopyClick = { openKeyboardForCopy(it) },
                onEditClick = { openKeyboardForEdit(it) },
                onDeleteClick = { db.collection("records").document(it.id).delete() },
                onChangeIconClick = { iconTargetRecord = it; showIconSourceDialog = true },
                onFilterByName = { name ->
                    filterCategory = null; filterMonth = null
                    filterSearch = TextFieldValue(name); filterModeOn = true
                },
                showKeyboard = showKeyboard, keyboardState = keyboardState,
                onKeyboardStateChange = { keyboardState = it },
                onKeyboardDismiss = { showKeyboard = false; keyboardState = KeyboardState() },
                onKeyboardConfirm = { saveFromKeyboard() },
                onKeyboardNext = { keyboardState = keyboardState.copy(editingNote = true) },
                onKeyboardPickCategory = { },
                showFuture = showFuture, onShowFutureChange = { showFuture = it },
                allNoteNames = allNoteNames,
                noteCategoryMap = noteCategoryMap,
                scrollToTopTrigger = scrollToTopTrigger,
                nameFlashTrigger = nameFlashTrigger,
                justAddedId = justAddedId,
                afterSaveHint = afterSaveHint,
                onAfterSaveHintDismiss = { afterSaveHint = null }
            )
            1 -> CompareContent(
                records = records,
                availableMonths = availableMonths,
                onAddClick = {
                    currentPage = 0
                    openKeyboardForNew()
                }
            )
        }

        androidx.compose.animation.AnimatedVisibility(
            visible = !showKeyboard,
            enter = fadeIn(tween(220)),
            exit = fadeOut(tween(200)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = NAV_BOTTOM_PADDING)
        ) {
            FloatingNavBar(
                items = listOf(
                    NavItem("記帳", Icons.Default.Receipt),
                    NavItem("比較", Icons.Default.CompareArrows)),
                selectedIndex = currentPage,
                onIndexChange = { currentPage = it },
                modifier = Modifier)
        }

        if (!showKeyboard && currentPage == 0) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp, end = 20.dp,
                        bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(Modifier.weight(1f)) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = filterModeOn,
                        enter = expandHorizontally(
                            animationSpec = tween(300, easing = FastOutSlowInEasing),
                            expandFrom = Alignment.End
                        ) + fadeIn(tween(200)),
                        exit = shrinkHorizontally(
                            animationSpec = tween(260, easing = FastOutSlowInEasing),
                            shrinkTowards = Alignment.End
                        ) + fadeOut(tween(180))
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            color = SURFACE_CARD,
                            shadowElevation = 8.dp
                        ) {
                            Row(
                                Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(Modifier.weight(1f)) {
                                    BasicTextField(
                                        value = filterSearch,
                                        onValueChange = { filterSearch = it },
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
                                        modifier = Modifier.fillMaxWidth().focusRequester(filterSearchFocusRequester)
                                    )
                                }
                                if (filterSearch.text.isNotBlank()) {
                                    Spacer(Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.Close, "清除", tint = TEXT_SECONDARY,
                                        modifier = Modifier.size(20.dp).clickable { filterSearch = TextFieldValue("") }
                                    )
                                }
                            }
                        }
                    }

                    if (filterModeOn) {
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
                                                    .clickable { filterSearch = TextFieldValue(s) }
                                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(s, fontSize = 14.sp, color = TEXT_PRIMARY,
                                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            }
                                            HorizontalDivider(color = DIVIDER_COLOR.copy(alpha = 0.5f))
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
                        .pointerInput(filterModeOn) {
                            detectTapGestures(
                                onTap = { filterModeOn = !filterModeOn },
                                onDoubleTap = {
                                    if (!filterModeOn) {
                                        filterModeOn = true
                                        filterSelectAllTrigger++
                                    }
                                }
                            )
                        },
                    shape = CircleShape,
                    color = if (filterModeOn) BRAND_PRIMARY else Color.White,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.FilterAlt, "篩選",
                            tint = if (filterModeOn) Color.White else BRAND_PRIMARY,
                            modifier = Modifier.size(22.dp))
                    }
                }

                FloatingActionButton(
                    onClick = {
                        if (filterModeOn) filterModeOn = false
                        openKeyboardForNew()
                    },
                    containerColor = BRAND_PRIMARY,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, "新增")
                }
            }
        }

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
                        val uri = createTempImageUri(context); pendingCameraUri = uri
                        takePictureLauncher.launch(uri)
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
fun FloatingNavBar(items: List<NavItem>, selectedIndex: Int, onIndexChange: (Int) -> Unit, modifier: Modifier = Modifier) {
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
        if (isDragging) snap<Float>() else spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "bubble")

    Surface(
        modifier = modifier.width(NAV_TAB_WIDTH * items.size).height(NAV_HEIGHT),
        shape = RoundedCornerShape(NAV_HEIGHT / 2),
        color = SURFACE_CARD, shadowElevation = 12.dp
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
            Box(Modifier.offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .width(NAV_TAB_WIDTH).fillMaxHeight().padding(6.dp)
                .clip(RoundedCornerShape((NAV_HEIGHT - 12.dp) / 2))
                .background(BRAND_PRIMARY_LIGHT))
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LedgerContent(
    loading: Boolean, filtered: List<Record>,
    groupedByDate: List<Pair<String, List<Record>>>,
    topNotes: List<Pair<String, Int>>, noteIconMap: Map<String, String>,
    hasIncome: Boolean, hasExpense: Boolean,
    totalIncome: Double, totalExpense: Double,
    filterMode: Boolean, filterCategory: String?,
    onFilterCategoryChange: (String?) -> Unit,
    filterMonth: String?, onFilterMonthChange: (String?) -> Unit,
    availableMonths: List<String>,
    visibleCategories: List<String>,
    expandedId: String?, onExpandChange: (String?) -> Unit,
    onQuickInputClick: (String) -> Unit,
    onCopyClick: (Record) -> Unit, onEditClick: (Record) -> Unit,
    onDeleteClick: (Record) -> Unit, onChangeIconClick: (Record) -> Unit,
    onFilterByName: (String) -> Unit,
    showKeyboard: Boolean, keyboardState: KeyboardState,
    onKeyboardStateChange: (KeyboardState) -> Unit,
    onKeyboardDismiss: () -> Unit, onKeyboardConfirm: () -> Unit,
    onKeyboardNext: () -> Unit, onKeyboardPickCategory: () -> Unit,
    showFuture: Boolean, onShowFutureChange: (Boolean) -> Unit,
    allNoteNames: List<String>,
    noteCategoryMap: Map<String, String>,
    scrollToTopTrigger: Int,
    nameFlashTrigger: Int,
    justAddedId: String?,
    afterSaveHint: AfterSaveHint?,
    onAfterSaveHintDismiss: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger > 0) {
            try { listState.requestScrollToItem(0) }
            catch (_: Exception) { try { listState.scrollToItem(0) } catch (_: Exception) {} }
        }
    }
    LaunchedEffect(showFuture) {
        try { listState.requestScrollToItem(0) }
        catch (_: Exception) { try { listState.scrollToItem(0) } catch (_: Exception) {} }
    }

    Column(Modifier.fillMaxSize().background(SURFACE_BG)) {
        AnimatedVisibility(
            visible = filterMode,
            enter = fadeIn(tween(FILTER_ANIM_MS)) + expandVertically(tween(FILTER_ANIM_MS), expandFrom = Alignment.Top),
            exit = fadeOut(tween(FILTER_ANIM_MS)) + shrinkVertically(tween(FILTER_ANIM_MS), shrinkTowards = Alignment.Top)
        ) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 72.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    userScrollEnabled = false
                ) {
                    item(key = "__all__") {
                        AnimatedFilterChip(
                            selected = filterCategory == null,
                            label = "全部",
                            onClick = { onFilterCategoryChange(null) }
                        )
                    }
                    items(
                        items = visibleCategories,
                        key = { it }
                    ) { cat ->
                        AnimatedFilterChip(
                            modifier = Modifier.animateItem(),
                            selected = filterCategory == cat,
                            label = cat,
                            onClick = {
                                onFilterCategoryChange(if (filterCategory == cat) null else cat)
                            }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (availableMonths.isNotEmpty()) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        AnimatedFilterChip(
                            selected = filterMonth == null,
                            label = "全年",
                            onClick = { onFilterMonthChange(null) }
                        )
                        availableMonths.forEach { m ->
                            AnimatedFilterChip(
                                selected = filterMonth == m,
                                label = formatMonthLabel(m),
                                onClick = { onFilterMonthChange(if (filterMonth == m) null else m) }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }

        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BRAND_PRIMARY)
            }
            filtered.isEmpty() && !showKeyboard -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(if (filterMode) "冇符合篩選條件嘅記錄" else "仲未有記錄,撳右下角 + 新增", color = TEXT_SECONDARY)
            }
            else -> {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.weight(if (filterMode) 2f else 1f)) {
                        TopStats(hasIncome, hasExpense, totalIncome, totalExpense)
                    }
                    if (filterMode) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "筆數",
                                fontSize = 11.sp,
                                color = TEXT_SECONDARY,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "${filtered.size}",
                                fontSize = 22.sp,
                                color = BRAND_PRIMARY,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = { onShowFutureChange(!showFuture) }) {
                        Icon(
                            if (showFuture) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            "顯示未來項目",
                            tint = if (showFuture) BRAND_PRIMARY else TEXT_TERTIARY
                        )
                    }
                }

                AnimatedVisibility(
                    visible = !filterMode && topNotes.isNotEmpty(),
                    enter = fadeIn(tween(FILTER_ANIM_MS)) + expandVertically(tween(FILTER_ANIM_MS), expandFrom = Alignment.Top),
                    exit = fadeOut(tween(FILTER_ANIM_MS)) + shrinkVertically(tween(FILTER_ANIM_MS), shrinkTowards = Alignment.Top)
                ) {
                    Column {
                        QuickInputSection(topNotes, noteIconMap, onQuickInputClick)
                        Spacer(Modifier.height(4.dp))
                    }
                }

                Box(Modifier.weight(1f).fillMaxWidth()) {
                                        LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 12.dp, end = 12.dp, top = 4.dp,
                            bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 80.dp
                        )
                    ) {
                        groupedByDate.forEach { (dateKey, dayRecords) ->
                            val dayIncome = dayRecords.sumOf {
                                if (it.category == INCOME_CATEGORY) it.amount else 0.0
                            }
                            val dayExpense = dayRecords.sumOf {
                                if (it.category != INCOME_CATEGORY) it.amount else 0.0
                            }

                            item(
                                key = "group_$dateKey",
                                contentType = "day_group"
                            ) {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 10.dp,
                                            end = 10.dp,
                                            top = 8.dp,
                                            bottom = 8.dp
                                        )
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = RoundedCornerShape(18.dp),
                                            clip = false,
                                            ambientColor = Color(0x40000000),
                                            spotColor = Color(0x40000000)
                                        )
                                        .shadow(
                                            elevation = 2.dp,
                                            shape = RoundedCornerShape(18.dp),
                                            clip = false,
                                            ambientColor = Color(0x30000000),
                                            spotColor = Color(0x30000000)
                                        )
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(SURFACE_CARD)
                                ) {
                                    DayHeader(
                                        dateKey = dateKey,
                                        income = dayIncome,
                                        expense = dayExpense,
                                        itemCount = if (filterMode) dayRecords.size else 0
                                    )

                                    dayRecords.forEachIndexed { idx, r ->
                                        AnimatedRecordItem(
                                            animateOnMount = r.id == justAddedId
                                        ) {
                                            SwipeableRecordItem(
                                                backgroundColor = if (idx % 2 == 0)
                                                    SURFACE_CARD else ROW_ALT_COLOR,
                                                record = r,
                                                expandedId = expandedId,
                                                onExpand = onExpandChange,
                                                onCopy = { onCopyClick(r) },
                                                onEdit = { onEditClick(r) },
                                                onFilter = { onFilterByName(r.note) },
                                                onDelete = { onDeleteClick(r) },
                                                onChangeIcon = { onChangeIconClick(r) }
                                            )
                                        }

                                        if (afterSaveHint?.recordId == r.id) {
                                            CategoryTotalHint(
                                                hint = afterSaveHint,
                                                onDismiss = onAfterSaveHintDismiss
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    androidx.compose.animation.AnimatedVisibility(
                        visible = showKeyboard,
                        enter = slideInVertically(initialOffsetY = { it },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
                        ) + fadeIn(tween(150)),
                        exit = slideOutVertically(targetOffsetY = { it },
                            animationSpec = tween(320, easing = FastOutSlowInEasing)
                        ) + fadeOut(tween(220)),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        LedgerKeyboardPanel(
                            state = keyboardState, onStateChange = onKeyboardStateChange,
                            onDismiss = onKeyboardDismiss, onConfirm = onKeyboardConfirm,
                            onNext = onKeyboardNext, onPickCategory = onKeyboardPickCategory,
                            allNoteNames = allNoteNames,
                            noteCategoryMap = noteCategoryMap,
                            nameFlashTrigger = nameFlashTrigger,
                            modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedRecordItem(
    animateOnMount: Boolean,
    content: @Composable () -> Unit
) {
    var appeared by remember { mutableStateOf(!animateOnMount) }
    LaunchedEffect(Unit) {
        if (animateOnMount) {
            delay(16)
            appeared = true
        }
    }
    val slideY by animateFloatAsState(
        targetValue = if (appeared) 0f else 60f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "arSlide"
    )
    val scale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.75f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "arScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(280, easing = FastOutSlowInEasing),
        label = "arAlpha"
    )
    Box(
        Modifier.graphicsLayer {
            translationY = slideY
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
        }
    ) {
        content()
    }
}

@Composable
fun CategoryTotalHint(
    hint: AfterSaveHint,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
        delay(5000)
        visible = false
        delay(320)
        onDismiss()
    }
    val isIncome = hint.category == INCOME_CATEGORY
    val bgColor = if (isIncome) Color(0xFFD1FAE5) else Color(0xFFFEE2E2)
    val fgColor = if (isIncome) Color(0xFF065F46) else Color(0xFF991B1B)
    val icon = if (isIncome) Icons.Default.TrendingUp else Icons.Default.TrendingDown

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(250)) + expandVertically(tween(250), expandFrom = Alignment.Top),
        exit = fadeOut(tween(250)) + shrinkVertically(tween(250), shrinkTowards = Alignment.Top)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(bgColor)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = fgColor, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (isIncome)
                    "今個月「${hint.category}」共收入 ${formatAmount(hint.monthTotal)}"
                else
                    "今個月「${hint.category}」共支出 ${formatAmount(hint.monthTotal)}",
                fontSize = 13.sp,
                color = fgColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CompareContent(
    records: List<Record>,
    availableMonths: List<String>,
    onAddClick: () -> Unit,
) {
    var monthA by remember { mutableStateOf<String?>(null) }
    var monthB by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(availableMonths) {
        if (monthA == null || monthA !in availableMonths) monthA = availableMonths.getOrNull(0)
        if (monthB == null || monthB !in availableMonths) monthB = availableMonths.getOrNull(1) ?: availableMonths.getOrNull(0)
    }
    val sortedCategories = remember(records, monthB) {
        val expenses = EXPENSE_CATEGORIES.sortedByDescending { cat ->
            if (monthB != null) sumByCategoryAndMonth(records, cat, monthB!!) else 0.0
        }
        listOf(INCOME_CATEGORY) + expenses
    }

    Box(Modifier.fillMaxSize().background(SURFACE_BG)) {
        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("月份比較", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TEXT_PRIMARY,
                modifier = Modifier.padding(vertical = 8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MonthDropdown(monthA, availableMonths, { monthA = it }, Modifier.weight(1f))
                MonthDropdown(monthB, availableMonths, { monthB = it }, Modifier.weight(1f))
            }
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("類別", Modifier.weight(1.4f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TEXT_SECONDARY)
                Text(monthA?.let { formatMonthLabel(it) } ?: "-", Modifier.weight(1f),
                    fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TEXT_SECONDARY, textAlign = TextAlign.End)
                Text(monthB?.let { formatMonthLabel(it) } ?: "-", Modifier.weight(1f),
                    fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TEXT_SECONDARY, textAlign = TextAlign.End)
            }
            HorizontalDivider(color = DIVIDER_COLOR)

            LazyColumn(Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 20.dp)) {
                items(sortedCategories) { cat ->
                    val amtA = if (monthA != null) sumByCategoryAndMonth(records, cat, monthA!!) else 0.0
                    val amtB = if (monthB != null) sumByCategoryAndMonth(records, cat, monthB!!) else 0.0
                    val style = CATEGORY_STYLES[cat]
                    val isIncome = cat == INCOME_CATEGORY

                    Row(Modifier.fillMaxWidth().padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Row(Modifier.weight(1.4f), verticalAlignment = Alignment.CenterVertically) {
                            if (style != null) {
                                Box(Modifier.size(26.dp).clip(RoundedCornerShape(8.dp)).background(style.bgColor),
                                    contentAlignment = Alignment.Center) {
                                    Icon(style.icon, null, tint = style.fgColor, modifier = Modifier.size(15.dp))
                                }
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(cat, fontSize = 15.sp, color = TEXT_PRIMARY,
                                fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Medium)
                        }
                        Text(formatAmount(amtA), Modifier.weight(1f), fontSize = 15.sp,
                            color = if (isIncome) COLOR_INCOME else TEXT_PRIMARY,
                            fontWeight = if (isIncome) FontWeight.SemiBold else FontWeight.Normal,
                            textAlign = TextAlign.End)
                        Text(formatAmount(amtB), Modifier.weight(1f), fontSize = 15.sp,
                            color = if (isIncome) COLOR_INCOME else TEXT_PRIMARY,
                            fontWeight = if (isIncome) FontWeight.SemiBold else FontWeight.Normal,
                            textAlign = TextAlign.End)
                    }
                    HorizontalDivider(color = DIVIDER_COLOR)
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
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
fun MonthDropdown(value: String?, months: List<String>, onChange: (String?) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier) {
        Button(
            onClick = { expanded = true },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = SURFACE_ELEVATED,
                contentColor = TEXT_PRIMARY
            )
        ) {
            Text(value?.let { formatMonthLabel(it) } ?: "選擇月份",
                fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(14.dp), containerColor = SURFACE_CARD) {
            months.forEach { m ->
                DropdownMenuItem(text = { Text(formatMonthLabel(m)) },
                    onClick = { onChange(m); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickInputSection(
    topNotes: List<Pair<String, Int>>,
    noteIconMap: Map<String, String>,
    onClick: (String) -> Unit
) {
    if (topNotes.isEmpty()) return
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val availWidth = maxWidth - 32.dp
        val perRow = max(1, (availWidth / 90.dp).toInt()).coerceIn(2, 5)
        val perPage = perRow * 4
        val pageCount = max(1, (topNotes.size + perPage - 1) / perPage)
        val pagerState = rememberPagerState { pageCount }
        val linesToShow = if (pageCount == 1) {
            ((topNotes.size + perRow - 1) / perRow).coerceAtLeast(1)
        } else 4
        val rowHeight = 40.dp
        val vGap = 4.dp
        val pagerHeight = rowHeight * linesToShow + vGap * (linesToShow - 1).coerceAtLeast(0)

        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .shadow(2.dp, RoundedCornerShape(18.dp))
                .background(SURFACE_CARD, RoundedCornerShape(18.dp))
                .padding(vertical = 10.dp)
        ) {
            Column(Modifier.fillMaxWidth()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth().height(pagerHeight)
                ) { page ->
                    val start = page * perPage
                    val end = minOf(start + perPage, topNotes.size)
                    if (start >= end) return@HorizontalPager

                    Column(
                        Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(vGap)
                    ) {
                        for (line in 0 until linesToShow) {
                            val lineStart = start + line * perRow
                            val lineEnd = minOf(lineStart + perRow, end)
                            if (lineStart >= lineEnd) break

                            Row(
                                Modifier.fillMaxWidth().height(rowHeight),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                for (i in lineStart until lineEnd) {
                                    val name = topNotes[i].first
                                    Surface(
                                        modifier = Modifier.weight(1f).fillMaxHeight(),
                                        shape = RoundedCornerShape(11.dp),
                                        color = SURFACE_ELEVATED,
                                        onClick = { onClick(name) }
                                    ) {
                                        Row(
                                            Modifier.padding(horizontal = 6.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            IconView(noteIconMap[name] ?: "", name, size = 18.dp)
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                name,
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                color = TEXT_PRIMARY,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (pageCount > 1) {
                    Row(
                        Modifier.fillMaxWidth().padding(top = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(pageCount) { i ->
                            val active = i == pagerState.currentPage
                            Box(
                                Modifier.padding(horizontal = 3.dp)
                                    .size(if (active) 6.dp else 4.dp)
                                    .clip(CircleShape)
                                    .background(if (active) BRAND_PRIMARY else DIVIDER_COLOR)
                            )
                        }
                    }
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
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current

    // 名稱變更 → 自動用返上次同名項目嘅類別
    LaunchedEffect(state.noteText) {
        if (state.noteText.isNotBlank()) {
            val lastCat = noteCategoryMap[state.noteText]
            if (lastCat != null && lastCat != state.category) {
                onStateChange(state.copy(category = lastCat))
            }
        }
    }

    Surface(
        modifier = modifier.imePadding(),
        color = SURFACE_CARD,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 16.dp
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 12.dp)
        ) {
            // ===== 日期 / 時間 =====
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

            // ===== 金額顯示 =====
            Surface(Modifier.fillMaxWidth().height(76.dp), shape = RoundedCornerShape(16.dp), color = SURFACE_ELEVATED) {
                Box(Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)).padding(horizontal = 22.dp),
                    contentAlignment = Alignment.CenterEnd) {
                    val showText = if (state.amountText.isEmpty()) "0" else state.amountText
                    Text(showText, fontSize = 38.sp, fontWeight = FontWeight.Bold,
                        color = if (state.amountText.isEmpty()) TEXT_TERTIARY else TEXT_PRIMARY,
                        textAlign = TextAlign.End, maxLines = 1)
                }
            }

            Spacer(Modifier.height(10.dp))

            // ===== 數字鍵盤 =====
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

            // ===== 名稱輸入 + 類別 =====
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
                        .then(
                            if (glowAlpha.value > 0.01f) {
                                Modifier
                                    .border(
                                        width = 2.5.dp,
                                        color = BRAND_PRIMARY.copy(alpha = glowAlpha.value),
                                        shape = glowShape
                                    )
                                    .background(BRAND_PRIMARY_LIGHT.copy(alpha = glowAlpha.value * 0.25f))
                            } else Modifier
                        )
                ) {
                    if (state.editingNote) {
                        var tfValue by remember {
                            mutableStateOf(TextFieldValue(text = state.noteText,
                                selection = TextRange(0, state.noteText.length)))
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
                            onValueChange = { nv -> tfValue = nv; onStateChange(state.copy(noteText = nv.text)) },
                            placeholder = { Text("名稱") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = SURFACE_ELEVATED,
                                unfocusedContainerColor = SURFACE_ELEVATED,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent),
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
                                                Text(s, fontSize = 14.sp, color = TEXT_PRIMARY,
                                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                            Text(if (state.noteText.isBlank()) "輸入名稱" else state.noteText,
                                maxLines = 1, fontSize = 15.sp,
                                color = if (state.noteText.isBlank()) TEXT_TERTIARY else TEXT_PRIMARY)
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
                                            Modifier.weight(1f).height(44.dp).clip(chipShape)
                                                .background(
                                                    if (sel) (s?.fgColor ?: BRAND_PRIMARY)
                                                    else (s?.bgColor ?: SURFACE_ELEVATED)
                                                )
                                                .clickable { onStateChange(state.copy(category = cat)); showCategoryMenu = false },
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

            // ===== 底部按鈕 =====
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
) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.9f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium), label = "cs")
    FilterChip(
        selected = selected, onClick = onClick,
        label = { Text(label) }, interactionSource = src,
        shape = RoundedCornerShape(12.dp),
        border = null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = BRAND_PRIMARY, selectedLabelColor = Color.White,
            containerColor = SURFACE_ELEVATED, labelColor = TEXT_SECONDARY),
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale })
}

@Composable
fun TopStats(hasIncome: Boolean, hasExpense: Boolean, income: Double, expense: Double) {
    BoxWithConstraints(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)) {
        val density = LocalDensity.current
        val fullPx = with(density) { maxWidth.toPx() }
        val halfPx = fullPx / 2f
        val slotWidth = maxWidth / 2
        val incomeX by animateFloatAsState(
            if (hasIncome && hasExpense) 0f else if (hasIncome) halfPx / 2f else -halfPx,
            spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow), label = "ix")
        val expenseX by animateFloatAsState(
            if (hasIncome && hasExpense) halfPx else if (hasExpense) halfPx / 2f else fullPx,
            spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow), label = "ex")
        val iAlpha by animateFloatAsState(if (hasIncome) 1f else 0f, tween(220), label = "ia")
        val eAlpha by animateFloatAsState(if (hasExpense) 1f else 0f, tween(220), label = "ea")

        Box(Modifier.width(slotWidth).offset { IntOffset(incomeX.roundToInt(), 0) }.graphicsLayer { alpha = iAlpha },
            contentAlignment = Alignment.Center) {
            StatCard(Icons.Default.TrendingUp, "收入", formatAmount(income), Color(0xFF10B981), Color(0xFF059669))
        }
        Box(Modifier.width(slotWidth).offset { IntOffset(expenseX.roundToInt(), 0) }.graphicsLayer { alpha = eAlpha },
            contentAlignment = Alignment.Center) {
            StatCard(Icons.Default.TrendingDown, "支出", formatAmount(expense), Color(0xFFF87171), Color(0xFFDC2626))
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
fun AnimatedAmount(text: String, color: Color, fontSize: TextUnit, fontWeight: FontWeight = FontWeight.Bold) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        text.forEachIndexed { idx, c ->
            AnimatedContent(c, transitionSpec = {
                if (targetState > initialState)
                    (slideInVertically { it } + fadeIn()) togetherWith (slideOutVertically { -it } + fadeOut())
                else
                    (slideInVertically { -it } + fadeIn()) togetherWith (slideOutVertically { it } + fadeOut())
            }, label = "d_$idx") { ch -> Text(ch.toString(), color = color, fontSize = fontSize, fontWeight = fontWeight) }
        }
    }
}

@Composable
fun DayHeader(dateKey: String, income: Double, expense: Double, itemCount: Int = 0) {
    Row(Modifier
        .fillMaxWidth()
        .background(SURFACE_ELEVATED)
        .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(formatDateHeader(dateKey), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TEXT_SECONDARY)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (income > 0) {
                Icon(Icons.Default.TrendingUp, null, tint = COLOR_INCOME, modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(2.dp))
                Text(formatAmount(income), fontSize = 12.sp, color = COLOR_INCOME, fontWeight = FontWeight.SemiBold)
            }
            if (income > 0 && expense > 0) Spacer(Modifier.width(10.dp))
            if (expense > 0) {
                Icon(Icons.Default.TrendingDown, null, tint = COLOR_EXPENSE, modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(2.dp))
                Text(formatAmount(expense), fontSize = 12.sp, color = COLOR_EXPENSE, fontWeight = FontWeight.SemiBold)
                if (itemCount > 0) {
                    Spacer(Modifier.width(6.dp))
                    Text("(${itemCount}筆)", fontSize = 11.sp,
                        color = COLOR_EXPENSE.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Normal)
                }
            }
        }
    }
}

@Composable
fun IconSourceOption(icon: ImageVector, label: String, tint: Color = TEXT_PRIMARY, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
        .clickable { onClick() }.padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(label, color = tint, style = MaterialTheme.typography.bodyLarge)
    }
}
fun createTempImageUri(context: Context): Uri {
    val f = File.createTempFile("camera_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", f)
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
        val ch = name.trim().take(1).ifBlank { "?" }
        Box(Modifier.size(size).clip(CircleShape).background(avatarColor(name)), contentAlignment = Alignment.Center) {
            Text(
                ch,
                color = Color.White,
                fontSize = (size.value * 0.42f).sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(y = (-size.value * 0.03f).dp)
            )
        }
    } else {
        AsyncImage(model = iconUrl, contentDescription = null, contentScale = ContentScale.Crop,
            modifier = Modifier.size(size).clip(CircleShape))
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
    var isDragging by remember { mutableStateOf(false) }
    LaunchedEffect(expandedId) { if (expandedId != record.id && targetOffset != 0f) targetOffset = 0f }
    val offsetX by animateFloatAsState(targetOffset,
        if (isDragging) snap<Float>() else spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioNoBouncy),
        label = "swipe")

    val leftProgress = if (maxLeft == 0f) 0f else (offsetX / maxLeft).coerceIn(0f, 1f)
    val rightProgress = if (maxRight == 0f) 0f else (offsetX / maxRight).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(backgroundColor)
    ) {
        Row(
            Modifier.matchParentSize().padding(end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(gap, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedActionButton(
                icon = Icons.Default.ContentCopy, label = "複制",
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
                iconTint = Color(0xFFEF4444),
                width = bw, height = bh,
                progress = leftProgress, delay = 0.36f
            ) { targetOffset = 0f; onExpand(null); onDelete() }
        }

        Row(
            Modifier.matchParentSize().padding(start = 8.dp),
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(record.id) {
                    detectHorizontalDragGestures(
                        onDragStart = { isDragging = true; onExpand(record.id) },
                        onDragEnd = {
                            isDragging = false
                            val newOffset = when {
                                targetOffset < maxLeft * 0.25f -> maxLeft
                                targetOffset > maxRight * 0.25f -> maxRight
                                else -> 0f
                            }
                            targetOffset = newOffset
                            if (newOffset == 0f) onExpand(null)
                        },
                        onDragCancel = { isDragging = false; targetOffset = 0f; onExpand(null) },
                        onHorizontalDrag = { c, d ->
                            c.consume(); targetOffset = (targetOffset + d).coerceIn(maxLeft, maxRight)
                        })
                }
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    if (expandedId != null) onExpand(null)
                }
                .background(backgroundColor)
        ) {
            ListItem(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                leadingContent = { IconView(record.iconUrl, record.note) },
                headlineContent = { Text(record.note.ifBlank { "(無名稱)" },
                    fontSize = NOTE_FONT_SIZE, fontWeight = FontWeight.SemiBold, color = TEXT_PRIMARY) },
                supportingContent = { Text("${record.category}．${formatRecordTime(record.timestamp)}",
                    fontSize = META_FONT_SIZE, color = TEXT_TERTIARY) },
                trailingContent = { Text(displayAmount(record), color = amountColor(record.category),
                    fontSize = AMOUNT_FONT_SIZE, fontWeight = FontWeight.Bold) }
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
