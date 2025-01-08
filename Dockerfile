# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-21 as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project files (update paths as necessary)
COPY pom.xml ./
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create a minimal runtime image
FROM openjdk:17-jdk-slim

# Create a non-root user
RUN useradd -m -u 1001 appuser

# Set the working directory for the runtime
WORKDIR /app

# Copy only the built JAR file from the builder stage
COPY --from=builder /app/target/*.jar /app/application.jar

# Change ownership of the application folder
RUN chown -R appuser:appuser /app

# Switch to the non-root user
USER appuser

# Expose the application port (optional)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/application.jar"]
