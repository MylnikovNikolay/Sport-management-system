package ru.emkn.kotlin.sms

/*
Менеджеры существуют для рутинной работы с бумажками
 */
interface ProtocolManager {
    fun fromString(protocol: String): Competitions

    //Складывает результаты всех групп в единый протокол (results.csv)
    fun getTotalResults(comp: Competitions): String

    //Обработка заявления от команды (applications)
    fun takeTeamApplication(protocol: String, comp: Competitions)

    //Создание дистанций и КП из конфигурационного протокола (courses.csv)
    fun takeDistancesAndCPs(protocol: String, comp: Competitions)

    //Создание групп из конфигурационного протокола (classes.csv)
    fun takeGroupsAndDistances(protocol: String, comp: Competitions)

    //Заполнение всех результатов из конфигурационного протокола (splits.csv)
    fun takeResults(protocol: String, comp: Competitions)

    //Запись стартов из стартового протокола (README.md)
    fun takeStartsProtocol(protocol: String, group: Group)

    //Генерация стартового протокола (README.md)
    fun getStartsProtocol(group: Group): String

    //Генерация протокола результатов (README.md)
    fun getResultsProtocol(group: Group): String

    //Протокол прохождения КП (README.md)
    fun getCPPassingProtocol(group: ControlPoint): String

    //Протокол прохождения дистанции (README.md)
    fun getDistancePassingProtocol(group: CompetitionsSportsman): String
}

object csvProtocolManager: ProtocolManager{
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



    override fun takeStartsProtocol(protocol: String, group: Group) {
        TODO("Not yet implemented")
    }

    override fun getStartsProtocol(group: Group): String {
        TODO("Not yet implemented")
    }

    override fun getResultsProtocol(group: Group): String {
        TODO("Not yet implemented")
    }




    override fun getCPPassingProtocol(group: ControlPoint): String {
        TODO("Not yet implemented")
    }

    override fun getDistancePassingProtocol(group: CompetitionsSportsman): String {
        TODO("Not yet implemented")
    }

}