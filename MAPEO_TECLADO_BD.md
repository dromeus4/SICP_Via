# Mapeo de teclas especiales - Representación en BD

## Estructura de datos en JavaFX

Cada combinación de teclas se representa como `DescritorTecla` con:
- `KeyCode` (la tecla base: F1, F8, etc.)
- `boolean ctrl` (si Ctrl está presionado)
- `boolean shift` (si Shift está presionado)
- `boolean alt` (si Alt está presionado)

## Serialización para BD / Archivo

El formato es: `[CTRL+][SHIFT+][ALT+]<KEYCODE_NAME>`

Ejemplos:
- `CTRL+F8` = Ctrl+F8
- `SHIFT+F1` = Shift+F1
- `CTRL+SHIFT+F5` = Ctrl+Shift+F5
- `F1` = solo F1 (sin modificadores)

## Tabla de base de datos recomendada

```sql
CREATE TABLE TECLAS_ESPECIALES (
    id_tecla INT PRIMARY KEY AUTO_INCREMENT,
    nombre_tecla VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(200),
    tipo ENUM('CATEGORIA', 'ACCION') NOT NULL DEFAULT 'ACCION'
);

CREATE TABLE MAPEO_TECLADO (
    id_mapeo INT PRIMARY KEY AUTO_INCREMENT,
    id_tecla INT NOT NULL,
    combinacion_teclas VARCHAR(100) NOT NULL UNIQUE,
    estacion_id INT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_tecla) REFERENCES TECLAS_ESPECIALES(id_tecla)
);
```

## Datos iniciales

```sql
-- Teclas especiales
INSERT INTO TECLAS_ESPECIALES (nombre_tecla, descripcion, tipo) VALUES
  ('CATEGORIA_1', 'Categoría 1', 'CATEGORIA'),
  ('CATEGORIA_2', 'Categoría 2', 'CATEGORIA'),
  ('CATEGORIA_3', 'Categoría 3', 'CATEGORIA'),
  ('CATEGORIA_4', 'Categoría 4', 'CATEGORIA'),
  ('CATEGORIA_5', 'Categoría 5', 'CATEGORIA'),
  ('CATEGORIA_6', 'Categoría 6', 'CATEGORIA'),
  ('CATEGORIA_7', 'Categoría 7', 'CATEGORIA'),
  ('CATEGORIA_8', 'Categoría 8', 'CATEGORIA'),
  ('CATEGORIA_9', 'Categoría 9', 'CATEGORIA'),
  ('CATEGORIA_10', 'Categoría 10', 'CATEGORIA'),
  ('ROJO', 'Botón Rojo - Anular', 'ACCION'),
  ('VERDE', 'Botón Verde - Confirmar', 'ACCION'),
  ('EFECTIVO', 'Pago en Efectivo', 'ACCION'),
  ('POS', 'Pago con POS', 'ACCION'),
  ('BOMBERO', 'Categoría Bombero', 'ACCION'),
  ('AMBULANCIA', 'Categoría Ambulancia', 'ACCION'),
  ('POLICIA', 'Categoría Policía', 'ACCION'),
  ('EXENTO', 'Exención de pago', 'ACCION'),
  ('NOMINAR', 'Nominar/Asignar', 'ACCION');

-- Mapeos por defecto
INSERT INTO MAPEO_TECLADO (id_tecla, combinacion_teclas) VALUES
  (1, 'SHIFT+F1'),
  (2, 'SHIFT+F2'),
  (3, 'SHIFT+F3'),
  (4, 'SHIFT+F4'),
  (5, 'SHIFT+F5'),
  (6, 'SHIFT+F6'),
  (7, 'SHIFT+F7'),
  (8, 'SHIFT+F8'),
  (9, 'SHIFT+F9'),
  (10, 'SHIFT+F10'),
  (11, 'CTRL+F8'),
  (12, 'CTRL+F9'),
  (13, 'CTRL+F1'),
  (14, 'CTRL+F2'),
  (15, 'CTRL+F4'),
  (16, 'CTRL+F3'),
  (17, 'CTRL+F5'),
  (18, 'CTRL+F6'),
  (19, 'CTRL+F7');
```

## Consultas útiles

```sql
-- Obtener todas las asignaciones actuales
SELECT ts.nombre_tecla, ts.descripcion, mt.combinacion_teclas
FROM TECLAS_ESPECIALES ts
JOIN MAPEO_TECLADO mt ON ts.id_tecla = mt.id_tecla
WHERE mt.activo = TRUE
ORDER BY ts.nombre_tecla;

-- Buscar tecla por combinación
SELECT ts.nombre_tecla, ts.descripcion
FROM TECLAS_ESPECIALES ts
JOIN MAPEO_TECLADO mt ON ts.id_tecla = mt.id_tecla
WHERE mt.combinacion_teclas = 'CTRL+F8' AND mt.activo = TRUE;

-- Actualizar mapeo
UPDATE MAPEO_TECLADO
SET combinacion_teclas = 'ALT+SHIFT+F5', fecha_creacion = NOW()
WHERE id_tecla = 5 AND activo = TRUE;
```

## Consideraciones

1. **Compatibilidad**: El formato `CTRL+SHIFT+ALT+KEY` es agnóstico de plataforma.
2. **Validación**: Garantizar que no existan dos teclas asignadas a la misma combinación en la misma estación.
3. **Auditoría**: Registrar cambios de mapeo con timestamp y usuario.
4. **Multi-estación**: Si el sistema es para múltiples estaciones, la tabla debe incluir `estacion_id`.

