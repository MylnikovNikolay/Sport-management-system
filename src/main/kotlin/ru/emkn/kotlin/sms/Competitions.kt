package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader


class Competitions(val name: String,
                   val date: String,
                   val teams: MutableList<CompetitionsTeam>,
                   val groups: MutableSet<Group>,
                   val distances: MutableSet<Distance>,
                   val groupToDistance: MutableMap<String, String>,
) {

    companion object{
        fun fromProtocols(startProtocols: List<String>, CP_protocols: List<String>): Competitions{
            TODO()
        }

        fun getCompetitionsByConfig(path: String): Competitions {
            val filepath = "$path%s.csv"
            val eventData = csvReader().readAllWithHeader(filepath.format("event"))
            assert(eventData.size == 1)
            requireNotNull(eventData[0]["Название"])
            val name = eventData[0]["Название"]!!
            requireNotNull(eventData[0]["Дата"])
            val date = eventData[0]["Дата"]!!

            val competitions = Competitions(name, date)

            val groupToDistanceData = csvReader().readAllWithHeader(filepath.format("classes"))


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

    constructor(competitions: Competitions):
            this(competitions.name,
                competitions.date,
                competitions.teams,
                competitions.groups,
                competitions.distances,
                competitions.groupToDistance)

    constructor(name: String, date: String):
            this(name, date, mutableListOf(), mutableSetOf(), mutableSetOf(), mutableMapOf())

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
    fun takeTeamApplication(protocol: String){
        val rows: List<List<String>> = csvReader().readAll(protocol)
        val team = CompetitionsTeam(rows[0][0])
        for(i in 1 until rows.size){
            val row = rows[i]
            val group = findGroupByName(row[5])?:continue
            val sportsman = Sportsman.getFromProtocolRow(row)
            val compSportsman = CompetitionsSportsman(sportsman, team, group)
            group.members.add(compSportsman)
        }
    }

    /*
    Начало соревнований - во всех группах проводится жеребьевка
     */
    fun calcStarts(){
        giveNumbersToSportsmenByGroups()
        val time = Time(12,0,0)
        groups.forEach { it.calcStarts(time) }
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


    //получаем результаты из splits

    fun getResultsFromSplits(protocol: String) {
        val rows = csvReader().readAll(protocol)
        rows.forEach { row ->
            assert(row.size % 2 == 1)
            val strNumber = row[0]
            val number = strNumber.toIntOrNull()
            checkNotNull(number) {"Спортсмены идентифицируются по целому числу! $strNumber"}
            val group = findGroupByNumber(number)
            checkNotNull(group) {"Номер никому не присвоен: $number"}
            val distance = group.distance
            val sportsman = group.numbersToMembers[number]
            checkNotNull(sportsman) {"Номер никому не присвоен: $number"}

            val stringsCPs = row.drop(1).filterIndexed{index,_ -> index % 2 == 0 }
            val stringsTimes = row.drop(1).filterIndexed{index,_ -> index % 2 == 1}

            val stringsPairs = stringsCPs.zip(stringsTimes)


            TODO("дописать")

        }
    }

}


/*
Карточка группы на соревнованиях
 */
data class CompetitionsTeam(val name: String,) {
    val members: MutableList<CompetitionsSportsman> = mutableListOf()
}

typealias Time = java.sql.Time


/*
Карточка спортсмена на соревнованиях
 */
data class CompetitionsSportsman(
    val sportsman: Sportsman,
    val team: CompetitionsTeam,
    val group: Group,
    var number: Int? = null,
    var startInfo: StartInfo? = null,
    val resultInfo: ResultInfo? = null,
){

    val distance: Distance = group.distance
    //Составляет протокол прохождения дистанции
    fun getResultProtocol(): String{
        val res = StringBuilder("$number")
        if(resultInfo==null){
            res.appendLine("There's no information!")
            return res.toString()
        }
        distance.controlPoints.forEach { point ->
            val time = resultInfo.time[point]
            res.appendLine("${point.name}, ${time?:"wasn't passed"}")
        }
        return res.toString()
    }

    fun toProtocolRow(): List<String> {
        val sp = sportsman
        return listOf(
            number.toString(), sp.surname, sp.name, sp.birthYear.toString(), sp.level
        )
    }

    /*
    Информация о старте спортсмена - время старта
    */
    data class StartInfo(var time: Time)
    /*
    Результаты забега: список отметок времени,
    когда спортсмен пересекал контрольные точки.
    */
    class ResultInfo(val distance: Distance) {
        val time: MutableMap<ControlPoint,Time> = mutableMapOf()
        //val totalTime: Time get() = time[distance.finish]. - time[distance.start]
    }
}

