Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

param(
    [string]$ProjectRoot = (Resolve-Path (Join-Path $PSScriptRoot "../..")).Path,
    [string]$MainClass = "Main",
    [string]$JarName = "CobolAssistant.jar"
)

Write-Host "[1/4] Build JAR started"
Write-Host "ProjectRoot: $ProjectRoot"

$srcDir = Join-Path $ProjectRoot "src"
$buildDir = Join-Path $ProjectRoot "build"
$classesDir = Join-Path $buildDir "classes"
$manifestPath = Join-Path $buildDir "manifest.mf"
$jarPath = Join-Path $buildDir $JarName

if (-not (Test-Path $srcDir)) {
    throw "src directory not found: $srcDir"
}

New-Item -ItemType Directory -Force -Path $buildDir | Out-Null
New-Item -ItemType Directory -Force -Path $classesDir | Out-Null

Write-Host "Compiling Java sources..."
& javac -encoding UTF-8 -d $classesDir (Join-Path $srcDir "*.java")
if ($LASTEXITCODE -ne 0) {
    throw "javac failed."
}

$manifest = @(
    "Main-Class: $MainClass",
    ""
)
Set-Content -Path $manifestPath -Value $manifest -Encoding ascii

Write-Host "Creating executable JAR..."
& jar --create --file $jarPath --manifest $manifestPath -C $classesDir .
if ($LASTEXITCODE -ne 0) {
    throw "jar create failed."
}

Write-Host "JAR created: $jarPath"
