package framework.teclado;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Gestiona la deteccion de teclas especiales y enruta acciones segun estado.
 * Las teclas normales no se bloquean ni alteran su comportamiento natural.
 */
public final class ControlTecladoIndustrial {

    private ConfigTecladoIndustrial config;
    private final Map<EstadoSistema, Map<TeclaEspecial, Consumer<EventoTeclaEspecial>>> accionesPorEstado;
    private final List<Consumer<EventoTeclaEspecial>> observadoresGlobales;

    private EstadoSistema estadoActual;
    private Scene escenaConectada;
    private EventHandler<KeyEvent> listener;

    public ControlTecladoIndustrial(ConfigTecladoIndustrial config, EstadoSistema estadoInicial) {
        this.config = Objects.requireNonNull(config, "config no puede ser null");
        this.estadoActual = Objects.requireNonNull(estadoInicial, "estadoInicial no puede ser null");
        this.accionesPorEstado = new EnumMap<>(EstadoSistema.class);
        this.observadoresGlobales = new ArrayList<>();
    }

    public void conectar(Scene scene) {
        Objects.requireNonNull(scene, "scene no puede ser null");
        desconectar();

        listener = this::procesarKeyPressed;
        scene.addEventFilter(KeyEvent.KEY_PRESSED, listener);
        escenaConectada = scene;
    }

    public void desconectar() {
        if (escenaConectada != null && listener != null) {
            escenaConectada.removeEventFilter(KeyEvent.KEY_PRESSED, listener);
        }
        escenaConectada = null;
        listener = null;
    }

    public void setEstadoActual(EstadoSistema nuevoEstado) {
        this.estadoActual = Objects.requireNonNull(nuevoEstado, "nuevoEstado no puede ser null");
    }

    public EstadoSistema getEstadoActual() {
        return estadoActual;
    }

    public ConfigTecladoIndustrial getConfigActual() {
        return config;
    }

    public void actualizarConfig(ConfigTecladoIndustrial nuevaConfig) {
        this.config = Objects.requireNonNull(nuevaConfig, "nuevaConfig no puede ser null");
    }

    public void registrarAccion(EstadoSistema estado, TeclaEspecial tecla, Consumer<EventoTeclaEspecial> accion) {
        Objects.requireNonNull(estado, "estado no puede ser null");
        Objects.requireNonNull(tecla, "tecla no puede ser null");
        Objects.requireNonNull(accion, "accion no puede ser null");

        Map<TeclaEspecial, Consumer<EventoTeclaEspecial>> accionesTecla =
            accionesPorEstado.computeIfAbsent(estado, e -> new EnumMap<>(TeclaEspecial.class));

        accionesTecla.put(tecla, accion);
    }

    public void registrarObservadorGlobal(Consumer<EventoTeclaEspecial> observador) {
        observadoresGlobales.add(Objects.requireNonNull(observador, "observador no puede ser null"));
    }

    private void procesarKeyPressed(KeyEvent event) {
        DescritorTecla descritor = new DescritorTecla(event.getCode(), event.isControlDown(), event.isShiftDown(), event.isAltDown());
        TeclaEspecial teclaEspecial = config.buscarTeclaEspecial(descritor);
        if (teclaEspecial == null) {
            return;
        }

        // Toda tecla especial se consume para evitar comportamiento de teclado comun.
        event.consume();

        EventoTeclaEspecial evento = new EventoTeclaEspecial(teclaEspecial, estadoActual, event.getCode());

        for (Consumer<EventoTeclaEspecial> observador : observadoresGlobales) {
            observador.accept(evento);
        }

        Consumer<EventoTeclaEspecial> accion = buscarAccion(estadoActual, teclaEspecial);
        if (accion != null) {
            accion.accept(evento);
        }
    }

    private Consumer<EventoTeclaEspecial> buscarAccion(EstadoSistema estado, TeclaEspecial tecla) {
        Map<TeclaEspecial, Consumer<EventoTeclaEspecial>> accionesTecla = accionesPorEstado.get(estado);
        if (accionesTecla == null) {
            return null;
        }
        return accionesTecla.get(tecla);
    }
}


