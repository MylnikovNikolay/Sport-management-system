package ru.emkn.kotlin.sms
import java.io.File
import kotlin.text.StringBuilder


open class CompetitionsByCSV(
    name: String,
    date: String,
): Competitions(name, date) {

    override val distances
        get() = super.distances
    override val groups
        get() = super.groups
    override val controlPoints
        get() = super.controlPoints
    override val sportsmen
        get() = super.sportsmen
    override val teams
        get() = super.teams


    companion object{
        fun fromString(protocol: String): CompetitionsByCSV{
            if (!CsvReader.checkProtocolIsCorrectCSV(protocol)) {
                printError("В файле с общей информацией о соревновании ошибка: файл не является корректным csv")
                throw Exception("Без корректного файла с общей информацией о соревновании дальнейшая работа невозможна")
            }
            if (protocol.lines().isEmpty() ||
                CsvReader.readOneLine(protocol.lines()[0])!![0] != "Название" ||
                CsvReader.readOneLine(protocol.lines()[0])!![1] != "Дата"
            ){
                printError("В файле с общей информацией о соревновании ошибка: в этом файле первая строка " +
                        "должна быть: 'Название,Дата'")
                throw Exception("Без корректного файла с общей информацией о соревновании дальнейшая работа невозможна")
            }
            val eventData = CsvReader.readWithHeader(protocol)!!
            if (eventData.size != 1) {
                printError("В файле с общей информацией о соревновании ошибка: в этом файле должно быть ровно две" +
                        " строки")
                throw Exception("Без корректного файла с общей информацией о соревновании дальнейшая работа невозможна")
            }
            val name = eventData[0]["Название"]!!
            if (name.isEmpty()){
                printError("В файле с общей информацией о соревновании ошибка: пустое название недопустимо")
                throw Exception("Без корректного файла с общей информацией о соревновании дальнейшая работа невозможна")
            }
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
        teams.sortedBy{ -it.teamPoints }.forEach {
            strBuilder.appendLine("${it.name}," + String.format("%.2f", it.teamPoints))
        }
        return strBuilder.toString()
    }

    /*
    Концепция такова - всю некорректную информацию пропускаем
     */

    //Прием заявления от команды
    override fun takeTeamApplication(protocol: String) {
        if(!CsvReader.checkProtocolIsCorrectCSV(protocol)) {
            printError("В файле с заявкой команды ошибка: файл не является корректным csv")
            return
        }
        val rows = CsvReader.read(protocol)!!
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
        if(!CsvReader.checkProtocolIsCorrectCSV(protocol)){
            printError("В файле с соответствиями дистанций и КП ошибка: файл не является корректным csv")
        }
        val rows = CsvReader.read(protocol)!!.drop(1)
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
        if(!CsvReader.checkProtocolIsCorrectCSV(protocol)){
            printError("В файле с соответствиями групп и дистанций ошибка: файл не является корректным csv")
            return
        }
        val rows = CsvReader.read(protocol)!!.drop(1)
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
    override fun takeResultsFromSplits(protocol: String) {
        if (!CsvReader.checkProtocolIsCorrectCSV(protocol)) {
            printError("В файле с данными пробега ошибка: файл не является корректным csv")
            return
        }
        val data = CsvReader.read(protocol)
        val rows = data!!.map{it.filter{str -> str.isNotEmpty()}}
        for(row in rows){
            if(row.size % 2 != 1) {
                printError("В файле с данными пробега ошибка: в этом файле должно быть нечетное количество непустых полей " +
                        "в каждой строке")
                continue
            }
            val spNumber = row[0].toIntOrNull()
            if (spNumber == null){
                printError("В файле с данными пробега ошибка: на первом месте в строке должно стоять целое число -" +
                        " номер спортсмена" )
                continue
            }
            val sportsman = findSportsmanByNumber(spNumber)
            if (sportsman == null) {
                printError("В файле с данными пробега ошибка: никакому спортсмену не присвоен номер $spNumber")
                continue
            }
            that@for(i in 0 until row.size/2){
                val CP = findCPByName(row[2*i+1])
                if (CP == null){
                    printWarning("В файле с данными пробега потенциальная ошибка: не найден КП по названию " +
                            row[2*i+1]
                    )
                    continue@that
                }
                val time = stringToTimeOrNull(row[2*i+2])
                if (time == null){
                    printWarning("В файле с данными пробега потенциальная ошибка: невозможное время " +
                            row[2*i+2]
                    )
                    continue@that
                }

                //Автоматически добавляется куда нужно в спортсмена и в КП
                PassingCP(sportsman,CP,time)
            }
        }
    }


    override fun takeResultsFromReverseSplits(protocol: String) {
        if (!CsvReader.checkProtocolIsCorrectCSV(protocol)) {
            printError("В файле с данными пробега ошибка: файл не является корректным csv")
            return
        }
        val data = CsvReader.read(protocol)
        val rows = data!!.map{it.filter{str -> str.isNotEmpty()}}
        for(row in rows){
            if(row.size % 2 != 1) {
                printError("В файле с данными пробега ошибка: в этом файле должно быть нечетное количество непустых полей " +
                        "в каждой строке")
                continue
            }
            val cp = findCPByName(row[0])
            if (cp == null){
                printError("В файле с данными пробега ошибка: КП ${row[0]} не найден" )
                continue
            }

            that@for(i in 0 until row.size/2){
                val spNumber = row[2*i+1].toIntOrNull()
                if (spNumber == null){
                    printWarning("В файле с данными пробега потенциальная ошибка: номер спортсмена должен быть целым" +
                            " числом"
                    )
                    continue@that
                }
                val sportsman = findSportsmanByNumber(spNumber)
                if (sportsman == null) {
                    printError("В файле с данными пробега ошибка: никакому спортсмену не присвоен номер $spNumber")
                    continue@that
                }
                val time = stringToTimeOrNull(row[2*i+2])
                if (time == null){
                    printWarning("В файле с данными пробега потенциальная ошибка: невозможное время " +
                            row[2*i+2]
                    )
                    continue@that
                }

                //Автоматически добавляется куда нужно в спортсмена и в КП
                PassingCP(sportsman,cp,time)
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
        var number = 100
        for(group in groups){
            for(member in group.sportsmen){
                member.number = number
                number++
            }
            number = (number / 100 + 1) * 100
            // чтобы в каждой группе с круглого числа начинать
        }
    }

}
