package conexa.starwarschallenge.service;

import conexa.starwarschallenge.dto.FilmDto;
import conexa.starwarschallenge.dto.PagedResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SwapiService {

    private final WebClient webClient;

    public SwapiService(WebClient webClient) {
        this.webClient = webClient;
    }

import org.springframework.util.StringUtils;

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
}
