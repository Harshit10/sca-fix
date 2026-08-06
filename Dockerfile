# syntax=docker/dockerfile:1

# ---- build stage ----
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY build.gradle settings.gradle gradle.properties ./
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies || true

COPY src ./src
RUN ./gradlew --no-daemon clean build -x integrationTest

# ---- runtime stage ----
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

RUN useradd --system --uid 10001 appuser
COPY --from=build /workspace/build/libs/*.jar app.jar
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
