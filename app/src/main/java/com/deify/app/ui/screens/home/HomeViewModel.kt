package com.deify.app.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.deify.app.data.local.CheckInRepository
import com.deify.app.domain.model.DailySummary
import com.deify.app.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as com.deify.app.DeifyApp
    private val checkInRepo = CheckInRepository(app.database.checkInDao())

    private val _summary = MutableStateFlow(DailySummary())
    val summary: StateFlow<DailySummary> = _summary.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _summary.value = checkInRepo.getDailySummary(DateUtils.format(DateUtils.today()))
        }
    }
}
