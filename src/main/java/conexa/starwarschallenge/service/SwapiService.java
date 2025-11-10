package conexa.starwarschallenge.service;

import conexa.starwarschallenge.dto.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SwapiService {

    private final WebClient webClient;

    public SwapiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<PagedResponseDto<FilmDto>> findFilms(int page, int limit, String name) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/films");

                    if (StringUtils.hasText(name)) {
                        uriBuilder.queryParam("search", name);
                    }

                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(FilmListResponseDto.class)
                .map(filmResponse -> {
                    PagedResponseDto<FilmDto> pagedResponse = new PagedResponseDto<>();

                    List<FilmDto> allFilms = filmResponse.getResults();

                    int totalRecords = allFilms.size();
                    int startIndex = (page - 1) * limit;
                    int endIndex = Math.min(startIndex + limit, totalRecords);

                    List<FilmDto> pagedFilms;
                    if (startIndex < totalRecords) {
                        pagedFilms = new ArrayList<>(allFilms.subList(startIndex, endIndex));
                    } else {
                        pagedFilms = Collections.emptyList();
                    }

                    pagedResponse.setResults(pagedFilms);
                    pagedResponse.setTotalRecords(totalRecords);

                    return pagedResponse;
                })
                .flatMap(this::enrichFilms);
    }

    public Mono<PagedResponseDto<PersonDto>> findPeople(int page, int limit, String name) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/people")
                            .queryParam("page", page)
                            .queryParam("limit", limit);
                    if (StringUtils.hasText(name)) {
                        uriBuilder.queryParam("search", name);
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
                        uriBuilder.queryParam("search", name);
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
                        uriBuilder.queryParam("search", name);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PagedResponseDto<VehicleDto>>() {})
                .flatMap(this::enrichVehicles);
    }

    private Mono<PagedResponseDto<FilmDto>> enrichFilms(PagedResponseDto<FilmDto> pagedResponse) {
        if (pagedResponse.getResults().isEmpty()) return Mono.just(pagedResponse);
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
        if (pagedResponse.getResults().isEmpty()) return Mono.just(pagedResponse);
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
        if (pagedResponse.getResults().isEmpty()) return Mono.just(pagedResponse);
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
        if (pagedResponse.getResults().isEmpty()) return Mono.just(pagedResponse);
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