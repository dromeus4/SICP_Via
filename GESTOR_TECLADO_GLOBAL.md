# GestorTecladoGlobal - Control total del teclado

## Visión general

`GestorTecladoGlobal` es una clase que intercepta **TODAS las teclas presionadas** antes de que lleguen a sus destinos finales. Proporciona:

- **Control absoluto** sobre qué teclas "pasan" a componentes (TextFields, etc.)
- **Distinción automática** entre teclas especiales (19) y normales (44)
- **Políticas por estado** que definen qué se permite/bloquea según el modo del sistema
- **Observadores globales** para monitorear teclas especiales
- **Modo bloqueado** para bloquear entrada mientras se procesa algo crítico

## Arquitectura

```
Usuario presiona tecla
         ↓
Scene intercepta KeyEvent (EventFilter)
         ↓
GestorTecladoGlobal.interceptarKeyEvent()
         ↓
¿Es tecla especial? ──YES──→ procesarTeclaEspecial()
                              ├─ Notificar observadores
                              └─ Siempre consume (no pasa a componentes)
         ↓NO
¿Está bloqueado? ──YES──→ Consume (bloquea tecla)
         ↓NO
¿Permite política de estado? ──NO──→ Consume (bloquea tecla)
         ↓YES
Deja pasar (permite llegue a TextField/etc.)
```

## Uso básico

```java
// 1. Crear configuración
ConfigTecladoIndustrial config = ConfigTecladoIndustrial.porDefecto();

// 2. Crear gestor global
GestorTecladoGlobal gestor = new GestorTecladoGlobal(config, EstadoSistema.EN_ESPERA);

// 3. Definir políticas por estado
gestor.definirPolitica(EstadoSistema.EN_ESPERA,
    new GestorTecladoGlobal.PoliticaTeclas().permitirTodo()
);

gestor.definirPolitica(EstadoSistema.VENTA_ACTIVA,
    new GestorTecladoGlobal.PoliticaTeclas()
        .bloquearSolo(new DescritorTecla(KeyCode.ESCAPE, false, false, false))
);

// 4. Conectar a la Scene (intercepta TODOS los eventos)
gestor.conectar(scene);

// 5. Cambiar estado
gestor.setEstadoActual(EstadoSistema.BLOQUEADO);
```

## PoliticaTeclas - Filtrado granular

Define qué teclas se permiten o se bloquean en cada estado.

### Permitir todo (por defecto)
```java
new GestorTecladoGlobal.PoliticaTeclas().permitirTodo()
// Todas las teclas normales pasan. Solo bloquea si el gestor está bloqueado() o tecla especial.
```

### Bloquear todo
```java
new GestorTecladoGlobal.PoliticaTeclas().bloquearTodo()
// Ninguna tecla normal pasa (solo especiales siguen funcionando)
```

### Permitir solo algunas
```java
new GestorTecladoGlobal.PoliticaTeclas()
    .permitirSolo(
        new DescritorTecla(KeyCode.DIGIT1, false, false, false),
        new DescritorTecla(KeyCode.DIGIT2, false, false, false),
        new DescritorTecla(KeyCode.ENTER, false, false, false)
    )
// Solo números 1,2 y ENTER pasan.
```

### Bloquear solo algunas
```java
new GestorTecladoGlobal.PoliticaTeclas()
    .bloquearSolo(
        new DescritorTecla(KeyCode.ESCAPE, false, false, false),
        new DescritorTecla(KeyCode.ALT, false, false, false)
    )
// Todo pasa excepto ESC y ALT
```

## Estados predefinidos

| Estado | Política | Uso |
|---|---|---|
| `EN_ESPERA` | Permitir todo | Pantalla de menú, esperando acción |
| `VENTA_ACTIVA` | Bloquear ESC | Prohibir salida durante operación |
| `PAGO` | Bloquear todo | Solo teclas especiales durante pago |
| `BLOQUEADO` | Bloquear todo | Procesando pago, congelado |
| `INICIO` | Permitir todo | Arranque, configuración |

## Controlar bloqueo total

```java
// Bloquear: NO pasan teclas normales, SOLO especiales
gestor.setBloqueado(true);

// Desbloquear: Se respeta la política de estado
gestor.setBloqueado(false);

// Consultar
if (gestor.isBloqueado()) { /* ... */ }
```

## Observadores globales

Monitorear TODAS las teclas especiales detectadas:

```java
gestor.registrarObservadorGlobal(evento -> {
    System.out.println("Tecla: " + evento.getTecla());
    System.out.println("Estado: " + evento.getEstadoSistema());
    System.out.println("KeyCode: " + evento.getKeyCodeOrigen());
});
```

## Flujo de estados recomendado

```
EN_ESPERA (permitir todo)
    ↓ presiona VERDE (Ctrl+F9)
VENTA_ACTIVA (bloquear ESC, ALT+F4)
    ↓ selecciona items
PAGO (bloquear todo, solo especiales)
    ↓ presiona EFECTIVO o POS
BLOQUEADO (bloquear todo, procesando)
    ↓ pago completado
EN_ESPERA (volver)
```

## Cambio dinámico de políticas

Cambiar políticas en runtime según evento:

```java
gestor.registrarObservadorGlobal(evento -> {
    if (evento.getTecla() == TeclaEspecial.VERDE) {
        // Al presionar VERDE, cambiar a modo VENTA_ACTIVA
        gestor.setEstadoActual(EstadoSistema.VENTA_ACTIVA);
        
        // Y opcionalmente cambiar su política
        gestor.definirPolitica(EstadoSistema.VENTA_ACTIVA,
            new GestorTecladoGlobal.PoliticaTeclas()
                .bloquearSolo(
                    new DescritorTecla(KeyCode.ESCAPE, false, false, false)
                )
        );
    }
    
    if (evento.getTecla() == TeclaEspecial.ROJO) {
        // Al presionar ROJO, volver a EN_ESPERA
        gestor.setEstadoActual(EstadoSistema.EN_ESPERA);
        gestor.setBloqueado(false);
    }
});
```

## Diferencia: GestorTecladoGlobal vs. ControlTecladoIndustrial

| Aspecto | GestorTecladoGlobal | ControlTecladoIndustrial |
|---|---|---|
| Alcance | TODAS las teclas | Solo teclas especiales |
| Filtrado | Sí, por política de estado | No |
| Control de paso | Sí, consume/permite | No |
| Teclas normales | Gestiona | Ignora |
| Bloqueo | Sí, setBloqueado() | No |

**GestorTecladoGlobal es el nuevo gestor central recomendado.**

## Desconexión y limpieza

```java
// Desconectar del Scene (dejar de interceptar)
gestor.desconectar();

// Ahora las teclas fluyen normalmente a componentes
```

