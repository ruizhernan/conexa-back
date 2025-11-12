package conexa.starwarschallenge.dto;

import java.time.Instant;

public record ErrorDto(
        String error,
        int status,
        Instant timestamp
) {}