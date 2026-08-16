@echo off
REM Minimal gradle-wrapper for Windows using Docker
docker run --rm -v "%cd%":/home/gradle/project -w /home/gradle/project gradle:8.4.1-jdk17 gradle %*