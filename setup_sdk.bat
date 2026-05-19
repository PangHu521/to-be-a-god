@echo off
setlocal ENABLEDELAYEDEXPANSION
chcp 65001 >nul
echo ============================================
echo   Deify - Android SDK Ò»¼ü°²×° ^& ±àÒë
echo ============================================
echo.

:: --- 1. JDK 17 ---
set "JDK17=D:\dev_env\jdk\jdk-17.0.11"
if not exist "%JDK17%\bin\java.exe" (
    echo [ERROR] JDK17 not found at %JDK17%
    echo Please edit this script ^(line 10^) and set JDK17 to your JDK17 path.
    pause
    exit /b 1
)
echo [OK] JDK 17: %JDK17%

:: --- 2. Android SDK ---
set "ANDROID_SDK_ROOT=D:\android_sdk"
set "CMDLINE_TOOLS=%ANDROID_SDK_ROOT%\cmdline-tools\latest"

if not exist "%CMDLINE_TOOLS%\bin\sdkmanager.bat" goto :download_sdk
echo [OK] Android SDK already installed: %ANDROID_SDK_ROOT%
goto :install_packages

:download_sdk
echo [DOWNLOAD] Getting Android command-line tools...
set "CMDLINE_ZIP=%TEMP%\cmdline-tools.zip"
set "CMDLINE_URL=https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip"

:: Use certutil as fallback (built into Windows, no PowerShell needed)
certutil -urlcache -split -f "%CMDLINE_URL%" "%CMDLINE_ZIP%" >nul 2>&1
if %ERRORLEVEL% EQU 0 goto :extract_sdk

:: Fallback: PowerShell
echo [RETRY] Trying PowerShell download...
powershell -ExecutionPolicy Bypass -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%CMDLINE_URL%' -OutFile '%CMDLINE_ZIP%'" >nul 2>&1
if %ERRORLEVEL% EQU 0 goto :extract_sdk

echo [ERROR] Cannot download cmdline-tools. Check internet connection.
echo Manual download: https://developer.android.com/studio#command-line-tools-only
echo Extract the zip so that sdkmanager.bat is at:
echo   %CMDLINE_TOOLS%\bin\sdkmanager.bat
pause
exit /b 1

:extract_sdk
echo [EXTRACT] Unzipping...
mkdir "%ANDROID_SDK_ROOT%" 2>nul
mkdir "%CMDLINE_TOOLS%" 2>nul
powershell -ExecutionPolicy Bypass -Command "Expand-Archive -Path '%CMDLINE_ZIP%' -DestinationPath '%TEMP%\cmdline_extract' -Force" >nul 2>&1
set "EXTRACT_DIR=%TEMP%\cmdline_extract\cmdline-tools"
if exist "%EXTRACT_DIR%\bin\sdkmanager.bat" (
    xcopy /E /Y "%EXTRACT_DIR%\*" "%CMDLINE_TOOLS%\" >nul
    rmdir /S /Q "%TEMP%\cmdline_extract" 2>nul
) else (
    :: maybe extracted flat
    xcopy /E /Y "%TEMP%\cmdline_extract\*" "%CMDLINE_TOOLS%\" >nul
    rmdir /S /Q "%TEMP%\cmdline_extract" 2>nul
)
del "%CMDLINE_ZIP%" 2>nul
echo [OK] Command-line tools installed.

:install_packages
echo [INSTALL] Setting up Android SDK packages...
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: Accept licenses
echo y | "%CMDLINE_TOOLS%\bin\sdkmanager.bat" --sdk_root="%ANDROID_SDK_ROOT%" --licenses >nul 2>&1

:: Install packages
call "%CMDLINE_TOOLS%\bin\sdkmanager.bat" --sdk_root="%ANDROID_SDK_ROOT%" "platform-tools" "platforms;android-34" "build-tools;34.0.0"
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] SDK package install failed. Error code: %ERRORLEVEL%
    pause
    exit /b 1
)
echo [OK] SDK packages installed.

:: --- 3. local.properties ---
echo sdk.dir=%ANDROID_SDK_ROOT%> "%~dp0local.properties"
echo [OK] local.properties written.

:: --- 4. Build ---
echo.
echo ============================================
echo   Building Deify APK...
echo ============================================
cd /d "%~dp0"

set "JAVA_HOME=%JDK17%"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "ANDROID_SDK_ROOT=%ANDROID_SDK_ROOT%"
set "GRADLE_USER_HOME=%~dp0.gradle_home"

call gradlew.bat assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [BUILD FAILED] See errors above.
    pause
    exit /b 1
)

echo.
echo ============================================
echo   BUILD SUCCESS!
echo ============================================
echo.
echo APK: app\build\outputs\apk\debug\app-debug.apk
echo.
echo Install to phone:
echo   1. Enable USB Debugging on your phone
echo   2. Connect USB cable
echo   3. Run:
echo      %ANDROID_SDK_ROOT%\platform-tools\adb install app\build\outputs\apk\debug\app-debug.apk
echo.
pause
