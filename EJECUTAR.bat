@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "REPO_DIR=%~dp0"
if "%REPO_DIR:~-1%"=="\" set "REPO_DIR=%REPO_DIR:~0,-1%"

set "SRC_DIR=%REPO_DIR%\src\main\java"
set "OUT_DIR=%REPO_DIR%\out-test"
set "OPENJFX_REPO=%USERPROFILE%\.m2\repository\org\openjfx"
set "JAVAFX_VERSION=17.0.2"

echo [SICP_Via] Preparando ejecucion...

where javac >nul 2>&1
if errorlevel 1 (
    echo [ERROR] No se encontro javac en PATH. Instala/activa JDK 17.
    exit /b 1
)

where java >nul 2>&1
if errorlevel 1 (
    echo [ERROR] No se encontro java en PATH. Instala/activa JDK 17.
    exit /b 1
)

if not exist "%OPENJFX_REPO%" (
    echo [ERROR] No existe %OPENJFX_REPO%
    echo         Ejecuta una vez la app con Maven o descarga dependencias OpenJFX.
    exit /b 1
)

if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
mkdir "%OUT_DIR%" >nul 2>&1

set "MODULE_PATH="
for /r "%OPENJFX_REPO%" %%F in (*.jar) do (
    echo %%~fF | findstr /i /c:"%JAVAFX_VERSION%" >nul
    if not errorlevel 1 (
        echo %%~nxF | findstr /i /v "sources javadoc" >nul
        if not errorlevel 1 (
            if defined MODULE_PATH (
                set "MODULE_PATH=!MODULE_PATH!;%%~fF"
            ) else (
                set "MODULE_PATH=%%~fF"
            )
        )
    )
)

if not defined MODULE_PATH (
    echo [ERROR] No se encontraron jars JavaFX %JAVAFX_VERSION% en %OPENJFX_REPO%
    exit /b 1
)

set "SRC_LIST=%TEMP%\sicpvia-sources-%RANDOM%%RANDOM%.txt"
if exist "%SRC_LIST%" del /f /q "%SRC_LIST%" >nul 2>&1

for /r "%SRC_DIR%" %%F in (*.java) do echo %%~fF>>"%SRC_LIST%"

echo [SICP_Via] Compilando...
javac -encoding UTF-8 --module-path "%MODULE_PATH%" --add-modules javafx.controls -d "%OUT_DIR%" @"%SRC_LIST%"
set "COMPILE_EXIT=%ERRORLEVEL%"
del /f /q "%SRC_LIST%" >nul 2>&1

if not "%COMPILE_EXIT%"=="0" (
    echo [ERROR] Fallo la compilacion.
    exit /b %COMPILE_EXIT%
)

REM Copiar recursos (css, fonts, imgs) al directorio de salida
set "RES_DIR=%REPO_DIR%\src\main\resources"
if exist "%RES_DIR%\css" xcopy /s /y /q "%RES_DIR%\css" "%OUT_DIR%\css\" >nul 2>&1
if exist "%RES_DIR%\fonts" xcopy /s /y /q "%RES_DIR%\fonts" "%OUT_DIR%\fonts\" >nul 2>&1
if exist "%RES_DIR%\imgs" xcopy /s /y /q "%RES_DIR%\imgs" "%OUT_DIR%\imgs\" >nul 2>&1

echo [SICP_Via] Ejecutando app...
java --module-path "%MODULE_PATH%;%OUT_DIR%" --module sicpvia/app.Main
exit /b %ERRORLEVEL%

