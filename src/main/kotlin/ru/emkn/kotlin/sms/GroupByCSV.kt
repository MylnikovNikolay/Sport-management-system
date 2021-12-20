package ru.emkn.kotlin.sms


/*
Группа: определяется дистанцией и участниками, хранит
всю информацию о соревновании
 */
class GroupByCSV(name: String, distance: Distance, competition: Competitions): Group(name, distance, competition) {



    override fun takeStartProtocol(protocol: String) = csvProtocolManager.takeStartsProtocol(protocol,this)

    override fun getStartsProtocol(): String=csvProtocolManager.getStartsProtocol(this)

    override fun takeResultsProtocol(protocol: String) = csvProtocolManager.takeResultsProtocol(protocol, this)

    override fun getResultsProtocol(): String = csvProtocolManager.getResultsProtocol(this)
}
