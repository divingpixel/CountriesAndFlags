@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.epikron.countriesandflags.ui.composables.cards

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.epikron.countriesandflags.CARDS_COUNT
import com.epikron.countriesandflags.ui.models.CardState
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.utils.fifth
import com.epikron.utils.half
import com.epikron.utils.quarter
import com.epikron.utils.third
import kotlin.math.abs

private fun checkIfShouldUpdateCard(shouldUpdateCard: Boolean, offsetX: Float, offsetY: Float, distance: Float, action: () -> Unit) {
    val resetDistance = -distance..distance
    if (shouldUpdateCard && offsetX in resetDistance && offsetY in resetDistance) action.invoke()
}

private fun chooseAnimationAction(
    offsetX: Float,
    offsetY: Float,
    heightLimit: Float,
    widthLimit: Float,
    onWayOutAction: () -> Unit,
    onReturnAction: () -> Unit
) {
    if ((abs(offsetY) < heightLimit || abs(offsetX) < widthLimit)) onWayOutAction.invoke()
    if ((abs(offsetY) >= heightLimit || abs(offsetX) >= widthLimit)) onReturnAction.invoke()
}

@Composable
fun CountryCard(
    cardState: CardState,
    cardStackIndex: Int = 0,
    onCardSwipedOut: (cardIndex: Int) -> Unit,
    onCardUpdate: (cardIndex: Int) -> Unit,
    onToggleDetails: (shouldShowDetails: Boolean) -> Unit
) {
    var shouldResetPosition by remember { mutableStateOf(false) }
    var shouldUpdateCard by remember { mutableStateOf(false) }
    var shouldAnimateOut by remember { mutableStateOf(false) }
    var cardOffsetX by remember { mutableFloatStateOf(0f) }
    var cardOffsetY by remember { mutableFloatStateOf(0f) }
    var motionRotation by remember { mutableFloatStateOf(0f) }
    var heightPx by remember { mutableFloatStateOf(0f) }
    var widthPx by remember { mutableFloatStateOf(0f) }

    if (shouldResetPosition) {
        cardOffsetX -= cardOffsetX.fifth
        cardOffsetY -= cardOffsetY.fifth
        val zeroRange = -0.1f..0.1f
        shouldResetPosition = cardOffsetX !in zeroRange && cardOffsetY !in zeroRange
        checkIfShouldUpdateCard(shouldUpdateCard, cardOffsetX, cardOffsetY, widthPx.quarter) {
            shouldUpdateCard = false
            onCardUpdate(cardStackIndex)
        }
    }

    if (shouldAnimateOut)
        chooseAnimationAction(cardOffsetX, cardOffsetY, heightPx, widthPx,
            onWayOutAction = {
                cardOffsetX += cardOffsetX.fifth
                cardOffsetY += cardOffsetY.fifth
            },
            onReturnAction = {
                shouldAnimateOut = false
                shouldResetPosition = true
                shouldUpdateCard = true
                onCardSwipedOut(cardStackIndex)
            }
        )

    SharedTransitionLayout(
        modifier = Modifier
            .zIndex(cardState.zIndex.toFloat())
            .onPlaced { coordinates ->
                heightPx = coordinates.size.height.toFloat()
                widthPx = coordinates.size.width.toFloat()
            }
            .graphicsLayer(
                translationX = cardOffsetX,
                translationY = cardOffsetY,
                rotationZ = cardState.rotation + motionRotation * (cardOffsetY / 10000)
            )
            .pointerInput(cardState.zIndex) {
                val dragEndAction: () -> Unit = {
                    val shouldReturn = (abs(cardOffsetY) < heightPx.third) && (abs(cardOffsetX) < widthPx.quarter)
                    shouldAnimateOut = !shouldReturn
                    shouldResetPosition = shouldReturn
                }
                detectDragGestures(
                    onDragStart = { position ->
                        shouldResetPosition = false
                        shouldAnimateOut = false
                        motionRotation = (position.x - widthPx.half)
                    },
                    onDragEnd = dragEndAction,
                    onDragCancel = dragEndAction,
                    onDrag = { change, dragAmount ->
                        if (cardState.zIndex == CARDS_COUNT - 1) {
                            cardOffsetX += dragAmount.x
                            cardOffsetY += dragAmount.y
                        }
                        change.consume()
                    })
            }
    ) {
        AnimatedContent(
            targetState = cardState.showDetails,
            label = "card_transition"
        ) { targetState ->
            if (!targetState) {
                CountryCardCollapsed(
                    country = cardState.data,
                    onShowDetails = { onToggleDetails(true) },
                    animatedVisibilityScope = this@AnimatedContent,
                    sharedTransitionScope = this@SharedTransitionLayout
                )
            } else {
                CountryCardExpanded(
                    country = cardState.data,
                    onBack = { onToggleDetails(false) },
                    animatedVisibilityScope = this@AnimatedContent,
                    sharedTransitionScope = this@SharedTransitionLayout
                )
            }
        }
    }
}

@Preview(name = "Phone - Portrait", device = "spec:height=891dp,width=411dp,dpi=420,orientation=portrait")
@Composable
fun PreviewCountryCardPortrait() {
    SwipingCardsTheme {
        CountryCard(cardState = CardState.test, onCardSwipedOut = {}, onCardUpdate = {}, onToggleDetails = {})
    }
}

@Preview(name = "Phone - Landscape", device = "spec:height=891dp,width=411dp,dpi=420,orientation=landscape")
@Composable
fun PreviewCountryCardLandscape() {
    SwipingCardsTheme {
        CountryCard(cardState = CardState.test, onCardSwipedOut = {}, onCardUpdate = {}, onToggleDetails = {})
    }
}
