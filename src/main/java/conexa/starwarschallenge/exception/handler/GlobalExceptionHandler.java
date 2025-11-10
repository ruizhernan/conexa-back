package conexa.starwarschallenge.exception.handler;

import conexa.starwarschallenge.exception.DuplicateUserException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateUserException(DuplicateUserException ex) {
        return new ResponseEntity<>(
                Map.of("error", ex.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Invalid username or password"),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String>> handleJwtExceptions(JwtException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Invalid or expired token"),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(WebExchangeBindException ex) {
        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value"
                ));
        return new ResponseEntity<>(
                Map.of("errors", errors),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(WebClientResponseException.NotFound.class)
    public ResponseEntity<Map<String, String>> handleSwapiNotFoundException(WebClientResponseException.NotFound ex) {
        return new ResponseEntity<>(
                Map.of("error", "The requested resource was not found in the Star Wars API."),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllUncaughtException(Exception ex) {
        return new ResponseEntity<>(
                Map.of("error", "Something went wrong: " + ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
