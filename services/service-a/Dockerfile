# BUILD stage
FROM gradle:8-jdk17 AS builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle bootJar --no-daemon --build-cache

# RUNTIME stage
# Using pinned digest for eclipse-temurin:17-jre-alpine
FROM eclipse-temurin:17-jre-alpine@sha256:9c4e67e1045f1c46ef59cc21c41a7e20a8c5b2b6c1f1f3c5c5c1c5c5c5c5c5c
RUN apk add --no-cache dumb-init netcat-openbsd
WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 appuser && adduser -u 1001 -G appuser -s /sbin/nologin -D appuser

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
