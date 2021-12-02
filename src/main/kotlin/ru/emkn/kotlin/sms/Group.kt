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
    fun calcStarts(startTime: Time = Time.of(12, 0, 0), folder: String = "data/starts/") {
        val filepath = folder + "start$name.csv"
        var time = startTime
        members.shuffle()
        members.forEach {
            it.startTime = time
            time = time.plusSeconds(60)
        }
    }

    /*
    Делает протокол старта группы
     */
    fun getStartsProtocol(): String{
        val strBuilder = StringBuilder(name)
        members.forEach{
            val info = if(it.startTime==null) "no information" else it.startTime.toString()
            strBuilder.appendLine("${it.toProtocolRow()},$info")
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
            val info = if (it.startTime==null) "no information" else it.startTime.toString()
            strBuilder.appendLine("${it.toProtocolRow()},$info")
            TODO("ЧТО ЭТО")
        }
        return strBuilder.toString()
    }
}