package upgrade.challenge.availability.v1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upgrade.challenge.availability.domain.CampsiteOccupancy;
import upgrade.challenge.availability.domain.EventType;
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
        final CampsiteOccupancy createdCampsiteOccupancy = campsiteOccupancyRepository.save(campsiteOccupancy);

        eventMessagePublisher.publishEvent(buildCampsiteReservedEvent(createdCampsiteOccupancy),
                EventType.CAMPSITE_RESERVED);

        return createdCampsiteOccupancy;
    }

    private CampsiteReservedEvent buildCampsiteReservedEvent(final CampsiteOccupancy campsiteOccupancy) {
        return CampsiteReservedEvent.builder()
                .reservationId(campsiteOccupancy.getReservationId())
                .build();
    }
}
