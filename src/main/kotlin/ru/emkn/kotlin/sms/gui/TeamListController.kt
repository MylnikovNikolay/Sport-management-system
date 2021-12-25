package ru.emkn.kotlin.sms.gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.Window
import ru.emkn.kotlin.sms.*

class TeamListController(val teams: List<CompetitionsTeam>, val isOpen: MutableState<Boolean>) {

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
        val childWindowsState = teams.associateWith { mutableStateOf(false) }
        Column {
            for(team in teams){
                Button(onClick = { childWindowsState[team]?.value = true }) { Text(team.name) }
            }
        }
        openChildWindows(childWindowsState)
    }

    @Composable
    @Preview
    fun openChildWindows(CWS: Map<CompetitionsTeam, MSB>){
        //TODO("Открыть окна команд, которые нужно")
    }
}