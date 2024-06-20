@file:OptIn(ExperimentalCoroutinesApi::class)

package com.epikron.countriesandflags.ui.logic

import android.content.SharedPreferences
import app.cash.turbine.test
import com.epikron.countriesandflags.DISABLE_CARD_INTERVAL_SECONDS
import com.epikron.countriesandflags.GAME_DURATION_SECONDS
import com.epikron.data.data.models.ContinentModel
import com.epikron.data.data.models.CountryModel
import com.epikron.data.data.repository.CountriesRepository
import com.epikron.utils.timeStringFromSeconds
import com.epikron.utils.toZeroPaddedString
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GameViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val mockCountriesRepository: CountriesRepository = mockk()
    private val mockSharedPreferences: SharedPreferences = mockk()
    private val mockSharedPreferencesEditor: SharedPreferences.Editor = mockk()

    private val countries = CountryModel.generateRandomCountriesList(18, ContinentModel.list[3].name)
    private val gameTime = GAME_DURATION_SECONDS.timeStringFromSeconds()

    private lateinit var mockGameViewModel: GameViewModel

    @Before
    fun setUp() {
        coEvery { mockCountriesRepository.getAllCountries() } returns countries
        coEvery { mockSharedPreferencesEditor.putInt(any(), any()) } returns mockSharedPreferencesEditor
        coEvery { mockSharedPreferencesEditor.apply() } returns Unit
        coEvery { mockSharedPreferences.edit() } returns mockSharedPreferencesEditor
        coEvery { mockSharedPreferences.getInt(any(), any()) } returns 123
        runTest {
            mockGameViewModel = GameViewModel(mockCountriesRepository, mockSharedPreferences)
            advanceUntilIdle()
        }
    }

    @Test
    fun `WHEN game screen is selected THEN the correct uiState is generated`() {
        val currentUiState = mockGameViewModel.uiState.value
        coVerify { mockCountriesRepository.getAllCountries() }
        coVerify { mockSharedPreferences.getInt(any(), any()) }
        assertTrue(currentUiState.isFlagsGame)
        assertTrue(currentUiState.isGameOver.not())
        assertTrue(currentUiState.isAbortRequest.not())
        assertTrue(currentUiState.time == "00:00")
        assertTrue(currentUiState.highScore == "123")
        assertTrue(currentUiState.score == "000")
        assertTrue(currentUiState.results.isEmpty())
        assertTrue(currentUiState.countries.isEmpty())
    }

    @Test
    fun `WHEN the flags option is selected at the game start THEN the correct uiState is generated`() {
        runTest {
            mockGameViewModel.uiState.test {
                assertTrue(awaitItem().isFlagsGame)
                mockGameViewModel.onSelectorChanged()
                assertTrue(awaitItem().isFlagsGame.not())
            }
        }
    }

    @Test
    fun `WHEN the abort game button is pressed during the game THEN the correct uiState is generated`() {
        runTest {
            mockGameViewModel.uiState.test {
                assertTrue(awaitItem().isAbortRequest.not())
                mockGameViewModel.initialiseGame()
                var expectedUiState = awaitItem()
                assertTrue(expectedUiState.time == gameTime)
                assertTrue(expectedUiState.isAbortRequest.not())
                mockGameViewModel.abortDialogToggle()
                expectedUiState = awaitItem()
                assertTrue(expectedUiState.isAbortRequest)
            }
        }
    }

    @Test
    fun `WHEN start game is pressed THEN the correct uiState is generated`() {
        runTest {
            mockGameViewModel.uiState.test {
                assertTrue(awaitItem().time == "00:00")
                mockGameViewModel.initialiseGame()
                val expectedUiState = awaitItem()
                assertTrue(expectedUiState.time == gameTime)
                assertTrue(expectedUiState.countries.size == 4)
                assertTrue(expectedUiState.matchItem in expectedUiState.countries.map { it.first })
            }
        }
    }

    @Test
    fun `WHEN the game is aborted THEN the correct uiState is generated`() {
        runTest {
            mockGameViewModel.uiState.test {
                assertTrue(awaitItem().isAbortRequest.not())
                mockGameViewModel.initialiseGame()
                var expectedUiState = awaitItem()
                assertTrue(expectedUiState.time == gameTime)
                mockGameViewModel.clearGame()
                expectedUiState = awaitItem()
                assertTrue(expectedUiState.time == "00:00")
                assertTrue(expectedUiState.highScore == "123")
                assertTrue(expectedUiState.score == "000")
                assertTrue(expectedUiState.results.isEmpty())
            }
        }
    }

    @Test
    fun `WHEN item is dropped in the correct position THEN the correct uiState is generated`() {
        runTest {
            mockGameViewModel.uiState.test {
                assertTrue(awaitItem().time == "00:00")
                mockGameViewModel.initialiseGame()
                var expectedUiState = awaitItem()
                assertTrue(expectedUiState.time == gameTime)
                assertTrue(expectedUiState.countries.size == 4)
                assertTrue(expectedUiState.matchItem in expectedUiState.countries.map { it.first })

                mockGameViewModel.onItemDropped(expectedUiState.matchItem.name)
                expectedUiState = awaitItem()
                assertTrue(expectedUiState.time == (GAME_DURATION_SECONDS + 1).timeStringFromSeconds())
                assertTrue(expectedUiState.score == 3.toZeroPaddedString(3))
                assertTrue(expectedUiState.isScoreUp)
            }
        }
    }

    @Test
    fun `WHEN item is dropped in the wrong position THEN the correct uiState is generated`() {
        runTest {
            mockGameViewModel.uiState.test {
                assertTrue(awaitItem().time == "00:00")
                mockGameViewModel.initialiseGame()
                var expectedUiState = awaitItem()
                assertTrue(expectedUiState.time == gameTime)
                assertTrue(expectedUiState.countries.size == 4)
                assertTrue(expectedUiState.matchItem in expectedUiState.countries.map { it.first })

                val wrongCountry = (expectedUiState.countries.map { it.first } - expectedUiState.matchItem).first()
                mockGameViewModel.onItemDropped(wrongCountry.name)
                expectedUiState = awaitItem()
                assertTrue(expectedUiState.score == (-1).toZeroPaddedString(3))
                assertTrue(expectedUiState.isScoreUp.not())
            }
        }
    }

    @Test
    fun `WHEN no item is dropped in the first DISABLE_CARD_INTERVAL_SECONDS THEN the correct uiState is generated`() {
        runTest {
            mockGameViewModel.uiState.test {
                assertTrue(awaitItem().time == "00:00")
                mockGameViewModel.initialiseGame()
                assertTrue(awaitItem().time == gameTime)
                val expectedTime = (GAME_DURATION_SECONDS - DISABLE_CARD_INTERVAL_SECONDS).timeStringFromSeconds()
                var expectedUiState = awaitItem()
                while (expectedUiState.time != expectedTime) {
                    expectedUiState = awaitItem()
                }
                assertTrue(expectedUiState.countries.map { it.second }.any { it.not() })
            }
        }
    }
}
