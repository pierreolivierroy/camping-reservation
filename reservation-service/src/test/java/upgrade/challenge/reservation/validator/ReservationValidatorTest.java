package upgrade.challenge.reservation.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import upgrade.challenge.reservation.domain.Reservation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ExtendWith(MockitoExtension.class)
class ReservationValidatorTest {

    private static final String OBJECT_NAME = "reservation";

    private ReservationValidator testee;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        testee = new ReservationValidator();
        reservation = buildReservation();
    }

    @Test
    void supports() {
        assertThat(testee.supports(Reservation.class)).isTrue();
        assertThat(testee.supports(Object.class)).isFalse();
    }

    @Test
    void validate() {
        final BindException expectedErrors = new BindException(reservation, OBJECT_NAME);

        testee.validate(reservation, expectedErrors);

        assertThat(expectedErrors.getErrorCount()).isEqualTo(0);
    }

    @Test
    void validate_withArrivalDateNotInOneDay_shouldContainErrors() {
        reservation.setArrivalDate(Instant.now());

        final BindException expectedErrors = new BindException(reservation, OBJECT_NAME);

        testee.validate(reservation, expectedErrors);

        assertThat(expectedErrors.getFieldErrors()).extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(tuple("arrivalDate", "The arrival date must be in minimum 1 day or more."));
    }

    @Test
    void validate_withArrivalDateFurtherThanAMonth_shouldContainErrors() {
        reservation.setArrivalDate(Instant.now().plus(32L, ChronoUnit.DAYS));
        reservation.setDepartureDate(Instant.now().plus(33L, ChronoUnit.DAYS));

        final BindException expectedErrors = new BindException(reservation, OBJECT_NAME);

        testee.validate(reservation, expectedErrors);

        assertThat(expectedErrors.getFieldErrors()).extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(tuple("arrivalDate", "The arrival date can be up to 1 month in advance."));
    }

    @Test
    void validate_withDepartureDateSameDayAsArrivalDate_shouldContainErrors() {
        reservation.setArrivalDate(Instant.now().plus(2L, ChronoUnit.DAYS));
        reservation.setDepartureDate(Instant.now().plus(2L, ChronoUnit.DAYS));

        final BindException expectedErrors = new BindException(reservation, OBJECT_NAME);

        testee.validate(reservation, expectedErrors);

        assertThat(expectedErrors.getFieldErrors()).extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(tuple("departureDate", "The departure date must be at least the next day after the arrival date."));
    }

    @Test
    void validate_withReservationDurationExceeding3Days_shouldContainErrors() {
        reservation.setArrivalDate(Instant.now().plus(2L, ChronoUnit.DAYS));
        reservation.setDepartureDate(Instant.now().plus(6L, ChronoUnit.DAYS));

        final BindException expectedErrors = new BindException(reservation, OBJECT_NAME);

        testee.validate(reservation, expectedErrors);

        assertThat(expectedErrors.getFieldErrors()).extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(tuple("departureDate", "The departure date is too far, duration must not exceed 3 days."));
    }

    @Test
    void validate_withArrivalDateOnSameDayAndDurationExceeding3Days_shouldContainErrors() {
        reservation.setArrivalDate(Instant.now());
        reservation.setDepartureDate(Instant.now().plus(4L, ChronoUnit.DAYS));

        final BindException expectedErrors = new BindException(reservation, OBJECT_NAME);

        testee.validate(reservation, expectedErrors);

        assertThat(expectedErrors.getFieldErrors()).extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(
                        tuple("arrivalDate", "The arrival date must be in minimum 1 day or more."),
                        tuple("departureDate", "The departure date is too far, duration must not exceed 3 days."));
    }

    @Test
    void validate_withArrivalDateFurtherThanOneMonthAndDepartureDateOnSameDay_shouldContainErrors() {
        final Instant arrivalDate = Instant.now().plus(32L, ChronoUnit.DAYS);
        reservation.setArrivalDate(arrivalDate);
        reservation.setDepartureDate(arrivalDate);

        final BindException expectedErrors = new BindException(reservation, OBJECT_NAME);

        testee.validate(reservation, expectedErrors);

        assertThat(expectedErrors.getFieldErrors()).extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(
                        tuple("arrivalDate", "The arrival date can be up to 1 month in advance."),
                        tuple("departureDate", "The departure date must be at least the next day after the arrival date."));
    }

    @Test
    void validate_withArrivalDateFurtherThanOneMonthAndReservationDurationMoreThan3Days_shouldContainErrors() {
        reservation.setArrivalDate(Instant.now().plus(32L, ChronoUnit.DAYS));
        reservation.setDepartureDate(Instant.now().plus(36L, ChronoUnit.DAYS));

        final BindException expectedErrors = new BindException(reservation, OBJECT_NAME);

        testee.validate(reservation, expectedErrors);

        assertThat(expectedErrors.getFieldErrors()).extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(
                        tuple("arrivalDate", "The arrival date can be up to 1 month in advance."),
                        tuple("departureDate", "The departure date is too far, duration must not exceed 3 days."));
    }

    @Test
    void validate_withArrivalDateInLessThanOneDayAndDepartureDateOnSameDay_shouldContainErrors() {
        final Instant arrivalDate = Instant.now();
        reservation.setArrivalDate(arrivalDate);
        reservation.setDepartureDate(arrivalDate);

        final BindException expectedErrors = new BindException(reservation, OBJECT_NAME);

        testee.validate(reservation, expectedErrors);

        assertThat(expectedErrors.getFieldErrors()).extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(
                        tuple("arrivalDate", "The arrival date must be in minimum 1 day or more."),
                        tuple("departureDate", "The departure date must be at least the next day after the arrival date."));
    }

    @Test
    void validate_withDepartureDateFurtherThanArrivalDate_shouldContainErrors() {
        reservation.setArrivalDate(Instant.now().plus(2L, ChronoUnit.DAYS));
        reservation.setDepartureDate(Instant.now().minus(1L, ChronoUnit.DAYS));

        final BindException expectedErrors = new BindException(reservation, OBJECT_NAME);

        testee.validate(reservation, expectedErrors);

        assertThat(expectedErrors.getFieldErrors()).extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(tuple("departureDate", "The departure date must be at least the next day after the arrival date."));
    }

    private Reservation buildReservation() {
        final Instant now = Instant.now();

        return Reservation.builder()
                .arrivalDate(now.plus(25L, ChronoUnit.HOURS))
                .departureDate(now.plus(49L, ChronoUnit.HOURS))
                .build();
    }
}
