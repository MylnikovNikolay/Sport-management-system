package ru.emkn.kotlin.sms






class DistanceByCSV(name: String, controlPoints: List<ControlPoint>): Distance(name, controlPoints) {
    //val start = ControlPoint("$name-Start",this)
    //val finish = ControlPoint("$name-Finish",this)

    fun findCPByName(name: String) = controlPoints.find {it.name == name}

}

