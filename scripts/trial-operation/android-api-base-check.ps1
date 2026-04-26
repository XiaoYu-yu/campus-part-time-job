param(
    [switch]$Strict
)

$ErrorActionPreference = "Stop"
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
$frontendRoot = Join-Path $repoRoot "frontend"
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

function Read-EnvValue {
    param(
        [string]$Path,
        [string]$Key
    )

    if (-not (Test-Path -LiteralPath $Path)) {
        return $null
    }

    $line = Get-Content -LiteralPath $Path | Where-Object { $_ -match "^\s*$([regex]::Escape($Key))\s*=" } | Select-Object -Last 1
    if (-not $line) {
        return ""
    }

    return ($line -replace "^\s*$([regex]::Escape($Key))\s*=\s*", "").Trim()
}

function Classify-ApiBase {
    param([string]$Value)

    if (-not $Value) {
        return "missing"
    }
    if ($Value -eq "/api") {
        return "relative-proxy"
    }
    if ($Value -match "^http://10\.0\.2\.2(:\d+)?/api/?$") {
        return "android-emulator"
    }
    if ($Value -match "^http://(192\.168\.|10\.|172\.(1[6-9]|2\d|3[01])\.)") {
        return "lan-phone"
    }
    if ($Value -match "^https://") {
        return "public-https"
    }
    if ($Value -match "^http://") {
        return "public-or-lan-http"
    }
    return "unknown"
}

function Test-EnvFile {
    param(
        [string]$FileName,
        [string]$ExpectedClass,
        [bool]$Required
    )

    $path = Join-Path $frontendRoot $FileName
    if (-not (Test-Path -LiteralPath $path)) {
        if ($Required -or $Strict) {
            Write-Check $FileName "FAIL" "missing env file" $true
        } else {
            Write-Check $FileName "WARN" "missing local env file; copy the matching .example before this build mode is used"
        }
        return
    }

    $apiBase = Read-EnvValue $path "VITE_API_BASE_URL"
    $class = Classify-ApiBase $apiBase
    if ($class -eq $ExpectedClass) {
        Write-Check $FileName "OK" "VITE_API_BASE_URL classified as $class"
        return
    }

    if ($ExpectedClass -eq "public-https" -and $class -eq "public-or-lan-http") {
        Write-Check $FileName "WARN" "uses HTTP; acceptable only for temporary internal smoke, prefer HTTPS for public trial"
        return
    }

    $message = "expected $ExpectedClass, got $class"
    if ($Required -or $Strict) {
        Write-Check $FileName "FAIL" $message $true
    } else {
        Write-Check $FileName "WARN" $message
    }
}

Write-Host "Android API base check"
Write-Host "Repo: $repoRoot"

Test-EnvFile ".env.android-user" "android-emulator" $true
Test-EnvFile ".env.android-parttime" "android-emulator" $true
Test-EnvFile ".env.android-user-emulator" "android-emulator" $true
Test-EnvFile ".env.android-parttime-emulator" "android-emulator" $true
Test-EnvFile ".env.android-user-lan" "lan-phone" $false
Test-EnvFile ".env.android-parttime-lan" "lan-phone" $false
Test-EnvFile ".env.android-user-public" "public-https" $false
Test-EnvFile ".env.android-parttime-public" "public-https" $false

Write-Host "Android API base check completed with $script:HardFailures hard failure(s) and $script:Warnings warning(s)."

if ($script:HardFailures -gt 0) {
    exit 1
}

if ($script:Warnings -gt 0) {
    exit 2
}

exit 0
