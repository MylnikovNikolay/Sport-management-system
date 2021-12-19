package ru.emkn.kotlin.sms

import java.io.StringBufferInputStream
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

    //Жеребьевка - присвоение номеров участникам и жеребьевка в каждой из групп
    abstract fun makeADraw()

    //Складывает результаты всех групп в единый протокол (results.csv)
    abstract fun getTotalResults(): String

    //Формирует протокол результатов для команд
    abstract fun getTeamResults(): String

    //Обработка заявления от команды (applications)
    abstract fun takeTeamApplication(protocol: String)

    //Создание дистанций и КП из конфигурационного протокола (courses.csv)
    abstract fun takeDistancesAndCPs(protocol: String)

    //Создание групп из конфигурационного протокола (classes.csv)
    abstract fun takeGroupsAndDistances(protocol: String)

    //Заполнение всех результатов из конфигурационного протокола (splits.csv)
    abstract fun takeResults(protocol: String)

    abstract fun takeResultsFromReverseFile(protocol: String)

}


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


abstract class Distance(val name: String, val controlPoints: List<ControlPoint>,){
    init {
        UsualLogger.log(
            "Добавлена дистанция '$name'"
        )
        controlPoints.forEach {
            UsualLogger.log(
                "К дистанции '$name' привязан КП '${it.name}'"
            )
        }
    }
    fun findCPByName(name: String) = controlPoints.find {it.name == name}
    val start = controlPoints.first()
    val finish = controlPoints.last()
}


typealias CP = ControlPoint
abstract class ControlPoint(val name: String){
    init {
        UsualLogger.log(
            "Добавлен КП '$name'"
        )
    }
    private val data: TreeSet<PassingCP> = TreeSet()

    val passingList: List<PassingCP>
        get() = data.toList()

    //Функции для заполнения data - информации о прохождении этой точки спортсменами
    fun addPassingCP(passingCP: PassingCP) = data.add(passingCP)
    fun addPassingCPs(collection: Collection<PassingCP>) = data.addAll(collection)
    fun removePassingCP(passingCP: PassingCP) = data.remove(passingCP)

    //Протокол прохождения КП (README.md)
    abstract fun getProtocol(): String

}

typealias CompTeam = CompetitionsTeam
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

typealias CompSportsman = CompetitionsSportsman
abstract class CompetitionsSportsman(
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

    abstract val points: Double

    //Информация о прохождении спортсменом контрольных пунктов, никак не отсортирована (уже видимо отсортирована? + dataWasChanged уже не нужно?)
    private val passingData: TreeSet<PassingCP> = TreeSet()
    private var dataWasChanged = false

    //Функции для заполнения passingData - информации о прохождении дистанции
    fun addPassingCP(passingCP: PassingCP){
        passingData.add(passingCP)
        dataWasChanged = true
    }
    fun addPassingCPs(collection: Collection<PassingCP>){
        passingData.addAll(collection)
        dataWasChanged = true
    }
    fun removePassingCP(passingCP: PassingCP){
        passingData.remove(passingCP)
        dataWasChanged = true
    }

    val distance: Distance  get() = group.distance

    //Маршрут, по которому должен бежать спортсмен
    val route: List<ControlPoint>  get() = distance.controlPoints

    //Результат спортсмена, !!первый КП считается как стартовый
    val totalTime: Time?
        get() = if(distanceWasPassed)
            passingData.last().time - passingData.first().time
                else null//Time.of(23,59,59) //такого точно не будет, это уже следующий день


    //События прохождения спортсменом КП в порядке времени.
    val passingList: List<PassingCP>
        get() = passingData.toList()

    //Была ли дистанция корректно пройдена
    val distanceWasPassed: Boolean
        get() = number != null && passingData.isNotEmpty() &&
                (startTime ?: Time.of(23, 59,59)) <= passingData.first().time &&
                passingList.map { it.CP } == route

    //Протокол прохождения дистанции (README.md)
    abstract fun getDistancePassingProtocol(): String
}

/*
Событие: спортсмен пересек КП в момент времени time.
Это нужно для простого хранения всей информации.
Спортсмены и КП автоматически получают ссылки на эти объекты, и не нужно заполнять все отдельно
 */
data class PassingCP(val sportsman: CompSportsman, val CP: ControlPoint, val time: Time): Comparable<PassingCP>{
    init {
        UsualLogger.log(
            "Информация о событии: спортсмен ${sportsman.name} ${sportsman.surname} прошел КП '${CP.name}'" +
                    " в $time"
        )
        sportsman.addPassingCP(this)
        CP.addPassingCP(this)
    }
    fun destroy(){
        sportsman.removePassingCP(this)
        CP.removePassingCP(this)
    }
    override fun compareTo(other: PassingCP): Int = time.compareTo(other.time)
}

enum class Gender{ MALE, FEMALE, UNKNOWN }
open class Sportsman(
    val name: String,
    val surname: String,
    val birthYear: Int,
    val level: String,                      //спортивный разряд
    val gender: Gender = Gender.UNKNOWN,    //пол
    val medExamination: String="",          //данные про медосмотр
    val insurance: String="",               //страхование
){
    constructor(sp: Sportsman): this(sp.name,sp.surname,sp.birthYear,sp.level,sp.gender,sp.medExamination,sp.insurance)
}

