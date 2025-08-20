# ---------------------------------------------
# Stage 1 - Build the application using Maven
# ---------------------------------------------
FROM maven:3.8.4-temurin-21 AS builder

WORKDIR /app

# Copy Maven descriptor
COPY pom.xml .

# Preload dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# ---------------------------------------------
# Stage 2 - Create the final runtime image
# ---------------------------------------------
FROM eclipse-temurin:21-jre-alpine

# Create a non-root user
RUN addgroup --system spring && adduser --system --ingroup spring spring

WORKDIR /app

# Copy the built JAR file
COPY --from=builder /app/target/*.jar app.jar

# Set permissions
RUN chown spring:spring app.jar

USER spring:spring

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
