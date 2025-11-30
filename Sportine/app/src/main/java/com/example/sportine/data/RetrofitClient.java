package com.example.sportine.data;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    // IP PARA MI CELULAR --EMMANUEL
    //public static final String BASE_URL = "http://192.168.100.5:8080/";

    //IP PARA ESCUELA --EMMANUEL--
    //private static final String BASE_URL = "https://noncommodious-ingrid-geomorphologic.ngrok-free.dev/";

    // IP PARA CELULAR --JP--
    public static final String BASE_URL = "http://192.168.1.75:8080/";

    //IP para Escuela Alonso
    //public static final String BASE_URL = "https://jason-waterworn-kaysen.ngrok-free.dev/";
    // IP para grok (escuela)
    // public static  final String BASE_URL = "https://chasmal-plastometric-isabell.ngrok-free.dev/";

    // IP PARA EMULADOR
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    public static Retrofit getClient(Context context) {

        if (retrofit == null) {

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                        @Override
                        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            String fechaTexto = json.getAsString();

                            try {
                                return parsearFecha(fechaTexto, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                            } catch (Exception e1) {}

                            try {
                                return parsearFecha(fechaTexto, "yyyy-MM-dd'T'HH:mm:ss.SSS");
                            } catch (Exception e2) {}

                            try {
                                return parsearFecha(fechaTexto, "yyyy-MM-dd'T'HH:mm:ss");
                            } catch (Exception e3) {}

                            return null;
                        }

                        // Método helper para probar formatos forzando UTC
                        private Date parsearFecha(String fecha, String patron) throws ParseException {
                            SimpleDateFormat format = new SimpleDateFormat(patron, Locale.US);
                            format.setTimeZone(TimeZone.getTimeZone("UTC"));
                            return format.parse(fecha);
                        }
                    })
                    .create();

            AuthInterceptor authInterceptor = new AuthInterceptor(context);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)

                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}