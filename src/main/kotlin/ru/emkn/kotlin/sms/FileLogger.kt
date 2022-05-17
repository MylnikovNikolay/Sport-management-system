package ru.emkn.kotlin.sms

import java.io.File

abstract class FileLogger (private val pathnameToFile: String) {
    private fun clear() {
        assert(File(pathnameToFile).exists())
        File(pathnameToFile).writeText("")
    }
    fun log(string: String) {
        assert(File(pathnameToFile).exists())
        File(pathnameToFile).appendText(string + "\n")
    }
    fun start() {
        clear()
        log("Сессия начата")
    }
}
