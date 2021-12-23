package ru.emkn.kotlin.sms.gui
import ru.emkn.kotlin.sms.*
import androidx.compose.desktop.ui.tooling.preview.Preview
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
            Button(
                onClick = ::openGroupListPage,
                modifier = Modifier.graphicsLayer { translationX = 0.0f; translationY = 0.0f; }
            ) {
                Text("Список групп")
                Modifier.graphicsLayer {  }
            }
            Button(
                onClick = ::openTeamListPage ,
                modifier = Modifier.graphicsLayer { translationX = 0.0f; translationY = 100.0f; }
            ) {
                Text("Список команд")
                //modifier = Modifier.graphicsLayer { translationX = 50.0f; translationY = 0.0f; }
            }
            Button(
                onClick = ::openProtocolPage,
                modifier = Modifier.graphicsLayer { translationX = 0.0f; translationY = 200.0f; }
            ) {
                Text("Работа с протоколами")
                Modifier.graphicsLayer { translationX = 0.0f; translationY = 50.0f; }
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