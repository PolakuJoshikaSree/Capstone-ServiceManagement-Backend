# Multi-stage Dockerfile: build with Maven, run with lightweight JRE

FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /workspace/app

# Copy only Maven files first to leverage Docker layer cache for dependencies
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn

# Copy source and build
COPY src ./src

RUN mvn -B -DskipTests package

# Runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the packaged jar from the build stage
COPY --from=build /workspace/app/target/auth-service-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

