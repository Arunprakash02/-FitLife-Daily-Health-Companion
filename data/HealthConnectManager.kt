package com.fitlife.app.data

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class ActivitySummary(
    val steps: Long = 0,
    val distanceKm: Double = 0.0,
    val calories: Double = 0.0,
    val heartAvg: Long? = null,
    val walkMinutes: Long = 0
)

class HealthConnectManager(private val context: Context) {

    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class)
    )

    fun sdkStatus(): Int = HealthConnectClient.getSdkStatus(context)
    fun isAvailable(): Boolean = sdkStatus() == HealthConnectClient.SDK_AVAILABLE

    private val client by lazy { HealthConnectClient.getOrCreate(context) }

    suspend fun hasAllPermissions(): Boolean =
        client.permissionController.getGrantedPermissions().containsAll(permissions)

    private fun startOfToday(): Instant =
        LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()

    suspend fun readToday(): ActivitySummary {
        val range = TimeRangeFilter.between(startOfToday(), Instant.now())
        val r = client.aggregate(
            AggregateRequest(
                metrics = setOf(
                    StepsRecord.COUNT_TOTAL,
                    DistanceRecord.DISTANCE_TOTAL,
                    ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL,
                    HeartRateRecord.BPM_AVG
                ),
                timeRangeFilter = range
            )
        )
        val steps = r[StepsRecord.COUNT_TOTAL] ?: 0L
        val km = (r[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0.0) / 1000.0
        val kcal = r[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories ?: 0.0
        val hr = r[HeartRateRecord.BPM_AVG]
        return ActivitySummary(
            steps = steps,
            distanceKm = if (km > 0) km else steps * 0.0008,      // fallback ~0.8 m/step
            calories = if (kcal > 0) kcal else steps * 0.04,      // fallback estimate
            heartAvg = hr,
            walkMinutes = steps / 100                              // ~100 steps/min estimate
        )
    }

    /** Steps for each hour of today (24 values). */
    suspend fun hourlySteps(): LongArray {
        val out = LongArray(24)
        val zone = ZoneId.systemDefault()
        val start = startOfToday()
        val res = client.aggregateGroupByDuration(
            AggregateGroupByDurationRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(start, Instant.now()),
                timeRangeSlicer = Duration.ofHours(1)
            )
        )
        for (g in res) {
            val hour = g.startTime.atZone(zone).hour
            out[hour] = g.result[StepsRecord.COUNT_TOTAL] ?: 0L
        }
        return out
    }
}
