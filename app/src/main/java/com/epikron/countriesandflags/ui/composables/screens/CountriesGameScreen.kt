package com.epikron.countriesandflags.ui.composables.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.epikron.countriesandflags.R
import com.epikron.countriesandflags.ui.composables.MenuBar
import com.epikron.countriesandflags.ui.composables.dialogs.AbortGameDialog
import com.epikron.countriesandflags.ui.composables.dialogs.FinishGameDialog
import com.epikron.countriesandflags.ui.composables.game.GameCardsLayout
import com.epikron.countriesandflags.ui.composables.game.GameHeader
import com.epikron.countriesandflags.ui.composables.game.GameSelector
import com.epikron.countriesandflags.ui.composables.game.GameStatistics
import com.epikron.countriesandflags.ui.models.GameScreenState
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.data.data.models.CountryModel
import com.epikron.utils.toZeroPaddedString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ColumnScope.GameEnd(
    uiState: GameScreenState,
    onStartClicked: () -> Unit = {},
    onSelectorChange: () -> Unit = {},
) {
    if (uiState.results.isNotEmpty())
        Box(modifier = Modifier.weight(1f)) {
            GameStatistics(
                results = uiState.results,
                isFlagsGame = uiState.isFlagsGame
            )
        }
    GameSelector(
        shouldShowHint = uiState.results.isEmpty(),
        isFlagsGame = uiState.isFlagsGame,
        onSelectorChange = onSelectorChange,
        onStartClicked = onStartClicked
    )
}

@Composable
fun CountriesGameScreen(
    uiState: GameScreenState,
    onStartClicked: () -> Unit = {},
    onItemAction: (String) -> Unit = {},
    onSelectorChange: () -> Unit = {},
    onDismissDialog: () -> Unit = {},
    onAbortConfirmed: () -> Unit = {},
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    var width by remember { mutableIntStateOf(1) }

    fun closeDrawer(action: (() -> Unit)? = null) {
        coroutineScope.launch { drawerState.close() }.invokeOnCompletion {
            action?.invoke()
        }
    }

    if (uiState.isAbortRequest) {
        AbortGameDialog(
            onDismissRequest = {
                closeDrawer { onDismissDialog.invoke() }
            },
            onConfirmAction = onAbortConfirmed
        )
    }

    if (uiState.isGameOver) {
        FinishGameDialog(
            onDismissRequest = {
                closeDrawer { onDismissDialog.invoke() }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .onPlaced { coordinates ->
                width = coordinates.size.width.coerceAtLeast(1)
            }
    ) {
        MenuBar(stringResource(id = R.string.top_bar_game_screen_text), drawerState)
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            if (uiState.results.isNotEmpty() || uiState.time != "00:00")
                GameHeader(
                    highScore = uiState.highScore,
                    currentScore = uiState.score,
                    isScoreIncreased = uiState.isScoreUp,
                    time = uiState.time,
                    shouldFlashTime = uiState.shouldFlashTime
                )
            if (uiState.time == "00:00") {
                GameEnd(
                    uiState = uiState,
                    onStartClicked = onStartClicked,
                    onSelectorChange = onSelectorChange
                )
            } else {
                GameCardsLayout(
                    countries = uiState.countries,
                    item = uiState.matchItem,
                    isFlagsGame = uiState.isFlagsGame,
                    width = width,
                    onItemAction = onItemAction,
                    onAbortClicked = onDismissDialog
                )
            }
        }
    }
}

internal val previewCountries = (CountryModel.previewTestList.map { it to true }) + (CountryModel.testLong to false)
internal val previewResults = CountryModel.previewTestList.map { it to true } + CountryModel.previewTestList.map { it to false }

internal val previewGameScreenState = GameScreenState(
    matchItem = CountryModel.testLong,
    isFlagsGame = false,
    countries = previewCountries,
    results = previewResults,
    time = "05:00",
    shouldFlashTime = false,
    highScore = "234",
    score = 34.toZeroPaddedString(3),
    isScoreUp = true,
    isAbortRequest = false,
    isGameOver = false
)

@Preview
@Composable
fun PreviewCapitalsGamePage() {
    SwipingCardsTheme {
        CountriesGameScreen(previewGameScreenState)
    }
}

@Preview(device = "id:pixel_2", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewCapitalsGameShortPage() {
    SwipingCardsTheme {
        CountriesGameScreen(previewGameScreenState.copy(isFlagsGame = false))
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewFlagsGamePage() {
    SwipingCardsTheme {
        CountriesGameScreen(previewGameScreenState.copy(isFlagsGame = true, time = "03:45", isScoreUp = false))
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewFlagsAbortGamePage() {
    SwipingCardsTheme {
        CountriesGameScreen(previewGameScreenState.copy(isFlagsGame = true, time = "03:45", isScoreUp = false, isAbortRequest = true))
    }
}

@Preview
@Composable
fun PreviewFinalGamePage() {
    SwipingCardsTheme {
        CountriesGameScreen(previewGameScreenState.copy(time = "00:00"))
    }
}

@Preview(device = "id:Nexus 4", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewFinalGameShortPage() {
    SwipingCardsTheme {
        CountriesGameScreen(previewGameScreenState.copy(time = "00:00", isGameOver = true))
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewStartGamePage() {
    SwipingCardsTheme {
        CountriesGameScreen(previewGameScreenState.copy(results = listOf(), time = "00:00"))
    }
}

@Preview(device = "spec:parent=pixel_3,orientation=landscape", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewLandscapeGameSelector() {
    SwipingCardsTheme {
        CountriesGameScreen(previewGameScreenState)
    }
}
