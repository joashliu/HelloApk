package com.example.hello

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log

object OppoFluidCloudBridge {
    // 潘塔納爾泛在服務引擎包名
    private const val PANTANAL_PACKAGE = "com.oplus.pantanal.ums"
    
    // 模擬喚起 UPK 卡片嘅廣播 / 服務 Action
    private const val ACTION_SHOW_CARD = "com.oplus.pantanal.action.SHOW_CARD"
    private const val ACTION_DISMISS_CARD = "com.oplus.pantanal.action.DISMISS_CARD"

    /**
     * 檢查當前手機是否支援 OPPO 流体云/潘塔納爾框架
     */
    fun isPantanalSupported(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(PANTANAL_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * 檢查是否已授予泛在服務卡片顯示權限
     */
    fun hasPantanalPermission(context: Context): Boolean {
        // 實際開發中，需依賴 OPPO SDK 提供的 API 檢查 (如 CardManager)
        // 此處模擬：若系統有安裝引擎即假設支援，但用戶可能喺系統設定閂咗
        return isPantanalSupported(context)
    }

    /**
     * 喚起流体云卡片 (Type: immediate, Style: notification_sm)
     * @return true 代表喚起指令發送成功；false 代表不支援，需降級
     */
    fun triggerFluidCloud(
        context: Context,
        note: String,
        amount: Double,
        category: String,
        timestamp: Long
    ): Boolean {
        if (!isPantanalSupported(context)) {
            Log.d("OppoFluidCloud", "⚠️ 當前設備不支援流体云，降級使用普通通知")
            return false
        }
        
        try {
            // 為該次消費生成唯一卡片 ID
            val cardId = "ledger_card_${timestamp % 10000}"
            
            val intent = Intent(ACTION_SHOW_CARD).apply {
                setPackage(PANTANAL_PACKAGE)
                // 傳遞畀 UPK 嘅參數 (UPK 負責渲染 UI)
                putExtra("upk_id", "com.example.hello.upk") 
                putExtra("card_id", cardId)
                putExtra("type", "immediate")
                putExtra("view_type", "notification_sm")
                putExtra("param_note", note)
                putExtra("param_amount", amount)
                putExtra("param_category", category)
                
                // 🟢 核心交互：設定 UPK 點擊「新增記帳」後回傳畀 APK 嘅 DeepLink
                val addUri = "ledgerapp://quickadd?note=${Uri.encode(note)}&amount=$amount&category=${Uri.encode(category)}&card_id=$cardId"
                putExtra("action_add", addUri)
            }
            
            // 透過廣播或服務通知引擎顯示卡片
            context.sendBroadcast(intent)
            
            Log.d("OppoFluidCloud", "✅ 成功向流体云引擎發送顯示卡片指令: $cardId")
            return true
        } catch (e: Exception) {
            Log.d("OppoFluidCloud", "❌ 喚起流体云失敗: ${e.message}")
            return false
        }
    }

    /**
     * 記帳成功後，通知引擎銷毀指定流体云卡片
     */
    fun dismissCard(context: Context, cardId: String) {
        if (!isPantanalSupported(context)) return
        try {
            val intent = Intent(ACTION_DISMISS_CARD).apply {
                setPackage(PANTANAL_PACKAGE)
                putExtra("card_id", cardId)
            }
            context.sendBroadcast(intent)
            Log.d("OppoFluidCloud", "🗑 已通知流体云銷毀卡片: $cardId")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
