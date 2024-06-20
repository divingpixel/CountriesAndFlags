package com.epikron.countriesandflags.ui.composables.game

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.epikron.countriesandflags.DISABLE_CARD_INTERVAL_SECONDS
import com.epikron.countriesandflags.R
import com.epikron.countriesandflags.ui.composables.MultiSelector
import com.epikron.countriesandflags.ui.composables.onBackgroundColor
import com.epikron.countriesandflags.ui.theme.Space
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.countriesandflags.ui.theme.mainTypography

@Composable
fun GameSelector(
    shouldShowHint: Boolean,
    isFlagsGame: Boolean,
    onSelectorChange: () -> Unit = {},
    onStartClicked: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(Space.medium)
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background.copy(0.75f),
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .padding(Space.medium, Space.small),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(Space.small),
                color = onBackgroundColor(),
                text = stringResource(id = R.string.test_type),
                style = mainTypography.titleMedium
            )
            MultiSelector(
                modifier = Modifier
                    .padding(Space.small)
                    .fillMaxWidth()
                    .height(Space.extraLarge),
                options = listOf(
                    stringResource(id = R.string.test_flag).replaceFirstChar { it.uppercase() },
                    stringResource(id = R.string.test_capital).replaceFirstChar { it.uppercase() }
                ),
                textStyle = MaterialTheme.typography.displayLarge,
                selectedOptionIndex = if (isFlagsGame) 0 else 1,
                onOptionSelect = { onSelectorChange.invoke() }
            )
            if (shouldShowHint) Text(
                modifier = Modifier
                    .padding(Space.small)
                    .weight(2f,false)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                textAlign = TextAlign.Justify,
                color = onBackgroundColor(),
                text = stringResource(
                    id = R.string.test_rules,
                    stringResource(id = if (isFlagsGame) R.string.test_flag else R.string.test_capital),
                    DISABLE_CARD_INTERVAL_SECONDS
                ),
                style = mainTypography.displayLarge,
            )
            ElevatedButton(
                modifier = Modifier
                    .padding(Space.small)
                    .fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
                onClick = onStartClicked
            ) {
                Text(
                    modifier = Modifier.padding(Space.small),
                    text = stringResource(id = R.string.test_button_start),
                    style = mainTypography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewGameSelector() {
    SwipingCardsTheme {
        Column {
            GameSelector(shouldShowHint = true, isFlagsGame = true)
        }
    }
}

@Preview(device = "spec:parent=pixel_3,orientation=landscape", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewLandscapeGameSelector() {
    SwipingCardsTheme {
        Column {
            GameSelector(shouldShowHint = true, isFlagsGame = true)
        }
    }
}