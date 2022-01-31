package upgrade.challenge.availability.v1.v1.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upgrade.challenge.availability.v1.v1.dto.AvailabilityDto;
import upgrade.challenge.availability.v1.v1.helper.AvailabilityBuilder;

import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityAdapterTest {

    private AvailabilityAdapter testee;

    @Mock
    private AvailabilityBuilder availabilityBuilder;

    private Instant searchStartDate;
    private Instant searchEndDate;

    @BeforeEach
    void setUp() {
        testee = new AvailabilityAdapter(availabilityBuilder);

        searchStartDate = Instant.now();
        searchEndDate = searchStartDate.plus(1L, DAYS);
    }

    @Test
    void searchAvailabilities() {
        final List<AvailabilityDto> expected = List.of(buildAvailabilityDto(), buildAvailabilityDto());

        when(availabilityBuilder.searchAvailabilities(searchStartDate, searchEndDate)).thenReturn(expected);

        final List<AvailabilityDto> actual = testee.searchAvailabilities(searchStartDate, searchEndDate);

        assertThat(actual).isEqualTo(expected);

        verify(availabilityBuilder).searchAvailabilities(searchStartDate, searchEndDate);
    }

    @Test
    void searchAvailabilities_withMissingSearchDates() {
        final List<AvailabilityDto> expected = List.of(buildAvailabilityDto(), buildAvailabilityDto());
        final ArgumentCaptor<Instant> startDateArgumentCaptor = ArgumentCaptor.forClass(Instant.class);
        final ArgumentCaptor<Instant> endDateArgumentCaptor = ArgumentCaptor.forClass(Instant.class);

        when(availabilityBuilder.searchAvailabilities(any(Instant.class), any(Instant.class))).thenReturn(expected);

        final List<AvailabilityDto> actual = testee.searchAvailabilities(null, null);

        assertThat(actual).isEqualTo(expected);

        verify(availabilityBuilder).searchAvailabilities(startDateArgumentCaptor.capture(), endDateArgumentCaptor.capture());

        final Instant startDate = startDateArgumentCaptor.getValue();
        final Instant endDate = endDateArgumentCaptor.getValue();

        assertThat(DAYS.between(startDate, endDate)).isEqualTo(30);
    }

    private AvailabilityDto buildAvailabilityDto() {
        return AvailabilityDto.builder()
                .available(true)
                .checkinTime(Instant.now())
                .checkoutTime(Instant.now())
                .build();
    }
}
