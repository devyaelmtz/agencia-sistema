# Agencia: acceso de usuarios

Interfaz activa: login, registro, recuperación de contraseña, dashboard, administración de vehículos y solicitudes ARCO. La raíz redirige a login.

## Desarrollo

Requisitos: JDK 21, Node compatible con Angular 22, npm 12.1.0 y PostgreSQL.
En este entorno: `source /workspace/.agencia-setup/activate.sh` y `bash /workspace/.agencia-setup/start-db.sh`.

- Backend: desde `backend`, ejecutar `bash mvnw spring-boot:run`. En el entorno cloud añadir `-s /workspace/.agencia-setup/maven-settings.xml -Dmaven.repo.local=/workspace/.m2/repository`.
- Frontend: desde `frontend`, ejecutar `npm ci` y `npm start`. Angular redirige `/api` al backend local en el puerto 8080 mediante `proxy.conf.json`.
- PostgreSQL: base `agencia_autos`. La configuración de desarrollo existente está en `backend/src/main/resources/application.properties`; se puede sobrescribir mediante `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`. Hibernate agrega la columna `telefono` y la unicidad de correo. Revisar duplicados antes de usar una base preexistente con datos reales.

## Probar el flujo

1. Ir a `/register`, completar nombre (2–50 caracteres), correo, teléfono obligatorio de exactamente 10 dígitos y contraseña de 8–64 caracteres (máximo 72 bytes para BCrypt).
2. Tras el mensaje de registro exitoso, seguir el enlace a login.
3. Introducir correo y contraseña: el servidor retorna un JWT Bearer y Angular redirige a `/dashboard`, donde carga el perfil mediante `/api/auth/me`. El botón Cerrar sesión elimina el token y vuelve a `/login`.

API: `POST /api/auth/register` acepta `nombreUsuario`, `correo`, `telefono`, `password`; `POST /api/auth/login` acepta `correo`, `password`. Respuestas sin contraseñas; registro 201, validación 400, duplicado 409, credenciales incorrectas 401.

## Sesión JWT

El login retorna `accessToken`, `tokenType` (Bearer), `expiresIn` (3600 segundos) y `usuario`. La firma es HS256 y se validan emisor y caducidad mediante el filtro Bearer oficial de Spring Security. La API es stateless: registro y login son públicos; el resto requiere un token válido. La gestión legacy `/api/usuarios` sigue bloqueada.

Angular conserva el token en `sessionStorage` (por pestaña), lo añade a las llamadas de la API y redirige a login ante un 401. El guard rechaza tokens ausentes, ilegibles o caducados; la verificación criptográfica corresponde al backend. El dashboard consulta el perfil autenticado; cerrar sesión elimina el token local, pero una copia del token sigue siendo válida hasta su caducidad. No se incluye refresh token ni revocación remota.

Configura `JWT_SECRET` de forma segura en el entorno de ejecución del backend: debe ser un secreto aleatorio de al menos 32 bytes, compartido por las instancias. No lo subas al repositorio. Sin esa variable se genera una clave temporal de desarrollo; cada reinicio invalida los tokens existentes. Usa HTTPS en despliegues y sirve `/api` mediante un proxy al backend (el proxy Angular incluido es para desarrollo).

Los servicios y clases de los módulos anteriores se conservan con sus plantillas retiradas y fuera de las rutas activas. Esta etapa agrega autenticación; la autorización por roles y permisos de cada recurso se implementará al recuperar esos módulos.

## Verificación

- Frontend: `npm test -- --watch=false` y `npm run build`.
- Backend: `bash mvnw verify` (PostgreSQL debe estar activo).
- Las pruebas de integración usan transacciones con rollback; no requieren usuarios reales.

## Vehículos y roles

El registro asigna `ROLE_USER`. Para habilitar el primer administrador:

1. Registra una cuenta normalmente.
2. Arranca el backend con `ADMIN_BOOTSTRAP_EMAIL` igual al correo de esa cuenta (variable del proceso, no del navegador).
3. Retira esa variable tras la promoción y vuelve a iniciar sesión para obtener un JWT con `roles: ["ROLE_ADMIN"]`.

Un administrador puede promover otra cuenta con `PUT /api/admin/users/{id}/admin` (204), enviando su token Bearer. La cuenta promovida debe volver a iniciar sesión. No se admite asignar roles desde el registro público. Las cuentas antiguas sin rol explícito se consideran `ROLE_USER`. Mantén `JWT_SECRET` estable para conservar los tokens entre reinicios.

En el navegador, inicia sesión como administrador y sigue **Administrar vehículos** desde el dashboard, o entra en `/admin/vehicles`. El panel incluye alta, edición, listado, estados de carga, validación, mensajes de resultado y confirmación de eliminación.

### Postman

Usa `Authorization: Bearer <accessToken>` del login y `Content-Type: application/json`.

| Método | Endpoint | Permiso | Resultado |
|---|---|---|---|
| GET | `/api/vehicles` | Usuario autenticado | 200, listado |
| GET | `/api/vehicles/{id}` | Usuario autenticado | 200 o 404 |
| POST | `/api/vehicles` | ADMIN | 201 y Location |
| PUT | `/api/vehicles/{id}` | ADMIN | 200 o 404 |
| DELETE | `/api/vehicles/{id}` | ADMIN | 204 o 404 |

Ejemplo de cuerpo para POST/PUT:

```json
{"marca":"Toyota","modelo":"Corolla","anio":2024,"vin":"1HGCM82633A004352","precio":250000.50,"kilometraje":12000,"estado":"DISPONIBLE"}
```

VIN: único, 17 caracteres alfanuméricos en mayúsculas sin I/O/Q. Año: 1886 hasta el siguiente año calendario. Precio: no negativo, máximo 13 enteros y 2 decimales. Kilometraje: entero no negativo. Estados: `DISPONIBLE`, `VENDIDO`, `MANTENIMIENTO`. Datos inválidos devuelven 400; VIN duplicado 409; sin JWT 401; escritura con ROLE_USER 403. La API legacy `/api/vehiculos` queda restringida a ADMIN para impedir escrituras por una vía alternativa. El nuevo inventario usa la tabla `vehicles`; no se migran automáticamente registros del modelo legacy.

## Red local, recuperación y nuevo ARCO

Consulta [la guía para Windows, SMTP y solicitudes ARCO](docs/REVISION-RED-CORREO-ARCO.md). El servidor de desarrollo Angular y Spring permiten acceso LAN; los nuevos formularios están en `/forgot-password`, `/reset-password` y `/arco`.
