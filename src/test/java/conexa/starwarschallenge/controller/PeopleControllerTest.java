package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.PersonDto;
import conexa.starwarschallenge.dto.PersonPropertiesDto; // Re-add import
import conexa.starwarschallenge.dto.PersonRawItemDto;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PeopleControllerTest {

    @Mock
    private SwapiService swapiService;

    @InjectMocks
    private PeopleController peopleController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(peopleController).build();
    }

    @Test
    void getPeople_shouldReturnPagedPeople() throws Exception {
        PersonRawItemDto personRawItemDto = new PersonRawItemDto();
        personRawItemDto.setName("Luke Skywalker");
        personRawItemDto.setUid("1");
        personRawItemDto.setUrl("http://swapi.dev/api/people/1/");

        PagedResponseDto<PersonRawItemDto> pagedResponse = new PagedResponseDto<>();
        pagedResponse.setResults(Collections.singletonList(personRawItemDto));
        pagedResponse.setTotalRecords(1);

        when(swapiService.findPeople(eq(1), eq(10), isNull()))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/people?page=1&limit=10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].name").value("Luke Skywalker"));
    }

    @Test
    void getPeople_shouldFilterPeopleByName() throws Exception {
        PersonRawItemDto personRawItemDto = new PersonRawItemDto();
        personRawItemDto.setName("Luke Skywalker");
        personRawItemDto.setUid("1");
        personRawItemDto.setUrl("http://swapi.dev/api/people/1/");

        PagedResponseDto<PersonRawItemDto> pagedResponse = new PagedResponseDto<>();
        pagedResponse.setResults(Collections.singletonList(personRawItemDto));
        pagedResponse.setTotalRecords(1);

        when(swapiService.findPeople(eq(1), eq(1), eq("Luke")))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/people?name=Luke")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].name").value("Luke Skywalker"));
    }
}
