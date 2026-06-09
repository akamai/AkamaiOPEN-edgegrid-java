#!/bin/sh
set -e
REPO_ROOT=$(git rev-parse --show-toplevel)
IMAGE="edgegrid-java-test:$(uuidgen | tr '[:upper:]' '[:lower:]')"

echo "Building docker image: $IMAGE in $REPO_ROOT/ci"
docker build -t "$IMAGE" "$REPO_ROOT/ci"

echo "Running Maven tests inside the container..."
set -x
docker run --rm -v "$REPO_ROOT":/testdir -w /testdir "$IMAGE" \
    mvn -B clean install -Ddependency-check.skip=true
