#!/bin/bash
# Script para crear instalador .pkg de SICP Via en macOS usando jpackage
# Ejecutar DESPUÉS de correr CONSTRUIR_CROSS.bat 3 en macOS

RUNTIME_DIR="$(dirname \"$0\")/target/sicpvia-macos-x64"
JAR="$(dirname \"$0\")/target/cross/sicpvia.jar"
ICON="$(dirname \"$0\")/imgs/icono.ico"
DEST="$(dirname \"$0\")/target/instaladores"

if [ ! -d "$RUNTIME_DIR" ]; then
  echo "[ERROR] No se encuentra el runtime de macOS. Ejecuta CONSTRUIR_CROSS.bat 3 primero."
  exit 1
fi
if [ ! -f "$JAR" ]; then
  echo "[ERROR] No se encuentra el JAR. Ejecuta CONSTRUIR_CROSS.bat 3 primero."
  exit 1
fi
if [ ! -f "$ICON" ]; then
  echo "[WARN] No se encuentra el icono. El instalador usara el icono por defecto."
  ICON=""
fi
mkdir -p "$DEST"

jpackage \
  --type pkg \
  --input "$RUNTIME_DIR/lib" \
  --name SICPVia \
  --main-jar "$JAR" \
  --main-class sicpvia.app.Main \
  --runtime-image "$RUNTIME_DIR" \
  --icon "$ICON" \
  --dest "$DEST"

echo "Instalador generado en $DEST/SICPVia-*.pkg"

