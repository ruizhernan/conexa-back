package conexa.starwarschallenge.service;

import conexa.starwarschallenge.dto.FilmDto;
import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.PersonDto;
import conexa.starwarschallenge.dto.StarshipDto;
import conexa.starwarschallenge.dto.VehicleDto;
import conexa.starwarschallenge.dto.SingleResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.util.StringUtils;

@Service
public class SwapiService {

    private final WebClient webClient;

    public SwapiService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Finds a paginated list of films, optionally filtered by name.
     *
     * @param page the page number to retrieve.
     * @param limit the number of items per page.
     * @param name the name to filter by (optional).
     * @return a Mono containing a paged response of FilmDto.
     */
    public Mono<PagedResponseDto<FilmDto>> findFilms(int page, int limit, String name) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/films")
                            .queryParam("page", page)
                            .queryParam("limit", limit);
                    if (StringUtils.hasText(name)) {
                        uriBuilder.queryParam("name", name);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PagedResponseDto<FilmDto>>() {});
    }

    /**
     * Finds a paginated list of people, optionally filtered by name.
     *
     * @param page the page number to retrieve.
     * @param limit the number of items per page.
     * @param name the name to filter by (optional).
     * @return a Mono containing a paged response of PersonDto.
     */
    public Mono<PagedResponseDto<PersonDto>> findPeople(int page, int limit, String name) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/people")
                            .queryParam("page", page)
                            .queryParam("limit", limit);
                    if (StringUtils.hasText(name)) {
                        uriBuilder.queryParam("name", name);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PagedResponseDto<PersonDto>>() {});
    }

    /**
     * Finds a paginated list of starships, optionally filtered by name.
     *
     * @param page the page number to retrieve.
     * @param limit the number of items per page.
     * @param name the name to filter by (optional).
     * @return a Mono containing a paged response of StarshipDto.
     */
    public Mono<PagedResponseDto<StarshipDto>> findStarships(int page, int limit, String name) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/starships")
                            .queryParam("page", page)
                            .queryParam("limit", limit);
                    if (StringUtils.hasText(name)) {
                        uriBuilder.queryParam("name", name);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PagedResponseDto<StarshipDto>>() {});
    }

    /**
     * Finds a paginated list of vehicles, optionally filtered by name.
     *
     * @param page the page number to retrieve.
     * @param limit the number of items per page.
     * @param name the name to filter by (optional).
     * @return a Mono containing a paged response of VehicleDto.
     */
    public Mono<PagedResponseDto<VehicleDto>> findVehicles(int page, int limit, String name) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/vehicles")
                            .queryParam("page", page)
                            .queryParam("limit", limit);
                    if (StringUtils.hasText(name)) {
                        uriBuilder.queryParam("name", name);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PagedResponseDto<VehicleDto>>() {});
    }

    /**
     * Finds a single film by its ID.
     *
     * @param id the ID of the film to retrieve.
     * @return a Mono containing a single response of FilmDto.
     */
    public Mono<SingleResponseDto<FilmDto>> findFilmById(String id) {
        return webClient.get()
                .uri("/films/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SingleResponseDto<FilmDto>>() {});
    }

    /**
     * Finds a single person by their ID.
     *
     * @param id the ID of the person to retrieve.
     * @return a Mono containing a single response of PersonDto.
     */
    public Mono<SingleResponseDto<PersonDto>> findPersonById(String id) {
        return webClient.get()
                .uri("/people/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SingleResponseDto<PersonDto>>() {});
    }

    /**
     * Finds a single starship by its ID.
     *
     * @param id the ID of the starship to retrieve.
     * @return a Mono containing a single response of StarshipDto.
     */
    public Mono<SingleResponseDto<StarshipDto>> findStarshipById(String id) {
        return webClient.get()
                .uri("/starships/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SingleResponseDto<StarshipDto>>() {});
    }

    /**
     * Finds a single vehicle by its ID.
     *
     * @param id the ID of the vehicle to retrieve.
     * @return a Mono containing a single response of VehicleDto.
     */
    public Mono<SingleResponseDto<VehicleDto>> findVehicleById(String id) {
        return webClient.get()
                .uri("/vehicles/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SingleResponseDto<VehicleDto>>() {});
    }
}
