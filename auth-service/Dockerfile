# 1) Build stage: compile the JAR
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app

# only copy pom & fetch deps (caches on changes to pom.xml)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# now copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# 2) Run stage: slim runtime image
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# copy the fat JAR from build stage
COPY --from=build /app/target/auth-service-0.0.1-SNAPSHOT.jar app.jar

# expose port and run
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
