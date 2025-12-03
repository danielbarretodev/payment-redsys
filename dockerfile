# Fase 1: build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -q -DskipTests package

# Fase 2: runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# 👇 Ya no fijamos aquí el perfil, lo mandamos desde fuera
ENTRYPOINT ["java", "-jar", "app.jar"]
