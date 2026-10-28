# Agencia: acceso de usuarios

Interfaz activa: `/login` y `/register`. La raíz redirige a login.

## Desarrollo

Requisitos: JDK 21, Node compatible con Angular 22, npm 12.1.0 y PostgreSQL.
En este entorno: `source /workspace/.agencia-setup/activate.sh` y `bash /workspace/.agencia-setup/start-db.sh`.

- Backend: desde `backend`, ejecutar `bash mvnw spring-boot:run`. En el entorno cloud añadir `-s /workspace/.agencia-setup/maven-settings.xml -Dmaven.repo.local=/workspace/.m2/repository`.
- Frontend: desde `frontend`, ejecutar `npm ci` y `npm start`. Angular redirige `/api` al backend local en el puerto 8080 mediante `proxy.conf.json`.
- PostgreSQL: base `agencia_autos`. La configuración de desarrollo existente está en `backend/src/main/resources/application.properties`; se puede sobrescribir mediante `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`. Hibernate agrega la columna `telefono` y la unicidad de correo. Revisar duplicados antes de usar una base preexistente con datos reales.

## Probar el flujo

1. Ir a `/register`, completar nombre (2–50 caracteres), correo, teléfono obligatorio de exactamente 10 dígitos y contraseña de 8–64 caracteres (máximo 72 bytes para BCrypt).
2. Tras el mensaje de registro exitoso, seguir el enlace a login.
3. Introducir correo y contraseña: la vista muestra el nombre del usuario si las credenciales son válidas.

API: `POST /api/auth/register` acepta `nombreUsuario`, `correo`, `telefono`, `password`; `POST /api/auth/login` acepta `correo`, `password`. Respuestas sin contraseñas; registro 201, validación 400, duplicado 409, credenciales incorrectas 401.

El login valida credenciales reales; esta fase no crea una sesión autenticada ni emite tokens para otras funciones. No hay pantalla de negocio posterior. Los servicios y clases de los módulos anteriores se conservan, con sus plantillas retiradas y fuera de las rutas activas. La API antigua `/api/usuarios` queda bloqueada para evitar registros sin validar; su implementación se conserva. El resto de las APIs de negocio mantienen su configuración previa y requieren una etapa de autorización antes de un despliegue público.

## Verificación

- Frontend: `npm test -- --watch=false` y `npm run build`.
- Backend: `bash mvnw verify` (PostgreSQL debe estar activo).
- Las pruebas de integración usan transacciones con rollback; no requieren usuarios reales.
