param(
    [string]$ApiBase = "",
    [string]$ApiBaseEnvFile = "",
    [string]$AdbPath = "",
    [string]$DeviceId = "",
    [switch]$StartEmulator,
    [string]$AvdName = "campus_api35",
    [int]$EmulatorBootTimeoutSeconds = 360,
    [int]$Port = 9223,
    [int]$LaunchWaitSeconds = 8,
    [int]$WaitTimeoutSeconds = 35,
    [switch]$ClearData,
    [string]$OutputPath = "",
    [string]$ScreenshotPath = ""
)

$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
$parttimePackage = "com.xiaoyu.campus.parttime"

if (-not $ApiBaseEnvFile) {
    $ApiBaseEnvFile = Join-Path $repoRoot "frontend\.env.android-parttime-public"
}
if (-not $OutputPath) {
    $OutputPath = Join-Path $repoRoot "project-logs\campus-relay\runtime\step-123-android-public-webview\parttime-public-webview-smoke.json"
}
if (-not $ScreenshotPath) {
    $ScreenshotPath = Join-Path $repoRoot "project-logs\campus-relay\runtime\step-123-android-public-webview\parttime-workbench-public-webview.png"
}

function Resolve-ApiBase {
    param([string]$Value, [string]$EnvFile)
    $candidate = $Value
    if (-not $candidate -and (Test-Path $EnvFile)) {
        $line = Get-Content -LiteralPath $EnvFile | Where-Object { $_ -match "^\s*VITE_API_BASE_URL\s*=" } | Select-Object -First 1
        if ($line) {
            $candidate = ($line -replace "^\s*VITE_API_BASE_URL\s*=", "").Trim()
        }
    }
    if (-not $candidate) {
        throw "ApiBase is required. Pass -ApiBase or create frontend/.env.android-parttime-public with VITE_API_BASE_URL."
    }
    $normalized = $candidate.Trim().TrimEnd("/")
    if (-not ($normalized -match "^https?://") -or -not ($normalized -match "/api$")) {
        throw "ApiBase must start with http:// or https:// and end with /api"
    }
    return $normalized
}

function Redact-ApiBase {
    param([string]$Value)
    $uri = [Uri]$Value
    return "$($uri.Scheme)://<redacted>$($uri.AbsolutePath.TrimEnd('/'))"
}

function Resolve-Adb {
    param([string]$ExplicitPath)
    if ($ExplicitPath -and (Test-Path $ExplicitPath)) {
        return (Resolve-Path $ExplicitPath).Path
    }
    $candidates = @()
    if ($env:ANDROID_HOME) { $candidates += (Join-Path $env:ANDROID_HOME "platform-tools\adb.exe") }
    if ($env:ANDROID_SDK_ROOT) { $candidates += (Join-Path $env:ANDROID_SDK_ROOT "platform-tools\adb.exe") }
    $candidates += (Join-Path $env:LOCALAPPDATA "Android\Sdk\platform-tools\adb.exe")
    foreach ($candidate in $candidates) {
        if ($candidate -and (Test-Path $candidate)) {
            return (Resolve-Path $candidate).Path
        }
    }
    throw "adb.exe not found. Install Android SDK platform-tools or pass -AdbPath."
}

function Resolve-Emulator {
    $candidates = @()
    if ($env:ANDROID_HOME) { $candidates += (Join-Path $env:ANDROID_HOME "emulator\emulator.exe") }
    if ($env:ANDROID_SDK_ROOT) { $candidates += (Join-Path $env:ANDROID_SDK_ROOT "emulator\emulator.exe") }
    $candidates += (Join-Path $env:LOCALAPPDATA "Android\Sdk\emulator\emulator.exe")
    foreach ($candidate in $candidates) {
        if ($candidate -and (Test-Path $candidate)) {
            return (Resolve-Path $candidate).Path
        }
    }
    throw "emulator.exe not found. Install Android SDK emulator package first."
}

function Invoke-Adb {
    param([string[]]$CommandArgs)
    $adbArgs = @()
    if ($DeviceId) { $adbArgs += @("-s", $DeviceId) }
    $adbArgs += $CommandArgs
    & $script:adb @adbArgs
}

function Get-ConnectedDevices {
    $output = & $script:adb devices -l
    $devices = @()
    foreach ($line in $output) {
        if ($line -match "^(\S+)\s+device\s") { $devices += $matches[1] }
    }
    return $devices
}

function Start-EmulatorAndWait {
    $devices = Get-ConnectedDevices
    if ($devices.Count -gt 0) {
        Write-Host "Device already online: $($devices -join ', ')"
        return
    }
    $emulator = Resolve-Emulator
    Write-Host "Starting emulator '$AvdName'"
    Start-Process -FilePath $emulator -ArgumentList @("-avd", $AvdName, "-no-snapshot", "-gpu", "swiftshader_indirect", "-no-boot-anim") -WindowStyle Hidden | Out-Null
    $deadline = (Get-Date).AddSeconds($EmulatorBootTimeoutSeconds)
    do {
        Start-Sleep -Seconds 5
        $devices = Get-ConnectedDevices
        if ($devices.Count -gt 0) {
            $bootCompleted = (Invoke-Adb -CommandArgs @("shell", "getprop", "sys.boot_completed") 2>$null | Select-Object -First 1).Trim()
            Write-Host "Emulator boot status: devices=$($devices -join ', ') boot=$bootCompleted"
            if ($bootCompleted -eq "1") { return }
        } else {
            Write-Host "Waiting for emulator..."
        }
    } while ((Get-Date) -lt $deadline)
    throw "Emulator '$AvdName' did not become ready within $EmulatorBootTimeoutSeconds seconds."
}

function Assert-Device {
    $devices = Get-ConnectedDevices
    if ($DeviceId) {
        if ($devices -notcontains $DeviceId) {
            throw "Device '$DeviceId' is not online. Current online devices: $($devices -join ', ')"
        }
        return
    }
    if ($devices.Count -lt 1) { throw "No online Android device or emulator found." }
    if ($devices.Count -gt 1) { throw "Multiple devices are online: $($devices -join ', '). Rerun with -DeviceId <serial>." }
}

function Launch-ParttimeApp {
    if ($ClearData) {
        Invoke-Adb -CommandArgs @("shell", "pm", "clear", $parttimePackage) | Out-Null
    }
    Invoke-Adb -CommandArgs @("shell", "monkey", "-p", $parttimePackage, "-c", "android.intent.category.LAUNCHER", "1") | Out-Null
    Start-Sleep -Seconds $LaunchWaitSeconds
}

function Get-PackagePid {
    $deadline = (Get-Date).AddSeconds($WaitTimeoutSeconds)
    do {
        $pidOutput = Invoke-Adb -CommandArgs @("shell", "pidof", $parttimePackage) 2>$null | Select-Object -First 1
        $pidText = "$pidOutput".Trim()
        if ($pidText -match "^\d+$") { return $pidText }
        Start-Sleep -Milliseconds 500
    } while ((Get-Date) -lt $deadline)
    throw "Could not resolve pid for $parttimePackage."
}

function Wait-ForCdpPage {
    $deadline = (Get-Date).AddSeconds($WaitTimeoutSeconds)
    do {
        try {
            $pages = Invoke-RestMethod -Uri "http://127.0.0.1:$Port/json" -TimeoutSec 2
            $page = $pages | Where-Object { $_.webSocketDebuggerUrl } | Select-Object -First 1
            if ($page) { return $page }
        } catch {
            Start-Sleep -Milliseconds 500
        }
    } while ((Get-Date) -lt $deadline)
    throw "No WebView DevTools page exposed on port $Port."
}

function Receive-CdpMessage {
    param([System.Net.WebSockets.ClientWebSocket]$Socket)
    $buffer = [byte[]]::new(65536)
    $segment = [ArraySegment[byte]]::new($buffer)
    $builder = New-Object System.Text.StringBuilder
    do {
        $result = $Socket.ReceiveAsync($segment, [Threading.CancellationToken]::None).GetAwaiter().GetResult()
        if ($result.Count -gt 0) {
            [void]$builder.Append([Text.Encoding]::UTF8.GetString($buffer, 0, $result.Count))
        }
    } until ($result.EndOfMessage)
    return $builder.ToString()
}

function Send-CdpMessage {
    param([System.Net.WebSockets.ClientWebSocket]$Socket, [hashtable]$Payload)
    $json = $Payload | ConvertTo-Json -Depth 12 -Compress
    $bytes = [Text.Encoding]::UTF8.GetBytes($json)
    $segment = [ArraySegment[byte]]::new($bytes)
    $Socket.SendAsync($segment, [System.Net.WebSockets.WebSocketMessageType]::Text, $true, [Threading.CancellationToken]::None).GetAwaiter().GetResult() | Out-Null
}

function Invoke-Cdp {
    param([System.Net.WebSockets.ClientWebSocket]$Socket, [string]$Method, [hashtable]$Params = @{})
    $script:cdpId += 1
    $id = $script:cdpId
    Send-CdpMessage -Socket $Socket -Payload @{ id = $id; method = $Method; params = $Params }
    while ($true) {
        $message = Receive-CdpMessage -Socket $Socket
        if (-not $message) { continue }
        $parsed = $message | ConvertFrom-Json
        if ($parsed.id -eq $id) {
            if ($parsed.error) { throw "CDP $Method failed: $($parsed.error.message)" }
            return $parsed
        }
    }
}

function Invoke-Javascript {
    param([System.Net.WebSockets.ClientWebSocket]$Socket, [string]$Expression)
    $response = Invoke-Cdp -Socket $Socket -Method "Runtime.evaluate" -Params @{
        expression = $Expression
        awaitPromise = $true
        returnByValue = $true
    }
    $result = $response.result.result
    if ($result.subtype -eq "error") { throw "JavaScript evaluation failed: $($result.description)" }
    return $result.value
}

function Wait-Until {
    param([scriptblock]$Check, [string]$Description, [int]$TimeoutSeconds = $WaitTimeoutSeconds)
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    do {
        $value = & $Check
        if ($value) { return $value }
        Start-Sleep -Milliseconds 500
    } while ((Get-Date) -lt $deadline)
    throw "Timed out waiting for $Description."
}

function Save-Screenshot {
    param([string]$Path)
    $dir = Split-Path -Parent $Path
    New-Item -ItemType Directory -Path $dir -Force | Out-Null
    $remoteFile = "/sdcard/step123-parttime-public-webview.png"
    Invoke-Adb -CommandArgs @("shell", "screencap", "-p", $remoteFile) | Out-Null
    Invoke-Adb -CommandArgs @("pull", $remoteFile, $Path) | Out-Null
    Invoke-Adb -CommandArgs @("shell", "rm", $remoteFile) | Out-Null
}

$script:normalizedApiBase = Resolve-ApiBase -Value $ApiBase -EnvFile $ApiBaseEnvFile
$script:adb = Resolve-Adb $AdbPath
$script:cdpId = 0

if ($StartEmulator) { Start-EmulatorAndWait }
Assert-Device
Launch-ParttimeApp
$appPid = Get-PackagePid
Invoke-Adb -CommandArgs @("forward", "tcp:$Port", "localabstract:webview_devtools_remote_$appPid") | Out-Null
$page = Wait-ForCdpPage

$socket = [System.Net.WebSockets.ClientWebSocket]::new()
try {
    $socket.ConnectAsync([Uri]$page.webSocketDebuggerUrl, [Threading.CancellationToken]::None).GetAwaiter().GetResult() | Out-Null
    Invoke-Cdp -Socket $socket -Method "Runtime.enable" | Out-Null

    Write-Host "Interacting with parttime login page through WebView DevTools"
    $loginScript = @'
(() => {
  const inputs = Array.from(document.querySelectorAll('input'));
  if (inputs.length >= 2) {
    inputs[0].value = '13900139001';
    inputs[0].dispatchEvent(new Event('input', { bubbles: true }));
    inputs[1].value = '123456';
    inputs[1].dispatchEvent(new Event('input', { bubbles: true }));
  }
  const buttons = Array.from(document.querySelectorAll('button'));
  const button = document.querySelector('.login-btn') || buttons.find((item) => item.className && String(item.className).includes('login-btn')) || buttons[buttons.length - 1];
  if (!button) {
    return { ok: false, reason: 'login button not found', href: location.href, inputCount: inputs.length };
  }
  button.click();
  return { ok: true, href: location.href, inputCount: inputs.length, buttonCount: buttons.length };
})()
'@
    $loginClick = Invoke-Javascript -Socket $socket -Expression $loginScript
    if (-not $loginClick.ok) { throw "Parttime login form interaction failed: $($loginClick.reason)" }

    Write-Host "Waiting for courier_token in WebView localStorage"
    $loginState = Wait-Until -Description "courier token after WebView login" -Check {
        $state = Invoke-Javascript -Socket $socket -Expression "(() => ({ href: location.href, hasToken: !!localStorage.getItem('courier_token') }))()"
        if ($state.hasToken) { return $state }
        return $null
    }

    Write-Host "Running parttime public API smoke inside the Android WebView context"
    $escapedApiBase = $script:normalizedApiBase.Replace("\", "\\").Replace('"', '\"')
    $apiSmokeScriptTemplate = @'
(async () => {
  const apiBase = "__API_BASE__";
  const token = localStorage.getItem('courier_token');
  const jsonFetch = async (path, options = {}) => {
    const headers = Object.assign({ 'Content-Type': 'application/json' }, options.headers || {});
    if (token) headers.Authorization = 'Bearer ' + token;
    const response = await fetch(apiBase + path, Object.assign({}, options, { headers }));
    const text = await response.text();
    let body = null;
    try {
      body = text ? JSON.parse(text) : null;
    } catch (error) {
      body = { code: null, msg: 'non-json response', raw: text.slice(0, 200) };
    }
    return { path, httpStatus: response.status, ok: response.ok, bodyCode: body && body.code, bodyMsg: body && body.msg, data: body && body.data };
  };
  const profile = await jsonFetch('/campus/courier/profile');
  const review = await jsonFetch('/campus/courier/review-status');
  const available = await jsonFetch('/campus/courier/orders/available?page=1&pageSize=5');
  const passed = profile.bodyCode === 200 && review.bodyCode === 200 && available.bodyCode === 200;
  return {
    ok: passed,
    profile: { httpStatus: profile.httpStatus, bodyCode: profile.bodyCode, bodyMsg: profile.bodyMsg, id: profile.data && profile.data.id, reviewStatus: profile.data && profile.data.reviewStatus, enabled: profile.data && profile.data.enabled },
    review: { httpStatus: review.httpStatus, bodyCode: review.bodyCode, bodyMsg: review.bodyMsg, reviewStatus: review.data && review.data.reviewStatus, enabled: review.data && review.data.enabled },
    available: { httpStatus: available.httpStatus, bodyCode: available.bodyCode, bodyMsg: available.bodyMsg, total: available.data && available.data.total, records: available.data && available.data.records ? available.data.records.length : null }
  };
})()
'@
    $apiSmokeScript = $apiSmokeScriptTemplate.Replace("__API_BASE__", $escapedApiBase)
    $apiResult = Invoke-Javascript -Socket $socket -Expression $apiSmokeScript

    Invoke-Javascript -Socket $socket -Expression "location.href = '/parttime/workbench'" | Out-Null
    Start-Sleep -Seconds 4
    Save-Screenshot -Path $ScreenshotPath

    $result = [ordered]@{
        step = "Step 123"
        date = (Get-Date -Format "yyyy-MM-dd")
        purpose = "Android public WebView parttime smoke for login, profile/review-status, available orders"
        apiBase = Redact-ApiBase $script:normalizedApiBase
        packageName = $parttimePackage
        webView = [ordered]@{
            devtoolsPort = $Port
            launchUrl = $page.url
            loginResult = [ordered]@{
                clicked = $true
                hasCourierToken = [bool]$loginState.hasToken
                currentUrl = $loginState.href
            }
        }
        checks = $apiResult
        screenshot = [ordered]@{
            path = $ScreenshotPath
            description = "Parttime workbench after WebView public API smoke"
        }
        summary = [ordered]@{
            passed = [bool]$apiResult.ok
            courierProfileId = $apiResult.profile.id
            reviewStatus = $apiResult.review.reviewStatus
            availableOrders = $apiResult.available.records
        }
        notes = @(
            "The real API host is redacted. Do not commit frontend/.env.android-parttime-public.",
            "This smoke uses the Android WebView DevTools context, not a host-only curl.",
            "It logs in through the visible parttime login page, then invokes courier APIs from the WebView context.",
            "It does not change bridge behavior, token attachment logic, routes, backend state machine, or old takeaway modules."
        )
    }

    $outputDir = Split-Path -Parent $OutputPath
    New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
    $result | ConvertTo-Json -Depth 16 | Set-Content -LiteralPath $OutputPath -Encoding UTF8

    Write-Host "Android parttime public WebView smoke completed: passed=$($result.summary.passed) profile=$($result.summary.courierProfileId)"
    Write-Host "Report: $OutputPath"
    Write-Host "Screenshot: $ScreenshotPath"

    if (-not $result.summary.passed) { exit 1 }
} finally {
    if ($socket.State -eq [System.Net.WebSockets.WebSocketState]::Open) {
        try {
            $socket.CloseOutputAsync([System.Net.WebSockets.WebSocketCloseStatus]::NormalClosure, "done", [Threading.CancellationToken]::None).GetAwaiter().GetResult() | Out-Null
        } catch {
            Write-Warning "WebSocket close handshake skipped: $($_.Exception.Message)"
        }
    }
    $socket.Dispose()
}

exit 0
