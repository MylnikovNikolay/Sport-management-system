package ru.emkn.kotlin.sms

import java.util.*

/*
В этом файле собраны абстрактные классы, определяющие работу программы.
Они не зависят от формата протоколов - вся работа с протоколами должна быть определена в потомках.
При этом большая часть внутренних вычислений уже реализована тут.
 */
abstract class Competitions(val name: String, val date: String) {
    init {
        UsualLogger.log(
            "Соревнование с названием '$name' и датой $date создано"
        )
    }
    protected open val teams: MutableSet<CompetitionsTeam> = mutableSetOf()
    protected open val groups: MutableSet<Group> = mutableSetOf()
    protected open val distances: MutableSet<Distance> = mutableSetOf()
    protected open val controlPoints: MutableSet<ControlPoint> = mutableSetOf()
    protected open val sportsmen: MutableSet<CompetitionsSportsman> = mutableSetOf()

    fun findGroupByName(nameOfGroup: String) = groups.find{it.name == nameOfGroup}
    fun findCPByName(nameOfCP: String) = controlPoints.find{it.name == nameOfCP}
    fun findSportsmanByNumber(numberOfSportsman: Int) = sportsmen.find{it.number == numberOfSportsman}
    fun findDistanceByName (nameOfDistance: String) = distances.find {it.name == nameOfDistance}

    fun teams() = teams.toList()

    fun getGroupsSet(): Set<Group> = groups.toSet()
    fun getTeamsSet(): Set<CompetitionsTeam> = teams.toSet()
    fun getDistancesSet(): Set<Distance> = distances.toSet()

    fun addTeam(team: CompetitionsTeam) = teams.add(team)
    fun addGroup(group: Group) = groups.add(group)
    fun addDistance(dist: Distance) = distances.add(dist)
    fun addSportsman(sp: CompetitionsSportsman) = sportsmen.add(sp)
    fun addCP(cp: ControlPoint) = controlPoints.add(cp)
    fun addCPs(cp: Collection<ControlPoint>) = controlPoints.addAll(cp)

    override fun toString() = this.name
    //Жеребьевка - присвоение номеров участникам и жеребьевка в каждой из групп
    fun makeADraw() {
        giveNumbersToSportsmenByGroups()
        val time = Time.of(12,0,0)
        groups.forEach { it.makeADraw(time) }
    }

    abstract fun writeSimpleResultsByGroups(folder: String)

    private fun giveNumbersToSportsmenByGroups(){
        var number = 100
        for(group in groups){
            for(member in group.sportsmen){
                member.number = number
                number++
            }
            number = (number / 100 + 1) * 100
            // чтобы в каждой группе с круглого числа начинать
        }
    }

    abstract fun makeADrawAndWrite(folder: String)
}
