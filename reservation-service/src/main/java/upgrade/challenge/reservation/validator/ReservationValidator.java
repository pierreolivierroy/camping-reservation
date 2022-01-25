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
    }

    private void validateArrivalDate(final Reservation reservation, final Errors errors) {
        if (DAYS.between(Instant.now(), reservation.getArrivalDate()) < 1) {
            errors.rejectValue("arrivalDate", null, "The arrival date must be in minimum one day or more.");
        }
    }
}
