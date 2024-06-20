@file:Suppress("unused")

package com.epikron.countriesandflags.util

import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.compose.ui.unit.Dp
import com.epikron.utils.ascendInRange
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.random.Random

fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.pxToDp(): Dp {
    val displayDensity = Resources.getSystem().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT
    return Dp(this / displayDensity)
}
