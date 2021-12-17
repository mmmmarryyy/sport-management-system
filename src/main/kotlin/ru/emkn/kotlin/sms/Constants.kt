package ru.emkn.kotlin.sms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

enum class Sex {
    MALE, FEMALE, NS
}

enum class Rank {
    MS, CMS, ADULT_FIRST, ADULT_SECOND, ADULT_THIRD, JUNIOR_FIRST, JUNIOR_SECOND, JUNIOR_THIRD
}

enum class Phase {
    FIRST, SECOND, THIRD
}

enum class DistanceType{
    ALL_POINTS,SOME_POINTS
}

enum class Colors(val _name: String) {

    RESET("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m")
}

interface Scrollable {
    @Composable
    fun <T> show(list: SnapshotStateList<T>, index: Int)
}