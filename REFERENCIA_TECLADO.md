# Referencia rápida - Teclado Industrial

## Flujo de tecla especial

```
Usuario presiona (ej: Ctrl+F8)
       ↓
ControlTecladoIndustrial.procesarKeyPressed(KeyEvent)
       ↓
Crear DescritorTecla(F8, ctrl=true, shift=false, alt=false)
       ↓
Buscar en ConfigTecladoIndustrial.buscarTeclaEspecial(descritor)
       ↓
Si encontrada → dispara EventoTeclaEspecial
       ↓
Ejecutar acción registrada para el estado actual
```

## DescritorTecla - Uso

```java
// Crear descriptor (Ctrl+F8)
DescritorTecla desc = new DescritorTecla(KeyCode.F8, true, false, false);

// Serializar para archivo/BD
String representacion = desc.toString(); // "CTRL+F8"

// Deserializar desde archivo/BD
DescritorTecla desc2 = DescritorTecla.fromString("CTRL+F8");

// Comparar con evento
if (desc.equals(desc2)) { /* son iguales */ }

// Verificar si coincide con evento de teclado
if (desc.coincide(keyEvent)) { /* evento es Ctrl+F8 */ }
```

## Mapeo por defecto

Las 19 teclas están mapeadas en `ConfigTecladoIndustrial.porDefecto()`:

```java
// CATEGORIAS: Shift+F1..F10
builder.mapear(TeclaEspecial.CATEGORIA_1, KeyCode.F1, false, true, false);

// ACCIONES: Ctrl+F1..F9
builder.mapear(TeclaEspecial.ROJO, KeyCode.F8, true, false, false);
builder.mapear(TeclaEspecial.VERDE, KeyCode.F9, true, false, false);
// ...
```

## Persistencia

### En archivo (sicpvia-config.properties)
```properties
teclado.CATEGORIA_1=SHIFT+F1
teclado.ROJO=CTRL+F8
teclado.VERDE=CTRL+F9
```

### En BD (tabla MAPEO_TECLADO)
```sql
id_tecla | combinacion_teclas
---------|-------------------
1        | SHIFT+F1
11       | CTRL+F8
12       | CTRL+F9
```

## Registrar una acción por estado

En `app.Main.start()`:

```java
// Cuando se presiona VERDE (Ctrl+F9) en estado EN_ESPERA,
// cambia a VENTA_ACTIVA
controlTecladoIndustrial.registrarAccion(
    EstadoSistema.EN_ESPERA,
    TeclaEspecial.VERDE,
    evento -> controlTecladoIndustrial.setEstadoActual(EstadoSistema.VENTA_ACTIVA)
);

// Cuando se presiona ROJO (Ctrl+F8) en estado VENTA_ACTIVA,
// vuelve a EN_ESPERA
controlTecladoIndustrial.registrarAccion(
    EstadoSistema.VENTA_ACTIVA,
    TeclaEspecial.ROJO,
    evento -> controlTecladoIndustrial.setEstadoActual(EstadoSistema.EN_ESPERA)
);
```

## Cambiar mapeo en runtime

Desde `VentanaConfigTecladoIndustrial`:

```java
// Captura Ctrl+Shift+F5, asigna a CATEGORIA_5, guarda
nuevaConfig = builder
    .mapear(TeclaEspecial.CATEGORIA_5, KeyCode.F5, true, true, false)
    .build();

configSistemaRepository.guardarTeclado(nuevaConfig);
controlTecladoIndustrial.actualizarConfig(nuevaConfig);
```

## Serialización - Combinaciones soportadas

| Representación | Descripción |
|---|---|
| `F1` | Solo F1 (sin modificadores) |
| `CTRL+F1` | Ctrl + F1 |
| `SHIFT+F1` | Shift + F1 |
| `ALT+F1` | Alt + F1 |
| `CTRL+SHIFT+F1` | Ctrl + Shift + F1 |
| `CTRL+ALT+F1` | Ctrl + Alt + F1 |
| `SHIFT+ALT+F1` | Shift + Alt + F1 |
| `CTRL+SHIFT+ALT+F1` | Ctrl + Shift + Alt + F1 |

## Testing

```bash
# Compilar y ejecutar self-test
javac --module-path $MP --add-modules javafx.controls -d out src/main/java/**/*.java
java --module-path "$MP;out" --module sicpvia/framework.teclado.TecladoIndustrialSelfTest

# Esperado: "OK - mapeo de teclas especiales: 19"
```

## Troubleshooting

| Problema | Causa | Solución |
|---|---|---|
| Tecla no funciona | No está mapeada | Revisar `ConfigTecladoIndustrial.porDefecto()` |
| Cambio de mapeo no persiste | No se guardó en BD/archivo | Ejecutar `guardarTeclado()` y `actualizarConfig()` |
| Tecla genera KeyCode distinto | Hardware diferente | Recalibrar en ventana CONFIGURAR |
| Conflicto de teclas | Dos acciones mapean a la misma combinación | BD debe validar uniqueness |

