package app;

import framework.*;
import framework.teclado.EstadoSistema;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Pie de la aplicación con 6 controles:
 * - RELOJ: biselado estilo PANEL, fuente Ericsson, dos puntos parpadeantes
 * - BOLSA: rótulo + contenido, proporción 1
 * - TURNO: rótulo + contenido, proporción 3
 * - CAJERO: rótulo + contenido, proporción 5
 * - CONFIGURAR: botón icono asterisco con tooltip
 * - SALIR: botón icono menos con tooltip
 */
public class AppPie extends FwPie {

    // Porcentajes y proporciones
    private static final double PCT_ALTO_CONTROLES = 0.90;  // 90% del alto del PIE
    private static final double MIN_ALTO_BOTON = 20;        // Mínimo 20px

    // Proporciones de BOLSA, TURNO, CAJERO (1:3:5 = total 9)
    private static final double PROP_BOLSA = 1.0 / 9.0;
    private static final double PROP_TURNO = 3.0 / 9.0;
    private static final double PROP_CAJERO = 5.0 / 9.0;

    // Controles
    private final StackPane relojContainer;
    private final Label relojLabel;
    private final FwCampoInfo campoBolsa;
    private final FwCampoInfo campoTurno;
    private final FwCampoInfo campoCajero;
    private final FwBoton btnConfig;
    private final FwBoton btnSalir;

    // Control de parpadeo de dos puntos
    private boolean mostrarDosPuntos = true;
    // Fuente ERICSSON para el reloj
    private final Font fuenteReloj;

    public AppPie(Runnable onConfigurar, Runnable onSalir) {
        // Cargar fuente ERICSSON para el reloj
        Font fuenteCargada = Font.loadFont(getClass().getResourceAsStream("/fonts/Ericsson.ttf"), 12);
        if (fuenteCargada == null) {
            fuenteCargada = Font.font("Monospaced", 12);
        }
        this.fuenteReloj = fuenteCargada;

        // ========== RELOJ ==========
        relojLabel = new Label();
        relojLabel.setTextFill(Color.BLACK);
        relojLabel.setMinWidth(Region.USE_PREF_SIZE);

        relojContainer = new StackPane(relojLabel);
        relojContainer.setAlignment(Pos.CENTER);
        relojContainer.setMinWidth(Region.USE_PREF_SIZE);
        aplicarEstiloBiselado(relojContainer);

        // Timeline para actualizar reloj cada segundo con parpadeo de dos puntos
        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                LocalDateTime ahora = LocalDateTime.now();
                String fecha = ahora.format(fmtFecha);
                String hora = ahora.format(fmtHora);

                // Alternar dos puntos
                if (mostrarDosPuntos) {
                    relojLabel.setText(fecha + " " + hora);
                } else {
                    relojLabel.setText(fecha + " " + hora.replace(":", " "));
                }
                mostrarDosPuntos = !mostrarDosPuntos;
            })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // Valor inicial
        LocalDateTime ahora = LocalDateTime.now();
        relojLabel.setText(ahora.format(fmtFecha) + " " + ahora.format(fmtHora));

        // ========== BOLSA ==========
        campoBolsa = new FwCampoInfo("BOLSA:", "---");
        campoBolsa.setColorTexto(Color.WHITE);

        // ========== TURNO ==========
        campoTurno = new FwCampoInfo("TURNO:", "CERRADO");
        campoTurno.setColorTexto(Color.WHITE);

        // ========== CAJERO ==========
        campoCajero = new FwCampoInfo("CAJERO:", "Sin identificar");
        campoCajero.setColorTexto(Color.WHITE);

        // ========== BOTÓN CONFIGURAR ==========
        btnConfig = crearBotonPie("/imgs/teclas1.bmp", "CONFIGURAR", onConfigurar);

        // ========== BOTÓN SALIR ==========
        btnSalir = crearBotonPie("/imgs/teclas2.bmp", "SALIR", onSalir);

        // Espaciador para empujar BOLSA, TURNO, CAJERO al centro
        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        // Agregar todos los componentes
        agregarComponentes(relojContainer, campoBolsa, campoTurno, campoCajero, espaciador, btnConfig, btnSalir);
    }

    /**
     * Crea un FwBoton solo con icono para el pie, con tooltip y atajo de teclado.
     */
    private FwBoton crearBotonPie(String rutaIcono, String tooltipText, Runnable onAction) {
        FwBoton boton = new FwBoton("", rutaIcono);

        Tooltip tooltip = new Tooltip(tooltipText);
        Tooltip.install(boton, tooltip);

        boton.setOnAction(evt -> {
            if (onAction != null) onAction.run();
        });

        return boton;
    }


    /**
     * Aplica estilo biselado tipo PANEL al contenedor (SIN PADDING).
     */
    private void aplicarEstiloBiselado(StackPane container) {
        container.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-border-color: %s %s %s %s; " +
            "-fx-border-width: 2; " +
            "-fx-padding: 0 4 0 4;",
            FwColorTema.toCss(FwColorTema.PANEL_FONDO),
            FwColorTema.toCss(FwColorTema.PANEL_BRDOSC),  // top
            FwColorTema.toCss(FwColorTema.PANEL_BRDCLR),  // right
            FwColorTema.toCss(FwColorTema.PANEL_BRDCLR),  // bottom
            FwColorTema.toCss(FwColorTema.PANEL_BRDOSC)   // left
        ));
    }


    @Override
    public void configurarResponsivo(ReadOnlyDoubleProperty anchoVentana, ReadOnlyDoubleProperty altoVentana) {
        super.configurarResponsivo(anchoVentana, altoVentana);

        // Escuchar cambios de alto del PIE
        heightProperty().addListener((obs, oldVal, newVal) -> {
            actualizarTamaños(newVal.doubleValue(), anchoVentana.get());
        });

        // Escuchar cambios de ancho
        anchoVentana.addListener((obs, oldVal, newVal) -> {
            actualizarTamaños(getHeight(), newVal.doubleValue());
        });

        // Configuración inicial
        actualizarTamaños(getHeight() > 0 ? getHeight() : 50, anchoVentana.get());
    }

    private void actualizarTamaños(double altoPie, double anchoVentana) {
        // Alto de controles: 90% del PIE con mínimo 20px
        double altoControles = Math.max(altoPie * PCT_ALTO_CONTROLES, MIN_ALTO_BOTON);

        // ========== RELOJ ==========
        // Descontar el borde (2px * 2 = 4px) para calcular el espacio interno real
        double altoInternoReloj = altoControles - 4; // 4px de borde total
        // Fuente ERICSSON: 90% del alto interno del control
        double fontSizeReloj = altoInternoReloj * 0.90;
        relojLabel.setFont(Font.font(fuenteReloj.getFamily(), fontSizeReloj));
        relojContainer.setMinHeight(altoControles);
        relojContainer.setMaxHeight(altoControles);
        relojContainer.setPrefHeight(altoControles);

        // El reloj NO tiene límite de ancho - muestra todo su contenido
        relojContainer.setMinWidth(Region.USE_PREF_SIZE);
        relojContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
        relojContainer.setMaxWidth(Region.USE_PREF_SIZE);

        // ========== BOTONES ========== (calcular primero para saber espacio restante)
        // Ancho de botones: 135% del alto
        double anchoBoton = altoControles * 1.35;
        btnConfig.setMinSize(anchoBoton, altoControles);
        btnConfig.setPrefSize(anchoBoton, altoControles);
        btnConfig.setMaxSize(anchoBoton, altoControles);

        btnSalir.setMinSize(anchoBoton, altoControles);
        btnSalir.setPrefSize(anchoBoton, altoControles);
        btnSalir.setMaxSize(anchoBoton, altoControles);


        // ========== CAMPOS INFO (BOLSA, TURNO, CAJERO) ==========
        // Obtener el ancho real del reloj (después de que se renderice)
        double anchoRelojReal = relojContainer.prefWidth(-1);
        if (anchoRelojReal < 100) {
            // Estimar el ancho basado en el texto y tamaño de fuente
            anchoRelojReal = fontSizeReloj * 10; // Aproximadamente 10 caracteres
        }

        // Espacio para los 3 campos = ventana - reloj - 2 botones - separaciones - padding izq/der
        double sep = FwLayoutResponsivo.SEPARACION_CAMPOS;
        // 6 separaciones entre 7 elementos (reloj, bolsa, turno, cajero, espaciador, config, salir)
        // + 2 paddings (izquierdo y derecho)
        double espacioOcupado = anchoRelojReal + (anchoBoton * 2) + (sep * 8); // 6 spacings + 2 paddings
        double anchoDisponible = Math.max(150, anchoVentana - espacioOcupado);

        // Distribuir según proporciones 1:3:5
        double anchoBolsa = anchoDisponible * PROP_BOLSA;
        double anchoTurno = anchoDisponible * PROP_TURNO;
        double anchoCajero = anchoDisponible * PROP_CAJERO;

        campoBolsa.configurarDimensiones(anchoBolsa, altoControles);
        campoTurno.configurarDimensiones(anchoTurno, altoControles);
        campoCajero.configurarDimensiones(anchoCajero, altoControles);
    }

    public void actualizarEstado(EstadoSistema estado) {
        campoTurno.setContenido(estado == EstadoSistema.ABIERTO ? "ABIERTO" : "CERRADO");
    }

    public void setBolsa(String valor) {
        campoBolsa.setContenido(valor);
    }

    public void setTurno(String valor) {
        campoTurno.setContenido(valor);
    }

    public void setCajero(String valor) {
        campoCajero.setContenido(valor);
    }
}
