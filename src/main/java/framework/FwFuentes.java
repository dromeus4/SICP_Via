package framework;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registra fuentes TTF empaquetadas para que JavaFX las considere disponibles
 * en cualquier plataforma donde se ejecute la aplicacion.
 * Expone constantes para usar los tipos de letra sin hardcodear nombres.
 */
public final class FwFuentes {

    // ========== CONSTANTES DE TIPOS DE LETRA ==========

    /** Fuente base regular para textos normales */
    public static final String REGULAR = "Barlow";

    /** Fuente para textos en negrita */
    public static final String BOLD = "Barlow";

    /** Fuente extra negrita para títulos destacados */
    public static final String EXTRA_BOLD = "Barlow";

    /** Fuente negra para encabezados principales */
    public static final String BLACK = "Barlow";

    /** Fuente ligera para textos secundarios */
    public static final String LIGHT = "Barlow";

    /** Fuente media para textos con énfasis moderado */
    public static final String MEDIUM = "Barlow";

    /** Fuente semi-negrita para subtítulos */
    public static final String SEMI_BOLD = "Barlow";

    /** Fuente especial para el reloj (Ericsson) */
    public static final String RELOJ = "Ericsson";

    // ========== PESOS DE FUENTE ==========

    public static final FontWeight PESO_REGULAR = FontWeight.NORMAL;
    public static final FontWeight PESO_BOLD = FontWeight.BOLD;
    public static final FontWeight PESO_EXTRA_BOLD = FontWeight.EXTRA_BOLD;
    public static final FontWeight PESO_BLACK = FontWeight.BLACK;
    public static final FontWeight PESO_LIGHT = FontWeight.LIGHT;
    public static final FontWeight PESO_MEDIUM = FontWeight.MEDIUM;
    public static final FontWeight PESO_SEMI_BOLD = FontWeight.SEMI_BOLD;

    // ========== ARCHIVOS TTF ==========

    private static final String[] ARCHIVOS_TTF = {
        "Barlow-Black.ttf",
        "Barlow-BlackItalic.ttf",
        "Barlow-Bold.ttf",
        "Barlow-BoldItalic.ttf",
        "Barlow-ExtraBold.ttf",
        "Barlow-ExtraBoldItalic.ttf",
        "Barlow-ExtraLight.ttf",
        "Barlow-ExtraLightItalic.ttf",
        "Barlow-Italic.ttf",
        "Barlow-Light.ttf",
        "Barlow-LightItalic.ttf",
        "Barlow-Medium.ttf",
        "Barlow-MediumItalic.ttf",
        "Barlow-Regular.ttf",
        "Barlow-SemiBold.ttf",
        "Barlow-SemiBoldItalic.ttf",
        "Barlow-Thin.ttf",
        "Barlow-ThinItalic.ttf",
        "Ericsson.ttf"
    };

    // Fuentes cargadas para acceso rápido
    private static Font fuenteRegular;
    private static Font fuenteBold;
    private static Font fuenteReloj;
    private static boolean inicializado = false;

    private FwFuentes() {
    }

    /**
     * Registra todas las fuentes empaquetadas en el sistema.
     * Debe llamarse al inicio de la aplicación.
     */
    public static List<String> registrarFuentesEmpaquetadas() {
        List<String> familiasCargadas = new ArrayList<>();

        for (String archivo : ARCHIVOS_TTF) {
            URL fuenteUrl = FwFuentes.class.getResource("/fonts/" + archivo);
            if (fuenteUrl == null) {
                System.err.println("Fuente no encontrada: " + archivo);
                continue;
            }

            Font font = Font.loadFont(fuenteUrl.toExternalForm(), 12);
            if (font != null) {
                familiasCargadas.add(font.getFamily());
            }
        }

        inicializado = true;
        return Collections.unmodifiableList(familiasCargadas);
    }

    /**
     * Obtiene una fuente con el tipo y tamaño especificados.
     */
    public static Font obtenerFuente(String familia, FontWeight peso, double tamano) {
        return Font.font(familia, peso, tamano);
    }

    /**
     * Obtiene la fuente regular con el tamaño especificado.
     */
    public static Font regular(double tamano) {
        return Font.font(REGULAR, PESO_REGULAR, tamano);
    }

    /**
     * Obtiene la fuente bold con el tamaño especificado.
     */
    public static Font bold(double tamano) {
        return Font.font(BOLD, PESO_BOLD, tamano);
    }

    /**
     * Obtiene la fuente extra bold con el tamaño especificado.
     */
    public static Font extraBold(double tamano) {
        return Font.font(EXTRA_BOLD, PESO_EXTRA_BOLD, tamano);
    }

    /**
     * Obtiene la fuente black con el tamaño especificado.
     */
    public static Font black(double tamano) {
        return Font.font(BLACK, PESO_BLACK, tamano);
    }

    /**
     * Obtiene la fuente light con el tamaño especificado.
     */
    public static Font light(double tamano) {
        return Font.font(LIGHT, PESO_LIGHT, tamano);
    }

    /**
     * Obtiene la fuente medium con el tamaño especificado.
     */
    public static Font medium(double tamano) {
        return Font.font(MEDIUM, PESO_MEDIUM, tamano);
    }

    /**
     * Obtiene la fuente semi-bold con el tamaño especificado.
     */
    public static Font semiBold(double tamano) {
        return Font.font(SEMI_BOLD, PESO_SEMI_BOLD, tamano);
    }

    /**
     * Obtiene la fuente especial para el reloj con el tamaño especificado.
     */
    public static Font reloj(double tamano) {
        return Font.font(RELOJ, tamano);
    }

    /**
     * Genera el estilo CSS para una fuente regular.
     */
    public static String cssRegular(double tamano) {
        return String.format("-fx-font-family: '%s'; -fx-font-size: %.1fpx;", REGULAR, tamano);
    }

    /**
     * Genera el estilo CSS para una fuente bold.
     */
    public static String cssBold(double tamano) {
        return String.format("-fx-font-family: '%s'; -fx-font-weight: bold; -fx-font-size: %.1fpx;", BOLD, tamano);
    }

    /**
     * Genera el estilo CSS para una fuente con peso específico.
     */
    public static String css(String familia, String peso, double tamano) {
        return String.format("-fx-font-family: '%s'; -fx-font-weight: %s; -fx-font-size: %.1fpx;", familia, peso, tamano);
    }
}
