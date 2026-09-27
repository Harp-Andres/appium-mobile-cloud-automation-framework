# App under test + multi-farm execution (cloud repo)

This repo’s specialty is **running the same Appium suite on different farms** with one codebase and `ENV`-selected property overlays.

## Free apps

| Platform | Artifact | Source |
| --- | --- | --- |
| Android | `apps/TheApp.apk` | [appium-pro/TheApp](https://github.com/appium-pro/TheApp) |
| iOS Simulator | `apps/TheApp.app.zip` | same |
| iOS **real device** (BrowserStack/AWS) | `apps/SauceLabs-Sample.ipa` | [saucelabs/sample-app-mobile](https://github.com/saucelabs/sample-app-mobile) |

```bash
./scripts/download-test-apps.sh
```

## Execution matrix

| ENV | Command / notes |
| --- | --- |
| `local` | `./scripts/download-test-apps.sh` then `./gradlew test -DENV=local` with local Appium (`appium.auto.start=true` in `local.properties`) |
| `browserstack` | Upload APK/IPA → set `BROWSERSTACK_ANDROID_APP` / `BROWSERSTACK_IOS_APP` (or `bs://` in `browserstack.properties`) → BrowserStack workflow |
| `aws` | `config/aws.properties` + Device Farm `create-upload` / `schedule-run` (see comments in file) |

## Sibling demos (do not confuse scope)

| Repo | Role |
| --- | --- |
| [`appium-mobile-automation-framework`](https://github.com/Harp-Andres/appium-mobile-automation-framework) | Local-only Appium |
| [`demo-serenity-screenplay-mobile`](https://github.com/Harp-Andres/demo-serenity-screenplay-mobile) | Serenity Screenplay teaching + Docker hub |
