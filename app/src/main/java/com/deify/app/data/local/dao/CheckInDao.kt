package com.deify.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deify.app.data.local.entity.CheckInRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {
    @Query("SELECT * FROM checkin_records ORDER BY date DESC")
    fun observeAll(): Flow<List<CheckInRecord>>

    @Query("SELECT * FROM checkin_records WHERE date = :date")
    suspend fun getByDate(date: String): List<CheckInRecord>

    @Query("SELECT * FROM checkin_records WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun observeByDateRange(start: String, end: String): Flow<List<CheckInRecord>>

    @Query("SELECT COUNT(DISTINCT date) FROM checkin_records")
    fun observeTotalDays(): Flow<Int>

    @Query("SELECT COUNT(*) FROM checkin_records WHERE date = :date")
    suspend fun countByDate(date: String): Int

    @Query("SELECT SUM(duration_seconds) FROM checkin_records WHERE date = :date")
    suspend fun totalDurationByDate(date: String): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: CheckInRecord): Long

    @Query("DELETE FROM checkin_records WHERE id = :id")
    suspend fun deleteById(id: Long)
}
