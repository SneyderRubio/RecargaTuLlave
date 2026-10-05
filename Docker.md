# Guía Docker — RecargaTuLlave

Guía de emergencia para levantar, validar y probar la API con Docker.

## 1. Verificar Docker

```powershell
docker --version
docker compose version
```

Desde la raíz del proyecto:

```powershell
Get-Location
Get-ChildItem
```

Deben existir `Dockerfile`, `docker-compose.yml`, `pom.xml`, `src`, `postman` y `scripts`.

## 2. Levantar la aplicación

```powershell
docker compose up --build
```

API:
`http://localhost:8080`

Swagger:
`http://localhost:8080/swagger-ui/index.html`

OpenAPI:
`http://localhost:8080/v3/api-docs`

Mantener esta terminal abierta para ver los logs.

## 3. Verificar contenedores

En otra terminal:

```powershell
docker ps
docker compose ps
```

Deben estar ejecutándose la API y PostgreSQL.

## 4. Ver logs

```powershell
docker compose logs -f api
```

PostgreSQL:

```powershell
docker compose logs -f postgres
```

Todos:

```powershell
docker compose logs -f
```

Salir de los logs: `Ctrl + C`.

## 5. Si aparece "container name is already in use"

Verificar primero:

```powershell
docker ps -a --filter "name=tlv-rcg-26"
```

Si los contenedores pertenecen a este proyecto y se pueden recrear:

```powershell
docker rm -f tlv-rcg-26-api
docker rm -f tlv-rcg-26-postgres
```

Después:

```powershell
docker compose up --build
```

No eliminar contenedores de otros proyectos.

## 6. POST — Crear recarga

```powershell
curl.exe -X POST "http://localhost:8080/api/v1/recharges" `
  -H "Content-Type: application/json" `
  -d "{\"cardNumber\":\"1010000012345678\",\"amount\":50000,\"paymentMethod\":\"NEQUI\"}"
```

Esperado: `201 Created`.

Guardar el `id` devuelto para DELETE.

## 7. GET — Consultar recargas

```powershell
curl.exe -X GET "http://localhost:8080/api/v1/getRecharges?page=0&size=10" `
  -H "Accept: application/json"
```

Esperado: `200 OK`.

## 8. GET — Filtrar por cardNumber

```powershell
curl.exe -X GET "http://localhost:8080/api/v1/getRecharges?cardNumber=1010000012345678&page=0&size=10" `
  -H "Accept: application/json"
```

Esperado: `200 OK`.

## 9. DELETE — Recarga existente

Si el POST devolvió `id = 3`:

```powershell
curl.exe -i -X DELETE "http://localhost:8080/api/v1/recharges/3"
```

Esperado: `204 No Content` y:

```text
X-Message: Recharge deleted successfully
```

## 10. DELETE — ID inexistente

```powershell
curl.exe -i -X DELETE "http://localhost:8080/api/v1/recharges/9999"
```

Esperado: `404 Not Found`.

## 11. POST — Validación de tarjeta

```powershell
curl.exe -X POST "http://localhost:8080/api/v1/recharges" `
  -H "Content-Type: application/json" `
  -d "{\"cardNumber\":\"12345\",\"amount\":50000,\"paymentMethod\":\"NEQUI\"}"
```

Esperado: `400 Bad Request`.

## 12. POST — Validación de monto

```powershell
curl.exe -X POST "http://localhost:8080/api/v1/recharges" `
  -H "Content-Type: application/json" `
  -d "{\"cardNumber\":\"1010000012345678\",\"amount\":1000,\"paymentMethod\":\"NEQUI\"}"
```

Esperado: `400 Bad Request`.

## 13. Smoke test

Con Docker ejecutándose:

```powershell
./scripts/smoke-test.ps1
```

Flujo esperado:

```text
POST → 201 Created
GET  → 200 OK
DELETE → 204 No Content
```

## 14. Swagger

Abrir:

```text
http://localhost:8080/swagger-ui/index.html
```

Endpoints:

```text
POST   /api/v1/recharges
GET    /api/v1/getRecharges
DELETE /api/v1/recharges/{id}
```

Usar `Try it out` → parámetros → `Execute` → revisar status y respuesta.

## 15. Postman

Importar:

```text
postman/TLV-RCG-26.postman_collection.json
```

Ejecutar:

```text
Success
├── Create recharge
├── Get recharges
├── Filter by cardNumber
└── Delete recharge

Errors
```

## 16. Comprobar puertos

API:

```powershell
Test-NetConnection localhost -Port 8080
```

PostgreSQL:

```powershell
Test-NetConnection localhost -Port 5432
```

Esperado:

```text
TcpTestSucceeded : True
```

## 17. Reiniciar

```powershell
docker compose restart
```

Reconstruir:

```powershell
docker compose up --build
```

## 18. Detener

```powershell
docker compose down
```

Esto conserva el volumen de PostgreSQL.

## 19. Reiniciar desde cero

ADVERTENCIA: elimina los datos del volumen PostgreSQL.

```powershell
docker compose down -v
docker compose up --build
```

## 20. Limpieza de emergencia

```powershell
docker compose down
docker ps -a --filter "name=tlv-rcg-26"
```

Si todavía hay conflicto y los contenedores son de este proyecto:

```powershell
docker rm -f tlv-rcg-26-api
docker rm -f tlv-rcg-26-postgres
docker compose up --build
```

## 21. Flujo rápido para la sustentación

Terminal 1:

```powershell
docker compose up --build
```

Terminal 2:

```powershell
docker compose ps
./scripts/smoke-test.ps1
```

Navegador:

```text
http://localhost:8080/swagger-ui/index.html
```

Postman:

```text
Create recharge
Get recharges
Filter by cardNumber
Delete recharge
```

Errores:

```text
POST inválido → 400
DELETE inexistente → 404
```

## 22. Git

Si se agrega este archivo al repositorio:

```powershell
git status
git add .
git commit -m "docs: add Docker execution and endpoint guide"
git push origin main
```

Si se requiere Pull Request:

```powershell
git checkout -b docs/docker-execution-guide
git add .
git commit -m "docs: add Docker execution and endpoint guide"
git push -u origin docs/docker-execution-guide
```

Luego crear el PR hacia `main`.

## Checklist

- [ ] Docker instalado
- [ ] `docker compose up --build`
- [ ] API y PostgreSQL ejecutándose
- [ ] Swagger disponible
- [ ] POST → 201
- [ ] GET → 200
- [ ] GET + cardNumber → 200
- [ ] DELETE → 204
- [ ] DELETE inexistente → 404
- [ ] POST inválido → 400
- [ ] Smoke test exitoso
- [ ] Postman ejecutado
- [ ] `docker compose down`
