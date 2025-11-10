package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.JwtAuthenticationResponse;
import conexa.starwarschallenge.dto.SignInRequest;
import conexa.starwarschallenge.dto.SignUpRequest;
import conexa.starwarschallenge.dto.UserDto;
import conexa.starwarschallenge.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(authController).build();
    }

    @Test
    void signup_shouldReturnCreatedUser() {
        SignUpRequest signUpRequest = new SignUpRequest("testuser", "password");
        UserDto userDto = UserDto.builder().username("testuser").role("USER").build();

        when(authenticationService.signup(any(SignUpRequest.class)))
                .thenReturn(Mono.just(userDto));

        webTestClient.post().uri("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signUpRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserDto.class)
                .consumeWith(response -> {
                    UserDto responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.getUsername().equals("testuser");
                });
    }

    @Test
    void signin_shouldReturnJwtAuthenticationResponse() {
        SignInRequest signInRequest = new SignInRequest("testuser", "password");
        JwtAuthenticationResponse jwtResponse = JwtAuthenticationResponse.builder().token("jwt_token").role("USER").build();

        when(authenticationService.signin(any(SignInRequest.class)))
                .thenReturn(Mono.just(jwtResponse));

        webTestClient.post().uri("/api/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtAuthenticationResponse.class)
                .consumeWith(response -> {
                    JwtAuthenticationResponse responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.getToken().equals("jwt_token");
                });
    }
}
