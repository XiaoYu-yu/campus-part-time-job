param(
    [string]$ApiBase = "",
    [string]$ApiBaseEnvFile = "",
    [string]$AdbPath = "",
    [string]$DeviceId = "",
    [switch]$StartEmulator,
    [string]$AvdName = "campus_api35",
    [int]$EmulatorBootTimeoutSeconds = 360,
    [int]$Port = 9222,
    [int]$LaunchWaitSeconds = 8,
    [int]$WaitTimeoutSeconds = 35,
    [switch]$ClearData,
    [string]$OutputPath = "",
    [string]$ScreenshotPath = ""
)

$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
$userPackage = "com.xiaoyu.campus.user"

if (-not $ApiBaseEnvFile) {
    $ApiBaseEnvFile = Join-Path $repoRoot "frontend\.env.android-user-public"
}
if (-not $OutputPath) {
    $OutputPath = Join-Path $repoRoot "project-logs\campus-relay\runtime\step-123-android-public-webview\user-public-webview-smoke.json"
}
if (-not $ScreenshotPath) {
    $ScreenshotPath = Join-Path $repoRoot "project-logs\campus-relay\runtime\step-123-android-public-webview\user-campus-orders-public-webview.png"
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
        throw "ApiBase is required. Pass -ApiBase or create frontend/.env.android-user-public with VITE_API_BASE_URL."
    }

    $normalized = $candidate.Trim().TrimEnd("/")
    if (-not ($normalized -match "^https?://")) {
        throw "ApiBase must start with http:// or https://"
    }
    if (-not ($normalized -match "/api$")) {
        throw "ApiBase must end with /api"
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
    if ($env:ANDROID_HOME) {
        $candidates += (Join-Path $env:ANDROID_HOME "platform-tools\adb.exe")
    }
    if ($env:ANDROID_SDK_ROOT) {
        $candidates += (Join-Path $env:ANDROID_SDK_ROOT "platform-tools\adb.exe")
    }
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
    if ($env:ANDROID_HOME) {
        $candidates += (Join-Path $env:ANDROID_HOME "emulator\emulator.exe")
    }
    if ($env:ANDROID_SDK_ROOT) {
        $candidates += (Join-Path $env:ANDROID_SDK_ROOT "emulator\emulator.exe")
    }
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
    if ($DeviceId) {
        $adbArgs += @("-s", $DeviceId)
    }
    $adbArgs += $CommandArgs
    & $script:adb @adbArgs
}

function Get-ConnectedDevices {
    $output = & $script:adb devices -l
    $devices = @()
    foreach ($line in $output) {
        if ($line -match "^(\S+)\s+device\s") {
            $devices += $matches[1]
        }
    }
    return $devices
}

function Assert-Device {
    $devices = Get-ConnectedDevices
    if ($DeviceId) {
        if ($devices -notcontains $DeviceId) {
            throw "Device '$DeviceId' is not online. Current online devices: $($devices -join ', ')"
        }
        return
    }
    if ($devices.Count -lt 1) {
        throw "No online Android device or emulator found."
    }
    if ($devices.Count -gt 1) {
        throw "Multiple devices are online: $($devices -join ', '). Rerun with -DeviceId <serial>."
    }
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
            if ($bootCompleted -eq "1") {
                return
            }
        } else {
            Write-Host "Waiting for emulator..."
        }
    } while ((Get-Date) -lt $deadline)

    throw "Emulator '$AvdName' did not become ready within $EmulatorBootTimeoutSeconds seconds."
}

function Launch-UserApp {
    if ($ClearData) {
        Invoke-Adb -CommandArgs @("shell", "pm", "clear", $userPackage) | Out-Null
    }
    Invoke-Adb -CommandArgs @("shell", "monkey", "-p", $userPackage, "-c", "android.intent.category.LAUNCHER", "1") | Out-Null
    Start-Sleep -Seconds $LaunchWaitSeconds
}

function Get-PackagePid {
    $deadline = (Get-Date).AddSeconds($WaitTimeoutSeconds)
    do {
        $pidOutput = Invoke-Adb -CommandArgs @("shell", "pidof", $userPackage) 2>$null | Select-Object -First 1
        $pidText = "$pidOutput".Trim()
        if ($pidText -match "^\d+$") {
            return $pidText
        }
        Start-Sleep -Milliseconds 500
    } while ((Get-Date) -lt $deadline)

    throw "Could not resolve pid for $userPackage."
}

function Wait-ForCdpPage {
    $deadline = (Get-Date).AddSeconds($WaitTimeoutSeconds)
    do {
        try {
            $pages = Invoke-RestMethod -Uri "http://127.0.0.1:$Port/json" -TimeoutSec 2
            $page = $pages | Where-Object { $_.webSocketDebuggerUrl } | Select-Object -First 1
            if ($page) {
                return $page
            }
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
    $Socket.SendAsync($segment, [System.Net.WebSockets.WebSocketMessageType]::Text, $true, [Threading.CancellationToken]::None).GetAwaiter().GetResult()
}

function Invoke-Cdp {
    param(
        [System.Net.WebSockets.ClientWebSocket]$Socket,
        [string]$Method,
        [hashtable]$Params = @{}
    )

    $script:cdpId += 1
    $id = $script:cdpId
    Send-CdpMessage -Socket $Socket -Payload @{
        id = $id
        method = $Method
        params = $Params
    }

    while ($true) {
        $message = Receive-CdpMessage -Socket $Socket
        if (-not $message) {
            continue
        }
        $parsed = $message | ConvertFrom-Json
        if ($parsed.id -eq $id) {
            if ($parsed.error) {
                throw "CDP $Method failed: $($parsed.error.message)"
            }
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
    if ($result.subtype -eq "error") {
        throw "JavaScript evaluation failed: $($result.description)"
    }
    return $result.value
}

function Wait-Until {
    param(
        [scriptblock]$Check,
        [string]$Description,
        [int]$TimeoutSeconds = $WaitTimeoutSeconds
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    do {
        $value = & $Check
        if ($value) {
            return $value
        }
        Start-Sleep -Milliseconds 500
    } while ((Get-Date) -lt $deadline)

    throw "Timed out waiting for $Description."
}

function Save-Screenshot {
    param([string]$Path)

    $dir = Split-Path -Parent $Path
    New-Item -ItemType Directory -Path $dir -Force | Out-Null
    $remoteFile = "/sdcard/step123-user-public-webview.png"
    Invoke-Adb -CommandArgs @("shell", "screencap", "-p", $remoteFile) | Out-Null
    Invoke-Adb -CommandArgs @("pull", $remoteFile, $Path) | Out-Null
    Invoke-Adb -CommandArgs @("shell", "rm", $remoteFile) | Out-Null
}

$script:normalizedApiBase = Resolve-ApiBase -Value $ApiBase -EnvFile $ApiBaseEnvFile
$script:adb = Resolve-Adb $AdbPath
$script:cdpId = 0

if ($StartEmulator) {
    Start-EmulatorAndWait
}

Assert-Device
Launch-UserApp
$appPid = Get-PackagePid
Invoke-Adb -CommandArgs @("forward", "tcp:$Port", "localabstract:webview_devtools_remote_$appPid") | Out-Null
$page = Wait-ForCdpPage

$socket = [System.Net.WebSockets.ClientWebSocket]::new()
try {
    $socket.ConnectAsync([Uri]$page.webSocketDebuggerUrl, [Threading.CancellationToken]::None).GetAwaiter().GetResult() | Out-Null
    Invoke-Cdp -Socket $socket -Method "Runtime.enable" | Out-Null

    $loginScript = @'
(() => {
  const inputs = Array.from(document.querySelectorAll('input'));
  if (inputs.length < 2) {
    return { ok: false, reason: 'login inputs not found', href: location.href, inputCount: inputs.length };
  }
  inputs[0].value = '13900139000';
  inputs[0].dispatchEvent(new Event('input', { bubbles: true }));
  inputs[1].value = '123456';
  inputs[1].dispatchEvent(new Event('input', { bubbles: true }));
  const buttons = Array.from(document.querySelectorAll('button'));
  const button = document.querySelector('.login-btn') || buttons[buttons.length - 1];
  if (!button) {
    return { ok: false, reason: 'login button not found', href: location.href };
  }
  button.click();
  return { ok: true, href: location.href, inputCount: inputs.length };
})()
'@

    Write-Host "Interacting with user login page through WebView DevTools"
    $loginClick = Invoke-Javascript -Socket $socket -Expression $loginScript
    if (-not $loginClick.ok) {
        throw "Login form interaction failed: $($loginClick.reason)"
    }

    Write-Host "Waiting for customer_token in WebView localStorage"
    $loginState = Wait-Until -Description "customer token after WebView login" -Check {
        $state = Invoke-Javascript -Socket $socket -Expression "(() => ({ href: location.href, hasToken: !!localStorage.getItem('customer_token') }))()"
        if ($state.hasToken) {
            return $state
        }
        return $null
    }

    Write-Host "Running public API smoke inside the Android WebView context"
    $escapedApiBase = $script:normalizedApiBase.Replace("\", "\\").Replace('"', '\"')
    $apiSmokeScriptTemplate = @'
(async () => {
  const apiBase = "__API_BASE__";
  const token = localStorage.getItem('customer_token');
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
  const pickup = await jsonFetch('/campus/public/pickup-points');
  const rules = await jsonFetch('/campus/public/delivery-rules');
  const before = await jsonFetch('/campus/customer/orders?page=1&pageSize=5');
  const firstPoint = Array.isArray(pickup.data) && pickup.data.length > 0 ? pickup.data[0] : null;
  if (!firstPoint || !firstPoint.id) {
    return {
      ok: false,
      failedAt: 'pickup-points',
      pickup: { httpStatus: pickup.httpStatus, bodyCode: pickup.bodyCode, bodyMsg: pickup.bodyMsg, count: Array.isArray(pickup.data) ? pickup.data.length : null },
      rules: { httpStatus: rules.httpStatus, bodyCode: rules.bodyCode, bodyMsg: rules.bodyMsg },
      before: { httpStatus: before.httpStatus, bodyCode: before.bodyCode, bodyMsg: before.bodyMsg }
    };
  }
  const suffix = String(Date.now()).slice(-8);
  const createPayload = {
    pickupPointId: firstPoint.id,
    targetType: 'DORMITORY',
    deliveryBuilding: '\u7af9\u56ed',
    deliveryDetail: '\u7af9\u56ed2\u680b\u697c\u4e0b',
    contactName: 'Android WebView',
    contactPhone: '13900139000',
    foodDescription: 'Android WebView public smoke order',
    externalPlatformName: 'AndroidSmoke',
    externalOrderRef: 'AWV-' + suffix,
    pickupCode: 'A18',
    remark: 'Android public WebView smoke',
    tipFee: 1,
    urgentFlag: 0
  };
  const create = await jsonFetch('/campus/customer/orders', { method: 'POST', body: JSON.stringify(createPayload) });
  const orderId = create.data;
  let pay = null;
  let detail = null;
  if (create.bodyCode === 200 && orderId) {
    pay = await jsonFetch('/campus/customer/orders/' + orderId + '/mock-pay', { method: 'POST' });
    detail = await jsonFetch('/campus/customer/orders/' + orderId);
  }
  const passed = pickup.bodyCode === 200 && rules.bodyCode === 200 && before.bodyCode === 200 &&
    create.bodyCode === 200 && pay && pay.bodyCode === 200 && detail && detail.bodyCode === 200;
  return {
    ok: passed,
    pickup: { httpStatus: pickup.httpStatus, bodyCode: pickup.bodyCode, bodyMsg: pickup.bodyMsg, count: Array.isArray(pickup.data) ? pickup.data.length : null, firstPointId: firstPoint.id },
    rules: { httpStatus: rules.httpStatus, bodyCode: rules.bodyCode, bodyMsg: rules.bodyMsg },
    before: { httpStatus: before.httpStatus, bodyCode: before.bodyCode, bodyMsg: before.bodyMsg, total: before.data && before.data.total },
    create: { httpStatus: create.httpStatus, bodyCode: create.bodyCode, bodyMsg: create.bodyMsg, orderId },
    pay: pay ? { httpStatus: pay.httpStatus, bodyCode: pay.bodyCode, bodyMsg: pay.bodyMsg } : null,
    detail: detail ? {
      httpStatus: detail.httpStatus,
      bodyCode: detail.bodyCode,
      bodyMsg: detail.bodyMsg,
      id: detail.data && detail.data.id,
      status: detail.data && detail.data.status,
      paymentStatus: detail.data && detail.data.paymentStatus
    } : null
  };
})()
'@
    $apiSmokeScript = $apiSmokeScriptTemplate.Replace("__API_BASE__", $escapedApiBase)

    $apiResult = Invoke-Javascript -Socket $socket -Expression $apiSmokeScript

    Invoke-Javascript -Socket $socket -Expression "location.href = '/user/campus/orders'" | Out-Null
    Start-Sleep -Seconds 4
    Save-Screenshot -Path $ScreenshotPath

    $result = [ordered]@{
        step = "Step 123"
        date = (Get-Date -Format "yyyy-MM-dd")
        purpose = "Android public WebView user smoke for login, customer campus order read/create/mock-pay"
        apiBase = Redact-ApiBase $script:normalizedApiBase
        packageName = $userPackage
        webView = [ordered]@{
            devtoolsPort = $Port
            launchUrl = $page.url
            loginResult = [ordered]@{
                clicked = $true
                hasCustomerToken = [bool]$loginState.hasToken
                currentUrl = $loginState.href
            }
        }
        checks = $apiResult
        screenshot = [ordered]@{
            path = $ScreenshotPath
            description = "User campus orders page after WebView public API smoke"
        }
        summary = [ordered]@{
            passed = [bool]$apiResult.ok
            createdOrderId = $apiResult.create.orderId
            finalOrderStatus = $apiResult.detail.status
            finalPaymentStatus = $apiResult.detail.paymentStatus
        }
        notes = @(
            "The real API host is redacted. Do not commit frontend/.env.android-user-public.",
            "This smoke uses the Android WebView DevTools context, not a host-only curl.",
            "It logs in through the visible user login page, then invokes public customer APIs from the WebView context.",
            "It does not change bridge behavior, token attachment logic, routes, backend state machine, or old takeaway modules."
        )
    }

    $outputDir = Split-Path -Parent $OutputPath
    New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
    $result | ConvertTo-Json -Depth 16 | Set-Content -LiteralPath $OutputPath -Encoding UTF8

    Write-Host "Android user public WebView smoke completed: passed=$($result.summary.passed) order=$($result.summary.createdOrderId)"
    Write-Host "Report: $OutputPath"
    Write-Host "Screenshot: $ScreenshotPath"

    if (-not $result.summary.passed) {
        exit 1
    }
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
