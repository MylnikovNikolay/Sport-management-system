package ru.emkn.kotlin.sms

import java.util.*

class CompetitionsSportsman(
    sportsman: Sportsman,
    val team: CompetitionsTeam,
    val group: Group,
): Sportsman(sportsman){
    var number: Int? = null
        set(value) {
            UsualLogger.log("Спортсмену $name $surname присвоен номер $value")
            field = value
        }
    var startTime: Time? = null
        set(value) {
            UsualLogger.log("Cпортсмену $name $surname присвоено стартовое время $value")
            field = value
        }

    init{
        UsualLogger.log(
            "Спортсмен ${sportsman.name} ${sportsman.surname} из команды " +
                    "'${team.name}' зарегистрирован и добавлен в группу '${group.name}'"
        )
        team.sportsmen.add(this)
        group.sportsmen.add(this)
    }

    val points: Double
        get() {

            if (totalTime == null || group.bestTime == Time.of(23, 59, 59)) {
                UsualLogger.log(
                    "Посчитаны очки спортсмена '$name $surname': 0.0"
                )
                return 0.0
            }
            else{
                UsualLogger.log(
                    "Посчитаны очки спортсмена '$name $surname': ${
                        java.lang.Double.max(
                            0.0,
                            100 * (2 - totalTime!!.toSecondOfDay().toDouble() / group.bestTime.toSecondOfDay())
                        )
                    }"
                )
                return java.lang.Double.max(
                    0.0,
                    100 * (2 - totalTime!!.toSecondOfDay().toDouble() / group.bestTime.toSecondOfDay())
                )
            }
        }

    //Информация о прохождении спортсменом контрольных пунктов, никак не отсортирована (уже видимо отсортирована? + dataWasChanged уже не нужно?)
    private val passingData: TreeSet<PassingCP> = TreeSet()

    //Функции для заполнения passingData - информации о прохождении дистанции
    fun addPassingCP(passingCP: PassingCP){
        passingData.add(passingCP)
    }
    fun removePassingCP(passingCP: PassingCP){
        passingData.remove(passingCP)
    }

    val distance: Distance  get() = group.distance

    //Маршрут, по которому должен бежать спортсмен
    val route: List<ControlPoint>  get() = distance.controlPoints

    //Результат спортсмена, !!первый КП считается как стартовый
    val totalTime: Time?
        get() = if(distanceWasPassed)
            passingData.last().time - passingData.first().time
        else if (totalTimeByResults != null)
            totalTimeByResults
        else null

    var totalTimeByResults: Time? = null

    //События прохождения спортсменом КП в порядке времени.
    val passingList: List<PassingCP>
        get() = passingData.toList()

    //Была ли дистанция корректно пройдена
    private val distanceWasPassed: Boolean
        get() {
            if (number == null)
                return false
            if (startTime == null)
                return false
            //общее условие для обоих режимов - надо чтобы первым был пройден старт, а последним финиш
            if (passingList.size <= 2 || passingList.first().CP != distance.controlPoints.first())
                return false
            if (passingList.last().CP != distance.controlPoints.last())
                return false

            if (distance.modeOfDistance == ModeOfDistance.Strict) {
                val list1 = passingList.map{it.CP}.drop(1).dropLast(1)
                val list2 = distance.controlPoints.drop(1).dropLast(1)
                return list1.MCSsize(list2) >= distance.numberOfCPtoPass - 2
            }

            else {
                val map1 = passingList.map{it.CP}.drop(1).dropLast(1).groupBy {it}.toMutableMap().mapValues {
                        entry -> entry.value.size
                }
                val map2 = distance.controlPoints.drop(1).dropLast(1).groupBy {it}.toMutableMap().mapValues {
                        entry -> entry.value.size
                }
                val CPpassed = map1.mapValues {
                    entry -> kotlin.math.min(entry.value, map2[entry.key]?:0)
                }.map { it.value }.sumOf { it }
                return CPpassed >= distance.numberOfCPtoPass - 2
            }
        }
}

