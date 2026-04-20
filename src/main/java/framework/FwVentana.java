package framework;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Ventana principal reutilizable con estructura de 4 franjas horizontales:
 * - CABECERA: 7% del alto (mínimo 35px)
 * - PANEL: 5.5% del alto (mínimo 25px)
 * - CONTENIDO: 81% del alto (mínimo 300px)
 * - PIE: 6.5% del alto (mínimo 30px)
 *
 * Las subclases proporcionan las secciones concretas.
 * Todos los componentes se recomponen automáticamente al redimensionar.
 *
 * Tamaños mínimos:
 * - Ventana: 300px ancho x 350px alto
 * - Franjas: 500px ancho mínimo (scroll horizontal si es necesario)
 *
 * Cuando la ventana se achica más de lo que caben las franjas,
 * aparece scroll vertical automático.
 *
 * Sistema de texto unificado:
 * - TEXTO_TAMANO se calcula como porcentaje del ALTO de la ventana
 * - Todos los tamaños de texto son porcentajes de TEXTO_TAMANO
 */
public abstract class FwVentana {

    private final Stage stage;
    private final ScrollPane scrollPrincipal;
    private final VBox contenedorFranjas;
    private final Scene scene;

    // Componentes principales
    private final FwCabecera cabecera;
    private final FwBarraPanel panel;
    private final FwContenido contenido;
    private final FwPie pie;

    public FwVentana(Stage stage, String titulo, double ancho, double alto) {
        this.stage = stage;

        // Contenedor de las 4 franjas
        this.contenedorFranjas = new VBox();

        // ScrollPane principal para scroll horizontal cuando las franjas no caben
        this.scrollPrincipal = new ScrollPane(contenedorFranjas);
        scrollPrincipal.setFitToWidth(true);   // Ajustar al ancho de la ventana
        scrollPrincipal.setFitToHeight(true);  // Ajustar al alto de la ventana
        scrollPrincipal.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPrincipal.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Sin scroll vertical
        scrollPrincipal.getStyleClass().add("ventana-scroll");
        scrollPrincipal.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0; -fx-border-width: 0;");

        cabecera = crearCabecera();
        panel = crearPanel();
        contenido = crearContenido();
        pie = crearPie();

        contenedorFranjas.getChildren().addAll(cabecera, panel, contenido, pie);

        // Ancho mínimo del contenedor = suma de anchos mínimos (500px)
        contenedorFranjas.setMinWidth(FwLayoutResponsivo.MIN_FRANJA_ANCHO);

        // Alto mínimo del contenedor = suma de altos mínimos de franjas
        double altoMinimoTotal = FwLayoutResponsivo.MIN_CABECERA_ALTO
                               + FwLayoutResponsivo.MIN_PANEL_ALTO
                               + FwLayoutResponsivo.MIN_CONTENIDO_ALTO
                               + FwLayoutResponsivo.MIN_PIE_ALTO;
        contenedorFranjas.setMinHeight(altoMinimoTotal);

        scene = new Scene(scrollPrincipal, ancho, alto);

        // Configurar tamaños mínimos de la ventana
        stage.setMinWidth(FwLayoutResponsivo.MIN_VENTANA_ANCHO);
        stage.setMinHeight(FwLayoutResponsivo.MIN_VENTANA_ALTO);

        // Vincular alturas al tamaño de la escena con mínimos
        configurarBindingsResponsivos();

        // Contenido ocupa el espacio restante
        VBox.setVgrow(contenido, Priority.ALWAYS);

        URL cssApp = getClass().getResource("/css/app.css");
        if (cssApp != null) {
            scene.getStylesheets().add(cssApp.toExternalForm());
        }
        stage.setScene(scene);
        stage.setTitle(titulo);
    }

    /**
     * Configura los bindings responsivos para que los componentes
     * se ajusten automáticamente cuando cambia el tamaño de la ventana.
     *
     * Cada franja tiene un porcentaje del alto total pero respeta un mínimo en píxeles.
     * El ancho de cada franja es 100% pero con un mínimo de 500px (scroll horizontal).
     */
    private void configurarBindingsResponsivos() {
        ReadOnlyDoubleProperty altoScene = scene.heightProperty();
        ReadOnlyDoubleProperty anchoScene = scene.widthProperty();

        // Alturas basadas en porcentaje con mínimo garantizado
        bindAltoConMinimo(cabecera, altoScene, FwLayoutResponsivo.PCT_CABECERA, FwLayoutResponsivo.MIN_CABECERA_ALTO);
        bindAltoConMinimo(panel, altoScene, FwLayoutResponsivo.PCT_PANEL, FwLayoutResponsivo.MIN_PANEL_ALTO);
        bindAltoConMinimo(pie, altoScene, FwLayoutResponsivo.PCT_PIE, FwLayoutResponsivo.MIN_PIE_ALTO);

        // El contenido usa el mínimo de su clase base (ya configurado en FwContenido)

        // Notificar a los componentes las propiedades de referencia
        cabecera.configurarResponsivo(anchoScene, altoScene);
        panel.configurarResponsivo(anchoScene, altoScene);
        contenido.configurarResponsivo(anchoScene, altoScene);
        pie.configurarResponsivo(anchoScene, altoScene);
    }

    /**
     * Vincula el alto de una región a un porcentaje del alto de referencia,
     * garantizando un valor mínimo en píxeles.
     */
    private void bindAltoConMinimo(javafx.scene.layout.Region region,
                                    ReadOnlyDoubleProperty altoReferencia,
                                    double porcentaje, double minimo) {
        var binding = Bindings.createDoubleBinding(
            () -> Math.max(altoReferencia.get() * porcentaje, minimo),
            altoReferencia
        );
        region.minHeightProperty().bind(binding);
        region.prefHeightProperty().bind(binding);
        region.maxHeightProperty().bind(binding);
    }

    /** Subclase debe proveer la cabecera concreta. */
    protected abstract FwCabecera crearCabecera();

    /** Subclase debe proveer el panel concreto. */
    protected abstract FwBarraPanel crearPanel();

    /** Subclase debe proveer el contenido concreto. */
    protected abstract FwContenido crearContenido();

    /** Subclase debe proveer el pie concreto. */
    protected abstract FwPie crearPie();

    public void mostrar() {
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }

    /** Obtiene la propiedad de ancho de la escena para bindings externos. */
    public ReadOnlyDoubleProperty anchoProperty() {
        return scene.widthProperty();
    }

    /** Obtiene la propiedad de alto de la escena para bindings externos. */
    public ReadOnlyDoubleProperty altoProperty() {
        return scene.heightProperty();
    }
}
