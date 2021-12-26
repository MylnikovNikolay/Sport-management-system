package ru.emkn.kotlin.sms

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class MSportsman(
    val mutName: MutableState<String>,
    val mutSurname: MutableState<String>,
    val mutBirthYear: MutableState<Int>,
    val mutLevel: MutableState<String>,
    ): Sportsman(
    mutName.value,
    mutName.value,
    mutBirthYear.value,
    mutLevel.value
    ){
        val mutGender = mutableStateOf(Gender.UNKNOWN)
        override val gender
            get() = mutGender.value
        override val name
            get() = mutName.value
        override val surname
            get() = mutSurname.value
        override val birthYear: Int
            get() = mutBirthYear.value
        override val level
            get() = mutLevel.value
        val mutMedExamination = mutableStateOf("")
        override val medExamination: String
            get() = mutMedExamination.value
        val mutInsurance = mutableStateOf("")
        override val insurance: String
            get() = mutInsurance.value
}