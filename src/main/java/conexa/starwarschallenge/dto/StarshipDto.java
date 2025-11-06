package conexa.starwarschallenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StarshipDto {

    private StarshipPropertiesDto properties;
    private String description;
    private String _id;
    private String uid;
    private String __v;
}
