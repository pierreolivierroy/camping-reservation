package upgrade.challenge.reservation.v1.v1.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upgrade.challenge.reservation.domain.Reservation;
import upgrade.challenge.reservation.domain.ReservationStatus;
import upgrade.challenge.reservation.v1.service.ReservationService;
import upgrade.challenge.reservation.v1.v1.dto.ReservationDto;
import upgrade.challenge.reservation.v1.v1.dto.ReservationResponseDto;
import upgrade.challenge.reservation.v1.v1.dto.UpdateReservationDatesDto;
import upgrade.challenge.reservation.v1.v1.mapper.ReservationMapper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationAdapterTest {

    private static final String EMAIL = "email@test.com";
    private static final Long RESERVATION_ID = 123456789L;

    private ReservationAdapter testee;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private ReservationService reservationService;

    private Reservation reservation;
    private ReservationDto reservationDto;
    private UpdateReservationDatesDto updateReservationDatesDto;

    @BeforeEach
    void setUp() {
        testee = new ReservationAdapter(reservationMapper, reservationService);
        reservation = buildReservation();
        reservationDto = buildReservationDto();
        updateReservationDatesDto = buildUpdateReservationDatesDto();
    }

    @Test
    void deleteReservation() {
        doNothing().when(reservationService).cancelReservation(RESERVATION_ID);

        testee.deleteReservation(RESERVATION_ID);

        verify(reservationService).cancelReservation(RESERVATION_ID);
    }

    @Test
    void deleteReservation_withNullId_shouldThrowException() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> testee.deleteReservation(null))
                .withMessage("The id parameter is mandatory.");

        verifyNoInteractions(reservationService);
    }

    @Test
    void getAllReservationsByEmail() {
        final Reservation reservation = Reservation.builder().build();
        final ReservationResponseDto reservationResponseDto = buildReservationResponseDto();
        final List<ReservationResponseDto> expected = List.of(reservationResponseDto, reservationResponseDto);

        when(reservationService.getAllReservationsByEmail(EMAIL))
                .thenReturn(List.of(reservation, reservation));
        when(reservationMapper.mapToDto(reservation)).thenReturn(reservationResponseDto);

        final List<ReservationResponseDto> actual = testee.getAllReservationsByEmail(EMAIL);

        assertThat(actual).isEqualTo(expected);

        verify(reservationService).getAllReservationsByEmail(EMAIL);
        verify(reservationMapper, times(2)).mapToDto(reservation);
    }

    @Test
    void getAllReservationsByEmail_withEmptyResult() {
        final List<ReservationResponseDto> expected = Collections.emptyList();

        when(reservationService.getAllReservationsByEmail(EMAIL)).thenReturn(Collections.emptyList());

        final List<ReservationResponseDto> actual = testee.getAllReservationsByEmail(EMAIL);

        assertThat(actual).isEqualTo(expected);

        verify(reservationService).getAllReservationsByEmail(EMAIL);
        verifyNoInteractions(reservationMapper);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void getAllReservationsByEmail_withInvalidEmail_shouldThrowException(final String email) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> testee.getAllReservationsByEmail(email))
                .withMessage("The email parameter is mandatory.");

        verifyNoInteractions(reservationService, reservationMapper);
    }

    @Test
    void makeReservation() {
        final ReservationResponseDto expected = buildReservationResponseDto(reservationDto.getArrivalDate(),
                reservationDto.getDepartureDate());

        when(reservationMapper.mapToEntity(reservationDto)).thenReturn(reservation);
        when(reservationService.createReservation(reservation)).thenReturn(reservation);
        when(reservationMapper.mapToDto(reservation)).thenReturn(expected);

        final ReservationResponseDto actual = testee.makeReservation(reservationDto);

        assertThat(actual).isEqualTo(expected);

        verify(reservationMapper).mapToEntity(reservationDto);
        verify(reservationService).createReservation(reservation);
        verify(reservationMapper).mapToDto(reservation);
    }

    @Test
    void makeReservation_withMissingReservationDto_shouldThrowException() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> testee.makeReservation(null))
                .withMessage("The reservationDto is mandatory.");
    }

    @Test
    void updateReservationDates() {
        final ReservationResponseDto expected = buildReservationResponseDto();

        when(reservationMapper.mapUpgradeDatesDtoToEntity(RESERVATION_ID, updateReservationDatesDto))
                .thenReturn(reservation);
        when(reservationService.patchReservation(RESERVATION_ID, reservation)).thenReturn(reservation);
        when(reservationMapper.mapToDto(reservation)).thenReturn(expected);

        final ReservationResponseDto actual = testee.updateReservationDates(RESERVATION_ID, updateReservationDatesDto);

        assertThat(actual).isEqualTo(expected);

        verify(reservationMapper).mapUpgradeDatesDtoToEntity(RESERVATION_ID, updateReservationDatesDto);
        verify(reservationService).patchReservation(RESERVATION_ID, reservation);
        verify(reservationMapper).mapToDto(reservation);
    }

    @ParameterizedTest
    @MethodSource("getInvalidUpdateReservationDatesDtos")
    void updateReservationDates_withInvalidFields_shouldThrowException(final Long id,
                                                                       final UpdateReservationDatesDto updateReservationDatesDto,
                                                                       final String errorMessage) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> testee.updateReservationDates(id, updateReservationDatesDto))
                .withMessage(errorMessage);

        verifyNoInteractions(reservationMapper, reservationService);
    }

    private static Stream<Arguments> getInvalidUpdateReservationDatesDtos() {
        return Stream.of(
                Arguments.of(null, buildUpdateReservationDatesDto(), "The id parameter is mandatory."),
                Arguments.of(RESERVATION_ID, null, "The updateReservationDatesDto parameter is mandatory."),
                Arguments.of(RESERVATION_ID, buildUpdateReservationDatesDto().setArrivalDate(null),
                        "The updateReservationDatesDto#arrivalDate parameter is mandatory."),
                Arguments.of(RESERVATION_ID, buildUpdateReservationDatesDto().setDepartureDate(null),
                        "The updateReservationDatesDto#departureDate parameter is mandatory.")
        );
    }

    private Reservation buildReservation() {
        final Instant now = Instant.now();

        return Reservation.builder()
                .status(ReservationStatus.RESERVATION_PENDING)
                .guestEmail(EMAIL)
                .arrivalDate(now)
                .departureDate(now.plus(1L, ChronoUnit.DAYS))
                .build();
    }

    private static ReservationDto buildReservationDto() {
        final Instant now = Instant.now();

        return ReservationDto.builder()
                .guestEmail("email")
                .firstName("first-name")
                .lastName("last-name")
                .arrivalDate(now)
                .departureDate(now.plus(1L, ChronoUnit.DAYS))
                .build();
    }

    private ReservationResponseDto buildReservationResponseDto() {
        return buildReservationResponseDto(Instant.now(), Instant.now().plus(1L, ChronoUnit.DAYS));
    }

    private ReservationResponseDto buildReservationResponseDto(final Instant arrivalDate,
                                                               final Instant departureDate) {
        return ReservationResponseDto.builder()
                .reservationId(String.valueOf(RESERVATION_ID))
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .build();
    }

    private static UpdateReservationDatesDto buildUpdateReservationDatesDto() {
        final Instant now = Instant.now();

        return UpdateReservationDatesDto.builder()
                .arrivalDate(now)
                .departureDate(now.plus(1L, ChronoUnit.DAYS))
                .build();
    }
}
