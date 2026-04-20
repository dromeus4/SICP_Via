package framework.teclado;

import javafx.scene.input.KeyCode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Mapea KeyCode de JavaFX a teclas especiales del teclado industrial.
 *
 * Nota: los codigos exactos pueden variar segun firmware/controlador del
 * teclado industrial. Si hace falta, ajusta este mapeo en un solo lugar.
 */
public final class ConfigTecladoIndustrial {

    private final Map<DescritorTecla, TeclaEspecial> mapeoEspeciales;
    private final Map<TeclaEspecial, DescritorTecla> mapeoPorTecla;

    private ConfigTecladoIndustrial(Map<TeclaEspecial, DescritorTecla> mapeoPorTecla) {
        EnumMap<TeclaEspecial, DescritorTecla> copiaPorTecla = new EnumMap<>(TeclaEspecial.class);
        copiaPorTecla.putAll(mapeoPorTecla);

        Map<DescritorTecla, TeclaEspecial> copiaInversa = new java.util.HashMap<>();
        for (Map.Entry<TeclaEspecial, DescritorTecla> entry : copiaPorTecla.entrySet()) {
            copiaInversa.put(entry.getValue(), entry.getKey());
        }

        this.mapeoPorTecla = Collections.unmodifiableMap(copiaPorTecla);
        this.mapeoEspeciales = Collections.unmodifiableMap(copiaInversa);
    }

    public static ConfigTecladoIndustrial porDefecto() {
        Builder builder = builder();

        // 10 teclas de categoria (Shift+F1 al Shift+F10).
        builder.mapear(TeclaEspecial.CATEGORIA_1, KeyCode.F1, false, true, false);
        builder.mapear(TeclaEspecial.CATEGORIA_2, KeyCode.F2, false, true, false);
        builder.mapear(TeclaEspecial.CATEGORIA_3, KeyCode.F3, false, true, false);
        builder.mapear(TeclaEspecial.CATEGORIA_4, KeyCode.F4, false, true, false);
        builder.mapear(TeclaEspecial.CATEGORIA_5, KeyCode.F5, false, true, false);
        builder.mapear(TeclaEspecial.CATEGORIA_6, KeyCode.F6, false, true, false);
        builder.mapear(TeclaEspecial.CATEGORIA_7, KeyCode.F7, false, true, false);
        builder.mapear(TeclaEspecial.CATEGORIA_8, KeyCode.F8, false, true, false);
        builder.mapear(TeclaEspecial.CATEGORIA_9, KeyCode.F9, false, true, false);
        builder.mapear(TeclaEspecial.CATEGORIA_10, KeyCode.F10, false, true, false);

        // 9 teclas especiales funcionales.
        builder.mapear(TeclaEspecial.ROJO, KeyCode.F8, true, false, false);       // Ctrl+F8
        builder.mapear(TeclaEspecial.VERDE, KeyCode.F9, true, false, false);      // Ctrl+F9
        builder.mapear(TeclaEspecial.EFECTIVO, KeyCode.F1, true, false, false);   // Ctrl+F1
        builder.mapear(TeclaEspecial.POS, KeyCode.F2, true, false, false);        // Ctrl+F2
        builder.mapear(TeclaEspecial.BOMBERO, KeyCode.F4, true, false, false);    // Ctrl+F4
        builder.mapear(TeclaEspecial.AMBULANCIA, KeyCode.F3, true, false, false); // Ctrl+F3
        builder.mapear(TeclaEspecial.POLICIA, KeyCode.F5, true, false, false);    // Ctrl+F5
        builder.mapear(TeclaEspecial.EXENTO, KeyCode.F6, true, false, false);     // Ctrl+F6
        builder.mapear(TeclaEspecial.NOMINAR, KeyCode.F7, true, false, false);    // Ctrl+F7

        return builder.build();
    }

    public TeclaEspecial buscarTeclaEspecial(DescritorTecla descritor) {
        return mapeoEspeciales.get(descritor);
    }

    public boolean esTeclaEspecial(DescritorTecla descritor) {
        return mapeoEspeciales.containsKey(descritor);
    }

    public Map<DescritorTecla, TeclaEspecial> getMapeoEspeciales() {
        return mapeoEspeciales;
    }

    public DescritorTecla getDescritorTecla(TeclaEspecial teclaEspecial) {
        return mapeoPorTecla.get(teclaEspecial);
    }

    public Map<TeclaEspecial, DescritorTecla> getMapeoPorTecla() {
        return mapeoPorTecla;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final EnumMap<TeclaEspecial, DescritorTecla> mapeo = new EnumMap<>(TeclaEspecial.class);

        public Builder mapear(TeclaEspecial teclaEspecial, KeyCode keyCode) {
            return mapear(teclaEspecial, keyCode, false, false, false);
        }

        public Builder mapear(TeclaEspecial teclaEspecial, DescritorTecla descritor) {
            Objects.requireNonNull(teclaEspecial, "teclaEspecial no puede ser null");
            Objects.requireNonNull(descritor, "descritor no puede ser null");

            // Si otra tecla ya usaba ese descritor, se desasigna para mantener unicidad.
            mapeo.entrySet().removeIf(entry -> entry.getValue().equals(descritor) && entry.getKey() != teclaEspecial);
            mapeo.put(teclaEspecial, descritor);
            return this;
        }

        public Builder mapear(TeclaEspecial teclaEspecial, KeyCode keyCode, boolean ctrl, boolean shift, boolean alt) {
            return mapear(teclaEspecial, new DescritorTecla(keyCode, ctrl, shift, alt));
        }

        public ConfigTecladoIndustrial build() {
            validarCoberturaCompleta();
            return new ConfigTecladoIndustrial(mapeo);
        }

        private void validarCoberturaCompleta() {
            for (TeclaEspecial teclaEspecial : TeclaEspecial.values()) {
                if (!mapeo.containsKey(teclaEspecial)) {
                    throw new IllegalStateException("Falta mapear la tecla especial: " + teclaEspecial);
                }
            }
        }
    }
}

