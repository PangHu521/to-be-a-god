package com.deify.app.domain.model

data class DailySummary(
    val totalWorkouts: Int = 0,
    val totalDurationMinutes: Long = 0,
    val totalCalories: Int = 0,
    val streakDays: Int = 0,
    val stepsFromHealthConnect: Long = 0
)
