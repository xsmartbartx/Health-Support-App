# Monitoring i obserwowalność

14 Monitoring i obserwowalosc — elementy

- Metrics: Prometheus (node-exporter, kube-state-metrics, app metrics).
- Logs: strukturalne JSON logs → Fluentd/Vector → S3 + SIEM (Elastic/Splunk).
- Tracing: OpenTelemetry → Tempo / Jaeger.
- Dashboards: Grafana (SLO dashboards), Kibana dla logs.

Zalecenia:
- Instrumentuj aplikacje OpenTelemetry SDK.
- Ustal SLOs i eksportuj metryki do Prometheus.
- Utrzymuj parsowanie logów i schematy JSON (ECS lub CEF mapping).
