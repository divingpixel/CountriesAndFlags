@file:OptIn(ExperimentalFoundationApi::class)

package com.epikron.countriesandflags.ui.composables.game

import android.content.ClipDescription
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.epikron.countriesandflags.CARD_ELEVATION
import com.epikron.countriesandflags.FLAG_SIZE_CARD
import com.epikron.countriesandflags.R
import com.epikron.countriesandflags.ui.composables.FlagImageLoader
import com.epikron.countriesandflags.ui.composables.cardBorderStroke
import com.epikron.countriesandflags.ui.composables.cardDetailsBackground
import com.epikron.countriesandflags.ui.composables.cardHeaderBackground
import com.epikron.countriesandflags.ui.composables.grayScale
import com.epikron.countriesandflags.ui.theme.Shapes
import com.epikron.countriesandflags.ui.theme.Space
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.countriesandflags.ui.theme.mainTypography
import com.epikron.countriesandflags.util.pxToDp
import com.epikron.data.data.models.CountryModel
import com.epikron.utils.half

internal class DropTarget(val action: () -> Unit) : DragAndDropTarget {
    override fun onDrop(event: DragAndDropEvent): Boolean {
        action.invoke()
        return true
    }
}

private fun getSpacing(orientation: Int) =
    if (orientation == Configuration.ORIENTATION_PORTRAIT) Space.small else Space.extraSmall

private fun Modifier.getCardModifier(isEnabled: Boolean, dropTarget: DropTarget): Modifier {
    return this.then(
        if (!isEnabled) Modifier.grayScale()
        else Modifier.dragAndDropTarget(
            shouldStartDragAndDrop = { event ->
                event
                    .mimeTypes()
                    .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
            },
            target = dropTarget
        )
    )
}

@Composable
fun GameSmallCard(
    country: CountryModel,
    isEnabled: Boolean,
    isFlagsGame: Boolean,
    width: Int,
    onItemAction: (String) -> Unit = {},
    orientation: Int = LocalConfiguration.current.orientation
) {
    val action = { onItemAction.invoke(if (isFlagsGame) country.name else country.capital) }
    var dropTarget by remember { mutableStateOf(DropTarget(action)) }
    val imageSize =  width.half.pxToDp() - Space.small

    LaunchedEffect(country) {
        if (isEnabled) dropTarget = DropTarget(action)
    }

    OutlinedCard(
        modifier = Modifier
            .padding(Space.small)
            .wrapContentHeight()
            .width(width.pxToDp() - Space.large),
        border = cardBorderStroke(),
        shape = Shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = Dp(CARD_ELEVATION)),
        onClick = { if (isEnabled) action.invoke() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .getCardModifier(isEnabled, dropTarget),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val cardTextStyle = mainTypography.displayLarge
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(cardHeaderBackground(width.toFloat())),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(getSpacing(orientation)),
                    textAlign = TextAlign.Center,
                    text = country.name,
                    style = cardTextStyle,
                    maxLines = 4,
                    overflow = TextOverflow.Clip,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardDetailsBackground(width.toFloat()))
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (isFlagsGame) {
                    Image(
                        modifier = Modifier
                            .padding(Space.small)
                            .size(imageSize),
                        painter = painterResource(id = R.mipmap.white_flag_quest),
                        contentScale = ContentScale.Fit,
                        contentDescription = null,
                    )
                } else {
                    FlagImageLoader(
                        modifier = Modifier.padding(Space.small),
                        countryCode = country.code,
                        countryFlag = country.flag,
                        countryName = country.name,
                        textFlagSize = FLAG_SIZE_CARD,
                        imageSize = imageSize
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSmallCard() {
    SwipingCardsTheme {
        val width = LocalConfiguration.current.screenWidthDp
        Row {
            GameSmallCard(country = CountryModel.test, isEnabled = true, isFlagsGame = true, width = width)
            GameSmallCard(country = CountryModel.test, isEnabled = true, isFlagsGame = false, width = width)
        }
    }
}