# Usamos una imagen base de OpenJDK con JDK 17
FROM eclipse-temurin:17-jdk-alpine AS build

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar los archivos de Maven Wrapper y el archivo de configuración
# Copiar primero estos archivos para aprovechar el caché de Docker si no cambian
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./

# --- CORRECCIÓN AQUÍ ---
# Asegurar que mvnw tenga permisos de ejecución
RUN chmod +x mvnw

# Descargar dependencias de Maven antes de copiar el código fuente
# Esto aprovecha el caché de Docker si las dependencias no cambian
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente del proyecto
COPY src ./src

# Compilar la aplicación con Maven sin ejecutar pruebas
RUN ./mvnw package -DskipTests

# --- Etapa final ---
# Usar una imagen ligera de OpenJDK para ejecutar la aplicación
FROM eclipse-temurin:17-jre-alpine

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el JAR generado desde la etapa de compilación
# Usar un comodín es más robusto si el nombre del JAR cambia
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto en el que la aplicación se ejecutará (Render usará el $PORT)
# EXPOSE 8080 # No es estrictamente necesario si se usa $PORT en el comando start

# Definir el comando para ejecutar la aplicación
# Render pasará el puerto correcto a través de la variable de entorno $PORT
# El Start Command en Render debería ser: java -Dserver.port=$PORT -jar target/*.jar
# Pero el ENTRYPOINT en Dockerfile debe ser genérico:
ENTRYPOINT ["java", "-jar", "app.jar"]

