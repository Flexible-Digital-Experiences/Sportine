package com.example.sportine.data;

import com.example.sportine.ui.usuarios.dto.LoginRequest;
import com.example.sportine.ui.usuarios.dto.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // Esto le dice a Retrofit: "Cuando llame a 'login', haz un POST a la URL '/auth/login'"
    @POST("/auth/login") // <-- Esta URL debe coincidir con la de tu Controller de Spring Boot
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // Aquí pondrás tus otras 4 cosas (GET, POST, PUT, DELETE)
    // Ej: @GET("/api/social/feed")
    //     Call<List<Post>> getSocialFeed();
}