package upgrade.challenge.reservation.v1.v1.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upgrade.challenge.reservation.domain.Reservation;
import upgrade.challenge.reservation.v1.service.ReservationService;
import upgrade.challenge.reservation.v1.v1.dto.ReservationDto;
import upgrade.challenge.reservation.v1.v1.dto.ReservationResponseDto;
import upgrade.challenge.reservation.v1.v1.mapper.ReservationMapper;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

@Component
public class ReservationAdapter {

    private final ReservationMapper reservationMapper;
    private final ReservationService reservationService;

    @Autowired
    public ReservationAdapter(ReservationMapper reservationMapper, ReservationService reservationService) {
        this.reservationMapper = reservationMapper;
        this.reservationService = reservationService;
    }

    public void deleteReservation(final Long id) {
        notNull(id, "The id parameter is mandatory.");

        reservationService.cancelReservation(id);
    }

    public List<ReservationResponseDto> getAllReservationsByEmail(final String email) {
        hasText(email, "The email parameter is mandatory.");

        return reservationService.getAllReservationsByEmail(email).stream()
                .map(reservationMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public ReservationResponseDto makeReservation(final ReservationDto reservationDto) {
        notNull(reservationDto, "The reservationDto is mandatory.");

        final Reservation reservation = reservationService
                .createReservation(reservationMapper.mapToEntity(reservationDto));

        return reservationMapper.mapToDto(reservation);
    }
}
