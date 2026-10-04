$ErrorActionPreference = "Stop"
$baseUrl = "http://localhost:8080/api/v1"

Write-Host "Creating recharge..."
$created = Invoke-RestMethod -Uri "$baseUrl/recharges" -Method POST -ContentType "application/json" -Body (@{
    cardNumber = "1010000012345678"
    amount = 50000
    paymentMethod = "NEQUI"
} | ConvertTo-Json)
$created | ConvertTo-Json -Depth 10

$id = $created.data.id
Write-Host "Listing recharges..."
Invoke-RestMethod -Uri "$baseUrl/getRecharges?page=0&size=10" -Method GET | ConvertTo-Json -Depth 10

Write-Host "Deleting recharge id $id..."
Invoke-WebRequest -Uri "$baseUrl/recharges/$id" -Method DELETE -UseBasicParsing | Select-Object StatusCode, Headers
