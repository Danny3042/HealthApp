package utils

import androidx.health.connect.client.records.SleepSessionRecord
import java.time.Duration
import java.time.ZoneOffset

/**
 * Represents sleep data, raw, aggregated and sleep stages, for a given [SleepSessionRecord].
 */
data class SleepSessionData(
    val uid: String,
    val title: String?,
    val notes: String?,
    val startTime: java.time.Instant,
    val startZoneOffset: ZoneOffset?,
    val endTime: java.time.Instant,
    val endZoneOffset: ZoneOffset?,
    val duration: Duration?,
    val stages: List<SleepSessionRecord.Stage> = listOf()
)