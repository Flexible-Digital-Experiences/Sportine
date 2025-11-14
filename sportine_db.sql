use sportine_db;
CREATE TABLE Usuario (
    usuario VARCHAR(255) PRIMARY KEY,
    contraseña VARCHAR(255),
    nombre VARCHAR(255),
    apellidos VARCHAR(255),
    sexo VARCHAR(50),
    estado VARCHAR(100),
    ciudad VARCHAR(100)
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


CREATE TABLE Informacion_Alumno (
    usuario VARCHAR(255),
    estatura FLOAT,
    peso FLOAT,
    lesiones VARCHAR(255),
    nivel VARCHAR(100),
    padecimientos VARCHAR(255),
    foto_perfil TEXT,
    fecha_nacimiento DATE,
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Alumno_Deporte (
    id_alumno_deporte INT PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255),
    deporte VARCHAR(100),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Informacion_Entrenador (
    usuario VARCHAR(255),
    costo_mensualidad INT,
    descripcion_perfil VARCHAR(255),
    foto_perfil TEXT,
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);


CREATE TABLE Tarjeta (
    id_tarjeta INT PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255),
    numero_tarjeta VARCHAR(50),
    fecha_caducidad DATE,
    nombre_titular VARCHAR(100),
    apellidos_titular VARCHAR(100),
    direccion_facturacion VARCHAR(255),
    localidad VARCHAR(100),
    codigo_postal VARCHAR(20),
    pais VARCHAR(100),
    telefono VARCHAR(20),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);


CREATE TABLE Entrenador_Alumno (
    id_relacion INT PRIMARY KEY AUTO_INCREMENT,
    usuario_entrenador VARCHAR(255),
    usuario_alumno VARCHAR(255),
    fecha_inicio DATE,
    status_relacion VARCHAR(50),
    FOREIGN KEY (usuario_entrenador) REFERENCES Usuario(usuario),
    FOREIGN KEY (usuario_alumno) REFERENCES Usuario(usuario)
);

CREATE TABLE Solicitudes_Entrenamiento (
    id_solicitud INT PRIMARY KEY AUTO_INCREMENT,
    usuario_alumno VARCHAR(255),
    usuario_entrenador VARCHAR(255),
    descripcion_solicitud VARCHAR(255),
    fecha_solicitud DATE,
    status_solicitud VARCHAR(50),
    FOREIGN KEY (usuario_alumno) REFERENCES Usuario(usuario),
    FOREIGN KEY (usuario_entrenador) REFERENCES Usuario(usuario)
);

CREATE TABLE Contrato (
    id_contrato INT PRIMARY KEY AUTO_INCREMENT,
    usuario_alumno VARCHAR(255),
    usuario_entrenador VARCHAR(255),
    fecha_inicio DATE,
    fecha_fin DATE,
    estado_contrato VARCHAR(50),
    FOREIGN KEY (usuario_alumno) REFERENCES Usuario(usuario),
    FOREIGN KEY (usuario_entrenador) REFERENCES Usuario(usuario)
);


CREATE TABLE Entrenamiento (
    id_entrenamiento INT PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255),
    titulo_entrenamiento VARCHAR(255),
    objetivo VARCHAR(255),
    fecha_entrenamiento DATE,
    hora_entrenamiento TIME,
    dificultad VARCHAR(50),
    estado_entrenamiento ENUM('pendiente', 'en_progreso', 'finalizado'),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Catalogo_Ejercicios (
    id_catalogo INT PRIMARY KEY AUTO_INCREMENT,
    deporte VARCHAR(100),
    nombre_ejercicio VARCHAR(255),
    descripcion VARCHAR(255),
    tipo_medida VARCHAR(50)
);

CREATE TABLE Ejercicios_Asignados (
    id_asignado INT PRIMARY KEY AUTO_INCREMENT,
    id_entrenamiento INT,
    id_catalogo INT,
    usuario VARCHAR(255),
    repeticiones INT,
    series INT,
    duracion INT,
    distancia FLOAT,
    peso FLOAT,
    status_ejercicio ENUM('pendiente', 'completado', 'omitido'),
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id_entrenamiento),
    FOREIGN KEY (id_catalogo) REFERENCES Catalogo_Ejercicios(id_catalogo),
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


CREATE TABLE Calificaciones (
    id_calificacion INT PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255),
    calificacion INT,
    comentarios VARCHAR(255),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);

CREATE TABLE Publicacion (
    id_publicacion INT PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255),
    descripcion VARCHAR(255),
    fecha_publicacion DATE,
    imagen TEXT,
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

CREATE TABLE Amistad(
	id_amistad INT PRIMARY KEY AUTO_INCREMENT,
    usuario_1 VARCHAR(255),
    usuario_2 VARCHAR(255),
	FOREIGN KEY (usuario_1) REFERENCES Usuario(usuario),
    FOREIGN KEY (usuario_2) REFERENCES Usuario(usuario),
    -- Para evitar repetidos en la tabla
    UNIQUE(usuario_1, usuario_2) 
);

CREATE TABLE Entrenador_Deporte (
    id_entrenador_deporte INT PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255),
    deporte VARCHAR(100),
    FOREIGN KEY (usuario) REFERENCES Usuario(usuario)
);


INSERT INTO Rol (rol) VALUES ('alumno');
INSERT INTO Rol (rol) VALUES ('entrenador');


