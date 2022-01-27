package upgrade.challenge.reservation.domain;

import lombok.Getter;

@Getter
public enum EventType {

    RESERVATION_CREATED("ReservationCreatedEvent");

    private String value;

    EventType(String value) {
        this.value = value;
    }
}
