# App under test + multi-farm execution (cloud repo)

This repo’s specialty is **running the same Appium suite on different farms**.

## Free apps

| Platform | Artifact | Source |
| --- | --- | --- |
| Android | `apps/TheApp.apk` | [appium-pro/TheApp](https://github.com/appium-pro/TheApp) |
| iOS Simulator | `apps/TheApp.app.zip` | same |
| iOS **real device** (BrowserStack/AWS) | `apps/SauceLabs-Sample.ipa` | [saucelabs/sample-app-mobile](https://github.com/saucelabs/sample-app-mobile) |
| Android widgets (optional) | `ApiDemos-debug.apk` (repo root) | existing local fallback |

```bash
./scripts/download-test-apps.sh
```

## Execution matrix

| ENV | Command / notes |
| --- | --- |
| `local` | Keep working: `./gradlew test` (unit) / Cucumber with local Appium + ApiDemos or TheApp path |
| `browserstack` | Upload APK/IPA → set `BROWSERSTACK_*` secrets → existing BrowserStack workflow |
| `aws` | Use `config/aws.properties` + Device Farm `create-upload` / `schedule-run` (see AWS CLI) |

Local sibling (do not break):  
[`appium-mobile-automation-framework`](https://github.com/Harp-Andres/appium-mobile-automation-framework)

Serenity Screenplay + Docker Appium hub:  
[`demo-serenity-screenplay-mobile`](https://github.com/Harp-Andres/demo-serenity-screenplay-mobile)
