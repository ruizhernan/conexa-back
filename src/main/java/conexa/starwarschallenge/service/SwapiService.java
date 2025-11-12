package conexa.starwarschallenge.service;

import conexa.starwarschallenge.dto.*;
import conexa.starwarschallenge.exception.FilmNotFoundException;
import conexa.starwarschallenge.exception.TooManyRequestsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

    public PagedResponseDto<FilmDto> findFilms(int page, int limit, String name) {
        String uri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/films")
                .build().toUriString();

        FilmListResponseDto filmListResponse;
        try {
            filmListResponse = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    FilmListResponseDto.class
            ).getBody();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new TooManyRequestsException("Too Many Requests to SWAPI for films. Please try again later.");
            }
            throw ex;
        }

        // Obtener lista inicial o vacía
        List<FilmDto> allFilms = filmListResponse.getResult() != null ? filmListResponse.getResult() : Collections.emptyList();

        if (StringUtils.hasText(name)) {
            allFilms = allFilms.stream()
                    .filter(film -> film.getProperties().getTitle().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        int totalRecords = allFilms.size();
        int totalPages = (int) Math.ceil((double) totalRecords / limit);

        int offset = (page - 1) * limit;
        List<FilmDto> pagedFilms;

        if (offset < totalRecords) {
            int endIndex = Math.min(offset + limit, totalRecords);
            pagedFilms = allFilms.subList(offset, endIndex);
        } else {
            pagedFilms = Collections.emptyList();
        }

        PagedResponseDto<FilmDto> pagedResponse = new PagedResponseDto<>();
        pagedResponse.setResults(pagedFilms);
        pagedResponse.setMessage(filmListResponse.getMessage());
        pagedResponse.setTotalRecords(totalRecords);
        pagedResponse.setTotalPages(totalPages);

        return pagedResponse;
    }

    public PagedResponseDto<PersonDto> findPeople(int page, int limit, String name) {
        String uri;
        PagedResponseDto<PersonDto> pagedResponse;

        try {
            if (StringUtils.hasText(name)) {
                uri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                        .path("/people")
                        .queryParam("search", name)
                        .build().toUriString();
                ListResponseDto<PersonDto> listResponse = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ListResponseDto<PersonDto>>() {}
                ).getBody();

                pagedResponse = new PagedResponseDto<>();
                pagedResponse.setMessage(listResponse.getMessage());
                if (listResponse.getResult() != null) {
                    pagedResponse.setResults(listResponse.getResult());
                    pagedResponse.setTotalRecords(listResponse.getResult().size());
                }
                pagedResponse.setTotalPages(1);
            } else {
                uri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                        .path("/people")
                        .queryParam("page", page)
                        .queryParam("limit", limit)
                        .build().toUriString();
                pagedResponse = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<PagedResponseDto<PersonDto>>() {}
                ).getBody();
            }
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new TooManyRequestsException("Too Many Requests to SWAPI for people. Please try again later.");
            }
            throw ex;
        }
        return pagedResponse;
    }

    public PagedResponseDto<StarshipDto> findStarships(int page, int limit, String name) {
        String uri;
        PagedResponseDto<StarshipDto> pagedResponse;

        try {
            if (StringUtils.hasText(name)) {
                uri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                        .path("/starships")
                        .queryParam("search", name)
                        .build().toUriString();
                ListResponseDto<StarshipDto> listResponse = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ListResponseDto<StarshipDto>>() {}
                ).getBody();

                pagedResponse = new PagedResponseDto<>();
                pagedResponse.setMessage(listResponse.getMessage());
                if (listResponse.getResult() != null) {
                    pagedResponse.setResults(listResponse.getResult());
                    pagedResponse.setTotalRecords(listResponse.getResult().size());
                }
                pagedResponse.setTotalPages(1);
            } else {
                uri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                        .path("/starships")
                        .queryParam("page", page)
                        .queryParam("limit", limit)
                        .build().toUriString();
                pagedResponse = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<PagedResponseDto<StarshipDto>>() {}
                ).getBody();
            }
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new TooManyRequestsException("Too Many Requests to SWAPI for starships. Please try again later.");
            }
            throw ex;
        }
        return pagedResponse;
    }

    public PagedResponseDto<VehicleDto> findVehicles(int page, int limit, String name) {
        String uri;
        PagedResponseDto<VehicleDto> pagedResponse;

        try {
            if (StringUtils.hasText(name)) {
                uri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                        .path("/vehicles")
                        .queryParam("search", name)
                        .build().toUriString();
                ListResponseDto<VehicleDto> listResponse = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ListResponseDto<VehicleDto>>() {}
                ).getBody();

                pagedResponse = new PagedResponseDto<>();
                pagedResponse.setMessage(listResponse.getMessage());
                if (listResponse.getResult() != null) {
                    pagedResponse.setResults(listResponse.getResult());
                    pagedResponse.setTotalRecords(listResponse.getResult().size());
                }
                pagedResponse.setTotalPages(1);
            } else {
                uri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                        .path("/vehicles")
                        .queryParam("page", page)
                        .queryParam("limit", limit)
                        .build().toUriString();
                pagedResponse = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<PagedResponseDto<VehicleDto>>() {}
                ).getBody();
            }
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new TooManyRequestsException("Too Many Requests to SWAPI for vehicles. Please try again later.");
            }
            throw ex;
        }
        return pagedResponse;
    }

    public SingleResponseDto<FilmDto> findFilmById(String uid) {
        final String apiUrl = swapiBaseUrl + "/films/" + uid;

        try {
            return restTemplate.getForObject(apiUrl, SingleResponseDto.class);

        } catch (HttpClientErrorException ex) {

            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new FilmNotFoundException(uid);
            }
            throw ex;
        }
    }


    public SingleResponseDto<PersonDto> findPersonById(String id) {
        String uri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/people/{id}")
                .buildAndExpand(id).toUriString();
        try {
            return restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<SingleResponseDto<PersonDto>>() {}
            ).getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new TooManyRequestsException("Too Many Requests to SWAPI for person ID " + id + ". Please try again later.");
            }
            throw ex;
        }
    }

    public SingleResponseDto<StarshipDto> findStarshipById(String id) {
        String uri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/starships/{id}")
                .buildAndExpand(id).toUriString();
        try {
            return restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<SingleResponseDto<StarshipDto>>() {}
            ).getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new TooManyRequestsException("Too Many Requests to SWAPI for starship ID " + id + ". Please try again later.");
            }
            throw ex;
        }
    }

    public SingleResponseDto<VehicleDto> findVehicleById(String id) {
        String uri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/vehicles/{id}")
                .buildAndExpand(id).toUriString();
        try {
            return restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<SingleResponseDto<VehicleDto>>() {}
            ).getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new TooManyRequestsException("Too Many Requests to SWAPI for vehicle ID " + id + ". Please try again later.");
            }
            throw ex;
        }
    }
}