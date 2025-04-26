# Используем официальный образ Java 17
FROM eclipse-temurin:17-jdk

# Создаем директорию для приложения
WORKDIR /app

# Копируем собранный jar-файл в контейнер
COPY target/*.jar app.jar

# Открываем порт 8080
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java","-jar","/app/app.jar"]
