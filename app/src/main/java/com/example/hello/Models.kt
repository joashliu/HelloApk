package com.example.hello

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 🟢 啟動狀態：等 Firestore 第一次回傳先收 Splash
object AppStartupState {
    @Volatile var isFirstDataReady = false
}

// ===== 供通知顯示嘅最近項目資訊 =====
@Immutable
data class QuickNoteInfo(
    val note: String,
    val category: String,
    val amount: Double
)

// ===== 最近捕獲嘅手機消費通知（例如：地鐵 · 4.9） =====
@Immutable
data class CapturedNotification(
    val note: String,
    val amount: Double,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)

// ===== 配色（泥黃色風格）=====
val BRAND_PRIMARY = Color(0xFFB68D40)       // 泥黃色 (主色)
val BRAND_PRIMARY_DARK = Color(0xFF8B6A2A)  // 深泥黃色 (用於按壓或強烈對比)
val BRAND_PRIMARY_LIGHT = Color(0xFFF5EFE6) // 淺泥黃底色 (用於淡色標籤或背景)

// 頂部（TopStats／快速輸入）滑入 同 鍵盤展開：統一時長，同步完成
const val HEADER_KEYBOARD_EXPAND_DELAY_MS = 30L
const val HEADER_KEYBOARD_EXPAND_MS = 480
const val HEADER_KEYBOARD_COLLAPSE_MS = 420

// iOS systemGroupedBackground / secondarySystemGroupedBackground
val SURFACE_BG = Color(0xFFF2F2F7)
val SURFACE_CARD = Color(0xFFFFFFFF)
val SURFACE_ELEVATED = Color(0xFFE9E9EF)

// iOS label / secondaryLabel / tertiaryLabel
val TEXT_PRIMARY = Color(0xFF2A2B33)
val TEXT_SECONDARY = Color(0xFF8E8E93)
val TEXT_TERTIARY = Color(0xFFC7C7CC)
val DIVIDER_COLOR = Color(0xFFE5E5EA)

val COLOR_INCOME = Color(0xFF047857)
val COLOR_EXPENSE = Color(0xFFB91C1C)

val CELL_BG_POS = Color(0xFFD1FAE5)
val CELL_BAR_POS = Color(0xFF34D399)
val CELL_BG_NEG = Color(0xFFFEE2E2)
val CELL_BAR_NEG = Color(0xFFF87171)

const val CLOUDINARY_CLOUD_NAME = "dfl59grn"
const val CLOUDINARY_UPLOAD_PRESET = "ledger_icons"
const val ICON_SIZE = 100

val NOTE_FONT_SIZE = 16.sp
val META_FONT_SIZE = 12.sp
val AMOUNT_FONT_SIZE = 18.sp
val STAT_AMOUNT_FONT_SIZE = 22.sp
val STAT_LABEL_FONT_SIZE = 11.sp
val NAV_HEIGHT = 60.dp
val NAV_TAB_WIDTH = 96.dp
val NAV_BOTTOM_PADDING = 20.dp
val ROW_ALT_COLOR = Color(0xFFF4F7FC)
const val FILTER_ANIM_MS = 250

@Immutable
data class CategoryStyle(val icon: ImageVector, val bgColor: Color, val fgColor: Color)

val CATEGORIES = listOf("收入", "娛樂", "家用", "飲食", "交通", "個人", "購物", "月費", "旅遊", "還錢")
val EXPENSE_CATEGORIES = CATEGORIES.filter { it != "收入" }
const val INCOME_CATEGORY = "收入"

val CATEGORY_STYLES: Map<String, CategoryStyle> = mapOf(
    "收入" to CategoryStyle(Icons.Default.TrendingUp, Color(0xFFD1FAE5), Color(0xFF065F46)),
    "娛樂" to CategoryStyle(Icons.Default.SportsEsports, Color(0xFFEDE9FE), Color(0xFF5B21B6)),
    "家用" to CategoryStyle(Icons.Default.Home, Color(0xFFCFFAFE), Color(0xFF155E75)),
    "飲食" to CategoryStyle(Icons.Default.Restaurant, Color(0xFFFFEDD5), Color(0xFF9A3412)),
    "交通" to CategoryStyle(Icons.Default.DirectionsBus, Color(0xFFDBEAFE), Color(0xFF1E40AF)),
    "個人" to CategoryStyle(Icons.Default.Person, Color(0xFFFCE7F3), Color(0xFF9D174D)),
    "購物" to CategoryStyle(Icons.Default.ShoppingCart, Color(0xFFFEF3C7), Color(0xFF854D0E)),
    "月費" to CategoryStyle(Icons.Default.CreditCard, Color(0xFFE2E8F0), Color(0xFF334155)),
    "旅遊" to CategoryStyle(Icons.Default.Flight, Color(0xFFCCFBF1), Color(0xFF115E59)),
    "還錢" to CategoryStyle(Icons.Default.Payments, Color(0xFFFFE4E6), Color(0xFFBE123C))
)

val AVATAR_COLORS = listOf(
    Color(0xFFE57373), Color(0xFFF06292), Color(0xFFBA68C8), Color(0xFF9575CD),
    Color(0xFF7986CB), Color(0xFF64B5F6), Color(0xFF4FC3F7), Color(0xFF4DB6AC),
    Color(0xFF81C784), Color(0xFFAED581), Color(0xFFFFB74D), Color(0xFFFF8A65),
    Color(0xFFA1887F), Color(0xFF90A4AE)
)

@Immutable
data class Record(
    val amount: Double = 0.0,
    val note: String = "",
    val category: String = "飲食",
    val timestamp: Long = System.currentTimeMillis(),
    val iconUrl: String = "",
    val id: String = ""
)

@Immutable
data class NavItem(val label: String, val icon: ImageVector)

@Immutable
data class KeyboardState(
    val amountText: String = "",
    val noteText: String = "",
    val category: String = "飲食",
    val editingNote: Boolean = false,
    val editingRecordId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val selectAmountOnInput: Boolean = false,
    val currency: String = "HKD",
    val exchangeRateText: String = "0.052",
    val focusedField: String = "amount"
)

@Immutable
data class AfterSaveHint(
    val recordId: String,
    val category: String,
    val monthTotal: Double
)

@Immutable
data class DateHeaderInfo(
    val year: Int,
    val month: Int,
    val day: Int,
    val weekdayChar: String,
    val isWeekend: Boolean,
    val dayTag: String?
)

@Immutable
data class FilterState(
    val modeOn: Boolean = false,
    val category: String? = null,
    val month: String? = null,
    val search: String = "",
    val showFuture: Boolean = false,
    val startTimeMin: Int = 0,
    val endTimeMin: Int = 1440,
)

@Immutable
data class LedgerUiState(
    val loading: Boolean = true,
    val appliedFilterMode: Boolean = false,
    val appliedFilterCategory: String? = null,
    val appliedFilterMonth: String? = null,
    val ledgerRecords: List<Record> = emptyList(),
    val groupedByDate: List<Pair<String, List<Record>>> = emptyList(),
    val hasIncome: Boolean = false,
    val hasExpense: Boolean = false,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val isExactNoteFilter: Boolean = false,
    val topNotes: List<Pair<String, Int>> = emptyList(),
    val recentAmountByNote: Map<String, Double> = emptyMap(),
    val noteIconMap: Map<String, String> = emptyMap(),
    val allNoteNames: List<String> = emptyList(),
    val noteCategoryMap: Map<String, String> = emptyMap(),
    val noteUsageCount: Map<String, Int> = emptyMap(),
    val availableMonths: List<String> = emptyList(),
    val visibleCategories: List<String> = emptyList(),
    val topQuickNotesInfo: List<QuickNoteInfo> = emptyList(),
    val hiddenQuickNotes: Set<String> = emptySet(),
)

@Immutable
data class NoteIndex(
    val recentAmountByNote: Map<String, Double> = emptyMap(),
    val noteIconMap: Map<String, String> = emptyMap(),
    val allNoteNames: List<String> = emptyList(),
    val noteCategoryMap: Map<String, String> = emptyMap(),
    val noteUsageCount: Map<String, Int> = emptyMap(),
    val topNotes: List<Pair<String, Int>> = emptyList(),
)

@Immutable
data class CompareUiState(
    val categoryMonthSum: Map<String, Map<String, Double>> = emptyMap(),
    val sortedCategories: List<String> = emptyList(),
    val displayCategories: List<String> = emptyList(),
    val incomeA: Double = 0.0,
    val incomeB: Double = 0.0,
    val totalExpenseA: Double = 0.0,
    val totalExpenseB: Double = 0.0,
    val balanceA: Double = 0.0,
    val balanceB: Double = 0.0,
    val histogramData: Map<String, List<Double>> = emptyMap(),
    val monthlyKeysThisYear: List<String> = emptyList(),
)

@Immutable
data class CalendarUiState(
    val monthKey: String = "",
    val monthAllRecords: List<Record> = emptyList(),
    val visibleCategories: List<String> = emptyList(),
    val monthRecords: List<Record> = emptyList(),
    val recordsByDay: Map<Int, List<Record>> = emptyMap(),
    val maxAbsNet: Double = 0.0,
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val totalNet: Double = 0.0,
    val itemCount: Int = 0,
    val avgPerItem: Double = 0.0,
    val avgPerDay: Double = 0.0,
)

@Immutable
data class PivotCell(
    val total: Double = 0.0,
    val count: Int = 0
)

@Immutable
data class PivotRow(
    val category: String,
    val note: String,
    val isCategory: Boolean,
    val cells: Map<String, PivotCell> = emptyMap(),
    val rowTotal: Double = 0.0,
)

@Immutable
data class PivotUiState(
    val months: List<String> = emptyList(),
    val rows: List<PivotRow> = emptyList(),
    val monthTotals: Map<String, Double> = emptyMap(),
    val grandTotal: Double = 0.0,
    val loading: Boolean = true,
    val availableYears: List<Int> = emptyList(),
    val selectedYear: Int = 0,
    val categories: List<String> = emptyList(),
    val noteIconMap: Map<String, String> = emptyMap(),
)

@Immutable
data class WeeklyExpenseDay(
    val dayOfMonth: Int,
    val weekdayChar: String,
    val amount: Double,
    val isToday: Boolean
)

@Immutable
internal data class TimestampMeta(
    val year: Int,
    val month: Int,
    val dayOfMonth: Int,
    val dayOfWeek: Int,
    val hour: Int,
    val minute: Int,
    val secOfDay: Int,
    val startOfDayMillis: Long,
    val dateKey: String,
    val monthKey: String,
    val datePart: String,
    val timePart: String,
    val weekTimePrefix: String
)
