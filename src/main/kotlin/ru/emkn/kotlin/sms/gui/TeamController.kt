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
import androidx.compose.material.icons.filled.Settings
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

class TeamController(
    val team: MutableState<CompetitionsTeam>,
    val isOpen: MutableState<Boolean>,
    val groups: MutableState<List<MutableState<Group>>>,
) {
    @Composable
    @Preview
    fun createWindow() {
        Window(
            onCloseRequest = {isOpen.value=false},
            title = "Команда " + team.value.name,
        ) {
            MaterialTheme(shapes = Shapes()) {
                content()
            }
        }
    }

    var editingTeam: MutableState<Boolean> = mutableStateOf(false)
    var editingSportsman: MutableState<Int?> = mutableStateOf(null)
    var addingSportsman: MutableState<Boolean> = mutableStateOf(false)

    @Composable
    fun content() {
        val listState = rememberLazyListState()
        Column {
            Row {
                IconButton(onClick = {
                    editingTeam.value = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null
                    )
                }
                IconButton(onClick = {
                    addingSportsman.value = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }

            Box {
                LazyColumn(state = listState) {
                    items(team.value.sportsmen.toList()) {
                        Row(modifier = Modifier.clickable(onClick = { editingSportsman.value = it.hashCode() })) {
                            Text(
                                text = AnnotatedString(formatToRow(it)),
                                modifier = Modifier.weight(1F).align(Alignment.CenterVertically),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            IconButton(onClick = {
                                team.value =
                                    team.value.copy(
                                        sportsmen = team.value.sportsmen.filter {t -> t.hashCode() != it.hashCode()}
                                                as MutableList<CompetitionsSportsman>
                                    )
                                groups.value = groups.value.map { group ->
                                    if (group.value.hashCode() == it.group.hashCode()) {
                                        group.value.sportsmen.remove(it)
                                    }
                                    group
                                }
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
            }
        }

        if (editingTeam.value)
            EditTeamDialog().dialog()

        if (addingSportsman.value)
            AddSportsmanDialog().dialog()
    }

    inner class EditTeamDialog {
        var name: MutableState<String> = mutableStateOf("")

        @Composable
        fun dialog() {
            name.value = team.value.name

            Dialog(
                title = "Изменение команды ${team.value.name}",
                onCloseRequest = { editingTeam.value = false },
            ) {
                Column {
                    TextField(
                        value = name.value,
                        onValueChange = { name.value = it }
                    )

                    Button(onClick = {
                        team.value = team.value.copy(name = name.value)
                        editingTeam.value = false
                    }) {
                        Text(text="Сохранить изменения и выйти")
                    }
                }
            }
        }
    }

    inner class AddSportsmanDialog {
        var surname: MutableState<String> = mutableStateOf("")
        var name: MutableState<String> = mutableStateOf("")
        var birthYear: MutableState<String> = mutableStateOf("")
        var level: MutableState<String> = mutableStateOf("")
        var group: MutableState<Group>? = null
        private var expanded = mutableStateOf(false)

        @Composable
        fun dialog() {
            Dialog(
                title = "Добавление нового участника",
                onCloseRequest = { addingSportsman.value = false },
            ) {
                Column {
                    TextField(
                        value = surname.value,
                        onValueChange = { surname.value = it },
                        label = { Text(text="фамилия") }
                    )
                    TextField(
                        value = name.value,
                        onValueChange = { name.value = it },
                        label = { Text(text="имя") }
                    )
                    Row {
                        TextField(
                            value = birthYear.value,
                            onValueChange = { birthYear.value = it },
                            label = { Text(text="год рождения") }
                        )
                        TextField(
                            value = level.value,
                            onValueChange = { level.value = it },
                            label = { Text(text="разряд") }
                        )
                    }

                    Box {
                        Button(
                            onClick = {expanded.value = true},
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (group == null) Color.Red else Color.LightGray
                            )
                        ) {
                            Text(text = group?.value?.name ?: "--выберите группу--")
                        }
                        DropdownMenu(
                            expanded = expanded.value,
                            onDismissRequest = { expanded.value = false },
                            modifier = Modifier.fillMaxWidth().background(Color.LightGray),
                        ) {
                            groups.value.forEach {
                                DropdownMenuItem(
                                    onClick = { group = it ; expanded.value = false
                                }) {
                                    Text(text = it.value.name)
                                }
                            }
                        }
                    }

                    Button(onClick = {
                        if (group == null) {
                            addingSportsman.value = false
                            return@Button
                        }
                        val sp = Sportsman(
                            name.value, surname.value, birthYear.value.trim().toIntOrNull() ?: 0, level.value
                        )
                        CompetitionsSportsman(sp, team.value, group!!.value) //пока что просто кидаем ошибку
                        addingSportsman.value = false
                    }) {
                        Text(text="Добавить участника")
                    }
                }
            }
        }
    }
}