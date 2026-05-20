package com.deify.app.data.healthconnect

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.ZonedDateTime
import androidx.compose.foundation.layout.weight

class HealthConnectRepo(private val context: Context) {

    private val client: HealthConnectClient by lazy {
        HealthConnectClient.getOrCreate(context)
    }

    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(WeightRecord::class)
    )

    suspend fun hasAllPermissions(): Boolean {
        return client.permissionController
            .getGrantedPermissions()
            .containsAll(permissions)
    }

    suspend fun getTodaySteps(): Long {
        val now = ZonedDateTime.now()
        val start = now.toLocalDate().atStartOfDay(now.zone)

        val response = client.aggregate(
            AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(
                    start.toInstant(),
                    Instant.now()
                )
            )
        )

        return response[StepsRecord.COUNT_TOTAL] ?: 0L
    }

    suspend fun getTodayHeartRate(): List<HeartRateRecord> {
        val now = ZonedDateTime.now()
        val start = now.toLocalDate().atStartOfDay(now.zone)

        val response = client.readRecords(
            ReadRecordsRequest(
                recordType = HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(
                    start.toInstant(),
                    Instant.now()
                )
            )
        )

        return response.records
    }

    suspend fun getTodayCalories(): Double {
        val now = ZonedDateTime.now()
        val start = now.toLocalDate().atStartOfDay(now.zone)

        val response = client.aggregate(
            AggregateRequest(
                metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(
                    start.toInstant(),
                    Instant.now()
                )
            )
        )

        return response[TotalCaloriesBurnedRecord.ENERGY_TOTAL]
            ?.inKilocalories ?: 0.0
    }

    suspend fun getLatestWeight(): WeightRecord? {

        val now = Instant.now()
        val thirtyDaysAgo = now.minusSeconds(30L * 24 * 3600)

        val response = client.readRecords(
            ReadRecordsRequest(
                recordType = WeightRecord::class,
                timeRangeFilter = TimeRangeFilter.between(
                    thirtyDaysAgo,
                    now
                ),
                pageSize = 1
            )
        )

        return response.records.firstOrNull()
    }
}