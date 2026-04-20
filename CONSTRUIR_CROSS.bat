@echo off
setlocal EnableDelayedExpansion

echo ===============================================================
echo  SICP Via - Construir para OTRA PLATAFORMA desde Windows
echo ===============================================================
echo.

REM ========== CONFIGURACION ==========
set "JDK_VERSION=17.0.11+9"
set "JDK_VERSION_URL=17.0.11%%2B9"
set "JAVAFX_VERSION=21.0.10"
set "PROJECT_DIR=%~dp0"
if "%PROJECT_DIR:~-1%"=="\" set "PROJECT_DIR=%PROJECT_DIR:~0,-1%"
set "CROSS_DIR=%PROJECT_DIR%\target\cross"
set "TARGET_CLASSES=%PROJECT_DIR%\target\classes"

REM ========== SELECCIONAR PLATAFORMA ==========
set "PLATFORM_CHOICE=%~1"
if "%PLATFORM_CHOICE%"=="" (
    echo Plataformas disponibles:
    echo   1. Linux x64
    echo   2. Linux aarch64
    echo   3. macOS x64
    echo   4. macOS aarch64
    echo   0. Windows x64
    echo.
    echo   5. TODAS (Windows + Linux x64 + macOS x64^)
    echo.
    set /p PLATFORM_CHOICE="Selecciona plataforma [0-5]: "
)

REM ========== BUILD MULTIPLATAFORMA ==========
if "!PLATFORM_CHOICE!"=="5" (
    echo.
    echo === Construyendo para TODAS las plataformas ===
    echo.
    call "%~f0" 0
    if errorlevel 1 (
        echo [ERROR] Fallo al construir Windows x64
        exit /b 1
    )
    call "%~f0" 1
    if errorlevel 1 (
        echo [ERROR] Fallo al construir Linux x64
        exit /b 1
    )
    call "%~f0" 3
    if errorlevel 1 (
        echo [ERROR] Fallo al construir macOS x64
        exit /b 1
    )
    echo.
    echo ===============================================================
    echo  TODAS las plataformas construidas exitosamente
    echo ===============================================================
    endlocal
    exit /b 0
)

REM ========== CONFIGURAR PLATAFORMA INDIVIDUAL ==========
if "!PLATFORM_CHOICE!"=="0" (
    set "PLATFORM=windows-x64"
    set "JDK_OS=windows"
    set "JDK_ARCH=x64"
    set "JAVAFX_OS=windows-x64_bin"
)
if "!PLATFORM_CHOICE!"=="1" (
    set "PLATFORM=linux-x64"
    set "JDK_OS=linux"
    set "JDK_ARCH=x64"
    set "JAVAFX_OS=linux-x64_bin"
)
if "!PLATFORM_CHOICE!"=="2" (
    set "PLATFORM=linux-aarch64"
    set "JDK_OS=linux"
    set "JDK_ARCH=aarch64"
    set "JAVAFX_OS=linux-aarch64"
)
if "!PLATFORM_CHOICE!"=="3" (
    set "PLATFORM=macos-x64"
    set "JDK_OS=mac"
    set "JDK_ARCH=x64"
    set "JAVAFX_OS=osx-x64_bin"
)
if "!PLATFORM_CHOICE!"=="4" (
    set "PLATFORM=macos-aarch64"
    set "JDK_OS=mac"
    set "JDK_ARCH=aarch64"
    set "JAVAFX_OS=osx-aarch64"
)

if not defined PLATFORM (
    echo [ERROR] Opcion invalida: !PLATFORM_CHOICE!
    endlocal
    exit /b 1
)

echo.
echo ---------------------------------------------------------------
echo  Construyendo para: !PLATFORM!
echo ---------------------------------------------------------------
echo.

REM ========== VERIFICAR COMPILACION ==========
if not exist "%TARGET_CLASSES%\module-info.class" (
    echo [INFO] Compilando proyecto...
    call "%PROJECT_DIR%\mvnw.cmd" compile -q
    if errorlevel 1 (
        echo [ERROR] Fallo la compilacion
        endlocal
        exit /b 1
    )
)

REM ========== CREAR DIRECTORIO ==========
if not exist "%CROSS_DIR%" mkdir "%CROSS_DIR%"

REM ========== DESCARGAR JDK ==========
set "JDK_DIR=%CROSS_DIR%\jdk-!JDK_OS!-!JDK_ARCH!"
set "JDK_ARCHIVE=%CROSS_DIR%\jdk-!JDK_OS!-!JDK_ARCH!.tar.gz"

REM Resolver ruta jmods para macOS (Contents\Home\jmods)
if not exist "!JDK_DIR!\jmods" (
    if exist "!JDK_DIR!\Contents\Home\jmods" set "JDK_DIR=!JDK_DIR!\Contents\Home"
)

if not exist "!JDK_DIR!\jmods" (
    echo [1/4] Descargando JDK para !PLATFORM!...
    set "JDK_URL=https://api.adoptium.net/v3/binary/version/jdk-!JDK_VERSION_URL!/!JDK_OS!/!JDK_ARCH!/jdk/hotspot/normal/eclipse"

    if not exist "!JDK_ARCHIVE!" (
        echo      Descargando de Adoptium...
        powershell -Command "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest -Uri '!JDK_URL!' -OutFile '!JDK_ARCHIVE!'"
        if errorlevel 1 (
            echo [ERROR] No se pudo descargar JDK
            endlocal
            exit /b 1
        )
    )

    echo      Extrayendo...
    if not exist "!JDK_DIR!" mkdir "!JDK_DIR!"
    powershell -Command "tar -xzf '!JDK_ARCHIVE!' -C '!JDK_DIR!' --strip-components=1"

    if not exist "!JDK_DIR!\jmods" (
        REM macOS JDK: Contents\Home\jmods
        if exist "!JDK_DIR!\Contents\Home\jmods" (
            set "JDK_DIR=!JDK_DIR!\Contents\Home"
        ) else (
            for /d %%D in ("!JDK_DIR!\*") do (
                if exist "%%D\jmods" set "JDK_DIR=%%D"
                if exist "%%D\Home\jmods" set "JDK_DIR=%%D\Home"
            )
        )
    )
    echo      OK
) else (
    echo [1/4] JDK ya existe para !PLATFORM!
)

REM ========== DESCARGAR JAVAFX ==========
set "JAVAFX_DIR=%CROSS_DIR%\javafx-!JAVAFX_OS!"

if "!PLATFORM!"=="linux-x64" (
    set "JAVAFX_ARCHIVE=%CROSS_DIR%\openjfx-21.0.10_linux-x64_bin-jmods.zip"
) else if "!PLATFORM!"=="macos-x64" (
    set "JAVAFX_ARCHIVE=%CROSS_DIR%\openjfx-21.0.10_osx-x64_bin-jmods.zip"
) else if "!PLATFORM!"=="windows-x64" (
    set "JAVAFX_ARCHIVE=%CROSS_DIR%\openjfx-21.0.10_windows-x64_bin-jmods.zip"
) else (
    set "JAVAFX_ARCHIVE=%CROSS_DIR%\javafx-!JAVAFX_OS!.zip"
)

set "FX_URL=https://download2.gluonhq.com/openjfx/!JAVAFX_VERSION!/openjfx-!JAVAFX_VERSION!_!JAVAFX_OS!-jmods_!JAVAFX_VERSION!.zip"

if not exist "!JAVAFX_DIR!\javafx.base.jmod" (
    echo [2/4] JavaFX jmods para !PLATFORM!...
    if not exist "!JAVAFX_ARCHIVE!" (
        echo [ERROR] No se encuentra el archivo de JavaFX: !JAVAFX_ARCHIVE!
        echo Descargalo manualmente y colocalo en la ruta indicada.
        endlocal
        exit /b 1
    )
    echo      Verificando archivo...
    for %%F in ("!JAVAFX_ARCHIVE!") do set "FSIZE=%%~zF"
    if !FSIZE! LSS 1000000 (
        echo [ERROR] Archivo muy pequeno, parece invalido. Eliminalo y descarga de nuevo.
        del /q "!JAVAFX_ARCHIVE!"
        endlocal
        exit /b 1
    )
    REM ========== LIMPIEZA DE CARPETAS DE JAVAFX ==========
    if exist "!JAVAFX_DIR!" rmdir /s /q "!JAVAFX_DIR!"
    set "TMP_FX_DIR=%CROSS_DIR%\fx-tmp-!PLATFORM!"
    if exist "!TMP_FX_DIR!" rmdir /s /q "!TMP_FX_DIR!"

    REM ========== EXTRACCION SEGURA DE JAVAFX ==========
    echo      Extrayendo...
    powershell -Command "Expand-Archive -Path '!JAVAFX_ARCHIVE!' -DestinationPath '!TMP_FX_DIR!' -Force"
    for /d %%D in ("!TMP_FX_DIR!\*") do (
        if exist "%%D\javafx.base.jmod" move "%%D" "!JAVAFX_DIR!" >nul
    )
    if exist "!TMP_FX_DIR!" rmdir /s /q "!TMP_FX_DIR!"

    if not exist "!JAVAFX_DIR!\javafx.base.jmod" (
        echo [ERROR] Extraccion fallida. Elimina el zip y descargalo de nuevo.
        endlocal
        exit /b 1
    )
    echo      OK
) else (
    echo [2/4] JavaFX ya existe para !PLATFORM!
)

REM ========== CREAR JAR ==========
echo [3/4] Empaquetando modulo para !PLATFORM!...
set "APP_JAR=%CROSS_DIR%\sicpvia-!PLATFORM!.jar"
jar --create --file "!APP_JAR!" --module-version 1.0 -C "%TARGET_CLASSES%" .

REM ========== JLINK ==========
echo [4/4] Ejecutando jlink para !PLATFORM!...
set "OUTPUT_DIR=%PROJECT_DIR%\target\sicpvia-!PLATFORM!"
set "OUTPUT_ZIP=%PROJECT_DIR%\target\sicpvia-!PLATFORM!.zip"

if exist "!OUTPUT_DIR!" rmdir /s /q "!OUTPUT_DIR!"
if exist "!OUTPUT_ZIP!" del /q "!OUTPUT_ZIP!"

set "MPATH=!JDK_DIR!\jmods;!JAVAFX_DIR!;!APP_JAR!"

jlink --module-path "!MPATH!" --add-modules sicpvia --output "!OUTPUT_DIR!" --strip-debug --compress 2 --no-header-files --no-man-pages --launcher sicpvia=sicpvia/app.Main

if errorlevel 1 (
    echo [ERROR] Fallo jlink para !PLATFORM!
    endlocal
    exit /b 1
)

REM ========== ZIP ==========
echo      Comprimiendo...
powershell -Command "Compress-Archive -Path '!OUTPUT_DIR!\*' -DestinationPath '!OUTPUT_ZIP!' -Force"

for %%F in ("!OUTPUT_ZIP!") do set "ZIPSIZE=%%~zF"
set /a "SIZEMB=!ZIPSIZE!/1048576"

echo.
echo ===============================================================
echo  LISTO: sicpvia-!PLATFORM!.zip  [!SIZEMB! MB]
echo ===============================================================
echo.
echo Para usar en !PLATFORM!:
echo   1. Copia target\sicpvia-!PLATFORM!.zip
echo   2. Descomprime en la maquina destino
echo   3. Ejecuta: bin/sicpvia
echo.

endlocal
exit /b 0
