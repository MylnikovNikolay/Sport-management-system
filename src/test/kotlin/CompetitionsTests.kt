import ru.emkn.kotlin.sms.*
import kotlin.test.*

internal class CompetitionsTests {
    @Test
    fun fromStringTest() {
        var comp = Competitions.fromString("Название,Дата\n" + "Первенство пятой бани,01.01.2022")
        assertEquals(comp.name, "Первенство пятой бани")
        assertEquals(comp.date, "01.01.2022")

        comp = Competitions.fromString("Дата,Название\n" + "04.01.2021,Первенство четвёртой бани")
        assertEquals(comp.name, "Первенство четвёртой бани")
        assertEquals(comp.date, "04.01.2021")

        try {
            comp = Competitions.fromString("abracadabra,abacaba")
            assert(false) // Выглядит не очень, сделать потом адекватно если это возможно!!
        } catch (_: IllegalArgumentException) { }

        try {
            comp = Competitions.fromString("Дота,название\n" + "Первенство пятой бани,01.01.2022")
            assert(false)
        } catch (_: IllegalArgumentException) { }
    }

}