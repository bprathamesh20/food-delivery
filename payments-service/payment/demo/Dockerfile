FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Build and install the shared module first so this service can resolve it
COPY common-events /tmp/common-events
RUN mvn -f /tmp/common-events/pom.xml -DskipTests clean install

COPY payments-service/payment/demo/pom.xml .
COPY payments-service/payment/demo/src ./src

RUN mvn -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
