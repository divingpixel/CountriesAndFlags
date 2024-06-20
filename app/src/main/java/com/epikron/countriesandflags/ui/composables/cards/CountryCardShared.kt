@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.epikron.countriesandflags.ui.composables.cards

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.epikron.countriesandflags.BUTTON_ELEVATION
import com.epikron.countriesandflags.R
import com.epikron.data.data.models.CountryModel
import com.epikron.countriesandflags.ui.composables.detailsContentColor
import com.epikron.countriesandflags.ui.theme.Space
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.countriesandflags.ui.theme.mainTypography

@Composable
fun DetailsButton(
    text: String,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        ElevatedButton(
            modifier = Modifier
                .padding(Space.small, Space.none)
                .wrapContentWidth()
                .sharedBounds(
                    rememberSharedContentState(key = "button"),
                    animatedVisibilityScope = animatedVisibilityScope
                ),
            colors = ButtonDefaults.textButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.onSecondary,
                contentColor = MaterialTheme.colorScheme.secondary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = BUTTON_ELEVATION.dp),
            onClick = { onClick.invoke() }
        ) {
            Text(
                text = text,
                style = mainTypography.displayMedium
            )
        }
    }
}

@Composable
fun CountrySharedDetails(
    country: CountryModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Space.extraSmall)
                .sharedBounds(
                    rememberSharedContentState(key = "shared_details"),
                    animatedVisibilityScope = animatedVisibilityScope
                ),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                modifier = Modifier
                    .padding(Space.small)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    textAlign = TextAlign.Start,
                    color = detailsContentColor(),
                    text = stringResource(id = R.string.capital),
                    style = mainTypography.displayLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier.padding(start = Space.small),
                    textAlign = TextAlign.Start,
                    color = detailsContentColor(),
                    text = country.capital,
                    style = mainTypography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            HorizontalDivider(modifier = Modifier.padding(Space.tiny), color = detailsContentColor())

            Row(
                modifier = Modifier
                    .padding(Space.small)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    textAlign = TextAlign.Start,
                    color = detailsContentColor(),
                    text = stringResource(id = R.string.currency),
                    style = mainTypography.displayLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier.padding(start = Space.small),
                    textAlign = TextAlign.Start,
                    color = detailsContentColor(),
                    text = country.currency,
                    style = mainTypography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            HorizontalDivider(modifier = Modifier.padding(Space.tiny), color = detailsContentColor())

            Row(
                modifier = Modifier
                    .padding(Space.small)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    textAlign = TextAlign.Start,
                    color = detailsContentColor(),
                    text = stringResource(id = R.string.languages),
                    style = mainTypography.displayLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier.padding(start = Space.small),
                    textAlign = TextAlign.Start,
                    color = detailsContentColor(),
                    text = country.languages,
                    style = mainTypography.bodyLarge,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewSharedDetails() {
    SwipingCardsTheme {
        SharedTransitionLayout {
            AnimatedContent(targetState = true, label = "") { state ->
                Column {
                    if (state) CountrySharedDetails(country = CountryModel.test, this@SharedTransitionLayout, this@AnimatedContent)
                }
            }
        }
    }
}