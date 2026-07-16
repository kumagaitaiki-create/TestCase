Optional packaging resources
==========================

- Place Windows app icon at: packaging/app.ico
- If this file exists, package-exe.ps1 will pass it to jpackage via --icon.
- If it does not exist, EXE is still generated without a custom icon.
