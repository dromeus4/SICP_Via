package framework;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;

/**
 * Panel horizontal entre cabecera y contenido.
 *
 * Extiende FwFranjaContenedor para soporte de scroll horizontal automático
 * cuando el contenido excede el ancho disponible.
 *
 * Fondo: PANEL_FONDO
 * Borde superior/izquierdo: PANEL_BRDOSC
 * Borde inferior/derecho: PANEL_BRDCLR
 *
 * Para agregar componentes usar: getContenedorInterno().getChildren().addAll(...)
 */
public class FwBarraPanel extends FwFranjaContenedor {

    // Porcentajes de padding relativo
    private static final double PCT_PADDING_H = 0.01;   // 1% del ancho
    private static final double PCT_PADDING_V = 0.10;   // 10% del alto del panel

    public FwBarraPanel() {
        super(FwLayoutResponsivo.MIN_PANEL_ALTO);

        getStyleClass().add("barra-panel");
        getContenedorInterno().setAlignment(Pos.CENTER_LEFT);
        getContenedorInterno().setSpacing(8);

        // Fondo y bordes biselados invertidos (oscuro arriba/izq, claro abajo/der)
        // Sin padding interno adicional
        String estiloFondo = String.format(
            "-fx-background-color: %s; " +
            "-fx-border-color: %s %s %s %s; " +
            "-fx-border-width: 2; " +
            "-fx-padding: 0;",
            FwColorTema.toCss(FwColorTema.PANEL_FONDO),
            FwColorTema.toCss(FwColorTema.PANEL_BRDOSC),  // top
            FwColorTema.toCss(FwColorTema.PANEL_BRDCLR),  // right
            FwColorTema.toCss(FwColorTema.PANEL_BRDCLR),  // bottom
            FwColorTema.toCss(FwColorTema.PANEL_BRDOSC)   // left
        );
        setStyle(estiloFondo);
        getContenedorInterno().setStyle("-fx-background-color: " + FwColorTema.toCss(FwColorTema.PANEL_FONDO) + "; -fx-padding: 0; -fx-border-width: 0;");
    }

    /**
     * Configura los bindings responsivos. Llamado automáticamente por FwVentana.
     * Las subclases pueden sobreescribir para agregar más bindings.
     */
    @Override
    public void configurarResponsivo(ReadOnlyDoubleProperty anchoVentana, ReadOnlyDoubleProperty altoVentana) {
        super.configurarResponsivo(anchoVentana, altoVentana);

        // Padding responsivo
        anchoVentana.addListener((obs, oldVal, newVal) -> actualizarPadding());
        heightProperty().addListener((obs, oldVal, newVal) -> actualizarPadding());
        actualizarPadding();
    }

    private void actualizarPadding() {
        double paddingH = anchoVentana != null ? anchoVentana.get() * PCT_PADDING_H : 12;
        double paddingV = getHeight() * PCT_PADDING_V;
        getContenedorInterno().setPadding(new Insets(paddingV, paddingH, paddingV, paddingH));
    }

    /**
     * Agrega componentes al contenedor interno del panel.
     * Usar este método en lugar de getChildren().addAll()
     */
    public void agregarComponentes(Node... nodos) {
        getContenedorInterno().getChildren().addAll(nodos);
    }
}
