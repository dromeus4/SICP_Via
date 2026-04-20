package framework;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Control visual reutilizable que muestra un campo de información
 * con un rótulo arriba (más pequeño) y un contenido abajo (más grande, negrita).
 */
public class FwCampoInfo extends VBox {

    private final Label lblRotulo;
    private final Label lblContenido;
    private Color colorTexto = FwColorTema.TEXTO_CLARO;

    /**
     * Constructor con rótulo y contenido inicial.
     *
     * @param rotulo     Texto del rótulo (ej: "BOLSA:")
     * @param contenido  Texto del contenido inicial (ej: "---")
     */
    public FwCampoInfo(String rotulo, String contenido) {
        super(2); // spacing de 2px
        setAlignment(Pos.CENTER_LEFT);

        lblRotulo = new Label(rotulo);
        lblRotulo.setTextFill(colorTexto);

        lblContenido = new Label(contenido);
        lblContenido.setTextFill(colorTexto);

        getChildren().addAll(lblRotulo, lblContenido);
    }

    /**
     * Constructor solo con rótulo.
     */
    public FwCampoInfo(String rotulo) {
        this(rotulo, "---");
    }

    /**
     * Establece el color del texto para ambos labels.
     */
    public void setColorTexto(Color color) {
        this.colorTexto = color;
        lblRotulo.setTextFill(color);
        lblContenido.setTextFill(color);
    }

    /**
     * Actualiza el texto del contenido.
     */
    public void setContenido(String texto) {
        lblContenido.setText(texto);
    }

    /**
     * Obtiene el texto del contenido.
     */
    public String getContenido() {
        return lblContenido.getText();
    }

    /**
     * Actualiza el texto del rótulo.
     */
    public void setRotulo(String texto) {
        lblRotulo.setText(texto);
    }

    /**
     * Obtiene el texto del rótulo.
     */
    public String getRotulo() {
        return lblRotulo.getText();
    }

    /**
     * Configura los tamaños de fuente según el alto disponible.
     * Rótulo: fuente más pequeña, normal
     * Contenido: fuente más grande, negrita
     *
     * @param alto Alto del contenedor
     */
    public void configurarTamanos(double alto) {
        double fontRotulo = alto * 0.25;
        double fontContenido = alto * 0.35;

        lblRotulo.setStyle(FwFuentes.cssRegular(fontRotulo));
        lblContenido.setStyle(FwFuentes.cssBold(fontContenido));

        // Mantener el color del texto
        lblRotulo.setTextFill(colorTexto);
        lblContenido.setTextFill(colorTexto);
    }

    /**
     * Configura dimensiones fijas para el control.
     */
    public void configurarDimensiones(double ancho, double alto) {
        setMinWidth(ancho);
        setPrefWidth(ancho);
        setMaxWidth(ancho);
        setMinHeight(alto);
        setPrefHeight(alto);
        setMaxHeight(alto);

        configurarTamanos(alto);
    }

    /**
     * Acceso al label del rótulo para configuración avanzada.
     */
    public Label getLabelRotulo() {
        return lblRotulo;
    }

    /**
     * Acceso al label del contenido para configuración avanzada.
     */
    public Label getLabelContenido() {
        return lblContenido;
    }
}

