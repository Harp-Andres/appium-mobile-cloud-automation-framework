#!/usr/bin/env bash
# Downloads free apps for local fallback + BrowserStack/AWS upload.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
OUT="${ROOT}/apps"
mkdir -p "$OUT"
THEAPP_VERSION="${THEAPP_VERSION:-v1.12.0}"
SAUCE_VERSION="${SAUCE_VERSION:-2.7.1}"

dl() {
  local url="$1" dest="$2"
  if [[ -f "$dest" ]]; then echo "OK (cached): $dest"; return; fi
  echo "Downloading $(basename "$dest")..."
  curl -fL --retry 3 -o "$dest.partial" "$url"
  mv "$dest.partial" "$dest"
  echo "OK: $dest"
}

dl "https://github.com/appium-pro/TheApp/releases/download/${THEAPP_VERSION}/TheApp.apk" "${OUT}/TheApp.apk"
dl "https://github.com/appium-pro/TheApp/releases/download/${THEAPP_VERSION}/TheApp.app.zip" "${OUT}/TheApp.app.zip"

if [[ "${WITH_SAUCE_IOS_IPA:-1}" == "1" ]]; then
  dl "https://github.com/saucelabs/sample-app-mobile/releases/download/${SAUCE_VERSION}/iOS.RealDevice.SauceLabs.Mobile.Sample.app.${SAUCE_VERSION}.ipa" \
    "${OUT}/SauceLabs-Sample.ipa"
fi

cat <<EOF

Upload to BrowserStack (Android TheApp):
  curl -u "\$BROWSERSTACK_USERNAME:\$BROWSERSTACK_ACCESS_KEY" \\
    -X POST "https://api-cloud.browserstack.com/app-automate/upload" \\
    -F "file=@${OUT}/TheApp.apk" -F "custom_id=TheApp"

Upload iOS real device (Sauce Sample IPA):
  curl -u "\$BROWSERSTACK_USERNAME:\$BROWSERSTACK_ACCESS_KEY" \\
    -X POST "https://api-cloud.browserstack.com/app-automate/upload" \\
    -F "file=@${OUT}/SauceLabs-Sample.ipa" -F "custom_id=SauceSampleiOS"

Then set android.app / ios.app to the returned bs:// URL (or use custom_id).
EOF
