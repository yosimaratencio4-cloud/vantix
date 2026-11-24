-- Ejecutar conectado como dueño de las tablas o superusuario.
-- La aplicación conecta como vantix_app: nunca como ADMIN ni como superusuario.

CREATE ROLE vantix_app LOGIN PASSWORD 'CAMBIAR_PASSWORD_DE_LA_APP';
GRANT USAGE ON SCHEMA public TO vantix_app;

GRANT SELECT, INSERT, UPDATE ON usuario, accion_catalogo, ejecucion, hallazgo TO vantix_app;
GRANT SELECT, INSERT ON auditoria TO vantix_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO vantix_app;

-- auditoria no tiene UPDATE ni DELETE para la aplicación: es solo inserción.
