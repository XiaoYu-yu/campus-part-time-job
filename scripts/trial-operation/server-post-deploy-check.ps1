param(
    [Parameter(Mandatory = $true)]
    [string]$ApiBase,

    [string]$FrontendBase = '',
    [string]$RuntimeDir = '',
    [string]$SshHost = '',
    [string]$SshUser = 'root',
    [string]$SshIdentity = '',
    [int]$TimeoutSec = 15,
    [switch]$SkipRemoteSmoke,
    [switch]$AllowFailures
)

$ErrorActionPreference = 'Stop'

$RepoRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..')
if (-not $RuntimeDir) {
    $RuntimeDir = Join-Path $RepoRoot 'project-logs\campus-relay\runtime\server-post-deploy-check'
}
New-Item -ItemType Directory -Force -Path $RuntimeDir | Out-Null

$Results = @()

function Add-Result {
    param(
        [string]$Name,
        [string]$Status,
        [string]$Message,
        [object]$Details = $null
    )

    $script:Results += [pscustomobject]@{
        name = $Name
        status = $Status
        message = $Message
        details = $Details
    }
    Write-Host "[$Status] $Name - $Message"
}

function Redact-Url {
    param([string]$Url)

    if (-not $Url) {
        return ''
    }

    try {
        $uri = [System.Uri]$Url
        $port = ''
        if (-not $uri.IsDefaultPort) {
            $port = ":$($uri.Port)"
        }
        return "$($uri.Scheme)://<redacted>$port$($uri.PathAndQuery)"
    } catch {
        return '<redacted-url>'
    }
}

function Invoke-CheckedCommand {
    param(
        [string]$Name,
        [string]$FilePath,
        [string[]]$Arguments
    )

    $output = & $FilePath @Arguments 2>&1 | ForEach-Object { $_.ToString() }
    $exit = $LASTEXITCODE

    return [pscustomobject]@{
        name = $Name
        exitCode = $exit
        output = $output
    }
}

Write-Host "Campus internal-trial server post-deploy check"
Write-Host "Repo: $RepoRoot"
Write-Host "RuntimeDir: $RuntimeDir"
Write-Host ""

$remoteSmokeOutput = Join-Path $RuntimeDir 'remote-smoke-report.json'
if ($SkipRemoteSmoke) {
    Add-Result -Name 'remote smoke' -Status 'SKIP' -Message 'Skipped by parameter.'
} else {
    $remoteArgs = @(
        '-ExecutionPolicy', 'Bypass',
        '-File', (Join-Path $PSScriptRoot 'remote-smoke.ps1'),
        '-ApiBase', $ApiBase,
        '-OutputPath', $remoteSmokeOutput,
        '-TimeoutSec', $TimeoutSec
    )
    if ($FrontendBase) {
        $remoteArgs += @('-FrontendBase', $FrontendBase)
    }
    if ($AllowFailures) {
        $remoteArgs += '-AllowFailures'
    }

    $remote = Invoke-CheckedCommand -Name 'remote smoke' -FilePath 'powershell' -Arguments $remoteArgs
    if ($remote.exitCode -eq 0) {
        Add-Result -Name 'remote smoke' -Status 'PASS' -Message "Report written to $remoteSmokeOutput"
    } else {
        Add-Result -Name 'remote smoke' -Status 'FAIL' -Message "remote-smoke.ps1 exited $($remote.exitCode)" -Details $remote.output
    }
}

if ($SshHost) {
    $remoteCommand = @'
set -eu
cd /opt/campus-part-time-job
echo "commit=$(git rev-parse --short HEAD)"
for container in campus-trial-backend-1 campus-trial-frontend-1 campus-trial-mysql-1; do
  echo "$container=$(docker inspect --format='{{json .HostConfig.LogConfig}}' "$container")"
done
'@

    $sshArgs = @()
    if ($SshIdentity) {
        $sshArgs += @('-i', $SshIdentity, '-o', 'IdentitiesOnly=yes')
    }
    $sshArgs += @(
        '-o', 'BatchMode=yes',
        '-o', "ConnectTimeout=$TimeoutSec",
        "$SshUser@$SshHost",
        $remoteCommand
    )

    $ssh = Invoke-CheckedCommand -Name 'ssh deployment checks' -FilePath 'ssh' -Arguments $sshArgs
    $joined = ($ssh.output -join "`n")
    $hasLogConfig = $joined -match 'json-file' -and $joined -match 'max-size' -and $joined -match 'max-file'

    if ($ssh.exitCode -eq 0 -and $hasLogConfig) {
        Add-Result -Name 'ssh deployment checks' -Status 'PASS' -Message 'Key SSH, commit read, and container LogConfig checks passed.' -Details $ssh.output
    } elseif ($ssh.exitCode -eq 0) {
        Add-Result -Name 'ssh deployment checks' -Status 'FAIL' -Message 'SSH succeeded but LogConfig output did not include expected fields.' -Details $ssh.output
    } else {
        Add-Result -Name 'ssh deployment checks' -Status 'FAIL' -Message "ssh exited $($ssh.exitCode)" -Details $ssh.output
    }
} else {
    Add-Result -Name 'ssh deployment checks' -Status 'SKIP' -Message 'No SshHost provided.'
}

$pass = ($Results | Where-Object { $_.status -eq 'PASS' }).Count
$fail = ($Results | Where-Object { $_.status -eq 'FAIL' }).Count
$skip = ($Results | Where-Object { $_.status -eq 'SKIP' }).Count

$report = [pscustomobject]@{
    generatedAt = (Get-Date).ToString('s')
    apiBase = Redact-Url $ApiBase
    frontendBase = Redact-Url $FrontendBase
    sshHost = $(if ($SshHost) { '<redacted-host>' } else { '' })
    urlsRedacted = $true
    pass = $pass
    fail = $fail
    skip = $skip
    results = $Results
}

$reportPath = Join-Path $RuntimeDir 'server-post-deploy-check-report.json'
$report | ConvertTo-Json -Depth 8 | Set-Content -Path $reportPath -Encoding UTF8
Write-Host ""
Write-Host "Report: $reportPath"
Write-Host "Summary: PASS=$pass FAIL=$fail SKIP=$skip"

if ($fail -gt 0 -and -not $AllowFailures) {
    exit 1
}

