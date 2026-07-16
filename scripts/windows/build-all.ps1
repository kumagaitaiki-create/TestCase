Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

param(
    [string]$ProjectRoot = (Resolve-Path (Join-Path $PSScriptRoot "../..")).Path,
    [string]$AppName = "CobolAssistant",
    [string]$AppVersion = "1.0.0",
    [string]$Vendor = "YourCompany",
    [string]$MainClass = "Main",
    [string]$JarName = "CobolAssistant.jar"
)

Write-Host "[0/4] Full packaging flow started"

& (Join-Path $PSScriptRoot "build-jar.ps1") `
    -ProjectRoot $ProjectRoot `
    -MainClass $MainClass `
    -JarName $JarName

& (Join-Path $PSScriptRoot "build-runtime.ps1") `
    -ProjectRoot $ProjectRoot `
    -JarName $JarName

& (Join-Path $PSScriptRoot "package-exe.ps1") `
    -ProjectRoot $ProjectRoot `
    -AppName $AppName `
    -AppVersion $AppVersion `
    -Vendor $Vendor `
    -MainClass $MainClass `
    -JarName $JarName

Write-Host "[4/4] Done. Check dist directory for EXE installer."
