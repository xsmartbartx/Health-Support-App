#!/usr/bin/env bash
set -euo pipefail

# Minimal gradle wrapper using docker to run Gradle inside a container
# Usage: ./gradlew <gradle-args>
DOCKER_IMAGE=gradle:8-jdk17
PROJECT_DIR="$(pwd)/services/service-a"
docker run --rm -v "$PROJECT_DIR":/home/gradle/project -w /home/gradle/project $DOCKER_IMAGE gradle "$@"