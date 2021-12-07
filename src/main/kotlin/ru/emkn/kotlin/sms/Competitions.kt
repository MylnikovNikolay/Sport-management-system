package ru.emkn.kotlin.sms

import java.util.*

/*
В этом файле собраны абстрактные классы, определяющие работу программы.
Они не зависят от формата протоколов - вся работа с протоколами должна быть определена в потомках.
При этом большая часть внутренних вычислений уже реализована тут.
 */
abstract class Competitions(val name: String, val date: String) {
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

    //Обработка заявления от команды (applications)
    abstract fun takeTeamApplication(protocol: String)

    //Создание дистанций и КП из конфигурационного протокола (courses.csv)
    abstract fun takeDistancesAndCPs(protocol: String)

    //Создание групп из конфигурационного протокола (classes.csv)
    abstract fun takeGroupsAndDistances(protocol: String)

    //Заполнение всех результатов из конфигурационного протокола (splits.csv)
    abstract fun takeResults(protocol: String)

}


abstract class Group(val name: String, val distance: Distance){

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
        get() = sportsmen.filter{it.distanceWasPassed}.minOf { it.totalTime }
}


abstract class Distance(val name: String, open val controlPoints: List<ControlPoint>,)


typealias CP = ControlPoint
abstract class ControlPoint(val name: String){
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
    val sportsmen: MutableSet<CompetitionsSportsman> = mutableSetOf()
}

typealias CompSportsman = CompetitionsSportsman
abstract class CompetitionsSportsman(
    sportsman: Sportsman,
    val team: CompetitionsTeam,
    val group: Group,
): Sportsman(sportsman){
    var number: Int? = null
    var startTime: Time? = null

    init{
        team.sportsmen.add(this)
        group.sportsmen.add(this)
    }

    //Информация о прохождении спортсменом контрольных пунктов, никак не отсортирована
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

    //Результат спортсмена
    val totalTime: Time
        get() = if(distanceWasPassed)
            passingData.last().time - passingData.first().time
                else Time.of(23,59,59) //такого точно не будет, это уже следующий день


    //События прохождения спортсменом КП в порядке времени.
    val passingList: List<PassingCP>
        get() = passingData.toList()

    //Была ли дистанция корректно пройдена
    val distanceWasPassed: Boolean
        get() = number!=null && startTime!=null && passingList.map{it.CP}==route

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

