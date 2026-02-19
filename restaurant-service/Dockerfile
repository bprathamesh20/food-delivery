# Multi-stage build for Restaurant Service
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Build and install the shared module first so this service can resolve it
COPY common-events /tmp/common-events
RUN mvn -f /tmp/common-events/pom.xml -DskipTests clean install

COPY restaurant-service/pom.xml .
COPY restaurant-service/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
