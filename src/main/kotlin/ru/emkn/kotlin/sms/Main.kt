package ru.emkn.kotlin.sms



fun main(args: Array<String>) {
    val configPath = "./data/config/%s"
    val competitions = Competitions.fromString(readCSV( configPath.format("event.csv") ))
    competitions.takeDistancesAndCPs(readCSV( configPath.format("courses.csv") ))
    competitions.takeGroupsAndDistances(readCSV( configPath.format("classes.csv") ))

    competitions.takeAllApplicationsFromFolder("./data/applications/")

    competitions.makeADrawAndWrite()

    /*
     * Тут пользователь как-то сообщит программе, что соревнование закончилось и результаты загружены
     * (пока что хотя бы в папку в виде файлов). После этого произойдет загрузка рез-ов.
     */

    // Рассчет на то, что сплиты будут хранится не только в одном файле
    competitions.takeResults(readCSV( "./data/splits/splits.csv" ))
    competitions.writeTotalResults()
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
