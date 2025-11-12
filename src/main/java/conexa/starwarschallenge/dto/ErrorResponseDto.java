package conexa.starwarschallenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private String error;
    private int status;
    private ZonedDateTime timestamp;
    private String path;
}