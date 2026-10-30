# SaveBite PowerShell Runner
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

Write-Host "========================================================" -ForegroundColor Green
Write-Host "                SaveBite Marketplace" -ForegroundColor Cyan
Write-Host "      Solving Food Waste with 23 Design Patterns" -ForegroundColor Yellow
Write-Host "========================================================" -ForegroundColor Green

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "[ERROR] Java is not installed or not in PATH. Please install JDK 17+." -ForegroundColor Red
    exit 1
}

if (-not (Test-Path "target\savebite.jar")) {
    Write-Host "[INFO] target\savebite.jar not found. Building project with Maven..." -ForegroundColor Cyan
    if (Test-Path "mvnw.cmd") {
        .\mvnw.cmd clean package -DskipTests=true
    } else {
        mvn clean package -DskipTests=true
    }
}

Write-Host "[INFO] Launching SaveBite at http://localhost:7070 ..." -ForegroundColor Green
Write-Host "[INFO] Press Ctrl+C to stop the server." -ForegroundColor Yellow
java -jar target\savebite.jar

