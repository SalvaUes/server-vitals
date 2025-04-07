# Server Vitals (SVIT)

**Server Vitals (SVIT)** es una aplicación Java desarrollada con Spring Boot que permite monitorear el consumo de recursos del servidor (CPU, RAM, Disco), generar alertas configurables, consultar información del dominio, hacer health checks de la base de datos y mucho más.

Este proyecto se encuentra **dockerizado**, lo que facilita su ejecución y despliegue en cualquier entorno compatible con Docker.

---

## 🚀 Tecnologías utilizadas

- **Java 17**
- **Spring Boot**
- **PostgreSQL**
- **Docker + Docker Compose**
- **Maven Wrapper (mvnw)**

---

## ⚙️ Instrucciones de instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu_usuario/server-vitals.git
cd server-vitals

 Ejecutar la aplicación con Docker Compose

docker compose --build up
