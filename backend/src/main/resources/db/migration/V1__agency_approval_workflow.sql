-- Additive migration; preserve legacy clients and request history.
CREATE TABLE IF NOT EXISTS usuarios (id BIGSERIAL PRIMARY KEY, nombre_usuario VARCHAR(50), correo VARCHAR(100) UNIQUE, password VARCHAR(255), rol_id BIGINT, rol VARCHAR(255) DEFAULT 'ROLE_USER', telefono VARCHAR(255));
CREATE TABLE IF NOT EXISTS clientes (id BIGSERIAL PRIMARY KEY, nombre VARCHAR(100) NOT NULL, apellido VARCHAR(100) NOT NULL, telefono VARCHAR(20), correo VARCHAR(100), direccion VARCHAR(255), recibir_publicidad BOOLEAN);
CREATE TABLE IF NOT EXISTS vehicles (id BIGSERIAL PRIMARY KEY, marca VARCHAR(255) NOT NULL, modelo VARCHAR(255) NOT NULL, anio INTEGER NOT NULL, vin VARCHAR(17) UNIQUE NOT NULL, precio NUMERIC(15,2) NOT NULL, kilometraje BIGINT NOT NULL, estado VARCHAR(255) NOT NULL);
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS usuario_id BIGINT REFERENCES usuarios(id);
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS rfc VARCHAR(13);
ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
-- Hibernate-generated enum checks must accommodate RESERVADO as well as legacy values.
ALTER TABLE vehicles DROP CONSTRAINT IF EXISTS vehicles_estado_check;
ALTER TABLE vehicles ADD CONSTRAINT vehicles_estado_check CHECK (estado IN ('DISPONIBLE','VENDIDO','RESERVADO','MANTENIMIENTO'));
DO $$ BEGIN
 IF to_regclass('public.arco_requests') IS NOT NULL AND to_regclass('public.solicitudes_arco') IS NULL THEN
  ALTER TABLE arco_requests RENAME TO solicitudes_arco;
 END IF;
END $$;
CREATE TABLE IF NOT EXISTS solicitudes_arco (id BIGSERIAL PRIMARY KEY, usuario_id BIGINT NOT NULL, tipo VARCHAR(255) NOT NULL, descripcion VARCHAR(4000) NOT NULL, estado VARCHAR(255) NOT NULL, evidencia VARCHAR(1000), aviso_leido BOOLEAN NOT NULL, aviso_version VARCHAR(255), created_at TIMESTAMPTZ);
ALTER TABLE solicitudes_arco ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE solicitudes_arco ADD COLUMN IF NOT EXISTS tabla_afectada VARCHAR(255);
ALTER TABLE solicitudes_arco ADD COLUMN IF NOT EXISTS registro_id BIGINT;
ALTER TABLE solicitudes_arco ADD COLUMN IF NOT EXISTS registro_version BIGINT;
ALTER TABLE solicitudes_arco ADD COLUMN IF NOT EXISTS propuesta VARCHAR(8000);
ALTER TABLE solicitudes_arco ADD COLUMN IF NOT EXISTS resultado VARCHAR(8000);
ALTER TABLE solicitudes_arco ADD COLUMN IF NOT EXISTS administrador_id BIGINT;
ALTER TABLE solicitudes_arco ADD COLUMN IF NOT EXISTS fecha_resolucion TIMESTAMPTZ;
ALTER TABLE solicitudes_arco ADD COLUMN IF NOT EXISTS motivo_resolucion VARCHAR(1000);
CREATE INDEX IF NOT EXISTS solicitudes_arco_usuario_fecha ON solicitudes_arco(usuario_id,created_at DESC);
CREATE TABLE IF NOT EXISTS ventas (id BIGSERIAL PRIMARY KEY, cliente_id BIGINT NOT NULL REFERENCES clientes(id), vehicle_id BIGINT NOT NULL UNIQUE REFERENCES vehicles(id), usuario_id BIGINT NOT NULL REFERENCES usuarios(id), fecha TIMESTAMPTZ NOT NULL, monto_total NUMERIC(15,2) NOT NULL CHECK(monto_total>0), metodo_pago VARCHAR(30) NOT NULL, version BIGINT NOT NULL DEFAULT 0);
