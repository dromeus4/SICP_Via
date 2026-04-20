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
 * Ventana de diálogo para configurar los datos de conexión al servidor MySQL.
 * <p>
 * Campos: URL, PUERTO, USUARIO, CLAVE.
 * No contiene lógica de conexión: solo recoge los datos ingresados
 * y los devuelve al llamador.
 * <p>
 * Botones: GUARDAR y CANCELAR (mismo estilo que FwMensaje).
 * <p>
 * Retorna {@code null} si el usuario cancela, o un {@link Resultado}
 * con los datos ingresados si acepta.
 */
public final class FwDialogoConfigServidor {

    /**
     * Contiene los datos de conexión ingresados por el usuario.
     */
    public static final class Resultado {
        private final String url;
        private final String puerto;
        private final String usuario;
        private final String clave;

        public Resultado(String url, String puerto, String usuario, String clave) {
            this.url = url;
            this.puerto = puerto;
            this.usuario = usuario;
            this.clave = clave;
        }

        public String getUrl() { return url; }
        public String getPuerto() { return puerto; }
        public String getUsuario() { return usuario; }
        public String getClave() { return clave; }
    }

    private final Stage stage;
    private Resultado resultado = null;
    private final Timeline timerCierre;

    /**
     * Constructor.
     *
     * @param owner       Ventana padre (puede ser null)
     * @param datosActuales Datos actuales para precargar (puede ser null)
     */
    public FwDialogoConfigServidor(Stage owner, Resultado datosActuales) {
        this(owner, datosActuales, 0);
    }

    /**
     * Constructor con tiempo de vida.
     *
     * @param owner         Ventana padre (puede ser null)
     * @param datosActuales Datos actuales para precargar (puede ser null)
     * @param tiempoVida    Segundos antes de cerrar automáticamente (0 = 60s por defecto)
     */
    public FwDialogoConfigServidor(Stage owner, Resultado datosActuales, int tiempoVida) {
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
        HBox barraTitulo = crearBarraTitulo("CONFIGURAR CONEXIÓN AL SERVIDOR");
        root.setTop(barraTitulo);

        // ========== CONTENIDO CENTRAL ==========
        VBox contenidoCentral = new VBox(12);
        contenidoCentral.setAlignment(Pos.CENTER_LEFT);
        contenidoCentral.setPadding(new Insets(20, 25, 20, 25));

        // Encabezado descriptivo
        Label lblEncabezado = new Label("Datos de acceso al servidor de base de datos");
        lblEncabezado.setWrapText(true);
        lblEncabezado.setMaxWidth(450);
        lblEncabezado.setStyle(String.format(
            "-fx-font-family: '%s'; -fx-font-size: 14px; -fx-text-fill: %s;",
            FwFuentes.REGULAR,
            FwColorTema.toCss(FwColorTema.TEXTO_OSCURO)
        ));

        FwCampoTexto campoUrl = new FwCampoTexto("URL:", false, 400);
        FwCampoTexto campoPuerto = new FwCampoTexto("PUERTO:", false, 150);
        FwCampoTexto campoUsuario = new FwCampoTexto("USUARIO:", false, 300);
        FwCampoTexto campoClave = new FwCampoTexto("CLAVE:", true, 300);

        // Precargar datos actuales si existen
        if (datosActuales != null) {
            campoUrl.setTexto(datosActuales.getUrl());
            campoPuerto.setTexto(datosActuales.getPuerto());
            campoUsuario.setTexto(datosActuales.getUsuario());
            campoClave.setTexto(datosActuales.getClave());
        }

        contenidoCentral.getChildren().addAll(lblEncabezado, campoUrl, campoPuerto, campoUsuario, campoClave);
        root.setCenter(contenidoCentral);

        // ========== BOTONES ==========
        HBox panelBotones = crearPanelBotones(campoUrl, campoPuerto, campoUsuario, campoClave);
        root.setBottom(panelBotones);

        // ========== ESCENA ==========
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMinWidth(520);
        stage.setMinHeight(400);

        // Centrar en pantalla o respecto al owner
        stage.setOnShown(e -> {
            stage.sizeToScene();
            if (owner != null) {
                stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth()) / 2);
                stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);
            } else {
                stage.centerOnScreen();
            }
            campoUrl.getCampoTexto().requestFocus();
        });

        // ========== TIMER DE CIERRE AUTOMÁTICO ==========
        int tiempoEfectivo = tiempoVida > 0 ? tiempoVida : 60;
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
     * @return Un {@link Resultado} con los datos ingresados,
     *         o {@code null} si el usuario canceló
     */
    public Resultado mostrar() {
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

    private HBox crearPanelBotones(FwCampoTexto campoUrl, FwCampoTexto campoPuerto,
                                    FwCampoTexto campoUsuario, FwCampoTexto campoClave) {
        HBox panel = new HBox();
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(15, 25, 20, 25));
        panel.setSpacing(15);

        FwBoton btnGuardar = crearBoton("GUARDAR", "/imgs/teclas3.bmp");
        btnGuardar.setOnAction(e -> {
            resultado = new Resultado(
                campoUrl.getTexto(),
                campoPuerto.getTexto(),
                campoUsuario.getTexto(),
                campoClave.getTexto()
            );
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

        panel.getChildren().addAll(btnGuardar, espacioIzq, espacioDer, btnCancelar);
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

