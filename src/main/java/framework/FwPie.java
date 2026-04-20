package framework;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;

/**
 * Barra de pie reutilizable con alto responsivo y ancho al 100%.
 *
 * Extiende FwFranjaContenedor para soporte de scroll horizontal automático
 * cuando el contenido excede el ancho disponible.
 *
 * Para agregar componentes usar: getContenedorInterno().getChildren().addAll(...)
 */
public class FwPie extends FwFranjaContenedor {

    // Porcentajes para botones del pie
    protected static final double PCT_BOTON_CONFIG_ANCHO = 0.10;  // 10% del ancho
    protected static final double PCT_BOTON_SALIR_ANCHO = 0.08;   // 8% del ancho
    protected static final double PCT_BOTON_ALTO = 0.77;          // 77% del alto del pie

    public FwPie() {
        super(FwLayoutResponsivo.MIN_PIE_ALTO);

        getStyleClass().add("barra-pie");
        getContenedorInterno().setAlignment(Pos.CENTER_LEFT);
        getContenedorInterno().setSpacing(FwLayoutResponsivo.SEPARACION_CAMPOS);

        // Estilos sin bordes
        setStyle("-fx-background-color: " + FwColorTema.toCss(FwColorTema.FONDO_OSCURO) + "; -fx-padding: 0; -fx-border-width: 0;");
        // Padding izquierdo y derecho con SEPARACION_CAMPOS, spacing entre controles
        double sep = FwLayoutResponsivo.SEPARACION_CAMPOS;
        getContenedorInterno().setStyle(
            "-fx-background-color: " + FwColorTema.toCss(FwColorTema.FONDO_OSCURO) + "; " +
            "-fx-padding: 0 " + (int)sep + " 0 " + (int)sep + "; " +
            "-fx-border-width: 0;"
        );
    }

    /**
     * Configura los bindings responsivos. Llamado automáticamente por FwVentana.
     * Las subclases pueden sobreescribir para agregar más bindings.
     */
    @Override
    public void configurarResponsivo(ReadOnlyDoubleProperty anchoVentana, ReadOnlyDoubleProperty altoVentana) {
        super.configurarResponsivo(anchoVentana, altoVentana);
    }

    /**
     * Crea un FwBoton responsivo para el pie con porcentajes predeterminados.
     */
    protected FwBoton crearBoton(String texto) {
        FwBoton btn = new FwBoton(texto);
        return btn;
    }

    /**
     * Crea un FwBoton responsivo para el pie con porcentaje de ancho específico.
     */
    protected FwBoton crearBotonResponsivo(String texto, double pctAncho) {
        FwBoton btn = new FwBoton(texto);

        if (anchoVentana != null) {
            btn.prefWidthProperty().bind(anchoVentana.multiply(pctAncho));
        }
        btn.prefHeightProperty().bind(heightProperty().multiply(PCT_BOTON_ALTO));

        return btn;
    }

    /**
     * @deprecated Usar crearBoton(String) o crearBotonResponsivo para botones responsivos
     */
    @Deprecated
    protected FwBoton crearBoton(String texto, double ancho, double alto) {
        FwBoton btn = new FwBoton(texto);
        btn.setPrefSize(ancho, alto);
        return btn;
    }


    /**
     * Agrega componentes al contenedor interno del pie.
     * Usar este método en lugar de getChildren().addAll()
     */
    public void agregarComponentes(Node... nodos) {
        getContenedorInterno().getChildren().addAll(nodos);
    }
}
