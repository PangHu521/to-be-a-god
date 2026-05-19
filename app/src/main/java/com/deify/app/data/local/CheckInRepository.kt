package com.deify.app.data.local

import com.deify.app.data.local.dao.CheckInDao
import com.deify.app.data.local.entity.CheckInRecord
import com.deify.app.domain.model.DailySummary
import com.deify.app.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class CheckInRepository(private val dao: CheckInDao) {
    fun observeAll(): Flow<List<CheckInRecord>> = dao.observeAll()

    suspend fun getByDate(date: String): List<CheckInRecord> = dao.getByDate(date)

    fun observeByDateRange(start: String, end: String): Flow<List<CheckInRecord>> =
        dao.observeByDateRange(start, end)

    fun observeStreak(): Flow<Int> = dao.observeAll().map { records ->
        calcStreak(records.map { it.date }.distinct().sortedDescending())
    }

    suspend fun getDailySummary(date: String): DailySummary {
        val count = dao.countByDate(date)
        val totalSeconds = dao.totalDurationByDate(date) ?: 0
        val streak = calcStreak(
            dao.observeAll().first().map { it.date }.distinct().sortedDescending()
        )

        return DailySummary(
            totalWorkouts = count,
            totalDurationMinutes = totalSeconds / 60,
            streakDays = streak
        )
    }

    suspend fun insert(record: CheckInRecord): Long = dao.insert(record)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    private fun calcStreak(sortedDates: List<String>): Int {
        if (sortedDates.isEmpty()) return 0
        var streak = 0
        var expected = DateUtils.today()
        for (dateStr in sortedDates) {
            val date = DateUtils.parse(dateStr)
            if (date == expected) {
                streak++
                expected = expected.minusDays(1)
            } else if (date < expected) {
                break
            }
        }
        return streak
    }
}
