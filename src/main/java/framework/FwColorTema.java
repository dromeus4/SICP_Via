package framework;

import javafx.scene.paint.Color;

/**
 * Paleta de colores centralizada para toda la aplicación.
 */
public final class FwColorTema {
    private FwColorTema() {}

    public static final Color FONDO_CLARO      = Color.rgb(148, 228, 148);
    public static final Color FONDO_MEDIO      = Color.rgb(96, 192, 96);
    public static final Color FONDO_OSCURO     = Color.rgb(40, 141, 83);
    public static final Color TEXTO_CLARO      = Color.rgb(255, 255, 255);
    public static final Color TEXTO_OSCURO     = Color.rgb(0, 0, 0);
    public static final Color CBORDE_BLANCO    = Color.rgb(255, 255, 255);
    public static final Color BORDE_GRIS       = Color.rgb(192, 192, 192);
    public static final Color BORDE_NEGRO      = Color.rgb(0, 0, 0);

    // Colores del Panel
    public static final Color PANEL_FONDO      = Color.rgb(224, 224, 208);
    public static final Color PANEL_BRDOSC     = Color.rgb(160, 160, 116);
    public static final Color PANEL_BRDCLR     = Color.rgb(255, 255, 224);

    /** Convierte un Color de JavaFX a string CSS. */
    public static String toCss(Color c) {
        return String.format("rgb(%d,%d,%d)",
                (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
    }
}

