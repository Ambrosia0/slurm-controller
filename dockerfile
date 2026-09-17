FROM node AS frontend-build

WORKDIR /tmp/app

COPY ./frontend/package*.json ./

RUN npm ci --legacy-peer-deps

COPY ./frontend ./

RUN npm run build

FROM gradle:jdk25-ubi AS build

WORKDIR /tmp/app

COPY --from=frontend-build /tmp/app/build/ ./src/main/resources/static/
COPY . .

RUN gradle bootJar

FROM eclipse-temurin:25-jre-alpine AS runtime

COPY --from=build ./tmp/app/build/libs/app.jar /app.jar
ENTRYPOINT [ "java", "-jar", "/app.jar"]