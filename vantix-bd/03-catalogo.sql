-- Catálogo de la lista blanca. La IA/el usuario elige código, nunca escribe comandos.

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
