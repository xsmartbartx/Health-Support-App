# CI/CD

CI/CD

- GitHub Actions: pipeliney do build/test, SAST, SCA, publish container
- Local debug: instalacja act do lokalnego uruchamiania workflowów
- Promotion: ArgoCD automatycznie synchronizuje obrazy ze środowiskami po tagowaniu lub merge

Przykładowe kroki pipeline:
- checkout
- set up JDK/node/python
- cache deps
- build
- unit tests
- SAST (semgrep/tfsec/trivy)
- publish container
- open PR -> merge -> ArgoCD promuje
