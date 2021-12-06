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
        strBuilder.appendLine("Номер,Фамилия,Имя,Г.р.,Разр.,Время старта")
        sportsmen.forEach{
            //it as CompetitionsSportsman
            val info = if(it.startTime==null) "no information" else it.startTime.toString()
            strBuilder.appendLine("${it.number},${it.surname},${it.name},${it.birthYear},${it.level},$info")
        }
        return strBuilder.toString()
    }

    override fun getResultsProtocol(): String{
        val membersByResult = sportsmen.toMutableList()
        membersByResult.sortBy { it.totalTime } // ? totalTime - startTime
        val strBuilder = StringBuilder("$name\n")

        // Я не понял зачем и что такое № п/п в results.csv в sample-data
        strBuilder.appendLine("Номер,Фамилия,Имя,Г.р.,Разр.,Команда,Результат,Место,Отставание")
        for (i in membersByResult.indices) {
            val sp = membersByResult[i]
            val line = "${sp.number},${sp.surname},${sp.name},${sp.birthYear},${sp.level},${sp.team.name}," +
                    "${sp.totalTime}" + if (i > 0) "${sp.totalTime - membersByResult[i - 1].totalTime}" else ""
            
            strBuilder.appendLine(line)
        }

        return strBuilder.toString()
    }
}
/*
    fun createStartProtocolFile(folder: String) {
        val file = File(folder + "startProtocol$name.csv")
        file.createNewFile()
        file.writeText(getStartsProtocol())
    }
 */