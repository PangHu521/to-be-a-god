package com.deify.app.ui.screens.bodydata

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.deify.app.data.local.entity.BodyMeasurement
import com.deify.app.ui.theme.*
import com.deify.app.util.DateUtils

@Composable
fun BodyDataScreen(viewModel: BodyDataViewModel = viewModel()) {
    val measurements by viewModel.measurements.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("身体数据", style = MaterialTheme.typography.headlineMedium)
                IconButton(onClick = { viewModel.showAddDialog() }) {
                    Icon(Icons.Default.Add, contentDescription = "添加", tint = Green500)
                }
            }
        }

        // 最新数据概览
        val latest = measurements.firstOrNull()
        if (latest != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = DateUtils.displayFormat(DateUtils.parse(latest.date)),
                            style = MaterialTheme.typography.labelMedium,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            BodyStatItem("体重", "${latest.weightKg}", "kg")
                            BodyStatItem("体脂", "${latest.bodyFatPct}", "%")
                            BodyStatItem("胸围", "${latest.chestCm}", "cm")
                            BodyStatItem("腰围", "${latest.waistCm}", "cm")
                        }
                    }
                }
            }
        }

        // 趋势图位置(后续用 Canvas 实现折线图)
        if (measurements.size >= 2) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("体重趋势", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        SimpleTrendChart(measurements)
                    }
                }
            }
        }

        // 历史记录
        item {
            Text("历史记录", style = MaterialTheme.typography.titleMedium)
        }

        if (measurements.isEmpty()) {
            item {
                Text(
                    text = "暂无数据，点击右上角 + 添加",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
        }

        items(measurements) { m ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = DateUtils.displayFormat(DateUtils.parse(m.date)),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${m.weightKg}kg | ${m.bodyFatPct}% BF",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    IconButton(onClick = { viewModel.deleteMeasurement(m.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "删除", tint = Red400, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    // 添加数据对话框
    if (showAddDialog) {
        AddMeasurementDialog(
            onDismiss = { viewModel.hideAddDialog() },
            onConfirm = { viewModel.addMeasurement(it) }
        )
    }
}

@Composable
private fun BodyStatItem(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$value$unit",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
    }
}

@Composable
private fun SimpleTrendChart(measurements: List<BodyMeasurement>) {
    if (measurements.size < 2) return
    val newest = measurements.first().weightKg
    val oldest = measurements.last().weightKg
    val diff = newest - oldest
    val isDown = diff <= 0

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (isDown) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
            contentDescription = null,
            tint = if (isDown) Green500 else Red400
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${if (isDown) "下降" else "上升"} ${kotlin.math.abs(diff)}kg",
            style = MaterialTheme.typography.bodyLarge,
            color = if (isDown) Green500 else Red400
        )
    }
}

@Composable
private fun AddMeasurementDialog(
    onDismiss: () -> Unit,
    onConfirm: (BodyMeasurement) -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var bodyFat by remember { mutableStateOf("") }
    var chest by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加身体数据") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("体重 (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bodyFat,
                    onValueChange = { bodyFat = it },
                    label = { Text("体脂率 (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = chest,
                    onValueChange = { chest = it },
                    label = { Text("胸围 (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = waist,
                    onValueChange = { waist = it },
                    label = { Text("腰围 (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val m = BodyMeasurement(
                    date = DateUtils.format(DateUtils.today()),
                    weightKg = weight.toFloatOrNull() ?: 0f,
                    bodyFatPct = bodyFat.toFloatOrNull() ?: 0f,
                    chestCm = chest.toFloatOrNull() ?: 0f,
                    waistCm = waist.toFloatOrNull() ?: 0f
                )
                onConfirm(m)
            }) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
