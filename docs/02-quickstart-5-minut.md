# Quickstart (5 minut)

Quickstart (5 minut)

1. Klon repo:

   git clone https://github.com/xsmartbartx/Health-Support-App.git
   cd Health-Support-App
   git checkout docs/readme-refactor

2. Uruchom lokalnie zależności przez docker-compose (przykład):

   docker-compose -f docker/docker-compose.dev.yml up --build

3. Build i testy (przykład dla gradle/multi-module):

   ./gradlew clean build
   ./gradlew test

4. Migracje DB (lokalny Postgres):

   docker run --name hs-postgres -e POSTGRES_PASSWORD=pass -p 5432:5432 -d postgres:14
   ./gradlew flywayMigrate

5. Otwórz usługę: http://localhost:8080

(Uwaga: dostosuj porty i komendy do rzeczywistego stacka projektu.)
