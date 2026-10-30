#!/usr/bin/env bash
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "$DIR"

echo "========================================================"
echo "                SaveBite Marketplace"
echo "      Solving Food Waste with 23 Design Patterns"
echo "========================================================"

if ! command -v java &> /dev/null; then
    echo "[ERROR] java could not be found. Please install Java 17+."
    exit 1
fi

if [ ! -f "target/savebite.jar" ]; then
    echo "[INFO] target/savebite.jar not found. Building project with Maven..."
    if [ -f "./mvnw" ]; then
        ./mvnw clean package -DskipTests=true
    else
        mvn clean package -DskipTests=true
    fi
fi

echo "[INFO] Starting SaveBite at http://localhost:7070 ..."
echo "[INFO] Press Ctrl+C to stop the server."
java -jar target/savebite.jar
