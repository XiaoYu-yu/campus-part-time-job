param(
    [Parameter(Mandatory = $true)]
    [string]$SshHost,

    [string]$SshUser = 'root',
    [string]$SshIdentity = '',
    [string]$RuntimeDir = '',
    [int]$TimeoutSec = 15,
    [switch]$AllowFailures
)

$ErrorActionPreference = 'Stop'

$RepoRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..')
if (-not $RuntimeDir) {
    $RuntimeDir = Join-Path $RepoRoot 'project-logs\campus-relay\runtime\server-ops-health'
}
New-Item -ItemType Directory -Force -Path $RuntimeDir | Out-Null

function Invoke-CheckedCommand {
    param(
        [string]$FilePath,
        [string[]]$Arguments
    )

    $output = & $FilePath @Arguments 2>&1 | ForEach-Object { $_.ToString() }
    return [pscustomobject]@{
        exitCode = $LASTEXITCODE
        output = $output
    }
}

$remoteCommand = @'
set -u
echo "timestamp=$(date -Is)"
echo "hostname=$(hostname)"
if [ -d /opt/campus-part-time-job/.git ]; then
  echo "deploy_commit=$(git -C /opt/campus-part-time-job rev-parse --short HEAD)"
fi
echo "__SECTION__ df"
df -h / /opt 2>/dev/null || df -h /
echo "__SECTION__ docker_system_df"
docker system df 2>/dev/null || true
echo "__SECTION__ compose_ps"
cd /opt/campus-part-time-job 2>/dev/null && docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml ps || true
echo "__SECTION__ container_log_sizes"
for container in campus-trial-backend-1 campus-trial-frontend-1 campus-trial-mysql-1; do
  log_path=$(docker inspect --format='{{.LogPath}}' "$container" 2>/dev/null || true)
  log_config=$(docker inspect --format='{{json .HostConfig.LogConfig}}' "$container" 2>/dev/null || true)
  if [ -n "$log_path" ] && [ -f "$log_path" ]; then
    log_size=$(du -h "$log_path" | awk '{print $1}')
  else
    log_size="missing"
  fi
  echo "$container log_size=$log_size log_config=$log_config"
done
echo "__SECTION__ backup_usage"
backup_dir="/opt/campus-part-time-job/deploy/internal-trial/backups"
if [ -d "$backup_dir" ]; then
  du -sh "$backup_dir" 2>/dev/null || true
  find "$backup_dir" -maxdepth 1 -mindepth 1 -type d -printf '%f\n' 2>/dev/null | sort | tail -5
else
  echo "backup_dir_missing=$backup_dir"
fi
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

Write-Host "Campus internal-trial server ops health"
Write-Host "Repo: $RepoRoot"
Write-Host "RuntimeDir: $RuntimeDir"
Write-Host ""

$ssh = Invoke-CheckedCommand -FilePath 'ssh' -Arguments $sshArgs
$rawPath = Join-Path $RuntimeDir 'server-ops-health.raw.txt'
$ssh.output | Set-Content -Path $rawPath -Encoding UTF8

$joined = $ssh.output -join "`n"
$hasDf = $joined -match '__SECTION__ df'
$hasDocker = $joined -match '__SECTION__ docker_system_df'
$hasLogs = $joined -match '__SECTION__ container_log_sizes'
$hasBackups = $joined -match '__SECTION__ backup_usage'

$status = 'PASS'
$messages = @()
if ($ssh.exitCode -ne 0) {
    $status = 'FAIL'
    $messages += "ssh exited $($ssh.exitCode)"
}
if (-not $hasDf) {
    $status = 'FAIL'
    $messages += 'df section missing'
}
if (-not $hasDocker) {
    $status = 'FAIL'
    $messages += 'docker system df section missing'
}
if (-not $hasLogs) {
    $status = 'FAIL'
    $messages += 'container log size section missing'
}
if (-not $hasBackups) {
    $status = 'FAIL'
    $messages += 'backup usage section missing'
}
if (-not $messages) {
    $messages += 'read-only server ops health checks completed'
}

$report = [pscustomobject]@{
    generatedAt = (Get-Date).ToString('s')
    sshHost = '<redacted-host>'
    hostRedacted = $true
    status = $status
    message = ($messages -join '; ')
    rawOutputPath = 'server-ops-health.raw.txt'
    checks = [pscustomobject]@{
        sshExitCode = $ssh.exitCode
        dfSection = $hasDf
        dockerSystemDfSection = $hasDocker
        containerLogSizeSection = $hasLogs
        backupUsageSection = $hasBackups
    }
}

$reportPath = Join-Path $RuntimeDir 'server-ops-health-report.json'
$report | ConvertTo-Json -Depth 8 | Set-Content -Path $reportPath -Encoding UTF8

Write-Host "Raw output: $rawPath"
Write-Host "Report: $reportPath"
Write-Host "Status: $status - $($messages -join '; ')"

if ($status -eq 'FAIL' -and -not $AllowFailures) {
    exit 1
}

