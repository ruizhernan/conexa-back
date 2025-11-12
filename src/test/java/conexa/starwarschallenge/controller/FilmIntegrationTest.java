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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = StarwarschallengeApplication.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class FilmIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;

    private static String jwtToken;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    void setUpOnce() throws Exception {
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
        MvcResult result = mockMvc.perform(post("/api/v1/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JwtAuthenticationResponse response = objectMapper.readValue(responseContent, JwtAuthenticationResponse.class);

        assertNotNull(response);
        assertNotNull(response.getToken());
        jwtToken = response.getToken();
    }

    @Test
    @DisplayName("Should return a paginated list of films from SWAPI or handle Too Many Requests")
    void getFilms_shouldReturnPagedFilmsFromSwapi() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/films?limit=10")
                        .header("Authorization", "Bearer " + jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        HttpStatus status = HttpStatus.valueOf(result.getResponse().getStatus());
        String responseContent = result.getResponse().getContentAsString();

        if (status.equals(HttpStatus.TOO_MANY_REQUESTS)) {
            Map<String, String> errorResponse = objectMapper.readValue(responseContent, new TypeReference<Map<String, String>>() {});
            assertTrue(errorResponse.get("error").contains("Too Many Requests"), "Error message should indicate Too Many Requests");
        } else if (status.equals(HttpStatus.OK)) {
            PagedResponseDto<FilmDto> pagedResponse = objectMapper.readValue(responseContent, new TypeReference<PagedResponseDto<FilmDto>>() {});
            assertNotNull(pagedResponse);
            assertNotNull(pagedResponse.getResults());
            assertFalse(pagedResponse.getResults().isEmpty(), "The results list should not be empty");
            assertTrue(pagedResponse.getTotalRecords() > 0, "Total records should be greater than 0");
            assertNotNull(pagedResponse.getResults().get(0).getProperties().getTitle(), "Film title should not be null");
        } else {
            fail("Unexpected status code: " + status);
        }
    }

    @Test
    @DisplayName("Should return a single film by ID from SWAPI or handle Too Many Requests")
    void getFilmById_shouldReturnSingleFilmFromSwapi() throws Exception {
        MvcResult initialResult = mockMvc.perform(get("/api/v1/films?limit=1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        HttpStatus initialStatus = HttpStatus.valueOf(initialResult.getResponse().getStatus());
        String initialResponseContent = initialResult.getResponse().getContentAsString();

        if (initialStatus.equals(HttpStatus.TOO_MANY_REQUESTS)) {
            Map<String, String> errorResponse = objectMapper.readValue(initialResponseContent, new TypeReference<Map<String, String>>() {});
            assertTrue(errorResponse.get("error").contains("Too Many Requests"), "Error message should indicate Too Many Requests");
        } else if (initialStatus.equals(HttpStatus.OK)) {
            PagedResponseDto<FilmDto> pagedResponse = objectMapper.readValue(initialResponseContent, new TypeReference<PagedResponseDto<FilmDto>>() {});
            assertNotNull(pagedResponse);
            assertFalse(pagedResponse.getResults().isEmpty(), "The results list should not be empty to get an ID");

            String filmId = pagedResponse.getResults().get(0).getUid();
            assertNotNull(filmId, "Film ID should not be null");

            MvcResult filmResult = mockMvc.perform(get("/api/v1/films/{id}", filmId)
                            .header("Authorization", "Bearer " + jwtToken)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            HttpStatus filmStatus = HttpStatus.valueOf(filmResult.getResponse().getStatus());
            String filmResponseContent = filmResult.getResponse().getContentAsString();

            if (filmStatus.equals(HttpStatus.TOO_MANY_REQUESTS)) {
                Map<String, String> errorResponse = objectMapper.readValue(filmResponseContent, new TypeReference<Map<String, String>>() {});
                assertTrue(errorResponse.get("error").contains("Too Many Requests"), "Error message should indicate Too Many Requests");
            } else if (filmStatus.equals(HttpStatus.OK)) {
                SingleResponseDto<FilmDto> singleResponse = objectMapper.readValue(filmResponseContent, new TypeReference<SingleResponseDto<FilmDto>>() {});
                assertNotNull(singleResponse);
                assertNotNull(singleResponse.getResult());
                assertNotNull(singleResponse.getResult().getProperties().getTitle(), "Film title should not be null");
                assertEquals(filmId, singleResponse.getResult().getUid(), "Returned film ID should match the requested ID");
            } else {
                fail("Unexpected status code for single film: " + filmStatus);
            }
        } else {
            fail("Unexpected status code for initial films list: " + initialStatus);
        }
    }

    @Test
    @DisplayName("Should return 404 for a non-existent film ID or handle Too Many Requests")
    void getFilmById_shouldReturnNotFoundForNonExistentId() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/films/{id}", "99999")
                        .header("Authorization", "Bearer " + jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        HttpStatus status = HttpStatus.valueOf(result.getResponse().getStatus());
        String responseContent = result.getResponse().getContentAsString();

        if (status.equals(HttpStatus.TOO_MANY_REQUESTS)) {
            // CAMBIO: Usamos TypeReference de Jackson
            Map<String, String> errorResponse = objectMapper.readValue(responseContent, new TypeReference<Map<String, String>>() {});
            assertNotNull(errorResponse);
            assertTrue(errorResponse.get("error").contains("Too Many Requests"), "Error message should indicate Too Many Requests");
        } else if (status.equals(HttpStatus.NOT_FOUND)) {
            // Expected behavior, no further assertions needed for 404
        } else {
            fail("Unexpected status code: " + status);
        }
    }

    @Test
    @DisplayName("Should filter films by name from SWAPI or handle Too Many Requests")
    void getFilms_shouldFilterFilmsByNameFromSwapi() throws Exception {
        String filmName = "Hope";

        MvcResult result = mockMvc.perform(get("/api/v1/films?name={name}", filmName)
                        .header("Authorization", "Bearer " + jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        HttpStatus status = HttpStatus.valueOf(result.getResponse().getStatus());
        String responseContent = result.getResponse().getContentAsString();

        if (status.equals(HttpStatus.TOO_MANY_REQUESTS)) {
            Map<String, String> errorResponse = objectMapper.readValue(responseContent, new TypeReference<Map<String, String>>() {});
            assertTrue(errorResponse.get("error").contains("Too Many Requests"), "Error message should indicate Too Many Requests");
        } else {
            PagedResponseDto<FilmDto> pagedResponse = objectMapper.readValue(responseContent, new TypeReference<PagedResponseDto<FilmDto>>() {});
            assertNotNull(pagedResponse);
            assertNotNull(pagedResponse.getResults());
            assertFalse(pagedResponse.getResults().isEmpty(), "The results list should not be empty when filtering by name");
            assertTrue(pagedResponse.getResults().stream()
                            .allMatch(film -> film.getProperties().getTitle().toLowerCase().contains(filmName.toLowerCase())),
                    "All returned films should contain the filter name in their title");
        }
    }
}