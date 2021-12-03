package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File


class Competitions(
    name: String,
    date: String,
): _Competitions(name, date) {

    companion object{
        fun fromString(protocol: String): Competitions{
            val eventData = csvReader().readAllWithHeader(protocol
            )
            require(eventData.size == 1)
            requireNotNull(eventData[0]["Название"])
            val name = eventData[0]["Название"]!!

            requireNotNull(eventData[0]["Дата"])
            val date = eventData[0]["Дата"]!!

            return Competitions(name, date)
        }
    }


    override fun makeADraw() {
        TODO("Жеребьевка - перенести сюда и исправить старый код")
    }

    override fun getTotalResults(): String {
        TODO("Все результаты складываются в единый протокол по типу results.csv")
    }

    override fun takeTeamApplication(protocol: String) {
        TODO("Прием заявления от команды - перенести и исправить старый код")
    }

    override fun takeDistancesAndCPs(protocol: String) {
        TODO("Создание дистанций и КП - как из courses.csv")
    }

    override fun takeGroupsAndDistances(protocol: String) {
        TODO("Создание групп по протоколу, как из файла classes.csv")
    }

    override fun takeResults(protocol: String) {
        TODO("Заполнение всех результатов - как из splits.csv - перенести и исправить старый код")
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


//получаем результаты из splits
fun Competitions.getResultsFromSplits(protocol: String) {
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
