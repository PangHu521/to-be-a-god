package com.deify.app.ui.screens.bodydata

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.deify.app.data.local.BodyDataRepository
import com.deify.app.data.local.entity.BodyMeasurement
import com.deify.app.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BodyDataViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as com.deify.app.DeifyApp
    private val repo = BodyDataRepository(app.database.bodyDataDao())

    private val _measurements = MutableStateFlow<List<BodyMeasurement>>(emptyList())
    val measurements: StateFlow<List<BodyMeasurement>> = _measurements.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    init {
        viewModelScope.launch {
            repo.observeAll().collect { _measurements.value = it }
        }
    }

    fun showAddDialog() { _showAddDialog.value = true }
    fun hideAddDialog() { _showAddDialog.value = false }

    fun addMeasurement(measurement: BodyMeasurement) {
        viewModelScope.launch {
            repo.insert(measurement)
            _showAddDialog.value = false
        }
    }

    fun deleteMeasurement(id: Long) {
        viewModelScope.launch { repo.deleteById(id) }
    }
}
