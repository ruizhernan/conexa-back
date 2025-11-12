package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.StarshipDto;
import conexa.starwarschallenge.dto.StarshipPropertiesDto; // Re-add import
import conexa.starwarschallenge.dto.StarshipRawItemDto;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StarshipControllerTest {

    @Mock
    private SwapiService swapiService;

    @InjectMocks
    private StarshipController starshipController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(starshipController).build();
    }

    @Test
    void getStarships_shouldReturnPagedStarships() throws Exception {
        StarshipRawItemDto starshipRawItemDto = new StarshipRawItemDto();
        starshipRawItemDto.setName("Death Star");
        starshipRawItemDto.setUid("1");
        starshipRawItemDto.setUrl("http://swapi.dev/api/starships/1/");

        PagedResponseDto<StarshipRawItemDto> pagedResponse = new PagedResponseDto<>();
        pagedResponse.setResults(Collections.singletonList(starshipRawItemDto));
        pagedResponse.setTotalRecords(1);

        when(swapiService.findStarships(eq(1), eq(10), isNull()))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/starships?page=1&limit=10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].name").value("Death Star"));
    }

    @Test
    void getStarships_shouldFilterStarshipsByName() throws Exception {
        StarshipRawItemDto starshipRawItemDto = new StarshipRawItemDto();
        starshipRawItemDto.setName("Death Star");
        starshipRawItemDto.setUid("1");
        starshipRawItemDto.setUrl("http://swapi.dev/api/starships/1/");

        PagedResponseDto<StarshipRawItemDto> pagedResponse = new PagedResponseDto<>();
        pagedResponse.setResults(Collections.singletonList(starshipRawItemDto));
        pagedResponse.setTotalRecords(1);

        when(swapiService.findStarships(eq(1), eq(1), eq("Death")))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/starships?name=Death")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].name").value("Death Star"));
    }
}
