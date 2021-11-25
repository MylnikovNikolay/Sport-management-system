package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File


typealias Time = String

/*
Информация о старте спортсмена - время старта
 */
typealias StartInfo = Time

/*
Результаты забега: список отметок времени,
когда спортсмен пересекал контрольные точки.
 */
typealias ResultInfo = Map<ControlPoint,Time>

/*
Группа: определяется дистанцией и участниками, хранит
всю информацию о соревновании
 */
class Group(val name: String, val distance: Distance) {
    val members: MutableList<CompetitionsMember> = mutableListOf()

    /*
    //По протоколам создает группу, чтобы генерировать групповой протокол
    constructor(startProtocol: String, controlPointProtocols: List<String>){
        TODO()
    }
    */

    //Делает жеребьевку в группе
    fun calcStarts(startTime: Time = "12:00:00", folder: String = "data/starts/") {
        val filepath = folder + "start$name.csv"
        val file = File(filepath)
        if (!file.exists()) {
            file.createNewFile()
        }

        var time = startTime
        val startOrder = members.toMutableList()
        startOrder.shuffle()

        // Увеличивает время на seconds секунд
        fun inc(timeStr: String, seconds: Int): String {
            TODO()
        }

        csvWriter().open(file) {
            writeRow(name)
            startOrder.forEach {
                writeRow(it.toRow(), time)
            }
            time = inc(time, 60)
        }
    }

    //Делает протокол старта группы
    fun getStartsProtocol(): String{
        TODO()
    }

    //Делает протокол результатов группы
    fun getResultsProtocol(): String{
        TODO()
    }
}