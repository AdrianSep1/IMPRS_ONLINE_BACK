# Use the official Maven image to build the project
FROM maven:3.8.4-openjdk-17 AS build 
WORKDIR /app

# Copy the pom.xml and install dependencies
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Use OpenJDK to run the application
FROM openjdk:17-jdk-slim
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
