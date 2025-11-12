package conexa.starwarschallenge.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StarshipRawItemDto {
    private String uid;
    private String name;
    private String url;
}