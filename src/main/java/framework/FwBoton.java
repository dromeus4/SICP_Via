package framework;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Map;

/**
 * Botón estilizado con bordes biselados y soporte para icono.
 *
 * Modos:
 * - Solo texto: centrado
 * - Solo icono: centrado, 75% del alto del control, preservando aspecto
 * - Icono + texto: icono izquierda (75% alto), texto 5px a la derecha.
 *   Si el texto no cabe en una línea se divide en dos líneas centradas
 *   verticalmente con respecto al icono.
 *
 * Los iconos tecla0..tecla9.bmp y teclas1..teclas4.bmp representan teclas
 * del teclado y activan el botón al ser presionadas.
 */
public class FwBoton extends StackPane {

    private static final double DESPLAZAMIENTO_PRESS = 3.0;
    private static final double PCT_ICONO_ALTO = 0.75;
    private static final double SEPARACION_ICONO_TEXTO = 5.0;
    private static final double FONT_SIZE = 14.0;

    // ========================================================
    // Mapeo de iconos de tecla → KeyCode (teclado normal y numérico)
    // ========================================================

    /** Mapa: sufijo del nombre de archivo → par de KeyCodes (normal, numpad) */
    private static final Map<String, KeyCode[]> MAPA_TECLAS = Map.ofEntries(
        Map.entry("tecla0.bmp", new KeyCode[]{KeyCode.DIGIT0, KeyCode.NUMPAD0}),
        Map.entry("tecla1.bmp", new KeyCode[]{KeyCode.DIGIT1, KeyCode.NUMPAD1}),
        Map.entry("tecla2.bmp", new KeyCode[]{KeyCode.DIGIT2, KeyCode.NUMPAD2}),
        Map.entry("tecla3.bmp", new KeyCode[]{KeyCode.DIGIT3, KeyCode.NUMPAD3}),
        Map.entry("tecla4.bmp", new KeyCode[]{KeyCode.DIGIT4, KeyCode.NUMPAD4}),
        Map.entry("tecla5.bmp", new KeyCode[]{KeyCode.DIGIT5, KeyCode.NUMPAD5}),
        Map.entry("tecla6.bmp", new KeyCode[]{KeyCode.DIGIT6, KeyCode.NUMPAD6}),
        Map.entry("tecla7.bmp", new KeyCode[]{KeyCode.DIGIT7, KeyCode.NUMPAD7}),
        Map.entry("tecla8.bmp", new KeyCode[]{KeyCode.DIGIT8, KeyCode.NUMPAD8}),
        Map.entry("tecla9.bmp", new KeyCode[]{KeyCode.DIGIT9, KeyCode.NUMPAD9}),
        Map.entry("teclas1.bmp", new KeyCode[]{KeyCode.MULTIPLY, KeyCode.MULTIPLY}),
        Map.entry("teclas2.bmp", new KeyCode[]{KeyCode.MINUS, KeyCode.SUBTRACT}),
        Map.entry("teclas3.bmp", new KeyCode[]{KeyCode.ADD, KeyCode.ADD}),
        Map.entry("teclas4.bmp", new KeyCode[]{KeyCode.SLASH, KeyCode.DIVIDE})
    );

    private final HBox contenido;
    private final ImageView iconoView;

    private boolean pressed = false;
    private String texto = "";
    private Image icono = null;
    private javafx.event.EventHandler<javafx.event.ActionEvent> actionHandler;

    /** KeyCodes que activan este botón (null si no tiene atajo) */
    private KeyCode[] teclasCodigo = null;
    /** Handler registrado en la Scene para atajos de teclado */
    private javafx.event.EventHandler<KeyEvent> keyHandler = null;
    /** Referencia a la scene donde está registrado el handler */
    private Scene sceneRegistrada = null;

    public FwBoton() {
        this("", (Image) null);
    }

    public FwBoton(String texto) {
        this(texto, (Image) null);
    }

    /**
     * Constructor con texto e icono ya cargado (sin atajo de teclado automático).
     */
    public FwBoton(String texto, Image icono) {
        this.texto = texto != null ? texto : "";
        this.icono = icono;

        contenido = new HBox();
        contenido.setAlignment(Pos.CENTER);
        contenido.setSpacing(SEPARACION_ICONO_TEXTO);

        iconoView = new ImageView();
        iconoView.setPreserveRatio(true);

        getChildren().add(contenido);

        inicializarEstilos();
        actualizarContenido();
        registrarEventos();
        registrarListenerScene();

        heightProperty().addListener((obs, oldVal, newVal) -> actualizarContenido());
        widthProperty().addListener((obs, oldVal, newVal) -> actualizarContenido());
    }

    /**
     * Constructor con texto y ruta de recurso del icono.
     * Carga la imagen y detecta automáticamente el atajo de teclado.
     */
    public FwBoton(String texto, String rutaIcono) {
        this(texto, cargarImagen(rutaIcono));
        asignarTeclaPorRuta(rutaIcono);
    }

    // ========================================================
    // Atajo de teclado
    // ========================================================

    /** Carga una imagen desde un recurso, retorna null si falla. */
    private static Image cargarImagen(String ruta) {
        if (ruta == null) return null;
        try {
            var is = FwBoton.class.getResourceAsStream(ruta);
            if (is != null) return new Image(is);
        } catch (Exception e) {
            // ignorar
        }
        return null;
    }

    /** Extrae el nombre del archivo de una ruta y busca el atajo de teclado. */
    private void asignarTeclaPorRuta(String ruta) {
        if (ruta == null) return;
        // Extraer nombre del archivo: "/imgs/tecla1.bmp" → "tecla1.bmp"
        int idx = ruta.lastIndexOf('/');
        String nombre = (idx >= 0) ? ruta.substring(idx + 1) : ruta;
        teclasCodigo = MAPA_TECLAS.get(nombre.toLowerCase());
    }

    /**
     * Asigna manualmente los KeyCodes que activan este botón.
     */
    public void setTeclaAtajo(KeyCode... codigos) {
        this.teclasCodigo = (codigos != null && codigos.length > 0) ? codigos : null;
        // Re-registrar si ya tiene scene
        desregistrarKeyHandler();
        if (getScene() != null) {
            registrarKeyHandler(getScene());
        }
    }

    /** Escucha cuando el botón se agrega/quita de una Scene para registrar el key handler. */
    private void registrarListenerScene() {
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            desregistrarKeyHandler();
            if (newScene != null && teclasCodigo != null) {
                registrarKeyHandler(newScene);
            }
        });
    }

    private void registrarKeyHandler(Scene scene) {
        if (teclasCodigo == null || keyHandler != null) return;

        keyHandler = event -> {
            // Solo activar si el botón está visible y en el scene graph
            if (!isVisible() || !isManaged() || getScene() == null) return;
            if (getParent() == null || !getParent().isVisible()) return;

            KeyCode code = event.getCode();
            for (KeyCode tc : teclasCodigo) {
                if (tc == code) {
                    // Simular press visual
                    simularPress();
                    event.consume();
                    return;
                }
            }
        };

        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyHandler);
        sceneRegistrada = scene;
    }

    private void desregistrarKeyHandler() {
        if (keyHandler != null && sceneRegistrada != null) {
            sceneRegistrada.removeEventFilter(KeyEvent.KEY_PRESSED, keyHandler);
        }
        keyHandler = null;
        sceneRegistrada = null;
    }

    /** Simula visualmente la pulsación del botón y dispara el action. */
    private void simularPress() {
        aplicarEstilo(true);
        contenido.setTranslateX(DESPLAZAMIENTO_PRESS);
        contenido.setTranslateY(DESPLAZAMIENTO_PRESS);

        // Restaurar después de 100ms y ejecutar la acción
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(100));
        pause.setOnFinished(ev -> {
            aplicarEstilo(false);
            contenido.setTranslateX(0);
            contenido.setTranslateY(0);
            // Diferir la acción fuera del contexto de animación para permitir showAndWait
            if (actionHandler != null) {
                javafx.application.Platform.runLater(() ->
                    actionHandler.handle(new javafx.event.ActionEvent(this, null))
                );
            }
        });
        pause.play();
    }


    public void setTexto(String texto) {
        this.texto = texto != null ? texto : "";
        actualizarContenido();
    }

    public String getTexto() { return texto; }

    public void setIcono(Image icono) {
        this.icono = icono;
        actualizarContenido();
    }

    public void setIcono(String rutaRecurso) {
        try {
            this.icono = new Image(getClass().getResourceAsStream(rutaRecurso));
        } catch (Exception e) {
            this.icono = null;
        }
        asignarTeclaPorRuta(rutaRecurso);
        actualizarContenido();
    }

    public Image getIcono() { return icono; }

    public void setOnAction(javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        this.actionHandler = handler;
    }

    private void inicializarEstilos() {
        setMinWidth(60);
        setMinHeight(35);
        setPadding(new Insets(4, 8, 4, 8));
        aplicarEstilo(false);
    }

    // ========================================================
    // Medición y división de texto
    // ========================================================

    private Font crearFuente() {
        return Font.font(FwFuentes.BOLD, FontWeight.BOLD, FONT_SIZE);
    }

    /** Mide el ancho en píxeles que ocupa un texto con la fuente actual. */
    private double medirAncho(String txt, Font font) {
        Text medidor = new Text(txt);
        medidor.setFont(font);
        return medidor.getLayoutBounds().getWidth();
    }

    /**
     * Divide el texto en máximo 2 líneas que quepan en anchoDisponible.
     * - Si cabe en 1 línea → retorna array de 1 elemento.
     * - Si no cabe, busca el último espacio que permita que la primera parte quepa.
     *   Si no hay espacio, corta la palabra.
     * - La segunda línea se trunca con "..." si no cabe.
     */
    private String[] dividirTexto(String txt, double anchoDisponible, Font font) {
        if (anchoDisponible <= 0) return new String[]{txt};

        double anchoTotal = medirAncho(txt, font);
        if (anchoTotal <= anchoDisponible) {
            return new String[]{txt};
        }

        // Buscar el último espacio cuya primera parte quepa
        String linea1 = null;
        String linea2 = null;

        int ultimoEspacio = -1;
        for (int i = 0; i < txt.length(); i++) {
            if (txt.charAt(i) == ' ') {
                String candidato = txt.substring(0, i);
                if (medirAncho(candidato, font) <= anchoDisponible) {
                    ultimoEspacio = i;
                } else {
                    break; // ya no van a caber más
                }
            }
        }

        if (ultimoEspacio > 0) {
            // Dividir por el espacio
            linea1 = txt.substring(0, ultimoEspacio);
            linea2 = txt.substring(ultimoEspacio + 1);
        } else {
            // No hay espacio: cortar la palabra por la mitad que quepa
            int corte = txt.length();
            for (int i = 1; i < txt.length(); i++) {
                if (medirAncho(txt.substring(0, i + 1), font) > anchoDisponible) {
                    corte = i;
                    break;
                }
            }
            linea1 = txt.substring(0, corte);
            linea2 = txt.substring(corte);
        }

        // Truncar linea2 si no cabe
        if (medirAncho(linea2, font) > anchoDisponible) {
            String puntos = "...";
            double anchoPuntos = medirAncho(puntos, font);
            int corte2 = linea2.length();
            for (int i = 1; i < linea2.length(); i++) {
                if (medirAncho(linea2.substring(0, i), font) + anchoPuntos > anchoDisponible) {
                    corte2 = Math.max(1, i - 1);
                    break;
                }
            }
            linea2 = linea2.substring(0, corte2) + puntos;
        }

        return new String[]{linea1, linea2};
    }

    // ========================================================
    // Actualizar contenido visual
    // ========================================================

    private javafx.scene.control.Label crearLabel(String txt) {
        javafx.scene.control.Label lbl = new javafx.scene.control.Label(txt);
        lbl.setStyle(String.format(
            "-fx-font-family: '%s'; -fx-text-fill: %s; -fx-font-weight: bold; -fx-font-size: %.1fpx;",
            FwFuentes.BOLD,
            FwColorTema.toCss(FwColorTema.TEXTO_OSCURO),
            FONT_SIZE
        ));
        lbl.setAlignment(Pos.CENTER_LEFT);
        return lbl;
    }

    private void actualizarContenido() {
        contenido.getChildren().clear();

        boolean tieneIcono = icono != null;
        boolean tieneTexto = texto != null && !texto.isBlank();

        if (tieneIcono && tieneTexto) {
            contenido.setAlignment(Pos.CENTER_LEFT);

            double altoControl = getHeight() > 0 ? getHeight() : 40;
            double altoIcono = altoControl * PCT_ICONO_ALTO;

            double anchoIcono = altoIcono;
            if (icono.getWidth() > 0 && icono.getHeight() > 0) {
                anchoIcono = altoIcono * (icono.getWidth() / icono.getHeight());
            }
            iconoView.setImage(icono);
            iconoView.setFitHeight(altoIcono);
            iconoView.setFitWidth(anchoIcono);

            // Calcular espacio disponible para texto
            double anchoBoton = getWidth() > 0 ? getWidth() : (getPrefWidth() > 0 ? getPrefWidth() : 200);
            Insets pad = getPadding();
            double padH = (pad != null) ? pad.getLeft() + pad.getRight() : 16;
            double anchoTexto = anchoBoton - padH - anchoIcono - SEPARACION_ICONO_TEXTO;

            Font font = crearFuente();
            String[] lineas = dividirTexto(texto, anchoTexto, font);

            if (lineas.length == 1) {
                javafx.scene.control.Label lbl = crearLabel(lineas[0]);
                contenido.getChildren().addAll(iconoView, lbl);
            } else {
                VBox textoBox = new VBox(0);
                textoBox.setAlignment(Pos.CENTER_LEFT);
                textoBox.getChildren().addAll(crearLabel(lineas[0]), crearLabel(lineas[1]));
                contenido.getChildren().addAll(iconoView, textoBox);
            }

        } else if (tieneIcono) {
            contenido.setAlignment(Pos.CENTER);
            double altoIcono = (getHeight() > 0 ? getHeight() : 40) * PCT_ICONO_ALTO;
            double anchoIcono = altoIcono;
            if (icono.getWidth() > 0 && icono.getHeight() > 0) {
                anchoIcono = altoIcono * (icono.getWidth() / icono.getHeight());
            }
            iconoView.setImage(icono);
            iconoView.setFitHeight(altoIcono);
            iconoView.setFitWidth(anchoIcono);
            contenido.getChildren().add(iconoView);

        } else if (tieneTexto) {
            contenido.setAlignment(Pos.CENTER);
            contenido.getChildren().add(crearLabel(texto));
        }
    }

    private void registrarEventos() {
        setOnMousePressed(e -> {
            pressed = true;
            aplicarEstilo(true);
            contenido.setTranslateX(DESPLAZAMIENTO_PRESS);
            contenido.setTranslateY(DESPLAZAMIENTO_PRESS);
        });

        setOnMouseReleased(e -> {
            if (pressed) {
                pressed = false;
                aplicarEstilo(false);
                contenido.setTranslateX(0);
                contenido.setTranslateY(0);
                if (contains(e.getX(), e.getY()) && actionHandler != null) {
                    actionHandler.handle(new javafx.event.ActionEvent(this, null));
                }
            }
        });

        setOnMouseExited(e -> {
            if (pressed) {
                pressed = false;
                aplicarEstilo(false);
                contenido.setTranslateX(0);
                contenido.setTranslateY(0);
            }
        });

        setCursor(javafx.scene.Cursor.HAND);
    }

    private void aplicarEstilo(boolean presionado) {
        String colorBorde1, colorBorde2;
        if (!presionado) {
            colorBorde1 = FwColorTema.toCss(FwColorTema.CBORDE_BLANCO);
            colorBorde2 = FwColorTema.toCss(FwColorTema.BORDE_GRIS);
        } else {
            colorBorde1 = FwColorTema.toCss(FwColorTema.BORDE_GRIS);
            colorBorde2 = FwColorTema.toCss(FwColorTema.CBORDE_BLANCO);
        }
        setStyle(String.format(
            "-fx-background-color: %s; -fx-border-color: %s %s %s %s; -fx-border-width: 2;",
            FwColorTema.toCss(FwColorTema.FONDO_MEDIO),
            colorBorde1, colorBorde2, colorBorde2, colorBorde1
        ));
    }
}
