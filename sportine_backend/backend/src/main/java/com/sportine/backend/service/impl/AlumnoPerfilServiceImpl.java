package com.sportine.backend.service.impl;

import com.sportine.backend.dto.ActualizarDatosAlumnoDTO;
import com.sportine.backend.dto.PerfilAlumnoDTO;
import com.sportine.backend.dto.PerfilAlumnoResponseDTO;
import com.sportine.backend.model.*;

import com.sportine.backend.dto.TarjetaDTO;
import com.sportine.backend.dto.TarjetaResponseDTO;
import com.sportine.backend.repository.*;
import com.sportine.backend.service.AlumnoPerfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlumnoPerfilServiceImpl implements AlumnoPerfilService {

    private final UsuarioRepository usuarioRepository;
    private final InformacionAlumnoRepository informacionAlumnoRepository;
    private final AlumnoDeporteRepository alumnoDeporteRepository;
    private final TarjetaRepository tarjetaRepository;
    private final EstadoRepository estadoRepository;
    private final NivelRepository nivelRepository;
    private final DeporteRepository deporteRepository;
    private final SeguidoresRepository seguidoresRepository;
    private final EntrenadorAlumnoRepository entrenadorAlumnoRepository;

    // ========================================
    // MÉTODO OBTENER PERFIL
    // ========================================

    @Override
    public PerfilAlumnoResponseDTO obtenerPerfilAlumno(String usuario) {

        Usuario usuarioEntity = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        InformacionAlumno infoAlumno = informacionAlumnoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Perfil de alumno no encontrado"));

        // Obtener deportes como IDs y buscar nombres manualmente
        List<AlumnoDeporte> deportesEntity = alumnoDeporteRepository.findByUsuario(usuario);
        List<PerfilAlumnoResponseDTO.DeporteConNivelDTO> deportes = deportesEntity.stream()
                .map(ad -> {
                    // Buscar el deporte por ID
                    Deporte deporte = deporteRepository.findById(ad.getIdDeporte())
                            .orElse(null);

                    // Buscar el nivel por ID
                    Nivel nivel = nivelRepository.findById(ad.getIdNivel())
                            .orElse(null);

                    return new PerfilAlumnoResponseDTO.DeporteConNivelDTO(
                            deporte != null ? deporte.getNombreDeporte() : "Desconocido",
                            nivel != null ? nivel.getNombreNivel() : "Sin nivel",
                            ad.getFechaInicio()
                    );
                })
                .collect(Collectors.toList());

        Integer edad = calcularEdad(infoAlumno.getFechaNacimiento());

        Estado estado = estadoRepository.findById(usuarioEntity.getIdEstado())
                .orElse(null);
        String nombreEstado = estado != null ? estado.getEstado() : "";

        Integer totalAmigos = seguidoresRepository.contarAmigos(usuario);
        Integer totalEntrenadores = entrenadorAlumnoRepository.contarEntrenadoresActivos(usuario);

        return new PerfilAlumnoResponseDTO(
                usuarioEntity.getUsuario(),
                usuarioEntity.getNombre(),
                usuarioEntity.getApellidos(),
                usuarioEntity.getSexo(),
                nombreEstado,
                usuarioEntity.getCiudad(),
                infoAlumno.getEstatura(),
                infoAlumno.getPeso(),
                infoAlumno.getLesiones(),
                infoAlumno.getPadecimientos(),
                infoAlumno.getFotoPerfil(),
                infoAlumno.getFechaNacimiento(),
                edad,
                deportes,
                totalAmigos,
                totalEntrenadores,
                "Perfil obtenido exitosamente"
        );
    }

    // ========================================
    // MÉTODO CREAR PERFIL - CORREGIDO
    // ========================================

    @Override
    @Transactional
    public PerfilAlumnoResponseDTO crearPerfilAlumno(PerfilAlumnoDTO dto) {

        Usuario usuario = usuarioRepository.findByUsuario(dto.getUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (informacionAlumnoRepository.existsByUsuario(dto.getUsuario())) {
            throw new RuntimeException("El alumno ya tiene un perfil creado");
        }

        // Crear perfil básico SIN deportes
        InformacionAlumno infoAlumno = new InformacionAlumno();
        infoAlumno.setUsuario(dto.getUsuario());
        infoAlumno.setEstatura(dto.getEstatura());
        infoAlumno.setPeso(dto.getPeso());
        infoAlumno.setLesiones(dto.getLesiones());
        infoAlumno.setPadecimientos(dto.getPadecimientos());
        infoAlumno.setFotoPerfil(dto.getFotoPerfil());
        infoAlumno.setFechaNacimiento(dto.getFechaNacimiento());

        informacionAlumnoRepository.save(infoAlumno);

        // Los deportes se agregan después
        Integer edad = calcularEdad(dto.getFechaNacimiento());

        Estado estado = estadoRepository.findById(usuario.getIdEstado())
                .orElse(null);
        String nombreEstado = estado != null ? estado.getEstado() : "";

        Integer totalAmigos = seguidoresRepository.contarAmigos(dto.getUsuario());
        Integer totalEntrenadores = entrenadorAlumnoRepository.contarEntrenadoresActivos(dto.getUsuario());

        // ========================================
        // CORRECCIÓN: Lista vacía del tipo correcto
        // ========================================
        return new PerfilAlumnoResponseDTO(
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getSexo(),
                nombreEstado,
                usuario.getCiudad(),
                infoAlumno.getEstatura(),
                infoAlumno.getPeso(),
                infoAlumno.getLesiones(),
                infoAlumno.getPadecimientos(),
                infoAlumno.getFotoPerfil(),
                infoAlumno.getFechaNacimiento(),
                edad,
                new ArrayList<>(),  // ✅ Lista vacía de DeporteConNivelDTO
                totalAmigos,
                totalEntrenadores,
                "Perfil de alumno creado exitosamente"
        );
    }

    // ========================================
    // MÉTODO ACTUALIZAR PERFIL - CORREGIDO
    // ========================================

    @Override
    @Transactional
    public PerfilAlumnoResponseDTO actualizarPerfilAlumno(String usuario, PerfilAlumnoDTO dto) {

        Usuario usuarioEntity = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        InformacionAlumno infoAlumno = informacionAlumnoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Perfil de alumno no encontrado"));

        // Actualizar datos básicos
        infoAlumno.setEstatura(dto.getEstatura());
        infoAlumno.setPeso(dto.getPeso());
        infoAlumno.setLesiones(dto.getLesiones());
        infoAlumno.setPadecimientos(dto.getPadecimientos());
        infoAlumno.setFotoPerfil(dto.getFotoPerfil());
        infoAlumno.setFechaNacimiento(dto.getFechaNacimiento());

        informacionAlumnoRepository.save(infoAlumno);

        // Obtener deportes usando IDs
        List<AlumnoDeporte> deportesEntity = alumnoDeporteRepository.findByUsuario(usuario);
        List<PerfilAlumnoResponseDTO.DeporteConNivelDTO> deportes = deportesEntity.stream()
                .map(ad -> {
                    // Buscar el deporte por ID
                    Deporte deporte = deporteRepository.findById(ad.getIdDeporte())
                            .orElse(null);

                    // Buscar el nivel por ID
                    Nivel nivel = nivelRepository.findById(ad.getIdNivel())
                            .orElse(null);

                    return new PerfilAlumnoResponseDTO.DeporteConNivelDTO(
                            deporte != null ? deporte.getNombreDeporte() : "Desconocido",
                            nivel != null ? nivel.getNombreNivel() : "Sin nivel",
                            ad.getFechaInicio()
                    );
                })
                .collect(Collectors.toList());

        Integer edad = calcularEdad(dto.getFechaNacimiento());

        Estado estado = estadoRepository.findById(usuarioEntity.getIdEstado())
                .orElse(null);
        String nombreEstado = estado != null ? estado.getEstado() : "";

        Integer totalAmigos = seguidoresRepository.contarAmigos(usuario);
        Integer totalEntrenadores = entrenadorAlumnoRepository.contarEntrenadoresActivos(usuario);

        return new PerfilAlumnoResponseDTO(
                usuarioEntity.getUsuario(),
                usuarioEntity.getNombre(),
                usuarioEntity.getApellidos(),
                usuarioEntity.getSexo(),
                nombreEstado,
                usuarioEntity.getCiudad(),
                infoAlumno.getEstatura(),
                infoAlumno.getPeso(),
                infoAlumno.getLesiones(),
                infoAlumno.getPadecimientos(),
                infoAlumno.getFotoPerfil(),
                infoAlumno.getFechaNacimiento(),
                edad,
                deportes,
                totalAmigos,
                totalEntrenadores,
                "Perfil actualizado exitosamente"
        );
    }

    // ========================================
    // MÉTODO ACTUALIZAR DATOS PARCIALES
    // ========================================

    @Override
    @Transactional
    public void actualizarDatosAlumno(String usuario, ActualizarDatosAlumnoDTO datosDTO) {

        System.out.println("=== INICIANDO ACTUALIZACIÓN DE DATOS PARCIALES ===");
        System.out.println("Usuario: " + usuario);
        System.out.println("Datos recibidos: " + datosDTO);

        Usuario usuarioEntity = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuario));

        System.out.println("✓ Usuario encontrado: " + usuarioEntity.getNombre());

        InformacionAlumno infoAlumno = informacionAlumnoRepository
                .findByUsuario(usuario)
                .orElseGet(() -> {
                    System.out.println("⚠ No existe información del alumno, creando nueva entrada...");
                    InformacionAlumno nuevaInfo = new InformacionAlumno();
                    nuevaInfo.setUsuario(usuario);
                    return nuevaInfo;
                });

        boolean huboActualizacion = false;

        if (datosDTO.getEstatura() != null) {
            System.out.println("✓ Actualizando estatura: " + datosDTO.getEstatura());
            infoAlumno.setEstatura(datosDTO.getEstatura());
            huboActualizacion = true;
        }

        if (datosDTO.getPeso() != null) {
            System.out.println("✓ Actualizando peso: " + datosDTO.getPeso());
            infoAlumno.setPeso(datosDTO.getPeso());
            huboActualizacion = true;
        }

        if (datosDTO.getLesiones() != null && !datosDTO.getLesiones().trim().isEmpty()) {
            System.out.println("✓ Actualizando lesiones: " + datosDTO.getLesiones());
            infoAlumno.setLesiones(datosDTO.getLesiones());
            huboActualizacion = true;
        }

        if (datosDTO.getPadecimientos() != null && !datosDTO.getPadecimientos().trim().isEmpty()) {
            System.out.println("✓ Actualizando padecimientos: " + datosDTO.getPadecimientos());
            infoAlumno.setPadecimientos(datosDTO.getPadecimientos());
            huboActualizacion = true;
        }

        if (datosDTO.getFechaNacimiento() != null && !datosDTO.getFechaNacimiento().isEmpty()) {
            try {
                LocalDate fechaNacimiento = LocalDate.parse(datosDTO.getFechaNacimiento());
                System.out.println("✓ Actualizando fecha de nacimiento: " + fechaNacimiento);
                infoAlumno.setFechaNacimiento(fechaNacimiento);
                huboActualizacion = true;
            } catch (Exception e) {
                System.err.println("❌ Error al parsear fecha: " + datosDTO.getFechaNacimiento());
                throw new RuntimeException("Formato de fecha inválido. Use: yyyy-MM-dd");
            }
        }

        if (datosDTO.getSexo() != null && !datosDTO.getSexo().isEmpty()) {
            System.out.println("✓ Actualizando sexo en Usuario: " + datosDTO.getSexo());
            usuarioEntity.setSexo(datosDTO.getSexo());
            usuarioRepository.save(usuarioEntity);
            huboActualizacion = true;
        }

        if (huboActualizacion) {
            informacionAlumnoRepository.save(infoAlumno);
            System.out.println("✓✓✓ Datos del alumno actualizados correctamente");
        } else {
            System.out.println("⚠ No se enviaron campos para actualizar");
            throw new RuntimeException("No se proporcionaron datos para actualizar");
        }

        System.out.println("=== FIN ACTUALIZACIÓN ===");
    }

    // ========================================================
    // MÉTODOS DE TARJETAS
    // ========================================================

    @Override
    @Transactional
    public TarjetaResponseDTO agregarTarjeta(String usuario, TarjetaDTO dto) {

        usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setUsuario(usuario);
        tarjeta.setNumeroTarjeta(dto.getNumeroTarjeta());
        tarjeta.setFechaCaducidad(dto.getFechaCaducidad());
        tarjeta.setNombreTitular(dto.getNombreTitular());
        tarjeta.setApellidosTitular(dto.getApellidosTitular());
        tarjeta.setDireccionFacturacion(dto.getDireccionFacturacion());
        tarjeta.setLocalidad(dto.getLocalidad());
        tarjeta.setCodigoPostal(dto.getCodigoPostal());
        tarjeta.setPais(dto.getPais());
        tarjeta.setTelefono(dto.getTelefono());

        tarjeta = tarjetaRepository.save(tarjeta);

        return convertirATarjetaResponseDTO(tarjeta, "Tarjeta agregada exitosamente");
    }

    @Override
    public List<TarjetaResponseDTO> obtenerTarjetas(String usuario) {

        usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Tarjeta> tarjetas = tarjetaRepository.findByUsuario(usuario);

        return tarjetas.stream()
                .map(t -> convertirATarjetaResponseDTO(t, null))
                .collect(Collectors.toList());
    }

    @Override
    public TarjetaResponseDTO obtenerTarjetaPorId(String usuario, Integer idTarjeta) {

        Tarjeta tarjeta = tarjetaRepository.findByIdTarjetaAndUsuario(idTarjeta, usuario)
                .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada o no pertenece al usuario"));

        return convertirATarjetaResponseDTO(tarjeta, null);
    }

    @Override
    @Transactional
    public TarjetaResponseDTO actualizarTarjeta(String usuario, Integer idTarjeta, TarjetaDTO dto) {

        Tarjeta tarjeta = tarjetaRepository.findByIdTarjetaAndUsuario(idTarjeta, usuario)
                .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada o no pertenece al usuario"));

        tarjeta.setNumeroTarjeta(dto.getNumeroTarjeta());
        tarjeta.setFechaCaducidad(dto.getFechaCaducidad());
        tarjeta.setNombreTitular(dto.getNombreTitular());
        tarjeta.setApellidosTitular(dto.getApellidosTitular());
        tarjeta.setDireccionFacturacion(dto.getDireccionFacturacion());
        tarjeta.setLocalidad(dto.getLocalidad());
        tarjeta.setCodigoPostal(dto.getCodigoPostal());
        tarjeta.setPais(dto.getPais());
        tarjeta.setTelefono(dto.getTelefono());

        tarjetaRepository.save(tarjeta);

        return convertirATarjetaResponseDTO(tarjeta, "Tarjeta actualizada exitosamente");
    }

    @Override
    @Transactional
    public void eliminarTarjeta(String usuario, Integer idTarjeta) {

        Tarjeta tarjeta = tarjetaRepository.findByIdTarjetaAndUsuario(idTarjeta, usuario)
                .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada o no pertenece al usuario"));

        tarjetaRepository.delete(tarjeta);
    }

    // ========================================================
    // MÉTODOS AUXILIARES
    // ========================================================

    private Integer calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return null;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    private TarjetaResponseDTO convertirATarjetaResponseDTO(Tarjeta tarjeta, String mensaje) {
        String numeroEnmascarado = enmascararNumeroTarjeta(tarjeta.getNumeroTarjeta());

        return new TarjetaResponseDTO(
                tarjeta.getIdTarjeta(),
                tarjeta.getUsuario(),
                numeroEnmascarado,
                tarjeta.getFechaCaducidad(),
                tarjeta.getNombreTitular(),
                tarjeta.getApellidosTitular(),
                tarjeta.getDireccionFacturacion(),
                tarjeta.getLocalidad(),
                tarjeta.getCodigoPostal(),
                tarjeta.getPais(),
                tarjeta.getTelefono(),
                mensaje
        );
    }

    private String enmascararNumeroTarjeta(String numeroTarjeta) {
        if (numeroTarjeta == null || numeroTarjeta.length() < 4) {
            return "****";
        }
        String ultimosCuatro = numeroTarjeta.substring(numeroTarjeta.length() - 4);
        return "**** **** **** " + ultimosCuatro;
    }
}