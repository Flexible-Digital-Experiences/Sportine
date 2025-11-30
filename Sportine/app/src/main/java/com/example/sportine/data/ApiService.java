package com.example.sportine.data;

import com.example.sportine.models.Comentario;
import com.example.sportine.models.CompletarEntrenamientoRequestDTO;
import com.example.sportine.models.DetalleEntrenamientoDTO;
import com.example.sportine.models.EntrenadorCardDTO;
import com.example.sportine.models.FormularioSolicitudDTO;
import com.example.sportine.models.HomeAlumnoDTO;
import com.example.sportine.models.HomeEntrenadorDTO;
import com.example.sportine.models.InfoDeporteAlumnoDTO;
import com.example.sportine.models.PerfilEntrenadorDTO;
import com.example.sportine.models.Publicacion;
import com.example.sportine.models.RespuestaRegistro;
import com.example.sportine.models.Usuario;
import com.example.sportine.models.PublicacionFeedDTO;
import com.example.sportine.models.UsuarioDetalle;
import com.example.sportine.ui.usuarios.dto.LoginRequest;
import com.example.sportine.ui.usuarios.dto.LoginResponse;
import com.example.sportine.ui.usuarios.dto.ComentarioRequest;
// Asegúrate de que este DTO exista en tu Android, si no, usa UsuarioDetalle
import com.example.sportine.ui.usuarios.dto.UsuarioDetalleDTO;
import com.example.sportine.ui.usuarios.dto.PerfilAlumnoResponseDTO;
import com.example.sportine.ui.usuarios.enviarsolicitud.EnviarSolicitud;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiService {

    // Usuarios

    @POST("/api/usuarios/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("/api/usuarios/registrar")
    Call<RespuestaRegistro> registrarUsuario(@Body Usuario usuario);

    @GET("/api/usuarios/{usuario}")
    Call<UsuarioDetalleDTO> obtenerUsuario(@Path("usuario") String usuario);

    // Alumnos

    @GET("/api/alumnos/perfil/{usuario}")
    Call<PerfilAlumnoResponseDTO> obtenerPerfilAlumno(@Path("usuario") String usuario);

    // Social

    @GET("/api/social/feed")
    Call<List<PublicacionFeedDTO>> getSocialFeed();

    @Multipart
    @POST("/api/social/post")
    Call<Publicacion> crearPost(
            @Part("data") RequestBody data,
            @Part MultipartBody.Part file
    );

    @DELETE("/api/social/post/{id}")
    Call<Void> borrarPost(@Path("id") Integer idPublicacion);

    @POST("/api/social/post/{id}/like")
    Call<Void> darLike(@Path("id") Integer idPublicacion);

    @DELETE("/api/social/post/{id}/like")
    Call<Void> quitarLike(@Path("id") Integer idPublicacion);

    @GET("/api/social/post/{id}/comentarios")
    Call<List<Comentario>> verComentarios(@Path("id") Integer idPublicacion);

    @POST("/api/social/post/{id}/comentarios")
    Call<Void> crearComentario(@Path("id") Integer idPublicacion, @Body ComentarioRequest request);

    @POST("/api/social/seguir/{username}")
    Call<Map<String, String>> seguirUsuario(@Path("username") String username);

    @GET("/api/social/verificar/{username}")
    Call<Boolean> verificarSeguimiento(@Path("username") String username);

    @GET("/api/social/amigos/buscar")
    Call<List<UsuarioDetalle>> buscarPersonas(@Query("q") String termino);

    @GET("/api/social/amigos")
    Call<List<UsuarioDetalle>> verMisAmigos();

    @PUT("/api/social/post/{id}")
    Call<Void> editarPost(@Path("id") Integer id, @Body com.example.sportine.models.Publicacion publicacionActualizada);

    // Buscar

    @GET("api/buscar-entrenadores")
    Call<List<EntrenadorCardDTO>> buscarEntrenadores(@Query("query") String query);

    @GET("api/buscar-entrenadores/ver/{usuario}")
    Call<PerfilEntrenadorDTO> obtenerPerfilEntrenador(@Path("usuario") String usuario);

    // Solicitudes

    @GET("api/Solicitudes/formulario/{usuarioEntrenador}")
    Call<FormularioSolicitudDTO> obtenerFormularioSolicitud(@Path("usuarioEntrenador") String usuarioEntrenador);

    @GET("api/Solicitudes/deporte/{idDeporte}")
    Call<InfoDeporteAlumnoDTO> obtenerInfoDeporte(@Path("idDeporte") Integer idDeporte);

    @POST("api/Solicitudes/enviar")
    Call<Map<String, String>> enviarSolicitud(@Body EnviarSolicitud.SolicitudRequest request);

    // Notificaciones

    @GET("/api/notificaciones")
    Call<List<com.example.sportine.models.Notificacion>> obtenerNotificaciones();

    // Inicio
    @GET("/api/alumnos/home/{usuario}")
    Call<HomeAlumnoDTO> obtenerHomeAlumno(@Path("usuario") String usuario);
    // --- DETALLES ENTRENAMIENTO ALUMNO ---

    @GET("/api/alumno/entrenamientos/{id}") // Antes era /entrenamiento/{id}
    Call<DetalleEntrenamientoDTO> obtenerDetalleEntrenamiento(@Path("id") Integer idEntrenamiento);

    // Marcar CheckBox (Ruta Actualizada)
    @PUT("/api/alumno/entrenamientos/ejercicio/{idAsignado}/estado")
    Call<Void> cambiarEstadoEjercicio(
            @Path("idAsignado") Integer idAsignado,
            @Query("completado") boolean completado
    );

    // Endpoint para completar el entrenamiento y mandar feedback
    @POST("/api/alumno/entrenamientos/completar")
    Call<Void> completarEntrenamiento(@Body CompletarEntrenamientoRequestDTO request);


    // Home Entrenador
    @GET("/api/entrenador/home/{usuario}")
    Call<HomeEntrenadorDTO> obtenerHomeEntrenador(@Path("usuario") String usuario);

}