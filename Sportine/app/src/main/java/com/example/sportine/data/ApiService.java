package com.example.sportine.data;

import com.example.sportine.models.RespuestaRegistro;
import com.example.sportine.models.Usuario;
import com.example.sportine.ui.usuarios.dto.LoginRequest;
import com.example.sportine.ui.usuarios.dto.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {


    // 1. Descomentado
    // 2. URL corregida para que coincida con el @RequestMapping de Spring
    @POST("/api/usuarios/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);


    @POST("/api/usuarios/registrar")
    Call<RespuestaRegistro> registrarUsuario(@Body Usuario usuario);

}