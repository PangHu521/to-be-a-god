package com.deify.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.deify.app.ui.components.ProgressRing
import com.deify.app.ui.components.StatCard
import com.deify.app.ui.theme.CardBg
import com.deify.app.ui.theme.Green500

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val summary by viewModel.summary.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "今日概览",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // 打卡进度环
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ProgressRing(
                    progress = if (summary.totalWorkouts > 0) 1f else 0f,
                    label = if (summary.totalWorkouts > 0) "已打卡" else "今日未打卡",
                    color = Green500
                )
            }
        }

        // 数据卡片行
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    value = "${summary.streakDays}",
                    label = "连续打卡",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = "${summary.totalWorkouts}",
                    label = "今日训练",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    value = "${summary.totalDurationMinutes}",
                    label = "今日分钟",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = "${summary.totalCalories}",
                    label = "千卡消耗",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 快捷入口
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "快捷入口",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBg)
                    .clickable { /* 导航到训练页 */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "开始训练",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Green500
                )
            }
        }
    }
}
