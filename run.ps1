# Run the Spring Boot backend with JDK 21.
# Usage: .\run.ps1
# Requires .env in the project root OR environment variables already set.

$jdk21 = "C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot"
if (-not (Test-Path $jdk21)) {
    Write-Error "JDK 21 not found at $jdk21. Install Eclipse Temurin 21 from https://adoptium.net"
    exit 1
}

$env:JAVA_HOME = $jdk21
$env:PATH = "$jdk21\bin;$env:PATH"

Write-Host "Using Java: $(java -version 2>&1 | Select-Object -First 1)"

# Load .env from backend folder if present
$envFile = Join-Path $PSScriptRoot ".env"
if (Test-Path $envFile) {
    Get-Content $envFile | Where-Object { $_ -match "^\s*[^#]\S+=\S" } | ForEach-Object {
        $parts = $_ -split "=", 2
        [System.Environment]::SetEnvironmentVariable($parts[0].Trim(), $parts[1].Trim(), "Process")
        Write-Host "Loaded $($parts[0].Trim()) from .env"
    }
}

if (-not $env:DATABASE_URL) {
    Write-Error "DATABASE_URL is not set. Copy .env.example to .env and fill in your Neon connection string."
    exit 1
}
if (-not $env:JWT_SECRET) {
    Write-Error "JWT_SECRET is not set. Copy .env.example to .env and fill in your secret."
    exit 1
}

# Parse DATABASE_URL (postgresql://user:pass@host/db?params) into separate Spring Boot properties.
# Spring Boot uses spring.datasource.url, .username, .password — keeping credentials out of the URL
# avoids JDBC driver parsing issues with special characters.
$rawUrl = $env:DATABASE_URL -replace '^jdbc:', ''   # normalize: strip any existing jdbc: prefix
if ($rawUrl -match "^postgresql://([^:@]+):([^@]+)@([^/?]+)/([^?]*)(.*)$") {
    $dbUser  = $Matches[1]
    $dbPass  = $Matches[2]
    $dbHost  = $Matches[3]
    $dbName  = $Matches[4]
    $dbExtra = $Matches[5] -replace '^\?', ''   # strip leading '?'

    $jdbcUrl = "jdbc:postgresql://$dbHost/$dbName"
    if ($dbExtra) { $jdbcUrl += "?$dbExtra" }

    $env:DB_URL      = $jdbcUrl
    $env:DB_USER     = $dbUser
    $env:DB_PASSWORD = $dbPass
    Write-Host "DB_URL      = $jdbcUrl"
    Write-Host "DB_USER     = $dbUser"
    Write-Host "DB_PASSWORD = (set)"
} else {
    Write-Error "DATABASE_URL format not recognized. Expected: postgresql://user:password@host/database?sslmode=require"
    exit 1
}

mvn spring-boot:run
