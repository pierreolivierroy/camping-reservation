package upgrade.challenge.availability.v1.v1.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upgrade.challenge.availability.v1.v1.dto.AvailabilityDto;
import upgrade.challenge.availability.v1.v1.helper.AvailabilityBuilder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
public class AvailabilityAdapter {

    private final AvailabilityBuilder availabilityBuilder;

    @Autowired
    public AvailabilityAdapter(AvailabilityBuilder availabilityBuilder) {
        this.availabilityBuilder = availabilityBuilder;
    }

    public List<AvailabilityDto> searchAvailabilities(final Instant searchStartDate, final Instant searchEndDate) {
        return availabilityBuilder.searchAvailabilities(getSearchStartDate(searchStartDate), getSearchEndDate(searchEndDate));
    }

    private Instant getSearchStartDate(final Instant searchStartDate) {
        return Optional.ofNullable(searchStartDate)
                .orElse(Instant.now());
    }

    private Instant getSearchEndDate(final Instant searchEndDate) {
        return Optional.ofNullable(searchEndDate)
                .orElse(Instant.now().plus(30L, ChronoUnit.DAYS));
    }
}
