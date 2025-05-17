$Root = Split-Path -Parent $MyInvocation.MyCommand.Path

$services = @(
    "auth-service",
    "EventManager",
    "notification-service",
    "gateway-service"
)
$gatewayDir = "gateway-service"
$frontendDir = "event-manager-frontend"

Write-Host "=== Building all microservices ==="
foreach ($svc in $services) {
    $svcPath = Join-Path $Root $svc
    if (Test-Path $svcPath) {
        Write-Host "`nBuilding $svc..." -ForegroundColor Cyan
        Push-Location $svcPath
        mvn clean package -DskipTests
        Pop-Location
    } else {
        Write-Host "`nService folder not found: $svcPath" -ForegroundColor Red
    }
}

Write-Host "`n=== Starting Docker Compose for Gateway in a new terminal window ===" -ForegroundColor Cyan
$gwPath = Join-Path $Root $gatewayDir
if (Test-Path $gwPath) {
    $command = "cd `"$gwPath`"; docker compose up --build"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command
} else {
    Write-Host "Gateway folder not found: $gwPath" -ForegroundColor Red
}

Write-Host "`n=== Setting up Frontend ===" -ForegroundColor Cyan
$fePath = Join-Path $Root $frontendDir
if (Test-Path $fePath) {
    Push-Location $fePath
    npm install
    npm start
    Pop-Location
} else {
    Write-Host "Frontend folder not found: $fePath" -ForegroundColor Red
}
