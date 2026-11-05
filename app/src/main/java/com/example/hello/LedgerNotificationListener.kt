package com.example.hello

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class LedgerNotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "LedgerNotifListener"
        
        // 萬能金額 Regex：食晒 HKD, HK$, $ (例如 HKD6.00, hk$4.90, $10)
        private val AMOUNT_PATTERN = Regex("""(?:HKD|HK\$|\$)\s*(\d+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE)
        
        // 一般消費關鍵字（只係用嚟做保底，有指定商戶就唔使理佢）
        private val DEDUCT_KEYWORDS = listOf(
            "扣款", "已付款", "付款", "消費", "交易", "易乘碼", "地鐵", "港鐵", "入閘", "出閘",
            "transaction", "payment", "paid"
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val extras = sbn.notification?.extras ?: return
        
        val ticker = sbn.notification?.tickerText?.toString().orEmpty()
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()

        val combined = "$ticker $title $text $bigText"
        if (combined.isBlank()) return

        // 🎯 第一步：全句掃描搵金額 (搵到 HKD6.00 就會抽出 6.00)
        val match = AMOUNT_PATTERN.find(combined)
        val amount = match?.groupValues?.getOrNull(1)?.toDoubleOrNull() ?: 0.0

        // 如果連金額都冇，或者金額唔合理，就肯定唔係消費通知，直接飛走
        if (amount <= 0.0 || amount > 5000.0) return

        // 🎯 第二步：優先檢查「商戶白名單」 (無視任何扣款關鍵字！)
        // 只要句嘢有「McDonalds」加埋頭先搵到嘅金額，即刻無條件彈出嚟！
        if (combined.contains("McDonalds", ignoreCase = true) || combined.contains("麥當勞", ignoreCase = true)) {
            Log.d(TAG, "🔥 觸發直通車：麥當勞 · $amount")
            NotificationCaptureBridge.pushCaptured("麥當勞", amount, "飲食")
            return
        }
        
        if (combined.contains("KFC", ignoreCase = true)) {
            Log.d(TAG, "🔥 觸發直通車：KFC · $amount")
            NotificationCaptureBridge.pushCaptured("KFC", amount, "飲食")
            return
        }

        // ==========================================
        // 🎯 第三步：如果唔係白名單商戶，先用一般關鍵字過濾 (做保底)
        // ==========================================
        if (DEDUCT_KEYWORDS.none { combined.contains(it, ignoreCase = true) }) {
            return // 冇「付款」、「transaction」等字眼，當佢係普通短訊/新聞
        }

        // 歸納名稱
        val note = when {
            combined.contains("易乘碼", ignoreCase = true) -> "地鐵"
            combined.contains("AEON", ignoreCase = true) -> "AEON消費" // 保底名
            title.isNotBlank() && title.length <= 20 -> title
            else -> "消費"
        }

        // 分類映射
        val category = when {
            note == "地鐵" || combined.contains("港鐵", true) -> "交通"
            combined.contains("餐", true) || combined.contains("食", true) -> "飲食"
            combined.contains("超市", true) || combined.contains("店", true) -> "購物"
            else -> "個人"
        }

        Log.d(TAG, "捕獲一般通知：$note · $amount · $category → $combined")
        NotificationCaptureBridge.pushCaptured(note, amount, category)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
    }
}
