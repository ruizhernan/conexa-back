# Use a Java 21 base image
FROM eclipse-temurin:21-jdk-jammy

# Set the working directory in the container
WORKDIR /app

# Copy the Maven wrapper files to the container
COPY mvnw .
COPY .mvn .mvn

# Copy the pom.xml and build the dependencies
COPY pom.xml .
RUN ./mvnw dependency:go-offline

# Copy the rest of the application source code
COPY src ./src

# Build the application
RUN ./mvnw clean install -DskipTests

# Expose the port the application runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "target/starwarschallenge-0.0.1-SNAPSHOT.jar"]
