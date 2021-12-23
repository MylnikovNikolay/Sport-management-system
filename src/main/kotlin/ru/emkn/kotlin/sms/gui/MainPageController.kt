package ru.emkn.kotlin.sms.gui
import ru.emkn.kotlin.sms.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
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
    @Composable @Preview fun content(){
        MaterialTheme(shapes = Shapes()) {
            Surface {  }
            Column {
                Button(
                    onClick = ::openGroupListPage,
                ) {
                    Text("Список групп")
                }
                Button(
                    onClick = ::openTeamListPage ,
                ) {
                    Text("Список команд")
                }
                Button(
                    onClick = ::openProtocolPage,
                ) {
                    Text("Работа с протоколами")
                }
            }
        }
    }
    //@Composable @Preview
    fun openGroupListPage(){
        GroupListController()
    }

    fun openTeamListPage(){
        TeamListController()
    }

    fun openProtocolPage(){
        ProtocolPageController()
    }



}