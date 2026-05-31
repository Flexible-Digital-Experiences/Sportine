package com.sportine.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtro de Rate Limiting para el endpoint de login.
 *
 * Protege contra ataques de fuerza bruta bloqueando IPs que
 * superen 5 intentos fallidos de login en menos de 1 minuto.
 *
 * Cómo funciona:
 * - Por cada petición POST a /api/usuarios/login, registra la IP.
 * - Si la IP tiene 5 o más intentos en el último minuto → HTTP 429.
 * - Si el login es exitoso (backend responde 200) → limpia el contador.
 * - Los contadores se limpian automáticamente después de 1 minuto.
 *
 * Notas de implementación:
 * - Usa ConcurrentHashMap para ser thread-safe (múltiples requests simultáneos).
 * - En producción con varios servidores se reemplazaría por Redis,
 *   pero para un entorno académico de un solo servidor es suficiente.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Máximo de intentos permitidos por IP en la ventana de tiempo
    private static final int MAX_INTENTOS = 5;

    // Ventana de tiempo en milisegundos (1 minuto)
    private static final long VENTANA_MS = 60_000;

    // Mapa: IP → datos del intento (timestamp del primero + contador)
    private final Map<String, DatosIntento> intentosPorIp = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Solo aplica a POST /api/usuarios/login
        return !(request.getMethod().equals("POST") &&
                request.getRequestURI().equals("/api/usuarios/login"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = obtenerIp(request);
        long ahora = System.currentTimeMillis();

        DatosIntento datos = intentosPorIp.get(ip);

        // Si no hay registro previo o ya pasó el minuto → reiniciar
        if (datos == null || (ahora - datos.primerIntento) > VENTANA_MS) {
            datos = new DatosIntento(ahora);
            intentosPorIp.put(ip, datos);
        }

        // Verificar si ya superó el límite ANTES de procesar
        if (datos.contador >= MAX_INTENTOS) {
            long tiempoRestante = VENTANA_MS - (ahora - datos.primerIntento);
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"mensaje\": \"Demasiados intentos. Espera " +
                            (tiempoRestante / 1000) + " segundos antes de intentar de nuevo.\"}"
            );
            return; // Cortocircuito — no pasa al siguiente filtro
        }

        // Incrementar contador ANTES de procesar (cuenta el intento actual)
        datos.contador++;

        // Usar wrapper para poder leer el status DESPUÉS de que los filtros corran
        StatusCapturandoResponse responseWrapper = new StatusCapturandoResponse(response);
        filterChain.doFilter(request, responseWrapper);

        // Si el login fue exitoso → limpiar el contador de esa IP
        if (responseWrapper.getStatus() == 200) {
            intentosPorIp.remove(ip);
        }
    }

    /**
     * Obtiene la IP real del cliente.
     * Revisa X-Forwarded-For primero por si hay un proxy/nginx adelante.
     */
    private String obtenerIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    // ── Clases internas ──────────────────────────────────────────

    private static class DatosIntento {
        long primerIntento;
        int contador;

        DatosIntento(long primerIntento) {
            this.primerIntento = primerIntento;
            this.contador = 0;
        }
    }

    /**
     * Wrapper de HttpServletResponse que nos permite leer el status
     * después de que la cadena de filtros termine.
     */
    private static class StatusCapturandoResponse
            extends jakarta.servlet.http.HttpServletResponseWrapper {

        private int status = 200;

        public StatusCapturandoResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setStatus(int sc) {
            this.status = sc;
            super.setStatus(sc);
        }

        @Override
        public void sendError(int sc) throws IOException {
            this.status = sc;
            super.sendError(sc);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            this.status = sc;
            super.sendError(sc, msg);
        }

        public int getStatus() {
            return status;
        }
    }
}