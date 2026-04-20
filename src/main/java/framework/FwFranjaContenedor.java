package framework;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * Contenedor base para las franjas horizontales (cabecera, panel, pie).
 *
 * Funciona como un ScrollPane inteligente que:
 * - Muestra scroll horizontal y vertical solo cuando el contenido excede el espacio
 * - Respeta altura mínima configurada
 * - Respeta ancho mínimo de franja (500px)
 * - Permite que el contenido interno sea un HBox para distribución horizontal
 *
 * Las subclases agregan componentes al contenedor interno via getContenedorInterno().
 */
public abstract class FwFranjaContenedor extends StackPane {

    private final ScrollPane scrollPane;
    private final HBox contenedorInterno;
    private final double altoMinimo;

    protected ReadOnlyDoubleProperty anchoVentana;
    protected ReadOnlyDoubleProperty altoVentana;

    /**
     * Crea un contenedor de franja con altura mínima específica.
     *
     * @param altoMinimo altura mínima en píxeles que la franja nunca reducirá
     */
    protected FwFranjaContenedor(double altoMinimo) {
        this.altoMinimo = altoMinimo;

        // Contenedor interno donde van los componentes visuales
        contenedorInterno = new HBox();
        contenedorInterno.setAlignment(Pos.CENTER_LEFT);
        // Ancho mínimo del contenedor interno = ancho mínimo de franja
        contenedorInterno.setMinWidth(FwLayoutResponsivo.MIN_FRANJA_ANCHO);

        // ScrollPane para scroll horizontal solo cuando necesario (sin scroll vertical)
        scrollPane = new ScrollPane(contenedorInterno);
        scrollPane.setFitToHeight(true);   // Ajustar al alto disponible
        scrollPane.setFitToWidth(true);    // Ajustar al ancho disponible
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);  // Nunca scroll vertical en franjas
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("franja-scroll");

        // Estilo transparente y sin padding/márgenes
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0; -fx-border-width: 0;");

        getChildren().add(scrollPane);

        // Establecer altura mínima
        setMinHeight(altoMinimo);
    }

    /**
     * Obtiene el contenedor interno (HBox) donde se agregan los componentes visuales.
     */
    public HBox getContenedorInterno() {
        return contenedorInterno;
    }

    /**
     * Configura los bindings responsivos. Llamado automáticamente por FwVentana.
     * Las subclases deben llamar a super.configurarResponsivo() primero.
     */
    public void configurarResponsivo(ReadOnlyDoubleProperty anchoVentana, ReadOnlyDoubleProperty altoVentana) {
        this.anchoVentana = anchoVentana;
        this.altoVentana = altoVentana;

        // El contenedor interno tiene ancho mínimo de 500px
        contenedorInterno.setMinWidth(FwLayoutResponsivo.MIN_FRANJA_ANCHO);
    }

    /**
     * Obtiene la altura mínima configurada para esta franja.
     */
    public double getAltoMinimo() {
        return altoMinimo;
    }

    /**
     * Crea un espaciador que empuja elementos a la derecha dentro del contenedor interno.
     */
    protected Region crearEspaciador() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    /**
     * Agrega un nodo al contenedor interno.
     */
    protected void agregarAlContenedor(Node... nodos) {
        contenedorInterno.getChildren().addAll(nodos);
    }

    /**
     * Obtiene los hijos del contenedor interno.
     */
    protected javafx.collections.ObservableList<Node> getHijosContenedor() {
        return contenedorInterno.getChildren();
    }
}
