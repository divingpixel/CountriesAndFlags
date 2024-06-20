package com.epikron.countriesandflags.ui.composables.dialogs

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.epikron.countriesandflags.BUTTON_ELEVATION
import com.epikron.countriesandflags.DIALOG_ICON_SIZE
import com.epikron.countriesandflags.R
import com.epikron.countriesandflags.ui.theme.Shapes
import com.epikron.countriesandflags.ui.theme.Space
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.countriesandflags.ui.theme.mainTypography

@Composable
fun AbortGameDialog(
    onDismissRequest: () -> Unit,
    onConfirmAction: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        val mainColor = MaterialTheme.colorScheme.onSurface
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Space.medium),
            shape = Shapes.medium,
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = mainColor
            )
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    modifier = Modifier.size(DIALOG_ICON_SIZE.dp),
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
                HorizontalDivider(modifier = Modifier.padding(Space.small, Space.none).weight(0.1f,false), color = mainColor)
                Text(
                    text = stringResource(id = R.string.test_abort_dialog_text),
                    color = MaterialTheme.colorScheme.error,
                    style = mainTypography.displayLarge,
                    modifier = Modifier.padding(Space.medium, Space.medium),
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(modifier = Modifier.padding(Space.small, Space.none).weight(0.1f,false), color = mainColor)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Space.medium)
                        .weight(1f, false),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    ElevatedButton(
                        onClick = { onConfirmAction() },
                        modifier = Modifier.padding(Space.none, Space.small, Space.none, Space.small),
                        colors = ButtonDefaults.textButtonColors().copy(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = BUTTON_ELEVATION.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.yes),
                            style = mainTypography.displayMedium
                        )
                    }
                    ElevatedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(Space.none, Space.small, Space.none, Space.small),
                        colors = ButtonDefaults.textButtonColors().copy(
                            containerColor = MaterialTheme.colorScheme.onSurface,
                            contentColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = BUTTON_ELEVATION.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.no),
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
fun PreviewLightPortraitAbortDialog() {
    SwipingCardsTheme {
        AbortGameDialog ({}, {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewDarkPortraitAbortDialog() {
    SwipingCardsTheme {
        AbortGameDialog ({}, {})
    }
}
