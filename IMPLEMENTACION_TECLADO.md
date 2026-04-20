# SICP Via - Resumen de implementación del teclado industrial

## ✅ Completado

### 1. Arquitectura de teclas especiales
- **19 teclas especiales** mapeadas: 10 categorías + 9 acciones
- **44 teclas normales** mantienen comportamiento estándar
- Estados del sistema: `INICIO`, `EN_ESPERA`, `VENTA_ACTIVA`, `PAGO`, `BLOQUEADO`

### 2. Soporte para combinaciones de teclas
- Nueva clase `DescritorTecla` para representar: `KeyCode + Ctrl + Shift + Alt`
- Ejemplos: `SHIFT+F1`, `CTRL+F8`, `CTRL+SHIFT+F5`
- Serialización en formato agnóstico: `[CTRL+][SHIFT+][ALT+]<KEYCODE>`

### 3. Mapeo configurable
- Ventana de configuración interactiva (`VentanaConfigTecladoIndustrial`)
- Captura de combinaciones en tiempo real
- Validación de unicidad (no duplicados)
- Actualización en caliente (sin reiniciar)

### 4. Persistencia
- **Archivo**: `sicpvia-config.properties` junto al ejecutable
- Formato: `teclado.<TECLA_ESPECIAL>=<COMBINACION>`
- Migración transparente de configuraciones antiguas (legacy support)
- **BD**: esquema SQL con tablas `TECLAS_ESPECIALES` y `MAPEO_TECLADO`

### 5. Valores por defecto

**Categorías (Shift+F1..F10)**
```
CATEGORIA_1..10 = SHIFT+F1..F10
```

**Acciones (Ctrl+F1..F9)**
```
EFECTIVO       = CTRL+F1
POS            = CTRL+F2
AMBULANCIA     = CTRL+F3
BOMBERO        = CTRL+F4
POLICIA        = CTRL+F5
EXENTO         = CTRL+F6
NOMINAR        = CTRL+F7
ROJO           = CTRL+F8
VERDE          = CTRL+F9
```

### 6. Documentación
- `README.md` - visión general del proyecto
- `MAPEO_TECLADO_BD.md` - esquema SQL y consultas
- `REFERENCIA_TECLADO.md` - guía rápida para desarrolladores

## 📦 Clases clave

| Clase | Responsabilidad |
|---|---|
| `TeclaEspecial` | Enum de las 19 teclas especiales |
| `EstadoSistema` | Estados de operación del sistema |
| `DescritorTecla` | Representación de combinación tecla + modificadores |
| `ConfigTecladoIndustrial` | Mapeo TeclaEspecial ↔ DescritorTecla (bidireccional) |
| `ControlTecladoIndustrial` | Motor de eventos y gestión de estado |
| `ConfigSistemaRepository` | Persistencia en archivo `.properties` |
| `VentanaConfigTecladoIndustrial` | UI para remapeo en runtime |
| `EventoTeclaEspecial` | Evento tipado con contexto de tecla y estado |

## 🔄 Flujo de captura de tecla especial

```
Usuario presiona Ctrl+F8
         ↓
KeyEvent interceptado en Scene
         ↓
ControlTecladoIndustrial.procesarKeyPressed()
         ↓
Crear DescritorTecla(F8, ctrl=true, shift=false, alt=false)
         ↓
ConfigTecladoIndustrial.buscarTeclaEspecial(descritor)
         ↓
Coincide con TeclaEspecial.ROJO
         ↓
Buscar acción registrada para (EN_ESPERA, ROJO)
         ↓
Ejecutar acción → cambiar estado a VENTA_ACTIVA
         ↓
Notificar observadores globales
```

## 🗄️ Integración con BD (opcional)

Para persistencia en base de datos:

```sql
-- Tabla de teclas especiales
CREATE TABLE TECLAS_ESPECIALES (
    id_tecla INT PRIMARY KEY,
    nombre_tecla VARCHAR(50) UNIQUE,
    tipo ENUM('CATEGORIA', 'ACCION')
);

-- Tabla de mapeos activos
CREATE TABLE MAPEO_TECLADO (
    id_mapeo INT PRIMARY KEY,
    id_tecla INT FOREIGN KEY,
    combinacion_teclas VARCHAR(100),
    estacion_id INT,
    activo BOOLEAN DEFAULT TRUE
);
```

Implementar interfaz `TecladoRepository` para DAO pattern.

## 🧪 Testing

```bash
mvn clean compile
mvn javafx:run
```

Self-test: presionar las 19 teclas en ventana de configuración.

## 🎯 Próximos pasos (opcional)

1. **Integración con BD**: reemplazar `sicpvia-config.properties` por queries SQL
2. **Logs de auditoría**: registrar quién cambió qué combinación y cuándo
3. **Presets por rol**: perfiles de operador con mapeos predefinidos
4. **Validación en BD**: trigger para evitar duplicados de combinación
5. **Sincronización multi-estación**: replicar mapeos entre cajas conectadas

