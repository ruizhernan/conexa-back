package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.StarwarschallengeApplication;
import conexa.starwarschallenge.dto.FilmDto;
import conexa.starwarschallenge.dto.JwtAuthenticationResponse;
import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.SignInRequest;
import conexa.starwarschallenge.dto.SingleResponseDto;
import conexa.starwarschallenge.entity.Role;
import conexa.starwarschallenge.entity.User;
import conexa.starwarschallenge.repository.UserRepository;
import conexa.starwarschallenge.service.AuthenticationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = StarwarschallengeApplication.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class FilmIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;

    private static String jwtToken;

    @BeforeAll
    void setUpOnce(@Autowired WebTestClient client) {
        WebTestClient localClient = client.mutate()
                .responseTimeout(Duration.ofSeconds(10))
                .build();

        User user = User.builder()
                .username("testuser_integration")
                .password(passwordEncoder.encode("testpassword"))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        userRepository.flush();
        userRepository.findByUsername("testuser_integration")
                .orElseThrow(() -> new RuntimeException("Test user not found after saving"));

        SignInRequest signInRequest = new SignInRequest("testuser_integration", "testpassword");
        JwtAuthenticationResponse response = authenticationService.signin(signInRequest).block();

        assertNotNull(response);
        assertNotNull(response.getToken());
        jwtToken = response.getToken();
    }

    @Test
    @DisplayName("Should return a paginated list of films from SWAPI or handle Too Many Requests")
    void getFilms_shouldReturnPagedFilmsFromSwapi() {
        webTestClient.get().uri("/api/v1/films?limit=10")
                .header("Authorization", "Bearer " + jwtToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(new ParameterizedTypeReference<PagedResponseDto<FilmDto>>() {})
                .consumeWith(response -> {
                    HttpStatus status = (HttpStatus) response.getStatus();
                    if (status.equals(HttpStatus.TOO_MANY_REQUESTS)) {
                        assertTrue(response.getResponseBody().getMessage().contains("Too Many Requests"), "Error message should indicate Too Many Requests");
                    } else if (status.equals(HttpStatus.OK)) {
                        PagedResponseDto<FilmDto> pagedResponse = response.getResponseBody();
                        assertNotNull(pagedResponse);
                        assertNotNull(pagedResponse.getResults());
                        assertFalse(pagedResponse.getResults().isEmpty(), "The results list should not be empty");
                        assertTrue(pagedResponse.getTotalRecords() > 0, "Total records should be greater than 0");
                        assertNotNull(pagedResponse.getResults().get(0).getProperties().getTitle(), "Film title should not be null");
                    } else {
                        fail("Unexpected status code: " + status);
                    }
                });
    }

    @Test
    @DisplayName("Should return a single film by ID from SWAPI or handle Too Many Requests")
    void getFilmById_shouldReturnSingleFilmFromSwapi() {
        webTestClient.get().uri("/api/v1/films?limit=1")
                .header("Authorization", "Bearer " + jwtToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(new ParameterizedTypeReference<PagedResponseDto<FilmDto>>() {})
                .consumeWith(result -> {
                    HttpStatus status = (HttpStatus) result.getStatus();
                    if (status.equals(HttpStatus.TOO_MANY_REQUESTS)) {
                        assertTrue(result.getResponseBody().getMessage().contains("Too Many Requests"), "Error message should indicate Too Many Requests");
                    } else if (status.equals(HttpStatus.OK)) {
                        PagedResponseDto<FilmDto> pagedResponse = result.getResponseBody();
                        assertNotNull(pagedResponse);
                        assertFalse(pagedResponse.getResults().isEmpty(), "The results list should not be empty to get an ID");

                        String filmId = pagedResponse.getResults().get(0).getUid();
                        assertNotNull(filmId, "Film ID should not be null");

                        webTestClient.get().uri("/api/v1/films/{id}", filmId)
                                .header("Authorization", "Bearer " + jwtToken)
                                .accept(MediaType.APPLICATION_JSON)
                                .exchange()
                                .expectBody(new ParameterizedTypeReference<SingleResponseDto<FilmDto>>() {})
                                .consumeWith(response -> {
                                    HttpStatus filmStatus = (HttpStatus) response.getStatus();
                                    if (filmStatus.equals(HttpStatus.TOO_MANY_REQUESTS)) {
                                        assertTrue(response.getResponseBody().getMessage().contains("Too Many Requests"), "Error message should indicate Too Many Requests");
                                    } else if (filmStatus.equals(HttpStatus.OK)) {
                                        SingleResponseDto<FilmDto> singleResponse = response.getResponseBody();
                                        assertNotNull(singleResponse);
                                        assertNotNull(singleResponse.getResult());
                                        assertNotNull(singleResponse.getResult().getProperties().getTitle(), "Film title should not be null");
                                        assertEquals(filmId, singleResponse.getResult().getUid(), "Returned film ID should match the requested ID");
                                    } else {
                                        fail("Unexpected status code for single film: " + filmStatus);
                                    }
                                });
                    } else {
                        fail("Unexpected status code for initial films list: " + status);
                    }
                });
    }

    @Test
    @DisplayName("Should return 404 for a non-existent film ID or handle Too Many Requests")
    void getFilmById_shouldReturnNotFoundForNonExistentId() {
        webTestClient.get().uri("/api/v1/films/{id}", "99999")
                .header("Authorization", "Bearer " + jwtToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(new ParameterizedTypeReference<Map<String, String>>() {})
                .consumeWith(response -> {
                    HttpStatus status = (HttpStatus) response.getStatus();
                    if (status.equals(HttpStatus.TOO_MANY_REQUESTS)) {
                        Map<String, String> errorResponse = response.getResponseBody();
                        assertNotNull(errorResponse);
                        assertTrue(errorResponse.get("error").contains("Too Many Requests"), "Error message should indicate Too Many Requests");
                    } else if (status.equals(HttpStatus.NOT_FOUND)) {
                    } else {
                        fail("Unexpected status code: " + status);
                    }
                });
    }

    @Test
    @DisplayName("Should filter films by name from SWAPI or handle Too Many Requests")
    void getFilms_shouldFilterFilmsByNameFromSwapi() {
        String filmName = "Hope";

        webTestClient.get().uri("/api/v1/films?name={name}", filmName)
                .header("Authorization", "Bearer " + jwtToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(new ParameterizedTypeReference<PagedResponseDto<FilmDto>>() {})
                .consumeWith(response -> {
                    if (response.getStatus().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                        assertTrue(response.getResponseBody().getMessage().contains("Too Many Requests"), "Error message should indicate Too Many Requests");
                    } else {
                        PagedResponseDto<FilmDto> pagedResponse = response.getResponseBody();
                        assertNotNull(pagedResponse);
                        assertNotNull(pagedResponse.getResults());
                        assertFalse(pagedResponse.getResults().isEmpty(), "The results list should not be empty when filtering by name");
                        assertTrue(pagedResponse.getResults().stream()
                                .allMatch(film -> film.getProperties().getTitle().toLowerCase().contains(filmName.toLowerCase())),
                                "All returned films should contain the filter name in their title");
                    }
                });
    }
}