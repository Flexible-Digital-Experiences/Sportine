package com.sportine.backend.service;

public interface SeguidoresService {

    String toggleSeguirUsuario(String miUsuario, String usuarioObjetivo);

    boolean loSigo(String miUsuario, String usuarioObjetivo);
}