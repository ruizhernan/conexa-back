package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.StarshipDto;
import conexa.starwarschallenge.service.SwapiService;
import conexa.starwarschallenge.dto.SingleResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

    @GetMapping
    public Mono<PagedResponseDto<StarshipDto>> getStarships(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return swapiService.findStarships(page, limit);
    }

    @GetMapping("/{id}")
    public Mono<SingleResponseDto<StarshipDto>> getStarshipById(@PathVariable String id) {
        return swapiService.findStarshipById(id);
    }
}
