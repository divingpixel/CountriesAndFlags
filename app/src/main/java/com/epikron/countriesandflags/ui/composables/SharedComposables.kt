package com.epikron.countriesandflags.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.epikron.countriesandflags.CARD_ELEVATION
import com.epikron.countriesandflags.R
import com.epikron.data.data.models.CountryModel
import com.epikron.countriesandflags.ui.theme.Space
import com.epikron.countriesandflags.ui.theme.mainTypography

@Composable
fun onBackgroundColor() = MaterialTheme.colorScheme.onBackground

@Composable
fun detailsContentColor() = MaterialTheme.colorScheme.onSecondary

@Composable
fun headerContentColor() = MaterialTheme.colorScheme.onPrimary

@Composable
fun cardBorderStroke() = BorderStroke(
    1.dp, Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.outline,
            Color.Transparent,
            Color.Transparent,
            MaterialTheme.colorScheme.outlineVariant,
        )
    )
)

@Composable
fun cardHeaderBackground(radius: Float) = Brush.radialGradient(
    colors = listOf(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.primary
    ),
    center = Offset.Zero,
    radius = radius
)

@Composable
fun cardDetailsBackground(radius: Float) = Brush.radialGradient(
    colors = listOf(
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.secondary
    ),
    center = Offset.Zero,
    radius = radius
)

@Composable
fun ListStickyHeader(headerText: String) {
    var width by remember { mutableFloatStateOf(1f) }
    Card(
        modifier = Modifier
            .onPlaced { coordinates -> width = coordinates.size.width.toFloat() }
            .padding(Space.small, Space.none, Space.small, Space.tiny)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.extraSmall,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Dp(CARD_ELEVATION)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardDetailsBackground(radius = width)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.padding(Space.small, Space.small),
                text = headerText.capitalize(Locale("EN")),
                style = mainTypography.headlineLarge,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ListCountryItem(country: CountryModel, extraText: String? = null, action: ((String) -> Unit)? = null) {
    var width by remember { mutableFloatStateOf(1f) }
    Card(
        modifier = Modifier
            .onPlaced { coordinates -> width = coordinates.size.width.toFloat() }
            .padding(Space.small, Space.tiny)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.extraSmall,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        onClick = { action?.invoke(country.name) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardHeaderBackground(radius = width)),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(Space.small),
                text = country.flag.ifEmpty { "🏴‍☠️" },
                style = mainTypography.titleMedium,
                maxLines = 1
            )
            Text(
                modifier = Modifier.padding(Space.none, Space.small, Space.small, Space.small),
                text = country.name + (extraText?.let { " - $it" } ?: ""),
                style = mainTypography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun Modifier.grayScale(): Modifier {
    val saturationMatrix = ColorMatrix().apply { setToSaturation(0f) }
    val saturationFilter = ColorFilter.colorMatrix(saturationMatrix)
    val paint = Paint().apply { colorFilter = saturationFilter }

    return drawWithCache {
        val canvasBounds = Rect(Offset.Zero, size)
        onDrawWithContent {
            drawIntoCanvas {
                it.saveLayer(canvasBounds, paint)
                drawContent()
                it.restore()
            }
        }
    }
}

@Composable
fun FlagImageLoader(
    modifier: Modifier?,
    countryCode: String,
    countryFlag: String,
    countryName: String,
    textFlagSize : Int,
    imageSize : Dp? = null
) {
    val shouldDisplayImage = remember { mutableStateOf(true) }
    val imageSizeModifier = imageSize?.let { Modifier.size(it) } ?: Modifier.fillMaxWidth()
    val mergedModifier = modifier?.then(imageSizeModifier) ?: imageSizeModifier

    if (shouldDisplayImage.value)
        AsyncImage(
            modifier = mergedModifier,
            model = ImageRequest.Builder(LocalContext.current)
                .data(stringResource(id = R.string.flagcdn, countryCode.lowercase()))
                .crossfade(true)
                .diskCacheKey(countryCode)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            placeholder = painterResource(R.mipmap.white_flag),
            onError = { shouldDisplayImage.value = false },
            contentDescription = countryName,
            contentScale = ContentScale.Fit
        )
    else Text(
        textAlign = TextAlign.Center,
        text = countryFlag.ifEmpty { "🏴‍☠️" },
        style = mainTypography.titleLarge.copy(fontSize = textFlagSize.sp),
        maxLines = 1,
        overflow = TextOverflow.Clip
    )
}

