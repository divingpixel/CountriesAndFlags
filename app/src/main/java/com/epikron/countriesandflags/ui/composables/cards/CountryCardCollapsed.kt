@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.epikron.countriesandflags.ui.composables.cards

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epikron.countriesandflags.CARD_ELEVATION
import com.epikron.countriesandflags.R
import com.epikron.data.data.models.CountryModel
import com.epikron.countriesandflags.ui.composables.cardBorderStroke
import com.epikron.countriesandflags.ui.composables.cardDetailsBackground
import com.epikron.countriesandflags.ui.composables.cardHeaderBackground
import com.epikron.countriesandflags.ui.composables.detailsContentColor
import com.epikron.countriesandflags.ui.composables.dialogs.FlagZoomDialog
import com.epikron.countriesandflags.ui.composables.headerContentColor
import com.epikron.countriesandflags.ui.theme.Shapes
import com.epikron.countriesandflags.ui.theme.Space
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.countriesandflags.ui.theme.mainTypography

@Composable
fun CountryHeaderCollapsed(
    country: CountryModel,
    isPortrait: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var width by remember { mutableFloatStateOf(1f) }
    val openZoomDialog = remember { mutableStateOf(false) }

    if (openZoomDialog.value) {
        FlagZoomDialog(
            countryName = country.name,
            countryCode = country.code,
            countryFlag = country.flag
        ) { openZoomDialog.value = false }
    }

    Box(
        modifier = Modifier
            .wrapContentHeight()
            .onPlaced { coordinates ->
                width = coordinates.size.width
                    .toFloat()
                    .coerceAtLeast(1f)
            }
            .background(cardHeaderBackground(width)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = if (isPortrait) {
                Modifier.fillMaxWidth()
            } else {
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
            }
                .padding(Space.medium),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            with(sharedTransitionScope) {
                Text(
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(key = "flag"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .padding(bottom = Space.medium)
                        .clickable { openZoomDialog.value = true },
                    textAlign = TextAlign.Center,
                    text = country.flag.ifEmpty { "🏴‍☠️" },
                    style = mainTypography.titleLarge.copy(fontSize = 64.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
                Text(
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(key = "name"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .padding(Space.small, Space.none),
                    textAlign = TextAlign.Center,
                    color = headerContentColor(),
                    text = country.name,
                    style = mainTypography.titleMedium,
                    overflow = TextOverflow.Clip,
                )
                Text(
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(key = "native"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .padding(Space.tiny),
                    textAlign = TextAlign.Center,
                    color = headerContentColor(),
                    text = country.nativeName,
                    style = mainTypography.headlineMedium,
                    overflow = TextOverflow.Clip,
                )
            }
        }
    }
}

@Composable
fun CountryDetailsCollapsed(
    country: CountryModel,
    onShowDetails: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Column(
        modifier = Modifier.padding(bottom = Space.small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(Modifier.weight(1f)) {
            CountrySharedDetails(
                country = country,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(Space.small, Space.tiny), color = detailsContentColor()
        )

        DetailsButton(
            text = stringResource(id = R.string.country_info),
            onClick = { onShowDetails.invoke() },
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
        )
    }
}

@Composable
fun CountryContentsCollapsed(
    country: CountryModel,
    onShowDetails: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    orientation: Int = LocalConfiguration.current.orientation
) {
    var height by remember { mutableFloatStateOf(1f) }

    when (orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            Column(
                modifier = Modifier
                    .onPlaced { coordinates ->
                        height = coordinates.size.height
                            .toFloat()
                            .coerceAtLeast(1f)
                    }
                    .fillMaxWidth()
                    .requiredHeightIn(
                        min = (LocalConfiguration.current.screenHeightDp * 0.5).dp,
                        max = (LocalConfiguration.current.screenHeightDp * 0.66).dp
                    )
                    .background(cardDetailsBackground(height)),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CountryHeaderCollapsed(
                    country = country,
                    isPortrait = true,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
                CountryDetailsCollapsed(
                    country = country,
                    onShowDetails = onShowDetails,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }

        else -> {
            Row(
                modifier = Modifier
                    .onPlaced { coordinates ->
                        height = coordinates.size.height
                            .toFloat()
                            .coerceAtLeast(1f)
                    }
                    .fillMaxHeight()
                    .wrapContentHeight()
                    .background(cardDetailsBackground(height)),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start
            ) {
                CountryHeaderCollapsed(
                    country = country,
                    isPortrait = false,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
                CountryDetailsCollapsed(
                    country = country,
                    onShowDetails = onShowDetails,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }
    }
}

@Composable
fun CountryCardCollapsed(
    country: CountryModel,
    onShowDetails: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        OutlinedCard(
            border = cardBorderStroke(),
            shape = Shapes.extraLarge,
            elevation = CardDefaults.cardElevation(defaultElevation = Dp(CARD_ELEVATION)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(Space.extraLarge, Space.extraLarge)
                .sharedBounds(
                    rememberSharedContentState(key = "card"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
        ) {
            CountryContentsCollapsed(
                country = country,
                onShowDetails = onShowDetails,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    }
}

@Preview
@Composable
fun PreviewCountryCardCollapsed() {
    SwipingCardsTheme {
        SharedTransitionLayout {
            AnimatedContent(targetState = true, label = "") { state ->
                if (state) CountryCardCollapsed(country = CountryModel.test, {}, this@SharedTransitionLayout, this@AnimatedContent)
            }
        }
    }
}
