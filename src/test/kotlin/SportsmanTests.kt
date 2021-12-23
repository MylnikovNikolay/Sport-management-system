import TestCompetitions.Companion.fromString
import ru.emkn.kotlin.sms.*
import kotlin.test.*

internal class SportsmanTests {
    @Test
    fun testReadTeamFromCSV() {
        UsualLogger.start()
        ErrorsAndWarningsLogger.start()
        val filepath = "./test-data/sample-data/applications/application1.csv"
        val comp = fromString("Название,Дата\n" + "test,test")
        CsvProtocolManager.createDistancesAndCPs(readCSV("./test-data/sample-data/courses.csv"), comp)
        Csv.createGroupsAndDistances(readCSV("./test-data/sample-data/classes.csv"), comp)
        CsvProtocolManager.processTeamApplication(readCSV(filepath), comp)
        val team = comp.teams.first()

        assertEquals("\"ПСКОВ,РУСЬ\"", team.name)
        assertEquals(34, team.sportsmen.size)

        assertNotNull(team.sportsmen.find {
            it.surname == "НИКИТИН" && it.name == "ВАЛЕНТИН" && it.birthYear == 1941 && it.level == ""
        })
        assertNotNull(team.sportsmen.find {
            it.surname == "ТИХОМИРОВ" && it.name == "ЕВГЕНИЙ" && it.birthYear == 2015 && it.level == ""
        })
        assertNull(team.sportsmen.find {
            it.surname == "ТИХОМИРОВА" && it.name == "ЕВГЕНИЯ" && it.birthYear == 2015 && it.level == ""
        })
        assertNull(team.sportsmen.find {
            it.surname == "ТИХОМИРОВ" && it.name == "ЕВГЕНИЙ" && it.birthYear == 2014 && it.level == ""
        })
        assertNull(team.sportsmen.find {
            it.surname == "ТИХОМИРОВ" && it.name == "ЕВГЕНИЙ" && it.birthYear == 2015 && it.level == "a"
        })
        assertNull(team.sportsmen.find {
            it.surname == "ТИХОМИРОВ" && it.name == "ЕВГЕНИЙ" && it.birthYear == 2015 && it.level == "" &&
                    it.gender == Gender.MALE
        })
    }

}