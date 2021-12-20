package ru.emkn.kotlin.sms

object CsvProtocolManager: ProtocolManager{

    override fun getTeamResults(comp: Competitions): String {
        val strBuilder = StringBuilder("Протокол результатов для команд,\n")
        comp.teams().sortedBy{ -it.teamPoints }.forEach {
            strBuilder.appendLine("${it.name}," + String.format("%.2f", it.teamPoints))
        }
        return strBuilder.toString()
    }

    override fun fromString(protocol: String): Competitions {
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

    override fun getTotalResults(comp: Competitions): String {
        val strBuilder = StringBuilder("Протокол результатов\n")
        val groups = comp.getGroupsSet()
        groups.forEach {
            strBuilder.appendLine(it.getResultsProtocol())
        }
        return strBuilder.toString()
    }

    override fun takeTeamApplication(protocol: String, comp: Competitions) {
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
        comp.addTeam(team)
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

            val group = comp.findGroupByName(row[0])
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
            comp.addSportsman(CompetitionsSportsmanByCSV(sportsman, team, group))
        }
    }

    override fun takeDistancesAndCPs(protocol: String, comp: Competitions) {
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
                    val CP = comp.findCPByName(CPname) ?: ControlPointByCSV(CPname)
                    CPList.add(CP)
                }
            }
            if (CPList.size < 2){
                printError("В файле с соответствиями дистанций и КП ошибка: строчка с дистанцией должна " +
                        "содержать хотя бы три непустых поля (название, старт и финиш)")
                continue
            }
            if (comp.findDistanceByName(distName) != null) {
                printError("В файле с соответствиями дистанций и КП ошибка: не допускаются две дистанции с одним " +
                        "названием")
                continue
            }
            comp.addDistance(DistanceByCSV(distName,CPList))
            comp.addCPs(CPList.toSet())
        }
    }

    override fun takeGroupsAndDistances(protocol: String, comp: Competitions) {
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
            if(comp.findGroupByName(row[0])!=null) {
                printError("В файле с соответствиями групп и дистанций ошибка: группы с одинаковыми названиями" +
                        " не допускаются")
                continue
            }
            val distance = comp.findDistanceByName(row[1])
            if (distance == null) {
                printError("В файле с соответствиями групп и дистанций ошибка: не найдена дистанция '${row[1]}'")
                continue
            }
            comp.addGroup(GroupByCSV(row[0],distance, comp))
        }
    }

    override fun takeResults(protocol: String, comp: Competitions) {
        if (!CsvReader.checkProtocolIsCorrectCSV(protocol)) {
            printError(
                "Ошибка в файле с результатами группы: файл не является корректным csv"
            )
            return
        }
        if (protocol.lines().isEmpty()) {
            printError(
                "Ошибка в файле с результатами группы: отсутствует обязательная строка с названием группы"
            )
            return
        }
        val group = comp.findGroupByName(CsvReader.readOneLine(protocol.lines()[0])!![0])
        if (group == null) {
            printError("Ошибка в файле с результатами группы: не найдена группа по названию '${
                CsvReader.readOneLine(protocol.lines()[0])!![0]
            }'")
            return
        }
        group.takeResultsProtocol(protocol.lines().drop(1).joinToString ("\n"))
    }

    override fun takeResultsFromSplits(protocol: String, comp: Competitions){
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
            val sportsman = comp.findSportsmanByNumber(spNumber)
            if (sportsman == null) {
                printError("В файле с данными пробега ошибка: никакому спортсмену не присвоен номер $spNumber")
                continue
            }
            that@for(i in 0 until row.size/2){
                val CP = comp.findCPByName(row[2*i+1])
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

    override fun takeResultsFromReverseSplits(protocol: String, comp: Competitions){
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
            val cp = comp.findCPByName(row[0])
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
                val sportsman = comp.findSportsmanByNumber(spNumber)
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

    override fun takeStartProtocol(protocol: String, comp: Competitions){
        if (!CsvReader.checkProtocolIsCorrectCSV(protocol)) {
            printError(
                "В файле со стартовым протоколом группы ошибка: файл не является корректным csv"
            )
            return
        }
        if (protocol.lines().isEmpty()) {
            printError(
                "В файле со стартовым протоколом группы ошибка: в файле отсутствует обязательная первая строка " +
                        "с названием группы"
            )
            return
        }
        val group = comp.findGroupByName(CsvReader.readOneLine(protocol.lines()[0])!![0])
        if (group == null) {
            printError(
                "В файле со стартовым протоколом группы ошибка: группа по названию группы " +
                        "'${CsvReader.readOneLine(protocol.lines()[0])!![0]}' не найдена"
            )
            return
        }
        group.takeStartProtocol(protocol.lines().drop(1).joinToString("\n"))
    }

    override fun takeStartsProtocol(protocol: String, group: Group) {
        if (!CsvReader.checkProtocolIsCorrectCSV(protocol)) {
            printError("В файле со стартовым протоколом группы '${group.name}' ошибка: файл не является корректным csv")
            return
        }
        if (protocol.lines().isEmpty() || protocol.lines()[0] != "Номер,Фамилия,Имя,Г.р.,Разр.,Время старта"){
            printError(
                "В файле со стартовым протоколом группы '${group.name}' ошибка: в файле отсутствует обязательная вторая" +
                        " строчка 'Номер,Фамилия,Имя,Г.р.,Разр.,Время старта'"
            )
            return
        }
        val rows = CsvReader.readWithHeader(protocol)
        that@for (row in rows!!) {
            val number = row["Номер"]!!.toIntOrNull()
            if (number == null) {
                printError(
                    "В файле со стартовым протоколом группы '${group.name}' ошибка: номер спортсмена (${row["Номер"]!!}) " +
                            "должен быть целым числом"
                )
                continue@that
            }
            if (group.competition.findSportsmanByNumber(number) != null) {
                printError(
                    "В файле со стартовым протоколом группы '${group.name}' ошибка: на соревновании у разных " +
                            "спортсменов не бывает одинаковых номеров ($number)"
                )
                continue@that
            }

            val surname = row["Фамилия"]!!
            val spName = row["Имя"]!!
            val birthYear = row["Г.р."]!!.toIntOrNull()
            if (birthYear == null) {
                printError(
                    "В файле со стартовым протоколм группы '${group.name}' ошибка: год рождения (${row["Г.р."]})" +
                            " должен быть целым числом"
                )
                continue@that
            }
            val time = stringToTimeOrNull(row["Время старта"]!!)
            if (time == null) {
                printError(
                    "В файле со стартовым протоколом группы '${group.name}' ошибка: невозможное время ($time)"
                )
                continue@that
            }
            val sp = group.findSportsmanByNameSurnameBirthYear(spName, surname, birthYear)
            if (sp == null) {
                printError(
                    "В файле со стартовым протоколом группы '${group.name}' ошибка: не найден спортсмен по строке " +
                            "'${row.toList().joinToString(",")}'"
                )
                continue@that
            }
            sp.number = number
            sp.startTime = time
        }

    }

    override fun getStartsProtocol(group: Group): String {
        val strBuilder = StringBuilder("${group.name},,,,,\n")
        strBuilder.appendLine("Номер,Фамилия,Имя,Г.р.,Разр.,Время старта")
        group.sportsmen.forEach{
            //it as CompetitionsSportsman
            val info = if(it.startTime==null) "no information" else it.startTime.toString()
            strBuilder.appendLine("${it.number},${it.surname},${it.name},${it.birthYear},${it.level},$info")
        }
        return strBuilder.toString()
    }

    override fun getResultsProtocol(group: Group): String {
        val membersByResult = group.sportsmen.toMutableList()
        membersByResult.sortBy { if (it.totalTime == null) Time.of(23, 59, 59) else it.totalTime}
        val strBuilder = StringBuilder("${group.name},,,,,,,,,\n")

        strBuilder.appendLine("№ п/п,Номер,Фамилия,Имя,Г.р.,Разр.,Команда,Результат,Место,Отставание")
        for (i in membersByResult.indices) {
            val sp = membersByResult[i]
            val totalTime = sp.totalTime
            val line: String = "${i + 1},${sp.number},${sp.surname},${sp.name},${sp.birthYear},${sp.level},${sp.team.name}," +
                    if (totalTime != null)
                        "${sp.totalTime},${i + 1},${totalTime - group.bestTime}"
                    else
                        "снят с дистанции/неявка/некорректные данные,,"

            strBuilder.appendLine(line)
        }

        return strBuilder.toString()
    }

    override fun takeResultsProtocol(protocol: String, group: Group){
        if(!CsvReader.checkProtocolIsCorrectCSV(protocol)){
            printError("Ошибка в файле с результатами группы '${group.name}': файл не является корректным csv")
            return
        }
        val rows = CsvReader.read(protocol)!!
        for (row in rows){
            if (row.size != 7) {
                printError("Ошибка в файле с результатами группы '${group.name}': в этом файле в каждой строке " +
                        "7 полей (место, номер, фамилия, имя, год рождения, разряд, время)")
                return
            }
            val number = row[1].toIntOrNull()
            if (number == null || group.findSportsmanByNumber(number) == null) {
                printError("Ошибка в файле с результатами группы '${group.name}': не найден спортсмен по номеру " +
                        "'${row[1]}'")
                return
            }
            val sp = group.findSportsmanByNumber(number)!!
            val time = row[6].toTimeOrNull()
            if (time == null) {
                printWarning(
                    "Потенциальная ошибка в файле с результатами группы '${group.name}': невозможное время '${row[6]}'"
                )
            }
            sp.totalTimeByResults = time
        }
    }

    override fun getCPPassingProtocol(CP: ControlPoint): String {
        val strBuilder = StringBuilder(CP.name).append(",")
        CP.passingList.forEach { passingCP ->
            strBuilder.appendLine(passingCP.sportsman.number)
            strBuilder.append(",")
            strBuilder.append(passingCP.time)
        }
        return strBuilder.toString()
    }

    override fun getDistancePassingProtocol(sp: CompetitionsSportsman): String {
        val strBuilder = StringBuilder()
        strBuilder.appendLine("${sp.number},")
        val copyOfPassingList = sp.passingList.toMutableList()
        sp.group.distance.controlPoints.forEach { point ->
            val infoAboutPassing = copyOfPassingList.firstOrNull {it.CP == point}
            if (infoAboutPassing == null)
                strBuilder.appendLine("${point.name},-")
            else {
                strBuilder.appendLine("${point.name},${infoAboutPassing.time}")
                copyOfPassingList.remove(infoAboutPassing) //убираем информацию о первом прохождении
            }
        }
        return strBuilder.toString()
    }
}