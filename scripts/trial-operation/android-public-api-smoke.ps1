param(
    [Parameter(Mandatory = $true)]
    [string]$ApiBase,
    [string]$OutputPath = "",
    [int]$TimeoutSec = 12,
    [switch]$AllowFailures,
    [switch]$NoRedact
)

$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
if (-not $OutputPath) {
    $OutputPath = Join-Path $repoRoot "project-logs\campus-relay\runtime\step-121-public-api-base\public-api-smoke.json"
}

function Normalize-ApiBase {
    param([string]$Value)

    $trimmed = $Value.Trim().TrimEnd("/")
    if (-not ($trimmed -match "^https?://")) {
        throw "ApiBase must start with http:// or https://"
    }
    if (-not ($trimmed -match "/api$")) {
        throw "ApiBase must end with /api, for example https://example.com/api"
    }
    return $trimmed
}

function Redact-ApiBase {
    param([string]$Value)

    if ($NoRedact) {
        return $Value
    }

    $uri = [Uri]$Value
    return "$($uri.Scheme)://<redacted>$($uri.AbsolutePath.TrimEnd('/'))"
}

function Invoke-EndpointCheck {
    param(
        [string]$Name,
        [string]$Path,
        [string]$ExpectedMethod = "GET"
    )

    $url = "$script:normalizedBase$Path"
    $startedAt = Get-Date
    try {
        $response = Invoke-WebRequest -Uri $url -Method $ExpectedMethod -UseBasicParsing -TimeoutSec $TimeoutSec
        $content = $response.Content
        $bodyCode = $null
        $bodyMsg = $null

        if ($content) {
            try {
                $json = $content | ConvertFrom-Json
                $bodyCode = $json.code
                $bodyMsg = $json.msg
            } catch {
                $bodyMsg = "non-json response"
            }
        }

        $passed = ($response.StatusCode -eq 200) -and ($bodyCode -eq 200)
        return [ordered]@{
            name = $Name
            path = $Path
            method = $ExpectedMethod
            httpStatus = [int]$response.StatusCode
            bodyCode = $bodyCode
            bodyMsg = $bodyMsg
            passed = $passed
            error = $null
            checkedAt = $startedAt.ToString("s")
        }
    } catch {
        $statusCode = $null
        if ($_.Exception.Response -and $_.Exception.Response.StatusCode) {
            $statusCode = [int]$_.Exception.Response.StatusCode
        }
        return [ordered]@{
            name = $Name
            path = $Path
            method = $ExpectedMethod
            httpStatus = $statusCode
            bodyCode = $null
            bodyMsg = $null
            passed = $false
            error = $_.Exception.Message
            checkedAt = $startedAt.ToString("s")
        }
    }
}

$script:normalizedBase = Normalize-ApiBase $ApiBase
$checks = @(
    Invoke-EndpointCheck "pickup points" "/campus/public/pickup-points"
    Invoke-EndpointCheck "delivery rules" "/campus/public/delivery-rules"
)

$passedCount = ($checks | Where-Object { $_.passed }).Count
$failedCount = $checks.Count - $passedCount
$result = [ordered]@{
    step = "Step 121"
    date = (Get-Date -Format "yyyy-MM-dd")
    purpose = "Android public API base smoke for customer campus order entry dependencies"
    apiBase = Redact-ApiBase $script:normalizedBase
    apiBaseScheme = ([Uri]$script:normalizedBase).Scheme
    checks = $checks
    summary = [ordered]@{
        total = $checks.Count
        passed = $passedCount
        failed = $failedCount
        usableForAndroidPublicSmoke = ($failedCount -eq 0)
    }
    notes = @(
        "This script only checks read-only public dependencies needed by the Android user campus order entry.",
        "It does not create orders, mutate data, change bridge behavior, or store the real host unless -NoRedact is used.",
        "If public endpoints fail, build-mode validation can still pass, but WebView API smoke must stay blocked."
    )
}

$outputDir = Split-Path -Parent $OutputPath
New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
$result | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $OutputPath -Encoding UTF8

Write-Host "Android public API smoke completed: passed=$passedCount failed=$failedCount"
Write-Host "Report: $OutputPath"

if ($failedCount -gt 0 -and -not $AllowFailures) {
    exit 1
}

exit 0
