#!/bin/sh
set -e

APP_HOME=$(cd "${0%/*}" && pwd -P)
export GRADLE_USER_HOME="${APP_HOME}/.gradle-home"
GRADLE_VERSION=8.11.1
DIST_DIR="${GRADLE_USER_HOME}/codex-wrapper/gradle-${GRADLE_VERSION}-bin"
GRADLE_BIN="${DIST_DIR}/gradle-${GRADLE_VERSION}/bin/gradle"
ZIP_FILE="${DIST_DIR}/gradle-${GRADLE_VERSION}-bin.zip"

if [ ! -x "$GRADLE_BIN" ]; then
  mkdir -p "$DIST_DIR"
  if command -v curl >/dev/null 2>&1; then
    curl -L "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o "$ZIP_FILE"
  elif command -v wget >/dev/null 2>&1; then
    wget "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -O "$ZIP_FILE"
  else
    echo "curl or wget is required to download Gradle ${GRADLE_VERSION}." >&2
    exit 1
  fi
  unzip -q -o "$ZIP_FILE" -d "$DIST_DIR"
fi

exec "$GRADLE_BIN" -p "$APP_HOME" "$@"
