package io.github.garykam.readit.util

import java.time.Duration
import java.time.Instant

fun Long.toElapsed(): String {
    val elapsed = Duration.between(Instant.ofEpochSecond(this), Instant.now())
    val minutes = elapsed.toMinutes()
    val hours = elapsed.toHours()
    val days = elapsed.toDays()

    return when {
        days > 1L -> "$days days"
        days == 1L -> "1 day"
        hours > 0 -> "$hours hr."
        minutes > 0 -> "$minutes min."
        else -> { "Less than 1 min." }
    }
}