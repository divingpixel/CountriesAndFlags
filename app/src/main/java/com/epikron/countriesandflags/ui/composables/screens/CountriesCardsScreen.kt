package com.epikron.countriesandflags.ui.composables.screens

import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.epikron.countriesandflags.BUTTON_ELEVATION
import com.epikron.countriesandflags.CARDS_COUNT
import com.epikron.countriesandflags.R
import com.epikron.data.data.START_CONTINENT
import com.epikron.countriesandflags.ui.composables.MenuBar
import com.epikron.countriesandflags.ui.composables.cards.CountryCard
import com.epikron.countriesandflags.ui.models.CardState
import com.epikron.countriesandflags.ui.theme.Space
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.countriesandflags.ui.theme.mainTypography

@Composable
fun CountriesCardsScreen(
    continent: String,
    displayCountries: List<CardState>,
    onCardSwipedOut: (cardIndex: Int) -> Unit = {},
    onCardUpdate: (cardIndex: Int) -> Unit = {},
    onToggleDetails: (shouldShowDetails: Boolean) -> Unit = {},
    onStartNewGame: () -> Unit = {},
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed)
) {
    var orientation by remember { mutableIntStateOf(Configuration.ORIENTATION_PORTRAIT) }
    val configuration = LocalConfiguration.current
    LaunchedEffect(configuration) {
        snapshotFlow { configuration.orientation }
            .collect { orientation = it }
    }
    Column(
        verticalArrangement = Arrangement.Center
    ) {
        MenuBar(" ${stringResource(id = R.string.top_bar_cards_screen_text)}  $continent", drawerState)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(3f)
                .rotate(
                    animateFloatAsState(
                        targetValue = -(displayCountries.firstOrNull { it.zIndex == CARDS_COUNT - 1 }?.rotation ?: 0f),
                        animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
                        label = "stack_rotation"
                    ).value
                ),
            contentAlignment = Alignment.Center
        ) {
            displayCountries.forEachIndexed { index, card ->
                CountryCard(
                    cardState = card,
                    cardStackIndex = index,
                    onCardSwipedOut = { cardIndex -> onCardSwipedOut.invoke(cardIndex) },
                    onCardUpdate = { cardIndex -> onCardUpdate.invoke(cardIndex) },
                    onToggleDetails = { shouldShowDetails -> onToggleDetails.invoke(shouldShowDetails) }
                )
            }
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .zIndex(-10f),
                contentAlignment = Alignment.Center
            ) {
                ElevatedButton(
                    modifier = Modifier
                        .padding(Space.huge, Space.small, Space.huge, Space.huge)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    colors = ButtonDefaults.textButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = BUTTON_ELEVATION.dp),
                    onClick = { onStartNewGame.invoke() }
                ) {
                    Text(
                        modifier = Modifier.padding(Space.small),
                        text = stringResource(id = R.string.test_button_text),
                        style = mainTypography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewLandingPage() {
    SwipingCardsTheme {
        CountriesCardsScreen(START_CONTINENT, listOf(CardState.test))
    }
}
