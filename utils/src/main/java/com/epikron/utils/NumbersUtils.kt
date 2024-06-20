package com.epikron.utils

import kotlin.math.absoluteValue
import kotlin.random.Random

val Float.half: Float get() = this / 2f
val Int.half: Int get() = this / 2

val Float.third: Float get() = this / 3f
val Int.third: Int get() = this / 3

val Float.quarter: Float get() = this / 4f
val Int.quarter: Int get() = this / 4

val Float.fifth: Float get() = this / 5f
val Int.fifth: Int get() = this / 5

/** Returns the closest Integer value from the input value. Ex: (9.42).closestInt() => 9, (9.62).closestInt() => 10*/
fun Double.closestInt(): Int {
    val base = this.toInt()
    val decimals = ((this - base) * 10).toInt()
    return if (decimals >= 5) base + 1 else base
}

/** Returns a random value between 0 and the input value */
val Float.random: Float get() = this * Random.nextFloat()

/**If the value of the input Int is smaller than the [upperLimit] (inclusive) returns the input value.
 * If the value is greater than the [upperLimit], the value will be [lowerLimit] plus the modulo of the difference between the [upperLimit] and [lowerLimit]*/
fun Int.overflowInRange(upperLimit: Int, lowerLimit: Int = 0): Int = if (this <= upperLimit) this else lowerLimit + this.mod(upperLimit - lowerLimit)

/**Gets the next value of the input Int by incrementing it with one, until the [upperLimit] (inclusive) is reached.
 * If the next value is greater than the [upperLimit], the next value will be [lowerLimit]*/
fun Int.ascendInRange(upperLimit: Int, lowerLimit: Int = 0): Int = if (this < upperLimit) this + 1 else lowerLimit

/**Gets the next value of the input Int by decrementing it with one, until the [lowerLimit] (inclusive) is reached.
 * If the next value is lower than the [lowerLimit], the next value will be [upperLimit]*/
fun Int.descendInRange(upperLimit: Int, lowerLimit: Int = 0): Int = if (this > lowerLimit) this - 1 else upperLimit

/**Returns a string formatted like 00:00:00 according to the input value.
 * If [showHoursWhenZero] is set to false the output format will be 00:00*/
fun Int.timeStringFromSeconds(showHoursWhenZero: Boolean = false): String {
    val minutes = this.div(60)
    val seconds = this.mod(60)
    val minutesAndSecondsString = "${minutes.toZeroPaddedString(2)}:${seconds.toZeroPaddedString(2)}"
    return if (showHoursWhenZero) {
        val hours = this.div(3600)
        val hoursString = "${hours.toZeroPaddedString(2)}:"
        hoursString + minutesAndSecondsString
    } else {
        minutesAndSecondsString
    }
}

/**Returns a string of [finalLength] characters, padded with zeroes.
 * The [addPositiveSign] parameter, adds the "+" prefix to the output string in case the input is positive.
 * Ex: 123.toZeroPaddedString(5, true) returns "+00123"*/
fun Int.toZeroPaddedString(finalLength: Int, addPositiveSign: Boolean = false): String {
    val isNegative = this < 0
    var result = this.absoluteValue.toString()
    while (result.length < finalLength) {
        result = "0$result"
    }
    if (isNegative) result = "-$result"
    if (addPositiveSign && !isNegative) result = "+$result"
    return result
}
