param(
    [string]$SchemaFile,
    [string]$DataFile,
    [switch]$StrictOptional
)

$ErrorActionPreference = "Stop"
$script:HardFailures = 0
$script:Warnings = 0

function Write-Check {
    param(
        [string]$Name,
        [string]$Status,
        [string]$Message,
        [bool]$Hard = $false
    )

    Write-Host "[$Status] $Name - $Message"

    if ($Status -eq "FAIL") {
        if ($Hard) {
            $script:HardFailures += 1
        } else {
            $script:Warnings += 1
        }
    } elseif ($Status -eq "WARN") {
        $script:Warnings += 1
    }
}

function Test-Text {
    param(
        [string]$Name,
        [string]$Content,
        [string[]]$Patterns,
        [string]$SuccessMessage,
        [string]$FailureMessage,
        [bool]$Hard = $true
    )

    $missing = @()
    foreach ($pattern in $Patterns) {
        if ($Content -notmatch [regex]::Escape($pattern)) {
            $missing += $pattern
        }
    }

    if ($missing.Count -eq 0) {
        Write-Check $Name "OK" $SuccessMessage
    } else {
        $message = "$FailureMessage Missing: $($missing -join ', ')"
        if ($Hard) {
            Write-Check $Name "FAIL" $message $true
        } else {
            Write-Check $Name "WARN" $message
        }
    }
}

$RepoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
if (-not $SchemaFile) {
    $SchemaFile = Join-Path $RepoRoot "backend\src\main\resources\db\schema-h2.sql"
}
if (-not $DataFile) {
    $DataFile = Join-Path $RepoRoot "backend\src\main\resources\db\data-h2.sql"
}

Write-Host "Campus relay trial-operation sample validation"
Write-Host "Schema: $SchemaFile"
Write-Host "Data: $DataFile"

if (-not (Test-Path -LiteralPath $SchemaFile)) {
    Write-Check "schema file" "FAIL" "schema file is missing" $true
}
if (-not (Test-Path -LiteralPath $DataFile)) {
    Write-Check "data file" "FAIL" "data file is missing" $true
}
if ($script:HardFailures -gt 0) {
    exit 1
}

$schema = Get-Content -LiteralPath $SchemaFile -Raw
$data = Get-Content -LiteralPath $DataFile -Raw

Test-Text "admin account seed" $data @("13800138000", "employee") "admin seed account exists" "admin seed account is incomplete"
Test-Text "customer account seed" $data @("13900139000", "campus_customer_profile") "customer seed account exists" "customer seed account is incomplete"
Test-Text "courier account seed" $data @("13900139001", "campus_courier_profile") "existing courier seed account exists" "courier seed account is incomplete"
Test-Text "courier profile seed" $data @("MERGE INTO campus_courier_profile", "APPROVED", "2023123402") "approved courier profile seed exists" "approved courier profile seed is incomplete"
Test-Text "available order seed" $data @("CR202604070002", "WAITING_ACCEPT") "available order seed exists" "available order seed is incomplete"
Test-Text "completed order seed" $data @("CR202604060001", "COMPLETED") "completed order seed exists" "completed order seed is incomplete"
Test-Text "location report seed" $data @("MERGE INTO campus_location_report", "CR202604060001", "106.5515500") "location report seed exists" "location report seed is incomplete"
Test-Text "settlement seed" $data @("MERGE INTO campus_settlement_record", "CR202604060001", "PENDING") "settlement seed exists" "settlement seed is incomplete"

Test-Text "exception history schema" $schema @("CREATE TABLE IF NOT EXISTS campus_exception_record", "process_status", "reported_at") "exception history schema exists" "exception history schema is incomplete"
Test-Text "after-sale history schema" $schema @("CREATE TABLE IF NOT EXISTS campus_after_sale_execution_record", "execution_status", "executed_at") "after-sale execution history schema exists" "after-sale execution history schema is incomplete"
Test-Text "settlement reconcile schema" $schema @("CREATE TABLE IF NOT EXISTS campus_settlement_reconcile_difference_record", "difference_status", "reported_at") "settlement reconcile difference schema exists" "settlement reconcile schema is incomplete"

$optionalHard = [bool]$StrictOptional
Test-Text "after-sale fixed sample" $data @("campus_after_sale_execution_record") "after-sale fixed sample exists" "after-sale fixed sample is not pre-seeded; use page empty state or prepare runtime sample" $optionalHard
Test-Text "exception fixed sample" $data @("campus_exception_record") "exception fixed sample exists" "exception fixed sample is not pre-seeded; create via courier exception-report during runtime validation" $optionalHard
Test-Text "reconcile difference fixed sample" $data @("campus_settlement_reconcile_difference_record") "reconcile difference fixed sample exists" "reconcile difference sample is not pre-seeded; create via admin API during runtime validation" $optionalHard

Write-Host "Sample validation completed with $script:HardFailures hard failure(s) and $script:Warnings warning(s)."

if ($script:HardFailures -gt 0) {
    exit 1
}

if ($script:Warnings -gt 0) {
    exit 2
}

exit 0
