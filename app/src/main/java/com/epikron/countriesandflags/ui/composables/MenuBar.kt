package com.epikron.countriesandflags.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.epikron.countriesandflags.ui.composables.dialogs.InfoDialog
import com.epikron.countriesandflags.ui.theme.Space
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.countriesandflags.ui.theme.mainTypography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MenuBar(
    text: String,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    val openInfoDialog = remember { mutableStateOf(false) }

    if (openInfoDialog.value) {
        InfoDialog { openInfoDialog.value = false }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { coroutineScope.launch { drawerState.open() } },
            modifier = Modifier.wrapContentWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.Menu,
                tint = onBackgroundColor(),
                contentDescription = ""
            )
        }

        Text(
            modifier = Modifier
                .padding(Space.small),
            textAlign = TextAlign.Center,
            color = onBackgroundColor(),
            text = text,
            style = mainTypography.titleSmall
        )
        IconButton(
            onClick = { openInfoDialog.value = true },
            modifier = Modifier.wrapContentWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                tint = onBackgroundColor(),
                contentDescription = ""
            )
        }
    }
}

@Preview
@Composable
fun PreviewMenuBar() {
    SwipingCardsTheme {
        MenuBar("Menu")
    }
}
