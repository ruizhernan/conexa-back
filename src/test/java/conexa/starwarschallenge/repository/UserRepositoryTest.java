package conexa.starwarschallenge.repository;

import conexa.starwarschallenge.entity.User;
import conexa.starwarschallenge.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByUsername_shouldReturnUser_whenUserExists() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(Role.USER);
        entityManager.persistAndFlush(user);

        // When
        Optional<User> foundUser = userRepository.findByUsername("testuser");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenUserDoesNotExist() {
        // When
        Optional<User> foundUser = userRepository.findByUsername("nonexistentuser");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenUserExists() {
        // Given
        User user = new User();
        user.setUsername("existinguser");
        user.setPassword("password");
        user.setRole(Role.USER);
        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByUsername("existinguser");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUserDoesNotExist() {
        // When
        boolean exists = userRepository.existsByUsername("anothernonexistentuser");

        // Then
        assertThat(exists).isFalse();
    }
}
