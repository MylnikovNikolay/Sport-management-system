import TestCompetitions.Companion.testGeneration
import ru.emkn.kotlin.sms.*
import java.io.File
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.*

class StartProtocolTests {
    /*
    Первая функция - рандомное заполнение соревнования на одну заявку от команды, 10 дистанций, 30 кп, 15 групп,
    120 участников
    В первой части проверяется, что корректно считывается поданный файл стартовых протоколов
    Во второй - что сама программа умеет их составлять
     */


    @BeforeTest
    fun startProtocolTestGeneration() {
        testGeneration()
    }

    @Test
    fun test1part1(){
        val folder = "test-data/sample-data/additional sample-data/StartProtocolTest/"
        val comp = TestCompetitions.fromString(readCSV(folder + "event.csv"))
        CsvProtocolManager.takeDistancesAndCPs(readCSV(folder + "courses.csv"), comp)
        Csv.takeGroupsAndDistances(readCSV(folder + "classes.csv"), comp)
        CsvProtocolManager.takeTeamApplication(readCSV(folder + "application.csv"), comp)
    }

    @Test
    fun test2part1(){
        UsualLogger.start()
        ErrorsAndWarningsLogger.start()
        val folder = "test-data/sample-data/additional sample-data/StartProtocolTest/"
        val comp = TestCompetitions.fromString(readCSV(folder + "event.csv"))
        CsvProtocolManager.takeDistancesAndCPs(readCSV(folder + "courses.csv"), comp)
        Csv.takeGroupsAndDistances(readCSV(folder + "classes.csv"), comp)
        CsvProtocolManager.takeTeamApplication(readCSV(folder + "application.csv"), comp)

        var number = 234
        comp.sportsmen.forEach { sp ->
            number += kotlin.math.abs(Random.nextInt()) % 1000 + 1
            File(folder + "protocols/${sp.group.name}.csv").appendText(
                "${number},${sp.surname},${sp.name},${sp.birthYear},,${Random.nextInt(12..23)}:" +
                        "${Random.nextInt(10..59)}:${Random.nextInt(10..59)}\n"
            )
        }
        comp.groups.forEach {
            Csv.takeStartProtocol(readCSV(folder + "protocols/${it.name}.csv"), comp)
        }
        comp.sportsmen.forEach {
            assertNotNull(it.number)
            assertNotNull(it.startTime)
        }

    }


    @Test
    fun test3part1() {
        UsualLogger.start()
        ErrorsAndWarningsLogger.start()
        val folder = "test-data/sample-data/additional sample-data/StartProtocolTest/"
        val comp = TestCompetitions.fromString(readCSV(folder + "event.csv"))
        CsvProtocolManager.takeDistancesAndCPs(readCSV(folder + "courses.csv"),comp)
        Csv.takeGroupsAndDistances(readCSV(folder + "classes.csv"), comp)
        CsvProtocolManager.takeTeamApplication(readCSV(folder + "application.csv"), comp)


        var number = 0
        comp.sportsmen.forEach { sp ->
            number +=  1
            number %= 44
            File(folder + "protocols/${sp.group.name}.csv").appendText(
                "${number},${sp.surname},${sp.name},${sp.birthYear},,${Random.nextInt(12..23)}:" +
                        "${Random.nextInt(10..59)}:${Random.nextInt(10..59)}\n"
            )
        }

        comp.groups.forEach {
            Csv.takeStartProtocol(readCSV(folder + "protocols/${it.name}.csv"), comp)
        }

        assertEquals(44, comp.sportsmen.filter{it.number != null}.size)
    }


    @Test
    fun test4part1(){
        UsualLogger.start()
        ErrorsAndWarningsLogger.start()
        val folder = "test-data/sample-data/additional sample-data/StartProtocolTest/"
        val comp = TestCompetitions.fromString(readCSV(folder + "event.csv"))
        CsvProtocolManager.takeDistancesAndCPs(readCSV(folder + "courses.csv"), comp)
        Csv.takeGroupsAndDistances(readCSV(folder + "classes.csv"), comp)
        CsvProtocolManager.takeTeamApplication(readCSV(folder + "application.csv"), comp)

        var number = 0
        comp.sportsmen.forEach { sp ->
            number += 1
            File(folder + "protocols/${sp.group.name}.csv").appendText(
                "${number},${sp.surname},${sp.name},${sp.birthYear},,${Random.nextInt(12..23)}:" +
                        "${if (number % 40 == 0) 60 else Random.nextInt(10..59)}:" +
                        "${Random.nextInt(10..59)}\n"
            )
        }
        comp.groups.forEach {
            Csv.takeStartProtocol(readCSV(folder + "protocols/${it.name}.csv"), comp)
        }

        assertEquals(117, comp.sportsmen.filter{it.number != null}.size)
    }

    @Test
    fun test5part1() {
        UsualLogger.start()
        ErrorsAndWarningsLogger.start()
        val folder = "test-data/sample-data/additional sample-data/StartProtocolTest/"
        val comp = TestCompetitions.fromString(readCSV(folder + "event.csv"))
        CsvProtocolManager.takeDistancesAndCPs(readCSV(folder + "courses.csv"), comp)
        Csv.takeGroupsAndDistances(readCSV(folder + "classes.csv"), comp)
        CsvProtocolManager.takeTeamApplication(readCSV(folder + "application.csv"), comp)

        var number = 0
        comp.sportsmen.forEach { sp ->
            number += 1
            File(folder + "protocols/${sp.group.name}.csv").appendText(
                "${number},${sp.surname}${if (number % 30 == 0) "no" else ""},${sp.name},${sp.birthYear},," +
                        "${Random.nextInt(12..23)}:" +
                        "${Random.nextInt(10..59)}:" +
                        "${Random.nextInt(10..59)}\n"
            )
        }
        comp.groups.forEach {
            Csv.takeStartProtocol(readCSV(folder + "protocols/${it.name}.csv"), comp)
        }

        assertEquals(116, comp.sportsmen.filter{it.number != null}.size)
    }

    @Test
    fun part2() {
        UsualLogger.start()
        ErrorsAndWarningsLogger.start()
        val folder = "test-data/sample-data/additional sample-data/StartProtocolTest/"
        val comp = TestCompetitions.fromString(readCSV(folder + "event.csv"))
        CsvProtocolManager.takeDistancesAndCPs(readCSV(folder + "courses.csv"), comp)
        Csv.takeGroupsAndDistances(readCSV(folder + "classes.csv"), comp)
        CsvProtocolManager.takeTeamApplication(readCSV(folder + "application.csv"), comp)

        comp.makeADrawAndWrite(folder + "my protocols/")

        val comp2 = TestCompetitions.fromString(readCSV(folder + "event.csv"))
        CsvProtocolManager.takeDistancesAndCPs(readCSV(folder + "courses.csv"), comp2)
        Csv.takeGroupsAndDistances(readCSV(folder + "classes.csv"), comp2)
        CsvProtocolManager.takeTeamApplication(readCSV(folder + "application.csv"), comp2)

        comp2.groups.forEach {
            Csv.takeStartProtocol(readCSV(folder + "my protocols/startProtocol${it.name}.csv"), comp2)
        }
        comp2.sportsmen.forEach {
            assertNotNull(it.number)
            assertNotNull(it.startTime)
            assert(it.startTime!! >= Time.of(12,0,0))
        }
    }

}
