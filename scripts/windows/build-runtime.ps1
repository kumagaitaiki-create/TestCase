Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

param(
    [string]$ProjectRoot = (Resolve-Path (Join-Path $PSScriptRoot "../..")).Path,
    [string]$JarName = "CobolAssistant.jar",
    [string]$RuntimeDirName = "runtime"
)

Write-Host "[2/4] Build runtime image started"
Write-Host "ProjectRoot: $ProjectRoot"

$buildDir = Join-Path $ProjectRoot "build"
$jarPath = Join-Path $buildDir $JarName
$runtimeDir = Join-Path $buildDir $RuntimeDirName

if (-not (Test-Path $jarPath)) {
    throw "JAR not found: $jarPath. Run build-jar.ps1 first."
}

if (Test-Path $runtimeDir) {
    Remove-Item -Recurse -Force $runtimeDir
}

Write-Host "Resolving Java modules with jdeps..."
$modules = (& jdeps --ignore-missing-deps --multi-release 17 --print-module-deps $jarPath).Trim()
if ([string]::IsNullOrWhiteSpace($modules)) {
    $modules = "java.base,java.desktop"
}

if ($modules -notmatch "java.desktop") {
    $modules = "$modules,java.desktop"
}

Write-Host "Modules: $modules"
Write-Host "Creating runtime image with jlink..."
& jlink --add-modules $modules --output $runtimeDir
if ($LASTEXITCODE -ne 0) {
    throw "jlink failed."
}

Write-Host "Runtime image created: $runtimeDir"
