[![Build status](https://ci.appveyor.com/api/projects/status/jxda1fujx0w16vkk?svg=true)](https://ci.appveyor.com/project/k-emiko/aqa-diploma)

### [План тестирования](https://github.com/k-emiko/aqa-diploma/blob/master/reporting/Plan.md)


### [Отчетность по результатам тестирования](https://github.com/k-emiko/aqa-diploma/blob/master/reporting/Report.md)

### [Отчетность по результатам автоматизации](https://github.com/k-emiko/aqa-diploma/blob/master/reporting/Summary.md)

### Порядок запуска тестов

1. Перейти в папку [корневую папку проекта](https://github.com/k-emiko/aqa-diploma/tree/master/) и выполнить автотесты командой `./gradlew build`
1. Выполнить в терминале команду `./gradlew allureReport && ./gradlew allureServe` для генерации отчета после прохождения тестов.
