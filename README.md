# TestCase

Windows portable packaging with Maven and jpackage
===================================================

This repository now builds the Swing app into a portable Windows app-image through Maven. The GitHub Actions workflow runs `mvn clean package` on a Windows runner and then uses `jpackage` from the JDK.

Prerequisites
-------------

1. JDK 17 or later
2. Maven 3.9 or later
3. Windows runner or local Windows machine for the app-image step

Local build
-----------

Build the JAR only:

```bash
mvn -B clean package -DskipTests
```

Build the portable app-image on Windows:

```bash
mvn -B clean package -DskipTests -Pwindows-app-image
```

The app-image output is written to `target/dist/`.

GitHub Actions
--------------

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

- The workflow installs JDK 21 on the `windows-latest` runner.
- The portable artifact is uploaded from `target/dist/**`.