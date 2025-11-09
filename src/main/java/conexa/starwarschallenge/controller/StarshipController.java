package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.StarshipDto;
import conexa.starwarschallenge.service.SwapiService;
import conexa.starwarschallenge.dto.SingleResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/starships")
public class StarshipController {

    private final SwapiService swapiService;

    public StarshipController(SwapiService swapiService) {
        this.swapiService = swapiService;
    }

    /**
     * Retrieves a paginated list of starships, with optional filtering by name.
     *
     * @param page the page number for pagination (default is 1).
     * @param limit the number of items per page (default is 10).
     * @param name an optional name to filter the list of starships.
     * @return a Mono containing a paged response with starship data.
     */
    @GetMapping
    public Mono<PagedResponseDto<StarshipDto>> getStarships(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String name) {
        return swapiService.findStarships(page, limit, name);
    }

    /**
     * Retrieves a single starship by its unique ID.
     *
     * @param id the unique identifier of the starship.
     * @return a Mono containing a single response with the starship's data.
     */
    @GetMapping("/{id}")
    public Mono<SingleResponseDto<StarshipDto>> getStarshipById(@PathVariable String id) {
        return swapiService.findStarshipById(id);
    }
}
