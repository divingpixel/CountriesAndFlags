package com.epikron.countriesandflags.ui.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.epikron.countriesandflags.ui.theme.Space
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

private const val ANIMATION_DURATION_MILLIS = 500
private const val START_CORNER_RADIUS = 15f
private const val END_CORNER_RADIUS = 50f

@Stable
interface MultiSelectorState {
    val selectedIndex: Float
    val startCornerPercent: Int
    val endCornerPercent: Int
    val textColors: List<Color>
    val dividerAlphas: List<Float>
    fun selectOption(scope: CoroutineScope, index: Int)
}

@Stable
class MultiSelectorStateImpl(
    options: List<String>,
    selectedOptionIndex: Int,
    private val selectedColor: Color,
    private val unselectedColor: Color
    ) : MultiSelectorState {

    override val selectedIndex: Float
        get() = _selectedIndex.value
    override val startCornerPercent: Int
        get() = _startCornerPercent.value.toInt()
    override val endCornerPercent: Int
        get() = _endCornerPercent.value.toInt()
    override val textColors: List<Color>
        get() = _textColors.value
    override val dividerAlphas: List<Float>
        get() = _dividerAlphas.value

    private var _selectedIndex = Animatable(selectedOptionIndex.toFloat())
    private var _startCornerPercent = Animatable(
        if (selectedOptionIndex == 0) {
            END_CORNER_RADIUS
        } else {
            START_CORNER_RADIUS
        }
    )
    private var _endCornerPercent = Animatable(
        if (selectedOptionIndex == options.size - 1) {
            END_CORNER_RADIUS
        } else {
            START_CORNER_RADIUS
        }
    )

    private var _textColors: State<List<Color>> = derivedStateOf {
        List(numOptions) { index ->
            lerp(
                start = unselectedColor,
                stop = selectedColor,
                fraction = 1f - (((selectedIndex - index.toFloat()).absoluteValue).coerceAtMost(1f))
            )
        }
    }

    private var _dividerAlphas: State<List<Float>> = derivedStateOf {
        List(numOptions) { index ->
            if (index > 0) {
                val alphaOffset = if (selectedIndex - index.toFloat() > 0) 0 else 1
                (selectedIndex + alphaOffset - index.toFloat()).absoluteValue.coerceAtMost(1f) * 0.33f
            } else {
                0f
            }
        }
    }

    private val numOptions = options.size
    private val animationSpec = tween<Float>(
        durationMillis = ANIMATION_DURATION_MILLIS,
        easing = FastOutSlowInEasing,
    )

    override fun selectOption(scope: CoroutineScope, index: Int) {
        scope.launch {
            _selectedIndex.animateTo(
                targetValue = index.toFloat(),
                animationSpec = animationSpec,
            )
        }
        scope.launch {
            _startCornerPercent.animateTo(
                targetValue = if (index == 0) END_CORNER_RADIUS else START_CORNER_RADIUS,
                animationSpec = animationSpec,
            )
        }
        scope.launch {
            _endCornerPercent.animateTo(
                targetValue = if (index == numOptions - 1) END_CORNER_RADIUS else START_CORNER_RADIUS,
                animationSpec = animationSpec,
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MultiSelectorStateImpl

        if (selectedColor != other.selectedColor) return false
        if (unselectedColor != other.unselectedColor) return false
        if (_selectedIndex != other._selectedIndex) return false
        if (_startCornerPercent != other._startCornerPercent) return false
        if (_endCornerPercent != other._endCornerPercent) return false
        if (numOptions != other.numOptions) return false
        return animationSpec == other.animationSpec
    }

    override fun hashCode(): Int {
        var result = selectedColor.hashCode()
        result = 31 * result + unselectedColor.hashCode()
        result = 31 * result + _selectedIndex.hashCode()
        result = 31 * result + _startCornerPercent.hashCode()
        result = 31 * result + _endCornerPercent.hashCode()
        result = 31 * result + numOptions
        result = 31 * result + animationSpec.hashCode()
        return result
    }
}

@Composable
fun rememberMultiSelectorState(
    options: List<String>,
    selectedOption: Int,
    selectedColor: Color,
    unSelectedColor: Color
) = remember {
    MultiSelectorStateImpl(
        options = options,
        selectedOptionIndex = selectedOption.coerceIn(0, options.size - 1),
        selectedColor = selectedColor,
        unselectedColor = unSelectedColor
    )
}

enum class MultiSelectorOption {
    Option,
    Background,
    Divider
}

@Composable
fun MultiSelector(
    options: List<String>,
    selectedOptionIndex: Int,
    onOptionSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.displayMedium,
    selectedTextColor: Color = MaterialTheme.colorScheme.onPrimary,
    selectedBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    unselectedTextColor: Color = MaterialTheme.colorScheme.onSecondary,
    unselectedBackgroundColor: Color = MaterialTheme.colorScheme.secondary,
    state: MultiSelectorState = rememberMultiSelectorState(
        options = options,
        selectedOption = selectedOptionIndex,
        selectedColor = selectedTextColor,
        unSelectedColor = unselectedTextColor
    ),
) {
    if (options.isEmpty()) {
        return
    }
    if (options.size < 2) {
        onOptionSelect(options.first())
        return
    }
    val selectedIndex = selectedOptionIndex.coerceIn(0, options.size - 1)
    LaunchedEffect(key1 = options, key2 = selectedOptionIndex) {
        state.selectOption(this, selectedIndex)
    }
    Layout(
        modifier = modifier
            .clip(shape = RoundedCornerShape(percent = END_CORNER_RADIUS.roundToInt()))
            .background(unselectedBackgroundColor),
        content = {
            val colors = state.textColors
            val alphas = state.dividerAlphas
            options.forEachIndexed { index, option ->
                Box(
                    modifier = Modifier
                        .layoutId(MultiSelectorOption.Option)
                        .clickable { onOptionSelect(option) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = option,
                        style = textStyle,
                        color = colors[index],
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                }
                Box(
                    modifier = Modifier.layoutId(MultiSelectorOption.Divider),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    VerticalDivider(
                        modifier = Modifier
                            .fillMaxHeight(0.66f)
                            .width(1.dp),
                        color = colors[index].copy(alpha = alphas[index])
                    )
                }
            }
            Box(
                modifier = Modifier
                    .layoutId(MultiSelectorOption.Background)
                    .clip(
                        shape = RoundedCornerShape(
                            topStartPercent = state.startCornerPercent,
                            bottomStartPercent = state.startCornerPercent,
                            topEndPercent = state.endCornerPercent,
                            bottomEndPercent = state.endCornerPercent,
                        )
                    )
                    .background(selectedBackgroundColor),
            )
        }
    ) { elements, constraints ->
        val optionWidth = constraints.maxWidth / options.size
        val optionConstraints = Constraints.fixed(
            width = optionWidth,
            height = constraints.maxHeight,
        )
        val optionElements = elements
            .filter { element -> element.layoutId == MultiSelectorOption.Option }
            .map { element -> element.measure(optionConstraints) }
        val dividerElements = elements
            .filter { element -> element.layoutId == MultiSelectorOption.Divider }
            .map { element -> element.measure(optionConstraints) }
        val selectedElement = elements
            .first { element -> element.layoutId == MultiSelectorOption.Background }
            .measure(optionConstraints)
        layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight,
        ) {
            dividerElements.forEachIndexed { index, element ->
                element.placeRelative(
                    x = optionWidth * index,
                    y = 0,
                )
            }
            selectedElement.placeRelative(
                x = (state.selectedIndex * optionWidth).toInt(),
                y = 0,
            )
            optionElements.forEachIndexed { index, element ->
                element.placeRelative(
                    x = optionWidth * index,
                    y = 0,
                )
            }
        }
    }
}

@Preview(widthDp = 420)
@Composable
fun PreviewMultiSelector() {
    SwipingCardsTheme {
        MultiSelector(
            modifier = Modifier
                .padding(all = Space.medium)
                .fillMaxWidth()
                .height(Space.large),
            options = listOf("ipsum", "dolor", "sit", "amet"),
            selectedOptionIndex = 2,
            onOptionSelect = {}
        )
    }
}

