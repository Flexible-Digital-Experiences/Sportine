package com.example.sportine.data;

// ¡Imports de tus modelos y DTOs reales!
import com.example.sportine.models.Publicacion;
import com.example.sportine.models.RespuestaRegistro;
import com.example.sportine.models.Usuario;
import com.example.sportine.models.PublicacionFeedDTO; // <-- ¡NUEVO IMPORT!
import com.example.sportine.ui.usuarios.dto.LoginRequest;
import com.example.sportine.ui.usuarios.dto.LoginResponse;
import com.example.sportine.ui.usuarios.dto.PublicacionRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // --- MÓDULO USUARIOS ---
    @POST("/api/usuarios/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("/api/usuarios/registrar")
    Call<RespuestaRegistro> registrarUsuario(@Body Usuario usuario);

    @GET("/api/social/feed")
    Call<List<PublicacionFeedDTO>> getSocialFeed();

    @POST("/api/social/post")
    Call<Publicacion> crearPost(@Body PublicacionRequest postRequest);

    @POST("/api/social/post/{id}/like")
    Call<Void> darLike(@Path("id") Integer idPublicacion);

    @DELETE("/api/social/post/{id}/like")
    Call<Void> quitarLike(@Path("id") Integer idPublicacion);
}