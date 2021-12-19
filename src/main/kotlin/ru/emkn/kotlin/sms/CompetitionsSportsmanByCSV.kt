package ru.emkn.kotlin.sms

import java.lang.Double.max


class CompetitionsTeamByCSV(name: String): CompetitionsTeam(name)

class CompetitionsSportsmanByCSV(
    sportsman: Sportsman,
    team: CompetitionsTeam,
    group: Group,
) : CompetitionsSportsman(sportsman,team,group){

    override val points: Double
        get() =
            if (totalTime == null)
                0.0
            else
                max(
                    0.0,
                    100 * (2 - totalTime!!.toSecondOfDay().toDouble() / group.bestTime.toSecondOfDay())
                )

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
