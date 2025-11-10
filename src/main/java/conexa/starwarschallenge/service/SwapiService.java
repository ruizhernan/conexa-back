package conexa.starwarschallenge.service;

import conexa.starwarschallenge.dto.FilmDto;
import conexa.starwarschallenge.dto.FilmListResponseDto;
import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.PersonDto;
import conexa.starwarschallenge.dto.StarshipDto;
import conexa.starwarschallenge.dto.VehicleDto;
import conexa.starwarschallenge.dto.SingleResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import conexa.starwarschallenge.exception.TooManyRequestsException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(FilmListResponseDto.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        return Mono.error(new TooManyRequestsException("Too Many Requests to SWAPI for films. Please try again later."));
                    }
                    return Mono.error(ex);
                })
                .map(filmListResponse -> {
                    PagedResponseDto<FilmDto> pagedResponse = new PagedResponseDto<>();
                    pagedResponse.setMessage(filmListResponse.getMessage());
                    pagedResponse.setResults(filmListResponse.getResult());
                    pagedResponse.setTotalRecords(filmListResponse.getResult().size());
                    pagedResponse.setTotalPages(1);

                    if (StringUtils.hasText(name)) {
                        List<FilmDto> filteredFilms = pagedResponse.getResults().stream()
                                .filter(film -> film.getProperties().getTitle().toLowerCase().contains(name.toLowerCase()))
                                .collect(Collectors.toList());
                        pagedResponse.setResults(filteredFilms);
                        pagedResponse.setTotalRecords(filteredFilms.size());
                        int originalLimit = limit;
                        if (originalLimit == 0) originalLimit = 10;
                        pagedResponse.setTotalPages((int) Math.ceil((double) filteredFilms.size() / originalLimit));
                    }
                    return pagedResponse;
                });
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
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        return Mono.error(new TooManyRequestsException("Too Many Requests to SWAPI for people. Please try again later."));
                    }
                    return Mono.error(ex);
                })
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
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        return Mono.error(new TooManyRequestsException("Too Many Requests to SWAPI for starships. Please try again later."));
                    }
                    return Mono.error(ex);
                })
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
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        return Mono.error(new TooManyRequestsException("Too Many Requests to SWAPI for vehicles. Please try again later."));
                    }
                    return Mono.error(ex);
                })
                .flatMap(this::enrichVehicles);
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
                .bodyToMono(new ParameterizedTypeReference<SingleResponseDto<FilmDto>>() {})
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        return Mono.error(new TooManyRequestsException("Too Many Requests to SWAPI for film ID " + id + ". Please try again later."));
                    }
                    return Mono.error(ex);
                });
    }

    public Mono<SingleResponseDto<PersonDto>> findPersonById(String id) {
        return webClient.get()
                .uri("/people/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SingleResponseDto<PersonDto>>() {})
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        return Mono.error(new TooManyRequestsException("Too Many Requests to SWAPI for person ID " + id + ". Please try again later."));
                    }
                    return Mono.error(ex);
                });
    }

    public Mono<SingleResponseDto<StarshipDto>> findStarshipById(String id) {
        return webClient.get()
                .uri("/starships/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SingleResponseDto<StarshipDto>>() {})
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        return Mono.error(new TooManyRequestsException("Too Many Requests to SWAPI for starship ID " + id + ". Please try again later."));
                    }
                    return Mono.error(ex);
                });
    }

    public Mono<SingleResponseDto<VehicleDto>> findVehicleById(String id) {
        return webClient.get()
                .uri("/vehicles/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SingleResponseDto<VehicleDto>>() {})
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        return Mono.error(new TooManyRequestsException("Too Many Requests to SWAPI for vehicle ID " + id + ". Please try again later."));
                    }
                    return Mono.error(ex);
                });
    }
}