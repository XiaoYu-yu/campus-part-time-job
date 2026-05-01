param(
    [ValidateSet("default", "emulator", "lan", "public")]
    [string]$Mode = "public",
    [string]$Jdk21Home = "",
    [string]$OutputDirectory = "",
    [switch]$SkipCapSync
)

$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
$frontendRoot = Join-Path $repoRoot "frontend"
$userRoot = Join-Path $repoRoot "mobile\user-app"
$parttimeRoot = Join-Path $repoRoot "mobile\parttime-app"
$generatedAt = Get-Date -Format "yyyyMMdd-HHmmss"

if (-not $OutputDirectory) {
    $OutputDirectory = Join-Path $repoRoot "project-logs\campus-relay\runtime\android-qa-apks\$generatedAt"
}

function Invoke-CommandChecked {
    param(
        [string]$FilePath,
        [string[]]$CommandArgs,
        [string]$WorkingDirectory
    )

    Write-Host ">> $FilePath $($CommandArgs -join ' ')"
    Push-Location $WorkingDirectory
    try {
        & $FilePath @CommandArgs 2>&1 | ForEach-Object {
            Write-Host $_
        }
        if ($LASTEXITCODE -ne 0) {
            throw "Command failed with exit code ${LASTEXITCODE}: $FilePath $($CommandArgs -join ' ')"
        }
    } finally {
        Pop-Location
    }
}

function Test-Jdk21 {
    param([string]$CandidateHome)

    if (-not $CandidateHome) {
        return $false
    }
    $javac = Join-Path $CandidateHome "bin\javac.exe"
    if (-not (Test-Path $javac)) {
        return $false
    }
    $version = (& $javac -version 2>&1 | Select-Object -First 1).ToString()
    return ($version -match 'javac\s+21\.')
}

function Resolve-Jdk21 {
    param([string]$ExplicitHome)

    $candidates = @()
    if ($ExplicitHome) {
        $candidates += $ExplicitHome
    }
    if ($env:JAVA_HOME) {
        $candidates += $env:JAVA_HOME
    }
    $candidates += "D:\software\jdk-21-temurin"

    $globRoots = @(
        "C:\Program Files\Eclipse Adoptium",
        "C:\Program Files\Java"
    )
    foreach ($root in $globRoots) {
        if (Test-Path $root) {
            Get-ChildItem -Path $root -Directory -Filter "jdk-21*" -ErrorAction SilentlyContinue | ForEach-Object {
                $candidates += $_.FullName
            }
        }
    }

    foreach ($candidate in ($candidates | Where-Object { $_ } | Select-Object -Unique)) {
        if (Test-Jdk21 $candidate) {
            return (Resolve-Path $candidate).Path
        }
    }

    throw "JDK 21 not found. Install JDK 21 or pass -Jdk21Home <path>."
}

function Get-CapSyncScript {
    param([string]$Mode)

    if ($Mode -eq "default") {
        return "cap:sync"
    }
    return "cap:sync:$Mode"
}

function Build-AndroidShell {
    param(
        [string]$Name,
        [string]$Root,
        [string]$PackageName,
        [string]$JdkHome,
        [string]$OutputRoot
    )

    $syncScript = Get-CapSyncScript $Mode
    if (-not $SkipCapSync) {
        Invoke-CommandChecked -FilePath "npm" -CommandArgs @("run", $syncScript) -WorkingDirectory $Root
    } else {
        Write-Host "Skipping Capacitor sync for $Name by request."
    }

    $androidRoot = Join-Path $Root "android"
    $gradle = Join-Path $androidRoot "gradlew.bat"
    if (-not (Test-Path $gradle)) {
        throw "Gradle wrapper not found: $gradle"
    }

    $oldJavaHome = $env:JAVA_HOME
    $oldPath = $env:Path
    try {
        $env:JAVA_HOME = $JdkHome
        $env:Path = "$JdkHome\bin;$oldPath"
        Invoke-CommandChecked -FilePath $gradle -CommandArgs @("assembleDebug") -WorkingDirectory $androidRoot
    } finally {
        $env:JAVA_HOME = $oldJavaHome
        $env:Path = $oldPath
    }

    $sourceApk = Join-Path $androidRoot "app\build\outputs\apk\debug\app-debug.apk"
    if (-not (Test-Path $sourceApk)) {
        throw "Built APK not found: $sourceApk"
    }

    New-Item -ItemType Directory -Path $OutputRoot -Force | Out-Null
    $outputApk = Join-Path $OutputRoot ("campus-{0}-{1}-debug.apk" -f $Name, $Mode)
    Copy-Item -LiteralPath $sourceApk -Destination $outputApk -Force

    $hash = Get-FileHash -Algorithm SHA256 -Path $outputApk
    $item = Get-Item $outputApk

    return [ordered]@{
        name = $Name
        packageName = $PackageName
        mode = $Mode
        sourceApk = $sourceApk
        outputApk = $outputApk
        sha256 = $hash.Hash
        sizeBytes = $item.Length
    }
}

New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null
$jdkHome = Resolve-Jdk21 $Jdk21Home
$gitCommit = (& git -C $repoRoot rev-parse HEAD).Trim()

Write-Host "Repo: $repoRoot"
Write-Host "Mode: $Mode"
Write-Host "JDK 21: $jdkHome"
Write-Host "Output: $OutputDirectory"
Write-Host "Commit: $gitCommit"
Write-Host ""

$artifacts = @()
$artifacts += Build-AndroidShell -Name "user" -Root $userRoot -PackageName "com.xiaoyu.campus.user" -JdkHome $jdkHome -OutputRoot $OutputDirectory
$artifacts += Build-AndroidShell -Name "parttime" -Root $parttimeRoot -PackageName "com.xiaoyu.campus.parttime" -JdkHome $jdkHome -OutputRoot $OutputDirectory

$manifest = [ordered]@{
    generatedAt = (Get-Date).ToString("o")
    repo = $repoRoot.Path
    commit = $gitCommit
    mode = $Mode
    jdkHome = $jdkHome
    cleartextNotice = "Debug QA APKs are for owner-controlled smoke only. Do not distribute as a production release before HTTPS/domain/certificate and cleartext hardening."
    artifacts = $artifacts
}

$manifestPath = Join-Path $OutputDirectory "android-qa-apk-manifest.json"
$manifest | ConvertTo-Json -Depth 5 | Set-Content -Path $manifestPath -Encoding UTF8

Write-Host ""
Write-Host "Android QA APK package completed."
Write-Host "Manifest: $manifestPath"
foreach ($artifact in $artifacts) {
    Write-Host ("{0}: {1} SHA256={2}" -f $artifact.name, $artifact.outputApk, $artifact.sha256)
}
