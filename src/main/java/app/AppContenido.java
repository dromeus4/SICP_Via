package app;

import framework.*;
import framework.teclado.EstadoSistema;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AppContenido extends FwContenido {

    /** Ancho fijo de la franja OPCIONES en píxeles */
    private static final double OPCIONES_ANCHO = 200;

    /** Ancho fijo de la franja GALERIA en píxeles */
    private static final double GALERIA_ANCHO = 240;

    /** Ancho mínimo de la franja PRINC_CONT en píxeles */
    private static final double PRINC_CONT_MIN_ANCHO = 700;

    /** Ancho fijo de los botones de OPCIONES en píxeles */
    private static final double BOTON_ANCHO = 200;

    /** Alto fijo de los botones de OPCIONES en píxeles */
    private static final double BOTON_ALTO = 50;

    /** Ancho mínimo del CONTENIDO en píxeles */
    private static final double MIN_CONTENIDO_ANCHO = 1200;

    private final FwBoton btnAbrirCaja;
    private final FwBoton btnAbrirEmergencia;
    private final FwBoton btnBloquearCaja;
    private final FwBoton btnMantenimiento;
    private final FwBoton btnDesbloquearCaja;
    private final FwBoton btnConfigTeclado;
    private final FwBoton btnCerrarCaja;

    private final HBox layoutPrincipal;
    private final VBox franjaOpciones;
    private final StackPane franjaPrincCont;
    private final StackPane franjaGaleria;

    public AppContenido(
        Runnable onAbrirCaja,
        Runnable onAbrirEmergencia,
        Runnable onBloquearCaja,
        Runnable onMantenimiento,
        Runnable onDesbloquearCaja,
        Runnable onConfigTeclado,
        Runnable onCerrarCaja
    ) {
        double sep = FwLayoutResponsivo.SEPARACION_CONTENIDO;

        // --- Franja OPCIONES (izquierda) ---
        franjaOpciones = new VBox(sep);
        franjaOpciones.setAlignment(Pos.TOP_CENTER);
        franjaOpciones.setMinWidth(OPCIONES_ANCHO);
        franjaOpciones.setPrefWidth(OPCIONES_ANCHO);
        franjaOpciones.setMaxWidth(OPCIONES_ANCHO);

        // --- Franja PRINC_CONT (centro, vacía por ahora) ---
        franjaPrincCont = new StackPane();
        franjaPrincCont.setStyle("-fx-background-color: transparent;");
        franjaPrincCont.setMinWidth(PRINC_CONT_MIN_ANCHO);

        // --- Franja GALERIA (derecha, vacía por ahora) ---
        franjaGaleria = new StackPane();
        franjaGaleria.setStyle("-fx-background-color: transparent;");
        franjaGaleria.setMinWidth(GALERIA_ANCHO);
        franjaGaleria.setPrefWidth(GALERIA_ANCHO);
        franjaGaleria.setMaxWidth(GALERIA_ANCHO);

        // --- Layout principal: HBox con las 3 franjas ---
        layoutPrincipal = new HBox(sep);
        layoutPrincipal.setPadding(new Insets(sep));
        layoutPrincipal.setMinWidth(MIN_CONTENIDO_ANCHO);
        layoutPrincipal.getChildren().addAll(franjaOpciones, franjaPrincCont, franjaGaleria);

        // PRINC_CONT ocupa todo el espacio sobrante
        HBox.setHgrow(franjaPrincCont, Priority.ALWAYS);

        // Crear botones con tamaño fijo e iconos
        btnAbrirCaja = crearBotonOpcion("ABRIR CAJA", "/imgs/tecla1.bmp");
        btnAbrirEmergencia = crearBotonOpcion("ABRIR CAJA DE EMERGENCIA", "/imgs/tecla2.bmp");
        btnBloquearCaja = crearBotonOpcion("BLOQUEAR CAJA", "/imgs/tecla7.bmp");
        btnMantenimiento = crearBotonOpcion("MANTENIMIENTO", "/imgs/tecla8.bmp");
        btnDesbloquearCaja = crearBotonOpcion("DESBLOQUEAR CAJA", "/imgs/tecla1.bmp");
        btnConfigTeclado = crearBotonOpcion("CONFIGURAR TECLADO", "/imgs/tecla4.bmp");
        btnCerrarCaja = crearBotonOpcion("CERRAR CAJA", "/imgs/tecla9.bmp");

        btnAbrirCaja.setOnAction(e -> { if (onAbrirCaja != null) onAbrirCaja.run(); });
        btnAbrirEmergencia.setOnAction(e -> { if (onAbrirEmergencia != null) onAbrirEmergencia.run(); });
        btnBloquearCaja.setOnAction(e -> { if (onBloquearCaja != null) onBloquearCaja.run(); });
        btnMantenimiento.setOnAction(e -> { if (onMantenimiento != null) onMantenimiento.run(); });
        btnDesbloquearCaja.setOnAction(e -> { if (onDesbloquearCaja != null) onDesbloquearCaja.run(); });
        btnConfigTeclado.setOnAction(e -> { if (onConfigTeclado != null) onConfigTeclado.run(); });
        btnCerrarCaja.setOnAction(e -> { if (onCerrarCaja != null) onCerrarCaja.run(); });

        agregarComponentes(layoutPrincipal);

        // Ancho mínimo del contenido
        setMinWidth(MIN_CONTENIDO_ANCHO);

        actualizarEstado(EstadoSistema.CERRADO);
    }

    /**
     * Crea un botón de OPCIONES con tamaño fijo (200x50) e icono.
     */
    private FwBoton crearBotonOpcion(String texto, String rutaIcono) {
        FwBoton btn = new FwBoton(texto, rutaIcono);
        btn.setPrefWidth(BOTON_ANCHO);
        btn.setMinWidth(BOTON_ANCHO);
        btn.setMaxWidth(BOTON_ANCHO);
        btn.setPrefHeight(BOTON_ALTO);
        btn.setMinHeight(BOTON_ALTO);
        btn.setMaxHeight(BOTON_ALTO);
        return btn;
    }

    @Override
    public void configurarResponsivo(ReadOnlyDoubleProperty anchoVentana, ReadOnlyDoubleProperty altoVentana) {
        super.configurarResponsivo(anchoVentana, altoVentana);
    }

    public void actualizarEstado(EstadoSistema estado) {
        franjaOpciones.getChildren().clear();

        switch (estado) {
            case CERRADO:
                franjaOpciones.getChildren().addAll(btnAbrirCaja, btnAbrirEmergencia, btnMantenimiento);
                break;
            case ABIERTO:
                franjaOpciones.getChildren().addAll(btnBloquearCaja, btnCerrarCaja);
                break;
            case BLOQUEADO:
                franjaOpciones.getChildren().addAll(btnDesbloquearCaja, btnCerrarCaja);
                break;
            case MANTENIMIENTO:
                franjaOpciones.getChildren().addAll(btnConfigTeclado, btnCerrarCaja);
                break;
            default:
                break;
        }
    }
}
