$ErrorActionPreference = 'Stop'
$base = 'http://127.0.0.1:8080/api'
$runtimeDir = 'D:\20278\code\Campus part-time job\project-logs\campus-relay\runtime\step-131-local-smoke'
New-Item -ItemType Directory -Force -Path $runtimeDir | Out-Null

$results = @()

function Add-Result {
    param(
        [string]$Name,
        [string]$Status,
        [string]$Message,
        [string]$Endpoint = ''
    )
    $script:results += [pscustomobject]@{
        name = $Name
        status = $Status
        message = $Message
        endpoint = $Endpoint
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

$adminLogin = Invoke-Api 'admin login' 'Post' "$base/employees/login" @{ phone='13800138000'; password='123456' }
$customerLogin = Invoke-Api 'customer login' 'Post' "$base/users/login" @{ phone='13900139000'; password='123456' }
$courierLogin = Invoke-Api 'parttime token' 'Post' "$base/campus/courier/auth/token" @{ phone='13900139001'; password='123456' }

$adminToken = $adminLogin.token
$customerToken = $customerLogin.token
$courierToken = $courierLogin.token
if (-not $adminToken -or -not $customerToken -or -not $courierToken) {
    throw 'token missing'
}

Invoke-Api 'admin employee list' 'Get' "$base/employees?page=1&size=10" $null $adminToken | Out-Null
Invoke-Api 'admin settlements list' 'Get' "$base/campus/admin/settlements?page=1&pageSize=5" $null $adminToken | Out-Null
Invoke-Api 'admin settlement summary' 'Get' "$base/campus/admin/settlements/reconcile-summary" $null $adminToken | Out-Null
Invoke-Api 'admin after-sale executions' 'Get' "$base/campus/admin/orders/after-sale-executions?page=1&pageSize=5" $null $adminToken | Out-Null
Invoke-Api 'admin couriers list' 'Get' "$base/campus/admin/couriers?page=1&pageSize=5" $null $adminToken | Out-Null
Invoke-Api 'admin exception history list' 'Get' "$base/campus/admin/exceptions?page=1&pageSize=5" $null $adminToken | Out-Null

Invoke-Api 'customer onboarding profile' 'Get' "$base/campus/customer/courier-onboarding/profile" $null $customerToken | Out-Null
Invoke-Api 'customer token eligibility' 'Get' "$base/campus/customer/courier-onboarding/token-eligibility" $null $customerToken | Out-Null
Invoke-Api 'customer completed order detail' 'Get' "$base/campus/customer/orders/CR202604060001" $null $customerToken | Out-Null

Invoke-Api 'parttime profile' 'Get' "$base/campus/courier/profile" $null $courierToken | Out-Null
Invoke-Api 'parttime review status' 'Get' "$base/campus/courier/review-status" $null $courierToken | Out-Null
Invoke-Api 'parttime available orders' 'Get' "$base/campus/courier/orders/available?page=1&pageSize=5" $null $courierToken | Out-Null
Invoke-Api 'parttime completed order detail' 'Get' "$base/campus/courier/orders/CR202604060001" $null $courierToken | Out-Null

$report = [pscustomobject]@{
    generatedAt = (Get-Date).ToString('s')
    baseUrl = $base
    tokensRedacted = $true
    pass = ($results | Where-Object status -eq 'PASS').Count
    fail = ($results | Where-Object status -eq 'FAIL').Count
    results = $results
}
$reportPath = Join-Path $runtimeDir 'api-smoke-report.json'
$report | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $reportPath -Encoding UTF8
Write-Host "Report: $reportPath"

if ($report.fail -gt 0) {
    exit 1
}
