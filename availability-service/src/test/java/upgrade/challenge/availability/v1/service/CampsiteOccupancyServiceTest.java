package upgrade.challenge.availability.v1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;
import upgrade.challenge.availability.domain.CampsiteOccupancy;
import upgrade.challenge.availability.domain.EventType;
import upgrade.challenge.availability.exception.NotFoundException;
import upgrade.challenge.availability.exception.ValidationException;
import upgrade.challenge.availability.repository.CampsiteOccupancyRepository;
import upgrade.challenge.availability.v1.messaging.eventmessage.CampsiteReservedEvent;
import upgrade.challenge.availability.v1.messaging.publisher.EventMessagePublisher;
import upgrade.challenge.availability.validator.CampsiteOccupancyValidator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampsiteOccupancyServiceTest {

    private static final Long RESERVATION_ID = 123456789L;

    private CampsiteOccupancyService testee;

    @Mock
    private CampsiteOccupancyRepository campsiteOccupancyRepository;

    @Mock
    private CampsiteOccupancyValidator campsiteOccupancyValidator;

    @Mock
    private EventMessagePublisher eventMessagePublisher;

    private CampsiteOccupancy campsiteOccupancy;

    @BeforeEach
    void setUp() {
        testee = new CampsiteOccupancyService(campsiteOccupancyRepository, campsiteOccupancyValidator, eventMessagePublisher);
        campsiteOccupancy = buildCampsiteOccupancy();
    }

    @Test
    void cancel() {
        testee.cancel(RESERVATION_ID);

        verify(campsiteOccupancyRepository).deleteByReservationId(RESERVATION_ID);
    }

    @Test
    void create() {
        final Long insertedId = 12345L;
        final CampsiteOccupancy expected = campsiteOccupancy.setId(insertedId);

        doAnswer(invocation -> ((CampsiteOccupancy) invocation.getArguments()[0]).setId(insertedId))
                .when(campsiteOccupancyRepository).save(campsiteOccupancy);

        final CampsiteOccupancy actual = testee.create(campsiteOccupancy);

        assertThat(actual).isEqualTo(expected);

        verify(campsiteOccupancyValidator).validate(eq(campsiteOccupancy), isA(Errors.class));
        verify(campsiteOccupancyRepository).save(campsiteOccupancy);
        verify(eventMessagePublisher).publishEvent(buildCampsiteReservedEvent(), EventType.CAMPSITE_RESERVED);
    }

    @Test
    void create_withValidationErrors_shouldThrowException() {
        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("arrivalDate", "errorCode", "default message");
            return null;
        }).when(campsiteOccupancyValidator).validate(eq(campsiteOccupancy), isA(Errors.class));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> testee.create(campsiteOccupancy));

        verify(campsiteOccupancyValidator).validate(eq(campsiteOccupancy), isA(Errors.class));
        verify(eventMessagePublisher).publishEvent(buildCampsiteReservedEvent(), EventType.CAMPSITE_RESERVATION_ROLLBACK);
        verifyNoInteractions(campsiteOccupancyRepository);
    }

    @Test
    void getAllBetweenDates() {
        final Instant now = Instant.now();
        final List<CampsiteOccupancy> expected = List.of(buildCampsiteOccupancy(), buildCampsiteOccupancy());

        when(campsiteOccupancyRepository.findAllByArrivalDateGreaterThanEqualAndDepartureDateLessThanEqualOrderByArrivalDate(now, now))
                .thenReturn(expected);

        final List<CampsiteOccupancy> actual = testee.getAllBetweenDates(now, now);

        assertThat(actual).isEqualTo(expected);

        verify(campsiteOccupancyRepository).findAllByArrivalDateGreaterThanEqualAndDepartureDateLessThanEqualOrderByArrivalDate(now, now);
    }

    @Test
    void updateOccupancyDates() {
        final Instant arrivalDate = Instant.now();
        final CampsiteOccupancy existingOccupancy = buildCampsiteOccupancy(arrivalDate, arrivalDate.plus(3L, ChronoUnit.DAYS));
        final CampsiteOccupancy expected = buildCampsiteOccupancy(arrivalDate, arrivalDate.plus(1L, ChronoUnit.DAYS));

        when(campsiteOccupancyRepository.findByReservationId(RESERVATION_ID)).thenReturn(Optional.of(existingOccupancy));
        when(campsiteOccupancyRepository.save(existingOccupancy)).thenReturn(expected);

        final CampsiteOccupancy actual = testee.updateOccupancyDates(RESERVATION_ID, expected);

        assertThat(actual).isEqualTo(expected);

        verify(campsiteOccupancyRepository).findByReservationId(RESERVATION_ID);
        verify(campsiteOccupancyValidator).validate(eq(existingOccupancy), isA(Errors.class));
        verify(campsiteOccupancyRepository).save(expected);
        verify(eventMessagePublisher).publishEvent(buildCampsiteReservedEvent(), EventType.CAMPSITE_RESERVED);
    }

    @Test
    void updateOccupancyDates_withValidationErrors_shouldThrowException() {
        when(campsiteOccupancyRepository.findByReservationId(RESERVATION_ID)).thenReturn(Optional.of(campsiteOccupancy));
        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("arrivalDate", "errorCode", "default message");
            return null;
        }).when(campsiteOccupancyValidator).validate(eq(campsiteOccupancy), isA(Errors.class));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> testee.updateOccupancyDates(RESERVATION_ID, campsiteOccupancy));

        verify(campsiteOccupancyRepository).findByReservationId(RESERVATION_ID);
        verify(campsiteOccupancyValidator).validate(eq(campsiteOccupancy), isA(Errors.class));
        verify(eventMessagePublisher).publishEvent(buildCampsiteReservedEvent(), EventType.CAMPSITE_RESERVATION_ROLLBACK);
        verifyNoMoreInteractions(campsiteOccupancyRepository);
    }

    @Test
    void updateOccupancyDates_withOccupancyNotFound_shouldThrowException() {
        when(campsiteOccupancyRepository.findByReservationId(RESERVATION_ID)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> testee.updateOccupancyDates(RESERVATION_ID, campsiteOccupancy));

        verifyNoMoreInteractions(campsiteOccupancyRepository);
        verifyNoInteractions(campsiteOccupancyValidator, eventMessagePublisher);
    }

    private CampsiteOccupancy buildCampsiteOccupancy() {
        return buildCampsiteOccupancy(Instant.now(), Instant.now());
    }

    private CampsiteOccupancy buildCampsiteOccupancy(final Instant arrivalDate, final Instant departureDate) {
        return CampsiteOccupancy.builder()
                .reservationId(RESERVATION_ID)
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .build();
    }

    private CampsiteReservedEvent buildCampsiteReservedEvent() {
        return CampsiteReservedEvent.builder()
                .reservationId(RESERVATION_ID)
                .build();
    }
}
