#!/usr/bin/env bash
set -euo pipefail

# Minimal gradle wrapper using docker to run Gradle inside a container
# Usage: ./gradlew <gradle-args>
DOCKER_IMAGE=gradle:8.4.1-jdk17
docker run --rm -v "$(pwd)":/home/gradle/project -w /home/gradle/project $DOCKER_IMAGE gradle "$@"