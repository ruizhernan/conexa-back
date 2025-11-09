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
import conexa.starwarschallenge.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ReactiveAuthenticationManager authenticationManager;
    private final ReactiveUserDetailsService userDetailsService;

    @Override
    public Mono<UserDto> signup(SignUpRequest request) {
        return Mono.fromCallable(() -> {
            userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
                throw new DuplicateUserException("Username '" + request.getUsername() + "' is already taken.");
            });

            var user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER).build();
            userRepository.save(user);
            return UserDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .build();
        });
    }

    @Override
    public Mono<JwtAuthenticationResponse> signin(SignInRequest request) {
        return authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()))
                .flatMap(authentication -> userDetailsService.findByUsername(request.getUsername()))
                .map(userDetails -> {
                    var jwt = jwtService.generateToken(userDetails);
                    var role = userDetails.getAuthorities().stream()
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("User has no roles"))
                            .getAuthority();
                    return JwtAuthenticationResponse.builder()
                            .token(jwt)
                            .role(role)
                            .build();
                });
    }
}
