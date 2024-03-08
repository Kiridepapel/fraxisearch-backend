# Documentación de la API

## Descripción
Esta API proporciona endpoints para buscar información de usuarios basada en diferentes criterios como nombre completo, nombre individual o número de documento de identidad (DNI).

## Endpoints

### 1. Buscar por nombre completo
- **URL:** `/fraxisearch/api/v1/find-by-full-name`
- **Método:** GET
- **Descripción:** Busca información de usuario utilizando el nombre completo proporcionado.
- **Parámetros de solicitud:** 
  - `fullName`: El nombre completo del usuario.

    ```json
    {
      "fullName": "",
    }
    ```
- **Respuesta:** Devuelve la información del usuario correspondiente al nombre completo proporcionado.

### 2. Buscar por nombre individual
- **URL:** `/fraxisearch/api/v1/find-by-single-name`
- **Método:** GET
- **Descripción:** Busca información de usuario utilizando un nombre individual.
- **Parámetros de solicitud:** 
  - `names`: El nombre del usuario.
  - `fatherLastName`: Apellido paterno del usuario.
  - `motherLastName`: Apellido materno del usuario.
  
    ```json
    {
      "names": "",
      "fatherLastName": "",
      "motherLastName": ""
    }
    ```
- **Respuesta:** Devuelve la información del usuario correspondiente al nombre individual proporcionado.

### 3. Buscar por número de documento de identidad (DNI)
- **URL:** `/fraxisearch/api/v1/find-by-dni`
- **Método:** GET
- **Descripción:** Busca información de usuario utilizando el número de documento de identidad (DNI) proporcionado.
- **Parámetros de solicitud:** 
  - `dni`: El número de DNI del usuario.
  
    ```json
    {
      "dni": ,
    }
    ```
- **Respuesta:** Devuelve la información del usuario correspondiente al número de DNI proporcionado.
