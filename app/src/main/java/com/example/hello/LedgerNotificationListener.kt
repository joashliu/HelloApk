package com.example.hello

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class LedgerNotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "LedgerNotifListener"
        
        // 終極金額 Regex：支援 HKD, HK$, $, 並且忽略大小寫 (例如 HKD6.00, hk$4.90, $10)
        private val AMOUNT_PATTERN = Regex("""(?:HKD|HK\$|\$)\s*(\d+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE)
        
        // 終極關鍵字：加入英文扣款字眼 (transaction, payment 等) 同埋 AEON
        private val DEDUCT_KEYWORDS = listOf(
            "扣款", "已付款", "付款", "消費", "交易", "易乘碼", "地鐵", "港鐵", "入閘", "出閘",
            "transaction", "payment", "paid", "aeon"
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val extras = sbn.notification?.extras ?: return
        
        // 攞晒所有文字出嚟 (測試 App 好多時會將字塞落 Ticker 度)
        val ticker = sbn.notification?.tickerText?.toString().orEmpty()
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()

        // 將所有字夾埋一齊，確保唔會漏
        val combined = "$ticker $title $text $bigText"
        if (combined.isBlank()) return

        // 1. 檢查有冇扣款/交易字眼 (中英都得，冇就飛走)
        if (DEDUCT_KEYWORDS.none { combined.contains(it, ignoreCase = true) }) {
            return
        }

        // 2. 搵金額 (成功搵到 HKD6.00 就會抽出 6.00)
        val match = AMOUNT_PATTERN.find(combined) ?: return
        val amount = match.groupValues.getOrNull(1)?.toDoubleOrNull() ?: return

        // 金額太細或太大就過濾走
        if (amount <= 0.0 || amount > 5000.0) return

        // 3. 判斷商戶名稱
        val note = when {
            combined.contains("McDonalds", ignoreCase = true) -> "麥當勞"
            combined.contains("KFC", ignoreCase = true) -> "KFC"
            // 如果係易乘碼
            combined.contains("易乘碼", ignoreCase = true) -> "地鐵"
            // 如果係 AEON，嘗試抽 transaction 同 at amount 之間嘅字
            combined.contains("AEON", ignoreCase = true) -> {
                val merchantRegex = """transaction\s+(.*?)\s+at amount""".toRegex(RegexOption.IGNORE_CASE)
                merchantRegex.find(combined)?.groupValues?.get(1)?.trim() ?: "AEON消費"
            }
            title.isNotBlank() && title.length <= 20 -> title
            else -> "消費"
        }

        // 4. 自動分類
        val category = when {
            note == "地鐵" || combined.contains("港鐵", true) -> "交通"
            note == "麥當勞" || note == "KFC" || combined.contains("餐", true) || combined.contains("食", true) -> "飲食"
            combined.contains("超市", true) || combined.contains("店", true) -> "購物"
            else -> "個人"
        }

        Log.d(TAG, "成功捕獲：$note · $amount · $category → $combined")
        NotificationCaptureBridge.pushCaptured(note, amount, category)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
    }
}
