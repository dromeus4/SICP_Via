package app;

import framework.FwBarraPanel;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * Panel horizontal de la aplicación entre cabecera y contenido.
 */
public class AppPanel extends FwBarraPanel {

    public AppPanel() {
        // Por ahora solo un placeholder
        Label placeholder = new Label("Panel de herramientas");
        placeholder.setTextFill(Color.BLACK);
        agregarComponentes(placeholder);
    }
}
