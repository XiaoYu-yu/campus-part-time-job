param(
    [string]$ApiBase = "https://xiaoyu.xin/api",
    [string]$AdbPath = "",
    [string]$DeviceId = "",
    [string]$OutputDirectory = "",
    [int]$LaunchWaitSeconds = 6,
    [int]$WaitTimeoutSeconds = 60,
    [switch]$ClearData,
    [switch]$RequireApiHealth
)

$ErrorActionPreference = "Stop"
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
if (-not $OutputDirectory) {
    $OutputDirectory = Join-Path $repoRoot "project-logs\campus-relay\runtime\android-release-webview"
}
$normalizedApiBase = $ApiBase.Trim().TrimEnd("/")
if ($normalizedApiBase -notmatch "^https://" -or $normalizedApiBase -notmatch "/api$") {
    throw "Release WebView smoke requires an HTTPS ApiBase ending with /api."
}

function Resolve-Adb {
    param([string]$ExplicitPath)
    $candidates = @($ExplicitPath)
    if ($env:ANDROID_HOME) { $candidates += (Join-Path $env:ANDROID_HOME "platform-tools\adb.exe") }
    if ($env:ANDROID_SDK_ROOT) { $candidates += (Join-Path $env:ANDROID_SDK_ROOT "platform-tools\adb.exe") }
    $candidates += (Join-Path $env:LOCALAPPDATA "Android\Sdk\platform-tools\adb.exe")
    foreach ($candidate in $candidates | Where-Object { $_ }) {
        if (Test-Path $candidate) { return (Resolve-Path $candidate).Path }
    }
    throw "adb.exe not found."
}

function Invoke-Adb {
    param([string[]]$CommandArgs)
    $args = @()
    if ($DeviceId) { $args += @("-s", $DeviceId) }
    $args += $CommandArgs
    & $script:adb @args
}

function Test-ReleaseApp {
    param(
        [string]$Name,
        [string]$PackageName,
        [int]$Port
    )

    if ($ClearData) {
        Invoke-Adb @("shell", "pm", "clear", $PackageName) | Out-Null
    }
    Invoke-Adb @("logcat", "-c") | Out-Null
    Invoke-Adb @("shell", "monkey", "-p", $PackageName, "-c", "android.intent.category.LAUNCHER", "1") | Out-Null
    Start-Sleep -Seconds $LaunchWaitSeconds
    $appPid = (Invoke-Adb @("shell", "pidof", $PackageName) | Select-Object -First 1).Trim()
    if ($appPid -notmatch "^\d+$") { throw "Could not resolve pid for $PackageName." }

    $window = (Invoke-Adb @("shell", "dumpsys", "window") | Select-String "mCurrentFocus|mFocusedApp") -join "`n"
    $focusOk = $window -match [regex]::Escape($PackageName)
    $logcat = (Invoke-Adb @("logcat", "-d", "-t", "800") | Select-String "FATAL EXCEPTION|AndroidRuntime|net::ERR_|ERR_CLEARTEXT|$PackageName") -join "`n"
    $fatal = $logcat -match "FATAL EXCEPTION|ERR_CLEARTEXT|net::ERR_"

    New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null
    $screenshotPath = Join-Path $OutputDirectory "$Name-release-launch.png"
    $remote = "/sdcard/$Name-release-launch.png"
    Invoke-Adb @("shell", "screencap", "-p", $remote) | Out-Null
    Invoke-Adb @("pull", $remote, $screenshotPath) | Out-Null
    Invoke-Adb @("shell", "rm", $remote) | Out-Null
    $screenshotSize = (Get-Item $screenshotPath).Length

    return [ordered]@{
        name = $Name
        packageName = $PackageName
        passed = ($focusOk -and -not $fatal -and $screenshotSize -gt 50000)
        devtoolsExpected = $false
        pid = "<redacted>"
        focusOk = $focusOk
        currentFocusRedacted = ($window -replace [regex]::Escape($PackageName), "<package>")
        fatalLogDetected = $fatal
        screenshot = $screenshotPath
        screenshotSizeBytes = $screenshotSize
    }
}

$script:adb = Resolve-Adb $AdbPath
$devices = & $script:adb devices
if ($DeviceId -and -not ($devices -match [regex]::Escape($DeviceId))) {
    throw "Device is not online: $DeviceId"
}

$apiHealth = [ordered]@{
    checked = $false
    required = $RequireApiHealth.IsPresent
    passed = $false
    httpStatus = $null
    bodyCode = $null
    serviceStatus = $null
    error = $null
}
try {
    $response = Invoke-WebRequest -UseBasicParsing -Uri "$normalizedApiBase/campus/public/health" -TimeoutSec 8
    $body = $response.Content | ConvertFrom-Json
    $apiHealth.checked = $true
    $apiHealth.httpStatus = $response.StatusCode
    $apiHealth.bodyCode = $body.code
    $apiHealth.serviceStatus = $body.data.status
    $apiHealth.passed = $response.StatusCode -eq 200 -and $body.code -eq 200 -and $body.data.status -eq "UP"
} catch {
    $apiHealth.checked = $true
    $apiHealth.error = $_.Exception.Message
}

$results = @()
$results += Test-ReleaseApp "user" "com.xiaoyu.campus.user" 9322
$results += Test-ReleaseApp "parttime" "com.xiaoyu.campus.parttime" 9323
$passed = ($results | Where-Object { -not $_.passed }).Count -eq 0
if ($RequireApiHealth -and -not $apiHealth.passed) {
    $passed = $false
}
$uri = [Uri]$normalizedApiBase
$report = [ordered]@{
    name = "Android release WebView read-only smoke"
    generatedAt = (Get-Date).ToString("o")
    apiBase = "$($uri.Scheme)://<redacted>$($uri.AbsolutePath)"
    deviceId = "<redacted>"
    passed = $passed
    apiHealth = $apiHealth
    checks = $results
    notes = @(
        "Release WebView DevTools are not expected to be exposed, so this smoke uses launch/focus/screenshot/log checks.",
        "The optional API health check is host-side read-only; it does not create orders or mutate server state.",
        "A passing app result proves release APKs install, launch, focus correctly, avoid cleartext errors, and render non-empty first screens."
    )
}
New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null
$reportPath = Join-Path $OutputDirectory "android-release-webview-smoke.json"
$report | ConvertTo-Json -Depth 10 | Set-Content -LiteralPath $reportPath -Encoding UTF8
Write-Host "Android release WebView smoke completed: passed=$passed"
Write-Host "Report: $reportPath"
if (-not $passed) { exit 1 }
