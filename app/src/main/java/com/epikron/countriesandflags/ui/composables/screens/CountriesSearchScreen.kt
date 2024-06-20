@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.epikron.countriesandflags.ui.composables.screens

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.epikron.countriesandflags.R
import com.epikron.data.data.models.ContinentModel
import com.epikron.data.data.models.CountryModel
import com.epikron.countriesandflags.ui.composables.ListCountryItem
import com.epikron.countriesandflags.ui.composables.ListStickyHeader
import com.epikron.countriesandflags.ui.composables.MenuBar
import com.epikron.countriesandflags.ui.models.SearchScreenState
import com.epikron.countriesandflags.ui.theme.Space
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.countriesandflags.ui.theme.mainTypography

@Composable
fun CountriesSearchScreen(
    uiState: SearchScreenState,
    onTextChanged: (String) -> Unit = {},
    onCountrySelected: (String) -> Unit = {},
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    orientation: Int = LocalConfiguration.current.orientation
) {
    val searchFieldColor = MaterialTheme.colorScheme.onBackground
    Column {
        MenuBar(stringResource(id = R.string.top_bar_search_screen_text), drawerState)
        Card(
            modifier = Modifier.padding(Space.medium),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.background.copy(0.75f),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        )
        {
            OutlinedTextField(
                value = uiState.searchText,
                onValueChange = onTextChanged,
                label = { Text(text = stringResource(id = R.string.search_label), style = mainTypography.displayLarge) },
                textStyle = mainTypography.displayLarge,
                singleLine = true,
                modifier = Modifier
                    .padding(Space.medium, Space.small)
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors().copy(
                    focusedTextColor = searchFieldColor,
                    unfocusedTextColor = searchFieldColor.copy(0.5f),
                    focusedLabelColor = searchFieldColor,
                    unfocusedLabelColor = searchFieldColor.copy(0.5f),
                    focusedIndicatorColor = searchFieldColor,
                    unfocusedIndicatorColor = searchFieldColor
                ),
                trailingIcon = {
                    if (uiState.searchText.isNotEmpty())
                        Icon(
                            modifier = Modifier.clickable { onTextChanged.invoke("") },
                            imageVector = Icons.Outlined.Clear,
                            tint = searchFieldColor,
                            contentDescription = ""
                        )
                }
            )
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                Text(
                    modifier = Modifier
                        .padding(Space.medium, Space.medium, Space.small, Space.small),
                    text = stringResource(id = R.string.search_results),
                    style = mainTypography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            LazyColumn(
                modifier = Modifier.padding(Space.small, Space.medium)
            ) {
                uiState.continentsCountries.keys.forEach { continent ->
                    stickyHeader {
                        ListStickyHeader(headerText = continent)
                    }
                    items(uiState.continentsCountries[continent] ?: listOf()) { country ->
                        ListCountryItem(country) { countryName ->
                            onCountrySelected.invoke(countryName)
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Phone - Portrait", device = "spec:height=891dp,width=411dp,dpi=420,orientation=portrait")
@Composable
fun PreviewPortraitSearchPage() {
    SwipingCardsTheme {
        CountriesSearchScreen(SearchScreenState(hashMapOf(ContinentModel.test.name to CountryModel.previewTestList), "", CountryModel.test))
    }
}

@Preview(name = "Phone - Portrait", device = "spec:height=891dp,width=411dp,dpi=420,orientation=landscape")
@Composable
fun PreviewLandscapeSearchPage() {
    SwipingCardsTheme {
        CountriesSearchScreen(SearchScreenState(hashMapOf(ContinentModel.test.name to CountryModel.previewTestList), "", CountryModel.test))
    }
}
