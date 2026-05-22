@echo off
setlocal
set "APP_HOME=%~dp0"
if "%APP_HOME:~-1%"=="\" set "APP_HOME=%APP_HOME:~0,-1%"
set "GRADLE_USER_HOME=%APP_HOME%\.gradle-home"
set "GRADLE_VERSION=8.11.1"
set "DIST_DIR=%GRADLE_USER_HOME%\codex-wrapper\gradle-%GRADLE_VERSION%-bin"
set "GRADLE_EXE=%DIST_DIR%\gradle-%GRADLE_VERSION%\bin\gradle.bat"
set "ZIP_FILE=%DIST_DIR%\gradle-%GRADLE_VERSION%-bin.zip"

if not exist "%GRADLE_EXE%" (
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; New-Item -ItemType Directory -Force -Path $env:DIST_DIR | Out-Null; Invoke-WebRequest -Uri ('https://services.gradle.org/distributions/gradle-' + $env:GRADLE_VERSION + '-bin.zip') -OutFile $env:ZIP_FILE; Expand-Archive -Path $env:ZIP_FILE -DestinationPath $env:DIST_DIR -Force"
)

call "%GRADLE_EXE%" -p "%APP_HOME%" %*
