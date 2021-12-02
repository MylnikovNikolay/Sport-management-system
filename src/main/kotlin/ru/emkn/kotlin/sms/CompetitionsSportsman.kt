package ru.emkn.kotlin.sms


/*
Карточка группы на соревнованиях
 */
data class CompetitionsTeam(val name: String,) {
    val members: MutableList<CompetitionsSportsman> = mutableListOf()
}




/*
Карточка спортсмена на соревнованиях
 */
data class CompetitionsSportsman(
    val sportsman: Sportsman,
    val team: CompetitionsTeam,
    val group: Group,
){
    var startTime: Time? = null
    var number: Int? = null
    val distance: Distance = group.distance
    var resultInfo: ResultInfo? = null

    //Составляет протокол прохождения дистанции
    fun getResultProtocol(): String{
        val res = StringBuilder("$number")
        if(resultInfo == null){
            res.appendLine("There's no information!")
            return res.toString()
        }
        distance.controlPoints.forEach { point ->
            val time = resultInfo!!.CPtimes[point]
            res.appendLine("${point.name}, ${time?:"wasn't passed"}")
        }
        return res.toString()
    }

    fun toProtocolRow(): String {
        val sp = sportsman
        return "${number.toString()},${sp.surname},${sp.name},${sp.birthYear},${sp.level}"

    }

    /*
    Результаты забега: список отметок времени,
    когда спортсмен пересекал контрольные точки.
    */
    inner class ResultInfo {
        val CPtimes: MutableMap<ControlPoint, Time?> = distance.controlPoints.map {it to null}.toMap().toMutableMap()
        //val totalTime: Time
        //    get() = CPtimes[distance.finish] - CPtimes[distance.start]
    }
}

/*
    Информация о старте спортсмена - время старта
*/
data class StartInfo(var time: Time)