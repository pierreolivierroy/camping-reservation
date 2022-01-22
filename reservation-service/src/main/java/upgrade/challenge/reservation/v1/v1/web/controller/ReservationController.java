package upgrade.challenge.reservation.v1.v1.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import upgrade.challenge.reservation.v1.v1.dto.ReservationDto;
import upgrade.challenge.reservation.v1.v1.dto.SuccessfulReservationDto;
import upgrade.challenge.reservation.v1.v1.web.adapter.ReservationAdapter;

import javax.validation.Valid;

@RestController
@RequestMapping("/reservation/v1.1")
public class ReservationController {

    private final ReservationAdapter reservationAdapter;

    @Autowired
    public ReservationController(ReservationAdapter reservationAdapter) {
        this.reservationAdapter = reservationAdapter;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessfulReservationDto reserveCampsite(final @Valid @RequestBody ReservationDto reservationDto) {
        return reservationAdapter.reserveCampsite(reservationDto);
    }
}
