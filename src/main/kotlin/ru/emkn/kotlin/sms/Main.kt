package ru.emkn.kotlin.sms

fun main(args: Array<String>) {
    UsualLogger.start()
    ErrorsAndWarningsLogger.start()
    /*
     * В папке test-data есть несколько наборов тестовых данных
     * Можно указать как параметр командной строки test-data/data , test-data/data1 или test-data/small-test/data
     * для проверки на одном из этих наборов
     */
    val dataFolder = if (args.isNotEmpty()) args[0] else "data"
    val configPath = "./$dataFolder/config/%s"
    val eventFileName = "event.csv"
    val coursesFileName = "courses.csv"
    val classesFileName = "classes.csv"
    val competitions = CompetitionsByCSV.fromString(readCSV( configPath.format(eventFileName) ))
    competitions as CompetitionsByCSV
    CsvProtocolManager.takeDistancesAndCPs(readCSV( configPath.format(coursesFileName) ), competitions)
    Csv.takeGroupsAndDistances(readCSV( configPath.format(classesFileName) ), competitions)

    competitions.takeAllApplicationsFromFolder("./$dataFolder/applications/")

    competitions.makeADrawAndWrite("./$dataFolder/start protocols/")

    /*
     * Тут пользователь как-то сообщит программе, что соревнование закончилось и результаты загружены
     * (пока что хотя бы в папку в виде файла). После этого произойдет загрузка рез-ов.
     */
    println("Поместите результаты в файл в папке $dataFolder/splits и введите название файла:")
    // TODO(стоит сделать эту часть адекватнее, все равно файл со сплитами один всего)
    val splitsFileName = readLine()

    Csv.takeResultsFromSplits(readCSV( "./$dataFolder/splits/$splitsFileName" ), competitions)
    competitions.writeTotalResults("./$dataFolder/results")
    competitions.writeTeamResults("./$dataFolder/results")
}

/*
Примерный план работы программы с дополнительными идеями, помеченные как optional

1. Создание соревнования
    Загрузка из конфигурационных файлов:
    - название соревнования и дата проведения
    - дистанции
    - список групп
      (optional: возможность делать ограничение для группы, которое будет проверяться автоматически,
       например: для М21 обязательное условие, что возраст участника - 21 год и мужской пол)

2. Формирование списка участников
    - приём заявок от команд с распределением по группам
      (заявки хранятся в .csv файлах в отдельной папке)
    - (optional: возможность исправить ошибки в заявках и подать дополнительные
       например через консоль или как-нибудь еще)

3. Соревнование
    - жеребьёвка
    - загрузка списков результатов
      (нужно уметь считывать из файлов и через консольный ввод)

4. Результаты
    - список результатов для групп
    - список результатов для команд

 */
