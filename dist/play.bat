@echo off
REM Script para ejecutar Dungeon Crawler Patterns v2.0.0 en Windows

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set JAR_FILE=%SCRIPT_DIR%dungeon-crawler-patterns-2.0.0.jar

if not exist "%JAR_FILE%" (
    echo Error: JAR file not found at %JAR_FILE%
    exit /b 1
)

REM Buscar Java 17
set JAVA_CMD=java

if defined JAVA_HOME (
    if exist "!JAVA_HOME!\bin\java.exe" (
        set JAVA_CMD=!JAVA_HOME!\bin\java.exe
    )
)

where /q %JAVA_CMD%
if errorlevel 1 (
    echo Error: Java 17 or higher is required. Please install Java 17 or set JAVA_HOME.
    exit /b 1
)

echo Starting Dungeon Crawler Patterns v2.0.0...
echo Java: %JAVA_CMD%
"%JAVA_CMD%" -jar "%JAR_FILE%"

endlocal
