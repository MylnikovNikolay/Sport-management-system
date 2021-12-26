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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Window
import ru.emkn.kotlin.sms.CompetitionsSportsman
import ru.emkn.kotlin.sms.Group
import ru.emkn.kotlin.sms.stringToTimeOrNull

class LoadSplitsController(val groups: MutableState<List<MutableState<Group>>>, val isOpen: MSB) {
    @Composable
    fun createWindow(){
        Window(
            onCloseRequest = {isOpen.value=false},
            title = "Загрузка результатов",
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
        openChildWindows(childWindowsState)
    }
    @Composable
    fun openChildWindows(CWS: Map<MutableState<Group>, MutableState<Boolean>>) {
        val toOpen = CWS.filterValues { it.value }.toList()
        LazyColumn {
            items(toOpen) { entry ->
                createWindowForGroup(entry.first, entry.second)
            }
        }
    }

    var editingTimeForSP: MutableState<CompetitionsSportsman?> = mutableStateOf(null)

    @Composable
    fun createWindowForGroup(group: MutableState<Group>, isOpen: MSB) {
        Window(
            title = "Группа ${group.value.name}",
            onCloseRequest = { isOpen.value = false },
        ) {
            val listState = rememberLazyListState()
            Box {
                LazyColumn(state = listState) {
                    items(group.value.sportsmen.toList()) { sp ->
                        Row(modifier = Modifier.clickable(onClick = { editingTimeForSP.value = sp})) {
                            Text(
                                text = AnnotatedString(sp.number.toString()),
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
        editingTimeForSP.value?.also {
            EditingTimeForSPWindow().dialog()
        }
    }

    inner class EditingTimeForSPWindow {
        val timeStart = mutableStateOf("")
        val timeFinish = mutableStateOf("")

        @Composable
        fun dialog() {
            /* TODO(Нормальная загрузка для каждого КП) */
            if (editingTimeForSP.value!!.startTime != null)
                timeStart.value = editingTimeForSP.value!!.startTime.toString()

            if (editingTimeForSP.value!!.totalTimeByResults != null)
                timeFinish.value = editingTimeForSP.value!!.totalTimeByResults.toString()

            Dialog(
                title = "Результат для участника " +
                        "${editingTimeForSP.value!!.number} " +
                        "${editingTimeForSP.value!!.surname} " +
                        editingTimeForSP.value!!.name,
                onCloseRequest = { editingTimeForSP.value = null }
            ) {
                Column {
                    TextField(
                        value = timeStart.value,
                        onValueChange = { timeStart.value = it.trim() },
                        label = {Text(text="время старта")},
                    )
                    TextField(
                        value = timeFinish.value,
                        onValueChange = { timeFinish.value = it.trim() },
                        label = {Text(text="время финиша")},
                    )
                    val textOnButton = mutableStateOf("Сохранить и выйти")
                    val buttonColor = mutableStateOf(Color.Green)
                    if (stringToTimeOrNull(timeFinish.value) == null) {
                        textOnButton.value = "Неверный формат"
                        buttonColor.value = Color.Red
                    }
                    Button(
                        onClick =
                        {
                            editingTimeForSP.value!!.totalTimeByResults = stringToTimeOrNull(timeFinish.value) ?: return@Button
                            editingTimeForSP.value = null
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor.value)
                    ) {
                        Text(text = textOnButton.value)
                    }

                }
            }
        }
    }
}