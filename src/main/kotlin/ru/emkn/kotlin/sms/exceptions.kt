package ru.emkn.kotlin.sms

class ImportantValueIsMissing(key: String):
    Exception("Важное значение '$key' отсутствует или имеет неверный формат")