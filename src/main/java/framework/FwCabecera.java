package framework;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 * Barra de cabecera reutilizable con alto responsivo y ancho al 100%.
 *
 * Extiende FwFranjaContenedor para soporte de scroll horizontal automático
 * cuando el contenido excede el ancho disponible.
 *
 * Para agregar componentes usar: getContenedorInterno().getChildren().addAll(...)
 */
public class FwCabecera extends FwFranjaContenedor {

    public FwCabecera() {
        super(FwLayoutResponsivo.MIN_CABECERA_ALTO);

        getStyleClass().add("barra-cabecera");
        getContenedorInterno().setAlignment(Pos.CENTER_LEFT);
        getContenedorInterno().setSpacing(FwLayoutResponsivo.SEPARACION_CAMPOS);

        // Estilos sin bordes internos
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

    /** Crea un botón estilizado para la cabecera. */
    protected Button crearBoton(String texto, double ancho, double alto) {
        Button btn = new Button(texto);
        btn.getStyleClass().add("btn-barra");
        btn.setPrefSize(ancho, alto);
        btn.setStyle("-fx-background-color: #373737; -fx-text-fill: white; -fx-border-color: #b4b4b4; -fx-border-width: 2; -fx-cursor: hand;");
        return btn;
    }

    /**
     * Agrega componentes al contenedor interno de la cabecera.
     * Usar este método en lugar de getChildren().addAll()
     */
    public void agregarComponentes(Node... nodos) {
        getContenedorInterno().getChildren().addAll(nodos);
    }
}
