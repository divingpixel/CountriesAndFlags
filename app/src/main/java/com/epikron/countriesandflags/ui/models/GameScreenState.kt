package com.epikron.countriesandflags.ui.models

import com.epikron.data.data.models.CountryModel

data class GameScreenState(
    val matchItem: CountryModel,
    val isFlagsGame: Boolean,
    val countries: List<Pair<CountryModel, Boolean>>,
    val results: List<Pair<CountryModel, Boolean>>,
    val time: String,
    val shouldFlashTime: Boolean,
    val highScore: String,
    val score: String,
    val isScoreUp: Boolean,
    val isAbortRequest: Boolean,
    val isGameOver: Boolean
) {
    companion object {
        val empty = GameScreenState(
            matchItem = CountryModel.empty,
            isFlagsGame = true,
            countries = listOf(),
            results = listOf(),
            time = "",
            shouldFlashTime = false,
            highScore = "",
            score = "",
            isScoreUp = false,
            isAbortRequest = false,
            isGameOver = false
        )
    }
}
