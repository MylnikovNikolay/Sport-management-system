package ru.emkn.kotlin.sms.gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import ru.emkn.kotlin.sms.Group

//Окно, содержащее список групп, позволяет открывать окна групп @Composable @Preview
class GroupListController(val groups: List<Group>, val isOpen: MutableState<Boolean>) {

    @Composable @Preview
    fun createWindow(){
        Window(
            onCloseRequest = {isOpen.value=false},
            title = "Список групп",
        ) {
            MaterialTheme(shapes = Shapes()) {
                content()
            }
        }
    }

    @Composable @Preview
    fun content(){
        val childWindowsState = groups.associateWith { mutableStateOf(false) }
        val listState = rememberLazyListState()

        LazyColumn(state = listState) {
            items(groups) { group ->
                Button(onClick = { childWindowsState[group]?.value = true }) { Text(group.name) }
            }
        }

        VerticalScrollbar(
            modifier = Modifier.fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState = listState)
        )

        openChildWindows(childWindowsState)
    }

    @Composable @Preview
    fun openChildWindows(CWS: Map<Group, MutableState<Boolean>>){
        //TODO("Открыть окна групп, которые нужно")
    }

}