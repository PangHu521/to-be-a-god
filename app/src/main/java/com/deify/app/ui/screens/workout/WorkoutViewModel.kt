package com.deify.app.ui.screens.workout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.deify.app.data.local.CheckInRepository
import com.deify.app.data.local.WorkoutRepository
import com.deify.app.data.local.entity.CheckInRecord
import com.deify.app.domain.model.Exercise
import com.deify.app.domain.model.PresetTemplates
import com.deify.app.domain.model.WorkoutTemplate
import com.deify.app.util.DateUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class TimerMode { COUNT_UP, COUNT_DOWN, EMOM, TABATA }

data class WorkoutUiState(
    val templates: List<WorkoutTemplate> = PresetTemplates.all,
    val selectedTemplate: WorkoutTemplate? = null,
    val currentExerciseIndex: Int = 0,
    val isActive: Boolean = false,
    val timerMode: TimerMode = TimerMode.COUNT_UP,
    val elapsedSeconds: Long = 0,
    val targetSeconds: Long = 60,
    val heartRate: Int = 0,
    val calories: Int = 0
)

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as com.deify.app.DeifyApp
    private val workoutRepo = WorkoutRepository(app.database.workoutDao())
    private val checkInRepo = CheckInRepository(app.database.checkInDao())

    private val _state = MutableStateFlow(WorkoutUiState())
    val state: StateFlow<WorkoutUiState> = _state.asStateFlow()

    private var timerJob: Job? = null

    fun selectTemplate(template: WorkoutTemplate?) {
        _state.value = _state.value.copy(
            selectedTemplate = template,
            currentExerciseIndex = 0,
            isActive = false,
            elapsedSeconds = 0
        )
        timerJob?.cancel()
    }

    val currentExercise: Exercise?
        get() = _state.value.selectedTemplate?.exercises?.getOrNull(_state.value.currentExerciseIndex)

    fun startTimer() {
        val s = _state.value
        _state.value = s.copy(isActive = true, elapsedSeconds = 0)
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val current = _state.value
                _state.value = current.copy(elapsedSeconds = current.elapsedSeconds + 1)
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _state.value = _state.value.copy(isActive = false)
    }

    fun resetTimer() {
        timerJob?.cancel()
        _state.value = _state.value.copy(isActive = false, elapsedSeconds = 0)
    }

    fun setTimerMode(mode: TimerMode, targetSeconds: Long = 60) {
        _state.value = _state.value.copy(timerMode = mode, targetSeconds = targetSeconds)
    }

    fun nextExercise() {
        val template = _state.value.selectedTemplate ?: return
        val nextIndex = _state.value.currentExerciseIndex + 1
        if (nextIndex < template.exercises.size) {
            _state.value = _state.value.copy(currentExerciseIndex = nextIndex, elapsedSeconds = 0)
        }
    }

    fun prevExercise() {
        val prev = (_state.value.currentExerciseIndex - 1).coerceAtLeast(0)
        _state.value = _state.value.copy(currentExerciseIndex = prev, elapsedSeconds = 0)
    }

    fun updateHeartRate(bpm: Int) {
        _state.value = _state.value.copy(heartRate = bpm)
    }

    fun finishWorkout() {
        timerJob?.cancel()
        val s = _state.value
        _state.value = s.copy(isActive = false)
        viewModelScope.launch {
            checkInRepo.insert(
                CheckInRecord(
                    date = DateUtils.format(DateUtils.today()),
                    durationSeconds = s.elapsedSeconds,
                    calories = (s.elapsedSeconds * 0.12).toInt(),
                    note = s.selectedTemplate?.name ?: "自由训练"
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
