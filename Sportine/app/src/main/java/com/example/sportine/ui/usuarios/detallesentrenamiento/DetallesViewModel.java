package com.example.sportine.ui.usuarios.detallesentrenamiento;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.CompletarEntrenamientoRequestDTO;
import com.example.sportine.models.DetalleEntrenamientoDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetallesViewModel extends AndroidViewModel {

    private final ApiService apiService;
    private final MutableLiveData<DetalleEntrenamientoDTO> detalle = new MutableLiveData<>();
    private final MutableLiveData<Boolean> entrenamientoCompletado = new MutableLiveData<>(); // Nuevo
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public DetallesViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.getClient(application).create(ApiService.class);
    }

    public LiveData<DetalleEntrenamientoDTO> getDetalle() { return detalle; }
    public LiveData<Boolean> getEntrenamientoCompletado() { return entrenamientoCompletado; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    // ... (Tu método cargarDetalles sigue igual) ...
    public void cargarDetalles(Integer idEntrenamiento) {
        isLoading.setValue(true);
        apiService.obtenerDetalleEntrenamiento(idEntrenamiento).enqueue(new Callback<DetalleEntrenamientoDTO>() {
            @Override
            public void onResponse(Call<DetalleEntrenamientoDTO> call, Response<DetalleEntrenamientoDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    detalle.setValue(response.body());
                } else {
                    errorMessage.setValue("Error al cargar detalles");
                }
            }
            @Override
            public void onFailure(Call<DetalleEntrenamientoDTO> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    // ... (Tu método cambiarEstadoEjercicio sigue igual) ...
    public void cambiarEstadoEjercicio(Integer idAsignado, boolean completado) {
        apiService.cambiarEstadoEjercicio(idAsignado, completado).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) Log.e("DetallesVM", "Error al actualizar estado");
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("DetallesVM", "Fallo de red");
            }
        });
    }

    // ✅ NUEVO MÉTODO: COMPLETAR CON FEEDBACK
    public void completarEntrenamiento(Integer id, String comentario, int cansancio, int dificultad, String animo) {
        isLoading.setValue(true);

        CompletarEntrenamientoRequestDTO request = new CompletarEntrenamientoRequestDTO(
                id, comentario, cansancio, dificultad, animo
        );

        apiService.completarEntrenamiento(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    entrenamientoCompletado.setValue(true);
                } else {
                    errorMessage.setValue("Error al completar el entrenamiento");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Fallo de conexión al completar");
            }
        });
    }
}