param(
    [string]$UserApiBase = "",
    [string]$ParttimeApiBase = "",
    [string]$AdbPath = "",
    [string]$DeviceId = "",
    [switch]$StartEmulator,
    [string]$AvdName = "campus_api35",
    [int]$EmulatorBootTimeoutSeconds = 360,
    [switch]$ClearData,
    [int]$UserPort = 9222,
    [int]$ParttimePort = 9223,
    [int]$LaunchWaitSeconds = 12,
    [int]$WaitTimeoutSeconds = 90,
    [string]$OutputDirectory = ""
)

$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
if (-not $OutputDirectory) {
    $OutputDirectory = Join-Path $repoRoot "project-logs\campus-relay\runtime\step-124-android-public-webview-readiness"
}

function Add-OptionalArg {
    param(
        [System.Collections.Generic.List[string]]$Args,
        [string]$Name,
        [string]$Value
    )
    if ($Value) {
        $Args.Add($Name)
        $Args.Add($Value)
    }
}

function Invoke-SmokeScript {
    param(
        [string]$Name,
        [string]$ScriptPath,
        [string]$OutputPath,
        [string]$ScreenshotPath,
        [int]$Port,
        [string]$ApiBase
    )

    $argsList = [System.Collections.Generic.List[string]]::new()
    $argsList.Add("-NoProfile")
    $argsList.Add("-ExecutionPolicy")
    $argsList.Add("Bypass")
    $argsList.Add("-File")
    $argsList.Add($ScriptPath)
    Add-OptionalArg -Args $argsList -Name "-ApiBase" -Value $ApiBase
    Add-OptionalArg -Args $argsList -Name "-AdbPath" -Value $AdbPath
    Add-OptionalArg -Args $argsList -Name "-DeviceId" -Value $DeviceId
    Add-OptionalArg -Args $argsList -Name "-AvdName" -Value $AvdName
    $argsList.Add("-EmulatorBootTimeoutSeconds")
    $argsList.Add([string]$EmulatorBootTimeoutSeconds)
    $argsList.Add("-Port")
    $argsList.Add([string]$Port)
    $argsList.Add("-LaunchWaitSeconds")
    $argsList.Add([string]$LaunchWaitSeconds)
    $argsList.Add("-WaitTimeoutSeconds")
    $argsList.Add([string]$WaitTimeoutSeconds)
    $argsList.Add("-OutputPath")
    $argsList.Add($OutputPath)
    $argsList.Add("-ScreenshotPath")
    $argsList.Add($ScreenshotPath)
    if ($StartEmulator) { $argsList.Add("-StartEmulator") }
    if ($ClearData) { $argsList.Add("-ClearData") }

    Write-Host "Running $Name Android public WebView smoke..."
    $startedAt = Get-Date
    $previousErrorActionPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    try {
        $childOutput = & powershell.exe @argsList 2>&1
        $exitCode = $LASTEXITCODE
    } finally {
        $ErrorActionPreference = $previousErrorActionPreference
    }
    foreach ($line in $childOutput) {
        Write-Host $line
    }
    $finishedAt = Get-Date

    $report = $null
    if (Test-Path $OutputPath) {
        $report = Get-Content -LiteralPath $OutputPath -Raw | ConvertFrom-Json
    }

    return [ordered]@{
        name = $Name
        script = $ScriptPath
        exitCode = $exitCode
        passed = ($exitCode -eq 0 -and $report -and $report.summary -and [bool]$report.summary.passed)
        startedAt = $startedAt.ToString("s")
        finishedAt = $finishedAt.ToString("s")
        durationSeconds = [math]::Round(($finishedAt - $startedAt).TotalSeconds, 2)
        reportPath = $OutputPath
        screenshotPath = $ScreenshotPath
        summary = if ($report) { $report.summary } else { $null }
    }
}

New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null

$userScript = Join-Path $PSScriptRoot "android-webview-user-public-smoke.ps1"
$parttimeScript = Join-Path $PSScriptRoot "android-webview-parttime-public-smoke.ps1"

$userOutput = Join-Path $OutputDirectory "user-public-webview-smoke.json"
$userScreenshot = Join-Path $OutputDirectory "user-campus-orders-public-webview.png"
$parttimeOutput = Join-Path $OutputDirectory "parttime-public-webview-smoke.json"
$parttimeScreenshot = Join-Path $OutputDirectory "parttime-workbench-public-webview.png"

$results = @()
$results += Invoke-SmokeScript -Name "user" -ScriptPath $userScript -OutputPath $userOutput -ScreenshotPath $userScreenshot -Port $UserPort -ApiBase $UserApiBase
$results += Invoke-SmokeScript -Name "parttime" -ScriptPath $parttimeScript -OutputPath $parttimeOutput -ScreenshotPath $parttimeScreenshot -Port $ParttimePort -ApiBase $ParttimeApiBase

$passed = $true
foreach ($result in $results) {
    if (-not $result.passed) {
        $passed = $false
    }
}

$summaryPath = Join-Path $OutputDirectory "android-public-webview-readiness-summary.json"
$summary = [ordered]@{
    step = "Step 124"
    date = (Get-Date -Format "yyyy-MM-dd")
    purpose = "Repeatable Android public WebView readiness smoke for user and parttime apps"
    passed = $passed
    outputDirectory = $OutputDirectory
    checks = $results
    notes = @(
        "Child reports redact the real public API base.",
        "The smoke drives Android WebView through DevTools/CDP instead of host-only curl.",
        "This wrapper does not change bridge behavior, token attachment logic, routes, backend state machine, or old takeaway modules.",
        "HTTP public API is acceptable for owner-controlled smoke only; external user trial should move to HTTPS/domain/cert and cleartext tightening."
    )
}

$summary | ConvertTo-Json -Depth 18 | Set-Content -LiteralPath $summaryPath -Encoding UTF8

Write-Host "Android public WebView readiness smoke completed: passed=$passed"
Write-Host "Summary: $summaryPath"
Write-Host "Output directory: $OutputDirectory"

if (-not $passed) {
    exit 1
}

exit 0
