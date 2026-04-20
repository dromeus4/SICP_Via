# Fuentes del proyecto

Ubicacion de fuentes embebidas:

`src/main/resources/fonts`

TTF actualmente incluidos:

- `BarlowCondensed-Black.ttf`
- `BarlowCondensed-BlackItalic.ttf`
- `BarlowCondensed-Bold.ttf`
- `BarlowCondensed-BoldItalic.ttf`
- `BarlowCondensed-ExtraBold.ttf`
- `BarlowCondensed-ExtraBoldItalic.ttf`
- `BarlowCondensed-ExtraLight.ttf`
- `BarlowCondensed-ExtraLightItalic.ttf`
- `BarlowCondensed-Italic.ttf`
- `BarlowCondensed-Light.ttf`
- `BarlowCondensed-LightItalic.ttf`
- `BarlowCondensed-Medium.ttf`
- `BarlowCondensed-MediumItalic.ttf`
- `BarlowCondensed-Regular.ttf`
- `BarlowCondensed-SemiBold.ttf`
- `BarlowCondensed-SemiBoldItalic.ttf`
- `BarlowCondensed-Thin.ttf`
- `BarlowCondensed-ThinItalic.ttf`
- `Doto-ExtraBold.ttf`

Estas fuentes se registran al iniciar la app en `framework.FwFuentes` usando
`Font.loadFont(...)`, por lo que quedan disponibles para JavaFX en Windows,
Linux y macOS sin depender de fuentes instaladas en el sistema operativo.

Nota: verifica la licencia de redistribucion de cada TTF antes de generar
paquetes de distribucion.

