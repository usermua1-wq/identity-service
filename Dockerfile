# Build stage
FROM maven:3.9.5-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy the pom.xml file
COPY pom.xml .

# Copy the source code
COPY src ./src
COPY mvnw ./mvnw
COPY mvnw.cmd ./mvnw.cmd
COPY .mvn ./.mvn

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /build/target/identity-service-0.0.1-SNAPSHOT.jar ./identity-service.jar

# Expose the port
EXPOSE 8080

# Set the environment variables (optional - can be overridden at runtime)
ENV SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/identity_service
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=root

# Run the application
ENTRYPOINT ["java", "-jar", "identity-service.jar"]

