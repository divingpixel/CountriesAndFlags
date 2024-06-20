package com.epikron.countriesandflags.ui.composables.dialogs

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.epikron.countriesandflags.BIG_FLAG_SIZE
import com.epikron.countriesandflags.BUTTON_ELEVATION
import com.epikron.countriesandflags.R
import com.epikron.countriesandflags.ui.composables.FlagImageLoader
import com.epikron.countriesandflags.ui.theme.Shapes
import com.epikron.countriesandflags.ui.theme.Space
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.countriesandflags.ui.theme.mainTypography

@Composable
fun FlagZoomDialog(
    countryName: String,
    countryCode: String,
    countryFlag: String,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        val mainColor = MaterialTheme.colorScheme.onSurface
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(Space.medium),
            shape = Shapes.medium,
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = mainColor
            )
        ) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = countryName,
                    style = mainTypography.titleMedium,
                    modifier = Modifier.padding(Space.medium),
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(modifier = Modifier.padding(Space.small), color = mainColor)
                FlagImageLoader(
                    modifier = Modifier.padding(Space.large),
                    countryCode = countryCode,
                    countryFlag = countryFlag,
                    countryName = countryName,
                    textFlagSize = BIG_FLAG_SIZE
                )
                HorizontalDivider(modifier = Modifier.padding(Space.small), color = mainColor)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    ElevatedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(Space.small),
                        colors = ButtonDefaults.textButtonColors().copy(
                            containerColor = MaterialTheme.colorScheme.onSurface,
                            contentColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = BUTTON_ELEVATION.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.dismiss),
                            style = mainTypography.displayMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewLightPortraitFlagZoomDialog() {
    SwipingCardsTheme {
        FlagZoomDialog("Spain", "ES", "🏴‍☠️") {}
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewDarkPortraitFlagZoomDialog() {
    SwipingCardsTheme {
        FlagZoomDialog("Spain", "ES", "🏴‍☠️") {}
    }
}
