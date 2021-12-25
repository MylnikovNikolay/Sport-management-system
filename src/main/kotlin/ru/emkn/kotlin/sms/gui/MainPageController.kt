package ru.emkn.kotlin.sms.gui

import ru.emkn.kotlin.sms.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlin.system.exitProcess

//Уж слишком часто нужно
typealias MSB = MutableState<Boolean>

class MainPageController{
    var name: MutableState<String> = mutableStateOf("Название")
    var date: MutableState<String> = mutableStateOf("Дата")
    var groups: MutableState<List<MutableState<Group>>> = mutableStateOf(listOf())
    var distances: MutableState<List<MutableState<Distance>>> = mutableStateOf(listOf())
    var teams: MutableState<List<MutableState<CompetitionsTeam>>> = mutableStateOf(listOf())

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
        //Эти переменные указывают на то, открыты ли окна-дети
        val gr = mutableStateOf(false)
        val ds = mutableStateOf(false)
        val tm = mutableStateOf(false)
        val sp = mutableStateOf(false)
        val cp = mutableStateOf(false)
        val pr = mutableStateOf(false)

        Row {
            Column {
                Button(onClick = { gr.value = true }) { Text("Список групп") }
                Button(onClick = { ds.value = true }) { Text("Список дистанций") }
                Button(onClick = { tm.value = true }) { Text("Список команд") }
                Button(onClick = { sp.value = true }) { Text("Все участники") }
                Button(onClick = { cp.value = true }) { Text("Все КП") }
                Button(onClick = { pr.value = true }) { Text("Работа с протоколами") }
            }
            Column {
                Text(text = name.value)
                Text(text = date.value)
            }
        }
        createChildWindows(gr, ds, tm, sp, cp, pr)
    }

    /*
    Открывает окна списков групп, команд, ...
     */
    @Composable @Preview fun createChildWindows(gr: MSB, ds: MSB, tm: MSB, sp: MSB, cp: MSB, pr: MSB){
        if(gr.value)  GroupListController(groups, gr).createWindow()
        if(ds.value)  DistanceListController(distances, ds).createWindow()
        if(tm.value)  TeamListController(teams, tm, groups).createWindow()
    }
}