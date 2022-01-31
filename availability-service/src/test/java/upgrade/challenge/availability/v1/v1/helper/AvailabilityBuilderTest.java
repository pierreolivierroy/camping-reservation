package upgrade.challenge.availability.v1.v1.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upgrade.challenge.availability.domain.CampsiteOccupancy;
import upgrade.challenge.availability.v1.service.CampsiteOccupancyService;
import upgrade.challenge.availability.v1.v1.dto.AvailabilityDto;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityBuilderTest {

    private AvailabilityBuilder testee;

    @Mock
    private CampsiteOccupancyService campsiteOccupancyService;

    private Instant searchStartDate;
    private Instant searchEndDate;

    @BeforeEach
    void setUp() {
        testee = new AvailabilityBuilder(campsiteOccupancyService);

        searchStartDate = Instant.now().truncatedTo(DAYS);
        searchEndDate = searchStartDate.plus(10L, DAYS);
    }

    @Test
    void searchAvailabilities() {
        final List<AvailabilityDto> expected = List.of(
                buildAvailabilityDto(true, searchStartDate, searchStartDate.plus(1L, DAYS)),
                buildAvailabilityDto(true, searchStartDate.plus(1L, DAYS), searchStartDate.plus(2L, DAYS)),
                buildAvailabilityDto(false, searchStartDate.plus(2L, DAYS), searchStartDate.plus(3L, DAYS)),
                buildAvailabilityDto(false, searchStartDate.plus(3L, DAYS), searchStartDate.plus(4L, DAYS)),
                buildAvailabilityDto(true, searchStartDate.plus(4L, DAYS), searchStartDate.plus(5L, DAYS)),
                buildAvailabilityDto(true, searchStartDate.plus(5L, DAYS), searchStartDate.plus(6L, DAYS)),
                buildAvailabilityDto(false, searchStartDate.plus(6L, DAYS), searchStartDate.plus(7L, DAYS)),
                buildAvailabilityDto(false, searchStartDate.plus(7L, DAYS), searchStartDate.plus(8L, DAYS)),
                buildAvailabilityDto(false, searchStartDate.plus(8L, DAYS), searchStartDate.plus(9L, DAYS)),
                buildAvailabilityDto(false, searchStartDate.plus(9L, DAYS), searchStartDate.plus(10L, DAYS))
        );

        when(campsiteOccupancyService.getAllBetweenDates(searchStartDate, searchEndDate))
                .thenReturn(List.of(
                        buildCampsiteOccupancy(searchStartDate.plus(2L, DAYS), searchStartDate.plus(3L, DAYS)),
                        buildCampsiteOccupancy(searchStartDate.plus(6L, DAYS), searchStartDate.plus(9L, DAYS))
                ));

        final List<AvailabilityDto> actual = testee.searchAvailabilities(searchStartDate, searchEndDate);

        assertThat(actual).isEqualTo(expected);

        verify(campsiteOccupancyService).getAllBetweenDates(searchStartDate, searchEndDate);
    }

    @Test
    void searchAvailabilities_withoutOccupancy() {
        final List<AvailabilityDto> expected = List.of(
                buildAvailabilityDto(true, searchStartDate, searchStartDate.plus(1L, DAYS)),
                buildAvailabilityDto(true, searchStartDate.plus(1L, DAYS), searchStartDate.plus(2L, DAYS)),
                buildAvailabilityDto(true, searchStartDate.plus(2L, DAYS), searchStartDate.plus(3L, DAYS)),
                buildAvailabilityDto(true, searchStartDate.plus(3L, DAYS), searchStartDate.plus(4L, DAYS)),
                buildAvailabilityDto(true, searchStartDate.plus(4L, DAYS), searchStartDate.plus(5L, DAYS)),
                buildAvailabilityDto(true, searchStartDate.plus(5L, DAYS), searchStartDate.plus(6L, DAYS)),
                buildAvailabilityDto(true, searchStartDate.plus(6L, DAYS), searchStartDate.plus(7L, DAYS)),
                buildAvailabilityDto(true, searchStartDate.plus(7L, DAYS), searchStartDate.plus(8L, DAYS)),
                buildAvailabilityDto(true, searchStartDate.plus(8L, DAYS), searchStartDate.plus(9L, DAYS)),
                buildAvailabilityDto(true, searchStartDate.plus(9L, DAYS), searchStartDate.plus(10L, DAYS))
        );

        when(campsiteOccupancyService.getAllBetweenDates(searchStartDate, searchEndDate))
                .thenReturn(Collections.emptyList());

        final List<AvailabilityDto> actual = testee.searchAvailabilities(searchStartDate, searchEndDate);

        assertThat(actual).isEqualTo(expected);

        verify(campsiteOccupancyService).getAllBetweenDates(searchStartDate, searchEndDate);
    }

    private AvailabilityDto buildAvailabilityDto(final boolean available,
                                                 final Instant checkinTime,
                                                 final Instant checkoutTime) {
        return AvailabilityDto.builder()
                .available(available)
                .checkinTime(checkinTime)
                .checkoutTime(checkoutTime)
                .build();
    }

    private CampsiteOccupancy buildCampsiteOccupancy(final Instant arrivalDate, final Instant departureDate) {
        return CampsiteOccupancy.builder()
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .build();
    }
}
