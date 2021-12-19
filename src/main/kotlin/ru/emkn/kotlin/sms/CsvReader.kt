package ru.emkn.kotlin.sms

object CsvReader {
    fun read(protocol: String): List<List<String>>? {
        if (!checkProtocolIsCorrectCSV(protocol)){
            return null
        }
        val res = mutableListOf<List<String>>()
        protocol.lines().forEach{
            res.add(readOneLine(it)!!)
        }
        return res
    }
    fun readWithHeader(protocol: String): List<Map<String, String>>?{
        if (!checkProtocolIsCorrectCSV(protocol) || protocol.lines().isEmpty()){
            return null
        }
        val headers = readOneLine(protocol.lines()[0])!!.mapIndexed { index, s ->  index to s}.toMap()
        val listOfMaps = mutableListOf<Map<String, String>>()
        protocol.lines().drop(1).forEach {
            val map = mutableMapOf<String, String>()
            readOneLine(it)!!.forEachIndexed { index, it ->
                map[headers[index]!!] = it
            }
            listOfMaps.add(map)
        }
        return listOfMaps
    }
    fun checkProtocolIsCorrectCSV(protocol: String): Boolean {
        val list = protocol.lines()
        if (list.isNotEmpty()) {
            val neededSize = readOneLine(list[0])?.size?:return false
            list.forEach{
                if (readOneLine(it) == null || readOneLine(it)!!.size != neededSize) {
                    return false
                }
            }
        }
        return true
    }
    fun readOneLine(line: String): List<String>? {
        assert(line.lines().size == 1)
        if (line.count {it == '"'} % 2 == 1) {
            return null
        }
        val newLine = line.dropLastWhile { it.toString().matches("""\s""".toRegex()) }
            .dropWhile { it.toString().matches("""\s""".toRegex()) }
        val result = mutableListOf<String>()
        var between: Boolean = false //между кавычками или нет
        var afterQuotes: Boolean = false //если находимся между закрывающими кавычками и запятой
        var actualLine = StringBuilder()
        for (c in newLine) {
            if (between) {
                if (c!= '"')
                    actualLine.append(c)
                else {
                    result.add(actualLine.toString())
                    actualLine = StringBuilder()
                    between = false
                    afterQuotes = true
                }
            }
            else if (afterQuotes) {
                if (c.toString().matches("""\s""".toRegex())) {
                    continue
                }
                else if (c == ','){
                    afterQuotes = false
                }
                else {
                    return null
                }
            }
            else if (!afterQuotes && !between) {
                if (c == '"'){
                    if(actualLine.isNotEmpty())
                        return null
                    else {
                        between = true
                    }
                }
                else if (c.toString().matches("""\s""".toRegex())) {
                    if (actualLine.isEmpty())
                        continue
                    else
                        actualLine.append(c)
                }
                else if (c == ','){
                    result.add(actualLine.toString())
                    actualLine = StringBuilder()
                    between = false
                }
                else {
                    actualLine.append(c)
                }
            }
        }
        if (newLine.isEmpty() || newLine[newLine.lastIndex] != '"'){
            result.add(actualLine.toString())
        }
        return result.map {
            it.dropLastWhile { it.toString().matches("""\s""".toRegex()) }
                .dropWhile { it.toString().matches("""\s""".toRegex()) }.split("""(\s)+""".toRegex()).joinToString(" ")
        }.map {
            if (it.count{char -> char == ','} > 0)
                "\"$it\""
            else
                it
        }
    }
}