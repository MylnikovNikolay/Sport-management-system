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
    val members: MutableList<CompetitionsSportsman> = mutableListOf()

    /*
    //По протоколам создает группу, чтобы генерировать групповой протокол
    constructor(startProtocol: String, controlPointProtocols: List<String>){
        TODO()
    }
    */

    //Делает жеребьевку в группе
    fun calcStarts(startTime: Time = "12:00:00", folder: String = "data/starts/") {
        val filepath = folder + "start$name.csv"
        var time = startTime
        members.shuffle()

        // Увеличивает время на seconds секунд
        fun inc(timeStr: String, seconds: Int): String {
            var hh = timeStr.substring(0..1).toInt()
            var mm = timeStr.substring(3..4).toInt()
            var ss = timeStr.substring(6..7).toInt()

            ss += seconds
            mm += ss / 60
            ss %= 60

            hh += mm / 60
            mm %= 60
            // я не рад тому, что я написал, но оно работает
            //это тебе не короткий код в алгосах
            fun toStr(x: Int): String {
                return if(x < 10) "0$x" else "$x"
            }

            return "${toStr(hh)}:${toStr(mm)}:${toStr(ss)}"
        }
        members.forEach{ it.startInfo = inc(time,60)}
    }

    //Делает протокол старта группы
    fun getStartsProtocol(): String{
        val strBuilder = StringBuilder(name)
        members.forEach{
            val info = if(it.startInfo==null) "no information" else it.startInfo.toString()
            strBuilder.appendLine("${it.toProtocolRow()},$info")
        }
        return strBuilder.toString()
    }

    //Делает протокол результатов группы
    fun getResultsProtocol(): String{
        TODO()
    }
}