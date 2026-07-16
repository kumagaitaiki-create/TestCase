# TestCase

Windows EXE packaging with jpackage
===================================

This repository now includes PowerShell scripts for packaging the Swing app as a Windows EXE installer.
No Java source changes are required for this flow.

Added files
-----------

- scripts/windows/build-jar.ps1
- scripts/windows/build-runtime.ps1
- scripts/windows/package-exe.ps1
- scripts/windows/build-all.ps1
- packaging/README.txt

Prerequisites (Windows)
-----------------------

1. JDK 17 or later (must include javac, jar, jdeps, jlink, jpackage)
2. PowerShell
3. Optional: WiX Toolset (only if jpackage requests it)

Quick start (recommended)
-------------------------

Run all steps in order (compile -> jar -> runtime -> exe):

```powershell
cd <your-local-path>\TestCase
powershell -ExecutionPolicy Bypass -File .\scripts\windows\build-all.ps1
```

Output installer:

- dist\CobolAssistant-1.0.0.exe (or similar name)

Step-by-step commands
---------------------

1) Build executable JAR

```powershell
cd <your-local-path>\TestCase
powershell -ExecutionPolicy Bypass -File .\scripts\windows\build-jar.ps1
```

2) Build bundled runtime image (for PCs without Java)

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\windows\build-runtime.ps1
```

3) Package EXE installer

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\windows\package-exe.ps1
```

Useful customization examples
-----------------------------

```powershell
# Change app name/version/vendor at packaging time
powershell -ExecutionPolicy Bypass -File .\scripts\windows\build-all.ps1 `
  -AppName "CobolAssistant" `
  -AppVersion "1.2.0" `
  -Vendor "MyCompany"
```

```powershell
# Change main class or jar name if needed
powershell -ExecutionPolicy Bypass -File .\scripts\windows\build-all.ps1 `
  -MainClass "Main" `
  -JarName "CobolAssistant.jar"
```

Optional app icon
-----------------

Place icon at packaging/app.ico.
If present, package-exe.ps1 automatically uses it.
If absent, packaging still works with default icon.

Troubleshooting
---------------

1. Error: path not found when using cd C:\work\TestCase
	- Replace with your actual local path where this repository exists.

2. Error: Unable to access jarfile ...
	- Check jar name under build directory.

3. Error from jpackage mentioning WiX
	- Install WiX Toolset, reopen terminal, retry package-exe.ps1.

4. Swing app cannot open in Linux dev container
	- This environment is headless. Run the EXE/JAR verification on Windows GUI environment.

Build EXE with GitHub Actions
-----------------------------

You can build a Windows EXE without installing JDK locally by using GitHub Actions.

Workflow file:

- .github/workflows/build-windows-exe.yml

How to run:

1. Push this repository to GitHub.
2. Open the repository on GitHub.
3. Go to Actions -> Build Windows EXE.
4. Click Run workflow.
5. Set appName / appVersion if needed, then run.
6. After success, download artifact named windows-exe.

Notes:

- The workflow installs JDK 21 and WiX Toolset on windows-latest runner.
- Output EXE is uploaded as an artifact from dist/*.exe.