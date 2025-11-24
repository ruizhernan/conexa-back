package conexa.starwarschallenge.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilmRawItemDto {
    private String uid;
    @JsonProperty("title")
    private String name;
    private String url;
}