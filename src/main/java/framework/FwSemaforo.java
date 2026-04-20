package framework;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Control visual SEMÁFORO con tres estados: APAGADO, ROJO y VERDE.
 * Es un control cuadrado (ancho = alto) que muestra una imagen según el estado.
 */
public class FwSemaforo extends StackPane {

    public enum Estado {
        APAGADO, ROJO, VERDE
    }

    private final ImageView imageView;
    private final Image imgApagado;
    private final Image imgRojo;
    private final Image imgVerde;
    private Estado estadoActual;

    public FwSemaforo() {
        imageView = new ImageView();
        imageView.setPreserveRatio(true);

        imgApagado = cargarImagen("/imgs/semaforoapagado.jpg");
        imgRojo = cargarImagen("/imgs/semafororojo.jpg");
        imgVerde = cargarImagen("/imgs/semaforoverde.jpg");

        getChildren().add(imageView);
        setEstado(Estado.APAGADO);
    }

    private Image cargarImagen(String ruta) {
        try {
            var is = getClass().getResourceAsStream(ruta);
            if (is != null) {
                return new Image(is);
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar imagen semáforo: " + ruta);
        }
        return null;
    }

    /**
     * Cambia el estado del semáforo y actualiza la imagen.
     */
    public void setEstado(Estado estado) {
        this.estadoActual = estado;
        switch (estado) {
            case APAGADO:
                imageView.setImage(imgApagado);
                break;
            case ROJO:
                imageView.setImage(imgRojo);
                break;
            case VERDE:
                imageView.setImage(imgVerde);
                break;
        }
    }

    public Estado getEstado() {
        return estadoActual;
    }

    /**
     * Configura el tamaño del semáforo (cuadrado: ancho = alto).
     */
    public void configurarTamano(double lado) {
        setMinSize(lado, lado);
        setPrefSize(lado, lado);
        setMaxSize(lado, lado);
        imageView.setFitWidth(lado);
        imageView.setFitHeight(lado);
    }
}

