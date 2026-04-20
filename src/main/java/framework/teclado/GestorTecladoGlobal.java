package framework.teclado;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Gestor global de teclado que intercepta y controla TODAS las teclas presionadas.
 *
 * Funcionalidades:
 * - Captura todas las teclas antes de que lleguen a componentes.
 * - Distingue teclas especiales vs. normales.
 * - Filtra teclas según el estado del sistema (whitelist/blacklist).
 * - Ejecuta acciones de teclas especiales.
 * - Permite o bloquea teclas normales segun politica de estado.
 * - Modo "bloqueado total" para prohibir todo excepto especiales.
 */
public final class GestorTecladoGlobal {

    private final ConfigTecladoIndustrial config;
    private final Map<EstadoSistema, PoliticaTeclas> politicasPorEstado;
    private final List<Consumer<EventoTeclaEspecial>> observadoresGlobales;
    private final List<Consumer<DescritorTecla>> observadoresTeclasNormales;

    private EstadoSistema estadoActual;
    private Scene escenaConectada;
    private EventHandler<KeyEvent> filterGlobal;
    private boolean bloqueado;

    public GestorTecladoGlobal(ConfigTecladoIndustrial config, EstadoSistema estadoInicial) {
        this.config = Objects.requireNonNull(config, "config no puede ser null");
        this.estadoActual = Objects.requireNonNull(estadoInicial, "estadoInicial no puede ser null");
        this.politicasPorEstado = new EnumMap<>(EstadoSistema.class);
        this.observadoresGlobales = new ArrayList<>();
        this.observadoresTeclasNormales = new ArrayList<>();
        this.bloqueado = false;

        inicializarPoliticasDefault();
    }

    private void inicializarPoliticasDefault() {
        // Por defecto, permitir todas las teclas hasta que Main defina políticas.
        for (EstadoSistema estado : EstadoSistema.values()) {
            politicasPorEstado.put(estado, new PoliticaTeclas());
        }
    }

    public void conectar(Scene scene) {
        Objects.requireNonNull(scene, "scene no puede ser null");
        desconectar();

        filterGlobal = this::interceptarKeyEvent;
        scene.addEventFilter(KeyEvent.KEY_PRESSED, filterGlobal);
        escenaConectada = scene;
    }

    public void desconectar() {
        if (escenaConectada != null && filterGlobal != null) {
            escenaConectada.removeEventFilter(KeyEvent.KEY_PRESSED, filterGlobal);
        }
        escenaConectada = null;
        filterGlobal = null;
    }

    public void setEstadoActual(EstadoSistema nuevoEstado) {
        this.estadoActual = Objects.requireNonNull(nuevoEstado, "nuevoEstado no puede ser null");
    }

    public EstadoSistema getEstadoActual() {
        return estadoActual;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void definirPolitica(EstadoSistema estado, PoliticaTeclas politica) {
        politicasPorEstado.put(
            Objects.requireNonNull(estado, "estado no puede ser null"),
            Objects.requireNonNull(politica, "politica no puede ser null")
        );
    }

    public void registrarObservadorGlobal(Consumer<EventoTeclaEspecial> observador) {
        observadoresGlobales.add(Objects.requireNonNull(observador, "observador no puede ser null"));
    }

    public void registrarObservadorTeclasNormales(Consumer<DescritorTecla> observador) {
        observadoresTeclasNormales.add(Objects.requireNonNull(observador, "observador no puede ser null"));
    }

    private void interceptarKeyEvent(KeyEvent event) {
        DescritorTecla descritor = new DescritorTecla(
            event.getCode(),
            event.isControlDown(),
            event.isShiftDown(),
            event.isAltDown()
        );

        boolean esEspecial = config.esTeclaEspecial(descritor);

        // Si está bloqueado TOTAL, solo permitir teclas especiales
        if (bloqueado && !esEspecial) {
            event.consume();
            return;
        }

        if (esEspecial) {
            procesarTeclaEspecial(descritor, event);
        } else {
            procesarTeclaNormal(descritor, event);
        }
    }

    private void procesarTeclaEspecial(DescritorTecla descritor, KeyEvent event) {
        TeclaEspecial teclaEspecial = config.buscarTeclaEspecial(descritor);
        if (teclaEspecial == null) {
            return;
        }

        EventoTeclaEspecial evento = new EventoTeclaEspecial(teclaEspecial, estadoActual, event.getCode());

        for (Consumer<EventoTeclaEspecial> observador : observadoresGlobales) {
            observador.accept(evento);
        }

        event.consume();
    }

    private void procesarTeclaNormal(DescritorTecla descritor, KeyEvent event) {
        PoliticaTeclas politica = politicasPorEstado.get(estadoActual);
        if (politica == null) {
            return;
        }

        // Notificar observadores de teclas normales
        for (Consumer<DescritorTecla> observador : observadoresTeclasNormales) {
            observador.accept(descritor);
        }

        if (!politica.permitir(descritor)) {
            event.consume();
        }
    }

    /**
     * Define la política de filtrado de teclas según el estado del sistema.
     * Permite whitelist/blacklist granular de teclas.
     */
    public static final class PoliticaTeclas {
        private final List<DescritorTecla> whitelist;
        private final List<DescritorTecla> blacklist;
        private boolean permitirTodo;
        private boolean bloqueaTodo;

        public PoliticaTeclas() {
            this.whitelist = new ArrayList<>();
            this.blacklist = new ArrayList<>();
            this.permitirTodo = true;
            this.bloqueaTodo = false;
        }

        public PoliticaTeclas permitirTodo() {
            this.permitirTodo = true;
            this.whitelist.clear();
            this.blacklist.clear();
            return this;
        }

        public PoliticaTeclas bloquearTodo() {
            this.bloqueaTodo = true;
            this.permitirTodo = false;
            this.whitelist.clear();
            this.blacklist.clear();
            return this;
        }

        public PoliticaTeclas bloquearSolo(DescritorTecla... descriptores) {
            this.permitirTodo = true;
            this.bloqueaTodo = false;
            this.whitelist.clear();
            this.blacklist.clear();
            Collections.addAll(this.blacklist, descriptores);
            return this;
        }

        public PoliticaTeclas permitirSolo(DescritorTecla... descriptores) {
            this.permitirTodo = false;
            this.bloqueaTodo = false;
            this.whitelist.clear();
            this.blacklist.clear();
            Collections.addAll(this.whitelist, descriptores);
            return this;
        }

        public boolean permitir(DescritorTecla descritor) {
            if (bloqueaTodo) {
                return false;
            }
            if (permitirTodo) {
                return !blacklist.contains(descritor);
            }
            return whitelist.contains(descritor);
        }
    }

    public ConfigTecladoIndustrial getConfig() {
        return config;
    }
}

