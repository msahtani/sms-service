# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app
COPY pom.xml ./
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Create a minimal runtime image
FROM openjdk:21-jdk-slim

RUN useradd -m -u 1001 appuser
USER appuser

WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar


# Run the application
CMD ["java", "-jar", "app.jar"]
