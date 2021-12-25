package ru.emkn.kotlin.sms.gui

fun getNewName(name: String, id: Int) = if (id == 0) name else "$name($id)"