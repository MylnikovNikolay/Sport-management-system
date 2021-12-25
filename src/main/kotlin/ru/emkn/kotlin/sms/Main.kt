package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.gui.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState


fun main(){
     val CPM = CsvProtocolManager
     val dir = "./test-data/sample-data/"
     val comp = CompetitionsByCSV("Мои соревнования","24.12.2021")
     CPM.createDistancesAndCPs(readCSV(dir+"courses.csv"),comp)
     CPM.createGroupsAndDistances(readCSV(dir+"classes.csv"),comp)
}

/*
fun main() {
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Sport Management System",
            state = rememberWindowState(
                position = WindowPosition(alignment = Alignment.Center),
            ),
        ) {
            MaterialTheme {
                RootContent(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
*/



/*
fun main(args: Array<String>) {
    UsualLogger.start()
    ErrorsAndWarningsLogger.start()
    /*
     * В папке test-data есть несколько наборов тестовых данных
     * Можно указать как параметр командной строки test-data/data , test-data/data1 или test-data/small-test/data
     * для проверки на одном из этих наборов
     */
    val dataFolder = if (args.isNotEmpty()) args[0] else "data"
    val configPath = "./$dataFolder/config/%s"
    val eventFileName = "event.csv"
    val coursesFileName = "courses.csv"
    val classesFileName = "classes.csv"
    val competitions = CompetitionsByCSV.fromString(readCSV( configPath.format(eventFileName) ))
    competitions as CompetitionsByCSV
    CsvProtocolManager.createDistancesAndCPs(readCSV( configPath.format(coursesFileName) ), competitions)
    Csv.createGroupsAndDistances(readCSV( configPath.format(classesFileName) ), competitions)

    competitions.takeAllApplicationsFromFolder("./$dataFolder/applications/")

    competitions.makeADrawAndWrite("./$dataFolder/start protocols/")

    /*
     * Тут пользователь как-то сообщит программе, что соревнование закончилось и результаты загружены
     * (пока что хотя бы в папку в виде файла). После этого произойдет загрузка рез-ов.
     */
    println("Поместите результаты в файл в папке $dataFolder/splits и введите название файла:")
    // TODO(стоит сделать эту часть адекватнее, все равно файл со сплитами один всего)
    val splitsFileName = readLine()

    Csv.fillResultsByGroups(readCSV( "./$dataFolder/splits/$splitsFileName" ), competitions)
    competitions.writeTotalResults("./$dataFolder/results")
    competitions.writeTeamResults("./$dataFolder/results")
}
*/

