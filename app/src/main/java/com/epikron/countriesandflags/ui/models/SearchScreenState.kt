package com.epikron.countriesandflags.ui.models

import com.epikron.data.data.models.CountryModel

data class SearchScreenState(
    val continentsCountries: Map<String, List<CountryModel>>,
    val searchText: String,
    val selectedCountry: CountryModel
) {
    companion object {
        val empty = SearchScreenState(mapOf(), "", CountryModel.empty)
    }
}

