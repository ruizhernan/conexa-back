package conexa.starwarschallenge.service;

import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.PersonDto;
import conexa.starwarschallenge.dto.PersonPropertiesDto;
import conexa.starwarschallenge.dto.PersonRawItemDto;
import conexa.starwarschallenge.dto.StarshipDto;
import conexa.starwarschallenge.dto.StarshipPropertiesDto;
import conexa.starwarschallenge.dto.StarshipRawItemDto;
import conexa.starwarschallenge.dto.VehicleDto;
import conexa.starwarschallenge.dto.VehiclePropertiesDto;
import conexa.starwarschallenge.dto.VehicleRawItemDto;
import conexa.starwarschallenge.dto.FilmRawItemDto;
import conexa.starwarschallenge.dto.SearchResponseDto;
import conexa.starwarschallenge.dto.SingleResponseDto;
import conexa.starwarschallenge.exception.PersonNotFoundException;
import conexa.starwarschallenge.exception.StarshipNotFoundException;
import conexa.starwarschallenge.exception.VehicleNotFoundException;
import conexa.starwarschallenge.exception.FilmNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SwapiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SwapiService swapiService;

    private String swapiBaseUrl = "http://swapi.dev/api";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(swapiService, "swapiBaseUrl", swapiBaseUrl);
    }

    @Test
    @DisplayName("Should return a paginated response of people")
    void findPeople_ShouldReturnPagedResponse() {
        int page = 1;
        int limit = 10;
        String name = null;

        PersonRawItemDto personRawItemDto = new PersonRawItemDto();
        personRawItemDto.setName("Luke Skywalker");
        personRawItemDto.setUid("1");
        personRawItemDto.setUrl("http://swapi.dev/api/people/1/");

        PagedResponseDto<PersonRawItemDto> mockPagedResponse = new PagedResponseDto<>();
        mockPagedResponse.setTotalRecords(1);
        mockPagedResponse.setTotalPages(1);
        mockPagedResponse.setResults(List.of(personRawItemDto));

        URI expectedUri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/people")
                .queryParam("page", page)
                .build().toUri();

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockPagedResponse, HttpStatus.OK));

        PagedResponseDto<PersonRawItemDto> result = swapiService.findPeople(page, limit, name);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getResults().size());
        assertEquals("Luke Skywalker", result.getResults().get(0).getName());

        verify(restTemplate, times(1)).exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    @DisplayName("Should filter people by name")
    void findPeople_ShouldFilterByName() {
        int page = 1;
        int limit = 10;
        String name = "Luke";

        PersonRawItemDto personRawItemDto = new PersonRawItemDto();
        personRawItemDto.setName("Luke Skywalker");
        personRawItemDto.setUid("1");
        personRawItemDto.setUrl("http://swapi.dev/api/people/1/");

        SearchResponseDto<PersonRawItemDto> mockSearchResponse = new SearchResponseDto<>();
        mockSearchResponse.setMessage("ok");
        mockSearchResponse.setResults(List.of(personRawItemDto));

        URI expectedUri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/people")
                .queryParam("search", name)
                .build().toUri();

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockSearchResponse, HttpStatus.OK));

        PagedResponseDto<PersonRawItemDto> result = swapiService.findPeople(page, limit, name);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getResults().size());
        assertEquals("Luke Skywalker", result.getResults().get(0).getName());

        verify(restTemplate, times(1)).exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    @DisplayName("Should return a single person by ID")
    void findPersonById_ShouldReturnSinglePerson() {
        String id = "1";
        PersonPropertiesDto personPropertiesDto = new PersonPropertiesDto();
        personPropertiesDto.setName("Luke Skywalker");
        PersonDto personDto = new PersonDto();
        personDto.setUid("1");
        personDto.setProperties(personPropertiesDto);

        SingleResponseDto<PersonDto> mockResponse = new SingleResponseDto<>();
        mockResponse.setResult(personDto);

        String expectedUri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/people/{id}")
                .buildAndExpand(id).toUriString();

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        SingleResponseDto<PersonDto> result = swapiService.findPersonById(id);

        assertNotNull(result);
        assertNotNull(result.getResult());
        assertEquals("Luke Skywalker", result.getResult().getProperties().getName());

        verify(restTemplate, times(1)).exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    @DisplayName("Should throw PersonNotFoundException when person is not found by ID")
    void findPersonById_ShouldThrowNotFound() {
        String id = "999";
        String expectedUri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/people/{id}")
                .buildAndExpand(id).toUriString();

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new PersonNotFoundException("Person with id " + id + " not found."));

        assertThrows(PersonNotFoundException.class, () -> swapiService.findPersonById(id));

        verify(restTemplate, times(1)).exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    @DisplayName("Should handle empty paged response for people")
    void findPeople_ShouldHandleEmptyResponse() {
        int page = 1;
        int limit = 10;
        String name = null;

        PagedResponseDto<PersonRawItemDto> mockResponse = new PagedResponseDto<>();
        mockResponse.setTotalRecords(0);
        mockResponse.setTotalPages(0);
        mockResponse.setResults(Collections.emptyList());

        URI expectedUri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/people")
                .queryParam("page", page)
                .build().toUri();

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        PagedResponseDto<PersonRawItemDto> result = swapiService.findPeople(page, limit, name);

        assertNotNull(result);
        assertEquals(0, result.getTotalRecords());
        assertTrue(result.getResults().isEmpty());

        verify(restTemplate, times(1)).exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    @DisplayName("Should return a paginated response of films")
    void findFilms_ShouldReturnPagedResponse() {
        int page = 1;
        int limit = 10;
        String name = null;

        FilmRawItemDto filmRawItemDto = new FilmRawItemDto();
        filmRawItemDto.setName("A New Hope");
        filmRawItemDto.setUid("1");
        filmRawItemDto.setUrl("http://swapi.dev/api/films/1/");

        PagedResponseDto<FilmRawItemDto> mockPagedResponse = new PagedResponseDto<>();
        mockPagedResponse.setTotalRecords(1);
        mockPagedResponse.setTotalPages(1);
        mockPagedResponse.setResults(List.of(filmRawItemDto));

        URI expectedUri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/films")
                .queryParam("page", page)
                .build().toUri();

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockPagedResponse, HttpStatus.OK));

        PagedResponseDto<FilmRawItemDto> result = swapiService.findFilms(page, limit, name);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getResults().size());
        assertEquals("A New Hope", result.getResults().get(0).getName());

        verify(restTemplate, times(1)).exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    @DisplayName("Should filter films by name")
    void findFilms_ShouldFilterByName() {
        int page = 1;
        int limit = 10;
        String name = "New Hope";

        FilmRawItemDto filmRawItemDto = new FilmRawItemDto();
        filmRawItemDto.setName("A New Hope");
        filmRawItemDto.setUid("1");
        filmRawItemDto.setUrl("http://swapi.dev/api/films/1/");

        SearchResponseDto<FilmRawItemDto> mockSearchResponse = new SearchResponseDto<>();
        mockSearchResponse.setMessage("ok");
        mockSearchResponse.setResults(List.of(filmRawItemDto));

        URI expectedUri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/films")
                .queryParam("search", name)
                .build().toUri();

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockSearchResponse, HttpStatus.OK));

        PagedResponseDto<FilmRawItemDto> result = swapiService.findFilms(page, limit, name);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getResults().size());
        assertEquals("A New Hope", result.getResults().get(0).getName());

        verify(restTemplate, times(1)).exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    @DisplayName("Should throw FilmNotFoundException when film is not found by ID")
    void findFilmById_ShouldThrowNotFound() {
        String id = "999";
        String expectedUri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/films/{id}")
                .buildAndExpand(id).toUriString();

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new FilmNotFoundException("Film with id " + id + " not found."));

        assertThrows(FilmNotFoundException.class, () -> swapiService.findFilmById(id));

        verify(restTemplate, times(1)).exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    @DisplayName("Should filter starships by name")
    void findStarships_ShouldFilterByName() {
        int page = 1;
        int limit = 10;
        String name = "Death";

        StarshipRawItemDto starshipRawItemDto = new StarshipRawItemDto();
        starshipRawItemDto.setName("Death Star");
        starshipRawItemDto.setUid("1");
        starshipRawItemDto.setUrl("http://swapi.dev/api/starships/1/");

        SearchResponseDto<StarshipRawItemDto> mockSearchResponse = new SearchResponseDto<>();
        mockSearchResponse.setMessage("ok");
        mockSearchResponse.setResults(List.of(starshipRawItemDto));

        URI expectedUri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/starships")
                .queryParam("search", name)
                .build().toUri();

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockSearchResponse, HttpStatus.OK));

        PagedResponseDto<StarshipRawItemDto> result = swapiService.findStarships(page, limit, name);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getResults().size());
        assertEquals("Death Star", result.getResults().get(0).getName());

        verify(restTemplate, times(1)).exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    @DisplayName("Should throw StarshipNotFoundException when starship is not found by ID")
    void findStarshipById_ShouldThrowNotFound() {
        String id = "999";
        String expectedUri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/starships/{id}")
                .buildAndExpand(id).toUriString();

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new StarshipNotFoundException("Starship with id " + id + " not found."));

        assertThrows(StarshipNotFoundException.class, () -> swapiService.findStarshipById(id));

        verify(restTemplate, times(1)).exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    @DisplayName("Should filter vehicles by name")
    void findVehicles_ShouldFilterByName() {
        int page = 1;
        int limit = 10;
        String name = "Sand";

        VehicleRawItemDto vehicleRawItemDto = new VehicleRawItemDto();
        vehicleRawItemDto.setName("Sand Crawler");
        vehicleRawItemDto.setUid("1");
        vehicleRawItemDto.setUrl("http://swapi.dev/api/vehicles/1/");

        SearchResponseDto<VehicleRawItemDto> mockSearchResponse = new SearchResponseDto<>();
        mockSearchResponse.setMessage("ok");
        mockSearchResponse.setResults(List.of(vehicleRawItemDto));

        URI expectedUri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/vehicles")
                .queryParam("search", name)
                .build().toUri();

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockSearchResponse, HttpStatus.OK));

        PagedResponseDto<VehicleRawItemDto> result = swapiService.findVehicles(page, limit, name);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getResults().size());
        assertEquals("Sand Crawler", result.getResults().get(0).getName());

        verify(restTemplate, times(1)).exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    @DisplayName("Should throw VehicleNotFoundException when vehicle is not found by ID")
    void findVehicleById_ShouldThrowNotFound() {
        String id = "999";
        String expectedUri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/vehicles/{id}")
                .buildAndExpand(id).toUriString();

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new VehicleNotFoundException("Vehicle with id " + id + " not found."));

        assertThrows(VehicleNotFoundException.class, () -> swapiService.findVehicleById(id));

        verify(restTemplate, times(1)).exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }
}