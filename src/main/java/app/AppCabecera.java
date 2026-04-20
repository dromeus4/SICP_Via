package app;

import framework.*;
import framework.teclado.EstadoSistema;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * Cabecera de la aplicación con:
 * - 3 campos de información: ESTACIÓN, CAJA, ESTADO
 * - Logo a la derecha (90% del alto)
 */
public class AppCabecera extends FwCabecera {

    private final FwCampoInfo campoEstacion;
    private final FwCampoInfo campoCaja;
    private final FwSemaforo semaforo;
    private final FwCampoInfo campoEstado;
    private final ImageView logoView;

    public AppCabecera() {
        // Campos de información
        campoEstacion = new FwCampoInfo("ESTACIÓN:", "(MG) Minga Guazú");
        campoEstacion.setColorTexto(Color.WHITE);

        campoCaja = new FwCampoInfo("CAJA:", "MG06");
        campoCaja.setColorTexto(Color.WHITE);

        // Semáforo entre CAJA y ESTADO
        semaforo = new FwSemaforo();

        campoEstado = new FwCampoInfo("ESTADO:", "CERRADO");
        campoEstado.setColorTexto(Color.WHITE);

        // Logo
        logoView = new ImageView();
        logoView.setPreserveRatio(true);
        try {
            Image logo = new Image(getClass().getResourceAsStream("/imgs/logo01.png"));
            logoView.setImage(logo);
        } catch (Exception e) {
            System.err.println("No se pudo cargar el logo: " + e.getMessage());
        }

        // Espaciador para empujar el logo a la derecha
        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        agregarComponentes(campoEstacion, campoCaja, semaforo, campoEstado, espaciador, logoView);
        actualizarEstado(EstadoSistema.CERRADO);
    }

    @Override
    public void configurarResponsivo(ReadOnlyDoubleProperty anchoVentana, ReadOnlyDoubleProperty altoVentana) {
        super.configurarResponsivo(anchoVentana, altoVentana);

        // Escuchar cambios de alto de la cabecera
        heightProperty().addListener((obs, oldVal, newVal) -> {
            actualizarTamanos(newVal.doubleValue(), anchoVentana.get());
        });

        // Escuchar cambios de ancho
        anchoVentana.addListener((obs, oldVal, newVal) -> {
            actualizarTamanos(getHeight(), newVal.doubleValue());
        });

        // Configuración inicial
        actualizarTamanos(getHeight() > 0 ? getHeight() : 40, anchoVentana.get());
    }

    private void actualizarTamanos(double altoCabecera, double anchoVentana) {
        double altoControles = altoCabecera;

        // Logo: 100% del alto de la cabecera
        logoView.setFitHeight(altoCabecera);

        // Semáforo: cuadrado, 100% del alto de la cabecera
        semaforo.configurarTamano(altoCabecera);

        // Descontar semáforo y separaciones del espacio disponible para campos
        double sep = FwLayoutResponsivo.SEPARACION_CAMPOS;
        // Componentes: padding(sep) + estacion + sep + caja + sep + semaforo + sep + estado + sep + espaciador + sep + logo + padding(sep)
        double anchoFijo = altoCabecera + sep * 7 + sep * 2; // semáforo + 7 spacings + 2 paddings
        double anchoDisponibleCampos = (anchoVentana - anchoFijo) * 0.5; // 50% del resto para campos
        if (anchoDisponibleCampos < 100) anchoDisponibleCampos = 100;

        // Distribuir ancho de los campos (proporciones 3:1:2 = total 6)
        double anchoEstacion = anchoDisponibleCampos * (3.0 / 6.0);
        double anchoCaja = anchoDisponibleCampos * (1.0 / 6.0);
        double anchoEstado = anchoDisponibleCampos * (2.0 / 6.0);

        campoEstacion.configurarDimensiones(anchoEstacion, altoControles);
        campoCaja.configurarDimensiones(anchoCaja, altoControles);
        campoEstado.configurarDimensiones(anchoEstado, altoControles);
    }

    public void actualizarEstado(EstadoSistema estado) {
        campoEstado.setContenido(estado.name());

        // Actualizar semáforo según el estado
        switch (estado) {
            case ABIERTO:
                semaforo.setEstado(FwSemaforo.Estado.VERDE);
                break;
            case CERRADO:
            case BLOQUEADO:
                semaforo.setEstado(FwSemaforo.Estado.ROJO);
                break;
            case MANTENIMIENTO:
                semaforo.setEstado(FwSemaforo.Estado.APAGADO);
                break;
            default:
                semaforo.setEstado(FwSemaforo.Estado.APAGADO);
                break;
        }
    }

    public void setEstacion(String valor) {
        campoEstacion.setContenido(valor);
    }

    public void setCaja(String valor) {
        campoCaja.setContenido(valor);
    }
}
