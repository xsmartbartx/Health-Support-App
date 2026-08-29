# BUILD stage
FROM gradle:9-jdk17 AS builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle bootJar --no-daemon --build-cache

# RUNTIME stage
# Using eclipse-temurin JRE for security updates and support
FROM eclipse-temurin:25-jre
RUN apt-get update && apt-get install -y --no-install-recommends dumb-init netcat-openbsd && rm -rf /var/lib/apt/lists/*
WORKDIR /app

# Create non-root user
RUN groupadd -g 1001 appuser && useradd -u 1001 -g appuser -s /sbin/nologin -c "Docker app user" appuser

COPY --from=builder --chown=appuser:appuser /home/gradle/project/build/libs/*.jar /app/app.jar
COPY --from=builder --chown=appuser:appuser /home/gradle/project/bin/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

EXPOSE 8080
USER appuser

# Health check using Spring Actuator
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["/bin/sh", "-c", "/wait-for-it.sh ${DB_HOST:-postgres} ${DB_PORT:-5432} -- java -jar /app/app.jar"]
