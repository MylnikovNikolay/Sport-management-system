package ru.emkn.kotlin.sms

class Distance(
    val name: String,
    val controlPoints: List<ControlPoint>,
    val modeOfDistance: ModeOfDistance = ModeOfDistance.Strict,
    val numberOfCPtoPass: Int = controlPoints.size
){
    init {
        UsualLogger.log(
            "Добавлена дистанция '$name'"
        )
        controlPoints.forEach {
            UsualLogger.log(
                "К дистанции '$name' привязан КП '${it.name}'"
            )
        }
    }
    fun findCPByName(name: String) = controlPoints.find {it.name == name}

    override fun toString() = this.name
}