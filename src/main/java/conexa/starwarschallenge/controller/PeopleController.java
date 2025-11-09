package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.PersonDto;
import conexa.starwarschallenge.service.SwapiService;
import conexa.starwarschallenge.dto.SingleResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/people")
public class PeopleController {

    private final SwapiService swapiService;

    public PeopleController(SwapiService swapiService) {
        this.swapiService = swapiService;
    }

    /**
     * Retrieves a paginated list of people, with optional filtering by name.
     *
     * @param page the page number for pagination (default is 1).
     * @param limit the number of items per page (default is 10).
     * @param name an optional name to filter the list of people.
     * @return a Mono containing a paged response with people data.
     */
    @GetMapping
    public Mono<PagedResponseDto<PersonDto>> getPeople(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String name) {
        return swapiService.findPeople(page, limit, name);
    }

    /**
     * Retrieves a single person by their unique ID.
     *
     * @param id the unique identifier of the person.
     * @return a Mono containing a single response with the person's data.
     */
    @GetMapping("/{id}")
    public Mono<SingleResponseDto<PersonDto>> getPersonById(@PathVariable String id) {
        return swapiService.findPersonById(id);
    }
}
