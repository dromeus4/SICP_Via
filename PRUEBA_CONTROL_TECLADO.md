# Interfaz de prueba - Control global de teclado

## Descripción

Se ha agregado una interfaz de prueba temporal en la aplicación que permite verificar el funcionamiento del `GestorTecladoGlobal` en tiempo real.

## Componentes

### 1. Label informativo
```
"Presiona teclas para probar el control global de teclado"
```
Instrucciones al usuario sobre qué hacer.

### 2. Label de detección
```
"Se ha presionado: A"
"Se ha presionado: B"
"Se ha presionado: BACKSPACE"
```
Muestra en tiempo real cada tecla normal que se presiona.

**Funcionamiento:**
- Cuando presionas "A", el Label muestra: `"Se ha presionado: A"`
- Cuando presionas "B", el Label muestra: `"Se ha presionado: B"`
- Cuando presionas "BACKSPACE", el Label muestra: `"Se ha presionado: BACKSPACE"`

### 3. TextField (Cuadro de edición)
```
Escribe aquí... (A, B, BACKSPACE, etc.)
```
Campo de texto donde puedes escribir normalmente.

**Funcionamiento:**
- Las teclas normales que presionas llegan al TextField después de ser procesadas por el GestorTecladoGlobal
- Si escribes "A", aparece en el TextField
- Si escribes "B", aparece en el TextField
- Si presionas BACKSPACE, se borra el carácter anterior

## Flujo de funcionamiento

```
┌─────────────────────────────────────────────────┐
│ Usuario presiona tecla (ej: A)                  │
└──────────────────┬──────────────────────────────┘
                   │
                   ↓
┌─────────────────────────────────────────────────┐
│ GestorTecladoGlobal.interceptarKeyEvent()       │
│ - Detecta que es tecla normal (no especial)     │
│ - Notifica a observadores: "Se presionó A"     │
└──────────────────┬──────────────────────────────┘
                   │
                   ↓
┌─────────────────────────────────────────────────┐
│ Observador actualiza Label:                     │
│ labelTecla.setText("Se ha presionado: A")       │
└──────────────────┬──────────────────────────────┘
                   │
                   ↓
┌─────────────────────────────────────────────────┐
│ GestorTecladoGlobal verifica política:          │
│ - Estado EN_ESPERA permite todas las teclas     │
│ - Deja pasar el evento (NO consume)             │
└──────────────────┬──────────────────────────────┘
                   │
                   ↓
┌─────────────────────────────────────────────────┐
│ Evento llega al TextField                       │
│ - TextField recibe "A"                          │
│ - Muestra: "A" en el campo                      │
└─────────────────────────────────────────────────┘
```

## Prueba paso a paso

### Caso 1: Presionar "A"
1. Presionas "A"
2. Label muestra: `"Se ha presionado: A"`
3. TextField recibe la "A" y muestra: `"A"`

### Caso 2: Presionar "B" después de "A"
1. Presionas "B"
2. Label muestra: `"Se ha presionado: B"`
3. TextField recibe la "B" y muestra: `"AB"`

### Caso 3: Presionar BACKSPACE
1. Presionas BACKSPACE
2. Label muestra: `"Se ha presionado: BACKSPACE"`
3. TextField recibe el BACKSPACE y borra la "B": muestra `"A"`

## Verificaciones

✅ **Label se actualiza en tiempo real** con cada tecla presionada  
✅ **TextField recibe las teclas normales** que presionas  
✅ **BACKSPACE funciona** para eliminar caracteres  
✅ **Teclas especiales (Ctrl+F1, etc.)** no aparecen en el TextField, solo en logs  
✅ **El GestorTecladoGlobal controla todo** antes de que llegue a componentes  

## Cómo verificar que funciona correctamente

### Escenario 1: Texto normal
```
1. Haz clic en el TextField
2. Escribe: "ABC"
3. Verifica en Label que muestre:
   - "Se ha presionado: A"
   - "Se ha presionado: B"
   - "Se ha presionado: C"
4. El TextField debe mostrar: "ABC"
```

### Escenario 2: Corrección con BACKSPACE
```
1. Escribe: "ABCD"
2. Presiona BACKSPACE
3. Label muestra: "Se ha presionado: BACKSPACE"
4. El TextField debe mostrar: "ABC" (la D se borra)
```

### Escenario 3: Teclas especiales
```
1. Presiona Ctrl+F8 (ROJO)
2. Label NO muestra nada (solo speciales en logs)
3. TextField NO cambia (teclas especiales se consumen)
4. Consola muestra: "[TECLADO] ROJO | estado=EN_ESPERA"
```

## Notas técnicas

- El `GestorTecladoGlobal` registra un observador de teclas normales en `AppContenido`
- Se usa `ThreadLocal` para pasar el Label de `AppContenido` a `Main`
- La política de estado `EN_ESPERA` permite todas las teclas normales
- El Label se actualiza mediante `Platform.runLater()` implícitamente en el observador
- El TextField permite edición normal porque el gestor deja pasar las teclas

## Cómo remover esta interfaz de prueba

Cuando ya no necesites la interfaz:

1. Elimina el bloque `VBox contenedor` de `AppContenido.java`
2. Comenta el observador de teclas normales en `Main.java`
3. Recompila

El proyecto seguirá funcionando normalmente con el `GestorTecladoGlobal` en control.

