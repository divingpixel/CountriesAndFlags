@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.epikron.countriesandflags.ui.composables.cards

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.sp
import com.epikron.countriesandflags.AREA_UNIT
import com.epikron.countriesandflags.CARD_ELEVATION
import com.epikron.countriesandflags.R
import com.epikron.data.data.models.CountryModel
import com.epikron.countriesandflags.ui.composables.cardBorderStroke
import com.epikron.countriesandflags.ui.composables.cardDetailsBackground
import com.epikron.countriesandflags.ui.composables.cardHeaderBackground
import com.epikron.countriesandflags.ui.composables.detailsContentColor
import com.epikron.countriesandflags.ui.composables.headerContentColor
import com.epikron.countriesandflags.ui.theme.Shapes
import com.epikron.countriesandflags.ui.theme.Space
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.countriesandflags.ui.theme.mainTypography

@Composable
fun CountryHeaderExpanded(
    country: CountryModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var width by remember { mutableFloatStateOf(1f) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .onPlaced { coordinates ->
                width = coordinates.size.width
                    .toFloat()
                    .coerceAtLeast(1f)
            }
            .background(cardHeaderBackground(width)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            with(sharedTransitionScope) {
                Text(
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(key = "flag"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .padding(Space.small),
                    text = country.flag.ifEmpty { "🏴‍☠️" },
                    style = mainTypography.titleLarge,
                    maxLines = 1
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState(key = "name"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                            .padding(Space.extraSmall),
                        textAlign = TextAlign.Center,
                        color = headerContentColor(),
                        text = country.name,
                        style = mainTypography.titleSmall,
                        lineHeight = if (country.name.length < 20) 20.sp else 16.sp, // TODO find a better way to scale this text
                        fontSize = if (country.name.length < 20) 20.sp else 16.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState(key = "native"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                            .padding(Space.extraSmall, Space.none),
                        textAlign = TextAlign.Center,
                        color = headerContentColor(),
                        text = country.nativeName,
                        style = mainTypography.headlineSmall,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun CountryMoreDetails(country: CountryModel, isHorizontal: Boolean = false) {
    Column(
        modifier = Modifier
            .padding(Space.small)
            .fillMaxWidth(if (isHorizontal) 0.66f else 1f),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.padding(Space.extraSmall),
            textAlign = TextAlign.Start,
            text = stringResource(id = R.string.location),
            style = mainTypography.displayLarge,
            color = detailsContentColor(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = Modifier.padding(Space.small, Space.small),
            textAlign = TextAlign.Start,
            color = detailsContentColor(),
            text = country.location,
            style = mainTypography.bodyMedium,
        )
        Text(
            modifier = Modifier.padding(Space.extraSmall),
            textAlign = TextAlign.Start,
            text = stringResource(id = R.string.area),
            color = detailsContentColor(),
            style = mainTypography.displayLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = Modifier.padding(Space.small, Space.small),
            textAlign = TextAlign.Start,
            color = detailsContentColor(),
            text = "${country.area} $AREA_UNIT",
            style = mainTypography.bodyMedium,
        )
        Text(
            modifier = Modifier.padding(Space.extraSmall),
            textAlign = TextAlign.Start,
            color = detailsContentColor(),
            text = stringResource(id = R.string.background),
            style = mainTypography.displayLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = Modifier.padding(Space.small, Space.small),
            textAlign = TextAlign.Start,
            color = detailsContentColor(),
            text = country.background,
            style = mainTypography.bodyMedium,
        )
    }
}

@Composable
fun CountryDetailsExpanded(
    country: CountryModel,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    orientation: Int = LocalConfiguration.current.orientation
) {
    when (orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(Space.none, Space.small),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())) {
                    Column {
                        CountrySharedDetails(
                            country = country,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(Space.small), color = detailsContentColor()
                        )
                        CountryMoreDetails(country = country, false)
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(Space.small), color = detailsContentColor()
                )
                DetailsButton(
                    text = stringResource(id = R.string.country_back),
                    onClick = onBack,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Space.small),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        CountryMoreDetails(country = country, true)
                        Column {
                            CountrySharedDetails(
                                country = country,
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        }
                    }
                }
                HorizontalDivider(Modifier.padding(Space.small), color = detailsContentColor())
                DetailsButton(
                    text = stringResource(id = R.string.country_back),
                    onClick = onBack,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }
    }
}

@Composable
fun CountryContentsExpanded(
    country: CountryModel,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var height by remember { mutableFloatStateOf(1f) }
    Column(
        modifier = Modifier
            .onPlaced { coordinates ->
                height = coordinates.size.height
                    .toFloat()
                    .coerceAtLeast(1f)
            }
            .fillMaxWidth()
            .fillMaxHeight()
            .background(cardDetailsBackground(height)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CountryHeaderExpanded(
            country = country,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
        )
        CountryDetailsExpanded(
            country = country,
            onBack = onBack,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
        )
    }
}

@Composable
fun CountryCardExpanded(
    country: CountryModel,
    onBack: () -> Unit,
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
                .fillMaxHeight()
                .padding(Space.extraSmall)
                .sharedBounds(
                    rememberSharedContentState(key = "card"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
        ) {
            CountryContentsExpanded(
                country = country,
                onBack = onBack,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    }
}

@Preview(name = "Phone - Portrait", device = "spec:height=891dp,width=411dp,dpi=420,orientation=portrait")
@Composable
fun PreviewPortraitCountryCardExpanded() {
    SwipingCardsTheme {
        SharedTransitionLayout {
            AnimatedContent(targetState = true, label = "") { state ->
                if (state) CountryCardExpanded(country = CountryModel.test, {}, this@SharedTransitionLayout, this@AnimatedContent)
            }
        }
    }
}

@Preview(name = "Phone - Landscape", device = "spec:height=891dp,width=411dp,dpi=420,orientation=landscape")
@Composable
fun PreviewLandscapeCountryCardExpanded() {
    SwipingCardsTheme {
        SharedTransitionLayout {
            AnimatedContent(targetState = true, label = "") { state ->
                if (state) CountryCardExpanded(country = CountryModel.test, {}, this@SharedTransitionLayout, this@AnimatedContent)
            }
        }
    }
}