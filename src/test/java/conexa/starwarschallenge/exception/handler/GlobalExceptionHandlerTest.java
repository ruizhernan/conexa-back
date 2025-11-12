package conexa.starwarschallenge.exception.handler;

import conexa.starwarschallenge.exception.DuplicateUserException;
import conexa.starwarschallenge.exception.TooManyRequestsException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new MockController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleDuplicateUserException_shouldReturnConflict() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/test/duplicate-user")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("User already exists"));
    }

    @Test
    void handleTooManyRequestsException_shouldReturnTooManyRequests() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/test/too-many-requests")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Too Many Requests"));
    }

    @Test
    void handleBadCredentialsException_shouldReturnUnauthorized() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/test/bad-credentials")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid or expired token/credentials"));
    }

    @Test
    void handleMalformedJwtException_shouldReturnUnauthorized() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/test/malformed-jwt")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid or expired token/credentials"));
    }

    @Test
    void handleSignatureException_shouldReturnUnauthorized() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/test/signature-exception")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid or expired token/credentials"));
    }

    @Test
    void handleExpiredJwtException_shouldReturnUnauthorized() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/test/expired-jwt")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid or expired token/credentials"));
    }

    @Test
    void handleUnsupportedJwtException_shouldReturnUnauthorized() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/test/unsupported-jwt")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid or expired token/credentials"));
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/test/generic-exception")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Something went wrong"));
    }

    @Test
    void handleValidationExceptions_shouldReturnBadRequest() throws Exception {
        String invalidRequestBody = "{\"name\": \"\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/test/validation-error")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.name").value("Name is mandatory"));
    }

    @RestController
    static class MockController {
        @GetMapping({"/test/duplicate-user"})
        public String throwDuplicateUserException() {
            throw new DuplicateUserException("User already exists");
        }

        @GetMapping({"/test/too-many-requests"})
        public String throwTooManyRequestsException() {
            throw new TooManyRequestsException("Too Many Requests");
        }

        @GetMapping({"/test/bad-credentials"})
        public String throwBadCredentialsException() {
            throw new BadCredentialsException("Invalid credentials");
        }

        @GetMapping({"/test/malformed-jwt"})
        public String throwMalformedJwtException() {
            throw new MalformedJwtException("Malformed JWT");
        }

        @GetMapping({"/test/signature-exception"})
        public String throwSignatureException() {
            throw new SignatureException("Invalid signature");
        }

        @GetMapping({"/test/expired-jwt"})
        public String throwExpiredJwtException() {
            throw new ExpiredJwtException(null, null, "Expired JWT");
        }

        @GetMapping({"/test/unsupported-jwt"})
        public String throwUnsupportedJwtException() {
            throw new UnsupportedJwtException("Unsupported JWT");
        }

        @GetMapping({"/test/generic-exception"})
        public String throwGenericException() {
            throw new RuntimeException("Something went wrong");
        }

        @PostMapping({"/test/validation-error"})
        public String throwValidationException(@Valid @RequestBody TestRequestBody requestBody) {
            return "OK";
        }
    }

    static class TestRequestBody {
        @NotBlank(message = "Name is mandatory")
        private String name;

        public TestRequestBody() {}

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}