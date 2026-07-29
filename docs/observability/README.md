# Observability

Quick local setup (docker-compose)

```yaml
# example docker-compose snippet (not committed):
# prometheus, grafana, loki, tempo services for local dev
```

Start demo stack:

```bash
docker-compose -f infra/dev-observability.yml up -d
```

Access Grafana at http://localhost:3000 and import dashboards from grafana/dashboards.

Tracing

- Instrument app with OpenTelemetry SDK; ensure trace_id is included in logs.

