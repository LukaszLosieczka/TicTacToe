# Use a base image with Java and an appropriate version
FROM openjdk:17

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled Spring Boot application JAR file into the container
COPY target/backend-0.0.1-SNAPSHOT.jar /app/backend.jar

# Expose the port that your Spring Boot application runs on
EXPOSE 8080

# Specify the command to run your application when the container starts
CMD ["java", "-jar", "backend.jar"]