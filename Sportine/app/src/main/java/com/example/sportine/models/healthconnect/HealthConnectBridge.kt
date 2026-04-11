package com.example.sportine.models.healthconnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * Puente Kotlin que expone funciones suspend de Health Connect
 * como llamadas con callbacks, accesibles desde Java.
 *
 * Java no puede instanciar CoroutineScope ni usar KClass directamente,
 * así que este archivo actúa como adaptador.
 */
object HealthConnectBridge {

    interface OnResult<T> {
        fun onSuccess(result: T)
        fun onError(e: Exception)
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    // ── Permisos ────────────────────────────────────────────────────────────

    @JvmStatic
    fun getGrantedPermissions(
        client: HealthConnectClient,
        callback: OnResult<Set<String>>
    ) {
        scope.launch {
            try {
                val perms = client.permissionController.getGrantedPermissions()
                callback.onSuccess(perms)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    // ── Leer sesiones de ejercicio ──────────────────────────────────────────

    @JvmStatic
    fun readExerciseSessions(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant,
        callback: OnResult<List<ExerciseSessionRecord>>
    ) {
        scope.launch {
            try {
                val response = client.readRecords(
                    ReadRecordsRequest(
                        recordType = ExerciseSessionRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                    )
                )
                // ── LOG TEMPORAL DE DIAGNÓSTICO ──
                android.util.Log.d("HCBridge", "Total sesiones encontradas: ${response.records.size}")
                response.records.forEach { record ->
                    android.util.Log.d("HCBridge", "Sesión: tipo=${record.exerciseType} " +
                            "inicio=${record.startTime} " +
                            "app=${record.metadata.dataOrigin.packageName}")
                }
                // ────────────────────────────────
                callback.onSuccess(response.records)
            } catch (e: Exception) {
                android.util.Log.e("HCBridge", "Error leyendo sesiones: ${e.message}")
                callback.onError(e)
            }
        }
    }

    // ── Leer frecuencia cardíaca ────────────────────────────────────────────

    @JvmStatic
    fun readHeartRate(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant,
        callback: OnResult<List<HeartRateRecord>>
    ) {
        scope.launch {
            try {
                val response = client.readRecords(
                    ReadRecordsRequest(
                        HeartRateRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                    )
                )
                callback.onSuccess(response.records)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    // ── Leer pasos ──────────────────────────────────────────────────────────

    @JvmStatic
    fun readSteps(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant,
        callback: OnResult<List<StepsRecord>>
    ) {
        scope.launch {
            try {
                val response = client.readRecords(
                    ReadRecordsRequest(
                        StepsRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                    )
                )
                callback.onSuccess(response.records)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    // ── Leer distancia ──────────────────────────────────────────────────────

    @JvmStatic
    fun readDistance(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant,
        callback: OnResult<List<DistanceRecord>>
    ) {
        scope.launch {
            try {
                val response = client.readRecords(
                    ReadRecordsRequest(
                        DistanceRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                    )
                )
                callback.onSuccess(response.records)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    // ── Leer calorías activas ───────────────────────────────────────────────

    @JvmStatic
    fun readCalories(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant,
        callback: OnResult<List<ActiveCaloriesBurnedRecord>>
    ) {
        scope.launch {
            try {
                val response = client.readRecords(
                    ReadRecordsRequest(
                        ActiveCaloriesBurnedRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                    )
                )
                callback.onSuccess(response.records)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    // ── Leer velocidad ──────────────────────────────────────────────────────

    @JvmStatic
    fun readSpeed(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant,
        callback: OnResult<List<SpeedRecord>>
    ) {
        scope.launch {
            try {
                val response = client.readRecords(
                    ReadRecordsRequest(
                        SpeedRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                    )
                )
                callback.onSuccess(response.records)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    // ── Leer elevación ──────────────────────────────────────────────────────

    @JvmStatic
    fun readElevation(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant,
        callback: OnResult<List<ElevationGainedRecord>>
    ) {
        scope.launch {
            try {
                val response = client.readRecords(
                    ReadRecordsRequest(
                        ElevationGainedRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                    )
                )
                callback.onSuccess(response.records)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    @JvmStatic
    fun readTotalCalories(
        client: HealthConnectClient,
        startTime: Instant,
        endTime: Instant,
        callback: OnResult<List<TotalCaloriesBurnedRecord>>
    ) {
        scope.launch {
            try {
                val response = client.readRecords(
                    ReadRecordsRequest(
                        TotalCaloriesBurnedRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                    )
                )
                callback.onSuccess(response.records)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }
}