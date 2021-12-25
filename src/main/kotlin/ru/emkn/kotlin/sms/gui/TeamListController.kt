package ru.emkn.kotlin.sms.gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import ru.emkn.kotlin.sms.*

class TeamListController(
    val teams: MutableState<List<MutableState<CompetitionsTeam>>>, 
    val isOpen: MutableState<Boolean>,
    val groups: MutableState<List<MutableState<Group>>>,    
) {

    @Composable
    @Preview
    fun createWindow(){
        Window(
            onCloseRequest = {isOpen.value=false},
            title = "Список команд",
        ) {
            MaterialTheme(shapes = Shapes()) {
                content()
            }
        }
    }

    @Composable
    @Preview
    fun content(){
        val childWindowsState = teams.value.associateWith { mutableStateOf(false) }
        val listState = rememberLazyListState()

        Column {
            Row {
                IconButton(onClick = { /*TODO(Загрузка из файла)*/ }) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                }
                IconButton(onClick = {
                    teams.value = teams.value +
                            listOf(mutableStateOf(
                                CompetitionsTeam(getNameForTeam("empty", teams.value), mutableListOf())
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
                    items(teams.value) { team ->
                        Row(modifier = Modifier.clickable(onClick = { childWindowsState[team]?.value = true })) {
                            Text(
                                text = AnnotatedString(team.value.name),
                                modifier = Modifier.weight(1F).align(Alignment.CenterVertically),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            IconButton(onClick = {
                                teams.value = teams.value.filter {team.value != it.value}
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

        openChildWindows(childWindowsState)
    }

    @Composable
    @Preview
    fun openChildWindows(CWS: Map<MutableState<CompetitionsTeam>, MSB>){
        val toOpen = CWS.filterValues { it.value }.toList()
        LazyColumn {
            items(toOpen) { entry ->
                TeamController(entry.first, entry.second, groups).createWindow()
            }
        }
    }

    private fun getNameForTeam(name: String, list: List<MutableState<CompetitionsTeam>>): String {
        var id = 0
        while (list.any { it.value.name == getNewName(name, id) }) {
            id++
        }
        return getNewName(name, id)
    }
}