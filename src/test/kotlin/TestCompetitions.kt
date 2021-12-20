import ru.emkn.kotlin.sms.Competitions
import ru.emkn.kotlin.sms.Csv
import ru.emkn.kotlin.sms.writeToFile

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
            writeToFile(filepath.format(it.name), Csv.getStartProtocol(it))
        }
    }

    companion object {fun  fromString(string: String) = TestCompetitions(Csv.fromString(string))}
}