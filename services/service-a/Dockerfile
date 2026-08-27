# BUILD stage
FROM gradle:jdk17 AS builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle bootJar --no-daemon

# RUNTIME stage - no apt installs; copy wait-for-it from builder
FROM eclipse-temurin:17-jre
ARG JAR_FILE=/home/gradle/project/build/libs/*.jar
# Copy application jar
COPY --from=builder /home/gradle/project/build/libs/*.jar /app/app.jar
# Copy wait-for-it script from project into the image
COPY --from=builder /home/gradle/project/bin/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]