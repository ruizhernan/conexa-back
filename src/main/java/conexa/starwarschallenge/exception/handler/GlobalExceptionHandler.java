package conexa.starwarschallenge.exception.handler;

import conexa.starwarschallenge.dto.ErrorDto;
import conexa.starwarschallenge.dto.ValidationErrorDto;
import conexa.starwarschallenge.exception.DuplicateUserException;
import conexa.starwarschallenge.exception.FilmNotFoundException;
import conexa.starwarschallenge.exception.PersonNotFoundException;
import conexa.starwarschallenge.exception.StarshipNotFoundException;
import conexa.starwarschallenge.exception.VehicleNotFoundException;
import conexa.starwarschallenge.exception.TooManyRequestsException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // --- 409 Conflict ---
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorDto> handleDuplicateUserException(DuplicateUserException ex) {
        ErrorDto error = new ErrorDto(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // --- 429 Too Many Requests ---
    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorDto> handleTooManyRequestsException(TooManyRequestsException ex) {
        ErrorDto error = new ErrorDto(
                ex.getMessage(),
                HttpStatus.TOO_MANY_REQUESTS.value(),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
    }

    // --- 404 Not Found (Domain Specific) ---
    @ExceptionHandler(FilmNotFoundException.class)
    public ResponseEntity<ErrorDto> handleFilmNotFoundException(FilmNotFoundException ex) {
        ErrorDto error = new ErrorDto(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<ErrorDto> handlePersonNotFoundException(PersonNotFoundException ex) {
        ErrorDto error = new ErrorDto(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StarshipNotFoundException.class)
    public ResponseEntity<ErrorDto> handleStarshipNotFoundException(StarshipNotFoundException ex) {
        ErrorDto error = new ErrorDto(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<ErrorDto> handleVehicleNotFoundException(VehicleNotFoundException ex) {
        ErrorDto error = new ErrorDto(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // --- 401 Unauthorized (Authentication/JWT Errors) ---
    @ExceptionHandler({
            BadCredentialsException.class,
            MalformedJwtException.class,
            SignatureException.class,
            ExpiredJwtException.class,
            UnsupportedJwtException.class
    })
    public ResponseEntity<ErrorDto> handleAuthenticationExceptions(Exception ex) {
        String message = "Invalid or expired token/credentials";
        ErrorDto error = new ErrorDto(
                message,
                HttpStatus.UNAUTHORIZED.value(),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    // --- 400 Bad Request (Validation Errors) ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ValidationErrorDto error = new ValidationErrorDto(
                errors,
                HttpStatus.BAD_REQUEST.value(),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // --- 500 Internal Server Error (Generic Fallback) ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGenericException(Exception ex) {
        String message = ex.getMessage() != null && !ex.getMessage().isBlank() ? ex.getMessage() : "An unexpected error occurred";

        ErrorDto error = new ErrorDto(
                message,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}