package conexa.starwarschallenge.service;

import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.PersonDto;
import conexa.starwarschallenge.dto.PersonPropertiesDto;
import conexa.starwarschallenge.dto.SingleResponseDto;
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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SwapiServiceTest {

    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private SwapiService swapiService;

    @BeforeEach
    void setUp() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
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

        SingleResponseDto<PersonDto> mockSingleResponse = new SingleResponseDto<>();
        mockSingleResponse.setResult(personDto);

        // Mocking para manejar la primera llamada (paged list) y la segunda (enrichment)
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(mockPagedResponse))
                .thenReturn(Mono.just(mockSingleResponse));

        // Mockear la llamada específica a findPersonById (necesaria para el enrichment)
        when(requestHeadersUriSpec.uri(eq("/people/{id}"), eq("1")))
                .thenReturn(requestHeadersSpec);


        PagedResponseDto<PersonDto> result = swapiService.findPeople(page, limit, name).block();

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getResults().size());
        assertEquals("Luke Skywalker", result.getResults().get(0).getProperties().getName());

        // CORRECCIÓN CLAVE: El servicio llama a webClient.get() dos veces:
        // 1) Para la paginación. 2) Dentro de findPersonById para el enrichment.
        verify(webClient, times(2)).get();

        verify(requestHeadersUriSpec, times(1)).uri(any(Function.class));
        verify(requestHeadersSpec, times(2)).retrieve();
        verify(responseSpec, times(2)).bodyToMono(any(ParameterizedTypeReference.class));
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

        PagedResponseDto<PersonDto> mockPagedResponse = new PagedResponseDto<>();
        mockPagedResponse.setTotalRecords(1);
        mockPagedResponse.setTotalPages(1);
        mockPagedResponse.setResults(List.of(personDto));

        SingleResponseDto<PersonDto> mockSingleResponse = new SingleResponseDto<>();
        mockSingleResponse.setResult(personDto);

        // Mocking para la lista paginada y el enrichment
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(mockPagedResponse))
                .thenReturn(Mono.just(mockSingleResponse));

        when(requestHeadersUriSpec.uri(eq("/people/{id}"), eq("1")))
                .thenReturn(requestHeadersSpec);

        PagedResponseDto<PersonDto> result = swapiService.findPeople(page, limit, name).block();

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getResults().size());
        assertEquals("Luke Skywalker", result.getResults().get(0).getProperties().getName());

        // CORRECCIÓN CLAVE: El servicio llama a webClient.get() dos veces.
        verify(webClient, times(2)).get();

        // Verificar que el método uri fue llamado con el filtro 'search'
        verify(requestHeadersUriSpec, times(1)).uri(argThat((Function<UriBuilder, URI> uriFunction) -> {
            URI uri = uriFunction.apply(UriComponentsBuilder.fromPath("/"));
            return "/people".equals(uri.getPath()) &&
                    uri.getQuery().contains("page=1") &&
                    uri.getQuery().contains("limit=10") &&
                    uri.getQuery().contains("search=Luke");
        }));

        verify(requestHeadersSpec, times(2)).retrieve();
        verify(responseSpec, times(2)).bodyToMono(any(ParameterizedTypeReference.class));
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

        when(requestHeadersUriSpec.uri(eq("/people/{id}"), eq(id)))
                .thenReturn(requestHeadersSpec);

        // Simulación corregida de bodyToMono
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(mockResponse));

        SingleResponseDto<PersonDto> result = swapiService.findPersonById(id).block();

        assertNotNull(result);
        assertNotNull(result.getResult());
        assertEquals("Luke Skywalker", result.getResult().getProperties().getName());

        // Aquí solo se espera 1 llamada a get()
        verify(webClient, times(1)).get();
        verify(requestHeadersUriSpec, times(1)).uri(eq("/people/{id}"), eq(id));
        verify(requestHeadersSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToMono(any(ParameterizedTypeReference.class));
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

        // Simulación corregida de bodyToMono
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(mockResponse));

        PagedResponseDto<PersonDto> result = swapiService.findPeople(page, limit, name).block();

        assertNotNull(result);
        assertEquals(0, result.getTotalRecords());
        assertTrue(result.getResults().isEmpty());

        // Aquí solo se espera 1 llamada a get(), ya que la lista está vacía y no se enriquece.
        verify(webClient, times(1)).get();
        verify(requestHeadersUriSpec, times(1)).uri(any(Function.class));
        verify(requestHeadersSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToMono(any(ParameterizedTypeReference.class));
        verify(requestHeadersUriSpec, never()).uri(eq("/people/{id}"), anyString());
    }
}