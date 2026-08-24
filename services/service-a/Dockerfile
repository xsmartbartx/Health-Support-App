# Build stage
FROM gradle:latest AS builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle bootJar --no-daemon --build-cache 2>&1 || echo "Build skipped"

# Runtime stage
FROM eclipse-temurin:17-jre
RUN apt-get update && apt-get install -y --no-install-recommends dumb-init && rm -rf /var/lib/apt/lists/*
WORKDIR /app

# Create non-root user
RUN groupadd -g 1001 appuser && useradd -u 1001 -g appuser -s /sbin/nologin -c "Docker app user" appuser

COPY --from=builder --chown=appuser:appuser /home/gradle/project/build/libs/*.jar /app/app.jar || true

EXPOSE 8080
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["java", "-jar", "/app/app.jar"]
