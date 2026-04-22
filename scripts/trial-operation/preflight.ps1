param(
    [switch]$CheckPorts,
    [switch]$StrictPorts,
    [switch]$RunBackendCompile,
    [switch]$RunFrontendBuild,
    [switch]$RunSampleValidation
)

$ErrorActionPreference = "Stop"
$script:HardFailures = 0
$script:Warnings = 0

function Write-Check {
    param(
        [string]$Name,
        [string]$Status,
        [string]$Message,
        [bool]$Hard = $false
    )

    Write-Host "[$Status] $Name - $Message"

    if ($Status -eq "FAIL") {
        if ($Hard) {
            $script:HardFailures += 1
        } else {
            $script:Warnings += 1
        }
    } elseif ($Status -eq "WARN") {
        $script:Warnings += 1
    }
}

function Test-RepoFile {
    param([string]$RelativePath)

    $path = Join-Path $RepoRoot $RelativePath
    if (Test-Path -LiteralPath $path) {
        Write-Check $RelativePath "OK" "found"
    } else {
        Write-Check $RelativePath "FAIL" "missing required file" $true
    }
}

function Test-TcpPort {
    param([int]$Port)

    $client = New-Object System.Net.Sockets.TcpClient
    try {
        $async = $client.BeginConnect("127.0.0.1", $Port, $null, $null)
        $connected = $async.AsyncWaitHandle.WaitOne(1000, $false)
        if ($connected -and $client.Connected) {
            $client.EndConnect($async)
            return $true
        }
        return $false
    } catch {
        return $false
    } finally {
        $client.Close()
    }
}

function Invoke-Step {
    param(
        [string]$Name,
        [string]$WorkingDirectory,
        [string]$Command,
        [string[]]$Arguments,
        [int[]]$WarningExitCodes = @()
    )

    Push-Location $WorkingDirectory
    try {
        & $Command @Arguments
        if ($LASTEXITCODE -eq 0) {
            Write-Check $Name "OK" "command completed"
        } elseif ($WarningExitCodes -contains $LASTEXITCODE) {
            Write-Check $Name "WARN" "command completed with warnings, exit code $LASTEXITCODE"
        } else {
            Write-Check $Name "FAIL" "command exited with code $LASTEXITCODE" $true
        }
    } finally {
        Pop-Location
    }
}

$RepoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
Write-Host "Campus relay trial-operation preflight"
Write-Host "Repo: $RepoRoot"

$requiredFiles = @(
    "backend\mvnw.cmd",
    "backend\src\main\resources\application-test.properties",
    "backend\src\main\resources\db\schema-h2.sql",
    "backend\src\main\resources\db\data-h2.sql",
    "frontend\package.json",
    "docs\trial-operation-preflight.md",
    "docs\simulated-funds-boundary.md",
    "project-logs\campus-relay\summary.md",
    "project-logs\campus-relay\pending-items.md"
)

foreach ($file in $requiredFiles) {
    Test-RepoFile $file
}

$envLocal = Join-Path $RepoRoot "frontend\.env.local"
if (Test-Path -LiteralPath $envLocal) {
    $content = Get-Content -LiteralPath $envLocal -Raw
    if ($content -match "(?m)^\s*VITE_TENCENT_MAP_KEY\s*=") {
        Write-Check "frontend/.env.local" "OK" "Tencent map key variable exists; value is not printed"
    } else {
        Write-Check "frontend/.env.local" "WARN" "file exists but VITE_TENCENT_MAP_KEY is not set"
    }
} else {
    Write-Check "frontend/.env.local" "WARN" "missing; map preview can show key-missing state"
}

$trackedEnv = & git -C $RepoRoot ls-files "frontend/.env.local"
if ($trackedEnv) {
    Write-Check "frontend/.env.local tracking" "FAIL" "local env file is tracked by git" $true
} else {
    Write-Check "frontend/.env.local tracking" "OK" "local env file is not tracked"
}

if ($CheckPorts) {
    foreach ($port in @(8080, 5173)) {
        $reachable = Test-TcpPort $port
        if ($reachable) {
            Write-Check "127.0.0.1:$port" "OK" "port is reachable"
        } elseif ($StrictPorts) {
            Write-Check "127.0.0.1:$port" "FAIL" "port is not reachable" $true
        } else {
            Write-Check "127.0.0.1:$port" "WARN" "port is not reachable; start backend/frontend before browser validation"
        }
    }
}

if ($RunBackendCompile) {
    Invoke-Step "backend compile" (Join-Path $RepoRoot "backend") ".\mvnw.cmd" @("-DskipTests", "compile")
}

if ($RunFrontendBuild) {
    Invoke-Step "frontend build" (Join-Path $RepoRoot "frontend") "npm" @("run", "build")
}

if ($RunSampleValidation) {
    Invoke-Step "sample validation" $RepoRoot "powershell" @("-ExecutionPolicy", "Bypass", "-File", (Join-Path $RepoRoot "scripts\trial-operation\validate-samples.ps1")) @(2)
}

Write-Host "Preflight completed with $script:HardFailures hard failure(s) and $script:Warnings warning(s)."

if ($script:HardFailures -gt 0) {
    exit 1
}

exit 0
