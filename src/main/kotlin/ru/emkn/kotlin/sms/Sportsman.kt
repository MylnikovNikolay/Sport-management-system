package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.util.*

enum class Gender{ MALE, FEMALE, UNKNOWN}



data class Sportsman(
    //val group: String,
    val name: String,
    val surname: String,
    val birthYear: Int,
    val level: String,          //спортивный разряд
    val gender: Gender = Gender.UNKNOWN,  //пол
    val medExamination: String="", //данные про медосмотр
    val insurance: String="",      //страхование
) {
    constructor(sp: Sportsman): this(sp.name, sp.surname,
        sp.birthYear, sp.level, sp.gender, sp.medExamination, sp.insurance)
    constructor(row: Map<String, String>): this(getSportsmanByRow(row))

    companion object {
        fun getSportsmanByRow(row: Map<String, String>): Sportsman {
            val name = row["Имя"]?.lowercase(Locale.getDefault())
                ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                ?: throw ImportantValueIsMissing("Имя")
            val surname = row["Фамилия"] ?.lowercase(Locale.getDefault())
                ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                ?: throw ImportantValueIsMissing("Фамилия")
            //val group = row["Группа"] ?: throw ImportantValueIsMissing("Группа")
            val birthYear = row["Г.р."] ?: throw ImportantValueIsMissing("Г.р.")
            val level = row["Разр."] ?: throw ImportantValueIsMissing("Разр.")
            val gender = toGender(row["Пол"] ?: "")
            val medExamination = row["Медосмотр"] ?: ""
            val insurance = row["Страховка"] ?: ""

            return Sportsman(name, surname, birthYear.toInt(), level, gender, medExamination, insurance)
        }

        private fun toGender(str: String): Gender {
            return when(str) {
                "муж" -> Gender.MALE
                "муж." -> Gender.MALE
                "мужской" -> Gender.MALE
                "жен" -> Gender.FEMALE
                "жен." -> Gender.FEMALE
                "женский" -> Gender.FEMALE
                "м" -> Gender.MALE
                "ж" -> Gender.FEMALE
                "м." -> Gender.MALE
                "ж." -> Gender.FEMALE
                else -> Gender.UNKNOWN
            }
        }
        fun getFromProtocolRow(row: List<String>): Sportsman{
            return Sportsman(
                surname = row[0],
                name = row[1],
                birthYear =  row[2].toInt(),
                level = row[3]
            )
        }
    }
}



class Team(
    val name: String,
    val sportsmen: List<Sportsman>,
) {
    constructor(team: Team): this(team.name, team.sportsmen)
    constructor(file: File): this(readTeamFromCSV(file))

    companion object {
        private fun readTeamFromCSV(file: File): Team {
            // Ожидается формат как в sample-data:
            // первая строка это название команды, вторая это header, а дальше идут описания участников
            val rows: List< List<String> > = csvReader().readAll(file)
            require(rows.size >= 3) { "В команде нет участников или неверный формат ввода" }

            val name = rows[0][0]
            val header = rows[1]
            val sportsmen = rows.drop(2).map {row ->
                Sportsman((header zip row).toMap())
            }
            return Team(name, sportsmen)
        }
    }
}

