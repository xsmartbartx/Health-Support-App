# Architektura — skrót

Topologia:

- Frontend (CDN) -> API Gateway -> Ingress (Kubernetes/EKS) -> Mikroserwisy
- Persistencja: PostgreSQL (primary), Redis (cache)
- Observability: Prometheus, Grafana, Loki, Tempo, OpenTelemetry
- CI/CD: GitHub Actions -> Container Registry -> ArgoCD (promocje)
- Secrets: Vault (or SOPS/encrypted files)

Główne decyzje projektowe:

- Microservices w kontenerach, manifesty Helm dla K8s
- Infrastruktura zarządzana przez Terraform (moduły, state backend)
- Security-first: immutable logs, RBAC policy engine, audytowalny pipeline

## Komponenty systemu

### Frontend
- CDN-hosted SPA (React/Vue/Angular)
- API Gateway (Kong/AWS API Gateway)
- WebSocket support dla real-time updates

### Backend Services
- Microservices w Kubernetes
- REST + gRPC endpoints
- Service mesh (opcjonalnie: Istio)

### Data Layer
- PostgreSQL 14+ (primary DB)
- Redis 6+ (cache + session store)
- Object storage (S3 compatible)

### Observability
- Prometheus: metrics collection
- Grafana: visualization
- Loki: log aggregation
- Tempo: distributed tracing
- OpenTelemetry: instrumentation

### Security
- RBAC policy engine
- WAF (ModSecurity or cloud WAF)
- SIEM/SOAR for security operations
- Immutable audit logs (WORM storage)

### CI/CD
- GitHub Actions: build, test, scan, publish
- Container Registry: image storage
- ArgoCD: GitOps-based deployment

## Deployment Model

- **Local Dev**: Docker Compose (all services)
- **Staging**: EKS / k3s with Helm
- **Production**: EKS with ArgoCD promotion

## Scalability Considerations

- Horizontal scaling: Kubernetes HPA (Horizontal Pod Autoscaler)
- Database: Read replicas, connection pooling
- Cache: Redis clustering
- Storage: S3-compatible backend with versioning
