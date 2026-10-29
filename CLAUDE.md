# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Spring Boot 4.1.2 / Java 21 (Gradle, Lombok) app that acts as a Camunda 8 (self-managed) client. The codebase is currently a bare skeleton: only `CamundaAiApplication` and a context-load test exist. The Camunda client config is in `src/main/resources/application.yaml` (no auth, `self-managed` mode); the app listens on port **8081**.

Note: `build.gradle` does not yet declare a Camunda client dependency (e.g. `camunda-spring-boot-starter`), even though `application.yaml` has `camunda.client.*` properties. Add it before writing workers or process-deployment code.

## Commands

Use the Gradle wrapper (`./gradlew`, or `gradlew.bat` on Windows).

- Build: `./gradlew build`
- Run: `./gradlew bootRun`
- All tests: `./gradlew test`
- Single test class: `./gradlew test --tests lk.tech.camundaai.CamundaAiApplicationTests`
- Single test method: `./gradlew test --tests 'lk.tech.camundaai.CamundaAiApplicationTests.methodName'`

## Local infrastructure

`docker-compose.yml` brings up the full stack: Elasticsearch 8.19 (9200) → Camunda 8.8.24 (REST/Operate/Tasklist on 8080, gRPC 26500, management 9600; auth disabled, admin/admin user) → Connectors bundle 8.8.21 → the app container (`calculator-app`, 8081).

- Run only the backing services and develop the app locally: `docker compose up elasticsearch camunda connectors`. The app's defaults (`localhost:26500` / `localhost:8080`) point at it.
- Inside compose, the app gets its Camunda addresses via `CAMUNDA_CLIENT_GRPC_ADDRESS` / `CAMUNDA_CLIENT_REST_ADDRESS`, which `application.yaml` reads.

## Known inconsistency

The `Dockerfile` is a **Maven** build (`COPY pom.xml`, `mvn package`, copies `target/*.jar`), but the project uses **Gradle** and has no `pom.xml`. `docker compose up app`/`build` will fail until the Dockerfile is switched to Gradle (`./gradlew bootJar`, jar in `build/libs/`). The container name `calculator-app` also hints at the app's intended purpose (a calculator process), but no such code exists yet.
