//package upgrade.challenge.reservation.v1.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import upgrade.challenge.reservation.domain.Reservation;
//import upgrade.challenge.reservation.domain.ReservationStatus;
//import upgrade.challenge.reservation.repository.ReservationRepository;
//
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ReservationServiceTest {
//
//    private static final String EMAIL = "email@test.com";
//    private static final Long RESERVATION_ID = 123456789L;
//
//    private ReservationService testee;
//
//    @Mock
//    private ReservationRepository reservationRepository;
//
//    private Reservation reservation;
//
//    @BeforeEach
//    void setUp() {
//        testee = new ReservationService(reservationRepository);
//        reservation = buildReservation(null);
//    }
//
//    @Test
//    void createReservation() {
//        final Reservation expected = reservation.setId(RESERVATION_ID);
//
//        doAnswer(invocation -> ((Reservation) invocation.getArguments()[0]).setId(RESERVATION_ID))
//                .when(reservationRepository).save(reservation);
//
//        final Reservation actual = testee.createReservation(reservation);
//
//        assertThat(actual).isEqualTo(expected);
//
//        verify(reservationRepository).save(reservation);
//    }
//
//    @Test
//    void createReservation_withRuntimeException_shouldThrowSameException() {
//        when(reservationRepository.save(reservation)).thenThrow(new RuntimeException("error"));
//
//        assertThatExceptionOfType(RuntimeException.class)
//                .isThrownBy(() -> testee.createReservation(reservation))
//                .withMessage("error");
//
//        verify(reservationRepository).save(reservation);
//    }
//
//    @Test
//    void getAllReservationsByEmail() {
//        final List<Reservation> expected = List.of(buildReservation(), buildReservation());
//
//        when(reservationRepository.findAllByGuestEmailOrderByCreatedDateDesc(EMAIL))
//                .thenReturn(expected);
//
//        final List<Reservation> actual = testee.getAllReservationsByEmail(EMAIL);
//
//        assertThat(actual).isEqualTo(expected);
//
//        verify(reservationRepository).findAllByGuestEmailOrderByCreatedDateDesc(EMAIL);
//    }
//
//    private Reservation buildReservation(final Long id) {
//        final Instant now = Instant.now();
//
//        return Reservation.builder()
//                .id(id)
//                .status(ReservationStatus.RESERVATION_PENDING)
//                .guestEmail(EMAIL)
//                .arrivalDate(now)
//                .departureDate(now.plus(1L, ChronoUnit.DAYS))
//                .build();
//    }
//
//    private Reservation buildReservation() {
//        return buildReservation(RESERVATION_ID);
//    }
//}
