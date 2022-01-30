package upgrade.challenge.availability.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import upgrade.challenge.availability.domain.CampsiteOccupancy;
import upgrade.challenge.availability.repository.CampsiteOccupancyRepository;

@Component
public class CampsiteOccupancyValidator implements Validator {

    private static final String DATE_UNAVAILABLE_ERROR_MESSAGE = "The dates specified are not available.";

    private final CampsiteOccupancyRepository campsiteOccupancyRepository;

    @Autowired
    public CampsiteOccupancyValidator(CampsiteOccupancyRepository campsiteOccupancyRepository) {
        this.campsiteOccupancyRepository = campsiteOccupancyRepository;
    }

    @Override
    public boolean supports(final Class<?> clazz) {
        return CampsiteOccupancy.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        final CampsiteOccupancy campsiteOccupancyToValidate = (CampsiteOccupancy) target;

        validateArrivalDate(campsiteOccupancyToValidate, errors);
        validateDepartureDate(campsiteOccupancyToValidate, errors);
    }

    private void validateArrivalDate(final CampsiteOccupancy campsiteOccupancy, final Errors errors) {
        long numberOfExistingOccupancies = campsiteOccupancyRepository
                .countByArrivalDateLessThanEqualAndDepartureDateGreaterThanEqual(campsiteOccupancy.getArrivalDate(), campsiteOccupancy.getDepartureDate());

        if (numberOfExistingOccupancies > 0) {
            errors.rejectValue("arrivalDate", null, DATE_UNAVAILABLE_ERROR_MESSAGE);
        }
    }

    private void validateDepartureDate(final CampsiteOccupancy campsiteOccupancy, final Errors errors) {
        long numberOfExistingOccupancies = campsiteOccupancyRepository
                .countByDepartureDateGreaterThanEqualAndDepartureDateLessThanEqual(campsiteOccupancy.getArrivalDate(), campsiteOccupancy.getDepartureDate());

        if (numberOfExistingOccupancies > 0) {
            errors.rejectValue("departureDate", null, DATE_UNAVAILABLE_ERROR_MESSAGE);
        }
    }
}
