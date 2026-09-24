package com.example.hello

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

// 9 個類別,順序跟住你俾我嗰個
val CATEGORIES = listOf(
    "收入", "娛樂", "家用", "飲食", "交通",
    "個人", "購物", "月費", "旅遊"
)

data class Record(
    val amount: Double = 0.0,
    val note: String = "",
    val category: String = "飲食",
    val timestamp: Long = System.currentTimeMillis(),
    var id: String = ""
)

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
    val records = remember { mutableStateListOf<Record>() }
    var showDialog by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // 即時監聽 Firestore
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
                        if (r != null) {
                            r.id = doc.id
                            records.add(r)
                        }
                    }
                }
            }
        onDispose { listener.remove() }
    }

    // 根據揀咗嘅類別篩選
    val filtered = if (selectedCategory == null) records
                   else records.filter { it.category == selectedCategory }

    Scaffold(
        topBar = { TopAppBar(title = { Text("記帳") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "新增")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 頂部類別篩選列
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
                loading -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                filtered.isEmpty() -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("仲未有記錄,撳右下角 + 新增")
                    }
                }
                else -> {
                    val total = filtered.sumOf { it.amount }
                    Text(
                        text = "總數:$total",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filtered, key = { it.id }) { r ->
                            ListItem(
                                headlineContent = {
                                    Text(r.note.ifBlank { "(無備註)" })
                                },
                                supportingContent = {
                                    Text(r.category)
                                },
                                trailingContent = {
                                    Text(r.amount.toString())
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddDialog(
            onDismiss = { showDialog = false },
            onConfirm = { amount, note, category ->
                val record = Record(
                    amount = amount,
                    note = note,
                    category = category
                )
                db.collection("records")
                    .add(record)
                    .addOnSuccessListener { Log.d("Ledger", "新增成功") }
                    .addOnFailureListener { e ->
                        Log.w("Ledger", "新增失敗", e)
                    }
                showDialog = false
            }
        )
    }
}

@Composable
fun AddDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double, String, String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("飲食") }
    var showCategoryPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新增記錄") },
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
                Text(
                    "類別",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(Modifier.height(4.dp))
                OutlinedButton(
                    onClick = { showCategoryPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(category)
                }
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
                        Text(
                            cat,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
