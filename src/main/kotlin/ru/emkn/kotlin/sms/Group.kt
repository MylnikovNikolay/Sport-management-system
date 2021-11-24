package ru.emkn.kotlin.sms




typealias Time = String

/*
Информация о старте спортсмена - время старта
 */
typealias StartInfo = Time

/*
Результаты забега: список отметок времени,
когда спортсмен пересекал контрольные точки.
 */
typealias ResultInfo = Map<ControlPoint,Time>

/*
Группа: определяется дистанцией и участниками, хранит
всю информацию о соревновании
 */
class Group(val name: String, val distance: Distance) {
    val members: MutableList<CompetitionsMember> = mutableListOf()

    /*
    //По протоколам создает группу, чтобы генерировать групповой протокол
    constructor(startProtocol: String, controlPointProtocols: List<String>){
        TODO()
    }
    */

    //Делает жеребьевку в группе
    fun calcStarts(startTime: Time = "12:00:00"){
        TODO()
    }

    //Делает протокол старта группы
    fun getStartsProtocol(): String{
        TODO()
    }

    //Делает протокол результатов группы
    fun getResultsProtocol(): String{
        TODO()
    }
}