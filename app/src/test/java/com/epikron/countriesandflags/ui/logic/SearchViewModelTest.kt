@file:OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)

package com.epikron.countriesandflags.ui.logic

import app.cash.turbine.test
import com.epikron.data.data.models.ContinentModel
import com.epikron.data.data.models.CountryModel
import com.epikron.data.data.repository.ContinentsRepository
import com.epikron.data.data.repository.CountriesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val mockContinentsRepository: ContinentsRepository = mockk()
    private val mockCountriesRepository: CountriesRepository = mockk()

    private val countriesCount = 7
    private val continent0countries = CountryModel.generateRandomCountriesList(countriesCount, ContinentModel.list[0].name)
    private val continent1countries = CountryModel.generateRandomCountriesList(countriesCount, ContinentModel.list[1].name)
    private val countries = continent0countries + continent1countries

    private lateinit var mockSearchViewModel: SearchViewModel

    @Before
    fun setUp() {
        coEvery { mockCountriesRepository.getAllCountries() } returns countries
        coEvery { mockContinentsRepository.getContinents() } returns ContinentModel.list
        runTest {
            mockSearchViewModel = SearchViewModel(mockCountriesRepository, mockContinentsRepository)
            advanceUntilIdle()
        }
    }

    @Test
    fun `WHEN search screen is selected THEN the list displays all the countries and continents`() {
        coVerify { mockContinentsRepository.getContinents() }
        coVerify { mockCountriesRepository.getAllCountries() }
        runTest {
            mockSearchViewModel.uiState.test {
                val currentUiState = awaitItem()
                assertTrue(currentUiState.continentsCountries.values.flatten().size == countries.size)
                assertTrue(currentUiState.continentsCountries.keys.size == 2)
            }
        }
    }

    @Test
    fun `WHEN input text changes THEN the list updates with the countries found`() {
        runTest {
            mockSearchViewModel.uiState.test {
                var currentUiState = awaitItem()
                assertTrue(currentUiState.continentsCountries.values.flatten().size == countries.size)
                val searchString = countries[0].name
                mockSearchViewModel.onSearchTextChange(searchString)
                currentUiState = awaitItem()
                assertTrue(currentUiState.searchText == searchString)
                assertTrue(currentUiState.continentsCountries.values.flatten().size == countries.filter { it.name == searchString }.size)
            }
        }
    }

    @Test
    fun `WHEN a country is selected THEN the uiState changes accordingly`() {
        runTest {
            mockSearchViewModel.uiState.test {
                var currentUiState = awaitItem()
                assertTrue(currentUiState.selectedCountry == CountryModel.empty)
                val selectedCountryName = countries[0].name
                mockSearchViewModel.onCountrySelected(selectedCountryName)
                currentUiState = awaitItem()
                assertTrue(currentUiState.selectedCountry.name == selectedCountryName)
            }
        }
    }
}
