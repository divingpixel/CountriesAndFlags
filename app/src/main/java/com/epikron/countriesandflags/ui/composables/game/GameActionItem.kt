@file:OptIn(ExperimentalFoundationApi::class)

package com.epikron.countriesandflags.ui.composables.game

import android.content.ClipData
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.epikron.countriesandflags.FLAG_SIZE_DROP
import com.epikron.countriesandflags.R
import com.epikron.countriesandflags.ui.composables.FlagImageLoader
import com.epikron.countriesandflags.ui.theme.Space
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.countriesandflags.ui.theme.mainTypography
import com.epikron.countriesandflags.util.dpToPx
import com.epikron.countriesandflags.util.pxToDp
import com.epikron.data.data.models.CountryModel
import com.epikron.utils.fifth
import com.epikron.utils.half

@Composable
fun GameActionCapitalCard(
    isLandscape: Boolean,
    capital: String
) {
    Card(
        modifier = Modifier
            .padding(Space.small)
            .wrapContentHeight(),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = if (isLandscape) Space.small else Space.extraLarge, vertical = Space.extraLarge)
                .then(
                    if (isLandscape) Modifier.fillMaxWidth()
                    else Modifier.wrapContentWidth()
                ),
            text = capital.ifEmpty { stringResource(id = R.string.test_no_capital_name) },
            style = if (isLandscape) mainTypography.titleSmall else mainTypography.titleMedium,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Visible
        )
    }
}

@Composable
fun GameActionItem(
    country: CountryModel,
    isFlagsGame: Boolean,
    size: Int,
    orientation: Int = LocalConfiguration.current.orientation
) {
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE
    Box(
        modifier = Modifier
            .dragAndDropSource {
                detectDragGestures(
                    onDrag = { _, _ ->
                        startTransfer(
                            DragAndDropTransferData(ClipData.newPlainText("name", if (isFlagsGame) country.name else country.capital))
                        )
                    }
                )
            }
            .then(
                if (isLandscape) Modifier
                    .requiredWidth(size.pxToDp())
                    .fillMaxHeight()
                else Modifier
                    .requiredHeight((size / 3).pxToDp())
                    .fillMaxWidth()
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isFlagsGame) {
            FlagImageLoader(
                modifier = Modifier.padding(Space.large, Space.small),
                countryCode = country.code,
                countryFlag = country.flag,
                countryName = country.name,
                textFlagSize = FLAG_SIZE_DROP
            )
        } else {
            GameActionCapitalCard(
                isLandscape = isLandscape,
                capital = country.capital
            )
        }
    }
}

@Preview
@Composable
fun PreviewActionItem() {
    val size = LocalConfiguration.current.screenHeightDp.dpToPx().half
    SwipingCardsTheme {
        Column {
            GameActionItem(country = CountryModel.test, isFlagsGame = true, size)
            GameActionItem(country = CountryModel.testLong, isFlagsGame = false, size)
            GameActionItem(country = CountryModel.test, isFlagsGame = false, size)
        }
    }
}

@Preview(device = "spec:parent=pixel_3,orientation=landscape", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewLandscapeActionItem() {
    SwipingCardsTheme {
        Row {
            val size = LocalConfiguration.current.screenWidthDp.dpToPx().fifth
            GameActionItem(country = CountryModel.test, isFlagsGame = true, size)
            GameActionItem(country = CountryModel.testLong, isFlagsGame = false, size)
            GameActionItem(country = CountryModel.test, isFlagsGame = false, size)
        }
    }
}
