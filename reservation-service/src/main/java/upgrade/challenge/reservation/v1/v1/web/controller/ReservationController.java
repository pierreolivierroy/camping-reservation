package upgrade.challenge.reservation.v1.v1.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import upgrade.challenge.reservation.v1.v1.dto.ReservationDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/reservation/v1.1")
public class ReservationController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void reserveCampsite(final @Valid @RequestBody ReservationDto reservationDto) {

    }
}
