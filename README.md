

---

# FoodSave API документация

**FoodSave** — это платформа для снижения потерь пищи, которая соединяет поставщиков (супермаркеты, рестораны) с потребителями, желающими приобрести продукты со скидками. Этот репозиторий содержит бэкенд-реализацию платформы FoodSave, построенную с использованием **Spring Boot**.

## Стек технологий

- **Backend:** Java, Spring Boot
- **База данных:** PostgreSQL
- **ORM:** Hibernate (JPA)
- **Документация API:** Swagger/OpenAPI

## Возможности

- **Управление пользователями:** Регистрация, обновление и удаление пользователей.
- **Управление магазинами:** Добавление, обновление и удаление магазинов.
- **Управление продуктами:** Добавление, обновление, удаление продуктов и применение скидок.
- **Управление заказами:** Создание, просмотр и обновление заказов.
- **Управление скидками:** Применение скидок к продуктам.
- **Управление отзывами:** Добавление, обновление и удаление отзывов.

## Установка и запуск

### Требования

- **JDK 17+**
- **Maven**
- **PostgreSQL** для базы данных

### Шаги для установки

1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/bexiiiii/Foodsave_app_backend.git
   ```

2. Перейдите в директорию проекта:
   ```bash
   cd foodsave
   ```

3. Установите зависимости с помощью Maven:
   ```bash
   mvn install
   ```

4. Настройте базу данных PostgreSQL:
    - Создайте базу данных `foodsave_db`.
    - Обновите настройки подключения в `application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/foodsave_db
   spring.datasource.username=postgres
   spring.datasource.password=yourpassword
   ```

5. Запустите приложение:
   ```bash
   mvn spring-boot:run
   ```

### Доступ к Swagger UI

После того как приложение запущено, доступ к Swagger UI будет доступен по адресу:

```
http://localhost:8081/swagger-ui/
```

## API документация

### 1. **Регистрация пользователя (POST /api/users/register)**

- **Метод:** POST
- **URL:** `/api/users/register`
- **Тело запроса:**
```json
{
  "name": "Имя",
  "email": "email@example.com",
  "password": "пароль"
}
```

---

### 2. **Обновление пользователя (PUT /api/users/{id})**

- **Метод:** PUT
- **URL:** `/api/users/{id}`
- **Тело запроса:**
```json
{
  "name": "Обновленное имя",
  "email": "updatedemail@example.com",
  "password": "newpassword"
}
```

---

### 3. **Удаление пользователя (DELETE /api/users/{id})**

- **Метод:** DELETE
- **URL:** `/api/users/{id}`
- **Примечание:** Этот запрос удаляет пользователя с указанным ID.

---

### 4. **Добавление магазина (POST /api/stores)**

- **Метод:** POST
- **URL:** `/api/stores`
- **Тело запроса:**
```json
{
  "name": "Магазин",
  "type": "Restaurant",
  "category": "Fast Food",
  "location": "Москва",
  "contactInfo": "123-456-7890"
}
```

---

### 5. **Обновление магазина (PUT /api/stores/{id})**

- **Метод:** PUT
- **URL:** `/api/stores/{id}`
- **Тело запроса:**
```json
{
  "name": "Обновленный магазин",
  "type": "Cafe",
  "category": "Cafe",
  "location": "Новая локация",
  "contactInfo": "987-654-3210"
}
```

---

### 6. **Удаление магазина (DELETE /api/stores/{id})**

- **Метод:** DELETE
- **URL:** `/api/stores/{id}`
- **Примечание:** Этот запрос удаляет магазин с указанным ID.

---

### 7. **Применение скидки к продукту (PATCH /api/products/{id}/apply-discount)**

- **Метод:** PATCH
- **URL:** `/api/products/{id}/apply-discount`
- **Тело запроса:**
```json
{
  "discountPercentage": 20
}
```

---

### 8. **Поиск продуктов (GET /api/products/search?query=)**

- **Метод:** GET
- **URL:** `/api/products/search?query=exampleProduct`
- **Примечание:** Этот запрос позволяет искать продукты по ключевому слову.

---

### 9. **Получение информации о заказе по ID (GET /api/orders/{id})**

- **Метод:** GET
- **URL:** `/api/orders/{id}`
- **Примечание:** Получить заказ по его ID.

---

### 10. **Обновление статуса заказа (PUT /api/orders/{id}/status)**

- **Метод:** PUT
- **URL:** `/api/orders/{id}/status`
- **Тело запроса:**
```json
{
  "status": "Shipped"
}
```

---

### 11. **Получение списка отзывов по продукту (GET /api/reviews/product/{productId})**

- **Метод:** GET
- **URL:** `/api/reviews/product/{productId}`
- **Примечание:** Получить все отзывы для конкретного продукта.

---

### 12. **Удаление отзыва (DELETE /api/reviews/{id})**

- **Метод:** DELETE
- **URL:** `/api/reviews/{id}`
- **Примечание:** Удалить отзыв по его ID.

---

### 13. **Получение всех скидок (GET /api/discounts)**

- **Метод:** GET
- **URL:** `/api/discounts`
- **Примечание:** Получить все скидки.

---

### 14. **Получение скидки по ID (GET /api/discounts/{id})**

- **Метод:** GET
- **URL:** `/api/discounts/{id}`
- **Примечание:** Получить скидку по ID.

---

### 15. **Создание скидки (POST /api/discounts)**

- **Метод:** POST
- **URL:** `/api/discounts`
- **Тело запроса:**
```json
{
  "discountPercentage": 20,
  "productId": 1
}
```

---

### 16. **Обновление скидки (PUT /api/discounts/{id})**

- **Метод:** PUT
- **URL:** `/api/discounts/{id}`
- **Тело запроса:**
```json
{
  "discountPercentage": 25,
  "productId": 1
}
```

---

### 17. **Удаление скидки (DELETE /api/discounts/{id})**

- **Метод:** DELETE
- **URL:** `/api/discounts/{id}`
- **Примечание:** Удалить скидку по ее ID.

---

## Лицензия

Этот проект лицензируется под лицензией **MIT License**. См. файл [LICENSE](LICENSE) для подробностей.

---

## Заключение

Эта документация предоставляет всю необходимую информацию для настройки и использования API платформы **FoodSave**. Вы можете тестировать все функциональные возможности через Postman или через интерфейс Swagger UI, доступный по адресу `http://localhost:8081/swagger-ui/`.

---

