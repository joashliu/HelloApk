package com.example.hello

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class LedgerNotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "LedgerNotifListener"
        
        // 擷取一般消費金額嘅 Regex 規則 (例如 "HK$4.90" 或 "$100")
        private val AMOUNT_PATTERN = Regex("""(?:HK\$|\$)\s*(\d+(?:\.\d{1,2})?)""")
        
        // 觸發一般記帳嘅關鍵字（包括易乘碼）
        private val DEDUCT_KEYWORDS = listOf(
            "扣款", "已付款", "付款", "消費", "交易", "易乘碼", "地鐵", "港鐵", "入閘", "出閘"
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val extras = sbn.notification?.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString().orEmpty()

        // 將所有可能出現文字嘅欄位夾埋一齊，確保唔會漏
        val combined = "$title $text $bigText $subText"
        if (combined.isBlank()) return

        // ==========================================
        // 1. AEON 短訊 / 任何 App 通知特定處理
        // ==========================================
        // 只要句嘢同時有 "AEON" 同 "We received a transaction" 就當係，唔使理有冇冒號
        if (combined.contains("AEON", ignoreCase = true) && combined.contains("We received a transaction", ignoreCase = true)) {
            
            // 使用 \s+ 代替普通空格，就算 SMS 隱藏咗換行符號 (Newline) 都一定捉得中
            val aeonRegex = """transaction\s+(.*?)\s+at amount HKD\s*([0-9.]+)""".toRegex(RegexOption.IGNORE_CASE)
            val match = aeonRegex.find(combined)

            if (match != null) {
                val rawMerchant = match.groupValues[1].trim()
                val amount = match.groupValues[2].toDoubleOrNull() ?: 0.0

                if (amount > 0.0) {
                    // 自動轉中文名
                    val noteName = if (rawMerchant.contains("McDonalds", ignoreCase = true)) {
                        "麥當勞"
                    } else {
                        rawMerchant
                    }
                    // 根據名稱設定分類
                    val category = if (noteName == "麥當勞") "飲食" else "個人"

                    Log.d(TAG, "捕獲 AEON 通知：$noteName · $amount · $category")
                    NotificationCaptureBridge.pushCaptured(noteName, amount, category)
                    return // 處理完 AEON 就直接 Return，結束
                }
            } else {
                // 除錯用：如果中咗關鍵字但 Regex 抽唔到嘢，可以喺 Logcat 睇返系統到底俾咗咩字串你
                Log.d(TAG, "AEON Regex 匹配失敗，原本通知字串係: $combined")
            }
        }

        // ==========================================
        // 2. 一般消費處理邏輯 (掃描所有通知，例如易乘碼)
        // ==========================================
        // 冇扣款關鍵字就跳過（避免誤觸一般聊天或新聞通知）
        if (DEDUCT_KEYWORDS.none { combined.contains(it, ignoreCase = true) }) return

        val match = AMOUNT_PATTERN.find(combined) ?: return
        val amount = match.groupValues.getOrNull(1)?.toDoubleOrNull() ?: return
        
        // 金額太細或太大就過濾走
        if (amount <= 0.0 || amount > 5000.0) return

        // 歸納名稱：優先用通知標題 (Title)，否則預設叫「消費」
        val note = when {
            title.isNotBlank() && title.length <= 20 -> title
            else -> "消費"
        }

        // 分類映射 (按字眼自動估計)
        val category = when {
            combined.contains("地鐵", true) || combined.contains("港鐵", true)
                || combined.contains("易乘碼", true) -> "交通"
            combined.contains("餐", true) || combined.contains("食", true) -> "飲食"
            combined.contains("超市", true) || combined.contains("店", true) -> "購物"
            else -> "個人"
        }

        Log.d(TAG, "捕獲通知：$note · $amount · $category → $combined")
        NotificationCaptureBridge.pushCaptured(note, amount, category)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // 唔需要處理通知移除事件
    }
}
