package framework.teclado;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Objects;

/**
 * Representa una combinacion de tecla con sus modificadores (Ctrl, Shift, Alt).
 * Puede serializarse como "CTRL+SHIFT+F1" o "F8" para persistencia en BD.
 */
public final class DescritorTecla {

    private final KeyCode keyCode;
    private final boolean ctrl;
    private final boolean shift;
    private final boolean alt;

    public DescritorTecla(KeyCode keyCode, boolean ctrl, boolean shift, boolean alt) {
        this.keyCode = Objects.requireNonNull(keyCode, "keyCode no puede ser null");
        this.ctrl = ctrl;
        this.shift = shift;
        this.alt = alt;
    }

    public DescritorTecla(KeyCode keyCode) {
        this(keyCode, false, false, false);
    }

    public KeyCode getKeyCode() {
        return keyCode;
    }

    public boolean isCtrl() {
        return ctrl;
    }

    public boolean isShift() {
        return shift;
    }

    public boolean isAlt() {
        return alt;
    }

    public boolean coincide(KeyEvent event) {
        return event.getCode() == keyCode
            && event.isControlDown() == ctrl
            && event.isShiftDown() == shift
            && event.isAltDown() == alt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (ctrl) sb.append("CTRL+");
        if (shift) sb.append("SHIFT+");
        if (alt) sb.append("ALT+");
        sb.append(keyCode.name());
        return sb.toString();
    }

    public static DescritorTecla fromString(String representacion) {
        if (representacion == null || representacion.isBlank()) {
            throw new IllegalArgumentException("Representacion no puede estar vacia");
        }

        boolean ctrl = false;
        boolean shift = false;
        boolean alt = false;
        String parte = representacion.toUpperCase();

        if (parte.startsWith("CTRL+")) {
            ctrl = true;
            parte = parte.substring(5);
        }
        if (parte.startsWith("SHIFT+")) {
            shift = true;
            parte = parte.substring(6);
        }
        if (parte.startsWith("ALT+")) {
            alt = true;
            parte = parte.substring(4);
        }

        KeyCode keyCode = KeyCode.valueOf(parte.trim());
        return new DescritorTecla(keyCode, ctrl, shift, alt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DescritorTecla)) return false;
        DescritorTecla that = (DescritorTecla) o;
        return ctrl == that.ctrl && shift == that.shift && alt == that.alt && keyCode == that.keyCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyCode, ctrl, shift, alt);
    }
}

