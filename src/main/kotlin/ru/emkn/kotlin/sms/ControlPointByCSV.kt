package ru.emkn.kotlin.sms


class ControlPointByCSV(name: String): ControlPoint(name) {

    //Выдает протокол прохождения КП (как в README.MD)
    override fun getProtocol(): String = CsvProtocolManager.getCPPassingProtocol(this)
}
