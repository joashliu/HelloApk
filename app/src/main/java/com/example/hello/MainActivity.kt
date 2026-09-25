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
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
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
import kotlin.math.roundToInt

// ===== 常數 =====
val CATEGORIES = listOf(
    "收入", "娛樂", "家用", "飲食", "交通",
    "個人", "購物", "月費", "旅遊"
)

val INCOME_CATEGORY = "收入"

val COLOR_INCOME = Color(0xFF1B5E20)
val COLOR_EXPENSE = Color(0xFFB71C1C)

const val CLOUDINARY_CLOUD_NAME = "dfl59grn"
const val CLOUDINARY_UPLOAD_PRESET = "ledger_icons"
const val ICON_SIZE = 100

// ===== 字體大小 =====
val NOTE_FONT_SIZE = 19.sp       // 項目名（原 16sp 大 1 號）
val META_FONT_SIZE = 13.sp       // 類別時間（原 14sp 細 1 號）
val AMOUNT_FONT_SIZE = 20.sp     // 金額

// ===== 資料模型 =====
data class Record(
    val amount: Double = 0.0,
    val note: String = "",
    val category: String = "飲食",
    val timestamp: Long = System.currentTimeMillis(),
    val iconUrl: String = "",
    var id: String = ""
)

sealed interface DialogState {
    data class Add(
        val title: String = "新增記錄",
        val initialNote: String = "",
        val initialAmount: String = "",
        val initialCategory: String = "飲食"
    ) : DialogState

    data class Edit(val record: Record) : DialogState
}

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
    val time = "$hh:$mm"

    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val recordStart = Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val daysDiff = ((todayStart - recordStart) / 86_400_000L).toInt()

    val relative = when {
        daysDiff <= 0 -> "今日"
        daysDiff == 1 -> "琴日"
        daysDiff == 2 -> "前日"
        else -> "${daysDiff}日前"
    }

    return "$week．$time．$relative"
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
                    LedgerScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerScreen() {
    val db = Firebase.firestore
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val records = remember { mutableStateListOf<Record>() }
    var dialogState by remember { mutableStateOf<DialogState?>(null) }
    var loading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var expandedId by remember { mutableStateOf<String?>(null) }

    var iconTargetRecord by remember { mutableStateOf<Record?>(null) }
    var showIconSourceDialog by remember { mutableStateOf(false) }
    var showUrlInputDialog by remember { mutableStateOf(false) }
    var urlInput by remember { mutableStateOf("") }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }

    // 列表滾動狀態 + 新增後自動滾頂
    val listState = rememberLazyListState()
    var pendingScrollToTop by remember { mutableStateOf(false) }

    // 當 records 數量增加,而且係新增後,滾返最頂
    LaunchedEffect(records.size) {
        if (pendingScrollToTop && records.isNotEmpty()) {
            // 等一拍先讓動畫播完,再滾到頂
            listState.animateScrollToItem(0)
            pendingScrollToTop = false
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

    val filtered = if (selectedCategory == null) records
                   else records.filter { it.category == selectedCategory }

    val totalIncome = filtered
        .filter { it.category == INCOME_CATEGORY }
        .sumOf { it.amount }
    val totalExpense = filtered
        .filter { it.category != INCOME_CATEGORY }
        .sumOf { it.amount }
    val balance = totalIncome - totalExpense

    val topNotes: List<Pair<String, Int>> = filtered
        .filter { it.note.isNotBlank() }
        .groupBy { it.note }
        .map { (name, list) -> name to list.size }
        .sortedByDescending { it.second }
        .take(20)

    val noteIconMap: Map<String, String> = filtered
        .filter { it.note.isNotBlank() && it.iconUrl.isNotBlank() }
        .groupBy { it.note }
        .mapValues { (_, list) ->
            list.maxByOrNull { it.timestamp }?.iconUrl ?: ""
        }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                dialogState = DialogState.Add()
            }) {
                Icon(Icons.Default.Add, contentDescription = "新增")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("全部") }
                        )
                    }
                    items(CATEGORIES) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = {
                                selectedCategory =
                                    if (selectedCategory == cat) null else cat
                            },
                            label = { Text(cat) }
                        )
                    }
                }

                when {
                    loading -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }

                    filtered.isEmpty() -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { Text("仲未有記錄,撳右下角 + 新增") }

                    else -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "餘額:${formatAmount(balance)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (balance >= 0) COLOR_INCOME
                                        else COLOR_EXPENSE
                            )
                            Text(
                                text = "收入:${formatAmount(totalIncome)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = COLOR_INCOME
                            )
                            Text(
                                text = "支出:${formatAmount(totalExpense)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = COLOR_EXPENSE
                            )
                        }

                        if (topNotes.isNotEmpty()) {
                            Column(Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = "快速輸入",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(4.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(topNotes) { pair ->
                                        val name = pair.first
                                        SuggestionChip(
                                            onClick = {
                                                dialogState = DialogState.Add(
                                                    initialNote = name
                                                )
                                            },
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
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider()
                        }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = filtered,
                                key = { it.id }
                            ) { r ->
                                SwipeableRecordItem(
                                    modifier = Modifier.animateItem(),
                                    record = r,
                                    expandedId = expandedId,
                                    onExpand = { expandedId = it },
                                    onCopy = {
                                        dialogState = DialogState.Add(
                                            title = "複製記錄",
                                            initialNote = r.note,
                                            initialAmount = r.amount.toString(),
                                            initialCategory = r.category
                                        )
                                    },
                                    onEdit = {
                                        dialogState = DialogState.Edit(r)
                                    },
                                    onFilter = {
                                        Toast.makeText(
                                            context,
                                            "篩選功能開發中",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onDelete = {
                                        db.collection("records")
                                            .document(r.id)
                                            .delete()
                                    },
                                    onChangeIcon = {
                                        iconTargetRecord = r
                                        showIconSourceDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }

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

    dialogState?.let { state ->
        when (state) {
            is DialogState.Add -> AddDialog(
                title = state.title,
                initialNote = state.initialNote,
                initialAmount = state.initialAmount,
                initialCategory = state.initialCategory,
                allRecords = records,
                onDismiss = { dialogState = null },
                onConfirm = { amount, note, category ->
                    val inheritedIcon = records
                        .filter { it.note == note && it.note.isNotBlank() }
                        .maxByOrNull { it.timestamp }
                        ?.iconUrl ?: ""
                    // 標記:新增完要滾返最頂
                    pendingScrollToTop = true
                    db.collection("records").add(
                        Record(
                            amount = amount,
                            note = note,
                            category = category,
                            iconUrl = inheritedIcon
                        )
                    )
                    dialogState = null
                }
            )
            is DialogState.Edit -> AddDialog(
                title = "編輯記錄",
                initialNote = state.record.note,
                initialAmount = state.record.amount.toString(),
                initialCategory = state.record.category,
                allRecords = records,
                onDismiss = { dialogState = null },
                onConfirm = { amount, note, category ->
                    val updated = state.record.copy(
                        amount = amount,
                        note = note,
                        category = category
                    )
                    db.collection("records")
                        .document(state.record.id)
                        .set(updated)
                    dialogState = null
                }
            )
        }
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

// ===== 壓縮圖片到 ICON_SIZE x ICON_SIZE =====
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

// ===== 上傳圖片並套用到所有同名項目 =====
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

    val leftTotalPx = buttonWidthPx * 4 + gapPx * 3
    val rightTotalPx = buttonWidthPx

    val maxLeftReveal = -leftTotalPx
    val maxRightReveal = rightTotalPx

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
            modifier = Modifier.matchParentSize(),
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
            modifier = Modifier.matchParentSize(),
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
                                targetOffset < maxLeftReveal / 2 -> maxLeftReveal
                                targetOffset > maxRightReveal / 2 -> maxRightReveal
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
        ) {
            Column {
                ListItem(
                    leadingContent = {
                        IconView(record.iconUrl, record.note)
                    },
                    headlineContent = {
                        // 項目名 - 大 1 號
                        Text(
                            text = record.note.ifBlank { "(無名稱)" },
                            fontSize = NOTE_FONT_SIZE,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    supportingContent = {
                        // 類別 + 時間 - 細 1 號 + 淺色
                        Text(
                            text = "${record.category}．${formatRecordTime(record.timestamp)}",
                            fontSize = META_FONT_SIZE,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingContent = {
                        Text(
                            text = displayAmount(record),
                            color = amountColor(record.category),
                            fontSize = AMOUNT_FONT_SIZE,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                )
                HorizontalDivider()
            }
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

@Composable
fun AddDialog(
    title: String = "新增記錄",
    initialNote: String = "",
    initialAmount: String = "",
    initialCategory: String = "飲食",
    allRecords: List<Record> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (Double, String, String) -> Unit
) {
    var amountText by remember(initialAmount) { mutableStateOf(initialAmount) }
    var noteText by remember(initialNote) { mutableStateOf(initialNote) }
    var category by remember(initialCategory) { mutableStateOf(initialCategory) }
    var showCategoryPicker by remember { mutableStateOf(false) }

    val amountFocusRequester = remember { FocusRequester() }
    val noteFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(noteText) {
        if (noteText.isNotBlank()) {
            val match = allRecords
                .filter { it.note == noteText }
                .maxByOrNull { it.timestamp }
            if (match != null) {
                category = match.category
            }
        }
    }

    LaunchedEffect(Unit) {
        amountFocusRequester.requestFocus()
    }

    val doSave: () -> Unit = {
        val amt = amountText.toDoubleOrNull()
        if (amt != null) {
            keyboardController?.hide()
            onConfirm(amt, noteText, category)
        }
    }

    val amountImeAction = if (noteText.isNotBlank()) ImeAction.Done
                          else ImeAction.Next

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("金額") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = amountImeAction
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { noteFocusRequester.requestFocus() },
                        onDone = { doSave() }
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(amountFocusRequester)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("名稱") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { doSave() }
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(noteFocusRequester)
                )
                Spacer(Modifier.height(12.dp))
                Text("類別", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                OutlinedButton(
                    onClick = { showCategoryPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(category) }
            }
        },
        confirmButton = {
            TextButton(onClick = { doSave() }) { Text("確定") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )

    if (showCategoryPicker) {
        CategoryPickerDialog(
            current = category,
            onDismiss = { showCategoryPicker = false },
            onSelect = {
                category = it
                showCategoryPicker = false
            }
        )
    }
}

@Composable
fun CategoryPickerDialog(
    current: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("揀類別") },
        text = {
            Column {
                CATEGORIES.forEach { cat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(cat) }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = cat == current,
                            onClick = { onSelect(cat) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(cat, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
