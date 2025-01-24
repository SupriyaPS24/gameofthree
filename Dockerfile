# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean install -DskipTests --batch-mode


# Stage 2: Run the application
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar gameofthree.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "gameofthree.jar"]
