package com.epikron.countriesandflags.ui.composables.game

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.epikron.countriesandflags.HEADER_BG_ALPHA
import com.epikron.countriesandflags.R
import com.epikron.countriesandflags.TRANSITION_DURATION_MILLIS
import com.epikron.countriesandflags.ui.composables.onBackgroundColor
import com.epikron.countriesandflags.ui.theme.Shapes
import com.epikron.countriesandflags.ui.theme.Space
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.countriesandflags.ui.theme.mainTypography

@Composable
fun GameHeaderTime(time: String) {
    Row(modifier = Modifier.padding(Space.extraSmall))
    {
        Text(
            textAlign = TextAlign.Start,
            color = onBackgroundColor(),
            text = stringResource(id = R.string.test_time),
            style = mainTypography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.tertiary,
            text = " $time",
            style = mainTypography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun GameHeaderScores(currentScore: String, highScore: String) {
    Text(
        modifier = Modifier.padding(Space.small, Space.extraSmall),
        textAlign = TextAlign.Start,
        color = onBackgroundColor(),
        text = "${stringResource(id = R.string.test_score)} $currentScore  |  ${stringResource(id = R.string.test_highest_score)} $highScore",
        style = mainTypography.titleMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun GameHeader(
    highScore: String,
    currentScore: String,
    isScoreIncreased: Boolean,
    time: String,
    shouldFlashTime: Boolean,
    orientation: Int = LocalConfiguration.current.orientation
) {
    var headerBackgroundColor by remember { mutableStateOf(Color.Transparent) }
    val scoreAlpha = remember { Animatable(HEADER_BG_ALPHA) }
    val isInEditMode = LocalView.current.isInEditMode

    LaunchedEffect(currentScore) {
        if (!isInEditMode) scoreAlpha.animateTo(HEADER_BG_ALPHA, snap(0))
        headerBackgroundColor = if (time != "00:00") {
            (if (isScoreIncreased) Color.Green else Color.Red)
        } else {
            Color.Transparent
        }
        if (!isInEditMode) scoreAlpha.animateTo(0f, animationSpec = tween(TRANSITION_DURATION_MILLIS)) {
            headerBackgroundColor = headerBackgroundColor.copy(alpha = scoreAlpha.value)
        }
    }

    LaunchedEffect(time) {
        if (shouldFlashTime && time != "00:00") {
            scoreAlpha.animateTo(HEADER_BG_ALPHA, snap(0))
            headerBackgroundColor = Color.Yellow
            scoreAlpha.animateTo(0f, animationSpec = tween(TRANSITION_DURATION_MILLIS)) {
                headerBackgroundColor = headerBackgroundColor.copy(alpha = scoreAlpha.value)
            }
        }
    }

    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        Column(
            modifier = Modifier
                .padding(Space.medium)
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = headerBackgroundColor, shape = Shapes.large),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameHeaderTime(time = time)
            GameHeaderScores(currentScore = currentScore, highScore = highScore)
        }
    } else {
        Row(
            modifier = Modifier
                .padding(Space.huge, Space.tiny)
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = headerBackgroundColor, shape = Shapes.large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GameHeaderTime(time = time)
            GameHeaderScores(currentScore = currentScore, highScore = highScore)
        }
    }
}

@Preview
@Composable
fun PreviewGameHeader() {
    SwipingCardsTheme {
        Column {
            GameHeader(
                highScore = "100",
                currentScore = "200",
                isScoreIncreased = false,
                time = "12:34",
                shouldFlashTime = false
            )
        }
    }
}

@Preview(device = "spec:parent=pixel_3,orientation=landscape", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewLandscapeGameHeader() {
    SwipingCardsTheme {
        Column {
            GameHeader(
                highScore = "100",
                currentScore = "200",
                isScoreIncreased = false,
                time = "12:34",
                shouldFlashTime = false
            )
        }
    }
}