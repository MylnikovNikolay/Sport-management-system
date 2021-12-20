package ru.emkn.kotlin.sms
import java.io.File


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
    Концепция такова - всю некорректную информацию пропускаем
     */

    fun takeAllApplicationsFromFolder(path: String) {
        File(path).walk().drop(1).forEach {
            CsvProtocolManager.takeTeamApplication(readCSV(it.path), this)
        }
    }
    /*
    Функции, связанные с выводом
     */
    override fun makeADrawAndWrite(folder: String){
        makeADraw()
        groups.forEach {
            val filepath = folder + "startProtocol%s.csv"
            writeToFile(filepath.format(it.name), Csv.getStartProtocol(it))
        }
    }

    fun writeTotalResults(folder: String = "./data/results") {
        writeToFile("$folder/results.csv", CsvProtocolManager.getTotalResults(this))
    }

    fun writeTeamResults(folder: String = "./data/results") {
        writeToFile("$folder/teamResults.csv", Csv.getTeamResults(this))
    }
}
