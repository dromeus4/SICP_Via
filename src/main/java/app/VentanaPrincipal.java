package app;

import framework.*;
import framework.teclado.EstadoSistema;
import javafx.stage.Stage;

public class VentanaPrincipal extends FwVentana {

    private static final ThreadLocal<CallbacksConstruccion> CALLBACKS_HOLDER = new ThreadLocal<>();

    private AppCabecera cabecera;
    private AppPanel panel;
    private AppContenido contenido;
    private AppPie pie;

    private VentanaPrincipal(Stage stage) {
        super(stage, "SICP Vía - Sistema de Caja", 1280, 800);
    }

    public static VentanaPrincipal crear(
        Stage stage,
        Runnable onConfigurar,
        Runnable onSalir,
        Runnable onAbrirCaja,
        Runnable onAbrirEmergencia,
        Runnable onBloquearCaja,
        Runnable onMantenimiento,
        Runnable onDesbloquearCaja,
        Runnable onConfigTeclado,
        Runnable onCerrarCaja
    ) {
        CALLBACKS_HOLDER.set(new CallbacksConstruccion(
            onConfigurar,
            onSalir,
            onAbrirCaja,
            onAbrirEmergencia,
            onBloquearCaja,
            onMantenimiento,
            onDesbloquearCaja,
            onConfigTeclado,
            onCerrarCaja
        ));
        try {
            return new VentanaPrincipal(stage);
        } finally {
            CALLBACKS_HOLDER.remove();
        }
    }

    @Override
    protected FwCabecera crearCabecera() {
        cabecera = new AppCabecera();
        return cabecera;
    }

    @Override
    protected FwBarraPanel crearPanel() {
        panel = new AppPanel();
        return panel;
    }

    @Override
    protected FwContenido crearContenido() {
        CallbacksConstruccion callbacks = CALLBACKS_HOLDER.get();
        contenido = new AppContenido(
            callbacks == null ? null : callbacks.onAbrirCaja,
            callbacks == null ? null : callbacks.onAbrirEmergencia,
            callbacks == null ? null : callbacks.onBloquearCaja,
            callbacks == null ? null : callbacks.onMantenimiento,
            callbacks == null ? null : callbacks.onDesbloquearCaja,
            callbacks == null ? null : callbacks.onConfigTeclado,
            callbacks == null ? null : callbacks.onCerrarCaja
        );
        return contenido;
    }

    @Override
    protected FwPie crearPie() {
        CallbacksConstruccion callbacks = CALLBACKS_HOLDER.get();
        pie = new AppPie(
            callbacks == null ? null : callbacks.onConfigurar,
            callbacks == null ? null : callbacks.onSalir
        );
        return pie;
    }

    public void actualizarEstado(EstadoSistema estado) {
        if (cabecera != null) {
            cabecera.actualizarEstado(estado);
        }
        if (contenido != null) {
            contenido.actualizarEstado(estado);
        }
        if (pie != null) {
            pie.actualizarEstado(estado);
        }
    }

    private static final class CallbacksConstruccion {
        private final Runnable onConfigurar;
        private final Runnable onSalir;
        private final Runnable onAbrirCaja;
        private final Runnable onAbrirEmergencia;
        private final Runnable onBloquearCaja;
        private final Runnable onMantenimiento;
        private final Runnable onDesbloquearCaja;
        private final Runnable onConfigTeclado;
        private final Runnable onCerrarCaja;

        private CallbacksConstruccion(
            Runnable onConfigurar,
            Runnable onSalir,
            Runnable onAbrirCaja,
            Runnable onAbrirEmergencia,
            Runnable onBloquearCaja,
            Runnable onMantenimiento,
            Runnable onDesbloquearCaja,
            Runnable onConfigTeclado,
            Runnable onCerrarCaja
        ) {
            this.onConfigurar = onConfigurar;
            this.onSalir = onSalir;
            this.onAbrirCaja = onAbrirCaja;
            this.onAbrirEmergencia = onAbrirEmergencia;
            this.onBloquearCaja = onBloquearCaja;
            this.onMantenimiento = onMantenimiento;
            this.onDesbloquearCaja = onDesbloquearCaja;
            this.onConfigTeclado = onConfigTeclado;
            this.onCerrarCaja = onCerrarCaja;
        }
    }
}
