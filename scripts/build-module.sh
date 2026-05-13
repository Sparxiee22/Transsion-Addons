#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

if [[ -z "${JAVA_HOME:-}" && -d "/usr/lib/jvm/java-21-openjdk-amd64" ]]; then
    export JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"
fi

if [[ -n "${GRADLE_BIN:-}" ]]; then
    "$GRADLE_BIN" assembleDebug
elif [[ -x "./gradlew" ]]; then
    ./gradlew assembleDebug
else
    gradle assembleDebug
fi

APK="app/build/outputs/apk/debug/app-debug.apk"
OUT_DIR="dist/transsionaddons-system-module"
ZIP_FILE="dist/TranssionAddons-system-module.zip"

rm -rf "$OUT_DIR" "$ZIP_FILE"
mkdir -p "$OUT_DIR/system/priv-app/TranssionAddons"
cp -a module/. "$OUT_DIR/"
cp "$APK" "$OUT_DIR/system/priv-app/TranssionAddons/TranssionAddons.apk"
chmod 0755 "$OUT_DIR/action.sh" "$OUT_DIR/service.sh" "$OUT_DIR/customize.sh"

(
    cd "$OUT_DIR"
    zip -qr "../TranssionAddons-system-module.zip" .
)

echo "Built $ZIP_FILE"
