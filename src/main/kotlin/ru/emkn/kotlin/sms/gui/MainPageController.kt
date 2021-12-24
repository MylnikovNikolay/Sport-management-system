package ru.emkn.kotlin.sms.gui
import ru.emkn.kotlin.sms.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
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

    /*
    Рисует все детали внутри главного окна
     */
    @Composable @Preview fun content(){
        var gr = remember { mutableStateOf(false) }
        var ds = remember { mutableStateOf(false) }
        var tm = remember { mutableStateOf(false) }
        var sp = remember { mutableStateOf(false) }
        var cp = remember { mutableStateOf(false) }
        var pr = remember { mutableStateOf(false) }

        Column {
            Button(onClick = { gr.value = true }) { Text("Список групп") }
            Button(onClick = { ds.value = true }) { Text("Список дистанций") }
            Button(onClick = { tm.value = true }) { Text("Список команд") }
            Button(onClick = { sp.value = true }) { Text("Все участники") }
            Button(onClick = { cp.value = true }) { Text("Все КП") }
            Button(onClick = { pr.value = true }) { Text("Работа с протоколами") }
        }
        createWindows(gr.value, ds.value, tm.value, sp.value, cp.value, pr.value)
    }

    /*
    Открывает окна списков групп, команд, ...
     */
    @Composable @Preview fun createWindows(gr: Boolean, ds: Boolean, tm: Boolean, sp: Boolean, cp: Boolean, pr: Boolean){
        if(gr)  GroupListController(comp.getGroupsSet().toList()).createWindow()

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