package ru.emkn.kotlin.sms



/*
Менеджеры существуют для рутинной работы с бумажками
 */
/*
make = 
fill = 
create = 
process = 
 */
interface ProtocolManager {
    fun makeResultsProtocolSimple(group: Group): String

    fun makeTeamResultsProtocol(comp: Competitions): String

    fun createCompetitions(protocol: String): Competitions

    //Складывает результаты всех групп в единый протокол (results.csv)
    fun makeGroupResultsProtocol(comp: Competitions): String

    //Обработка заявления от команды (applications)
    fun processTeamApplication(protocol: String, comp: Competitions)

    //Создание дистанций и КП из конфигурационного протокола (courses.csv)
    fun createDistancesAndCPs(protocol: String, comp: Competitions)

    //Создание групп из конфигурационного протокола (classes.csv)
    fun createGroupsAndDistances(protocol: String, comp: Competitions)

    fun fillResultsByGroups(protocol: String, comp: Competitions)

    //Заполнение всех результатов из конфигурационного протокола (splits.csv)
    fun fillResultsBySportsmen(protocol: String, comp: Competitions)

    fun fillResultsByCPs(protocol: String, comp: Competitions)

    fun fillStarts(protocol: String, comp: Competitions)



    //Запись стартов из стартового протокола (README.md)
    fun fillStarts(protocol: String, group: Group)

    //Генерация стартового протокола (README.md)
    fun makeStartsProtocol(group: Group): String

    //Генерация протокола результатов (README.md)
    fun makeResultsProtocol(group: Group): String

    fun fillResults(protocol: String, group: Group)

    //Протокол прохождения КП (README.md)
    fun makeCPPassingProtocol(CP: ControlPoint): String

    //Протокол прохождения дистанции (README.md)
    fun makeDistancePassingProtocol(sp: CompetitionsSportsman): String
}

