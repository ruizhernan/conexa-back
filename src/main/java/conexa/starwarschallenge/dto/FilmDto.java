package conexa.starwarschallenge.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilmDto {

    private FilmPropertiesDto properties;
    private String description;
    private String _id;
    private String uid;
    private String __v;
}