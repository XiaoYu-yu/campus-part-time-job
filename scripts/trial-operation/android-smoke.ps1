param(
    [string]$AdbPath = "",
    [string]$DeviceId = "",
    [switch]$ListDevices,
    [switch]$All,
    [switch]$InstallUser,
    [switch]$InstallParttime,
    [switch]$Launch,
    [switch]$CaptureScreenshots,
    [switch]$ClearData,
    [switch]$StartEmulator,
    [string]$AvdName = "campus_api35",
    [int]$EmulatorBootTimeoutSeconds = 360,
    [int]$LaunchWaitSeconds = 8
)

$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
$userPackage = "com.xiaoyu.campus.user"
$parttimePackage = "com.xiaoyu.campus.parttime"
$userApk = Join-Path $repoRoot "mobile\user-app\android\app\build\outputs\apk\debug\app-debug.apk"
$parttimeApk = Join-Path $repoRoot "mobile\parttime-app\android\app\build\outputs\apk\debug\app-debug.apk"
$captureRoot = Join-Path $repoRoot "project-logs\campus-relay\runtime\android-smoke"

if (-not $All -and -not $InstallUser -and -not $InstallParttime -and -not $Launch -and -not $CaptureScreenshots -and -not $ListDevices) {
    $All = $true
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

    throw "emulator.exe not found. Install the Android SDK emulator package first."
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

function Start-EmulatorAndWait {
    $devices = Get-ConnectedDevices
    if ($devices.Count -gt 0) {
        Write-Host "Device already online: $($devices -join ', ')"
        return
    }

    $emulator = Resolve-Emulator
    Write-Host "Starting emulator '$AvdName' with $emulator"
    Start-Process -FilePath $emulator -ArgumentList @("-avd", $AvdName, "-no-snapshot", "-gpu", "swiftshader_indirect", "-no-boot-anim") | Out-Null

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

    throw "Emulator '$AvdName' did not become ready within $EmulatorBootTimeoutSeconds seconds. Run emulator -accel-check and verify Android Emulator Hypervisor Driver or Windows hypervisor setup."
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
        throw "No online Android device or emulator found. Connect a phone with USB debugging enabled or start an emulator, then rerun this script."
    }
    if ($devices.Count -gt 1) {
        throw "Multiple devices are online: $($devices -join ', '). Rerun with -DeviceId <serial>."
    }
}

function Assert-Apk {
    param([string]$Path)
    if (-not (Test-Path $Path)) {
        throw "APK not found: $Path. Build it first with the mobile README commands."
    }
}

function Install-Apk {
    param([string]$ApkPath, [string]$PackageName)

    Assert-Apk $ApkPath
    Write-Host "Installing $PackageName from $ApkPath"
    Invoke-Adb -CommandArgs @("install", "-r", $ApkPath) | Write-Host
    $packagePath = Invoke-Adb -CommandArgs @("shell", "pm", "path", $PackageName)
    if (-not ($packagePath -match "package:")) {
        throw "Package install check failed for $PackageName."
    }
    Write-Host "Installed ${PackageName}: $packagePath"
}

function Launch-App {
    param([string]$PackageName)

    Write-Host "Launching $PackageName"
    Invoke-Adb -CommandArgs @("shell", "monkey", "-p", $PackageName, "-c", "android.intent.category.LAUNCHER", "1") | Write-Host
    Start-Sleep -Seconds $LaunchWaitSeconds
}

function Clear-AppData {
    param([string]$PackageName)

    Write-Host "Clearing app data for $PackageName"
    Invoke-Adb -CommandArgs @("shell", "pm", "clear", $PackageName) | Write-Host
}

function Capture-Screenshot {
    param([string]$Name)

    New-Item -ItemType Directory -Path $captureRoot -Force | Out-Null
    $file = Join-Path $captureRoot ("{0}-{1}.png" -f (Get-Date -Format "yyyyMMdd-HHmmss"), $Name)
    $remoteFile = "/sdcard/$Name.png"
    Invoke-Adb -CommandArgs @("shell", "screencap", "-p", $remoteFile) | Out-Null
    Invoke-Adb -CommandArgs @("pull", $remoteFile, $file) | Write-Host
    Invoke-Adb -CommandArgs @("shell", "rm", $remoteFile) | Out-Null
    Write-Host "Screenshot saved: $file"
}

$script:adb = Resolve-Adb $AdbPath
Write-Host "Using adb: $script:adb"

Write-Host "Connected devices:"
& $script:adb devices -l | Write-Host

if ($ListDevices) {
    exit 0
}

if ($StartEmulator) {
    Start-EmulatorAndWait
}

Assert-Device

if ($All -or $InstallUser) {
    Install-Apk -ApkPath $userApk -PackageName $userPackage
}

if ($All -or $InstallParttime) {
    Install-Apk -ApkPath $parttimeApk -PackageName $parttimePackage
}

if ($All -or $Launch) {
    if ($ClearData) {
        Clear-AppData $userPackage
    }
    Launch-App $userPackage
    if ($All -or $CaptureScreenshots) {
        Capture-Screenshot "user-app-launch"
    }

    if ($ClearData) {
        Clear-AppData $parttimePackage
    }
    Launch-App $parttimePackage
    if ($All -or $CaptureScreenshots) {
        Capture-Screenshot "parttime-app-launch"
    }
}

Write-Host "Android smoke completed. Verify screenshots and login/API behavior manually in the app."
