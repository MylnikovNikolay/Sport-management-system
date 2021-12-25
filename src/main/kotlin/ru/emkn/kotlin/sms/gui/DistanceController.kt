package ru.emkn.kotlin.sms.gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import ru.emkn.kotlin.sms.*
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window

class DistanceController(val distance: Distance, val isOpen: MutableState<Boolean>) {

    @Composable
    @Preview
    fun createWindow() {
        Window(
            onCloseRequest = {isOpen.value=false},
            title = "Дистанция " + distance.name,
        ) {
            MaterialTheme(shapes = Shapes()) {
                content()
            }
        }
    }

    @Composable
    @Preview
    fun content() {
        val listState = rememberLazyListState()
        LazyColumn(state = listState) {
            items(distance.controlPoints) {
                Text(text = it.name)
            }
        }

        VerticalScrollbar(
            modifier = Modifier.fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState = listState)
        )
    }
}