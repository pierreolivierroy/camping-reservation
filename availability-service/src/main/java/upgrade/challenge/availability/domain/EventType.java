package upgrade.challenge.availability.domain;

import lombok.Getter;

@Getter
public enum EventType {

    CAMPSITE_RESERVATION_ROLLBACK("CampsiteReservationRollbackEvent"),
    CAMPSITE_RESERVED("CampsiteReservedEvent");

    private String value;

    EventType(String value) {
        this.value = value;
    }
}
