package com.deify.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.deify.app.data.local.dao.BodyDataDao
import com.deify.app.data.local.dao.CheckInDao
import com.deify.app.data.local.dao.WorkoutDao
import com.deify.app.data.local.entity.BodyMeasurement
import com.deify.app.data.local.entity.CheckInRecord
import com.deify.app.data.local.entity.WorkoutPlan

@Database(
    entities = [
        WorkoutPlan::class,
        CheckInRecord::class,
        BodyMeasurement::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun checkInDao(): CheckInDao
    abstract fun bodyDataDao(): BodyDataDao

    companion object {
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "deify.db")
                .fallbackToDestructiveMigration() // 开发阶段，生产环境应使用 Migration
                .build()
    }
}
