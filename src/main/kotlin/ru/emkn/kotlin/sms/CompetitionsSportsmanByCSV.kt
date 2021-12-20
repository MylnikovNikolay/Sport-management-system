package ru.emkn.kotlin.sms

import java.lang.Double.max




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

    override fun getDistancePassingProtocol(): String = csvProtocolManager.getDistancePassingProtocol(this)

    fun toProtocolRow(): String =
        "$number,$surname,$name,$birthYear,$level"

}
