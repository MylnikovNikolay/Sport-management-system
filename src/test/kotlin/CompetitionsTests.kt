import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.CompetitionsByCSV.Companion.fromString
import java.io.File
import kotlin.test.*

class TestCompetitions(comp: CompetitionsByCSV): CompetitionsByCSV(comp.name, comp.date) {
    public override val distances
        get() = super.distances
    public override val groups
        get() = super.groups
    public override val controlPoints
        get() = super.controlPoints
    public override val sportsmen
        get() = super.sportsmen
    public override val teams
        get() = super.teams

    companion object {fun  fromString(string: String) = TestCompetitions(CompetitionsByCSV.fromString(string))}
}


internal class CompetitionsTests {
    @Test
    fun fromStringTest() {
        var comp = CompetitionsByCSV.fromString("Название,Дата\n" + "Первенство пятой бани,01.01.2022")
        assertEquals(comp.name, "Первенство пятой бани")
        assertEquals(comp.date, "01.01.2022")

        comp = CompetitionsByCSV.fromString("Дата,Название\n" + "04.01.2021,Первенство четвёртой бани")
        assertEquals(comp.name, "Первенство четвёртой бани")
        assertEquals(comp.date, "04.01.2021")

        try {
            comp = CompetitionsByCSV.fromString("abracadabra,abacaba")
            assert(false) // Выглядит не очень, сделать потом адекватно если это возможно!!
        } catch (_: IllegalArgumentException) { }

        try {
            comp = CompetitionsByCSV.fromString("Дота,название\n" + "Первенство пятой бани,01.01.2022")
            assert(false)
        } catch (_: IllegalArgumentException) { }
    }


    @Test
    fun takeDistancesAndCPsTest () {
        val comp = TestCompetitions.fromString("Название,Дата\n" + "Первенство пятой бани,01.01.2022")
        comp.takeDistancesAndCPs(File("sample-data/additional sample-data/takeDistancesAndCPsTest.csv").readText())
    }

}