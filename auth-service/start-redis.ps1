# start-redis.ps1
# PowerShell helper to start Redis (via docker-compose) and wait until healthy.

Param()

function Abort($msg) {
    Write-Host "ERROR: $msg" -ForegroundColor Red
    exit 1
}

# Check Docker CLI
try {
    docker version > $null 2>&1
} catch {
    Abort "Docker CLI not available. Make sure Docker Desktop / Engine is installed and running."
}

Write-Host "Starting Redis using docker-compose..."

# Run docker-compose up -d
$composeCmd = "docker-compose up -d"
$pwd = Get-Location
Push-Location $pwd
$rc = cmd /c $composeCmd
Write-Host $rc

# Wait for container to appear
$timeout = 60
$elapsed = 0
while ($elapsed -lt $timeout) {
    $container = docker ps -a --filter "name=auth-redis" --format "{{.Names}}|{{.Status}}" 2>$null
    if ($container) { break }
    Start-Sleep -Seconds 1
    $elapsed += 1
}

if (-not $container) {
    Abort "Redis container not found after starting docker-compose. Check docker-compose.yml and docker logs."
}

Write-Host "Found container: $container"

# Wait for health (if healthcheck configured)
$elapsed = 0
$healthy = $false
while ($elapsed -lt 60) {
    $status = docker inspect --format '{{range .State.Health}}{{.Status}}{{end}}' auth-redis 2>$null
    if ($status -eq 'healthy') { $healthy = $true; break }
    if ($status -eq '') {
        # no healthcheck; check logs for Ready message or use redis-cli inside container
        $logs = docker logs --tail 20 auth-redis 2>$null
        if ($logs -match 'Ready to accept connections' -or $logs -match 'Ready to accept connections') { $healthy = $true; break }
    }
    Start-Sleep -Seconds 1
    $elapsed += 1
}

if (-not $healthy) {
    Write-Host "Warning: Redis container did not report healthy within timeout. Showing last 100 lines of logs:" -ForegroundColor Yellow
    docker logs --tail 100 auth-redis
} else {
    Write-Host "Redis container is healthy." -ForegroundColor Green
}

# Ping Redis using redis-cli inside container
try {
    $pong = docker exec auth-redis redis-cli ping 2>$null
    if ($pong -match 'PONG') {
        Write-Host "redis-cli PING -> PONG" -ForegroundColor Green
        Write-Host "Redis is ready and accessible at localhost:6379"
    } else {
        Write-Host "redis-cli ping did not return PONG. Output:" -ForegroundColor Yellow
        Write-Host $pong
    }
} catch {
    Write-Host "Could not run redis-cli inside container. Ensure docker is running and container is accessible." -ForegroundColor Yellow
}

Pop-Location

