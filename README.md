# Appium Mobile Cloud Automation Framework (BrowserStack)

Demo framework for **cloud device farm** automation with **BrowserStack App Automate**, Screenplay-style structure, Cucumber, JUnit 5, and Allure. Local emulator/device flows are maintained only as an optional fallback; the primary path is `ENV=browserstack`.

For **local-only** Appium (emulator/USB), use [`appium-mobile-automation-framework`](https://github.com/Harp-Andres/appium-mobile-automation-framework).

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Appium](https://img.shields.io/badge/Appium-9.2.3-blue.svg)](https://appium.io/)
[![Gradle](https://img.shields.io/badge/Gradle-9.x-green.svg)](https://gradle.org/)

## Scope

| Primary | Optional fallback |
|---------|-------------------|
| `ENV=browserstack` + Hub URL with credentials via env/secrets | `ENV=local` + local Appium (`appium.auto.start`) |
| Parallel Android/iOS via platform-prefixed properties | Same codebase, different property files |
| BrowserStack CI workflow | Local Gradle `test` with `-DENV=local` |

Never commit real BrowserStack keys. Use `BROWSERSTACK_USERNAME` / `BROWSERSTACK_ACCESS_KEY` (or placeholders in `browserstack.properties` resolved at runtime).

## Stack

- **Java 17**, **Gradle**, **Appium Java Client 9.2.x**
- **Screenplay-inspired** packages (`actors`, `abilities`, `tasks`, `questions`)
- **Cucumber 7** + **JUnit Platform**
- **Allure** with dynamic `environment.properties` via `AllureEnvironmentWriter`
- **SLF4J + Logback** for framework logging

## Configuration

```text
src/test/resources/config/
  base.properties
  local.properties
  browserstack.properties
```

Set `ENV` to select the overlay file:

```bash
export ENV=browserstack
# or
./gradlew test -DENV=browserstack
```

## Run tests

**Unit tests** (no Appium):

```bash
./gradlew unitTest
# or with env for config-related tests:
./gradlew unitTest -DENV=local
```

**Full Cucumber suite** (requires Appium endpoint — BrowserStack or local):

```bash
./gradlew test -DENV=browserstack
```

`check` depends on `unitTest` so CI can verify pure logic without a device.

## CI

- `.github/workflows/ci-cd-mobile-tests-browserstack.yml` — cloud runs with secrets
- `.github/workflows/ci-cd-mobile-tests.yml` — optional local/self-hosted path

## Packages

```text
com.automatizacion.moderna/
  config/       FrameworkConfig (ENV + platform-aware keys)
  driver/       DriverFactory, AppiumServerManager (local only)
  hooks/        Lifecycle + Allure environment
  tests/unit/   JUnit 5 pure logic tests
```
