@echo off
REM ============================================================================
REM Dungeon Crawler Patterns - Launcher Windows
REM ============================================================================
REM Script para ejecutar el juego en Windows
REM Requiere: Java 17 (OpenJDK/Temurin)
REM
REM USO: 
REM   1. Ejecuta este archivo desde cualquier ubicacion
REM   2. O invocalo desde la raiz: scripts\play.bat
REM
REM ============================================================================

setlocal enabledelayedexpansion
title Dungeon Crawler Patterns - Loading...
pushd "%~dp0\.." >nul

REM Colores (Windows 10+)
for /F %%A in ('copy /Z "%~f0" nul') do set "BS=%%A"

echo.
echo ==============================================================
echo    %BS%[92m█████╗ ██╗   ██╗███╗   ██╗ ██████╗ ███████╗ ██████╗ ██╗   ██╗ ██████╗ ██╗   ██╗%BS%[0m
echo    %BS%[92m██╔══██╗██║   ██║████╗  ██║██╔════╝ ██╔════╝██╔═══██╗██║   ██║██╔════╝ ██║   ██║%BS%[0m
echo    %BS%[92m██║  ██║██║   ██║██╔██╗ ██║██║  ███╗█████╗ ██║   ██║██║   ██║██║  ███╗██║   ██║%BS%[0m
echo    %BS%[92m██║  ██║██║   ██║██║╚██╗██║██║   ██║██╔══╝ ██║   ██║██║   ██║██║   ██║██║   ██║%BS%[0m
echo    %BS%[92m██████╔╝╚██████╔╝██║ ╚████║╚██████╔╝███████╗╚██████╔╝╚██████╔╝╚██████╔╝╚██████╔╝%BS%[0m
echo    %BS%[92m╚═════╝  ╚═════╝ ╚═╝  ╚═══╝ ╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝  ╚═════╝  ╚═════╝ %BS%[0m
echo ==============================================================
echo.

REM ============================================================================
REM Detectar Java
REM ============================================================================

echo Buscando Java 17...
echo.

set "JAVA_CMD="

REM Opción 1: Usar JAVA_HOME si está definido
if defined JAVA_HOME (
    if exist "!JAVA_HOME!\bin\java.exe" (
        set "JAVA_CMD=!JAVA_HOME!\bin\java.exe"
        goto :check_version
    )
)

REM Opción 2: Buscar en PATH
for /F "tokens=*" %%i in ('where java.exe 2^>nul') do (
    set "JAVA_CMD=%%i"
    goto :check_version
)

REM Opción 3: Buscar en rutas comunes
for %%P in (
    "C:\Program Files\Eclipse Adoptium\jdk-*"
    "C:\Program Files\Java\jdk-17*"
    "C:\Program Files\Microsoft\jdk-17*"
    "C:\Program Files (x86)\Java\jdk-17*"
) do (
    if exist %%P\bin\java.exe (
        set "JAVA_CMD=%%P\bin\java.exe"
        goto :check_version
    )
)

REM Java no encontrado
echo.
echo %BS%[91m╔════════════════════════════════════════════════════════════════╗%BS%[0m
echo %BS%[91m║%BS%[0m ERROR: Java 17 no encontrado en el sistema                    %BS%[91m║%BS%[0m
echo %BS%[91m║%BS%[0m                                                                %BS%[91m║%BS%[0m
echo %BS%[91m║%BS%[0m Descarga Java 17 desde:                                       %BS%[91m║%BS%[0m
echo %BS%[91m║%BS%[0m • https://adoptium.net/ (Recomendado)                         %BS%[91m║%BS%[0m
echo %BS%[91m║%BS%[0m • https://www.microsoft.com/openjdk                           %BS%[91m║%BS%[0m
echo %BS%[91m║%BS%[0m                                                                %BS%[91m║%BS%[0m
echo %BS%[91m║%BS%[0m O instala con Chocolatey:                                    %BS%[91m║%BS%[0m
echo %BS%[91m║%BS%[0m   choco install temurin17jdk                                  %BS%[91m║%BS%[0m
echo %BS%[91m╚════════════════════════════════════════════════════════════════╝%BS%[0m
echo.
pause
exit /b 1

:check_version
echo Java encontrado: !JAVA_CMD!
echo.
!JAVA_CMD! -version
echo.

REM ============================================================================
REM Verificar archivos necesarios
REM ============================================================================

echo Verificando archivos del proyecto...
echo.

if not exist "target\dungeon-crawler-patterns-1.0-SNAPSHOT.jar" (
    echo %BS%[91m✗ Error: JAR no encontrado%BS%[0m
    echo   Ejecuta primero: mvn clean package
    echo.
    pause
    exit /b 1
)
echo %BS%[92m✓ JAR encontrado%BS%[0m

if not exist "target\dependency" (
    echo %BS%[91m✗ Error: Dependencias no encontradas%BS%[0m
    echo   Ejecuta primero: mvn dependency:copy-dependencies -DincludeScope=runtime
    echo.
    pause
    exit /b 1
)
echo %BS%[92m✓ Dependencias encontradas%BS%[0m
echo.

REM ============================================================================
REM Construir classpath
REM ============================================================================

setlocal enabledelayedexpansion
set "CLASSPATH=target\dungeon-crawler-patterns-1.0-SNAPSHOT.jar"
for /R "target\dependency" %%f in (*.jar) do (
    set "CLASSPATH=!CLASSPATH!;%%f"
)

REM ============================================================================
REM Ejecutar el juego
REM ============================================================================

echo.
echo %BS%[92m═════════════════════════════════════════════════════════════════%BS%[0m
echo %BS%[92m  Iniciando Dungeon Crawler Patterns...%BS%[0m
echo %BS%[92m═════════════════════════════════════════════════════════════════%BS%[0m
echo.

title Dungeon Crawler Patterns - En Ejecución

!JAVA_CMD! ^
    -cp "!CLASSPATH!" ^
    game.ui.GameWebApplication

REM ============================================================================
REM Fin
REM ============================================================================

if errorlevel 1 (
    echo.
    echo %BS%[91m✗ El juego finalizó con un error%BS%[0m
    echo.
    pause
    exit /b 1
) else (
    echo.
    echo %BS%[92m✓ Juego finalizado correctamente%BS%[0m
    echo.
)

endlocal
exit /b 0
