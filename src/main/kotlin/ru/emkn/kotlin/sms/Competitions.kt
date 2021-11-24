package ru.emkn.kotlin.sms

/*
class Competitions {
    val name: String
    val date: String
    val groups: List<Group>
    val teams: List<Team>
    val distances: List<Distance>

    constructor(path: String){
        TODO()
    }

    //Прием заявления от команды, добавление всех участников
    fun takeTeamApplication(application: String){
        TODO()
    }

    //Начало соревнований - во всех группах проводится жеребьевка
    fun calcStarts(){
        TODO()
    }
}
*/

data class ControlPoint(val name: String)
data class Distance(
    val controlPoints: List<ControlPoint>
)

