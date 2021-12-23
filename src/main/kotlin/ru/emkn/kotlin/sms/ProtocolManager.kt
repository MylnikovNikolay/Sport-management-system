package ru.emkn.kotlin.sms

/*
Менеджеры существуют для рутинной работы с бумажками
 */

/*
make = генерирует новый протокол данного вида
fill = заполняет какие-то данные из протокола
create = создает новые объекты, указанные в названии, и заполняет их данные
process = обрабатывает протокол (так только с teamApplication)
 */
interface ProtocolManager {
    //???
    fun makeResultsProtocolSimple(group: Group): String

    //Генерирует протокол результатов по командам (...)
    fun makeTeamResultsProtocol(comp: Competitions): String

    //Создает соревнования (...)
    fun createCompetitions(protocol: String): Competitions

    //Складывает результаты всех групп в единый протокол (results.csv)
    fun makeGroupResultsProtocol(comp: Competitions): String

    //Обрабатывает заявление от команды (applications)
    fun processTeamApplication(protocol: String, comp: Competitions)

    //Создает дистанции и КП из конфигурационного протокола (courses.csv)
    fun createDistancesAndCPs(protocol: String, comp: Competitions)

    //Создает группы из конфигурационного протокола (classes.csv)
    fun createGroupsAndDistances(protocol: String, comp: Competitions)

    //Заполняет результаты из протокола результатов по группам (results.csv)
    fun fillResultsByGroups(protocol: String, comp: Competitions)

    //Заполняет результаты из протокола результатов по спортсменам (splits.csv)
    fun fillResultsBySportsmen(protocol: String, comp: Competitions)

    //Заполняет результаты из протокола результатов по КП (reverseSplits)
    fun fillResultsByCPs(protocol: String, comp: Competitions)

    //Заполняет время стартов (...)
    fun fillStarts(protocol: String, comp: Competitions)



    //Заполняет старты из стартового протокола (README.md)
    fun fillStarts(protocol: String, group: Group)

    //Генерирует стартовый протокол (README.md)
    fun makeStartsProtocol(group: Group): String

    //Генерирует протокол результатов (README.md)
    fun makeResultsProtocol(group: Group): String

    //Заполняет результаты группы из протокола результатов группы (README.md)
    fun fillResults(protocol: String, group: Group)



    //Генерирует протокол прохождения КП участниками (README.md)
    fun makeCPPassingProtocol(CP: ControlPoint): String

    //Генерирует протокол прохождения дистанции участником (README.md)
    fun makeDistancePassingProtocol(sp: CompetitionsSportsman): String
}

