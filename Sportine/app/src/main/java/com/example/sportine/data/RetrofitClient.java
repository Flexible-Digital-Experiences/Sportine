// Ubicación: com/example/sportine/data/RetrofitClient.java
package com.example.sportine.data;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    // IP PARA MI CELULAR --EMMANUEL--
    //public static final String BASE_URL = "http://192.168.100.5:8080/";

    // IP PARA EMULADOR
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    /**
     * Obtiene el cliente de Retrofit.
     * AHORA NECESITA EL CONTEXTO para poder crear el Interceptor.
     */
    public static Retrofit getClient(Context context) {

        // Solo creamos el cliente una vez (Singleton)
        if (retrofit == null) {

            // 1. Crear el Interceptor, pasándole el Contexto
            AuthInterceptor authInterceptor = new AuthInterceptor(context);

            // 2. Crear un cliente de OkHttp y AÑADIRLE el interceptor
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .build();

            // 3. Construir Retrofit USANDO ese cliente de OkHttp
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient) // <-- ¡CAMBIO CLAVE!
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}