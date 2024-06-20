@file:OptIn(ExperimentalCoroutinesApi::class)

package com.epikron.countriesandflags.ui.logic

import android.content.SharedPreferences
import app.cash.turbine.test
import com.epikron.countriesandflags.CARDS_COUNT
import com.epikron.countriesandflags.util.ConnectivityObserver
import com.epikron.data.data.models.ContinentModel
import com.epikron.data.data.models.CountryModel
import com.epikron.data.data.repository.ContinentsRepository
import com.epikron.data.data.repository.CountriesRepository
import com.epikron.data.data.repository.IpRepository
import com.epikron.utils.ascendInRange
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CardsViewModelTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val mockIpRepository: IpRepository = mockk()
    private val mockContinentsRepository: ContinentsRepository = mockk()
    private val mockCountriesRepository: CountriesRepository = mockk()
    private val mockSharedPreferences: SharedPreferences = mockk()
    private val mockSharedPreferencesEditor: SharedPreferences.Editor = mockk()

    private val countriesCount = 7
    private val continent0countries = CountryModel.generateRandomCountriesList(countriesCount, ContinentModel.list[0].name)
    private val continent1countries = CountryModel.generateRandomCountriesList(countriesCount, ContinentModel.list[1].name)
    private val countries = continent0countries + continent1countries

    private lateinit var mockCardsViewModel: CardsViewModel

    @Before
    fun setUp() {
        mockkObject(ConnectivityObserver)
        every { ConnectivityObserver.register(any()) } just runs
        coEvery { mockIpRepository.getIpCountryCode() } returns countries[0].code
        coEvery { mockCountriesRepository.getAllCountries() } returns countries
        coEvery { mockCountriesRepository.getContinentForCountry(any()) } returns ContinentModel.list[0].name
        coEvery { mockCountriesRepository.getCountriesForContinent(ContinentModel.list[0].name) } returns continent0countries
        coEvery { mockCountriesRepository.getCountriesForContinent(ContinentModel.list[1].name) } returns continent1countries
        coEvery { mockContinentsRepository.getContinents() } returns ContinentModel.list
        coEvery { mockSharedPreferencesEditor.putString(any(), any()) } returns mockSharedPreferencesEditor
        coEvery { mockSharedPreferencesEditor.apply() } returns Unit
        coEvery { mockSharedPreferences.edit() } returns mockSharedPreferencesEditor
        coEvery { mockSharedPreferences.getString(any(), any()) } returns countries[0].code
    }

    @Test
    fun `WHEN cards screen is initialised and network available THEN the correct cards are displayed`() {
        every { ConnectivityObserver.isNetworkAvailable } returns true
        runTest {
            mockCardsViewModel = CardsViewModel(mockCountriesRepository, mockContinentsRepository, mockSharedPreferences, mockIpRepository)
            advanceUntilIdle()
        }
        coVerify { mockContinentsRepository.getContinents() }
        coVerify { mockCountriesRepository.getAllCountries() }
        coVerify { mockIpRepository.getIpCountryCode() }
        coVerify { mockSharedPreferences.edit() }
        runTest {
            mockCardsViewModel.uiState.test {
                val currentUiState = awaitItem()
                assertTrue(currentUiState.cards.size == CARDS_COUNT)
                assertTrue(currentUiState.currentContinent == ContinentModel.list[0].name)
                assertTrue(continent0countries.map { it.name }.containsAll(currentUiState.cards.map { it.data.name }))
            }
        }
    }

    @Test
    fun `WHEN cards screen is initialised and network is unavailable THEN the correct cards are displayed`() {
        every { ConnectivityObserver.isNetworkAvailable } returns false
        runTest {
            mockCardsViewModel = CardsViewModel(mockCountriesRepository, mockContinentsRepository, mockSharedPreferences, mockIpRepository)
            advanceUntilIdle()
        }
        coVerify { mockContinentsRepository.getContinents() }
        coVerify { mockCountriesRepository.getAllCountries() }
        coVerify(exactly = 0) { mockIpRepository.getIpCountryCode() }
        coVerify { mockSharedPreferences.getString(any(), any()) }
        runTest {
            mockCardsViewModel.uiState.test {
                val currentUiState = awaitItem()
                assertTrue(currentUiState.cards.size == CARDS_COUNT)
                assertTrue(currentUiState.currentContinent == ContinentModel.list[0].name)
                assertTrue(continent0countries.map { it.name }.containsAll(currentUiState.cards.map { it.data.name }))
            }
        }
    }

    @Test
    fun `WHEN card is swiped THEN cards are updated`() {
        runTest {
            mockCardsViewModel = CardsViewModel(mockCountriesRepository, mockContinentsRepository, mockSharedPreferences, mockIpRepository)
            advanceUntilIdle()
            mockCardsViewModel.uiState.test {
                var currentUiState = awaitItem()
                assertTrue(currentUiState.cards.size == CARDS_COUNT)
                assertTrue(currentUiState.currentContinent == ContinentModel.list[0].name)
                assertTrue(continent0countries.map { it.name }.containsAll(currentUiState.cards.map { it.data.name }))

                val cardChangeIndex = 5
                val previousCardZIndex = currentUiState.cards[cardChangeIndex].zIndex
                mockCardsViewModel.updateOnSwipeOut(cardChangeIndex)
                currentUiState = awaitItem()
                val cardZIndex = currentUiState.cards[cardChangeIndex].zIndex
                assertTrue(cardZIndex == previousCardZIndex.ascendInRange(CARDS_COUNT - 1))

                val remainingCountries = continent0countries.map { it.name } - currentUiState.cards.map { it.data.name }.toSet()
                mockCardsViewModel.updateVisibleCountries(cardChangeIndex)
                currentUiState = awaitItem()
                assertTrue(remainingCountries.first() in currentUiState.cards.map { it.data.name })
            }
        }
    }

    @Test
    fun `WHEN continent is changed THEN cards are updated`() {
        runTest {
            mockCardsViewModel = CardsViewModel(mockCountriesRepository, mockContinentsRepository, mockSharedPreferences, mockIpRepository)
            advanceUntilIdle()
            mockCardsViewModel.uiState.test {
                var currentUiState = awaitItem()
                assertTrue(currentUiState.cards.size == CARDS_COUNT)
                assertTrue(currentUiState.currentContinent == ContinentModel.list[0].name)
                assertTrue(continent0countries.map { it.name }.containsAll(currentUiState.cards.map { it.data.name }))
                mockCardsViewModel.updateCountriesForContinent(ContinentModel.list[1].name)
                currentUiState = awaitItem()
                assertTrue(currentUiState.cards.size == CARDS_COUNT)
                assertTrue(currentUiState.currentContinent == ContinentModel.list[1].name)
                currentUiState = awaitItem()
                assertTrue(continent1countries.map { it.name }.containsAll(currentUiState.cards.map { it.data.name }))
            }
        }
    }

    @Test
    fun `WHEN country details are toggled THEN the uiState changes accordingly`() {
        runTest {
            mockCardsViewModel = CardsViewModel(mockCountriesRepository, mockContinentsRepository, mockSharedPreferences, mockIpRepository)
            advanceUntilIdle()
            mockCardsViewModel.uiState.test {
                var currentUiState = awaitItem()
                assertTrue(currentUiState.cards.map { it.showDetails }.any { it.not() })
                mockCardsViewModel.toggleDetails(true)
                currentUiState = awaitItem()
                assertTrue(currentUiState.cards.map { it.showDetails }.any { it })
            }
        }
    }
}
