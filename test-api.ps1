# Script de prueba para la API de Auditoría de Matrículas (PowerShell)
# Asegúrate de que el sistema esté corriendo: docker-compose up

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Test API - Sistema de Auditoría Kafka" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

$API_URL = "http://localhost:8080/api/enrollment"

Write-Host "1. Health Check" -ForegroundColor Blue
Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" | ConvertTo-Json
Write-Host ""
Start-Sleep -Seconds 3

Write-Host "2. Listar Estudiantes" -ForegroundColor Blue
$students = Invoke-RestMethod -Uri "$API_URL/students"
$students[0..2] | ConvertTo-Json
Write-Host ""
Start-Sleep -Seconds 3

Write-Host "3. Listar Cursos" -ForegroundColor Blue
$courses = Invoke-RestMethod -Uri "$API_URL/courses"
$courses[0..2] | ConvertTo-Json
Write-Host ""
Start-Sleep -Seconds 3

Write-Host "4. Crear Matrícula (Estudiante STU001 en Curso CRS003)" -ForegroundColor Yellow
$enrollBody = @{
    studentId = "STU001"
    courseId = "CRS003"
} | ConvertTo-Json

$enrollResponse = Invoke-RestMethod -Uri "$API_URL/enroll" -Method Post -Body $enrollBody -ContentType "application/json"
$enrollResponse | ConvertTo-Json
$ENROLL_ID = $enrollResponse.enrollmentId
Write-Host "Enrollment ID: $ENROLL_ID" -ForegroundColor Green
Write-Host ""
Write-Host "Esperando 5 segundos para que Kafka procese el evento..." -ForegroundColor Gray
Start-Sleep -Seconds 5

Write-Host "5. Registrar Pago" -ForegroundColor Yellow
$paymentBody = @{
    amount = 500.00
    currency = "USD"
    paymentMethod = "Credit Card"
} | ConvertTo-Json

Invoke-RestMethod -Uri "$API_URL/$ENROLL_ID/payment" -Method Post -Body $paymentBody -ContentType "application/json" | ConvertTo-Json
Write-Host ""
Write-Host "Esperando 5 segundos para que Kafka procese el evento..." -ForegroundColor Gray
Start-Sleep -Seconds 5

Write-Host "6. Cambiar de Curso (de CRS003 a CRS005)" -ForegroundColor Yellow
$changeCourseBody = @{
    newCourseId = "CRS005"
    reason = "Interés en bases de datos NoSQL"
} | ConvertTo-Json

Invoke-RestMethod -Uri "$API_URL/$ENROLL_ID/change-course" -Method Post -Body $changeCourseBody -ContentType "application/json" | ConvertTo-Json
Write-Host ""
Write-Host "Esperando 5 segundos para que Kafka procese el evento..." -ForegroundColor Gray
Start-Sleep -Seconds 5

Write-Host "7. Cancelar Matrícula" -ForegroundColor Yellow
$cancelBody = @{
    reason = "Estudiante abandonó el programa"
} | ConvertTo-Json

Invoke-RestMethod -Uri "$API_URL/$ENROLL_ID/cancel" -Method Post -Body $cancelBody -ContentType "application/json" | ConvertTo-Json
Write-Host ""
Write-Host "Esperando 5 segundos para que Kafka procese el evento..." -ForegroundColor Gray
Start-Sleep -Seconds 5

Write-Host "8. Consultar Matrícula" -ForegroundColor Blue
Invoke-RestMethod -Uri "$API_URL/$ENROLL_ID" | ConvertTo-Json
Write-Host ""
Start-Sleep -Seconds 3

Write-Host "9. Listar Todas las Matrículas" -ForegroundColor Blue
Invoke-RestMethod -Uri "$API_URL" | ConvertTo-Json
Write-Host ""

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Pruebas Completadas!" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Verifica los eventos en:"
Write-Host "- Kafka UI: http://localhost:8090"
Write-Host "- Logs: docker-compose logs -f enrollment-app"
Write-Host ""
