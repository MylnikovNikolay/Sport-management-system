package ru.emkn.kotlin.sms.gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import ru.emkn.kotlin.sms.CompetitionsByCSV
import ru.emkn.kotlin.sms.CompetitionsSportsman
import ru.emkn.kotlin.sms.Group

class StartCompetitionController(val groups: MutableState<List<MutableState<Group>>>, val isOpen: MSB) {

    @Composable @Preview
    fun createWindow(){
        Window(
            onCloseRequest = {isOpen.value=false},
            title = "Начало соревнования",
        ) {
            MaterialTheme(shapes = Shapes()) {
                content()
            }
        }
    }

    private val isClicked = mutableStateOf(false)

    @Composable
    fun content() {
        val childWindowsState = groups.value.associateWith { mutableStateOf(false) }
        val listState = rememberLazyListState()
        Column {
            Button(onClick = {
                val comp = CompetitionsByCSV("", "")
                groups.value.forEach {
                    comp.addGroup(it.value)
                }
                comp.makeADraw()
                groups.value = comp.getGroupsSet().toList().map { mutableStateOf(it) }
                isClicked.value = true
            },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isClicked.value) Color.Green else Color.LightGray
                )
            ) {
                Text(text = "Раздать номера и провести жеребьевку")
            }

            Text(text="Группы:")

            Box {
                LazyColumn(state = listState) {
                    items(groups.value) { group ->
                        Row(modifier = Modifier.clickable(onClick = { childWindowsState[group]?.value = true })) {
                            Text(
                                text = AnnotatedString(group.value.name + " --> " + group.value.distance.name),
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
        openChildWindows(childWindowsState)
    }

    @Composable
    fun openChildWindows(CWS: Map<MutableState<Group>, MutableState<Boolean>>) {
        val toOpen = CWS.filterValues { it.value }.toList()
        LazyColumn {
            items(toOpen) { entry ->
                createWindowForGroup(entry.first.value, entry.second)
            }
        }
    }

    @Composable
    fun createWindowForGroup(group: Group, isOpen: MSB) {
        Window(
            title = "Группа ${group.name}",
            onCloseRequest = { isOpen.value = false }
        ) {
            val listState = rememberLazyListState()
            val sportsmen = mutableStateOf(group.sportsmen.toList().sortedBy { it.startTime })
            val sportsmenWithHeader = mutableStateOf(createMapFromSportsmen(sportsmen.value))
            Box {
                Row {
                    sportsmenWithHeader.value.forEach {entry ->
                        Column {
                            Row {
                                Text(text = entry.key)
                                IconButton(
                                    onClick = {
                                        sportsmen.value = sortByColumnName(sportsmen.value, entry.key)
                                        sportsmenWithHeader.value = createMapFromSportsmen(sportsmen.value)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        sportsmen.value = sortDescByColumnName(sportsmen.value, entry.key)
                                        sportsmenWithHeader.value = createMapFromSportsmen(sportsmen.value)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = null
                                    )
                                }
                            }
                            LazyColumn(state = listState) {
                                items(entry.value) {
                                    Text(
                                        text = AnnotatedString(it.toString()),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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
            }
        }
    }

    private fun sortByColumnName(list: List<CompetitionsSportsman>, name: String) = when (name) {
            "Номер" -> list.sortedBy { it.number }
            "Фамилия" -> list.sortedBy { it.surname }
            "Имя" -> list.sortedBy { it.name }
            "Г.р." -> list.sortedBy { it.birthYear }
            "Разряд" -> list.sortedBy { it.level }
            else -> list.sortedBy { it.startTime }
        }

    private fun sortDescByColumnName(list: List<CompetitionsSportsman>, name: String) = when (name) {
        "Номер" -> list.sortedByDescending { it.number }
        "Фамилия" -> list.sortedByDescending { it.surname }
        "Имя" -> list.sortedByDescending { it.name }
        "Г.р." -> list.sortedByDescending { it.birthYear }
        "Разряд" -> list.sortedByDescending { it.level }
        else -> list.sortedByDescending { it.startTime }
    }

    private fun createMapFromSportsmen(list: List<CompetitionsSportsman>) = mapOf(
        "Номер" to list.map { it.number },
        "Фамилия" to list.map { it.surname },
        "Имя" to list.map { it.name },
        "Г.р." to list.map { it.birthYear },
        "Разряд" to list.map { it.level },
        "Время старта" to list.map { it.startTime },
    )
}