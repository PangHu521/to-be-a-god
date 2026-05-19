package com.deify.app.ui.screens.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.deify.app.data.local.CheckInRepository
import com.deify.app.data.local.entity.CheckInRecord
import com.deify.app.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as com.deify.app.DeifyApp
    private val checkInRepo = CheckInRepository(app.database.checkInDao())

    private val _records = MutableStateFlow<List<CheckInRecord>>(emptyList())
    val records: StateFlow<List<CheckInRecord>> = _records.asStateFlow()

    private val _checkInDates = MutableStateFlow<Set<String>>(emptySet())
    val checkInDates: StateFlow<Set<String>> = _checkInDates.asStateFlow()

    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate.asStateFlow()

    init {
        viewModelScope.launch {
            checkInRepo.observeAll().collect { allRecords ->
                _records.value = allRecords
                _checkInDates.value = allRecords.map { it.date }.toSet()
            }
        }
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            checkInRepo.deleteById(id)
        }
    }
}
