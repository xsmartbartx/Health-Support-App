# Observability

Komponenty i rekomendacje:

- Metrics: Prometheus (node-exporter, kube-state-metrics, app metrics). Export application metrics via OpenTelemetry or Prometheus client libraries.
- Logs: strukturalne JSON logs → Fluentd / Vector → Loki or S3 + SIEM (Elastic/OpenSearch). Centralizuj logi i utrzymuj schematy (ECS/CEF).
- Tracing: OpenTelemetry → Tempo / Jaeger. Instrumentuj end-to-end (frontend → backend → db).
- Dashboards: Grafana z SLO dashboards; definiuj alerty i error budgets.

Zalecenia operacyjne:
- Instrumentuj aplikacje z OpenTelemetry SDK.
- Zdefiniuj SLOs i mapuj do alertów: page vs. ticket alerts.
- Upewnij się, że non-prod ma ograniczone retentiony kosztowe.
- Zachowaj parsowalność logów (JSON) i źródła kontekstu (trace_id, span_id, request_id).

Integracje i tooling:
- Export metrics to Prometheus remote_write if centralization needed.
- Use Tempo/Jaeger for traces and correlate with logs via trace_id.
- Keep dashboards versioned in repo (Grafana as code, jsonnet/terraform-provider-grafana).

