package ru.emkn.kotlin.sms

import java.io.File


/*
Группа: определяется дистанцией и участниками, хранит
всю информацию о соревновании
 */
class Group(val name: String, val distance: Distance) {
    val members: MutableList<CompetitionsSportsman> = mutableListOf()

    //храним номера, принадлежащие данной группе
    var numbers: IntRange? = null
        set(value) {
            if (numbers == null)
                field = value
        }

    val numbersToMembers: MutableMap <Int, CompetitionsSportsman> = mutableMapOf()
    /*
    //По протоколам создает группу, чтобы генерировать групповой протокол
    constructor(startProtocol: String, controlPointProtocols: List<String>){
        TODO()
    }
    */

    /*
    Делает жеребьевку в группе
     */
    fun calcStarts(startTime: Time = Time(12, 0, 0), folder: String = "data/starts/") {
        val filepath = folder + "start$name.csv"
        var time = startTime
        members.shuffle()

        // Увеличивает время на seconds секунд
        fun increaseTime(time: Time, increaseBy: Int): Time {
            var hours = time.hours
            var minutes = time.minutes
            var seconds = time.seconds

            seconds += increaseBy
            minutes += seconds / 60
            seconds %= 60

            hours += minutes / 60
            minutes %= 60

            return Time(hours, minutes, seconds)
        }
        members.forEach {
            it.startInfo = StartInfo(time)
            time = increaseTime(time, 60)
        }
    }

    /*
    Делает протокол старта группы
     */
    fun getStartsProtocol(): String{
        val strBuilder = StringBuilder(name)
        members.forEach{
            val info = if(it.startInfo==null) "no information" else it.startInfo.toString()
            strBuilder.appendLine("${it.toProtocolRow().joinToString(",")},$info")
        }
        return strBuilder.toString()
    }

    fun createStartProtocolFile(folder: String) {
        val file = File(folder + "startProtocol$name.csv")
        file.createNewFile()
        file.writeText(getStartsProtocol())
    }

    //Делает протокол результатов группы
    fun getResultsProtocol(): String{
        val membersByResult = members
        val strBuilder = StringBuilder(name)
        membersByResult.forEach{
            val info = if (it.startInfo==null) "no information" else it.startInfo.toString()
            strBuilder.appendLine("${it.toProtocolRow()},$info")
        }
        return strBuilder.toString()
    }
}