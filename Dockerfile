# Start with a base image containing Java runtime
FROM openjdk:17-jdk-slim

# Copy the Maven build output (JAR file) into the container. Adjsut the name as needed.
COPY target/Ahweenu-0.0.1-SNAPSHOT.jar

# Expose the port on which your Spring Boot application will run
EXPOSE 5000

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
