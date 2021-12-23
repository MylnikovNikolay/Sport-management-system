import ru.emkn.kotlin.sms.Competitions
import ru.emkn.kotlin.sms.Csv
import ru.emkn.kotlin.sms.writeToFile
import java.io.File
import kotlin.random.Random

//это наследник для тестов

class TestCompetitions(comp: Competitions): Competitions(comp.name, comp.date) {
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

    override fun makeADrawAndWrite(folder: String) {
        makeADraw()
        groups.forEach {
            val filepath = folder + "startProtocol%s.csv"
            writeToFile(filepath.format(it.name), Csv.makeStartsProtocol(it))
        }
    }

    override fun writeSimpleResultsByGroups(folder: String) {
        groups.forEach {
            val filepath = folder + "resultProtocol%s.csv"
            writeToFile(filepath.format(it.name), Csv.makeResultsProtocolSimple(it))
        }
    }

    companion object {
        fun fromString(string: String) = TestCompetitions(Csv.createCompetitions(string))
        fun testGeneration() {
            val file1 = File("test-data/sample-data/additional sample-data/StartProtocolTest/courses.csv")
            file1.writeText("")
            file1.appendText("Название")
            file1.appendText(",".repeat(10))
            file1.appendText("\n")
            repeat(10) {
                val strbld = StringBuilder()
                strbld.append("distance_$it")
                repeat(10) {
                    val a = kotlin.math.abs(Random.nextInt()) % 31
                    if (a == 0)
                        strbld.append(",")
                    else
                        strbld.append(",cp$a")
                }
                strbld.append("\n")
                file1.appendText(strbld.toString())
            }
            val file = File("test-data/sample-data/additional sample-data/StartProtocolTest/classes.csv")
            file.writeText("")
            file.appendText("Название,Дистанция")
            file.appendText("\n")
            repeat(15) {
                val a = kotlin.math.abs(Random.nextInt()) % 10
                file.appendText("group_$it,distance_$a\n")
                if (!File("test-data/sample-data/additional sample-data/StartProtocolTest/protocols/group_$it.csv")
                        .exists())
                    File("test-data/sample-data/additional sample-data/StartProtocolTest/protocols/group_$it.csv")
                        .createNewFile()
                File("test-data/sample-data/additional sample-data/StartProtocolTest/protocols/group_$it.csv").
                writeText("group_$it,,,,,\nНомер,Фамилия,Имя,Г.р.,Разр.,Время старта\n")

                if (!File("test-data/sample-data/additional sample-data/StartProtocolTest/result_protocols/group_$it.csv")
                        .exists())
                    File("test-data/sample-data/additional sample-data/StartProtocolTest/result_protocols/group_$it.csv")
                        .createNewFile()
                File("test-data/sample-data/additional sample-data/StartProtocolTest/result_protocols/group_$it.csv").
                writeText("group_$it,,,,,,\nМесто,Номер,Фамилия,Имя,Г.р.,Разряд,Результат\n")
            }

            val file2 = File("test-data/sample-data/additional sample-data/StartProtocolTest/application.csv")
            file2.writeText("")
            file2.appendText("team,,,,\n")
            file2.appendText("Группа,Фамилия,Имя,Г.р.,Разр.\n")
            repeat(120) {
                file2.appendText(
                    "group_${kotlin.math.abs(Random.nextInt()) % 15},surname_$it,name_$it,${Random.nextInt()},\n"
                )
            }
        }
    }
}