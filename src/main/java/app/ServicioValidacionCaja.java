package app;

/**
 * Simula la validacion contra un web service de caja.
 *
 * Regla temporal:
 * - Apertura normal: clave de cajero >= 4 caracteres.
 * - Apertura emergencia: clave >= 4 y numero de bolsa numerico >= 1.
 */
public final class ServicioValidacionCaja {

    public ResultadoValidacion validarAperturaNormal(String claveCajero) {
        if (claveCajero == null || claveCajero.isBlank()) {
            return ResultadoValidacion.error("Debes ingresar la clave de cajero.");
        }
        if (claveCajero.trim().length() < 4) {
            return ResultadoValidacion.error("Clave de cajero inválida.");
        }
        return ResultadoValidacion.ok();
    }

    public ResultadoValidacion validarAperturaEmergencia(String claveCajero, String numeroBolsa) {
        ResultadoValidacion base = validarAperturaNormal(claveCajero);
        if (!base.permitido()) {
            return base;
        }

        if (numeroBolsa == null || numeroBolsa.isBlank()) {
            return ResultadoValidacion.error("Debes ingresar el número de bolsa.");
        }

        try {
            int bolsa = Integer.parseInt(numeroBolsa.trim());
            if (bolsa <= 0) {
                return ResultadoValidacion.error("Número de bolsa inválido.");
            }
        } catch (NumberFormatException ex) {
            return ResultadoValidacion.error("Número de bolsa debe ser numérico.");
        }

        return ResultadoValidacion.ok();
    }

    public ResultadoValidacion validarDesbloqueoUsuario(String claveUsuario) {
        if (claveUsuario == null || claveUsuario.isBlank()) {
            return ResultadoValidacion.error("Debes ingresar la clave de usuario.");
        }
        if (claveUsuario.trim().length() < 4) {
            return ResultadoValidacion.error("Clave de usuario inválida.");
        }
        return ResultadoValidacion.ok();
    }

    public ResultadoValidacion validarMantenimientoUsuario(String claveUsuario) {
        if (claveUsuario == null || claveUsuario.isBlank()) {
            return ResultadoValidacion.error("Debes ingresar la clave de usuario para mantenimiento.");
        }
        if (claveUsuario.trim().length() < 4) {
            return ResultadoValidacion.error("Clave de usuario inválida para mantenimiento.");
        }
        return ResultadoValidacion.ok();
    }

    public record ResultadoValidacion(boolean permitido, String mensajeError) {
        public static ResultadoValidacion ok() {
            return new ResultadoValidacion(true, "");
        }

        public static ResultadoValidacion error(String mensaje) {
            return new ResultadoValidacion(false, mensaje);
        }
    }
}

