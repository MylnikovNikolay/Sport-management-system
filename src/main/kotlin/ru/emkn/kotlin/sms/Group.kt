package ru.emkn.kotlin.sms

abstract class Group(val name: String, val distance: Distance){
    init {
        UsualLogger.log(
            "Группа с названием '$name' создана и привязана к дистанции '${distance.name}'"
        )
    }

    val sportsmen: MutableSet<CompetitionsSportsman> = mutableSetOf()

    //fun addSportsman(sportsman: _CompetitionsSportsman) = sportsmen.add(sportsman)

    //Жеребьевка в группе
    abstract fun makeADraw(startTime: Time)

    //Запись стартов из стартового протокола (README.md)
    abstract fun takeStartsProtocol(protocol: String)

    //Генерация стартового протокола (README.md)
    abstract fun getStartsProtocol(): String

    //Генерация протокола результатов (README.md)
    abstract fun getResultsProtocol(): String

    val bestTime: Time
        get() = sportsmen.minOf { it.totalTime?:Time.of(23,59,59) }
}