package com.example.sportine.ui.usuarios.home;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.HomeAlumnoDTO;
import com.example.sportine.models.EntrenamientoDelDiaDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel para el Home del Alumno
 * Maneja la lógica de negocio y los datos de la pantalla
 */
public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = "HomeViewModel";
    private final ApiService apiService;
    private final SharedPreferences sharedPreferences;

    // LiveData para los datos del home
    private final MutableLiveData<String> saludo = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeDinamico = new MutableLiveData<>();
    private final MutableLiveData<List<EntrenamientoDelDiaDTO>> entrenamientos = new MutableLiveData<>();

    // LiveData para el estado de carga y errores
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);

        // Inicializar ApiService
        apiService = RetrofitClient.getClient(application).create(ApiService.class);

        // ⭐ CORREGIDO: Usar el mismo nombre de SharedPreferences que LoginActivity
        sharedPreferences = application.getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);
    }

    // Getters para LiveData (exponer datos a la UI)
    public LiveData<String> getSaludo() {
        return saludo;
    }

    public LiveData<String> getMensajeDinamico() {
        return mensajeDinamico;
    }

    public LiveData<List<EntrenamientoDelDiaDTO>> getEntrenamientos() {
        return entrenamientos;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Carga los datos del home desde el backend
     */
    public void cargarHomeAlumno() {
        // ⭐ CORREGIDO: Usar la misma key que LoginActivity ("USER_USERNAME")
        String usuario = sharedPreferences.getString("USER_USERNAME", null);

        if (usuario == null || usuario.isEmpty()) {
            Log.e(TAG, "No se pudo obtener el usuario de SharedPreferences");
            errorMessage.setValue("No se pudo obtener el usuario. Por favor, inicia sesión de nuevo.");
            return;
        }

        Log.d(TAG, "Cargando home del alumno: " + usuario);

        isLoading.setValue(true);
        errorMessage.setValue(null); // Limpiar errores previos

        // Llamada al backend
        Call<HomeAlumnoDTO> call = apiService.obtenerHomeAlumno(usuario);
        call.enqueue(new Callback<HomeAlumnoDTO>() {
            @Override
            public void onResponse(Call<HomeAlumnoDTO> call, Response<HomeAlumnoDTO> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    HomeAlumnoDTO data = response.body();

                    Log.d(TAG, "✅ Datos recibidos correctamente");
                    Log.d(TAG, "Saludo: " + data.getSaludo());
                    Log.d(TAG, "Mensaje: " + data.getMensajeDinamico());

                    if (data.getEntrenamientosDelDia() != null) {
                        Log.d(TAG, "Entrenamientos: " + data.getEntrenamientosDelDia().size());
                    } else {
                        Log.w(TAG, "No hay entrenamientos para mostrar");
                    }

                    // Actualizar LiveData
                    saludo.setValue(data.getSaludo());
                    mensajeDinamico.setValue(data.getMensajeDinamico());
                    entrenamientos.setValue(data.getEntrenamientosDelDia());

                } else {
                    // Error en la respuesta
                    String error = "Error al cargar datos del home";
                    Log.e(TAG, "Error " + response.code() + ": " + response.message());

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al leer errorBody", e);
                    }

                    errorMessage.setValue(error);
                }
            }

            @Override
            public void onFailure(Call<HomeAlumnoDTO> call, Throwable t) {
                isLoading.setValue(false);
                String error = "Error de conexión: " + t.getMessage();
                Log.e(TAG, error, t);
                errorMessage.setValue(error);
            }
        });
    }

    /**
     * Método para refrescar los datos (útil para SwipeRefreshLayout)
     */
    public void refrescarDatos() {
        cargarHomeAlumno();
    }
}