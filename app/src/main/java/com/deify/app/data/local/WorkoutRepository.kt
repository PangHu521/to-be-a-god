package com.deify.app.data.local

import com.deify.app.data.local.dao.WorkoutDao
import com.deify.app.data.local.entity.WorkoutPlan
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val dao: WorkoutDao) {
    fun observeAll(): Flow<List<WorkoutPlan>> = dao.observeAll()

    suspend fun getById(id: Long): WorkoutPlan? = dao.getById(id)

    fun observeByCategory(category: String): Flow<List<WorkoutPlan>> = dao.observeByCategory(category)

    suspend fun insert(plan: WorkoutPlan): Long = dao.insert(plan)

    suspend fun update(plan: WorkoutPlan) = dao.update(plan)

    suspend fun delete(plan: WorkoutPlan) = dao.delete(plan)
}
