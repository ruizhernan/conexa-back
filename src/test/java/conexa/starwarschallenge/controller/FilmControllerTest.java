package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.FilmDto;
import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.SingleResponseDto;
import conexa.starwarschallenge.dto.FilmPropertiesDto; // Re-add import
import conexa.starwarschallenge.dto.FilmRawItemDto;
import conexa.starwarschallenge.service.SwapiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class FilmControllerTest {

    @Mock
    private SwapiService swapiService;

    @InjectMocks
    private FilmController filmController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(filmController).build();
    }



    @Test
    void getFilms_shouldReturnPagedFilms() throws Exception {
        FilmRawItemDto filmRawItemDto = new FilmRawItemDto();
        filmRawItemDto.setName("A New Hope");
        filmRawItemDto.setUid("1");
        filmRawItemDto.setUrl("http://swapi.dev/api/films/1/");

        PagedResponseDto<FilmRawItemDto> pagedResponse = new PagedResponseDto<>();
        pagedResponse.setResults(Collections.singletonList(filmRawItemDto));
        pagedResponse.setTotalRecords(1);

        when(swapiService.findFilms(anyInt(), anyInt(), anyString()))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/films?page=1&limit=10&name=New")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].name").value("A New Hope"));
    }

    @Test
    void getFilmById_shouldReturnSingleFilm() throws Exception {
        FilmPropertiesDto filmPropertiesDto = new FilmPropertiesDto();
        filmPropertiesDto.setTitle("A New Hope");
        FilmDto filmDto = new FilmDto();
        filmDto.setProperties(filmPropertiesDto);
        SingleResponseDto<FilmDto> singleResponse = new SingleResponseDto<>();
        singleResponse.setResult(filmDto);

        when(swapiService.findFilmById(anyString()))
                .thenReturn(singleResponse);

        mockMvc.perform(get("/api/v1/films/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.properties.title").value("A New Hope"));
    }
}
