package framework;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.layout.Region;

/**
 * Utilidad para gestionar layouts responsivos basados en porcentajes.
 *
 * Los porcentajes se definen como valores entre 0.0 y 1.0 (ej: 0.33 = 33%).
 * Los valores se convierten automáticamente a píxeles cuando cambia el tamaño
 * del contenedor padre.
 */
public final class FwLayoutResponsivo {

    private FwLayoutResponsivo() {
        // Utilidad estática
    }

    // ===============================
    // CONSTANTES DE PORCENTAJES
    // ===============================

    /** Porcentaje de altura para la cabecera: 7% */
    public static final double PCT_CABECERA = 0.07;

    /** Porcentaje de altura para el panel: 5.5% */
    public static final double PCT_PANEL = 0.055;

    /** Porcentaje de altura para el pie: 6.5% */
    public static final double PCT_PIE = 0.065;

    /** Porcentaje de altura para contenido: ~81.5% (el resto) */
    public static final double PCT_CONTENIDO = 1.0 - PCT_CABECERA - PCT_PANEL - PCT_PIE;

    // ===============================
    // ALTURAS MÍNIMAS EN PÍXELES
    // ===============================

    /** Alto mínimo de la ventana principal: 350px */
    public static final double MIN_VENTANA_ALTO = 350;

    /** Alto mínimo de la cabecera: 35px */
    public static final double MIN_CABECERA_ALTO = 35;

    /** Alto mínimo del panel: 25px */
    public static final double MIN_PANEL_ALTO = 25;

    /** Alto mínimo del pie: 30px */
    public static final double MIN_PIE_ALTO = 30;

    /** Alto mínimo del contenido: 300px */
    public static final double MIN_CONTENIDO_ALTO = 300;

    // ===============================
    // ANCHOS MÍNIMOS EN PÍXELES
    // ===============================

    /** Ancho mínimo de la ventana principal: 300px */
    public static final double MIN_VENTANA_ANCHO = 300;

    /** Ancho mínimo de las franjas (cabecera, panel, contenido, pie): 500px */
    public static final double MIN_FRANJA_ANCHO = 500;

    /** Separación mínima entre campos en CABECERA y PIE: 10px (spacing y padding) */
    public static final double SEPARACION_CAMPOS = 10;

    /** Separación y padding dentro del CONTENIDO (entre franjas verticales): 20px */
    public static final double SEPARACION_CONTENIDO = 20;

    // ===============================
    // SISTEMA DE TAMAÑO DE TEXTO
    // ===============================

    /**
     * Porcentaje del ALTO de la ventana para calcular TEXTO_TAMANO base.
     * Este es el tamaño de referencia para todos los textos.
     * TEXTO_TAMANO = altoVentana * PCT_TEXTO_TAMANO
     */
    public static final double PCT_TEXTO_TAMANO = 0.018;  // 1.8% del alto = tamaño base

    /** Mínimo absoluto para TEXTO_TAMANO en píxeles */
    public static final double MIN_TEXTO_TAMANO = 10;

    /** Máximo absoluto para TEXTO_TAMANO en píxeles */
    public static final double MAX_TEXTO_TAMANO = 48;

    // Porcentajes relativos a TEXTO_TAMANO para diferentes tipos de texto

    /** Texto normal: 100% de TEXTO_TAMANO */
    public static final double PCT_TEXTO_NORMAL = 1.0;

    /** Texto de botones: 110% de TEXTO_TAMANO */
    public static final double PCT_TEXTO_BOTON = 1.1;

    /** Texto de título (cabecera): 130% de TEXTO_TAMANO */
    public static final double PCT_TEXTO_TITULO = 1.3;

    /** Texto de logo: 115% de TEXTO_TAMANO */
    public static final double PCT_TEXTO_LOGO = 1.15;

    /** Texto pequeño (info, etiquetas): 90% de TEXTO_TAMANO */
    public static final double PCT_TEXTO_PEQUENO = 0.9;

    /** Texto del reloj: 110% de TEXTO_TAMANO */
    public static final double PCT_TEXTO_RELOJ = 1.1;

    // Porcentajes para botones principales del contenido
    /** Ancho del botón principal: 33% del ancho del contenedor */
    public static final double PCT_BOTON_PRINCIPAL_ANCHO = 0.33;

    /** Alto del botón principal: 12% del alto del contenedor */
    public static final double PCT_BOTON_PRINCIPAL_ALTO = 0.12;

    // Porcentajes para botones del pie
    /** Ancho botón configurar: 10% del ancho */
    public static final double PCT_BOTON_PIE_CONFIG_ANCHO = 0.10;

    /** Ancho botón salir: 8% del ancho */
    public static final double PCT_BOTON_PIE_SALIR_ANCHO = 0.08;

    /** Alto botones del pie: 77% del alto del pie */
    public static final double PCT_BOTON_PIE_ALTO = 0.77;

    // Porcentajes para padding
    /** Padding horizontal estándar: 1.25% del ancho */
    public static final double PCT_PADDING_H = 0.0125;

    /** Padding vertical estándar: 1% del alto */
    public static final double PCT_PADDING_V = 0.01;

    // ===============================
    // MÉTODOS DE TEXTO RESPONSIVO
    // ===============================

    /**
     * Calcula el TEXTO_TAMANO base en píxeles dado el alto de la ventana.
     * Este valor es la referencia para todos los tamaños de texto.
     *
     * @param altoVentana alto de la ventana en píxeles
     * @return tamaño base de texto en píxeles (entre MIN y MAX)
     */
    public static double calcularTextoTamano(double altoVentana) {
        double tamano = altoVentana * PCT_TEXTO_TAMANO;
        return Math.max(MIN_TEXTO_TAMANO, Math.min(MAX_TEXTO_TAMANO, tamano));
    }

    /**
     * Calcula el tamaño de un texto específico basado en TEXTO_TAMANO.
     *
     * @param altoVentana alto de la ventana en píxeles
     * @param porcentajeTexto porcentaje relativo a TEXTO_TAMANO (ej: 1.2 para 120%)
     * @return tamaño de texto en píxeles
     */
    public static double calcularTexto(double altoVentana, double porcentajeTexto) {
        return calcularTextoTamano(altoVentana) * porcentajeTexto;
    }

    /**
     * Calcula el tamaño de texto normal (100% de TEXTO_TAMANO).
     */
    public static double textoNormal(double altoVentana) {
        return calcularTexto(altoVentana, PCT_TEXTO_NORMAL);
    }

    /**
     * Calcula el tamaño de texto para botones (120% de TEXTO_TAMANO).
     */
    public static double textoBoton(double altoVentana) {
        return calcularTexto(altoVentana, PCT_TEXTO_BOTON);
    }

    /**
     * Calcula el tamaño de texto para títulos (130% de TEXTO_TAMANO).
     */
    public static double textoTitulo(double altoVentana) {
        return calcularTexto(altoVentana, PCT_TEXTO_TITULO);
    }

    /**
     * Calcula el tamaño de texto para el logo (115% de TEXTO_TAMANO).
     */
    public static double textoLogo(double altoVentana) {
        return calcularTexto(altoVentana, PCT_TEXTO_LOGO);
    }

    /**
     * Calcula el tamaño de texto pequeño (90% de TEXTO_TAMANO).
     */
    public static double textoPequeno(double altoVentana) {
        return calcularTexto(altoVentana, PCT_TEXTO_PEQUENO);
    }

    /**
     * Calcula el tamaño de texto para el reloj (110% de TEXTO_TAMANO).
     */
    public static double textoReloj(double altoVentana) {
        return calcularTexto(altoVentana, PCT_TEXTO_RELOJ);
    }

    // ===============================
    // MÉTODOS DE BINDING (deprecados, usar los nuevos)
    // ===============================

    /**
     * @deprecated Usar calcularTexto() en su lugar
     */
    @Deprecated
    public static double fontSizePx(double altoReferencia, double porcentaje) {
        double size = altoReferencia * porcentaje;
        return Math.max(10, Math.min(72, size));
    }

    /**
     * Vincula el ancho preferido de una región a un porcentaje del ancho del padre.
     */
    public static void bindAncho(Region region, ReadOnlyDoubleProperty anchoReferencia, double porcentaje) {
        region.prefWidthProperty().bind(anchoReferencia.multiply(porcentaje));
    }

    /**
     * Vincula el alto preferido de una región a un porcentaje del alto del padre.
     */
    public static void bindAlto(Region region, ReadOnlyDoubleProperty altoReferencia, double porcentaje) {
        region.prefHeightProperty().bind(altoReferencia.multiply(porcentaje));
    }

    /**
     * Vincula ancho y alto preferidos de una región a porcentajes de las referencias.
     */
    public static void bindTamaño(Region region,
                                   ReadOnlyDoubleProperty anchoReferencia, double pctAncho,
                                   ReadOnlyDoubleProperty altoReferencia, double pctAlto) {
        region.prefWidthProperty().bind(anchoReferencia.multiply(pctAncho));
        region.prefHeightProperty().bind(altoReferencia.multiply(pctAlto));
    }

    /**
     * Vincula el alto fijo (min, pref, max) de una región a un porcentaje del alto de referencia.
     * Útil para barras como cabecera, panel, pie.
     */
    public static void bindAltoFijo(Region region, ReadOnlyDoubleProperty altoReferencia, double porcentaje) {
        var binding = altoReferencia.multiply(porcentaje);
        region.minHeightProperty().bind(binding);
        region.prefHeightProperty().bind(binding);
        region.maxHeightProperty().bind(binding);
    }

    /**
     * Vincula el ancho fijo (min, pref, max) de una región a un porcentaje del ancho de referencia.
     */
    public static void bindAnchoFijo(Region region, ReadOnlyDoubleProperty anchoReferencia, double porcentaje) {
        var binding = anchoReferencia.multiply(porcentaje);
        region.minWidthProperty().bind(binding);
        region.prefWidthProperty().bind(binding);
        region.maxWidthProperty().bind(binding);
    }

    // ===============================
    // MÉTODOS DE CONVERSIÓN
    // ===============================

    /**
     * Convierte un porcentaje a píxeles dado un valor de referencia.
     */
    public static double aPx(double valorReferencia, double porcentaje) {
        return valorReferencia * porcentaje;
    }

    /**
     * Convierte píxeles a porcentaje dado un valor de referencia.
     */
    public static double aPct(double valorReferencia, double pixeles) {
        return valorReferencia > 0 ? pixeles / valorReferencia : 0;
    }
}
