FROM amazoncorretto:17.0.14-alpine3.21
LABEL authors="kittu"

WORKDIR /app

COPY target/CorpSuite-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]