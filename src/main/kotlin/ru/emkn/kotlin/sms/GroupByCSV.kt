package ru.emkn.kotlin.sms


/*
Группа: определяется дистанцией и участниками, хранит
всю информацию о соревновании
 */
class GroupByCSV(name: String, distance: Distance, competition: Competitions): Group(name, distance, competition) {



    override fun takeStartProtocol(protocol: String) = CsvProtocolManager.takeStartsProtocol(protocol,this)

    override fun getStartsProtocol(): String=CsvProtocolManager.getStartsProtocol(this)

    override fun takeResultsProtocol(protocol: String) = CsvProtocolManager.takeResultsProtocol(protocol, this)

    override fun getResultsProtocol(): String = CsvProtocolManager.getResultsProtocol(this)
}
