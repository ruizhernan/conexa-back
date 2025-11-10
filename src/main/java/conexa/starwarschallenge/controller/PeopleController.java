package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.PersonDto;
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
@RequestMapping("/api/v1/people")
@Tag(name = "People", description = "Endpoints for retrieving Star Wars characters (people)")
@SecurityRequirement(name = "bearerAuth")
public class PeopleController {

    private final SwapiService swapiService;

    public PeopleController(SwapiService swapiService) {
        this.swapiService = swapiService;
    }

    @Operation(summary = "Get a paginated list of people",
            description = "Retrieves a list of people. The list can be filtered by name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT is missing or invalid", content = @Content)
    })
    @GetMapping
    public Mono<PagedResponseDto<PersonDto>> getPeople(
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "1") int limit,
            @Parameter(description = "Filter by person's name (case-insensitive)") @RequestParam(required = false) String name) {
        return swapiService.findPeople(1, limit, name);
    }

    @Operation(summary = "Get a single person by ID",
            description = "Retrieves the details of a specific person by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved person"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT is missing or invalid", content = @Content),
            @ApiResponse(responseCode = "404", description = "Person not found with the given ID", content = @Content)
    })
    @GetMapping("/{id}")
    public Mono<SingleResponseDto<PersonDto>> getPersonById(
            @Parameter(description = "ID of the person to retrieve") @PathVariable String id) {
        return swapiService.findPersonById(id);
    }
}
