package upgrade.challenge.availability.v1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import upgrade.challenge.availability.domain.CampsiteOccupancy;
import upgrade.challenge.availability.domain.EventType;
import upgrade.challenge.availability.exception.NotFoundException;
import upgrade.challenge.availability.exception.ValidationException;
import upgrade.challenge.availability.repository.CampsiteOccupancyRepository;
import upgrade.challenge.availability.v1.messaging.eventmessage.CampsiteReservedEvent;
import upgrade.challenge.availability.v1.messaging.publisher.EventMessagePublisher;
import upgrade.challenge.availability.validator.CampsiteOccupancyValidator;

import java.sql.SQLException;

@Service
public class CampsiteOccupancyService {

    private final CampsiteOccupancyRepository campsiteOccupancyRepository;
    private final CampsiteOccupancyValidator campsiteOccupancyValidator;
    private final EventMessagePublisher eventMessagePublisher;

    @Autowired
    public CampsiteOccupancyService(CampsiteOccupancyRepository campsiteOccupancyRepository,
                                    CampsiteOccupancyValidator campsiteOccupancyValidator,
                                    EventMessagePublisher eventMessagePublisher) {
        this.campsiteOccupancyRepository = campsiteOccupancyRepository;
        this.campsiteOccupancyValidator = campsiteOccupancyValidator;
        this.eventMessagePublisher = eventMessagePublisher;
    }

    @Transactional(rollbackFor = SQLException.class)
    public void cancel(final Long reservationId) {
        campsiteOccupancyRepository.deleteByReservationId(reservationId);
    }

    @Transactional(rollbackFor = SQLException.class)
    public CampsiteOccupancy create(final CampsiteOccupancy campsiteOccupancy) {
        validateOccupancy(campsiteOccupancy);

        final CampsiteOccupancy createdCampsiteOccupancy = campsiteOccupancyRepository.save(campsiteOccupancy);

        publishOccupancyConfirmation(createdCampsiteOccupancy);

        return createdCampsiteOccupancy;
    }

    public void publishOccupancyConfirmation(final CampsiteOccupancy campsiteOccupancy) {
        eventMessagePublisher.publishEvent(buildCampsiteReservedEvent(campsiteOccupancy), EventType.CAMPSITE_RESERVED);
    }

    public CampsiteOccupancy updateOccupancyDates(final Long reservationId, final CampsiteOccupancy campsiteOccupancy) {
        return campsiteOccupancyRepository.findByReservationId(reservationId)
                .map(existingCampsiteOccupancy -> updateOccupancyDates(existingCampsiteOccupancy, campsiteOccupancy))
                .orElseThrow(NotFoundException::new);
    }

    private void validateOccupancy(final CampsiteOccupancy campsiteOccupancy) {
        final BeanPropertyBindingResult beanPropertyBindingResult = new BeanPropertyBindingResult(campsiteOccupancy, "campsiteOccupancy");
        campsiteOccupancyValidator.validate(campsiteOccupancy, beanPropertyBindingResult);

        publishRollbackEvent(beanPropertyBindingResult, campsiteOccupancy);
    }

    private void publishRollbackEvent(final BeanPropertyBindingResult beanPropertyBindingResult,
                                      final CampsiteOccupancy campsiteOccupancy) {
        if (!CollectionUtils.isEmpty(beanPropertyBindingResult.getFieldErrors())) {
            eventMessagePublisher.publishEvent(buildCampsiteReservedEvent(campsiteOccupancy), EventType.CAMPSITE_RESERVATION_ROLLBACK);
            throw new ValidationException(beanPropertyBindingResult.getFieldErrors());
        }
    }

    private CampsiteReservedEvent buildCampsiteReservedEvent(final CampsiteOccupancy campsiteOccupancy) {
        return CampsiteReservedEvent.builder()
                .reservationId(campsiteOccupancy.getReservationId())
                .build();
    }

    private CampsiteOccupancy updateOccupancyDates(final CampsiteOccupancy existingCampsiteOccupancy,
                                                   final CampsiteOccupancy campsiteOccupancy) {
        existingCampsiteOccupancy.setArrivalDate(campsiteOccupancy.getArrivalDate());
        existingCampsiteOccupancy.setDepartureDate(campsiteOccupancy.getDepartureDate());

        validateOccupancy(existingCampsiteOccupancy);
        final CampsiteOccupancy updatedCampsiteOccupancy = campsiteOccupancyRepository.save(existingCampsiteOccupancy);
        publishOccupancyConfirmation(updatedCampsiteOccupancy);

        return updatedCampsiteOccupancy;
    }
}
