package upgrade.challenge.reservation.v1.v1.web.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import upgrade.challenge.reservation.v1.v1.dto.ReservationDto;
import upgrade.challenge.reservation.v1.v1.dto.SuccessfulReservationDto;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@ExtendWith(MockitoExtension.class)
class ReservationAdapterTest {

    private ReservationAdapter testee;

    private ReservationDto reservationDto;

    @BeforeEach
    void setUp() {
        testee = new ReservationAdapter();
        reservationDto = buildReservationDto();
    }

    @Test
    void reserveCampsite() {
        final SuccessfulReservationDto expected = buildSuccessfulReservationDto(reservationDto.getArrivalDate(),
                reservationDto.getDepartureDate());

        final SuccessfulReservationDto actual = testee.reserveCampsite(reservationDto);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void reserveCampsite_withMissingReservationDto_shouldThrowException() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> testee.reserveCampsite(null))
                .withMessage("The reservationDto is mandatory.");
    }

    private static ReservationDto buildReservationDto() {
        final Instant now = Instant.now();

        return ReservationDto.builder()
                .email("email")
                .firstName("first-name")
                .lastName("last-name")
                .arrivalDate(now)
                .departureDate(now.plus(1L, ChronoUnit.DAYS))
                .build();
    }

    private SuccessfulReservationDto buildSuccessfulReservationDto(final Instant arrivalDate,
                                                                   final Instant departureDate) {
        return SuccessfulReservationDto.builder()
                .reservationId("temporary-id")
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .build();
    }
}
