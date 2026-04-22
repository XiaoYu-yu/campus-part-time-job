FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace/backend

COPY backend/.mvn .mvn
COPY backend/mvnw backend/pom.xml ./

RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY backend/src src

RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080 \
    APP_UPLOAD_STORAGE_PATH=/app/uploads/private/images/

COPY --from=build /workspace/backend/target/takeaway-0.0.1-SNAPSHOT.jar /app/app.jar

VOLUME ["/app/uploads"]

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
