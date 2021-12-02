package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File
import java.time.LocalTime

typealias Time = LocalTime//java.sql.Time

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


/*
Перевод строки во время - чтобы не писать каждый раз try-catch
 */
fun stringToTimeOrNull(str: String): Time?{
    try {
        val time = Time.parse(str)
        return time
    }finally{
        return null
    }
}


