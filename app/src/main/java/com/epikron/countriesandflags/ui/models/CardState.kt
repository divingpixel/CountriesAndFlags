package com.epikron.countriesandflags.ui.models

import com.epikron.data.data.models.CountryModel
import com.epikron.utils.random

data class CardState(
    val data: CountryModel,
    val zIndex: Int,
    val rotation: Float,
    val showDetails: Boolean
) {
    companion object {
        val empty = CardState(CountryModel.empty, 0, 0f, false)
        val test = CardState(CountryModel.test, 0, 1f.random, false)
    }
}
