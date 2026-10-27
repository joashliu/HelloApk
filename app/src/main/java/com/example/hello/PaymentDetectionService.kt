package com.example.hello

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent // 🟢 補返呢個 import
import android.view.accessibility.AccessibilityEvent

class PaymentDetectionService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // App 內軟開關，用戶喺 App 閂咗就即刻 Return
        val prefs = getSharedPreferences("ledger_prefs", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("a11y_feature_enabled", false)) return

        // 只監聽通知狀態改變
        if (event?.eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            val pkg = event.packageName?.toString() ?: return
            val text = event.text.joinToString(" ")
            
            // 例如偵測 AlipayHK 或 八達通 嘅扣費通知
            if (pkg.contains("alipay") || pkg.contains("octopus")) {
                if (text.contains("付款") || text.contains("扣費") || text.contains("成功")) {
                    // 觸發截圖流程
                    val intent = Intent(this, ScreenCaptureRequestActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
        }
    }

    override fun onInterrupt() {}
}
