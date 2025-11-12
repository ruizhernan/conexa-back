package conexa.starwarschallenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StarshipNotFoundException extends RuntimeException {

    public StarshipNotFoundException(String id) {
        super("Starship with ID " + id + " not found.");
    }
}