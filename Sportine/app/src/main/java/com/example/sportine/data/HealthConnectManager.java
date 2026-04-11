package com.example.sportine.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.health.connect.client.HealthConnectClient;
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord;
import androidx.health.connect.client.records.DistanceRecord;
import androidx.health.connect.client.records.ElevationGainedRecord;
import androidx.health.connect.client.records.ExerciseSessionRecord;
import androidx.health.connect.client.records.HeartRateRecord;
import androidx.health.connect.client.records.SpeedRecord;
import androidx.health.connect.client.records.StepsRecord;
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord;

import com.example.sportine.models.healthconnect.HcSesionEjercicio;

import com.example.sportine.models.healthconnect.HealthConnectBridge;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Manager para interactuar con Health Connect desde Java.
 * Delega todas las llamadas suspend a HealthConnectBridge.kt,
 * que sí puede instanciar CoroutineScope y usar KClass.
 */
public class HealthConnectManager {

    private static final String TAG = "HealthConnectManager";
    private static final int TIMEOUT_SEG = 15;

    public static final Set<String> PERMISOS_REQUERIDOS = new HashSet<>(Arrays.asList(
            "android.permission.health.READ_EXERCISE",
            "android.permission.health.READ_HEART_RATE",
            "android.permission.health.READ_STEPS",
            "android.permission.health.READ_DISTANCE",
            "android.permission.health.READ_ACTIVE_CALORIES_BURNED",
            "android.permission.health.READ_SPEED",
            "android.permission.health.READ_ELEVATION_GAINED"
    ));

    private final Context context;
    private final Handler mainHandler;
    private HealthConnectClient client;

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public HealthConnectManager(Context context) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    // ══════════════════════════════════════════════════════════════════
    // DISPONIBILIDAD
    // ══════════════════════════════════════════════════════════════════

    public boolean isDisponible() {
        return HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE;
    }

    public boolean necesitaActualizacion() {
        return HealthConnectClient.getSdkStatus(context)
                == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED;
    }

    private HealthConnectClient getClient() {
        if (client == null) {
            client = HealthConnectClient.getOrCreate(context);
        }
        return client;
    }

    // ══════════════════════════════════════════════════════════════════
    // PERMISOS — via Bridge
    // ══════════════════════════════════════════════════════════════════

    public void tieneTodosLosPermisos(Callback<Boolean> callback) {
        if (!isDisponible()) {
            mainHandler.post(() -> callback.onSuccess(false));
            return;
        }

        HealthConnectBridge.getGrantedPermissions(getClient(),
                new HealthConnectBridge.OnResult<Set<String>>() {
                    @Override
                    public void onSuccess(Set<String> otorgados) {
                        boolean tiene = otorgados.containsAll(PERMISOS_REQUERIDOS);
                        mainHandler.post(() -> callback.onSuccess(tiene));
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Error verificando permisos HC", e);
                        mainHandler.post(() -> callback.onError(e));
                    }
                });
    }

    // ══════════════════════════════════════════════════════════════════
    // LEER SESIONES — via Bridge
    // ══════════════════════════════════════════════════════════════════

    public void leerSesionesDeHoy(Callback<List<HcSesionEjercicio>> callback) {
        if (!isDisponible()) {
            mainHandler.post(() -> callback.onError(new Exception("Health Connect no disponible")));
            return;
        }

        ZoneId zona = ZoneId.systemDefault();
        Instant inicio = LocalDate.now().minusDays(7).atStartOfDay(zona).toInstant();
        Instant fin = Instant.now();

        HealthConnectBridge.readExerciseSessions(getClient(), inicio, fin,
                new HealthConnectBridge.OnResult<List<ExerciseSessionRecord>>() {
                    @Override
                    public void onSuccess(List<ExerciseSessionRecord> sesiones) {
                        new Thread(() -> {
                            List<HcSesionEjercicio> resultado = new ArrayList<>();
                            for (ExerciseSessionRecord sesion : sesiones) {
                                HcSesionEjercicio hc = new HcSesionEjercicio();
                                hc.setSesionId(sesion.getMetadata().getId());
                                hc.setTipoEjercicio(sesion.getExerciseType());
                                hc.setHoraInicio(sesion.getStartTime());
                                hc.setHoraFin(sesion.getEndTime());
                                long durMs = sesion.getEndTime().toEpochMilli()
                                        - sesion.getStartTime().toEpochMilli();
                                hc.setDuracionActivaMin((int) (durMs / 60000));
                                enricherConMetricas(hc, sesion.getStartTime(), sesion.getEndTime());
                                resultado.add(hc);
                            }
                            mainHandler.post(() -> callback.onSuccess(resultado));
                        }).start();
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Error leyendo sesiones HC", e);
                        mainHandler.post(() -> callback.onError(e));
                    }
                });
    }

    public void leerSesionesDeFecha(LocalDate fecha,
                                    Callback<List<HcSesionEjercicio>> callback) {
        if (!isDisponible()) {
            mainHandler.post(() ->
                    callback.onError(new Exception("Health Connect no disponible")));
            return;
        }

        ZoneId zona = ZoneId.systemDefault();
        Instant inicio = fecha.atStartOfDay(zona).toInstant();
        Instant fin = fecha.plusDays(1).atStartOfDay(zona).toInstant();

        HealthConnectBridge.readExerciseSessions(getClient(), inicio, fin,
                new HealthConnectBridge.OnResult<List<ExerciseSessionRecord>>() {
                    @Override
                    public void onSuccess(List<ExerciseSessionRecord> sesiones) {
                        // Enriquecer cada sesión con sus métricas en un hilo background
                        new Thread(() -> {
                            List<HcSesionEjercicio> resultado = new ArrayList<>();
                            for (ExerciseSessionRecord sesion : sesiones) {
                                HcSesionEjercicio hc = new HcSesionEjercicio();
                                hc.setSesionId(sesion.getMetadata().getId());
                                hc.setTipoEjercicio(sesion.getExerciseType());
                                hc.setHoraInicio(sesion.getStartTime());
                                hc.setHoraFin(sesion.getEndTime());

                                long durMs = sesion.getEndTime().toEpochMilli()
                                        - sesion.getStartTime().toEpochMilli();
                                hc.setDuracionActivaMin((int) (durMs / 60000));

                                enricherConMetricas(hc,
                                        sesion.getStartTime(), sesion.getEndTime());
                                resultado.add(hc);
                            }
                            mainHandler.post(() -> callback.onSuccess(resultado));
                        }).start();
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Error leyendo sesiones HC", e);
                        mainHandler.post(() -> callback.onError(e));
                    }
                });
    }

    // ══════════════════════════════════════════════════════════════════
    // ENRIQUECER MÉTRICAS — bloquea en hilo background usando latch
    // ══════════════════════════════════════════════════════════════════

    /**
     * Llama al Bridge de forma bloqueante (via CountDownLatch).
     * Solo llamar desde un hilo que NO sea el Main Thread.
     */
    private void enricherConMetricas(HcSesionEjercicio sesion,
                                     Instant inicio, Instant fin) {
        // Frecuencia cardíaca
        try {
            List<HeartRateRecord> registros = llamarSync(latch -> {
                HealthConnectBridge.readHeartRate(getClient(), inicio, fin,
                        new HealthConnectBridge.OnResult<List<HeartRateRecord>>() {
                            @Override public void onSuccess(List<HeartRateRecord> r) {
                                latch.result.set(r); latch.latch.countDown();
                            }
                            @Override public void onError(Exception e) {
                                latch.latch.countDown();
                            }
                        });
            });
            if (registros != null) {
                double suma = 0; long max = 0; int total = 0;
                for (HeartRateRecord r : registros) {
                    for (HeartRateRecord.Sample s : r.getSamples()) {
                        suma += s.getBeatsPerMinute();
                        max = Math.max(max, s.getBeatsPerMinute());
                        total++;
                    }
                }
                if (total > 0) {
                    sesion.setFcPromedio((int) (suma / total));
                    sesion.setFcMaxima((int) max);
                }
            }
        } catch (Exception e) { Log.w(TAG, "Sin FC: " + e.getMessage()); }

        // Pasos
        try {
            List<StepsRecord> registros = llamarSync(latch ->
                    HealthConnectBridge.readSteps(getClient(), inicio, fin,
                            new HealthConnectBridge.OnResult<List<StepsRecord>>() {
                                @Override public void onSuccess(List<StepsRecord> r) {
                                    latch.result.set(r); latch.latch.countDown();
                                }
                                @Override public void onError(Exception e) {
                                    latch.latch.countDown();
                                }
                            }));
            if (registros != null) {
                long total = 0;
                for (StepsRecord r : registros) total += r.getCount();
                if (total > 0) sesion.setPasos((int) total);
            }
        } catch (Exception e) { Log.w(TAG, "Sin pasos: " + e.getMessage()); }

        // Distancia
        try {
            List<DistanceRecord> registros = llamarSync(latch ->
                    HealthConnectBridge.readDistance(getClient(), inicio, fin,
                            new HealthConnectBridge.OnResult<List<DistanceRecord>>() {
                                @Override public void onSuccess(List<DistanceRecord> r) {
                                    latch.result.set(r); latch.latch.countDown();
                                }
                                @Override public void onError(Exception e) {
                                    latch.latch.countDown();
                                }
                            }));
            if (registros != null) {
                double total = 0;
                for (DistanceRecord r : registros) total += r.getDistance().getMeters();
                if (total > 0) sesion.setDistanciaMetros(total);
            }
        } catch (Exception e) { Log.w(TAG, "Sin distancia: " + e.getMessage()); }

        // Calorías
        try {
            List<ActiveCaloriesBurnedRecord> registros = llamarSync(latch ->
                    HealthConnectBridge.readCalories(getClient(), inicio, fin,
                            new HealthConnectBridge.OnResult<List<ActiveCaloriesBurnedRecord>>() {
                                @Override public void onSuccess(List<ActiveCaloriesBurnedRecord> r) {
                                    latch.result.set(r); latch.latch.countDown();
                                }
                                @Override public void onError(Exception e) {
                                    latch.latch.countDown();
                                }
                            }));
            if (registros != null) {
                double total = 0;
                for (ActiveCaloriesBurnedRecord r : registros)
                    total += r.getEnergy().getKilocalories();
                if (total > 0) sesion.setCaloriasKcal((int) total);
            }
        } catch (Exception e) { Log.w(TAG, "Sin calorías: " + e.getMessage()); }

        // Velocidad
        try {
            List<SpeedRecord> registros = llamarSync(latch ->
                    HealthConnectBridge.readSpeed(getClient(), inicio, fin,
                            new HealthConnectBridge.OnResult<List<SpeedRecord>>() {
                                @Override public void onSuccess(List<SpeedRecord> r) {
                                    latch.result.set(r); latch.latch.countDown();
                                }
                                @Override public void onError(Exception e) {
                                    latch.latch.countDown();
                                }
                            }));
            if (registros != null) {
                double suma = 0; int total = 0;
                for (SpeedRecord r : registros) {
                    for (SpeedRecord.Sample s : r.getSamples()) {
                        suma += s.getSpeed().getMetersPerSecond();
                        total++;
                    }
                }
                if (total > 0) sesion.setVelocidadPromedioMs(suma / total);
            }
        } catch (Exception e) { Log.w(TAG, "Sin velocidad: " + e.getMessage()); }

        // Elevación
        try {
            List<ElevationGainedRecord> registros = llamarSync(latch ->
                    HealthConnectBridge.readElevation(getClient(), inicio, fin,
                            new HealthConnectBridge.OnResult<List<ElevationGainedRecord>>() {
                                @Override public void onSuccess(List<ElevationGainedRecord> r) {
                                    latch.result.set(r); latch.latch.countDown();
                                }
                                @Override public void onError(Exception e) {
                                    latch.latch.countDown();
                                }
                            }));
            if (registros != null) {
                double total = 0;
                for (ElevationGainedRecord r : registros)
                    total += r.getElevation().getMeters();
                if (total > 0) sesion.setElevacionGanadaMetros(total);
            }
        } catch (Exception e) { Log.w(TAG, "Sin elevación: " + e.getMessage()); }
        // Calorías totales (fallback si no hay activas)
        if (sesion.getCaloriasKcal() == null || sesion.getCaloriasKcal() == 0) {
            try {
                List<TotalCaloriesBurnedRecord> registros = llamarSync(latch ->
                        HealthConnectBridge.readTotalCalories(getClient(), inicio, fin,
                                new HealthConnectBridge.OnResult<List<TotalCaloriesBurnedRecord>>() {
                                    @Override public void onSuccess(List<TotalCaloriesBurnedRecord> r) {
                                        latch.result.set(r); latch.latch.countDown();
                                    }
                                    @Override public void onError(Exception e) {
                                        latch.latch.countDown();
                                    }
                                }));
                if (registros != null) {
                    double total = 0;
                    for (TotalCaloriesBurnedRecord r : registros)
                        total += r.getEnergy().getKilocalories();
                    if (total > 0) sesion.setCaloriasKcal((int) total);
                }
            } catch (Exception e) { Log.w(TAG, "Sin calorías totales: " + e.getMessage()); }
        }
        // Fallback: calcular velocidad desde distancia y duración si no hay SpeedRecord
        if ((sesion.getVelocidadPromedioMs() == null || sesion.getVelocidadPromedioMs() == 0)
                && sesion.getDistanciaMetros() != null && sesion.getDistanciaMetros() > 0
                && sesion.getDuracionActivaMin() > 0) {
            double velocidadCalculada = sesion.getDistanciaMetros() / (sesion.getDuracionActivaMin() * 60.0);
            sesion.setVelocidadPromedioMs(velocidadCalculada);
            Log.d(TAG, "Velocidad calculada desde distancia/duración: " + velocidadCalculada + " m/s");
        }
    }

    // ── Helper para convertir callback async en llamada bloqueante ────────────

    interface LatchCallback<T> {
        void call(LatchHolder<T> latch);
    }

    static class LatchHolder<T> {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<T> result = new AtomicReference<>(null);
    }

    private <T> T llamarSync(LatchCallback<T> fn) throws Exception {
        LatchHolder<T> holder = new LatchHolder<>();
        fn.call(holder);
        holder.latch.await(TIMEOUT_SEG, TimeUnit.SECONDS);
        return holder.result.get();
    }
}