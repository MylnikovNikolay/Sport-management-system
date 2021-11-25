package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File


class Competitions(val name: String, val date: String) {
    val members: List<CompetitionsMember>
    val groups: List<Group>
    val teams: List<Team>
    val distances: List<Distance>

    init {
        this.members = mutableListOf()
        this.groups = mutableListOf()
        this.teams = mutableListOf()
        this.distances = mutableListOf()
    }
    companion object{
        fun fromProtocols(startProtocols: List<String>, CP_protocols: List<String>): Competitions{
            TODO()
        }
    }
    /*
    constructor(path: String){
        TODO()
    }
    */

    //Прием заявления от команды, добавление всех участников
    fun takeTeamApplication(application: String){
        TODO()
    }

    //Начало соревнований - во всех группах проводится жеребьевка
    fun calcStarts(){
        TODO()
    }
}


/*
Спортсмен как участник соревнований.
 */
data class CompetitionsMember(
    val sportsman: Sportsman,
    val team: Team,
    val group: Group,
    var number: Int? = null,
    var startInfo: StartInfo? = null,
    val resultInfo: ResultInfo? = null,
){
    val distance: Distance
        get() = group.distance

    //Составляет протокол прохождения дистанции
    fun getResultProtocol(): String{
        val res = StringBuilder("$number")
        if(resultInfo==null){
            res.appendLine("There's no information!")
            return res.toString()
        }
        distance.controlPoints.forEach { point ->
            val time = resultInfo[point]
            res.appendLine("${point.name}, ${time?:"wasn't passed"}")
        }
        return res.toString()
    }

    fun toRow(): List<String> {
        val sp = sportsman
        return listOf(
            number.toString(), sp.surname, sp.name, sp.birthYear.toString(), sp.level
        )
    }
}


class ControlPoint(val name: String, val distance: Distance) {

    //Регистрирует номер прошедшего этот пункт участника и время
    val info: MutableMap<Int,Time> = mutableMapOf()


    //Конструктор строит сразу по протоколу
    constructor(inputDistance: Distance, inputProtocol: String):
            this(nameFromProtocol(inputProtocol), inputDistance){
                dataFromProtocol(inputProtocol)
            }

    //По файлу с протоколом
    constructor(pathname: String, inputDistance: Distance, fromFile: Boolean = true) :
            this(inputDistance, inputProtocol = stringFromPathname(pathname))

    companion object {
        fun stringFromPathname(pathname: String): String {
            assert(File(pathname).exists() && File(pathname).extension == "csv") {
                "Файла не существует либо у него не csv-расширение"
            }
            return File(pathname).readText()
        }
        //Вычленяет name из протокола
        fun nameFromProtocol(protocol: String): String {
            val rows: List<List<String>> = csvReader().readAll(protocol)
            assert(rows.isNotEmpty() && rows[0].isNotEmpty()) {
                "Неверный формат CSV-файла для протокола КП: отсутствует имя"
            }
            return rows[0][0]
        }
    }


    //Заполняет info из протокола
    fun dataFromProtocol(protocol: String){
        val rows: List<List<String>> = csvReader().readAll(protocol)
        assert(rows.isNotEmpty() && rows[0].isNotEmpty()) {
            "Неверный формат CSV-файла для протокола КП: отсутствует имя"
        }
        assert(rows[0][0] == name) {
            "Имя КП не соответствует КП"
        }

        rows.drop(1).forEach {
            info[it[0].toInt()] = it[1]
        }
    }

}


class Distance(val name: String) {
    val controlPoints: List<ControlPoint> = mutableListOf()
}

