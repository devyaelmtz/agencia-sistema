# Revisión: red local, correo y derechos ARCO

## 1. Compartir por Wi-Fi y usar un dominio local en Windows

1. Conecta tu computadora y la del profesor a la misma red Wi-Fi. Algunas redes escolares aíslan los equipos; en ese caso necesitarás una red que permita comunicación entre clientes.
2. En **tu computadora**, ejecuta `ipconfig` y copia la dirección IPv4 del adaptador Wi-Fi (ejemplo: `192.168.1.50`). No uses la IP del profesor, `127.0.0.1` ni `0.0.0.0`.
3. Arranca PostgreSQL y el backend desde `backend` con `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`). Spring escucha en `0.0.0.0:8080` por defecto; elimina cualquier argumento previo `--server.address=127.0.0.1` que lo sobrescriba.
4. En `frontend`, ejecuta `npm ci` y `npm start`. Angular escucha en `0.0.0.0:4200`; su proxy `/api` llama al backend de la misma computadora. Angular 22 reemplazó `--disable-host-check` por `allowedHosts: true`, configurado en `angular.json`. El script usa `ng serve --host 0.0.0.0` porque el parámetro antiguo causa un error de opción desconocida.
5. Permite conexiones entrantes TCP 4200 y 8080 en el Firewall de Windows **solo para el perfil Privado**. No necesitas exponer PostgreSQL (5432). No configures redirección de puertos del router para esta demostración.
6. En **cada computadora cliente**, incluida la del profesor, abre Bloc de notas **como administrador**. Abre `C:\Windows\System32\drivers\etc\hosts` seleccionando “Todos los archivos”. Añade, sustituyendo la IP por la tuya:

   ```text
   192.168.1.50 api.miagencia.local miagencia.local
   ```

7. Guarda sin extensión `.txt` y ejecuta `ipconfig /flushdns`. El archivo hosts solo afecta a esa computadora: no publica DNS ni modifica la red completa. Si DHCP cambia tu IP, actualiza la línea o reserva la IP en tu router.
8. El profesor puede abrir `http://miagencia.local:4200/login` y en Postman usar `http://api.miagencia.local:8080/api/auth/login`. Alternativamente, `http://miagencia.local:4200/api/...` funciona por el proxy Angular.
9. Postman no aplica CORS. En navegador se permiten orígenes localhost, rangos privados IPv4 10/8, 172.16–31/12 y 192.168/16, y `miagencia.local`/sus subdominios, mediante HTTP/HTTPS. Si añades otro dominio, agrégalo a la configuración CORS. Usa HTTPS y una lista de hosts explícita en despliegues reales; la configuración abierta de Vite se utiliza aquí para la demostración solicitada.

Si Postman usa un proxy corporativo, excluye los nombres locales de ese proxy. Si falla la resolución, prueba primero con la IP y puerto. Los nombres `.local` pueden coexistir con mDNS: aquí se resuelven explícitamente mediante hosts.

## 2. SMTP y recuperación

Configura estas variables **en el proceso que arranca Spring Boot**. No las pongas en Angular, Git o capturas compartidas:

| Variable | Gmail | Otro SMTP / pruebas |
|---|---|---|
| `SMTP_HOST` | `smtp.gmail.com` | Host del proveedor |
| `SMTP_PORT` | `587` | Puerto STARTTLS del proveedor |
| `SMTP_USERNAME` | Cuenta de Gmail | Usuario SMTP |
| `SMTP_PASSWORD` | Contraseña de aplicación con 2FA | Credencial SMTP |
| `SMTP_FROM` | Misma cuenta autorizada | Remitente autorizado |
| `SMTP_AUTH` | `true` | Según proveedor |
| `SMTP_STARTTLS` | `true` | `true` en proveedores remotos |
| `FRONTEND_URL` | `http://miagencia.local:4200` | URL accesible desde el equipo que abrirá el correo |

También conserva `JWT_SECRET`, conexión PostgreSQL y las demás variables ya utilizadas. En PowerShell se declaran variables de proceso con `$env:NOMBRE = 'valor'`, y se arranca `mvnw.cmd spring-boot:run` en esa misma terminal. No compartas la contraseña SMTP en chat. Para SMTP con TLS implícito (465), añade la propiedad estándar `spring.mail.properties.mail.smtp.ssl.enable=true` y ajusta STARTTLS según el proveedor.

Para un servidor **local** como Mailpit/MailHog: host local, puerto SMTP de pruebas (normalmente 1025), `SMTP_AUTH=false` y `SMTP_STARTTLS=false`. Esta excepción es solo para el servidor de pruebas local sin TLS; no deshabilites verificación de certificados para proveedores externos.

### Flujo de prueba

1. Registra una cuenta con un correo que controles.
2. Desde login entra en **Olvidé mi contraseña**, escribe ese correo y envía.
3. Recibirás un enlace y un código aleatorio de 43 caracteres. Abre el enlace o `/reset-password` e introduce código y nueva contraseña (8–64 caracteres; máximo 72 bytes por BCrypt).
4. Inicia sesión con la nueva contraseña. La anterior debe fallar. Reutilizar el código o usarlo después de 15 minutos devuelve 400. Solicitar un nuevo correo invalida los códigos anteriores.

Endpoints públicos:

```text
POST /api/auth/forgot-password   {"correo":"tu-correo@example.com"}       → 202
POST /api/auth/reset-password    {"token":"codigo","password":"NuevaClave123"} → 200
```

La respuesta a forgot-password es genérica, tanto si la cuenta existe como si no. Si SMTP falla, el backend registra un aviso sin datos sensibles y no conserva el token de esa transacción; la respuesta pública sigue siendo genérica. Si no llega el correo, revisa variables SMTP, conectividad del puerto y spam. El código se guarda como hash SHA-256, caduca y se consume de forma transaccional. No hay endpoint que devuelva códigos. Los JWT ya emitidos conservan su vigencia hasta caducar; esta fase no incluye revocación de sesiones ni limitación distribuida de peticiones de recuperación.

## 3. Solicitudes ARCO

Desde el dashboard, entra en **Mis solicitudes ARCO** (`/arco`). Se muestra el aviso antes del formulario. Selecciona Acceso, Rectificación, Cancelación u Oposición; describe tu petición y confirma que leíste el aviso. El backend también exige esta confirmación y la versión `academico-1`; no basta con saltar la casilla en el navegador.

**El aviso incluido es un borrador académico**, no las políticas legales definitivas del negocio. Antes de tratar datos reales, el responsable debe completar identidad/domicilio, contacto, finalidades, conservación, transferencias y procedimiento/plazos aplicables. Cambia la versión tanto en backend como en frontend cuando cambies el texto. La confirmación es de lectura, no una autorización extra ni una renuncia a derechos.

Con `Authorization: Bearer <accessToken>` en Postman:

```text
GET /api/arco-requests
POST /api/arco-requests
```

Cuerpo POST:

```json
{"tipo":"ACCESO","descripcion":"Solicito consultar los datos de mi cuenta.","evidencia":"Referencia opcional, sin datos sensibles","avisoLeido":true,"avisoVersion":"academico-1"}
```

POST devuelve 201 con folio, fecha y estado `PENDIENTE`. Descripción: 10–4000 caracteres. Evidencia: texto/referencia opcional hasta 1000 caracteres; no se suben archivos ni se consultan URLs externas. GET devuelve únicamente las solicitudes del propietario del JWT, incluso para administradores; el cliente no puede asignar usuario, estado ni fecha. Sin JWT devuelve 401; falta de confirmación, versión obsoleta o datos inválidos devuelven 400. No se implementa todavía la resolución administrativa de solicitudes; el módulo ARCO anterior sigue pausado.
