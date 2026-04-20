@echo off
REM Script para crear instalador .exe de SICP Via en Windows usando jpackage
REM Ejecutar DESPUÉS de correr CONSTRUIR_CROSS.bat para Windows

set "RUNTIME_DIR=%~dp0target\sicpvia-windows-x64"
set "JAR=%~dp0target\cross\sicpvia.jar"
set "ICON=%~dp0imgs\icono.ico"
set "DEST=%~dp0target\instaladores"

if not exist "%RUNTIME_DIR%" (
    echo [ERROR] No se encuentra el runtime de Windows. Ejecuta CONSTRUIR_CROSS.bat 0 primero.
    exit /b 1
)
if not exist "%JAR%" (
    echo [ERROR] No se encuentra el JAR. Ejecuta CONSTRUIR_CROSS.bat 0 primero.
    exit /b 1
)
if not exist "%ICON%" (
    echo [WARN] No se encuentra el icono. El instalador usara el icono por defecto.
    set "ICON="
)
if not exist "%DEST%" mkdir "%DEST%"

jpackage ^
  --type exe ^
  --input "%RUNTIME_DIR%\lib" ^
  --name SICPVia ^
  --main-jar "%JAR%" ^
  --main-class sicpvia.app.Main ^
  --runtime-image "%RUNTIME_DIR%" ^
  --icon "%ICON%" ^
  --dest "%DEST%"

echo Instalador generado en %DEST%\SICPVia-*.exe

