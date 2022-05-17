package ru.emkn.kotlin.sms

/*
Событие: спортсмен пересек КП в момент времени time.
Это нужно для простого хранения всей информации.
Спортсмены и КП автоматически получают ссылки на эти объекты, и не нужно заполнять все отдельно
 */
data class PassingCP(val sportsman: CompSportsman, val CP: ControlPoint, val time: Time): Comparable<PassingCP>{
    init {
        UsualLogger.log(
            "Информация о событии: спортсмен ${sportsman.name} ${sportsman.surname} прошел КП '${CP.name}'" +
                    " в $time"
        )
        sportsman.addPassingCP(this)
        CP.addPassingCP(this)
    }
    fun destroy(){
        sportsman.removePassingCP(this)
        CP.removePassingCP(this)
    }
    override fun compareTo(other: PassingCP): Int = time.compareTo(other.time)
}
