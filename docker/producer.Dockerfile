FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jre
COPY --from=build /app/example-producer/target/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]