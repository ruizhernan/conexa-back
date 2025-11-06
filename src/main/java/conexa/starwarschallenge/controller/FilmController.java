package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.FilmDto;
import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.service.SwapiService;
import conexa.starwarschallenge.dto.SingleResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/films")
public class FilmController {

    private final SwapiService swapiService;

    public FilmController(SwapiService swapiService) {
        this.swapiService = swapiService;
    }

    @GetMapping
    public Mono<PagedResponseDto<FilmDto>> getFilms(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return swapiService.findFilms(page, limit);
    }

    @GetMapping("/{id}")
    public Mono<SingleResponseDto<FilmDto>> getFilmById(@PathVariable String id) {
        return swapiService.findFilmById(id);
    }
}
