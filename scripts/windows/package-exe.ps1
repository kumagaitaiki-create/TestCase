param(
    [string]$ProjectRoot = (Resolve-Path (Join-Path $PSScriptRoot "../..")).Path,
    [string]$AppName = "CobolAssistant",
    [string]$AppVersion = "1.0.0",
    [string]$Vendor = "YourCompany",
    [string]$MainClass = "Main",
    [string]$JarName = "CobolAssistant.jar",
    [string]$RuntimeDirName = "runtime"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Write-Host "[3/4] Package EXE started"
Write-Host "ProjectRoot: $ProjectRoot"

$buildDir = Join-Path $ProjectRoot "build"
$distDir = Join-Path $ProjectRoot "dist"
$jarPath = Join-Path $buildDir $JarName
$runtimeDir = Join-Path $buildDir $RuntimeDirName
$iconPath = Join-Path $ProjectRoot "packaging/app.ico"

if (-not (Test-Path $jarPath)) {
    throw "JAR not found: $jarPath. Run build-jar.ps1 first."
}

if (-not (Test-Path $runtimeDir)) {
    throw "Runtime image not found: $runtimeDir. Run build-runtime.ps1 first."
}

New-Item -ItemType Directory -Force -Path $distDir | Out-Null

$arguments = @(
    "--type", "exe",
    "--name", $AppName,
    "--app-version", $AppVersion,
    "--vendor", $Vendor,
    "--input", $buildDir,
    "--main-jar", $JarName,
    "--main-class", $MainClass,
    "--runtime-image", $runtimeDir,
    "--dest", $distDir,
    "--win-menu",
    "--win-shortcut"
)

if (Test-Path $iconPath) {
    Write-Host "Using icon: $iconPath"
    $arguments += @("--icon", $iconPath)
} else {
    Write-Host "Icon not found. Place app icon at packaging/app.ico to include it."
}

Write-Host "Running jpackage..."
& jpackage @arguments
if ($LASTEXITCODE -ne 0) {
    throw "jpackage failed. If message mentions WiX, install WiX Toolset and retry."
}

Write-Host "EXE package created under: $distDir"
