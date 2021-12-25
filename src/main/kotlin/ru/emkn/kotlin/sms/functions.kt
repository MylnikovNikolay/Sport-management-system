package ru.emkn.kotlin.sms

import java.io.File
import java.time.LocalTime

typealias Time = LocalTime

//Вычитание времени. Если вычитается из меньшего большее, остается 00:00:00
operator fun Time.minus(other: Time): Time =
    Time.ofNanoOfDay(maxOf(this.toNanoOfDay()-other.toNanoOfDay(),0))

val defaultProtocolManager: ProtocolManager = CsvProtocolManager
typealias Csv = CsvProtocolManager



fun readCSV(pathname: String): String {
    if (!File(pathname).exists() && File(pathname).extension != "csv") {
        printError("Файла $pathname не существует либо у него не csv-расширение")
    }
    assert(File(pathname).exists() && File(pathname).extension == "csv") {
        "Файла $pathname не существует либо у него не csv-расширение"
    }
    return File(pathname).readLines().dropWhile { it.isBlank() }.dropLastWhile { it.isBlank() }.joinToString("\n")
}


fun writeToFile(filepath: String, str: String){
    val file = File(filepath)
    if (!file.exists()) {
        file.createNewFile()
    }
    file.writeText(str)
}

/*
Перевод строки во время - чтобы не писать каждый раз try-catch
 */
fun stringToTimeOrNull(str: String): Time?{
    return try {
        Time.parse(str)
    } catch (e : java.time.format.DateTimeParseException){
        null
    }
}

fun String.toTimeOrNull(): Time? {
    return try {
        Time.parse(this)
    } catch (e : java.time.format.DateTimeParseException){
        null
    }
}

fun printWarning(string: String) {
    ErrorsAndWarningsLogger.log("Warning: $string")
}

fun printError(string: String) {
    ErrorsAndWarningsLogger.log("Error: $string")
}

typealias CompSportsman = CompetitionsSportsman

fun <T> List<T>.MCSsize(other: List<T>): Int {

    //ищем НОП, точнее его размер, тут нельзя совсем неэффективно делать - так что решение за O(n * m)
    val size1 = this.size
    val size2 = other.size
    val listOfAnswersForPreviousSize = (0..size1).toList().map{0}.toMutableList()
    val listOfAnswersForCurrentSize = (0..size1).toList().map{0}.toMutableList()
    for (i in 1..size2) {
        for (j in 0..size1) {
            if (j == 0) {
                listOfAnswersForCurrentSize[j] = 0
            }
            else {
                listOfAnswersForCurrentSize[j] = kotlin.math.max(
                    listOfAnswersForPreviousSize[j-1] + if(this[j - 1] == other[i - 1]) 1 else 0,
                    kotlin.math.max(listOfAnswersForCurrentSize[j - 1], listOfAnswersForPreviousSize[j])
                )
            }
        }
        for (j in 0..size1)
            listOfAnswersForPreviousSize[j] = listOfAnswersForCurrentSize[j]
    }
    return listOfAnswersForPreviousSize[size1]
}
