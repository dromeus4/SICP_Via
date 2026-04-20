package framework.teclado;

import javafx.scene.input.KeyCode;

import java.time.Instant;

/**
 * Evento generado cuando se detecta una tecla especial del teclado industrial.
 */
public final class EventoTeclaEspecial {

    private final TeclaEspecial tecla;
    private final EstadoSistema estadoSistema;
    private final KeyCode keyCodeOrigen;
    private final Instant timestamp;

    public EventoTeclaEspecial(TeclaEspecial tecla, EstadoSistema estadoSistema, KeyCode keyCodeOrigen) {
        this.tecla = tecla;
        this.estadoSistema = estadoSistema;
        this.keyCodeOrigen = keyCodeOrigen;
        this.timestamp = Instant.now();
    }

    public TeclaEspecial getTecla() {
        return tecla;
    }

    public EstadoSistema getEstadoSistema() {
        return estadoSistema;
    }

    public KeyCode getKeyCodeOrigen() {
        return keyCodeOrigen;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}

