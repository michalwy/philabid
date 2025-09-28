#!/bin/bash

# Development run script for Philabid
# This script runs the application in development mode with proper JVM arguments

echo "Starting Philabid in development mode..."

# Set JVM arguments for JavaFX
export JAVAFX_ARGS="--add-modules javafx.controls,javafx.fxml --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED"

# Run with Gradle
./gradlew runApp

echo "Philabid application stopped."