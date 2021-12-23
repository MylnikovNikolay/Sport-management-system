package ru.emkn.kotlin.sms.gui
import ru.emkn.kotlin.sms.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application


class MainPageController(val comp: Competitions) {
    init{
        application(false) {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Sport Management System",
            ) {
                content()
            }
        }
    }

    //Рисует все детали внутри главного окна
    @Composable fun content(){
        MaterialTheme {
            Button(onClick = ::openGroupListPage) {
                Text("Список групп")
            }
            Button(onClick = ::openTeamListPage) {
                Text("Список команд")
            }
            Button(onClick = ::openProtocolPage) {
                Text("Работа с протоколами")
            }
        }
    }

    @Composable fun openGroupListPage(){
        GroupListController()
    }

    @Composable fun openTeamListPage(){
        TeamListController()
    }

    @Composable fun openProtocolPage(){
        ProtocolPageController()
    }



}