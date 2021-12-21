import ru.emkn.kotlin.sms.*
import java.io.File
import TestCompetitions.Companion.testGeneration


import kotlin.test.*

class ResultProtocolTests {
    /*
    работаем по той же схеме, что и в тестах со стартовыми протоколами
    сначала подготовка - генерация
    потом тесты на проверку считывания результатов
    потом проверка протоколов, составленных самой программой
     */

    private fun makeADrawAndSoAndSo(): TestCompetitions {
        val folder = "test-data/sample-data/additional sample-data/StartProtocolTest/"
        val comp = TestCompetitions.fromString(readCSV(folder + "event.csv"))
        CsvProtocolManager.takeDistancesAndCPs(readCSV(folder + "courses.csv"), comp)
        Csv.takeGroupsAndDistances(readCSV(folder + "classes.csv"), comp)
        CsvProtocolManager.takeTeamApplication(readCSV(folder + "application.csv"), comp)
        comp.makeADrawAndWrite(folder + "my protocols/")
        return comp
    }


    @BeforeTest
    fun resultProtocolTestGeneration() {
        testGeneration()
    }

    @Test
    fun test1part1() {
        val comp = makeADrawAndSoAndSo()
        val folder = "test-data/sample-data/additional sample-data/StartProtocolTest/"
        var time = "01:00:00".toTimeOrNull()!!
        comp.sportsmen.shuffled().forEach {
            val file = File(folder + "result_protocols/${it.group.name}.csv")
            time = time.plusSeconds(60)
            file.appendText(
                "${file.readLines().size - 1},${it.number},${it.surname},${it.name},${it.birthYear},${it.level}," +
                        "${time}\n"
            )
        }
        comp.groups.forEach{
            Csv.takeResults(readCSV(folder + "result_protocols/${it.name}.csv"), comp)
        }
        comp.sportsmen.forEach{
            assertNotNull(it.totalTime)
            assertNotNull(it.totalTimeByResults)
        }
    }

    @Test
    fun test2part1() {
        val comp = makeADrawAndSoAndSo()
        val folder = "test-data/sample-data/additional sample-data/StartProtocolTest/"
        var time = "01:00:00".toTimeOrNull()!!
        comp.sportsmen.shuffled().forEach {
            val file = File(folder + "result_protocols/${it.group.name}.csv")
            time = time.plusSeconds(60)
            file.appendText(
                "${file.readLines().size - 1},${it.number},${it.surname},${it.name},${it.birthYear},${it.level}," +
                        "${if (it.number!! % 100 == 0) -1 else time}\n"
            )
        }
        comp.groups.forEach{
            Csv.takeResults(readCSV(folder + "result_protocols/${it.name}.csv"), comp)
        }
        assertEquals(105, comp.sportsmen.count {it.totalTime != null})
    }

    @Test
    fun test3part1() {
        val comp = makeADrawAndSoAndSo()
        val folder = "test-data/sample-data/additional sample-data/StartProtocolTest/"
        var time = "01:00:00".toTimeOrNull()!!
        comp.sportsmen.shuffled().forEach {
            val file = File(folder + "result_protocols/${it.group.name}.csv")
            time = time.plusSeconds(60)
            file.appendText(
                "${file.readLines().size - 1},${if(time.hour == 1) it.number else -1},${it.surname},${it.name}," +
                        "${it.birthYear},${it.level},${time}\n"
            )
        }
        comp.groups.forEach{
            Csv.takeResults(readCSV(folder + "result_protocols/${it.name}.csv"), comp)
        }
        assertEquals(59, comp.sportsmen.count {it.totalTime != null})
    }

    @Test
    fun test1part2() {
        val comp = makeADrawAndSoAndSo()
        val folder = "test-data/sample-data/additional sample-data/StartProtocolTest/"
        comp.sportsmen.forEach { sp ->
            sp.totalTimeByResults = "01:00:00".toTimeOrNull()!!.plusSeconds((sp.number!! % 100) * 60.toLong())
        }
        comp.writeSimpleResultsByGroups(folder + "my result_protocols/")
        comp.groups.forEach {

            val rows = CsvReader.readWithHeader(readCSV(folder + "my result_protocols/resultProtocol${it.name}.csv")
                .lines().drop(1).joinToString("\n"))!!
            for (row in rows) {
                assertEquals(row["Номер"]!!.last(), row["Результат"]!!.last())
                assertEquals(row["Номер"]!!.last(), (row["Место"]!!.toInt() - 1).toString().last())
            }

        }
    }

    @Test
    fun test2part2() {
        val comp = makeADrawAndSoAndSo()
        val folder = "test-data/sample-data/additional sample-data/StartProtocolTest/"
        comp.sportsmen.forEach { sp ->
            sp.totalTimeByResults =
                if (sp.number!! % 100 != 2)
                    "01:00:00".toTimeOrNull()!!.plusSeconds((sp.number!! % 100) * 60.toLong())
                else
                    null
        }
        comp.writeSimpleResultsByGroups(folder + "my result_protocols/")
        comp.groups.forEach {

            val rows = CsvReader.readWithHeader(readCSV(folder + "my result_protocols/resultProtocol${it.name}.csv")
                .lines().drop(1).joinToString("\n"))!!
            assert(it.sportsmen.size < 2 || (rows.last()["Место"] == "-") && (rows.last()["Результат"] == "-"))
        }
    }


    /*
    В третьей части проверка работоспособности составления протокола для команд
     */

    @Test
    fun test1part3(){
        val comp = makeADrawAndSoAndSo()
        comp.sportsmen.forEach { sp ->
            sp.totalTimeByResults = "00:01:00".toTimeOrNull()!!.plusSeconds((sp.number!! % 100) * 60.toLong())
        }
        assertEquals(comp.groups.count{it.sportsmen.size > 0}, comp.sportsmen.count{it.points > 0.0})
    }

    @Test
    fun test2part3() {
        val comp = makeADrawAndSoAndSo()
        comp.sportsmen.forEach { sp ->
            sp.totalTimeByResults = "00:01:00".toTimeOrNull()!!.plusSeconds((sp.number!! % 100) * 60.toLong())
        }
        assertEquals(
            100 * comp.groups.count{it.sportsmen.size > 0} * 1.0,
            comp.teams.find{it.name == "team"}!!.teamPoints
        )
    }

    @Test
    fun test3part3() {
        UsualLogger.start()
        ErrorsAndWarningsLogger.start()
        val comp = makeADrawAndSoAndSo()
        Csv.takeTeamApplication(
            "TEAM,,,,\nГруппа,Фамилия,Имя,Г.р.,Разр.\ngroup_0,SURNAME,NAME,2000,",
                    comp
        )
        val gr = comp.findGroupByName("group_0")!!
        val sp = gr.findSportsmanByNameSurnameBirthYear("NAME", "SURNAME", 2000)!!
        sp.startTime = "12:00:00".toTimeOrNull()!!
        sp.number = 0
        comp.sportsmen.forEach { sportsman ->
            sportsman.totalTimeByResults = "00:01:00".toTimeOrNull()!!
                .plusSeconds((sportsman.number!! % 100) * 60.toLong())
        }
        sp.totalTimeByResults = "00:00:40".toTimeOrNull()!!
        assertEquals(
            100.0,
            sp.points
        )
        assertEquals(
            100.0,
            comp.teams.find{it.name == "TEAM"}!!.teamPoints
        )
        comp.groups.forEach {
            assert(it.bestTime >= Time.of(0, 0 ,0 ))
        }
        comp.sportsmen.forEach {
            assert(it.points <= 100.0)
            assert(it.points >= 0.0)
        }
        assertEquals("00:00:40".toTimeOrNull(), gr.bestTime)
        comp.groups.forEach {
            if(it != gr)
                assertEquals(it.bestTime, "00:01:00".toTimeOrNull())
        }
        assertEquals(
            comp.groups.count{it.name != "group_0" && it.sportsmen.isNotEmpty()} * 100.0
                    + if(gr.sportsmen.size >= 2) 50.0 else 0.0,
            comp.teams.find{it.name == "team"}!!.teamPoints
        )
        assertEquals(
            Csv.getTeamResults(comp).lines().size,
            3
        )
        UsualLogger.log(Csv.getTeamResults(comp))
        assertEquals(
            CsvReader.read(Csv.getTeamResults(comp))!![2][0],
            "TEAM"
        )
        assertEquals(
            CsvReader.read(Csv.getTeamResults(comp))!![2][1],
            "100.00"
        )
    }

    //TODO("Можно сделать часть четыре с проверкой обычных выводных результатов соревнования, а не simple")
}