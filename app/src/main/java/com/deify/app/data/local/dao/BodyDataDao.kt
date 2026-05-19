package com.deify.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deify.app.data.local.entity.BodyMeasurement
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyDataDao {
    @Query("SELECT * FROM body_measurements ORDER BY date DESC")
    fun observeAll(): Flow<List<BodyMeasurement>>

    @Query("SELECT * FROM body_measurements WHERE date = :date")
    suspend fun getByDate(date: String): BodyMeasurement?

    @Query("SELECT * FROM body_measurements WHERE date BETWEEN :start AND :end ORDER BY date ASC")
    fun observeByDateRange(start: String, end: String): Flow<List<BodyMeasurement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(measurement: BodyMeasurement): Long

    @Query("DELETE FROM body_measurements WHERE id = :id")
    suspend fun deleteById(id: Long)
}
