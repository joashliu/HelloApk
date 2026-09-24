package com.example.hello

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

data class Record(
    val amount: Double = 0.0,
    val note: String = "",
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

    // 即時監聽 Firestore 變化
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
            when {
                loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                records.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("仲未有記錄,撳右下角 + 新增")
                    }
                }
                else -> {
                    val total = records.sumOf { it.amount }
                    Text(
                        text = "總數:$total",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(records, key = { it.id }) { r ->
                            ListItem(
                                headlineContent = {
                                    Text(r.note.ifBlank { "(無備註)" })
                                },
                                trailingContent = { Text(r.amount.toString()) }
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
            onConfirm = { amount, note ->
                val record = Record(amount = amount, note = note)
                db.collection("records")
                    .add(record)
                    .addOnSuccessListener {
                        Log.d("Ledger", "新增成功")
                    }
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
    onConfirm: (Double, String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新增記錄") },
        text = {
            Column {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("金額") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amt = amountText.toDoubleOrNull()
                if (amt != null) onConfirm(amt, noteText)
            }) { Text("確定") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
