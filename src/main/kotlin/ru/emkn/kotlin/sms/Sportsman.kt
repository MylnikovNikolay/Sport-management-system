package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File

enum class Gender{ MALE, FEMALE, UNKNOWN}
data class Sportsman(
    val group: String,
    val name: String,
    val surname: String,
    val birthYear: Int,
    val level: String,          //спортивный разряд
    val gender: Gender = Gender.UNKNOWN,  //пол
    val medExamination: String="", //данные про медосмотр
    val insurance: String="",      //страхование
)


class Team(
    val name: String,
    val sportsmen: List<Sportsman>,
) {
    constructor(team: Team): this(team.name, team.sportsmen)
    constructor(file: File): this(readTeamFromCSV(file))
}

private fun readTeamFromCSV(file: File): Team {
    // Ожидается формат как в sample-data:
    // первая строка это название команды, а дальше идут описания участников
    val rows: List< List<String> > = csvReader().readAll(file)
    require(rows.size >= 2) { "В команде нет участников или неверный формат ввода" }

    val name = rows[0][0]
    val sportsmen = rows.drop(2).map {row ->
        require(row.size >= 4) { "Не хватает информации об участнике команды $name" }
        check(row[3].toIntOrNull() != null) { "В столбце 'год рождения' должно быть число" }

        Sportsman(group=row[0], surname=row[1], name=row[2], birthYear=row[3].toInt(), level=row[4])
    }
    return Team(name, sportsmen)
}