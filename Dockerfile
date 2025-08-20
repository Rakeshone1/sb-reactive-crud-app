# ---------------------------------------------
# Stage 1 - Build the application using Maven
# ---------------------------------------------
FROM maven:3.8.4-temurin-21 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven descriptor
COPY pom.xml .

# Download dependencies to cache them
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application and skip tests
RUN mvn clean package -DskipTests

# ---------------------------------------------
# Stage 2 - Create the final runtime image
# ---------------------------------------------
FROM eclipse-temurin:21-jre-alpine

# Create a non-root user for security
RUN addgroup --system spring && adduser --system --ingroup spring spring

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership of the JAR file
RUN chown spring:spring app.jar

# Switch to the non-root user
USER spring:spring

# Expose the application port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
