package ru.emkn.kotlin.sms

import java.util.*

class ControlPoint(val name: String){
    init {
        UsualLogger.log(
            "Добавлен КП '$name'"
        )
    }
    private val data: TreeSet<PassingCP> = TreeSet()

    val passingList: List<PassingCP>
        get() = data.toList()

    //Функции для заполнения data - информации о прохождении этой точки спортсменами
    fun addPassingCP(passingCP: PassingCP) = data.add(passingCP)
    fun removePassingCP(passingCP: PassingCP) = data.remove(passingCP)


    override fun toString() = this.name
}