package ru.emkn.kotlin.sms


/*
Группа: определяется дистанцией и участниками, хранит
всю информацию о соревновании
 */
class GroupByCSV(name: String, distance: Distance, competition: Competitions): Group(name, distance, competition) {



    override fun takeStartsProtocol(protocol: String) {
        if (!CsvReader.checkProtocolIsCorrectCSV(protocol)) {
            printError("В файле со стартовым протоколм группы '$name' ошибка: файл не является корректным csv")
            return
        }
        if (protocol.lines().isEmpty() || protocol.lines()[0] != "Номер,Фамилия,Имя,Г.р.,Разр.,Время старта"){
            printError(
                "В файле со стартовым протоколм группы '$name' ошибка: в файле отсутствует обязательная вторая" +
                        " строчка 'Номер,Фамилия,Имя,Г.р.,Разр.,Время старта'"
            )
            return
        }
        val rows = CsvReader.readWithHeader(protocol)
        that@for (row in rows!!) {
            val number = row["Номер"]!!.toIntOrNull()
            if (number == null) {
                printError(
                    "В файле со стартовым протоколм группы '$name' ошибка: номер спортсмена (${row["Номер"]!!}) " +
                            "должен быть целым числом"
                )
                continue@that
            }
            if (competition.findSportsmanByNumber(number) != null) {
                printError(
                    "В файле со стартовым протоколм группы '$name' ошибка: на соревновании у разных спортсмена не " +
                            "бывает одинаковых номеров ($number)"
                )
                continue@that
            }

            val surname = row["Фамилия"]!!
            val spName = row["Имя"]!!
            val birthYear = row["Г.р."]!!.toIntOrNull()
            if (birthYear == null) {
                printError(
                    "В файле со стартовым протоколм группы '$name' ошибка: год рождения (${row["Г.р."]}) должен быть" +
                            " целым числом"
                )
                continue@that
            }
            val time = stringToTimeOrNull(row["Время старта"]!!)
            if (time == null) {
                printError(
                    "В файле со стартовым протоколм группы '$name' ошибка: невозможное время ($time)"
                )
                continue@that
            }
            val sp = findSportsmanByNameSurnameBirthYear(spName, surname, birthYear)
            if (sp == null) {
                printError(
                "В файле со стартовым протоколом группы '$name' ошибка: не найден спортсмен по строке " +
                        "'${row.toList().joinToString(",")}'"
                )
                continue@that
            }
            sp.number = number
            sp.startTime = time
        }

    }

    override fun getStartsProtocol(): String{
        val strBuilder = StringBuilder("$name,,,,,\n")
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
