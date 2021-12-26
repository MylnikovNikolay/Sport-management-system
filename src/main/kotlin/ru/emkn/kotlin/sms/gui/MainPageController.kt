package ru.emkn.kotlin.sms.gui

import ru.emkn.kotlin.sms.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File

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
        val cnd = mutableStateOf(false) //changingName and Date
        val fp = mutableStateOf(false) //fromProtocols - дату и время из протокола подгрузить
        Row {
            Column {
                Button(onClick = { ds.value = true }) { Text("Список дистанций") }
                Button(onClick = { gr.value = true }) { Text("Список групп") }
                Button(onClick = { tm.value = true }) { Text("Список команд") }
                Button(onClick = { sp.value = true }) { Text("Начать соревнование") }
                Button(onClick = { cp.value = true }) { Text("Загрузить результаты") }
                Button(onClick = { pr.value = true }) { Text("Таблицы результатов") }
            }
            Column {
                Row {
                    Column {
                        Text(text=name.value)
                        Text(text=date.value)
                    }
                    IconButton(onClick = { cnd.value = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
                        )
                    }
                }
                Button (
                    onClick = {fp.value = true}
                ) {
                    Text("Загрузить информацию о соревновании из протокола")
                }
            }
        }

        createChildWindows(gr, ds, tm, sp, cp, pr, cnd, fp)
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
        cnd:MSB,
        fp:MSB
    ){
        if(gr.value)  GroupListController(groups, gr, distances).createWindow()
        if(ds.value)  DistanceListController(distances, ds).createWindow()
        if(tm.value)  TeamListController(teams, tm, groups).createWindow()
        if(sp.value)  StartCompetitionController(groups, sp).createWindow()
        if(cp.value)  LoadSplitsController(groups, cp).createWindow()
        if(pr.value)  ShowResultsController(groups, pr).createWindow()
        if(cnd.value) ChangingNameAndDate(cnd).createWindow()
        if(fp.value)  FromProtocols(fp).createWindow()
    }


    inner class ChangingNameAndDate(cnd: MSB){
        val isOpen = cnd
        val newName = mutableStateOf(name.value)
        val newDate = mutableStateOf(date.value)

        @Composable @Preview fun createWindow() {
            Dialog(
                title = "Редактировать информацию о соревновании",
                onCloseRequest = { isOpen.value = false }
            ) {
                Column {
                    TextField(
                        value = newName.value,
                        onValueChange = { newName.value = it },
                        label = { Text(text="название") }
                    )
                    TextField(
                        value = newDate.value,
                        onValueChange = { newDate.value = it },
                        label = { Text(text="дата") }
                    )
                    Button(onClick = {
                        name.value = newName.value
                        date.value = newDate.value
                        isOpen.value = false
                    }) {
                        Text(text="Сохранить и выйти")
                    }
                }
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