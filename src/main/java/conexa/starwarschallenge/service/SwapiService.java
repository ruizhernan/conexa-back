package conexa.starwarschallenge.service;

import conexa.starwarschallenge.dto.*;
import conexa.starwarschallenge.enums.SwapiResource;
import conexa.starwarschallenge.exception.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SwapiService {

    @Value("${swapi.base-url}")
    private String swapiBaseUrl;

    private final RestTemplate restTemplate;

    public SwapiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PagedResponseDto<FilmRawItemDto> findFilms(int page, int limit, String name) {
        return findResources(
                SwapiResource.FILMS,
                page,
                limit,
                name,
                new ParameterizedTypeReference<PagedResponseDto<FilmRawItemDto>>() {},
                new ParameterizedTypeReference<SearchResponseDto<FilmRawItemDto>>() {}
        );
    }

    public PagedResponseDto<PersonRawItemDto> findPeople(int page, int limit, String name) {
        return findResources(
                SwapiResource.PEOPLE,
                page,
                limit,
                name,
                new ParameterizedTypeReference<PagedResponseDto<PersonRawItemDto>>() {},
                new ParameterizedTypeReference<SearchResponseDto<PersonRawItemDto>>() {}
        );
    }

    public PagedResponseDto<StarshipRawItemDto> findStarships(int page, int limit, String name) {
        return findResources(
                SwapiResource.STARSHIPS,
                page,
                limit,
                name,
                new ParameterizedTypeReference<PagedResponseDto<StarshipRawItemDto>>() {},
                new ParameterizedTypeReference<SearchResponseDto<StarshipRawItemDto>>() {}
        );
    }

    public PagedResponseDto<VehicleRawItemDto> findVehicles(int page, int limit, String name) {
        return findResources(
                SwapiResource.VEHICLES,
                page,
                limit,
                name,
                new ParameterizedTypeReference<PagedResponseDto<VehicleRawItemDto>>() {},
                new ParameterizedTypeReference<SearchResponseDto<VehicleRawItemDto>>() {}
        );
    }

    private <T> PagedResponseDto<T> findResources(
            SwapiResource resource,
            int page,
            int limit,
            String name,
            ParameterizedTypeReference<PagedResponseDto<T>> pagedResponseType,
            ParameterizedTypeReference<SearchResponseDto<T>> searchResponseType) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(swapiBaseUrl).path(resource.getPath());

        boolean isSearch = StringUtils.hasText(name);

        if (isSearch) {
            builder.queryParam("search", name);
        } else {
            builder.queryParam("page", page);
        }
        URI uri = builder.build().toUri();

        try {
            if (isSearch) {
                ResponseEntity<SearchResponseDto<T>> response = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        null,
                        searchResponseType
                );

                SearchResponseDto<T> body = response.getBody();
                if (body == null || body.getResults() == null) {
                    return new PagedResponseDto<>(Collections.emptyList(), "No search data received from SWAPI.", 0, 0);
                }

                return PagedResponseDto.<T>builder()
                        .message(body.getMessage())
                        .results(body.getResults())
                        .totalRecords(body.getResults().size())
                        .totalPages(1)
                        .build();

            } else {
                ResponseEntity<PagedResponseDto<T>> response = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        null,
                        pagedResponseType
                );

                PagedResponseDto<T> body = response.getBody();
                if (body == null) {
                    return new PagedResponseDto<>(Collections.emptyList(), "No paginated data received from SWAPI.", 0, 0);
                }
                return body;
            }

        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new TooManyRequestsException("Too Many Requests to SWAPI for " + resource.getResourceName() + ". Please try again later.");
            }
            throw ex;
        }
    }

    public SingleResponseDto<FilmDto> findFilmById(String id) {
        return findResourceById(SwapiResource.FILMS, id, new ParameterizedTypeReference<SingleResponseDto<FilmDto>>() {}, FilmNotFoundException::new);
    }

    public SingleResponseDto<PersonDto> findPersonById(String id) {
        return findResourceById(SwapiResource.PEOPLE, id, new ParameterizedTypeReference<SingleResponseDto<PersonDto>>() {}, PersonNotFoundException::new);
    }

    public SingleResponseDto<StarshipDto> findStarshipById(String id) {
        return findResourceById(SwapiResource.STARSHIPS, id, new ParameterizedTypeReference<SingleResponseDto<StarshipDto>>() {}, StarshipNotFoundException::new);
    }

    public SingleResponseDto<VehicleDto> findVehicleById(String id) {
        return findResourceById(SwapiResource.VEHICLES, id, new ParameterizedTypeReference<SingleResponseDto<VehicleDto>>() {}, VehicleNotFoundException::new);
    }

    private <T> SingleResponseDto<T> findResourceById(
            SwapiResource resource,
            String id,
            ParameterizedTypeReference<SingleResponseDto<T>> responseType,
            java.util.function.Function<String, RuntimeException> notFoundExceptionSupplier) {

        String uri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path(resource.getPath() + "/{id}")
                .buildAndExpand(id)
                .toUriString();

        try {
            ResponseEntity<SingleResponseDto<T>> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    responseType
            );

            if (response.getBody() == null) {
                return new SingleResponseDto<>(null, "No data received from SWAPI.", false);
            }
            return response.getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            throw notFoundExceptionSupplier.apply(id);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new TooManyRequestsException("Too Many Requests to SWAPI for " + resource.getResourceName() + " ID " + id + ". Please try again later.");
            }
            throw ex;
        }
    }
}