package ru.emkn.kotlin.sms.gui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val MARGIN_SCROLLBAR: Dp = 8.dp

internal typealias ScrollbarAdapter = androidx.compose.foundation.ScrollbarAdapter

@Composable
internal fun rememberScrollbarAdapter(scrollState: LazyListState): ScrollbarAdapter =
    androidx.compose.foundation.rememberScrollbarAdapter(scrollState)

@Composable
internal fun VerticalScrollbar(
    modifier: Modifier,
    adapter: ScrollbarAdapter
) {
    androidx.compose.foundation.VerticalScrollbar(
        modifier = modifier,
        adapter = adapter
    )
}

@Composable
internal fun Dialog(
    title: String,
    onCloseRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onCloseRequest = onCloseRequest,
        focusable = true,
        title = title,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}


internal fun Modifier.onKeyUp(key: Key, action: () -> Unit): Modifier =
    onKeyEvent { event ->
        if ((event.type == KeyEventType.KeyUp) && (event.key == key)) {
            action()
            true
        } else {
            false
        }
    }
