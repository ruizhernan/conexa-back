package conexa.starwarschallenge.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListResponseDto<T> {
    private String message;
    private int total_records;
    private int total_pages;
    private String previous;
    private String next;
    private List<T> results;
}