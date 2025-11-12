package conexa.starwarschallenge.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleRawItemDto {
    private String uid;
    private String name;
    private String url;
}