#!/bin/sh
set -eu
PROJECT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$PROJECT_DIR"
JAR="$PROJECT_DIR/build/libs/enterprise-hr-0.0.1-SNAPSHOT.jar"
if [ ! -f "$JAR" ]; then
  ./gradlew bootJar --no-daemon
fi
exec java -jar "$JAR"
