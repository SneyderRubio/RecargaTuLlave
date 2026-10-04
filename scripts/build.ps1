$ErrorActionPreference = "Stop"
if (Get-Command mvn -ErrorAction SilentlyContinue) {
    mvn clean package
} else {
    Write-Host "Maven no esta instalado. Use Docker para construir y ejecutar el proyecto:" -ForegroundColor Yellow
    Write-Host "  docker compose up --build"
    exit 1
}
