package upgrade.challenge.reservation.v1.v1.mapper;

import org.springframework.stereotype.Component;
import upgrade.challenge.reservation.domain.Reservation;
import upgrade.challenge.reservation.v1.v1.dto.ReservationDto;
import upgrade.challenge.reservation.v1.v1.dto.ReservationResponseDto;

@Component
public class ReservationMapper {

    public ReservationResponseDto mapToDto(final Reservation reservation) {
        return ReservationResponseDto.builder()
                .reservationId(reservation.getId().toString())
                .status(reservation.getStatus().toString())
                .arrivalDate(reservation.getArrivalDate())
                .departureDate(reservation.getDepartureDate())
                .reservationDate(reservation.getCreatedDate())
                .build();
    }

    public Reservation mapToEntity(final ReservationDto reservationDto) {
        return Reservation.builder()
                .guestEmail(reservationDto.getGuestEmail())
                .firstName(reservationDto.getFirstName())
                .lastName(reservationDto.getLastName())
                .arrivalDate(reservationDto.getArrivalDate())
                .departureDate(reservationDto.getDepartureDate())
                .build();
    }
}
