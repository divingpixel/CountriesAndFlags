package com.epikron.countriesandflags.ui.logic

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epikron.countriesandflags.DISABLE_CARD_INTERVAL_SECONDS
import com.epikron.countriesandflags.GAME_DURATION_SECONDS
import com.epikron.countriesandflags.ui.models.GameScreenState
import com.epikron.data.data.models.CountryModel
import com.epikron.data.data.repository.CountriesRepository
import com.epikron.utils.ascendInRange
import com.epikron.utils.takeRandomIncluding
import com.epikron.utils.timeStringFromSeconds
import com.epikron.utils.toZeroPaddedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val countriesRepository: CountriesRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private var nextCountryIndex = 0
    private var matchItem: CountryModel = CountryModel.empty
    private var matchCountries: MutableStateFlow<List<Pair<CountryModel, Boolean>>> = MutableStateFlow(listOf())

    private var disableCardCountdownJob: Job = Job()
    private var mainTimeCountdownJob: Job = Job()
    private var remainingTime: MutableStateFlow<Int> = MutableStateFlow(0)

    private var highScore = 0
    private var oldScore = 0
    private var score = 0
    private var roundWinnablePoints = 0

    private var isFlagGame: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private var isAbortRequest: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var isGameOver: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private var countries: List<CountryModel> = listOf()
    private var results: MutableList<Pair<CountryModel, Boolean>> = mutableListOf()

    val uiState: StateFlow<GameScreenState> =
        combine(isFlagGame, isAbortRequest, isGameOver, remainingTime, matchCountries) { isFlagGame, isAbortRequest, isGameOver, time, countries ->
            GameScreenState(
                matchItem = matchItem,
                isFlagsGame = isFlagGame,
                countries = countries,
                results = results,
                time = time.timeStringFromSeconds(),
                shouldFlashTime = time < 10 || time == 60 || time == 30,
                highScore = highScore.toZeroPaddedString(3),
                score = score.toZeroPaddedString(3),
                isScoreUp = score > oldScore,
                isAbortRequest = isAbortRequest,
                isGameOver = isGameOver
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = GameScreenState.empty
        )

    init {
        viewModelScope.launch {
            countries = countriesRepository.getAllCountries().shuffled()
            getHighScore()
            clearGame()
        }
    }

    fun initialiseGame() {
        clearGame()
        roundWinnablePoints = 3
        nextCountryIndex = 0
        countries = countries.shuffled()
        matchItem = countries[nextCountryIndex]
        matchCountries.value = countries.take(4).shuffled().map { it to true }
        remainingTime.value = GAME_DURATION_SECONDS
        updateTotalTime()
        startDisableCardCountdown()
    }

    fun onItemDropped(item: String) {
        disableCardCountdownJob.cancel()
        if (remainingTime.value > 0) {
            evaluateDrop(item)
            if (!isGameOver.value) {
                pickNextCard()
                roundWinnablePoints = 3
                startDisableCardCountdown()
            }
        }
    }

    fun onSelectorChanged() {
        isFlagGame.value = !isFlagGame.value
    }

    fun abortDialogToggle() {
        if (remainingTime.value != 0) {
            isAbortRequest.value = !isAbortRequest.value
        }
    }

    fun clearGame() {
        disableCardCountdownJob.cancel()
        mainTimeCountdownJob.cancel()
        score = 0
        oldScore = 0
        getHighScore()
        results.clear()
        isAbortRequest.value = false
        remainingTime.value = 0
    }

    private fun getHighScoreKey(): String =
        if (isFlagGame.value) FLAGS_HIGH_SCORE_KEY else CAPITALS_HIGH_SCORE_KEY

    private fun getHighScore() {
        highScore = sharedPreferences.getInt(getHighScoreKey(), 0)
    }

    private fun saveHighScore() {
        if (highScore < score) {
            highScore = score
            with(sharedPreferences.edit()) {
                putInt(getHighScoreKey(), highScore)
                apply()
            }
        }
    }

    private fun updateTotalTime() {
        mainTimeCountdownJob = viewModelScope.launch {
            while (remainingTime.value > 0) {
                delay(1000)
                remainingTime.value -= 1
            }
            saveHighScore()
        }
    }

    private fun startDisableCardCountdown() {
        disableCardCountdownJob = viewModelScope.launch {
            delay(DISABLE_CARD_INTERVAL_SECONDS * 1000L)
            disableOneCard()
        }
    }

    private fun disableOneCard() {
        roundWinnablePoints -= 1
        var countryDisabled = false
        matchCountries.value = matchCountries.value.map {
            if (it.second && it.first != matchItem && !countryDisabled) {
                countryDisabled = true
                it.first to false
            } else {
                it
            }
        }
        if (roundWinnablePoints > 1) startDisableCardCountdown()
    }

    private fun evaluateDrop(card: String) {
        oldScore = score
        if (matchItem.name == card || matchItem.capital == card) {
            score += roundWinnablePoints
            results.add(matchItem to true)
            if (roundWinnablePoints == 3) remainingTime.value += 1
        } else {
            score -= 4 - roundWinnablePoints
            results.add(matchItem to false)
        }
        if (results.size == countries.size) {
            score += remainingTime.value
            isGameOver.value = true
            remainingTime.value = 0
        }
    }

    private fun pickNextCard() {
        nextCountryIndex = nextCountryIndex.ascendInRange(countries.size - 1)
        matchItem = countries[nextCountryIndex]
        matchCountries.value = countries.takeRandomIncluding(4, matchItem).map { it to true }
    }

    companion object {
        const val FLAGS_HIGH_SCORE_KEY = "flags_high_score"
        const val CAPITALS_HIGH_SCORE_KEY = "capitals_high_score"
    }
}
