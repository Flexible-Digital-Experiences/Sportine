package com.example.sportine.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor de OkHttp que automáticamente añade el Token JWT
 * (guardado en SharedPreferences) a cada petición.
 */
public class AuthInterceptor implements Interceptor {

    private Context context;

    public AuthInterceptor(Context context) {
        // Necesitamos el Context para poder leer las SharedPreferences
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        // 1. Obtener la petición original
        Request originalRequest = chain.request();

        // 2. Leer el token guardado
        SharedPreferences prefs = context.getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("USER_TOKEN", null);

        // 3. Si NO hay token (ej. el usuario está en Login),
        //    simplemente dejamos pasar la petición original.
        if (token == null) {
            return chain.proceed(originalRequest);
        }

        // 4. Si SÍ hay token, construimos una nueva petición con el Header
        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token) // <-- ¡La magia!
                .build();

        // 5. Dejamos pasar la NUEVA petición (con el token)
        return chain.proceed(newRequest);
    }
}