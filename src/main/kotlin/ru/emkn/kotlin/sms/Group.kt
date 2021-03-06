package ru.emkn.kotlin.sms

data class Group(val name: String, val distance: Distance, val competition: Competitions){
    init {
        UsualLogger.log(
            "Группа с названием '$name' создана и привязана к дистанции '${distance.name}'"
        )
    }

    val sportsmen: MutableSet<CompetitionsSportsman> = mutableSetOf()

    //fun addSportsman(sportsman: _CompetitionsSportsman) = sportsmen.add(sportsman)
    override fun toString(): String {
        return this.name
    }

    //Жеребьевка в группе
    fun makeADraw(startTime: Time) {
        var time = startTime
        val members = sportsmen.toMutableList()
        members.shuffle()
        members.forEach {
            it.startTime = time
            time = time.plusSeconds(60)
        }
    }

    val bestTime: Time
        get() = sportsmen.minOfOrNull {
            it.totalTime?:Time.of(23,59,59)
        }?:Time.of(23,59,59)

    fun findSportsmanByNumber(int: Int): CompetitionsSportsman?{
        return sportsmen.find{it.number == int}
    }

    fun findSportsmanByNameSurnameBirthYear(
        name: String,
        surname: String,
        birthYear: Int
    ): CompetitionsSportsman? {
        return sportsmen.find{
            it.birthYear == birthYear && it.name == name && it.surname == surname
        }
    }

}