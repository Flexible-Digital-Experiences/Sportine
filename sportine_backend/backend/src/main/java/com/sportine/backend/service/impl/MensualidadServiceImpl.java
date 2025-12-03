package com.sportine.backend.service.impl;

import com.sportine.backend.model.EntrenadorAlumno;
import com.sportine.backend.repository.EntrenadorAlumnoRepository;
import com.sportine.backend.service.MensualidadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MensualidadServiceImpl implements MensualidadService {

    private static final Logger logger = LoggerFactory.getLogger(MensualidadServiceImpl.class);

    @Autowired
    private EntrenadorAlumnoRepository entrenadorAlumnoRepository;

    @Override
    @Transactional
    public int actualizarMensualidadesVencidas() {
        LocalDate hoy = LocalDate.now();

        logger.info("Iniciando actualización de mensualidades vencidas para la fecha: {}", hoy);

        try {
            // Opción A: Actualización en lote (más eficiente)
            int relacionesActualizadas = entrenadorAlumnoRepository
                    .actualizarMensualidadesVencidasEnLote(hoy);

            logger.info("Se actualizaron {} relaciones a 'pendiente'", relacionesActualizadas);
            return relacionesActualizadas;

            /* Opción B: Actualización individual (si necesitas lógica adicional por cada registro)
            List<EntrenadorAlumno> relacionesVencidas =
                entrenadorAlumnoRepository.findByStatusRelacionAndFinMensualidadBefore("activo", hoy);

            for (EntrenadorAlumno relacion : relacionesVencidas) {
                relacion.setStatusRelacion("pendiente");
                entrenadorAlumnoRepository.save(relacion);

                // Aquí podrías agregar lógica adicional:
                // - Enviar notificación al alumno
                // - Registrar en una tabla de historial
                // - etc.
            }

            logger.info("Se actualizaron {} relaciones a 'pendiente'", relacionesVencidas.size());
            return relacionesVencidas.size();
            */

        } catch (Exception e) {
            logger.error("Error al actualizar mensualidades vencidas: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void verificarYActualizarMensualidad(Integer idRelacion) {
        EntrenadorAlumno relacion = entrenadorAlumnoRepository.findById(idRelacion)
                .orElseThrow(() -> new IllegalArgumentException("Relación no encontrada con ID: " + idRelacion));

        LocalDate hoy = LocalDate.now();

        // Si está activo pero la mensualidad venció, cambiar a pendiente
        if ("activo".equals(relacion.getStatusRelacion()) &&
                relacion.getFinMensualidad() != null &&
                relacion.getFinMensualidad().isBefore(hoy)) {

            logger.info("Actualizando relación {} a 'pendiente'. Fecha vencimiento: {}",
                    idRelacion, relacion.getFinMensualidad());

            relacion.setStatusRelacion("pendiente");
            entrenadorAlumnoRepository.save(relacion);
        }
    }

    @Override
    public List<EntrenadorAlumno> obtenerMensualidadesPorVencer(int dias) {
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = LocalDate.now().plusDays(dias);

        // Usar el método del repository que ya creamos
        return entrenadorAlumnoRepository.encontrarMensualidadesPorVencer(fechaInicio, fechaFin);
    }


    @Override
    @Transactional
    public EntrenadorAlumno renovarMensualidad(Integer idRelacion, int diasExtension) {
        EntrenadorAlumno relacion = entrenadorAlumnoRepository.findById(idRelacion)
                .orElseThrow(() -> new IllegalArgumentException("Relación no encontrada con ID: " + idRelacion));

        LocalDate nuevaFechaVencimiento;

        // Si está vencida, extender desde HOY
        if (relacion.getFinMensualidad() == null ||
                relacion.getFinMensualidad().isBefore(LocalDate.now())) {
            nuevaFechaVencimiento = LocalDate.now().plusDays(diasExtension);
        } else {
            // Si aún no vence, extender desde la fecha actual de vencimiento
            nuevaFechaVencimiento = relacion.getFinMensualidad().plusDays(diasExtension);
        }

        relacion.setFinMensualidad(nuevaFechaVencimiento);
        relacion.setStatusRelacion("activo");

        logger.info("Mensualidad renovada. Relación: {}, Nueva fecha: {}",
                idRelacion, nuevaFechaVencimiento);

        return entrenadorAlumnoRepository.save(relacion);
    }
}