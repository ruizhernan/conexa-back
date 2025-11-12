package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.FilmDto;
import conexa.starwarschallenge.dto.FilmRawItemDto;
import conexa.starwarschallenge.dto.PagedResponseDto;
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
@RequestMapping("/api/v1/films")
@Tag(name = "Films", description = "Endpoints for retrieving Star Wars films")
@SecurityRequirement(name = "bearerAuth")
public class FilmController {

    private final SwapiService swapiService;

    public FilmController(SwapiService swapiService) {
        this.swapiService = swapiService;
    }

    @Operation(summary = "Get a paginated list of films",
            description = "Retrieves a list of films. The list can be filtered by title.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT is missing or invalid", content = @Content)
    })
    @GetMapping
    public PagedResponseDto<FilmRawItemDto> getFilms(
            @Parameter(description = "Page number for pagination") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Filter by film's title (case-insensitive)") @RequestParam(required = false) String name) {
        return swapiService.findFilms(page, limit, name);
    }

    @Operation(summary = "Get a single film by ID",
            description = "Retrieves the details of a specific film by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved film"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT is missing or invalid", content = @Content),
            @ApiResponse(responseCode = "404", description = "Film not found with the given ID", content = @Content)
    })
    @GetMapping("/{id}")
    public SingleResponseDto<FilmDto> getFilmById(
            @Parameter(description = "ID of the film to retrieve") @PathVariable String id) {
        return swapiService.findFilmById(id);
    }
}