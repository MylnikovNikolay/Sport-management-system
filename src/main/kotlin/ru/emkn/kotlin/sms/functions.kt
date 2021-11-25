package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

fun readCSV(pathname: String): String {
    assert(File(pathname).exists() && File(pathname).extension == "csv") {
        "Файла не существует либо у него не csv-расширение"
    }
    return File(pathname).readText()
}


fun writeToFile(filepath: String, str: String){
    val file = File(filepath)
    if (!file.exists()) {
        file.createNewFile()
    }
    TODO()
}