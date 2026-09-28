#!/bin/sh
set -eu
PROJECT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$PROJECT_DIR"
exec java -jar "$PROJECT_DIR"/build/libs/enterprise-hr-0.0.1-SNAPSHOT.jar
