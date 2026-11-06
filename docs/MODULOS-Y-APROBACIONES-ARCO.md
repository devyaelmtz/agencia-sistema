# Módulos de agencia y aprobación ARCO

Spring Boot **4.1.1**, Java 21, Angular 22 y PostgreSQL. Se conserva el filtro Bearer oficial de Spring Security; no se instala otro filtro JWT.

## Arranque y base de datos

1. Inicia PostgreSQL y configura la conexión mediante `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD` si difiere del desarrollo local.
2. Desde `backend`, ejecuta `bash mvnw verify` y `bash mvnw spring-boot:run`.
3. Desde `frontend`, ejecuta `npm ci`, `npm test -- --watch=false`, `npm run build` y `npm start`.

En el entorno cloud preparado, primero ejecuta `source /workspace/.agencia-setup/activate.sh` y `bash /workspace/.agencia-setup/start-db.sh`. Añade a Maven `-s /workspace/.agencia-setup/maven-settings.xml -Dmaven.repo.local=/workspace/.m2/repository`.

Flyway aplica las migraciones de `backend/src/main/resources/db/migration` antes de Hibernate. Conserva las tablas existentes, renombra `arco_requests` a `solicitudes_arco` y añade versiones, propietario de clientes y ventas. No ejecutes manualmente esas migraciones sobre una base ya gestionada por Flyway ni modifiques una migración aplicada. Hibernate conserva `ddl-auto=update` para los módulos legacy; Flyway gestiona los cambios de esta entrega.

Antes de actualizar una base con datos reales, realiza un respaldo y revisa duplicados de `lower(correo)` en `usuarios`: el nuevo índice único los rechazará. Las credenciales del archivo de propiedades son exclusivamente de desarrollo. `JWT_SECRET` admite una clave estable de al menos 32 bytes; sin ella se utiliza una clave temporal que cambia al reiniciar.

## Navegación y permisos

Desde `/dashboard`:

| Pantalla            | Ruta              | Permiso                                |
| ------------------- | ----------------- | -------------------------------------- |
| Perfil propio       | `/perfil`         | Cuenta autenticada                     |
| Clientes            | `/clientes`       | Usuario: propios; administrador: todos |
| Inventario          | `/admin/vehicles` | Administrador                          |
| Usuarios            | `/admin/usuarios` | Administrador                          |
| Ventas              | `/admin/ventas`   | Administrador                          |
| Solicitudes propias | `/arco`           | Cuenta autenticada                     |
| Resolución ARCO     | `/admin/arco`     | Administrador                          |

El registro público siempre crea `ROLE_USER`. Para el primer administrador, registra la cuenta, inicia el backend una vez con `ADMIN_BOOTSTRAP_EMAIL` igual a su correo y retira esa variable después. Inicia sesión de nuevo para obtener el rol actualizado.

Los clientes históricos quedan con propietario nulo y solo son visibles para administradores. En Clientes, **Vincular cuenta** permite asignarlos explícitamente a una cuenta; no se deduce la propiedad por correo o teléfono. El cambio incrementa la versión e invalida solicitudes pendientes basadas en la versión anterior.

## Contrato HTTP

Todas las rutas protegidas reciben `Authorization: Bearer <accessToken>`. El login continúa aceptando `correo`, no `email`.

| Recurso                                 | Operaciones                                                                                       |
| --------------------------------------- | ------------------------------------------------------------------------------------------------- |
| `/api/usuarios`                         | POST y GET de listado: ADMIN                                                                      |
| `/api/usuarios/{id}`                    | GET: propietario o ADMIN; PUT/DELETE: ADMIN inmediato, propietario genera ARCO                    |
| `/api/clientes`                         | GET filtrado por propietario; POST crea un cliente vinculado al solicitante                       |
| `/api/clientes/{id}`                    | GET: propietario o ADMIN; PUT/DELETE: ADMIN inmediato, propietario genera ARCO                    |
| `/api/admin/clientes/{id}/owner`        | PUT: ADMIN, `{ "version": 0, "usuarioId": 12 }`                                                   |
| `/api/vehicles` y `/{id}`               | GET autenticado; POST/PUT/DELETE: ADMIN                                                           |
| `/api/ventas` y `/{id}`                 | CRUD: ADMIN                                                                                       |
| `/api/arco/my-requests`                 | GET de solicitudes propias con propuestas y auditoría                                             |
| `/api/arco-requests`                    | GET propio; POST conserva solicitudes descriptivas del módulo anterior                            |
| `/api/arco-requests/actions`            | POST de solicitud ejecutable sobre un registro propio                                             |
| `/api/arco-requests/{id}`               | GET propio o ADMIN; PUT de descripción propia pendiente; DELETE retira solicitud propia pendiente |
| `/api/admin/arco-requests`              | GET: ADMIN, historial completo                                                                    |
| `/api/admin/arco-requests/{id}/resolve` | POST: ADMIN, decisión y motivo                                                                    |

Los DELETE de usuarios/clientes reciben `version` como parámetro. Para usuarios normales, también requieren `descripcion`, `avisoLeido=true` y `avisoVersion=academico-1`. El formulario Angular se encarga de enviarlos. Se recomienda el POST estructurado para las solicitudes ARCO, especialmente si la descripción contiene información personal.

### Cliente

POST `/api/clientes`:

```json
{
  "nombre": "Ana",
  "apellido": "Pérez",
  "rfc": "XAXX010101000",
  "direccion": "Calle Principal 123",
  "telefono": "5551234567",
  "correo": "ana@example.com",
  "recibirPublicidad": true
}
```

Para rectificarlo, PUT `/api/clientes/12` con la versión devuelta por GET:

```json
{
  "version": 0,
  "datos": {
    "nombre": "Ana María",
    "apellido": "Pérez",
    "rfc": "XAXX010101000",
    "direccion": "Calle Principal 123",
    "telefono": "5551234567",
    "correo": "ana@example.com",
    "recibirPublicidad": true
  },
  "descripcion": "Solicito corregir mi nombre completo",
  "avisoLeido": true,
  "avisoVersion": "academico-1"
}
```

Un usuario recibe **202 Accepted** con una solicitud `PENDIENTE`; el cliente original sigue intacto. Un administrador recibe **200 OK** con el cliente actualizado. El solicitante no puede elegir otro propietario ni enviar campos de privilegios.

### Usuarios

POST administrativo `/api/usuarios`:

```json
{
  "nombreUsuario": "Vendedor",
  "correo": "vendedor@example.com",
  "telefono": "5551234567",
  "password": "CambiarEstaClave123!",
  "rol": "ROLE_USER"
}
```

PUT utiliza `{ "version": 0, "datos": { ... } }`. Una contraseña omitida o vacía en una actualización administrativa conserva la anterior. Las contraseñas nuevas se almacenan con BCrypt, nunca se devuelven en respuestas ni se incluyen en las propuestas ARCO.

Para rectificar el perfil propio como usuario, `datos` solo contiene `nombreUsuario`, `correo` y `telefono`, acompañado del aviso y descripción. Cambiar la contraseña se realiza mediante el flujo existente de recuperación; los roles no pueden solicitarse mediante ARCO.

La eliminación puede rechazarse con 409 si existen clientes o ventas relacionados. No se borran en cascada transacciones ni datos ajenos. Una cuenta eliminada pierde acceso incluso si conserva un JWT emitido previamente. Un cambio de rol invalida los JWT con el rol anterior; debe iniciarse sesión otra vez. Las claves y JWT existentes no se revocan por editar otros campos del perfil.

### Vehículos y ventas

El inventario conserva `/api/vehicles`, VIN único, precio decimal y kilometraje. Añade `RESERVADO`; conserva `MANTENIMIENTO` para no invalidar datos anteriores.

POST `/api/ventas`:

```json
{
  "clienteId": 12,
  "vehiculoId": 7,
  "montoTotal": 250000.5,
  "metodoPago": "TRANSFERENCIA"
}
```

Métodos: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA`, `FINANCIAMIENTO`. La fecha y el usuario vendedor provienen del servidor. La venta bloquea el vehículo, verifica `DISPONIBLE` o `RESERVADO`, registra la transacción y cambia el estado a `VENDIDO` de manera atómica. Una restricción única evita una segunda venta del mismo vehículo.

PUT `/api/ventas/{id}` recibe `{ "version": 0, "datos": { ... } }`; permite cambiar cliente, monto y método, conservando vendedor, fecha y vehículo. Para cambiar vehículo hay que eliminar la venta y registrar otra. DELETE `/api/ventas/{id}?version=0` elimina la venta y devuelve el vehículo a `DISPONIBLE` dentro de la misma transacción. Mientras exista la venta, el inventario no permite cambiarlo a otro estado.

### Solicitudes ARCO estructuradas

POST `/api/arco-requests/actions`:

```json
{
  "tablaAfectada": "clientes",
  "registroId": 12,
  "registroVersion": 0,
  "tipo": "OPOSICION",
  "descripcion": "No deseo recibir publicidad de la agencia",
  "avisoLeido": true,
  "avisoVersion": "academico-1"
}
```

Solo se admiten los destinos registrados `clientes` y `usuarios`. Para `RECTIFICACION`, agrega `propuesta` con los campos admitidos del cliente o perfil. Las solicitudes no contienen SQL ni nombres arbitrarios que el servidor ejecute.

| Derecho       | Efecto al aprobar                                                                               |
| ------------- | ----------------------------------------------------------------------------------------------- |
| ACCESO        | Genera una copia del registro sin contraseñas, disponible en `resultado` de la solicitud propia |
| RECTIFICACION | Aplica exclusivamente la propuesta validada                                                     |
| CANCELACION   | Elimina el registro si sus relaciones lo permiten                                               |
| OPOSICION     | Desactiva `recibirPublicidad` del cliente; no aplica a cuentas de usuario                       |

La consulta operativa de registros propios sigue disponible para utilizar las pantallas. ACCESO es el flujo adicional de entrega de una copia autorizada.

El administrador revisa solicitante, destino, versión y propuesta. Para resolver:

```json
{
  "decision": "APROBAR",
  "motivo": "Identidad y corrección verificadas"
}
```

Enviar a POST `/api/admin/arco-requests/31/resolve`. Para rechazar, utiliza `RECHAZAR`. La aprobación bloquea la solicitud, comprueba propiedad y versión nuevamente, aplica el cambio y registra estado, administrador, fecha y motivo en una sola transacción. Si hay un conflicto o una relación que impide eliminar, la transacción se revierte y la solicitud permanece pendiente. No puede resolverla el propio solicitante.

Solo se edita la descripción de una solicitud pendiente mediante PUT `/api/arco-requests/31` con `{ "version": 0, "descripcion": "Descripción corregida de la solicitud" }`. Para cambiar la propuesta, retira la solicitud y crea otra desde el registro actualizado.

DELETE `/api/arco-requests/31?version=0` es un **retiro lógico**: queda `RECHAZADA`, motivo `Retirada por el solicitante` y fecha de resolución, sin administrador. El historial no se elimina físicamente. Una solicitud ya resuelta no admite edición, retiro ni segunda resolución.

Las solicitudes descriptivas históricas se conservan. Como no tienen destino/propuesta/versionado, la API impide su aprobación automática (409); el administrador puede rechazarlas indicando cómo presentar una solicitud vinculada a un registro.

## Pruebas de red local

En el archivo `hosts` del cliente:

```text
192.168.0.29 agencia.local api.agencia.local
```

Angular: `http://agencia.local:4200`. API: `http://api.agencia.local:8080`.

```bash
curl -i 'http://api.agencia.local:8080/api/auth/login' \
  -H 'Content-Type: application/json' \
  --data '{"correo":"usuario@example.com","password":"TuClaveDePrueba123!"}'

curl -i 'http://api.agencia.local:8080/api/arco/my-requests' \
  -H 'Authorization: Bearer REEMPLAZA_CON_ACCESS_TOKEN'
```

La configuración permite CORS para localhost, rangos privados y dominios locales documentados, sin cookies de autenticación. Postman/cURL no aplican CORS. 401 indica token inválido, vencido, cuenta eliminada o roles desactualizados; 403 indica permisos insuficientes. 400 identifica validación y 409 identifica conflictos de versión, resolución repetida o integridad. Las rutas legacy de otros módulos quedan restringidas a ADMIN para evitar vías alternativas de escritura.

## Verificación automatizada

- Backend: pruebas de propuestas pendientes, aprobación/rechazo, acceso/oposición/cancelación, propiedad, privilegios, retiro, versiones, JWT y CORS mediante HTTP real.
- Concurrencia: dos aprobaciones simultáneas y dos ventas simultáneas del mismo vehículo; solo una operación puede ganar.
- Frontend: formularios, confirmación del aviso, visualización de pendientes, conservación del original y manejo de conflictos administrativos.

Esta entrega no publica el entorno ni fusiona automáticamente el Pull Request.
