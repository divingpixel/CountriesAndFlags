package com.epikron.countriesandflags.ui.composables.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.epikron.countriesandflags.MENU_ICON_SIZE
import com.epikron.countriesandflags.R
import com.epikron.countriesandflags.ui.composables.onBackgroundColor
import com.epikron.countriesandflags.ui.theme.Space
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.countriesandflags.ui.theme.mainTypography

@Composable
fun CountriesLoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(MENU_ICON_SIZE.dp),
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .padding(Space.medium, Space.large, Space.small, Space.small),
            text = stringResource(id = R.string.loading),
            style = mainTypography.titleSmall,
            color = onBackgroundColor(),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        LinearProgressIndicator(
            color = MaterialTheme.colorScheme.tertiary,
            trackColor = MaterialTheme.colorScheme.onTertiary
        )
    }
}

@Preview
@Composable
fun PreviewLoadingPage() {
    SwipingCardsTheme {
        CountriesLoadingScreen()
    }
}
