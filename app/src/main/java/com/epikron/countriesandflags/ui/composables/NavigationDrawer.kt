package com.epikron.countriesandflags.ui.composables

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.epikron.countriesandflags.MENU_ICON_SIZE
import com.epikron.countriesandflags.R
import com.epikron.countriesandflags.ui.composables.screens.CountriesCardsScreen
import com.epikron.countriesandflags.ui.composables.screens.CountriesDetailsScreen
import com.epikron.countriesandflags.ui.composables.screens.CountriesGameScreen
import com.epikron.countriesandflags.ui.composables.screens.CountriesLoadingScreen
import com.epikron.countriesandflags.ui.composables.screens.CountriesSearchScreen
import com.epikron.countriesandflags.ui.logic.CardsViewModel
import com.epikron.countriesandflags.ui.logic.GameViewModel
import com.epikron.countriesandflags.ui.logic.SearchViewModel
import com.epikron.countriesandflags.ui.theme.Space
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.countriesandflags.ui.theme.mainTypography
import com.epikron.data.data.models.ContinentModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class Screens { Loading, Cards, Search, Game, Details }

@Composable
fun MenuDrawerSheet(
    continents: List<ContinentModel>,
    selectedContinent: String,
    continentsAction: (continent: String) -> Unit,
    searchButtonAction: () -> Unit,
    gameButtonAction: () -> Unit,
    currentScreen: String
) {
    val drawColor = MaterialTheme.colorScheme.onSurface
    ModalDrawerSheet(
        modifier = Modifier
            .padding(Space.none, Space.extraLarge, Space.extraLarge, Space.extraLarge),
        drawerContainerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(Space.medium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(MENU_ICON_SIZE.dp),
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(drawColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(vertical = Space.small),
                    text = stringResource(id = R.string.menu_pick_continent),
                    style = mainTypography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            continents.forEach { continent ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Space.small)
                        .clickable { continentsAction.invoke(continent.name) },
                    text = continent.name,
                    textAlign = TextAlign.Center,
                    fontWeight = if (continent.name == selectedContinent && currentScreen == Screens.Cards.name)
                        FontWeight.Bold else FontWeight.Normal,
                    color = drawColor,
                    overflow = TextOverflow.Visible,
                )
            }
            HorizontalDivider(modifier = Modifier.padding(Space.tiny), color = drawColor)
            if (currentScreen != Screens.Game.name) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Space.small)
                        .clickable { gameButtonAction.invoke() },
                    text = stringResource(id = R.string.menu_test_button),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    color = drawColor
                )
                HorizontalDivider(modifier = Modifier.padding(Space.tiny), color = drawColor)
            }
            if (currentScreen != Screens.Search.name) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Space.small)
                        .clickable { searchButtonAction.invoke() },
                    text = stringResource(id = R.string.menu_countries_button),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    color = drawColor
                )
                HorizontalDivider(modifier = Modifier.padding(Space.tiny), color = drawColor)
            }
        }
    }
}

@Composable
fun NavigationDrawer(
    cardsViewModel: CardsViewModel = viewModel(),
    searchViewModel: SearchViewModel = viewModel(),
    gameViewModel: GameViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
) {
    val dataLoaded = rememberSaveable { mutableStateOf(false) }
    val cardsScreenState = cardsViewModel.uiState.collectAsState().value
    val continents = cardsScreenState.continents
    val selectedContinent = cardsScreenState.currentContinent

    val searchScreenState = searchViewModel.uiState.collectAsState().value

    val gameScreenState = gameViewModel.uiState.collectAsState().value

    var currentScreen by rememberSaveable { mutableStateOf(Screens.Loading.name) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    fun closeDrawer(action: (() -> Unit)? = null) {
        coroutineScope.launch { drawerState.close() }.invokeOnCompletion {
            action?.invoke()
        }
    }

    LaunchedEffect(currentScreen) {
        if (dataLoaded.value && navBackStackEntry?.destination?.route != currentScreen) {
            navController.popBackStack()
            navController.navigate(currentScreen) {
                popUpTo(Screens.Loading.name) { inclusive = true }
            }
        }
    }

    LaunchedEffect(selectedContinent) {
        if (selectedContinent.isNotEmpty() && !dataLoaded.value) {
            dataLoaded.value = true
            currentScreen = Screens.Cards.name
        }
    }

    LaunchedEffect(drawerState.isOpen) {
        if (drawerState.isOpen && currentScreen == Screens.Game.name) {
            gameViewModel.abortDialogToggle()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuDrawerSheet(
                continents = continents,
                selectedContinent = selectedContinent,
                continentsAction = { continent ->
                    if (selectedContinent != continent) {
                        cardsViewModel.updateCountriesForContinent(continent) { closeDrawer { currentScreen = Screens.Cards.name } }
                    } else {
                        closeDrawer { currentScreen = Screens.Cards.name }
                    }
                },
                searchButtonAction = {
                    closeDrawer { currentScreen = Screens.Search.name }
                },
                gameButtonAction = {
                    closeDrawer { currentScreen = Screens.Game.name }
                },
                currentScreen = currentScreen,
            )
        }
    ) {
        NavHost(navController = navController, startDestination = Screens.Loading.name) {
            composable(Screens.Loading.name) {
                CountriesLoadingScreen()
            }
            composable(Screens.Cards.name) {
                CountriesCardsScreen(
                    continent = selectedContinent,
                    displayCountries = cardsScreenState.cards,
                    onCardSwipedOut = cardsViewModel::updateOnSwipeOut,
                    onCardUpdate = cardsViewModel::updateVisibleCountries,
                    onToggleDetails = cardsViewModel::toggleDetails,
                    onStartNewGame = { currentScreen = Screens.Game.name },
                    drawerState = drawerState
                )
            }
            composable(Screens.Search.name) {
                CountriesSearchScreen(
                    uiState = searchScreenState,
                    onTextChanged = searchViewModel::onSearchTextChange,
                    onCountrySelected = { country ->
                        searchViewModel.onCountrySelected(country)
                        currentScreen = Screens.Details.name
                    },
                    drawerState = drawerState
                )
            }
            composable(Screens.Game.name) {
                CountriesGameScreen(
                    uiState = gameScreenState,
                    drawerState = drawerState,
                    onItemAction = gameViewModel::onItemDropped,
                    onStartClicked = gameViewModel::initialiseGame,
                    onSelectorChange = gameViewModel::onSelectorChanged,
                    onDismissDialog = gameViewModel::abortDialogToggle,
                    onAbortConfirmed = gameViewModel::clearGame
                )
            }
            composable(Screens.Details.name) {
                CountriesDetailsScreen(
                    country = searchScreenState.selectedCountry,
                    onCloseClicked = {
                        searchViewModel.onCountrySelected()
                        currentScreen = Screens.Search.name
                    }
                )
            }
        }
    }
}

@Preview(name = "Phone - Portrait", device = "spec:height=891dp,width=411dp,dpi=420,orientation=portrait")
@Composable
fun PreviewPortraitDrawerPage() {
    SwipingCardsTheme {
        MenuDrawerSheet(ContinentModel.list, ContinentModel.test.name, {}, {}, {}, "")
    }
}

@Preview(device = "spec:parent=pixel_3,orientation=landscape", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewLandscapeDrawerPage() {
    SwipingCardsTheme {
        MenuDrawerSheet(ContinentModel.list, ContinentModel.test.name, {}, {}, {}, "")
    }
}