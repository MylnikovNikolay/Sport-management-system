package ru.emkn.kotlin.sms


open class Sportsman(
    open val name: String,
    open val surname: String,
    open val birthYear: Int,
    open val level: String,                      //спортивный разряд
    open val gender: Gender = Gender.UNKNOWN,    //пол
    open val medExamination: String="",          //данные про медосмотр
    open val insurance: String="",               //страхование
){
    constructor(sp: Sportsman): this(sp.name,sp.surname,sp.birthYear,sp.level,sp.gender,sp.medExamination,sp.insurance)
}

