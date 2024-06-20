package com.epikron.countriesandflags.ui.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epikron.data.data.models.ContinentModel
import com.epikron.data.data.models.CountryModel
import com.epikron.data.data.repository.ContinentsRepository
import com.epikron.data.data.repository.CountriesRepository
import com.epikron.countriesandflags.ui.models.SearchScreenState
import com.epikron.utils.containsAllChars
import com.epikron.utils.containsAnyWord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val countriesRepository: CountriesRepository,
    private val continentsRepository: ContinentsRepository
) : ViewModel() {

    private var dbCountries: MutableStateFlow<List<CountryModel>> = MutableStateFlow(listOf())
    private var dbContinents: List<ContinentModel> = listOf()

    private var searchText: MutableStateFlow<String> = MutableStateFlow("")
    private var selectedCountry: MutableStateFlow<CountryModel> = MutableStateFlow(CountryModel.empty)

    val uiState: StateFlow<SearchScreenState> = combine(searchText, dbCountries, selectedCountry) { text, countries, country ->
        SearchScreenState(makeContinentsCountries(filterCountries(text, countries)), text, country)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SearchScreenState(makeContinentsCountries(dbCountries.value), "", CountryModel.empty)
    )

    init {
        viewModelScope.launch {
            dbContinents = continentsRepository.getContinents()
            dbCountries.value = countriesRepository.getAllCountries().sortedBy { it.name }
        }
    }

    private fun filterCountries(text: String, countries: List<CountryModel>): List<CountryModel> {
        return if (text.isBlank()) {
            countries
        } else {
            if (text.first().isUpperCase()) {
                countries.filter { country -> country.name.containsAllChars(text.trim(), false) }
            } else {
                countries.filter { country -> country.name.uppercase().containsAnyWord(text.trim().uppercase()) }
            }
        }
    }

    private fun makeContinentsCountries(countries: List<CountryModel>): Map<String, List<CountryModel>> {
        val result: HashMap<String, List<CountryModel>> = hashMapOf()
        dbContinents.forEach { continent ->
            val continentCountries = countries.filter { it.continent == continent.name }
            if (continentCountries.isNotEmpty()) result[continent.name] = continentCountries
        }
        return result
    }

    fun onSearchTextChange(text: String) {
        searchText.value = text
    }

    fun onCountrySelected(countryName: String = "") {
        if (countryName.isNotEmpty())
            selectedCountry.value = dbCountries.value.firstOrNull { it.name == countryName } ?: CountryModel.empty
        else CountryModel.empty
    }
}
