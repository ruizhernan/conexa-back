package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.StarshipDto;
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
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/starships")
@Tag(name = "Starships", description = "Endpoints for retrieving Star Wars starships")
@SecurityRequirement(name = "bearerAuth")
public class StarshipController {

    private final SwapiService swapiService;

    public StarshipController(SwapiService swapiService) {
        this.swapiService = swapiService;
    }

    @Operation(summary = "Get a paginated list of starships",
            description = "Retrieves a list of starships. The list can be filtered by name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT is missing or invalid", content = @Content)
    })
    @GetMapping
    public Mono<PagedResponseDto<StarshipDto>> getStarships(
            @Parameter(description = "Page number for pagination") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Filter by starship's name (case-insensitive)") @RequestParam(required = false) String name) {
        return swapiService.findStarships(page, limit, name);
    }

    @Operation(summary = "Get a single starship by ID",
            description = "Retrieves the details of a specific starship by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved starship"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT is missing or invalid", content = @Content),
            @ApiResponse(responseCode = "404", description = "Starship not found with the given ID", content = @Content)
    })
    @GetMapping("/{id}")
    public Mono<SingleResponseDto<StarshipDto>> getStarshipById(
            @Parameter(description = "ID of the starship to retrieve") @PathVariable String id) {
        return swapiService.findStarshipById(id);
    }
}
