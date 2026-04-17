# Тестовое задание CDEK - Task time tracker

## Описание сервиса

### Эндпоинты

- `POST /api/v1/tasks` - создание задачи
- `GET /api/v1/tasks/{taskId}` - получение информации о задаче по ID
- `PATCH /api/v1/tasks/{taskId}/status` - изменение статуса задачи
- `POST /api/v1/time-records` - создать запись о затраченном времени сотрудника на задачу
- `GET /api/v1/time-records/employees/{employeeId}?startDate={start_time}&endDate={end_time}` - получить информацию о затраченном времени сотрудника на задачи за определённый
  период времени
- `GET /api/v1/time-records/{timeRecordId}` - получить информацию о затраченном времени по ID

### Функционал

- Генерация документации API с помощью SpringDoc OpenAPI (Swagger)
- Валидация входящих данных с помощью Bean Validation
- Обработка исключений через глобальный обработчик исключений
- Интеграционные тесты взаимодействия с БД через TestContainers

### Проверка работоспособности

Для проверки работоспособности сервиса можно запустить тесты:

```bash
./mvnw test
```

## Сборка м запуск проекта

Для сборки и запуска проекта необходимо выполнить следующие шаги:

1. Убедитесь, что у вас установлен JDK 25 и запущен Docker (для TestContainers и БД PostgreSQL).

2. Соберите проект с помощью Maven:

```bash
./mvnw clean install
```

3. Запустите приложение:

```bash
./mvnw spring-boot:run
```

## Тестовые запросы

Для проверки работы всех реализованных эндпоинтов можно использовать следующий набор тестовых запросов:

[Набот тестовых запросов для HTTP-клиента IntelliJ IDEA](./http/requests.http).

Также его [clean-версию](./http/clean-requests.http) можно использовать в VS Code с помощью расширения [REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client).
