package com.deify.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_plans")
data class WorkoutPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "category") val category: String, // chest/back/legs/cardio/custom
    @ColumnInfo(name = "exercises_json") val exercisesJson: String, // JSON array of exercises
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
