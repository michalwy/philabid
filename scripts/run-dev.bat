@echo off
REM Development run script for Philabid on Windows
REM This script runs the application in development mode

echo Starting Philabid in development mode...

REM Run with Gradle
gradlew.bat runApp

echo Philabid application stopped.
pause