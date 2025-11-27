package com.sportine.backend.service.impl;

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

    // ========================================================
    // MÉTODO OBTENER PERFIL - CORREGIDO
    // ========================================================

    @Override
    public PerfilAlumnoResponseDTO obtenerPerfilAlumno(String usuario) {

        Usuario usuarioEntity = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        InformacionAlumno infoAlumno = informacionAlumnoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Perfil de alumno no encontrado"));

        // ========================================
        // CAMBIO CRÍTICO: Obtener nombres de deportes correctamente
        // ========================================
        List<AlumnoDeporte> deportesEntity = alumnoDeporteRepository.findByUsuario(usuario);
        List<String> deportes = deportesEntity.stream()
                .map(ad -> ad.getDeporte().getNombreDeporte())  // ← Usar relación
                .collect(Collectors.toList());

        Integer edad = calcularEdad(infoAlumno.getFechaNacimiento());

        Estado estado = estadoRepository.findById(usuarioEntity.getIdEstado())
                .orElse(null);
        String nombreEstado = estado != null ? estado.getEstado() : "";

        // ========================================
        // CAMBIO CRÍTICO: Obtener nombre del nivel
        // ========================================
        String nombreNivel = infoAlumno.getNivel() != null
                ? infoAlumno.getNivel().getNombreNivel()
                : null;

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
                nombreNivel,  // ← Usar nombre, no objeto
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

    // ========================================================
    // MÉTODO CREAR PERFIL - CORREGIDO
    // ========================================================

    @Override
    @Transactional
    public PerfilAlumnoResponseDTO crearPerfilAlumno(PerfilAlumnoDTO dto) {

        Usuario usuario = usuarioRepository.findByUsuario(dto.getUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (informacionAlumnoRepository.existsByUsuario(dto.getUsuario())) {
            throw new RuntimeException("El alumno ya tiene un perfil creado");
        }

        // ========================================
        // CAMBIO CRÍTICO: Buscar el nivel por nombre
        // ========================================
        Nivel nivel = nivelRepository.findByNombreNivel(dto.getNivel())
                .orElseThrow(() -> new RuntimeException("Nivel no encontrado: " + dto.getNivel()));

        InformacionAlumno infoAlumno = new InformacionAlumno();
        infoAlumno.setUsuario(dto.getUsuario());
        infoAlumno.setEstatura(dto.getEstatura());
        infoAlumno.setPeso(dto.getPeso());
        infoAlumno.setLesiones(dto.getLesiones());
        infoAlumno.setNivel(nivel);  // ← Asignar objeto Nivel, no String
        infoAlumno.setPadecimientos(dto.getPadecimientos());
        infoAlumno.setFotoPerfil(dto.getFotoPerfil());
        infoAlumno.setFechaNacimiento(dto.getFechaNacimiento());

        informacionAlumnoRepository.save(infoAlumno);

        // ========================================
        // CAMBIO CRÍTICO: Buscar deportes por nombre
        // ========================================
        if (dto.getDeportes() != null && !dto.getDeportes().isEmpty()) {
            for (String nombreDeporte : dto.getDeportes()) {
                Deporte deporte = deporteRepository.findByNombreDeporte(nombreDeporte)
                        .orElseThrow(() -> new RuntimeException("Deporte no encontrado: " + nombreDeporte));

                AlumnoDeporte alumnoDeporte = new AlumnoDeporte();
                alumnoDeporte.setUsuario(dto.getUsuario());
                alumnoDeporte.setDeporte(deporte);  // ← Asignar objeto Deporte
                alumnoDeporte.setNivel(nivel);  // ← Asignar nivel
                alumnoDeporte.setFechaInicio(LocalDate.now());

                alumnoDeporteRepository.save(alumnoDeporte);
            }
        }

        Integer edad = calcularEdad(dto.getFechaNacimiento());

        Estado estado = estadoRepository.findById(usuario.getIdEstado())
                .orElse(null);
        String nombreEstado = estado != null ? estado.getEstado() : "";

        Integer totalAmigos = seguidoresRepository.contarAmigos(dto.getUsuario());
        Integer totalEntrenadores = entrenadorAlumnoRepository.contarEntrenadoresActivos(dto.getUsuario());

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
                nivel.getNombreNivel(),  // ← Nombre del nivel
                infoAlumno.getPadecimientos(),
                infoAlumno.getFotoPerfil(),
                infoAlumno.getFechaNacimiento(),
                edad,
                dto.getDeportes(),
                totalAmigos,
                totalEntrenadores,
                "Perfil de alumno creado exitosamente"
        );
    }

    // ========================================================
    // MÉTODO ACTUALIZAR PERFIL - CORREGIDO
    // ========================================================

    @Override
    @Transactional
    public PerfilAlumnoResponseDTO actualizarPerfilAlumno(String usuario, PerfilAlumnoDTO dto) {

        Usuario usuarioEntity = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        InformacionAlumno infoAlumno = informacionAlumnoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Perfil de alumno no encontrado"));

        // Buscar el nivel por nombre
        Nivel nivel = nivelRepository.findByNombreNivel(dto.getNivel())
                .orElseThrow(() -> new RuntimeException("Nivel no encontrado: " + dto.getNivel()));

        infoAlumno.setEstatura(dto.getEstatura());
        infoAlumno.setPeso(dto.getPeso());
        infoAlumno.setLesiones(dto.getLesiones());
        infoAlumno.setNivel(nivel);  // ← Asignar objeto Nivel
        infoAlumno.setPadecimientos(dto.getPadecimientos());
        infoAlumno.setFotoPerfil(dto.getFotoPerfil());
        infoAlumno.setFechaNacimiento(dto.getFechaNacimiento());

        informacionAlumnoRepository.save(infoAlumno);

        // Eliminar deportes anteriores
        alumnoDeporteRepository.deleteByUsuario(usuario);

        // Agregar nuevos deportes
        if (dto.getDeportes() != null && !dto.getDeportes().isEmpty()) {
            for (String nombreDeporte : dto.getDeportes()) {
                Deporte deporte = deporteRepository.findByNombreDeporte(nombreDeporte)
                        .orElseThrow(() -> new RuntimeException("Deporte no encontrado: " + nombreDeporte));

                AlumnoDeporte alumnoDeporte = new AlumnoDeporte();
                alumnoDeporte.setUsuario(usuario);
                alumnoDeporte.setDeporte(deporte);
                alumnoDeporte.setNivel(nivel);
                alumnoDeporte.setFechaInicio(LocalDate.now());

                alumnoDeporteRepository.save(alumnoDeporte);
            }
        }

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
                nivel.getNombreNivel(),
                infoAlumno.getPadecimientos(),
                infoAlumno.getFotoPerfil(),
                infoAlumno.getFechaNacimiento(),
                edad,
                dto.getDeportes(),
                totalAmigos,
                totalEntrenadores,
                "Perfil actualizado exitosamente"
        );
    }

    // ========================================================
    // MÉTODOS DE TARJETAS (CONFIGURACIÓN DEL PERFIL)
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