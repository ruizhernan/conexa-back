package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.VehicleDto;
import conexa.starwarschallenge.service.SwapiService;
import conexa.starwarschallenge.dto.SingleResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/vehicles")
@Tag(name = "Vehicles", description = "Endpoints for retrieving Star Wars vehicles")
@SecurityRequirement(name = "bearerAuth")
public class VehicleController {

    private final SwapiService swapiService;

    public VehicleController(SwapiService swapiService) {
        this.swapiService = swapiService;
    }

    @Operation(summary = "Get a paginated list of vehicles",
            description = "Retrieves a list of vehicles. The list can be filtered by name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT is missing or invalid", content = @Content)
    })
    @GetMapping
    public PagedResponseDto<VehicleDto> getVehicles(
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "1") int limit,
            @Parameter(description = "Filter by vehicle's name (case-insensitive)") @RequestParam(required = false) String name) {
        return swapiService.findVehicles(1, limit, name);
    }

    @Operation(summary = "Get a single vehicle by ID",
            description = "Retrieves the details of a specific vehicle by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved vehicle"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT is missing or invalid", content = @Content),
            @ApiResponse(responseCode = "404", description = "Vehicle not found with the given ID", content = @Content)
    })
    @GetMapping("/{id}")
    public SingleResponseDto<VehicleDto> getVehicleById(
            @Parameter(description = "ID of the vehicle to retrieve") @PathVariable String id) {
        return swapiService.findVehicleById(id);
    }
}
