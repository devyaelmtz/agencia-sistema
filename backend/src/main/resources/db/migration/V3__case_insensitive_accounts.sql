CREATE UNIQUE INDEX IF NOT EXISTS usuarios_correo_normalizado ON usuarios(lower(correo));
