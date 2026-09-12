FROM gradle:jdk25-ubi AS build

COPY . /tmp/app
WORKDIR /tmp/app

RUN gradle bootJar

FROM eclipse-temurin:25-jre-alpine AS runtime

COPY --from=build ./tmp/app/build/libs/app.jar /app.jar
ENTRYPOINT [ "java", "-jar", "/app.jar"]