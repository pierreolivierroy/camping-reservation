package upgrade.challenge.availability.v1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upgrade.challenge.availability.domain.CampsiteOccupancy;
import upgrade.challenge.availability.domain.EventType;
import upgrade.challenge.availability.exception.NotFoundException;
import upgrade.challenge.availability.repository.CampsiteOccupancyRepository;
import upgrade.challenge.availability.v1.messaging.eventmessage.CampsiteReservedEvent;
import upgrade.challenge.availability.v1.messaging.publisher.EventMessagePublisher;

import java.sql.SQLException;

@Service
public class CampsiteOccupancyService {

    private final CampsiteOccupancyRepository campsiteOccupancyRepository;
    private final EventMessagePublisher eventMessagePublisher;

    @Autowired
    public CampsiteOccupancyService(CampsiteOccupancyRepository campsiteOccupancyRepository,
                                    EventMessagePublisher eventMessagePublisher) {
        this.campsiteOccupancyRepository = campsiteOccupancyRepository;
        this.eventMessagePublisher = eventMessagePublisher;
    }

    @Transactional(rollbackFor = SQLException.class)
    public void cancel(final Long reservationId) {
        campsiteOccupancyRepository.deleteByReservationId(reservationId);
    }

    @Transactional(rollbackFor = SQLException.class)
    public CampsiteOccupancy create(final CampsiteOccupancy campsiteOccupancy) {
        // TODO: 2022-01-26 Validate occupancy
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

    private CampsiteReservedEvent buildCampsiteReservedEvent(final CampsiteOccupancy campsiteOccupancy) {
        return CampsiteReservedEvent.builder()
                .reservationId(campsiteOccupancy.getReservationId())
                .build();
    }

    private CampsiteOccupancy updateOccupancyDates(final CampsiteOccupancy existingCampsiteOccupancy,
                                                   final CampsiteOccupancy campsiteOccupancy) {
        existingCampsiteOccupancy.setArrivalDate(campsiteOccupancy.getArrivalDate());
        existingCampsiteOccupancy.setDepartureDate(campsiteOccupancy.getDepartureDate());

        // TODO: 2022-01-26 Validate occupancy
        final CampsiteOccupancy updatedCampsiteOccupancy = campsiteOccupancyRepository.save(existingCampsiteOccupancy);
        publishOccupancyConfirmation(updatedCampsiteOccupancy);

        return updatedCampsiteOccupancy;
    }
}
