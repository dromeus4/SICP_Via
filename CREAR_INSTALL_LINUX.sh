#!/bin/bash
# Script para crear instalador .deb de SICP Via en Linux usando jpackage
# Ejecutar DESPUÉS de correr CONSTRUIR_CROSS.bat 1 en Linux

RUNTIME_DIR="$(dirname "$0")/target/sicpvia-linux-x64"
JAR="$(dirname "$0")/target/cross/sicpvia.jar"
ICON="$(dirname "$0")/imgs/icono.ico"
DEST="$(dirname "$0")/target/instaladores"

if [ ! -d "$RUNTIME_DIR" ]; then
  echo "[ERROR] No se encuentra el runtime de Linux. Ejecuta CONSTRUIR_CROSS.bat 1 primero."
  exit 1
fi
if [ ! -f "$JAR" ]; then
  echo "[ERROR] No se encuentra el JAR. Ejecuta CONSTRUIR_CROSS.bat 1 primero."
  exit 1
fi
if [ ! -f "$ICON" ]; then
  echo "[WARN] No se encuentra el icono. El instalador usara el icono por defecto."
  ICON=""
fi
mkdir -p "$DEST"

jpackage \
  --type deb \
  --input "$RUNTIME_DIR/lib" \
  --name SICPVia \
  --main-jar "$JAR" \
  --main-class sicpvia.app.Main \
  --runtime-image "$RUNTIME_DIR" \
  --icon "$ICON" \
  --dest "$DEST"

echo "Instalador generado en $DEST/SICPVia-*.deb"

