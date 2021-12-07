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
            val eventData = csvReader().readAllWithHeader(protocol)
            require(eventData.size == 1)
            requireNotNull(eventData[0]["Название"])
            val name = eventData[0]["Название"]!!

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
        val strBuilder = StringBuilder("Протокол результатов.,,,,,,,,,")
        groups.forEach { group ->
            strBuilder.appendLine(group.getResultsProtocol())
        }
        return strBuilder.toString()
    }

    /*
    Концепция такова - всю некорректную информацию пропускаем
     */

    //Прием заявления от команды
    override fun takeTeamApplication(protocol: String) {
        val rows = csvReader().readAll(protocol).map {list-> list.map { removeExtraSpaces(it) } }
        val teamName = rows[0][0]
        val team = CompetitionsTeamByCSV(teamName)
        teams.add(team)

        for(row in rows.drop(1)){
            if(row.size!=5) continue
            val group = findGroupByName(row[0])?:continue
            val birthYear = row[3].toIntOrNull()?:continue
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
        val rows = csvReader().readAll(protocol).drop(1).map {list-> list.map { removeExtraSpaces(it) } }
        for(row in rows){
            val distName = row.firstOrNull()?:continue
            if (distName.isEmpty()) continue
            val CPList = mutableListOf<ControlPoint>()
            for(CPname in row.drop(1)){
                if (CPname.isNotEmpty()) {
                    val CP = findCPByName(CPname) ?: ControlPointByCSV(CPname)
                    CPList.add(CP)
                }
            }
            if (CPList.size < 2) continue
            if (findDistanceByName(distName) != null) continue
            distances.add(DistanceByCSV(distName,CPList))
            controlPoints.addAll(CPList.toSet())
        }
    }

    //Создание групп по протоколу, как из файла classes.csv
    override fun takeGroupsAndDistances(protocol: String) {
        val rows = csvReader().readAll(protocol).drop(1).map {list-> list.map { removeExtraSpaces(it) } }
        for (row in rows){
            if(row.size!=2) continue
            //не допускаем двух групп с одним именем
            if (row[0].isEmpty()) continue
            if(findGroupByName(row[0])!=null) continue
            val distance = findDistanceByName(row[1])?:continue
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
            val filepath = "$folder/startProtocol%s.csv"
            writeToFile(filepath.format(it.name), it.getStartsProtocol())
        }
    }

    fun writeTotalResults(folder: String = "./data/results") {
        val strBuilder = StringBuilder("Протокол результатов\n")
        groups.forEach {
            strBuilder.appendLine(it.getResultsProtocol())
        }
        writeToFile("$folder/results.csv", strBuilder.toString())
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



/*
companion object{
    fun getCompetitionsByConfig(path: String): Competitions {
        val filepath = "$path%s.csv"
        val eventData = csvReader().readAllWithHeader(File(filepath.format("event")))
        assert(eventData.size == 1)
        requireNotNull(eventData[0]["Название"])
        val name = eventData[0]["Название"]!!
        requireNotNull(eventData[0]["Дата"])
        val date = eventData[0]["Дата"]!!

        val competitions = Competitions(name, date)

        val groupToDistanceData = csvReader().readAllWithHeader(File(filepath.format("classes")))

        //собираем дистанция
        groupToDistanceData.forEach {
            requireNotNull(it["Дистанция"])
            val distance = competitions.findDistanceByName(it["Дистанция"]!!)
            if (distance == null) {
                competitions.distances.add(Distance(it["Дистанция"]!!))
            }

        }

        //теперь считываем группы и на всякий случай еще мапу группа - дистанция
        groupToDistanceData.forEach {
            requireNotNull(it["Название"])
            requireNotNull(it["Дистанция"])
            competitions.groupToDistance[it["Название"]!!] = it["Дистанция"]!!

            val group = competitions.findGroupByName(it["Название"]!!)
            if (group == null)
                competitions.groups.add(Group(it["Название"]!!, Distance(it["Дистанция"]!!)))

        }



        val distanceDataWithHeader = csvReader().readAll(filepath.format("courses"))
        assert(distanceDataWithHeader.isNotEmpty())
        val distanceData = distanceDataWithHeader.drop(1)
        distanceData.forEach { row ->
            assert(row.isNotEmpty())

            val distance = competitions.findDistanceByName(row[0])
            checkNotNull(distance) {"Distance doesn't exist: ${row[0]}"}
            row.drop(1).forEach{
                if (it.isNotEmpty())
                    distance.controlPoints.add(ControlPoint("$it-${distance.name}", distance))
                //Чтобы надежно различать КП, пусть на всякий случай в названии КП есть и название дистанции тоже
            }
        }

        return competitions
    }
}


constructor(path: String): this(getCompetitionsByConfig(path))

fun findGroupByName(name: String) = groups.find{ it.name == name }

fun findDistanceByName (name: String) = distances.find{it.name == name}

fun findGroupByNumber (number: Int): Group? {
    var result: Group? = null
    groups.forEach { group ->
        if (group.numbers != null && number in group.numbers!!)
            result = group
    }
    return result
}

//Прием заявления от команды, добавление всех участников
override fun takeTeamApplication(filepath: String){
    val rows: List<List<String>> = csvReader().readAll(File(filepath))
    val team = CompetitionsTeam(rows[0][0])
    for(i in 1 until rows.size){
        val row = rows[i]
        val group = findGroupByName(row[0])?:continue
        val sportsman = Sportsman.getFromProtocolRow(row)
        val compSportsman = CompetitionsSportsman(sportsman, team, group)
        team.members.add(compSportsman)
        group.members.add(compSportsman)
    }
    teams.add(team)
}

fun takeAllApplicationsFromFolder(path: String) {
    File(path).walk().drop(1).forEach {
        takeTeamApplication(it.path)
    }
}

/*
Начало соревнований - во всех группах проводится жеребьевка
 */
fun calcStarts(){
    giveNumbersToSportsmenByGroups()
    val time = Time.of(12,0,0)
    groups.forEach {
        it.calcStarts(time)
        it.createStartProtocolFile("./data/start protocols/")
    }
}

/*
Все участники получают номера.
В каждой группе номера у участников близкие.
 */
private fun giveNumbersToSportsmenByGroups(){
    var number: Int = 100
    for(group in groups){
        val beginningNumberInGroup = number
        for(member in group.members){
            member.number = number
            group.numbersToMembers[number] = member
            number++
        }
        number = (number / 100 + 1) * 100
        group.numbers = beginningNumberInGroup..number
        // чтобы в каждой группе с круглого числа начинать
    }
}
}

*/
//получаем результаты из splits
/*
fun CompetitionsByCSV.getResultsFromSplits(protocol: String) {
val rows = csvReader().readAll(protocol)
toBeContinued@ for (row in rows) {

    assert(row.size % 2 == 1 && row.size >= 5)
    val strNumber = row[0]
    val number = strNumber.toIntOrNull()
    checkNotNull(number) {"Спортсмены идентифицируются по целому числу! $strNumber"}
    val group = findGroupByNumber(number)
    checkNotNull(group) {"Номер никому не присвоен: $number"}
    val distance = group.distance
    val sportsman = group.numbersToMembers[number]
    checkNotNull(sportsman) {"Номер никому не присвоен: $number"}

    val stringsCPs = row.drop(1).filterIndexed{index, element -> index % 2 == 0 && element.isNotEmpty() }
    val stringsTimes = row.drop(1).filterIndexed{index, element -> index % 2 == 1 && element.isNotEmpty()}
    check(stringsCPs.size == stringsTimes.size)
    val stringsPairs = stringsCPs.zip(stringsTimes)
    check(stringsPairs.size >= 2)

    val pairsWithoutStartAndFinish = stringsPairs.drop(1).dropLast(1)



    val startPair = stringsPairs[0]
    val finishPair = stringsPairs[stringsPairs.lastIndex]

    val startCP = distance.findCPByName("${distance.name}-Start")
    startCP!!.info[number] = stringToTimeOrNull(startPair.second)
    sportsman.resultInfo = sportsman.ResultInfo()
    sportsman.resultInfo!!.CPtimes[startCP] = stringToTimeOrNull(startPair.second)

    val finishCP = distance.findCPByName("${distance.name}-Finish")
    finishCP!!.info[number] = stringToTimeOrNull(finishPair.second)

    sportsman.resultInfo!!.CPtimes[finishCP] = stringToTimeOrNull(finishPair.second)
    /* У меня вопросы к файлу splits - я его не понимаю */

    for (it in pairsWithoutStartAndFinish)  {
        val stringCP = it.first
        val stringTime = it.second
        val CP = distance.findCPByName("${distance.name}-${stringCP}") ?: continue@toBeContinued
        val time = stringToTimeOrNull(stringTime)
        CP.info[number] = time

        sportsman.resultInfo!!.CPtimes[CP] = time
    }
    TODO("Поменять ассерты на continue")
}
 */
