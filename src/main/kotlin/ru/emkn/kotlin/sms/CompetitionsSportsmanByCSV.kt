package ru.emkn.kotlin.sms


class CompetitionsTeamByCSV(name: String): CompetitionsTeam(name)

class CompetitionsSportsmanByCSV(
    sportsman: Sportsman,
    team: CompetitionsTeam,
    group: Group,
) : CompetitionsSportsman(sportsman,team,group){

    override fun getDistancePassingProtocol(): String {
        val strBuilder = StringBuilder()
        strBuilder.appendLine("$number,")
        val copyOfPassingList = passingList.toMutableList()
        group.distance.controlPoints.forEach { point ->
            val infoAboutPassing = copyOfPassingList.firstOrNull {it.CP == point}
            if (infoAboutPassing == null)
                strBuilder.appendLine("${point.name},-")
            else {
                strBuilder.appendLine("${point.name},${infoAboutPassing.time}")
                copyOfPassingList.remove(infoAboutPassing) //убираем информацию о первом прохождении
            }
        }
        return strBuilder.toString()
    }

    fun toProtocolRow(): String =
        "$number,$surname,$name,$birthYear,$level"

}
/*
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
    var number: Int? = null,
    var startInfo: StartInfo? = null,
    val resultInfo: ResultInfo = ResultInfo(group.distance),
){

    val distance: Distance = group.distance
    //Составляет протокол прохождения дистанции
    fun getResultProtocol(): String{
        val res = StringBuilder("$number")
        if(resultInfo.isEmpty()){
            res.appendLine("There's no information!")
            return res.toString()
        }
        distance.controlPoints.forEach { point ->
            val time = resultInfo.time[point]
            res.appendLine("${point.name}, ${time?:"wasn't passed"}")
        }
        return res.toString()
    }

    fun toProtocolRow(): List<String> {
        val sp = sportsman
        return listOf(
            number.toString(), sp.surname, sp.name, sp.birthYear.toString(), sp.level
        )
    }

    /*
    Результаты забега: список отметок времени,
    когда спортсмен пересекал контрольные точки.
    */
    class ResultInfo(val distance: Distance) {
        val time: MutableMap<ControlPoint,Time?> = mutableMapOf()
        fun isEmpty(): Boolean = time.isEmpty()
        //val totalTime: Time get() = time[distance.finish]. - time[dstance.start]
    }
}

/*
    Информация о старте спортсмена - время старта
*/
data class StartInfo(var time: Time)
*/
