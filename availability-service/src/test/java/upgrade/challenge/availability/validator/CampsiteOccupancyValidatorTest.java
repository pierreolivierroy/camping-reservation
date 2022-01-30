package upgrade.challenge.availability.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import upgrade.challenge.availability.domain.CampsiteOccupancy;
import upgrade.challenge.availability.repository.CampsiteOccupancyRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampsiteOccupancyValidatorTest {

    private static final String OBJECT_NAME = "campsiteOccupancy";
    private static final Long RESERVATION_ID = 123456789L;

    private CampsiteOccupancyValidator testee;

    @Mock
    private CampsiteOccupancyRepository campsiteOccupancyRepository;

    private CampsiteOccupancy campsiteOccupancy;

    @BeforeEach
    void setUp() {
        testee = new CampsiteOccupancyValidator(campsiteOccupancyRepository);
        campsiteOccupancy = buildCampsiteOccupancy();
    }

    @Test
    void supports() {
        assertThat(testee.supports(CampsiteOccupancy.class)).isTrue();
        assertThat(testee.supports(Object.class)).isFalse();
    }

    @Test
    void validate() {
        final BindException expectedErrors = new BindException(campsiteOccupancy, OBJECT_NAME);

        when(campsiteOccupancyRepository.findAllByArrivalDateLessThanEqualAndDepartureDateGreaterThanEqual(
                campsiteOccupancy.getArrivalDate(),
                campsiteOccupancy.getDepartureDate()))
                .thenReturn(Collections.emptyList());
        when(campsiteOccupancyRepository.findAllByDepartureDateGreaterThanEqualAndDepartureDateLessThanEqual(
                campsiteOccupancy.getArrivalDate(),
                campsiteOccupancy.getDepartureDate()))
                .thenReturn(Collections.emptyList());

        testee.validate(campsiteOccupancy, expectedErrors);

        assertThat(expectedErrors.getErrorCount()).isEqualTo(0);

        commonVerify();
    }

    @Test
    void validate_withExistingOccupancyInRangeOwnedBySameReservationId() {
        final List<CampsiteOccupancy> existingOccupancies = List.of(
                buildCampsiteOccupancy(RESERVATION_ID, Instant.parse("2022-02-04T00:00:00.000Z"), Instant.parse("2022-02-07T00:00:00.000Z"))
        );

        campsiteOccupancy = buildCampsiteOccupancy(RESERVATION_ID,
                Instant.parse("2022-02-05T00:00:00.000Z"), Instant.parse("2022-02-06T00:00:00.000Z"));

        final BindException expectedErrors = new BindException(campsiteOccupancy, OBJECT_NAME);

        when(campsiteOccupancyRepository.findAllByArrivalDateLessThanEqualAndDepartureDateGreaterThanEqual(
                campsiteOccupancy.getArrivalDate(),
                campsiteOccupancy.getDepartureDate()))
                .thenReturn(existingOccupancies);
        when(campsiteOccupancyRepository.findAllByDepartureDateGreaterThanEqualAndDepartureDateLessThanEqual(
                campsiteOccupancy.getArrivalDate(),
                campsiteOccupancy.getDepartureDate()))
                .thenReturn(Collections.emptyList());

        testee.validate(campsiteOccupancy, expectedErrors);

        assertThat(expectedErrors.getErrorCount()).isEqualTo(0);

        commonVerify();
    }

    @Test
    void validate_withArrivalDateAlreadyOccupiedByAnotherReservation_shouldContainErrors() {
        final List<CampsiteOccupancy> existingOccupancies = List.of(
                buildCampsiteOccupancy(),
                buildCampsiteOccupancy(987654321L, Instant.parse("2022-02-05T00:00:00.000Z"), Instant.parse("2022-02-06T00:00:00.000Z"))
        );

        campsiteOccupancy = buildCampsiteOccupancy(RESERVATION_ID,
                Instant.parse("2022-02-04T00:00:00.000Z"), Instant.parse("2022-02-06T00:00:00.000Z"));

        final BindException expectedErrors = new BindException(campsiteOccupancy, OBJECT_NAME);

        when(campsiteOccupancyRepository.findAllByArrivalDateLessThanEqualAndDepartureDateGreaterThanEqual(
                campsiteOccupancy.getArrivalDate(),
                campsiteOccupancy.getDepartureDate()))
                .thenReturn(existingOccupancies);
        when(campsiteOccupancyRepository.findAllByDepartureDateGreaterThanEqualAndDepartureDateLessThanEqual(
                campsiteOccupancy.getArrivalDate(),
                campsiteOccupancy.getDepartureDate()))
                .thenReturn(Collections.emptyList());

        testee.validate(campsiteOccupancy, expectedErrors);

        assertThat(expectedErrors.getFieldErrors()).extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(tuple("arrivalDate", "The dates specified are not available."));

        commonVerify();
    }

    @Test
    void validate_withDepartureDateAlreadyOccupiedByAnotherReservation_shouldContainErrors() {
        final List<CampsiteOccupancy> existingOccupancies = List.of(
                buildCampsiteOccupancy(),
                buildCampsiteOccupancy(987654321L, Instant.parse("2022-02-05T00:00:00.000Z"), Instant.parse("2022-02-06T00:00:00.000Z"))
        );

        campsiteOccupancy = buildCampsiteOccupancy(RESERVATION_ID,
                Instant.parse("2022-02-04T00:00:00.000Z"), Instant.parse("2022-02-06T00:00:00.000Z"));

        final BindException expectedErrors = new BindException(campsiteOccupancy, OBJECT_NAME);

        when(campsiteOccupancyRepository.findAllByArrivalDateLessThanEqualAndDepartureDateGreaterThanEqual(
                campsiteOccupancy.getArrivalDate(),
                campsiteOccupancy.getDepartureDate()))
                .thenReturn(Collections.emptyList());
        when(campsiteOccupancyRepository.findAllByDepartureDateGreaterThanEqualAndDepartureDateLessThanEqual(
                campsiteOccupancy.getArrivalDate(),
                campsiteOccupancy.getDepartureDate()))
                .thenReturn(existingOccupancies);

        testee.validate(campsiteOccupancy, expectedErrors);

        assertThat(expectedErrors.getFieldErrors()).extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(tuple("departureDate", "The dates specified are not available."));

        commonVerify();
    }

    @Test
    void validate_withArrivalAndDepartureDatesAlreadyOccupiedByAnotherReservation_shouldContainErrors() {
        final List<CampsiteOccupancy> existingOccupancies = List.of(
                buildCampsiteOccupancy(),
                buildCampsiteOccupancy(987654321L, Instant.parse("2022-02-05T00:00:00.000Z"), Instant.parse("2022-02-06T00:00:00.000Z"))
        );

        campsiteOccupancy = buildCampsiteOccupancy(RESERVATION_ID,
                Instant.parse("2022-02-04T00:00:00.000Z"), Instant.parse("2022-02-06T00:00:00.000Z"));

        final BindException expectedErrors = new BindException(campsiteOccupancy, OBJECT_NAME);

        when(campsiteOccupancyRepository.findAllByArrivalDateLessThanEqualAndDepartureDateGreaterThanEqual(
                campsiteOccupancy.getArrivalDate(),
                campsiteOccupancy.getDepartureDate()))
                .thenReturn(existingOccupancies);
        when(campsiteOccupancyRepository.findAllByDepartureDateGreaterThanEqualAndDepartureDateLessThanEqual(
                campsiteOccupancy.getArrivalDate(),
                campsiteOccupancy.getDepartureDate()))
                .thenReturn(existingOccupancies);

        testee.validate(campsiteOccupancy, expectedErrors);

        assertThat(expectedErrors.getFieldErrors()).extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(
                        tuple("arrivalDate", "The dates specified are not available."),
                        tuple("departureDate", "The dates specified are not available."));

        commonVerify();
    }

    private CampsiteOccupancy buildCampsiteOccupancy() {
        return buildCampsiteOccupancy(RESERVATION_ID);
    }

    private CampsiteOccupancy buildCampsiteOccupancy(final Long reservationId) {
        return buildCampsiteOccupancy(reservationId,
                Instant.parse("2022-02-04T00:00:00.000Z"),
                Instant.parse("2022-02-05T00:00:00.000Z"));
    }

    private CampsiteOccupancy buildCampsiteOccupancy(final Long reservationId,
                                                     final Instant arrivalDate,
                                                     final Instant departureDate) {
        return CampsiteOccupancy.builder()
                .reservationId(reservationId)
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .build();
    }

    private void commonVerify() {
        verify(campsiteOccupancyRepository).findAllByArrivalDateLessThanEqualAndDepartureDateGreaterThanEqual(
                campsiteOccupancy.getArrivalDate(),
                campsiteOccupancy.getDepartureDate());
        verify(campsiteOccupancyRepository).findAllByDepartureDateGreaterThanEqualAndDepartureDateLessThanEqual(
                campsiteOccupancy.getArrivalDate(),
                campsiteOccupancy.getDepartureDate());
    }
}
