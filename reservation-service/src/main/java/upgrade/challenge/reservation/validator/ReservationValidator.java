package upgrade.challenge.reservation.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import upgrade.challenge.reservation.domain.Reservation;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
public class ReservationValidator implements Validator {

    @Override
    public boolean supports(final Class<?> clazz) {
        return Reservation.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        final Reservation reservationToValidate = (Reservation) target;

        validateArrivalDate(reservationToValidate, errors);
        validateReservationDuration(reservationToValidate, errors);
    }

    private void validateArrivalDate(final Reservation reservation, final Errors errors) {
        if (DAYS.between(Instant.now(), reservation.getArrivalDate()) < 1) {
            errors.rejectValue("arrivalDate", null, "The arrival date must be in minimum 1 day or more.");
        }

        if (DAYS.between(Instant.now(), reservation.getArrivalDate()) > 30) {
            errors.rejectValue("arrivalDate", null, "The arrival date can be up to 1 month in advance.");
        }
    }

    private void validateReservationDuration(final Reservation reservation, final Errors errors) {
        if (DAYS.between(reservation.getArrivalDate(), reservation.getDepartureDate()) > 3) {
            errors.rejectValue("departureDate", null, "The departure date is too far, duration must not exceed 3 days.");
        }
    }
}
