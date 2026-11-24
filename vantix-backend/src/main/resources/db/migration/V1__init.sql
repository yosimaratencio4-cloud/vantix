CREATE TABLE usuario (
  id               BIGSERIAL PRIMARY KEY,
  nombre           VARCHAR(100) NOT NULL,
  correo           VARCHAR(150) NOT NULL UNIQUE,
  hash_contrasena  VARCHAR(255) NOT NULL,
  rol              VARCHAR(20)  NOT NULL CHECK (rol IN ('TECNICO','ADMIN')),
  activo           BOOLEAN      NOT NULL DEFAULT TRUE,
  creado_en        TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE accion_catalogo (
  id                 BIGSERIAL PRIMARY KEY,
  codigo             VARCHAR(60)  NOT NULL UNIQUE,
  herramienta        VARCHAR(30)  NOT NULL,
  descripcion        VARCHAR(255) NOT NULL,
  esquema_parametros JSONB        NOT NULL DEFAULT '{}',
  riesgo             VARCHAR(10)  NOT NULL CHECK (riesgo IN ('BAJO','MEDIO','ALTO')),
  habilitada         BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE ejecucion (
  id              BIGSERIAL PRIMARY KEY,
  accion_id       BIGINT      NOT NULL REFERENCES accion_catalogo(id),
  usuario_id      BIGINT      NOT NULL REFERENCES usuario(id),
  parametros      JSONB       NOT NULL DEFAULT '{}',
  comando_final   TEXT        NOT NULL,
  estado          VARCHAR(15) NOT NULL DEFAULT 'PENDIENTE'
                  CHECK (estado IN ('PENDIENTE','RECHAZADA','EJECUTANDO','TERMINADA','ERROR','TIMEOUT')),
  aprobada_por    BIGINT      REFERENCES usuario(id),
  solicitada_en   TIMESTAMPTZ NOT NULL DEFAULT now(),
  aprobada_en     TIMESTAMPTZ,
  iniciada_en     TIMESTAMPTZ,
  terminada_en    TIMESTAMPTZ,
  codigo_salida   INTEGER,
  salida_truncada TEXT
);

CREATE TABLE hallazgo (
  id           BIGSERIAL PRIMARY KEY,
  ejecucion_id BIGINT       NOT NULL REFERENCES ejecucion(id),
  tipo         VARCHAR(20)  NOT NULL CHECK (tipo IN ('VULNERABILIDAD','MALWARE','SISTEMA')),
  severidad    VARCHAR(10)  NOT NULL CHECK (severidad IN ('INFO','BAJA','MEDIA','ALTA','CRITICA')),
  titulo       VARCHAR(255) NOT NULL,
  detalle      TEXT
);

CREATE TABLE auditoria (
  id           BIGSERIAL PRIMARY KEY,
  fecha        TIMESTAMPTZ NOT NULL DEFAULT now(),
  usuario_id   BIGINT      REFERENCES usuario(id),
  evento       VARCHAR(60) NOT NULL,
  ejecucion_id BIGINT      REFERENCES ejecucion(id),
  detalle      TEXT
);

CREATE INDEX idx_ejecucion_usuario ON ejecucion(usuario_id);
CREATE INDEX idx_ejecucion_estado  ON ejecucion(estado);
CREATE INDEX idx_hallazgo_ejec     ON hallazgo(ejecucion_id);
CREATE INDEX idx_auditoria_fecha   ON auditoria(fecha);
CREATE INDEX idx_auditoria_ejec    ON auditoria(ejecucion_id);

INSERT INTO accion_catalogo (codigo, herramienta, descripcion, esquema_parametros, riesgo) VALUES
('osquery_procesos',    'osquery',    'Lista de procesos en ejecución',
   '{}', 'BAJO'),
('osquery_puertos',     'osquery',    'Puertos abiertos y proceso asociado',
   '{}', 'BAJO'),
('osquery_inicio',      'osquery',    'Programas de inicio',
   '{}', 'BAJO'),
('osquery_usuarios',    'osquery',    'Usuarios del sistema',
   '{}', 'BAJO'),
('inventario_vulns',    'syft_grype', 'Inventario y vulnerabilidades de una ruta',
   '{"ruta":{"tipo":"ruta"}}', 'BAJO'),
('escaneo_carpeta',     'clamav',     'Escaneo de una carpeta (solo reporta)',
   '{"ruta":{"tipo":"ruta"}}', 'MEDIO'),
('actualizar_grype_db', 'syft_grype', 'Actualizar base de vulnerabilidades (usa red)',
   '{}', 'MEDIO'),
('actualizar_firmas',   'clamav',     'Actualizar firmas de ClamAV (usa red)',
   '{}', 'MEDIO');
