package ru.emkn.kotlin.sms


class ControlPointByCSV(name: String): ControlPoint(name) {

    //Выдает протокол прохождения КП (как в README.MD)
    override fun getProtocol(): String {
        val strBuilder = StringBuilder(name).append(",")
        passingList.forEach { passingCP ->
            strBuilder.appendLine(passingCP.sportsman.number)
            strBuilder.append(",")
            strBuilder.append(passingCP.time)
        }
        return strBuilder.toString()
    }
}
