# Etapa de construcción
FROM maven:3.9.3-eclipse-temurin-17 AS build

# Copiar el pom.xml y los fuentes
COPY pom.xml .
COPY src ./src/

# Construir la aplicación
RUN mvn -X -f pom.xml clean package -DskipTests

# Etapa de ejecución
FROM openjdk:17-jdk-slim

# Actualizar los paquetes y luego instalar libglib2.0-0
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        libglib2.0-0 && \
        libnspr4 \
        libnss3 \
        libnss3-nssdb \
        libnss3-tools \
    rm -rf /var/lib/apt/lists/*

# Copiar el JAR de la etapa de construcción
COPY --from=build target/demo-0.0.1-SNAPSHOT.jar /app/demo-0.0.1-SNAPSHOT.jar

# Exponer el puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app/demo-0.0.1-SNAPSHOT.jar"]
