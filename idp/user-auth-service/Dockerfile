# 1. Build stage
FROM maven:3.9.0-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 2. Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/user-auth-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
