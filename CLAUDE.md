# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Spring Boot 4.1.2 / Java 21 (Gradle, Lombok) Camunda 8.8 client (`camunda-spring-boot-4-starter`, same version as the server). App listens on port **8081**.

FAQ-answering process `faq-question` (`src/main/resources/bpmn/faq-question.bpmn`, deployed on startup via `@Deployment`):
1. Start event (start form `faq-question-start-form`) — variable `question`.
2. `Task_loadFaq` (job type `faq-load`) — `FaqLoaderWorker` reads `faq.location` (default `classpath:faq/faq.md`, override with `FAQ_LOCATION=file:...`) into variable `faq`.
3. `Task_aiAnswer` — **Camunda AI Agent connector** (template `io.camunda.connectors.agenticai.aiagent.v1` v4, job type `io.camunda.agenticai:aiagent:1`), runs in the `connectors` container, provider `openaiCompatible` → local Ollama. Endpoint/model come from connector secrets `OLLAMA_ENDPOINT` / `OLLAMA_MODEL` (env of the `connectors` service). FAQ goes into the system prompt; result `aiAnswer`.
4. `Task_review` — Camunda user task with form `faq-answer-form` (Tasklist), shows `question` + editable `aiAnswer`.

`POST /api/questions {"question": "..."}` starts an instance. No Spring AI — the LLM is called only by the connector.

## Commands

Use the Gradle wrapper (`./gradlew`, or `gradlew.bat` on Windows).

- Build: `./gradlew build`
- Run: `./gradlew bootRun`
- All tests: `./gradlew test`
- Single test class: `./gradlew test --tests lk.tech.camundaai.CamundaAiApplicationTests`
- Single test method: `./gradlew test --tests 'lk.tech.camundaai.CamundaAiApplicationTests.methodName'`

## Local infrastructure

`docker-compose.yml` brings up the full stack: Elasticsearch 8.19 (9200) → Camunda 8.8.24 (REST/Operate/Tasklist on 8080, gRPC 26500, management 9600; auth disabled, admin/admin user) → Connectors bundle 8.8.21 → the app container (`camunda-ai-app`, 8081).

- Ollama runs on the host (`qwen3:14b`); containers reach it at `http://host.docker.internal:11434/v1`.
- Run only the backing services and develop the app locally: `docker compose up elasticsearch camunda connectors`. The app's defaults (`localhost:26500` / `localhost:8080`) point at it.
- Inside compose, the app (`camunda-ai-app`) gets its Camunda addresses via `CAMUNDA_CLIENT_GRPC_ADDRESS` / `CAMUNDA_CLIENT_REST_ADDRESS`. Dockerfile builds with Gradle (`bootJar`).
- Tests run without Camunda (`camunda.client.enabled=false` + mocked `CamundaClient`).

## Known issue

`connectors` 8.8.21 logs `Failed to import process elements ... filter.state cannot be parsed` against Camunda 8.8.24. It only affects inbound connectors (webhooks, message starts); the outbound AI Agent works.
