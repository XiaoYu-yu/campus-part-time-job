param(
    [Parameter(Mandatory = $true)]
    [string]$ApiBase,

    [string]$FrontendBase = '',
    [string]$OutputPath = '',
    [int]$TimeoutSec = 15,
    [switch]$NoRedact,
    [switch]$AllowFailures
)

$ErrorActionPreference = 'Stop'

$RepoRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..')
if (-not $OutputPath) {
    $OutputPath = Join-Path $RepoRoot 'project-logs\campus-relay\runtime\step-134-remote-smoke\remote-smoke-report.json'
}

function Normalize-BaseUrl {
    param([string]$Url)

    if (-not $Url) {
        return ''
    }

    return $Url.Trim().TrimEnd('/')
}

function Redact-Url {
    param([string]$Url)

    if (-not $Url) {
        return ''
    }

    if ($NoRedact) {
        return $Url
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

function Add-Result {
    param(
        [string]$Name,
        [string]$Status,
        [string]$Message,
        [string]$Endpoint = ''
    )

    $script:Results += [pscustomobject]@{
        name = $Name
        status = $Status
        message = $Message
        endpoint = Redact-Url $Endpoint
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
        TimeoutSec = $TimeoutSec
    }

    if ($null -ne $Body) {
        $params.Body = ($Body | ConvertTo-Json -Depth 8)
    }

    try {
        $res = Invoke-RestMethod @params
        if ($res.code -eq 200) {
            Add-Result $Name 'PASS' 'code=200' $Url
            return $res.data
        }

        Add-Result $Name 'FAIL' "code=$($res.code), msg=$($res.msg)" $Url
        return $null
    } catch {
        Add-Result $Name 'FAIL' $_.Exception.Message $Url
        return $null
    }
}

function Invoke-FrontendShellCheck {
    param(
        [string]$Name,
        [string]$Url
    )

    try {
        $res = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec $TimeoutSec
        $content = [string]$res.Content
        $looksLikeSpa = $content.Contains('id="app"') -or $content.Contains('/assets/') -or $content.Contains('type="module"')

        if ($res.StatusCode -ge 200 -and $res.StatusCode -lt 300 -and $looksLikeSpa) {
            Add-Result $Name 'PASS' "status=$($res.StatusCode), spa-shell=true" $Url
            return
        }

        Add-Result $Name 'FAIL' "status=$($res.StatusCode), spa-shell=$looksLikeSpa" $Url
    } catch {
        Add-Result $Name 'FAIL' $_.Exception.Message $Url
    }
}

$ApiBase = Normalize-BaseUrl $ApiBase
$FrontendBase = Normalize-BaseUrl $FrontendBase

if (-not $ApiBase.EndsWith('/api')) {
    throw 'ApiBase must end with /api, for example: http://your-host:8080/api or https://your-domain.example.com/api'
}

$Results = @()

Invoke-Api 'public health' 'Get' "$ApiBase/campus/public/health" | Out-Null

$adminLogin = Invoke-Api 'admin login' 'Post' "$ApiBase/employees/login" @{ phone = '13800138000'; password = '123456' }
$customerLogin = Invoke-Api 'customer login' 'Post' "$ApiBase/users/login" @{ phone = '13900139000'; password = '123456' }
$courierLogin = Invoke-Api 'parttime token' 'Post' "$ApiBase/campus/courier/auth/token" @{ phone = '13900139001'; password = '123456' }

$adminToken = $adminLogin.token
$customerToken = $customerLogin.token
$courierToken = $courierLogin.token

if ($adminToken -and $customerToken -and $courierToken) {
    Invoke-Api 'admin employee list' 'Get' "$ApiBase/employees?page=1&size=10" $null $adminToken | Out-Null
    Invoke-Api 'admin settlements list' 'Get' "$ApiBase/campus/admin/settlements?page=1&pageSize=5" $null $adminToken | Out-Null
    Invoke-Api 'admin settlement summary' 'Get' "$ApiBase/campus/admin/settlements/reconcile-summary" $null $adminToken | Out-Null
    Invoke-Api 'admin after-sale executions' 'Get' "$ApiBase/campus/admin/orders/after-sale-executions?page=1&pageSize=5" $null $adminToken | Out-Null
    Invoke-Api 'admin couriers list' 'Get' "$ApiBase/campus/admin/couriers?page=1&pageSize=5" $null $adminToken | Out-Null
    Invoke-Api 'admin exception history list' 'Get' "$ApiBase/campus/admin/exceptions?page=1&pageSize=5" $null $adminToken | Out-Null

    Invoke-Api 'customer onboarding profile' 'Get' "$ApiBase/campus/customer/courier-onboarding/profile" $null $customerToken | Out-Null
    Invoke-Api 'customer token eligibility' 'Get' "$ApiBase/campus/customer/courier-onboarding/token-eligibility" $null $customerToken | Out-Null
    Invoke-Api 'customer completed order detail' 'Get' "$ApiBase/campus/customer/orders/CR202604060001" $null $customerToken | Out-Null

    Invoke-Api 'parttime profile' 'Get' "$ApiBase/campus/courier/profile" $null $courierToken | Out-Null
    Invoke-Api 'parttime review status' 'Get' "$ApiBase/campus/courier/review-status" $null $courierToken | Out-Null
    Invoke-Api 'parttime available orders' 'Get' "$ApiBase/campus/courier/orders/available?page=1&pageSize=5" $null $courierToken | Out-Null
    Invoke-Api 'parttime completed order detail' 'Get' "$ApiBase/campus/courier/orders/CR202604060001" $null $courierToken | Out-Null
} else {
    Add-Result 'protected API checks' 'FAIL' 'required login token missing, skipped protected API checks' $ApiBase
}

if ($FrontendBase) {
    $frontendPaths = @(
        [pscustomobject]@{ name = 'frontend shell home'; path = '/' },
        [pscustomobject]@{ name = 'frontend shell admin login'; path = '/login' },
        [pscustomobject]@{ name = 'frontend shell admin dashboard'; path = '/dashboard' },
        [pscustomobject]@{ name = 'frontend shell admin settlements'; path = '/campus/settlements' },
        [pscustomobject]@{ name = 'frontend shell admin after-sale'; path = '/campus/after-sale-executions' },
        [pscustomobject]@{ name = 'frontend shell admin exceptions'; path = '/campus/exceptions' },
        [pscustomobject]@{ name = 'frontend shell customer result'; path = '/user/campus/order-result?orderId=CR202604060001' },
        [pscustomobject]@{ name = 'frontend shell parttime workbench'; path = '/parttime/workbench' }
    )

    foreach ($entry in $frontendPaths) {
        Invoke-FrontendShellCheck $entry.name "$FrontendBase$($entry.path)"
    }
} else {
    Add-Result 'frontend shell checks' 'SKIP' 'FrontendBase not provided; API-only smoke executed' ''
}

$report = [pscustomobject]@{
    generatedAt = (Get-Date).ToString('s')
    apiBase = Redact-Url $ApiBase
    frontendBase = Redact-Url $FrontendBase
    urlsRedacted = (-not $NoRedact.IsPresent)
    tokensRedacted = $true
    pass = ($Results | Where-Object { $_.status -eq 'PASS' }).Count
    fail = ($Results | Where-Object { $_.status -eq 'FAIL' }).Count
    skip = ($Results | Where-Object { $_.status -eq 'SKIP' }).Count
    results = $Results
}

$reportDir = Split-Path -Parent $OutputPath
New-Item -ItemType Directory -Force -Path $reportDir | Out-Null
$report | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $OutputPath -Encoding UTF8
Write-Host "Report: $OutputPath"

if ($report.fail -gt 0 -and -not $AllowFailures) {
    exit 1
}
