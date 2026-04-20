package app;

import framework.FwBoton;
import framework.FwColorTema;
import framework.FwFuentes;
import framework.teclado.ConfigTecladoIndustrial;
import framework.teclado.TeclaEspecial;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import framework.teclado.DescritorTecla;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Ventana para mapear teclas especiales del teclado industrial por captura.
 * Usa el estilo visual del sistema (UNDECORATED, colores del tema, FwBoton).
 */
public final class VentanaConfigTecladoIndustrial {

    private final Stage stage;
    private final Label estadoCaptura;
    private final EnumMap<TeclaEspecial, DescritorTecla> asignaciones;
    private final EnumMap<TeclaEspecial, Label> etiquetasValor;

    private TeclaEspecial teclaPendiente;

    private VentanaConfigTecladoIndustrial(Stage owner,
                                           ConfigTecladoIndustrial configActual,
                                           Consumer<ConfigTecladoIndustrial> onGuardar) {
        this.stage = new Stage();
        this.asignaciones = new EnumMap<>(TeclaEspecial.class);
        this.etiquetasValor = new EnumMap<>(TeclaEspecial.class);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);

        for (TeclaEspecial teclaEspecial : TeclaEspecial.values()) {
            asignaciones.put(teclaEspecial, configActual.getDescritorTecla(teclaEspecial));
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
        HBox barraTitulo = crearBarraTitulo("CONFIGURAR TECLADO INDUSTRIAL");
        root.setTop(barraTitulo);

        // ========== CONTENIDO CENTRAL ==========
        VBox contenidoCentral = new VBox(10);
        contenidoCentral.setPadding(new Insets(15, 20, 10, 20));

        // Estado de captura
        estadoCaptura = new Label("Selecciona una tecla especial y presiona 'Capturar'.");
        estadoCaptura.setWrapText(true);
        estadoCaptura.setMaxWidth(560);
        estadoCaptura.setStyle(String.format(
            "-fx-font-family: '%s'; -fx-font-size: 12px; -fx-text-fill: %s;",
            FwFuentes.REGULAR,
            FwColorTema.toCss(FwColorTema.TEXTO_OSCURO)
        ));

        // Tabla de teclas
        GridPane tabla = construirTabla();

        ScrollPane scroll = new ScrollPane(tabla);
        scroll.setFitToWidth(true);
        scroll.setStyle(String.format(
            "-fx-background: %s; -fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 1;",
            FwColorTema.toCss(FwColorTema.FONDO_CLARO),
            FwColorTema.toCss(FwColorTema.FONDO_CLARO),
            FwColorTema.toCss(FwColorTema.BORDE_GRIS)
        ));
        VBox.setVgrow(scroll, Priority.ALWAYS);

        contenidoCentral.getChildren().addAll(estadoCaptura, scroll);
        root.setCenter(contenidoCentral);

        // ========== BOTONES ==========
        HBox panelBotones = crearPanelBotones(onGuardar);
        root.setBottom(panelBotones);

        // ========== ESCENA ==========
        Scene scene = new Scene(root, 640, 700);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::capturarTeclaSiCorresponde);
        stage.setScene(scene);
        stage.setMinWidth(640);
        stage.setMinHeight(500);

        // Centrar respecto al owner
        stage.setOnShown(e -> {
            if (owner != null) {
                stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth()) / 2);
                stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);
            } else {
                stage.centerOnScreen();
            }
        });

        // Bloquear cierre externo
        stage.setOnCloseRequest(e -> e.consume());
    }

    public static void mostrar(Stage owner,
                               ConfigTecladoIndustrial configActual,
                               Consumer<ConfigTecladoIndustrial> onGuardar) {
        Objects.requireNonNull(owner, "owner no puede ser null");
        Objects.requireNonNull(configActual, "configActual no puede ser null");
        Objects.requireNonNull(onGuardar, "onGuardar no puede ser null");

        VentanaConfigTecladoIndustrial ventana =
            new VentanaConfigTecladoIndustrial(owner, configActual, onGuardar);
        ventana.stage.showAndWait();
    }

    // ========== BARRA DE TÍTULO ==========

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

    // ========== TABLA DE TECLAS ==========

    private GridPane construirTabla() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(6);
        grid.setPadding(new Insets(8, 10, 8, 10));

        String estiloHeader = String.format(
            "-fx-font-family: '%s'; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: %s;",
            FwFuentes.BOLD,
            FwColorTema.toCss(FwColorTema.TEXTO_OSCURO)
        );

        Label h1 = new Label("Tecla especial");
        h1.setStyle(estiloHeader);
        Label h2 = new Label("Combinación asignada");
        h2.setStyle(estiloHeader);
        Label h3 = new Label("Acción");
        h3.setStyle(estiloHeader);

        grid.add(h1, 0, 0);
        grid.add(h2, 1, 0);
        grid.add(h3, 2, 0);

        String estiloTexto = String.format(
            "-fx-font-family: '%s'; -fx-font-size: 12px; -fx-text-fill: %s;",
            FwFuentes.REGULAR,
            FwColorTema.toCss(FwColorTema.TEXTO_OSCURO)
        );

        int fila = 1;
        for (TeclaEspecial teclaEspecial : TeclaEspecial.values()) {
            Label lblTecla = new Label(teclaEspecial.name());
            lblTecla.setStyle(estiloTexto);

            Label lblValor = new Label(formatear(asignaciones.get(teclaEspecial)));
            lblValor.setMinWidth(180);
            lblValor.setStyle(estiloTexto);
            lblValor.setTooltip(new Tooltip("Combinación de teclas actualmente asociada"));

            FwBoton btnCapturar = new FwBoton("Capturar");
            btnCapturar.setMinWidth(90);
            btnCapturar.setMinHeight(28);
            btnCapturar.setPrefWidth(90);
            btnCapturar.setPrefHeight(28);
            btnCapturar.setMaxWidth(90);
            btnCapturar.setMaxHeight(28);
            final TeclaEspecial te = teclaEspecial;
            btnCapturar.setOnAction(e -> iniciarCaptura(te));

            etiquetasValor.put(teclaEspecial, lblValor);

            grid.add(lblTecla, 0, fila);
            grid.add(lblValor, 1, fila);
            grid.add(btnCapturar, 2, fila);
            fila++;
        }

        return grid;
    }

    // ========== PANEL DE BOTONES ==========

    private HBox crearPanelBotones(Consumer<ConfigTecladoIndustrial> onGuardar) {
        HBox panel = new HBox();
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(15, 25, 20, 25));
        panel.setSpacing(15);

        FwBoton btnGuardar = crearBotonAccion("GUARDAR", "/imgs/teclas3.bmp");
        btnGuardar.setOnAction(e -> guardarYSalir(onGuardar));

        FwBoton btnCancelar = crearBotonAccion("CANCELAR", "/imgs/teclas2.bmp");
        btnCancelar.setOnAction(e -> stage.close());

        Region espacioIzq = new Region();
        Region espacioDer = new Region();
        HBox.setHgrow(espacioIzq, Priority.ALWAYS);
        HBox.setHgrow(espacioDer, Priority.ALWAYS);

        panel.getChildren().addAll(btnGuardar, espacioIzq, espacioDer, btnCancelar);
        return panel;
    }

    private FwBoton crearBotonAccion(String texto, String rutaIcono) {
        FwBoton boton = new FwBoton(texto, rutaIcono);
        boton.setMinWidth(140);
        boton.setMinHeight(40);
        boton.setPrefWidth(140);
        boton.setPrefHeight(40);
        boton.setMaxWidth(140);
        boton.setMaxHeight(40);
        return boton;
    }

    // ========== LÓGICA DE CAPTURA ==========

    private void iniciarCaptura(TeclaEspecial teclaEspecial) {
        teclaPendiente = teclaEspecial;
        estadoCaptura.setText("Presiona una tecla para asignar a: " + teclaEspecial.name() + " (ESC cancela)");
        stage.getScene().getRoot().requestFocus();
    }

    private void capturarTeclaSiCorresponde(KeyEvent event) {
        if (teclaPendiente == null) {
            return;
        }

        event.consume();

        if (event.getCode() == KeyCode.ESCAPE) {
            estadoCaptura.setText("Captura cancelada.");
            teclaPendiente = null;
            return;
        }

        DescritorTecla nuevoDescritor = new DescritorTecla(event.getCode(), event.isControlDown(), event.isShiftDown(), event.isAltDown());
        TeclaEspecial conflicto = buscarTeclaConDescritor(nuevoDescritor);
        if (conflicto != null && conflicto != teclaPendiente) {
            asignaciones.put(conflicto, null);
            actualizarEtiqueta(conflicto);
        }

        asignaciones.put(teclaPendiente, nuevoDescritor);
        actualizarEtiqueta(teclaPendiente);

        estadoCaptura.setText("Asignada " + nuevoDescritor + " a " + teclaPendiente.name());
        teclaPendiente = null;
    }

    private void actualizarEtiqueta(TeclaEspecial teclaEspecial) {
        Label label = etiquetasValor.get(teclaEspecial);
        if (label != null) {
            label.setText(formatear(asignaciones.get(teclaEspecial)));
        }
    }

    private TeclaEspecial buscarTeclaConDescritor(DescritorTecla descritor) {
        for (Map.Entry<TeclaEspecial, DescritorTecla> entry : asignaciones.entrySet()) {
            if (entry.getValue() != null && entry.getValue().equals(descritor)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void guardarYSalir(Consumer<ConfigTecladoIndustrial> onGuardar) {
        TeclaEspecial faltante = buscarFaltante();
        if (faltante != null) {
            estadoCaptura.setText("Falta asignar la tecla especial: " + faltante.name());
            return;
        }

        ConfigTecladoIndustrial.Builder builder = ConfigTecladoIndustrial.builder();
        for (TeclaEspecial teclaEspecial : TeclaEspecial.values()) {
            builder.mapear(teclaEspecial, asignaciones.get(teclaEspecial));
        }

        onGuardar.accept(builder.build());
        stage.close();
    }

    private TeclaEspecial buscarFaltante() {
        for (TeclaEspecial teclaEspecial : TeclaEspecial.values()) {
            if (asignaciones.get(teclaEspecial) == null) {
                return teclaEspecial;
            }
        }
        return null;
    }

    private String formatear(DescritorTecla descritor) {
        return descritor == null ? "(sin asignar)" : descritor.toString();
    }
}

