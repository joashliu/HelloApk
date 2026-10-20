package com.example.hello

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class PaymentNotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "PaymentNotif"

        // 🟢 唔再限制邊個 App，任何 App 嘅通知都會處理
        // （如果你想日後再加白名單，喺 onNotificationPosted 內加 if 判斷即可）

        // 🟢 修正：同時支援 HKD / HK$ / $ 三種寫法，支援大小寫
        private val AMOUNT_PATTERN = Regex(
            """(?:HKD|HK\$|\$)\s*(\d+(?:\.\d{1,2})?)""",
            RegexOption.IGNORE_CASE
        )

        // 🟢 必須包含以下至少一個關鍵字，避免促銷 / 廣告通知誤觸發
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

        // 🟢 調試用：每次收到通知都 log 低，方便排查
        Log.d(TAG, "收到通知 | pkg=$pkg | 內容=$combined")

        if (combined.isBlank()) return

        // 關鍵詞過濾
        if (DEDUCT_KEYWORDS.none { combined.contains(it, ignoreCase = true) }) {
            Log.d(TAG, "跳過：無消費關鍵字")
            return
        }

        // 金額解析
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

        // 名稱判斷
        val note = when {
            combined.contains("地鐵", true) || combined.contains("港鐵", true)
                || combined.contains("易乘碼", true) -> "地鐵"
            title.isNotBlank() && title.length <= 20 -> title
            else -> "消費"
        }

        // 分類判斷
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

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // 唔需要處理
    }
}
