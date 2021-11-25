package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader


class Competitions(val name: String, val date: String) {
    val teams: List<CompetitionsTeam>
    val groups: List<Group>
    val distances: List<Distance>

    init {
        this.groups = mutableListOf()
        this.teams = mutableListOf()
        this.distances = mutableListOf()
    }
    companion object{
        fun fromProtocols(startProtocols: List<String>, CP_protocols: List<String>): Competitions{
            TODO()
        }
    }
    /*
    constructor(path: String){
        TODO()
    }
    */
    fun findGroupByName(name: String) = groups.find{ it.name == name }

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
        var number = 1
        for(group in groups){
            for(member in group.members){
                member.number = number
                number++
            }
        }
    }

}


/*
Карточка группы на соревнованиях
 */
data class CompetitionsTeam(val name: String,) {
    val members: MutableList<CompetitionsSportsman> = mutableListOf()
}

typealias Time = java.sql.Time//String




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
    data class StartInfo(val time: Time)
    /*
    Результаты забега: список отметок времени,
    когда спортсмен пересекал контрольные точки.
    */
    class ResultInfo(val distance: Distance) {
        val time: MutableMap<ControlPoint,Time> = mutableMapOf()
        //val totalTime: Time get() = time[distance.finish]. - time[distance.start]
    }
}

