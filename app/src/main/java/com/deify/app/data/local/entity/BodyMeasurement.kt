package com.deify.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "body_measurements")
data class BodyMeasurement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "date") val date: String, // yyyy-MM-dd
    @ColumnInfo(name = "weight_kg") val weightKg: Float = 0f,
    @ColumnInfo(name = "body_fat_pct") val bodyFatPct: Float = 0f,
    @ColumnInfo(name = "chest_cm") val chestCm: Float = 0f,
    @ColumnInfo(name = "waist_cm") val waistCm: Float = 0f,
    @ColumnInfo(name = "biceps_cm") val bicepsCm: Float = 0f,
    @ColumnInfo(name = "thigh_cm") val thighCm: Float = 0f,
    @ColumnInfo(name = "note") val note: String = ""
)
