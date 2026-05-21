FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -y curl

WORKDIR /app

COPY target/order-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]