package conexa.starwarschallenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class FilmListResponseDto {

    private String message;

    @JsonProperty("result")
    private List<FilmDto> results;

}