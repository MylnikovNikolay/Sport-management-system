package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import kotlin.text.StringBuilder


open class CompetitionsByCSV(
    name: String,
    date: String,
): Competitions(name, date) {

    open override val distances
        get() = super.distances
    open override val groups
        get() = super.groups
    open override val controlPoints
        get() = super.controlPoints
    open override val sportsmen
        get() = super.sportsmen
    open override val teams
        get() = super.teams


    companion object{
        fun fromString(protocol: String): CompetitionsByCSV{
            if (!checkProtocolIsCorrectCSV(protocol)) {
                printError("В файле с общей информацией о соревновании ошибка: в csv-файле в каждой строчке равное" +
                        " количество полей")
            }
            if (protocol.lines()[0] != "Название,Дата"){
                printError("В файле с общей информацией о соревновании ошибка: в этом файле первая строчка " +
                        "должна быть: 'Название,Дата'")
            }
            val eventData = csvReader().readAllWithHeader(protocol)
            if (eventData.size != 1) {
                printError("В файле с общей информацией о соревновании ошибка: в этом файле должно быть ровно две" +
                        " строки")
            }
            require(eventData.size == 1)
            requireNotNull(eventData[0]["Название"])
            val name = eventData[0]["Название"]!!
            if (name.isEmpty()){
                printError("В файле с общей информацией о соревновании ошибка: пустое название недопустимо")
                require(name.isNotEmpty())
            }

            requireNotNull(eventData[0]["Дата"])
            val date = eventData[0]["Дата"]!!

            return CompetitionsByCSV(name, date)
        }
    }


    /*
    Переопределения
     */
    override fun makeADraw() {
        giveNumbersToSportsmenByGroups()
        val time = Time.of(12,0,0)
        groups.forEach { it.makeADraw(time) }
    }

    //Все результаты складываются в единый протокол по типу results.csv
    override fun getTotalResults(): String {
        val strBuilder = StringBuilder("Протокол результатов\n")
        groups.forEach {
            strBuilder.appendLine(it.getResultsProtocol())
        }
        return strBuilder.toString()
    }

    override fun getTeamResults(): String {
        val strBuilder = StringBuilder("Протокол результатов для команд,\n")
        teams.sortedBy{ it.teamPoints }.forEach {
            strBuilder.appendLine("${it.name}," + String.format("%.2f", it.teamPoints))
        }
        return strBuilder.toString()
    }

    /*
    Концепция такова - всю некорректную информацию пропускаем
     */

    //Прием заявления от команды
    override fun takeTeamApplication(protocol: String) {
        if(!checkProtocolIsCorrectCSV(protocol)) {
            printError("В файле с заявкой команды ошибка: в csv-файле в каждой строчке равное количество полей")
            return
        }
        val rows = csvReader().readAll(protocol).map {list-> list.map { removeExtraSpaces(it) } }
        if (rows.isEmpty()) {
            printError("В файле с заявкой команды ошибка: отсутствует строка с названием команды")
            return
        }
        val teamName = rows[0][0]
        val team = CompetitionsTeamByCSV(teamName)
        teams.add(team)
        if(rows.size == 1 || rows[1].joinToString(",") != "Группа,Фамилия,Имя,Г.р.,Разр.") {
            printError("В файле с заявкой команды ошибка: отсутствует обязательная вторая строка" +
                    " 'Группа,Фамилия,Имя,Г.р.,Разр.'")
            return
        }

        for(row in rows.drop(2)){
            if(row.size!=5) {
                printError("В файле с заявкой команды ошибка: в каждой строке должно быть ровно пять полей")
                continue
            }

            val group = findGroupByName(row[0])
            if (group == null) {
                printError("В файле с заявкой команды ошибка: группа ${row[0]} не найдена")
                continue
            }
            val birthYear = row[3].toIntOrNull()
            if (birthYear == null) {
                printError("В файле с заявкой команды ошибка: год рождения ${row[3]} должен быть целым числом")
                continue
            }
            val sportsman = Sportsman(name=row[2], surname = row[1], birthYear = birthYear, level = row[4])

            //При создании CompSportsman автоматически добавляется в свою команду и группу
            sportsmen.add(CompetitionsSportsmanByCSV(sportsman, team, group))
        }
    }

    fun takeAllApplicationsFromFolder(path: String) {
        File(path).walk().drop(1).forEach {
            takeTeamApplication(readCSV(it.path))
        }
    }

    //Создание дистанций и КП - как из courses.csv
    override fun takeDistancesAndCPs(protocol: String) {
        if(!checkProtocolIsCorrectCSV(protocol)){
            printError("В файле с соответствиями дистанций и КП ошибка: в csv-файле в каждой строчке равное" +
            " количество полей")
        }
        val rows = csvReader().readAll(protocol).drop(1).map {list-> list.map { removeExtraSpaces(it) } }
        for(row in rows){
            val distName = row.firstOrNull()
            if (distName == null) {
                printError("В файле с соответствиями дистанций и КП ошибка: строчка с дистанцией должна " +
                        "содержать хотя бы три непустых поля (название, старт и финиш)")
                continue
            }
            if (distName.isEmpty()) {
                printError("В файле с соответствиями дистанций и КП ошибка: название дистанции не может быть пустым")
                continue
            }
            val CPList = mutableListOf<ControlPoint>()
            for(CPname in row.drop(1)){
                if (CPname.isNotEmpty()) {
                    val CP = findCPByName(CPname) ?: ControlPointByCSV(CPname)
                    CPList.add(CP)
                }
            }
            if (CPList.size < 2){
                printError("В файле с соответствиями дистанций и КП ошибка: строчка с дистанцией должна " +
                        "содержать хотя бы три непустых поля (название, старт и финиш)")
                continue
            }
            if (findDistanceByName(distName) != null) {
                printError("В файле с соответствиями дистанций и КП ошибка: не допускаются две дистанции с одним " +
                        "названием")
                continue
            }
            distances.add(DistanceByCSV(distName,CPList))
            controlPoints.addAll(CPList.toSet())
        }
    }

    //Создание групп по протоколу, как из файла classes.csv
    override fun takeGroupsAndDistances(protocol: String) {
        if(!checkProtocolIsCorrectCSV(protocol)){
            printError("В файле с соответствиями групп и дистанций ошибка: в csv-файле в каждой строчке равное" +
                    " количество полей")
        }
        val rows = csvReader().readAll(protocol).drop(1).map {list-> list.map { removeExtraSpaces(it) } }
        for (row in rows){
            if(row.size!=2) {
                printError("В файле с соответствиями групп и дистанций ошибка: в этом файле должно быть ровно " +
                        "два поля в каждой строке")
                continue
            }
            //не допускаем двух групп с одним именем
            if (row[0].isEmpty()) {
                printError("В файле с соответствиями групп и дистанций ошибка: недопустимо пустое название у группы")
                continue
            }
            if(findGroupByName(row[0])!=null) {
                printError("В файле с соответствиями групп и дистанций ошибка: группы с одинаковыми названиями" +
                        " не допускаются")
                continue
            }
            val distance = findDistanceByName(row[1])
            if (distance == null) {
                printError("В файле с соответствиями групп и дистанций ошибка: не найдена дистанция '${row[1]}'")
                continue
            }
            groups.add(GroupByCSV(row[0],distance))
        }
    }

    //Заполнение всех результатов - как из splits.csv
    override fun takeResults(protocol: String) {
        val data = csvReader().readAll(protocol).map {list-> list.map { removeExtraSpaces(it) } }
        val rows = data.map{it.filter{str -> str.isNotEmpty()}}
        for(row in rows){
            if(row.size % 2 != 1) continue
            val spNumber = row[0].toIntOrNull()?:continue
            val sportsman = findSportsmanByNumber(spNumber)?:continue
            for(i in 0 until row.size/2){
                val CP = findCPByName(row[2*i+1])?:continue
                val time = stringToTimeOrNull(row[2*i+2])?:continue

                //Автоматически добавляется куда нужно в спортсмена и в КП
                PassingCP(sportsman,CP,time)
            }
        }
    }

    /*
    Функции, связанные с выводом
     */
    fun makeADrawAndWrite(folder: String = "./data/start protocols"){
        makeADraw()
        groups.forEach {
            val filepath = folder + "startProtocol%s.csv"
            writeToFile(filepath.format(it.name), it.getStartsProtocol())
        }
    }

    fun writeTotalResults(folder: String = "./data/results") {
        writeToFile("$folder/results.csv", getTotalResults())
    }

    fun writeTeamResults(folder: String = "./data/results") {
        writeToFile("$folder/teamResults.csv", getTeamResults())
    }

    /*
    Просто внутренние функции
     */
    private fun giveNumbersToSportsmenByGroups(){
        var number: Int = 100
        for(group in groups){
            //val beginningNumberInGroup = number
            for(member in group.sportsmen){
                member.number = number
                //group.numbersToMembers[number] = member
                number++
            }
            number = (number / 100 + 1) * 100
            //group.numbers = beginningNumberInGroup..number
            // чтобы в каждой группе с круглого числа начинать
        }
    }

}
