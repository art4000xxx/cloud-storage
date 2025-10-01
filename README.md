# Cloud Storage Project

REST-сервис для облачного хранения файлов с поддержкой загрузки, скачивания, удаления и переименования.  
Используется Spring Boot, Spring Security с JWT, PostgreSQL и Docker.

---

## Функционал

- Регистрация и аутентификация пользователей через JWT
- Загрузка файлов
- Скачивание файлов
- Переименование файлов
- Удаление файлов
- Ограничение доступа к файлам по пользователям

---

## Технологии

- Java 17
- Spring Boot
- Spring Security + JWT
- PostgreSQL
- JPA / Hibernate
- Docker / Docker Compose
- Lombok
- Gradle

## Структура проекта
cloud-storage/
├── src/main/java/com/example/cloudstorage
│ ├── controller/ # Контроллеры REST
│ ├── service/ # Сервисы
│ ├── repository/ # Репозитории для JPA
│ ├── entity/ # Сущности
│ ├── dto/ # DTO для запросов и ответов
│ ├── security/ # JWT и Spring Security
│ └── config/ # Конфигурации, инициализация данных
├── src/main/resources
│ ├── application.properties
│ └── db/ # Скрипты или миграции базы
├── build.gradle
└── Dockerfile
## Как запустить

### Backend

1. Клонируем репозиторий:
   ```bash
   git clone https://github.com/art4000xxx/cloud-storage.git
   cd cloud-storage
2.   Собираем проект:
   ./gradlew build
3.  Запускаем Docker Compose (PostgreSQL + приложение):
4.  Backend доступен по http://localhost:8080
5.  Frontend

Фронт запускается отдельно из папки netology-diplom-frontend

Настройки CORS уже настроены на http://localhost:8081

Логин: admin / 123
DTO

Все ответы и запросы используют DTO, чтобы не работать с Map и JSON руками, например:

SuccessResponse — для успешных операций

ErrorResponse — для ошибок

RenameFileRequest — для переименования файлов
Автор:
art4000xxx




