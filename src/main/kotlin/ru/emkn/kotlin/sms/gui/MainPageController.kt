package ru.emkn.kotlin.sms.gui
import ru.emkn.kotlin.sms.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application


//Уж слишком часто нужно
typealias MSB = MutableState<Boolean>

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

    /*
    Рисует все детали внутри главного окна
     */
    @Composable @Preview fun content(){
        val gr = remember { mutableStateOf(false) }
        val ds = remember { mutableStateOf(false) }
        val tm = remember { mutableStateOf(false) }
        val sp = remember { mutableStateOf(false) }
        val cp = remember { mutableStateOf(false) }
        val pr = remember { mutableStateOf(false) }

        Column {
            Button(onClick = { gr.value = true }) { Text("Список групп") }
            Button(onClick = { ds.value = true }) { Text("Список дистанций") }
            Button(onClick = { tm.value = true }) { Text("Список команд") }
            Button(onClick = { sp.value = true }) { Text("Все участники") }
            Button(onClick = { cp.value = true }) { Text("Все КП") }
            Button(onClick = { pr.value = true }) { Text("Работа с протоколами") }
        }
        createWindows(gr, ds, tm, sp, cp, pr)
    }

    /*
    Открывает окна списков групп, команд, ...
     */
    @Composable @Preview fun createWindows(gr: MSB, ds: MSB, tm: MSB, sp: MSB, cp: MSB, pr: MSB){
        if(gr.value)  GroupListController(comp.getGroupsSet().toList(), gr).createWindow()

    }
}