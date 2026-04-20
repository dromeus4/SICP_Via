# SICP Via - Teclado industrial

Este proyecto incluye un gestor especifico para un teclado industrial de 63 teclas:

- 44 teclas normales: mantienen el comportamiento estandar del teclado.
- 19 teclas especiales: se detectan y se enrutan por estado.

## Sistema de Layout Responsivo con Mínimos

La ventana principal tiene 4 franjas horizontales con porcentajes de altura y **mínimos garantizados**:

| Franja | Porcentaje Alto | Mínimo Alto (px) |
|--------|-----------------|------------------|
| CABECERA | 7% | 35 |
| PANEL | 5.5% | 25 |
| CONTENIDO | ~81% | 300 |
| PIE | 6.5% | 30 |

**Tamaños mínimos:**
- Ventana: 300px ancho × 350px alto
- Franjas: 500px ancho mínimo (scroll horizontal si es necesario)

### Scroll Automático

Cada franja es un contenedor con scroll automático:
- **CABECERA, PANEL, PIE:** Scroll horizontal cuando el contenido excede el ancho mínimo (500px)
- **CONTENIDO:** Scroll vertical y horizontal cuando el contenido excede los mínimos

## Sistema Unificado de Tamaño de Texto

Todos los textos se calculan basándose en el **ALTO de la ventana**, no el ancho.

### TEXTO_TAMANO (Tamaño Base)

```
TEXTO_TAMANO = altoVentana × 1.8%
```

Con límites: mínimo 10px, máximo 48px

### Porcentajes de Texto

| Tipo de Texto | Porcentaje de TEXTO_TAMANO | Ejemplo (ventana 800px alto) |
|---------------|---------------------------|------------------------------|
| Normal | 100% | 14.4px |
| Pequeño (info) | 90% | 13.0px |
| Reloj | 110% | 15.8px |
| Logo | 115% | 16.6px |
| Botones | 120% | 17.3px |
| Título | 130% | 18.7px |

### Uso en código

```java
// En cualquier componente con acceso a altoVentana:
double fontSize = FwLayoutResponsivo.textoBoton(altoVentana.get());  // 120%
double fontTitulo = FwLayoutResponsivo.textoTitulo(altoVentana.get());  // 130%
double fontPequeno = FwLayoutResponsivo.textoPequeno(altoVentana.get());  // 90%
```

## Clases del framework

- `framework.teclado.TeclaEspecial`
- `framework.teclado.EstadoSistema`
- `framework.teclado.EventoTeclaEspecial`
- `framework.teclado.ConfigTecladoIndustrial`
- `framework.teclado.DescritorTecla`
- `framework.teclado.ConfigSistemaRepository`
- `framework.teclado.GestorTecladoGlobal` ⭐ **Clase central**
- `framework.teclado.ControlTecladoIndustrial`
- `app.VentanaConfigTecladoIndustrial`

## Integracion

Se integra en `app.Main` y se conecta al `Scene` principal:

- registra fuentes
- crea `GestorTecladoGlobal` con configuración del teclado
- define políticas de filtrado para cada estado del sistema
- conecta gestor con `conectar(scene)` para interceptar TODAS las teclas
- todas las teclas (normales + especiales) pasan por el gestor

## Mapeo por configuracion (array asociativo)

El mapeo de las 19 teclas especiales se gestiona con un mapa asociativo en memoria
(`Map<TeclaEspecial, DescritorTecla>`) y se persiste en archivo.

- Archivo: `sicpvia-config.properties`
- Ubicacion: junto al ejecutable (o directorio base de ejecucion)
- Claves de teclado: `teclado.<TECLA_ESPECIAL>=<COMBINACION>`
  - Formato: `[CTRL+][SHIFT+][ALT+]<KEYCODE>` (ej: `CTRL+F8`, `SHIFT+F1`)

Desde el boton `CONFIGURAR` se abre una ventana para:

1. elegir la tecla especial a configurar,
2. presionar una tecla fisica (con o sin modificadores),
3. guardar y actualizar en caliente.

## Valores por defecto de mapeo

| Tecla especial | Combinación | Descripción |
|---|---|---|
| CATEGORIA_1 | Shift+F1 | Categoría 1 |
| CATEGORIA_2 | Shift+F2 | Categoría 2 |
| CATEGORIA_3 | Shift+F3 | Categoría 3 |
| CATEGORIA_4 | Shift+F4 | Categoría 4 |
| CATEGORIA_5 | Shift+F5 | Categoría 5 |
| CATEGORIA_6 | Shift+F6 | Categoría 6 |
| CATEGORIA_7 | Shift+F7 | Categoría 7 |
| CATEGORIA_8 | Shift+F8 | Categoría 8 |
| CATEGORIA_9 | Shift+F9 | Categoría 9 |
| CATEGORIA_10 | Shift+F10 | Categoría 10 |
| ROJO | Ctrl+F8 | Botón Rojo - Anular |
| VERDE | Ctrl+F9 | Botón Verde - Confirmar |
| EFECTIVO | Ctrl+F1 | Pago en Efectivo |
| POS | Ctrl+F2 | Pago con POS |
| BOMBERO | Ctrl+F4 | Categoría Bombero |
| AMBULANCIA | Ctrl+F3 | Categoría Ambulancia |
| POLICIA | Ctrl+F5 | Categoría Policía |
| EXENTO | Ctrl+F6 | Exención de pago |
| NOMINAR | Ctrl+F7 | Nominar/Asignar |

## Integración con base de datos

Ver `MAPEO_TECLADO_BD.md` para:
- Esquema SQL recomendado
- Ejemplos de consultas
- Estrategia de persistencia multi-estación

## Control global del teclado

`GestorTecladoGlobal` intercepta y controla **TODAS** las teclas presionadas:

- **Teclas especiales** (19): se detectan automáticamente y disparan acciones
- **Teclas normales** (44): se permiten o bloquean según política de estado
- **Políticas por estado**: define whitelist/blacklist de teclas para cada modo
- **Modo bloqueado**: bloquea entrada excepto teclas especiales

```java
// Ejemplo: en VENTA_ACTIVA, bloquear ESC
gestor.definirPolitica(EstadoSistema.VENTA_ACTIVA,
    new GestorTecladoGlobal.PoliticaTeclas()
        .bloquearSolo(new DescritorTecla(KeyCode.ESCAPE, false, false, false))
);
```

Ver `GESTOR_TECLADO_GLOBAL.md` para uso avanzado y ejemplos.

## Ejecución rápida en Windows

Se agregó el lanzador `EJECUTAR.bat` en la raíz del proyecto.

Desde consola, dentro de `SICP_Via`, ejecuta:

```bat
EJECUTAR
```

El script compila en UTF-8 y abre la app JavaFX automáticamente.
