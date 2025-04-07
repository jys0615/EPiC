FROM gradle:8.13-jdk21 AS build
WORKDIR /app

COPY . .

RUN gradle clean build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

CMD ["java", "-jar", "app.jar"]
