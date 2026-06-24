param(
    [Parameter(Mandatory = $true)]
    [int]$VersionCode,
    [Parameter(Mandatory = $true)]
    [string]$VersionName,
    [string]$ApiBase = "https://xiaoyu.xin/api",
    [string]$Jdk21Home = "",
    [string]$OutputDirectory = "",
    [switch]$RequireSigning,
    [switch]$SkipCapSync
)

$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
$frontendRoot = Join-Path $repoRoot "frontend"
$userRoot = Join-Path $repoRoot "mobile\user-app"
$parttimeRoot = Join-Path $repoRoot "mobile\parttime-app"
$generatedAt = Get-Date -Format "yyyyMMdd-HHmmss"

if ($VersionCode -lt 1) {
    throw "VersionCode must be a positive integer."
}
if ($VersionName -notmatch "^[0-9A-Za-z][0-9A-Za-z._-]{0,49}$") {
    throw "VersionName must be 1-50 characters using letters, digits, dot, underscore or hyphen."
}

$normalizedApiBase = $ApiBase.Trim().TrimEnd("/")
if ($normalizedApiBase -notmatch "^https://" -or $normalizedApiBase -notmatch "/api$") {
    throw "Release ApiBase must use HTTPS and end with /api."
}

if (-not $OutputDirectory) {
    $OutputDirectory = Join-Path $repoRoot "project-logs\campus-relay\runtime\android-release-candidates\$generatedAt"
}

function Invoke-CommandChecked {
    param(
        [string]$FilePath,
        [string[]]$CommandArgs,
        [string]$WorkingDirectory
    )

    Write-Host ">> $FilePath $($CommandArgs -join ' ')"
    Push-Location $WorkingDirectory
    $previousErrorActionPreference = $ErrorActionPreference
    try {
        $ErrorActionPreference = "Continue"
        $commandOutput = & $FilePath @CommandArgs 2>&1
        $exitCode = $LASTEXITCODE
        $commandOutput | ForEach-Object { Write-Host $_ }
        if ($exitCode -ne 0) {
            throw "Command failed with exit code ${exitCode}: $FilePath $($CommandArgs -join ' ')"
        }
    } finally {
        $ErrorActionPreference = $previousErrorActionPreference
        Pop-Location
    }
}

function Test-Jdk21 {
    param([string]$CandidateHome)
    if (-not $CandidateHome) { return $false }
    $javac = Join-Path $CandidateHome "bin\javac.exe"
    if (-not (Test-Path $javac)) { return $false }
    return ((& $javac -version 2>&1 | Select-Object -First 1).ToString() -match "javac\s+21\.")
}

function Resolve-Jdk21 {
    param([string]$ExplicitHome)

    $candidates = @($ExplicitHome, $env:JAVA_HOME, "D:\software\jdk-21-temurin") |
        Where-Object { $_ } |
        Select-Object -Unique
    foreach ($candidate in $candidates) {
        if (Test-Jdk21 $candidate) {
            return (Resolve-Path $candidate).Path
        }
    }
    throw "JDK 21 not found. Install JDK 21 or pass -Jdk21Home."
}

function Resolve-AndroidSdk {
    $candidates = @($env:ANDROID_HOME, $env:ANDROID_SDK_ROOT, (Join-Path $env:LOCALAPPDATA "Android\Sdk")) |
        Where-Object { $_ } |
        Select-Object -Unique
    foreach ($candidate in $candidates) {
        if (Test-Path (Join-Path $candidate "build-tools")) {
            return (Resolve-Path $candidate).Path
        }
    }
    throw "Android SDK not found. Set ANDROID_HOME / ANDROID_SDK_ROOT."
}

function Resolve-BuildTool {
    param([string]$SdkRoot, [string]$FileName)

    $tool = Get-ChildItem -LiteralPath (Join-Path $SdkRoot "build-tools") -Directory |
        Sort-Object { [version]$_.Name } -Descending |
        ForEach-Object { Join-Path $_.FullName $FileName } |
        Where-Object { Test-Path $_ } |
        Select-Object -First 1
    if (-not $tool) {
        throw "Android build tool not found: $FileName"
    }
    return $tool
}

function Set-ReleaseEnv {
    param([string]$Path)
    $content = "VITE_API_BASE_URL=$normalizedApiBase`nVITE_USE_MOCK=false`n"
    [IO.File]::WriteAllText($Path, $content, [Text.UTF8Encoding]::new($false))
}

function Get-BadgingValue {
    param([string]$Badging, [string]$Pattern, [string]$Name)
    $match = [regex]::Match($Badging, $Pattern)
    if (-not $match.Success) {
        throw "Could not read $Name from APK badging."
    }
    return $match.Groups[1].Value
}

function Build-ReleaseApp {
    param(
        [string]$Name,
        [string]$Root,
        [string]$ExpectedPackage,
        [string]$ExpectedLabel,
        [string]$JdkHome,
        [string]$Aapt2,
        [string]$ApkSigner
    )

    if (-not $SkipCapSync) {
        Invoke-CommandChecked -FilePath "npm" -CommandArgs @("run", "cap:sync:public") -WorkingDirectory $Root
    }

    $androidRoot = Join-Path $Root "android"
    $gradle = Join-Path $androidRoot "gradlew.bat"
    $gradleArgs = @(
        "assembleRelease",
        "bundleRelease",
        "-PCAMPUS_VERSION_CODE=$VersionCode",
        "-PCAMPUS_VERSION_NAME=$VersionName",
        "-PCAMPUS_REQUIRE_RELEASE_SIGNING=$($RequireSigning.IsPresent.ToString().ToLowerInvariant())",
        "--no-daemon"
    )

    $oldJavaHome = $env:JAVA_HOME
    $oldPath = $env:Path
    try {
        $env:JAVA_HOME = $JdkHome
        $env:Path = "$JdkHome\bin;$oldPath"
        Invoke-CommandChecked -FilePath $gradle -CommandArgs $gradleArgs -WorkingDirectory $androidRoot
    } finally {
        $env:JAVA_HOME = $oldJavaHome
        $env:Path = $oldPath
    }

    $releaseApkDirectory = Join-Path $androidRoot "app\build\outputs\apk\release"
    $sourceApk = @(
        (Join-Path $releaseApkDirectory "app-release.apk"),
        (Join-Path $releaseApkDirectory "app-release-unsigned.apk")
    ) | Where-Object { Test-Path $_ } | Select-Object -First 1
    $sourceAab = Join-Path $androidRoot "app\build\outputs\bundle\release\app-release.aab"
    if (-not $sourceApk) { throw "Release APK was not generated for $Name." }
    if (-not (Test-Path $sourceAab)) { throw "Release AAB was not generated for $Name." }

    $badging = (& $Aapt2 dump badging $sourceApk 2>&1) -join "`n"
    if ($LASTEXITCODE -ne 0) { throw "aapt2 badging failed for $Name." }
    $packageName = Get-BadgingValue $badging "package: name='([^']+)'" "package name"
    $actualVersionCode = Get-BadgingValue $badging "versionCode='([^']+)'" "versionCode"
    $actualVersionName = Get-BadgingValue $badging "versionName='([^']+)'" "versionName"
    $label = Get-BadgingValue $badging "application-label:'([^']+)'" "application label"

    if ($packageName -ne $ExpectedPackage -or $label -ne $ExpectedLabel) {
        throw "APK identity mismatch for ${Name}: package=$packageName label=$label"
    }
    if ($actualVersionCode -ne [string]$VersionCode -or $actualVersionName -ne $VersionName) {
        throw "APK version mismatch for ${Name}: code=$actualVersionCode name=$actualVersionName"
    }

    & $ApkSigner verify --verbose $sourceApk *> $null
    $signed = $LASTEXITCODE -eq 0
    if ($RequireSigning -and -not $signed) {
        throw "Release APK is not signed: $sourceApk"
    }

    $certificateSha256 = $null
    if ($signed) {
        $certificateOutput = (& $ApkSigner verify --print-certs $sourceApk 2>&1) -join "`n"
        $certificateMatch = [regex]::Match($certificateOutput, "certificate SHA-256 digest:\s*([0-9a-fA-F]+)")
        if ($certificateMatch.Success) {
            $certificateSha256 = $certificateMatch.Groups[1].Value.ToUpperInvariant()
        }
    }

    $mergedManifest = Get-ChildItem -LiteralPath (Join-Path $androidRoot "app\build\intermediates\merged_manifests\release") `
        -Recurse -Filter "AndroidManifest.xml" |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1
    if (-not $mergedManifest) { throw "Merged release manifest not found for $Name." }
    $manifestText = Get-Content -LiteralPath $mergedManifest.FullName -Raw
    if ($manifestText -notmatch 'android:usesCleartextTraffic="false"') {
        throw "Release manifest still allows cleartext traffic for $Name."
    }
    if ($manifestText -notmatch 'android:allowBackup="false"') {
        throw "Release manifest still allows Android backup for $Name."
    }

    $generatedCapacitorConfig = Join-Path $androidRoot "app\src\main\assets\capacitor.config.json"
    $capacitorConfig = Get-Content -LiteralPath $generatedCapacitorConfig -Raw | ConvertFrom-Json
    if ($capacitorConfig.server.androidScheme -ne "https" -or $capacitorConfig.server.cleartext -ne $false) {
        throw "Generated public Capacitor config is not HTTPS-only for $Name."
    }

    New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null
    $signedSuffix = if ($signed) { "signed" } else { "unsigned" }
    $outputApk = Join-Path $OutputDirectory "campus-$Name-$VersionName-$VersionCode-release-$signedSuffix.apk"
    $outputAab = Join-Path $OutputDirectory "campus-$Name-$VersionName-$VersionCode-release-$signedSuffix.aab"
    Copy-Item -LiteralPath $sourceApk -Destination $outputApk -Force
    Copy-Item -LiteralPath $sourceAab -Destination $outputAab -Force

    return [ordered]@{
        name = $Name
        packageName = $packageName
        applicationLabel = $label
        versionCode = [int]$actualVersionCode
        versionName = $actualVersionName
        apiBase = $normalizedApiBase
        androidScheme = $capacitorConfig.server.androidScheme
        cleartext = [bool]$capacitorConfig.server.cleartext
        manifestAllowsCleartext = $false
        androidBackupAllowed = $false
        signed = $signed
        certificateSha256 = $certificateSha256
        apk = [ordered]@{
            path = $outputApk
            sizeBytes = (Get-Item $outputApk).Length
            sha256 = (Get-FileHash -Algorithm SHA256 $outputApk).Hash
        }
        aab = [ordered]@{
            path = $outputAab
            sizeBytes = (Get-Item $outputAab).Length
            sha256 = (Get-FileHash -Algorithm SHA256 $outputAab).Hash
        }
    }
}

$jdkHome = Resolve-Jdk21 $Jdk21Home
$androidSdk = Resolve-AndroidSdk
$aapt2 = Resolve-BuildTool $androidSdk "aapt2.exe"
$apkSigner = Resolve-BuildTool $androidSdk "apksigner.bat"
$userEnvPath = Join-Path $frontendRoot ".env.android-user-public"
$parttimeEnvPath = Join-Path $frontendRoot ".env.android-parttime-public"
$envSnapshots = @{}
foreach ($path in @($userEnvPath, $parttimeEnvPath)) {
    $envSnapshots[$path] = if (Test-Path $path) { Get-Content -LiteralPath $path -Raw } else { $null }
}

try {
    Set-ReleaseEnv $userEnvPath
    Set-ReleaseEnv $parttimeEnvPath
    $artifacts = @()
    $artifacts += Build-ReleaseApp -Name "user" -Root $userRoot -ExpectedPackage "com.xiaoyu.campus.user" `
        -ExpectedLabel "用户端" -JdkHome $jdkHome -Aapt2 $aapt2 -ApkSigner $apkSigner
    $artifacts += Build-ReleaseApp -Name "parttime" -Root $parttimeRoot -ExpectedPackage "com.xiaoyu.campus.parttime" `
        -ExpectedLabel "兼职端" -JdkHome $jdkHome -Aapt2 $aapt2 -ApkSigner $apkSigner
} finally {
    foreach ($path in @($userEnvPath, $parttimeEnvPath)) {
        if ($null -eq $envSnapshots[$path]) {
            Remove-Item -LiteralPath $path -Force -ErrorAction SilentlyContinue
        } else {
            [IO.File]::WriteAllText($path, $envSnapshots[$path], [Text.UTF8Encoding]::new($false))
        }
    }
}

$manifest = [ordered]@{
    name = "Campus Android release artifacts"
    generatedAt = (Get-Date).ToString("o")
    commit = (& git -C $repoRoot rev-parse HEAD).Trim()
    versionCode = $VersionCode
    versionName = $VersionName
    apiBase = $normalizedApiBase
    signingRequired = $RequireSigning.IsPresent
    jdkHome = $jdkHome
    androidSdk = $androidSdk
    artifacts = $artifacts
}
$manifestPath = Join-Path $OutputDirectory "android-release-manifest.json"
$manifest | ConvertTo-Json -Depth 10 | Set-Content -LiteralPath $manifestPath -Encoding UTF8

Write-Host "Android release build completed."
Write-Host "Manifest: $manifestPath"
foreach ($artifact in $artifacts) {
    Write-Host "$($artifact.name): signed=$($artifact.signed) APK=$($artifact.apk.path)"
}
