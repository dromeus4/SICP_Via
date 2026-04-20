package framework;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Control reutilizable que agrupa un rótulo (label) y un campo de texto.
 * <p>
 * Soporta dos modos:
 * <ul>
 *   <li>Texto normal ({@code esClave = false}): muestra un {@link TextField}</li>
 *   <li>Clave/contraseña ({@code esClave = true}): muestra un {@link PasswordField} con asteriscos</li>
 * </ul>
 * <p>
 * El rótulo es pequeño, sin negrita, pegado al campo de entrada.
 * El campo de entrada tiene fuente regular de 14px.
 * <p>
 * Esta clase se utiliza en diálogos como FwDialogoClave y FwDialogoClaveBolsa,
 * y debe usarse para cualquier futuro control de entrada (comboboxes, etc.)
 * para mantener consistencia visual.
 */
public class FwCampoTexto extends VBox {

    private final TextField campoTexto;
    private final Label rotulo;

    /**
     * Crea un campo de texto con rótulo.
     *
     * @param nombreRotulo Texto del rótulo (e.g. "CLAVE:", "BOLSA:")
     * @param esClave      Si es true, muestra asteriscos (PasswordField)
     */
    public FwCampoTexto(String nombreRotulo, boolean esClave) {
        this(nombreRotulo, esClave, 300);
    }

    /**
     * Crea un campo de texto con rótulo y ancho personalizado.
     *
     * @param nombreRotulo  Texto del rótulo (e.g. "CLAVE:", "BOLSA:")
     * @param esClave       Si es true, muestra asteriscos (PasswordField)
     * @param anchoCampo    Ancho preferido del campo en píxeles
     */
    public FwCampoTexto(String nombreRotulo, boolean esClave, double anchoCampo) {
        super(2); // spacing mínimo entre rótulo y campo

        // Rótulo: pequeño, sin negrita, discreto
        rotulo = new Label(nombreRotulo);
        rotulo.setStyle(String.format(
            "-fx-font-family: '%s'; -fx-font-size: 11px; -fx-text-fill: %s;",
            FwFuentes.REGULAR,
            FwColorTema.toCss(FwColorTema.TEXTO_OSCURO)
        ));

        // Campo de texto: fuente regular 14px, fondo blanco, borde gris
        campoTexto = esClave ? new PasswordField() : new TextField();
        campoTexto.setMaxWidth(anchoCampo);
        campoTexto.setPrefWidth(anchoCampo);
        campoTexto.setStyle(String.format(
            "-fx-font-family: '%s'; -fx-font-size: 14px; " +
            "-fx-background-color: white; " +
            "-fx-border-color: %s; -fx-border-width: 2; -fx-padding: 6;",
            FwFuentes.REGULAR,
            FwColorTema.toCss(FwColorTema.BORDE_GRIS)
        ));

        getChildren().addAll(rotulo, campoTexto);
    }

    /** Obtiene el texto ingresado por el usuario. */
    public String getTexto() {
        return campoTexto.getText();
    }

    /** Establece el texto del campo. */
    public void setTexto(String texto) {
        campoTexto.setText(texto);
    }

    /** Obtiene la referencia al TextField interno (para focus, listeners, etc.). */
    public TextField getCampoTexto() {
        return campoTexto;
    }

    /** Obtiene la referencia al Label del rótulo. */
    public Label getRotulo() {
        return rotulo;
    }
}

