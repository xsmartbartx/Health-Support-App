# Build stage
FROM gradle:8.4.1-jdk17 AS builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
# Build the jar (uses Gradle wrapper if present in project; gradle image provides gradle too)
RUN gradle bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre
ARG JAR_FILE=/home/gradle/project/build/libs/*.jar
COPY --from=builder /home/gradle/project/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
