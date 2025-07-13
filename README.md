# Linktic - Solución de Microservicios (Productos e Inventario)

Este repositorio contiene una solución técnica para el ejercicio de entrevista en Linktic, basada en dos microservicios desarrollados con **Spring Boot**, comunicados mediante **JSON**\*\*:API\*\*, autenticados por **API Key**, desplegados con **Docker** y documentados con **Swagger/OpenAPI**.

---

## 🌐 Tecnologías Utilizadas

- Java 17 + Spring Boot
- Spring Data JPA + PostgreSQL
- Springdoc OpenAPI 3 (Swagger UI)
- Docker + Docker Compose
- Postman (pruebas)

---

## 🌐 Repositorio con código fuente (Acceso publico):

- https://github.com/JuanOrtiz1/Inventario.git

---

## 📁 Estructura del Proyecto

```
/
├── productos/             # Microservicio de productos
│   ├── Dockerfile
│   └── src/
├── inventario/            # Microservicio de inventario
│   ├── Dockerfile
│   └── src/
├── docker-compose.yml     # Orquestación global
└── README.md
```

---

## 🎓 Descripción de los Microservicios

### 👉 Productos

- Gestiona recursos `productos` con campos: `id`, `nombre`, `precio`.
- Soporta:
  - Crear, consultar, actualizar, eliminar producto por ID.
  - Listar productos con paginación.

### 👉 Inventario

- Gestiona inventarios con campos: `producto_id`, `cantidad`.
- Soporta:
  - Consultar cantidad por producto ID (llama a microservicio productos).
  - Registrar venta (resta la cantidad vendida).
  - Actualizar cantidad directamente.
  - Emitir evento en consola cuando cambia el inventario.

---

## 🧱 Base de Datos

- **Nombre:** `linktic_bd`
- **Tablas:** `tb_producto`, `tb_inventario`
- Motor: **PostgreSQL** (contenedorizado)

---

## 🚀 Levantar la Solución con Docker

### 1. Compilar los proyectos:

```bash
cd productos
./mvnw clean package -DskipTests
cd ../inventario
./mvnw clean package -DskipTests
cd ..
```

### 2. Levantar todo con Docker Compose:

```bash
docker-compose up --build
```

### 3. Acceso a los servicios:

| Servicio   | URL local                                      |
| ---------- | ---------------------------------------------- |
| Productos  | [http://localhost:8083]                        |
| Inventario | [http://localhost:8084]                        |
| PostgreSQL | localhost:5432 / linktic                       |

---

## 📃 Documentación Swagger

| Servicio   | Swagger UI                                                                     |
| ---------- | ------------------------------------------------------------------------------ |
| Productos  | [http://localhost:8083/swagger-ui.html]                                        |
| Inventario | [http://localhost:8084/swagger-ui.html]                                        |

---

## ⚡ JSON\:API y Seguridad

### 🌐 JSON\:API

Todos los endpoints usan estructura JSON\:API:

```json
{
  "data": {
    "type": "productos",
    "id": "1",
    "attributes": {
      "nombre": "Teclado",
      "precio": 50.0
    }
  }
}
```

### 🔐 API Key

- Se define la API Key en el `.env` y se pasa en cada petición:

```
x-api-key: micro123
```

---

## 📝 Pruebas con Postman

### Headers necesarios

```
Content-Type: application/vnd.api+json
Accept: application/vnd.api+json
x-api-key: micro123
```

### Ejemplo: Crear producto

**POST** [http://localhost:8083/productos]

```json
{
  "data": {
    "type": "productos",
    "attributes": {
      "nombre": "Mouse",
      "precio": 75.00
    }
  }
}
```

### Ejemplo: Consultar inventario

**GET** [http://localhost:8084/inventarios/1]

---

## ✅ Pruebas unitarias

- Se han incluido pruebas de:
  - Creación y actualización de productos
  - Comunicación con el microservicio productos desde inventario
  - Manejo de errores comunes (producto no encontrado, stock insuficiente)

---

## 📚 Consideraciones de arquitectura

- Arquitectura basada en microservicios
- Servicios desacoplados, comunicados por REST con JSON\:API
- Autenticación interna por API Key
- Docker como plataforma de despliegue local y potencialmente cloud
- PostgreSQL como base de datos central compartida

---

## 🌊 Extensiones futuras

- Incorporar RabbitMQ o Kafka para eventos reales
- Persistencia de logs de eventos
- Seguridad con OAuth2 / JWT en vez de API key
- Monitoreo con Prometheus + Grafana

---

## 🏆 Autor

**Juan Pablo Ortiz Merchán**
Proyecto desarrollado como parte de la prueba técnica para Linktic.

