@echo off
setlocal
cd /d "%~dp0"

echo ========================================================
echo                 SaveBite Marketplace
echo       Solving Food Waste with 23 Design Patterns
echo ========================================================

where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java 17+ not found in PATH.
    echo Please install JDK 17 or higher from https://adoptium.net
    pause
    exit /b 1
)

if not exist "target\savebite.jar" (
    echo [INFO] target\savebite.jar not found. Building project with Maven...
    if exist "mvnw.cmd" (
        call mvnw.cmd clean package -DskipTests=true
    ) else (
        call mvn clean package -DskipTests=true
    )
)

if not exist "target\savebite.jar" (
    echo [ERROR] Build failed. Please verify Maven and JDK 17+ installation.
    pause
    exit /b 1
)

echo [INFO] Starting SaveBite on http://localhost:7070 ...
echo [INFO] Press Ctrl+C to stop the server.
echo ========================================================
java -jar target\savebite.jar
pause
