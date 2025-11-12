package conexa.starwarschallenge.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StarshipDto {

    private StarshipPropertiesDto properties;
    private String description;
    @JsonProperty("_id")
    private String _id;
    private String uid;
    @JsonProperty("__v")
    private String __v;
}
