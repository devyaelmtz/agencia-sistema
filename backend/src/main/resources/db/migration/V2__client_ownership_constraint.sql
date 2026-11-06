DO $$ BEGIN
 IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid='clientes'::regclass AND conname='clientes_usuario_id_fkey') THEN
  ALTER TABLE clientes ADD CONSTRAINT clientes_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES usuarios(id);
 END IF;
END $$;
