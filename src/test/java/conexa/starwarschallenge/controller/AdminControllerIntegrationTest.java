package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.StarwarschallengeApplication;
import conexa.starwarschallenge.dto.UpdateUserRoleRequest;
import conexa.starwarschallenge.dto.UserRoleDto;
import conexa.starwarschallenge.entity.Role;
import conexa.starwarschallenge.entity.User;
import conexa.starwarschallenge.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = StarwarschallengeApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        adminUser = User.builder()
                .username("admin_test")
                .password(passwordEncoder.encode("adminpass"))
                .role(Role.ADMIN)
                .build();
        userRepository.save(adminUser);

        regularUser = User.builder()
                .username("user_test")
                .password(passwordEncoder.encode("userpass"))
                .role(Role.USER)
                .build();
        userRepository.save(regularUser);
    }

    @Test
    @DisplayName("Admin should be able to get all users")
    @WithMockUser(username = "admin_test", authorities = {"ADMIN"})
    void adminCanGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is(adminUser.getUsername())))
                .andExpect(jsonPath("$[0].role", is(adminUser.getRole().name())))
                .andExpect(jsonPath("$[1].username", is(regularUser.getUsername())))
                .andExpect(jsonPath("$[1].role", is(regularUser.getRole().name())));
    }

    @Test
    @DisplayName("Admin should be able to update a user's role")
    @WithMockUser(username = "admin_test", authorities = {"ADMIN"})
    void adminCanUpdateUserRole() throws Exception {
        UpdateUserRoleRequest request = new UpdateUserRoleRequest(Role.ADMIN);

        mockMvc.perform(put("/api/v1/admin/users/{id}/role", regularUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(regularUser.getId())))
                .andExpect(jsonPath("$.username", is(regularUser.getUsername())))
                .andExpect(jsonPath("$.role", is(Role.ADMIN.name())));

        User updatedUser = userRepository.findById(regularUser.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals(Role.ADMIN, updatedUser.getRole());
    }

    @Test
    @DisplayName("Regular user should be forbidden from getting all users")
    @WithMockUser(username = "user_test", authorities = {"USER"})
    void regularUserCannotGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Regular user should be forbidden from updating a user's role")
    @WithMockUser(username = "user_test", authorities = {"USER"})
    void regularUserCannotUpdateUserRole() throws Exception {
        UpdateUserRoleRequest request = new UpdateUserRoleRequest(Role.ADMIN);

        mockMvc.perform(put("/api/v1/admin/users/{id}/role", regularUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Unauthenticated user should be forbidden from getting all users")
    void unauthenticatedUserCannotGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Unauthenticated user should be forbidden from updating a user's role")
    void unauthenticatedUserCannotUpdateUserRole() throws Exception {
        UpdateUserRoleRequest request = new UpdateUserRoleRequest(Role.ADMIN);

        mockMvc.perform(put("/api/v1/admin/users/{id}/role", regularUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
