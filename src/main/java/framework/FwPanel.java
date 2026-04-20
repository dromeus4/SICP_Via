package framework;

import javafx.scene.layout.Region;

/**
 * Clase base del framework para paneles reutilizables.
 * Todas las secciones visuales (cabecera, pie, contenido) heredan de esta clase.
 */
public abstract class FwPanel extends Region {

    public FwPanel() {
        inicializar();
    }

    /** Template method: las subclases construyen su contenido aquí. */
    protected abstract void inicializar();

    /** Llamado cuando el panel debe refrescarse. Sobreescribir si es necesario. */
    public void refrescar() { }

    /** Llamado al destruir el panel. Sobreescribir para liberar recursos. */
    public void destruir() { }
}

