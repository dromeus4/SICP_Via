package framework.teclado;

/**
 * Mini harness para validar rapidamente el mapeo de teclas especiales.
 */
public final class TecladoIndustrialSelfTest {

    private TecladoIndustrialSelfTest() {
    }

    public static void main(String[] args) {
        ConfigTecladoIndustrial config = ConfigTecladoIndustrial.porDefecto();
        int totalMapeadas = config.getMapeoEspeciales().size();

        if (totalMapeadas != 19) {
            throw new IllegalStateException("Se esperaban 19 teclas especiales, pero hay " + totalMapeadas);
        }

        for (TeclaEspecial teclaEspecial : TeclaEspecial.values()) {
            if (config.getDescritorTecla(teclaEspecial) == null) {
                throw new IllegalStateException("La tecla especial no esta mapeada: " + teclaEspecial);
            }
        }

        System.out.println("OK - mapeo de teclas especiales: " + totalMapeadas);
    }
}

