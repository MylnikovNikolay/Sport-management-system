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
    fun makeADrawAndWrite(folder: String = "./data/start protocols/"){
        makeADraw()
        groups.forEach {
            val filepath = folder + "startProtocol%s.csv"
            writeToFile(filepath.format(it.name), it.getStartsProtocol())
        }
    }

    fun writeTotalResults(folder: String = "./data/results") {
        writeToFile("$folder/results.csv", CsvProtocolManager.getTotalResults(this))
    }

    fun writeTeamResults(folder: String = "./data/results") {
        writeToFile("$folder/teamResults.csv", Csv.getTeamResults(this))
    }
}
