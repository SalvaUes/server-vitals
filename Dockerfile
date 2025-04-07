# Usamos una imagen base de OpenJDK con JDK 17
FROM eclipse-temurin:17-jdk-alpine AS build

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar los archivos de Maven Wrapper y el archivo de configuración
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn

# Copiar el archivo pom.xml para descargar dependencias
COPY pom.xml ./

# Descargar dependencias de Maven antes de copiar el código fuente
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente del proyecto
COPY src ./src

# Compilar la aplicación con Maven sin ejecutar pruebas
RUN ./mvnw package -DskipTests

# Usar una imagen ligera de OpenJDK para ejecutar la aplicación
FROM eclipse-temurin:17-jre-alpine

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el JAR generado desde la etapa de compilación
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto en el que la aplicación se ejecutará
EXPOSE 8080

# Definir el comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]