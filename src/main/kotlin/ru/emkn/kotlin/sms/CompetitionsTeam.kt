package ru.emkn.kotlin.sms

open class CompetitionsTeam(open var name: String, open val sportsmen: MutableList<CompetitionsSportsman> = mutableListOf()){
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