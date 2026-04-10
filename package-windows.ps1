# Empaquetado nativo Windows con runtime incluido (JRE + JavaFX)
#
# Uso:
#   .\package-windows.ps1
#   .\package-windows.ps1 -Type app-image
#   .\package-windows.ps1 -SkipBuild

param(
  [ValidateSet("exe", "app-image")]
  [string]$Type = "exe",

  [string]$MainClass = "game.ui.GameWebApplication",
  [string]$AppName = "dungeon-crawler-patterns",
  [string]$AppVersion = "1.0.0",
  [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"

$ProjectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ProjectDir

function Require-Command {
  param([Parameter(Mandatory = $true)][string]$Name)

  if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
    throw "Comando requerido no encontrado: $Name"
  }
}

function Select-Java17Home {
  if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    $versionOutput = & "$env:JAVA_HOME\bin\java.exe" -version 2>&1
    if ($versionOutput -match 'version "17(\.|\")') {
      return $env:JAVA_HOME
    }
  }

  $candidates = @(
    "C:\Program Files\Eclipse Adoptium\jdk-17*",
    "C:\Program Files\Java\jdk-17*",
    "C:\Program Files\Microsoft\jdk-17*"
  )

  foreach ($pattern in $candidates) {
    $match = Get-ChildItem -Path $pattern -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($match -and (Test-Path "$($match.FullName)\bin\java.exe")) {
      return $match.FullName
    }
  }

  throw "No se encontro Java 17. Configura JAVA_HOME hacia un JDK 17 valido."
}

if (-not (Test-Path "pom.xml")) {
  throw "No se encontro pom.xml en la raiz del proyecto."
}

$javaHome = Select-Java17Home
$env:JAVA_HOME = $javaHome

if (($env:Path -split ';') -notcontains "$javaHome\bin") {
  $env:Path = "$javaHome\bin;$env:Path"
}

Require-Command -Name "mvn"
Require-Command -Name "jlink"
Require-Command -Name "jpackage"

if ($Type -eq "exe") {
  # jpackage en Windows necesita WiX para crear exe/msi.
  if (-not (Get-Command light -ErrorAction SilentlyContinue) -and -not (Get-Command wix -ErrorAction SilentlyContinue)) {
    throw "Para generar .exe necesitas WiX Toolset instalado y en PATH."
  }
}

if (-not $SkipBuild) {
  Write-Host "[1/4] Compilando proyecto y copiando dependencias runtime..."
  & mvn -B -DskipTests clean package dependency:copy-dependencies `
    -DincludeScope=runtime `
    -DoutputDirectory=target/dependency
}
else {
  Write-Host "[1/4] Build omitido por -SkipBuild"
}

$mainJar = Get-ChildItem -Path target -Filter *.jar |
  Where-Object { $_.Name -notmatch 'sources|javadoc' } |
  Select-Object -First 1

if (-not $mainJar) {
  throw "No se encontro el JAR principal en target/."
}

$javafxJars = Get-ChildItem -Path target/dependency -Filter "javafx-*.jar" -File | Sort-Object Name
if (-not $javafxJars -or $javafxJars.Count -eq 0) {
  throw "No se encontraron JARs de JavaFX en target/dependency."
}

$jpackageInput = Join-Path $ProjectDir "target/jpackage-input"
if (Test-Path $jpackageInput) {
  Remove-Item -Path $jpackageInput -Recurse -Force
}
New-Item -Path $jpackageInput -ItemType Directory | Out-Null
Copy-Item -Path $mainJar.FullName -Destination $jpackageInput
Copy-Item -Path "target/dependency/*.jar" -Destination $jpackageInput

$javafxModulePath = ($javafxJars | ForEach-Object { $_.FullName }) -join ";"
$runtimeImage = Join-Path $ProjectDir "target/runtime-image"
$destDir = Join-Path $ProjectDir "target/packages"

if (Test-Path $runtimeImage) {
  Remove-Item -Path $runtimeImage -Recurse -Force
}

$jlinkModules = "java.base,java.desktop,java.logging,java.xml,java.scripting,jdk.jsobject,jdk.unsupported,java.net.http,java.sql,java.naming,javafx.controls,javafx.web,javafx.media"

Write-Host "[2/4] Generando runtime con jlink..."
& jlink `
  --module-path "$javaHome\jmods;$javafxModulePath" `
  --add-modules $jlinkModules `
  --strip-debug `
  --no-header-files `
  --no-man-pages `
  --compress=2 `
  --output $runtimeImage

if (-not (Test-Path $destDir)) {
  New-Item -Path $destDir -ItemType Directory | Out-Null
}

$jpackageArgs = @(
  "--type", $Type,
  "--name", $AppName,
  "--dest", $destDir,
  "--input", $jpackageInput,
  "--main-jar", $mainJar.Name,
  "--main-class", $MainClass,
  "--runtime-image", $runtimeImage,
  "--vendor", "Dungeon Crawler Patterns",
  "--app-version", $AppVersion
)

if ($Type -eq "exe") {
  $jpackageArgs += @("--win-shortcut", "--win-menu", "--win-dir-chooser")
}

Write-Host "[3/4] Generando paquete $Type..."
& jpackage @jpackageArgs

Write-Host "[4/4] Artefactos generados en: $destDir"
Get-ChildItem -Path $destDir -File | Select-Object Name, Length | Format-Table -AutoSize
