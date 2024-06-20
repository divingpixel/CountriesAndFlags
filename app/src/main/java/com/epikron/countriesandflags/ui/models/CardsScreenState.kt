package com.epikron.countriesandflags.ui.models

import com.epikron.data.data.models.ContinentModel

data class CardsScreenState(
    val continents: List<ContinentModel>,
    val currentContinent: String,
    val cards: List<CardState>
) {
    companion object {
        val empty = CardsScreenState(listOf(), "", listOf())
    }
}
