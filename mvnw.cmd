@REM Maven Wrapper for Windows
@REM Downloads and runs Maven if not already cached

@echo off
setlocal

set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.9"
set "MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd"

if exist "%MAVEN_CMD%" goto runMaven

echo Downloading Maven 3.9.9...

for /f "tokens=1,* delims==" %%a in ('findstr "distributionUrl" "%~dp0.mvn\wrapper\maven-wrapper.properties"') do set "DIST_URL=%%b"

if not exist "%MAVEN_HOME%" mkdir "%MAVEN_HOME%"

set "TEMP_FILE=%TEMP%\maven-download.zip"
powershell -Command "Invoke-WebRequest -Uri '%DIST_URL%' -OutFile '%TEMP_FILE%'"
powershell -Command "Expand-Archive -Path '%TEMP_FILE%' -DestinationPath '%USERPROFILE%\.m2\wrapper\dists' -Force"
del "%TEMP_FILE%" 2>nul

:runMaven
"%MAVEN_CMD%" %*
