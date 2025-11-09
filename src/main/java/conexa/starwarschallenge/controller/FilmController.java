package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.FilmDto;
import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.service.SwapiService;
import conexa.starwarschallenge.dto.SingleResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/films")
public class FilmController {

    private final SwapiService swapiService;

    public FilmController(SwapiService swapiService) {
        this.swapiService = swapiService;
    }

    /**
     * Retrieves a paginated list of films, with optional filtering by name.
     *
     * @param page the page number for pagination (default is 1).
     * @param limit the number of items per page (default is 10).
     * @param name an optional name to filter the list of films.
     * @return a Mono containing a paged response with film data.
     */
    @GetMapping
    public Mono<PagedResponseDto<FilmDto>> getFilms(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String name) {
        return swapiService.findFilms(page, limit, name);
    }

    /**
     * Retrieves a single film by its unique ID.
     *
     * @param id the unique identifier of the film.
     * @return a Mono containing a single response with the film's data.
     */
    @GetMapping("/{id}")
    public Mono<SingleResponseDto<FilmDto>> getFilmById(@PathVariable String id) {
        return swapiService.findFilmById(id);
    }
}
