package ru.emkn.kotlin.sms.gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import ru.emkn.kotlin.sms.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Window

class DistanceController(val distance: MutableState<Distance>, val isOpen: MutableState<Boolean>) {

    @Composable
    @Preview
    fun createWindow() {
        Window(
            onCloseRequest = {isOpen.value=false},
            title = "Дистанция " + distance.value.name,
        ) {
            MaterialTheme(shapes = Shapes()) {
                content()
            }
        }
    }

    var editingDistance: MutableState<Boolean> = mutableStateOf(false)

    @Composable
    @Preview
    fun content() {
        val listState = rememberLazyListState()
        Row {
            Column {
                IconButton(onClick = { /*TODO(Загрузка из файла)*/ }) {
                    Icon(
                        imageVector = Icons.Default.Edit, //Эта иконка не очень подходит, но лучше я не нашел
                        contentDescription = null
                    )
                }
                IconButton(onClick = { /*TODO(Добавление нового КП)*/ }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
                IconButton(onClick = {
                    editingDistance.value = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null
                    )
                }
            }

            Box {
                Column {
                    Text(text = "Тип дистанции ${distance.value.modeOfDistance}, " +
                            "пройти КП ${distance.value.numberOfCPtoPass}/${distance.value.controlPoints.size}")
                    LazyColumn(state = listState) {
                        items(distance.value.controlPoints) {
                            Row(modifier = Modifier.clickable(onClick = { /*TODO(Изменение названия)*/ })) {
                                Text(
                                    text = AnnotatedString(it.name),
                                    modifier = Modifier.weight(1F).align(Alignment.CenterVertically),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                IconButton(onClick = {
                                    distance.value =
                                        distance.value.copy(controlPoints = distance.value.controlPoints.filter {t -> t.name != it.name})
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(scrollState = listState)
                )

                if (editingDistance.value) {
                    EditDistanceDialog().dialog()
                }
            }
        }
    }

    inner class EditDistanceDialog {
        var name: MutableState<String> = mutableStateOf("")
        var numberOfCP: MutableState<String> = mutableStateOf("")
        var isStrict: MutableState<Boolean> = mutableStateOf(false)

        @Composable
        fun dialog() {
            name.value = distance.value.name
            numberOfCP.value = distance.value.numberOfCPtoPass.toString()
            isStrict.value = (distance.value.modeOfDistance == ModeOfDistance.Strict)

            Dialog(
                title = "Изменение дистанции ${distance.value.name}",
                onCloseRequest = { editingDistance.value = false },
            ) {
                Column {
                    Column {
                        Text(text="Имя:")
                        TextField(
                            value = name.value,
                            onValueChange = { name.value = it }
                        )
                    }
                    Column {
                        Text(text="КП пройти:")
                        TextField(
                            value = numberOfCP.value,
                            onValueChange = { numberOfCP.value = it }
                        )
                    }
                    Row {
                        Text(text="Важен порядок:  ")
                        OutlinedButton(
                            onClick = { isStrict.value = !isStrict.value },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (isStrict.value) Color.Green else Color.Red
                            )
                        ) {
                            Icon(
                                imageVector = if (isStrict.value) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = null
                            )
                        }
                    }
                    Button(onClick = {
                        distance.value = distance.value.copy(
                            name = name.value,
                            numberOfCPtoPass = numberOfCP.value.toIntOrNull() ?: distance.value.numberOfCPtoPass,
                            modeOfDistance = if (isStrict.value) ModeOfDistance.Strict else ModeOfDistance.Lax
                        )
                        editingDistance.value = false
                    }) {
                        Text(text="Сохранить изменения и выйти")
                    }
                }
            }
        }
    }
}