package ru.emkn.kotlin.sms
import java.io.File
import kotlin.text.StringBuilder


open class CompetitionsByCSV(
    name: String,
    date: String,
): Competitions(name, date) {

    override val distances
        get() = super.distances
    override val groups
        get() = super.groups
    override val controlPoints
        get() = super.controlPoints
    override val sportsmen
        get() = super.sportsmen
    override val teams
        get() = super.teams


    companion object{
        fun fromString(protocol: String): Competitions = СsvProtocolManager.createCompetitions(protocol)
    }


    /*
    Переопределения
     */
    override fun makeADraw() {
        giveNumbersToSportsmenByGroups()
        val time = Time.of(12,0,0)
        groups.forEach { it.makeADraw(time) }
    }

    //Все результаты складываются в единый протокол по типу results.csv
    override fun getTotalResults(): String = СsvProtocolManager.makeResultsProtocol(this)

    override fun getTeamResults(): String {
        val strBuilder = StringBuilder("Протокол результатов для команд,\n")
        teams.sortedBy{ -it.teamPoints }.forEach {
            strBuilder.appendLine("${it.name}," + String.format("%.2f", it.teamPoints))
        }
        return strBuilder.toString()
    }

    /*
    Концепция такова - всю некорректную информацию пропускаем
     */

    //Прием заявления от команды
    override fun takeTeamApplication(protocol: String) = СsvProtocolManager.processTeamApplication(protocol, this)

    fun takeAllApplicationsFromFolder(path: String) {
        File(path).walk().drop(1).forEach {
            takeTeamApplication(readCSV(it.path))
        }
    }

    //Создание дистанций и КП - как из courses.csv
    override fun takeDistancesAndCPs(protocol: String) = СsvProtocolManager.createDistancesAndCPs(protocol, this)

    //Создание групп по протоколу, как из файла classes.csv
    override fun takeGroupsAndDistances(protocol: String) = СsvProtocolManager.createGroupsAndDistances(protocol, this)

    //Заполнение всех результатов - как из splits.csv
    override fun takeResultsFromSplits(protocol: String) = СsvProtocolManager.fillResultsFromSplits(protocol, this)


    override fun takeResultsFromReverseSplits(protocol: String) = СsvProtocolManager.fillResultsFromReverseSplits(protocol,this)


    override fun takeResultsProtocol(protocol: String) = СsvProtocolManager.fillAllResults(protocol, this)


    override fun takeStartProtocol(protocol: String) = СsvProtocolManager.fillStarts(protocol, this)
    /*
    Функции, связанные с выводом
     */
    fun makeADrawAndWrite(folder: String = "./data/start protocols"){
        makeADraw()
        groups.forEach {
            val filepath = folder + "startProtocol%s.csv"
            writeToFile(filepath.format(it.name), it.getStartsProtocol())
        }
    }

    fun writeTotalResults(folder: String = "./data/results") {
        writeToFile("$folder/results.csv", getTotalResults())
    }

    fun writeTeamResults(folder: String = "./data/results") {
        writeToFile("$folder/teamResults.csv", getTeamResults())
    }
}
