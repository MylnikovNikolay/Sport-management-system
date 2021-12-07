package ru.emkn.kotlin.sms


class ControlPointByCSV(name: String): ControlPoint(name) {

    //Выдает протокол прохождения КП (как в README.MD)
    override fun getProtocol(): String {
        val strBuilder = StringBuilder(name).append(",")
        passingList.forEach { passingCP ->
            strBuilder.appendLine(passingCP.sportsman.number)
            strBuilder.append(",")
            strBuilder.append(passingCP.time)
        }
        return strBuilder.toString()
    }
}
/*
    //Регистрирует номер прошедшего этот пункт участника и время
    val info: MutableMap<Int,Time?> = mutableMapOf()

    //Конструктор строит сразу по протоколу
    constructor(inputDistance: Distance, inputProtocol: String):
            this(nameFromProtocol(inputProtocol), inputDistance){
        dataFromProtocol(inputProtocol)
    }

    //По файлу с протоколом
    constructor(pathname: String, inputDistance: Distance, fromFile: Boolean = true) :
            this(inputDistance, inputProtocol = readCSV(pathname))

    companion object {

        //Вычленяет name из протокола
        fun nameFromProtocol(protocol: String): String {
            val rows: List<List<String>> = csvReader().readAll(protocol)
            assert(rows.isNotEmpty() && rows[0].isNotEmpty()) {
                "Неверный формат CSV-файла для протокола КП: отсутствует имя"
            }
            return rows[0][0]
        }
    }


    //Заполняет info из протокола
    fun dataFromProtocol(protocol: String){
        val rows: List<List<String>> = csvReader().readAll(protocol)
        assert(rows.isNotEmpty() && rows[0].isNotEmpty()) {
            "Неверный формат CSV-файла для протокола КП: отсутствует имя"
        }

        assert(rows[0][0] == name) {
            "Имя КП не соответствует КП"
        }

        rows.drop(1).forEach {
            val timeOrNull = stringToTimeOrNull(it[1])
            info[it[0].toInt()] = timeOrNull
        }
    }
 */