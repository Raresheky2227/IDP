# Ensure Docker Desktop is running and Docker engine is available (if not on auto start on host machine)
function Start-DockerDesktop {
    $dockerProcess = Get-Process -Name "Docker Desktop" -ErrorAction SilentlyContinue
    if (-not $dockerProcess) {
        Write-Host "Starting Docker Desktop..." -ForegroundColor Cyan
        Start-Process "Docker Desktop"
        Start-Sleep -Seconds 5
    } else {
        Write-Host "Docker Desktop is already running." -ForegroundColor Green
    }

    # Wait for Docker Engine to become available (must be)
    $maxAttempts = 30
    $attempt = 0
    while ($attempt -lt $maxAttempts) {
        try {
            docker info | Out-Null
            Write-Host "Docker Engine is available." -ForegroundColor Green
            return
        } catch {
            Write-Host "Waiting for Docker Engine to start..." -ForegroundColor Yellow
            Start-Sleep -Seconds 3
            $attempt++
        }
    }
    throw "Docker Engine failed to start after $($maxAttempts * 3) seconds."
}

Start-DockerDesktop

# Compiling
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

# Starting up the containers in a different terminal
Write-Host "`n=== Starting Docker Compose for Gateway in a new terminal window ===" -ForegroundColor Cyan
$gwPath = Join-Path $Root $gatewayDir
if (Test-Path $gwPath) {
    $command = "cd `"$gwPath`"; docker compose up --build"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command
} else {
    Write-Host "Gateway folder not found: $gwPath" -ForegroundColor Red
}

# Start the front
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
