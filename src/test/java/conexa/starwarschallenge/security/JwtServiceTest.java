package conexa.starwarschallenge.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private final String TEST_SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET_KEY);
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);
    }

    @Test
    @DisplayName("Should generate a valid token")
    void generateToken_ShouldReturnValidToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(TEST_USERNAME, extractedUsername);
    }

    @Test
    @DisplayName("Should extract correct username from token")
    void extractUsername_ShouldReturnCorrectUsername() {
        String token = jwtService.generateToken(userDetails);
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(TEST_USERNAME, extractedUsername);
    }

    @Test
    @DisplayName("Should return true for a valid token and matching user details")
    void isTokenValid_ShouldReturnTrueForValidTokenAndMatchingUserDetails() {
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    @DisplayName("Should return false for an invalid user details")
    void isTokenValid_ShouldReturnFalseForInvalidUserDetails() {
        String token = jwtService.generateToken(userDetails);

        UserDetails otherUserDetails = mock(UserDetails.class);
        when(otherUserDetails.getUsername()).thenReturn("otheruser");

        assertFalse(jwtService.isTokenValid(token, otherUserDetails));
    }

    @Test
    @DisplayName("Should return false for an expired token")
    void isTokenValid_ShouldReturnFalseForExpiredToken() throws InterruptedException {
        String expiredToken = Jwts.builder()
                .setSubject(TEST_USERNAME)
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60))
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 30))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET_KEY)))
                .compact();

        assertFalse(jwtService.isTokenValid(expiredToken, userDetails));
    }

    @Test
    @DisplayName("Should return false for malformed token")
    void isTokenValid_ShouldReturnFalseForMalformedToken() {
        String malformedToken = "invalid.token.string";
        assertFalse(jwtService.isTokenValid(malformedToken, userDetails));
    }
}
