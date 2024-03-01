# # Etapa de construcción
# FROM maven:3.9.3-eclipse-temurin-17 AS build

# # Copiar el pom.xml y los fuentes
# COPY pom.xml .
# COPY src ./src/

# # Construir la aplicación
# RUN mvn -X -f pom.xml clean package -DskipTests

# # Etapa de ejecución
# FROM openjdk:17-jdk-slim

# # Copiar el JAR de la etapa de construcción
# COPY --from=build target/demo-0.0.1-SNAPSHOT.jar /app/demo-0.0.1-SNAPSHOT.jar

# # Exponer el puerto
# EXPOSE 8080

# # Comando para ejecutar la aplicación
# ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app/demo-0.0.1-SNAPSHOT.jar"]

# Etapa de construcción
FROM ubuntu:20.04 AS build

# Actualizar e instalar las dependencias necesarias
RUN apt-get update && \
    apt-get install -y maven openjdk-17-jdk && \
    apt-get install -y libglib2.0-0 && \
    apt-get clean

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el pom.xml y los fuentes
COPY pom.xml .
COPY src ./src/

# Construir la aplicación
RUN mvn -X -f pom.xml clean package -DskipTests

# Etapa de ejecución
FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el JAR de la etapa de construcción
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar /app/demo-0.0.1-SNAPSHOT.jar

# Exponer el puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app/demo-0.0.1-SNAPSHOT.jar"]
