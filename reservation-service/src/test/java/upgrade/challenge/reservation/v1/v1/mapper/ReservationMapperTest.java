package upgrade.challenge.reservation.v1.v1.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import upgrade.challenge.reservation.domain.Reservation;
import upgrade.challenge.reservation.domain.ReservationStatus;
import upgrade.challenge.reservation.v1.v1.dto.ReservationDto;
import upgrade.challenge.reservation.v1.v1.dto.ReservationResponseDto;
import upgrade.challenge.reservation.v1.v1.dto.UpdateReservationDatesDto;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationMapperTest {

    private static final String FIRST_NAME = "first-name";
    private static final String GUEST_EMAIL = "test@email.com";
    private static final String LAST_NAME = "last-name";
    private static final Long RESERVATION_ID = 123456789L;

    private ReservationMapper testee;

    private Instant arrivalDate;

    @BeforeEach
    void setUp() {
        testee = new ReservationMapper();

        arrivalDate = Instant.now();
    }

    @Test
    void mapToDto() {
        final ReservationResponseDto expected = buildReservationResponseDto();

        final ReservationResponseDto actual = testee.mapToDto(buildReservation());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void mapToEntity() {
        final Reservation expected = buildReservation()
                .setId(null)
                .setCreatedDate(null);

        final Reservation actual = testee.mapToEntity(buildReservationDto());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void mapUpgradeDatesDtoToEntity() {
        final Reservation expected = buildBasicReservation();

        final Reservation actual = testee.mapUpgradeDatesDtoToEntity(RESERVATION_ID, buildUpdateReservationDatesDto());

        assertThat(actual).isEqualTo(expected);
    }

    private Reservation buildReservation() {
        return buildBasicReservation()
                .setStatus(ReservationStatus.RESERVATION_PENDING)
                .setFirstName(FIRST_NAME)
                .setLastName(LAST_NAME)
                .setGuestEmail(GUEST_EMAIL)
                .setCreatedDate(arrivalDate);
    }

    private Reservation buildBasicReservation() {
        return Reservation.builder()
                .id(RESERVATION_ID)
                .arrivalDate(arrivalDate)
                .departureDate(arrivalDate.plus(1L, ChronoUnit.DAYS))
                .build();
    }

    private ReservationDto buildReservationDto() {
        return ReservationDto.builder()
                .guestEmail(GUEST_EMAIL)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .arrivalDate(arrivalDate)
                .departureDate(arrivalDate.plus(1L, ChronoUnit.DAYS))
                .build();
    }

    private ReservationResponseDto buildReservationResponseDto() {
        return ReservationResponseDto.builder()
                .reservationId(String.valueOf(RESERVATION_ID))
                .status(ReservationStatus.RESERVATION_PENDING.toString())
                .arrivalDate(arrivalDate)
                .departureDate(arrivalDate.plus(1L, ChronoUnit.DAYS))
                .reservationCreationDate(arrivalDate)
                .build();
    }

    private UpdateReservationDatesDto buildUpdateReservationDatesDto() {
        return UpdateReservationDatesDto.builder()
                .arrivalDate(arrivalDate)
                .departureDate(arrivalDate.plus(1L, ChronoUnit.DAYS))
                .build();
    }
}
