package upgrade.challenge.reservation.v1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;
import upgrade.challenge.reservation.domain.EventType;
import upgrade.challenge.reservation.domain.Reservation;
import upgrade.challenge.reservation.domain.ReservationEvent;
import upgrade.challenge.reservation.domain.ReservationStatus;
import upgrade.challenge.reservation.exception.NotFoundException;
import upgrade.challenge.reservation.exception.ValidationException;
import upgrade.challenge.reservation.repository.ReservationRepository;
import upgrade.challenge.reservation.validator.ReservationValidator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private static final String EMAIL = "email@test.com";
    private static final Long RESERVATION_ID = 123456789L;

    private ReservationService testee;

    @Mock
    private ReservationEventFactory reservationEventFactory;

    @Mock
    private ReservationEventService reservationEventService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationValidator reservationValidator;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        testee = new ReservationService(reservationEventFactory, reservationEventService, reservationRepository, reservationValidator);
        reservation = buildReservation(null);
    }

    @Test
    void confirmReservation() {
        final Reservation existingTransaction = buildReservation();
        final ArgumentCaptor<Reservation> argumentCaptor = ArgumentCaptor.forClass(Reservation.class);

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(existingTransaction));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(existingTransaction);

        testee.confirmReservation(RESERVATION_ID);

        verify(reservationRepository).findById(RESERVATION_ID);
        verify(reservationRepository).save(argumentCaptor.capture());

        final Reservation expected = existingTransaction.setStatus(ReservationStatus.RESERVATION_CONFIRMED);
        final Reservation actual = argumentCaptor.getValue();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void confirmReservation_withReservationNotFound_shouldThrowException() {
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> testee.confirmReservation(RESERVATION_ID));

        verify(reservationRepository).findById(RESERVATION_ID);
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    void createReservation() {
        final Reservation expected = reservation.setId(RESERVATION_ID);
        final ReservationEvent reservationEvent = ReservationEvent.builder().build();

        doAnswer(invocation -> ((Reservation) invocation.getArguments()[0]).setId(RESERVATION_ID))
                .when(reservationRepository).save(reservation);
        when(reservationEventFactory.buildReservationEvent(expected, EventType.RESERVATION_CREATED))
                .thenReturn(reservationEvent);

        final Reservation actual = testee.createReservation(reservation);

        assertThat(actual).isEqualTo(expected);

        verify(reservationValidator).validate(eq(reservation), isA(Errors.class));
        verify(reservationRepository).save(reservation);
        verify(reservationEventFactory).buildReservationEvent(expected, EventType.RESERVATION_CREATED);
        verify(reservationEventService).create(reservationEvent);
    }

    @Test
    void createReservation_withRuntimeException_shouldThrowSameException() {
        when(reservationRepository.save(reservation)).thenThrow(new RuntimeException("error"));

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> testee.createReservation(reservation))
                .withMessage("error");

        verify(reservationValidator).validate(eq(reservation), isA(Errors.class));
        verify(reservationRepository).save(reservation);
        verifyNoInteractions(reservationEventFactory, reservationEventService);
    }

    @Test
    void createReservation_withValidationError_shouldThrowException() {
        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("arrivalDate", "errorCode", "default message");
            return null;
        }).when(reservationValidator).validate(eq(reservation), isA(Errors.class));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> testee.createReservation(reservation))
                .withMessage("Invalid field(s) provided");

        verify(reservationValidator).validate(eq(reservation), isA(Errors.class));
        verifyNoInteractions(reservationRepository, reservationEventFactory, reservationEventService);
    }

    @Test
    void getAllReservationsByEmail() {
        final List<Reservation> expected = List.of(buildReservation(), buildReservation());

        when(reservationRepository.findAllByGuestEmailOrderByCreatedDateDesc(EMAIL))
                .thenReturn(expected);

        final List<Reservation> actual = testee.getAllReservationsByEmail(EMAIL);

        assertThat(actual).isEqualTo(expected);

        verify(reservationRepository).findAllByGuestEmailOrderByCreatedDateDesc(EMAIL);
    }

    @Test
    void patchReservation() {
        final Instant newArrivalDate = Instant.now().plus(10L, ChronoUnit.DAYS);
        final Reservation reservationWithUpdate = Reservation.builder()
                .arrivalDate(newArrivalDate)
                .departureDate(newArrivalDate)
                .build();
        final Reservation expected = buildReservation()
                .setArrivalDate(newArrivalDate)
                .setDepartureDate(newArrivalDate)
                .setStatus(ReservationStatus.RESERVATION_CHANGE_PENDING);

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(buildReservation()));
        when(reservationRepository.save(expected)).thenReturn(expected);

        final Reservation actual = testee.patchReservation(RESERVATION_ID, reservationWithUpdate);

        assertThat(actual).isEqualTo(expected);

        verify(reservationRepository).findById(RESERVATION_ID);
        verify(reservationValidator).validate(eq(expected), isA(Errors.class));
        verify(reservationRepository).save(expected);
    }

    @Test
    void patchReservation_withSameDates_shouldNotModifyEntity() {
        final Reservation expected = buildReservation();
        final Reservation reservationWithUpdate = Reservation.builder()
                .arrivalDate(expected.getArrivalDate())
                .departureDate(expected.getDepartureDate())
                .build();

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(expected));

        final Reservation actual = testee.patchReservation(RESERVATION_ID, reservationWithUpdate);

        assertThat(actual).isEqualTo(expected);

        verify(reservationRepository).findById(RESERVATION_ID);
        verifyNoMoreInteractions(reservationRepository);
        verifyNoInteractions(reservationValidator);
    }

    @Test
    void patchReservation_withValidationException_shouldThrowException() {
        final Instant newArrivalDate = Instant.now().plus(10L, ChronoUnit.DAYS);
        final Reservation foundExistingReservation = buildReservation();
        final Reservation modifiedExistingReservation = buildReservation()
                .setArrivalDate(newArrivalDate)
                .setDepartureDate(newArrivalDate)
                .setStatus(ReservationStatus.RESERVATION_CHANGE_PENDING);
        final Reservation reservationWithUpdate = Reservation.builder()
                .arrivalDate(newArrivalDate)
                .departureDate(newArrivalDate)
                .build();

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(foundExistingReservation));
        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("arrivalDate", "errorCode", "default message");
            return null;
        }).when(reservationValidator).validate(eq(modifiedExistingReservation), isA(Errors.class));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> testee.patchReservation(RESERVATION_ID, reservationWithUpdate))
                .withMessage("Invalid field(s) provided");

        verify(reservationRepository).findById(RESERVATION_ID);
        verify(reservationValidator).validate(eq(modifiedExistingReservation), isA(Errors.class));
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    void patchReservation_withEntityNotFound_shouldThrowException() {
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> testee.patchReservation(RESERVATION_ID, buildReservation()))
                .withMessage("Item was not found.");

        verify(reservationRepository).findById(RESERVATION_ID);
        verifyNoMoreInteractions(reservationRepository);
        verifyNoInteractions(reservationValidator);
    }

    private Reservation buildReservation(final Long id) {
        final Instant now = Instant.now();

        return Reservation.builder()
                .id(id)
                .status(ReservationStatus.RESERVATION_PENDING)
                .guestEmail(EMAIL)
                .arrivalDate(now)
                .departureDate(now.plus(1L, ChronoUnit.DAYS))
                .build();
    }

    private Reservation buildReservation() {
        return buildReservation(RESERVATION_ID);
    }
}
