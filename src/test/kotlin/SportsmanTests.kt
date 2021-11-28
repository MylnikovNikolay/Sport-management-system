import ru.emkn.kotlin.sms.*
import java.io.File
import kotlin.test.*

internal class SportsmanTests {
    @Test
    fun testReadTeamFromCSV() {
        val filepath = "sample-data/applications/application1.csv"
        val file = File(filepath)
        val team = Team(file)
        assertEquals("ПСКОВ,РУСЬ", team.name)
        assertEquals(35, team.sportsmen.size)

        //assertEquals("VIP", team.sportsmen[0].group)
        assertEquals("Никитин", team.sportsmen[0].surname)
        assertEquals("Валентин", team.sportsmen[0].name)
        assertEquals(1941, team.sportsmen[0].birthYear)
        assertEquals("", team.sportsmen[0].level)

        //assertEquals("М10", team.sportsmen[34].group)
        assertEquals("Тихомиров", team.sportsmen[34].surname)
        assertEquals("Евгений", team.sportsmen[34].name)
        assertEquals(2015, team.sportsmen[34].birthYear)
        assertEquals("", team.sportsmen[34].level)
    }
}