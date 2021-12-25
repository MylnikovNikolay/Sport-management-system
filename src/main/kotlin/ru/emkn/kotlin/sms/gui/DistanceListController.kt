package ru.emkn.kotlin.sms.gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Window
import ru.emkn.kotlin.sms.*

class DistanceListController(val distances: MutableState<List< MutableState<Distance> >>, val isOpen: MutableState<Boolean>) {

    @Composable
    @Preview
    fun createWindow(){
        Window(
            onCloseRequest = {isOpen.value=false},
            title = "Список дистанций",
        ) {
            MaterialTheme(shapes = Shapes()) {
                content()
            }
        }
    }

    @Composable
    @Preview
    fun content() {
        val childWindowsState = distances.value.associateWith { mutableStateOf(false) }
        val listState = rememberLazyListState()

        Row {
            Column {
                IconButton(onClick = { /*TODO(Загрузка из файла)*/ }) {
                    Icon(
                        imageVector = Icons.Default.Edit, //Эта иконка не очень подходит, но лучше я не нашел
                        contentDescription = null
                    )
                }
                IconButton(onClick = {
                    distances.value = distances.value +
                            listOf(mutableStateOf(
                                Distance(getName("empty", distances.value), listOf())
                            ))
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
            Box {
                LazyColumn(state = listState) {
                    items(distances.value) { dist ->
                        Row(modifier = Modifier.clickable(onClick = { childWindowsState[dist]?.value = true })) {
                            Text(
                                text = AnnotatedString(dist.value.name),
                                modifier = Modifier.weight(1F).align(Alignment.CenterVertically),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            IconButton(onClick = {
                                distances.value = distances.value.filter {dist.value != it.value}
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }

                VerticalScrollbar(
                    modifier = Modifier.fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(scrollState = listState)
                )
            }
        }

        openChildWindows(childWindowsState)
    }

    @Composable
    @Preview
    fun openChildWindows(CWS: Map<MutableState<Distance>, MutableState<Boolean>>){
        val toOpen = CWS.filterValues { it.value }.toList()
        LazyColumn {
            items(toOpen) { entry ->
                DistanceController(entry.first, entry.second).createWindow()
            }
        }
    }

    private fun getName(name: String, list: List<MutableState<Distance>>): String {
        fun name(id: Int) = if (id == 0) name else "$name($id)"
        var id = 0
        while (list.any { it.value.name == name(id) }) {
            id++
        }
        return name(id)
    }
}
