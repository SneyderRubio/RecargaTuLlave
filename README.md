# TLV-RCG-26 — API de Recargas Digitales tuLlave

API REST desarrollada con **Java 21**, **Spring Boot 3.3.5**, **Spring Data JPA** y **PostgreSQL** para registrar, consultar y eliminar recargas digitales.

El proyecto está preparado para ejecutarse de forma reproducible mediante **Docker Compose**, incluye validaciones, manejo centralizado de errores, logs con SLF4J, documentación OpenAPI/Swagger, pruebas unitarias con JUnit 5 + Mockito y una colección Postman con escenarios exitosos y de error.

## 1. Tecnologías

- Java 21
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA / Hibernate
- Spring Validation
- PostgreSQL 16
- Maven
- Docker / Docker Compose
- Springdoc OpenAPI / Swagger UI
- JUnit 5 + Mockito
- SLF4J

## 2. Arquitectura

La aplicación utiliza una separación por capas:

```text
HTTP Request
    |
    v
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
PostgreSQL
```

Responsabilidades principales:

- **controller**: expone los endpoints REST y define los códigos HTTP.
- **service**: contiene la lógica de aplicación.
- **repository**: acceso a datos mediante Spring Data JPA.
- **dto**: contratos de entrada, salida y errores; la entidad JPA no se expone directamente.
- **mapper**: transforma DTOs y entidades.
- **exception**: manejo centralizado de excepciones mediante `TlvErrorAdvisor`.
- **entity**: modelo persistente `Recharge`.
- **enums**: valores permitidos para medios de pago.

## 3. Estructura del proyecto

```text
TLV-RCG-26/
├── postman/
│   └── TLV-RCG-26.postman_collection.json
├── scripts/
│   ├── build.ps1
│   ├── docker-down.ps1
│   ├── docker-up.ps1
│   └── smoke-test.ps1
├── src/
│   ├── main/
│   │   ├── java/co/tullave/rcg/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── enums/
│   │   │   ├── exception/
│   │   │   ├── mapper/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── TullaveApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/co/tullave/rcg/service/
│           └── RechargeServiceImplTest.java
├── .dockerignore
├── .env.example
├── .gitignore
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

## 4. Modelo de recarga

| Campo | Tipo | Regla |
|---|---|---|
| `id` | Long | Autogenerado |
| `cardNumber` | String | Obligatorio, exactamente 16 dígitos numéricos |
| `amount` | BigDecimal | Obligatorio, mínimo 2.000 y máximo 200.000 |
| `paymentMethod` | Enum | `PSE`, `NEQUI`, `DAVIPLATA`, `CREDIT_CARD` |
| `createdAt` | LocalDateTime | Generado automáticamente |

## 5. Ejecución recomendada con Docker

### Requisitos

- Docker Desktop o Docker Engine con Compose v2.

No es necesario tener Java, Maven ni PostgreSQL instalados localmente cuando se utiliza Docker.

### Levantar la solución

Desde la raíz del proyecto:

```bash
docker compose up --build
```

Docker Compose:

1. construye la API con Java 21 y Maven;
2. inicia PostgreSQL 16;
3. espera a que PostgreSQL esté saludable;
4. inicia la API en el puerto `8080`.

API:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

### Detener los contenedores

```bash
docker compose down
```

Para eliminar también el volumen de PostgreSQL y comenzar con una base vacía:

```bash
docker compose down -v
```

## 6. Ejecución local

Si se desea ejecutar fuera de Docker, se requiere:

- Java 21 o superior.
- Maven 3.9+.
- PostgreSQL disponible.

Compilar y ejecutar pruebas:

```bash
mvn clean package
```

Ejecutar la aplicación:

```bash
java -jar target/TLV-RCG-26-1.0.0.jar
```

Por defecto, la ejecución local intenta conectarse a PostgreSQL en `localhost:5432`.

## 7. Variables de entorno

La configuración utiliza valores por defecto adecuados para desarrollo y puede sobrescribirse mediante variables de entorno.

| Variable | Valor por defecto |
|---|---|
| `DB_HOST` | `localhost` |
| `DB_PORT` | `5432` |
| `POSTGRES_DB` | `tullave` |
| `POSTGRES_USER` | `postgres` |
| `POSTGRES_PASSWORD` | `postgres` |
| `SERVER_PORT` | `8080` |

El archivo `.env.example` contiene una configuración de referencia. No se debe versionar un archivo `.env` con credenciales reales.

## 8. Endpoints

### Crear recarga

```http
POST /api/v1/recharges
Content-Type: application/json
```

Ejemplo:

```json
{
  "cardNumber": "1010000012345678",
  "amount": 50000,
  "paymentMethod": "NEQUI"
}
```

Respuesta esperada: `201 Created`.

```json
{
  "timestamp": "2026-10-04T01:22:34.259625",
  "status": 201,
  "message": "Recharge created successfully",
  "data": {
    "id": 1,
    "cardNumber": "1010000012345678",
    "amount": 50000.00,
    "paymentMethod": "NEQUI",
    "createdAt": "2026-10-04T01:22:34.259625"
  }
}
```

### Listar recargas

```http
GET /api/v1/getRecharges?page=0&size=10
```

Respuesta esperada: `200 OK` con resultado paginado.

### Filtrar por número de tarjeta

```http
GET /api/v1/getRecharges?page=0&size=10&cardNumber=1010000012345678
```

### Eliminar recarga

```http
DELETE /api/v1/recharges/{id}
```

- Si existe: `204 No Content`.
- Si no existe: `404 Not Found`.

El `204` no devuelve cuerpo por definición del protocolo HTTP. La respuesta incluye el header `X-Message: Recharge deleted successfully`.

## 9. Validaciones y errores

Las validaciones de entrada se implementan con Jakarta Bean Validation.

Ejemplo de tarjeta inválida:

```json
{
  "timestamp": "2026-10-04T01:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Request validation failed",
  "path": "/api/v1/recharges",
  "fieldErrors": {
    "cardNumber": "Card number must contain 16 digits"
  }
}
```

El manejo global se centraliza en:

```text
co.tullave.rcg.exception.TlvErrorAdvisor
```

Códigos controlados:

- `400 Bad Request`: validaciones, JSON inválido o medio de pago no soportado.
- `404 Not Found`: recarga inexistente.
- `500 Internal Server Error`: error no controlado.

## 10. Logs

La aplicación utiliza SLF4J para registrar operaciones relevantes:

- creación de recargas;
- consultas paginadas;
- eliminación;
- recursos no encontrados;
- errores inesperados.

Por seguridad, el número completo de tarjeta no se registra en los logs.

## 11. Pruebas unitarias

Ejecutar:

```bash
mvn test
```

Las pruebas de `RechargeServiceImpl` cubren:

- creación de una recarga;
- listado paginado;
- filtro por `cardNumber`;
- eliminación de una recarga existente;
- excepción al eliminar una recarga inexistente.

## 12. Colección Postman

Importar:

```text
postman/TLV-RCG-26.postman_collection.json
```

La colección se llama **TLV-RCG-26** e incluye:

### Casos exitosos

- Crear recarga.
- Listar recargas.
- Filtrar por `cardNumber`.
- Eliminar recarga.

### Casos de error

- Número de tarjeta inválido.
- Monto inferior al mínimo.
- Monto superior al máximo.
- Medio de pago inválido.
- Eliminación de recarga inexistente.

La colección incluye aserciones sobre los códigos HTTP y reutiliza el ID generado durante la creación.

## 13. Prueba rápida en PowerShell

Con los contenedores en ejecución:

```powershell
.\scripts\smoke-test.ps1
```

El script crea una recarga, consulta el listado y elimina el registro creado.

## 14. Decisiones técnicas

### BigDecimal para valores monetarios

Se utiliza `BigDecimal` para evitar problemas de precisión asociados a `float` o `double`.

### DTOs

La entidad JPA no se expone directamente a través de la API. Esto desacopla el modelo de persistencia del contrato HTTP y permite evolucionar ambos de forma independiente.

### Enum para medios de pago

`PaymentMethod` restringe los valores válidos a `PSE`, `NEQUI`, `DAVIPLATA` y `CREDIT_CARD`.

### Paginación en base de datos

Se utiliza `Pageable` de Spring Data JPA. Los registros no se cargan completos en memoria para paginarlos posteriormente.

### Manejo centralizado de excepciones

`TlvErrorAdvisor` garantiza una respuesta consistente para validaciones, recursos inexistentes y errores inesperados.

### Docker multi-stage

El `Dockerfile` utiliza una etapa de compilación con Maven y una imagen JRE separada para ejecución. Esto evita depender de un JAR generado previamente en el equipo del evaluador.

### Healthcheck de PostgreSQL

Docker Compose espera a que PostgreSQL esté listo antes de iniciar la API, evitando condiciones de carrera durante el arranque.

## 15. Flujo Git sugerido

Ramas:

```text
main
feature/recharge-api
feature/docker
```

Ejemplos de Conventional Commits:

```text
feat: implement recharge creation endpoint
feat: add paginated recharge query
feat: add recharge deletion
fix: standardize validation error responses
chore: dockerize application with PostgreSQL
test: add recharge service unit tests
docs: document API setup and architecture
```

Antes de entregar se recomienda crear al menos un Pull Request hacia `main`, documentando los cambios y las pruebas realizadas.

## 16. Mejoras para un escenario productivo

El alcance de esta prueba es deliberadamente pequeño. Para una operación productiva se considerarían, entre otros:

- migraciones de esquema con Flyway o Liquibase en lugar de `ddl-auto=update`;
- autenticación/autorización;
- observabilidad y métricas;
- trazabilidad distribuida;
- rate limiting;
- idempotencia en la creación de recargas;
- políticas explícitas de protección/tokenización de datos sensibles;
- índices en columnas utilizadas en búsquedas frecuentes;
- gestión de secretos fuera del repositorio;
- pruebas de integración con PostgreSQL mediante Testcontainers.

## 17. Checklist de entrega

- [x] Java 21 / Spring Boot 3.
- [x] PostgreSQL.
- [x] DTOs y Bean Validation.
- [x] Manejo global de errores con `TlvErrorAdvisor`.
- [x] Controller / Service / Repository.
- [x] Logs SLF4J.
- [x] Dockerfile.
- [x] Docker Compose.
- [x] Paginación y filtro por tarjeta.
- [x] Swagger UI.
- [x] JUnit 5 + Mockito.
- [x] Colección Postman `TLV-RCG-26`.
- [x] README con arquitectura y decisiones técnicas.
- [ ] Publicar repositorio Git y crear Pull Request antes de la entrega.

Ref. interna: TLV-RCG-26
