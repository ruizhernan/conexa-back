package conexa.starwarschallenge.service;

import conexa.starwarschallenge.dto.FilmDto;
import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.PersonDto;
import conexa.starwarschallenge.dto.StarshipDto;
import conexa.starwarschallenge.dto.VehicleDto;
import conexa.starwarschallenge.dto.SingleResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SwapiService {

    private final WebClient webClient;

    public SwapiService(WebClient webClient) {
        this.webClient = webClient;
    }

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
                .bodyToMono(new ParameterizedTypeReference<PagedResponseDto<FilmDto>>() {})
                .flatMap(this::enrichFilms);
    }

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
                .bodyToMono(new ParameterizedTypeReference<PagedResponseDto<PersonDto>>() {})
                .flatMap(this::enrichPeople);
    }

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
                .bodyToMono(new ParameterizedTypeReference<PagedResponseDto<StarshipDto>>() {})
                .flatMap(this::enrichStarships);
    }

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
                .bodyToMono(new ParameterizedTypeReference<PagedResponseDto<VehicleDto>>() {})
                .flatMap(this::enrichVehicles);
    }

    private Mono<PagedResponseDto<FilmDto>> enrichFilms(PagedResponseDto<FilmDto> pagedResponse) {
        return Flux.fromIterable(pagedResponse.getResults())
                .flatMap(film -> findFilmById(film.getUid()))
                .map(SingleResponseDto::getResult)
                .collectList()
                .map(enrichedFilms -> {
                    pagedResponse.setResults(enrichedFilms);
                    return pagedResponse;
                });
    }

    private Mono<PagedResponseDto<PersonDto>> enrichPeople(PagedResponseDto<PersonDto> pagedResponse) {
        return Flux.fromIterable(pagedResponse.getResults())
                .flatMap(person -> findPersonById(person.getUid()))
                .map(SingleResponseDto::getResult)
                .collectList()
                .map(enrichedPeople -> {
                    pagedResponse.setResults(enrichedPeople);
                    return pagedResponse;
                });
    }

    private Mono<PagedResponseDto<StarshipDto>> enrichStarships(PagedResponseDto<StarshipDto> pagedResponse) {
        return Flux.fromIterable(pagedResponse.getResults())
                .flatMap(starship -> findStarshipById(starship.getUid()))
                .map(SingleResponseDto::getResult)
                .collectList()
                .map(enrichedStarships -> {
                    pagedResponse.setResults(enrichedStarships);
                    return pagedResponse;
                });
    }

    private Mono<PagedResponseDto<VehicleDto>> enrichVehicles(PagedResponseDto<VehicleDto> pagedResponse) {
        return Flux.fromIterable(pagedResponse.getResults())
                .flatMap(vehicle -> findVehicleById(vehicle.getUid()))
                .map(SingleResponseDto::getResult)
                .collectList()
                .map(enrichedVehicles -> {
                    pagedResponse.setResults(enrichedVehicles);
                    return pagedResponse;
                });
    }

    public Mono<SingleResponseDto<FilmDto>> findFilmById(String id) {
        return webClient.get()
                .uri("/films/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SingleResponseDto<FilmDto>>() {});
    }

    public Mono<SingleResponseDto<PersonDto>> findPersonById(String id) {
        return webClient.get()
                .uri("/people/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SingleResponseDto<PersonDto>>() {});
    }

    public Mono<SingleResponseDto<StarshipDto>> findStarshipById(String id) {
        return webClient.get()
                .uri("/starships/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SingleResponseDto<StarshipDto>>() {});
    }

    public Mono<SingleResponseDto<VehicleDto>> findVehicleById(String id) {
        return webClient.get()
                .uri("/vehicles/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SingleResponseDto<VehicleDto>>() {});
    }
}
