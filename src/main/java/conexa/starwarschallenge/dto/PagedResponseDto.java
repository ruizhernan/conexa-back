package conexa.starwarschallenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder; // Asegúrate de que esta línea esté presente
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder // Añadimos @Builder para habilitar PagedResponseDto.builder()
@NoArgsConstructor
@AllArgsConstructor // Necesario para que @Builder funcione correctamente con todos los campos
public class PagedResponseDto<T> {

    private String message;
    @JsonProperty("total_records")
    private int totalRecords;
    @JsonProperty("total_pages")
    private int totalPages;
    private String previous;
    private String next;

    private List<T> results = new ArrayList<>();

    public PagedResponseDto(List<T> results, String message, int totalRecords, int totalPages) {
        this.results = results;
        this.message = message;
        this.totalRecords = totalRecords;
        this.totalPages = totalPages;
    }
}