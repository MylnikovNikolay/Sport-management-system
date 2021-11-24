package ru.emkn.kotlin.sms


/*
Спортсмен как участник одной из групп
 */
data class GroupMember(
    val sportsman: Sportsman,
    var number: Int? = null,
    var startInfo: StartInfo? = null,
    var resultInfo: ResultInfo? = null,
){
    //Составляет протокол прохождения дистанции
    fun getResultProtocol(): String{
        TODO()
    }
}

/*
Информация о старте спортсмена - время старта
 */
data class StartInfo(val time: String)

/*
Результаты забега: список отметок времени,
когда спортсмен пересекал контрольные точки
 */
data class ResultInfo(val controlPointTimes: List<String>)


/*
Группа: определяется дистанцией и участниками, хранит
всю информацию о соревновании
 */
class Group(val name: String, val distance: Distance) {
    val members: MutableList<GroupMember> = mutableListOf()

    /*
    //По протоколам создает группу, чтобы генерировать групповой протокол
    constructor(startProtocol: String, controlPointProtocols: List<String>){
        TODO()
    }
    */

    //Делает жеребьевку
    fun calcStarts(){
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