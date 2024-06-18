package utils

import kotlinx.datetime.FixedOffsetTimeZone
import kotlinx.datetime.Instant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.dateWithTimeIntervalSince1970

actual fun formatDisplayTimeStartEnd(
    startTime: Instant,
    startZoneOffset: FixedOffsetTimeZone?,
    endTime: Instant,
    endZoneOffset: FixedOffsetTimeZone?
): String {
    val dateFormatter = NSDateFormatter().apply {
        setDateFormat("hh:mm a")
    }

    val start = startTime.toNSDate().let { dateFormatter.stringFromDate(it) }
    val end = endTime.toNSDate().let { dateFormatter.stringFromDate(it) }

    return "$start - $end"
}

private fun Instant.toNSDate(): NSDate {
    val timeInterval = this.epochSeconds.toDouble()
    return NSDate.dateWithTimeIntervalSince1970(timeInterval)
}