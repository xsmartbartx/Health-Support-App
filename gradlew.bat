@echo off
REM Minimal gradle-wrapper for Windows using Docker
docker run --rm -v "%cd%\services\service-a":/home/gradle/project -w /home/gradle/project gradle:8-jdk17 gradle %*
