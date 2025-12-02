FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn -q -DskipTests clean package


FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/modulo3-lambda-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
