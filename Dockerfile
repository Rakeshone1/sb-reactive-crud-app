# Multistage Dockerfile for building Java Spring Boot REST API application

# Stage 1 - Build the application using Maven
FROM maven:3.8.4-openjdk-21 AS builder

# Set the working directory
WORKDIR /app

# Copy the pom.xml
COPY pom.xml .

# Download maven dependencies and cache them
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2 - Create the final image with the built application
FROM eclipse-temurin:21-jre-alpine

# Run as a non-root user for security
RUN addgroup --system spring && adduser --system --ingroup spring spring

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership of the JAR file to the non-root user
RUN chown spring:spring app.jar

# Switch to the non-root user
USER spring:spring

# Expose the port the application runs on
EXPOSE 8081

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
