package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

class ImportantValueIsMissing(key: String):
    Exception("Важное значение '$key' отсутствует или имеет неверный формат")



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


fun stringToTimeOrNull(str: String): Time?{
    val arr = str.split(":")
    if(arr.size!=3) return null
    val hour = arr[0].toIntOrNull()
    val minute = arr[0].toIntOrNull()
    val second = arr[0].toIntOrNull()
    if(hour==null || minute==null || second==null || hour >= 24 || minute >= 60 || second >= 60 || hour < 0 || minute < 0
        || second < 0) return null
    return Time(hour,minute,second)
}


