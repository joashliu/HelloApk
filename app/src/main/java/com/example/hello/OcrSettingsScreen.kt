package com.example.hello 

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun OcrSettingsScreen() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("ledger_prefs", Context.MODE_PRIVATE)
    
    // 檢查 Android 版本，低於 12 隱藏
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S) {
        Text("此功能僅支援 Android 12 或以上機型。", modifier = Modifier.padding(16.dp))
        return
    }

    var a11yEnabled by remember { mutableStateOf(prefs.getBoolean("a11y_feature_enabled", false)) }
    var floatingEnabled by remember { mutableStateOf(prefs.getBoolean("floating_enabled", false)) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("畫面擷取與記帳助手", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(Modifier.padding(16.dp)) {
                Text("🛡️ 私隱與安全說明", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("1. 本功能採用 Google ML Kit 進行完全離線嘅文字識別。")
                Text("2. 螢幕截取只會喺你授權同點擊時影一張快照，分析完畢即刻銷毀。")
                Text("3. 無障礙服務僅用於偵測支付 App 嘅通知，絕不會主動讀取你嘅私人對話或畫面內容。")
                Text("4. 絕無後台持續錄屏，所有數據絕不上傳。")
            }
        }

        Spacer(Modifier.height(24.dp))

        // 無障礙軟開關
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text("啟用通知智能捕捉", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text("收到付款通知時自動觸發截圖詢問", style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked = a11yEnabled,
                onCheckedChange = { 
                    a11yEnabled = it
                    prefs.edit().putBoolean("a11y_feature_enabled", it).apply()
                    // 提示用戶去系統設定開權限
                    if (it) {
                        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    }
                }
            )
        }
        
        Spacer(Modifier.height(16.dp))

        // 懸浮按鈕開關
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text("顯示懸浮截圖按鈕", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text("隨時手動點擊截取畫面金額", style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked = floatingEnabled,
                onCheckedChange = { 
                    floatingEnabled = it
                    prefs.edit().putBoolean("floating_enabled", it).apply()
                    if (it) {
                        if (Settings.canDrawOverlays(context)) {
                            FloatingButtonManager.show(context)
                        } else {
                            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                            context.startActivity(intent)
                        }
                    } else {
                        FloatingButtonManager.hide()
                    }
                }
            )
        }
    }
}
