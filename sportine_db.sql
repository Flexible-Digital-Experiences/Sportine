drop database if exists sportine_db;
CREATE DATABASE sportine_db;
USE sportine_db;

-- ============================================
-- 1. TABLAS BASE
-- ============================================

CREATE TABLE Estado(
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
    usuario VARCHAR(255),
    estatura FLOAT,
    peso FLOAT,
    lesiones VARCHAR(255),
    padecimientos VARCHAR(255),
    foto_perfil TEXT,
    fecha_nacimiento DATE,
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Informacion_Entrenador (
    usuario VARCHAR(255),
    costo_mensualidad INT,
    descripcion_perfil VARCHAR(255),
    foto_perfil TEXT,
    limite_alumnos INT,
    -- Platform Partner Paypal
    merchant_id VARCHAR(255) NULL COMMENT 'PayPal Merchant ID del entrenador (partner_merchant_id)',
    merchant_id_in_paypal VARCHAR(255) NULL COMMENT 'Merchant ID en PayPal (payer_id)',
    paypal_email_confirmed VARCHAR(255) NULL COMMENT 'Email confirmado en PayPal del entrenador',
    tracking_id VARCHAR(255) NULL COMMENT 'Tracking ID del proceso de onboarding',
    onboarding_status ENUM('pending', 'completed', 'failed', 'not_started') DEFAULT 'not_started' COMMENT 'Estado del onboarding PayPal',
    onboarding_link TEXT NULL COMMENT 'Link de onboarding generado por PayPal',
    fecha_onboarding DATE NULL COMMENT 'Fecha en que completó el onboarding',
    permissions_granted TEXT NULL COMMENT 'JSON con permisos otorgados por el entrenador',
    
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario),
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_onboarding_status (onboarding_status)
);

-- ============================================
-- TABLAS DE SUSCRIPCIONES V2 - PLATFORM PARTNER
-- ============================================

-- Suscripciones de estudiantes a entrenadores (multiparty)
CREATE TABLE Estudiante_Suscripcion_Entrenador (
    id_suscripcion INT PRIMARY KEY AUTO_INCREMENT,
    usuario_estudiante VARCHAR(255) NOT NULL,
    usuario_entrenador VARCHAR(255) NOT NULL,
    id_deporte INT NOT NULL,
    
    -- Información de la suscripción en PayPal
    subscription_id VARCHAR(255) NOT NULL COMMENT 'ID de suscripción multiparty en PayPal',
    plan_id VARCHAR(255) COMMENT 'ID del plan creado por el entrenador',
    
    -- Montos
    monto_total DECIMAL(10,2) NOT NULL COMMENT 'Monto total que paga el estudiante',
    monto_entrenador DECIMAL(10,2) NOT NULL COMMENT 'Monto que recibe el entrenador (después de comisión)',
    monto_comision_sportine DECIMAL(10,2) NOT NULL COMMENT 'Comisión de Sportine',
    porcentaje_comision DECIMAL(5,2) DEFAULT 10.00 COMMENT 'Porcentaje de comisión aplicado (ej: 10.00 = 10%)',
    moneda VARCHAR(3) DEFAULT 'MXN',
    
    -- Estados
    status_suscripcion ENUM('active', 'cancelled', 'expired', 'suspended', 'pending') DEFAULT 'pending' COMMENT 'Estado de la suscripción',
    
    -- Fechas
    fecha_inicio_suscripcion DATE NULL COMMENT 'Fecha en que se activó la suscripción',
    fecha_proximo_pago DATE NULL COMMENT 'Fecha del próximo cobro mensual',
    fecha_fin_suscripcion DATE NULL COMMENT 'Fecha de cancelación/expiración (NULL si está activa)',
    fecha_cancelacion DATE NULL COMMENT 'Fecha específica de cancelación',
    motivo_cancelacion TEXT NULL COMMENT 'Razón de la cancelación',
    
    -- Metadata
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_estudiante) REFERENCES Usuario(usuario),
    FOREIGN KEY (usuario_entrenador) REFERENCES Usuario(usuario),
    FOREIGN KEY (id_deporte) REFERENCES Deporte(id_deporte),
    
    -- Índices para optimización
    INDEX idx_subscription_id (subscription_id),
    INDEX idx_estudiante (usuario_estudiante),
    INDEX idx_entrenador (usuario_entrenador),
    INDEX idx_status (status_suscripcion),
    INDEX idx_fecha_proximo_pago (fecha_proximo_pago),
    
    -- Un estudiante solo puede tener UNA suscripción activa con un entrenador para un deporte específico
    UNIQUE KEY unique_estudiante_entrenador_deporte_activo (usuario_estudiante, usuario_entrenador, id_deporte, status_suscripcion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Suscripciones multiparty: estudiantes pagan a entrenadores con comisión a Sportine';

-- Historial de pagos mensuales de estudiantes a entrenadores
CREATE TABLE Historial_Pagos_Estudiante_Entrenador (
    id_pago INT PRIMARY KEY AUTO_INCREMENT,
    id_suscripcion INT NOT NULL COMMENT 'FK a Estudiante_Suscripcion_Entrenador',
    
    -- Información del pago en PayPal
    paypal_transaction_id VARCHAR(255) COMMENT 'ID de transacción en PayPal',
    paypal_payment_id VARCHAR(255) COMMENT 'Payment ID de PayPal',
    paypal_sale_id VARCHAR(255) COMMENT 'Sale ID de PayPal',
    
    -- Montos del pago específico
    monto_total DECIMAL(10,2) NOT NULL COMMENT 'Monto total pagado por el estudiante',
    monto_entrenador DECIMAL(10,2) NOT NULL COMMENT 'Monto recibido por el entrenador',
    monto_comision_sportine DECIMAL(10,2) NOT NULL COMMENT 'Comisión recibida por Sportine',
    moneda VARCHAR(3) DEFAULT 'MXN',
    
    -- Estado del pago
    status_pago VARCHAR(50) COMMENT 'COMPLETED, PENDING, FAILED, REFUNDED, CANCELLED',
    
    -- Fechas
    fecha_pago DATETIME COMMENT 'Fecha y hora en que se procesó el pago',
    fecha_esperada_pago DATE COMMENT 'Fecha en que se esperaba el pago mensual',
    
    -- Datos del webhook de PayPal
    evento_webhook TEXT COMMENT 'JSON completo del webhook de PayPal',
    tipo_evento VARCHAR(100) COMMENT 'Tipo de evento: PAYMENT.SALE.COMPLETED, etc.',
    
    -- Metadata
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_suscripcion) REFERENCES Estudiante_Suscripcion_Entrenador(id_suscripcion),
    
    INDEX idx_suscripcion (id_suscripcion),
    INDEX idx_transaction_id (paypal_transaction_id),
    INDEX idx_status (status_pago),
    INDEX idx_fecha_pago (fecha_pago),
    INDEX idx_tipo_evento (tipo_evento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Registro de cada pago mensual de estudiantes a entrenadores';

-- Comisiones de Sportine por cada transacción
CREATE TABLE Comisiones_Sportine (
    id_comision INT PRIMARY KEY AUTO_INCREMENT,
    id_pago INT NOT NULL COMMENT 'FK a Historial_Pagos_Estudiante_Entrenador',
    
    -- Montos de la comisión
    monto_comision DECIMAL(10,2) NOT NULL COMMENT 'Monto de la comisión',
    moneda VARCHAR(3) DEFAULT 'MXN',
    porcentaje_aplicado DECIMAL(5,2) COMMENT 'Porcentaje aplicado en esta transacción',
    
    -- Estado del depósito a Sportine
    status_deposito ENUM('pending', 'deposited', 'failed') DEFAULT 'pending' COMMENT 'Estado del depósito de PayPal a Sportine',
    fecha_deposito_esperado DATE COMMENT 'PayPal deposita las platform fees diariamente',
    fecha_deposito_real DATETIME NULL COMMENT 'Fecha real en que se depositó',
    
    -- Información de PayPal
    paypal_payout_batch_id VARCHAR(255) COMMENT 'ID del batch de payout de PayPal',
    paypal_payout_item_id VARCHAR(255) COMMENT 'ID del item individual de payout',
    
    -- Metadata
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_pago) REFERENCES Historial_Pagos_Estudiante_Entrenador(id_pago),
    
    INDEX idx_pago (id_pago),
    INDEX idx_status (status_deposito),
    INDEX idx_fecha_deposito (fecha_deposito_esperado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Comisiones que Sportine recibe de cada transacción';

-- ============================================
-- 3. RELACIONES Y CONTRATOS
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
    fin_mensualidad DATE,
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
    status_solicitud ENUM ('En_revisión','Aprobada', 'Rechazada'),
    FOREIGN KEY (usuario_alumno) REFERENCES Usuario(usuario),
    FOREIGN KEY (usuario_entrenador) REFERENCES Usuario(usuario),
    FOREIGN KEY (id_deporte) REFERENCES Deporte(id_deporte)
);

-- ============================================
-- 4. MÓDULO DE ENTRENAMIENTO
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
    id_entrenamiento INT,
    id_catalogo INT,
    nombre_personalizado VARCHAR(255),
    usuario VARCHAR(255),
    nombre_ejercicio VARCHAR(255) NOT NULL,
    series INT,
    repeticiones INT,
    peso FLOAT,
    duracion INT,
    distancia FLOAT,
    status_ejercicio ENUM('pendiente', 'completado', 'omitido') DEFAULT 'pendiente',
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Progreso_Entrenamiento (
    id_progreso INT PRIMARY KEY AUTO_INCREMENT,
    id_entrenamiento INT,
    usuario VARCHAR(255),
    fecha_inicio DATETIME,
    fecha_finalizacion DATETIME,
    completado BOOLEAN,
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Feedback_Entrenamiento (
    id_feedback INT PRIMARY KEY AUTO_INCREMENT,
    id_entrenamiento INT,
    usuario VARCHAR(255),
    nivel_cansancio INT,
    dificultad_percibida INT,
    estado_animo VARCHAR(50),
    comentarios VARCHAR(255),
    fecha_feedback DATETIME,
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

-- ============================================
-- 5. MÓDULO SOCIAL Y OTROS
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
    fecha_publicacion DATETIME,
    imagen TEXT,
    tipo INT DEFAULT 1,
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Likes (
    id_like INT PRIMARY KEY AUTO_INCREMENT,
    id_publicacion INT,
    usuario_like VARCHAR(255),
    fecha_like DATETIME,
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
    id_referencia INT,              
    mensaje VARCHAR(255),           
    leido BOOLEAN DEFAULT FALSE,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_destino) REFERENCES Usuario(usuario),
    FOREIGN KEY (usuario_actor) REFERENCES Usuario(usuario)
);

-- ============================================
-- 6. TABLAS ESTADÍSTICAS
-- ============================================

CREATE TABLE Estadisticas_Futbol (
    id_estadistica INT PRIMARY KEY AUTO_INCREMENT,
    id_entrenamiento INT,
    usuario VARCHAR(255),
    goles_anotados INT DEFAULT 0,
    asistencias INT DEFAULT 0,
    pases_completados INT DEFAULT 0,
    pases_intentados INT DEFAULT 0,
    tiros_a_gol INT DEFAULT 0,
    tiros_esquina INT DEFAULT 0,
    faltas_cometidas INT DEFAULT 0,
    tarjetas_amarillas INT DEFAULT 0,
    tarjetas_rojas INT DEFAULT 0,
    distancia_recorrida_km FLOAT,
    tiempo_jugado_minutos INT,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Estadisticas_Basketball (
    id_estadistica INT PRIMARY KEY AUTO_INCREMENT,
    id_entrenamiento INT,
    usuario VARCHAR(255),
    puntos_anotados INT DEFAULT 0,
    tiros_libres_anotados INT DEFAULT 0,
    tiros_libres_intentados INT DEFAULT 0,
    tiros_2_puntos_anotados INT DEFAULT 0,
    tiros_2_puntos_intentados INT DEFAULT 0,
    tiros_3_puntos_anotados INT DEFAULT 0,
    tiros_3_puntos_intentados INT DEFAULT 0,
    rebotes_defensivos INT DEFAULT 0,
    rebotes_ofensivos INT DEFAULT 0,
    asistencias INT DEFAULT 0,
    robos INT DEFAULT 0,
    bloqueos INT DEFAULT 0,
    perdidas INT DEFAULT 0,
    faltas_cometidas INT DEFAULT 0,
    tiempo_jugado_minutos INT,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Estadisticas_Natacion (
    id_estadistica INT PRIMARY KEY AUTO_INCREMENT,
    id_entrenamiento INT,
    usuario VARCHAR(255),
    distancia_total_metros FLOAT,
    tiempo_total_minutos FLOAT,
    estilo_natacion VARCHAR(50),
    numero_vueltas INT,
    tiempo_mejor_vuelta FLOAT,
    tiempo_promedio_vuelta FLOAT,
    calorias_quemadas INT,
    frecuencia_cardiaca_promedio INT,
    frecuencia_cardiaca_maxima INT,
    brazadas_por_largo INT,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Estadisticas_Running (
    id_estadistica INT PRIMARY KEY AUTO_INCREMENT,
    id_entrenamiento INT,
    usuario VARCHAR(255),
    distancia_km FLOAT,
    tiempo_minutos FLOAT,
    ritmo_promedio_min_km FLOAT, 
    velocidad_promedio_kmh FLOAT,
    velocidad_maxima_kmh FLOAT,
    calorias_quemadas INT,
    elevacion_ganada_metros FLOAT,
    elevacion_perdida_metros FLOAT,
    frecuencia_cardiaca_promedio INT,
    frecuencia_cardiaca_maxima INT,
    cadencia_promedio INT, 
    temperatura_celsius FLOAT,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Estadisticas_Boxeo (
    id_estadistica INT PRIMARY KEY AUTO_INCREMENT,
    id_entrenamiento INT,
    usuario VARCHAR(255),
    rounds_completados INT,
    duracion_round_minutos INT,
    golpes_totales INT,
    jabs INT DEFAULT 0,
    directos INT DEFAULT 0,
    ganchos INT DEFAULT 0,
    uppercuts INT DEFAULT 0,
    golpes_al_saco INT,
    tiempo_sparring_minutos INT,
    calorias_quemadas INT,
    frecuencia_cardiaca_promedio INT,
    frecuencia_cardiaca_maxima INT,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Estadisticas_Tenis (
    id_estadistica INT PRIMARY KEY AUTO_INCREMENT,
    id_entrenamiento INT,
    usuario VARCHAR(255),
    sets_ganados INT DEFAULT 0,
    sets_perdidos INT DEFAULT 0,
    games_ganados INT DEFAULT 0,
    games_perdidos INT DEFAULT 0,
    aces INT DEFAULT 0,
    dobles_faltas INT DEFAULT 0,
    primer_servicio_porcentaje FLOAT,
    puntos_ganados_primer_servicio INT DEFAULT 0,
    puntos_ganados_segundo_servicio INT DEFAULT 0,
    winners INT DEFAULT 0,
    errores_no_forzados INT DEFAULT 0,
    puntos_break_convertidos INT DEFAULT 0,
    puntos_break_oportunidades INT DEFAULT 0,
    distancia_recorrida_km FLOAT,
    tiempo_juego_minutos INT,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Estadisticas_Gimnasio (
    id_estadistica INT PRIMARY KEY AUTO_INCREMENT,
    id_entrenamiento INT,
    usuario VARCHAR(255),
    peso_total_levantado_kg FLOAT,
    repeticiones_totales INT,
    series_totales INT,
    ejercicios_completados INT,
    peso_maximo_levantado_kg FLOAT,
    ejercicio_peso_maximo VARCHAR(100),
    tiempo_descanso_promedio_segundos INT,
    calorias_quemadas INT,
    frecuencia_cardiaca_promedio INT,
    frecuencia_cardiaca_maxima INT,
    zona_muscular_trabajada VARCHAR(100), 
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Estadisticas_Ciclismo (
    id_estadistica INT PRIMARY KEY AUTO_INCREMENT,
    id_entrenamiento INT,
    usuario VARCHAR(255),
    distancia_km FLOAT,
    tiempo_minutos FLOAT,
    velocidad_promedio_kmh FLOAT,
    velocidad_maxima_kmh FLOAT,
    elevacion_ganada_metros FLOAT,
    elevacion_perdida_metros FLOAT,
    cadencia_promedio INT, 
    cadencia_maxima INT,
    potencia_promedio_watts INT,
    potencia_maxima_watts INT,
    calorias_quemadas INT,
    frecuencia_cardiaca_promedio INT,
    frecuencia_cardiaca_maxima INT,
    temperatura_celsius FLOAT,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Estadisticas_Beisbol (
    id_estadistica INT PRIMARY KEY AUTO_INCREMENT,
    id_entrenamiento INT,
    usuario VARCHAR(255),
    bateo_hits INT DEFAULT 0,
    bateo_intentos INT DEFAULT 0,
    carreras_anotadas INT DEFAULT 0,
    carreras_impulsadas INT DEFAULT 0,
    home_runs INT DEFAULT 0,
    dobles INT DEFAULT 0,
    triples INT DEFAULT 0,
    bases_robadas INT DEFAULT 0,
    ponches_bateando INT DEFAULT 0,
    boletos_recibidos INT DEFAULT 0,
    innings_lanzados FLOAT,
    ponches_lanzando INT DEFAULT 0,
    boletos_otorgados INT DEFAULT 0,
    hits_permitidos INT DEFAULT 0,
    carreras_permitidas INT DEFAULT 0,
    outs_defensivos INT DEFAULT 0,
    asistencias_defensivas INT DEFAULT 0,
    errores_defensivos INT DEFAULT 0,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

-- ============================================
-- INSERTS DE DATOS INICIALES
-- ============================================

-- ESTADOS
INSERT INTO Estado (estado) VALUES
    ('Ciudad de México'), ('Aguascalientes'), ('Baja California'), ('Baja California Sur'), ('Campeche'),
    ('Chiapas'), ('Chihuahua'), ('Coahuila'), ('Colima'), ('Durango'), ('Guanajuato'), ('Guerrero'),
    ('Hidalgo'), ('Jalisco'), ('México'), ('Michoacán'), ('Morelos'), ('Nayarit'), ('Nuevo León'),
    ('Oaxaca'), ('Puebla'), ('Querétaro'), ('Quintana Roo'), ('San Luis Potosí'), ('Sinaloa'),
    ('Sonora'), ('Tabasco'), ('Tamaulipas'), ('Tlaxcala'), ('Veracruz'), ('Yucatán'), ('Zacatecas');

-- ROLES
INSERT INTO Rol (rol) VALUES ('alumno'), ('entrenador');

-- DEPORTES
INSERT INTO Deporte (nombre_deporte) VALUES 
    ('Fútbol'), ('Basketball'), ('Natación'), ('Running'), ('Boxeo'),
    ('Tenis'), ('Gimnasio'), ('Ciclismo'), ('Béisbol');

-- NIVELES
INSERT INTO Nivel (nombre_nivel) VALUES ('Principiante'), ('Intermedio'), ('Avanzado');

select*from Usuario;