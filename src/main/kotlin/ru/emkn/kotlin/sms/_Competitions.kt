package ru.emkn.kotlin.sms

/*
В этом файле собраны абстрактные классы, определяющие работу программы.
Они не зависят от формата протоколов - вся работа с протоколами должна быть определена в потомках.
При этом большая часть внутренних вычислений уже реализована тут.
ЭТОТ ФАЙЛ НЕ МЕНЯТЬ, А ТО ВСЕ ПОЛЕТИТ К ЧЕРТЯМ
 */
abstract class _Competitions(val name: String, val date: String) {
    protected val teams: MutableSet<_CompetitionsTeam> = mutableSetOf()
    protected val groups: MutableSet<_Group> = mutableSetOf()
    protected val distances: MutableSet<_Distance> = mutableSetOf()

    companion object{
        //Эту функцию надо реализовать в наследнике (чтение event.csv)
        //fun fromString(protocol: String): Competitions{}
    }

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


abstract class _Group(val name: String, val distance: _Distance){

    val sportsmen: MutableSet<_CompetitionsSportsman> = mutableSetOf()

    //fun addSportsman(sportsman: _CompetitionsSportsman) = sportsmen.add(sportsman)

    //Жеребьевка в группе
    abstract fun makeADraw(startTime: Time)

    //Запись стартов из стартового протокола (README.md)
    abstract fun takeStartsProtocol(protocol: String)

    //Генерация стартового протокола (README.md)
    abstract fun getStartsProtocol(): String

    //Генерация стартового протокола (README.md)
    abstract fun getResultsProtocol(): String

    val bestTime: Time = sportsmen.filter{it.distanceWasPassed}.minOf { it.totalTime }
}


abstract class _Distance(val name: String, open val controlPoints: List<_ControlPoint>,)


typealias CP = _ControlPoint
abstract class _ControlPoint(val name: String){
    private val data: MutableSet<PassingCP> = mutableSetOf()

    //Функции для заполнения data - информации о прохождении этой точки спортсменами
    fun addPassingCP(passingCP: PassingCP) = data.add(passingCP)
    fun addPassingCPs(collection: Collection<PassingCP>) = data.addAll(collection)
    fun removePassingCP(passingCP: PassingCP) = data.remove(passingCP)

    //Протокол прохождения КП (README.md)
    abstract fun getProtocol(): String

}

typealias CompTeam = _CompetitionsTeam
abstract class _CompetitionsTeam(val name: String){
    val sportsmen: MutableSet<_CompetitionsSportsman> = mutableSetOf()
}

typealias CompSportsman = _CompetitionsSportsman
abstract class _CompetitionsSportsman(
    sportsman: Sportsman,
    val team: _CompetitionsTeam,
    val group: _Group,
): Sportsman(sportsman){
    var number: Int? = null
    var startTime: Time? = null

    //Информация о прохождении спортсменом контрольных пунктов, никак не отсортирована
    private val passingData: MutableSet<PassingCP> = mutableSetOf()
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

    val distance: _Distance  get() = group.distance

    //Маршрут, по которому должен бежать спортсмен
    val route: List<_ControlPoint>  get() = distance.controlPoints

    //Результат спортсмена
    val totalTime: Time
        get() = if(distanceWasPassed)
            passingList.last().time - passingList.first().time
        else Time.of(0,0,0)


    //События прохождения спортсменом КП в порядке времени.
    //Считаются лениво
    var passingList: List<PassingCP> = listOf()
        private set
        get(){
            if(dataWasChanged) passingList = passingData.sorted()
            dataWasChanged = false
            return passingList
        }

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
data class PassingCP(val sportsman: CompSportsman, val CP: _ControlPoint, val time: Time): Comparable<PassingCP>{
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

