package app;

import framework.FwDialogoConfigServidor;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Wrapper para mostrar la ventana de configuración del servidor.
 * Recoge los datos y los devuelve al llamador mediante un callback.
 */
public final class VentanaConfigServidor {

    private VentanaConfigServidor() {}

    /**
     * Muestra la ventana de configuración del servidor.
     *
     * @param owner         Ventana padre
     * @param datosActuales Datos actuales precargados (puede ser null)
     * @param onGuardar     Callback con los datos ingresados al presionar GUARDAR
     */
    public static void mostrar(Stage owner,
                               FwDialogoConfigServidor.Resultado datosActuales,
                               Consumer<FwDialogoConfigServidor.Resultado> onGuardar) {
        Objects.requireNonNull(owner, "owner no puede ser null");
        Objects.requireNonNull(onGuardar, "onGuardar no puede ser null");

        FwDialogoConfigServidor dialogo = new FwDialogoConfigServidor(owner, datosActuales);
        FwDialogoConfigServidor.Resultado resultado = dialogo.mostrar();

        if (resultado != null) {
            onGuardar.accept(resultado);
        }
    }
}

