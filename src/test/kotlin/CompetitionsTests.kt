import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.CompetitionsByCSV.Companion.fromString
import kotlin.test.*

internal class CompetitionsTests {
    @Test
    fun fromStringTest() {
        UsualLogger.start()
        ErrorsAndWarningsLogger.start()
        val comp = fromString("Название,Дата\n" + "Первенство пятой бани,01.01.2022")
        assertEquals(comp.name, "Первенство пятой бани")
        assertEquals(comp.date, "01.01.2022")

        assertFails {
            fromString("Дата,Название\n" + "04.01.2021,Первенство четвёртой бани")
        }

        assertFails {
            fromString("abracadabra,abacaba")
        }

        assertFails{
            fromString("Название,Дота\n" + "Первенство пятой бани,01.01.2022")
        }
    }


    @Test
    fun takeDistancesAndCPsTest () {
        UsualLogger.start()
        ErrorsAndWarningsLogger.start()
        val comp = TestCompetitions.fromString("Название,Дата\n" + "Первенство пятой бани,01.01.2022")
        CsvProtocolManager.createDistancesAndCPs(readCSV("test-data/sample-data/additional sample-data" +
                "/takeDistancesAndCPsTest.csv"), comp)
        assertEquals(3, comp.distances.size)

        val distance1 = comp.findDistanceByName("distance1")
        assertTrue(distance1 != null)
        assertEquals(4, distance1.controlPoints.size)

        val distance3 = comp.findDistanceByName("distance3")
        assertTrue(distance3 != null)
        assertEquals(2, distance3.controlPoints.size)

        val distance2 = comp.findDistanceByName("distance2")
        assertNotNull(distance2)

        assertEquals(distance2.modeOfDistance, ModeOfDistance.Lax)
        assertEquals(distance2.numberOfCPtoPass, 3)

        val distance4 = comp.findDistanceByName("distance4")
        assertTrue(distance4 == null)

        val distance5 = comp.findDistanceByName("distance5")
        assertNull(distance5)

        val cp100 = comp.findCPByName("100")
        assertTrue(cp100 != null)
        assertTrue(cp100 in distance1.controlPoints)
        assertTrue(cp100 in distance2.controlPoints)

        val cp = comp.findCPByName("")
        assertTrue(cp == null)

        val distance = comp.findDistanceByName("")
        assertTrue(distance == null)

        val cpStart = comp.findCPByName("start")
        assertTrue(cpStart == null)

        assertEquals(1, comp.distances.count {it.name == "distance2"})
        assertEquals(4, distance2.controlPoints.size)
        assertEquals(1, comp.controlPoints.count{it.name == "finish"})
    }

    @Test
    fun takeGroupsAndDistances() {
        UsualLogger.start()
        ErrorsAndWarningsLogger.start()
        val comp = TestCompetitions.fromString("Название,Дата\n" + "Первенство пятой бани,01.01.2022")
        CsvProtocolManager.createDistancesAndCPs(
            readCSV("test-data/sample-data/additional sample-data/takeGroupsAndDistancesTest(SG)/distances.csv"),
            comp
        )
        Csv.createGroupsAndDistances(
            readCSV("test-data/sample-data/additional sample-data/takeGroupsAndDistancesTest(SG)/groups-distances.csv"),
            comp
        )
        assertEquals(4, comp.groups.size)
        val group67 = comp.findGroupByName("М67")
        assertTrue(group67 != null)

        val distance1 = comp.findDistanceByName("distance1")
        assertEquals(distance1, group67.distance)

        val group25 = comp.findGroupByName("М25")
        assertTrue(group25 != null)
        assertEquals(distance1, group25.distance)

        val group = comp.findGroupByName("")
        assertTrue(group == null)

        val group34 = comp.findGroupByName("М34")
        assertTrue(group34 == null)

        val group29 = comp.findGroupByName("Ж29")
        assertTrue(group29 == null)

        assertEquals(Time.of(23,59,59), group25.bestTime)

        val group28 = comp.findGroupByName("Ж28")
        assertTrue(group28 != null)

        val distance2 = comp.findDistanceByName("distance2")
        assertEquals(distance2, group28.distance)

        assertEquals(1, comp.groups.count{it.name == "Ж28"})
    }
}