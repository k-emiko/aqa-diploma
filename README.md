[![Build status](https://ci.appveyor.com/api/projects/status/jxda1fujx0w16vkk?svg=true)](https://ci.appveyor.com/project/k-emiko/aqa-diploma)

### [План тестирования](https://github.com/k-emiko/aqa-diploma/blob/master/reporting/Plan.md)


### [Отчетность по резульатам тестирования](https://github.com/k-emiko/aqa-diploma/blob/master/reporting/Report.md)


### Порядок запуска тестов

1. Перейти в папку [artifacts](https://github.com/k-emiko/aqa-diploma/tree/master/artifacts)
1. Выполнить в терминале команду `docker-compose up` и дождаться сообщения вида `Started ShopApplication in X seconds (JVM running for Y)`
1. Перейти в папку [корневую папку проекта](https://github.com/k-emiko/aqa-diploma/tree/master/) и выполнить автотесты командой `./gradlew build`
1. Выполнить в терминале команду `./gradlew allureReport && ./gradlew allureServe` для генерации отчета после прохождения тестов.