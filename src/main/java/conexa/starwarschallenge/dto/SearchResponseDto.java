package conexa.starwarschallenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseDto<T> {

    private String message;

    @JsonProperty("results")
    private List<T> results;
}