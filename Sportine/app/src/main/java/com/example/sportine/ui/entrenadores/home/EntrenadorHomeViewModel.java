package com.example.sportine.ui.entrenadores.home;

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
import com.example.sportine.models.AlumnoProgresoDTO;
import com.example.sportine.models.HomeEntrenadorDTO;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EntrenadorHomeViewModel extends AndroidViewModel {

    private final ApiService apiService;
    private final SharedPreferences sharedPreferences;

    private final MutableLiveData<String> saludo = new MutableLiveData<>();
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();
    private final MutableLiveData<List<AlumnoProgresoDTO>> alumnos = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public EntrenadorHomeViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.getClient(application).create(ApiService.class);
        sharedPreferences = application.getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);
    }

    public LiveData<String> getSaludo() { return saludo; }
    public LiveData<String> getMensaje() { return mensaje; }
    public LiveData<List<AlumnoProgresoDTO>> getAlumnos() { return alumnos; }

    public void cargarDatos() {
        String usuario = sharedPreferences.getString("USER_USERNAME", null);
        if (usuario == null) return;

        isLoading.setValue(true);
        apiService.obtenerHomeEntrenador().enqueue(new Callback<HomeEntrenadorDTO>() {
            @Override
            public void onResponse(Call<HomeEntrenadorDTO> call, Response<HomeEntrenadorDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    saludo.setValue(response.body().getSaludo());
                    mensaje.setValue(response.body().getMensajeDinamico());
                    alumnos.setValue(response.body().getAlumnos());
                }
            }

            @Override
            public void onFailure(Call<HomeEntrenadorDTO> call, Throwable t) {
                isLoading.setValue(false);
                Log.e("EntrenadorHomeVM", "Error cargando home", t);
            }
        });
    }
}