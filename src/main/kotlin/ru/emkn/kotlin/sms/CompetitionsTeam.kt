package ru.emkn.kotlin.sms

data class CompetitionsTeam(var name: String, val sportsmen: MutableList<CompetitionsSportsman> = mutableListOf()){
    init {
        UsualLogger.log(
            "Зарегистрирована команда '$name'"
        )
    }
    val teamPoints: Double
        get() = sportsmen.sumOf { it.points }

    override fun toString(): String {
        return this.name
    }
}