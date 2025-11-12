package conexa.starwarschallenge.service;

import conexa.starwarschallenge.dto.JwtAuthenticationResponse;
import conexa.starwarschallenge.dto.SignInRequest;
import conexa.starwarschallenge.dto.SignUpRequest;
import conexa.starwarschallenge.dto.UserDto;

public interface AuthenticationService {
    UserDto signup(SignUpRequest request);
    JwtAuthenticationResponse signin(SignInRequest request);
}
