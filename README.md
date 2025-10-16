# caom2ui
CAOM-2 web user interface

<a href="https://travis-ci.org/opencadc/caom2ui"><img src="https://travis-ci.org/opencadc/caom2ui.svg?branch=master" /></a>

## Advanced Search

The Advanced Search web application now lives under `advanced-search/` in this repository.

- **Build:** From the repo root run `./gradlew :advanced-search:build -x intTestChrome -x intTestFirefox` (the Selenium integration tests remain optional until shared infrastructure is available).
- **Deployment:** A concise guide covering prerequisites, required Docker volumes, and the local `docker-compose.yaml` lives at `advanced-search/docs/deployment.md`.
- **Artifacts:** The module produces the `AdvancedSearch.war` file and bundles supporting Docker assets (see `advanced-search/Dockerfile` and `advanced-search/docs/docker-compose.yaml`).
- **Coverage:** Unit and integration test sources are in `advanced-search/src/test` and `advanced-search/src/intTest`; the tasks mirror the historical GitLab setup so existing pipelines can be reinstated when environments are ready.
