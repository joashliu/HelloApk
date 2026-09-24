package com.example.hello

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlin.math.roundToInt

val CATEGORIES = listOf(
    "收入", "娛樂", "家用", "飲食", "交通",
    "個人", "購物", "月費", "旅遊"
)

data class Record(
    val amount: Double = 0.0,
    val note: String = "",
    val category: String = "飲食",
    val timestamp: Long = System.currentTimeMillis(),
    @DocumentId var id: String = ""
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
    val records = remember { mutableStateListOf<Record>() }
    var dialogState by remember { mutableStateOf<DialogState?>(null) }
    var loading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        val listener = db.collection("records")
            .orderBy("timestamp")
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
                        if (r != null) records.add(r)
                    }
                }
            }
        onDispose { listener.remove() }
    }

    val filtered = if (selectedCategory == null) records
                   else records.filter { it.category == selectedCategory }

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
                    val total = filtered.sumOf { it.amount }
                    Text(
                        text = "總數:$total",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    )

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
                                        label = {
                                            Text("${pair.first} · ${pair.second}")
                                        }
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
                                onCopy = {
                                    val copy = r.copy(
                                        id = "",
                                        timestamp = System.currentTimeMillis()
                                    )
                                    db.collection("records").add(copy)
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
                                onQuickEdit = {
                                    dialogState = DialogState.Add(
                                        title = "新增類似記錄",
                                        initialNote = r.note,
                                        initialAmount = r.amount.toString(),
                                        initialCategory = r.category
                                    )
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
}

@Composable
fun SwipeableRecordItem(
    record: Record,
    onCopy: () -> Unit,
    onEdit: () -> Unit,
    onFilter: () -> Unit,
    onDelete: () -> Unit,
    onQuickEdit: () -> Unit,
) {
    val density = LocalDensity.current
    val actionWidth = 76.dp
    val actionWidthPx = with(density) { actionWidth.toPx() }

    val maxLeftReveal = -actionWidthPx * 4f
    val maxRightReveal = actionWidthPx * 1f

    var targetOffset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val offsetX by animateFloatAsState(
        targetValue = targetOffset,
        animationSpec = if (isDragging) snap() else spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioNoBouncy
        ),
        label = "swipe"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // 右側（向左滑顯示）：4 個按鈕
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
        ) {
            ActionButton(
                Icons.Default.ContentCopy, "複制", Color(0xFF607D8B)
            ) {
                targetOffset = 0f; onCopy()
            }
            ActionButton(
                Icons.Default.Edit, "編輯", Color(0xFF2196F3)
            ) {
                targetOffset = 0f; onEdit()
            }
            ActionButton(
                Icons.Default.FilterList, "篩選", Color(0xFF9C27B0)
            ) {
                targetOffset = 0f; onFilter()
            }
            ActionButton(
                Icons.Default.Delete, "刪除", Color(0xFFF44336)
            ) {
                targetOffset = 0f; onDelete()
            }
        }

        // 左側（向右滑顯示）：1 個按鈕
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
        ) {
            ActionButton(
                Icons.Default.Edit, "編輯/新增", Color(0xFF4CAF50)
            ) {
                targetOffset = 0f; onQuickEdit()
            }
        }

        // 上層：ListItem
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = {
                            isDragging = false
                            targetOffset = when {
                                targetOffset < maxLeftReveal / 2 -> maxLeftReveal
                                targetOffset > maxRightReveal / 2 -> maxRightReveal
                                else -> 0f
                            }
                        },
                        onDragCancel = {
                            isDragging = false
                            targetOffset = 0f
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
                    headlineContent = {
                        Text(record.note.ifBlank { "(無備註)" })
                    },
                    supportingContent = { Text(record.category) },
                    trailingContent = { Text(record.amount.toString()) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun RowScope.ActionButton(
    icon: ImageVector,
    label: String,
    background: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(76.dp)
            .fillMaxHeight()
            .background(background)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun AddDialog(
    title: String = "新增記錄",
    initialNote: String = "",
    initialAmount: String = "",
    initialCategory: String = "飲食",
    onDismiss: () -> Unit,
    onConfirm: (Double, String, String) -> Unit
) {
    var amountText by remember(initialAmount) { mutableStateOf(initialAmount) }
    var noteText by remember(initialNote) { mutableStateOf(initialNote) }
    var category by remember(initialCategory) { mutableStateOf(initialCategory) }
    var showCategoryPicker by remember { mutableStateOf(false) }

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
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("備註") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
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
            TextButton(onClick = {
                val amt = amountText.toDoubleOrNull()
                if (amt != null) onConfirm(amt, noteText, category)
            }) { Text("確定") }
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
