# Advanced CQRS with Event Store

Учебный пример CQRS с отдельным event store и read projection: Spring Boot, Java 17, Gradle Kotlin DSL, H2 file database.

Команды пишут события в `stored_events`; обработчик синхронно обновляет отдельную таблицу `task_read_model`. Есть API для чтения журнала и пересборки projection повторным проигрыванием событий.

Подробное объяснение компонентов, основных принципов, API и команд находится в `start.txt`.
