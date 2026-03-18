package com.sportine.backend.service;

public interface SuscripcionRecurrenteService {
    int marcarSuscripcionesExpiradas();
    int procesarCancelacionesPendientes();
}