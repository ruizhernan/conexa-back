Desafío Star Wars API - Backend

Tecnologías

Framework: Spring Boot 3.2.5

Lenguaje: Java 21

Seguridad: Spring Security y JWT (Autenticación Stateless)

Persistencia: Spring Data JPA (H2/MySQL)

Cliente HTTP: RestTemplate

Estrategia de Pruebas (JUnit 5 y MockMvc)

Integración: Verifica el flujo completo de la API (incluyendo la capa de seguridad y el cliente HTTP). Implementada con @SpringBootTest para simular peticiones HTTP reales.

Unitaria / Componente: Valida la lógica de negocio y el manejo de excepciones. Incluye pruebas específicas para el GlobalExceptionHandler utilizando MockMvcBuilders.standaloneSetup.