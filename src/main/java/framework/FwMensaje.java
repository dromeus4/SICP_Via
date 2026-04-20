package framework;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Ventana de mensaje personalizada que mantiene el estilo visual del sistema.
 * <p>
 * Soporta diferentes clases de diálogo (SI/NO, ACEPTAR/CANCELAR, SI/NO/CANCELAR),
 * iconos opcionales, y tiempo de vida automático.
 */
public final class FwMensaje {

    // ========== CLASES DE DIALOGO ==========

    /** Muestra botones SI y NO. Por defecto: NO */
    public static final int CLASE_SINO = 1;

    /** Muestra botones ACEPTAR y CANCELAR. Por defecto: CANCELAR */
    public static final int CLASE_ACEPTAR = 2;

    /** Muestra botones SI, NO y CANCELAR. Por defecto: CANCELAR */
    public static final int CLASE_CANCELAR = 3;

    /** Muestra solo el botón ACEPTAR (para mensajes informativos) */
    public static final int CLASE_SOLO_ACEPTAR = 4;

    // ========== ICONOS ==========

    /** Icono de pregunta (?) */
    public static final int ICONO_PREGUNTA = 1;

    /** Icono de información (i) */
    public static final int ICONO_INFO = 2;

    /** Icono de advertencia (!) */
    public static final int ICONO_ADVERTENCIA = 3;

    /** Icono de pare/error (X) */
    public static final int ICONO_PARE = 4;

    /** Sin icono */
    public static final int ICONO_NINGUNO = 0;

    // ========== RESULTADOS ==========

    /** El usuario presionó SI */
    public static final int RESULTADO_SI = 1;

    /** El usuario presionó NO */
    public static final int RESULTADO_NO = 2;

    /** El usuario presionó CANCELAR o cerró la ventana */
    public static final int RESULTADO_CANCELAR = 3;

    /** El usuario presionó ACEPTAR */
    public static final int RESULTADO_ACEPTAR = 4;

    // ========== CAMPOS ==========

    private final Stage stage;
    private int resultado;
    private Timeline timerCierre;

    /**
     * Constructor completo.
     *
     * @param owner       Ventana padre (puede ser null)
     * @param titulo      Título de la ventana
     * @param mensaje     Texto del mensaje
     * @param clase       Tipo de botones (CLASE_SINO, CLASE_ACEPTAR, CLASE_CANCELAR)
     * @param icono       Icono a mostrar (ICONO_PREGUNTA, ICONO_INFO, etc.) o ICONO_NINGUNO
     * @param tiempoVida  Segundos antes de cerrar automáticamente (0 = sin límite)
     */
    public FwMensaje(Stage owner, String titulo, String mensaje, int clase, int icono, int tiempoVida) {
        this.stage = new Stage();
        this.resultado = obtenerResultadoPorDefecto(clase);

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

        // ========== CONTENIDO CENTRAL (icono + mensaje) ==========
        HBox contenidoCentral = new HBox(15);
        contenidoCentral.setAlignment(Pos.CENTER_LEFT);
        contenidoCentral.setPadding(new Insets(20, 25, 20, 25));

        // Icono (si corresponde)
        if (icono != ICONO_NINGUNO) {
            Node iconoNode = crearIcono(icono);
            if (iconoNode != null) {
                contenidoCentral.getChildren().add(iconoNode);
            }
        }

        // Texto del mensaje
        Label lblMensaje = new Label(mensaje);
        lblMensaje.setWrapText(true);
        lblMensaje.setMaxWidth(400);
        lblMensaje.setStyle(String.format(
            "-fx-font-family: '%s'; -fx-font-size: 14px; -fx-text-fill: %s;",
            FwFuentes.REGULAR,
            FwColorTema.toCss(FwColorTema.TEXTO_OSCURO)
        ));
        HBox.setHgrow(lblMensaje, Priority.ALWAYS);
        contenidoCentral.getChildren().add(lblMensaje);

        root.setCenter(contenidoCentral);

        // ========== BOTONES ==========
        HBox panelBotones = crearPanelBotones(clase);
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
        });

        // ========== TIMER DE CIERRE AUTOMÁTICO ==========
        // Si no se especifica tiempo (0), usar 30 segundos por defecto
        int tiempoEfectivo = tiempoVida > 0 ? tiempoVida : 30;
        timerCierre = new Timeline(new KeyFrame(Duration.seconds(tiempoEfectivo), e -> {
            stage.close();
        }));
        timerCierre.setCycleCount(1);

        // Bloquear cierre con X (no hay X visible, pero por si acaso)
        stage.setOnCloseRequest(e -> {
            resultado = obtenerResultadoPorDefecto(clase);
        });
    }

    /**
     * Constructor sin tiempo de vida.
     */
    public FwMensaje(Stage owner, String titulo, String mensaje, int clase, int icono) {
        this(owner, titulo, mensaje, clase, icono, 0);
    }

    /**
     * Constructor sin icono ni tiempo de vida.
     */
    public FwMensaje(Stage owner, String titulo, String mensaje, int clase) {
        this(owner, titulo, mensaje, clase, ICONO_NINGUNO, 0);
    }

    /**
     * Muestra el diálogo y espera la respuesta del usuario.
     *
     * @return El resultado (RESULTADO_SI, RESULTADO_NO, RESULTADO_CANCELAR, RESULTADO_ACEPTAR)
     */
    public int mostrar() {
        if (timerCierre != null) {
            timerCierre.play();
        }
        stage.showAndWait();
        if (timerCierre != null) {
            timerCierre.stop();
        }
        return resultado;
    }

    // ========== MÉTODOS ESTÁTICOS DE CONVENIENCIA ==========

    /**
     * Muestra un diálogo SI/NO.
     */
    public static int preguntarSiNo(Stage owner, String titulo, String mensaje) {
        return new FwMensaje(owner, titulo, mensaje, CLASE_SINO, ICONO_PREGUNTA).mostrar();
    }

    /**
     * Muestra un diálogo ACEPTAR/CANCELAR.
     */
    public static int confirmar(Stage owner, String titulo, String mensaje) {
        return new FwMensaje(owner, titulo, mensaje, CLASE_ACEPTAR, ICONO_PREGUNTA).mostrar();
    }

    /**
     * Muestra un diálogo de información con solo ACEPTAR.
     */
    public static void informar(Stage owner, String titulo, String mensaje) {
        FwMensaje msg = new FwMensaje(owner, titulo, mensaje, CLASE_SOLO_ACEPTAR, ICONO_INFO);
        msg.mostrar();
    }

    /**
     * Muestra un diálogo de advertencia.
     */
    public static int advertir(Stage owner, String titulo, String mensaje) {
        return new FwMensaje(owner, titulo, mensaje, CLASE_ACEPTAR, ICONO_ADVERTENCIA).mostrar();
    }

    /**
     * Muestra un diálogo de error con solo ACEPTAR.
     */
    public static void error(Stage owner, String titulo, String mensaje) {
        FwMensaje msg = new FwMensaje(owner, titulo, mensaje, CLASE_SOLO_ACEPTAR, ICONO_PARE);
        msg.mostrar();
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

    private HBox crearPanelBotones(int clase) {
        HBox panel = new HBox();
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(15, 25, 20, 25));
        panel.setSpacing(15);

        switch (clase) {
            case CLASE_SINO:
                // SI → teclas3, NO (sin CANCELAR) → teclas2
                FwBoton btnSi = crearBoton("SI", RESULTADO_SI, "/imgs/teclas3.bmp");
                FwBoton btnNo = crearBoton("NO", RESULTADO_NO, "/imgs/teclas2.bmp");

                Region espacioIzq1 = new Region();
                Region espacioDer1 = new Region();
                HBox.setHgrow(espacioIzq1, Priority.ALWAYS);
                HBox.setHgrow(espacioDer1, Priority.ALWAYS);

                panel.getChildren().addAll(btnSi, espacioIzq1, espacioDer1, btnNo);
                break;

            case CLASE_ACEPTAR:
                // ACEPTAR → teclas3, CANCELAR → teclas2
                FwBoton btnAceptar = crearBoton("ACEPTAR", RESULTADO_ACEPTAR, "/imgs/teclas3.bmp");
                FwBoton btnCancelar = crearBoton("CANCELAR", RESULTADO_CANCELAR, "/imgs/teclas2.bmp");

                Region espacioIzq2 = new Region();
                Region espacioDer2 = new Region();
                HBox.setHgrow(espacioIzq2, Priority.ALWAYS);
                HBox.setHgrow(espacioDer2, Priority.ALWAYS);

                panel.getChildren().addAll(btnAceptar, espacioIzq2, espacioDer2, btnCancelar);
                break;

            case CLASE_CANCELAR:
                // SI → teclas3, NO (con CANCELAR) → teclas1, CANCELAR → teclas2
                FwBoton btnSi3 = crearBoton("SI", RESULTADO_SI, "/imgs/teclas3.bmp");
                FwBoton btnNo3 = crearBoton("NO", RESULTADO_NO, "/imgs/teclas1.bmp");
                FwBoton btnCancelar3 = crearBoton("CANCELAR", RESULTADO_CANCELAR, "/imgs/teclas2.bmp");

                Region espacio1 = new Region();
                Region espacio2 = new Region();
                HBox.setHgrow(espacio1, Priority.ALWAYS);
                HBox.setHgrow(espacio2, Priority.ALWAYS);

                panel.getChildren().addAll(btnSi3, espacio1, btnNo3, espacio2, btnCancelar3);
                break;

            case CLASE_SOLO_ACEPTAR:
                // ACEPTAR → teclas3
                FwBoton btnAceptarSolo = crearBoton("ACEPTAR", RESULTADO_ACEPTAR, "/imgs/teclas3.bmp");
                panel.getChildren().add(btnAceptarSolo);
                break;

            default:
                break;
        }

        return panel;
    }

    private FwBoton crearBoton(String texto, int resultadoBoton, String rutaIcono) {
        FwBoton boton = new FwBoton(texto, rutaIcono);
        boton.setMinWidth(140);
        boton.setMinHeight(40);
        boton.setPrefWidth(140);
        boton.setPrefHeight(40);
        boton.setMaxWidth(140);
        boton.setMaxHeight(40);
        boton.setOnAction(e -> {
            resultado = resultadoBoton;
            if (timerCierre != null) {
                timerCierre.stop();
            }
            stage.close();
        });
        return boton;
    }

    private Node crearIcono(int tipoIcono) {
        StackPane contenedor = new StackPane();
        contenedor.setMinSize(52, 52);
        contenedor.setPrefSize(52, 52);
        contenedor.setMaxSize(52, 52);

        switch (tipoIcono) {
            case ICONO_PREGUNTA:
                return crearIconoPregunta();
            case ICONO_INFO:
                return crearIconoInfo();
            case ICONO_ADVERTENCIA:
                return crearIconoAdvertencia();
            case ICONO_PARE:
                return crearIconoPare();
            default:
                return null;
        }
    }

    private Node crearIconoPregunta() {
        StackPane stack = new StackPane();

        Circle circulo = new Circle(24);
        circulo.setFill(Color.rgb(33, 150, 243)); // Azul
        circulo.setStroke(Color.rgb(25, 118, 210));
        circulo.setStrokeWidth(2);

        Label texto = new Label("?");
        texto.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");

        stack.getChildren().addAll(circulo, texto);
        return stack;
    }

    private Node crearIconoInfo() {
        StackPane stack = new StackPane();

        Circle circulo = new Circle(24);
        circulo.setFill(Color.rgb(33, 150, 243)); // Azul
        circulo.setStroke(Color.rgb(25, 118, 210));
        circulo.setStrokeWidth(2);

        Label texto = new Label("i");
        texto.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-font-style: italic; -fx-text-fill: white;");

        stack.getChildren().addAll(circulo, texto);
        return stack;
    }

    private Node crearIconoAdvertencia() {
        StackPane stack = new StackPane();

        // Triángulo amarillo
        Polygon triangulo = new Polygon();
        triangulo.getPoints().addAll(
            24.0, 0.0,    // Punta superior
            48.0, 44.0,   // Esquina inferior derecha
            0.0, 44.0     // Esquina inferior izquierda
        );
        triangulo.setFill(Color.rgb(255, 193, 7)); // Amarillo
        triangulo.setStroke(Color.rgb(255, 160, 0));
        triangulo.setStrokeWidth(2);

        Label texto = new Label("!");
        texto.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: black;");
        texto.setTranslateY(6);

        stack.getChildren().addAll(triangulo, texto);
        return stack;
    }

    private Node crearIconoPare() {
        StackPane stack = new StackPane();

        // Octágono rojo (simulado con círculo)
        Circle circulo = new Circle(24);
        circulo.setFill(Color.rgb(244, 67, 54)); // Rojo
        circulo.setStroke(Color.rgb(198, 40, 40));
        circulo.setStrokeWidth(2);

        Label texto = new Label("✕");
        texto.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        stack.getChildren().addAll(circulo, texto);
        return stack;
    }

    private int obtenerResultadoPorDefecto(int clase) {
        switch (clase) {
            case CLASE_SINO:
                return RESULTADO_NO;
            case CLASE_ACEPTAR:
                return RESULTADO_CANCELAR;
            case CLASE_CANCELAR:
                return RESULTADO_CANCELAR;
            case CLASE_SOLO_ACEPTAR:
                return RESULTADO_ACEPTAR;
            default:
                return RESULTADO_CANCELAR;
        }
    }
}

