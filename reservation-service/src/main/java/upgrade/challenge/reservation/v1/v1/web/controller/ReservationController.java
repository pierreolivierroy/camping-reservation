package upgrade.challenge.reservation.v1.v1.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import upgrade.challenge.reservation.v1.v1.dto.ReservationDto;
import upgrade.challenge.reservation.v1.v1.dto.ReservationResponseDto;
import upgrade.challenge.reservation.v1.v1.adapter.ReservationAdapter;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/reservation/v1.1")
public class ReservationController {

    private final ReservationAdapter reservationAdapter;

    @Autowired
    public ReservationController(ReservationAdapter reservationAdapter) {
        this.reservationAdapter = reservationAdapter;
    }

    @GetMapping
    public List<ReservationResponseDto> getAllReservationsByEmail(final @RequestParam("email") String email) {
        return reservationAdapter.getAllReservationsByEmail(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponseDto makeReservation(final @Valid @RequestBody ReservationDto reservationDto) {
        return reservationAdapter.makeReservation(reservationDto);
    }
}
