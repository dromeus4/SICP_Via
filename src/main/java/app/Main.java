package app;

import framework.FwDialogoClave;
import framework.FwDialogoClaveBolsa;
import framework.FwDialogoConfigServidor;
import framework.FwFuentes;
import framework.FwMensaje;
import framework.teclado.ConfigTecladoIndustrial;
import framework.teclado.ConfigSistemaRepository;
import framework.teclado.EstadoSistema;
import framework.teclado.EventoTeclaEspecial;
import framework.teclado.GestorTecladoGlobal;
import framework.teclado.TeclaEspecial;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;


public class Main extends Application {

    private GestorTecladoGlobal gestorTeclado;
    private ConfigSistemaRepository configSistemaRepository;
    private VentanaPrincipal ventanaPrincipal;
    private final ServicioValidacionCaja servicioValidacion = new ServicioValidacionCaja();

    private EstadoSistema estadoActual = EstadoSistema.CERRADO;
    private boolean cierreAutorizado = false;

    /** Datos de conexión al servidor, almacenados en memoria (se pierden al salir) */
    private FwDialogoConfigServidor.Resultado datosServidor = null;

    @Override
    public void start(Stage stage) {
        FwFuentes.registrarFuentesEmpaquetadas();

        configSistemaRepository = new ConfigSistemaRepository();
        ConfigTecladoIndustrial configTeclado =
            configSistemaRepository.cargarTeclado(ConfigTecladoIndustrial.porDefecto());
        configSistemaRepository.guardarTeclado(configTeclado);

        ventanaPrincipal = VentanaPrincipal.crear(
            stage,
            this::abrirConfiguradorServidor,
            this::confirmarSalidaSistema,
            this::procesoAbrirCaja,
            this::procesoAbrirCajaEmergencia,
            this::procesoBloquearCaja,
            this::procesoMantenimiento,
            this::procesoDesbloquearCaja,
            this::abrirConfiguradorTeclado,
            this::procesoCerrarCaja
        );

        // Control global de teclado: todas las teclas normales bloqueadas.
        gestorTeclado = new GestorTecladoGlobal(configTeclado, estadoActual);
        for (EstadoSistema estado : EstadoSistema.values()) {
            gestorTeclado.definirPolitica(estado, new GestorTecladoGlobal.PoliticaTeclas().bloquearTodo());
        }

        gestorTeclado.registrarObservadorGlobal(this::manejarTeclaEspecial);
        gestorTeclado.conectar(ventanaPrincipal.getStage().getScene());

        // Bloquear cierre por ALT+F4 o boton X. Solo se sale por boton SALIR confirmado.
        stage.setOnCloseRequest(event -> {
            if (!cierreAutorizado) {
                event.consume();
                mostrarInformacion("Cierre bloqueado", "La única forma de salir es con el botón SALIR.");
            }
        });

        sincronizarEstado();
        ventanaPrincipal.mostrar();
    }

    private void manejarTeclaEspecial(EventoTeclaEspecial evento) {
        TeclaEspecial tecla = evento.getTecla();

        if (estadoActual == EstadoSistema.CERRADO) {
            if (tecla == TeclaEspecial.VERDE) {
                procesoAbrirCaja();
            } else if (tecla == TeclaEspecial.ROJO) {
                procesoAbrirCajaEmergencia();
            }
            return;
        }

        if (estadoActual == EstadoSistema.ABIERTO && tecla == TeclaEspecial.ROJO) {
            procesoBloquearCaja();
        }

        if (estadoActual == EstadoSistema.BLOQUEADO && tecla == TeclaEspecial.VERDE) {
            procesoDesbloquearCaja();
        }

        if (estadoActual == EstadoSistema.BLOQUEADO && tecla == TeclaEspecial.ROJO) {
            procesoCerrarCaja();
        }
    }

    private void procesoAbrirCaja() {
        if (estadoActual != EstadoSistema.CERRADO) {
            return;
        }

        FwDialogoClave dialogo = new FwDialogoClave(ventanaPrincipal.getStage(), "ABRIR CAJA", "Ingresa CLAVE DE CAJERO");
        String clave = dialogo.mostrar();
        if (clave == null || clave.isBlank()) {
            return;
        }

        ServicioValidacionCaja.ResultadoValidacion resultado =
            servicioValidacion.validarAperturaNormal(clave);

        if (!resultado.permitido()) {
            mostrarError("No se pudo abrir caja", resultado.mensajeError());
            return;
        }

        cambiarEstado(EstadoSistema.ABIERTO);
    }

    private void procesoAbrirCajaEmergencia() {
        if (estadoActual != EstadoSistema.CERRADO) {
            return;
        }

        FwDialogoClaveBolsa dialogo = new FwDialogoClaveBolsa(ventanaPrincipal.getStage(), "ABRIR CAJA DE EMERGENCIA", "Ingresa CLAVE DE CAJERO y NÚMERO DE BOLSA");
        FwDialogoClaveBolsa.Resultado datos = dialogo.mostrar();
        if (datos == null) {
            return;
        }

        ServicioValidacionCaja.ResultadoValidacion resultado =
            servicioValidacion.validarAperturaEmergencia(datos.getClave(), datos.getBolsa());

        if (!resultado.permitido()) {
            mostrarError("No se pudo abrir caja de emergencia", resultado.mensajeError());
            return;
        }

        cambiarEstado(EstadoSistema.ABIERTO);
    }

    private void procesoCerrarCaja() {
        if (estadoActual != EstadoSistema.ABIERTO
            && estadoActual != EstadoSistema.BLOQUEADO
            && estadoActual != EstadoSistema.MANTENIMIENTO) {
            return;
        }

        if (!confirmar("Cerrar caja", "¿Confirmas cerrar la caja y volver a estado CERRADO?")) {
            return;
        }

        cambiarEstado(EstadoSistema.CERRADO);
    }

    private void procesoMantenimiento() {
        if (estadoActual != EstadoSistema.CERRADO) {
            return;
        }

        FwDialogoClave dialogo = new FwDialogoClave(ventanaPrincipal.getStage(), "MANTENIMIENTO", "Ingresa CLAVE DE USUARIO");
        String clave = dialogo.mostrar();
        if (clave == null || clave.isBlank()) {
            return;
        }

        ServicioValidacionCaja.ResultadoValidacion resultado =
            servicioValidacion.validarMantenimientoUsuario(clave);

        if (!resultado.permitido()) {
            mostrarError("No se pudo pasar a mantenimiento", resultado.mensajeError());
            return;
        }

        cambiarEstado(EstadoSistema.MANTENIMIENTO);
    }

    private void procesoBloquearCaja() {
        if (estadoActual != EstadoSistema.ABIERTO) {
            return;
        }

        if (!confirmar("Bloquear caja", "¿Confirmas pasar la caja a estado BLOQUEADO?")) {
            return;
        }

        cambiarEstado(EstadoSistema.BLOQUEADO);
    }

    private void procesoDesbloquearCaja() {
        if (estadoActual != EstadoSistema.BLOQUEADO) {
            return;
        }

        FwDialogoClave dialogo = new FwDialogoClave(ventanaPrincipal.getStage(), "DESBLOQUEAR CAJA", "Ingresa CLAVE DE USUARIO");
        String clave = dialogo.mostrar();
        if (clave == null || clave.isBlank()) {
            return;
        }

        ServicioValidacionCaja.ResultadoValidacion resultado =
            servicioValidacion.validarDesbloqueoUsuario(clave);

        if (!resultado.permitido()) {
            mostrarError("No se pudo desbloquear caja", resultado.mensajeError());
            return;
        }


        cambiarEstado(EstadoSistema.ABIERTO);
    }

    private void confirmarSalidaSistema() {
        if (!confirmar("Salir del sistema", "¿Confirmas salir del sistema?")) {
            return;
        }

        cierreAutorizado = true;
        ventanaPrincipal.getStage().close();
        Platform.exit();
    }

    private void abrirConfiguradorTeclado() {
        VentanaConfigTecladoIndustrial.mostrar(
            ventanaPrincipal.getStage(),
            gestorTeclado.getConfig(),
            nuevaConfig -> {
                configSistemaRepository.guardarTeclado(nuevaConfig);
                mostrarInformacion("Configuración guardada",
                    "Se guardó el mapeo de teclas en: " + configSistemaRepository.rutaArchivoConfig());
            }
        );
    }

    private void abrirConfiguradorServidor() {
        VentanaConfigServidor.mostrar(
            ventanaPrincipal.getStage(),
            datosServidor,
            nuevoDatos -> {
                datosServidor = nuevoDatos;
                mostrarInformacion("Configuración guardada",
                    "Se guardaron los datos del servidor.");
            }
        );
    }

    private void cambiarEstado(EstadoSistema nuevoEstado) {
        estadoActual = nuevoEstado;
        gestorTeclado.setEstadoActual(nuevoEstado);
        sincronizarEstado();
    }

    private void sincronizarEstado() {
        ventanaPrincipal.actualizarEstado(estadoActual);
    }


    private boolean confirmar(String titulo, String mensaje) {
        int resultado = FwMensaje.confirmar(ventanaPrincipal.getStage(), titulo, mensaje);
        return resultado == FwMensaje.RESULTADO_ACEPTAR;
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        FwMensaje.informar(ventanaPrincipal.getStage(), titulo, mensaje);
    }

    private void mostrarError(String titulo, String mensaje) {
        FwMensaje.error(ventanaPrincipal.getStage(), titulo, mensaje);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

