# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# ---- Run stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/credit-risk-service-0.0.1-SNAPSHOT.jar app.jar

# Render injects PORT, Spring reads it via server.port
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
