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
    // MÉTODO OBTENER PERFIL - ACTUALIZADO SIN NIVEL GENERAL
    // ========================================

    @Override
    public PerfilAlumnoResponseDTO obtenerPerfilAlumno(String usuario) {

        Usuario usuarioEntity = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        InformacionAlumno infoAlumno = informacionAlumnoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Perfil de alumno no encontrado"));

        // Obtener deportes CON su nivel específico
        List<AlumnoDeporte> deportesEntity = alumnoDeporteRepository.findByUsuario(usuario);
        List<PerfilAlumnoResponseDTO.DeporteConNivelDTO> deportes = deportesEntity.stream()
                .map(ad -> new PerfilAlumnoResponseDTO.DeporteConNivelDTO(
                        ad.getDeporte().getNombreDeporte(),
                        ad.getNivel().getNombreNivel(),  // ← Nivel por deporte
                        ad.getFechaInicio()
                ))
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
                deportes,  // ← Deportes con nivel
                totalAmigos,
                totalEntrenadores,
                "Perfil obtenido exitosamente"
        );
    }

    // ========================================
    // MÉTODO CREAR PERFIL - ACTUALIZADO SIN NIVEL NI DEPORTES
    // ========================================

    @Override
    @Transactional
    public PerfilAlumnoResponseDTO crearPerfilAlumno(PerfilAlumnoDTO dto) {

        Usuario usuario = usuarioRepository.findByUsuario(dto.getUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (informacionAlumnoRepository.existsByUsuario(dto.getUsuario())) {
            throw new RuntimeException("El alumno ya tiene un perfil creado");
        }

        // ========================================
        // Crear perfil básico SIN nivel ni deportes
        // ========================================
        InformacionAlumno infoAlumno = new InformacionAlumno();
        infoAlumno.setUsuario(dto.getUsuario());
        infoAlumno.setEstatura(dto.getEstatura());
        infoAlumno.setPeso(dto.getPeso());
        infoAlumno.setLesiones(dto.getLesiones());
        // NO hay nivel general
        infoAlumno.setPadecimientos(dto.getPadecimientos());
        infoAlumno.setFotoPerfil(dto.getFotoPerfil());
        infoAlumno.setFechaNacimiento(dto.getFechaNacimiento());

        informacionAlumnoRepository.save(infoAlumno);

        // ========================================
        // Los deportes se agregarán DESPUÉS cuando:
        // - El usuario busque entrenador
        // - Se inscriba a una clase
        // - Agregue deportes manualmente desde otra pantalla
        // ========================================

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
                infoAlumno.getPadecimientos(),
                infoAlumno.getFotoPerfil(),
                infoAlumno.getFechaNacimiento(),
                edad,
                List.of(),  // ← Lista vacía de deportes (se agregarán después)
                totalAmigos,
                totalEntrenadores,
                "Perfil de alumno creado exitosamente"
        );
    }

    // ========================================
    // MÉTODO ACTUALIZAR PERFIL - ACTUALIZADO SIN NIVEL NI DEPORTES
    // ========================================

    @Override
    @Transactional
    public PerfilAlumnoResponseDTO actualizarPerfilAlumno(String usuario, PerfilAlumnoDTO dto) {

        Usuario usuarioEntity = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        InformacionAlumno infoAlumno = informacionAlumnoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Perfil de alumno no encontrado"));

        // Actualizar datos básicos SIN nivel
        infoAlumno.setEstatura(dto.getEstatura());
        infoAlumno.setPeso(dto.getPeso());
        infoAlumno.setLesiones(dto.getLesiones());
        // NO hay nivel general
        infoAlumno.setPadecimientos(dto.getPadecimientos());
        infoAlumno.setFotoPerfil(dto.getFotoPerfil());
        infoAlumno.setFechaNacimiento(dto.getFechaNacimiento());

        informacionAlumnoRepository.save(infoAlumno);

        // ========================================
        // Los deportes NO se modifican aquí
        // Se modifican en otra pantalla específica
        // ========================================

        // Obtener deportes actuales
        List<AlumnoDeporte> deportesEntity = alumnoDeporteRepository.findByUsuario(usuario);
        List<PerfilAlumnoResponseDTO.DeporteConNivelDTO> deportes = deportesEntity.stream()
                .map(ad -> new PerfilAlumnoResponseDTO.DeporteConNivelDTO(
                        ad.getDeporte().getNombreDeporte(),
                        ad.getNivel().getNombreNivel(),
                        ad.getFechaInicio()
                ))
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
    // 🆕 NUEVO MÉTODO: ACTUALIZAR DATOS PARCIALES
    // ========================================

    /**
     * Actualiza datos específicos del alumno de forma parcial
     * Solo actualiza los campos que vienen en el DTO (no nulos)
     *
     * Este método es diferente a actualizarPerfilAlumno() porque:
     * - Permite actualizaciones parciales (solo lo que envíes)
     * - No requiere enviar todos los campos
     * - Ideal para el formulario "Completar Datos"
     */
    @Override
    @Transactional
    public void actualizarDatosAlumno(String usuario, ActualizarDatosAlumnoDTO datosDTO) {

        System.out.println("=== INICIANDO ACTUALIZACIÓN DE DATOS PARCIALES ===");
        System.out.println("Usuario: " + usuario);
        System.out.println("Datos recibidos: " + datosDTO);

        // 1. Verificar que el usuario existe
        Usuario usuarioEntity = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuario));

        System.out.println("✓ Usuario encontrado: " + usuarioEntity.getNombre());

        // 2. Obtener o crear Informacion_Alumno
        InformacionAlumno infoAlumno = informacionAlumnoRepository
                .findByUsuario(usuario)
                .orElseGet(() -> {
                    System.out.println("⚠ No existe información del alumno, creando nueva entrada...");
                    InformacionAlumno nuevaInfo = new InformacionAlumno();
                    nuevaInfo.setUsuario(usuario);
                    return nuevaInfo;
                });

        // 3. Actualizar solo los campos que vienen en el DTO (no nulos)
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

        // 4. Actualizar sexo en la tabla Usuario si viene en el DTO
        if (datosDTO.getSexo() != null && !datosDTO.getSexo().isEmpty()) {
            System.out.println("✓ Actualizando sexo en Usuario: " + datosDTO.getSexo());
            usuarioEntity.setSexo(datosDTO.getSexo());
            usuarioRepository.save(usuarioEntity);
            huboActualizacion = true;
        }

        // 5. Guardar cambios solo si hubo al menos una actualización
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