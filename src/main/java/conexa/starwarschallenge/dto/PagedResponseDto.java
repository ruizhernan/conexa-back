package conexa.starwarschallenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PagedResponseDto<T> {

    private String message;
    @JsonProperty("total_records")
    private int totalRecords;
    @JsonProperty("total_pages")
    private int totalPages;
    private String previous;
    private String next;
    private List<T> results = new ArrayList<>();
}