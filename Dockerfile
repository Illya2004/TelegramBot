# Use the official OpenJDK base image
FROM eclipse-temurin:17-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot application's JAR file to the container
COPY target/TelegramBot-0.0.1-SNAPSHOT.jar /app/TelegramBot-0.0.1-SNAPSHOT.jar

# Expose the port that the Spring Boot application will run on
EXPOSE 8081

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/TelegramBot-0.0.1-SNAPSHOT.jar"]
