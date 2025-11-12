package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.VehicleDto;
import conexa.starwarschallenge.dto.VehiclePropertiesDto; // Re-add import
import conexa.starwarschallenge.dto.VehicleRawItemDto;
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
class VehicleControllerTest {

    @Mock
    private SwapiService swapiService;

    @InjectMocks
    private VehicleController vehicleController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();
    }

    @Test
    void getVehicles_shouldReturnPagedVehicles() throws Exception {
        VehicleRawItemDto vehicleRawItemDto = new VehicleRawItemDto();
        vehicleRawItemDto.setName("Sand Crawler");
        vehicleRawItemDto.setUid("4");
        vehicleRawItemDto.setUrl("http://swapi.dev/api/vehicles/4");

        PagedResponseDto<VehicleRawItemDto> pagedResponse = new PagedResponseDto<>();
        pagedResponse.setResults(Collections.singletonList(vehicleRawItemDto));
        pagedResponse.setTotalRecords(1);

        when(swapiService.findVehicles(eq(1), eq(10), isNull()))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/vehicles?page=1&limit=10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].name").value("Sand Crawler"));
    }

    @Test
    void getVehicles_shouldFilterVehiclesByName() throws Exception {
        VehicleRawItemDto vehicleRawItemDto = new VehicleRawItemDto();
        vehicleRawItemDto.setName("Sand Crawler");
        vehicleRawItemDto.setUid("4");
        vehicleRawItemDto.setUrl("http://swapi.dev/api/vehicles/4");

        PagedResponseDto<VehicleRawItemDto> pagedResponse = new PagedResponseDto<>();
        pagedResponse.setResults(Collections.singletonList(vehicleRawItemDto));
        pagedResponse.setTotalRecords(1);

        when(swapiService.findVehicles(eq(1), eq(1), eq("Sand")))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/vehicles?name=Sand")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].name").value("Sand Crawler"));
    }
}
