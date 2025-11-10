package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.FilmDto;
import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.SingleResponseDto;
import conexa.starwarschallenge.dto.FilmPropertiesDto;
import conexa.starwarschallenge.service.SwapiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmControllerTest {

    @Mock
    private SwapiService swapiService;

    @InjectMocks
    private FilmController filmController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(filmController).build();
    }

    @Test
    void getFilms_shouldReturnPagedFilms() {
        FilmPropertiesDto filmPropertiesDto = new FilmPropertiesDto();
        filmPropertiesDto.setTitle("A New Hope");
        FilmDto filmDto = new FilmDto();
        filmDto.setProperties(filmPropertiesDto);
        PagedResponseDto<FilmDto> pagedResponse = new PagedResponseDto<>();
        pagedResponse.setResults(Collections.singletonList(filmDto));
        pagedResponse.setTotalRecords(1);

        when(swapiService.findFilms(anyInt(), anyInt(), anyString()))
                .thenReturn(Mono.just(pagedResponse));

        webTestClient.get().uri("/api/v1/films?page=1&limit=10&name=New")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PagedResponseDto<FilmDto>>() {})
                .consumeWith(response -> {
                    PagedResponseDto<FilmDto> responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.getResults().get(0).getProperties().getTitle().equals("A New Hope");
                });
    }

    @Test
    void getFilmById_shouldReturnSingleFilm() {
        FilmPropertiesDto filmPropertiesDto = new FilmPropertiesDto();
        filmPropertiesDto.setTitle("A New Hope");
        FilmDto filmDto = new FilmDto();
        filmDto.setProperties(filmPropertiesDto);
        SingleResponseDto<FilmDto> singleResponse = new SingleResponseDto<>();
        singleResponse.setResult(filmDto);

        when(swapiService.findFilmById(anyString()))
                .thenReturn(Mono.just(singleResponse));

        webTestClient.get().uri("/api/v1/films/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<SingleResponseDto<FilmDto>>() {})
                .consumeWith(response -> {
                    SingleResponseDto<FilmDto> responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.getResult().getProperties().getTitle().equals("A New Hope");
                });
    }
}
