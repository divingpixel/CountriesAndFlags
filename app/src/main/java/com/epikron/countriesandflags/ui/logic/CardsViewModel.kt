package com.epikron.countriesandflags.ui.logic

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epikron.countriesandflags.CARDS_COUNT
import com.epikron.countriesandflags.CARD_MAX_ANGLE
import com.epikron.countriesandflags.ui.models.CardState
import com.epikron.countriesandflags.ui.models.CardsScreenState
import com.epikron.countriesandflags.util.ConnectivityObserver
import com.epikron.data.data.models.ContinentModel
import com.epikron.data.data.models.CountryModel
import com.epikron.data.data.repository.ContinentsRepository
import com.epikron.data.data.repository.CountriesRepository
import com.epikron.data.data.repository.IpRepository
import com.epikron.utils.ascendInRange
import com.epikron.utils.descendInRange
import com.epikron.utils.random
import com.epikron.utils.shift
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardsViewModel @Inject constructor(
    private val countriesRepository: CountriesRepository,
    private val continentsRepository: ContinentsRepository,
    private val sharedPreferences: SharedPreferences,
    private val ipRepository: IpRepository
) : ViewModel() {

    private var countries: List<CountryModel> = listOf()
    private var continents: List<ContinentModel> = listOf()
    private var userCountryCode: String = ""
    private val rotations: MutableList<Float> = mutableListOf()
    private var shouldDisplayDetails: Boolean = false
    private var cardsList: MutableStateFlow<List<CardState>> = MutableStateFlow(listOf())
    private var currentContinent: MutableStateFlow<String> = MutableStateFlow("")
    private var visibleCountryIndex = CARDS_COUNT - 1
    private var nextCountryIndex = 0
    private var appIsStarting = true

    val uiState: StateFlow<CardsScreenState> = cardsList.combine(currentContinent) { cardsList, currentContinent ->
        CardsScreenState(
            continents = continents,
            currentContinent = currentContinent,
            cards = cardsList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = CardsScreenState(
            continents = continents,
            currentContinent = currentContinent.value,
            cards = cardsList.value
        )
    )

    init {
        viewModelScope.launch {
            countries = countriesRepository.getAllCountries()
            continents = continentsRepository.getContinents()
            userCountryCode = if (ConnectivityObserver.isNetworkAvailable) {
                val countryCode = ipRepository.getIpCountryCode() ?: countries.first().code
                sharedPreferences.edit { putString(COUNTRY_CODE_KEY, countryCode) }
                countryCode
            } else {
                sharedPreferences.getString(COUNTRY_CODE_KEY, null) ?: countries.first().code
            }
            updateCountriesForContinent(countriesRepository.getContinentForCountry(userCountryCode))
        }
    }

    private fun generateCountriesList() {
        val newCountryList: MutableList<CountryModel> = mutableListOf()
        if (appIsStarting) {
            appIsStarting = false
            val userCountryIndex = countries.size - countries.indexOf(countries.first { it.code == userCountryCode })
            countries = countries.shift(userCountryIndex).toList()
        }
        if (countries.isNotEmpty()) {
            if (countries.size >= CARDS_COUNT) {
                newCountryList.addAll(countries.take(CARDS_COUNT))
                nextCountryIndex = newCountryList.size - 1
            } else {
                newCountryList.addAll(countries)
                newCountryList.addAll(countries.take(CARDS_COUNT - countries.size))
                nextCountryIndex = CARDS_COUNT - countries.size - 1
            }
        } else {
            repeat(CARDS_COUNT) {
                newCountryList.add(CountryModel.empty)
            }
        }
        newCountryList.shift((CARDS_COUNT - 1) - visibleCountryIndex)
        newCountryList.reverse()
        generateRotationsList()
        updateDisplayCountryList(newCountryList)
    }

    private fun generateAngle(isNegative: Boolean): Float {
        return if (isNegative) -CARD_MAX_ANGLE.random else CARD_MAX_ANGLE.random
    }

    private fun generateRotationsList() {
        repeat(CARDS_COUNT) { index ->
            rotations.add(generateAngle(index.mod(2) == 0))
        }
    }

    private fun updateRotations(updateIndex: Int) {
        if (rotations.isNotEmpty()) {
            val oldRotation = rotations[updateIndex]
            rotations.removeAt(updateIndex)
            rotations.add(updateIndex, generateAngle(oldRotation < 0))
        } else {
            generateRotationsList()
        }
    }

    private fun updateDisplayCountryList(countries: List<CountryModel> = cardsList.value.map { it.data }) {
        val result: MutableList<CardState> = mutableListOf()
        var zIndex = (CARDS_COUNT - 1) - visibleCountryIndex
        repeat(CARDS_COUNT) { index ->
            val showDetails = shouldDisplayDetails && zIndex == CARDS_COUNT - 1
            result.add(CardState(countries[index], zIndex, rotations[index], showDetails))
            zIndex = zIndex.ascendInRange(CARDS_COUNT - 1)
        }
        cardsList.value = result
    }

    fun updateOnSwipeOut(cardIndex: Int) {
        visibleCountryIndex = cardIndex.descendInRange(CARDS_COUNT - 1)
        updateRotations(cardIndex)
        updateDisplayCountryList()
    }

    fun updateVisibleCountries(updateIndex: Int) {
        val updatedCountryList: MutableList<CountryModel> = mutableListOf()
        updatedCountryList.addAll(cardsList.value.map { it.data })
        nextCountryIndex = nextCountryIndex.ascendInRange(countries.size - 1)
        updatedCountryList.removeAt(updateIndex)
        updatedCountryList.add(updateIndex, countries[nextCountryIndex])
        updateDisplayCountryList(updatedCountryList)
    }

    fun updateCountriesForContinent(continentName: String, action: (() -> Unit)? = null) {
        currentContinent.value = continentName
        viewModelScope.launch {
            countries = countriesRepository.getCountriesForContinent(continentName)
            generateCountriesList()
            action?.invoke()
        }
    }

    fun toggleDetails(shouldShowDetails: Boolean) {
        shouldDisplayDetails = shouldShowDetails
        updateDisplayCountryList()
    }

    companion object {
        const val COUNTRY_CODE_KEY = "last_country_code"
    }
}
