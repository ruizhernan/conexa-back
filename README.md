Desafío Star Wars API - Backend

Tecnologías

Framework: Spring Boot 3.2.5

Lenguaje: Java 21

Seguridad: Spring Security y JWT (Autenticación Stateless)

Persistencia: Spring Data JPA (H2/MySQL)

Conteinerizado: Docker

Documentación: Swagger

Cliente HTTP: RestTemplate

Estrategia de Pruebas (JUnit 5 y MockMvc)

Integración: Verifica el flujo completo de la API (incluyendo la capa de seguridad y el cliente HTTP). Implementada con @SpringBootTest para simular peticiones HTTP reales.

Unitaria / Componente: Valida la lógica de negocio y el manejo de excepciones. Incluye pruebas específicas para el GlobalExceptionHandler utilizando MockMvcBuilders.standaloneSetup.

Este proyecto se encuentra dockerizado, para ser desplegado en cualquier servicio de nube. (se encuentra en un servidor VPS en este momento)
Para correrlo localmente:

1 - Clonar el repo

2 - En la carpeta root del proyecto correr docker-compose up -d --build (se requiere docker desktop instalado)

3 - Para ver los test unit e integration usar mvn test

4 - La app va a quedar corriendo en el puerto 8080

5 - Documentación Swagger disponible en http://srv559732.hstgr.cloud:8080/swagger-ui/index.html#
