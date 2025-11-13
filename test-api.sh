#!/bin/bash

# Script de prueba para la API de Auditoría de Matrículas
# Asegúrate de que el sistema esté corriendo: docker-compose up

echo "========================================="
echo "Test API - Sistema de Auditoría Kafka"
echo "========================================="
echo ""

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# API Base URL
API_URL="http://localhost:8080/api/enrollment"

echo -e "${BLUE}1. Health Check${NC}"
curl -s http://localhost:8080/actuator/health | jq .
echo ""
sleep 2

echo -e "${BLUE}2. Listar Estudiantes${NC}"
curl -s $API_URL/students | jq '.[0:3]'
echo ""
sleep 2

echo -e "${BLUE}3. Listar Cursos${NC}"
curl -s $API_URL/courses | jq '.[0:3]'
echo ""
sleep 2

echo -e "${YELLOW}4. Crear Matrícula (Estudiante STU001 en Curso CRS003)${NC}"
RESPONSE=$(curl -s -X POST $API_URL/enroll \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": "STU001",
    "courseId": "CRS003"
  }')

echo $RESPONSE | jq .
ENROLL_ID=$(echo $RESPONSE | jq -r '.enrollmentId')
echo -e "${GREEN}Enrollment ID: $ENROLL_ID${NC}"
echo ""
sleep 2

echo -e "${YELLOW}5. Registrar Pago${NC}"
curl -s -X POST $API_URL/$ENROLL_ID/payment \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 500.00,
    "currency": "USD",
    "paymentMethod": "Credit Card"
  }' | jq .
echo ""
sleep 2

echo -e "${YELLOW}6. Cambiar de Curso (de CRS003 a CRS005)${NC}"
curl -s -X POST $API_URL/$ENROLL_ID/change-course \
  -H "Content-Type: application/json" \
  -d '{
    "newCourseId": "CRS005",
    "reason": "Interés en bases de datos NoSQL"
  }' | jq .
echo ""
sleep 2

echo -e "${YELLOW}7. Cancelar Matrícula${NC}"
curl -s -X POST $API_URL/$ENROLL_ID/cancel \
  -H "Content-Type: application/json" \
  -d '{
    "reason": "Estudiante abandonó el programa"
  }' | jq .
echo ""
sleep 2

echo -e "${BLUE}8. Consultar Matrícula${NC}"
curl -s $API_URL/$ENROLL_ID | jq .
echo ""
sleep 2

echo -e "${BLUE}9. Listar Todas las Matrículas${NC}"
curl -s $API_URL | jq .
echo ""

echo "========================================="
echo -e "${GREEN}Pruebas Completadas!${NC}"
echo "========================================="
echo ""
echo "Verifica los eventos en:"
echo "- Kafka UI: http://localhost:8090"
echo "- Logs: docker-compose logs -f enrollment-app"
echo ""
