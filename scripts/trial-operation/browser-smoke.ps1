param(
    [string]$FrontendBase = 'http://127.0.0.1:5173',
    [string]$ApiBase = 'http://127.0.0.1:8080/api',
    [string]$RuntimeDir = ''
)

$ErrorActionPreference = 'Stop'

$RepoRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..')
if (-not $RuntimeDir) {
    $RuntimeDir = Join-Path $RepoRoot 'project-logs\campus-relay\runtime\step-132-browser-smoke'
}

New-Item -ItemType Directory -Force -Path $RuntimeDir | Out-Null

$FrontendBase = $FrontendBase.TrimEnd('/')
$ApiBase = $ApiBase.TrimEnd('/')

$results = @()

function Add-Result {
    param(
        [string]$Name,
        [string]$Status,
        [string]$Message,
        [string]$Path = '',
        [string]$Screenshot = '',
        [string]$CurrentUrl = ''
    )

    $script:results += [pscustomobject]@{
        name = $Name
        status = $Status
        message = $Message
        path = $Path
        screenshot = $Screenshot
        currentUrl = $CurrentUrl
    }
    Write-Host "[$Status] $Name - $Message"
}

function Invoke-Api {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [object]$Body = $null,
        [string]$Token = $null
    )

    $headers = @{}
    if ($Token) {
        $headers.Authorization = "Bearer $Token"
    }

    $params = @{
        Method = $Method
        Uri = $Url
        Headers = $headers
        ContentType = 'application/json'
    }
    if ($null -ne $Body) {
        $params.Body = ($Body | ConvertTo-Json -Depth 8)
    }

    $res = Invoke-RestMethod @params
    if ($res.code -ne 200) {
        throw "$Name failed: code=$($res.code), msg=$($res.msg)"
    }
    return $res.data
}

function Invoke-PwCli {
    param(
        [string[]]$CliArgs,
        [switch]$AllowFailure
    )

    $output = & npx --yes --package '@playwright/cli' -- playwright-cli @CliArgs 2>&1
    $exitCode = $LASTEXITCODE
    $outputText = ($output | Out-String).Trim()

    if ($exitCode -ne 0 -and -not $AllowFailure) {
        throw "playwright-cli failed ($exitCode): $($CliArgs -join ' ')`n$outputText"
    }

    return [pscustomobject]@{
        exitCode = $exitCode
        output = $outputText
    }
}

function Convert-RawCliValue {
    param([string]$RawValue)

    $value = ($RawValue | Out-String).Trim()
    try {
        return ($value | ConvertFrom-Json)
    } catch {
        return $value.Trim('"')
    }
}

function Invoke-PwRaw {
    param(
        [string]$Session,
        [string]$Expression
    )

    $res = Invoke-PwCli -CliArgs @("-s=$Session", '--raw', 'eval', $Expression)
    return Convert-RawCliValue $res.output
}

function Test-Port {
    param(
        [string]$Url,
        [string]$Name
    )

    $uri = [System.Uri]$Url
    $port = $uri.Port
    if ($port -lt 0) {
        if ($uri.Scheme -eq 'https') {
            $port = 443
        } else {
            $port = 80
        }
    }

    $client = [System.Net.Sockets.TcpClient]::new()
    try {
        $connect = $client.BeginConnect($uri.Host, $port, $null, $null)
        if (-not $connect.AsyncWaitHandle.WaitOne(5000, $false)) {
            throw "connection timed out"
        }
        $client.EndConnect($connect)
    } catch {
        throw "$Name is not reachable at $Url. Start the local service first. $($_.Exception.Message)"
    } finally {
        $client.Dispose()
    }
}

function Invoke-PageSmoke {
    param(
        [pscustomobject]$Page,
        [hashtable]$StorageItems
    )

    $session = "step132-$($Page.slug)"
    $screenshotPath = Join-Path $RuntimeDir "$($Page.slug).png"
    $loginUrl = "$FrontendBase$($Page.loginPath)"
    $targetUrl = "$FrontendBase$($Page.path)"

    Invoke-PwCli -CliArgs @("-s=$session", 'close') -AllowFailure | Out-Null
    Invoke-PwCli -CliArgs @("-s=$session", 'open', $loginUrl) | Out-Null
    Invoke-PwCli -CliArgs @("-s=$session", 'resize', '1440', '960') | Out-Null
    Invoke-PwCli -CliArgs @("-s=$session", 'localstorage-clear') | Out-Null

    foreach ($key in $StorageItems.Keys) {
        Invoke-PwCli -CliArgs @("-s=$session", 'localstorage-set', $key, [string]$StorageItems[$key]) | Out-Null
    }

    # Reload after localStorage writes so Pinia/router guards initialize from the intended token state.
    Invoke-PwCli -CliArgs @("-s=$session", 'reload') | Out-Null
    Start-Sleep -Seconds 1
    Invoke-PwCli -CliArgs @("-s=$session", 'goto', $targetUrl) | Out-Null
    Start-Sleep -Seconds 4

    $currentUrl = Invoke-PwRaw -Session $session -Expression 'location.href'
    $hasAppRoot = Invoke-PwRaw -Session $session -Expression "document.querySelector('#app') !== null"
    $appHtmlLength = Invoke-PwRaw -Session $session -Expression "(document.querySelector('#app') && document.querySelector('#app').innerHTML.length) || 0"

    if ($currentUrl -match '/login') {
        Add-Result $Page.name 'FAIL' "redirected to login: $currentUrl" $Page.path '' $currentUrl
        Invoke-PwCli -CliArgs @("-s=$session", 'close') -AllowFailure | Out-Null
        return
    }

    if ($hasAppRoot -ne $true -and $hasAppRoot -ne 'true') {
        Add-Result $Page.name 'FAIL' 'missing #app root' $Page.path '' $currentUrl
        Invoke-PwCli -CliArgs @("-s=$session", 'close') -AllowFailure | Out-Null
        return
    }

    if ([int]$appHtmlLength -le 0) {
        Add-Result $Page.name 'FAIL' 'empty rendered app root' $Page.path '' $currentUrl
        Invoke-PwCli -CliArgs @("-s=$session", 'close') -AllowFailure | Out-Null
        return
    }

    Invoke-PwCli -CliArgs @("-s=$session", 'screenshot', '--filename', $screenshotPath, '--full-page') | Out-Null
    Invoke-PwCli -CliArgs @("-s=$session", 'close') -AllowFailure | Out-Null

    Add-Result $Page.name 'PASS' 'page rendered and screenshot saved' $Page.path $screenshotPath $currentUrl
}

Test-Port -Url "$FrontendBase/" -Name 'frontend dev server'

$adminLogin = Invoke-Api 'admin login' 'Post' "$ApiBase/employees/login" @{ phone = '13800138000'; password = '123456' }
$customerLogin = Invoke-Api 'customer login' 'Post' "$ApiBase/users/login" @{ phone = '13900139000'; password = '123456' }
$courierLogin = Invoke-Api 'parttime token' 'Post' "$ApiBase/campus/courier/auth/token" @{ phone = '13900139001'; password = '123456' }

$adminToken = $adminLogin.token
$customerToken = $customerLogin.token
$courierToken = $courierLogin.token

if (-not $adminToken -or -not $customerToken -or -not $courierToken) {
    throw 'Required smoke token is missing.'
}

$adminStorage = @{
    admin_token = $adminToken
    admin_user_info = '{"name":"管理员","avatar":"","role":"校园运营"}'
}
$customerStorage = @{
    customer_token = $customerToken
    customer_user_info = '{"name":"校园用户","phone":"13900139000"}'
}
$parttimeStorage = @{
    courier_token = $courierToken
    courier_profile = '{}'
}

$pages = @(
    [pscustomobject]@{ name = 'admin dashboard'; slug = 'admin-dashboard'; loginPath = '/login'; path = '/dashboard'; storage = $adminStorage },
    [pscustomobject]@{ name = 'admin employee'; slug = 'admin-employee'; loginPath = '/login'; path = '/employee'; storage = $adminStorage },
    [pscustomobject]@{ name = 'admin settlements'; slug = 'admin-settlements'; loginPath = '/login'; path = '/campus/settlements'; storage = $adminStorage },
    [pscustomobject]@{ name = 'admin after-sale executions'; slug = 'admin-after-sale-executions'; loginPath = '/login'; path = '/campus/after-sale-executions'; storage = $adminStorage },
    [pscustomobject]@{ name = 'admin exceptions'; slug = 'admin-exceptions'; loginPath = '/login'; path = '/campus/exceptions'; storage = $adminStorage },
    [pscustomobject]@{ name = 'customer order result'; slug = 'customer-order-result'; loginPath = '/user/login'; path = '/user/campus/order-result?orderId=CR202604060001'; storage = $customerStorage },
    [pscustomobject]@{ name = 'parttime workbench'; slug = 'parttime-workbench'; loginPath = '/parttime/login'; path = '/parttime/workbench'; storage = $parttimeStorage }
)

foreach ($page in $pages) {
    Invoke-PageSmoke -Page $page -StorageItems $page.storage
}

$report = [pscustomobject]@{
    generatedAt = (Get-Date).ToString('s')
    frontendBase = $FrontendBase
    apiBase = $ApiBase
    tokensRedacted = $true
    screenshotDirectory = $RuntimeDir
    pass = ($results | Where-Object { $_.status -eq 'PASS' }).Count
    fail = ($results | Where-Object { $_.status -eq 'FAIL' }).Count
    results = $results
}

$reportPath = Join-Path $RuntimeDir 'browser-smoke-report.json'
$report | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $reportPath -Encoding UTF8
Write-Host "Report: $reportPath"

if ($report.fail -gt 0) {
    exit 1
}
