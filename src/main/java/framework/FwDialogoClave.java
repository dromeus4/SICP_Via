package framework;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Ventana de diálogo para ingresar una clave (contraseña).
 * <p>
 * Se utiliza para las operaciones ABRIR, MANTENIMIENTO y DESBLOQUEAR.
 * No contiene lógica de validación: solo recoge la clave ingresada
 * y la devuelve al llamador, quien decidirá qué hacer con ella.
 * <p>
 * Botones: ACEPTAR y CANCELAR (mismo estilo que FwMensaje).
 * <p>
 * Retorna {@code null} si el usuario cancela, o la clave ingresada si acepta.
 */
public final class FwDialogoClave {

    private final Stage stage;
    private String resultado = null;
    private final Timeline timerCierre;

    /**
     * Constructor.
     *
     * @param owner      Ventana padre (puede ser null)
     * @param titulo     Título de la ventana (e.g. "ABRIR CAJA", "MANTENIMIENTO", "DESBLOQUEAR CAJA")
     * @param encabezado Texto descriptivo (e.g. "Ingresa CLAVE DE CAJERO")
     */
    public FwDialogoClave(Stage owner, String titulo, String encabezado) {
        this(owner, titulo, encabezado, 0);
    }

    /**
     * Constructor con tiempo de vida.
     *
     * @param owner      Ventana padre (puede ser null)
     * @param titulo     Título de la ventana
     * @param encabezado Texto descriptivo mostrado sobre el campo de clave
     * @param tiempoVida Segundos antes de cerrar automáticamente (0 = 30s por defecto)
     */
    public FwDialogoClave(Stage owner, String titulo, String encabezado, int tiempoVida) {
        this.stage = new Stage();

        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            stage.initOwner(owner);
        }

        // ========== CONTENEDOR PRINCIPAL ==========
        BorderPane root = new BorderPane();
        root.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-border-color: %s %s %s %s; " +
            "-fx-border-width: 3;",
            FwColorTema.toCss(FwColorTema.FONDO_CLARO),
            FwColorTema.toCss(FwColorTema.CBORDE_BLANCO),
            FwColorTema.toCss(FwColorTema.BORDE_GRIS),
            FwColorTema.toCss(FwColorTema.BORDE_GRIS),
            FwColorTema.toCss(FwColorTema.CBORDE_BLANCO)
        ));
        root.setPadding(new Insets(0));

        // ========== BARRA DE TÍTULO ==========
        HBox barraTitulo = crearBarraTitulo(titulo);
        root.setTop(barraTitulo);

        // ========== CONTENIDO CENTRAL ==========
        VBox contenidoCentral = new VBox(12);
        contenidoCentral.setAlignment(Pos.CENTER_LEFT);
        contenidoCentral.setPadding(new Insets(20, 25, 20, 25));

        // Encabezado descriptivo: sin negrita, texto normal
        Label lblEncabezado = new Label(encabezado);
        lblEncabezado.setWrapText(true);
        lblEncabezado.setMaxWidth(450);
        lblEncabezado.setStyle(String.format(
            "-fx-font-family: '%s'; -fx-font-size: 14px; -fx-text-fill: %s;",
            FwFuentes.REGULAR,
            FwColorTema.toCss(FwColorTema.TEXTO_OSCURO)
        ));

        // Campo CLAVE (con asteriscos)
        FwCampoTexto campoClave = new FwCampoTexto("CLAVE:", true);

        contenidoCentral.getChildren().addAll(lblEncabezado, campoClave);
        root.setCenter(contenidoCentral);

        // ========== BOTONES ==========
        HBox panelBotones = crearPanelBotones(campoClave);
        root.setBottom(panelBotones);

        // ========== ESCENA ==========
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(250);

        // Centrar en pantalla o respecto al owner
        stage.setOnShown(e -> {
            stage.sizeToScene();
            if (owner != null) {
                stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth()) / 2);
                stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);
            } else {
                stage.centerOnScreen();
            }
            campoClave.getCampoTexto().requestFocus();
        });

        // ========== TIMER DE CIERRE AUTOMÁTICO ==========
        int tiempoEfectivo = tiempoVida > 0 ? tiempoVida : 30;
        timerCierre = new Timeline(new KeyFrame(Duration.seconds(tiempoEfectivo), e ->
            stage.close()
        ));
        timerCierre.setCycleCount(1);

        // Bloquear cierre con X
        stage.setOnCloseRequest(e -> resultado = null);
    }

    /**
     * Muestra el diálogo y espera la respuesta del usuario.
     *
     * @return La clave ingresada, o {@code null} si el usuario canceló
     */
    public String mostrar() {
        if (timerCierre != null) {
            timerCierre.play();
        }
        stage.showAndWait();
        if (timerCierre != null) {
            timerCierre.stop();
        }
        return resultado;
    }

    // ========== MÉTODOS PRIVADOS ==========

    private HBox crearBarraTitulo(String titulo) {
        HBox barra = new HBox();
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setPadding(new Insets(8, 12, 8, 12));
        barra.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-border-color: transparent transparent %s transparent; " +
            "-fx-border-width: 0 0 2 0;",
            FwColorTema.toCss(FwColorTema.FONDO_OSCURO),
            FwColorTema.toCss(FwColorTema.BORDE_GRIS)
        ));

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle(String.format(
            "-fx-font-family: '%s'; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: %s;",
            FwFuentes.BOLD,
            FwColorTema.toCss(FwColorTema.TEXTO_CLARO)
        ));

        barra.getChildren().add(lblTitulo);

        // Permitir arrastrar la ventana por la barra de título
        final double[] dragOffset = new double[2];
        barra.setOnMousePressed(e -> {
            dragOffset[0] = e.getSceneX();
            dragOffset[1] = e.getSceneY();
        });
        barra.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - dragOffset[0]);
            stage.setY(e.getScreenY() - dragOffset[1]);
        });

        return barra;
    }

    private HBox crearPanelBotones(FwCampoTexto campoClave) {
        HBox panel = new HBox();
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(15, 25, 20, 25));
        panel.setSpacing(15);

        FwBoton btnAceptar = crearBoton("ACEPTAR", "/imgs/teclas3.bmp");
        btnAceptar.setOnAction(e -> {
            resultado = campoClave.getTexto();
            if (timerCierre != null) timerCierre.stop();
            stage.close();
        });

        FwBoton btnCancelar = crearBoton("CANCELAR", "/imgs/teclas2.bmp");
        btnCancelar.setOnAction(e -> {
            resultado = null;
            if (timerCierre != null) timerCierre.stop();
            stage.close();
        });

        Region espacioIzq = new Region();
        Region espacioDer = new Region();
        HBox.setHgrow(espacioIzq, Priority.ALWAYS);
        HBox.setHgrow(espacioDer, Priority.ALWAYS);

        panel.getChildren().addAll(btnAceptar, espacioIzq, espacioDer, btnCancelar);
        return panel;
    }

    private FwBoton crearBoton(String texto, String rutaIcono) {
        FwBoton boton = new FwBoton(texto, rutaIcono);
        boton.setMinWidth(140);
        boton.setMinHeight(40);
        boton.setPrefWidth(140);
        boton.setPrefHeight(40);
        boton.setMaxWidth(140);
        boton.setMaxHeight(40);
        return boton;
    }
}

