package conexa.starwarschallenge.controller;

import conexa.starwarschallenge.dto.PagedResponseDto;
import conexa.starwarschallenge.dto.VehicleDto;
import conexa.starwarschallenge.service.SwapiService;
import conexa.starwarschallenge.dto.SingleResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final SwapiService swapiService;

    public VehicleController(SwapiService swapiService) {
        this.swapiService = swapiService;
    }

    @GetMapping
    public Mono<PagedResponseDto<VehicleDto>> getVehicles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return swapiService.findVehicles(page, limit);
    }

    @GetMapping("/{id}")
    public Mono<SingleResponseDto<VehicleDto>> getVehicleById(@PathVariable String id) {
        return swapiService.findVehicleById(id);
    }
}
