package conexa.starwarschallenge.service;

import conexa.starwarschallenge.dto.JwtAuthenticationResponse;
import conexa.starwarschallenge.dto.SignInRequest;
import conexa.starwarschallenge.dto.SignUpRequest;
import conexa.starwarschallenge.dto.UserDto;
import reactor.core.publisher.Mono;

public interface AuthenticationService {
    Mono<UserDto> signup(SignUpRequest request);
    Mono<JwtAuthenticationResponse> signin(SignInRequest request);
}
