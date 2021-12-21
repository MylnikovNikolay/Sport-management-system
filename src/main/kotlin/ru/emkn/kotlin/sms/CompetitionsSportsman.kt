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
    val distanceWasPassed: Boolean
        get() = number != null && passingData.isNotEmpty() &&
                (startTime ?: Time.of(23, 59,59)) <= passingData.first().time &&
                passingList.map { it.CP } == route
}

