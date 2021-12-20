package ru.emkn.kotlin.sms



/*
Менеджеры существуют для рутинной работы с бумажками
 */
interface ProtocolManager {
    fun fromString(protocol: String): Competitions

    //Складывает результаты всех групп в единый протокол (results.csv)
    fun getTotalResults(comp: Competitions): String

    //Обработка заявления от команды (applications)
    fun takeTeamApplication(protocol: String, comp: Competitions)

    //Создание дистанций и КП из конфигурационного протокола (courses.csv)
    fun takeDistancesAndCPs(protocol: String, comp: Competitions)

    //Создание групп из конфигурационного протокола (classes.csv)
    fun takeGroupsAndDistances(protocol: String, comp: Competitions)

    //Заполнение всех результатов из конфигурационного протокола (splits.csv)
    fun takeResults(protocol: String, comp: Competitions)

    //Заполнение всех результатов из конфигурационного протокола (splits.csv)
    fun takeResultsFromSplits(protocol: String, comp: Competitions)

    fun takeResultsFromReverseSplits(protocol: String, comp: Competitions)

    fun takeStartProtocol(protocol: String, comp: Competitions)



    //Запись стартов из стартового протокола (README.md)
    fun takeStartsProtocol(protocol: String, group: Group)

    //Генерация стартового протокола (README.md)
    fun getStartsProtocol(group: Group): String

    //Генерация протокола результатов (README.md)
    fun getResultsProtocol(group: Group): String

    fun takeResultsProtocol(protocol: String, group: Group)

    //Протокол прохождения КП (README.md)
    fun getCPPassingProtocol(group: ControlPoint): String

    //Протокол прохождения дистанции (README.md)
    fun getDistancePassingProtocol(group: CompetitionsSportsman): String
}

