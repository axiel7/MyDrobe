package com.axiel7.mydrobe.utils

import com.axiel7.mydrobe.R
import com.axiel7.mydrobe.models.Season
import java.time.LocalDateTime
import java.time.Month

object CalendarHelper {

    fun getSeason() : Season {
        val current = LocalDateTime.now()
        return when (current.month) {
            Month.JANUARY -> Season.WINTER
            Month.FEBRUARY -> Season.WINTER
            Month.MARCH -> Season.SPRING
            Month.APRIL -> Season.SPRING
            Month.MAY -> Season.SPRING
            Month.JUNE -> Season.SUMMER
            Month.JULY -> Season.SUMMER
            Month.AUGUST -> Season.SUMMER
            Month.SEPTEMBER -> Season.FALL
            Month.NOVEMBER -> Season.FALL
            Month.DECEMBER -> Season.WINTER
            else -> Season.NONE
        }
    }

    fun getSeasonName(season: Season) : Int {
        return when (season) {
            Season.SUMMER -> R.string.summer
            Season.SPRING -> R.string.spring
            Season.FALL -> R.string.fall
            Season.WINTER -> R.string.winter
            else -> R.string.spring
        }
    }
}