package com.example.hello

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

// ===== 常數 =====
val CATEGORIES = listOf(
    "收入", "娛樂", "家用", "飲食", "交通",
    "個人", "購物", "月費", "旅遊"
)
val EXPENSE_CATEGORIES = CATEGORIES.filter { it != "收入" }

val INCOME_CATEGORY = "收入"

val COLOR_INCOME = Color(0xFF1B5E20)
val COLOR_EXPENSE = Color(0xFFB71C1C)

const val CLOUDINARY_CLOUD_NAME = "dfl59grn"
const val CLOUDINARY_UPLOAD_PRESET = "ledger_icons"
const val ICON_SIZE = 100

val NOTE_FONT_SIZE = 19.sp
val META_FONT_SIZE = 13.sp
val AMOUNT_FONT_SIZE = 20.sp
val STAT_AMOUNT_FONT_SIZE = 19.sp
val STAT_LABEL_FONT_SIZE = 12.sp

val NAV_HEIGHT = 60.dp
val NAV_TAB_WIDTH = 96.dp
val NAV_BOTTOM_PADDING = 20.dp

val ROW_ALT_COLOR = Color(0xFFE3F2FD)

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
    val editingRecordId: String? = null
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
    val weekNames = arrayOf(
        "週日", "週一", "週二", "週三", "週四", "週五", "週六"
    )
    val week = weekNames[cal.get(Calendar.DAY_OF_WEEK) - 1]
    val hh = String.format(Locale.US, "%02d", cal.get(Calendar.HOUR_OF_DAY))
    val mm = String.format(Locale.US, "%02d", cal.get(Calendar.MINUTE))
    return "$week．$hh:$mm"
}

fun dateKeyFromTimestamp(timestamp: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    return String.format(
        Locale.US, "%04d-%02d-%02d",
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH) + 1,
        cal.get(Calendar.DAY_OF_MONTH)
    )
}

fun monthKeyFromTimestamp(timestamp: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    return String.format(
        Locale.US, "%04d-%02d",
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH) + 1
    )
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
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val weekNames = arrayOf(
        "週日", "週一", "週二", "週三", "週四", "週五", "週六"
    )
    val week = weekNames[cal.get(Calendar.DAY_OF_WEEK) - 1]

    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val daysDiff = ((todayStart - cal.timeInMillis) / 86_400_000L).toInt()

    val datePart = when {
        daysDiff <= 0 -> "今日"
        daysDiff == 1 -> "琴日"
        daysDiff == 2 -> "前日"
        else -> "${month}月${day}日"
    }
    return "$datePart $week"
}

// ===== 文字頭像調色盤 =====
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

// ===== 主 Activity =====
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainApp()
                }
            }
        }
    }
}

// ===== 主 App =====
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
    var filterSearch by remember { mutableStateOf("") }

    var iconTargetRecord by remember { mutableStateOf<Record?>(null) }
    var showIconSourceDialog by remember { mutableStateOf(false) }
    var showUrlInputDialog by remember { mutableStateOf(false) }
    var urlInput by remember { mutableStateOf("") }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }

    var showKeyboard by remember { mutableStateOf(false) }
    var keyboardState by remember { mutableStateOf(KeyboardState()) }
    var showKeyboardCategoryPicker by remember { mutableStateOf(false) }

    val filtered by remember {
        derivedStateOf {
            val snapshot = records.toList()
            snapshot.filter { r ->
                val catOk = filterCategory == null ||
                    r.category == filterCategory
                val monthOk = filterMonth == null ||
                    monthKeyFromTimestamp(r.timestamp) == filterMonth
                val searchOk = filterSearch.isBlank() ||
                    r.note.contains(filterSearch, ignoreCase = true) ||
                    r.category.contains(filterSearch, ignoreCase = true)
                catOk && monthOk && searchOk
            }
        }
    }
    val ledgerRecords by remember {
        derivedStateOf { if (filterModeOn) filtered else records.toList() }
    }

    val hasIncome by remember {
        derivedStateOf {
            ledgerRecords.any { it.category == INCOME_CATEGORY }
        }
    }
    val hasExpense by remember {
        derivedStateOf {
            ledgerRecords.any { it.category != INCOME_CATEGORY }
        }
    }
    val totalIncome by remember {
        derivedStateOf {
            ledgerRecords.filter { it.category == INCOME_CATEGORY }
                .sumOf { it.amount }
        }
    }
    val totalExpense by remember {
        derivedStateOf {
            ledgerRecords.filter { it.category != INCOME_CATEGORY }
                .sumOf { it.amount }
        }
    }
    val groupedByDate by remember {
        derivedStateOf {
            ledgerRecords.groupBy { dateKeyFromTimestamp(it.timestamp) }.toList()
        }
    }
    val topNotes by remember {
        derivedStateOf {
            ledgerRecords.filter { it.note.isNotBlank() }
                .groupBy { it.note }
                .map { (name, list) -> name to list.size }
                .sortedByDescending { it.second }
                .take(20)
        }
    }
    val noteIconMap by remember {
        derivedStateOf {
            ledgerRecords.filter { it.note.isNotBlank() && it.iconUrl.isNotBlank() }
                .groupBy { it.note }
                .mapValues { (_, list) ->
                    list.maxByOrNull { it.timestamp }?.iconUrl ?: ""
                }
        }
    }
    val availableMonths by remember {
        derivedStateOf {
            records.map { monthKeyFromTimestamp(it.timestamp) }
                .distinct()
                .sortedDescending()
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        val target = iconTargetRecord
        if (uri != null && target != null) {
            scope.launch {
                uploading = true
                uploadIconAndApplyToSameName(
                    context, db, target.id, target.note, uri
                )
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
                uploadIconAndApplyToSameName(
                    context, db, target.id, target.note, uri
                )
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
                if (error != null) {
                    Log.w("Ledger", "Listen failed.", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    records.clear()
                    snapshot.documents.forEach { doc ->
                        val r = doc.toObject(Record::class.java)
                        if (r != null) {
                            r.id = doc.id
                            records.add(r)
                        }
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
            amountText = "",
            noteText = r.note,
            category = r.category
        )
        showKeyboard = true
    }

    fun openKeyboardForEdit(r: Record) {
        val amt = if (r.amount % 1.0 == 0.0)
            r.amount.toInt().toString()
        else
            r.amount.toString()
        keyboardState = KeyboardState(
            amountText = amt,
            noteText = r.note,
            category = r.category,
            editingRecordId = r.id
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
                mapOf(
                    "amount" to amt,
                    "note" to note,
                    "category" to category
                )
            )
        } else {
            val inheritedIcon = records
                .filter { it.note == note && it.note.isNotBlank() }
                .maxByOrNull { it.timestamp }
                ?.iconUrl ?: ""
            db.collection("records").add(
                Record(
                    amount = amt,
                    note = note,
                    category = category,
                    iconUrl = inheritedIcon
                )
            )
        }
        showKeyboard = false
        keyboardState = KeyboardState()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentPage) {
            0 -> LedgerContent(
                loading = loading,
                filtered = ledgerRecords,
                groupedByDate = groupedByDate,
                topNotes = topNotes,
                noteIconMap = noteIconMap,
                hasIncome = hasIncome,
                hasExpense = hasExpense,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                filterMode = filterModeOn,
                filterCategory = filterCategory,
                onFilterCategoryChange = { filterCategory = it },
                filterMonth = filterMonth,
                onFilterMonthChange = { filterMonth = it },
                availableMonths = availableMonths,
                expandedId = expandedId,
                onExpandChange = { expandedId = it },
                onQuickInputClick = { name -> openKeyboardForNew(name) },
                onCopyClick = { r -> openKeyboardForCopy(r) },
                onEditClick = { r -> openKeyboardForEdit(r) },
                onDeleteClick = { r ->
                    db.collection("records").document(r.id).delete()
                },
                onChangeIconClick = { r ->
                    iconTargetRecord = r
                    showIconSourceDialog = true
                },
                showKeyboard = showKeyboard,
                keyboardState = keyboardState,
                onKeyboardStateChange = { keyboardState = it },
                onKeyboardDismiss = {
                    showKeyboard = false
                    keyboardState = KeyboardState()
                },
                onKeyboardConfirm = { saveFromKeyboard() },
                onKeyboardNext = {
                    keyboardState = keyboardState.copy(editingNote = true)
                },
                onKeyboardPickCategory = {
                    showKeyboardCategoryPicker = true
                }
            )

            1 -> CompareContent(
                records = records,
                availableMonths = availableMonths
            )

            2 -> FilterContent(
                records = records,
                searchQuery = filterSearch,
                onSearchChange = { filterSearch = it },
                filterCategory = filterCategory,
                onFilterCategoryChange = { filterCategory = it },
                filterMonth = filterMonth,
                onFilterMonthChange = { filterMonth = it },
                availableMonths = availableMonths,
                onCopyClick = { r -> openKeyboardForCopy(r) },
                onEditClick = { r -> openKeyboardForEdit(r) },
                onDeleteClick = { r ->
                    db.collection("records").document(r.id).delete()
                },
                onChangeIconClick = { r ->
                    iconTargetRecord = r
                    showIconSourceDialog = true
                }
            )
        }

        if (!showKeyboard) {
            key("navbar") {
                FloatingNavBar(
                    items = listOf(
                        NavItem("記帳", Icons.Default.Receipt),
                        NavItem("比較", Icons.Default.CompareArrows),
                        NavItem("篩選", Icons.Default.FilterList)
                    ),
                    selectedIndex = currentPage,
                    onIndexChange = { currentPage = it },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = NAV_BOTTOM_PADDING)
                )
            }

            key("fab") {
                if (currentPage == 0 && filterModeOn) {
                    // 篩選模式：搜尋欄 + 篩選關 + 新增FAB（同一行）
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 12.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 6.dp,
                            tonalElevation = 3.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                BasicTextField(
                                    value = filterSearch,
                                    onValueChange = { filterSearch = it },
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    cursorBrush = SolidColor(
                                        MaterialTheme.colorScheme.primary
                                    ),
                                    decorationBox = { inner ->
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            if (filterSearch.isEmpty()) {
                                                Text(
                                                    "搜尋名稱或類別…",
                                                    fontSize = 15.sp,
                                                    color = MaterialTheme.colorScheme
                                                        .onSurfaceVariant
                                                )
                                            }
                                            inner()
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                if (filterSearch.isNotBlank()) {
                                    Spacer(Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "清除",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable { filterSearch = "" }
                                    )
                                }
                            }
                        }
                        SmallFloatingActionButton(
                            onClick = { filterModeOn = false },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(
                                Icons.Default.FilterAlt,
                                contentDescription = "退出篩選"
                            )
                        }
                        FloatingActionButton(
                            onClick = {
                                filterModeOn = false
                                openKeyboardForNew()
                            }
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "新增"
                            )
                        }
                    }
                } else if (currentPage == 0) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(
                                end = 20.dp,
                                bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 20.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SmallFloatingActionButton(
                            onClick = { filterModeOn = true },
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            Icon(
                                Icons.Default.FilterAlt,
                                contentDescription = "篩選開關"
                            )
                        }
                        FloatingActionButton(
                            onClick = { openKeyboardForNew() }
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "新增"
                            )
                        }
                    }
                }
            }
        }

        key("uploading") {
            if (uploading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x80000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Card {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(12.dp))
                            Text("上傳中...")
                        }
                    }
                }
            }
        }
    }

    if (showKeyboardCategoryPicker) {
        AlertDialog(
            onDismissRequest = { showKeyboardCategoryPicker = false },
            title = { Text("揀類別") },
            text = {
                Column {
                    CATEGORIES.forEach { cat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    keyboardState =
                                        keyboardState.copy(category = cat)
                                    showKeyboardCategoryPicker = false
                                }
                                .padding(
                                    vertical = 10.dp,
                                    horizontal = 4.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = cat == keyboardState.category,
                                onClick = {
                                    keyboardState =
                                        keyboardState.copy(category = cat)
                                    showKeyboardCategoryPicker = false
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                cat,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showKeyboardCategoryPicker = false
                }) { Text("取消") }
            }
        )
    }

    if (showIconSourceDialog) {
        val target = iconTargetRecord
        val hasIcon = target?.iconUrl?.isNotBlank() == true

        AlertDialog(
            onDismissRequest = {
                showIconSourceDialog = false
                iconTargetRecord = null
            },
            title = { Text("圖標設定") },
            text = {
                Column {
                    IconSourceOption(
                        icon = Icons.Default.PhotoLibrary,
                        label = "從相冊揀"
                    ) {
                        showIconSourceDialog = false
                        pickImageLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                    IconSourceOption(
                        icon = Icons.Default.PhotoCamera,
                        label = "即時拍照"
                    ) {
                        showIconSourceDialog = false
                        val uri = createTempImageUri(context)
                        pendingCameraUri = uri
                        takePictureLauncher.launch(uri)
                    }
                    IconSourceOption(
                        icon = Icons.Default.Link,
                        label = "貼上網址"
                    ) {
                        showIconSourceDialog = false
                        urlInput = ""
                        showUrlInputDialog = true
                    }
                    if (hasIcon) {
                        HorizontalDivider(Modifier.padding(vertical = 4.dp))
                        IconSourceOption(
                            icon = Icons.Default.Delete,
                            label = "刪除圖標",
                            tint = Color(0xFFF44336)
                        ) {
                            showIconSourceDialog = false
                            if (target != null) {
                                scope.launch {
                                    uploading = true
                                    removeIconFromSameName(
                                        context, db, target.note
                                    )
                                    uploading = false
                                }
                            }
                            iconTargetRecord = null
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showIconSourceDialog = false
                    iconTargetRecord = null
                }) { Text("取消") }
            }
        )
    }

    if (showUrlInputDialog) {
        AlertDialog(
            onDismissRequest = {
                showUrlInputDialog = false
                urlInput = ""
                iconTargetRecord = null
            },
            title = { Text("輸入圖片網址") },
            text = {
                Column {
                    Text(
                        "貼上 PNG / JPG / WebP 圖片連結",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text("URL") },
                        placeholder = { Text("https://...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val url = urlInput.trim()
                    val target = iconTargetRecord
                    val valid = url.startsWith("http://") ||
                                url.startsWith("https://")
                    if (target != null && url.isNotBlank() && valid) {
                        scope.launch {
                            uploading = true
                            applyUrlToSameName(context, db, target.note, url)
                            uploading = false
                        }
                    } else if (!valid) {
                        Toast.makeText(
                            context,
                            "網址要 http:// 或 https:// 開頭",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    showUrlInputDialog = false
                    urlInput = ""
                    iconTargetRecord = null
                }) { Text("確定") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showUrlInputDialog = false
                    urlInput = ""
                    iconTargetRecord = null
                }) { Text("取消") }
            }
        )
    }
}

// ===== 懸浮導航欄 =====
@Composable
fun FloatingNavBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val tabWidthPx = with(density) { NAV_TAB_WIDTH.toPx() }
    val totalWidthPx = tabWidthPx * items.size
    val maxOffset = totalWidthPx - tabWidthPx
    val viewConfiguration = LocalViewConfiguration.current

    val latestSelectedIndex by rememberUpdatedState(selectedIndex)
    val latestOnIndexChange by rememberUpdatedState(onIndexChange)
    val latestItemsSize by rememberUpdatedState(items.size)

    var bubbleOffset by remember {
        mutableFloatStateOf(selectedIndex * tabWidthPx)
    }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(selectedIndex) {
        if (!isDragging) {
            bubbleOffset = selectedIndex * tabWidthPx
        }
    }

    val animatedOffset by animateFloatAsState(
        targetValue = bubbleOffset,
        animationSpec = if (isDragging) {
            snap<Float>()
        } else {
            spring<Float>(
                stiffness = Spring.StiffnessMediumLow,
                dampingRatio = Spring.DampingRatioNoBouncy
            )
        },
        label = "bubble"
    )

    Surface(
        modifier = modifier
            .width(NAV_TAB_WIDTH * items.size)
            .height(NAV_HEIGHT),
        shape = RoundedCornerShape(NAV_HEIGHT / 2),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 10.dp,
        tonalElevation = 3.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val down = awaitFirstDown(
                                requireUnconsumed = false
                            )
                            val downX = down.position.x
                            val pointerId = down.id
                            var totalDx = 0f
                            var dragged = false

                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes
                                    .firstOrNull { it.id == pointerId }

                                if (change == null || !change.pressed) {
                                    if (!dragged) {
                                        val index = (downX / tabWidthPx)
                                            .toInt()
                                            .coerceIn(
                                                0,
                                                latestItemsSize - 1
                                            )
                                        if (index != latestSelectedIndex) {
                                            latestOnIndexChange(index)
                                        }
                                        bubbleOffset = index * tabWidthPx
                                    } else {
                                        val targetIndex =
                                            (bubbleOffset / tabWidthPx)
                                                .roundToInt()
                                                .coerceIn(
                                                    0,
                                                    latestItemsSize - 1
                                                )
                                        bubbleOffset =
                                            targetIndex * tabWidthPx
                                        if (targetIndex != latestSelectedIndex) {
                                            latestOnIndexChange(targetIndex)
                                        }
                                    }
                                    isDragging = false
                                    break
                                }

                                val dx = change.positionChange().x
                                totalDx += dx

                                if (!dragged &&
                                    abs(totalDx) > viewConfiguration.touchSlop
                                ) {
                                    dragged = true
                                    isDragging = true
                                }

                                if (dragged) {
                                    bubbleOffset =
                                        (bubbleOffset + dx)
                                            .coerceIn(0f, maxOffset)
                                    change.consume()

                                    val currentIdx =
                                        (bubbleOffset / tabWidthPx)
                                            .roundToInt()
                                            .coerceIn(
                                                0,
                                                latestItemsSize - 1
                                            )
                                    if (currentIdx != latestSelectedIndex) {
                                        latestOnIndexChange(currentIdx)
                                    }
                                }
                            }
                        }
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                    .width(NAV_TAB_WIDTH)
                    .fillMaxHeight()
                    .padding(6.dp)
                    .clip(RoundedCornerShape((NAV_HEIGHT - 12.dp) / 2))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            )

            Row(modifier = Modifier.fillMaxSize()) {
                items.forEachIndexed { index, item ->
                    val selected = index == selectedIndex
                    val tint = if (selected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant

                    Box(
                        modifier = Modifier
                            .width(NAV_TAB_WIDTH)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                tint = tint,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                item.label,
                                fontSize = 14.sp,
                                fontWeight = if (selected)
                                    FontWeight.SemiBold
                                else
                                    FontWeight.Normal,
                                color = tint
                            )
                        }
                    }
                }
            }
        }
    }
}

// ===== 記帳頁 =====
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    expandedId: String?,
    onExpandChange: (String?) -> Unit,
    onQuickInputClick: (String) -> Unit,
    onCopyClick: (Record) -> Unit,
    onEditClick: (Record) -> Unit,
    onDeleteClick: (Record) -> Unit,
    onChangeIconClick: (Record) -> Unit,
    showKeyboard: Boolean,
    keyboardState: KeyboardState,
    onKeyboardStateChange: (KeyboardState) -> Unit,
    onKeyboardDismiss: () -> Unit,
    onKeyboardConfirm: () -> Unit,
    onKeyboardNext: () -> Unit,
    onKeyboardPickCategory: () -> Unit,
) {
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {
        // ===== 篩選模式下頂部顯示類別 / 月份 chips =====
        AnimatedVisibility(
            visible = filterMode,
            enter = fadeIn(tween(220)) + expandVertically(
                animationSpec = tween(250),
                expandFrom = Alignment.Top
            ),
            exit = fadeOut(tween(180)) + shrinkVertically(
                animationSpec = tween(220),
                shrinkTowards = Alignment.Top
            )
        ) {
            Column {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    AnimatedFilterChip(
                        selected = filterCategory == null,
                        label = "全部",
                        onClick = { onFilterCategoryChange(null) }
                    )
                    CATEGORIES.forEach { cat ->
                        AnimatedFilterChip(
                            selected = filterCategory == cat,
                            label = cat,
                            onClick = {
                                onFilterCategoryChange(
                                    if (filterCategory == cat) null else cat
                                )
                            }
                        )
                    }
                }
                if (availableMonths.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        AnimatedFilterChip(
                            selected = filterMonth == null,
                            label = "全年",
                            onClick = { onFilterMonthChange(null) }
                        )
                        availableMonths.forEach { month ->
                            AnimatedFilterChip(
                                selected = filterMonth == month,
                                label = formatMonthLabel(month),
                                onClick = {
                                    onFilterMonthChange(
                                        if (filterMonth == month) null
                                        else month
                                    )
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                HorizontalDivider()
            }
        }

        when {
            loading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            filtered.isEmpty() && !showKeyboard -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (filterMode) "冇符合篩選條件嘅記錄"
                    else "仲未有記錄,撳右下角 + 新增"
                )
            }

            else -> {
                TopStats(
                    hasIncome = hasIncome,
                    hasExpense = hasExpense,
                    income = totalIncome,
                    expense = totalExpense
                )

                AnimatedVisibility(
                    visible = !filterMode && topNotes.isNotEmpty(),
                    enter = fadeIn(tween(220)),
                    exit = fadeOut(tween(180))
                ) {
                    Column {
                        QuickInputSection(
                            topNotes = topNotes,
                            noteIconMap = noteIconMap,
                            onClick = onQuickInputClick
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                if (showKeyboard) {
                    LedgerKeyboardPanel(
                        state = keyboardState,
                        onStateChange = onKeyboardStateChange,
                        onDismiss = onKeyboardDismiss,
                        onConfirm = onKeyboardConfirm,
                        onNext = onKeyboardNext,
                        onPickCategory = onKeyboardPickCategory,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(
                            bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING +
                                if (filterMode) 90.dp else 20.dp
                        )
                    ) {
                        groupedByDate.forEach { (dateKey, dayRecords) ->
                            val dayIncome = dayRecords.sumOf {
                                if (it.category == INCOME_CATEGORY) it.amount
                                else 0.0
                            }
                            val dayExpense = dayRecords.sumOf {
                                if (it.category != INCOME_CATEGORY) it.amount
                                else 0.0
                            }

                            item(key = "header_$dateKey") {
                                DayHeader(
                                    dateKey = dateKey,
                                    income = dayIncome,
                                    expense = dayExpense
                                )
                            }

                            itemsIndexed(
                                dayRecords,
                                key = { _, r -> r.id }
                            ) { idx, r ->
                                SwipeableRecordItem(
                                    modifier = Modifier.animateItem(),
                                    backgroundColor = if (idx % 2 == 0)
                                        MaterialTheme.colorScheme.surface
                                    else
                                        ROW_ALT_COLOR,
                                    record = r,
                                    expandedId = expandedId,
                                    onExpand = onExpandChange,
                                    onCopy = { onCopyClick(r) },
                                    onEdit = { onEditClick(r) },
                                    onFilter = {},
                                    onDelete = { onDeleteClick(r) },
                                    onChangeIcon = { onChangeIconClick(r) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ===== 比較頁 =====
@Composable
fun CompareContent(
    records: List<Record>,
    availableMonths: List<String>,
) {
    var monthA by remember { mutableStateOf<String?>(null) }
    var monthB by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(availableMonths) {
        if (monthA == null || monthA !in availableMonths) {
            monthA = availableMonths.getOrNull(0)
        }
        if (monthB == null || monthB !in availableMonths) {
            monthB = availableMonths.getOrNull(1)
                ?: availableMonths.getOrNull(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            "月份比較",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MonthDropdown(
                label = "月份 A",
                value = monthA,
                months = availableMonths,
                onChange = { monthA = it },
                modifier = Modifier.weight(1f)
            )
            MonthDropdown(
                label = "月份 B",
                value = monthB,
                months = availableMonths,
                onChange = { monthB = it },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "類別",
                modifier = Modifier.weight(1.2f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                monthA?.let { formatMonthLabel(it) } ?: "-",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.End
            )
            Text(
                monthB?.let { formatMonthLabel(it) } ?: "-",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.End
            )
            Text(
                "差異",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.End
            )
        }
        HorizontalDivider()

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 20.dp
            )
        ) {
            items(EXPENSE_CATEGORIES) { cat ->
                val amtA = if (monthA != null)
                    sumByCategoryAndMonth(records, cat, monthA!!)
                else 0.0
                val amtB = if (monthB != null)
                    sumByCategoryAndMonth(records, cat, monthB!!)
                else 0.0
                val diff = amtB - amtA

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        cat,
                        modifier = Modifier.weight(1.2f),
                        fontSize = 15.sp
                    )
                    Text(
                        formatAmount(amtA),
                        modifier = Modifier.weight(1f),
                        fontSize = 15.sp,
                        color = COLOR_EXPENSE,
                        textAlign = TextAlign.End
                    )
                    Text(
                        formatAmount(amtB),
                        modifier = Modifier.weight(1f),
                        fontSize = 15.sp,
                        color = COLOR_EXPENSE,
                        textAlign = TextAlign.End
                    )
                    Text(
                        (if (diff > 0) "+" else "") + formatAmount(diff),
                        modifier = Modifier.weight(1f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (diff > 0) COLOR_EXPENSE
                                else if (diff < 0) COLOR_INCOME
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End
                    )
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                        .copy(alpha = 0.15f)
                )
            }

            item {
                val totalA = if (monthA != null)
                    EXPENSE_CATEGORIES.sumOf {
                        sumByCategoryAndMonth(records, it, monthA!!)
                    }
                else 0.0
                val totalB = if (monthB != null)
                    EXPENSE_CATEGORIES.sumOf {
                        sumByCategoryAndMonth(records, it, monthB!!)
                    }
                else 0.0
                val totalDiff = totalB - totalA

                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant
                                .copy(alpha = 0.4f)
                        )
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "總計",
                        modifier = Modifier.weight(1.2f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        formatAmount(totalA),
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = COLOR_EXPENSE,
                        textAlign = TextAlign.End
                    )
                    Text(
                        formatAmount(totalB),
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = COLOR_EXPENSE,
                        textAlign = TextAlign.End
                    )
                    Text(
                        (if (totalDiff > 0) "+" else "") +
                            formatAmount(totalDiff),
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (totalDiff > 0) COLOR_EXPENSE
                                else if (totalDiff < 0) COLOR_INCOME
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

fun sumByCategoryAndMonth(
    records: List<Record>,
    category: String,
    month: String
): Double =
    records
        .filter {
            it.category == category &&
            monthKeyFromTimestamp(it.timestamp) == month
        }
        .sumOf { it.amount }

@Composable
fun MonthDropdown(
    label: String,
    value: String?,
    months: List<String>,
    onChange: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    label,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    value?.let { formatMonthLabel(it) } ?: "未選擇",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            months.forEach { m ->
                DropdownMenuItem(
                    text = { Text(formatMonthLabel(m)) },
                    onClick = {
                        onChange(m)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ===== 篩選頁（保留） =====
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterContent(
    records: List<Record>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    filterCategory: String?,
    onFilterCategoryChange: (String?) -> Unit,
    filterMonth: String?,
    onFilterMonthChange: (String?) -> Unit,
    availableMonths: List<String>,
    onCopyClick: (Record) -> Unit,
    onEditClick: (Record) -> Unit,
    onDeleteClick: (Record) -> Unit,
    onChangeIconClick: (Record) -> Unit,
) {
    var expandedId by remember { mutableStateOf<String?>(null) }

    val query = searchQuery.trim()
    val results = records.filter { r ->
        val catOk = filterCategory == null || r.category == filterCategory
        val monthOk = filterMonth == null ||
            monthKeyFromTimestamp(r.timestamp) == filterMonth
        val searchOk = query.isBlank() ||
            r.note.contains(query, ignoreCase = true) ||
            r.category.contains(query, ignoreCase = true)
        catOk && monthOk && searchOk
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            Text(
                text = if (query.isBlank()) "全部記錄（${results.size}）"
                       else "搜尋結果（${results.size}）",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.onSurface
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                AnimatedFilterChip(
                    selected = filterCategory == null,
                    label = "全部",
                    onClick = { onFilterCategoryChange(null) }
                )
                CATEGORIES.forEach { cat ->
                    AnimatedFilterChip(
                        selected = filterCategory == cat,
                        label = cat,
                        onClick = {
                            onFilterCategoryChange(
                                if (filterCategory == cat) null else cat
                            )
                        }
                    )
                }
            }

            if (availableMonths.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    AnimatedFilterChip(
                        selected = filterMonth == null,
                        label = "全年",
                        onClick = { onFilterMonthChange(null) }
                    )
                    availableMonths.forEach { month ->
                        AnimatedFilterChip(
                            selected = filterMonth == month,
                            label = formatMonthLabel(month),
                            onClick = {
                                onFilterMonthChange(
                                    if (filterMonth == month) null else month
                                )
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            HorizontalDivider()

            if (results.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        when {
                            query.isNotBlank() -> "搵唔到「$query」"
                            filterCategory != null || filterMonth != null ->
                                "冇符合篩選條件嘅記錄"
                            else -> "冇記錄"
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(
                        bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 80.dp
                    )
                ) {
                    itemsIndexed(results, key = { _, r -> r.id }) { idx, r ->
                        SwipeableRecordItem(
                            modifier = Modifier.animateItem(),
                            backgroundColor = if (idx % 2 == 0)
                                MaterialTheme.colorScheme.surface
                            else
                                ROW_ALT_COLOR,
                            record = r,
                            expandedId = expandedId,
                            onExpand = { expandedId = it },
                            onCopy = { onCopyClick(r) },
                            onEdit = { onEditClick(r) },
                            onFilter = {},
                            onDelete = { onDeleteClick(r) },
                            onChangeIcon = { onChangeIconClick(r) }
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = NAV_HEIGHT + NAV_BOTTOM_PADDING + 12.dp
                ),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 6.dp,
            tonalElevation = 3.dp
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("搜尋名稱或類別…") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "清除"
                            )
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ===== 記帳鍵盤 =====
@Composable
fun LedgerKeyboardPanel(
    state: KeyboardState,
    onStateChange: (KeyboardState) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onNext: () -> Unit,
    onPickCategory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp, top = 8.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
                    .copy(alpha = 0.5f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(14.dp))
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    val showText = if (state.amountText.isEmpty()) "0"
                                   else state.amountText
                    Text(
                        text = showText,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.amountText.isEmpty())
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf(".", "0", "backspace")
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                rows.forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        row.forEach { key ->
                            KeyboardKey(
                                label = key,
                                onClick = {
                                    when (key) {
                                        "backspace" -> {
                                            val t = state.amountText
                                            onStateChange(
                                                state.copy(
                                                    amountText = if (t.isEmpty())
                                                        t else t.dropLast(1)
                                                )
                                            )
                                        }
                                        "." -> {
                                            val t = state.amountText
                                            if (t.contains(".")) return@KeyboardKey
                                            val newT = if (t.isEmpty()) "0."
                                                       else "$t."
                                            onStateChange(
                                                state.copy(amountText = newT)
                                            )
                                        }
                                        else -> {
                                            val t = state.amountText
                                            val dotIdx = t.indexOf(".")
                                            val newT = if (dotIdx >= 0) {
                                                if (t.length - dotIdx - 1 >= 2) t
                                                else "$t$key"
                                            } else {
                                                if (t.length >= 9) t
                                                else "$t$key"
                                            }
                                            onStateChange(
                                                state.copy(amountText = newT)
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.editingNote) {
                    var tfValue by remember(state.editingNote) {
                        mutableStateOf(
                            TextFieldValue(
                                text = state.noteText,
                                selection = TextRange(
                                    0, state.noteText.length
                                )
                            )
                        )
                    }
                    val noteFocusRequester = remember { FocusRequester() }
                    LaunchedEffect(Unit) {
                        noteFocusRequester.requestFocus()
                    }
                    TextField(
                        value = tfValue,
                        onValueChange = { newValue ->
                            tfValue = newValue
                            onStateChange(
                                state.copy(noteText = newValue.text)
                            )
                        },
                        placeholder = { Text("名稱") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onStateChange(
                                    state.copy(editingNote = false)
                                )
                            }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .focusRequester(noteFocusRequester)
                    )
                } else {
                    OutlinedButton(
                        onClick = {
                            onStateChange(state.copy(editingNote = true))
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = if (state.noteText.isBlank())
                                "輸入名稱"
                            else
                                state.noteText,
                            maxLines = 1,
                            fontSize = 16.sp,
                            color = if (state.noteText.isBlank())
                                MaterialTheme.colorScheme.onSurfaceVariant
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                OutlinedButton(
                    onClick = onPickCategory,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .width(110.dp)
                        .fillMaxHeight()
                ) {
                    Text(state.category, maxLines = 1, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("收起", fontSize = 16.sp)
                }
                Button(
                    onClick = {
                        if (state.noteText.isBlank()) {
                            onNext()
                        } else {
                            onConfirm()
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight(),
                    enabled = state.amountText.isNotEmpty() &&
                        state.amountText != "0"
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        when {
                            state.noteText.isBlank() -> "下一步"
                            state.editingRecordId != null -> "更新"
                            else -> "入帳"
                        },
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun KeyboardKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "keyScale"
    )

    val bgColor by animateColorAsState(
        targetValue = if (pressed)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "keyBg"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (label == "backspace") {
            Icon(
                Icons.Default.Backspace,
                contentDescription = "退格",
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = label,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedFilterChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "chipScale"
    )

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        interactionSource = interactionSource,
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    )
}

// ===== 快速輸入 =====
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickInputSection(
    topNotes: List<Pair<String, Int>>,
    noteIconMap: Map<String, String>,
    onClick: (String) -> Unit
) {
    if (topNotes.isEmpty()) return

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val availWidth = maxWidth - 32.dp
        val chipEstimate = 110.dp
        val perRow = max(1, (availWidth / chipEstimate).toInt())
        val perPage = perRow * 4
        val pageCount = max(1, (topNotes.size + perPage - 1) / perPage)
        val pagerState = rememberPagerState { pageCount }

        Column {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) { page ->
                val start = page * perPage
                val end = minOf(start + perPage, topNotes.size)
                if (start >= end) return@HorizontalPager

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    maxItemsInEachRow = perRow,
                    maxLines = 4
                ) {
                    for (i in start until end) {
                        val name = topNotes[i].first
                        SuggestionChip(
                            onClick = { onClick(name) },
                            label = { Text(name) },
                            icon = {
                                IconView(
                                    iconUrl = noteIconMap[name] ?: "",
                                    name = name,
                                    size = 22.dp
                                )
                            }
                        )
                    }
                }
            }

            if (pageCount > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pageCount) { index ->
                        val active = index == pagerState.currentPage
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (active) 7.dp else 5.dp)
                                .clip(CircleShape)
                                .background(
                                    if (active)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                            .copy(alpha = 0.3f)
                                )
                        )
                    }
                }
            }
        }
    }
}

// ===== 頂部統計（只顯示收入或支出） =====
@Composable
fun TopStats(
    hasIncome: Boolean,
    hasExpense: Boolean,
    income: Double,
    expense: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top
    ) {
        AnimatedVisibility(
            visible = hasIncome,
            enter = fadeIn(tween(220)) + expandHorizontally(
                animationSpec = tween(260)
            ),
            exit = fadeOut(tween(180)) + shrinkHorizontally(
                animationSpec = tween(220)
            )
        ) {
            StatColumn(
                icon = Icons.Default.TrendingUp,
                label = "收入",
                amountText = formatAmount(income),
                color = COLOR_INCOME
            )
        }
        AnimatedVisibility(
            visible = hasExpense,
            enter = fadeIn(tween(220)) + expandHorizontally(
                animationSpec = tween(260)
            ),
            exit = fadeOut(tween(180)) + shrinkHorizontally(
                animationSpec = tween(220)
            )
        ) {
            StatColumn(
                icon = Icons.Default.TrendingDown,
                label = "支出",
                amountText = formatAmount(expense),
                color = COLOR_EXPENSE
            )
        }
    }
}

@Composable
fun StatColumn(
    icon: ImageVector,
    label: String,
    amountText: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = STAT_LABEL_FONT_SIZE,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(4.dp))
        AnimatedAmount(
            text = amountText,
            color = color,
            fontSize = STAT_AMOUNT_FONT_SIZE,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AnimatedAmount(
    text: String,
    color: Color,
    fontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Bold
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        text.forEachIndexed { index, c ->
            AnimatedContent(
                targetState = c,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInVertically { it } + fadeIn()) togetherWith
                            (slideOutVertically { -it } + fadeOut())
                    } else {
                        (slideInVertically { -it } + fadeIn()) togetherWith
                            (slideOutVertically { it } + fadeOut())
                    }
                },
                label = "digit_$index"
            ) { char ->
                Text(
                    text = char.toString(),
                    color = color,
                    fontSize = fontSize,
                    fontWeight = fontWeight
                )
            }
        }
    }
}

// ===== 日期分組標題 =====
@Composable
fun DayHeader(dateKey: String, income: Double, expense: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatDateHeader(dateKey),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (income > 0) {
                Icon(
                    Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = COLOR_INCOME,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text = formatAmount(income),
                    fontSize = 13.sp,
                    color = COLOR_INCOME,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (income > 0 && expense > 0) {
                Spacer(Modifier.width(12.dp))
            }
            if (expense > 0) {
                Icon(
                    Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = COLOR_EXPENSE,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text = formatAmount(expense),
                    fontSize = 13.sp,
                    color = COLOR_EXPENSE,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun IconSourceOption(
    icon: ImageVector,
    label: String,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            label,
            color = tint,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// ===== 建立拍照用嘅臨時檔案 URI =====
fun createTempImageUri(context: Context): Uri {
    val file = File.createTempFile("camera_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

// ===== 壓縮圖片 =====
suspend fun compressImage(
    context: Context,
    uri: Uri,
    maxSize: Int = ICON_SIZE
): ByteArray? = withContext(Dispatchers.IO) {
    try {
        val boundsOpts = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, boundsOpts)
        }
        val w = boundsOpts.outWidth
        val h = boundsOpts.outHeight
        if (w <= 0 || h <= 0) return@withContext null

        var sample = 1
        val minDim = minOf(w, h)
        while (minDim / (sample * 2) >= maxSize) {
            sample *= 2
        }

        val decodeOpts = BitmapFactory.Options().apply {
            inSampleSize = sample
        }
        val src = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, decodeOpts)
        } ?: return@withContext null

        val scaled = scaleCropCenter(src, maxSize)
        if (scaled !== src) src.recycle()

        val baos = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        scaled.recycle()
        baos.toByteArray()
    } catch (e: Exception) {
        Log.e("Ledger", "compress error", e)
        null
    }
}

fun scaleCropCenter(src: Bitmap, size: Int): Bitmap {
    val w = src.width
    val h = src.height
    val minDim = minOf(w, h)
    val x = (w - minDim) / 2
    val y = (h - minDim) / 2

    val cropped = if (x == 0 && y == 0 && w == minDim && h == minDim) {
        src
    } else {
        Bitmap.createBitmap(src, x, y, minDim, minDim)
    }

    val scaled = if (cropped.width == size && cropped.height == size) {
        cropped
    } else {
        Bitmap.createScaledBitmap(cropped, size, size, true)
    }

    if (cropped !== src && cropped !== scaled) cropped.recycle()
    return scaled
}

// ===== 上傳圖片 =====
suspend fun uploadIconAndApplyToSameName(
    context: Context,
    db: FirebaseFirestore,
    recordId: String,
    recordName: String,
    uri: Uri
) {
    try {
        val bytes = compressImage(context, uri)
        if (bytes == null) {
            Toast.makeText(context, "讀取圖片失敗", Toast.LENGTH_LONG).show()
            return
        }

        val imageUrl = uploadBytesToCloudinary(bytes)
        if (imageUrl == null) {
            Toast.makeText(context, "上傳失敗,請檢查網絡", Toast.LENGTH_LONG).show()
            return
        }

        applyUrlToSameName(context, db, recordName, imageUrl)
    } catch (e: Exception) {
        Log.e("Ledger", "upload failed", e)
        Toast.makeText(context, "失敗:${e.message}", Toast.LENGTH_LONG).show()
    }
}

suspend fun applyUrlToSameName(
    context: Context,
    db: FirebaseFirestore,
    recordName: String,
    imageUrl: String
) {
    try {
        if (recordName.isBlank()) return
        val snapshot = db.collection("records")
            .whereEqualTo("note", recordName)
            .get()
            .await()

        val batch = db.batch()
        snapshot.documents.forEach { doc ->
            batch.update(doc.reference, "iconUrl", imageUrl)
        }
        batch.commit().await()

        val count = snapshot.size()
        Toast.makeText(
            context,
            if (count > 1) "圖標已套用到 $count 條同名記錄"
            else "圖標已更新",
            Toast.LENGTH_SHORT
        ).show()
    } catch (e: Exception) {
        Log.e("Ledger", "apply url failed", e)
        Toast.makeText(context, "失敗:${e.message}", Toast.LENGTH_LONG).show()
    }
}

suspend fun removeIconFromSameName(
    context: Context,
    db: FirebaseFirestore,
    recordName: String
) {
    try {
        if (recordName.isBlank()) {
            Toast.makeText(context, "冇名稱,無法刪除", Toast.LENGTH_SHORT).show()
            return
        }
        val snapshot = db.collection("records")
            .whereEqualTo("note", recordName)
            .get()
            .await()

        val batch = db.batch()
        snapshot.documents.forEach { doc ->
            batch.update(doc.reference, "iconUrl", "")
        }
        batch.commit().await()

        val count = snapshot.size()
        Toast.makeText(
            context,
            if (count > 1) "已刪除 $count 條同名記錄嘅圖標"
            else "圖標已刪除",
            Toast.LENGTH_SHORT
        ).show()
    } catch (e: Exception) {
        Log.e("Ledger", "remove icon failed", e)
        Toast.makeText(context, "失敗:${e.message}", Toast.LENGTH_LONG).show()
    }
}

suspend fun uploadBytesToCloudinary(bytes: ByteArray): String? =
    withContext(Dispatchers.IO) {
        try {
            val endpoint =
                "https://api.cloudinary.com/v1_1/$CLOUDINARY_CLOUD_NAME/image/upload"
            val boundary = "----LedgerBoundary${System.currentTimeMillis()}"
            val lineEnd = "\r\n"

            val conn = URL(endpoint).openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.connectTimeout = 30_000
            conn.readTimeout = 60_000
            conn.setRequestProperty(
                "Content-Type",
                "multipart/form-data; boundary=$boundary"
            )

            conn.outputStream.use { output ->
                output.write("--$boundary$lineEnd".toByteArray())
                output.write(
                    ("Content-Disposition: form-data; " +
                        "name=\"upload_preset\"$lineEnd$lineEnd").toByteArray()
                )
                output.write("$CLOUDINARY_UPLOAD_PRESET$lineEnd".toByteArray())

                output.write("--$boundary$lineEnd".toByteArray())
                output.write(
                    ("Content-Disposition: form-data; " +
                        "name=\"file\"; filename=\"icon.jpg\"$lineEnd").toByteArray()
                )
                output.write("Content-Type: image/jpeg$lineEnd$lineEnd".toByteArray())
                output.write(bytes)
                output.write("$lineEnd".toByteArray())
                output.write("--$boundary--$lineEnd".toByteArray())
                output.flush()
            }

            val code = conn.responseCode
            val text = if (code in 200..299) {
                conn.inputStream.bufferedReader().use { it.readText() }
            } else {
                conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            }

            if (code in 200..299) {
                JSONObject(text).optString("secure_url").takeIf { it.isNotBlank() }
            } else {
                Log.e("Ledger", "Cloudinary $code: $text")
                null
            }
        } catch (e: Exception) {
            Log.e("Ledger", "Cloudinary upload error", e)
            null
        }
    }

@Composable
fun IconView(iconUrl: String, name: String, size: Dp = 40.dp) {
    if (iconUrl.isBlank()) {
        val firstChar = name.trim().take(1).ifBlank { "?" }
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(avatarColor(name)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = firstChar,
                color = Color.White,
                fontSize = (size.value * 0.42f).sp,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        AsyncImage(
            model = iconUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
        )
    }
}

@Composable
fun SwipeableRecordItem(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    record: Record,
    expandedId: String?,
    onExpand: (String?) -> Unit,
    onCopy: () -> Unit,
    onEdit: () -> Unit,
    onFilter: () -> Unit,
    onDelete: () -> Unit,
    onChangeIcon: () -> Unit,
) {
    val density = LocalDensity.current
    val buttonWidth = 56.dp
    val buttonHeight = 44.dp
    val gap = 6.dp
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

    LaunchedEffect(expandedId) {
        if (expandedId != record.id && targetOffset != 0f) {
            targetOffset = 0f
        }
    }

    val offsetX by animateFloatAsState(
        targetValue = targetOffset,
        animationSpec = if (isDragging) {
            snap<Float>()
        } else {
            spring<Float>(
                stiffness = Spring.StiffnessMediumLow,
                dampingRatio = Spring.DampingRatioNoBouncy
            )
        },
        label = "swipe"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(
                gap, Alignment.End
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                Icons.Default.ContentCopy, "複制", Color(0xFF607D8B),
                buttonWidth, buttonHeight
            ) { targetOffset = 0f; onExpand(null); onCopy() }
            ActionButton(
                Icons.Default.Edit, "編輯", Color(0xFF2196F3),
                buttonWidth, buttonHeight
            ) { targetOffset = 0f; onExpand(null); onEdit() }
            ActionButton(
                Icons.Default.FilterList, "篩選", Color(0xFF9C27B0),
                buttonWidth, buttonHeight
            ) { targetOffset = 0f; onExpand(null); onFilter() }
            ActionButton(
                Icons.Default.Delete, "刪除", Color(0xFFF44336),
                buttonWidth, buttonHeight
            ) { targetOffset = 0f; onExpand(null); onDelete() }
        }

        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(start = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(
                gap, Alignment.Start
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                Icons.Default.Image, "改圖標", Color(0xFF4CAF50),
                buttonWidth, buttonHeight
            ) { targetOffset = 0f; onExpand(null); onChangeIcon() }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(record.id) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            isDragging = true
                            onExpand(record.id)
                        },
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
                        onDragCancel = {
                            isDragging = false
                            targetOffset = 0f
                            onExpand(null)
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            targetOffset = (targetOffset + dragAmount)
                                .coerceIn(maxLeftReveal, maxRightReveal)
                        }
                    )
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (expandedId != null) {
                        onExpand(null)
                    }
                },
            color = backgroundColor
        ) {
            ListItem(
                leadingContent = {
                    IconView(record.iconUrl, record.note)
                },
                headlineContent = {
                    Text(
                        text = record.note.ifBlank { "(無名稱)" },
                        fontSize = NOTE_FONT_SIZE,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                supportingContent = {
                    Text(
                        text = "${record.category}．${formatRecordTime(record.timestamp)}",
                        fontSize = META_FONT_SIZE,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                            .copy(alpha = 0.7f)
                    )
                },
                trailingContent = {
                    Text(
                        text = displayAmount(record),
                        color = amountColor(record.category),
                        fontSize = AMOUNT_FONT_SIZE,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    background: Color,
    width: Dp,
    height: Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}
