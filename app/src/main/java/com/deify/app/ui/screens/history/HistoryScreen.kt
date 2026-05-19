package com.deify.app.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.deify.app.ui.theme.*
import com.deify.app.util.DateUtils
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel()) {
    val records by viewModel.records.collectAsState()
    val checkInDates by viewModel.checkInDates.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题
        item {
            Text(
                text = "打卡记录",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // 连续打卡天数
        item {
            val streak = checkInDates.size // simplified
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
                        text = "总打卡天数",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "${checkInDates.size}",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = Green500
                    )
                }
            }
        }

        // 月份切换
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Text("◀ 上月")
                }
                Text(
                    text = "${currentMonth.year}年${currentMonth.monthValue}月",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Text("下月 ▶")
                }
            }
        }

        // 日历网格 (简化版)
        item {
            val daysInMonth = currentMonth.lengthOfMonth()
            val firstDay = currentMonth.atDay(1).dayOfWeek.value // 1=Mon, 7=Sun
            val offset = if (firstDay == 7) 0 else firstDay // adjust for Chinese week start

            // 星期头
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("一", "二", "三", "四", "五", "六", "日").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 日历格子
            val today = DateUtils.today()
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(280.dp)
            ) {
                items(offset) {
                    Spacer(modifier = Modifier.size(36.dp))
                }
                items(daysInMonth) { day ->
                    val date = currentMonth.atDay(day)
                    val dateStr = DateUtils.format(date)
                    val isChecked = dateStr in checkInDates
                    val isToday = date == today
                    val isSelected = dateStr == selectedDate

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> Green500
                                    isChecked -> Green400.copy(alpha = 0.6f)
                                    isToday -> CardBg
                                    else -> DarkBg
                                }
                            )
                            .clickable { viewModel.selectDate(dateStr) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$day",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected || isChecked) DarkBg else TextPrimary,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // 选中日期的记录
        if (selectedDate != null) {
            item {
                Text(
                    text = "${selectedDate} 的训练",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            val dateRecords = records.filter { it.date == selectedDate }
            if (dateRecords.isEmpty()) {
                item {
                    Text(
                        text = "当天无训练记录",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            } else {
                items(dateRecords) { record ->
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
                                    text = "${record.durationSeconds / 60} 分钟",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (record.note.isNotEmpty()) {
                                    Text(
                                        text = record.note,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                            IconButton(onClick = { viewModel.deleteRecord(record.id) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "删除",
                                    tint = Red400
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
