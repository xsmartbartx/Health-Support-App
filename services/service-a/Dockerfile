# Build stage
FROM gradle:8.4.1-jdk17 AS builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle bootJar --no-daemon --build-cache

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache dumb-init
WORKDIR /app

# Create non-root user
RUN addgroup -g 1000 appuser && adduser -u 1000 -G appuser -S -H appuser

COPY --from=builder --chown=appuser:appuser /home/gradle/project/build/libs/*.jar /app/app.jar

EXPOSE 8080
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["java", "-jar", "/app/app.jar"]
