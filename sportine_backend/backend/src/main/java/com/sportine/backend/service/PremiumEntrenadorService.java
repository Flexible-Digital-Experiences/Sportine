package com.sportine.backend.service;

/**
 * Servicio para gestionar la lógica de suscripciones premium de entrenadores
 */
public interface PremiumEntrenadorService{

    /**
     * Downgrade masivo de coaches con premium expirado
     * Este método es llamado por el scheduler diariamente
     *
     * @return cantidad de coaches actualizados
     */
    int downgradearCoachesPremiumExpirados();

    /**
     * Downgrade individual de un coach específico
     *
     * @param usuario identificador del coach
     */
    void downgradearCoachIndividual(String usuario);

    /**
     * Verificar si un coach tiene premium activo o está en periodo de gracia
     *
     * @param usuario identificador del coach
     * @return true si tiene acceso premium, false si no
     */
    boolean tieneAccesoPremium(String usuario);

    /**
     * Obtener días restantes de premium para un coach
     *
     * @param usuario identificador del coach
     * @return días restantes, 0 si ya expiró, -1 si no tiene premium
     */
    int obtenerDiasRestantesPremium(String usuario);
}