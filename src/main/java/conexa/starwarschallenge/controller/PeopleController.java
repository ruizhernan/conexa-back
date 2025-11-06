package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.PersonDto;
import conexa.starwarschallenge.service.SwapiService;
import conexa.starwarschallenge.dto.SingleResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

    @GetMapping
    public Mono<PagedResponseDto<PersonDto>> getPeople(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return swapiService.findPeople(page, limit);
    }

    @GetMapping("/{id}")
    public Mono<SingleResponseDto<PersonDto>> getPersonById(@PathVariable String id) {
        return swapiService.findPersonById(id);
    }
}
