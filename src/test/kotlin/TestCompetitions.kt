import ru.emkn.kotlin.sms.CompetitionsByCSV

//это наследник для тестов

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