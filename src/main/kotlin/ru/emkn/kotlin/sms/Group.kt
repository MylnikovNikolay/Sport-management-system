package ru.emkn.kotlin.sms

import java.io.File


/*
Группа: определяется дистанцией и участниками, хранит
всю информацию о соревновании
 */
class Group(name: String, distance: Distance): _Group(name, distance) {
    //override val sportsmen: MutableList<CompetitionsSportsman> = mutableListOf()

    override fun makeADraw(startTime: Time) {
        //val filepath = folder + "start$name.csv"
        var time = startTime
        val members = sportsmen.toMutableList()
        members.shuffle()
        members.forEach {
            it.startTime = time
            time = time.plusSeconds(60)
        }
    }

    override fun takeStartsProtocol(protocol: String) {
        TODO("Not yet implemented")
    }

    override fun getStartsProtocol(): String{
        val strBuilder = StringBuilder(name)
        sportsmen.forEach{
            //it as CompetitionsSportsman
            val info = if(it.startTime==null) "no information" else it.startTime.toString()
            strBuilder.appendLine("${it.number},${it.surname},${it.name},${it.birthYear},${it.level},$info")
        }
        return strBuilder.toString()
    }

    override fun getResultsProtocol(): String{
        val membersByResult = sportsmen.toMutableList()
        TODO("Not yet implemented")
    }
/*
    fun createStartProtocolFile(folder: String) {
        val file = File(folder + "startProtocol$name.csv")
        file.createNewFile()
        file.writeText(getStartsProtocol())
    }
 */
}