package ru.emkn.kotlin.sms

enum class Gender{ MALE, FEMALE, UNKNOWN}
data class Sportsman(
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
)