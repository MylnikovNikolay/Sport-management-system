package ru.emkn.kotlin.sms


open class Sportsman(
    val name: String,
    val surname: String,
    val birthYear: Int,
    val level: String,                      //спортивный разряд
    val gender: Gender = Gender.UNKNOWN,    //пол
    val medExamination: String="",          //данные про медосмотр
    val insurance: String="",               //страхование
){
    constructor(sp: Sportsman): this(sp.name,sp.surname,sp.birthYear,sp.level,sp.gender,sp.medExamination,sp.insurance)
}

