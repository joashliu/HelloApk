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
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

// ===== 現代配色 =====
val BRAND_PRIMARY = Color(0xFF6366F1)         // Indigo
val BRAND_PRIMARY_DARK = Color(0xFF4F46E5)
val BRAND_PRIMARY_LIGHT = Color(0xFFE0E7FF)
val BRAND_ACCENT = Color(0xFFEC4899)          // Pink
val BRAND_GRADIENT_START = Color(0xFF6366F1)
val BRAND_GRADIENT_END = Color(0xFF8B5CF6)
val SURFACE_BG = Color(0xFFF8FAFC)            // 極淺灰白
val SURFACE_CARD = Color(0xFFFFFFFF)
val SURFACE_ELEVATED = Color(0xFFF1F5F9)
val TEXT_PRIMARY = Color(0xFF0F172A)
val TEXT_SECONDARY = Color(0xFF64748B)
val TEXT_TERTIARY = Color(0xFF94A3B8)
val DIVIDER_COLOR = Color(0xFFE2E8F0)

val COLOR_INCOME = Color(0xFF059669)          // Emerald
val COLOR_EXPENSE = Color(0xFFDC2626)         // Red

const val CLOUDINARY_CLOUD_NAME = "dfl59grn"
const val CLOUDINARY_UPLOAD_PRESET = "ledger_icons"
const val ICON_SIZE = 100

val NOTE_FONT_SIZE = 17.sp
val META_FONT_SIZE = 12.sp
val AMOUNT_FONT_SIZE = 19.sp
val STAT_AMOUNT_FONT_SIZE = 22.sp
val STAT_LABEL_FONT_SIZE = 11.sp

val NAV_HEIGHT = 60.dp
val NAV_TAB_WIDTH = 96.dp
val NAV_BOTTOM_PADDING = 20.dp

val ROW_ALT_COLOR = Color(0xFFF8FAFC)
const val FILTER_ANIM_MS = 250

// ===== 類別樣式 =====
data class CategoryStyle(
    val icon: ImageVector,
    val bgColor: Color,
    val fgColor: Color
)

val CATEGORIES = listOf(
    "收入", "娛樂", "家用", "飲食", "交通",
    "個人", "購物", "月費", "旅遊"
)
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

// ===== 資料模型 =====
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

// ===== 格式化工具 =====
fun formatAmount(amount: Double): String =
    String.format(Locale.US, "%,.1f", amount)

fun displayAmount(record: Record): String =
    if (record.category == INCOME_CATEGORY) formatAmount(record.amount)
    else formatAmount(-record.amount)

fun amountColor(category: String): Color =
    if (category == INCOME_CATEGORY) COLOR_INCOME else COLOR_EXPENSE

fun formatRecordTime(timestamp: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    val weekNames = arrayOf("週日", "週一", "週二", "週三", "週四", "週五", "週六")
    val week = weekNames[cal.get(Calendar.DAY_OF_WEEK) - 1]
    val hh = String.format(Locale.US, "%02d", cal.get(Calendar.HOUR_OF_DAY))
    val mm = String.format(Locale.US, "%02d", cal.get(Calendar.MINUTE))
    return "$week．$hh:$mm"
}

fun formatDateTime(timestamp: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    val y = cal.get(Calendar.YEAR)
    val mo = cal.get(Calendar.MONTH) + 1
    val d = cal.get(Calendar.DAY_OF_MONTH)
    val weekNames = arrayOf("週日", "週一", "週二", "週三", "週四", "週五", "週六")
    val week = weekNames[cal.get(Calendar.DAY_OF_WEEK) - 1]
    val hh = String.format(Locale.US, "%02d", cal.get(Calendar.HOUR_OF_DAY))
    val mm = String.format(Locale.US, "%02d", cal.get(Calendar.MINUTE))
    return "${y}年${mo}月${d}日 $week $hh:$mm"
}

fun dateKeyFromTimestamp(timestamp: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    return String.format(Locale.US, "%04d-%02d-%02d",
        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
}

fun monthKeyFromTimestamp(timestamp: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    return String.format(Locale.US, "%04d-%02d",
        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
}

fun formatMonthLabel(ym: String): String {
    val parts = ym.split("-")
    if (parts.size != 2) return ym
    val y = parts[0].toIntOrNull() ?: return ym
    val m = parts[1].toIntOrNull() ?: return ym
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    return if (y == currentYear) "${m}月" else "${y % 100}年${m}月"
}

fun formatDateHeader(dateKey: String): String {
    val parts = dateKey.split("-")
    if (parts.size != 3) return dateKey
    val year = parts[0].toIntOrNull() ?: return dateKey
    val month = parts[1].toIntOrNull() ?: return dateKey
    val day = parts[2].toIntOrNull() ?: return dateKey
    val cal = Calendar.getInstance().apply {
        set(year, month - 1, day)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val weekNames = arrayOf("週日", "週一", "週二", "週三", "週四", "週五", "週六")
    val week = weekNames[cal.get(Calendar.DAY_OF_WEEK) - 1]
    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val daysDiff = ((todayStart - cal.timeInMillis) / 86_400_000L).toInt()
    val datePart = when {
        daysDiff < 0 -> "未來"
        daysDiff == 0 -> "今日"
        daysDiff == 1 -> "琴日"
        daysDiff == 2 -> "前日"
        else -> "${month}月${day}日"
    }
    return "$datePart $week"
}

val AVATAR_COLORS = listOf(
    Color(0xFFE57373), Color(0xFFF06292), Color(0xFFBA68C8),
    Color(0xFF9575CD), Color(0xFF7986CB), Color(0xFF64B5F6),
    Color(0xFF4FC3F7), Color(0xFF4DB6AC), Color(0xFF81C784),
    Color(0xFFAED581), Color(0xFFFFB74D), Color(0xFFFF8A65),
    Color(0xFFA1887F), Color(0xFF90A4AE)
)

fun avatarColor(name: String): Color {
    if (name.isBlank()) return AVATAR_COLORS[0]
    val idx = (name.hashCode() and 0x7fffffff) % AVATAR_COLORS.size
    return AVATAR_COLORS[idx]
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = SURFACE_BG) {
                    MainApp()
                }
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

    val filtered by remember {
        derivedStateOf {
            records.toList().filter { r ->
                val catOk = filterCategory == null || r.category == filterCategory
                val monthOk = filterMonth == null || monthKeyFromTimestamp(r.timestamp) == filterMonth
                val searchOk = filterSearch.text.isBlank() ||
                    r.note.contains(filterSearch.text, ignoreCase = true) ||
                    r.category.contains(filterSearch.text, ignoreCase = true)
                catOk && monthOk && searchOk
            }
        }
    }
    val ledgerRecords by remember {
        derivedStateOf {
            val base = if (filterModeOn) filtered else records.toList()
            if (showFuture) base
            else base.filter { it.timestamp <= System.currentTimeMillis() + 60_000 }
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
                .map { (name, list) -> name to list.size }
                .sortedByDescending { it.second }.take(20)
        }
    }
    val noteIconMap by remember {
        derivedStateOf {
            ledgerRecords.filter { it.note.isNotBlank() && it.iconUrl.isNotBlank() }
                .groupBy { it.note }
                .mapValues { (_, list) -> list.maxByOrNull { it.timestamp }?.iconUrl ?: "" }
        }
    }
    val availableMonths by remember {
        derivedStateOf { records.map { monthKeyFromTimestamp(it.timestamp) }.distinct().sortedDescending() }
    }
    val globalIndexMap by remember {
        derivedStateOf {
            groupedByDate.flatMap { it.second }.withIndex().associate { (i, r) -> r.id to i }
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
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

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val target = iconTargetRecord
        val uri = pendingCameraUri
        if (success && target != null && uri != null) {
            scope.launch {
                uploading = true
                uploadIconAndApplyToSameName(context, db, target.id, target.note, uri)
                uploading = false
            }
        }
        pendingCameraUri = null
        iconTargetRecord = null
    }

    DisposableEffect(Unit) {
        val listener = db.collection("records")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                loading = false
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    records.clear()
                    snapshot.documents.forEach { doc ->
                        val r = doc.toObject(Record::class.java)
                        if (r != null) { r.id = doc.id; records.add(r) }
                    }
                }
            }
        onDispose { listener.remove() }
    }

    fun openKeyboardForNew(initialNote: String = "") {
        keyboardState = KeyboardState(noteText = initialNote)
        showKeyboard = true
    }
    fun openKeyboardForCopy(r: Record) {
        keyboardState = KeyboardState(
            amountText = r.amount.toString(),
            noteText = r.note, category = r.category,
            selectAmountOnInput = true
        )
        showKeyboard = true
    }
    fun openKeyboardForEdit(r: Record) {
        val amt = if (r.amount % 1.0 == 0.0) r.amount.toInt().toString() else r.amount.toString()
        keyboardState = KeyboardState(
            amountText = amt, noteText = r.note, category = r.category,
            editingRecordId = r.id, timestamp = r.timestamp, selectAmountOnInput = true
        )
        showKeyboard = true
    }
    fun saveFromKeyboard() {
        val amt = keyboardState.amountText.toDoubleOrNull() ?: return
        if (amt <= 0.0) {
            Toast.makeText(context, "請輸入金額", Toast.LENGTH_SHORT).show()
            return
        }
        val note = keyboardState.noteText
        val category = keyboardState.category
        val editId = keyboardState.editingRecordId
        if (editId != null) {
            db.collection("records").document(editId).update(
                mapOf("amount" to amt, "note" to note, "category" to category,
                    "timestamp" to keyboardState.timestamp)
            )
        } else {
            val inheritedIcon = records.filter { it.note == note && it.note.isNotBlank() }
                .maxByOrNull { it.timestamp }?.iconUrl ?: ""
            db.collection("records").add(Record(
                amount = amt, note = note, category = category,
                timestamp = keyboardState.timestamp, iconUrl = inheritedIcon))
        }
        showKeyboard = false
        keyboardState = KeyboardState()
    }

    BackHandler(enabled = showKeyboard) {
        showKeyboard = false
        keyboardState = KeyboardState()
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

    Box(modifier = Modifier.fillMaxSize().background(SURFACE_BG)) {
        when (currentPage) {
            0 -> LedgerContent(
                loading = loading, filtered = ledgerRecords,
                groupedByDate = groupedByDate, globalIndexMap = globalIndexMap,
                topNotes = topNotes, noteIconMap = noteIconMap,
                hasIncome = hasIncome, hasExpense = hasExpense,
                totalIncome = totalIncome, totalExpense = totalExpense,
                filterMode = filterModeOn, filterCategory = filterCategory,
                onFilterCategoryChange = { filterCategory = it },
                filterMonth = filterMonth, onFilterMonthChange = { filterMonth = it },
                availableMonths = availableMonths,
                expandedId = expandedId, onExpandChange = { expandedId = it },
                onQuickInputClick = { name -> openKeyboardForNew(name) },
                onCopyClick = { r -> openKeyboardForCopy(r) },
                onEditClick = { r -> openKeyboardForEdit(r) },
                onDeleteClick = { r -> db.collection("records").document(r.id).delete() },
                onChangeIconClick = { r -> iconTargetRecord = r; showIconSourceDialog = true },
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
                showFuture = showFuture, onShowFutureChange = { showFuture = it }
            )
            1 -> CompareContent(records = records, availableMonths = availableMonths)
        }

        androidx.compose.animation.AnimatedVisibility(
            visible = !showKeyboard,
            enter = fadeIn(tween(220)) + slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
            ),
            exit = fadeOut(tween(200)) + slideOutVertically(
                targetOffsetY = { it / 2 },
                animationSpec = tween(220, easing = FastOutSlowInEasing)
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                key("navbar") {
                    FloatingNavBar(
                        items = listOf(
                            NavItem("記帳", Icons.Default.Receipt),
                            NavItem("比較", Icons.Default.CompareArrows)
                        ),
                        selectedIndex = currentPage,
                        onIndexChange = { currentPage = it },
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = NAV_BOTTOM_PADDING)
                    )
                }
                key("filter_bottom") {
                    AnimatedVisibility(
                        visible = filterModeOn && currentPage == 0,
                        enter = slideInVertically(initialOffsetY = { it * 3 },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
                        ) + fadeIn(tween(180)),
                        exit = slideOutVertically(targetOffsetY = { it * 3 }, animationSpec = tween(220)) + fadeOut(tween(150)),
                        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(
                                start = 16.dp, end = 16.dp,
                                bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 12.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(26.dp),
                                color = SURFACE_CARD,
                                shadowElevation = 8.dp
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Search, null, tint = TEXT_SECONDARY, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    BasicTextField(
                                        value = filterSearch,
                                        onValueChange = { filterSearch = it },
                                        singleLine = true,
                                        textStyle = TextStyle(fontSize = 15.sp, color = TEXT_PRIMARY),
                                        cursorBrush = SolidColor(BRAND_PRIMARY),
                                        decorationBox = { inner ->
                                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                                                if (filterSearch.text.isEmpty()) {
                                                    Text("搜尋名稱或類別…", fontSize = 15.sp, color = TEXT_TERTIARY)
                                                }
                                                inner()
                                            }
                                        },
                                        modifier = Modifier.weight(1f).focusRequester(filterSearchFocusRequester)
                                    )
                                    if (filterSearch.text.isNotBlank()) {
                                        Spacer(Modifier.width(4.dp))
                                        Icon(Icons.Default.Close, "清除", tint = TEXT_SECONDARY,
                                            modifier = Modifier.size(20.dp).clickable { filterSearch = TextFieldValue("") })
                                    }
                                }
                            }
                            SmallFloatingActionButton(
                                onClick = { filterModeOn = false },
                                containerColor = BRAND_PRIMARY,
                                contentColor = Color.White
                            ) { Icon(Icons.Default.FilterAlt, "退出篩選") }
                            FloatingActionButton(
                                onClick = { filterModeOn = false; openKeyboardForNew() },
                                containerColor = BRAND_PRIMARY,
                                contentColor = Color.White
                            ) { Icon(Icons.Default.Add, "新增") }
                        }
                    }
                }
                key("fab") {
                    AnimatedVisibility(
                        visible = !filterModeOn && currentPage == 0,
                        enter = fadeIn(tween(180)) + scaleIn(initialScale = 0.6f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)),
                        exit = fadeOut(tween(150)) + scaleOut(targetScale = 0.6f, animationSpec = tween(180)),
                        modifier = Modifier.align(Alignment.BottomEnd).padding(
                            end = 20.dp, bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 20.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape)
                                    .background(SURFACE_CARD)
                                    .shadow(4.dp, CircleShape)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = { filterModeOn = true },
                                            onDoubleTap = { filterModeOn = true; filterSelectAllTrigger++ }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.FilterAlt, "篩選", tint = BRAND_PRIMARY, modifier = Modifier.size(22.dp))
                            }
                            FloatingActionButton(
                                onClick = { openKeyboardForNew() },
                                containerColor = BRAND_PRIMARY,
                                contentColor = Color.White
                            ) { Icon(Icons.Default.Add, "新增") }
                        }
                    }
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
                        value = urlInput, onValueChange = { urlInput = it },
                        label = { Text("URL") }, placeholder = { Text("https://...") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
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

// ===== 懸浮導航欄 =====
@Composable
fun FloatingNavBar(
    items: List<NavItem>, selectedIndex: Int,
    onIndexChange: (Int) -> Unit, modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val tabWidthPx = with(density) { NAV_TAB_WIDTH.toPx() }
    val totalWidthPx = tabWidthPx * items.size
    val maxOffset = totalWidthPx - tabWidthPx
    val viewConfiguration = LocalViewConfiguration.current
    val latestSelectedIndex by rememberUpdatedState(selectedIndex)
    val latestOnIndexChange by rememberUpdatedState(onIndexChange)
    val latestItemsSize by rememberUpdatedState(items.size)

    var bubbleOffset by remember { mutableFloatStateOf(selectedIndex * tabWidthPx) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(selectedIndex) { if (!isDragging) bubbleOffset = selectedIndex * tabWidthPx }

    val animatedOffset by animateFloatAsState(
        targetValue = bubbleOffset,
        animationSpec = if (isDragging) snap<Float>() else spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioNoBouncy),
        label = "bubble"
    )

    Surface(
        modifier = modifier.width(NAV_TAB_WIDTH * items.size).height(NAV_HEIGHT),
        shape = RoundedCornerShape(NAV_HEIGHT / 2),
        color = SURFACE_CARD,
        shadowElevation = 12.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val downX = down.position.x
                        val pointerId = down.id
                        var totalDx = 0f
                        var dragged = false
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == pointerId }
                            if (change == null || !change.pressed) {
                                if (!dragged) {
                                    val index = (downX / tabWidthPx).toInt().coerceIn(0, latestItemsSize - 1)
                                    if (index != latestSelectedIndex) latestOnIndexChange(index)
                                    bubbleOffset = index * tabWidthPx
                                } else {
                                    val targetIndex = (bubbleOffset / tabWidthPx).roundToInt().coerceIn(0, latestItemsSize - 1)
                                    bubbleOffset = targetIndex * tabWidthPx
                                    if (targetIndex != latestSelectedIndex) latestOnIndexChange(targetIndex)
                                }
                                isDragging = false
                                break
                            }
                            val dx = change.positionChange().x
                            totalDx += dx
                            if (!dragged && abs(totalDx) > viewConfiguration.touchSlop) {
                                dragged = true; isDragging = true
                            }
                            if (dragged) {
                                bubbleOffset = (bubbleOffset + dx).coerceIn(0f, maxOffset)
                                change.consume()
                                val currentIdx = (bubbleOffset / tabWidthPx).roundToInt().coerceIn(0, latestItemsSize - 1)
                                if (currentIdx != latestSelectedIndex) latestOnIndexChange(currentIdx)
                            }
                        }
                    }
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                    .width(NAV_TAB_WIDTH).fillMaxHeight().padding(6.dp)
                    .clip(RoundedCornerShape((NAV_HEIGHT - 12.dp) / 2))
                    .background(BRAND_PRIMARY_LIGHT)
            )
            Row(modifier = Modifier.fillMaxSize()) {
                items.forEachIndexed { index, item ->
                    val selected = index == selectedIndex
                    val tint = if (selected) BRAND_PRIMARY_DARK else TEXT_SECONDARY
                    Box(
                        modifier = Modifier.width(NAV_TAB_WIDTH).fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(item.icon, item.label, tint = tint, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(item.label, fontSize = 14.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                color = tint)
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
    globalIndexMap: Map<String, Int>,
    topNotes: List<Pair<String, Int>>, noteIconMap: Map<String, String>,
    hasIncome: Boolean, hasExpense: Boolean,
    totalIncome: Double, totalExpense: Double,
    filterMode: Boolean, filterCategory: String?,
    onFilterCategoryChange: (String?) -> Unit,
    filterMonth: String?, onFilterMonthChange: (String?) -> Unit,
    availableMonths: List<String>,
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
) {
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize().background(SURFACE_BG)) {
        AnimatedVisibility(
            visible = filterMode,
            enter = fadeIn(tween(FILTER_ANIM_MS)) + expandVertically(tween(FILTER_ANIM_MS), expandFrom = Alignment.Top),
            exit = fadeOut(tween(FILTER_ANIM_MS)) + shrinkVertically(tween(FILTER_ANIM_MS), shrinkTowards = Alignment.Top)
        ) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    AnimatedFilterChip(filterCategory == null, "全部") { onFilterCategoryChange(null) }
                    CATEGORIES.forEach { cat ->
                        AnimatedFilterChip(filterCategory == cat, cat) {
                            onFilterCategoryChange(if (filterCategory == cat) null else cat)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (availableMonths.isNotEmpty()) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        AnimatedFilterChip(filterMonth == null, "全年") { onFilterMonthChange(null) }
                        availableMonths.forEach { month ->
                            AnimatedFilterChip(filterMonth == month, formatMonthLabel(month)) {
                                onFilterMonthChange(if (filterMonth == month) null else month)
                            }
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
                Text(
                    if (filterMode) "冇符合篩選條件嘅記錄" else "仲未有記錄,撳右下角 + 新增",
                    color = TEXT_SECONDARY
                )
            }
            else -> {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        TopStats(hasIncome, hasExpense, totalIncome, totalExpense)
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
                        Spacer(Modifier.height(8.dp))
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 12.dp, end = 12.dp,
                            bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + if (filterMode) 90.dp else 20.dp)
                    ) {
                        groupedByDate.forEach { (dateKey, dayRecords) ->
                            val dayIncome = dayRecords.sumOf { if (it.category == INCOME_CATEGORY) it.amount else 0.0 }
                            val dayExpense = dayRecords.sumOf { if (it.category != INCOME_CATEGORY) it.amount else 0.0 }
                            item(key = "header_$dateKey") {
                                DayHeader(dateKey, dayIncome, dayExpense)
                            }
                            itemsIndexed(dayRecords, key = { _, r -> r.id }) { _, r ->
                                val idx = globalIndexMap[r.id] ?: 0
                                SwipeableRecordItem(
                                    modifier = Modifier.animateItem(),
                                    backgroundColor = if (idx % 2 == 0) SURFACE_CARD else ROW_ALT_COLOR,
                                    record = r,
                                    expandedId = expandedId, onExpand = onExpandChange,
                                    onCopy = { onCopyClick(r) }, onEdit = { onEditClick(r) },
                                    onFilter = { onFilterByName(r.note) },
                                    onDelete = { onDeleteClick(r) }, onChangeIcon = { onChangeIconClick(r) }
                                )
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
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LedgerKeyboardPanel(
                            state = keyboardState, onStateChange = onKeyboardStateChange,
                            onDismiss = onKeyboardDismiss, onConfirm = onKeyboardConfirm,
                            onNext = onKeyboardNext, onPickCategory = onKeyboardPickCategory,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

// ===== 比較頁 =====
@Composable
fun CompareContent(records: List<Record>, availableMonths: List<String>) {
    var monthA by remember { mutableStateOf<String?>(null) }
    var monthB by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(availableMonths) {
        if (monthA == null || monthA !in availableMonths) monthA = availableMonths.getOrNull(0)
        if (monthB == null || monthB !in availableMonths) monthB = availableMonths.getOrNull(1) ?: availableMonths.getOrNull(0)
    }
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("月份比較", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TEXT_PRIMARY,
            modifier = Modifier.padding(vertical = 8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MonthDropdown("月份 A", monthA, availableMonths, { monthA = it }, Modifier.weight(1f))
            MonthDropdown("月份 B", monthB, availableMonths, { monthB = it }, Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("類別", Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TEXT_SECONDARY)
            Text(monthA?.let { formatMonthLabel(it) } ?: "-", Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TEXT_SECONDARY, textAlign = TextAlign.End)
            Text(monthB?.let { formatMonthLabel(it) } ?: "-", Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TEXT_SECONDARY, textAlign = TextAlign.End)
            Text("差異", Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TEXT_SECONDARY, textAlign = TextAlign.End)
        }
        HorizontalDivider(color = DIVIDER_COLOR)
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 20.dp)) {
            items(EXPENSE_CATEGORIES) { cat ->
                val amtA = if (monthA != null) sumByCategoryAndMonth(records, cat, monthA!!) else 0.0
                val amtB = if (monthB != null) sumByCategoryAndMonth(records, cat, monthB!!) else 0.0
                val diff = amtB - amtA
                Row(Modifier.fillMaxWidth().padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(cat, Modifier.weight(1.2f), fontSize = 15.sp, color = TEXT_PRIMARY, fontWeight = FontWeight.Medium)
                    Text(formatAmount(amtA), Modifier.weight(1f), fontSize = 15.sp, color = TEXT_PRIMARY, textAlign = TextAlign.End)
                    Text(formatAmount(amtB), Modifier.weight(1f), fontSize = 15.sp, color = TEXT_PRIMARY, textAlign = TextAlign.End)
                    Text((if (diff > 0) "+" else "") + formatAmount(diff), Modifier.weight(1f), fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (diff > 0) COLOR_EXPENSE else if (diff < 0) COLOR_INCOME else TEXT_TERTIARY,
                        textAlign = TextAlign.End)
                }
                HorizontalDivider(color = DIVIDER_COLOR)
            }
            item {
                val totalA = if (monthA != null) EXPENSE_CATEGORIES.sumOf { sumByCategoryAndMonth(records, it, monthA!!) } else 0.0
                val totalB = if (monthB != null) EXPENSE_CATEGORIES.sumOf { sumByCategoryAndMonth(records, it, monthB!!) } else 0.0
                val totalDiff = totalB - totalA
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(SURFACE_ELEVATED)
                    .padding(vertical = 16.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("總計", Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TEXT_PRIMARY)
                    Text(formatAmount(totalA), Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TEXT_PRIMARY, textAlign = TextAlign.End)
                    Text(formatAmount(totalB), Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TEXT_PRIMARY, textAlign = TextAlign.End)
                    Text((if (totalDiff > 0) "+" else "") + formatAmount(totalDiff), Modifier.weight(1f),
                        fontWeight = FontWeight.Bold, fontSize = 15.sp,
                        color = if (totalDiff > 0) COLOR_EXPENSE else if (totalDiff < 0) COLOR_INCOME else TEXT_TERTIARY,
                        textAlign = TextAlign.End)
                }
            }
        }
    }
}

fun sumByCategoryAndMonth(records: List<Record>, category: String, month: String): Double =
    records.filter { it.category == category && monthKeyFromTimestamp(it.timestamp) == month }.sumOf { it.amount }

@Composable
fun MonthDropdown(label: String, value: String?, months: List<String>, onChange: (String?) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TEXT_PRIMARY)
        ) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Text(label, fontSize = 11.sp, color = TEXT_TERTIARY)
                Text(value?.let { formatMonthLabel(it) } ?: "未選擇", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(14.dp),
            containerColor = SURFACE_CARD) {
            months.forEach { m ->
                DropdownMenuItem(text = { Text(formatMonthLabel(m)) },
                    onClick = { onChange(m); expanded = false })
            }
        }
    }
}

// ===== 記帳鍵盤 =====
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LedgerKeyboardPanel(
    state: KeyboardState, onStateChange: (KeyboardState) -> Unit,
    onDismiss: () -> Unit, onConfirm: () -> Unit,
    onNext: () -> Unit, onPickCategory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    Surface(modifier = modifier, color = SURFACE_CARD,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(start = 14.dp, end = 14.dp, top = 14.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // 拖動指示條
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(Modifier.width(40.dp).height(4.dp).clip(CircleShape).background(DIVIDER_COLOR))
            }
            Spacer(Modifier.height(12.dp))

            // 日期時間
            Surface(
                modifier = Modifier.fillMaxWidth().height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        val cal = Calendar.getInstance().apply { timeInMillis = state.timestamp }
                        DatePickerDialog(ctx, { _, y, m, d ->
                            cal.set(Calendar.YEAR, y); cal.set(Calendar.MONTH, m); cal.set(Calendar.DAY_OF_MONTH, d)
                            TimePickerDialog(ctx, { _, h, mi ->
                                cal.set(Calendar.HOUR_OF_DAY, h); cal.set(Calendar.MINUTE, mi)
                                cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
                                onStateChange(state.copy(timestamp = cal.timeInMillis))
                            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                    },
                color = SURFACE_ELEVATED
            ) {
                Row(Modifier.fillMaxSize().padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Event, null, tint = BRAND_PRIMARY, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(formatDateTime(state.timestamp), fontSize = 14.sp, color = TEXT_PRIMARY)
                    Spacer(Modifier.weight(1f))
                    Text("點擊修改", fontSize = 11.sp, color = TEXT_TERTIARY)
                }
            }

            Spacer(Modifier.height(10.dp))

            // 金額顯示
            Surface(
                modifier = Modifier.fillMaxWidth().height(82.dp),
                shape = RoundedCornerShape(16.dp),
                color = SURFACE_ELEVATED
            ) {
                Box(Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)).padding(horizontal = 22.dp),
                    contentAlignment = Alignment.CenterEnd) {
                    val showText = if (state.amountText.isEmpty()) "0" else state.amountText
                    Text(showText, fontSize = 40.sp, fontWeight = FontWeight.Bold,
                        color = if (state.amountText.isEmpty()) TEXT_TERTIARY else TEXT_PRIMARY,
                        textAlign = TextAlign.End, maxLines = 1)
                }
            }

            Spacer(Modifier.height(12.dp))

            val rows = listOf(
                listOf("1", "2", "3"), listOf("4", "5", "6"),
                listOf("7", "8", "9"), listOf(".", "0", "backspace")
            )
            Column(Modifier.fillMaxWidth().height(200.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
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

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth().height(64.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                if (state.editingNote) {
                    var tfValue by remember(state.editingNote) {
                        mutableStateOf(TextFieldValue(text = state.noteText,
                            selection = TextRange(0, state.noteText.length)))
                    }
                    val noteFocusRequester = remember { FocusRequester() }
                    LaunchedEffect(Unit) { noteFocusRequester.requestFocus() }
                    TextField(
                        value = tfValue, onValueChange = { nv -> tfValue = nv; onStateChange(state.copy(noteText = nv.text)) },
                        placeholder = { Text("名稱") }, singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SURFACE_ELEVATED,
                            unfocusedContainerColor = SURFACE_ELEVATED,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onConfirm() }),
                        modifier = Modifier.weight(1f).fillMaxHeight().focusRequester(noteFocusRequester)
                    )
                } else {
                    OutlinedButton(
                        onClick = { onStateChange(state.copy(editingNote = true)) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TEXT_PRIMARY)
                    ) {
                        Text(
                            text = if (state.noteText.isBlank()) "輸入名稱" else state.noteText,
                            maxLines = 1, fontSize = 15.sp,
                            color = if (state.noteText.isBlank()) TEXT_TERTIARY else TEXT_PRIMARY
                        )
                    }
                }

                var showCategoryMenu by remember { mutableStateOf(false) }
                val currentStyle = CATEGORY_STYLES[state.category]
                Box {
                    OutlinedButton(
                        onClick = { showCategoryMenu = true },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.width(120.dp).fillMaxHeight(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TEXT_PRIMARY)
                    ) {
                        if (currentStyle != null) {
                            Icon(currentStyle.icon, null, tint = currentStyle.fgColor, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                        }
                        Text(state.category, maxLines = 1, fontSize = 14.sp)
                    }
                    DropdownMenu(
                        expanded = showCategoryMenu, onDismissRequest = { showCategoryMenu = false },
                        modifier = Modifier.width(288.dp),
                        shape = RoundedCornerShape(18.dp),
                        containerColor = SURFACE_CARD
                    ) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            maxItemsInEachRow = 2
                        ) {
                            CATEGORIES.forEach { cat ->
                                val s = CATEGORY_STYLES[cat]
                                val selected = cat == state.category
                                val chipShape = RoundedCornerShape(12.dp)
                                Row(
                                    modifier = Modifier.width(130.dp).clip(chipShape)
                                        .background(s?.bgColor ?: SURFACE_ELEVATED)
                                        .then(if (selected) Modifier.border(2.dp, BRAND_PRIMARY, chipShape) else Modifier)
                                        .clickable { onStateChange(state.copy(category = cat)); showCategoryMenu = false }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (s != null) {
                                        Icon(s.icon, null, tint = s.fgColor, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(6.dp))
                                    }
                                    Text(cat, fontSize = 14.sp,
                                        color = s?.fgColor ?: TEXT_PRIMARY,
                                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth().height(64.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onDismiss, shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TEXT_PRIMARY)
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
                        containerColor = BRAND_PRIMARY,
                        contentColor = Color.White)
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

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun KeyboardKey(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "keyScale"
    )
    val bgColor by animateColorAsState(
        targetValue = if (pressed) BRAND_PRIMARY_LIGHT else SURFACE_ELEVATED,
        animationSpec = spring(stiffness = Spring.StiffnessMedium), label = "keyBg"
    )
    Box(
        modifier = modifier.clip(RoundedCornerShape(14.dp)).background(bgColor)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (label == "backspace") {
            Icon(Icons.Default.Backspace, "退格", modifier = Modifier.size(22.dp), tint = TEXT_SECONDARY)
        } else {
            Text(label, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = TEXT_PRIMARY)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedFilterChip(selected: Boolean, label: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "chipScale"
    )
    FilterChip(
        selected = selected, onClick = onClick,
        label = { Text(label) },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(12.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = BRAND_PRIMARY,
            selectedLabelColor = Color.White,
            containerColor = SURFACE_CARD,
            labelColor = TEXT_SECONDARY
        ),
        modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
    )
}

// ===== 快速輸入 =====
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickInputSection(topNotes: List<Pair<String, Int>>, noteIconMap: Map<String, String>, onClick: (String) -> Unit) {
    if (topNotes.isEmpty()) return
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val availWidth = maxWidth - 32.dp
        val chipEstimate = 110.dp
        val perRow = max(1, (availWidth / chipEstimate).toInt())
        val perPage = perRow * 4
        val pageCount = max(1, (topNotes.size + perPage - 1) / perPage)
        val pagerState = rememberPagerState { pageCount }
        val linesToShow = if (pageCount == 1) (topNotes.size + perRow - 1) / perRow else 4
        val rowHeight = 48.dp
        val vGap = 6.dp
        val pagerHeight = rowHeight * linesToShow + vGap * (linesToShow - 1)

        Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().height(pagerHeight)) { page ->
                val start = page * perPage
                val end = minOf(start + perPage, topNotes.size)
                if (start >= end) return@HorizontalPager
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(vGap),
                    maxItemsInEachRow = perRow, maxLines = 4
                ) {
                    for (i in start until end) {
                        val name = topNotes[i].first
                        SuggestionChip(
                            onClick = { onClick(name) },
                            label = { Text(name) },
                            shape = RoundedCornerShape(12.dp),
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = SURFACE_CARD,
                                labelColor = TEXT_PRIMARY),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = DIVIDER_COLOR),
                            icon = { IconView(noteIconMap[name] ?: "", name, size = 22.dp) }
                        )
                    }
                }
            }
            if (pageCount > 1) {
                Row(Modifier.fillMaxWidth().padding(top = 6.dp),
                    horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    repeat(pageCount) { index ->
                        val active = index == pagerState.currentPage
                        Box(Modifier.padding(horizontal = 3.dp).size(if (active) 7.dp else 5.dp).clip(CircleShape)
                            .background(if (active) BRAND_PRIMARY else DIVIDER_COLOR))
                    }
                }
            }
        }
    }
}

// ===== 頂部統計（漸變卡片）=====
@Composable
fun TopStats(hasIncome: Boolean, hasExpense: Boolean, income: Double, expense: Double) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)) {
        val density = LocalDensity.current
        val fullWidthPx = with(density) { maxWidth.toPx() }
        val halfWidthPx = fullWidthPx / 2f
        val slotWidth = maxWidth / 2

        val incomeTargetX = when {
            hasIncome && hasExpense -> 0f
            hasIncome -> halfWidthPx / 2f
            else -> -halfWidthPx
        }
        val expenseTargetX = when {
            hasIncome && hasExpense -> halfWidthPx
            hasExpense -> halfWidthPx / 2f
            else -> fullWidthPx
        }

        val incomeX by animateFloatAsState(incomeTargetX,
            spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow), label = "ix")
        val expenseX by animateFloatAsState(expenseTargetX,
            spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow), label = "ex")
        val incomeAlpha by animateFloatAsState(if (hasIncome) 1f else 0f, tween(220), label = "ia")
        val expenseAlpha by animateFloatAsState(if (hasExpense) 1f else 0f, tween(220), label = "ea")

        Box(
            modifier = Modifier.width(slotWidth).offset { IntOffset(incomeX.roundToInt(), 0) }
                .graphicsLayer { alpha = incomeAlpha },
            contentAlignment = Alignment.Center
        ) {
            StatCard(
                icon = Icons.Default.TrendingUp, label = "收入",
                amountText = formatAmount(income),
                gradientStart = Color(0xFF10B981), gradientEnd = Color(0xFF059669)
            )
        }
        Box(
            modifier = Modifier.width(slotWidth).offset { IntOffset(expenseX.roundToInt(), 0) }
                .graphicsLayer { alpha = expenseAlpha },
            contentAlignment = Alignment.Center
        ) {
            StatCard(
                icon = Icons.Default.TrendingDown, label = "支出",
                amountText = formatAmount(expense),
                gradientStart = Color(0xFFF87171), gradientEnd = Color(0xFFDC2626)
            )
        }
    }
}

@Composable
fun StatCard(
    icon: ImageVector, label: String, amountText: String,
    gradientStart: Color, gradientEnd: Color
) {
    Column(
        modifier = Modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = gradientStart, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(label, fontSize = STAT_LABEL_FONT_SIZE, color = TEXT_SECONDARY, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(4.dp))
        AnimatedAmount(
            text = amountText, color = gradientEnd,
            fontSize = STAT_AMOUNT_FONT_SIZE, fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AnimatedAmount(text: String, color: Color, fontSize: TextUnit, fontWeight: FontWeight = FontWeight.Bold) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        text.forEachIndexed { index, c ->
            AnimatedContent(
                targetState = c,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInVertically { it } + fadeIn()) togetherWith (slideOutVertically { -it } + fadeOut())
                    } else {
                        (slideInVertically { -it } + fadeIn()) togetherWith (slideOutVertically { it } + fadeOut())
                    }
                },
                label = "digit_$index"
            ) { char ->
                Text(char.toString(), color = color, fontSize = fontSize, fontWeight = fontWeight)
            }
        }
    }
}

// ===== 日期分組標題 =====
@Composable
fun DayHeader(dateKey: String, income: Double, expense: Double) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(formatDateHeader(dateKey), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TEXT_SECONDARY)
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
            }
        }
    }
}

@Composable
fun IconSourceOption(icon: ImageVector, label: String, tint: Color = TEXT_PRIMARY, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }.padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(label, color = tint, style = MaterialTheme.typography.bodyLarge)
    }
}

// ===== 建立拍照用嘅臨時檔案 URI =====
fun createTempImageUri(context: Context): Uri {
    val file = File.createTempFile("camera_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

// ===== 壓縮圖片 =====
suspend fun compressImage(context: Context, uri: Uri, maxSize: Int = ICON_SIZE): ByteArray? =
    withContext(Dispatchers.IO) {
        try {
            val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, boundsOpts) }
            val w = boundsOpts.outWidth; val h = boundsOpts.outHeight
            if (w <= 0 || h <= 0) return@withContext null
            var sample = 1
            val minDim = minOf(w, h)
            while (minDim / (sample * 2) >= maxSize) sample *= 2
            val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sample }
            val src = context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, decodeOpts)
            } ?: return@withContext null
            val scaled = scaleCropCenter(src, maxSize)
            if (scaled !== src) src.recycle()
            val baos = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            scaled.recycle()
            baos.toByteArray()
        } catch (e: Exception) { null }
    }

fun scaleCropCenter(src: Bitmap, size: Int): Bitmap {
    val w = src.width; val h = src.height
    val minDim = minOf(w, h)
    val x = (w - minDim) / 2; val y = (h - minDim) / 2
    val cropped = if (x == 0 && y == 0 && w == minDim && h == minDim) src
                  else Bitmap.createBitmap(src, x, y, minDim, minDim)
    val scaled = if (cropped.width == size && cropped.height == size) cropped
                 else Bitmap.createScaledBitmap(cropped, size, size, true)
    if (cropped !== src && cropped !== scaled) cropped.recycle()
    return scaled
}

// ===== 上傳圖片 =====
suspend fun uploadIconAndApplyToSameName(
    context: Context, db: FirebaseFirestore,
    recordId: String, recordName: String, uri: Uri
) {
    try {
        val bytes = compressImage(context, uri)
        if (bytes == null) { Toast.makeText(context, "讀取圖片失敗", Toast.LENGTH_LONG).show(); return }
        val imageUrl = uploadBytesToCloudinary(bytes)
        if (imageUrl == null) { Toast.makeText(context, "上傳失敗,請檢查網絡", Toast.LENGTH_LONG).show(); return }
        applyUrlToSameName(context, db, recordName, imageUrl)
    } catch (e: Exception) {
        Toast.makeText(context, "失敗:${e.message}", Toast.LENGTH_LONG).show()
    }
}

suspend fun applyUrlToSameName(context: Context, db: FirebaseFirestore, recordName: String, imageUrl: String) {
    try {
        if (recordName.isBlank()) return
        val snapshot = db.collection("records").whereEqualTo("note", recordName).get().await()
        val batch = db.batch()
        snapshot.documents.forEach { doc -> batch.update(doc.reference, "iconUrl", imageUrl) }
        batch.commit().await()
        val count = snapshot.size()
        Toast.makeText(context,
            if (count > 1) "圖標已套用到 $count 條同名記錄" else "圖標已更新",
            Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "失敗:${e.message}", Toast.LENGTH_LONG).show()
    }
}

suspend fun removeIconFromSameName(context: Context, db: FirebaseFirestore, recordName: String) {
    try {
        if (recordName.isBlank()) { Toast.makeText(context, "冇名稱,無法刪除", Toast.LENGTH_SHORT).show(); return }
        val snapshot = db.collection("records").whereEqualTo("note", recordName).get().await()
        val batch = db.batch()
        snapshot.documents.forEach { doc -> batch.update(doc.reference, "iconUrl", "") }
        batch.commit().await()
        val count = snapshot.size()
        Toast.makeText(context,
            if (count > 1) "已刪除 $count 條同名記錄嘅圖標" else "圖標已刪除",
            Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "失敗:${e.message}", Toast.LENGTH_LONG).show()
    }
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
        conn.outputStream.use { output ->
            output.write("--$boundary$lineEnd".toByteArray())
            output.write(("Content-Disposition: form-data; name=\"upload_preset\"$lineEnd$lineEnd").toByteArray())
            output.write("$CLOUDINARY_UPLOAD_PRESET$lineEnd".toByteArray())
            output.write("--$boundary$lineEnd".toByteArray())
            output.write(("Content-Disposition: form-data; name=\"file\"; filename=\"icon.jpg\"$lineEnd").toByteArray())
            output.write("Content-Type: image/jpeg$lineEnd$lineEnd".toByteArray())
            output.write(bytes); output.write("$lineEnd".toByteArray())
            output.write("--$boundary--$lineEnd".toByteArray())
            output.flush()
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
        val firstChar = name.trim().take(1).ifBlank { "?" }
        Box(Modifier.size(size).clip(CircleShape).background(avatarColor(name)),
            contentAlignment = Alignment.Center) {
            Text(firstChar, color = Color.White,
                fontSize = (size.value * 0.42f).sp, fontWeight = FontWeight.Bold)
        }
    } else {
        AsyncImage(model = iconUrl, contentDescription = null,
            contentScale = ContentScale.Crop,
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
    val buttonWidth = 56.dp; val buttonHeight = 44.dp; val gap = 6.dp
    val buttonWidthPx = with(density) { buttonWidth.toPx() }
    val gapPx = with(density) { gap.toPx() }
    val edgePadding = 8.dp
    val edgePaddingPx = with(density) { edgePadding.toPx() }
    val leftTotalPx = buttonWidthPx * 4 + gapPx * 3
    val rightTotalPx = buttonWidthPx
    val maxLeftReveal = -(leftTotalPx + edgePaddingPx)
    val maxRightReveal = rightTotalPx + edgePaddingPx

    var targetOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    LaunchedEffect(expandedId) { if (expandedId != record.id && targetOffset != 0f) targetOffset = 0f }

    val offsetX by animateFloatAsState(
        targetValue = targetOffset,
        animationSpec = if (isDragging) snap<Float>() else spring(
            stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "swipe"
    )

    Box(modifier = modifier.fillMaxWidth().wrapContentHeight().padding(vertical = 3.dp)) {
        Row(
            modifier = Modifier.matchParentSize().padding(end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(gap, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(Icons.Default.ContentCopy, "複制", Color(0xFF64748B), buttonWidth, buttonHeight)
                { targetOffset = 0f; onExpand(null); onCopy() }
            ActionButton(Icons.Default.Edit, "編輯", Color(0xFF3B82F6), buttonWidth, buttonHeight)
                { targetOffset = 0f; onExpand(null); onEdit() }
            ActionButton(Icons.Default.FilterList, "篩選", Color(0xFF8B5CF6), buttonWidth, buttonHeight)
                { targetOffset = 0f; onExpand(null); onFilter() }
            ActionButton(Icons.Default.Delete, "刪除", Color(0xFFEF4444), buttonWidth, buttonHeight)
                { targetOffset = 0f; onExpand(null); onDelete() }
        }
        Row(
            modifier = Modifier.matchParentSize().padding(start = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(gap, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(Icons.Default.Image, "改圖標", Color(0xFF10B981), buttonWidth, buttonHeight)
                { targetOffset = 0f; onExpand(null); onChangeIcon() }
        }
        Surface(
            modifier = Modifier.fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(record.id) {
                    detectHorizontalDragGestures(
                        onDragStart = { isDragging = true; onExpand(record.id) },
                        onDragEnd = {
                            isDragging = false
                            val newOffset = when {
                                targetOffset < maxLeftReveal * 0.25f -> maxLeftReveal
                                targetOffset > maxRightReveal * 0.25f -> maxRightReveal
                                else -> 0f
                            }
                            targetOffset = newOffset
                            if (newOffset == 0f) onExpand(null)
                        },
                        onDragCancel = { isDragging = false; targetOffset = 0f; onExpand(null) },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            targetOffset = (targetOffset + dragAmount).coerceIn(maxLeftReveal, maxRightReveal)
                        }
                    )
                }
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    if (expandedId != null) onExpand(null)
                },
            shape = RoundedCornerShape(16.dp),
            color = backgroundColor,
            shadowElevation = 1.dp
        ) {
            ListItem(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                leadingContent = { IconView(record.iconUrl, record.note) },
                headlineContent = {
                    Text(record.note.ifBlank { "(無名稱)" },
                        fontSize = NOTE_FONT_SIZE, fontWeight = FontWeight.SemiBold, color = TEXT_PRIMARY)
                },
                supportingContent = {
                    Text("${record.category}．${formatRecordTime(record.timestamp)}",
                        fontSize = META_FONT_SIZE, color = TEXT_TERTIARY)
                },
                trailingContent = {
                    Text(displayAmount(record), color = amountColor(record.category),
                        fontSize = AMOUNT_FONT_SIZE, fontWeight = FontWeight.Bold)
                }
            )
        }
    }
}

@Composable
private fun ActionButton(icon: ImageVector, label: String, background: Color, width: Dp, height: Dp, onClick: () -> Unit) {
    Box(
        modifier = Modifier.width(width).height(height)
            .clip(RoundedCornerShape(14.dp)).background(background)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, label, tint = Color.White, modifier = Modifier.size(22.dp))
    }
}
