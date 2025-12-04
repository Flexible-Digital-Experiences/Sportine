package com.example.sportine.ui.entrenadores.estadisticas;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sportine.data.ApiService;
import com.example.sportine.models.AlumnoCardStatsDTO;
import com.example.sportine.models.DetalleEstadisticasAlumnoDTO;
import com.example.sportine.models.FeedbackPromedioDTO;
import com.example.sportine.models.SportsDistributionDTO;
import com.example.sportine.models.TrainingFrequencyDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel para el módulo de Estadísticas del Entrenador.
 * Maneja dos vistas principales:
 * 1. Lista de alumnos con métricas resumidas
 * 2. Detalle completo de un alumno específico
 */
public class EstadisticasEntrenadorViewModel extends ViewModel {

    private static final String TAG = "EstadisticasEntrenadorVM";

    // ApiService
    private ApiService apiService;

    // ==========================================
    // LISTA DE ALUMNOS (Vista Principal)
    // ==========================================

    private final MutableLiveData<List<AlumnoCardStatsDTO>> _listaAlumnos = new MutableLiveData<>();
    public final LiveData<List<AlumnoCardStatsDTO>> listaAlumnos = _listaAlumnos;

    // ==========================================
    // DETALLE DE ALUMNO ESPECÍFICO
    // ==========================================

    private final MutableLiveData<DetalleEstadisticasAlumnoDTO> _detalleAlumno = new MutableLiveData<>();
    public final LiveData<DetalleEstadisticasAlumnoDTO> detalleAlumno = _detalleAlumno;

    private final MutableLiveData<TrainingFrequencyDTO> _frecuenciaAlumno = new MutableLiveData<>();
    public final LiveData<TrainingFrequencyDTO> frecuenciaAlumno = _frecuenciaAlumno;

    private final MutableLiveData<SportsDistributionDTO> _distribucionAlumno = new MutableLiveData<>();
    public final LiveData<SportsDistributionDTO> distribucionAlumno = _distribucionAlumno;

    private final MutableLiveData<FeedbackPromedioDTO> _feedbackAlumno = new MutableLiveData<>();
    public final LiveData<FeedbackPromedioDTO> feedbackAlumno = _feedbackAlumno;

    // ==========================================
    // ESTADOS GENERALES
    // ==========================================

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;

    // Variable para almacenar el usuario del alumno seleccionado
    private String alumnoSeleccionado = null;

    // Constructor vacío
    public EstadisticasEntrenadorViewModel() {
        // ApiService se inicializará desde el Fragment
    }

    /**
     * Inicializa el ApiService desde el Fragment.
     * Debe llamarse antes de cargar datos.
     */
    public void inicializarApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    // ==========================================
    // MÉTODOS PARA LISTA DE ALUMNOS
    // ==========================================

    /**
     * Carga la lista de todos los alumnos del entrenador con sus métricas.
     * GET /api/entrenador/estadisticas/alumnos
     */
    public void cargarListaAlumnos() {
        _isLoading.setValue(true);

        apiService.obtenerResumenAlumnos().enqueue(new Callback<List<AlumnoCardStatsDTO>>() {
            @Override
            public void onResponse(Call<List<AlumnoCardStatsDTO>> call, Response<List<AlumnoCardStatsDTO>> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _listaAlumnos.setValue(response.body());
                    Log.d(TAG, "Lista de alumnos cargada: " + response.body().size() + " alumnos");
                } else {
                    _error.setValue("Error al cargar alumnos: " + response.code());
                    Log.e(TAG, "Error en respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<AlumnoCardStatsDTO>> call, Throwable t) {
                _isLoading.setValue(false);
                _error.setValue("Error de conexión: " + t.getMessage());
                Log.e(TAG, "Error al cargar alumnos", t);
            }
        });
    }

    /**
     * Refresca la lista de alumnos.
     */
    public void refrescarListaAlumnos() {
        Log.d(TAG, "Refrescando lista de alumnos...");
        cargarListaAlumnos();
    }

    // ==========================================
    // MÉTODOS PARA DETALLE DE ALUMNO
    // ==========================================

    /**
     * Carga el detalle completo de un alumno específico.
     * GET /api/entrenador/estadisticas/alumno/{usuarioAlumno}
     *
     * @param usuarioAlumno Username del alumno
     */
    public void cargarDetalleAlumno(String usuarioAlumno) {
        this.alumnoSeleccionado = usuarioAlumno;
        _isLoading.setValue(true);

        apiService.obtenerDetalleEstadisticasAlumno(usuarioAlumno).enqueue(new Callback<DetalleEstadisticasAlumnoDTO>() {
            @Override
            public void onResponse(Call<DetalleEstadisticasAlumnoDTO> call, Response<DetalleEstadisticasAlumnoDTO> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _detalleAlumno.setValue(response.body());
                    Log.d(TAG, "Detalle de alumno cargado: " + usuarioAlumno);
                } else {
                    _error.setValue("Error al cargar detalle: " + response.code());
                    Log.e(TAG, "Error en respuesta detalle: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DetalleEstadisticasAlumnoDTO> call, Throwable t) {
                _isLoading.setValue(false);
                _error.setValue("Error de conexión: " + t.getMessage());
                Log.e(TAG, "Error al cargar detalle", t);
            }
        });
    }

    /**
     * Carga la frecuencia de entrenamientos de un alumno específico.
     * GET /api/entrenador/estadisticas/alumno/{usuarioAlumno}/frequency?period=WEEK|MONTH|YEAR
     *
     * @param usuarioAlumno Username del alumno
     * @param periodo       "WEEK", "MONTH", o "YEAR"
     */
    public void cargarFrecuenciaAlumno(String usuarioAlumno, String periodo) {
        _isLoading.setValue(true);

        apiService.obtenerFrecuenciaAlumno(usuarioAlumno, periodo).enqueue(new Callback<TrainingFrequencyDTO>() {
            @Override
            public void onResponse(Call<TrainingFrequencyDTO> call, Response<TrainingFrequencyDTO> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _frecuenciaAlumno.setValue(response.body());
                    Log.d(TAG, "Frecuencia del alumno cargada: " + periodo);
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
     * Carga la distribución de deportes de un alumno específico.
     * GET /api/entrenador/estadisticas/alumno/{usuarioAlumno}/sports
     *
     * @param usuarioAlumno Username del alumno
     */
    public void cargarDistribucionAlumno(String usuarioAlumno) {
        _isLoading.setValue(true);

        apiService.obtenerDistribucionDeportesAlumno(usuarioAlumno).enqueue(new Callback<SportsDistributionDTO>() {
            @Override
            public void onResponse(Call<SportsDistributionDTO> call, Response<SportsDistributionDTO> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _distribucionAlumno.setValue(response.body());
                    Log.d(TAG, "Distribución del alumno cargada");
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
     * Carga el feedback promedio de un alumno específico.
     * GET /api/entrenador/estadisticas/alumno/{usuarioAlumno}/feedback
     *
     * @param usuarioAlumno Username del alumno
     */
    public void cargarFeedbackAlumno(String usuarioAlumno) {
        _isLoading.setValue(true);

        apiService.obtenerFeedbackAlumno(usuarioAlumno).enqueue(new Callback<FeedbackPromedioDTO>() {
            @Override
            public void onResponse(Call<FeedbackPromedioDTO> call, Response<FeedbackPromedioDTO> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _feedbackAlumno.setValue(response.body());
                    Log.d(TAG, "Feedback del alumno cargado");
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
     * Carga todas las estadísticas de un alumno (detalle completo + gráficas).
     * Útil para la pantalla de detalle.
     *
     * @param usuarioAlumno Username del alumno
     */
    public void cargarEstadisticasCompletasAlumno(String usuarioAlumno) {
        cargarDetalleAlumno(usuarioAlumno);
        cargarFrecuenciaAlumno(usuarioAlumno, "MONTH");
        cargarDistribucionAlumno(usuarioAlumno);
        cargarFeedbackAlumno(usuarioAlumno);
    }

    /**
     * Refresca las estadísticas del alumno actualmente seleccionado.
     */
    public void refrescarEstadisticasAlumno() {
        if (alumnoSeleccionado != null) {
            Log.d(TAG, "Refrescando estadísticas del alumno: " + alumnoSeleccionado);
            cargarEstadisticasCompletasAlumno(alumnoSeleccionado);
        }
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    /**
     * Reinicia el estado de error.
     */
    public void clearError() {
        _error.setValue(null);
    }

    /**
     * Limpia los datos del alumno seleccionado (útil al volver a la lista).
     */
    public void limpiarDetalleAlumno() {
        alumnoSeleccionado = null;
        _detalleAlumno.setValue(null);
        _frecuenciaAlumno.setValue(null);
        _distribucionAlumno.setValue(null);
        _feedbackAlumno.setValue(null);
    }

    /**
     * Obtiene el username del alumno actualmente seleccionado.
     */
    public String getAlumnoSeleccionado() {
        return alumnoSeleccionado;
    }
}