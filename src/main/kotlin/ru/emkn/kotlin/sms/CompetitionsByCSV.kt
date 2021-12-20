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
        fun fromString(protocol: String): Competitions = CsvProtocolManager.fromString(protocol)
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
    override fun getTotalResults(): String = CsvProtocolManager.getTotalResults(this)

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
    override fun takeTeamApplication(protocol: String) = CsvProtocolManager.takeTeamApplication(protocol, this)

    fun takeAllApplicationsFromFolder(path: String) {
        File(path).walk().drop(1).forEach {
            takeTeamApplication(readCSV(it.path))
        }
    }

    //Создание дистанций и КП - как из courses.csv
    override fun takeDistancesAndCPs(protocol: String) = CsvProtocolManager.takeDistancesAndCPs(protocol, this)

    //Создание групп по протоколу, как из файла classes.csv
    override fun takeGroupsAndDistances(protocol: String) = CsvProtocolManager.takeGroupsAndDistances(protocol, this)

    //Заполнение всех результатов - как из splits.csv
    override fun takeResultsFromSplits(protocol: String) = CsvProtocolManager.takeResultsFromSplits(protocol, this)


    override fun takeResultsFromReverseSplits(protocol: String) = CsvProtocolManager.takeResultsFromReverseSplits(protocol,this)


    override fun takeResultsProtocol(protocol: String) = CsvProtocolManager.takeResults(protocol, this)


    override fun takeStartProtocol(protocol: String) = CsvProtocolManager.takeStartProtocol(protocol, this)
    /*
    Функции, связанные с выводом
     */
    fun makeADrawAndWrite(folder: String = "./data/start protocols/"){
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
