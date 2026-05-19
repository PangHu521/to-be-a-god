package com.deify.app.data.local

import com.deify.app.data.local.dao.BodyDataDao
import com.deify.app.data.local.entity.BodyMeasurement
import kotlinx.coroutines.flow.Flow

class BodyDataRepository(private val dao: BodyDataDao) {
    fun observeAll(): Flow<List<BodyMeasurement>> = dao.observeAll()

    suspend fun getByDate(date: String): BodyMeasurement? = dao.getByDate(date)

    fun observeByDateRange(start: String, end: String): Flow<List<BodyMeasurement>> =
        dao.observeByDateRange(start, end)

    suspend fun insert(measurement: BodyMeasurement): Long = dao.insert(measurement)

    suspend fun deleteById(id: Long) = dao.deleteById(id)
}
