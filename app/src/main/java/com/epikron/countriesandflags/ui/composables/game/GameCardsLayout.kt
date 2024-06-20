@file:OptIn(ExperimentalLayoutApi::class)

package com.epikron.countriesandflags.ui.composables.game

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.epikron.countriesandflags.BUTTON_ELEVATION
import com.epikron.countriesandflags.R
import com.epikron.countriesandflags.ui.theme.Space
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.countriesandflags.ui.theme.mainTypography
import com.epikron.countriesandflags.util.dpToPx
import com.epikron.data.data.models.CountryModel
import com.epikron.utils.fifth
import com.epikron.utils.getOrDefault
import com.epikron.utils.half

@Composable
fun GameCardsLayoutLandscape(
    countries: List<Pair<CountryModel, Boolean>>,
    item: CountryModel,
    isFlagsGame: Boolean,
    width: Int,
    onItemAction: (String) -> Unit,
    onAbortClicked: () -> Unit
) {
    @Composable
    fun getCard(country: Pair<CountryModel, Boolean>) =
        GameSmallCard(
            country = country.first,
            isEnabled = country.second,
            isFlagsGame = isFlagsGame,
            width = width.fifth,
            onItemAction = onItemAction
        )
    Column(modifier = Modifier.wrapContentHeight()) {
        FlowRow(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            maxLines = 1,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            countries.forEachIndexed { index, country ->
                if (index == 2) {
                    GameActionItem(
                        country = item,
                        isFlagsGame = isFlagsGame,
                        size = width.fifth
                    )
                }
                getCard(country = country)
            }
        }
        ElevatedButton(
            modifier = Modifier
                .padding(Space.huge, Space.tiny, Space.huge, Space.large)
                .fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.secondary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = BUTTON_ELEVATION.dp),
            onClick = { onAbortClicked.invoke() }
        ) {
            Text(
                text = stringResource(id = R.string.test_button_abort),
                style = mainTypography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun GameCardsLayoutPortrait(
    countries: List<Pair<CountryModel, Boolean>>,
    item: CountryModel,
    isFlagsGame: Boolean,
    width: Int,
    onItemAction: (String) -> Unit,
    onAbortClicked: () -> Unit
) {
    @Composable
    fun getCard(country: Pair<CountryModel, Boolean>) =
        GameSmallCard(
            country = country.first,
            isEnabled = country.second,
            isFlagsGame = isFlagsGame,
            width = width.half,
            onItemAction = onItemAction
        )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.weight(1.5f), verticalAlignment = Alignment.CenterVertically) {
            getCard(country = countries.getOrDefault(0,CountryModel.empty to false))
            getCard(country = countries.getOrDefault(1,CountryModel.empty to false))
        }
        GameActionItem(
            country = item,
            isFlagsGame = isFlagsGame,
            size = width
        )
        Row(modifier = Modifier.weight(1.5f), verticalAlignment = Alignment.CenterVertically) {
            getCard(country = countries.getOrDefault(2,CountryModel.empty to false))
            getCard(country = countries.getOrDefault(3,CountryModel.empty to false))
        }
        ElevatedButton(
            modifier = Modifier
                .padding(Space.huge, Space.small, Space.huge, Space.large)
                .fillMaxWidth()
                .wrapContentHeight(),
            colors = ButtonDefaults.textButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.secondary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = BUTTON_ELEVATION.dp),
            onClick = { onAbortClicked.invoke() }
        ) {
            Text(
                modifier = Modifier.padding(Space.small),
                text = stringResource(id = R.string.test_button_abort),
                style = mainTypography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun GameCardsLayout(
    countries: List<Pair<CountryModel, Boolean>>,
    item: CountryModel,
    isFlagsGame: Boolean,
    width: Int,
    onItemAction: (String) -> Unit = {},
    onAbortClicked: () -> Unit = {},
    orientation: Int = LocalConfiguration.current.orientation
) {
    if (orientation == Configuration.ORIENTATION_PORTRAIT)
        GameCardsLayoutPortrait(
            countries = countries, item = item, isFlagsGame = isFlagsGame, width = width, onItemAction = onItemAction, onAbortClicked = onAbortClicked
        )
    else
        GameCardsLayoutLandscape(
            countries = countries, item = item, isFlagsGame = isFlagsGame, width = width, onItemAction = onItemAction, onAbortClicked = onAbortClicked
        )

}

@Preview
@Composable
fun PreviewCardsLayout() {
    SwipingCardsTheme {
        GameCardsLayout(
            countries = (CountryModel.previewTestList + CountryModel.test).map { it to true },
            item = CountryModel.test,
            isFlagsGame = true,
            width = LocalConfiguration.current.screenWidthDp.dpToPx()
        )
    }
}

@Preview(device = "spec:parent=pixel_3,orientation=landscape", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewLandscapeCardsLayout() {
    SwipingCardsTheme {
        GameCardsLayout(
            countries = (CountryModel.previewTestList + CountryModel.test).map { it to true },
            item = CountryModel.testLong,
            isFlagsGame = false,
            width = LocalConfiguration.current.screenWidthDp.dpToPx()
        )
    }
}