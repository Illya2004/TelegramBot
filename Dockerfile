# Use the official OpenJDK base image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot application's JAR file to the container
COPY target/TelegramBot-0.0.1-SNAPSHOT.jar /app/TelegramBot-0.0.1-SNAPSHOT.jar

# Expose the port that the Spring Boot application will run on
EXPOSE 8080

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/TelegramBot-0.0.1-SNAPSHOT.jar"]
