package conexa.starwarschallenge.service.impl;

import conexa.starwarschallenge.dto.JwtAuthenticationResponse;
import conexa.starwarschallenge.dto.SignInRequest;
import conexa.starwarschallenge.dto.SignUpRequest;
import conexa.starwarschallenge.dto.UserDto;
import conexa.starwarschallenge.entity.Role;
import conexa.starwarschallenge.entity.User;
import conexa.starwarschallenge.exception.DuplicateUserException;
import conexa.starwarschallenge.repository.UserRepository;
import conexa.starwarschallenge.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private ReactiveAuthenticationManager authenticationManager;
    @Mock
    private ReactiveUserDetailsService userDetailsService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private SignUpRequest signUpRequest;
    private SignInRequest signInRequest;
    private User user;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest("testuser", "password");
        signInRequest = new SignInRequest("testuser", "password");
        user = User.builder()
                .id(1)
                .username("testuser")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
        jwtToken = "mockedJwtToken";
    }

    @Test
    @DisplayName("Should successfully sign up a new user")
    void signup_Success() {
        when(userRepository.findByUsername(signUpRequest.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1); // Simulate ID generation by the database
            return savedUser;
        });

        Mono<UserDto> responseMono = authenticationService.signup(signUpRequest);
        UserDto response = responseMono.block(); // Blocking for testing reactive flow

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getId(), response.getId());
        verify(userRepository, times(1)).findByUsername(signUpRequest.getUsername());
        verify(passwordEncoder, times(1)).encode(signUpRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, never()).generateToken(any(User.class)); // signup does not generate JWT
    }

    @Test
    @DisplayName("Should throw DuplicateUserException when signing up with existing username")
    void signup_DuplicateUser() {
        when(userRepository.findByUsername(signUpRequest.getUsername())).thenReturn(Optional.of(user));

        assertThrows(DuplicateUserException.class, () -> authenticationService.signup(signUpRequest).block());

        verify(userRepository, times(1)).findByUsername(signUpRequest.getUsername());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should successfully sign in an existing user")
    void signin_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(mock(org.springframework.security.core.Authentication.class))); // Mock successful authentication
        when(userDetailsService.findByUsername(signInRequest.getUsername())).thenReturn(Mono.just(user));
        when(jwtService.generateToken(any(User.class))).thenReturn(jwtToken);

        Mono<JwtAuthenticationResponse> responseMono = authenticationService.signin(signInRequest);
        JwtAuthenticationResponse response = responseMono.block(); // Blocking for testing reactive flow

        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
        assertEquals(user.getRole().name(), response.getRole());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, times(1)).findByUsername(signInRequest.getUsername());
        verify(jwtService, times(1)).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when signing in with invalid credentials")
    void signin_InvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.error(new RuntimeException("Bad credentials"))); // Simulate bad credentials

        assertThrows(RuntimeException.class, () -> authenticationService.signin(signInRequest).block());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, never()).findByUsername(anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }
}
