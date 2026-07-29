# Health Support App

Krótki opis: aplikacja wspierająca opiekę zdrowotną — repo zawiera kod serwisów, infrastrukturę i dokumentację operacyjną.

Spis treści

- Quickstart (uruchomienie lokalne)
- Dokumentacja (docs/)
- Contributing & policies

Quickstart (dla developera)

1. Sklonuj repo i przejdź do katalogu:

```bash
git clone <repo-url>
cd Health-Support-App
```

2. Przygotuj środowisko (przykład dla Windows / PowerShell):

```powershell
choco install git docker-desktop  # lub zainstaluj Docker manualnie
# zainstaluj Java/Gradle/Python/Node jeśli potrzebne
```

3. Uruchom zależności lokalnie (prostym docker-compose lub lokalnymi serwisami):

```bash
# uruchom bazę i redis (przykład)
docker-compose -f infra/dev-docker-compose.yml up -d
```

4. Buduj i uruchom serwis (przykład Java/Gradle):

```bash
./gradlew build
./gradlew bootRun
```

5. Testy:

```bash
./gradlew test
```

Gdzie szukać dalszych informacji: zobacz pliki w docs/ (index w docs/README.md).

