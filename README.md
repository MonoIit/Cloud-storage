# Cloud Service

Учебный проект — облачное хранилище на **Spring Boot** с поддержкой аутентификации, хранения файлов и интеграцией с **MinIO**.

## Стек технологий
- **Backend:** Java 21, Spring Boot, Gradle 
- **База данных:** PostgreSQL
- **Объектное хранилище:** MinIO
- **Контейнеризация:** Docker & Docker Compose
- **Тестирование:** JUnit 5, Mockito, Testcontainers
- **Мониторинг:** Prometheus, Grafana

## Запуск приложения

Перейдите в директорию со скаченным репозиторием, и запустите:
```bash
    docker compose up --build
```

Опционально измените переменные окружения в `.env`

Обратите внимание, если запускает проект локально, например, из IntelliJ Idea, то изменять переменные окружения, нужно в файле `application.properties`

После этого будет запущенно 5 docker-контейнеров:
- Backend: [`http://localhost:8080`](http://localhost:8080)
- PostgreSQL: [`http://localhost:5432`](http://localhost:5432)
- MinIO UI: [`http://localhost:9001`](http://localhost:9001)
- Grafana: [`http://localhost:3000`](http://localhost:3000)
- Prometheus: [`http://localhost:9090`](http://localhost:9090)

## Описание функциональности
Основные функции сервиса:
- Регистрация пользователя по логину и паролю (`POST http://localhost:8080/register`)
- Аутентификация пользователя по логину и паролю с выдачей токена (`POST http://localhost:8080/login`)
- Загрузка файла с указанием пользовательского названия (`POST http://localhost:8080/file?filename=`)
- Просмотр информации о файлах по ограничению (`GET http://localhost:8080/list?limit=`)
  - параметр limit необязательный, по умолчанию limit=10 
- Скачивание файла по пользовательскому названию (`GET http://localhost:8080/file?filename=`)
- Изменение пользовательского названия файла (`PUT http://localhost:8080/file?filename=`)
- Удаление файла по пользовательскому названию (`DELETE http://localhost:8080/file?filename=`)
- Деактивация токена (`POST http://localhost:8080/logout`)
- Удаление аккаунта (`DELETE http://localhost:8080/account`)

## Авторизация пользователя
Пользователь передает логин и пароль, использованные при регистрации, в endpoint /login в POST запросе. В ответ от сервера приходит JSON, хранящий токен. Значение токена необходимо поместить в заголовок `auth-token` при последующих запросах на управление файлами и аккаунта.

Пример заголовка с токеном:
```yaml
auth-token: BTk4Zt3Vr-OEk95jzQpmbBci5tcvJKSJzdyQsvN41Bc
```

## Формальное описание API
Интерфейс (REST API) сервиса формализовано в openAPI описании в файле [src/main/resources/File.yaml](src/main/resources/File.yaml).
В openAPI описании учтены коды ответов и возможные входные и выходные сообщения в каждый endpoint.

## Хранение данных
Управление пользовательскими данными и метаданными файлов производится в базе данных PostgreSQL.
Хранение самих файлов осуществляется в объектном хранилище MinIo.

## Тестирование
Проект разделяет юнит-тесты и интеграционные тесты с помощью JUnit 5 tags.

Для запуска юнит-тестов
```
./gradlew unitTest
```

Для запуска интеграционных тестов
```
./gradlew integrationTest
```

Для запуска всех тестов
```
./gradlew test
```

## CI
Проект настроен с помощью **GitHub Actions**:
- запускаются юнит и интеграционные тесты
- собирается приложение
- артефакт (`.jar`) загружается в GitHub.

Файл пайплайна: [`.github/workflows/ci.yml`](.github/workflows/ci.yml)

## Мониторинг
Сбор метрик осуществляется с помощью Actuator и Micrometer. Метрики собираются в Prometheus
Визуализация метрик настроена в Grafana и доступна по адресу: [`http://localhost:3000`](http://localhost:3000) 



