@echo off
setlocal
echo ===============================================
echo  SICP Via - Construir distribucion
echo ===============================================
echo.

REM Detectar JAVA_HOME si no esta definido
if "%JAVA_HOME%"=="" (
    for /f "tokens=*" %%i in ('where java 2^>nul') do (
        for %%j in ("%%~dpi..") do set "JAVA_HOME=%%~fj"
        goto :found_java
    )
    echo [ERROR] No se encontro Java. Instala JDK 17 y define JAVA_HOME.
    exit /b 1
)
:found_java
echo JAVA_HOME: %JAVA_HOME%
echo.

REM Limpiar build anterior
echo [1/3] Limpiando build anterior...
if exist "target\sicpvia" rmdir /s /q "target\sicpvia"
if exist "target\sicpvia.zip" del /f /q "target\sicpvia.zip"

REM Construir imagen con jlink
echo [2/3] Construyendo imagen autocontenida con jlink...
call "%~dp0mvnw.cmd" javafx:jlink
if errorlevel 1 (
    echo.
    echo [ERROR] Fallo la construccion. Revisa los errores arriba.
    exit /b 1
)

REM Verificar resultado
echo.
echo [3/3] Verificando resultado...
if not exist "target\sicpvia.zip" (
    echo [ERROR] No se genero el archivo sicpvia.zip
    exit /b 1
)

for %%F in ("target\sicpvia.zip") do echo Archivo generado: %%~fF (%%~zF bytes)

echo.
echo ===============================================
echo  CONSTRUCCION EXITOSA
echo ===============================================
echo.
echo Para distribuir:
echo   - Copia target\sicpvia.zip a la computadora destino
echo   - Descomprime el zip
echo   - Ejecuta bin\sicpvia.bat (Windows) o bin/sicpvia (Linux/Mac)
echo.
echo NOTA: Esta imagen solo funciona en el mismo sistema operativo
echo       donde fue construida. Para otras plataformas, construye
echo       desde esa plataforma o usa GitHub Actions.
echo.

endlocal

