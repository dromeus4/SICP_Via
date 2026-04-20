package framework.teclado;

import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Gestiona el archivo de configuracion persistente junto al ejecutable.
 * Mantiene el mapeo de teclado bajo prefijos "teclado.*" para convivir
 * con otras configuraciones del sistema en el mismo archivo.
 */
public final class ConfigSistemaRepository {

    private static final String ARCHIVO_CONFIG = "sicpvia-config.properties";
    private static final String PREFIJO_TECLADO = "teclado.";

    public ConfigTecladoIndustrial cargarTeclado(ConfigTecladoIndustrial porDefecto) {
        Properties props = cargarProperties();
        ConfigTecladoIndustrial.Builder builder = ConfigTecladoIndustrial.builder();

        for (TeclaEspecial teclaEspecial : TeclaEspecial.values()) {
            String clave = PREFIJO_TECLADO + teclaEspecial.name();
            String valor = props.getProperty(clave);
            if (valor == null) {
                String claveLegacy = claveLegacy(teclaEspecial);
                if (claveLegacy != null) {
                    valor = props.getProperty(claveLegacy);
                }
            }

            DescritorTecla descritor = parseDescritor(valor);
            if (descritor == null) {
                descritor = porDefecto.getDescritorTecla(teclaEspecial);
            }

            builder.mapear(teclaEspecial, descritor);
        }

        return builder.build();
    }

    public void guardarTeclado(ConfigTecladoIndustrial configTeclado) {
        Properties props = cargarProperties();

        for (TeclaEspecial teclaEspecial : TeclaEspecial.values()) {
            DescritorTecla descritor = configTeclado.getDescritorTecla(teclaEspecial);
            props.setProperty(PREFIJO_TECLADO + teclaEspecial.name(), descritor.toString());
        }

        props.putIfAbsent("sistema.version", "1");

        Path ruta = rutaArchivoConfig();
        try {
            Files.createDirectories(ruta.getParent());
            try (OutputStream out = Files.newOutputStream(ruta)) {
                props.store(out, "Configuracion de SICP Via");
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar configuracion en " + ruta, e);
        }
    }

    public Path rutaArchivoConfig() {
        return resolverDirectorioBase().resolve(ARCHIVO_CONFIG);
    }

    private Properties cargarProperties() {
        Properties props = new Properties();
        Path ruta = rutaArchivoConfig();

        if (!Files.exists(ruta)) {
            return props;
        }

        try (InputStream in = Files.newInputStream(ruta)) {
            props.load(in);
            return props;
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer configuracion en " + ruta, e);
        }
    }

    private DescritorTecla parseDescritor(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        try {
            return DescritorTecla.fromString(valor.trim());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String claveLegacy(TeclaEspecial teclaEspecial) {
        String nombre = teclaEspecial.name();
        if (nombre.startsWith("CATEGORIA_")) {
            return PREFIJO_TECLADO + nombre.replace("CATEGORIA_", "GRANDE_");
        }
        return null;
    }

    private Path resolverDirectorioBase() {
        // 1. Si existe la property del sistema "sicpvia.config.dir", usarla
        String configDir = System.getProperty("sicpvia.config.dir");
        if (configDir != null && !configDir.isBlank()) {
            return Paths.get(configDir).toAbsolutePath();
        }

        // 2. Intentar ubicar el directorio junto al ejecutable/JAR
        try {
            URL location = ConfigSistemaRepository.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation();

            if (location != null && "file".equals(location.getProtocol())) {
                Path path = Paths.get(location.toURI()).toAbsolutePath();
                if (Files.isRegularFile(path)) {
                    Path parent = path.getParent();
                    return parent != null ? parent : Paths.get(System.getProperty("user.dir"));
                }
                return path;
            }
        } catch (URISyntaxException | RuntimeException ex) {
            // ignorar y usar fallback
        }

        // 3. Fallback: directorio de trabajo actual
        return Paths.get(System.getProperty("user.dir")).toAbsolutePath();
    }
}

