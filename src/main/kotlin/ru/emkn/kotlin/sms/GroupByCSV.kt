package ru.emkn.kotlin.sms


/*
Группа: определяется дистанцией и участниками, хранит
всю информацию о соревновании
 */
class GroupByCSV(name: String, distance: Distance): Group(name, distance) {

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
        membersByResult.sortBy { if (it.totalTime == null) Time.of(23, 59, 59) else it.totalTime}
        val strBuilder = StringBuilder("$name,,,,,,,,,\n")

        strBuilder.appendLine("№ п/п,Номер,Фамилия,Имя,Г.р.,Разр.,Команда,Результат,Место,Отставание")
        for (i in membersByResult.indices) {
            val sp = membersByResult[i]
            val totalTime = sp.totalTime
            val line: String = "${i + 1},${sp.number},${sp.surname},${sp.name},${sp.birthYear},${sp.level},${sp.team.name}," +
                    if (totalTime != null)
                        "${sp.totalTime},${i + 1},${totalTime - bestTime}"
                    else
                        "снят с дистанции/неявка/некорректные данные,,"
            
            strBuilder.appendLine(line)
        }

        return strBuilder.toString()
    }
}
