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

    /**
     * Retrieves a paginated list of vehicles, with optional filtering by name.
     *
     * @param page the page number for pagination (default is 1).
     * @param limit the number of items per page (default is 10).
     * @param name an optional name to filter the list of vehicles.
     * @return a Mono containing a paged response with vehicle data.
     */
    @GetMapping
    public Mono<PagedResponseDto<VehicleDto>> getVehicles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String name) {
        return swapiService.findVehicles(page, limit, name);
    }

    /**
     * Retrieves a single vehicle by its unique ID.
     *
     * @param id the unique identifier of the vehicle.
     * @return a Mono containing a single response with the vehicle's data.
     */
    @GetMapping("/{id}")
    public Mono<SingleResponseDto<VehicleDto>> getVehicleById(@PathVariable String id) {
        return swapiService.findVehicleById(id);
    }
}
