package ru.emkn.kotlin.sms.gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Window
import ru.emkn.kotlin.sms.Group

//Окно, содержащее список групп, позволяет открывать окна групп @Composable @Preview
class GroupListController(val groups: MutableState<List<MutableState<Group>>>, val isOpen: MutableState<Boolean>) {

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
        val childWindowsState = groups.value.associateWith { mutableStateOf(false) }
        val listState = rememberLazyListState()

        Box {
            LazyColumn(state = listState) {
                items(groups.value) { group ->
                    Row(modifier = Modifier.clickable(onClick = { childWindowsState[group]?.value = true })) {
                        Text(
                            text = AnnotatedString(group.value.name),
                            modifier = Modifier.weight(1F).align(Alignment.CenterVertically),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(scrollState = listState)
            )
        }

        openChildWindows(childWindowsState)
    }

    @Composable @Preview
    fun openChildWindows(CWS: Map<MutableState<Group>, MutableState<Boolean>>){
        val toOpen = CWS.filterValues { it.value }.toList()
        LazyColumn {
            items(toOpen) { entry ->
                GroupController(entry.first, entry.second).createWindow()
            }
        }
    }

}