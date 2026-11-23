package com.example.hello

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar
import kotlin.math.abs

class LedgerViewModel(app: Application) : AndroidViewModel(app) {

    private val db by lazy { Firebase.firestore }
    private val prefs by lazy { app.getSharedPreferences("ledger_prefs", Context.MODE_PRIVATE) }

    private val recordsById = HashMap<String, Record>()
    private val ghostRecordsById = HashMap<String, Record>()

    @Volatile private var hasReceivedFirstSnapshot = false

    private val _lastSavedCategory = MutableStateFlow("飲食")
    val lastSavedCategory: StateFlow<String> = _lastSavedCategory.asStateFlow()

    private val _records = MutableStateFlow<List<Record>>(emptyList())
    val records: StateFlow<List<Record>> = _records.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _filter = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filter.asStateFlow()

    fun setFilterModeOn(v: Boolean)     { _filter.update { it.copy(modeOn = v) } }
    fun setFilterCategory(v: String?)   { _filter.update { it.copy(category = v) } }
    fun setFilterMonth(v: String?)      { _filter.update { it.copy(month = v) } }
    fun setFilterSearch(v: String)      { _filter.update { it.copy(search = v) } }
    fun setShowFuture(v: Boolean)       { _filter.update { it.copy(showFuture = v) } }

    fun setFilterTimeRange(startMin: Int, endMin: Int) {
        _filter.update { it.copy(startTimeMin = startMin, endTimeMin = endMin) }
    }

    fun filterByName(name: String) {
        _filter.update {
            it.copy(
                modeOn = true,
                category = null,
                month = null,
                search = name,
                startTimeMin = 0,
                endTimeMin = 1440
            )
        }
    }

    private val _justAddedIds = MutableStateFlow<Set<String>>(emptySet())
    val justAddedIds: StateFlow<Set<String>> = _justAddedIds.asStateFlow()

    private val _deletingIds = MutableStateFlow<Set<String>>(emptySet())
    val deletingIds: StateFlow<Set<String>> = _deletingIds.asStateFlow()

    private val _compareSnapshotRecords = MutableStateFlow<List<Record>>(emptyList())
    private val _isComparePageActive = MutableStateFlow(false)
    @Volatile private var hasInitializedCompareSnapshot = false

    fun onComparePageEnter() {
        _isComparePageActive.value = true
        if (!_loading.value) {
            _compareSnapshotRecords.value = _records.value.filter { it.category != "還錢" }
            hasInitializedCompareSnapshot = true
        }
    }

    fun onComparePageExit() {
        _isComparePageActive.value = false
    }

    private val _defaultMonthA = monthKeyFromTimestamp(System.currentTimeMillis())
    private val _defaultMonthB = shiftMonthKey(_defaultMonthA, -1)

    private val _compareMonthA = MutableStateFlow(_defaultMonthA)
    val compareMonthA: StateFlow<String> = _compareMonthA.asStateFlow()

    private val _compareMonthB = MutableStateFlow(_defaultMonthB)
    val compareMonthB: StateFlow<String> = _compareMonthB.asStateFlow()

    fun setCompareMonthA(v: String) {
        _compareMonthA.value = v
        viewModelScope.launch(Dispatchers.IO) { prefs.edit().putString("compare_month_a", v).apply() }
    }
    fun setCompareMonthB(v: String) {
        _compareMonthB.value = v
        viewModelScope.launch(Dispatchers.IO) { prefs.edit().putString("compare_month_b", v).apply() }
    }

    fun updateLastSavedCategory(category: String) {
        _lastSavedCategory.value = category
        viewModelScope.launch(Dispatchers.IO) {
            prefs.edit().putString("last_category", category).apply()
        }
    }

    private val _calendarMonthKey = MutableStateFlow(
        monthKeyFromTimestamp(System.currentTimeMillis()))
    val calendarMonthKey: StateFlow<String> = _calendarMonthKey.asStateFlow()

    private val _calendarFilterCategory = MutableStateFlow<String?>(null)
    val calendarFilterCategory: StateFlow<String?> = _calendarFilterCategory.asStateFlow()

    fun setCalendarMonthKey(v: String)          { _calendarMonthKey.value = v }
    fun setCalendarFilterCategory(v: String?)   { _calendarFilterCategory.value = v }

    private val _timeBucket = MutableStateFlow(System.currentTimeMillis() / 600_000L)

    private val noteIndexState: StateFlow<NoteIndex> = combine(_records, _timeBucket) { records, _ ->
        buildNoteIndex(records)
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NoteIndex())

    private val _hiddenQuickNotes = MutableStateFlow<Set<String>>(emptySet())
    val hiddenQuickNotes: StateFlow<Set<String>> = _hiddenQuickNotes.asStateFlow()

    fun hideQuickNote(note: String) {
        if (note.isBlank()) return
        val currentVisibleCount = noteIndexState.value.topNotes.count { it.first !in _hiddenQuickNotes.value }
        if (currentVisibleCount <= 1) return
        val newSet = _hiddenQuickNotes.value + note
        _hiddenQuickNotes.value = newSet
        viewModelScope.launch(Dispatchers.IO) {
            prefs.edit().putStringSet("hidden_quick_notes", newSet).apply()
        }
    }

    fun unhideQuickNote(note: String) {
        if (note.isBlank()) return
        val newSet = _hiddenQuickNotes.value - note
        _hiddenQuickNotes.value = newSet
        viewModelScope.launch(Dispatchers.IO) {
            prefs.edit().putStringSet("hidden_quick_notes", newSet).apply()
        }
    }

    val ledgerUiState: StateFlow<LedgerUiState> = combine(
        _records, noteIndexState, _loading, _filter, _deletingIds
    ) { records, noteIndex, loading, filter, deletingIds ->
        Triple(records, noteIndex, Triple(loading, filter, deletingIds))
    }.combine(_hiddenQuickNotes) { triple, hidden ->
        val (records, noteIndex, rest) = triple
        val (loading, filter, deletingIds) = rest
        computeLedgerUiState(records, noteIndex, loading, filter, deletingIds, hidden)
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LedgerUiState())

    val compareUiState: StateFlow<CompareUiState> = combine(
        _compareSnapshotRecords, _compareMonthA, _compareMonthB
    ) { records, a, b -> computeCompareUiState(records, a, b) }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CompareUiState())

    val calendarUiState: StateFlow<CalendarUiState> = combine(
        _records, _calendarMonthKey, _calendarFilterCategory
    ) { records, mk, cat -> computeCalendarUiState(records, mk, cat) }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CalendarUiState())

    private val _pivotYear = MutableStateFlow(
        getTimestampMeta(System.currentTimeMillis()).year
    )
    val pivotYear: StateFlow<Int> = _pivotYear.asStateFlow()

    fun setPivotYear(year: Int) {
        if (year > 0) _pivotYear.value = year
    }

    val pivotUiState: StateFlow<PivotUiState> = combine(_records, _pivotYear) { records, year ->
        computePivotUiState(records, year)
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PivotUiState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val savedCategory = prefs.getString("last_category", "飲食") ?: "飲食"
            val savedMonthA = prefs.getString("compare_month_a", _defaultMonthA) ?: _defaultMonthA
            val savedMonthB = prefs.getString("compare_month_b", _defaultMonthB) ?: _defaultMonthB
            val savedHidden = prefs.getStringSet("hidden_quick_notes", emptySet()) ?: emptySet()
            _lastSavedCategory.value = savedCategory
            _compareMonthA.value = savedMonthA
            _compareMonthB.value = savedMonthB
            _hiddenQuickNotes.value = savedHidden
            withContext(Dispatchers.Default) { listenToFirestore() }
        }

        viewModelScope.launch {
            _records.collect { records ->
                val shouldSync =
                    _isComparePageActive.value ||
                    (!hasInitializedCompareSnapshot && !_loading.value)
                if (shouldSync) {
                    _compareSnapshotRecords.value = records.filter { it.category != "還錢" }
                    hasInitializedCompareSnapshot = true
                }
            }
        }

        viewModelScope.launch {
            while (true) {
                delay(60_000L)
                val bucket = System.currentTimeMillis() / 600_000L
                if (bucket != _timeBucket.value) {
                    _timeBucket.value = bucket
                }
            }
        }
    }

    private fun buildSortedList(): List<Record> {
        val list = ArrayList<Record>(recordsById.size + ghostRecordsById.size)
        list.addAll(recordsById.values)
        list.addAll(ghostRecordsById.values)
        list.sortWith(compareByDescending<Record> { it.timestamp }.thenByDescending { it.id })
        return list
    }

    private fun emitSorted() {
        _records.value = buildSortedList()
    }

    private fun listenToFirestore() {
        db.collection("records")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                _loading.value = false
                AppStartupState.isFirstDataReady = true
                if (err != null || snap == null) return@addSnapshotListener

                val changes = snap.documentChanges
                if (changes.isEmpty()) return@addSnapshotListener

                val isFirstSnapshot = !hasReceivedFirstSnapshot
                hasReceivedFirstSnapshot = true

                viewModelScope.launch(Dispatchers.Default) {
                    val remoteAdded = ArrayList<String>(changes.size)
                    val remoteRemoved = ArrayList<String>(changes.size)

                    for (change in changes) {
                        val r = change.document.toObject(Record::class.java)
                            ?.copy(id = change.document.id) ?: continue
                        getTimestampMeta(r.timestamp)
                        when (change.type) {
                            DocumentChange.Type.ADDED -> {
                                val existed = recordsById.containsKey(r.id)
                                recordsById[r.id] = r
                                if (!existed) remoteAdded.add(r.id)
                            }
                            DocumentChange.Type.MODIFIED -> recordsById[r.id] = r
                            DocumentChange.Type.REMOVED -> {
                                val existing = recordsById.remove(r.id)
                                if (existing != null) {
                                    ghostRecordsById[r.id] = existing
                                    remoteRemoved.add(r.id)
                                }
                            }
                        }
                    }

                    if (remoteAdded.isEmpty() && remoteRemoved.isEmpty()) {
                        emitSorted()
                        return@launch
                    }

                    if (remoteAdded.isNotEmpty() && !isFirstSnapshot) {
                        _justAddedIds.update { it + remoteAdded.toSet() }
                    }
                    if (remoteRemoved.isNotEmpty()) {
                        _deletingIds.update { it + remoteRemoved.toSet() }
                    }
                    emitSorted()

                    if (remoteAdded.isNotEmpty() && !isFirstSnapshot) {
                        val ids = remoteAdded.toSet()
                        viewModelScope.launch {
                            delay(800)
                            _justAddedIds.update { it - ids }
                        }
                    }
                    if (remoteRemoved.isNotEmpty()) {
                        val ids = remoteRemoved.toSet()
                        viewModelScope.launch {
                            delay(900)
                            for (id in ids) ghostRecordsById.remove(id)
                            emitSorted()
                            _deletingIds.update { it - ids }
                        }
                    }
                }
            }
    }

    fun addRecord(record: Record, onIdReady: (String) -> Unit = {}) {
        val newId = db.collection("records").document().id
        val toSave = record.copy(id = newId)
        getTimestampMeta(toSave.timestamp)
        recordsById[newId] = toSave
        _justAddedIds.update { it + newId }
        emitSorted()
        onIdReady(newId)
        viewModelScope.launch {
            delay(800)
            _justAddedIds.update { it - newId }
        }
        viewModelScope.launch(Dispatchers.IO) {
            try { db.collection("records").document(newId).set(toSave).await() } catch (_: Exception) {}
        }
    }

    fun updateRecord(id: String, amount: Double, note: String, category: String, timestamp: Long) {
        getTimestampMeta(timestamp)
        val existing = recordsById[id] ?: return

        val newIconUrl = if (existing.note != note) {
            latestIconForNote(note)
        } else {
            existing.iconUrl
        }

        recordsById[id] = existing.copy(
            amount = amount, note = note, category = category, timestamp = timestamp, iconUrl = newIconUrl
        )
        emitSorted()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("records").document(id).update(mapOf(
                    "amount" to amount, "note" to note,
                    "category" to category, "timestamp" to timestamp,
                    "iconUrl" to newIconUrl
                )).await()
            } catch (_: Exception) {}
        }
    }

    fun markAsDeleting(id: String) { _deletingIds.update { it + id } }
    fun cancelDelete(id: String)   { _deletingIds.update { it - id } }

    fun commitDelete(id: String) {
        _deletingIds.update { it - id }
        recordsById.remove(id)
        ghostRecordsById.remove(id)
        emitSorted()
        viewModelScope.launch(Dispatchers.IO) {
            try { db.collection("records").document(id).delete().await() } catch (_: Exception) {}
        }
    }

    fun deleteRecord(id: String) { commitDelete(id) }

    fun restoreRecord(record: Record) {
        getTimestampMeta(record.timestamp)
        recordsById[record.id] = record
        ghostRecordsById.remove(record.id)
        _deletingIds.update { it - record.id }
        _justAddedIds.update { it + record.id }
        emitSorted()
        viewModelScope.launch {
            delay(800)
            _justAddedIds.update { it - record.id }
        }
        viewModelScope.launch(Dispatchers.IO) {
            try { db.collection("records").document(record.id).set(record).await() } catch (_: Exception) {}
        }
    }

    fun latestIconForNote(note: String): String {
        if (note.isBlank()) return ""
        return _records.value.firstOrNull { it.note == note && it.iconUrl.isNotBlank() }?.iconUrl ?: ""
    }

    fun monthTotal(category: String, monthKey: String): Double {
        var sum = 0.0
        for (r in _records.value) {
            if (r.category == category && monthKeyFromTimestamp(r.timestamp) == monthKey) sum += r.amount
        }
        return sum
    }
}

internal fun buildNoteIndex(records: List<Record>): NoteIndex {
    if (records.isEmpty()) return NoteIndex()
    val now = System.currentTimeMillis()
    val secNow = getTimestampMeta(now).secOfDay

    val usageCountByNote = HashMap<String, Int>()
    val minDistByNote = LinkedHashMap<String, Int>()
    val recentAmountByNote = HashMap<String, Double>()
    val amountMatchDistByNote = HashMap<String, Int>()
    val amountMatchTsByNote = HashMap<String, Long>()
    val categoryTsByNote = HashMap<String, Long>()
    val noteIconMap = HashMap<String, String>()
    val noteCategoryMap = HashMap<String, String>()
    val allNoteNames = ArrayList<String>()
    val firstSeenOrder = LinkedHashMap<String, Int>()
    var seenIdx = 0

    for (r in records) {
        val note = r.note
        if (note.isBlank()) continue

        usageCountByNote[note] = (usageCountByNote[note] ?: 0) + 1

        if (!firstSeenOrder.containsKey(note)) {
            firstSeenOrder[note] = seenIdx++
            allNoteNames.add(note)
        }

        val sec = getTimestampMeta(r.timestamp).secOfDay
        val d = abs(secNow - sec)
        val dist = if (d < 86400 - d) d else 86400 - d
        val prevMin = minDistByNote[note]
        if (prevMin == null || dist < prevMin) {
            minDistByNote[note] = dist
        }

        val prevDist = amountMatchDistByNote[note]
        val prevTs = amountMatchTsByNote[note] ?: 0L
        val betterAmount = when {
            prevDist == null -> true
            dist < prevDist -> true
            dist == prevDist && r.timestamp >= prevTs -> true
            else -> false
        }
        if (betterAmount) {
            amountMatchDistByNote[note] = dist
            amountMatchTsByNote[note] = r.timestamp
            recentAmountByNote[note] = r.amount
        }

        val prevCatTs = categoryTsByNote[note] ?: 0L
        if (r.timestamp >= prevCatTs) {
            categoryTsByNote[note] = r.timestamp
            noteCategoryMap[note] = r.category
        }

        if (r.iconUrl.isNotBlank() && !noteIconMap.containsKey(note)) {
            noteIconMap[note] = r.iconUrl
        }
    }

    val TIME_MATCH_THRESHOLD_SEC = 6 * 3600
    val timeMatchedNote: String? = minDistByNote.entries
        .filter { it.value <= TIME_MATCH_THRESHOLD_SEC }
        .minWithOrNull(Comparator { a, b ->
            val cmp = a.value.compareTo(b.value)
            if (cmp != 0) cmp
            else (firstSeenOrder[a.key] ?: Int.MAX_VALUE)
                .compareTo(firstSeenOrder[b.key] ?: Int.MAX_VALUE)
        })?.key

    val restSorted = usageCountByNote.entries
        .filter { it.key != timeMatchedNote }
        .sortedWith(Comparator { a, b ->
            val cmp = b.value.compareTo(a.value)
            if (cmp != 0) cmp
            else (firstSeenOrder[a.key] ?: Int.MAX_VALUE)
                .compareTo(firstSeenOrder[b.key] ?: Int.MAX_VALUE)
        })
        .map { it.key to it.value }

    val topNotes = ArrayList<Pair<String, Int>>(30)
    if (timeMatchedNote != null) {
        topNotes.add(timeMatchedNote to (usageCountByNote[timeMatchedNote] ?: 0))
    }
    for (pair in restSorted) {
        if (topNotes.size >= 30) break
        topNotes.add(pair)
    }

    return NoteIndex(
        recentAmountByNote = recentAmountByNote,
        noteIconMap = noteIconMap,
        allNoteNames = allNoteNames,
        noteCategoryMap = noteCategoryMap,
        noteUsageCount = usageCountByNote,
        topNotes = topNotes,
    )
}

internal fun computeLedgerUiState(
    records: List<Record>, noteIndex: NoteIndex,
    loading: Boolean, filter: FilterState, deletingIds: Set<String>,
    hiddenQuickNotes: Set<String> = emptySet(),
): LedgerUiState {
    val now = System.currentTimeMillis()

    val searchFiltered: List<Record> = if (filter.search.isBlank()) records else {
        val q = filter.search
        records.filter { r ->
            r.note.contains(q, ignoreCase = true) || r.category.contains(q, ignoreCase = true)
        }
    }

    val filterCat = filter.category
    val filterMon = filter.month
    val startMin = filter.startTimeMin
    val endMin = filter.endTimeMin

    val catMonthFiltered = if (filterCat == null && filterMon == null && startMin == 0 && endMin == 1440) searchFiltered else {
        searchFiltered.filter { r ->
            val minOfDay = getTimestampMeta(r.timestamp).secOfDay / 60
            (filterCat == null || r.category == filterCat) &&
            (filterMon == null || getTimestampMeta(r.timestamp).monthKey == filterMon) &&
            (minOfDay in startMin..endMin)
        }
    }

    val base = if (filter.modeOn) catMonthFiltered else records
    val futureCutoff = now + 60_000L
    val ledgerRecords = if (filter.showFuture) base else base.filter { it.timestamp <= futureCutoff }

    var hasIncome = false
    var hasExpense = false
    var totalIncome = 0.0
    var totalExpense = 0.0
    val groupedMap = LinkedHashMap<String, ArrayList<Record>>()
    var firstNote: String? = null
    var allSameNote = true

    val currentMonthKey = getTimestampMeta(now).monthKey
    val topStatRecords = when {
        !filter.modeOn -> ledgerRecords.filter { getTimestampMeta(it.timestamp).monthKey == currentMonthKey }
        filter.month == null -> ledgerRecords
        else -> ledgerRecords.filter { getTimestampMeta(it.timestamp).monthKey == filter.month }
    }

    for (r in topStatRecords) {
        if (r.id !in deletingIds && r.category != "還錢") {
            if (r.category == INCOME_CATEGORY) {
                hasIncome = true; totalIncome += r.amount
            } else {
                hasExpense = true; totalExpense += r.amount
            }
        }
    }

    for (r in ledgerRecords) {
        val dKey = getTimestampMeta(r.timestamp).dateKey
        groupedMap.getOrPut(dKey) { ArrayList() }.add(r)
        if (filter.modeOn && allSameNote) {
            if (firstNote == null) firstNote = r.note
            else if (firstNote != r.note) allSameNote = false
        }
    }

    val groupedByDate = groupedMap.entries.map { it.key to it.value }
    val isExactNoteFilter = filter.modeOn && ledgerRecords.isNotEmpty() && allSameNote

    val monthsSet = HashSet<String>()
    var hasIncForCat = false
    val expenseSums = HashMap<String, Double>()
    for (r in searchFiltered) {
        val mKey = getTimestampMeta(r.timestamp).monthKey
        monthsSet.add(mKey)
        if (filterMon == null || mKey == filterMon) {
            if (r.category == INCOME_CATEGORY) hasIncForCat = true
            else expenseSums[r.category] = (expenseSums[r.category] ?: 0.0) + r.amount
        }
    }

    val availableMonths = monthsSet.sorted()
    val expenseCats = expenseSums.entries.sortedByDescending { it.value }.map { it.key }
    val visibleCategories =
        (if (hasIncForCat) listOf(INCOME_CATEGORY) else emptyList()) + expenseCats

    val finalTopNotes = if (filter.showFuture) noteIndex.topNotes else {
        val notesWithPast = HashSet<String>()
        for (r in records) {
            if (r.timestamp <= futureCutoff && r.note.isNotBlank()) notesWithPast.add(r.note)
        }
        noteIndex.topNotes.filter { it.first in notesWithPast }
    }

    val quickInfo = noteIndex.topNotes
        .filter { it.first !in hiddenQuickNotes }
        .take(3)
        .mapNotNull { (note, _) ->
            if (note.isBlank()) null
            else QuickNoteInfo(
                note = note,
                category = noteIndex.noteCategoryMap[note] ?: "飲食",
                amount = noteIndex.recentAmountByNote[note] ?: 0.0
            )
        }

    return LedgerUiState(
        loading = loading,
        ledgerRecords = ledgerRecords,
        groupedByDate = groupedByDate,
        hasIncome = hasIncome,
        hasExpense = hasExpense,
        totalIncome = totalIncome,
        totalExpense = totalExpense,
        isExactNoteFilter = isExactNoteFilter,
        topNotes = finalTopNotes,
        recentAmountByNote = noteIndex.recentAmountByNote,
        noteIconMap = noteIndex.noteIconMap,
        allNoteNames = noteIndex.allNoteNames,
        noteCategoryMap = noteIndex.noteCategoryMap,
        noteUsageCount = noteIndex.noteUsageCount,
        availableMonths = availableMonths,
        visibleCategories = visibleCategories,
        topQuickNotesInfo = quickInfo,
        hiddenQuickNotes = hiddenQuickNotes,
    )
}

internal fun computeCompareUiState(records: List<Record>, monthA: String, monthB: String): CompareUiState {
    val categoryMonthSum = HashMap<String, HashMap<String, Double>>()
    for (r in records) {
        if (r.category == "還錢") continue
        val inner = categoryMonthSum.getOrPut(r.category) { HashMap() }
        val key = getTimestampMeta(r.timestamp).monthKey
        inner[key] = (inner[key] ?: 0.0) + r.amount
    }
    fun sumLocal(cat: String, month: String): Double =
        categoryMonthSum[cat]?.get(month) ?: 0.0

    val sortedCategories = listOf(INCOME_CATEGORY) +
        EXPENSE_CATEGORIES.sortedByDescending { sumLocal(it, monthB) }
    val displayCategories = sortedCategories.filter { cat ->
        sumLocal(cat, monthA) != 0.0 || sumLocal(cat, monthB) != 0.0
    }

    val incomeA = sumLocal(INCOME_CATEGORY, monthA)
    val incomeB = sumLocal(INCOME_CATEGORY, monthB)
    val totalExpenseA = EXPENSE_CATEGORIES.sumOf { sumLocal(it, monthA) }
    val totalExpenseB = EXPENSE_CATEGORIES.sumOf { sumLocal(it, monthB) }

    val nowMeta = getTimestampMeta(System.currentTimeMillis())
    val currentYear = nowMeta.year
    val currentMonthNum = nowMeta.month
    val monthlyKeysThisYear = (1..currentMonthNum).map { m -> "$currentYear-${pad2(m)}" }
    val histogramData = EXPENSE_CATEGORIES.associateWith { cat ->
        monthlyKeysThisYear.map { key -> sumLocal(cat, key) }
    }

    return CompareUiState(
        categoryMonthSum = categoryMonthSum,
        sortedCategories = sortedCategories,
        displayCategories = displayCategories,
        incomeA = incomeA, incomeB = incomeB,
        totalExpenseA = totalExpenseA, totalExpenseB = totalExpenseB,
        balanceA = incomeA - totalExpenseA, balanceB = incomeB - totalExpenseB,
        histogramData = histogramData,
        monthlyKeysThisYear = monthlyKeysThisYear,
    )
}

internal fun computeCalendarUiState(records: List<Record>, monthKey: String, filterCategory: String?): CalendarUiState {
    val monthAllRecords = ArrayList<Record>()
    var hasIncome = false
    val expenseSums = HashMap<String, Double>()
    for (r in records) {
        if (getTimestampMeta(r.timestamp).monthKey == monthKey) {
            monthAllRecords.add(r)
            if (r.category == INCOME_CATEGORY) hasIncome = true
            else expenseSums[r.category] = (expenseSums[r.category] ?: 0.0) + r.amount
        }
    }
    val expenseCats = expenseSums.entries.sortedByDescending { it.value }.map { it.key }
    val visibleCategories =
        (if (hasIncome) listOf(INCOME_CATEGORY) else emptyList()) + expenseCats
    val monthRecords = when (filterCategory) {
        null -> monthAllRecords.filter { it.category != "還錢" }
        else -> monthAllRecords.filter { it.category == filterCategory }
    }

    val recordsByDay = HashMap<Int, ArrayList<Record>>()
    var monthIncome = 0.0
    var monthExpense = 0.0
    for (r in monthRecords) {
        val day = getTimestampMeta(r.timestamp).dayOfMonth
        recordsByDay.getOrPut(day) { ArrayList() }.add(r)
        if (r.category == INCOME_CATEGORY) monthIncome += r.amount
        else monthExpense += r.amount
    }

    var maxAbsNet = 0.0
    for (dayRecords in recordsByDay.values) {
        var inc = 0.0; var exp = 0.0
        for (r in dayRecords) {
            if (r.category == INCOME_CATEGORY) inc += r.amount else exp += r.amount
        }
        val netAbs = abs(inc - exp)
        if (netAbs > maxAbsNet) maxAbsNet = netAbs
    }

    val totalNet = monthIncome - monthExpense
    val itemCount = monthRecords.size
    val avgPerItem = if (itemCount > 0) (monthIncome + monthExpense) / itemCount else 0.0

    val nowMeta = getTimestampMeta(System.currentTimeMillis())
    val todayKey = nowMeta.monthKey
    val parts = monthKey.split("-")
    val curCal = threadLocalCal.get()!!.apply {
        if (parts.size == 2) set(parts[0].toInt(), parts[1].toInt() - 1, 1)
    }
    val daysInMonth = curCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val isCurrentMonth = monthKey == todayKey
    val daysElapsed = if (isCurrentMonth) nowMeta.dayOfMonth else daysInMonth
    val avgPerDay = if (daysElapsed > 0) monthExpense / daysElapsed else 0.0

    return CalendarUiState(
        monthKey = monthKey,
        monthAllRecords = monthAllRecords,
        visibleCategories = visibleCategories,
        monthRecords = monthRecords,
        recordsByDay = recordsByDay,
        maxAbsNet = maxAbsNet,
        monthIncome = monthIncome,
        monthExpense = monthExpense,
        totalNet = totalNet,
        itemCount = itemCount,
        avgPerItem = avgPerItem,
        avgPerDay = avgPerDay,
    )
}

internal fun computePivotUiState(records: List<Record>, selectedYear: Int): PivotUiState {
    val yearsSet = HashSet<Int>()
    val data = HashMap<String, HashMap<String, HashMap<String, PivotCell>>>()
    val noteIconMap = HashMap<String, String>()

    for (r in records) {
        val meta = getTimestampMeta(r.timestamp)
        yearsSet.add(meta.year)
        if (r.note.isNotBlank() && r.iconUrl.isNotBlank() && !noteIconMap.containsKey(r.note)) {
            noteIconMap[r.note] = r.iconUrl
        }
        if (meta.year != selectedYear) continue
        val mk = meta.monthKey
        val catMap = data.getOrPut(mk) { HashMap() }
        val noteMap = catMap.getOrPut(r.category) { HashMap() }
        val key = r.note.ifBlank { "(無名稱)" }
        val existing = noteMap[key] ?: PivotCell()
        noteMap[key] = PivotCell(existing.total + r.amount, existing.count + 1)
    }

    val availableYears = yearsSet.sorted()
    val year = when {
        selectedYear in yearsSet -> selectedYear
        availableYears.isNotEmpty() -> availableYears.last()
        else -> selectedYear
    }

    val nowMeta = getTimestampMeta(System.currentTimeMillis())
    val maxMonth = when {
        year < nowMeta.year -> 12
        year > nowMeta.year -> 0
        else -> nowMeta.month
    }
    val months = if (maxMonth <= 0) emptyList()
                 else (1..maxMonth).map { m -> "$year-${pad2(m)}" }

    if (data.isEmpty()) {
        return PivotUiState(
            months = months,
            rows = emptyList(),
            monthTotals = emptyMap(),
            grandTotal = 0.0,
            loading = false,
            availableYears = availableYears,
            selectedYear = year,
            categories = emptyList(),
            noteIconMap = noteIconMap,
        )
    }

    val categoryTotalAll = HashMap<String, Double>()
    val categoryLatest = HashMap<String, Double>()
    val noteTotalAll = HashMap<Pair<String, String>, Double>()
    val noteLatest = HashMap<Pair<String, String>, Double>()
    var latestMonthWithData: String? = null
    for (mk in months.asReversed()) {
        if (data.containsKey(mk)) {
            latestMonthWithData = mk
            break
        }
    }

    for ((mk, catMap) in data) {
        for ((cat, noteMap) in catMap) {
            var catSum = 0.0
            for ((note, cell) in noteMap) {
                catSum += cell.total
                val pair = cat to note
                noteTotalAll[pair] = (noteTotalAll[pair] ?: 0.0) + cell.total
                if (mk == latestMonthWithData) {
                    noteLatest[pair] = (noteLatest[pair] ?: 0.0) + cell.total
                }
            }
            categoryTotalAll[cat] = (categoryTotalAll[cat] ?: 0.0) + catSum
            if (mk == latestMonthWithData) {
                categoryLatest[cat] = (categoryLatest[cat] ?: 0.0) + catSum
            }
        }
    }

    val sortedCats = categoryTotalAll.keys.sortedWith(
        compareByDescending<String> { categoryLatest[it] ?: 0.0 }
            .thenByDescending { categoryTotalAll[it] ?: 0.0 }
    )

    val rows = mutableListOf<PivotRow>()
    for (cat in sortedCats) {
        val catCells = HashMap<String, PivotCell>()
        var catTotal = 0.0
        for (mk in months) {
            val noteMap = data[mk]?.get(cat) ?: continue
            var sum = 0.0; var cnt = 0
            for (cell in noteMap.values) { sum += cell.total; cnt += cell.count }
            if (sum != 0.0) { catCells[mk] = PivotCell(sum, cnt); catTotal += sum }
        }
        rows.add(PivotRow(cat, "", true, catCells, catTotal))

        val notesInCat = noteTotalAll.keys
            .filter { it.first == cat }
            .sortedWith(
                compareByDescending<Pair<String, String>> { noteLatest[it] ?: 0.0 }
                    .thenByDescending { noteTotalAll[it] ?: 0.0 }
            )

        for (pair in notesInCat) {
            val note = pair.second
            val noteCells = HashMap<String, PivotCell>()
            var rowTotal = 0.0
            for (mk in months) {
                val cell = data[mk]?.get(cat)?.get(note) ?: continue
                noteCells[mk] = cell; rowTotal += cell.total
            }
            rows.add(PivotRow(cat, note, false, noteCells, rowTotal))
        }
    }

    val monthTotals = HashMap<String, Double>()
    var grandTotal = 0.0
    for (mk in months) {
        var sum = 0.0
        data[mk]?.forEach { (_, noteMap) -> noteMap.values.forEach { sum += it.total } }
        if (sum != 0.0) monthTotals[mk] = sum
        grandTotal += sum
    }

    return PivotUiState(
        months = months,
        rows = rows,
        monthTotals = monthTotals,
        grandTotal = grandTotal,
        loading = false,
        availableYears = availableYears,
        selectedYear = year,
        categories = sortedCats,
        noteIconMap = noteIconMap,
    )
}

fun computeWeeklyExpenses(records: List<Record>): List<WeeklyExpenseDay> {
    val todayStart = getTodayStartMillis()
    val dayMs = 86_400_000L
    val startDay = todayStart - 6 * dayMs
    val amountByDay = HashMap<Long, Double>(7)
    for (r in records) {
        if (r.category == INCOME_CATEGORY || r.category == "還錢") continue

        val dayStart = getTimestampMeta(r.timestamp).startOfDayMillis
        if (dayStart in startDay..todayStart) {
            amountByDay[dayStart] = (amountByDay[dayStart] ?: 0.0) + r.amount
        }
    }
    val result = ArrayList<WeeklyExpenseDay>(7)
    for (i in 0..6) {
        val dayStart = startDay + i * dayMs
        val meta = getTimestampMeta(dayStart)
        result.add(
            WeeklyExpenseDay(
                dayOfMonth = meta.dayOfMonth,
                weekdayChar = WEEKDAY_CHARS[meta.dayOfWeek - 1],
                amount = amountByDay[dayStart] ?: 0.0,
                isToday = dayStart == todayStart
            )
        )
    }
    return result
}
