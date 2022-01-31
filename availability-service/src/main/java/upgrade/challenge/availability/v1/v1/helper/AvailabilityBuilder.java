package upgrade.challenge.availability.v1.v1.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upgrade.challenge.availability.domain.CampsiteOccupancy;
import upgrade.challenge.availability.v1.service.CampsiteOccupancyService;
import upgrade.challenge.availability.v1.v1.dto.AvailabilityDto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
public class AvailabilityBuilder {

    private final CampsiteOccupancyService campsiteOccupancyService;

    @Autowired
    public AvailabilityBuilder(CampsiteOccupancyService campsiteOccupancyService) {
        this.campsiteOccupancyService = campsiteOccupancyService;
    }

    public List<AvailabilityDto> searchAvailabilities(final Instant searchArrivalDate, final Instant searchEndDate) {
        final List<CampsiteOccupancy> campsiteOccupancies = campsiteOccupancyService.getAllBetweenDates(searchArrivalDate, searchEndDate);

        return searchAvailabilities(searchArrivalDate.truncatedTo(DAYS), searchEndDate, campsiteOccupancies);
    }

    private List<AvailabilityDto> searchAvailabilities(Instant startDate, final Instant searchEndDate,
                                                       final List<CampsiteOccupancy> campsiteOccupancies) {
        final List<AvailabilityDto> availabilities = new ArrayList<>();

        while (startDate.isBefore(searchEndDate)) {
            Instant endOfDay = startDate.plus(1L, DAYS);

            final boolean available = validateAvailability(campsiteOccupancies, startDate);
            final AvailabilityDto availabilityDto = buildAvailabilityDto(available, startDate, endOfDay);
            availabilities.add(availabilityDto);

            startDate = endOfDay;
        }

        return availabilities;
    }

    private boolean validateAvailability(final List<CampsiteOccupancy> occupancies, final Instant dayToValidate) {
        return occupancies.stream()
                .noneMatch(occupancy -> dayToValidate.compareTo(occupancy.getArrivalDate().truncatedTo(DAYS)) >= 0
                        && dayToValidate.compareTo(occupancy.getDepartureDate().plus(1L, DAYS).truncatedTo(DAYS)) < 0);
    }

    private AvailabilityDto buildAvailabilityDto(final boolean available, final Instant checkinTime, final Instant checkoutTime) {
        return AvailabilityDto.builder()
                .available(available)
                .checkinTime(checkinTime)
                .checkoutTime(checkoutTime)
                .build();
    }
}
