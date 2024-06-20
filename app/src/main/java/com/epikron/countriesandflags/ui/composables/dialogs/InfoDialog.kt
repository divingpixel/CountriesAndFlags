package com.epikron.countriesandflags.ui.composables.dialogs

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun InfoDialog(
    onDismissRequest: () -> Unit
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
                Column(
                    modifier = Modifier
                        .weight(2f, false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        modifier = Modifier.size(DIALOG_ICON_SIZE.dp),
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                    HorizontalDivider(modifier = Modifier.padding(Space.small), color = mainColor)
                    Text(
                        text = stringResource(id = R.string.tech_stack_title),
                        style = mainTypography.displayMedium,
                        modifier = Modifier.padding(Space.small),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(id = R.string.tech_stack_text),
                        modifier = Modifier.padding(Space.medium, Space.none),
                        style = mainTypography.bodyMedium,
                        textAlign = TextAlign.Start
                    )
                    HorizontalDivider(modifier = Modifier.padding(Space.small), color = mainColor)
                    Text(
                        text = stringResource(id = R.string.credentials_title),
                        style = mainTypography.displayMedium,
                        modifier = Modifier.padding(Space.small),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(id = R.string.credentials_text),
                        modifier = Modifier.padding(start = Space.medium, end = Space.medium, bottom = Space.medium),
                        style = mainTypography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(Space.small, Space.none).weight(0.1f, false), color = mainColor)
                Text(
                    text = stringResource(id = R.string.about_app),
                    style = mainTypography.displayMedium,
                    modifier = Modifier.padding(Space.medium, Space.small),
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(modifier = Modifier.padding(Space.small, Space.none).weight(0.1f, false), color = mainColor)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, false),
                    horizontalArrangement = Arrangement.Center,
                ) {
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
fun PreviewLightPortraitInfoDialog() {
    SwipingCardsTheme {
        InfoDialog {}
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewDarkPortraitInfoDialog() {
    SwipingCardsTheme {
        InfoDialog {}
    }
}
