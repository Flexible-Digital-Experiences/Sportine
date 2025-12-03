package com.example.sportine.ui.usuarios.estadisticas;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.FeedbackPromedioDTO;
import com.example.sportine.models.SportsDistributionDTO;
import com.example.sportine.models.StatisticsOverviewDTO;
import com.example.sportine.models.StreakInfoDTO;
import com.example.sportine.models.TrainingFrequencyDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel para el módulo de Estadísticas del Alumno.
 * Maneja todas las llamadas a la API y expone los datos mediante LiveData.
 */
public class EstadisticasViewModel extends ViewModel {

    private static final String TAG = "EstadisticasViewModel";

    // ApiService
    private ApiService apiService;

    // LiveData para el resumen general
    private final MutableLiveData<StatisticsOverviewDTO> _resumenGeneral = new MutableLiveData<>();
    public final LiveData<StatisticsOverviewDTO> resumenGeneral = _resumenGeneral;

    // LiveData para la frecuencia de entrenamientos
    private final MutableLiveData<TrainingFrequencyDTO> _frecuenciaEntrenamientos = new MutableLiveData<>();
    public final LiveData<TrainingFrequencyDTO> frecuenciaEntrenamientos = _frecuenciaEntrenamientos;

    // LiveData para la distribución de deportes
    private final MutableLiveData<SportsDistributionDTO> _distribucionDeportes = new MutableLiveData<>();
    public final LiveData<SportsDistributionDTO> distribucionDeportes = _distribucionDeportes;

    // LiveData para la información de racha
    private final MutableLiveData<StreakInfoDTO> _infoRacha = new MutableLiveData<>();
    public final LiveData<StreakInfoDTO> infoRacha = _infoRacha;

    // LiveData para el feedback promedio
    private final MutableLiveData<FeedbackPromedioDTO> _feedbackPromedio = new MutableLiveData<>();
    public final LiveData<FeedbackPromedioDTO> feedbackPromedio = _feedbackPromedio;

    // LiveData para estados de carga
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;

    // LiveData para errores
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;

    // Constructor vacío
    public EstadisticasViewModel() {
        // ApiService se inicializará usando setApiService()
    }

    /**
     * Inicializa el ApiService desde el Fragment.
     * Debe llamarse antes de cargar datos.
     */
    public void inicializarApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Carga todas las estadísticas del alumno.
     * Llama a todos los endpoints necesarios.
     */
    public void cargarEstadisticasCompletas() {
        cargarResumenGeneral();
        cargarFrecuenciaEntrenamientos("MONTH");
        cargarDistribucionDeportes();
        cargarInformacionRacha();
        cargarFeedbackPromedio();
    }

    /**
     * Carga el resumen general de estadísticas.
     * GET /api/alumno/estadisticas/overview
     */
    public void cargarResumenGeneral() {
        _isLoading.setValue(true);

        apiService.obtenerResumenEstadisticas().enqueue(new Callback<StatisticsOverviewDTO>() {
            @Override
            public void onResponse(Call<StatisticsOverviewDTO> call, Response<StatisticsOverviewDTO> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _resumenGeneral.setValue(response.body());
                    Log.d(TAG, "Resumen general cargado exitosamente");
                } else {
                    _error.setValue("Error al cargar resumen: " + response.code());
                    Log.e(TAG, "Error en respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StatisticsOverviewDTO> call, Throwable t) {
                _isLoading.setValue(false);
                _error.setValue("Error de conexión: " + t.getMessage());
                Log.e(TAG, "Error al cargar resumen", t);
            }
        });
    }

    /**
     * Carga la frecuencia de entrenamientos.
     * GET /api/alumno/estadisticas/frequency?period=WEEK|MONTH|YEAR
     *
     * @param periodo "WEEK", "MONTH", o "YEAR"
     */
    public void cargarFrecuenciaEntrenamientos(String periodo) {
        _isLoading.setValue(true);

        apiService.obtenerFrecuenciaEntrenamientos(periodo).enqueue(new Callback<TrainingFrequencyDTO>() {
            @Override
            public void onResponse(Call<TrainingFrequencyDTO> call, Response<TrainingFrequencyDTO> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _frecuenciaEntrenamientos.setValue(response.body());
                    Log.d(TAG, "Frecuencia cargada: " + periodo);
                } else {
                    _error.setValue("Error al cargar frecuencia: " + response.code());
                    Log.e(TAG, "Error en respuesta frecuencia: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TrainingFrequencyDTO> call, Throwable t) {
                _isLoading.setValue(false);
                _error.setValue("Error de conexión: " + t.getMessage());
                Log.e(TAG, "Error al cargar frecuencia", t);
            }
        });
    }

    /**
     * Carga la distribución de deportes.
     * GET /api/alumno/estadisticas/sports-distribution
     */
    public void cargarDistribucionDeportes() {
        _isLoading.setValue(true);

        apiService.obtenerDistribucionDeportes().enqueue(new Callback<SportsDistributionDTO>() {
            @Override
            public void onResponse(Call<SportsDistributionDTO> call, Response<SportsDistributionDTO> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _distribucionDeportes.setValue(response.body());
                    Log.d(TAG, "Distribución de deportes cargada");
                } else {
                    _error.setValue("Error al cargar deportes: " + response.code());
                    Log.e(TAG, "Error en respuesta deportes: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SportsDistributionDTO> call, Throwable t) {
                _isLoading.setValue(false);
                _error.setValue("Error de conexión: " + t.getMessage());
                Log.e(TAG, "Error al cargar deportes", t);
            }
        });
    }

    /**
     * Carga la información de racha.
     * GET /api/alumno/estadisticas/streak
     */
    public void cargarInformacionRacha() {
        _isLoading.setValue(true);

        apiService.obtenerInformacionRacha().enqueue(new Callback<StreakInfoDTO>() {
            @Override
            public void onResponse(Call<StreakInfoDTO> call, Response<StreakInfoDTO> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _infoRacha.setValue(response.body());
                    Log.d(TAG, "Información de racha cargada");
                } else {
                    _error.setValue("Error al cargar racha: " + response.code());
                    Log.e(TAG, "Error en respuesta racha: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StreakInfoDTO> call, Throwable t) {
                _isLoading.setValue(false);
                _error.setValue("Error de conexión: " + t.getMessage());
                Log.e(TAG, "Error al cargar racha", t);
            }
        });
    }

    /**
     * Carga el feedback promedio.
     * GET /api/alumno/estadisticas/feedback
     */
    public void cargarFeedbackPromedio() {
        _isLoading.setValue(true);

        apiService.obtenerFeedbackPromedio().enqueue(new Callback<FeedbackPromedioDTO>() {
            @Override
            public void onResponse(Call<FeedbackPromedioDTO> call, Response<FeedbackPromedioDTO> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _feedbackPromedio.setValue(response.body());
                    Log.d(TAG, "Feedback promedio cargado");
                } else {
                    _error.setValue("Error al cargar feedback: " + response.code());
                    Log.e(TAG, "Error en respuesta feedback: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<FeedbackPromedioDTO> call, Throwable t) {
                _isLoading.setValue(false);
                _error.setValue("Error de conexión: " + t.getMessage());
                Log.e(TAG, "Error al cargar feedback", t);
            }
        });
    }

    /**
     * Reinicia el estado de error.
     */
    public void clearError() {
        _error.setValue(null);
    }

    /**
     * Método para refrescar todas las estadísticas.
     */
    public void refrescarEstadisticas() {
        Log.d(TAG, "Refrescando estadísticas...");
        cargarEstadisticasCompletas();
    }
}