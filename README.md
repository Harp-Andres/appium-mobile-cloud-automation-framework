# Appium Mobile Cloud Automation Framework

Demo framework for running the **same Appium + Cucumber suite** on multiple device farms: **BrowserStack**, **AWS Device Farm**, and **local Appium** as an optional fallback. Specialty = `ENV` profiles (`local` / `browserstack` / `aws`), `DriverFactory` farm capabilities, and `scripts/download-test-apps.sh` for shared AUT binaries.

| Sibling repo | Focus |
| --- | --- |
| [`appium-mobile-automation-framework`](https://github.com/Harp-Andres/appium-mobile-automation-framework) | Mature **self-hosted/local** ExpandTesting (do not change its default AUT) |
| [`demo-serenity-screenplay-mobile`](https://github.com/Harp-Andres/demo-serenity-screenplay-mobile) | Serenity Screenplay + TheApp |
| **This repo** | Same suite on **BrowserStack + AWS** (+ local fallback with TheApp) |

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Appium](https://img.shields.io/badge/Appium-9.2.3-blue.svg)](https://appium.io/)
[![Gradle](https://img.shields.io/badge/Gradle-9.x-green.svg)](https://gradle.org/)

## App under test (AUT)

| Platform | Artifact | Package / bundle |
| --- | --- | --- |
| Android | `apps/TheApp.apk` (download script) | `com.appiumpro.the_app` |
| iOS (real device farms) | `apps/SauceLabs-Sample.ipa` | `com.saucelabs.mydemoapp.ios` |

```bash
./scripts/download-test-apps.sh
```

Do **not** commit APK/IPA files. Upload to farms and set `BROWSERSTACK_*` or Device Farm upload ARNs as documented in `docs/TEST_APP_AND_FARMS.md`.

## Scenarios (farm smoke)

1. **`framework_health.feature`** — create session, app launches in foreground.
2. **`theapp_smoke.feature`** — TheApp home → Login Screen → optional `alice` / `mypassword` login.

## Stack

- **Java 17**, **Gradle**, **Appium Java Client 9.2.x**
- Lightweight Screenplay-style packages (`actors`, `abilities`, `tasks`, `questions`)
- **Cucumber 7** + **JUnit Platform**
- **Allure** with dynamic `environment.properties` via `AllureEnvironmentWriter`

## Configuration

```text
src/test/resources/config/
  base.properties      # shared defaults (TheApp Android package/activity)
  local.properties     # apps/TheApp.apk + local Appium
  browserstack.properties
  aws.properties
```

```bash
export ENV=browserstack   # or aws | local
./gradlew test -DENV=browserstack
```

Never commit real farm credentials. BrowserStack: `BROWSERSTACK_USERNAME` / `BROWSERSTACK_ACCESS_KEY`. AWS: `AWS_DEVICE_FARM_APPIUM_URL` when running inside a custom test environment.

## Run tests

**Unit tests** (no Appium):

```bash
./gradlew unitTest
```

**Cucumber** (requires Appium endpoint):

```bash
./gradlew test -DENV=browserstack --tests "com.automatizacion.moderna.runner.RunMobileTestSuite"
# or health-only / TheApp-only runners under com.automatizacion.moderna.runner
```

`check` depends on `unitTest` so CI can verify pure logic without a device.

## CI

- `.github/workflows/ci-cd-mobile-tests-browserstack.yml`
- `.github/workflows/ci-cd-mobile-tests-aws-device-farm.yml`
- `.github/workflows/ci-cd-mobile-tests.yml` — optional local/self-hosted path
