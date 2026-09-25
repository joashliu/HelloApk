package com.example.hello

import android.content.Context
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
import androidx.compose.material.icons.filled.ReceiptLong
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
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt

val CATEGORIES = listOf(
    "收入", "娛樂", "家用", "飲食", "交通",
    "個人", "購物", "月費", "旅遊"
)

val INCOME_CATEGORY = "收入"

val COLOR_INCOME = Color(0xFF1B5E20)
val COLOR_EXPENSE = Color(0xFFB71C1C)

// ===== Cloudinary 設定 =====
const val CLOUDINARY_CLOUD_NAME = "dfl59grn"
const val CLOUDINARY_UPLOAD_PRESET = "ledger_icons"

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

fun formatAmount(amount: Double): String =
    String.format("%,.1f", amount)

fun displayAmount(record: Record): String =
    if (record.category == INCOME_CATEGORY) formatAmount(record.amount)
    else formatAmount(-record.amount)

fun amountColor(category: String): Color =
    if (category == INCOME_CATEGORY) COLOR_INCOME else COLOR_EXPENSE

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
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        val target = iconTargetRecord
        if (uri != null && target != null) {
            scope.launch {
                uploadIcon(context, db, target.id, uri)
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
                uploadIcon(context, db, target.id, uri)
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

    Scaffold(
        topBar = { TopAppBar(title = { Text("記帳") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                dialogState = DialogState.Add()
            }) {
                Icon(Icons.Default.Add, contentDescription = "新增")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
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
                                    SuggestionChip(
                                        onClick = {
                                            dialogState = DialogState.Add(
                                                initialNote = pair.first
                                            )
                                        },
                                        label = { Text(pair.first) }
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider()
                    }

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filtered, key = { it.id }) { r ->
                            SwipeableRecordItem(
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
                    db.collection("records").add(
                        Record(
                            amount = amount,
                            note = note,
                            category = category
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
        AlertDialog(
            onDismissRequest = {
                showIconSourceDialog = false
                iconTargetRecord = null
            },
            title = { Text("選擇圖標來源") },
            text = { Text("揀相冊入面嘅相,定係即時影一張?") },
            confirmButton = {
                TextButton(onClick = {
                    showIconSourceDialog = false
                    pickImageLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }) { Text("相冊") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showIconSourceDialog = false
                    val uri = createTempImageUri(context)
                    pendingCameraUri = uri
                    takePictureLauncher.launch(uri)
                }) { Text("拍照") }
            }
        )
    }
}

// ===== 建立拍照用嘅臨時檔案 URI =====
fun createTempImageUri(context: Context): Uri {
    val file = File.createTempFile(
        "camera_",
        ".jpg",
        context.cacheDir
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

// ===== 上傳圖標到 Cloudinary,再更新 Firestore =====
suspend fun uploadIcon(
    context: Context,
    db: FirebaseFirestore,
    recordId: String,
    uri: Uri
) {
    try {
        val imageUrl = withContext(Dispatchers.IO) {
            uploadToCloudinary(context, uri)
        }

        if (imageUrl == null) {
            Toast.makeText(
                context,
                "上傳失敗,請檢查網絡",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        db.collection("records").document(recordId)
            .update("iconUrl", imageUrl)
            .await()

        Toast.makeText(context, "圖標已更新", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Log.e("Ledger", "上傳圖標失敗", e)
        Toast.makeText(
            context,
            "上傳失敗:${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

// ===== 打 Cloudinary API 上傳圖片,回傳 secure_url =====
suspend fun uploadToCloudinary(
    context: Context,
    uri: Uri
): String? = withContext(Dispatchers.IO) {
    try {
        val endpoint =
            "https://api.cloudinary.com/v1_1/$CLOUDINARY_CLOUD_NAME/image/upload"
        val boundary = "----LedgerBoundary${System.currentTimeMillis()}"
        val lineEnd = "\r\n"

        val connection = URL(endpoint).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.connectTimeout = 30_000
        connection.readTimeout = 60_000
        connection.setRequestProperty(
            "Content-Type",
            "multipart/form-data; boundary=$boundary"
        )

        connection.outputStream.use { output ->
            // upload_preset 欄位
            output.write("--$boundary$lineEnd".toByteArray())
            output.write(
                "Content-Disposition: form-data; name=\"upload_preset\"$lineEnd$lineEnd"
                    .toByteArray()
            )
            output.write("$CLOUDINARY_UPLOAD_PRESET$lineEnd".toByteArray())

            // file 欄位
            output.write("--$boundary$lineEnd".toByteArray())
            output.write(
                "Content-Disposition: form-data; name=\"file\"; filename=\"icon.jpg\"$lineEnd"
                    .toByteArray()
            )
            output.write("Content-Type: image/jpeg$lineEnd$lineEnd".toByteArray())

            context.contentResolver.openInputStream(uri)?.use { input ->
                input.copyTo(output)
            } ?: run {
                Log.e("Ledger", "開唔到圖片 stream")
                return@withContext null
            }

            output.write("$lineEnd".toByteArray())
            output.write("--$boundary--$lineEnd".toByteArray())
            output.flush()
        }

        val responseCode = connection.responseCode
        val responseText = if (responseCode in 200..299) {
            connection.inputStream.bufferedReader().use { it.readText() }
        } else {
            connection.errorStream?.bufferedReader()?.use { it.readText() }
                ?: ""
        }

        if (responseCode in 200..299) {
            val json = JSONObject(responseText)
            json.optString("secure_url").takeIf { it.isNotBlank() }
        } else {
            Log.e("Ledger", "Cloudinary $responseCode: $responseText")
            null
        }
    } catch (e: Exception) {
        Log.e("Ledger", "Cloudinary upload error", e)
        null
    }
}

@Composable
fun IconView(iconUrl: String, size: Dp = 40.dp) {
    if (iconUrl.isBlank()) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.ReceiptLong,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(size * 0.55f)
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
        modifier = Modifier
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
                    leadingContent = { IconView(record.iconUrl) },
                    headlineContent = {
                        Text(record.note.ifBlank { "(無名稱)" })
                    },
                    supportingContent = { Text(record.category) },
                    trailingContent = {
                        Text(
                            text = displayAmount(record),
                            color = amountColor(record.category),
                            fontSize = 20.sp,
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
