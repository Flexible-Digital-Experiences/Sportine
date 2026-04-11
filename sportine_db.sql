DROP DATABASE IF EXISTS sportine_db;
CREATE DATABASE sportine_db;
USE sportine_db;

-- ============================================
-- 1. TABLAS BASE
-- ============================================

CREATE TABLE Estado (
    id_estado INT PRIMARY KEY AUTO_INCREMENT,
    estado VARCHAR(100)
);

CREATE TABLE Usuario (
    usuario VARCHAR(255) PRIMARY KEY,
    correo VARCHAR(255) UNIQUE NOT NULL,
    contraseña VARCHAR(255),
    nombre VARCHAR(255),
    apellidos VARCHAR(255),
    sexo VARCHAR(50),
    id_estado INT,
    ciudad VARCHAR(100),
    FOREIGN KEY (id_estado) REFERENCES Estado(id_estado),
    INDEX idx_correo (correo)
);

CREATE TABLE Rol (
    id_rol INT PRIMARY KEY AUTO_INCREMENT,
    rol VARCHAR(100)
);

CREATE TABLE Usuario_rol (
    id_usuario_rol INT PRIMARY KEY AUTO_INCREMENT,
    id_rol INT,
    usuario VARCHAR(255),
    FOREIGN KEY (id_rol) REFERENCES Rol(id_rol),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario),
    UNIQUE KEY unique_usuario_rol (usuario, id_rol)
);

CREATE TABLE Deporte (
    id_deporte INT PRIMARY KEY AUTO_INCREMENT,
    nombre_deporte VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE Nivel (
    id_nivel INT PRIMARY KEY AUTO_INCREMENT,
    nombre_nivel VARCHAR(50) UNIQUE NOT NULL
);

-- ============================================
-- 2. TABLAS DE INFORMACIÓN
-- ============================================

CREATE TABLE Informacion_Alumno (
    usuario VARCHAR(255) PRIMARY KEY,
    estatura FLOAT,
    peso FLOAT,
    lesiones VARCHAR(255),
    padecimientos VARCHAR(255),
    foto_perfil TEXT,
    fecha_nacimiento DATE,
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Informacion_Entrenador (
    usuario VARCHAR(255) PRIMARY KEY,
    costo_mensualidad INT,
    descripcion_perfil VARCHAR(255),
    foto_perfil TEXT,
    limite_alumnos INT,
    merchant_id VARCHAR(255) NULL,
    merchant_id_in_paypal VARCHAR(255) NULL,
    paypal_email_confirmed VARCHAR(255) NULL,
    tracking_id VARCHAR(255) NULL,
    onboarding_status ENUM('pending', 'completed', 'failed', 'not_started') DEFAULT 'not_started',
    onboarding_link TEXT NULL,
    fecha_onboarding DATE NULL,
    permissions_granted TEXT NULL,
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario),
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_onboarding_status (onboarding_status)
);

-- ============================================
-- 3. SUSCRIPCIONES - PLATFORM PARTNER PAYPAL
-- ============================================

CREATE TABLE Estudiante_Suscripcion_Entrenador (
    id_suscripcion INT PRIMARY KEY AUTO_INCREMENT,
    usuario_estudiante VARCHAR(255) NOT NULL,
    usuario_entrenador VARCHAR(255) NOT NULL,
    id_deporte INT NOT NULL,
    subscription_id VARCHAR(255) NOT NULL,
    plan_id VARCHAR(255) NULL,
    monto_total DECIMAL(10,2) NOT NULL,
    monto_entrenador DECIMAL(10,2) NOT NULL,
    monto_comision_sportine DECIMAL(10,2) NOT NULL,
    porcentaje_comision DECIMAL(5,2) DEFAULT 10.00,
    moneda VARCHAR(3) DEFAULT 'MXN',
    status_suscripcion ENUM('active', 'cancelled', 'expired', 'suspended', 'pending') DEFAULT 'pending',
    fecha_inicio_suscripcion DATE NULL,
    fecha_proximo_pago DATE NULL,
    fecha_fin_suscripcion DATE NULL,
    fecha_cancelacion DATE NULL,
    motivo_cancelacion TEXT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_estudiante) REFERENCES Usuario(usuario),
    FOREIGN KEY (usuario_entrenador) REFERENCES Usuario(usuario),
    FOREIGN KEY (id_deporte) REFERENCES Deporte(id_deporte),
    INDEX idx_subscription_id (subscription_id),
    INDEX idx_estudiante (usuario_estudiante),
    INDEX idx_entrenador (usuario_entrenador),
    INDEX idx_status (status_suscripcion),
    UNIQUE KEY unique_estudiante_entrenador_deporte_activo (usuario_estudiante, usuario_entrenador, id_deporte, status_suscripcion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Historial_Pagos_Estudiante_Entrenador (
    id_pago INT PRIMARY KEY AUTO_INCREMENT,
    id_suscripcion INT NOT NULL,
    paypal_transaction_id VARCHAR(255) NULL,
    paypal_payment_id VARCHAR(255) NULL,
    paypal_sale_id VARCHAR(255) NULL,
    monto_total DECIMAL(10,2) NOT NULL,
    monto_entrenador DECIMAL(10,2) NOT NULL,
    monto_comision_sportine DECIMAL(10,2) NOT NULL,
    moneda VARCHAR(3) DEFAULT 'MXN',
    status_pago VARCHAR(50) NULL,
    fecha_pago DATETIME NULL,
    fecha_esperada_pago DATE NULL,
    evento_webhook TEXT NULL,
    tipo_evento VARCHAR(100) NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_suscripcion) REFERENCES Estudiante_Suscripcion_Entrenador(id_suscripcion),
    INDEX idx_suscripcion (id_suscripcion),
    INDEX idx_transaction_id (paypal_transaction_id),
    INDEX idx_status (status_pago)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Comisiones_Sportine (
    id_comision INT PRIMARY KEY AUTO_INCREMENT,
    id_pago INT NOT NULL,
    monto_comision DECIMAL(10,2) NOT NULL,
    moneda VARCHAR(3) DEFAULT 'MXN',
    porcentaje_aplicado DECIMAL(5,2) NULL,
    status_deposito ENUM('pending', 'deposited', 'failed') DEFAULT 'pending',
    fecha_deposito_esperado DATE NULL,
    fecha_deposito_real DATETIME NULL,
    paypal_payout_batch_id VARCHAR(255) NULL,
    paypal_payout_item_id VARCHAR(255) NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pago) REFERENCES Historial_Pagos_Estudiante_Entrenador(id_pago),
    INDEX idx_pago (id_pago),
    INDEX idx_status (status_deposito)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 4. RELACIONES
-- ============================================

CREATE TABLE Alumno_Deporte (
    id_alumno_deporte INT PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255),
    id_deporte INT,
    fecha_inicio DATE,
    id_nivel INT,
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario),
    FOREIGN KEY (id_deporte) REFERENCES Deporte(id_deporte),
    FOREIGN KEY (id_nivel) REFERENCES Nivel(id_nivel),
    UNIQUE KEY unique_alumno_deporte (usuario, id_deporte)
);

CREATE TABLE Entrenador_Deporte (
    id_entrenador_deporte INT PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255),
    id_deporte INT,
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario),
    FOREIGN KEY (id_deporte) REFERENCES Deporte(id_deporte),
    UNIQUE KEY unique_entrenador_deporte (usuario, id_deporte)
);

CREATE TABLE Entrenador_Alumno (
    id_relacion INT PRIMARY KEY AUTO_INCREMENT,
    usuario_entrenador VARCHAR(255),
    usuario_alumno VARCHAR(255),
    id_deporte INT,
    fecha_inicio DATE DEFAULT (CURRENT_DATE),
    fin_mensualidad DATE NULL,
    status_relacion VARCHAR(50),
    FOREIGN KEY (usuario_entrenador) REFERENCES Usuario(usuario),
    FOREIGN KEY (usuario_alumno) REFERENCES Usuario(usuario),
    FOREIGN KEY (id_deporte) REFERENCES Deporte(id_deporte),
    UNIQUE KEY unique_alumno_deporte_activo (usuario_alumno, id_deporte, status_relacion)
);

CREATE TABLE Solicitudes_Entrenamiento (
    id_solicitud INT PRIMARY KEY AUTO_INCREMENT,
    usuario_alumno VARCHAR(255),
    usuario_entrenador VARCHAR(255),
    id_deporte INT,
    descripcion_solicitud VARCHAR(255),
    fecha_solicitud DATE,
    status_solicitud ENUM('En_revisión', 'Aprobada', 'Rechazada'),
    FOREIGN KEY (usuario_alumno) REFERENCES Usuario(usuario),
    FOREIGN KEY (usuario_entrenador) REFERENCES Usuario(usuario),
    FOREIGN KEY (id_deporte) REFERENCES Deporte(id_deporte)
);

-- ============================================
-- 5. MÓDULO DE ENTRENAMIENTO
-- ============================================

CREATE TABLE Entrenamiento (
    id_entrenamiento INT PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255),
    usuario_entrenador VARCHAR(255),
    id_deporte INT,
    titulo_entrenamiento VARCHAR(255),
    objetivo VARCHAR(255),
    fecha_entrenamiento DATE,
    hora_entrenamiento TIME,
    dificultad VARCHAR(50),
    estado_entrenamiento ENUM('pendiente', 'en_progreso', 'finalizado'),
    creado_en DATETIME DEFAULT CURRENT_TIMESTAMP,
    actualizado_en DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario),
    FOREIGN KEY (usuario_entrenador) REFERENCES Usuario(usuario),
    FOREIGN KEY (id_deporte) REFERENCES Deporte(id_deporte)
);

CREATE TABLE Ejercicios_Asignados (
    id_asignado INT PRIMARY KEY AUTO_INCREMENT,
    id_entrenamiento INT NOT NULL,
    usuario VARCHAR(255) NOT NULL,
    nombre_ejercicio VARCHAR(255) NOT NULL,
    series INT NULL,
    repeticiones INT NULL,
    peso FLOAT NULL,
    duracion INT NULL COMMENT 'En minutos',
    distancia FLOAT NULL COMMENT 'En km',
    status_ejercicio ENUM('pendiente', 'completado', 'parcial', 'omitido') DEFAULT 'pendiente',
    valor_completado_reps INT DEFAULT NULL,
    valor_completado_duracion INT DEFAULT NULL,
    valor_completado_distancia FLOAT DEFAULT NULL,
    valor_completado_peso FLOAT DEFAULT NULL,
    tiene_exitosos BOOLEAN,
    notas_alumno TEXT DEFAULT NULL,
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario),
    INDEX idx_entrenamiento (id_entrenamiento),
    INDEX idx_usuario (usuario)
);

CREATE TABLE Resultado_Series_Ejercicio (
    id_resultado INT PRIMARY KEY AUTO_INCREMENT,
    id_asignado INT NOT NULL,
    numero_serie INT NOT NULL,
    reps_esperadas INT DEFAULT NULL,
    peso_esperado FLOAT DEFAULT NULL,
    duracion_esperada_seg INT DEFAULT NULL,
    distancia_esperada_metros FLOAT DEFAULT NULL,
    reps_completadas INT DEFAULT 0,
    peso_usado FLOAT DEFAULT 0,
    duracion_completada_seg INT DEFAULT 0,
    distancia_completada_metros FLOAT DEFAULT 0,
    exitosos INT DEFAULT NULL COMMENT 'Reps exitosas según el deporte. NULL si no aplica.',
    status ENUM('pendiente', 'completado', 'parcial', 'omitido') DEFAULT 'pendiente',
    notas TEXT DEFAULT NULL,
    registrado_en DATETIME DEFAULT NULL,
    FOREIGN KEY (id_asignado) REFERENCES Ejercicios_Asignados(id_asignado),
    UNIQUE KEY unique_serie (id_asignado, numero_serie),
    INDEX idx_id_asignado (id_asignado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Progreso_Entrenamiento (
    id_progreso INT PRIMARY KEY AUTO_INCREMENT,
    id_entrenamiento INT NOT NULL,
    usuario VARCHAR(255) NOT NULL,
    fecha_finalizacion DATETIME NULL,
    completado BOOLEAN DEFAULT FALSE,
    hc_sesion_id VARCHAR(255) NULL,
    hc_tipo_ejercicio VARCHAR(100) NULL,
    hc_duracion_activa_min INT NULL,
    hc_calorias_kcal INT NULL,
    hc_pasos INT NULL,
    hc_distancia_metros FLOAT NULL,
    hc_velocidad_promedio_ms FLOAT NULL,
    hc_fuente_datos ENUM('health_connect', 'strava', 'manual') NULL,
    hc_sincronizado_en DATETIME NULL,
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario),
    INDEX idx_entrenamiento (id_entrenamiento),
    INDEX idx_usuario (usuario)
);

CREATE TABLE Feedback_Entrenamiento (
    id_feedback INT PRIMARY KEY AUTO_INCREMENT,
    id_entrenamiento INT NOT NULL,
    usuario VARCHAR(255) NOT NULL,
    nivel_cansancio INT NULL,
    dificultad_percibida INT NULL,
    estado_animo VARCHAR(50) NULL,
    comentarios VARCHAR(255) NULL,
    fecha_feedback DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

-- ============================================
-- 6. CONEXIONES CON APIs EXTERNAS
-- ============================================

CREATE TABLE Conexiones_Api_Externa (
    id_conexion INT PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255) NOT NULL,
    proveedor ENUM('health_connect', 'strava', 'garmin') NOT NULL,
    esta_conectado BOOLEAN DEFAULT FALSE,
    ultima_sincronizacion DATETIME NULL,
    oauth_access_token TEXT NULL,
    oauth_refresh_token TEXT NULL,
    oauth_expires_at DATETIME NULL,
    oauth_scope VARCHAR(255) NULL,
    proveedor_usuario_id VARCHAR(255) NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario),
    UNIQUE KEY unique_usuario_proveedor (usuario, proveedor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 7. MÉTRICAS POR DEPORTE
-- ============================================

CREATE TABLE Plantilla_Metricas_Deporte (
    id_plantilla INT PRIMARY KEY AUTO_INCREMENT,
    id_deporte INT NOT NULL,
    nombre_metrica VARCHAR(100) NOT NULL,
    etiqueta_display VARCHAR(150) NOT NULL,
    unidad VARCHAR(50) DEFAULT NULL,
    fuente ENUM('health_connect', 'manual', 'calculada') NOT NULL,
    es_por_serie BOOLEAN DEFAULT FALSE,
    orden_display INT DEFAULT 0,
    FOREIGN KEY (id_deporte) REFERENCES Deporte(id_deporte),
    UNIQUE KEY unique_deporte_metrica (id_deporte, nombre_metrica),
    INDEX idx_deporte (id_deporte)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Resultado_Metrica_Manual (
    id_resultado_metrica INT PRIMARY KEY AUTO_INCREMENT,
    id_entrenamiento INT NOT NULL,
    id_plantilla INT NOT NULL,
    usuario VARCHAR(255) NOT NULL,
    valor_numerico DOUBLE NOT NULL,
    numero_serie INT DEFAULT NULL,
    notas VARCHAR(500) DEFAULT NULL,
    registrado_en DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    FOREIGN KEY (id_plantilla) REFERENCES Plantilla_Metricas_Deporte(id_plantilla),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario),
    INDEX idx_entrenamiento (id_entrenamiento),
    INDEX idx_usuario (usuario),
    INDEX idx_plantilla (id_plantilla)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Estadisticas_Carrera_Usuario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255) NOT NULL,
    id_deporte INT NOT NULL,
    nombre_metrica VARCHAR(100) NOT NULL,
    valor_total DOUBLE DEFAULT 0,
    mejor_sesion DOUBLE DEFAULT 0,
    fecha_mejor_sesion DATE NULL,
    total_entrenamientos INT DEFAULT 0,
    ultima_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario),
    FOREIGN KEY (id_deporte) REFERENCES Deporte(id_deporte),
    UNIQUE KEY unique_usuario_deporte_metrica (usuario, id_deporte, nombre_metrica)
);

CREATE TABLE Logro_Desbloqueado (
    id_logro INT PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255) NOT NULL,
    id_deporte INT NOT NULL,
    id_entrenamiento INT NULL,
    nombre_metrica VARCHAR(100) NULL,
    valor_umbral DOUBLE NULL,
    mensaje VARCHAR(500) NOT NULL,
    publicado BOOLEAN DEFAULT FALSE,
    visto_en DATETIME NULL,
    desbloqueado_en DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario),
    FOREIGN KEY (id_deporte) REFERENCES Deporte(id_deporte),
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    INDEX idx_usuario (usuario),
    INDEX idx_usuario_visto (usuario, visto_en)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 8. MÓDULO SOCIAL
-- ============================================

CREATE TABLE Calificaciones (
    id_calificacion INT PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255),
    usuario_calificado VARCHAR(255),
    calificacion INT,
    comentarios VARCHAR(255),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario),
    FOREIGN KEY (usuario_calificado) REFERENCES Usuario(usuario)
);

CREATE TABLE Publicacion (
    id_publicacion INT PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255),
    descripcion VARCHAR(255),
    fecha_publicacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    imagen TEXT NULL,
    tipo INT DEFAULT 1 COMMENT '1=normal, 2=logro',
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Likes (
    id_like INT PRIMARY KEY AUTO_INCREMENT,
    id_publicacion INT,
    usuario_like VARCHAR(255),
    fecha_like DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_publicacion) REFERENCES Publicacion(id_publicacion),
    FOREIGN KEY (usuario_like) REFERENCES Usuario(usuario)
);

CREATE TABLE Comentario (
    id_comentario INT PRIMARY KEY AUTO_INCREMENT,
    id_publicacion INT NOT NULL,
    usuario VARCHAR(255) NOT NULL,
    texto TEXT NOT NULL,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_publicacion) REFERENCES Publicacion(id_publicacion),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Seguidores (
    id_seguimiento INT PRIMARY KEY AUTO_INCREMENT,
    usuario_seguidor VARCHAR(255),
    usuario_seguido VARCHAR(255),
    fecha_seguimiento DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_seguidor) REFERENCES Usuario(usuario),
    FOREIGN KEY (usuario_seguido) REFERENCES Usuario(usuario),
    UNIQUE KEY unique_seguimiento (usuario_seguidor, usuario_seguido)
);

CREATE TABLE Notificacion (
    id_notificacion INT PRIMARY KEY AUTO_INCREMENT,
    usuario_destino VARCHAR(255),
    usuario_actor VARCHAR(255),
    tipo ENUM('LIKE', 'COMENTARIO', 'SEGUIDOR') NOT NULL,
    id_referencia INT NULL,
    mensaje VARCHAR(255),
    leido BOOLEAN DEFAULT FALSE,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_destino) REFERENCES Usuario(usuario),
    FOREIGN KEY (usuario_actor) REFERENCES Usuario(usuario)
);

-- ============================================
-- DATOS INICIALES
-- ============================================

INSERT INTO Estado (estado) VALUES
    ('Ciudad de México'), ('Aguascalientes'), ('Baja California'), ('Baja California Sur'), ('Campeche'),
    ('Chiapas'), ('Chihuahua'), ('Coahuila'), ('Colima'), ('Durango'), ('Guanajuato'), ('Guerrero'),
    ('Hidalgo'), ('Jalisco'), ('México'), ('Michoacán'), ('Morelos'), ('Nayarit'), ('Nuevo León'),
    ('Oaxaca'), ('Puebla'), ('Querétaro'), ('Quintana Roo'), ('San Luis Potosí'), ('Sinaloa'),
    ('Sonora'), ('Tabasco'), ('Tamaulipas'), ('Tlaxcala'), ('Veracruz'), ('Yucatán'), ('Zacatecas');

INSERT INTO Rol (rol) VALUES ('alumno'), ('entrenador');

INSERT INTO Deporte (nombre_deporte) VALUES
    ('Fútbol'),('Basketball'),('Natación'),('Running'),('Boxeo'),
    ('Tenis'),('Gimnasio'),('Ciclismo'),('Béisbol');

INSERT INTO Nivel (nombre_nivel) VALUES ('Principiante'), ('Intermedio'), ('Avanzado');

-- ============================================
-- PLANTILLAS DE MÉTRICAS POR DEPORTE
-- ============================================

-- FÚTBOL (id=1)
INSERT INTO Plantilla_Metricas_Deporte (id_deporte, nombre_metrica, etiqueta_display, unidad, fuente, es_por_serie, orden_display) VALUES
(1,'duracion_activa','Duración activa','min','health_connect',FALSE,1),
(1,'calorias','Calorías quemadas','kcal','health_connect',FALSE,2),
(1,'distancia','Distancia recorrida','m','health_connect',FALSE,3),
(1,'tiros_dentro_area_intentados','Tiros dentro del área (int.)','tiros','calculada',FALSE,4),
(1,'tiros_dentro_area_anotados','Tiros dentro del área (ano.)','goles','calculada',FALSE,5),
(1,'tiros_fuera_area_intentados','Tiros fuera del área (int.)','tiros','calculada',FALSE,6),
(1,'tiros_fuera_area_anotados','Tiros fuera del área (ano.)','goles','calculada',FALSE,7),
(1,'remates_cabeza_intentados','Remates de cabeza (int.)','rem.','calculada',FALSE,8),
(1,'remates_cabeza_anotados','Remates de cabeza (ano.)','goles','calculada',FALSE,9),
(1,'penales_intentados','Penales (int.)','pen.','calculada',FALSE,10),
(1,'penales_anotados','Penales (ano.)','goles','calculada',FALSE,11),
(1,'tiros_libres_intentados','Tiros libres directos (int.)','tiros','calculada',FALSE,12),
(1,'tiros_libres_anotados','Tiros libres directos (ano.)','goles','calculada',FALSE,13),
(1,'pases_cortos_intentados','Pases cortos (int.)','pases','calculada',FALSE,14),
(1,'pases_cortos_completados','Pases cortos (comp.)','pases','calculada',FALSE,15),
(1,'pases_largos_intentados','Pases largos (int.)','pases','calculada',FALSE,16),
(1,'pases_largos_completados','Pases largos (comp.)','pases','calculada',FALSE,17),
(1,'centros_intentados','Centros (int.)','cent.','calculada',FALSE,18),
(1,'centros_completados','Centros (comp.)','cent.','calculada',FALSE,19),
(1,'asistencias','Asistencias de gol','asist.','calculada',FALSE,20),
(1,'regates_intentados','Regates (int.)','reg.','calculada',FALSE,21),
(1,'regates_exitosos','Regates exitosos','reg.','calculada',FALSE,22),
(1,'entradas_ganadas','Entradas ganadas','ent.','calculada',FALSE,23),
(1,'intercepciones','Intercepciones','int.','calculada',FALSE,24),
(1,'despejes','Despejes','desp.','calculada',FALSE,25);

-- BASKETBALL (id=2)
INSERT INTO Plantilla_Metricas_Deporte (id_deporte, nombre_metrica, etiqueta_display, unidad, fuente, es_por_serie, orden_display) VALUES
(2,'duracion_activa','Duración activa','min','health_connect',FALSE,1),
(2,'calorias','Calorías quemadas','kcal','health_connect',FALSE,2),
(2,'tiros_libres_intentados','Tiros libres (int.)','TL','calculada',TRUE,3),
(2,'tiros_libres_anotados','Tiros libres (ano.)','TL','calculada',TRUE,4),
(2,'tiros_2pts_zona_intentados','T2 zona (int.)','pts','calculada',FALSE,5),
(2,'tiros_2pts_zona_anotados','T2 zona (ano.)','pts','calculada',FALSE,6),
(2,'tiros_2pts_media_intentados','T2 media distancia (int.)','pts','calculada',FALSE,7),
(2,'tiros_2pts_media_anotados','T2 media distancia (ano.)','pts','calculada',FALSE,8),
(2,'tiros_2pts_bandeja_intentados','Bandejas (int.)','pts','calculada',FALSE,9),
(2,'tiros_2pts_bandeja_anotados','Bandejas (ano.)','pts','calculada',FALSE,10),
(2,'tiros_3pts_esquina_intentados','T3 esquina (int.)','pts','calculada',FALSE,11),
(2,'tiros_3pts_esquina_anotados','T3 esquina (ano.)','pts','calculada',FALSE,12),
(2,'tiros_3pts_arco_intentados','T3 arco (int.)','pts','calculada',FALSE,13),
(2,'tiros_3pts_arco_anotados','T3 arco (ano.)','pts','calculada',FALSE,14),
(2,'puntos_totales','Puntos totales','pts','calculada',FALSE,15),
(2,'rebotes_ofensivos','Rebotes ofensivos','reb','calculada',FALSE,16),
(2,'rebotes_defensivos','Rebotes defensivos','reb','calculada',FALSE,17),
(2,'asistencias','Asistencias','ast','calculada',FALSE,18),
(2,'robos','Robos de balón','rob','calculada',FALSE,19),
(2,'tapones','Tapones','tap','calculada',FALSE,20),
(2,'perdidas','Pérdidas de balón','perd','calculada',FALSE,21);

-- NATACIÓN (id=3)
INSERT INTO Plantilla_Metricas_Deporte (id_deporte, nombre_metrica, etiqueta_display, unidad, fuente, es_por_serie, orden_display) VALUES
(3,'duracion_activa','Duración activa','min','health_connect',FALSE,1),
(3,'calorias','Calorías quemadas','kcal','health_connect',FALSE,2),
(3,'distancia','Distancia total','m','health_connect',FALSE,3),
(3,'vueltas_completadas','Vueltas completadas','vtas','calculada',FALSE,4),
(3,'tiempo_mejor_largo_seg','Mejor tiempo por largo','seg','calculada',TRUE,5),
(3,'tiempo_promedio_largo_seg','Tiempo promedio por largo','seg','calculada',FALSE,6),
(3,'brazadas_por_largo','Brazadas por largo (prom.)','braz','calculada',TRUE,7),
(3,'largos_crol','Largos en crol','vtas','calculada',FALSE,8),
(3,'largos_espalda','Largos en espalda','vtas','calculada',FALSE,9),
(3,'largos_pecho','Largos en pecho','vtas','calculada',FALSE,10),
(3,'largos_mariposa','Largos en mariposa','vtas','calculada',FALSE,11),
(3,'indice_eficiencia','Índice de eficiencia (SWOLF)','pts','calculada',FALSE,12);

-- RUNNING (id=4)
INSERT INTO Plantilla_Metricas_Deporte (id_deporte, nombre_metrica, etiqueta_display, unidad, fuente, es_por_serie, orden_display) VALUES
(4,'duracion_activa','Duración activa','min','health_connect',FALSE,1),
(4,'calorias','Calorías quemadas','kcal','health_connect',FALSE,2),
(4,'distancia','Distancia total','m','health_connect',FALSE,3),
(4,'pasos','Pasos totales','pasos','health_connect',FALSE,4),
(4,'velocidad_promedio','Velocidad promedio','m/s','health_connect',FALSE,5),
(4,'elevacion','Elevación ganada','m','health_connect',FALSE,6),
(4,'velocidad_maxima_ms','Velocidad máxima','m/s','calculada',FALSE,7),
(4,'ritmo_promedio_min_km','Ritmo promedio','min/km','calculada',FALSE,8),
(4,'ritmo_mejor_km','Mejor ritmo por km','min/km','calculada',FALSE,9),
(4,'intervalos_completados','Intervalos completados','int.','calculada',TRUE,10),
(4,'distancia_mejor_intervalo','Distancia mejor intervalo','m','calculada',TRUE,11),
(4,'cadencia_promedio','Cadencia promedio','pasos/m','calculada',FALSE,12);

-- BOXEO (id=5)
INSERT INTO Plantilla_Metricas_Deporte (id_deporte, nombre_metrica, etiqueta_display, unidad, fuente, es_por_serie, orden_display) VALUES
(5,'duracion_activa','Duración activa','min','health_connect',FALSE,1),
(5,'calorias','Calorías quemadas','kcal','health_connect',FALSE,2),
(5,'rounds_completados','Rounds completados','rounds','calculada',FALSE,3),
(5,'jabs_intentados','Jabs (int.)','golpes','calculada',TRUE,4),
(5,'jabs_conectados','Jabs conectados','golpes','calculada',TRUE,5),
(5,'directos_intentados','Directos (int.)','golpes','calculada',TRUE,6),
(5,'directos_conectados','Directos conectados','golpes','calculada',TRUE,7),
(5,'ganchos_intentados','Ganchos (int.)','golpes','calculada',TRUE,8),
(5,'ganchos_conectados','Ganchos conectados','golpes','calculada',TRUE,9),
(5,'uppercuts_intentados','Uppercuts (int.)','golpes','calculada',TRUE,10),
(5,'uppercuts_conectados','Uppercuts conectados','golpes','calculada',TRUE,11),
(5,'golpes_cuerpo_intentados','Golpes al cuerpo (int.)','golpes','calculada',FALSE,12),
(5,'golpes_cuerpo_conectados','Golpes al cuerpo (con.)','golpes','calculada',FALSE,13),
(5,'golpes_cabeza_intentados','Golpes a la cabeza (int.)','golpes','calculada',FALSE,14),
(5,'golpes_cabeza_conectados','Golpes a la cabeza (con.)','golpes','calculada',FALSE,15),
(5,'golpes_totales_intentados','Golpes totales (int.)','golpes','calculada',FALSE,16),
(5,'golpes_totales_conectados','Golpes totales (con.)','golpes','calculada',FALSE,17);

-- TENIS (id=6)
INSERT INTO Plantilla_Metricas_Deporte (id_deporte, nombre_metrica, etiqueta_display, unidad, fuente, es_por_serie, orden_display) VALUES
(6,'duracion_activa','Duración activa','min','health_connect',FALSE,1),
(6,'calorias','Calorías quemadas','kcal','health_connect',FALSE,2),
(6,'distancia','Distancia recorrida','m','health_connect',FALSE,3),
(6,'sets_ganados','Sets ganados','sets','calculada',FALSE,4),
(6,'sets_perdidos','Sets perdidos','sets','calculada',FALSE,5),
(6,'games_ganados','Games ganados','games','calculada',FALSE,6),
(6,'primeros_saques_intentados','Primeros saques (int.)','saq.','calculada',TRUE,7),
(6,'primeros_saques_dentro','Primeros saques (dentro)','saq.','calculada',TRUE,8),
(6,'puntos_ganados_1er_saque','Pts ganados con 1er saque','pts','calculada',TRUE,9),
(6,'segundos_saques_intentados','Segundos saques (int.)','saq.','calculada',TRUE,10),
(6,'segundos_saques_dentro','Segundos saques (dentro)','saq.','calculada',TRUE,11),
(6,'puntos_ganados_2do_saque','Pts ganados con 2do saque','pts','calculada',TRUE,12),
(6,'dobles_faltas','Dobles faltas','pts','calculada',FALSE,13),
(6,'aces','Aces','pts','calculada',FALSE,14),
(6,'winners_derecha','Winners de derecha','pts','calculada',FALSE,15),
(6,'winners_reves','Winners de revés','pts','calculada',FALSE,16),
(6,'errores_no_forzados_derecha','ENF de derecha','pts','calculada',FALSE,17),
(6,'errores_no_forzados_reves','ENF de revés','pts','calculada',FALSE,18),
(6,'winners_totales','Winners totales','pts','calculada',FALSE,19),
(6,'errores_no_forzados_total','ENF totales','pts','calculada',FALSE,20),
(6,'subidas_a_red','Subidas a la red','vec.','calculada',FALSE,21),
(6,'puntos_ganados_en_red','Puntos ganados en red','pts','calculada',FALSE,22);

-- GIMNASIO (id=7)
INSERT INTO Plantilla_Metricas_Deporte (id_deporte, nombre_metrica, etiqueta_display, unidad, fuente, es_por_serie, orden_display) VALUES
(7,'duracion_activa','Duración activa','min','health_connect',FALSE,1),
(7,'calorias','Calorías quemadas','kcal','health_connect',FALSE,2),
(7,'volumen_total_kg','Volumen total','kg','calculada',FALSE,3),
(7,'repeticiones_totales','Repeticiones totales','reps','calculada',FALSE,4),
(7,'series_completadas','Series completadas','ser.','calculada',FALSE,5),
(7,'series_parciales','Series parciales','ser.','calculada',FALSE,6),
(7,'volumen_pecho','Volumen pecho','kg','calculada',FALSE,7),
(7,'volumen_espalda','Volumen espalda','kg','calculada',FALSE,8),
(7,'volumen_piernas','Volumen piernas','kg','calculada',FALSE,9),
(7,'volumen_hombros','Volumen hombros','kg','calculada',FALSE,10),
(7,'volumen_brazos','Volumen brazos','kg','calculada',FALSE,11),
(7,'peso_maximo_levantado','Peso máximo levantado','kg','calculada',FALSE,12),
(7,'rpe_promedio','RPE promedio (esfuerzo)','/10','calculada',FALSE,13);

-- CICLISMO (id=8)
INSERT INTO Plantilla_Metricas_Deporte (id_deporte, nombre_metrica, etiqueta_display, unidad, fuente, es_por_serie, orden_display) VALUES
(8,'duracion_activa','Duración activa','min','health_connect',FALSE,1),
(8,'calorias','Calorías quemadas','kcal','health_connect',FALSE,2),
(8,'distancia','Distancia','m','health_connect',FALSE,3),
(8,'velocidad_promedio','Velocidad promedio','m/s','health_connect',FALSE,4),
(8,'elevacion','Elevación ganada','m','health_connect',FALSE,5),
(8,'velocidad_maxima','Velocidad máxima','m/s','calculada',FALSE,6),
(8,'cadencia_promedio','Cadencia promedio','rpm','calculada',FALSE,7),
(8,'intervalos_completados','Intervalos completados','int.','calculada',TRUE,8),
(8,'distancia_mejor_intervalo','Distancia mejor intervalo','m','calculada',TRUE,9),
(8,'tiempo_zona_aerobica_min','Tiempo en zona aeróbica','min','calculada',FALSE,10),
(8,'tiempo_zona_umbral_min','Tiempo en zona umbral','min','calculada',FALSE,11),
(8,'tiempo_zona_anaerobica_min','Tiempo en zona anaeróbica','min','calculada',FALSE,12),
(8,'subidas_completadas','Subidas completadas','sub.','calculada',FALSE,13),
(8,'desnivel_positivo','Desnivel positivo acumulado','m','calculada',FALSE,14);

-- BÉISBOL (id=9)
INSERT INTO Plantilla_Metricas_Deporte (id_deporte, nombre_metrica, etiqueta_display, unidad, fuente, es_por_serie, orden_display) VALUES
(9,'duracion_activa','Duración activa','min','health_connect',FALSE,1),
(9,'calorias','Calorías quemadas','kcal','health_connect',FALSE,2),
(9,'turnos_al_bate','Turnos al bate','AB','calculada',TRUE,3),
(9,'hits','Hits totales','hits','calculada',TRUE,4),
(9,'sencillos','Sencillos','hits','calculada',TRUE,5),
(9,'dobles','Dobles','hits','calculada',TRUE,6),
(9,'triples','Triples','hits','calculada',TRUE,7),
(9,'home_runs','Home runs','HR','calculada',TRUE,8),
(9,'carreras_anotadas','Carreras anotadas','R','calculada',FALSE,9),
(9,'carreras_impulsadas','Carreras impulsadas (RBI)','RBI','calculada',FALSE,10),
(9,'bases_por_bolas','Bases por bolas (BB)','BB','calculada',FALSE,11),
(9,'ponches_bateando','Ponches como bateador','K','calculada',FALSE,12),
(9,'innings_lanzados','Innings lanzados','IP','calculada',TRUE,13),
(9,'ponches_lanzando','Ponches como lanzador','K','calculada',TRUE,14),
(9,'lanzamientos_totales','Lanzamientos totales','lanz.','calculada',TRUE,15),
(9,'strikes_lanzados','Strikes lanzados','str.','calculada',TRUE,16),
(9,'bolas_lanzadas','Bolas lanzadas','bolas','calculada',TRUE,17),
(9,'hits_permitidos','Hits permitidos','hits','calculada',TRUE,18),
(9,'carreras_permitidas','Carreras permitidas','R','calculada',TRUE,19),
(9,'outs_defensivos','Outs defensivos','outs','calculada',FALSE,20),
(9,'errores_cometidos','Errores cometidos','err.','calculada',FALSE,21),
(9,'doble_plays','Doble plays','DP','calculada',FALSE,22),
(9,'bases_robadas','Bases robadas','SB','calculada',FALSE,23);
