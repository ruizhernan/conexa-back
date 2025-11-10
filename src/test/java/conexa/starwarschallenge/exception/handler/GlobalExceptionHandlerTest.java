package conexa.starwarschallenge.exception.handler;

import conexa.starwarschallenge.exception.DuplicateUserException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

class GlobalExceptionHandlerTest {

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        this.webTestClient = WebTestClient.bindToController(new MockController())
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @RestController
    static class MockController {
        @GetMapping("/test/duplicate-user")
        public Mono<String> throwDuplicateUserException() {
            return Mono.error(new DuplicateUserException("User already exists"));
        }

        @GetMapping("/test/bad-credentials")
        public Mono<String> throwBadCredentialsException() {
            return Mono.error(new BadCredentialsException("Invalid credentials"));
        }

        @GetMapping("/test/malformed-jwt")
        public Mono<String> throwMalformedJwtException() {
            return Mono.error(new MalformedJwtException("Malformed JWT"));
        }

        @GetMapping("/test/signature-exception")
        public Mono<String> throwSignatureException() {
            return Mono.error(new SignatureException("Invalid signature"));
        }

        @GetMapping("/test/expired-jwt")
        public Mono<String> throwExpiredJwtException() {
            return Mono.error(new ExpiredJwtException(null, null, "Expired JWT"));
        }

        @GetMapping("/test/unsupported-jwt")
        public Mono<String> throwUnsupportedJwtException() {
            return Mono.error(new UnsupportedJwtException("Unsupported JWT"));
        }

        @GetMapping("/test/swapi-not-found")
        public Mono<String> throwSwapiNotFoundException() {
            return Mono.error(WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "404 Not Found", null, null, null));
        }

        @GetMapping("/test/generic-exception")
        public Mono<String> throwGenericException() {
            return Mono.error(new RuntimeException("Something went wrong"));
        }
    }

    @Test
    void handleDuplicateUserException_shouldReturnConflict() {
        webTestClient.get().uri("/test/duplicate-user")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("User already exists");
    }

    @Test
    void handleBadCredentialsException_shouldReturnUnauthorized() {
        webTestClient.get().uri("/test/bad-credentials")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Invalid username or password");
    }

    @Test
    void handleMalformedJwtException_shouldReturnUnauthorized() {
        webTestClient.get().uri("/test/malformed-jwt")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Invalid or expired token");
    }

    @Test
    void handleSignatureException_shouldReturnUnauthorized() {
        webTestClient.get().uri("/test/signature-exception")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Invalid or expired token");
    }

    @Test
    void handleExpiredJwtException_shouldReturnUnauthorized() {
        webTestClient.get().uri("/test/expired-jwt")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Invalid or expired token");
    }

    @Test
    void handleUnsupportedJwtException_shouldReturnUnauthorized() {
        webTestClient.get().uri("/test/unsupported-jwt")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Invalid or expired token");
    }

    @Test
    void handleSwapiNotFoundException_shouldReturnNotFound() {
        webTestClient.get().uri("/test/swapi-not-found")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("The requested resource was not found in the Star Wars API.");
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() {
        webTestClient.get().uri("/test/generic-exception")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Something went wrong: Something went wrong");
    }
}