package ru.emkn.kotlin.sms

abstract class CompetitionsTeam(val name: String){
    init {
        UsualLogger.log(
            "Зарегистрирована команда '$name'"
        )
    }
    val sportsmen: MutableSet<CompetitionsSportsman> = mutableSetOf()
    val teamPoints: Double
        get() = sportsmen.sumOf { it.points }
}