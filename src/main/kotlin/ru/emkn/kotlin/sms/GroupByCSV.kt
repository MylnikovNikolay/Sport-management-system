package ru.emkn.kotlin.sms


/*
Группа: определяется дистанцией и участниками, хранит
всю информацию о соревновании
 */
class GroupByCSV(name: String, distance: Distance, competition: Competitions): Group(name, distance, competition) {



    override fun takeStartProtocol(protocol: String) = СsvProtocolManager.fillStarts(protocol,this)

    override fun getStartsProtocol(): String=СsvProtocolManager.makeStartsProtocol(this)

    override fun takeResultsProtocol(protocol: String) = СsvProtocolManager.fillResults(protocol, this)

    override fun getResultsProtocol(): String = СsvProtocolManager.makeResultsProtocol(this)
}
