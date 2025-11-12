package conexa.starwarschallenge.service;

import conexa.starwarschallenge.dto.ListResponseDto;
import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.PersonDto;
import conexa.starwarschallenge.dto.PersonPropertiesDto;
import conexa.starwarschallenge.dto.SingleResponseDto;
import conexa.starwarschallenge.exception.TooManyRequestsException;
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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

        PersonPropertiesDto personPropertiesDto = new PersonPropertiesDto();
        personPropertiesDto.setName("Luke Skywalker");
        PersonDto personDto = new PersonDto();
        personDto.setUid("1");
        personDto.setProperties(personPropertiesDto);

        PagedResponseDto<PersonDto> mockPagedResponse = new PagedResponseDto<>();
        mockPagedResponse.setTotalRecords(1);
        mockPagedResponse.setTotalPages(1);
        mockPagedResponse.setResults(List.of(personDto));

        String expectedUri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/people")
                .queryParam("page", page)
                .queryParam("limit", limit)
                .build().toUriString();

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockPagedResponse, HttpStatus.OK));

        PagedResponseDto<PersonDto> result = swapiService.findPeople(page, limit, name);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getResults().size());
        assertEquals("Luke Skywalker", result.getResults().get(0).getProperties().getName());

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

        PersonPropertiesDto personPropertiesDto = new PersonPropertiesDto();
        personPropertiesDto.setName("Luke Skywalker");
        PersonDto personDto = new PersonDto();
        personDto.setUid("1");
        personDto.setProperties(personPropertiesDto);

        ListResponseDto<PersonDto> mockListResponse = new ListResponseDto<>();
        mockListResponse.setMessage("ok");
        mockListResponse.setResult(List.of(personDto));

        String expectedUri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/people")
                .queryParam("search", name)
                .build().toUriString();

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockListResponse, HttpStatus.OK));

        PagedResponseDto<PersonDto> result = swapiService.findPeople(page, limit, name);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getResults().size());
        assertEquals("Luke Skywalker", result.getResults().get(0).getProperties().getName());

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
    @DisplayName("Should handle empty paged response for people")
    void findPeople_ShouldHandleEmptyResponse() {
        int page = 1;
        int limit = 10;
        String name = null;

        PagedResponseDto<PersonDto> mockResponse = new PagedResponseDto<>();
        mockResponse.setTotalRecords(0);
        mockResponse.setTotalPages(0);
        mockResponse.setResults(Collections.emptyList());

        String expectedUri = UriComponentsBuilder.fromUriString(swapiBaseUrl)
                .path("/people")
                .queryParam("page", page)
                .queryParam("limit", limit)
                .build().toUriString();

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        PagedResponseDto<PersonDto> result = swapiService.findPeople(page, limit, name);

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
}