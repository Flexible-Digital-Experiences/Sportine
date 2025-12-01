package com.sportine.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Servicio para gestionar la subida de imágenes a Cloudinary
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Sube una imagen a Cloudinary y retorna la URL pública
     *
     * @param file Archivo de imagen a subir
     * @param folder Carpeta en Cloudinary donde se guardará (ej: "sportine/perfiles")
     * @return URL pública de la imagen subida
     * @throws IOException si hay error al subir
     */
    public String subirImagen(MultipartFile file, String folder) throws IOException {
        log.info("📤 Subiendo imagen a Cloudinary en carpeta: {}", folder);

        try {
            // ✅ CORREGIDO: Configurar opciones de subida con sintaxis correcta
            Map<String, Object> params = ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "auto",
                    "overwrite", true,
                    "transformation", new com.cloudinary.Transformation()
                            .width(500)
                            .height(500)
                            .crop("fill")
                            .gravity("face")
                            .quality("auto:good")
            );

            // Subir imagen
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

            String url = (String) uploadResult.get("secure_url");
            log.info("✅ Imagen subida exitosamente: {}", url);

            return url;

        } catch (IOException e) {
            log.error("❌ Error al subir imagen a Cloudinary: {}", e.getMessage());
            throw new IOException("Error al subir imagen: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina una imagen de Cloudinary usando su public_id
     *
     * @param publicId ID público de la imagen en Cloudinary
     * @throws IOException si hay error al eliminar
     */
    public void eliminarImagen(String publicId) throws IOException {
        log.info("🗑️ Eliminando imagen de Cloudinary: {}", publicId);

        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("✅ Imagen eliminada: {}", result.get("result"));
        } catch (IOException e) {
            log.error("❌ Error al eliminar imagen: {}", e.getMessage());
            throw new IOException("Error al eliminar imagen: " + e.getMessage(), e);
        }
    }

    /**
     * Extrae el public_id de una URL de Cloudinary
     *
     * @param url URL completa de Cloudinary
     * @return public_id de la imagen
     */
    public String extraerPublicId(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        try {
            // URL típica: https://res.cloudinary.com/[cloud]/image/upload/v[version]/[folder]/[public_id].[ext]
            // Buscar la parte después de "upload/"
            String[] parts = url.split("/upload/");
            if (parts.length < 2) {
                return null;
            }

            // Obtener la parte después de upload/ y antes de la extensión
            String afterUpload = parts[1];

            // Si hay versión (v1234567890), quitarla
            if (afterUpload.startsWith("v")) {
                String[] versionParts = afterUpload.split("/", 2);
                if (versionParts.length > 1) {
                    afterUpload = versionParts[1];
                }
            }

            // Quitar la extensión del archivo
            int lastDot = afterUpload.lastIndexOf('.');
            if (lastDot > 0) {
                afterUpload = afterUpload.substring(0, lastDot);
            }

            log.info("📋 Public ID extraído: {}", afterUpload);
            return afterUpload;

        } catch (Exception e) {
            log.warn("⚠️ No se pudo extraer public_id de URL: {}", url);
            return null;
        }
    }
}