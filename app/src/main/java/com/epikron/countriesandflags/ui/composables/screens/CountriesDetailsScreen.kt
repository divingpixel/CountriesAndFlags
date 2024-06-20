@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.epikron.countriesandflags.ui.composables.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.epikron.countriesandflags.ui.composables.cards.CountryCardExpanded
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.data.data.models.CountryModel

@Composable
fun CountriesDetailsScreen(
    country: CountryModel?,
    onCloseClicked: () -> Unit = {}
) {
    Box(modifier = Modifier.statusBarsPadding().navigationBarsPadding()) {
        SharedTransitionLayout {
            AnimatedContent(targetState = true, label = "") { targetState ->
                if (targetState) {
                    CountryCardExpanded(
                        country = country ?: CountryModel.empty,
                        onBack = onCloseClicked,
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                }
            }
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
fun PreviewDetailsPage() {
    SwipingCardsTheme {
        CountriesDetailsScreen(country = CountryModel.test)
    }
}
