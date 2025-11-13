# Sistema de Auditoría de Matrículas con Kafka

Sistema de registro de eventos históricos y auditoría para matrículas de estudiantes usando Apache Kafka. Este proyecto demuestra cómo Kafka puede ser utilizado para mantener un registro inmutable de todos los eventos relacionados con matrículas (creaciones, cancelaciones, cambios de curso, pagos) para auditorías y análisis posteriores.

## Características Principales

- **Registro Inmutable**: Todos los eventos se almacenan en Kafka de forma permanente (retención de 7 días)
- **Eventos de Auditoría**:
  - Creación de matrículas
  - Cancelación de matrículas
  - Cambios de curso
  - Pagos completados
- **API REST**: Endpoints para generar eventos de auditoría
- **Consumidores en Tiempo Real**: Procesamiento y logging de eventos
- **Kafka UI**: Interfaz web para visualizar topics y mensajes
- **Arquitectura de Microservicios**: Usando Spring Boot 3 y Spring Kafka

## Tecnologías Utilizadas

- Java 17
- Spring Boot 3.2.0
- Spring Kafka
- Apache Kafka 7.3.2
- ZooKeeper 7.3.2
- Docker & Docker Compose
- Kafka UI (Provectus)
- Maven

## Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────┐
│                    Enrollment REST API                       │
│  POST /api/enrollment/enroll                                │
│  POST /api/enrollment/{id}/cancel                           │
│  POST /api/enrollment/{id}/change-course                    │
│  POST /api/enrollment/{id}/payment                          │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
          ┌──────────────────────┐
          │ AuditEventProducer   │
          └──────────┬───────────┘
                     │
                     ▼
        ┌────────────────────────────┐
        │      Apache Kafka          │
        │  ┌──────────────────────┐  │
        │  │ enrollment.created   │  │
        │  │ enrollment.cancelled │  │
        │  │ course.changed       │  │
        │  │ payment.completed    │  │
        │  └──────────────────────┘  │
        └────────────┬───────────────┘
                     │
                     ▼
          ┌──────────────────────┐
          │ AuditEventConsumer   │
          │ (Logging & Storage)  │
          └──────────────────────┘
```

## Estructura del Proyecto

```
app-enrollment-kafka/
├── src/main/java/com/enrollment/audit/
│   ├── config/
│   │   └── KafkaConfig.java              # Configuración de topics
│   ├── controller/
│   │   └── EnrollmentController.java     # REST API endpoints
│   ├── dto/
│   │   ├── EnrollmentRequest.java
│   │   ├── CancelEnrollmentRequest.java
│   │   ├── ChangeCourseRequest.java
│   │   └── PaymentRequest.java
│   ├── event/
│   │   ├── EnrollmentCreatedEvent.java
│   │   ├── EnrollmentCancelledEvent.java
│   │   ├── CourseChangedEvent.java
│   │   └── PaymentCompletedEvent.java
│   ├── model/
│   │   ├── Student.java
│   │   ├── Course.java
│   │   └── Enrollment.java
│   ├── producer/
│   │   └── AuditEventProducer.java       # Kafka producer
│   ├── consumer/
│   │   └── AuditEventConsumer.java       # Kafka consumer
│   ├── service/
│   │   └── MockDataService.java          # Datos de prueba
│   └── EnrollmentAuditApplication.java   # Main class
├── src/main/resources/
│   └── application.properties            # Configuración
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## Requisitos Previos

- Docker Desktop instalado y corriendo
- Java 17+ (opcional, solo si quieres ejecutar localmente sin Docker)
- Maven 3.6+ (opcional, solo si quieres compilar localmente)
- Puerto 8080 (aplicación), 8090 (Kafka UI), 9092 (Kafka) disponibles

## Instalación y Ejecución

### Opción 1: Usando Docker Compose (Recomendado)

1. **Clonar o navegar al directorio del proyecto:**
   ```bash
   cd app-enrollment-kafka
   ```

2. **Iniciar todos los servicios:**
   ```bash
   docker-compose up --build
   ```

   Esto iniciará:
   - ZooKeeper (puerto 2181)
   - Kafka (puerto 9092)
   - Kafka UI (puerto 8090)
   - Aplicación Spring Boot (puerto 8080)

3. **Esperar a que todos los servicios estén saludables:**
   Verás mensajes en los logs indicando que la aplicación está lista:
   ```
   enrollment-app | Started EnrollmentAuditApplication in X seconds
   ```

4. **Acceder a las interfaces:**
   - API REST: http://localhost:8080
   - Kafka UI: http://localhost:8090
   - Health Check: http://localhost:8080/actuator/health

### Opción 2: Ejecución Local (sin Docker)

1. **Iniciar Kafka y ZooKeeper:**
   ```bash
   docker-compose up zookeeper kafka kafka-ui
   ```

2. **Compilar el proyecto:**
   ```bash
   mvn clean package
   ```

3. **Ejecutar la aplicación:**
   ```bash
   java -jar target/app-enrollment-kafka-1.0.0.jar
   ```

## Uso del Sistema

### 1. Ver Estudiantes Disponibles

```bash
curl http://localhost:8080/api/enrollment/students
```

**Respuesta:**
```json
[
  {
    "id": "STU001",
    "name": "Juan Pérez",
    "email": "juan.perez@email.com"
  },
  ...
]
```

### 2. Ver Cursos Disponibles

```bash
curl http://localhost:8080/api/enrollment/courses
```

**Respuesta:**
```json
[
  {
    "id": "CRS001",
    "name": "Introducción a Java",
    "instructor": "Dr. Alberto Ruiz"
  },
  ...
]
```

### 3. Crear una Matrícula

```bash
curl -X POST http://localhost:8080/api/enrollment/enroll \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": "STU001",
    "courseId": "CRS003"
  }'
```

**Respuesta:**
```json
{
  "message": "Enrollment created successfully",
  "enrollmentId": "ENR-a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "eventId": "EVT-x1y2z3a4-b5c6-7890-defg-hi9876543210",
  "student": "Juan Pérez",
  "course": "Microservicios con Kafka"
}
```

**Evento generado en Kafka:**
- Topic: `enrollment.created`
- Key: `ENR-a1b2c3d4-e5f6-7890-abcd-ef1234567890`
- Value: JSON con todos los detalles del evento

### 4. Cancelar una Matrícula

```bash
curl -X POST http://localhost:8080/api/enrollment/ENR-a1b2c3d4-e5f6-7890-abcd-ef1234567890/cancel \
  -H "Content-Type: application/json" \
  -d '{
    "reason": "Estudiante solicitó cambio de horario"
  }'
```

**Respuesta:**
```json
{
  "message": "Enrollment cancelled successfully",
  "enrollmentId": "ENR-a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "eventId": "EVT-...",
  "reason": "Estudiante solicitó cambio de horario"
}
```

**Evento generado en Kafka:**
- Topic: `enrollment.cancelled`

### 5. Cambiar de Curso

```bash
curl -X POST http://localhost:8080/api/enrollment/ENR-a1b2c3d4-e5f6-7890-abcd-ef1234567890/change-course \
  -H "Content-Type: application/json" \
  -d '{
    "newCourseId": "CRS005",
    "reason": "Interés en bases de datos NoSQL"
  }'
```

**Evento generado en Kafka:**
- Topic: `course.changed`

### 6. Registrar un Pago

```bash
curl -X POST http://localhost:8080/api/enrollment/ENR-a1b2c3d4-e5f6-7890-abcd-ef1234567890/payment \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 500.00,
    "currency": "USD",
    "paymentMethod": "Credit Card"
  }'
```

**Evento generado en Kafka:**
- Topic: `payment.completed`

### 7. Consultar una Matrícula

```bash
curl http://localhost:8080/api/enrollment/ENR-a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

### 8. Listar Todas las Matrículas

```bash
curl http://localhost:8080/api/enrollment
```

## Visualización de Eventos en Kafka UI

1. **Abrir Kafka UI:** http://localhost:8090

2. **Navegar a Topics:**
   - Verás los 4 topics de auditoría
   - `enrollment.created`
   - `enrollment.cancelled`
   - `course.changed`
   - `payment.completed`

3. **Ver Mensajes:**
   - Haz clic en cualquier topic
   - Selecciona "Messages"
   - Podrás ver todos los eventos con:
     - Offset
     - Partition
     - Timestamp
     - Key
     - Value (JSON completo del evento)

4. **Características útiles:**
   - Filtrar por clave
   - Buscar en valores
   - Ver detalles de particiones
   - Monitorear lag de consumidores

## Logs de Auditoría

Los eventos son procesados en tiempo real y registrados en los logs de la aplicación:

```bash
# Ver logs del consumidor
docker-compose logs -f enrollment-app
```

**Ejemplo de log:**
```
======================================
AUDIT LOG - ENROLLMENT CREATED
======================================
Event ID: EVT-x1y2z3a4-b5c6-7890-defg-hi9876543210
Enrollment ID: ENR-a1b2c3d4-e5f6-7890-abcd-ef1234567890
Student: Juan Pérez (juan.perez@email.com)
Course: Microservicios con Kafka - Instructor: Ing. Roberto Díaz
Timestamp: 2025-11-09T18:30:45.123
Kafka Metadata - Partition: 0, Offset: 42
======================================
```

## Configuración de Kafka

### Topics

Todos los topics se crean automáticamente con la siguiente configuración:

- **Particiones**: 3 (para escalabilidad horizontal)
- **Factor de replicación**: 1 (demo, en producción usar 3+)
- **Retención**: 7 días (168 horas)
- **Tamaño máximo**: 1 GB por topic

### Producer

- **Serialización**: JSON (usando Spring Kafka JsonSerializer)
- **Key**: EnrollmentId (para garantizar orden por matrícula)
- **Confirmaciones asíncronas**: Callbacks para logging

### Consumer

- **Group ID**: `enrollment-audit-group`
- **Auto Offset Reset**: `earliest` (lee desde el inicio)
- **Deserialización**: JSON (usando Spring Kafka JsonDeserializer)
- **Trusted Packages**: `*` (solo para demo, en producción especificar paquetes)

## Testing

### Prueba Manual Completa

```bash
# 1. Crear matrícula
ENROLL_ID=$(curl -s -X POST http://localhost:8080/api/enrollment/enroll \
  -H "Content-Type: application/json" \
  -d '{"studentId":"STU001","courseId":"CRS003"}' | jq -r '.enrollmentId')

echo "Enrollment ID: $ENROLL_ID"

# 2. Registrar pago
curl -X POST http://localhost:8080/api/enrollment/$ENROLL_ID/payment \
  -H "Content-Type: application/json" \
  -d '{"amount":500,"currency":"USD","paymentMethod":"Credit Card"}'

# 3. Cambiar curso
curl -X POST http://localhost:8080/api/enrollment/$ENROLL_ID/change-course \
  -H "Content-Type: application/json" \
  -d '{"newCourseId":"CRS005","reason":"Cambio de especialización"}'

# 4. Cancelar matrícula
curl -X POST http://localhost:8080/api/enrollment/$ENROLL_ID/cancel \
  -H "Content-Type: application/json" \
  -d '{"reason":"Estudiante abandonó el programa"}'

# 5. Verificar en Kafka UI
echo "Ver eventos en: http://localhost:8090"
```

Después de ejecutar estos comandos, verás:
- 4 eventos en Kafka (uno por cada operación)
- 4 entradas en los logs de auditoría
- Historial completo en Kafka UI

## Ventajas de Kafka para Auditoría

1. **Inmutabilidad**: Los eventos no se modifican ni eliminan
2. **Orden Garantizado**: Por partición/clave
3. **Replay**: Posibilidad de releer eventos históricos
4. **Escalabilidad**: Múltiples consumidores pueden leer el mismo log
5. **Durabilidad**: Persistencia configurable
6. **Performance**: Alta throughput para escritura y lectura
7. **Decoupling**: Productores y consumidores independientes

## Detener el Sistema

```bash
# Detener todos los servicios
docker-compose down

# Detener y eliminar volúmenes (limpia todos los datos)
docker-compose down -v
```

## Troubleshooting

### Puerto ya en uso
```bash
# Verificar puertos
netstat -ano | findstr :8080
netstat -ano | findstr :9092

# Matar proceso en Windows
taskkill /PID <PID> /F
```

### Kafka no se conecta
```bash
# Verificar salud de Kafka
docker-compose ps

# Ver logs de Kafka
docker-compose logs kafka
```

### Aplicación no inicia
```bash
# Ver logs de la aplicación
docker-compose logs enrollment-app

# Reconstruir imagen
docker-compose up --build enrollment-app
```

## Próximos Pasos (Mejoras Futuras)

- [ ] Persistencia en base de datos (PostgreSQL)
- [ ] API para consultar historial de auditoría
- [ ] Búsqueda avanzada de eventos
- [ ] Exportación de reportes (CSV, PDF)
- [ ] Autenticación y autorización
- [ ] Métricas con Prometheus
- [ ] Alertas para eventos críticos
- [ ] Schema Registry con Avro
- [ ] Compactación de logs para estado actual

## Licencia

Este proyecto es un demo educativo para demostrar el uso de Kafka en sistemas de auditoría.

## Autor

Sistema de Auditoría de Matrículas - Demo Kafka
