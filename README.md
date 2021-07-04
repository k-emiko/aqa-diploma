### [План тестировани](https://github.com/k-emiko/aqa-diploma/blob/master/reporting/Plan.md)


### [Отчетность по резульатам тестирования](https://github.com/k-emiko/aqa-diploma/blob/master/reporting/Report.md)


### Порядок запуска тестов

1. Перейти в папку [artifacts](https://github.com/k-emiko/aqa-diploma/tree/master/artifacts)
1. Выполнить в терминале команду `docker-compose up` и дождаться сообщения вида `Started ShopApplication in X seconds (JVM running for Y)`
1. Перейти в папку [project]() и выполнить автотесты командой `./gradlew build`
1. Выполнить в терминале команду `./gradlew allureReport && ./gradlew allureServe` для генерации отчета после прохождения тестов.
`

