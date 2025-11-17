// Ubicación: com/example/sportine/data/RetrofitClient.java
package com.example.sportine.data;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.TimeZone;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    // IP PARA MI CELULAR --EMMANUEL--
    public static final String BASE_URL = "http://192.168.100.5:8080/";

    // IP PARA EMULADOR
    //private static final String BASE_URL = "http://10.0.2.2:8080/";

    /**
     * Obtiene el cliente de Retrofit.
     * AHORA NECESITA EL CONTEXTO para poder crear el Interceptor.
     */
    public static Retrofit getClient(Context context) {

        if (retrofit == null) {

            // (El Interceptor se queda igual)
            AuthInterceptor authInterceptor = new AuthInterceptor(context);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .build();


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create()) // <-- ¡El default!
                    .build();
        }
        return retrofit;
    }
}