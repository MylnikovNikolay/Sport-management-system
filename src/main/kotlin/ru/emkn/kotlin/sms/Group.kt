package ru.emkn.kotlin.sms

import java.io.File


/*
Группа: определяется дистанцией и участниками, хранит
всю информацию о соревновании
 */
class Group(name: String, distance: _Distance): _Group(name, distance) {

    override fun makeADraw(startTime: Time) {
        var time = startTime
        val members = sportsmen.toMutableList()
        members.shuffle()
        members.forEach {
            it.startTime = time
            time = time.plusSeconds(60)
        }
    }

    override fun takeStartsProtocol(protocol: String) {
        TODO("По протоколу старта (как в README.MD) заполнить данные")
    }

    override fun getStartsProtocol(): String{
        val strBuilder = StringBuilder(name + "\n")
        sportsmen.forEach{
            //it as CompetitionsSportsman
            val info = if(it.startTime==null) "no information" else it.startTime.toString()
            strBuilder.appendLine("${it.number},${it.surname},${it.name},${it.birthYear},${it.level},$info")
        }
        return strBuilder.toString()
    }

    override fun getResultsProtocol(): String{
        val membersByResult = sportsmen.toMutableList()
        TODO("Создает протокол результатов группы (как в README.MD)")
    }
}
/*
    fun createStartProtocolFile(folder: String) {
        val file = File(folder + "startProtocol$name.csv")
        file.createNewFile()
        file.writeText(getStartsProtocol())
    }
 */