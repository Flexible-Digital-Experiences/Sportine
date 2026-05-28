package com.sportine.backend.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * Cliente HTTP interno para llamar al microservicio FastAPI de IA.
 *
 * Agrega automáticamente el header x-internal-secret en cada request,
 * de modo que los controllers solo llaman get() o post() sin preocuparse
 * por la autenticación con FastAPI.
 *
 * FastAPI corre en localhost:8001 (configurable en application.properties).
 * NUNCA expongas fastApiUrl o fastApiSecret al frontend ni al log de producción.
 *
 * Ubicación esperada: src/main/java/com/sportine/backend/client/FastApiClient.java
 */
@Component
@Slf4j
public class FastApiClient {

    @Value("${fastapi.url}")
    private String fastApiUrl;

    @Value("${fastapi.secret}")
    private String fastApiSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    // ─────────────────────────────────────────────────────────────
    // Construcción de headers — siempre incluye el secreto interno
    // ─────────────────────────────────────────────────────────────

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        // "x-internal-secret" — nombre exacto que valida security.py con APIKeyHeader
        headers.set("x-internal-secret", fastApiSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // ─────────────────────────────────────────────────────────────
    // GET
    // ─────────────────────────────────────────────────────────────

    /**
     * Realiza un GET autenticado al microservicio FastAPI.
     *
     * Ejemplos de path:
     *   "/sportine-score/alumno_test"
     *   "/ajuste-rutina/alumno_test?n_sesiones=5"
     *   "/prediccion-progreso/alumno_test?id_deporte=1&dias=30"
     *   "/patrones/alumno_test"
     *
     * @param path         Path relativo incluyendo query params si aplica
     * @param responseType Clase del DTO que se espera deserializar
     * @return Objeto deserializado desde el JSON de FastAPI
     */
    public <T> T get(String path, Class<T> responseType) {
        String url = fastApiUrl + path;
        log.info("→ FastAPI GET {}", url);
        try {
            HttpEntity<?> entity = new HttpEntity<>(buildHeaders());
            ResponseEntity<T> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, responseType
            );
            log.info("← FastAPI GET {} → {}", path, response.getStatusCode());
            return response.getBody();

        } catch (HttpClientErrorException e) {
            // FastAPI respondió con 4xx (ej: 404 alumno sin datos, 403 secreto inválido)
            log.error("FastAPI devolvió {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(
                    "FastAPI respondió con error " + e.getStatusCode().value() +
                            ". Verifica que el alumno tenga datos suficientes."
            );
        } catch (ResourceAccessException e) {
            // FastAPI no está corriendo o el puerto 8001 no responde
            log.error("FastAPI no disponible en {}: {}", fastApiUrl, e.getMessage());
            throw new RuntimeException(
                    "El microservicio de IA no está disponible. " +
                            "Asegúrate de que FastAPI está corriendo en el puerto 8001 (python main.py)."
            );
        }
    }

    // ─────────────────────────────────────────────────────────────
    // POST
    // ─────────────────────────────────────────────────────────────

    /**
     * Realiza un POST autenticado al microservicio FastAPI.
     *
     * Ejemplo de uso:
     *   fastApiClient.post("/recomendar-entrenadores", requestBody, RecomendacionDTO.class)
     *
     * @param path         Path relativo, ej: "/recomendar-entrenadores"
     * @param body         Objeto que se serializa como JSON en el body
     * @param responseType Clase del DTO que se espera deserializar
     * @return Objeto deserializado desde el JSON de FastAPI
     */
    public <T> T post(String path, Object body, Class<T> responseType) {
        String url = fastApiUrl + path;
        log.info("→ FastAPI POST {}", url);
        try {
            HttpEntity<Object> entity = new HttpEntity<>(body, buildHeaders());
            ResponseEntity<T> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, responseType
            );
            log.info("← FastAPI POST {} → {}", path, response.getStatusCode());
            return response.getBody();

        } catch (HttpClientErrorException e) {
            log.error("FastAPI devolvió {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(
                    "FastAPI respondió con error " + e.getStatusCode().value() +
                            ". Verifica el cuerpo del request."
            );
        } catch (ResourceAccessException e) {
            log.error("FastAPI no disponible en {}: {}", fastApiUrl, e.getMessage());
            throw new RuntimeException(
                    "El microservicio de IA no está disponible. " +
                            "Asegúrate de que FastAPI está corriendo en el puerto 8001 (python main.py)."
            );
        }
    }
}