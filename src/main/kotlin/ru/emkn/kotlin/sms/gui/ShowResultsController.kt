package ru.emkn.kotlin.sms.gui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Window
import ru.emkn.kotlin.sms.CompetitionsSportsman
import ru.emkn.kotlin.sms.Group
import ru.emkn.kotlin.sms.Time
import ru.emkn.kotlin.sms.minus

class ShowResultsController(val groups: MutableState<List<MutableState<Group>>>, val isOpen: MSB) {
    @Composable
    fun createWindow() {
        Window(
            onCloseRequest = {isOpen.value=false},
            title = "Таблицы результатов",
        ) {
            MaterialTheme(shapes = Shapes()) {
                content()
            }
        }
    }

    @Composable
    fun content() {
        val childWindowsState = groups.value.associateWith { mutableStateOf(false) }
        val listState = rememberLazyListState()
        Box {
            Column {
                Text(text="Группы:")
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
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(scrollState = listState)
            )
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
            title = "Результаты группы ${group.name}",
            onCloseRequest = { isOpen.value = false },
        ) {
            val listState = rememberLazyListState()
            val sportsmen = mutableStateOf(group.sportsmen.toList().sortedBy { getResult(it) })
            val sportsmenWithHeader = mutableStateOf(createMapFromSportsmen(sportsmen.value))
            Box {
                Row {
                    sportsmenWithHeader.value.forEach {entry ->
                        Column {
                            Text(text = entry.key + "  ")
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

    private fun getResult(sp: CompetitionsSportsman): Time = getResultOrNull(sp) ?: Time.of(23,59,59)

    private fun getResultOrNull(sp: CompetitionsSportsman): Time? =
        if (sp.startTime == null || sp.totalTimeByResults == null) {
            null
        } else {
            sp.totalTimeByResults!!.minus(sp.startTime!!)
        }

    private fun createMapFromSportsmen(list: List<CompetitionsSportsman>) = mapOf(
        "Номер" to list.map { it.number },
        "Фамилия" to list.map { it.surname },
        "Имя" to list.map { it.name },
        "Г.р." to list.map { it.birthYear },
        "Разряд" to list.map { it.level },
        "Результат" to list.map { getResultOrNull(it) },
    )
}