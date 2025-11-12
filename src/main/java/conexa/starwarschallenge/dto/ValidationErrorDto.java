package conexa.starwarschallenge.dto;

import java.time.Instant;
import java.util.Map;

public record ValidationErrorDto(
        Map<String, String> errors,
        int status,
        Instant timestamp
) {}