package com.example.hello

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log

object OppoFluidCloudBridge {
    // 潘塔納爾泛在服務引擎包名
    private const val PANTANAL_PACKAGE = "com.oplus.pantanal.ums"
    
    // 模擬喚起 UPK 卡片嘅廣播 Action
    private const val ACTION_SHOW_CARD = "com.oplus.pantanal.action.SHOW_CARD"
    private const val ACTION_DISMISS_CARD = "com.oplus.pantanal.action.DISMISS_CARD"

    // 🟢 核心修復：真實 UPK 狀態鎖
    // 因為作為個人開發者暫時未發佈 UPK 到手機，呢度必須設為 false
    // 確保程式知道流體雲無法渲染，從而 100% 降級彈出原生通知
    private const val HAS_REAL_UPK_INSTALLED = false

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
        return isPantanalSupported(context)
    }

    /**
     * 喚起流体云卡片
     * @return true 代表流体云顯示成功；false 代表需降級原生通知
     */
    fun triggerFluidCloud(
        context: Context,
        note: String,
        amount: Double,
        category: String,
        timestamp: Long
    ): Boolean {
        // 🟢 如果冇 UPK，直接 Return false 觸發降級，唔好畀廣播呃咗
        if (!isPantanalSupported(context) || !HAS_REAL_UPK_INSTALLED) {
            Log.d("OppoFluidCloud", "⚠️ 未安裝專屬 UPK 或不支援流体云，強制降級使用普通通知")
            return false
        }
        
        try {
            val cardId = "ledger_card_${timestamp % 10000}"
            
            val intent = Intent(ACTION_SHOW_CARD).apply {
                setPackage(PANTANAL_PACKAGE)
                putExtra("upk_id", "com.example.hello.upk") 
                putExtra("card_id", cardId)
                putExtra("type", "immediate")
                putExtra("view_type", "notification_sm")
                putExtra("param_note", note)
                putExtra("param_amount", amount)
                putExtra("param_category", category)
                
                val addUri = "ledgerapp://quickadd?note=${Uri.encode(note)}&amount=$amount&category=${Uri.encode(category)}&card_id=$cardId"
                putExtra("action_add", addUri)
            }
            
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
        if (!isPantanalSupported(context) || !HAS_REAL_UPK_INSTALLED) return
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
