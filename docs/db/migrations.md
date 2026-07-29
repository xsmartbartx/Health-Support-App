# Baza danych i migracje

Sekcja opisuje wzorzec migracji, strategie zero-downtime oraz architekturę bazy danych.

11.1 Baza danych
- Primary DB: Amazon RDS PostgreSQL Multi-AZ.
- Read replicas dla odczytów intensywnych.

11.2 Migracje
- Narzędzia: Flyway lub Liquibase.
- Zasady: migracje wstecznie kompatybilne, additive changes first, backfills as background jobs.
- Wersjonowanie: migracje trzymane w repo kodu serwisu.

11.3 Zero downtime
- Use feature flags for behavioral changes.
- Apply additive schema changes first (ADD COLUMN, new tables), backfill asynchronously, then switch to new column.

11.4 Backup i recovery
- Regularne snapshoty RDS, testowane procedury restore.
- Krytyczne restore drills wykonywane kwartalnie.
