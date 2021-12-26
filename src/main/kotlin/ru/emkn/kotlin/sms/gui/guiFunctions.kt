package ru.emkn.kotlin.sms.gui

import ru.emkn.kotlin.sms.CompetitionsSportsman

fun getNewName(name: String, id: Int) = if (id == 0) name else "$name($id)"

fun formatToRow(list: List<Any>): String {
    return list.joinToString(" ") { it.toString().padEnd(15, ' ') }
}

fun formatToRow(sp: CompetitionsSportsman): String {
    return formatToRow(listOf(sp.surname, sp.name, sp.birthYear, sp.level))
}