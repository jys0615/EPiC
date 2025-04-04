FROM gradle:8.13-jdk21 AS build
WORKDIR /app

COPY . .

RUN gradle clean build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENV FASTAPI_URL=http://host.docker.internal:8000

CMD ["java", "-jar", "app.jar"]
