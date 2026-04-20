package framework;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;

/**
 * Panel de contenido central reutilizable. Ocupa todo el espacio restante.
 *
 * Incluye un ScrollPane interno que muestra scroll solo cuando necesario:
 * - Scroll vertical cuando el contenido excede la altura disponible
 * - Scroll horizontal cuando el contenido excede el ancho disponible
 *
 * Para agregar componentes usar: getContenedorInterno().getChildren().addAll(...)
 */
public class FwContenido extends StackPane {

    // Porcentajes para botones principales
    protected static final double PCT_BOTON_ANCHO = 0.33;   // 33% del ancho
    protected static final double PCT_BOTON_ALTO = 0.12;    // 12% del alto del contenido

    private final ScrollPane scrollPane;
    private final StackPane contenedorInterno;

    protected ReadOnlyDoubleProperty anchoVentana;
    protected ReadOnlyDoubleProperty altoVentana;

    public FwContenido() {
        // Establecer altura mínima
        setMinHeight(FwLayoutResponsivo.MIN_CONTENIDO_ALTO);

        // Contenedor interno donde van los componentes visuales
        contenedorInterno = new StackPane();
        contenedorInterno.setAlignment(Pos.CENTER);
        contenedorInterno.setStyle("-fx-background-color: " + FwColorTema.toCss(FwColorTema.FONDO_CLARO) + ";");
        // Ancho mínimo del contenedor interno = ancho mínimo de franja
        contenedorInterno.setMinWidth(FwLayoutResponsivo.MIN_FRANJA_ANCHO);

        // ScrollPane para scroll solo cuando necesario
        scrollPane = new ScrollPane(contenedorInterno);
        scrollPane.setFitToWidth(true);    // Ajustar al ancho disponible
        scrollPane.setFitToHeight(true);   // Ajustar al alto disponible
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("contenido-scroll");

        // Estilo transparente sin bordes para el ScrollPane
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0; -fx-border-width: 0;");

        // Agregar al StackPane padre (this)
        super.getChildren().add(scrollPane);

        getStyleClass().add("panel-contenido");
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: " + FwColorTema.toCss(FwColorTema.FONDO_CLARO) + "; -fx-padding: 0; -fx-border-width: 0;");
    }

    /**
     * Obtiene el contenedor interno (StackPane) donde se agregan los componentes visuales.
     */
    public StackPane getContenedorInterno() {
        return contenedorInterno;
    }

    /**
     * Configura los bindings responsivos. Llamado automáticamente por FwVentana.
     * Las subclases pueden sobreescribir para agregar más bindings.
     */
    public void configurarResponsivo(ReadOnlyDoubleProperty anchoVentana, ReadOnlyDoubleProperty altoVentana) {
        this.anchoVentana = anchoVentana;
        this.altoVentana = altoVentana;

        // El contenedor interno tiene ancho mínimo de 500px
        contenedorInterno.setMinWidth(FwLayoutResponsivo.MIN_FRANJA_ANCHO);
    }

    /**
     * Crea un FwBoton con tamaño responsivo basado en porcentajes predeterminados.
     */
    protected FwBoton crearBotonPrincipal(String texto) {
        FwBoton btn = new FwBoton(texto);

        if (anchoVentana != null) {
            btn.prefWidthProperty().bind(anchoVentana.multiply(PCT_BOTON_ANCHO));
        }
        btn.prefHeightProperty().bind(heightProperty().multiply(PCT_BOTON_ALTO));

        return btn;
    }

    /**
     * Agrega componentes al contenedor interno del contenido.
     * Usar este método en lugar de getChildren().addAll()
     */
    public void agregarComponentes(Node... nodos) {
        contenedorInterno.getChildren().addAll(nodos);
    }
}
