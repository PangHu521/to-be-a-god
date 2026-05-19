# Deify - Android SDK 安装 & 编译 (清华镜像源)
$ErrorActionPreference = "Stop"
$ProgressPreference = "SilentlyContinue"

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  Deify - SDK 安装 & 编译 (清华源)" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# --- 1. JDK 17 ---
$JDK17 = "D:\dev_env\jdk\jdk-17.0.11"
if (-not (Test-Path "$JDK17\bin\java.exe")) {
    Write-Host "[ERROR] JDK17 not found at $JDK17" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host "[OK] JDK 17: $JDK17" -ForegroundColor Green
$env:JAVA_HOME = $JDK17
$env:PATH = "$JDK17\bin;$env:PATH"

# --- 2. Download & setup Android SDK from Tsinghua mirror ---
$SDK = "D:\android_sdk"
$TUNA = "https://mirrors.tuna.tsinghua.edu.cn/android/repository"

# Package definitions: (url_path, extract_subfolder, target_name)
$packages = @(
    @{
        Name = "Android 34 Platform"
        Url  = "$TUNA/platform-34_r03.zip"
        Dest = "$SDK\platforms\android-34"
    },
    @{
        Name = "Build Tools 34.0.0"
        Url  = "$TUNA/build-tools_r34.0.0-windows.zip"
        Dest = "$SDK\build-tools\34.0.0"
    },
    @{
        Name = "Platform Tools (adb)"
        Url  = "$TUNA/platform-tools_r37.0.0-windows.zip"
        Dest = "$SDK\platform-tools"
    }
)

# Download function
function Download-Extract($name, $url, $dest) {
    $already = (Test-Path "$dest\android.jar") -or (Test-Path "$dest\aapt2.exe") -or (Test-Path "$dest\adb.exe")
    if ($already) {
        Write-Host "[SKIP] $name already installed" -ForegroundColor Gray
        return
    }

    Write-Host "[DOWNLOAD] $name ..." -ForegroundColor Yellow
    $zip = "$env:TEMP\sdk_$($name -replace ' ','_').zip"

    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    $ProgressPreference = "Continue"
    Invoke-WebRequest -Uri $url -OutFile $zip -UseBasicParsing
    $ProgressPreference = "SilentlyContinue"
    Write-Host "  Downloaded $( [math]::Round((Get-Item $zip).Length/1MB, 1) )MB" -ForegroundColor Gray

    Write-Host "  Extracting to $dest ..." -ForegroundColor Gray
    $tmpDir = "$env:TEMP\sdk_extract"
    Remove-Item -Recurse -Force $tmpDir -ErrorAction SilentlyContinue
    Expand-Archive -Path $zip -DestinationPath $tmpDir -Force

    # Handle nested folder structure
    $inner = Get-ChildItem $tmpDir -Directory | Select-Object -First 1
    New-Item -ItemType Directory -Force -Path $dest | Out-Null

    # Try different nested structures
    if (Test-Path "$tmpDir\android-*") {
        Copy-Item -Path "$tmpDir\*" -Destination $dest -Recurse -Force
    } elseif ($inner) {
        Copy-Item -Path "$($inner.FullName)\*" -Destination $dest -Recurse -Force
    } else {
        Copy-Item -Path "$tmpDir\*" -Destination $dest -Recurse -Force
    }

    Remove-Item -Recurse -Force $tmpDir -ErrorAction SilentlyContinue
    Remove-Item -Force $zip -ErrorAction SilentlyContinue
    Write-Host "[OK] $name installed" -ForegroundColor Green
}

# Download all packages
foreach ($pkg in $packages) {
    Download-Extract $pkg.Name $pkg.Url $pkg.Dest
}

# --- 3. Write local.properties ---
"sdk.dir=$SDK" | Out-File -FilePath "$PSScriptRoot\local.properties" -Encoding ASCII
Write-Host "[OK] local.properties" -ForegroundColor Green

# --- 4. Build ---
Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  Building Deify APK (first time downloads Gradle + dependencies)" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan

$env:GRADLE_USER_HOME = "$PSScriptRoot\.gradle_home"
$env:ANDROID_SDK_ROOT = $SDK
Set-Location $PSScriptRoot

& .\gradlew.bat assembleDebug
if ($LASTEXITCODE -ne 0) {
    Write-Host "[BUILD FAILED]" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "  BUILD SUCCESS!" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "APK: app\build\outputs\apk\debug\app-debug.apk"
Write-Host ""
Write-Host "Install to phone:"
Write-Host "  $SDK\platform-tools\adb install app\build\outputs\apk\debug\app-debug.apk"
Write-Host ""
Read-Host "Press Enter to exit"
