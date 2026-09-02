<div>

# CTT Server

**REST API для социальной сети путешественников [CTT (TravelSocial)](https://github.com/Vex1re/CTT).**

Серверная часть Android-приложения CTT: хранит пользователей и публикации о местах, обрабатывает реакции (лайки/дизлайки), принимает и раздаёт фотографии постов и аватары.

[![Java](https://img.shields.io/badge/Java-21-007396.svg?logo=openjdk&logoColor=white)](https://adoptium.net)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F.svg?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1.svg?logo=postgresql&logoColor=white)](https://www.postgresql.org)
[![Maven](https://img.shields.io/badge/Maven-Wrapper-C71A36.svg?logo=apachemaven&logoColor=white)](https://maven.apache.org)
[![Railway](https://img.shields.io/badge/Deploy-Railway-8A2BE2.svg?logo=railway&logoColor=white)](https://railway.app)

</div>

---

## О проекте

**CTT Server** — бэкенд социальной сети путешественников [CTT (TravelSocial)](https://github.com/Vex1re/CTT). Приложение позволяет публиковать посты о понравившихся местах (достопримечательности, рестораны, кафе, парки, музеи), прикреплять фотографии, оценивать публикации других участников и отслеживать реакции через уведомления — эту серверную часть и реализует данный проект.

- Реализован на **Spring Boot**, развёрнут на [Railway](https://railway.app)
- Данные хранятся в **PostgreSQL**, изображения — в файловой системе сервера и раздаются как статика по `/uploads/**`
- Клиент (Android) обращается к API через **Retrofit + OkHttp** по адресу `https://spring-boot-production-6510.up.railway.app/` (константа `BASE_URL` в [`RetrofitClient.java`](https://github.com/Vex1re/CTT/blob/main/app/src/main/java/space/krokodilich/ctt/RetrofitClient.java))

**Примечание от 02.09.2026:** В дальнейшем я бы полностью переделал базу данных и добавил бы хеширование паролей пользователей.

## Возможности

- **Посты** — создание, чтение, обновление и удаление публикаций (название, город, категория, описание, автор)
- **Реакции и рейтинг** — лайки/дизлайки с автоматическим пересчётом рейтинга; одна реакция на пользователя в рамках поста
- **Изображения** — загрузка нескольких фото к посту (multipart), удаление, раздача файлов
- **Пользователи** — регистрация, обновление профиля, загрузка аватаров
- **Статика** — загруженные файлы доступны по `/uploads/<UUID>.<ext>`

## Технологический стек

| Категория | Технологии |
|---|---|
| Язык | Java 21 |
| Фреймворк | Spring Boot 3.3.4 (Spring Web, Spring Data JPA, Hibernate) |
| База данных | PostgreSQL (драйвер `org.postgresql`) |
| Сборка | Maven (Wrapper) |
| Контейнеризация | Docker (базовый образ `eclipse-temurin:21-jdk-alpine`) |
| Хостинг | Railway |

## Требования

- **JDK 21**
- **PostgreSQL 14+** — локально или облачный инстанс (например, Railway)
- **Docker** (опционально — для контейнерного запуска)

## Быстрый старт

1. **Клонируйте репозиторий:**

   ```bash
   git clone https://github.com/Vex1re/java-spring-boot.git
   cd java-spring-boot
   ```

2. **Настройте подключение к БД** — в `src/main/resources/application.properties` или через переменные окружения:

   ```bash
   # Linux / macOS
   export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/ctt"
   export SPRING_DATASOURCE_USERNAME="postgres"
   export SPRING_DATASOURCE_PASSWORD="<пароль>"
   ```

   ```powershell
   # Windows (PowerShell)
   $env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/ctt"
   $env:SPRING_DATASOURCE_USERNAME = "postgres"
   $env:SPRING_DATASOURCE_PASSWORD = "<пароль>"
   ```

3. **Запустите приложение** (Maven Wrapper входит в репозиторий):

   ```bash
   ./mvnw spring-boot:run      # Linux / macOS
   mvnw.cmd spring-boot:run    # Windows
   ```

4. **Проверьте работу:**

   ```bash
   curl http://localhost:8080/hello
   # Hello world!!
   ```

Сервер поднимется на порту **8080**, каталог загрузок `./uploads` будет создан автоматически при старте.

**Сборка JAR и тесты:**

```bash
./mvnw clean package -DskipTests   # сборка
java -jar target/helloworld-0.0.1-SNAPSHOT.jar
./mvnw test                        # тесты
```

## Конфигурация

Ключевые свойства `src/main/resources/application.properties`:

| Свойство | Значение по умолчанию | Описание |
|---|---|---|
| `spring.datasource.url` | JDBC-URL инстанса PostgreSQL на Railway | Строка подключения к БД |
| `spring.datasource.username` / `spring.datasource.password` | — | Учётные данные БД |
| `file.upload-dir` | `./uploads` | Каталог хранения загруженных файлов |
| `spring.servlet.multipart.max-file-size` | `10MB` | Максимальный размер одного файла |
| `spring.servlet.multipart.max-request-size` | `10MB` | Максимальный размер multipart-запроса |
| `spring.web.resources.static-locations` | `classpath:/static/,file:<upload-dir>` | Источники статики |
| `spring.mvc.static-path-pattern` | `/uploads/**` | URL-шаблон раздачи статики |
| `logging.level.com.railway.helloworld` | `DEBUG` | Логирование приложения |

> [!TIP]
> Любое свойство можно переопределить переменной окружения (Spring relaxed binding): `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `FILE_UPLOAD_DIR` и т. д. Это удобный способ передачи секретов при деплое.

> [!WARNING]
> В репозитории `application.properties` содержит реальные учётные данные БД в открытом виде. Для продакшена вынесите их в переменные окружения и смените пароль.

## REST API

Базовый URL: `http://localhost:8080` (в проде — адрес инстанса Railway).

### Посты — [`PublicationController`](src/main/java/com/railway/helloworld/controller/PublicationController.java)

| Метод | Endpoint | Описание |
|---|---|---|
| `GET` | `/posts/all` | Получить все посты |
| `GET` | `/posts/{id}` | Получить пост по ID (`404`, если не найден) |
| `POST` | `/posts/all` | Создать пост (тело — JSON `Publication`) |
| `PUT` | `/posts/{id}` | Обновить пост (перезаписываются только переданные поля) |
| `PUT` | `/posts/{id}/rating` | Поставить/изменить/снять реакцию: `{"userLogin": "...", "isPositive": true \| false \| null}` (`null` — снять); рейтинг пересчитывается на сервере |
| `DELETE` | `/posts/{id}` | Удалить пост (`204`) |
| `POST` | `/posts/{id}/images` | Загрузить изображения к посту (multipart, часть `files`, допускается несколько) |
| `DELETE` | `/posts/{id}/images?imageUrl=...` | Удалить изображение из списка поста |
| `GET` | `/posts/{id}/likes/check?userLogin=...` | Проверить лайк пользователя: `{"hasLiked": true, "postId": 1, "userLogin": "..."}` |
| `POST` | `/posts/{id}/like` | Добавить лайк: `{"userLogin": "..."}` → `{"post": {...}, "likes": "[...]"}` |
| `POST` | `/posts/{id}/like/remove` | Убрать лайк: `{"userLogin": "..."}` |
| `GET` | `/posts/user/{userLogin}/reactions` | Реакции пользователя на все посты: `{"<postId>": true/false}` |

### Пользователи — [`UserController`](src/main/java/com/railway/helloworld/controller/UserController.java)

| Метод | Endpoint | Описание |
|---|---|---|
| `GET` | `/users/all` | Получить всех пользователей |
| `GET` | `/users/{id}` | Получить пользователя по ID |
| `POST` | `/users/register` | Зарегистрировать пользователя |
| `PUT` | `/users/{id}` | Обновить пользователя (поля `name`, `email`, `avatar`) |
| `DELETE` | `/users/{id}` | Удалить пользователя (`204`) |
| `POST` | `/users/{id}/avatar` | Загрузить аватар (multipart, часть `file`), возвращает URL файла |

### Примеры запросов

Создание поста:

```bash
curl -X POST http://localhost:8080/posts/all \
  -H "Content-Type: application/json" \
  -d '{"name": "Казанский кремль", "location": "Казань", "tag": "достопримечательность",
       "placeName": "Казанский кремль", "description": "Обязателен к посещению", "login": "ivan"}'
```

Ответ — созданный пост:

```json
{
  "id": 1,
  "name": "Казанский кремль",
  "location": "Казань",
  "time": null,
  "placeName": "Казанский кремль",
  "description": "Обязателен к посещению",
  "rating": 0,
  "commentsCount": 0,
  "tag": "достопримечательность",
  "login": "ivan",
  "likes": "[]",
  "images": null
}
```

Реакция на пост (лайк):

```bash
curl -X PUT http://localhost:8080/posts/1/rating \
  -H "Content-Type: application/json" \
  -d '{"userLogin": "maria", "isPositive": true}'
```

Загрузка изображений к посту:

```bash
curl -X POST http://localhost:8080/posts/1/images \
  -F "files=@photo1.jpg" \
  -F "files=@photo2.jpg"
```

## Модель данных

Сущности JPA (схема генерируется Hibernate автоматически, миграции не требуются).

**Publication** — таблица `Publications`, публикация о месте:

| Поле | Тип | Описание |
|---|---|---|
| `id` | `Long` | Идентификатор (auto increment) |
| `name` | `String` | Название поста |
| `location` | `String` | Город |
| `time` | `String` | Дата/время (строкой) |
| `placeName` | `String` | Название места |
| `description` | `String` | Описание |
| `rating` | `int` | Рейтинг: лайки минус дизлайки |
| `commentsCount` | `Integer` | Количество комментариев (по умолчанию 0) |
| `tag` | `String` | Категория места |
| `login` | `String` | Логин автора |
| `likes` | `String`, колонка `jsonb` | JSON-массив строк `"<login>:<true|false>"` |
| `images` | `String`, колонка `jsonb` | JSON-массив URL вида `/uploads/<UUID>.jpg` |

**User** — таблица `Users`:

| Поле | Тип | Описание |
|---|---|---|
| `id` | `Long` | Идентификатор (auto increment) |
| `name` / `surname` | `String` | Имя и фамилия |
| `login` | `String` | Логин |
| `email` | `String` | E-mail |
| `city` | `String` | Город |
| `password` | `String` | Пароль (хранится в открытом виде) |
| `avatar` | `String` | URL аватара `/uploads/...` |

## Работа с изображениями

- Принимаются только файлы с MIME-типом `image/*`; имя генерируется как **UUID** с исходным расширением
- Файлы сохраняются в каталог `file.upload-dir` и раздаются как статика по `/uploads/**` (настройка в [`WebConfig`](src/main/java/com/railway/helloworld/config/WebConfig.java))
- `DELETE /posts/{id}/images` удаляет только URL из списка поста — **файл на диске не удаляется**

## Связка с CTT

Клиент общается с сервером через интерфейс [`ApiService`](https://github.com/Vex1re/CTT/blob/main/app/src/main/java/space/krokodilich/ctt/ApiService.java). Адрес сервера задаётся константой `BASE_URL` в [`RetrofitClient.java`](https://github.com/Vex1re/CTT/blob/main/app/src/main/java/space/krokodilich/ctt/RetrofitClient.java):

```java
private static final String BASE_URL = "https://spring-boot-production-6510.up.railway.app/";
```

Для локальной отладки укажите в клиенте адрес своего сервера: `http://10.0.2.2:8080/` (для эмулятора Android `10.0.2.2` — это хост-машина).

> [!IMPORTANT]
> **Расхождение контрактов.** В клиентском `ApiService` заявлены эндпоинты, которых нет в текущем коде сервера:
>
> | Вызов клиента | Состояние на сервере |
> |---|---|
> | `POST /users` (регистрация) | Реализован как `POST /users/register` |
> | `POST /users/login` (авторизация) | Отсутствует |
> | `GET /users/login/{login}` (пользователь по логину) | Отсутствует |
> | `DELETE /posts/{id}/like` (снятие лайка) | Реализован как `POST /posts/{id}/like/remove` |
>
> Если разворачиваете сервер из этого репозитория вместе с текущим клиентом — либо добавьте недостающие методы, либо адаптируйте `ApiService`.

## Развертывание на Railway

Проект готов к деплою через [`Dockerfile`](Dockerfile): базовый образ `eclipse-temurin:21-jdk-alpine`, сборка Maven Wrapper на этапе сборки образа, запуск `java -jar target/*.jar`.

1. Создайте проект на [Railway](https://railway.app) и подключите этот репозиторий
2. Добавьте базу данных **PostgreSQL** и задайте переменные окружения:

   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:<port>/<database>
   SPRING_DATASOURCE_USERNAME=<user>
   SPRING_DATASOURCE_PASSWORD=<password>
   ```

3. Выполните деплой — Railway соберёт и запустит контейнер

> [!NOTE]
> Файловая система контейнера **эфемерна**: загруженные изображения (`./uploads`) теряются при каждом редеплое. Для продакшена подключите Railway Volume или облачное хранилище (S3 и т. п.).

## Структура проекта

```
java-spring-boot/
├── src/
│   ├── main/
│   │   ├── java/com/railway/helloworld/
│   │   │   ├── HelloworldApplication.java           # Точка входа, health-check GET /hello
│   │   │   ├── PostRepository.java                  # JpaRepository: посты
│   │   │   ├── UserRepository.java                  # JpaRepository: пользователи
│   │   │   ├── config/
│   │   │   │   ├── FileStorageConfig.java           # Свойство file.upload-dir
│   │   │   │   ├── UploadDirectoryInitializer.java  # Создание каталога uploads при старте
│   │   │   │   └── WebConfig.java                   # Раздача статики /uploads/**
│   │   │   ├── controller/
│   │   │   │   ├── PublicationController.java       # Эндпоинты /posts/**
│   │   │   │   └── UserController.java              # Эндпоинты /users/**
│   │   │   ├── model/
│   │   │   │   ├── Publication.java                 # Сущность таблицы Publications
│   │   │   │   └── User.java                        # Сущность таблицы Users
│   │   │   └── service/
│   │   │       ├── PublicationService.java          # Логика постов, реакций, изображений
│   │   │       ├── UserService.java                 # Логика пользователей
│   │   │       └── FileStorageService.java          # Сохранение файлов (UUID, только image/*)
│   │   └── resources/
│   │       └── application.properties               # Конфигурация: БД, uploads, логирование
│   └── test/
│       └── java/com/railway/helloworld/
│           └── HelloworldApplicationTests.java      # Smoke-тест контекста
├── Dockerfile                                       # Образ для деплоя (Railway)
├── mvnw / mvnw.cmd / .mvn/                          # Maven Wrapper
└── pom.xml                                          # Зависимости и плагины сборки
```