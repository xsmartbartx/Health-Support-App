# Baza danych i migracje

Local migrations

- Use Docker Postgres for local development and apply migrations via Flyway CLI or Gradle/Maven plugin.

Example (Flyway):

```bash
flyway -url=jdbc:postgresql://localhost:5432/db -user=user -password=pass migrate
```

Backups and restore drills

- Keep instructions for creating and restoring snapshots; test restores regularly.

