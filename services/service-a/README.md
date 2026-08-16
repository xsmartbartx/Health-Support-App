# service-a (scaffold)

A minimal Spring Boot service useful for local development and verifying the docs.

Build & run locally with Docker Compose:
```bash
# from repo root
docker-compose -f infra/dev-docker-compose.yml up --build -d
# verify
curl http://localhost:8080/health
```

Build locally with Gradle (if you have Gradle or gradle wrapper):
```bash
cd services/service-a
# if ./gradlew exists at root you can run it from repo root; otherwise run gradle here
gradle clean build
gradle bootRun
```

Notes:
- This scaffold assumes Java 17.
- If you want exact parity with README commands (`./gradlew` from repo root), I can add the Gradle wrapper at repo root. Tell me if you'd like that included.
