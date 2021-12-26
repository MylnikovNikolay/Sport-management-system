package ru.emkn.kotlin.sms.gui

import ru.emkn.kotlin.sms.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.materialIcon
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File
import kotlin.system.exitProcess

//Уж слишком часто нужно
typealias MSB = MutableState<Boolean>

class MainPageController{
    val name: MutableState<String> = mutableStateOf("Название")
    val date: MutableState<String> = mutableStateOf("Дата")
    val groups: MutableState<List<MutableState<Group>>> = mutableStateOf(listOf())
    val distances: MutableState<List<MutableState<Distance>>> = mutableStateOf(listOf())
    val teams: MutableState<List<MutableState<CompetitionsTeam>>> = mutableStateOf(listOf())

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
        val cn = mutableStateOf(false) //changingName
        val cd = mutableStateOf(false) //changingDate
        val fp = mutableStateOf(false) //fromProtocols - дату и время из протокола подгрузить
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
                Row {
                    Column {
                        Text(text = name.value)
                        Button(onClick = {
                            cn.value = true
                        }) { Text("Поменять название соревнования") }

                    }
                    Column {
                        Text(text = date.value)

                        Button(onClick = {
                            cd.value = true
                        }) { Text("Поменять дату соревнования") }
                    }
                }
                Row {
                    Button (
                        onClick = {fp.value = true}
                            ) {
                        Text("Загрузить информацию о соревновании из протокола")
                    }
                }
            }
        }
        createChildWindows(gr, ds, tm, sp, cp, pr, cn, cd, fp)
    }

    /*
    Открывает окна списков групп, команд, ...
     */
    @Composable @Preview fun createChildWindows(
        gr: MSB,
        ds: MSB,
        tm: MSB,
        sp: MSB,
        cp: MSB,
        pr: MSB,
        cn:MSB,
        cd:MSB,
        fp:MSB
    ){
        println(distances.value.size)
        if(gr.value)  GroupListController(groups, gr, distances).createWindow()
        if(ds.value)  DistanceListController(distances, ds).createWindow()
        if(tm.value)  TeamListController(teams, tm, groups).createWindow()
        if(cn.value) ChangingName(cn).createWindow()
        if(cd.value) ChangingDate(cd).createWindow()
        if(fp.value) FromProtocols(fp).createWindow()
    }

    inner class ChangingDate(cd: MSB){
        val isOpen = cd
        val newDate = mutableStateOf(date.value)

        @Composable @Preview fun createWindow() {
            Window(
                onCloseRequest = {isOpen.value = false},
                title = "Замена названия ${date.value}"
            ) {
                MaterialTheme {
                    content()
                }
            }
        }


        @Composable
        @Preview
        fun content() {
            Row {
                 Input (
                    onTextChanged = {newDate.value = it.lines().first()},
                    onAddClicked = {
                        if (newDate.value.isNotBlank())
                            date.value = newDate.value
                        isOpen.value = false
                    },
                    text = newDate.value,
                     imageVector = Icons.Default.Edit
                )
            }

        }

    }

    inner class ChangingName(cn: MSB){
        val isOpen = cn
        val newName = mutableStateOf(name.value)

        @Composable @Preview fun createWindow() {
            Window(
                onCloseRequest = {isOpen.value = false},
                title = "Замена названия ${name.value}"
            ) {
                MaterialTheme {
                    content()
                }
            }
        }


        @Composable
        @Preview
        fun content() {
            Row {
                Input (
                    onTextChanged = {
                        newName.value = it.lines().first()
                                    },
                    onAddClicked = {
                        if (newName.value.isNotBlank())
                            name.value = newName.value
                        isOpen.value = false
                    },
                    text = newName.value,
                    imageVector = Icons.Default.Edit
                )
            }

        }

    }

    inner class FromProtocols(fp: MSB) {
        val isOpen = fp
        val fileContent: MutableState<String?> = mutableStateOf(null)
        val inp: MutableState<String> = mutableStateOf("test-data/data/config/")

        @Composable
        @Preview
        fun createWindow() {
            Window(
                onCloseRequest = {isOpen.value = false},
                title = "Загрузка из протокола"
            ) {
                Column {
                    Input(
                        onAddClicked = {
                            if (fileContent.value != null) {
                                val comp = Csv.createCompetitions(fileContent.value!!)
                                name.value = comp.name
                                date.value = comp.date
                                isOpen.value = false
                            }

                        },
                        onTextChanged = { text ->
                            val it = text.lines().first()
                            var file = if (File(it).exists()) File(it) else null
                            file = if(file != null && file.extension == "csv") file else null
                            if (file != null && CsvReader.read(readCSV(it)) != null){
                                try {
                                    val comp = Csv.createCompetitions(readCSV(it))
                                    fileContent.value =  readCSV(it)
                                } catch (e: Exception) {
                                    fileContent.value = null
                                }
                            }
                            else {
                                fileContent.value = null
                            }
                            inp.value = text
                        },
                        text = inp.value
                    )
                    Text(
                        color = Color.Red,
                        fontStyle = FontStyle(9),
                        text = if (fileContent.value == null) "Не существует корректного файла с таким расположением" else ""
                    )
                }
            }
        }
    }
}