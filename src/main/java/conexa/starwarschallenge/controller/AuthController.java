package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.JwtAuthenticationResponse;
import conexa.starwarschallenge.dto.SignInRequest;
import conexa.starwarschallenge.dto.SignUpRequest;
import conexa.starwarschallenge.dto.UserDto;
import conexa.starwarschallenge.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public Mono<ResponseEntity<UserDto>> signup(@RequestBody SignUpRequest request) {
        return authenticationService.signup(request)
                .map(userDto -> new ResponseEntity<>(userDto, HttpStatus.CREATED));
    }

    @PostMapping("/signin")
    public Mono<ResponseEntity<JwtAuthenticationResponse>> signin(@RequestBody SignInRequest request) {
        return authenticationService.signin(request)
                .map(ResponseEntity::ok);
    }
}
