# Используем Java
FROM openjdk:17-jdk-slim

# Рабочая папка внутри контейнера
WORKDIR /app

# Копируем jar файл
COPY target/*.jar app.jar

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]