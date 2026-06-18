package com.flexify.app.util.core

fun getLabelForRating(rating: Int): String {
    return when (rating) {
        0 -> "Not Rated"
        1 -> "⭐"
        2 -> "⭐⭐"
        3 -> "⭐⭐⭐"
        4 -> "⭐⭐⭐⭐"
        5 -> "⭐⭐⭐⭐⭐"
        else -> "Unknown"
    }
}

fun getLabelForYear(year: Int): String {
    return if (year > 0) year.toString() else "Unknown Year"
}

fun getLabelForDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return if (minutes > 0) {
        if (remainingSeconds > 0) "$minutes min $remainingSeconds sec"
        else "$minutes min"
    } else {
        "$seconds sec"
    }
}
