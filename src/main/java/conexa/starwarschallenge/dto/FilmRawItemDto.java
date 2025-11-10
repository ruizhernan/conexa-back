package conexa.starwarschallenge.dto;

import lombok.Data;

@Data
public class FilmRawItemDto {

    private FilmPropertiesDto properties;
    private String description;
    private String uid;

    public FilmDto toFilmDto() {
        FilmDto filmDto = new FilmDto();

        filmDto.setProperties(this.properties);
        filmDto.setDescription(this.description);
        filmDto.setUid(this.uid);
        filmDto.set_id(null);
        filmDto.set__v(null);

        return filmDto;
    }
}