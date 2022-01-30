package upgrade.challenge.availability.v1.v1.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import upgrade.challenge.availability.v1.v1.adapter.AvailabilityAdapter;
import upgrade.challenge.availability.v1.v1.dto.AvailabilityDto;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/availability/v1.1")
public class AvailabilityController {

    private final AvailabilityAdapter availabilityAdapter;

    @Autowired
    public AvailabilityController(AvailabilityAdapter availabilityAdapter) {
        this.availabilityAdapter = availabilityAdapter;
    }

    @GetMapping
    public List<AvailabilityDto> searchAvailabilities(final @RequestParam(value = "searchStartDate", required = false) Instant searchStartDate,
                                                      final @RequestParam(value = "searchEndDate", required = false) Instant searchEndDate) {
        return availabilityAdapter.searchAvailabilities(searchStartDate, searchEndDate);
    }
}
