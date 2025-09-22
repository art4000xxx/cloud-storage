# Cloud Storage Backend

## Описание
Это REST-сервис для облачного хранилища файлов.  
Фронтенд подключается к бэкенду без доработок. Всё работает сразу.

## Технологии
- Java 21+
- Spring Boot
- Spring Data JPA
- Spring Security
- PostgreSQL
- Lombok
- Gradle
- Docker + docker-compose

## Как запускать

### Через Docker
1. Перейти в корень проекта (где `docker-compose.yml`).
2. Выполнить:
```bash
docker-compose up --build
После запуска:

Бэкенд: http://localhost:8080

PostgreSQL: порт из docker-compose
Локально без Docker
./gradlew bootRun
Авторизация

POST /login
Пример запроса:
{
  "login": "admin",
  "password": "123"
}
Пример ответа:
{
  "auth-token": "..."
}
Примечание

Фронт подключается через .env:
VUE_APP_BASE_URL=http://localhost:8080
Автор: art4000xxx
