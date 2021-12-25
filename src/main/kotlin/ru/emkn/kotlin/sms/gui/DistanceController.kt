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
import androidx.compose.ui.text.font.FontStyle
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
    var editingCP: MutableState<String?> = mutableStateOf(null)

    @Composable
    @Preview
    fun content() {
        val listState = rememberLazyListState()
        Column {
            Text(text = "Порядок " +
                    (if(distance.value.modeOfDistance == ModeOfDistance.Strict) "" else "не") +
                    " важен, " +
                    "пройти КП ${distance.value.numberOfCPtoPass} из ${distance.value.controlPoints.size}")
            Text(
                if (distance.value.controlPoints.size < distance.value.numberOfCPtoPass
                    || distance.value.numberOfCPtoPass < 2)
                "Поправьте нужное для прохождения количество кп" else "",
                color = Color.Red,
                fontStyle = FontStyle(9)
            )
            Text(
                if (distance.value.name == "") "Пустое название дистанции" else "",
                color = Color.Red,
                fontStyle = FontStyle(9)
            )
            Row {
                IconButton(onClick = {
                    editingDistance.value = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null
                    )
                }
                IconButton(onClick = {
                    distance.value = distance.value.copy(
                        controlPoints = distance.value.controlPoints +
                                listOf(ControlPoint(getNameForCP("CP", distance.value.controlPoints)))
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }

            Box {
                LazyColumn(state = listState) {
                    items(distance.value.controlPoints) {
                        Row(modifier = Modifier.clickable(onClick = { editingCP.value = it.name })) {
                            Text(
                                text = AnnotatedString(it.name),
                                modifier = Modifier.weight(1F).align(Alignment.CenterVertically),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            IconButton(onClick = {
                                distance.value =
                                    distance.value.copy(
                                        controlPoints = distance.value.controlPoints.filter {t -> t.name != it.name}
                                    )
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
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(scrollState = listState)
                )

                if (editingDistance.value) {
                    EditDistanceDialog().dialog()
                }

                distance.value.controlPoints.find {it.name == editingCP.value}?.also {
                    EditCPDialog().dialog(it)
                }
            }


        }
    }

    inner class EditCPDialog {
        var name: MutableState<String> = mutableStateOf("")

        @Composable
        fun dialog(cp: ControlPoint) {
            name.value = cp.name

            Dialog(
                title = "Изменение КП ${cp.name}",
                onCloseRequest = { editingCP.value = null },
            ) {
                Column {
                    TextField(
                        value = name.value,
                        onValueChange = { name.value = it },
                        label = { Text(text="название") }
                    )

                    Button(onClick = {
                        distance.value = distance.value.copy(
                            controlPoints = distance.value.controlPoints.map {
                                if (it.name == cp.name)
                                    ControlPoint(
                                        getNameForCP(name.value, distance.value.controlPoints.filter {t -> t.name != cp.name})
                                    )
                                else
                                    it
                            }
                        )
                        editingCP.value = null
                    }) {
                        Text(text="Сохранить изменения и выйти")
                    }
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
                        TextField(
                            value = name.value,
                            onValueChange = { name.value = it },
                            label = { Text(text="название") }
                        )
                    }
                    Column {
                        TextField(
                            value = numberOfCP.value,
                            onValueChange = { numberOfCP.value = it },
                            label = { Text(text="КП пройти") },
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
                            numberOfCPtoPass = numberOfCP.value.trim().toIntOrNull() ?: distance.value.numberOfCPtoPass,
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

    private fun getNameForCP(name: String, list: List<ControlPoint>): String {
        var id = 0
        while (list.any { it.name == getNewName(name, id) }) {
            id++
        }
        return getNewName(name, id)
    }
}