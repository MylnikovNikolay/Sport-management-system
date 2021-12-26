package ru.emkn.kotlin.sms.gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import ru.emkn.kotlin.sms.*

class GroupController(
    val group: MutableState<Group>,
    val isOpen: MutableState<Boolean>,
    val distances: MutableState<List<MutableState<Distance>>>
) {

    @Composable
    @Preview
    fun createWindow() {
        Window(
            onCloseRequest = {isOpen.value=false},
            title = "Группа " + group.value.name,
        ) {
            MaterialTheme(shapes = Shapes()) {
                content()
            }
        }
    }

    val editingGroup = mutableStateOf(false)

    @Composable
    @Preview
    fun content() {
        val listState = rememberLazyListState()

        Column {
            Row {
                IconButton(onClick = {
                    editingGroup.value = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null
                    )
                }
            }
            Box {
                LazyColumn(state = listState) {
                    items(group.value.sportsmen.toList()) {
                        Row {
                            Text(
                                text = AnnotatedString(formatToRow(it)),
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
        }
        if (editingGroup.value) {
            EditGroupDialog().dialog()
        }
    }

    inner class EditGroupDialog {
        val name = mutableStateOf("")
        var distance: MutableState<Distance>? = null
        private var expanded = mutableStateOf(false)

        @Composable
        fun dialog() {
            name.value = group.value.name

            Dialog(
                title = "Изменение группы ${group.value.name}",
                onCloseRequest = { editingGroup.value = false }
            ) {
                Column {
                    TextField(
                        value = name.value,
                        onValueChange = { name.value = it },
                        label = { Text(text="название группы") }
                    )

                    Box {
                        Button(
                            onClick = {expanded.value = true},
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (distance == null) Color.Red else Color.LightGray
                            )
                        ) {
                            Text(text = distance?.value?.name ?: "--выберите дистанцию--")
                        }
                        DropdownMenu(
                            expanded = expanded.value,
                            onDismissRequest = { expanded.value = false },
                            modifier = Modifier.fillMaxWidth().background(Color.LightGray),
                        ) {
                            distances.value.forEach {
                                DropdownMenuItem(
                                    onClick = { distance = it ; expanded.value = false
                                    }) {
                                    Text(text = it.value.name)
                                }
                            }
                        }
                    }
                    Button(onClick = {
                        group.value = group.value.copy(name = name.value)

                        if (distance != null) {
                            group.value = group.value.copy(distance = distance!!.value)
                        }
                        editingGroup.value = false
                    }) {
                        Text(text="Сохранить изменения и выйти")
                    }
                }
            }
        }
    }
}