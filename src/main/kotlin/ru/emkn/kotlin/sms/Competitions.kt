package ru.emkn.kotlin.sms



class Competitions {
    val name: String
    val date: String
    val members: List<CompetitionsMember>
    val groups: List<Group>
    val teams: List<Team>
    val distances: List<Distance>

    constructor(name: String, date: String){
        this.name = name
        this.date = date
        this.members = mutableListOf()
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

    //Прием заявления от команды, добавление всех участников
    fun takeTeamApplication(application: String){
        TODO()
    }

    //Начало соревнований - во всех группах проводится жеребьевка
    fun calcStarts(){
        TODO()
    }
}


/*
Спортсмен как участник соревнований.
 */
data class CompetitionsMember(
    val sportsman: Sportsman,
    val team: Team,
    val group: Group,
    var number: Int? = null,
    var startInfo: StartInfo? = null,
    val resultInfo: ResultInfo = mutableMapOf(),
){
    val distance: Distance
        get() = group.distance

    //Составляет протокол прохождения дистанции
    fun getResultProtocol(): String{
        val res = StringBuilder("$number")
        if(resultInfo==null){
            res.appendLine("There's no information!")
            return res.toString()
        }
        distance.controlPoints.forEach { point ->
            val time = resultInfo.get(point)
            res.appendLine("${point.name}, ${time?:"wasn't passed"}")
        }
        return res.toString()
    }
}


class ControlPoint{
    val name: String
    val distance: Distance

    //Регистрирует номер прошедшего этот пункт участника и время
    val info: MutableMap<Int,Time> = mutableMapOf()

    constructor(name: String, distance: Distance){
        this.name = name
        this.distance = distance
    }

    //Заполняет info из протокола
    fun dataFromProtocol(protocol: String){
        TODO()
    }

}
class Distance{
    val name: String
    val controlPoints: List<ControlPoint> = mutableListOf()
    constructor(name: String){
        this.name = name
    }
}

