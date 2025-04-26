# 1. Build stage
FROM maven:3.9.0-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# 2. Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
