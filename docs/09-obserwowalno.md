# Obserwowalność


Obserwowalność

- Metryki: Prometheus + Grafana (dashboards), zbieranie z aplikacji przez OTLP
- Logging: Loki / centralized logging (lokalne docker-compose dev stack)
- Tracing: OpenTelemetry -> Tempo/Jaeger

Szybkie uruchomienie lokalne (docker-compose):
- docker-compose -f infra/observability/docker-compose.yml up
