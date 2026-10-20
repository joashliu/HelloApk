package com.example.hello

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import java.util.concurrent.ConcurrentHashMap

class PaymentNotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "PaymentNotif"

        // 🟢 通知級去重：10 秒內相同內容只處理一次
        private val recentKeys = ConcurrentHashMap<String, Long>(32)
        private const val DEDUP_WINDOW_MS = 10_000L

        private val AMOUNT_PATTERN = Regex(
            """(?:HKD|HK\$|\$)\s*(\d+(?:\.\d{1,2})?)""",
            RegexOption.IGNORE_CASE
        )

        private val DEDUCT_KEYWORDS = listOf(
            "扣款", "已付款", "付款", "消費", "交易",
            "易乘碼", "地鐵", "港鐵", "入閘", "出閘",
            "繳費", "轉賬", "支付"
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val pkg = sbn.packageName ?: "unknown"
        val extras = sbn.notification?.extras ?: return

        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString().orEmpty()

        val combined = "$title $text $bigText $subText".trim()
        Log.d(TAG, "收到通知 | pkg=$pkg | 內容=$combined")

        if (combined.isBlank()) return

        if (DEDUCT_KEYWORDS.none { combined.contains(it, ignoreCase = true) }) {
            Log.d(TAG, "跳過：無消費關鍵字")
            return
        }

        val match = AMOUNT_PATTERN.find(combined)
        if (match == null) {
            Log.d(TAG, "跳過：搵唔到金額")
            return
        }

        val amount = match.groupValues.getOrNull(1)?.toDoubleOrNull()
        if (amount == null || amount <= 0.0 || amount > 100000.0) {
            Log.d(TAG, "跳過：金額無效 ($amount)")
            return
        }

        // 🟢 通知級去重：用 pkg + 金額 + 文本 hash 做 key
        val contentKey = "$pkg|$amount|${combined.take(150)}"
        val now = System.currentTimeMillis()

        // 清理過期記錄，防止 map 無限增長
        if (recentKeys.size > 100) {
            recentKeys.entries.removeAll { it.value < now - DEDUP_WINDOW_MS * 2 }
        }

        val lastTime = recentKeys[contentKey]
        if (lastTime != null && (now - lastTime) < DEDUP_WINDOW_MS) {
            Log.d(TAG, "跳過重複通知 (${now - lastTime}ms 內): $contentKey")
            return
        }
        recentKeys[contentKey] = now

        val note = when {
            combined.contains("地鐵", true) || combined.contains("港鐵", true)
                || combined.contains("易乘碼", true) -> "地鐵"
            title.isNotBlank() && title.length <= 20 -> title
            else -> "消費"
        }

        val category = when {
            combined.contains("地鐵", true) || combined.contains("港鐵", true)
                || combined.contains("易乘碼", true) -> "交通"
            combined.contains("餐", true) || combined.contains("食", true)
                || combined.contains("飲", true) -> "飲食"
            combined.contains("超市", true) || combined.contains("店", true)
                || combined.contains("購物", true) -> "購物"
            combined.contains("繳費", true) || combined.contains("月費", true) -> "月費"
            else -> "個人"
        }

        Log.d(TAG, "✅ 捕獲成功：$note · $amount · $category（來自 $pkg）")
        NotificationCaptureBridge.pushCaptured(note, amount, category)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {}
}
