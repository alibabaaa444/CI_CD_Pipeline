# Dockerfile
FROM eclipse-temurin:17-alpine
COPY target/app.jar /app.jar
EXPOSE 7070
ENTRYPOINT ["java", "-jar", "/app.jar"]
