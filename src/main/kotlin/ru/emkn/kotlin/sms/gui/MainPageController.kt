package ru.emkn.kotlin.sms.gui
import ru.emkn.kotlin.sms.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
//import androidx.compose.material.
//GlobalScope


class MainPageController(val comp: Competitions){
    init{
        application(false) {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Sport Management System",
            ) {
                MaterialTheme(shapes = Shapes()) {
                    content()
                }
            }
        }
    }

    //Рисует все детали внутри главного окна
    @Composable @Preview fun content(){
        val openGroupList = remember { mutableStateOf(false) }
        val openTeamList = remember { mutableStateOf(false) }
        val openProtocolPage = remember { mutableStateOf(false) }
        Column {
            Button(
                onClick = { openGroupList.value = true },
            ) {
                Text("Список групп")
            }
            Button(
                onClick = { openTeamList.value = true },
            ) {
                Text("Список команд")
            }
            Button(
                onClick = { openProtocolPage.value = true },
            ) {
                Text("Работа с протоколами")
            }
        }
        if(openGroupList.value){
            GroupListController(comp.getGroupsSet().toList()).createWindow()
        }
    }
    /*
    //@Composable @Preview
    private fun openGroupListPage(){
        //GroupListController().createWindow()
    }

    fun openTeamListPage(){
        TeamListController()
    }

    fun openProtocolPage(){
        ProtocolPageController()
    }
*/


}