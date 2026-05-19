package com.deify.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.deify.app.data.local.entity.WorkoutPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_plans ORDER BY created_at DESC")
    fun observeAll(): Flow<List<WorkoutPlan>>

    @Query("SELECT * FROM workout_plans WHERE id = :id")
    suspend fun getById(id: Long): WorkoutPlan?

    @Query("SELECT * FROM workout_plans WHERE category = :category ORDER BY created_at DESC")
    fun observeByCategory(category: String): Flow<List<WorkoutPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: WorkoutPlan): Long

    @Update
    suspend fun update(plan: WorkoutPlan)

    @Delete
    suspend fun delete(plan: WorkoutPlan)
}
