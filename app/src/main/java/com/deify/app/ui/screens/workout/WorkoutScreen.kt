package com.deify.app.ui.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.deify.app.domain.model.PresetTemplates
import com.deify.app.domain.model.WorkoutTemplate
import com.deify.app.ui.components.TimerDisplay
import com.deify.app.ui.theme.*

@Composable
fun WorkoutScreen(viewModel: WorkoutViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    if (state.selectedTemplate == null) {
        TemplateSelectionScreen(
            templates = state.templates,
            onSelect = { viewModel.selectTemplate(it) }
        )
    } else if (!state.isActive) {
        PreWorkoutScreen(
            template = state.selectedTemplate!!,
            timerMode = state.timerMode,
            targetSeconds = state.targetSeconds,
            onStart = { viewModel.startTimer() },
            onBack = {
                viewModel.pauseTimer()
                viewModel.selectTemplate(null)
            },
            onModeChange = { mode, secs -> viewModel.setTimerMode(mode, secs) }
        )
    } else {
        ActiveWorkoutScreen(
            state = state,
            currentExercise = viewModel.currentExercise,
            onPause = { viewModel.pauseTimer() },
            onReset = { viewModel.resetTimer() },
            onNext = { viewModel.nextExercise() },
            onPrev = { viewModel.prevExercise() },
            onFinish = { viewModel.finishWorkout() }
        )
    }
}

@Composable
private fun TemplateSelectionScreen(
    templates: List<WorkoutTemplate>,
    onSelect: (WorkoutTemplate) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("选择训练", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("或直接开始自由训练", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
        }

        items(templates) { template ->
            val icon = when (template.category) {
                "chest" -> Icons.Default.FitnessCenter
                "back" -> Icons.Default.FitnessCenter
                "legs" -> Icons.Default.DirectionsRun
                "cardio" -> Icons.Default.Favorite
                else -> Icons.Default.FitnessCenter
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(template) },
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, contentDescription = null, tint = Green500)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = template.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${template.exercises.size} 个动作",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                }
            }
        }

        // 自由训练按钮
        item {
            Button(
                onClick = {
                    onSelect(WorkoutTemplate(category = "custom", name = "自由训练", exercises = emptyList()))
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green500),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("自由训练", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun PreWorkoutScreen(
    template: WorkoutTemplate,
    timerMode: TimerMode,
    targetSeconds: Long,
    onStart: () -> Unit,
    onBack: () -> Unit,
    onModeChange: (TimerMode, Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回", tint = TextPrimary)
                }
                Text(template.name, style = MaterialTheme.typography.headlineMedium)
            }
        }

        // 计时器模式选择
        item {
            Text("计时模式", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimerModeButton("正计时", TimerMode.COUNT_UP, timerMode, 0) { onModeChange(it, 0) }
                TimerModeButton("倒计时", TimerMode.COUNT_DOWN, timerMode, targetSeconds) { onModeChange(it, targetSeconds) }
                TimerModeButton("EMOM", TimerMode.EMOM, timerMode, 60) { onModeChange(it, 60) }
                TimerModeButton("Tabata", TimerMode.TABATA, timerMode, 240) { onModeChange(it, 240) }
            }
        }

        // 目标时间设置 (倒计时)
        if (timerMode == TimerMode.COUNT_DOWN) {
            item {
                Text("目标时间 (秒)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                TimePicker(seconds = targetSeconds) { onModeChange(TimerMode.COUNT_DOWN, it) }
            }
        }

        // 动作列表
        item {
            Text("动作列表", style = MaterialTheme.typography.titleMedium)
        }

        items(template.exercises) { exercise ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(exercise.name, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = if (exercise.durationSeconds > 0) "${exercise.durationSeconds}s"
                        else "${exercise.sets}×${exercise.reps}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        // 开始按钮
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green500),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("开始训练", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun TimerModeButton(
    label: String,
    mode: TimerMode,
    currentMode: TimerMode,
    target: Long,
    onClick: (TimerMode) -> Unit
) {
    val isSelected = mode == currentMode
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Green500 else CardBg)
            .clickable { onClick(mode) }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) DarkBg else TextPrimary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun TimePicker(seconds: Long, onSelect: (Long) -> Unit) {
    val options = listOf(30L, 60L, 90L, 120L, 180L, 300L, 600L)
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.height(80.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options) { sec ->
            val selected = sec == seconds
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selected) Green500 else CardBg)
                    .clickable { onSelect(sec) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (sec >= 60) "${sec / 60}分" else "${sec}秒",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected) DarkBg else TextPrimary
                )
            }
        }
    }
}

@Composable
private fun ActiveWorkoutScreen(
    state: WorkoutUiState,
    currentExercise: com.deify.app.domain.model.Exercise?,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 计时器
        Spacer(modifier = Modifier.height(24.dp))
        TimerDisplay(
            totalSeconds = state.elapsedSeconds,
            label = when (state.timerMode) {
                TimerMode.COUNT_UP -> "已训练"
                TimerMode.COUNT_DOWN -> "剩余"
                TimerMode.EMOM -> "EMOM"
                TimerMode.TABATA -> "Tabata"
            }
        )

        // 心率
        if (state.heartRate > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Red500, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${state.heartRate} BPM",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Red500
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 当前动作
        if (currentExercise != null && currentExercise.name.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentExercise.name,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (currentExercise.durationSeconds > 0) "${currentExercise.durationSeconds}秒"
                        else "${currentExercise.sets}组 × ${currentExercise.reps}次" +
                                if (currentExercise.weightKg > 0) " ${currentExercise.weightKg}kg" else "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 动作切换
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onPrev) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "上一个", tint = TextSecondary)
                }
                Text(
                    text = "${state.currentExerciseIndex + 1} / ${state.selectedTemplate?.exercises?.size ?: 0}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                IconButton(onClick = onNext) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, "下一个", tint = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 控制按钮行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("重置")
            }

            Button(
                onClick = onPause,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Orange500)
            ) {
                Icon(Icons.Default.Pause, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("暂停")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 结束按钮
        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Red400),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Stop, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("结束训练")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
