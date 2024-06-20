@file:OptIn(ExperimentalFoundationApi::class)

package com.epikron.countriesandflags.ui.composables.game

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.epikron.countriesandflags.R
import com.epikron.data.data.models.CountryModel
import com.epikron.countriesandflags.ui.composables.ListCountryItem
import com.epikron.countriesandflags.ui.composables.ListStickyHeader
import com.epikron.countriesandflags.ui.theme.Space

@Composable
fun GameStatistics(results: List<Pair<CountryModel, Boolean>>, isFlagsGame: Boolean) {
    if (results.isNotEmpty()) LazyColumn(
        modifier = Modifier
            .padding(horizontal = Space.small)
    ) {
        val matched = results.filter { it.second }.map { it.first }
        val mismatched = results.filter { !it.second }.map { it.first }
        stickyHeader {
            ListStickyHeader("${stringResource(id = R.string.test_stats_matched)}: ${matched.size}")
        }
        items(matched) { country ->
            ListCountryItem(country, if (!isFlagsGame) country.capital else null)
        }
        stickyHeader {
            ListStickyHeader("${stringResource(id = R.string.test_stats_mismatched)}: ${mismatched.size}")
        }
        items(mismatched) { country ->
            ListCountryItem(country, if (!isFlagsGame) country.capital else null)
        }
    }
}
