package com.sportine.backend.service.impl;

import com.sportine.backend.dto.ActualizarPerfilEntrenadorDTO;
import com.sportine.backend.dto.PerfilEntrenadorResponseDTO;
import com.sportine.backend.model.*;
import com.sportine.backend.repository.*;
import com.sportine.backend.service.EntrenadorPerfilService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EntrenadorPerfilServiceImpl implements EntrenadorPerfilService {

    private final UsuarioRepository usuarioRepository;
    private final InformacionEntrenadorRepository informacionEntrenadorRepository;
    private final EntrenadorDeporteRepository entrenadorDeporteRepository;
    private final DeporteRepository deporteRepository;
    private final EstadoRepository estadoRepository;
    private final SeguidoresRepository seguidoresRepository;
    private final EntrenadorAlumnoRepository entrenadorAlumnoRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final RolRepository rolRepository;
    // ✅ SEGURIDAD: BCrypt para verificar contraseña en eliminarCuenta()
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public PerfilEntrenadorResponseDTO obtenerPerfilEntrenador(String usuario) {

        Usuario usuarioEntity = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        InformacionEntrenador infoEntrenador = informacionEntrenadorRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    System.out.println("⚠ No existe información del entrenador, creando nueva entrada...");
                    InformacionEntrenador nuevo = new InformacionEntrenador();
                    nuevo.setUsuario(usuario);
                    nuevo.setLimiteAlumnos(3);
                    nuevo.setCostoMensualidad(0);
                    return informacionEntrenadorRepository.save(nuevo);
                });

        List<EntrenadorDeporte> deportesEntity = entrenadorDeporteRepository.findByUsuario(usuario);

        List<String> deportes = deportesEntity.stream()
                .map(ed -> {
                    Deporte deporte = deporteRepository.findById(ed.getIdDeporte()).orElse(null);
                    return deporte != null ? deporte.getNombreDeporte() : "Desconocido";
                })
                .collect(Collectors.toList());

        Estado estado = estadoRepository.findById(usuarioEntity.getIdEstado()).orElse(null);
        String nombreEstado = estado != null ? estado.getEstado() : "";

        Integer totalAmigos = seguidoresRepository.contarAmigos(usuario);
        Integer totalAlumnos = entrenadorAlumnoRepository.contarAlumnosActivos(usuario);

        return new PerfilEntrenadorResponseDTO(
                usuarioEntity.getUsuario(),
                usuarioEntity.getNombre(),
                usuarioEntity.getApellidos(),
                usuarioEntity.getSexo(),
                nombreEstado,
                usuarioEntity.getCiudad(),
                usuarioEntity.getCorreo(),
                infoEntrenador.getCostoMensualidad(),
                infoEntrenador.getLimiteAlumnos(),
                infoEntrenador.getDescripcionPerfil(),
                infoEntrenador.getFotoPerfil(),
                deportes,
                totalAlumnos,
                totalAmigos,
                "Perfil obtenido exitosamente"
        );
    }

    @Override
    @Transactional
    public PerfilEntrenadorResponseDTO actualizarPerfilEntrenador(
            String usuario,
            ActualizarPerfilEntrenadorDTO datos) {

        System.out.println("=== INICIANDO ACTUALIZACIÓN DE PERFIL ENTRENADOR ===");
        System.out.println("Usuario: " + usuario);
        System.out.println("Datos recibidos: " + datos);

        Usuario usuarioEntity = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        System.out.println("✓ Usuario encontrado: " + usuarioEntity.getNombre());

        InformacionEntrenador infoEntrenador = informacionEntrenadorRepository
                .findByUsuario(usuario)
                .orElseGet(() -> {
                    System.out.println("⚠ No existe información del entrenador, creando nueva entrada...");
                    InformacionEntrenador nuevo = new InformacionEntrenador();
                    nuevo.setUsuario(usuario);
                    nuevo.setLimiteAlumnos(3);
                    nuevo.setCostoMensualidad(0);
                    return nuevo;
                });

        if (datos.getCostoMensualidad() != null) {
            System.out.println("✓ Actualizando costo mensualidad: " + datos.getCostoMensualidad());
            infoEntrenador.setCostoMensualidad(datos.getCostoMensualidad());
        }

        if (datos.getDescripcionPerfil() != null && !datos.getDescripcionPerfil().trim().isEmpty()) {
            System.out.println("✓ Actualizando descripción: " + datos.getDescripcionPerfil());
            infoEntrenador.setDescripcionPerfil(datos.getDescripcionPerfil());
        }

        if (datos.getLimiteAlumnos() != null) {
            System.out.println("✓ Actualizando límite de alumnos: " + datos.getLimiteAlumnos());
            infoEntrenador.setLimiteAlumnos(datos.getLimiteAlumnos());
        }

        informacionEntrenadorRepository.save(infoEntrenador);
        System.out.println("✓✓✓ Datos del entrenador actualizados correctamente");
        System.out.println("=== FIN ACTUALIZACIÓN ===");

        if (datos.getDeportes() != null && !datos.getDeportes().isEmpty()) {
            entrenadorDeporteRepository.deleteAll(
                    entrenadorDeporteRepository.findByUsuario(usuario)
            );

            for (Integer idDeporte : datos.getDeportes()) {
                EntrenadorDeporte entrenadorDeporte = new EntrenadorDeporte();
                entrenadorDeporte.setUsuario(usuario);
                entrenadorDeporte.setIdDeporte(idDeporte);
                entrenadorDeporteRepository.save(entrenadorDeporte);
            }
        }

        return obtenerPerfilEntrenador(usuario);
    }

    @Override
    @Transactional
    public PerfilEntrenadorResponseDTO actualizarFotoPerfil(String usuario, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("No se proporcionó ninguna imagen");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("El archivo debe ser una imagen");
        }

        InformacionEntrenador infoEntrenador = informacionEntrenadorRepository
                .findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Perfil de entrenador no encontrado"));

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "sportine/entrenadores",
                            "resource_type", "image"
                    )
            );

            String imageUrl = (String) uploadResult.get("secure_url");
            infoEntrenador.setFotoPerfil(imageUrl);
            informacionEntrenadorRepository.save(infoEntrenador);

            return obtenerPerfilEntrenador(usuario);

        } catch (IOException e) {
            throw new RuntimeException("Error al subir la imagen: " + e.getMessage());
        }
    }

    // ============================================
    // GESTIÓN DE DEPORTES
    // ============================================

    @Override
    public List<String> obtenerCatalogoDeportes() {
        List<Deporte> deportes = deporteRepository.findAll();
        return deportes.stream()
                .map(Deporte::getNombreDeporte)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void agregarDeporte(String usuario, String nombreDeporte) {
        usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Deporte deporte = deporteRepository.findByNombreDeporte(nombreDeporte)
                .orElseThrow(() -> new RuntimeException("Deporte no encontrado: " + nombreDeporte));

        Optional<EntrenadorDeporte> deporteExistente =
                entrenadorDeporteRepository.findByUsuarioAndIdDeporte(usuario, deporte.getIdDeporte());

        if (deporteExistente.isPresent()) {
            throw new RuntimeException("Ya tienes este deporte en tu perfil");
        }

        EntrenadorDeporte nuevoDeporte = new EntrenadorDeporte();
        nuevoDeporte.setUsuario(usuario);
        nuevoDeporte.setIdDeporte(deporte.getIdDeporte());
        entrenadorDeporteRepository.save(nuevoDeporte);
    }

    @Override
    @Transactional
    public void eliminarDeporte(String usuario, String nombreDeporte) {
        usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Deporte deporte = deporteRepository.findByNombreDeporte(nombreDeporte)
                .orElseThrow(() -> new RuntimeException("Deporte no encontrado en el catálogo: " + nombreDeporte));

        EntrenadorDeporte entrenadorDeporte =
                entrenadorDeporteRepository.findByUsuarioAndIdDeporte(usuario, deporte.getIdDeporte())
                        .orElseThrow(() -> new RuntimeException("No tienes este deporte en tu perfil"));

        entrenadorDeporteRepository.delete(entrenadorDeporte);
    }

    // ========================================================
    // ELIMINAR CUENTA (soft delete vía rol)
    // ========================================================

    @Override
    @Transactional
    public void eliminarCuenta(String usuario, String contrasena) {

        System.out.println("🗑️ Solicitud de eliminación de cuenta entrenador: " + usuario);

        Usuario usuarioEntity = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // ✅ SEGURIDAD: BCrypt en lugar de .equals() en texto plano
        if (!passwordEncoder.matches(contrasena, usuarioEntity.getContrasena())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        UsuarioRol usuarioRol = usuarioRolRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Rol del usuario no encontrado"));

        Rol rolEliminado = rolRepository.findByRol("ELIMINADO")
                .orElseThrow(() -> new RuntimeException("Rol ELIMINADO no configurado en el sistema"));

        usuarioRol.setIdRol(rolEliminado.getIdRol());
        usuarioRolRepository.save(usuarioRol);

        System.out.println("✅ Cuenta de entrenador eliminada (soft delete): " + usuario);
    }
}